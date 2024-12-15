package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;
// 该类用于处理单位攻击效果的叠加监听器
public class CUnitAttackEffectListenerStacking {
	private boolean allowStacking; // 是否允许叠加
	private boolean allowSamePriorityStacking; // 是否允许相同优先级的叠加

	// 默认构造函数，开启叠加和相同优先级叠加
	public CUnitAttackEffectListenerStacking() {
		setAllowStacking(true);
		setAllowSamePriorityStacking(true);
	}

	// 带参数的构造函数，根据参数设置叠加状态
	public CUnitAttackEffectListenerStacking(boolean allowStacking) {
		setAllowStacking(allowStacking);
		setAllowSamePriorityStacking(true);
	}

	// 带参数的构造函数，根据参数设置叠加状态和相同优先级叠加状态
	public CUnitAttackEffectListenerStacking(boolean allowStacking, boolean allowSamePriorityStacking) {
		setAllowStacking(allowStacking);
		setAllowSamePriorityStacking(allowSamePriorityStacking);
	}

	// 返回是否允许叠加
	public boolean isAllowStacking() {
		return allowStacking;
	}

	// 设置是否允许叠加
	public void setAllowStacking(boolean allowStacking) {
		this.allowStacking = allowStacking;
	}

	// 返回是否允许相同优先级叠加
	public boolean isAllowSamePriorityStacking() {
		return allowSamePriorityStacking;
	}

	// 设置是否允许相同优先级叠加
	public void setAllowSamePriorityStacking(boolean allowSamePriorityStacking) {
		this.allowSamePriorityStacking = allowSamePriorityStacking;
	}

}
