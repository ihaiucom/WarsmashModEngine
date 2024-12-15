package com.etheller.warsmash.viewer5.handlers.w3x.simulation.state;

import com.etheller.interpreter.ast.util.CHandle;

// CGameState 枚举类，用于定义游戏状态
public enum CGameState implements CHandle {
	DIVINE_INTERVENTION,
	DISCONNECTED,
	TIME_OF_DAY;

	// 存储所有状态的数组
	public static CGameState[] VALUES = values();

	// 实现 CHandle 接口的方法，返回当前状态的 ordinal 值
	@Override
	public int getHandleId() {
		return ordinal();
	}
}

