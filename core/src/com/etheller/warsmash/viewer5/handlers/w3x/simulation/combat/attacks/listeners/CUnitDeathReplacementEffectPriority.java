package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

// 枚举类，表示单位死亡替换效果的优先级
public enum CUnitDeathReplacementEffectPriority {
	// 凤凰复活效果
	PHOENIXREVIVE(0),
	// 单位死亡时的通用动作
	GENERALONDEATHACTIONS(1),
	// 能力复活效果
	ABILITYREINCARNATION(2),
	// 物品复活效果
	ITEMREINCARNATION(3);

	private int priority;

	// 构造函数，初始化优先级
	CUnitDeathReplacementEffectPriority(int priority) {
		this.priority = priority;
	}
	// 获取优先级
	public int getPriority() {
		return priority;
	}
	// 设置优先级
	public void setPriority(int priority) {
		this.priority = priority;
	}
}
