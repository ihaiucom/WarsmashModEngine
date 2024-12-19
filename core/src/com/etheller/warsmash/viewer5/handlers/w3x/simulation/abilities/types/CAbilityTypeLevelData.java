package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

// 表示能力类型级别数据的类
public class CAbilityTypeLevelData {
	// 允许的目标类型
	private final EnumSet<CTargetType> targetsAllowed;

	// 构造函数，初始化允许的目标类型
	public CAbilityTypeLevelData(final EnumSet<CTargetType> targetsAllowed) {
		this.targetsAllowed = targetsAllowed;
	}

	// 获取允许的目标类型
	public EnumSet<CTargetType> getTargetsAllowed() {
		return this.targetsAllowed;
	}
}
