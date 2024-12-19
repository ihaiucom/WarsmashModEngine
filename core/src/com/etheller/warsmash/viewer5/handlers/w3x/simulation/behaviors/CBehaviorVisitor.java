package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

// 访问行为的目标（单位）接口
public interface CBehaviorVisitor<T> {
	T accept(CBehavior target);

	T accept(CRangedBehavior target);

}
