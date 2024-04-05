package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.projectile;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.projectile.ABProjectileCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public class ABActionSetProjectileTarget implements ABAction {

	private ABProjectileCallback projectile;
	private ABUnitCallback target;
	
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {

		CProjectile proj = projectile.callback(game, caster, localStore, castId);

		proj.setTarget(target.callback(game, caster, localStore, castId));
	}
}
