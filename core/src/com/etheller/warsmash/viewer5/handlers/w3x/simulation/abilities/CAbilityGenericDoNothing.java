package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericAliasedAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
/**
 * 不执行逻辑的能力
 * Represents an ability from the object data
 */
public class CAbilityGenericDoNothing extends AbstractGenericAliasedAbility {

	/**
	 * 构造函数，初始化能力对象
	 * @param code 能力代码
	 * @param alias 能力别名
	 * @param handleId 句柄ID
	 */
	public CAbilityGenericDoNothing(final War3ID code, final War3ID alias, final int handleId) {
		super(handleId, code, alias);
	}

	@Override
	/**
	 * 没目标的情况下施放该能力：不能执行
	 */
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();
	}

	@Override
	/**
	 * 目标是（单位/可破坏物/物品）：不能执行
	 */
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	/**
	 * 目标坐标点：不能执行
	 */
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	/**
	 * 没有目标：不能执行
	 */
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	/**
	 * 访问该能力的方法
	 */
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	/**
	 * 当该能力被添加到单位时的行为
	 */
	public void onAdd(final CSimulation game, final CUnit unit) {
	}

	@Override
	/**
	 * 当该能力从单位中移除时的行为
	 */
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	@Override
	/**
	 * 每个游戏循环中的行为
	 */
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	/**
	 * 在队列之前进行检查： 不能执行
	 */
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		return false;
	}

	@Override
	/**
	 * 开始能力行为
	 */
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		// 不能执行，执行下一个指令行为
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	/**
	 * 开始点目标的能力行为
	 */
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		// 不能执行，执行下一个指令行为
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	/**
	 * 开始无目标的能力行为
	 */
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		// 不能执行，执行下一个指令行为
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	/**
	 * 从队列中取消能力时的行为
	 */
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	/**
	 * 单位死亡时的行为
	 */
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

	@Override
	/**
	 * 检查能力是否为物理类型
	 */
	public boolean isPhysical() {
		return true;
	}

	@Override
	/**
	 * 检查能力是否为通用类型
	 */
	public boolean isUniversal() {
		return false;
	}

	@Override
	/**
	 * 获取能力类别
	 */
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.CORE;
	}

}
