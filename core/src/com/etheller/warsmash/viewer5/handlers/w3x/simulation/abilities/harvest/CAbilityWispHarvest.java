package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest;

import java.util.EnumSet;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorWispHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
/**
 * CAbilityWispHarvest 类用于表示一种特定的能力，允许单位收获树木的资源。
 */
public class CAbilityWispHarvest extends AbstractGenericSingleIconActiveAbility {
	public static final EnumSet<CTargetType> TREE_ALIVE_TYPE_ONLY = EnumSet.of(CTargetType.TREE, CTargetType.ALIVE);

	private int lumberPerInterval;
	private float artAttachmentHeight;
	private float castRange;
	private float periodicIntervalLength;
	private int periodicIntervalLengthTicks;
	private CBehaviorWispHarvest behaviorWispHarvest;

	/**
	 * 构造函数，用于初始化 CAbilityWispHarvest 的实例。
	 *
	 * @param handleId  处理 ID
	 * @param code      能力代码
	 * @param alias     别名
	 * @param lumberPerInterval 每次时间间隔收获的木材量
	 * @param artAttachmentHeight 附加效果的高度
	 * @param castRange 能力施放范围
	 * @param periodicIntervalLength 周期性间隔长度
	 */
	public CAbilityWispHarvest(final int handleId, final War3ID code, final War3ID alias, final int lumberPerInterval,
			final float artAttachmentHeight, final float castRange, final float periodicIntervalLength) {
		super(handleId, code, alias);
		this.lumberPerInterval = lumberPerInterval;
		this.artAttachmentHeight = artAttachmentHeight;
		this.castRange = castRange;
		this.periodicIntervalLength = periodicIntervalLength;
		this.periodicIntervalLengthTicks = (int) (periodicIntervalLength / WarsmashConstants.SIMULATION_STEP_TIME);
	}

	@Override
	/**
	 * 当该能力被添加到单位时调用。
	 */
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behaviorWispHarvest = new CBehaviorWispHarvest(unit, this);
	}

	@Override
	/**
	 * 当该能力从单位中移除时调用。
	 */
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	@Override
	/**
	 * 每个游戏 tick 调用，用于更新能力状态。
	 */
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	/**
	 * 开始能力的执行，针对指定目标。
	 */
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return this.behaviorWispHarvest.reset(target);
	}

	@Override
	/**
	 * 开始能力的执行，针对指定位置。
	 */
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	/**
	 * 开始能力的执行，不针对任何目标。
	 */
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	/**
	 * 获取基础命令 ID。
	 */
	public int getBaseOrderId() {
		return isToggleOn() ? OrderIds.returnresources : OrderIds.wispharvest;
	}

	@Override
	/**
	 * 检查能力是否处于开启状态。
	 */
	public boolean isToggleOn() {
		return false;
	}

	@Override
	/**
	 * 检查单位是否可以使用该能力。
	 */
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	@Override
	/**
	 * 检查目标单位是否可以被该能力所针对。
	 */
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (target instanceof CDestructable) {
			if (target.canBeTargetedBy(game, unit, TREE_ALIVE_TYPE_ONLY, receiver)) {
				receiver.targetOk(target);
			}
		}
		else {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_TREE);
		}
	}

	@Override
	/**
	 * 检查智能目标是否可以被该能力所针对。
	 */
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		innerCheckCanTarget(game, unit, orderId, target, receiver);
	}

	@Override
	/**
	 * 检查针对点目标的能力条件。
	 */
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	/**
	 * 检查智能目标的条件是否满足。
	 */
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	/**
	 * 检查没有目标时的能力条件。
	 */
	protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		if ((orderId == OrderIds.returnresources) && isToggleOn()) {
			receiver.targetOk(null);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	/**
	 * 获取附加效果的高度。
	 *
	 * @return 附加效果的高度
	 */
	public float getArtAttachmentHeight() {
		return this.artAttachmentHeight;
	}

	/**
	 * 获取周期性间隔长度。
	 *
	 * @return 周期性间隔长度
	 */
	public float getPeriodicIntervalLength() {
		return this.periodicIntervalLength;
	}

	/**
	 * 获取周期性间隔长度的滴答数。
	 *
	 * @return 周期性间隔长度的滴答数
	 */
	public int getPeriodicIntervalLengthTicks() {
		return this.periodicIntervalLengthTicks;
	}

	/**
	 * 获取每个时间间隔收获的木材量。
	 *
	 * @return 每个时间间隔收获的木材量
	 */
	public int getLumberPerInterval() {
		return this.lumberPerInterval;
	}

	/**
	 * 获取能力施放范围。
	 *
	 * @return 能力施放范围
	 */
	public float getCastRange() {
		return this.castRange;
	}

	@Override
	/**
	 * 取消排队的能力时调用。
	 */
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	/**
	 * 设置每次时间间隔收获的木材量。
	 *
	 * @param lumberPerInterval 每次时间间隔收获的木材量
	 */
	public void setLumberPerInterval(final int lumberPerInterval) {
		this.lumberPerInterval = lumberPerInterval;
	}

	/**
	 * 设置附加效果的高度。
	 *
	 * @param artAttachmentHeight 附加效果的高度
	 */
	public void setArtAttachmentHeight(final float artAttachmentHeight) {
		this.artAttachmentHeight = artAttachmentHeight;
	}

	/**
	 * 设置能力施放范围。
	 *
	 * @param castRange 能力施放范围
	 */
	public void setCastRange(final float castRange) {
		this.castRange = castRange;
	}

	/**
	 * 设置周期性间隔长度。
	 *
	 * @param periodicIntervalLength 周期性间隔长度
	 */
	public void setPeriodicIntervalLength(final float periodicIntervalLength) {
		this.periodicIntervalLength = periodicIntervalLength;
		this.periodicIntervalLengthTicks = (int) (periodicIntervalLength / WarsmashConstants.SIMULATION_STEP_TIME);
	}

	@Override
	/**
	 * 检查能力是否为物理类型。
	 */
	public boolean isPhysical() {
		return true;
	}

	@Override
	/**
	 * 检查能力是否为通用类型。
	 */
	public boolean isUniversal() {
		return false;
	}

	@Override
	/**
	 * 获取能力类别。
	 */
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.CORE;
	}

}
