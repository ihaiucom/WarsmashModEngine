package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;

public enum NonStackingStatBuffType {
	MELEEATK, //近战攻击力
	MELEEATKPCT, //近战攻击力百分比
	RNGDATK, //射程攻击力
	RNGDATKPCT, //射程攻击力百分比
	ATKSPD, //攻击速度
	DEF, //防御
	DEFPCT, //防御百分比
	HPGEN, //生命值生成
	HPGENPCT, //生命值生成百分比
	MAXHPGENPCT, //最大生命值生成百分比
	MPGEN, //法力值生成
	MPGENPCT, //法力值生成百分比
	MAXMPGENPCT, //最大法力值生成百分比
	MVSPD, //移动速度
	MVSPDPCT, //移动速度百分比
	HPSTEAL, //生命偷取
	THORNS, //刺伤效果
	THORNSPCT, //刺伤效果百分比
	MAXHP, //最大生命值
	MAXHPPCT, //最大生命值百分比
	MAXMP, //最大法力值
	MAXMPPCT, //最大法力值百分比
	ALLATK, //所有攻击（用于解析）
	ALLATKPCT, //所有攻击百分比（用于解析）
}
