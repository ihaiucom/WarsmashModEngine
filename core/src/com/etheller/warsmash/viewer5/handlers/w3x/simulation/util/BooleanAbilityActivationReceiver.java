package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.warsmash.util.War3ID;
/**
 * BooleanAbilityActivationReceiver 类实现了 AbilityActivationReceiver 接口
 * 用于处理能力激活的状态（是否可用）。
 */
public class BooleanAbilityActivationReceiver implements AbilityActivationReceiver {
	public static final BooleanAbilityActivationReceiver INSTANCE = new BooleanAbilityActivationReceiver();
	private boolean ok;

	@Override
	/**
	 * 标记当前能力为可用。
	 */
	public void useOk() {
		this.ok = true;
	}

	@Override
	/**
	 * 处理未知原因导致的不可用状态。
	 */
	public void unknownReasonUseNotOk() {
		this.ok = false;
	}

	@Override
	/**
	 * 处理当前能力未激活的状态。
	 */
	public void notAnActiveAbility() {
		this.ok = false;
	}

	@Override
	/**
	 * 处理缺少需求的情况。
	 */
	public void missingRequirement(final War3ID type, final int level) {
		this.ok = false;
	}

	@Override
	/**
	 * 处理缺少英雄等级要求的情况。
	 */
	public void missingHeroLevelRequirement(final int level) {
		this.ok = false;
	}

	@Override
	/**
	 * 处理没有可用的英雄技能点的情况。
	 */
	public void noHeroSkillPointsAvailable() {
		this.ok = false;
	}

	@Override
	/**
	 * 处理技术树达到最大限制的情况。
	 */
	public void techtreeMaximumReached() {
		this.ok = false;
	}

	@Override
	/**
	 * 处理技术项目已在进行中的情况。
	 */
	public void techItemAlreadyInProgress() {
		this.ok = false;
	}

	@Override
	/**
	 * 处理当前能力被禁用的情况。
	 */
	public void disabled() {
		this.ok = false;
	}

	@Override
	/**
	 * 处理冷却时间未准备好的情况。
	 */
	public void cooldownNotYetReady(final float cooldownRemaining, final float cooldownMax) {
		this.ok = false;
	}

	@Override
	/**
	 * 处理没有剩余次数的情况。
	 */
	public void noChargesRemaining() {
		this.ok = false;
	}

	@Override
	/**
	 * 处理激活检查失败的情况。
	 */
	public void activationCheckFailed(String commandStringErrorKey) {
		this.ok = false;
	}

	/**
	 * 返回当前能力状态是否可用。
	 * @return true 如果能力可用，false 如果不可用。
	 */
	public boolean isOk() {
		return this.ok;
	}

}

