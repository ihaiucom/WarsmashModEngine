package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidgetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
// 抽象范围行为：
// 检测是否在执行范围内，否则如果可以移动就先移动到指定范围，否则就执行指令队列里的下一个指令或默认行为。
// 检测朝向是否在目标方向，如果不在，就转向。
public abstract class CAbstractRangedBehavior implements CRangedBehavior {
	protected final CUnit unit; // 代表单位的属性

	public CAbstractRangedBehavior(final CUnit unit) {
		this.unit = unit; // 构造函数，初始化单位
	}

	protected AbilityTarget target; // 当前目标
	private boolean wasWithinPropWindow = false; // 是否需要考虑朝向
	private boolean wasInRange = false; // 是否在攻击范围内
	private boolean disableMove = false; // 移动是否被禁用
	private CBehaviorMove moveBehavior; // 移动行为

	// 内部重置方法，重置目标
	protected final CAbstractRangedBehavior innerReset(final AbilityTarget target) {
		return innerReset(target, false);
	}

	// 内部重置方法，重置目标和碰撞状态
	protected final CAbstractRangedBehavior innerReset(final AbilityTarget target, final boolean disableCollision) {
		this.target = target;
		this.wasWithinPropWindow = false;
		this.wasInRange = false;
		CBehaviorMove moveBehavior; // 声明移动行为
		// 不禁止移动
		if (!this.unit.isMovementDisabled()) {
			moveBehavior = this.unit.getMoveBehavior().reset(this.target, this, disableCollision); // 重置移动行为
		}
		// 禁止移动
		else {
			moveBehavior = null; // 如果单位不能移动，行为为空
		}
		this.moveBehavior = moveBehavior;
		return this;
	}

	// 抽象方法，已经到达范围内，更新逻辑
	protected abstract CBehavior update(CSimulation simulation, boolean withinFacingWindow);

	// 抽象方法，更新无效目标的逻辑
	protected abstract CBehavior updateOnInvalidTarget(CSimulation simulation);

	// 抽象方法，检查目标是否仍然有效
	protected abstract boolean checkTargetStillValid(CSimulation simulation);

	// 抽象方法，不在范围内时，如果可以移动，先执行移动到指定范围，这里就是重置参数
	protected abstract void resetBeforeMoving(CSimulation simulation);

	// 更新方法，返回行为状态
	// 如果目标有效，如果还没到达目标范围；如果可移动就进入移动行为，否则进入下一条指令
	// 如果达到范围内，如果不是可移动的就进入转向到目标
	@Override
	public final CBehavior update(final CSimulation simulation) {
		// 检查目标是否仍然有效
		if (!checkTargetStillValid(simulation)) {
			return updateOnInvalidTarget(simulation); // 如果目标无效，处理无效目标
		}
		// 判断当前状态是否在范围内
		if (!isWithinRange(simulation)) {
			// 如果不在范围内，且不能移动，就执行指令队列里的下一个指令或默认行为
			if ((this.moveBehavior == null) || this.disableMove) {
				return this.unit.pollNextOrderBehavior(simulation); // 检查单位的下一个行为
			}
			// 如果可以移动，就重置移动行为，执行移动行为
			this.wasInRange = false;
			resetBeforeMoving(simulation); // 在移动前重置状态
			return this.unit.getMoveBehavior(); // 返回当前的移动行为
		}
		// 在范围内
		this.wasInRange = true;
		// 如果不禁止移动
		if (!this.unit.isMovementDisabled()) {
			// 计算和目标的角度
			final float prevX = this.unit.getX();
			final float prevY = this.unit.getY();
			final float deltaX = this.target.getX() - prevX;
			final float deltaY = this.target.getY() - prevY;
			final double goalAngleRad = Math.atan2(deltaY, deltaX); // 计算目标角度
			float goalAngle = (float) Math.toDegrees(goalAngleRad);
			if (goalAngle < 0) {
				goalAngle += 360; // 确保角度在0到360之间
			}
			float facing = this.unit.getFacing(); // 获取当前朝向
			float delta = goalAngle - facing; // 计算角度差
			final float propulsionWindow = simulation.getGameplayConstants().getAttackHalfAngle(); // 获取攻击半角窗口
			final float turnRate = simulation.getUnitData().getTurnRate(this.unit.getTypeId()); // 获取转向速率

			if (delta < -180) {
				delta = 360 + delta;
			}
			if (delta > 180) {
				delta = -360 + delta;
			}
			final float absDelta = Math.abs(delta);

			// 根据角度更新单位的朝向
			if ((absDelta <= 1.0) && (absDelta != 0)) {
				this.unit.setFacing(goalAngle);
			}
			else {
				// 根据转向速率转向目标
				float angleToAdd = Math.signum(delta) * (float) Math.toDegrees(turnRate);
				if (absDelta < Math.abs(angleToAdd)) {
					angleToAdd = delta;
				}
				facing += angleToAdd;
				this.unit.setFacing(facing);
			}
			if (absDelta < propulsionWindow) {
				this.wasWithinPropWindow = true; // 在可攻击角度范围内
			}
			else {
				// 如果发生这种情况，单位的朝向错误，需要转向后才能移动。
				this.wasWithinPropWindow = false;
			}
		}
		else {
			this.wasWithinPropWindow = true; // 如果移动被禁用，不需要考虑朝向
		}

		return update(simulation, this.wasWithinPropWindow); // 返回更新后的行为
	}

	// 设置移动禁用状态
	public void setDisableMove(final boolean disableMove) {
		this.disableMove = disableMove;
	}

	@Override
	public AbilityTarget getTarget() {
		return this.target; // 获取当前目标
	}

	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this); // 接受访问者模式
	}

}
