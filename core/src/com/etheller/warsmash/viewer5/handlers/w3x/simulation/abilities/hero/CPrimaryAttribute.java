package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero;

// 主属性
public enum CPrimaryAttribute {
	STRENGTH, // 力量
	INTELLIGENCE, // 智力
	AGILITY; // 敏捷

	public static CPrimaryAttribute parsePrimaryAttribute(final String targetTypeString) {
		if (targetTypeString == null) {
			return STRENGTH;
		}
		switch (targetTypeString.toUpperCase()) {
		case "STR":
			return STRENGTH;
		case "INT":
			return INTELLIGENCE;
		case "AGI":
			return AGILITY;
		default:
			return STRENGTH;
		}
	}
}
