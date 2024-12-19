package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityBlightedGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorAcolyteHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver.TeamType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
/**
 * CAbilityAcolyteHarvest 类表示一种特定的能力，继承自 AbstractGenericSingleIconActiveAbility。
 * 该能力用于管理对被诅咒金矿的收获操作。
 */
public class CAbilityAcolyteHarvest extends AbstractGenericSingleIconActiveAbility {
	private float castRange;
	private float duration;
	private CBehaviorAcolyteHarvest behaviorAcolyteHarvest;

	/**
	 * 构造函数，用于初始化 CAbilityAcolyteHarvest 的实例。
	 *
	 * @param handleId 处理标识符
	 * @param code 能力代码
	 * @param alias 能力别名
	 * @param castRange 施法范围
	 * @param duration 持续时间
	 */
	public CAbilityAcolyteHarvest(final int handleId, final War3ID code, final War3ID alias, final float castRange, final float duration) {
		super(handleId, code, alias);
		this.castRange = castRange;
		this.duration = duration;
	}

	/**
	 * 当能力被添加到单位时调用，用于初始化行为。
	 *
	 * @param game 游戏模拟
	 * @param unit 受影响的单位
	 */
	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behaviorAcolyteHarvest = new CBehaviorAcolyteHarvest(unit, this);
	}

	/**
	 * 当能力从单位上移除时调用。
	 *
	 * @param game 游戏模拟
	 * @param unit 受影响的单位
	 */
	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	/**
	 * 每次游戏更新时调用，用于处理每个 Tick 的逻辑。
	 *
	 * @param game 游戏模拟
	 * @param unit 受影响的单位
	 */
	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	/**
	 * 开始施放能力，并返回行为。
	 *
	 * @param game 游戏模拟
	 * @param caster 施法单位
	 * @param orderId 订单标识
	 * @param target 目标单位
	 * @return 开始行为
	 */
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return this.behaviorAcolyteHarvest.reset(target);
	}

	/**
	 * 开始施放能力，针对指定的点目标，并返回行为。
	 *
	 * @param game 游戏模拟
	 * @param caster 施法单位
	 * @param orderId 订单标识
	 * @param point 目标点
	 * @return 开始行为
	 */
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return caster.pollNextOrderBehavior(game);
	}

	/**
	 * 开始施放能力，且不针对任何目标，并返回行为。
	 *
	 * @param game 游戏模拟
	 * @param caster 施法单位
	 * @param orderId 订单标识
	 * @return 开始行为
	 */
	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return caster.pollNextOrderBehavior(game);
	}

	/**
	 * 获取能力的基础订单标识。
	 *
	 * @return 基础订单标识
	 */
	@Override
	public int getBaseOrderId() {
		return OrderIds.acolyteharvest;
	}

	/**
	 * 检查能力是否为切换状态。
	 *
	 * @return 如果能力是切换状态，返回 true；否则返回 false
	 */
	@Override
	public boolean isToggleOn() {
		return false;
	}

	/**
	 * 检查单位是否可以使用该能力，内部使用。
	 *
	 * @param game 游戏模拟
	 * @param unit 施法单位
	 * @param orderId 订单标识
	 * @param receiver 能力激活接收者
	 */
	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	/**
	 * 检查单位是否可以将该能力的目标锁定在指定小部件上，内部使用。
	 *
	 * @param game 游戏模拟
	 * @param unit 施法单位
	 * @param orderId 订单标识
	 * @param target 目标小部件
	 * @param receiver 目标检查接收者
	 */
	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (target instanceof CUnit) {
			final CUnit targetUnit = (CUnit) target;
			boolean isBlightedMine = false;
			for (final CAbility ability : targetUnit.getAbilities()) {
				if (ability instanceof CAbilityBlightedGoldMine) {
					isBlightedMine = true;
				}
			}
			if (isBlightedMine) {
				if (targetUnit.getPlayerIndex() == unit.getPlayerIndex()) {
					receiver.targetOk(target);
				}
				else {
					receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_USE_A_MINE_CONTROLLED_BY_ANOTHER_PLAYER);
				}
			}
			else {
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_HAUNTED_GOLD_MINE);
			}
		}
		else {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
		}
	}

	/**
	 * 检查单位是否可以智能目标锁定，内部使用。
	 *
	 * @param game 游戏模拟
	 * @param unit 施法单位
	 * @param orderId 订单标识
	 * @param target 目标小部件
	 * @param receiver 目标检查接收者
	 */
	@Override
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		innerCheckCanTarget(game, unit, orderId, target, receiver);
	}

	/**
	 * 检查单位是否可以将能力的目标锁定在指定点目标上，内部使用。
	 *
	 * @param game 游戏模拟
	 * @param unit 施法单位
	 * @param orderId 订单标识
	 * @param target 目标点
	 * @param receiver 目标检查接收者
	 */
	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	/**
	 * 检查单位是否可以智能目标锁定在指定点目标上，内部使用。
	 *
	 * @param game 游戏模拟
	 * @param unit 施法单位
	 * @param orderId 订单标识
	 * @param target 目标点
	 * @param receiver 目标检查接收者
	 */
	@Override
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	/**
	 * 检查单位是否可以在没有指定目标的情况下使用该能力，内部使用。
	 *
	 * @param game 游戏模拟
	 * @param unit 施法单位
	 * @param orderId 订单标识
	 * @param receiver 目标检查接收者
	 */
	@Override
	protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	/**
	 * 从队列中取消该能力的施放。
	 *
	 * @param game 游戏模拟
	 * @param unit 施法单位
	 * @param orderId 订单标识
	 */
	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	/**
	 * 设置施法范围。
	 *
	 * @param castRange 施法范围
	 */
	public void setCastRange(final float castRange) {
		this.castRange = castRange;
	}

	/**
	 * 获取施法范围。
	 *
	 * @return 施法范围
	 */
	public float getCastRange() {
		return this.castRange;
	}

	/**
	 * 设置持续时间。
	 *
	 * @param duration 持续时间
	 */
	public void setDuration(final float duration) {
		this.duration = duration;
	}

	/**
	 * 获取持续时间。
	 *
	 * @return 持续时间
	 */
	public float getDuration() {
		return this.duration;
	}

	/**
	 * 检查该能力是否为物理类型。
	 *
	 * @return 如果是物理类型，返回 true；否则返回 false
	 */
	@Override
	public boolean isPhysical() {
		return true;
	}

	/**
	 * 检查该能力是否为通用类型。
	 *
	 * @return 如果是通用类型，返回 true；否则返回 false
	 */
	@Override
	public boolean isUniversal() {
		return false;
	}

	/**
	 * 获取能力分类。
	 *
	 * @return 能力分类
	 */
	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.CORE;
	}
}
