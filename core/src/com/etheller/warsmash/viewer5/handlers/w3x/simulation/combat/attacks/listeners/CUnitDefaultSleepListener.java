package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuffType;

// 收到伤害, 移除睡眠状态
public class CUnitDefaultSleepListener implements CUnitAttackDamageTakenListener {
	public static CUnitDefaultSleepListener INSTANCE = new CUnitDefaultSleepListener();
	
	public CUnitDefaultSleepListener () {
	}
	
	@Override
	public void onDamage(final CSimulation simulation, CUnit attacker, CUnit target, boolean isAttack, boolean isRanged, CDamageType damageType, float damage, float bonusDamage, float trueDamage) {
		// 移除睡眠状态
		target.removeAllStateModBuffs(StateModBuffType.SLEEPING);
		// 重新计算睡眠状态
		target.computeUnitState(simulation, StateModBuffType.SLEEPING);
	}

}
