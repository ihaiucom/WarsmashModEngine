package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
// 箭矢(穿透)攻击、炮灰(穿透) CWeaponType.MLINE、CWeaponType.ALINE
// 该类继承自CUnitAttackMissile，表示一种线性攻击导弹
public class CUnitAttackMissileLine extends CUnitAttackMissile {
	// 伤害溢出距离
	private float damageSpillDistance;
	// 伤害溢出半径
	private float damageSpillRadius;

	// 构造函数，初始化线性攻击导弹的各项属性
	public CUnitAttackMissileLine(final float animationBackswingPoint, final float animationDamagePoint,
			final CAttackType attackType, final float cooldownTime, final int damageBase, final int damageDice,
			final int damageSidesPerDie, final int damageUpgradeAmount, final int range, final float rangeMotionBuffer,
			final boolean showUI, final EnumSet<CTargetType> targetsAllowed, final String weaponSound,
			final CWeaponType weaponType, final float projectileArc, final String projectileArt,
			final boolean projectileHomingEnabled, final int projectileSpeed, final float damageSpillDistance,
			final float damageSpillRadius) {
		super(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime, damageBase, damageDice,
				damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound,
				weaponType, projectileArc, projectileArt, projectileHomingEnabled, projectileSpeed);
		this.damageSpillDistance = damageSpillDistance;
		this.damageSpillRadius = damageSpillRadius;
	}

	// 复制当前对象，返回一个新的CUnitAttackMissileLine实例
	@Override
	public CUnitAttack copy() {
		return new CUnitAttackMissileLine(getAnimationBackswingPoint(), getAnimationDamagePoint(), getAttackType(),
				getCooldownTime(), getDamageBase(), getDamageDice(), getDamageSidesPerDie(), getDamageUpgradeAmount(),
				getRange(), getRangeMotionBuffer(), isShowUI(), getTargetsAllowed(), getWeaponSound(), getWeaponType(),
				getProjectileArc(), getProjectileArt(), isProjectileHomingEnabled(), getProjectileSpeed(),
				this.damageSpillDistance, this.damageSpillRadius);
	}

	// 获取伤害溢出距离
	public float getDamageSpillDistance() {
		return this.damageSpillDistance;
	}

	// 获取伤害溢出半径
	public float getDamageSpillRadius() {
		return this.damageSpillRadius;
	}

	// 设置伤害溢出距离
	public void setDamageSpillDistance(final float damageSpillDistance) {
		this.damageSpillDistance = damageSpillDistance;
	}

	// 设置伤害溢出半径
	public void setDamageSpillRadius(final float damageSpillRadius) {
		this.damageSpillRadius = damageSpillRadius;
	}

}
