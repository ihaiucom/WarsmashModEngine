package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

//  onDeath 死亡替换效果列表
public interface CUnitDeathReplacementEffect {
	public CUnitDeathReplacementStacking onDeath(final CSimulation simulation, final CUnit unit, final CUnit killer,
			final CUnitDeathReplacementResult result);
}
