package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWorldCollision;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CPathfindingProcessor;
// 表示单位移动行为的类
public class CBehaviorMove implements CBehavior {
	// 定义一个静态布尔变量，用于控制是否总是中断移动操作
	private static boolean ALWAYS_INTERRUPT_MOVE = false;

	// 定义一个静态的Rectangle对象，用于临时存储矩形区域信息
	private static final Rectangle tempRect = new Rectangle();

	// 定义一个CUnit类型的成员变量，表示当前行为所属的单位
	private final CUnit unit;

	// 定义一个整型成员变量，表示高亮显示的订单ID
	private int highlightOrderId;

	// 定义一个TargetVisitingResetter类型的成员变量，用于重置目标访问状态
	private final TargetVisitingResetter targetVisitingResetter;


	// 构造函数，初始化单位和目标访问重置器
	public CBehaviorMove(final CUnit unit) {
		this.unit = unit;
		this.targetVisitingResetter = new TargetVisitingResetter();
	}

	// 等待转向，转向到移动窗口才能移动
	private boolean wasWithinPropWindow = false;

	// 存储实体移动路径的点列表
	private List<Point2D.Float> path = null;

	// 网格映射，用于路径查找处理
	private CPathfindingProcessor.GridMapping gridMapping;

	// 实体的目标位置
	private Point2D.Float target;

	// 路径查找的搜索周期数
	private int searchCycles = 0;

	// 实体跟随的单位
	private CUnit followUnit;

	// 实体的远程行为
	private CRangedBehavior rangedBehavior;

	// 标记是否是首次更新
	private boolean firstUpdate = true;

	// 禁用碰撞
	private boolean disableCollision = false;
	// 定义一个布尔变量，表示路径寻找是否激活
	private boolean pathfindingActive = false;

	// 定义一个布尔变量，表示是否是第一次进行路径寻找任务
	private boolean firstPathfindJob = false;

	// 定义一个布尔变量，表示路径寻找失败是否放弃
	private boolean pathfindingFailedGiveUp;

	// 定义一个整型变量，表示放弃路径寻找直到的回合数
	private int giveUpUntilTurnTick;


	// 重置移动行为
	public CBehaviorMove reset(final int highlightOrderId, final AbilityTarget target) {
		target.visit(this.targetVisitingResetter.reset(highlightOrderId));
		this.rangedBehavior = null;
		this.disableCollision = false;
		return this;
	}

	// 重置移动行为，包含范围行为和是否禁用碰撞
	public CBehaviorMove reset(final AbilityTarget target, final CRangedBehavior rangedBehavior,
			final boolean disableCollision) {
		final int highlightOrderId = rangedBehavior.getHighlightOrderId();
		target.visit(this.targetVisitingResetter.reset(highlightOrderId));
		this.rangedBehavior = rangedBehavior;
		this.disableCollision = disableCollision;
		return this;
	}

	// 内部重置移动方法，设置目标和路径
	private void internalResetMove(final int highlightOrderId, final float targetX, final float targetY) {
		// 设置高亮订单ID
		this.highlightOrderId = highlightOrderId;
		// 初始化是否在属性窗口内的标志为false
		this.wasWithinPropWindow = false;
		// 根据单位的碰撞大小决定使用角点映射还是单元格映射
		this.gridMapping = CPathfindingProcessor.isCollisionSizeBetterSuitedForCorners(
				this.unit.getUnitType().getCollisionSize()) ? CPathfindingProcessor.GridMapping.CORNERS
				: CPathfindingProcessor.GridMapping.CELLS;
		// 设置目标点坐标
		this.target = new Point2D.Float(targetX, targetY);
		// 初始化路径为null
		this.path = null;
		// 初始化搜索周期为0
		this.searchCycles = 0;
		// 初始化跟随单位为null
		this.followUnit = null;
		// 初始化首次更新标志为true
		this.firstUpdate = true;
		// 初始化路径查找失败放弃标志为false
		this.pathfindingFailedGiveUp = false;
		// 初始化放弃直到回合数为0
		this.giveUpUntilTurnTick = 0;

	}

