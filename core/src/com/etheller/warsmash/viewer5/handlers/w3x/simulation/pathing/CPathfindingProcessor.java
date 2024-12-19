package com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWorldCollision;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorMove;
public class CPathfindingProcessor {
	// 临时矩形，用于路径查找中的碰撞检测
	private static final Rectangle tempRect = new Rectangle();
	// 路径网格
	private final PathingGrid pathingGrid;
	// 世界碰撞检测
	private final CWorldCollision worldCollision;
	// 路径查找任务队列
	private final LinkedList<PathfindingJob> moveQueue = new LinkedList<>();
	// 当前任务修改状态的节点
	private final Node[][] nodes;
	// 拐角节点
	private final Node[][] cornerNodes;
	// 目标节点集合
	private final Node[] goalSet = new Node[4];
	// 目标数量，用于记录路径寻找过程中需要达到的目标点数
	private int goals = 0;
	// 路径寻找任务的ID，用于唯一标识每一次路径寻找任务
	private int pathfindJobId = 0;
	// 总迭代次数，记录路径寻找过程中的总迭代次数
	private int totalIterations = 0;
	// 总任务循环次数，记录路径寻找任务的总循环次数
	private int totalJobLoops = 0;
	// 路径网格单元格数量
	private final int pathingGridCellCount;

	// 构造方法，初始化路径查找处理器
	public CPathfindingProcessor(final PathingGrid pathingGrid, final CWorldCollision worldCollision) {
		this.pathingGrid = pathingGrid;
		this.worldCollision = worldCollision;
		// 初始化寻路网格节点数组
		this.nodes = new Node[pathingGrid.getHeight()][pathingGrid.getWidth()];
		// 初始化角落节点数组，大小比网格多一行一列
		this.cornerNodes = new Node[pathingGrid.getHeight() + 1][pathingGrid.getWidth() + 1];
		// 遍历每个网格，创建对应的Node对象，并设置其位置
		for (int i = 0; i < this.nodes.length; i++) {
			for (int j = 0; j < this.nodes[i].length; j++) {
				// 创建Node对象，并根据pathingGrid的坐标转换为世界坐标
				this.nodes[i][j] = new Node(new Point2D.Float(pathingGrid.getWorldX(j), pathingGrid.getWorldY(i)));
			}
		}

		// 遍历角节点数组的每一行
		for (int i = 0; i < this.cornerNodes.length; i++) {
			// 遍历角节点数组的每一列
			for (int j = 0; j < this.cornerNodes[i].length; j++) {
				// 创建一个新的节点对象
				// 使用pathingGrid的getWorldXFromCorner和getWorldYFromCorner方法获取世界坐标
				this.cornerNodes[i][j] = new Node(
						new Point2D.Float(pathingGrid.getWorldXFromCorner(j), pathingGrid.getWorldYFromCorner(i)));
			}
		}

		// 计算寻路网格中的单元格总数
		// 通过将网格的宽度乘以高度得到
		this.pathingGridCellCount = pathingGrid.getWidth() * pathingGrid.getHeight();

	}


	/**
	 * 寻找一条简单的慢速路径。
	 *
	 * @param ignoreIntersectionsWithThisUnit       忽略与此单位相交的点
	 * @param ignoreIntersectionsWithThisSecondUnit 忽略与第二个单位相交的点
	 * @param startX                                起始点的X坐标
	 * @param startY                                起始点的Y坐标
	 * @param goal                                  目标点
	 * @param movementType                          移动类型
	 * @param collisionSize                         碰撞大小
	 * @param allowSmoothing                        允许平滑路径
	 * @param queueItem                             移动队列项
	 */
	public void findNaiveSlowPath(final CUnit ignoreIntersectionsWithThisUnit,
								  final CUnit ignoreIntersectionsWithThisSecondUnit, final float startX, final float startY,
								  final Point2D.Float goal, final PathingGrid.MovementType movementType, final float collisionSize,
								  final boolean allowSmoothing, final CBehaviorMove queueItem) {
		// 将新的寻路任务加入到移动队列中
		this.moveQueue.offer(new PathfindingJob(ignoreIntersectionsWithThisUnit, ignoreIntersectionsWithThisSecondUnit,
				startX, startY, goal, movementType, collisionSize, allowSmoothing, queueItem));
	}


