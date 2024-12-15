package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

// 伤害修改器结果
public class CUnitAttackDamageTakenModificationListenerDamageModResult {
	private float baseDamage; // 基本伤害
	private float bonusDamage; // 额外伤害
	private float damageMultiplier; // 伤害倍率
	
	public CUnitAttackDamageTakenModificationListenerDamageModResult(float baseDamage, float bonusDamage) {
		this.baseDamage = baseDamage;
		this.bonusDamage = bonusDamage;
		this.damageMultiplier = 1;
		
	}

	public float getBaseDamage() {
		return baseDamage;
	}

	public void setBaseDamage(float baseDamage) {
		this.baseDamage = baseDamage;
	}

	public float getBonusDamage() {
		return bonusDamage;
	}

	public void setBonusDamage(float bonusDamage) {
		this.bonusDamage = bonusDamage;
	}

	public void addBonusDamage(float bonusDamage) {
		this.bonusDamage += bonusDamage;
	}

	public float getDamageMultiplier() {
		return damageMultiplier;
	}

	public void setDamageMultiplier(float damageMultiplier) {
		this.damageMultiplier = damageMultiplier;
	}

	public void addDamageMultiplier(float damageMultiplier) {
		this.damageMultiplier *= damageMultiplier;
	}

	// 计算最终伤害
	public float computeFinalDamage() {
		return (baseDamage * damageMultiplier) + bonusDamage;
	}
}