	// 内部重置移动方法，设置跟随单位目标
	private void internalResetMove(final int highlightOrderId, final CUnit followUnit) {
		// 设置高亮订单ID
		this.highlightOrderId = highlightOrderId;
		// 初始化是否在属性窗口内的标志为false
		this.wasWithinPropWindow = false;
		// 根据单位的碰撞大小选择更适合的网格映射方式
		// 如果碰撞大小更适合角落，则使用CORNERS，否则使用CELLS
		this.gridMapping = CPathfindingProcessor.isCollisionSizeBetterSuitedForCorners(
				this.unit.getUnitType().getCollisionSize()) ? CPathfindingProcessor.GridMapping.CORNERS
				: CPathfindingProcessor.GridMapping.CELLS;
		// 设置目标点为跟随单位的坐标
		this.target = new Float(followUnit.getX(), followUnit.getY());
		// 初始化路径为null
		this.path = null;
		// 初始化搜索周期数为0
		this.searchCycles = 0;
		// 设置跟随单位
		this.followUnit = followUnit;
		// 初始化首次更新标志为true
		this.firstUpdate = true;
		// 初始化寻路失败放弃标志为false
		this.pathfindingFailedGiveUp = false;
		// 初始化放弃直到回合数
		this.giveUpUntilTurnTick = 0;

	}

	@Override
	// 获取高亮顺序ID
	public int getHighlightOrderId() {
		return this.highlightOrderId;
	}

