package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;
/**
 * 表示一个用于检查目标是否仍然存活且可以被攻击的访问者类
 */
public final class AbilityTargetStillAliveAndTargetableVisitor implements AbilityTargetVisitor<Boolean> {
	private CSimulation simulation;
	private CUnit unit;
	// 允许的目标类型集合
	private EnumSet<CTargetType> targetsAllowed;

	/**
	 * 重置访问者的状态，以新的模拟环境、单位和允许的目标类型进行配置
	 * @param simulation 当前的模拟环境
	 * @param unit 当前单位
	 * @param targetsAllowed 允许的目标类型集合
	 * @return 当前实例以便于链式调用
	 */
	public AbilityTargetStillAliveAndTargetableVisitor reset(final CSimulation simulation, final CUnit unit,
			final EnumSet<CTargetType> targetsAllowed) {
		this.simulation = simulation;
		this.unit = unit;
		this.targetsAllowed = targetsAllowed;
		return this;
	}

	@Override
	/**
	 * 接受一个点目标，始终返回真
	 */
	public Boolean accept(final AbilityPointTarget target) {
		return Boolean.TRUE;
	}

	@Override
	/**
	 * 接受一个单位目标，检查其是否存活、可见并且可以被当前单位攻击
	 */
	public Boolean accept(final CUnit target) {
		return !target.isDead() && !target.isHidden()
				&& target.canBeTargetedBy(this.simulation, this.unit, this.targetsAllowed);
	}

	@Override
	/**
	 * 接受一个可破坏目标，检查其是否存活并且可以被当前单位攻击
	 */
	public Boolean accept(final CDestructable target) {
		return !target.isDead() && target.canBeTargetedBy(this.simulation, this.unit, this.targetsAllowed);
	}

	@Override
	/**
	 * 接受一个物品目标，检查其是否存活、可见并且可以被当前单位攻击
	 */
	public Boolean accept(final CItem target) {
		return !target.isDead() && !target.isHidden()
				&& target.canBeTargetedBy(this.simulation, this.unit, this.targetsAllowed);
	}

}
