package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

// 如果没有在攻击范围就移动到目标点，并且每帧查找攻击目标，如果找到攻击目标就会进入攻击行为
// 如果已经在攻击范围但是没有攻击目标，就会停止行为
public class CBehaviorAttackMove implements CRangedBehavior {

	private final CUnit unit;
	private AbilityPointTarget target;
	private boolean justAutoAttacked = false;
	private boolean endedMove = false;

	public CBehaviorAttackMove(final CUnit unit) {
		this.unit = unit;
	}

	public CBehavior reset(final AbilityPointTarget target) {
		this.target = target;
		this.endedMove = false;
		return this;
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.attack;
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		// 查找可攻击的目标，并将目标设置给攻击行为，启动攻击行为
		if (this.justAutoAttacked = this.unit.autoAcquireAttackTargets(simulation, false)) {
			// kind of a hack
			return true;
		}
		return innerIsWithinRange(); // TODO this is not how it was meant to be used
	}

	private boolean innerIsWithinRange() {
		return this.unit.distance(this.target.x, this.target.y) <= 16f;
	}

	@Override
	public CBehavior update(final CSimulation simulation) {
		// 已经找到攻击目标，并且设置了攻击行为，所有直接范围当前攻击行为（就是unit.autoAcquireAttackTargets里设置的攻击行为）
		if (this.justAutoAttacked) {
			this.justAutoAttacked = false;
			return this.unit.getCurrentBehavior();
		}
		// 检测是否到了攻击范围
		if (innerIsWithinRange()) {
			// 如果到了攻击范围，就停止行为
			this.unit.setDefaultBehavior(this.unit.getStopBehavior());
			return this.unit.pollNextOrderBehavior(simulation);
		}
		// 否则，继续移动到攻击目标
		return this.unit.getMoveBehavior().reset(this.target, this, false);
	}

	@Override
	public void begin(final CSimulation game) {

	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public AbilityTarget getTarget() {
		return this.target;
	}

	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}

}
