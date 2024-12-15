package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

// 处理单位死亡替换堆叠的类
public class CUnitDeathReplacementStacking {
	private boolean allowStacking; // 是否允许堆叠
	private boolean allowSamePriorityStacking; // 是否允许相同优先级的堆叠

	// 默认构造函数，允许堆叠和相同优先级的堆叠
	public CUnitDeathReplacementStacking() {
		setAllowStacking(true);
		setAllowSamePriorityStacking(true);
	}

	// 带一个参数的构造函数，根据传入的参数设置是否允许堆叠
	public CUnitDeathReplacementStacking(boolean allowStacking) {
		setAllowStacking(allowStacking);
		setAllowSamePriorityStacking(true);
	}

	// 带两个参数的构造函数，根据传入的参数设置堆叠选项
	public CUnitDeathReplacementStacking(boolean allowStacking, boolean allowSamePriorityStacking) {
		setAllowStacking(allowStacking);
		setAllowSamePriorityStacking(allowSamePriorityStacking);
	}

	// 获取是否允许堆叠
	public boolean isAllowStacking() {
		return allowStacking;
	}

	// 设置是否允许堆叠
	public void setAllowStacking(boolean allowStacking) {
		this.allowStacking = allowStacking;
	}

	// 获取是否允许相同优先级的堆叠
	public boolean isAllowSamePriorityStacking() {
		return allowSamePriorityStacking;
	}

	// 设置是否允许相同优先级的堆叠
	public void setAllowSamePriorityStacking(boolean allowSamePriorityStacking) {
		this.allowSamePriorityStacking = allowSamePriorityStacking;
	}

}
