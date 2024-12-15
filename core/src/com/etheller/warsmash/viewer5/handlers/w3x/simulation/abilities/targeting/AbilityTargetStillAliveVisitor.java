package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

// 该类实现了AbilityTargetVisitor接口，用于检查目标是否仍然存活
public class AbilityTargetStillAliveVisitor implements AbilityTargetVisitor<Boolean> {
	public static final AbilityTargetStillAliveVisitor INSTANCE = new AbilityTargetStillAliveVisitor();

	// 接受AbilityPointTarget目标，始终返回true
	@Override
	public Boolean accept(final AbilityPointTarget target) {
		return Boolean.TRUE;
	}

	// 接受CUnit目标，检查目标是否存活且未隐藏
	@Override
	public Boolean accept(final CUnit target) {
		return !target.isDead() && !target.isHidden();
	}

	// 接受CDestructable目标，检查目标是否存活
	@Override
	public Boolean accept(final CDestructable target) {
		return !target.isDead();
	}

	// 接受CItem目标，检查目标是否存活且未隐藏
	@Override
	public Boolean accept(final CItem target) {
		return !target.isDead() && !target.isHidden();
	}

}
