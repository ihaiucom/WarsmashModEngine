package com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing;

import java.util.EnumSet;

public enum CBuildingPathingType {
	BLIGHTED, // 被污染的区域，可能无法建造或行走
	UNBUILDABLE, // 无法建造的区域
	UNFLYABLE, // 无法飞行的区域
	UNWALKABLE, // 无法行走的区域
	UNAMPH, // 不能在水上建造的区域
	UNFLOAT, // 不能漂浮的区域
	BLOCKVISION; // 阻挡视线的区域

	public static CBuildingPathingType parsePathingType(final String typeString) {
		if ("_".equals(typeString) || "".equals(typeString)) {
			return null;
		}
		return valueOf(typeString.toUpperCase());
	}

	public static EnumSet<CBuildingPathingType> parsePathingTypeListSet(final String pathingListString) {
		final EnumSet<CBuildingPathingType> types = EnumSet.noneOf(CBuildingPathingType.class);
		for (final String type : pathingListString.split(",")) {
			final CBuildingPathingType parsedType = parsePathingType(type);
			if (parsedType != null) {
				types.add(parsedType);
			}
		}
		return types;
	}
}