	// 从路径查找队列中移除指定的行为移动
	public void removeFromPathfindingQueue(final CBehaviorMove behaviorMove) {
		// TODO 因为一些Java的特性，这个remove的复杂度是O(N)，
		// 我们可以进行一些重构使其复杂度为O(1)，但我们在乎吗？
		final Iterator<PathfindingJob> iterator = this.moveQueue.iterator();
		while (iterator.hasNext()) {
			final PathfindingJob job = iterator.next();
			if (job.queueItem == behaviorMove) {
				iterator.remove();
			}
		}
	}

	/**
	 * 检查两个点之间是否存在可行走的路径。
	 *
	 * @param ignoreIntersectionsWithThisUnit       忽略与此单位相交的检查
	 * @param ignoreIntersectionsWithThisSecondUnit 忽略与第二个单位相交的检查
	 * @param startX                                起始点的X坐标
	 * @param startY                                起始点的Y坐标
	 * @param movementType                          移动类型
	 * @param collisionSize                         碰撞尺寸
	 * @param x                                     目标点的X坐标
	 * @param y                                     目标点的Y坐标
	 * @return 如果两点之间存在可行走的路径，则返回true，否则返回false
	 */
	private boolean pathableBetween(final CUnit ignoreIntersectionsWithThisUnit,
									final CUnit ignoreIntersectionsWithThisSecondUnit, final float startX, final float startY,
									final PathingGrid.MovementType movementType, final float collisionSize, final float x, final float y) {
		// 检查目标点是否可行走
		boolean isTargetPathable = this.pathingGrid.isPathable(x, y, movementType, collisionSize);
		// 检查起始点的X坐标与目标点的Y坐标形成的线段是否可行走
		boolean isStartXPathable = this.pathingGrid.isPathable(startX, y, movementType, collisionSize);
		// 检查起始点的Y坐标与目标点的X坐标形成的线段是否可行走
		boolean isStartYPathable = this.pathingGrid.isPathable(x, startY, movementType, collisionSize);
		// 动态检查目标点是否可行走，考虑忽略的单位
		boolean isTargetDynamicPathable = isPathableDynamically(x, y, ignoreIntersectionsWithThisUnit, ignoreIntersectionsWithThisSecondUnit, movementType);
		// 动态检查起始点的Y坐标与目标点的X坐标形成的线段是否可行走，考虑忽略的单位
		boolean isStartYDynamicPathable = isPathableDynamically(x, startY, ignoreIntersectionsWithThisUnit, ignoreIntersectionsWithThisSecondUnit, movementType);
		// 动态检查起始点的X坐标与目标点的Y坐标形成的线段是否可行走，考虑忽略的单位
		boolean isStartXDynamicPathable = isPathableDynamically(startX, y, ignoreIntersectionsWithThisUnit, ignoreIntersectionsWithThisSecondUnit, movementType);

		// 所有检查都通过，则两点之间路径可行走
		return isTargetPathable && isStartXPathable && isStartYPathable && isTargetDynamicPathable && isStartYDynamicPathable && isStartXDynamicPathable;
	}


	/**
	 * 判断在动态情况下，指定坐标(x, y)是否可通行。
	 *
	 * @param x                                     指定的x坐标
	 * @param y                                     指定的y坐标
	 * @param ignoreIntersectionsWithThisUnit       忽略与此单位相交的检查
	 * @param ignoreIntersectionsWithThisSecondUnit 忽略与第二个单位相交的检查
	 * @param movementType                          移动类型
	 * @return 如果坐标(x, y)可通行则返回true，否则返回false
	 */
	private boolean isPathableDynamically(final float x, final float y, final CUnit ignoreIntersectionsWithThisUnit,
										  final CUnit ignoreIntersectionsWithThisSecondUnit, final PathingGrid.MovementType movementType) {
		// 设置临时矩形中心为(x, y)，并检查除指定单位外是否有其他物体相交
		// 如果没有相交，则返回true表示可通行，否则返回false
		return !this.worldCollision.intersectsAnythingOtherThan(tempRect.setCenter(x, y),
				ignoreIntersectionsWithThisUnit, ignoreIntersectionsWithThisSecondUnit, movementType);
	}


