package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetWidgetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListenerDamageModResult;

// 近战攻击 CWeaponType.NORMAL
public class CUnitAttackNormal extends CUnitAttack {

	// 构造函数，用于初始化普通单位攻击的属性
	public CUnitAttackNormal(final float animationBackswingPoint, final float animationDamagePoint,
			final CAttackType attackType, final float cooldownTime, final int damageBase, final int damageDice,
			final int damageSidesPerDie, final int damageUpgradeAmount, final int range, final float rangeMotionBuffer,
			final boolean showUI, final EnumSet<CTargetType> targetsAllowed, final String weaponSound,
			final CWeaponType weaponType) {
		super(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime, damageBase, damageDice,
				damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound,
				weaponType);
	}

	// 复制当前攻击实例的方法
	@Override
	public CUnitAttack copy() {
		return new CUnitAttackNormal(getAnimationBackswingPoint(), getAnimationDamagePoint(), getAttackType(),
				getCooldownTime(), getDamageBase(), getDamageDice(), getDamageSidesPerDie(), getDamageUpgradeAmount(),
				getRange(), getRangeMotionBuffer(), isShowUI(), getTargetsAllowed(), getWeaponSound(), getWeaponType());
	}

	// 发起攻击的方法
	@Override
	public void launch(final CSimulation simulation, final CUnit unit, final AbilityTarget target, final float damage,
			final CUnitAttackListener attackListener) {
		// 调攻击监听 发起攻击 onLaunch
		attackListener.onLaunch();
		// 获取目标
		final CWidget widget = target.visit(AbilityTargetWidgetVisitor.INSTANCE);
		if (widget != null) {
			// onAttack 攻击前伤害监听器处理结果
			CUnitAttackPreDamageListenerDamageModResult modDamage = runPreDamageListeners(simulation, unit, target, damage);
			// onDamage 伤害处理，返回真实伤害
			float damageDealt = widget.damage(simulation, unit, true, false, getAttackType(), getWeaponType().getDamageType(),
					modDamage.isMiss() ? null : getWeaponSound(), modDamage.computeFinalDamage(), modDamage.getBonusDamage());
			// onHit 攻击后伤害监听器处理
			runPostDamageListeners(simulation, unit, target, damageDealt);
			// 调攻击监听 发起攻击 onHit
			attackListener.onHit(target, damage);
		}
	}

}
