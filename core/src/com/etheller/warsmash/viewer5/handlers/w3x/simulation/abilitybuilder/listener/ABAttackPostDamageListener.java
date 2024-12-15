package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPostDamageListener;

// jass onHit 的动作监听 受击 回调
public class ABAttackPostDamageListener implements CUnitAttackPostDamageListener {

	private Map<String, Object> localStore;
	private List<ABAction> actions;
	
	private int triggerId = 0;
	
	public ABAttackPostDamageListener(Map<String, Object> localStore, List<ABAction> actions) {
		this.localStore = localStore;
		this.actions = actions;
	}
	
	@Override
	public void onHit(CSimulation simulation, CUnit attacker, AbilityTarget target, float damage) {
		this.triggerId++;
		localStore.put(ABLocalStoreKeys.ATTACKINGUNIT+triggerId, attacker);
		localStore.put(ABLocalStoreKeys.ATTACKEDUNIT+triggerId, target);
		localStore.put(ABLocalStoreKeys.TOTALDAMAGEDEALT+triggerId, damage);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(simulation, attacker, localStore, triggerId);
			}
		}
		localStore.remove(ABLocalStoreKeys.ATTACKINGUNIT+triggerId);
		localStore.remove(ABLocalStoreKeys.ATTACKEDUNIT+triggerId);
		localStore.remove(ABLocalStoreKeys.TOTALDAMAGEDEALT+triggerId);
	}

}