	/**
	 * 判断给定的碰撞尺寸是否更适合用于角落。
	 * 该方法的逻辑是：将碰撞尺寸乘以2，然后除以32，再对结果取模2，
	 * 如果余数为1，则认为碰撞尺寸更适合用于角落。
	 *
	 * @param collisionSize 碰撞尺寸，以浮点数表示
	 * @return 如果碰撞尺寸更适合用于角落，则返回true；否则返回false
	 */
	public static boolean isCollisionSizeBetterSuitedForCorners(final float collisionSize) {
		return (((2 * (int) collisionSize) / 32) % 2) == 1;
	}


	// 计算节点的f值
	public double f(final Node n) {
		return n.g + h(n);
	}

	// 计算节点的g值
	public double g(final Node n) {
		return n.g;
	}

	// 检查节点是否为目标节点
	private boolean isGoal(final Node n) {
		for (int i = 0; i < this.goals; i++) {
			if (n == this.goalSet[i]) {
				return true;
			}
		}
		return false;
	}


	/**
	 * 计算节点n到目标集合中所有目标的估计距离，并返回最大值。
	 * 这个方法用于启发式搜索算法中，例如A*算法，以估计从当前节点到最近目标的距离。
	 * 注意：这个方法总是高估实际距离，因为它返回的是到最远目标的距离。
	 *
	 * @param n 当前节点
	 * @return 到目标集合中最远目标的距离
	 */
	public float h(final Node n) {
		float bestDistance = 0; // 初始化最佳距离为0
		for (int i = 0; i < this.goals; i++) { // 遍历所有目标
			final float possibleDistance = (float) n.point.distance(this.goalSet[i].point); // 计算当前节点到目标i的距离
			if (possibleDistance > bestDistance) { // 如果当前距离大于已记录的最佳距离
				bestDistance = possibleDistance; // 更新最佳距离
			}
		}
		return bestDistance; // 返回最大估计距离
	}


	public static final class Node {
		// 从哪个方向过来的
		public Direction cameFromDirection;
		// 节点的坐标
		private final Point2D.Float point;
		// f值
		private double f;
		// g值
		private double g;
		// 来自的节点
		private Node cameFrom;
		// 路径查找任务ID
		private int pathfindJobId;

		// 节点构造器
		private Node(final Point2D.Float point) {
			this.point = point;
		}


		/**
		 * 更新路径查找作业ID，并重置相关的路径查找参数。
		 *
		 * @param pathfindJobId 新的路径查找作业ID
		 */
		private void touch(final int pathfindJobId) {
			// 如果传入的路径查找作业ID与当前的作业ID不同
			if (pathfindJobId != this.pathfindJobId) {
				// 重置g值为正无穷，表示从起点到当前节点的最小代价尚未计算
				this.g = Float.POSITIVE_INFINITY;
				// 重置f值为正无穷，表示从起点到目标节点的估计总代价尚未计算
				this.f = Float.POSITIVE_INFINITY;
				// 重置cameFrom为null，表示当前节点的前一个节点尚未确定
				this.cameFrom = null;
				// 重置cameFromDirection为null，表示当前节点的前一个节点的方向尚未确定
				this.cameFromDirection = null;
				// 更新路径查找作业ID为新的作业ID
				this.pathfindJobId = pathfindJobId;
			}
		}

	}

	// 方向枚举
	private static enum Direction {
		  NORTH_WEST(-1, 1), // 西北方向， 左上
		  NORTH(0, 1),       // 北方向， 上
		  NORTH_EAST(1, 1),   // 东北方向， 右上
		  EAST(1, 0),        // 东方向, 右
		  SOUTH_EAST(1, -1),  // 东南方向， 右下
		  SOUTH(0, -1),      // 南方向， 下
		  SOUTH_WEST(-1, -1),// 西南方向， 左下
		  WEST(-1, 0);       // 西方向， 左


