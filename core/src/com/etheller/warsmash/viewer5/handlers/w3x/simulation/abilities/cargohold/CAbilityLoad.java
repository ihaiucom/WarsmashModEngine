package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold;

import java.util.Set;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityRanged;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.cargohold.CBehaviorLoad;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ExternStringMsgTargetCheckReceiver;

// 表示装载能力的类，继承自抽象类 AbstractGenericSingleIconActiveAbility
public class CAbilityLoad extends AbstractGenericSingleIconActiveAbility implements CAbilityRanged {
	private float castRange; // 施法范围
	private Set<War3ID> allowedUnitTypes; // 允许的单位类型集合
	private CBehaviorLoad behaviorLoad; // 装载行为

	// 构造函数，初始化施法范围和允许的单位类型
	public CAbilityLoad(final int handleId, final War3ID code, final War3ID alias, final float castRange,
			final Set<War3ID> allowedUnitTypes) {
		super(handleId, code, alias);
		this.castRange = castRange;
		this.allowedUnitTypes = allowedUnitTypes;
	}

	@Override
	// 在单位添加此能力时调用，初始化行为
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behaviorLoad = new CBehaviorLoad(unit, this);
	}

	@Override
	// 在单位移除此能力时调用
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	@Override
	// 每个游戏 tick 调用，执行能力的更新逻辑
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	// 获取基础的订单 ID
	public int getBaseOrderId() {
		return OrderIds.load;
	}

	@Override
	// 开始能力时调用，基于目标启动行为
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return this.behaviorLoad.reset(target);
	}

	@Override
	// 针对指定点开始能力
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	@Override
	// 没有目标时开始能力
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	@Override
	// 检查能力是否可以使用
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	@Override
	// 从队列中取消能力时调用
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	// 当单位死亡时调用
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

	@Override
	// 检查能力是否处于开启状态
	public boolean isToggleOn() {
		return false;
	}

	@Override
	// 检查目标单位是否可以被此能力选中
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if ((target instanceof CUnit)) {
			if (target.canBeTargetedBy(game, unit, unit.getCargoData().getTargetsAllowed(), receiver)) {
				if (target != unit) {
					if (((CUnit) target).getPlayerIndex() == unit.getPlayerIndex()) {
						if (this.allowedUnitTypes.isEmpty()
								|| this.allowedUnitTypes.contains(((CUnit) target).getTypeId())) {
							if (!unit.isMovementDisabled()
									|| unit.canReach(target, unit.getCargoData().getCastRange())) {
								receiver.targetOk(target);
							}
							else {
								receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
							}
						}
						else {
							receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_PEON);
						}
					}
					else {
						receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_ONE_OF_YOUR_OWN_UNITS);
					}
				}
				else {
					receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_SELF);
				}
			}
			// else receiver called by canBeTargetedBy(...)
		}
		else {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
		}
	}

	@Override
	// 针对指定点检查目标单位
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	// 检查无目标能力的目标
	protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	// 获取施法范围
	public float getCastRange() {
		return this.castRange;
	}

	// 设置施法范围
	public void setCastRange(final float castRange) {
		this.castRange = castRange;
	}

	// 获取允许的单位类型集合
	public Set<War3ID> getAllowedUnitTypes() {
		return this.allowedUnitTypes;
	}

	// 设置允许的单位类型集合
	public void setAllowedUnitTypes(final Set<War3ID> allowedUnitTypes) {
		this.allowedUnitTypes = allowedUnitTypes;
	}

	@Override
	// 智能目标检查
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		innerCheckCanTarget(game, unit, orderId, target, receiver);
	}

	@Override
	// 针对指定点的智能目标检查
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	// 从运输单位获取装载能力
	public static CAbilityLoad getTransportLoad(final CSimulation game, final CUnit caster, final CUnit transport,
			final boolean ignoreRange, final boolean ignoreDisabled) {
		// 遍历运输单位的能力
		for (final CAbility potentialLoadAbility : transport.getAbilities()) {
			// 找到第一个有效的装载能力
			if (potentialLoadAbility instanceof CAbilityLoad) {
				final CAbilityLoad abilityLoad = (CAbilityLoad) potentialLoadAbility;
				// 检测转载能力是否可用
				final BooleanAbilityActivationReceiver transportUnitReceiver = BooleanAbilityActivationReceiver.INSTANCE;
				abilityLoad.checkCanUse(game, transport, OrderIds.smart, transportUnitReceiver);
				// NOTE: disabled load ability should enable later in case of under construction
				// entangled gold mine
				// 如果能力可以被使用，或者忽略禁用状态
				if (transportUnitReceiver.isOk() || (ignoreDisabled && abilityLoad.isDisabled())) {
					final ExternStringMsgTargetCheckReceiver<CWidget> transportUnitTargetCheckReceiver = ExternStringMsgTargetCheckReceiver
							.getInstance();
					// 检测转载单位是否可以被目标单位选中
					abilityLoad.checkCanTarget(game, transport, OrderIds.smart, caster,
							transportUnitTargetCheckReceiver.reset());
					// 如果目标单位可以被选中，或者忽略施法范围
					if ((transportUnitTargetCheckReceiver.getTarget() != null)
							|| (ignoreRange && (transportUnitTargetCheckReceiver
									.getExternStringKey() == CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE))) {
						// 返回装载能力
						return abilityLoad;
					}
				}
			}
		}
		// 如果没有找到有效的装载能力，返回 null
		return null;
	}

	@Override
	// 检查能力是否物理类型
	public boolean isPhysical() {
		return false;
	}

	@Override
	// 检查能力是否为通用类型
	public boolean isUniversal() {
		return false;
	}

	@Override
	// 获取能力的类别
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.CORE;
	}
}
