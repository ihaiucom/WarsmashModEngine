package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition;

import java.util.Map;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionIsUnitEqual implements ABCondition {

	private ABUnitCallback unit1;
	private ABUnitCallback unit2;

	@Override
	public boolean evaluate(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		CUnit lUnit = unit1.callback(game, caster, localStore);
		CUnit rUnit = unit2.callback(game, caster, localStore);
		if (lUnit == null) {
			if (rUnit == null) {
				return true;
			}
			return false;
		}
		return lUnit.equals(rUnit);
	}

}