		// 方向的值集合
		public static final Direction[] VALUES = values();

		private final int xOffset;
		private final int yOffset;
		private final double length;

		// 方向枚举构造器
		private Direction(final int xOffset, final int yOffset) {
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			final double sqrt = Math.sqrt((xOffset * xOffset) + (yOffset * yOffset));
			this.length = sqrt;
		}
	}

	// 网格映射接口
	public static interface GridMapping {
		/**
		 * 获取世界坐标对应的网格X坐标
		 *
		 * @param grid   路径网格
		 * @param worldX 世界坐标X
		 * @return 网格X坐标
		 */
		int getX(PathingGrid grid, float worldX);

		/**
		 * 获取世界坐标对应的网格Y坐标
		 *
		 * @param grid   路径网格
		 * @param worldY 世界坐标Y
		 * @return 网格Y坐标
		 */

		int getY(PathingGrid grid, float worldY);

		/**
		 * 默认的网格映射方式，以单元格中心为坐标点
		 */
		public static final GridMapping CELLS = new GridMapping() {
			@Override
			public int getX(final PathingGrid grid, final float worldX) {
				return grid.getCellX(worldX);
			}

			@Override
			public int getY(final PathingGrid grid, final float worldY) {
				return grid.getCellY(worldY);
			}
		};

		/**
		 * 另一种网格映射方式，以单元格角落为坐标点
		 */
		public static final GridMapping CORNERS = new GridMapping() {
			@Override
			public int getX(final PathingGrid grid, final float worldX) {
				return grid.getCornerX(worldX);
			}

			@Override
			public int getY(final PathingGrid grid, final float worldY) {
				return grid.getCornerY(worldY);
			}
		};
	}

