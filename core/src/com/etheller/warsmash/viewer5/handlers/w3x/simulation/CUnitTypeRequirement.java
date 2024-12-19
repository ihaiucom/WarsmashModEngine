package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import com.etheller.warsmash.util.War3ID;

// 单位类型要求
public class CUnitTypeRequirement {
	// 条件ID
	private final War3ID requirement;
	// 要求等级
	private final int requiredLevel;

	public CUnitTypeRequirement(final War3ID requirement, final int requiredLevel) {
		this.requirement = requirement;
		this.requiredLevel = requiredLevel;
	}

	public War3ID getRequirement() {
		return this.requirement;
	}

	public int getRequiredLevel() {
		return this.requiredLevel;
	}
}
