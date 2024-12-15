package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting;

public interface AbilityTarget {
	float getX();

	float getY();

	// 访问获得有效目标
	<T> T visit(AbilityTargetVisitor<T> visitor);
}
