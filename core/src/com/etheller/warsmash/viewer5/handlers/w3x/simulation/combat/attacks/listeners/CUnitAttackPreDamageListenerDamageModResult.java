package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

// 攻击前处理结果 结构体
// 表示单位攻击前伤害修饰结果的类
public class CUnitAttackPreDamageListenerDamageModResult {
	private float baseDamage; // 基础伤害
	private float bonusDamage; // 额外伤害
	private float damageMultiplier; // 伤害乘数

	private boolean miss = false; // 是否丢失

	private boolean unlockBonus; // 是否解锁额外伤害
	private boolean unlockMultiplier; // 是否解锁伤害乘数

	// 构造函数，初始化基础伤害
	public CUnitAttackPreDamageListenerDamageModResult(float baseDamage) {
		this.baseDamage = baseDamage;
		this.bonusDamage = 0;
		this.damageMultiplier = 1;

		unlockBonus = true;
		unlockMultiplier = true;
	}

	// 获取基础伤害
	public float getBaseDamage() {
		return baseDamage;
	}

	// 设置基础伤害
	public void setBaseDamage(float baseDamage) {
		this.baseDamage = baseDamage;
	}

	// 获取额外伤害
	public float getBonusDamage() {
		return bonusDamage;
	}

	// 设置额外伤害
	public void setBonusDamage(float bonusDamage) {
		if (unlockBonus) {
			this.bonusDamage = bonusDamage;
		}
	}

	// 增加额外伤害
	public void addBonusDamage(float bonusDamage) {
		if (unlockBonus) {
			this.bonusDamage += bonusDamage;
		}
	}

	// 获取伤害乘数
	public float getDamageMultiplier() {
		return damageMultiplier;
	}

	// 设置伤害乘数
	public void setDamageMultiplier(float damageMultiplier) {
		if (unlockMultiplier) {
			this.damageMultiplier = damageMultiplier;
		}
	}

	// 增加伤害乘数
	public void addDamageMultiplier(float damageMultiplier) {
		if (unlockMultiplier) {
			this.damageMultiplier *= damageMultiplier;
		}
	}

	// 锁定额外伤害和伤害乘数
	public void lockBonus() {
		unlockBonus = false;
		unlockMultiplier = false;
	}

	// 锁定
	public void lock() {
		unlockBonus = false;
		unlockMultiplier = false;
	}

	// 计算最终伤害
	public float computeFinalDamage() {
		return (baseDamage * damageMultiplier);
	}

	// 是否丢失
	public boolean isMiss() {
		return miss;
	}

	// 设置是否丢失
	public void setMiss(boolean miss) {
		this.miss = miss;
	}
}