	// 更新路径查找处理器状态，执行路径查找
	public void update(final CSimulation simulation) {
		int workIterations = 0;
		// 主循环，处理移动队列中的所有路径查找任务
		JobsLoop: while (!this.moveQueue.isEmpty()) {
			// 增加总的工作循环次数
			this.totalJobLoops++;
			// 获取队列中的下一个路径查找任务
			final PathfindingJob job = this.moveQueue.peek();
			// 如果任务尚未开始
			if (!job.jobStarted) {
				// 初始化任务相关变量
				this.pathfindJobId++;
				this.totalIterations = 0;
				this.totalJobLoops = 0;
				job.jobStarted = true;
				// 打印任务开始信息
				System.out.println("starting job with smoothing=" + job.allowSmoothing);
				workIterations += 5; // 任务的预测成本设置
				// 设置目标位置
				job.goalX = job.goal.x;
				job.goalY = job.goal.y;

				// 设置碰撞墙壁的权重
				job.weightForHittingWalls = 1E9f;
				// 检查目标位置是否可行走，考虑静态和动态障碍物
				if (!this.pathingGrid.isPathable(job.goalX, job.goalY, job.movementType, job.collisionSize)
						// 如果静态路径不可行走，或者动态检查也不可行走
						|| !isPathableDynamically(job.goalX, job.goalY, job.ignoreIntersectionsWithThisUnit, job.ignoreIntersectionsWithThisSecondUnit, job.movementType)) {
					// 设置撞击墙壁的权重，增加路径搜索的难度
					job.weightForHittingWalls = 5E2f;
				}

				// 打印开始寻找路径的信息
				System.out.println("beginning findNaiveSlowPath for  " + job.startX + "," + job.startY + "," + job.goalX
						+ "," + job.goalY);
				// 如果起点和终点相同，则直接将空路径设置为找到的路径，并从队列中移除当前任务，继续下一次循环
				if ((job.startX == job.goalX) && (job.startY == job.goalY)) {
					// 设置找到的路径为空列表，并执行模拟
					job.queueItem.pathFound(Collections.emptyList(), simulation);
					// 从移动队列中移除当前任务
					this.moveQueue.poll();
					// 继续下一次循环
					continue JobsLoop;
				}
				// 设置临时矩形区域的大小，用于碰撞检测
				tempRect.set(0, 0, job.collisionSize * 2, job.collisionSize * 2);
				// 如果碰撞大小更适合角落，则使用角落节点
				if (isCollisionSizeBetterSuitedForCorners(job.collisionSize)) {
					job.searchGraph = this.cornerNodes; // 设置搜索图为角落节点
					job.gridMapping = GridMapping.CORNERS; // 设置网格映射为角落
					System.out.println("using corners"); // 打印使用角落节点的信息
				} else {
					job.searchGraph = this.nodes; // 否则使用普通节点
					job.gridMapping = GridMapping.CELLS; // 设置网格映射为单元格
					System.out.println("using cells"); // 打印使用单元格节点的信息
				}
				// 获取目标单元格的Y坐标
				final int goalCellY = job.gridMapping.getY(this.pathingGrid, job.goalY);
				// 获取目标单元格的X坐标
				final int goalCellX = job.gridMapping.getX(this.pathingGrid, job.goalX);
				// 获取最可能的目标节点
				final Node mostLikelyGoal = job.searchGraph[goalCellY][goalCellX];
				// 标记目标节点为已访问
				mostLikelyGoal.touch(this.pathfindJobId);
				// 计算目标节点到目标点的最佳距离
				final double bestGoalDistance = mostLikelyGoal.point.distance(job.goalX, job.goalY);
				// 初始化目标集合
				Arrays.fill(this.goalSet, null);
				// 初始化目标数量
				this.goals = 0;
				// 遍历目标单元格周围的单元格
				for (int i = goalCellX - 1; i <= (goalCellX + 1); i++) {
					for (int j = goalCellY - 1; j <= (goalCellY + 1); j++) {
						// 检查是否在搜索图的边界内
						if ((j >= 0) && (j <= job.searchGraph.length)) {
							if ((i >= 0) && (i < job.searchGraph[j].length)) {
								// 获取可能的目标节点
								final Node possibleGoal = job.searchGraph[j][i];
								// 标记可能的目标节点为已访问
								possibleGoal.touch(this.pathfindJobId);
								// 如果可能的目标节点到目标点的距离小于等于最佳距离，则将其添加到目标集合中
								if (possibleGoal.point.distance(job.goalX, job.goalY) <= bestGoalDistance) {
									this.goalSet[this.goals++] = possibleGoal;
								}
							}
						}
					}
				}

				// 获取起始点的网格坐标
				final int startGridY = job.gridMapping.getY(this.pathingGrid, job.startY);
				final int startGridX = job.gridMapping.getX(this.pathingGrid, job.startX);

				// 初始化开放集合，用于存储待处理的节点
				// 使用优先队列来根据节点的f值进行排序，确保每次都能快速找到f值最小的节点进行处理
				job.openSet = new PriorityQueue<>(new Comparator<Node>() {
					/**
					 * 比较两个节点的f值，用于决定它们在优先队列中的顺序
					 *
					 * @param a 第一个节点
					 * @param b 第二个节点
					 * @return 如果a的f值小于b的f值，返回负数；如果相等，返回0；如果a的f值大于b的f值，返回正数
					 */
					@Override
					public int compare(final Node a, final Node b) {
						return Double.compare(f(a), f(b));
					}
				});

				// 设置作业的起始点为搜索图中的对应点
				job.start = job.searchGraph[startGridY][startGridX];
				// 触发起始点的触摸事件，传入当前路径查找作业的ID
				job.start.touch(this.pathfindJobId);

				// 根据起始点的X坐标与作业起始X坐标的比较，设置起始网格的X坐标范围
				if (job.startX > job.start.point.x) {
					job.startGridMinX = startGridX;
					job.startGridMaxX = startGridX + 1;
				} else if (job.startX < job.start.point.x) {
					job.startGridMinX = startGridX - 1;
					job.startGridMaxX = startGridX;
				} else {
					job.startGridMinX = startGridX;
					job.startGridMaxX = startGridX;
				}

				// 根据起始点的Y坐标与作业起始Y坐标的比较，设置起始网格的Y坐标范围
				if (job.startY > job.start.point.y) {
					job.startGridMinY = startGridY;
					job.startGridMaxY = startGridY + 1;
				} else if (job.startY < job.start.point.y) {
					job.startGridMinY = startGridY - 1;
					job.startGridMaxY = startGridY;
				} else {
					job.startGridMinY = startGridY;
					job.startGridMaxY = startGridY;
				}

				// 遍历起始网格区域内的所有单元格
				for (int cellX = job.startGridMinX; cellX <= job.startGridMaxX; cellX++) {
					for (int cellY = job.startGridMinY; cellY <= job.startGridMaxY; cellY++) {
						// 检查单元格坐标是否在路径网格的有效范围内
						if ((cellX >= 0) && (cellX < this.pathingGrid.getWidth()) && (cellY >= 0)
								&& (cellY < this.pathingGrid.getHeight())) {
							// 获取当前单元格对应的节点
							final Node possibleNode = job.searchGraph[cellY][cellX];
							// 标记节点已被访问
							possibleNode.touch(this.pathfindJobId);
							// 获取节点的坐标
							final float x = possibleNode.point.x;
							final float y = possibleNode.point.y;
							// 检查从起始点到当前节点是否可通行
							if (pathableBetween(job.ignoreIntersectionsWithThisUnit,
									job.ignoreIntersectionsWithThisSecondUnit, job.startX, job.startY, job.movementType,
									job.collisionSize, x, y)) {
								// 计算节点的临时评分（从起始点到当前节点的距离）
								final double tentativeScore = possibleNode.point.distance(job.startX, job.startY);
								// 更新节点的g值和f值
								possibleNode.g = tentativeScore;
								possibleNode.f = tentativeScore + h(possibleNode);
								// 将节点加入开放集合
								job.openSet.add(possibleNode);
							} else {
								// 如果不可通行，则给予一个惩罚值作为临时评分
								final double tentativeScore = job.weightForHittingWalls;
								// 更新节点的g值和f值
								possibleNode.g = tentativeScore;
								possibleNode.f = tentativeScore + h(possibleNode);
								// 将节点加入开放集合
								job.openSet.add(possibleNode);
							}
						}
					}
				}

			}

			// 当openSet不为空时，继续执行A*寻路算法
			while (!job.openSet.isEmpty()) {
				// 从openSet中取出f值最小的节点作为当前节点
				Node current = job.openSet.poll();
				// 标记当前节点已被访问
				current.touch(this.pathfindJobId);
				// 如果当前节点是目标节点
				if (isGoal(current)) {
					// 初始化总路径
					final LinkedList<Point2D.Float> totalPath = new LinkedList<>();
					// 初始化上一个方向
					Direction lastCameFromDirection = null;

					// 如果当前节点到起点的路径是可通行的，并且允许平滑处理
					if ((current.cameFrom != null)
							&& pathableBetween(job.ignoreIntersectionsWithThisUnit,
									job.ignoreIntersectionsWithThisSecondUnit, current.point.x, current.point.y,
									job.movementType, job.collisionSize, job.goalX, job.goalY)
							&& pathableBetween(job.ignoreIntersectionsWithThisUnit,
									job.ignoreIntersectionsWithThisSecondUnit, current.cameFrom.point.x,
									current.cameFrom.point.y, job.movementType, job.collisionSize, current.point.x,
									current.point.y)
							&& pathableBetween(job.ignoreIntersectionsWithThisUnit,
									job.ignoreIntersectionsWithThisSecondUnit, current.cameFrom.point.x,
									current.cameFrom.point.y, job.movementType, job.collisionSize, job.goalX, job.goalY)
							&& job.allowSmoothing) {
						// 如果路径不被阻塞，进行基本平滑处理，使角色直接走向目标，省略最后的网格位置
						totalPath.addFirst(job.goal);
						current = current.cameFrom;
					}
					else {
						// 否则，将目标点和当前点添加到总路径中
						totalPath.addFirst(job.goal);
						totalPath.addFirst(current.point);
					}
					lastCameFromDirection = current.cameFromDirection;
					Node lastNode = null;
					int stepsBackward = 0;
					// 回溯路径，构建总路径
					while (current.cameFrom != null) {
						lastNode = current;
						current = current.cameFrom;
						// 如果遇到不同的方向或者必须通过第一个点完成最后的行程，则添加该点
						if ((lastCameFromDirection == null) || (current.cameFromDirection != lastCameFromDirection)
								|| (current.cameFromDirection == null)) {
							if ((current.cameFromDirection != null) || (lastNode == null)
									|| !pathableBetween(job.ignoreIntersectionsWithThisUnit,
											job.ignoreIntersectionsWithThisSecondUnit, job.startX, job.startY,
											job.movementType, job.collisionSize, current.point.x, current.point.y)
									|| !pathableBetween(job.ignoreIntersectionsWithThisUnit,
											job.ignoreIntersectionsWithThisSecondUnit, current.point.x, current.point.y,
											job.movementType, job.collisionSize, lastNode.point.x, lastNode.point.y)
									|| !pathableBetween(job.ignoreIntersectionsWithThisUnit,
											job.ignoreIntersectionsWithThisSecondUnit, job.startX, job.startY,
											job.movementType, job.collisionSize, lastNode.point.x, lastNode.point.y)
									|| !job.allowSmoothing) {
								// 如果不是第一个点，或者必须通过第一个点完成最后的行程，则添加该点
								totalPath.addFirst(current.point);
								lastCameFromDirection = current.cameFromDirection;
							}
						}
						// 如果回溯步数超过网格单元数，说明可能出现了无限循环错误
						if (stepsBackward > this.pathingGridCellCount) {
							new IllegalStateException(
									"PATHING SYSTEM ERROR: The path finding algorithm hit an infinite cycle at or near pt: "
											+ current.cameFrom.point
											+ ".\nThis means the A* search algorithm heuristic 'admissable' constraint was probably violated.\n\nUnit1:"
											+ CUnit.maybeMeaningfulName(job.ignoreIntersectionsWithThisUnit)
											+ "\nUnit2:"
											+ CUnit.maybeMeaningfulName(job.ignoreIntersectionsWithThisSecondUnit))
									.printStackTrace();
							totalPath.clear();
							break;
						}
						// 更新回溯节点和步数
						stepsBackward++;
					}
					// 将找到的路径反馈回调
					job.queueItem.pathFound(totalPath, simulation);
					// 从移动队列中移除当前任务
					this.moveQueue.poll();
					// 打印任务执行信息
					System.out.println("Task " + this.pathfindJobId + " took " + this.totalIterations
							+ " iterations and " + this.totalJobLoops + " job loops!");
					// 继续下一个任务循环
					continue JobsLoop;
				}

				// 遍历当前节点的所有邻居节点
				for (final Direction direction : Direction.VALUES) {
					// 计算邻居节点的坐标
					final float x = current.point.x + (direction.xOffset * 32);
					final float y = current.point.y + (direction.yOffset * 32);
					// 如果邻居节点在寻路网格中
					if (this.pathingGrid.contains(x, y)) {
						// 计算转向成本
						double turnCost;
						if ((current.cameFromDirection != null) && (direction != current.cameFromDirection)) {
							turnCost = 0.25;
						}
						else {
							turnCost = 0;
						}
						// 计算邻居节点的临时评分
						double tentativeScore = current.g + ((direction.length + turnCost) * 32);
						// 如果从当前节点到邻居节点的路径不可通行，则增加碰撞惩罚
						if (!pathableBetween(job.ignoreIntersectionsWithThisUnit,
								job.ignoreIntersectionsWithThisSecondUnit, current.point.x, current.point.y,
								job.movementType, job.collisionSize, x, y)) {
							tentativeScore += (direction.length) * job.weightForHittingWalls;
						}
						// 获取邻居节点
						final Node neighbor = job.searchGraph[job.gridMapping.getY(this.pathingGrid, y)][job.gridMapping
								.getX(this.pathingGrid, x)];
						// 标记邻居节点已被访问
						neighbor.touch(this.pathfindJobId);
						// 如果临时评分小于邻居节点的当前评分，则更新邻居节点的信息
						if (tentativeScore < neighbor.g) {
							neighbor.cameFrom = current;
							neighbor.cameFromDirection = direction;
							neighbor.g = tentativeScore;
							neighbor.f = tentativeScore + h(neighbor);
							// 如果邻居节点不在openSet中，则将其添加到openSet中
							if (!job.openSet.contains(neighbor)) {
								job.openSet.add(neighbor);
							}
						}
					}
				}
				// 更新工作迭代次数和总迭代次数
				workIterations++;
				this.totalIterations++;
				// 如果总迭代次数超过20000次，则中断循环
				if (this.totalIterations > 20000) {
					break;
				}
				// 如果工作迭代次数达到1500次，则中断任务循环
				if (workIterations >= 1500) {
					break JobsLoop;
				}
			}
			// 当找到路径时，将空路径列表和模拟对象传递给job.queueItem.pathFound方法
			// 这意味着没有找到有效的路径
			job.queueItem.pathFound(Collections.emptyList(), simulation);

			// 从移动队列中移除当前处理的项
			this.moveQueue.poll();

			// 打印任务信息，包括任务ID、迭代次数和作业循环次数
			System.out.println("任务 " + this.pathfindJobId + " 花费了 " + this.totalIterations + " 次迭代和 "
					+ this.totalJobLoops + " 次作业循环！");

		}
	}

