package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

import com.etheller.interpreter.ast.util.CHandle;
// CAttackType 枚举表示不同类型的攻击
public enum CAttackType implements CodeKeyType, CHandle {
	UNKNOWN,  // 未知类型
	NORMAL,   // 正常攻击
	PIERCE,   // 穿刺攻击
	SIEGE,    // 围攻攻击
	SPELLS,   // 法术攻击
	CHAOS,    // 混沌攻击
	MAGIC,    // 魔法攻击
	HERO;     // 英雄攻击

	public static CAttackType[] VALUES = values();

	private String codeKey;  // 存储攻击类型的代码关键字
	private String damageKey; // 存储攻击类型的伤害关键字

	private CAttackType() {
		final String name = name();
		this.codeKey = name.charAt(0) + name.substring(1).toLowerCase();
		this.damageKey = this.codeKey;
	}

	@Override
	public String getCodeKey() {
		return this.codeKey;
	}

	public String getDamageKey() {
		return this.damageKey;
	}

	// 解析字符串为 CAttackType 枚举类型
	public static CAttackType parseAttackType(final String attackTypeString) {
		final String upperCaseAttackType = attackTypeString.toUpperCase();
		if ("SEIGE".equals(upperCaseAttackType)) {
			return SIEGE;
		}
		return valueOf(upperCaseAttackType);
	}

	@Override
	public int getHandleId() {
		return ordinal(); // 返回攻击类型的顺序
	}
}
