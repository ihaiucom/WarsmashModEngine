package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import java.util.HashMap;
import java.util.Map;

public enum CUnitRace {
	HUMAN, // 人类
	ORC, // 兽人
	UNDEAD, // 亡灵
	NIGHTELF, // 精灵
	NAGA, // 纳迦
	CREEPS, // 怪物
	DEMON, // 恶魔
	CRITTERS, // 小动物
	OTHER; // 其他

	private static Map<String, CUnitRace> keyToRace = new HashMap<>();

	static {
		for (final CUnitRace race : CUnitRace.values()) {
			keyToRace.put(race.name(), race);
		}
	}

	public static CUnitRace parseRace(final String raceString) {
		final CUnitRace race = keyToRace.get(raceString.toUpperCase());
		if (race == null) {
			return OTHER;
		}
		return race;
	}
}