package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CEffectType implements CHandle {
	EFFECT, //可能表示某种效果或事件。
	TARGET, //可能表示目标对象。
	CASTER, //可能表示施法者或发起者。
	SPECIAL, //可能表示特殊效果或条件。
	AREA_EFFECT, //可能表示区域效果。
	MISSILE, //可能表示投射物或导弹。
	LIGHTNING; //可能表示闪电效果。

	public static CEffectType[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
