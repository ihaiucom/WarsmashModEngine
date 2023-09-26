package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitqueue;

import java.util.Map;
import java.util.Queue;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;

public abstract class ABUnitQueueCallback implements ABCallback {

	abstract public Queue<CUnit> callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId);
}