	public static final class PathfindingJob {
		// 忽略与这个单位的交叉点
		private final CUnit ignoreIntersectionsWithThisUnit;
		// 忽略与第二个单位的交叉点
		private final CUnit ignoreIntersectionsWithThisSecondUnit;
		// 起始点的X坐标
		private final float startX;
		// 起始点的Y坐标
		private final float startY;
		// 目标点
		private final Point2D.Float goal;
		// 移动类型
		private final MovementType movementType;
		// 碰撞大小
		private final float collisionSize;
		// 是否允许平滑路径
		private final boolean allowSmoothing;
		// 移动队列项
		private final CBehaviorMove queueItem;
		// 任务是否已经开始
		private boolean jobStarted;

		// 目标点的Y坐标，公开访问
		public float goalY;
		// 目标点的X坐标，公开访问
		public float goalX;
		// 碰撞墙壁的权重
		public float weightForHittingWalls;

		// 搜索图
		Node[][] searchGraph;
		// 网格映射
		GridMapping gridMapping;
		// 开放集合
		PriorityQueue<Node> openSet;
		// 起始节点
		Node start;
		// 起始网格最小X坐标
		int startGridMinX;
		// 起始网格最小Y坐标
		int startGridMinY;
		// 起始网格最大X坐标
		int startGridMaxX;
		// 起始网格最大Y坐标
		int startGridMaxY;

