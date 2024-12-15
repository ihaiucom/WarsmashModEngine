package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

// 飘字
public enum TextTagConfigType {
	GOLD("Gold"), //表示金币，对应的字符串值是 "Gold"。
	LUMBER("Lumber"), //表示木材，对应的字符串值是 "Lumber"。
	GOLD_BOUNTY("Bounty"), //表示金币奖励，对应的字符串值是 "Bounty"。
	LUMBER_BOUNTY("LumberBounty"), //表示木材奖励，对应的字符串值是 "LumberBounty"。
	XP("XP"), //表示经验值，对应的字符串值是 "XP"。
	MISS_TEXT("MissText"), //表示未命中文本，对应的字符串值是 "MissText"。
	CRITICAL_STRIKE("CriticalStrike"), //表示暴击，对应的字符串值是 "CriticalStrike"。
	SHADOW_STRIKE("ShadowStrike"), //表示暗影打击，对应的字符串值是 "ShadowStrike"。
	MANA_BURN("ManaBurn"), //表示法力燃烧，对应的字符串值是 "ManaBurn"。
	BASH("Bash"); //表示重击，对应的字符串值是 "Bash"。
	
	private final String key;

	TextTagConfigType(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
