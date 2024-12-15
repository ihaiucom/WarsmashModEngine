package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

import java.util.EnumSet;
import java.util.List;

public enum CTargetType {
	AIR,//空气
	ALIVE,//活着
	ALLIES,//盟友
	DEAD,//死了
	DEBRIS,//碎片
	ENEMIES,//敌人
	GROUND,//地面
	HERO,//英雄
	INVULNERABLE,//无懈可击
	ITEM,//项目
	MECHANICAL,//机械的
	NEUTRAL,//中性
	NONE,//没有
	NONHERO,//非英雄
	NONSAPPER,//无症状
	NOTSELF,//不是自己
	ORGANIC,//有机
	PLAYERUNITS,//玩家
	SAPPER,//工兵
	SELF,//自我
	STRUCTURE,//结构
	TERRAIN,//地形
	TREE,//树
	VULNERABLE,//脆弱的
	WALL,//墙
	WARD,//病房
	ANCIENT,//古代
	NONANCIENT,//非古代
	FRIEND,//朋友
	BRIDGE,//桥
	DECORATION,//装饰
	// BELOW: internal values:,////下面：内部值：
	NON_MAGIC_IMMUNE,//非免疫性
	NON_ETHEREAL,//不存在


	;

	public static CTargetType parseTargetType(final String targetTypeString) {
		if (targetTypeString == null) {
			return null;
		}
		switch (targetTypeString.toLowerCase()) {
		case "air":
			return AIR;
		case "alive":
		case "aliv":
			return ALIVE;
		case "allies":
		case "alli":
		case "ally":
			return ALLIES;
		case "dead":
			return DEAD;
		case "debris":
		case "debr":
			return DEBRIS;
		case "enemies":
		case "enem":
		case "enemy":
			return ENEMIES;
		case "ground":
		case "grou":
			return GROUND;
		case "hero":
			return HERO;
		case "invulnerable":
		case "invu":
			return INVULNERABLE;
		case "item":
			return ITEM;
		case "mechanical":
		case "mech":
			return MECHANICAL;
		case "neutral":
		case "neut":
			return NEUTRAL;
		case "none":
			return NONE;
		case "nonhero":
		case "nonh":
			return NONHERO;
		case "nonsapper":
			return NONSAPPER;
		case "notself":
		case "nots":
			return NOTSELF;
		case "organic":
		case "orga":
			return ORGANIC;
		case "player":
		case "play":
			return PLAYERUNITS;
		case "sapper":
			return SAPPER;
		case "self":
			return SELF;
		case "structure":
		case "stru":
			return STRUCTURE;
		case "terrain":
		case "terr":
			return TERRAIN;
		case "tree":
			return TREE;
		case "vulnerable":
		case "vuln":
			return VULNERABLE;
		case "wall":
			return WALL;
		case "ward":
			return WARD;
		case "ancient":
			return ANCIENT;
		case "nonancient":
			return NONANCIENT;
		case "friend":
		case "frie":
			return FRIEND;
		case "bridge":
			return BRIDGE;
		case "decoration":
		case "deco":
			return DECORATION;
		default:
			return null;
		}
	}

	public static EnumSet<CTargetType> parseTargetTypeSet(final String targetTypeString) {
		final EnumSet<CTargetType> types = EnumSet.noneOf(CTargetType.class);
		for (final String type : targetTypeString.split(",")) {
			final CTargetType parsedType = parseTargetType(type);
			if (parsedType != null) {
				types.add(parsedType);
			}
		}
		return types;
	}

	public static EnumSet<CTargetType> parseTargetTypeSet(final List<String> targetTypeStrings) {
		final EnumSet<CTargetType> types = EnumSet.noneOf(CTargetType.class);
		for (final String type : targetTypeStrings) {
			final CTargetType parsedType = parseTargetType(type);
			if (parsedType != null) {
				types.add(parsedType);
			}
		}
		return types;
	}
}
