package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast;

public enum AutocastType {
	NONE,  //表示没有自动施法的状态。
	LOWESTHP,  //表示目标中生命值最低的单位。
	HIGESTHP,  //表示目标中生命值最高的单位。
	ATTACKTARGETING,  //表示正在攻击目标的单位。
	ATTACKINGALLY,  //表示正在攻击友方单位的情况下进行自动施法。
	ATTACKINGENEMY,  //表示正在攻击敌方单位的情况下进行自动施法。
	NEARESTVALID,  //表示寻找最近的有效目标进行自动施法。
	NEARESTENEMY,  //表示寻找最近的敌方单位进行自动施法。
	NOTARGET;  //表示没有有效目标的状态。
}
