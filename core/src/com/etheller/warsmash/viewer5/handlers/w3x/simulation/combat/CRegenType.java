package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

// 枚举类型CRegenType，表示不同的生命恢复类型。
public enum CRegenType {
	NONE,  //表示没有生命恢复。
	ALWAYS,  //表示生命恢复始终有效。
	BLIGHT,  //可能表示在某种特定条件下的生命恢复。
	DAY,  //可能表示在白天时的生命恢复。
	NIGHT;  //可能表示在夜晚时的生命恢复。

	// 根据字符串解析生命恢复类型。
	public static CRegenType parseRegenType(final String typeString) {
		try {
			return valueOf(typeString.toUpperCase());
		}
		catch (final Exception exc) {
			exc.printStackTrace();
			return NONE;
		}
	}
}
