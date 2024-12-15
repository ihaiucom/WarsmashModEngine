package com.etheller.warsmash.viewer5.handlers.w3x.simulation.state;

import com.etheller.interpreter.ast.util.CHandle;

// CUnitState 枚举类，表示单位的状态，包括生命、最大生命、法力和最大法力。
public enum CUnitState implements CHandle {
	LIFE, // 生命
	MAX_LIFE, // 最大生命
	MANA, // 法力
	MAX_MANA; // 最大法力

	// 存储所有单位状态的数组
	public static CUnitState[] VALUES = values();

	// 获取当前单位状态的句柄ID
	@Override
	public int getHandleId() {
		return ordinal();
	}
}
