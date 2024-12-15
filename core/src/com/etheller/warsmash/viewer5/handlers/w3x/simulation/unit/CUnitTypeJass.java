package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;

import com.etheller.interpreter.ast.util.CHandle;

public enum CUnitTypeJass implements CHandle {
	HERO, //英雄单位。
	DEAD, //死亡状态。
	STRUCTURE, //建筑单位。
	FLYING, //飞行单位。
	GROUND, //地面单位。
	ATTACKS_FLYING, //可以攻击飞行单位。
	ATTACKS_GROUND, //可以攻击地面单位。
	MELEE_ATTACKER, //近战攻击单位。
	RANGED_ATTACKER, //远程攻击单位。
	GIANT, //巨型单位。
	SUMMONED, //召唤单位。
	STUNNED, //被眩晕状态。
	PLAGUED, //被瘟疫影响。
	SNARED, //被减速状态。
	UNDEAD, //不死单位。
	MECHANICAL, //机械单位。
	PEON, //农民单位。
	SAPPER, //工兵单位。
	TOWNHALL, //城镇大厅。
	ANCIENT, //远古单位。
	TAUREN, //牛头人单位。
	POISONED, //中毒状态。
	POLYMORPHED, //被变形状态。
	SLEEPING, //睡眠状态。
	RESISTANT, //有抗性。
	ETHEREAL, //虚无单位。
	MAGIC_IMMUNE; //魔法免疫状态。

	public static CUnitTypeJass[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