	@Override
	// 更新移动行为
	public CBehavior update(final CSimulation simulation) {
		// 如果是范围行为调的移动行为，并且已经到了范围内，就把行为交给范围行为
		if ((this.rangedBehavior != null) && this.rangedBehavior.isWithinRange(simulation)) {
			return this.rangedBehavior.update(simulation);
		}
		if (this.firstUpdate) {
			// 当单位开始移动时，如果它们位于其他单位的上方，可能会将它们推向一侧
			this.unit.setPointAndCheckUnstuck(this.unit.getX(), this.unit.getY(), simulation);
			this.firstUpdate = false;
		}
		// TODO #寻路失败，放弃路径查找，执行下一个指令
		if (this.pathfindingFailedGiveUp) {
			// 如果路径查找失败并决定放弃，则调用onMoveGiveUp方法，并返回单位的下一个命令行为
			/**
			 * 当移动放弃时调用
			 * @param simulation 模拟环境
			 */
			onMoveGiveUp(simulation);
			return this.unit.pollNextOrderBehavior(simulation);
		}
		final float prevX = this.unit.getX(); // 获取单位之前的X坐标
		final float prevY = this.unit.getY(); // 获取单位之前的Y坐标

		MovementType movementType = this.unit.getMovementType(); // 获取单位的移动类型
		if (movementType == null) {
			movementType = MovementType.DISABLED; // 如果移动类型为空，则设置为不可移动类型
		} else if ((movementType == MovementType.FOOT) && this.disableCollision) {
			movementType = MovementType.FOOT_NO_COLLISION; // 如果移动类型为FOOT且禁用碰撞，则设置为FOOT_NO_COLLISION
		}

		// 获取模拟环境中的寻路网格
		final PathingGrid pathingGrid = simulation.getPathingGrid();
		// 获取模拟环境中的世界碰撞信息
		final CWorldCollision worldCollision = simulation.getWorldCollision();
		// 获取当前单位的碰撞尺寸
		final float collisionSize = this.unit.getUnitType().getCollisionSize();
		// 记录起始浮动位置的X坐标
		final float startFloatingX = prevX;
		// 记录起始浮动位置的Y坐标
		final float startFloatingY = prevY;

		// 如果路径为空
		if (this.path == null) {
			// 如果路径查找未激活
			if (!this.pathfindingActive) {
				// 如果有跟随单位
				if (this.followUnit != null) {
					// 设置目标位置为跟随单位的位置
					this.target.x = this.followUnit.getX();
					this.target.y = this.followUnit.getY();
				}
				// 调用模拟器的findNaiveSlowPath方法查找路径
				// 参数包括当前单位、跟随单位、起始浮点坐标、目标位置、移动类型、碰撞大小等
				simulation.findNaiveSlowPath(this.unit, this.followUnit, startFloatingX, startFloatingY, this.target,
						movementType, collisionSize, true, this);
				// 设置路径查找为激活状态
				this.pathfindingActive = true;
				// 设置首次路径查找任务为true
				this.firstPathfindJob = true;
			}
		}
		// 如果当前跟随的单位不为空，路径点数量大于1，并且目标点与跟随单位的距离大于目标点与当前单位距离的10%
		else if ((this.followUnit != null) && (this.path.size() > 1) && (this.target.distance(this.followUnit.getX(),
				this.followUnit.getY()) > (0.1 * this.target.distance(this.unit.getX(), this.unit.getY())))) {
			// 更新目标点的坐标为跟随单位的坐标
			this.target.x = this.followUnit.getX();
			this.target.y = this.followUnit.getY();
			// 如果路径搜索是激活状态，则从路径搜索队列中移除当前单位
			if (this.pathfindingActive) {
				simulation.removeFromPathfindingQueue(this);
			}
			// 重新计算从当前单位到跟随单位的路径
			simulation.findNaiveSlowPath(this.unit, this.followUnit, startFloatingX, startFloatingY, this.target,
					movementType, collisionSize, this.searchCycles < 4, this);
			// 设置路径搜索为激活状态
			this.pathfindingActive = true;
		}

		float currentTargetX;
		float currentTargetY;
		// 如果路径为空或不存在，则根据是否有跟随单位来设置目标位置
		if ((this.path == null) || this.path.isEmpty()) {
			if (this.followUnit != null) {
				// 如果有跟随单位，则目标位置为跟随单位的位置
				currentTargetX = this.followUnit.getX();
				currentTargetY = this.followUnit.getY();
			} else {
				// 如果没有跟随单位，则目标位置为预设的目标位置
				currentTargetX = this.target.x;
				currentTargetY = this.target.y;
			}
		} else {
			// 如果路径存在且不为空
			if ((this.followUnit != null) && (this.path.size() == 1)) {
				// 如果路径只有一个元素且有跟随单位，则目标位置为跟随单位的位置
				currentTargetX = this.followUnit.getX();
				currentTargetY = this.followUnit.getY();
			} else {
				// 否则，目标位置为路径的第一个元素的位置
				final Point2D.Float nextPathElement = this.path.get(0);
				currentTargetX = nextPathElement.x;
				currentTargetY = nextPathElement.y;
			}
		}


		// 计算目标角度
		float deltaX = currentTargetX - prevX; // 计算x轴方向上的变化量
		float deltaY = currentTargetY - prevY; // 计算y轴方向上的变化量
		double goalAngleRad = Math.atan2(deltaY, deltaX); // 使用atan2函数计算目标角度的弧度值
		float goalAngle = (float) Math.toDegrees(goalAngleRad); // 将弧度转换为角度
		if (goalAngle < 0) {
			goalAngle += 360; // 如果角度小于0，则加上360度使其变为正值
		}

		// 获取当前朝向并计算转向差值
		float facing = this.unit.getFacing(); // 获取当前单位的朝向
		float delta = goalAngle - facing; // 计算目标角度与当前朝向的差值

		// 获取单位属性以决定转向速度
		final float propulsionWindow = this.unit.getUnitType().getPropWindow(); // 移动窗口：和目标点角度小于该值才可用移动，否则需要等转向到目标方向
		final float turnRate = this.unit.getUnitType().getTurnRate(); // 获取转向速率
		final int speed = this.unit.getSpeed(); // 获取单位速度

		// 确保转向差值在-180到180度之间
		if (delta < -180) {
			delta = 360 + delta;
		}
		if (delta > 180) {
			delta = -360 + delta;
		}
		float absDelta = Math.abs(delta); // 获取转向差值的绝对值

		// 如果转向差值很小，则直接调整朝向
		if ((absDelta <= 1.0) && (absDelta != 0)) {
			this.unit.setFacing(goalAngle);
		} else {
			// 否则根据转向速率和差值计算需要增加的角度
			float angleToAdd = Math.signum(delta) * (float) Math.toDegrees(turnRate);
			if (absDelta < Math.abs(angleToAdd)) {
				angleToAdd = delta; // 如果差值小于单次可转的最大角度，则直接转向目标角度
			}
			facing += angleToAdd; // 更新朝向
			this.unit.setFacing(facing); // 设置新的朝向
		}

		// 检查是否因为放弃延迟而受阻，如果是当前游戏回合小于放弃直到回合，则受阻
		final boolean blockedByGiveUpUntilTickDelay = simulation.getGameTurnTick() < this.giveUpUntilTurnTick;

		// 如果没有受阻，并且路径不为空，寻路未激活，且绝对差值小于推进窗口
		if (!blockedByGiveUpUntilTickDelay && (this.path != null) && !this.pathfindingActive && (absDelta < propulsionWindow)) {
			// 计算每回合的速度
			final float speedTick = speed * WarsmashConstants.SIMULATION_STEP_TIME;
			// 计算继续前进的距离
			double continueDistance = speedTick;

			do {
				// 定义变量done，用于标记是否完成移动
				boolean done;
				// 定义变量nextX和nextY，用于存储下一个目标位置的坐标
				float nextX, nextY;
				// 计算移动的总距离，使用勾股定理计算deltaX和deltaY的平方和的平方根
				final double travelDistance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
				// 判断移动的总距离是否小于或等于设定的继续移动距离
				if (travelDistance <= continueDistance) {
					// 如果是，则将当前目标位置的坐标赋值给nextX和nextY
					nextX = currentTargetX;
					nextY = currentTargetY;
					// 更新继续移动距离，减去已经移动的距离
					continueDistance = continueDistance - travelDistance;
					// 标记移动完成
					done = true;
				}
				else {
					final double radianFacing = Math.toRadians(facing); // 将面向的角度转换为弧度
					nextX = (prevX + (float) (Math.cos(radianFacing) * continueDistance)); // 计算下一个X坐标
					nextY = (prevY + (float) (Math.sin(radianFacing) * continueDistance)); // 计算下一个Y坐标
					continueDistance = 0; // 重置继续距离
					// 下面的注释代码是用来检查是否到达目标位置的，但是被注释掉了
					// done = (this.gridMapping.getX(pathingGrid, nextX) == this.gridMapping.getX(pathingGrid,
					//         currentTargetX))
					//         && (this.gridMapping.getY(pathingGrid, nextY) == this.gridMapping.getY(pathingGrid,
					//                 currentTargetY));
					done = false; // 设置done为false，表示移动尚未完成

				}
				// 设置临时矩形为当前单位的碰撞矩形
				// 这样做是为了保留原始碰撞矩形的尺寸和形状
				tempRect.set(this.unit.getCollisionRectangle());

				// 将临时矩形的中心点设置为新的坐标（nextX, nextY）
				// 这样做可以移动矩形而不改变其尺寸和形状，仅改变位置
				tempRect.setCenter(nextX, nextY);

				// 检查移动类型是否为空，或者下一个位置是否可行走，并且不会与世界中的其他物体发生碰撞
				if ((movementType == null) || (pathingGrid.isPathable(nextX, nextY, movementType, collisionSize)// ((int)  // 检查下一个位置是否可行走
						// collisionSize
						// / 16)
						// * 16
						&& !worldCollision.intersectsAnythingOtherThan(tempRect, this.unit, movementType)))  // 并且不会与世界中的其他物体发生碰撞
				{
					// 移动单位
					this.unit.setPoint(nextX, nextY, worldCollision, simulation.getRegionManager());
					// 如果移动完成
					if (done) {
						// if we're making headway along the path then it's OK to start thinking fast
						// again
						// 如果移动距离大于0，则重置搜索周期计数器
						if (travelDistance > 0) {
							this.searchCycles = 0;
						}

						// 如果路径为空，表示无法继续移动
						if (this.path.isEmpty()) {
							// 调用放弃移动的回调函数
							onMoveGiveUp(simulation);
							// 返回单位的下一个订单行为
							return this.unit.pollNextOrderBehavior(simulation);
						}
						else {
							// 打印当前路径
							System.out.println(this.path);
							// 移除路径中的第一个元素，并将其值存储在removed变量中
							final Float removed = this.path.remove(0);
							// 打印移除的元素值以及当前位置
							System.out.println(
									"我们认为我们到达了 " + removed + " 因为我们现在在 " + nextX + "," + nextY);
							// 检查路径是否为空
							final boolean emptyPath = this.path.isEmpty();
							if (emptyPath) {
								// 如果跟随的单位不为空，则将当前目标位置设置为跟随单位的位置
								if (this.followUnit != null) {
									currentTargetX = this.followUnit.getX();
									currentTargetY = this.followUnit.getY();
								}
								// 如果跟随的单位为空，则将当前目标位置设置为目标的位置
								else {
									currentTargetX = this.target.x;
									currentTargetY = this.target.y;
								}
							}

							else {
								// 如果跟随的单位不为空且路径大小为1，则将当前目标位置设置为跟随单位的位置
								if ((this.followUnit != null) && (this.path.size() == 1)) {
									// 获取跟随单位的X坐标并赋值给当前目标X坐标
									currentTargetX = this.followUnit.getX();
									// 获取跟随单位的Y坐标并赋值给当前目标Y坐标
									currentTargetY = this.followUnit.getY();
								}
								// 否则，将当前目标位置设置为路径中的第一个元素的位置
								else {
									// 获取路径中的第一个元素
									final Point2D.Float firstPathElement = this.path.get(0);
									// 将第一个元素的X坐标赋值给当前目标X坐标
									currentTargetX = firstPathElement.x;
									// 将第一个元素的Y坐标赋值给当前目标Y坐标
									currentTargetY = firstPathElement.y;
								}
							}
							// 计算目标点与当前点的坐标差
							deltaY = currentTargetY - nextY;
							deltaX = currentTargetX - nextX;

							// 如果坐标差为0且路径为空，则放弃移动并返回下一个行为
							if ((deltaX == 0.000f) && (deltaY == 0.000f) && this.path.isEmpty()) {
								onMoveGiveUp(simulation); // 放弃移动
								return this.unit.pollNextOrderBehavior(simulation); // 获取下一个行为
							}

							// 打印新的目标点和坐标差
							System.out.println("new target: " + currentTargetX + "," + currentTargetY);
							System.out.println("new delta: " + deltaX + "," + deltaY);

							// 计算目标角度（弧度）
							goalAngleRad = Math.atan2(deltaY, deltaX);
							// 将弧度转换为角度
							goalAngle = (float) Math.toDegrees(goalAngleRad);

							// 确保角度为正数
							if (goalAngle < 0) {
								goalAngle += 360;
							}

							// 获取当前朝向
							facing = this.unit.getFacing();
							// 计算朝向与目标角度的差值
							delta = goalAngle - facing;

							// 调整差值范围在-180到180之间
							if (delta < -180) {
								delta = 360 + delta;
							}
							if (delta > 180) {
								delta = -360 + delta;
							}

							// 获取差值的绝对值
							absDelta = Math.abs(delta);

							// 如果差值大于等于推进窗口，则播放站立动画并返回当前行为
							if (absDelta >= propulsionWindow) {
								if (this.wasWithinPropWindow) {
									this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND,
											SequenceUtils.EMPTY, 1.0f, true);
								}
								this.wasWithinPropWindow = false;
								return this;
							}

						}
					}
				}
				// 如果不可行走
				else {
					// 如果跟随的单位不为空，则将目标位置设置为跟随单位的位置
					if (this.followUnit != null) {
						this.target.x = this.followUnit.getX();
						this.target.y = this.followUnit.getY();
					}

					// 如果当前没有进行路径寻找
					if (!this.pathfindingActive) {
						// 调用模拟器的findNaiveSlowPath方法来寻找路径
						// 参数包括当前单位、跟随单位、起始浮点坐标、目标位置、移动类型、碰撞大小、搜索周期小于4以及当前对象
						simulation.findNaiveSlowPath(this.unit, this.followUnit, startFloatingX, startFloatingY,
								this.target, movementType, collisionSize, this.searchCycles < 4, this);

						// 设置路径寻找为激活状态
						this.pathfindingActive = true;

						// 增加搜索周期计数
						this.searchCycles++;

						// 返回当前对象
						return this;
					}

				}
				// 调用单位的动画监听器播放行走动画，参数分别为：是否反向行走、行走速度、是否循环播放
				// 这行代码的目的是根据单位的当前速度播放相应的行走动画，并设置为循环播放
				this.unit.getUnitAnimationListener().playWalkAnimation(false, this.unit.getSpeed(), true);

				// 设置一个标志位，表示单位是否在道具窗口范围内
				// 这行代码的目的是记录单位是否处于可以互动的道具窗口范围内
				this.wasWithinPropWindow = true;

			}
			while (continueDistance > 0);
		}
		// 如果受阻
		else {
			// 如果在转向，播放站立动画
			if (this.wasWithinPropWindow) {
				this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f,
						true);
			}
			// 等待转向设置为false
			this.wasWithinPropWindow = false;
		}

		return this;
	}

	// 处理移动放弃逻辑
	private void onMoveGiveUp(final CSimulation simulation) {
		if (this.rangedBehavior != null) {
			this.rangedBehavior.endMove(simulation, true);
		}
	}

	// 目标访问重置器内部类
	private final class TargetVisitingResetter implements AbilityTargetVisitor<Void> {
		private int highlightOrderId;

		// 重置目标访问
		private TargetVisitingResetter reset(final int highlightOrderId) {
			this.highlightOrderId = highlightOrderId;
			return this;
		}

		@Override
		// 访问点目标
		public Void accept(final AbilityPointTarget target) {
			internalResetMove(this.highlightOrderId, target.x, target.y);
			return null;
		}

		@Override
		// 访问单位目标
		public Void accept(final CUnit target) {
			internalResetMove(this.highlightOrderId, target);
			return null;
		}

		@Override
		// 访问可摧毁目标
		public Void accept(final CDestructable target) {
			internalResetMove(this.highlightOrderId, target.getX(), target.getY());
			return null;
		}

		@Override
		// 访问物品目标
		public Void accept(final CItem target) {
			internalResetMove(this.highlightOrderId, target.getX(), target.getY());
			return null;
		}
	}

	@Override
	// 行为开始
	public void begin(final CSimulation game) {

	}

	@Override
	// 行为结束
	public void end(final CSimulation game, final boolean interrupted) {
		// 如果总是中断移动，则从寻路队列中移除当前对象，并将寻路活动状态设置为false
		if (ALWAYS_INTERRUPT_MOVE) {
			// 从寻路队列中移除当前对象
			game.removeFromPathfindingQueue(this);
			// 将寻路活动状态设置为false
			this.pathfindingActive = false;
		}
		// 如果当前对象有远程行为，则结束其移动
		if (this.rangedBehavior != null) {
			// 调用远程行为的endMove方法，传入游戏实例和是否被中断的标志
			this.rangedBehavior.endMove(game, interrupted);
		}

	}

	// 获取单位
	public CUnit getUnit() {
		return this.unit;
	}

	// 路径找到的处理
	public void pathFound(final List<Point2D.Float> waypoints, final CSimulation simulation) {
		// 禁用路径查找
		this.pathfindingActive = false;

		// 获取单位当前位置
		final float prevX = this.unit.getX();
		final float prevY = this.unit.getY();

		// 获取移动类型，如果没有设置，则默认为DISABLED
		MovementType movementType = this.unit.getMovementType();
		if (movementType == null) {
			movementType = MovementType.DISABLED;
		}
		// 如果移动类型为步行且禁用了碰撞，则将移动类型设置为步行无碰撞
		else if ((movementType == MovementType.FOOT) && this.disableCollision) {
			movementType = MovementType.FOOT_NO_COLLISION;
		}

		// 获取寻路网格和世界碰撞信息
		final PathingGrid pathingGrid = simulation.getPathingGrid();
		final CWorldCollision worldCollision = simulation.getWorldCollision();
		// 获取单位的碰撞尺寸
		final float collisionSize = this.unit.getUnitType().getCollisionSize();
		// 记录单位开始浮动的位置
		final float startFloatingX = prevX;
		final float startFloatingY = prevY;

		this.path = waypoints;
		if (this.firstPathfindJob) {
			// 如果是第一次寻路
			this.firstPathfindJob = false;
			System.out.println("初始化路径 " + this.path);
			// 检查是否需要平滑处理
			if (!this.path.isEmpty()) {
				float lastX = startFloatingX;
				float lastY = startFloatingY;
				float smoothingGroupStartX = startFloatingX;
				float smoothingGroupStartY = startFloatingY;
				final Point2D.Float firstPathElement = this.path.get(0);
				double totalPathDistance = firstPathElement.distance(lastX, lastY);
				lastX = firstPathElement.x;
				lastY = firstPathElement.y;
				int smoothingStartIndex = -1;
				// 遍历路径点，检查是否需要平滑处理
				for (int i = 0; i < (this.path.size() - 1); i++) {
					final Point2D.Float nextPossiblePathElement = this.path.get(i + 1);
					totalPathDistance += nextPossiblePathElement.distance(lastX, lastY);
					// 如果满足平滑条件，则记录开始平滑的索引
					if ((totalPathDistance < (1.15 * nextPossiblePathElement.distance(smoothingGroupStartX, smoothingGroupStartY)))
							&& pathingGrid.isPathable((smoothingGroupStartX + nextPossiblePathElement.x) / 2,
							(smoothingGroupStartY + nextPossiblePathElement.y) / 2, movementType)) {
						if (smoothingStartIndex == -1) {
							smoothingStartIndex = i;
						}
					}
					// 如果不满足平滑条件，则结束当前平滑组，并开始新的平滑组
					else {
						if (smoothingStartIndex != -1) {
							for (int j = i - 1; j >= smoothingStartIndex; j--) {
								this.path.remove(j);
							}
							i = smoothingStartIndex;
						}
						smoothingStartIndex = -1;
						final Point2D.Float smoothGroupNext = this.path.get(i);
						smoothingGroupStartX = smoothGroupNext.x;
						smoothingGroupStartY = smoothGroupNext.y;
						totalPathDistance = nextPossiblePathElement.distance(smoothGroupNext);
					}
					lastX = nextPossiblePathElement.x;
					lastY = nextPossiblePathElement.y;
				}
				// 处理最后一个平滑组
				if (smoothingStartIndex != -1) {
					for (int j = smoothingStartIndex; j < (this.path.size() - 1); j++) {
						final Point2D.Float removed = this.path.remove(j);
					}
				}
			}
		}
		// 如果路径为空或者寻路次数过多
		else if (this.path.isEmpty() || (this.searchCycles > 6)) {
			if (this.searchCycles > 9) {
				// 如果寻路次数超过9次，标记寻路失败
				this.pathfindingFailedGiveUp = true;
			} else {
				// 否则，设置放弃寻路的回合数
				this.giveUpUntilTurnTick = simulation.getGameTurnTick()
						+ (int) (5 / WarsmashConstants.SIMULATION_STEP_TIME);
			}
		}

	}

	@Override
	// 行为是否可中断
	public boolean interruptable() {
		return true;
	}

	@Override
	// 访问行为
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}
}
