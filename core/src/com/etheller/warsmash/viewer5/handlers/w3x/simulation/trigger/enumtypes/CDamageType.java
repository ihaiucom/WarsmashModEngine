package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CDamageType implements CHandle {
	// 枚举类型，表示各种状态或效果
	UNKNOWN,
	UNKNOWN_CODE_1,
	UNKNOWN_CODE_2,
	UNKNOWN_CODE_3,
	NORMAL,			// 普通效果
	ENHANCED,		// 强化效果
	UNKNOWN_CODE_6,
	UNKNOWN_CODE_7,
	FIRE,            // 火焰效果
	COLD,            // 寒冷效果
	LIGHTNING,       // 雷电效果
	POISON,          // 毒素效果
	DISEASE,         // 疾病效果
	DIVINE,          // 神圣效果
	MAGIC,           // 魔法效果
	SONIC,           // 声音效果
	ACID,            // 酸性效果
	FORCE,           // 力量效果
	DEATH,           // 死亡效果
	MIND,            // 心智效果
	PLANT,           // 植物效果
	DEFENSIVE,       // 防御效果
	DEMOLITION,      // 拆除效果
	SLOW_POISON,     // 渐进毒素效果
	SPIRIT_LINK,     // 灵魂连接效果
	SHADOW_STRIKE,   // 影子攻击效果
	UNIVERSAL;       // 普遍效果


	public static CDamageType[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
