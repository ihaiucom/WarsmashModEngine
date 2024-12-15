package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.warsmash.util.War3ID;
/**
 * 处理外部字符串消息激活的接收器类
 */
public class ExternStringMsgAbilityActivationReceiver implements AbilityActivationReceiver {
	/**
	 * 单例实例
	 */
	public static final ExternStringMsgAbilityActivationReceiver INSTANCE = new ExternStringMsgAbilityActivationReceiver();
	private String externStringKey;
	private boolean useOk = false;

	/**
	 * 重置接收器状态
	 * @return 当前实例
	 */
	public ExternStringMsgAbilityActivationReceiver reset() {
		this.externStringKey = null;
		this.useOk = false;
		return this;
	}

	/**
	 * 获取外部字符串键
	 * @return 外部字符串键
	 */
	public String getExternStringKey() {
		return externStringKey;
	}

	/**
	 * 检查是否使用成功
	 * @return 使用状态
	 */
	public boolean isUseOk() {
		return this.useOk;
	}

	@Override
	/**
	 * 设置使用成功
	 */
	public void useOk() {
		this.useOk = true;
	}

	@Override
	/**
	 * 处理未知原因使用不成功
	 */
	public void unknownReasonUseNotOk() {
		this.externStringKey = "Replaceme";
	}

	@Override
	/**
	 * 处理非活跃能力
	 */
	public void notAnActiveAbility() {
		this.externStringKey = ""; // no error message
	}

	@Override
	/**
	 * 处理缺少需求
	 */
	public void missingRequirement(final War3ID type, final int level) {
		this.externStringKey = ""; // no error message
	}

	@Override
	/**
	 * 处理缺少英雄等级要求
	 */
	public void missingHeroLevelRequirement(final int level) {
		this.externStringKey = ""; // no error message
	}

	@Override
	/**
	 * 处理英雄技能点不足
	 */
	public void noHeroSkillPointsAvailable() {
		this.externStringKey = ""; // no error message
	}

	@Override
	/**
	 * 处理冷却时间尚未准备好
	 */
	public void cooldownNotYetReady(final float cooldownRemaining, final float cooldown) {
		this.externStringKey = CommandStringErrorKeys.SPELL_IS_NOT_READY_YET;
	}

	@Override
	/**
	 * 处理达到了科技树的最大限制
	 */
	public void techtreeMaximumReached() {
		this.externStringKey = "";
	}

	@Override
	/**
	 * 处理科技项已经在进行中
	 */
	public void techItemAlreadyInProgress() {
		this.externStringKey = "";
	}

	@Override
	/**
	 * 处理被禁用状态
	 */
	public void disabled() {
		this.externStringKey = "";
	}

	@Override
	/**
	 * 处理没有剩余充能
	 */
	public void noChargesRemaining() {
		this.externStringKey = "";
	}

	@Override
	/**
	 * 处理激活检查失败
	 */
	public void activationCheckFailed(String commandStringErrorKey) {
		this.externStringKey = commandStringErrorKey;
	}
}