		/**
		 * 构造一个新的寻路任务。
		 *
		 * @param ignoreIntersectionsWithThisUnit       忽略与这个单位的交叉点
		 * @param ignoreIntersectionsWithThisSecondUnit 忽略与第二个单位的交叉点
		 * @param startX                                起始点的X坐标
		 * @param startY                                起始点的Y坐标
		 * @param goal                                  目标点
		 * @param movementType                          移动类型
		 * @param collisionSize                         碰撞大小
		 * @param allowSmoothing                        是否允许平滑路径
		 * @param queueItem                             移动队列项
		 */
		public PathfindingJob(final CUnit ignoreIntersectionsWithThisUnit,
							  final CUnit ignoreIntersectionsWithThisSecondUnit, final float startX, final float startY,
							  final Point2D.Float goal, final PathingGrid.MovementType movementType, final float collisionSize,
							  final boolean allowSmoothing, final CBehaviorMove queueItem) {
			this.ignoreIntersectionsWithThisUnit = ignoreIntersectionsWithThisUnit;
			this.ignoreIntersectionsWithThisSecondUnit = ignoreIntersectionsWithThisSecondUnit;
			this.startX = startX;
			this.startY = startY;
			this.goal = goal;
			this.movementType = movementType;
			this.collisionSize = collisionSize;
			this.allowSmoothing = allowSmoothing;
			this.queueItem = queueItem;
			this.jobStarted = false;
		}
	}
}
