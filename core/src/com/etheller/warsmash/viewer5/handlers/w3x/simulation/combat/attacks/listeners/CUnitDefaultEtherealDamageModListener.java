package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

// 虚无状态 修改伤害为0
public class CUnitDefaultEtherealDamageModListener implements CUnitAttackDamageTakenModificationListener {
	public static CUnitDefaultEtherealDamageModListener INSTANCE = new CUnitDefaultEtherealDamageModListener();

	@Override
	public CUnitAttackDamageTakenModificationListenerDamageModResult onDamage(CSimulation game, CUnit attacker,
			CUnit target, boolean isAttack, boolean isRanged, CAttackType attackType, CDamageType damageType,
			CUnitAttackDamageTakenModificationListenerDamageModResult previousDamage) {
		// 检查是否是联盟
		boolean allied = game.getPlayer(attacker.getPlayerIndex()).hasAlliance(target.getPlayerIndex(), CAllianceType.PASSIVE);
		if (!allied || (allied && game.getGameplayConstants().isEtherealDamageBonusAlly())) {
			// 设置额外伤害加成比例
			if (attackType == CAttackType.MAGIC) { // 魔法攻击
				previousDamage.addDamageMultiplier(game.getGameplayConstants().getEtherealDamageBonusMagic());
			}
			if (attackType == CAttackType.SPELLS) { // 法术攻击
				previousDamage.addDamageMultiplier(game.getGameplayConstants().getEtherealDamageBonusSpells());
			}
		}
		// 如果是普通攻击， 且不是魔法伤害， 则直接将伤害设置为0
		if (damageType == CDamageType.NORMAL && attackType != CAttackType.MAGIC) {
			previousDamage.setBaseDamage(0);
			previousDamage.setBonusDamage(0);
			previousDamage.setDamageMultiplier(0);
		}
		
		return previousDamage;
	}
}
