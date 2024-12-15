package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.warsmash.util.War3ID;

// 能力激活接收器接口
public interface AbilityActivationReceiver {
	// 使用成功的方法
	void useOk();

	// 使用失败的原因未知
	void unknownReasonUseNotOk();

	// 不是一个主动能力
	void notAnActiveAbility();

	// 缺少要求的方法，返回类型和等级
	void missingRequirement(War3ID type, int level);

	// 缺少英雄等级要求的方法，返回等级
	void missingHeroLevelRequirement(int level);

	// 没有可用的英雄技能点
	void noHeroSkillPointsAvailable();

	// 被禁用的方法
	void disabled();

	// 达到科技树最大限制
	void techtreeMaximumReached();

	// 科技项目已经在进行中
	void techItemAlreadyInProgress();

	// 冷却时间未准备好，返回剩余冷却时间和冷却总时间
	void cooldownNotYetReady(float cooldownRemaining, float cooldown);

	// 没有剩余的使用次数
	void noChargesRemaining();

	// 激活检查失败，返回错误信息
	void activationCheckFailed(String commandStringErrorKey);
}

