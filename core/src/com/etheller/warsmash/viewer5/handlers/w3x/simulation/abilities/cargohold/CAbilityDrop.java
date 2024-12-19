package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.cargohold.CBehaviorDrop;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
// 表示一个能力掉落的类，继承自 AbstractGenericSingleIconNoSmartActiveAbility
public class CAbilityDrop extends AbstractGenericSingleIconNoSmartActiveAbility {
	private float castRange; // 技能施放范围
	private CBehaviorDrop behaviorDrop; // 掉落行为

	// 构造函数，初始化能力掉落的基本信息
	public CAbilityDrop(final int handleId, final War3ID code, final War3ID alias, final float castRange) {
		super(handleId, code, alias);
		this.castRange = castRange;
	}

	// 当能力被添加到单位时调用
	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behaviorDrop = new CBehaviorDrop(unit, this);
	}

	// 当能力从单位移除时调用
	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	// 在每个游戏tick中调用
	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	// 获取基础命令ID
	@Override
	public int getBaseOrderId() {
		return OrderIds.unloadall;
	}

	// 开始行为，针对单个目标
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	// 开始行为，针对指定的点
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return this.behaviorDrop.reset(point);
	}

	// 无目标开始行为
	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	// 检查能力是否可以使用
	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	// 从队列中取消能力的调用
	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	// 单位死亡时调用
	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

	// 检查技能是否为开关类型
	@Override
	public boolean isToggleOn() {
		return false;
	}

	// 检查可以使用的目标
	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	// 检查可以使用的点目标
	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (!unit.isMovementDisabled() || unit.canReach(target, this.castRange)) {
			receiver.targetOk(target);
		}
		else {
			receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
		}
	}

	// 检查没有目标的可用性
	@Override
	protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	// 获取施放范围
	public float getCastRange() {
		return this.castRange;
	}

	// 设置施放范围
	public void setCastRange(final float castRange) {
		this.castRange = castRange;
	}

	// 检查技能是否为物理类型
	@Override
	public boolean isPhysical() {
		return false;
	}

	// 检查技能是否为通用类型
	@Override
	public boolean isUniversal() {
		return false;
	}

	// 获取能力类别
	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.CORE;
	}
}
