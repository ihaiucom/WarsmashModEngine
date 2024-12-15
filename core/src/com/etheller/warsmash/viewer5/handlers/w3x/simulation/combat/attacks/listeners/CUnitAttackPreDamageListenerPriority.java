package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;
/**
 * 枚举类：CUnitAttackPreDamageListenerPriority
 * 用于表示单位攻击前伤害监听器的优先级
 */
public enum CUnitAttackPreDamageListenerPriority {
	WINDWALK(0), // 与潜行或隐形攻击相关的特性。
	ACCURACY(1), // 与攻击的准确性相关的特性。
	CRITBASH(2), // 与暴击攻击相关的特性。
	MASKPLUS(3), // 可能与特殊效果或加成相关的特性。
	ORBSLOT1(4), // 这些常量代表不同的 orb 技能槽，也就是说可以有多种不同的效果可供选择。
	ORBSLOT2(5),
	ORBSLOT3(6),
	ORBSLOT4(7),
	ORBSLOT5(8),
	ORBSLOT6(9),
	FEEDBACK(10); // 表示某种反馈机制或效果。

	private int priority;

	/**
	 * 构造函数：CUnitAttackPreDamageListenerPriority
	 * 初始化优先级
	 * @param priority 优先级值
	 */
	CUnitAttackPreDamageListenerPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * 获取优先级
	 * @return 当前优先级值
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * 设置优先级
	 * @param priority 新的优先级值
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}


}

