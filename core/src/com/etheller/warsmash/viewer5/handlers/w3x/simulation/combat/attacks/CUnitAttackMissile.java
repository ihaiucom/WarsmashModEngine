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

// 箭矢攻击 CWeaponType.MISSILE
public class CUnitAttackMissile extends CUnitAttack {
	// 投射物 弧度
	private float projectileArc;
	// 投射物 美术资源
	private String projectileArt;
	// 投射物 是否追踪目标
	private boolean projectileHomingEnabled;
	// 投射物 速度
	private int projectileSpeed;

	// 构造函数，用于初始化导弹攻击单位的属性
	public CUnitAttackMissile(final float animationBackswingPoint, final float animationDamagePoint,
			final CAttackType attackType, final float cooldownTime, final int damageBase, final int damageDice,
			final int damageSidesPerDie, final int damageUpgradeAmount, final int range, final float rangeMotionBuffer,
			final boolean showUI, final EnumSet<CTargetType> targetsAllowed, final String weaponSound,
			final CWeaponType weaponType, final float projectileArc, final String projectileArt,
			final boolean projectileHomingEnabled, final int projectileSpeed) {
		super(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime, damageBase, damageDice,
				damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound,
				weaponType);
		this.projectileArc = projectileArc;
		this.projectileArt = projectileArt;
		this.projectileHomingEnabled = projectileHomingEnabled;
		this.projectileSpeed = projectileSpeed;
	}

	// 复制当前导弹攻击单位的实例
	@Override
	public CUnitAttack copy() {
		return new CUnitAttackMissile(this.getAnimationBackswingPoint(), getAnimationDamagePoint(), getAttackType(),
				getCooldownTime(), getDamageBase(), getDamageDice(), getDamageSidesPerDie(), getDamageUpgradeAmount(),
				getRange(), getRangeMotionBuffer(), isShowUI(), getTargetsAllowed(), getWeaponSound(), getWeaponType(),
				this.projectileArc, this.projectileArt, this.projectileHomingEnabled, this.projectileSpeed);
	}

	// 获取导弹的弧度
	public float getProjectileArc() {
		if (this.attackReplacement != null) {
			return this.attackReplacement.getProjectileArc();
		}
		return this.projectileArc;
	}

	// 获取导弹的艺术表现形式
	public String getProjectileArt() {
		if (this.attackReplacement != null) {
			return this.attackReplacement.getProjectileArt();
		}
		return this.projectileArt;
	}

	// 判断导弹是否启用自导
	public boolean isProjectileHomingEnabled() {
		if (this.attackReplacement != null) {
			return this.attackReplacement.isProjectileHomingEnabled();
		}
		return this.projectileHomingEnabled;
	}

	// 获取导弹的速度
	public int getProjectileSpeed() {
		if (this.attackReplacement != null) {
			return this.attackReplacement.getProjectileSpeed();
		}
		return this.projectileSpeed;
	}

	// 设置导弹的弧度
	public void setProjectileArc(final float projectileArc) {
		this.projectileArc = projectileArc;
	}

	// 设置导弹的艺术表现形式
	public void setProjectileArt(final String projectileArt) {
		this.projectileArt = projectileArt;
	}

	// 设置导弹是否启用自导
	public void setProjectileHomingEnabled(final boolean projectileHomingEnabled) {
		this.projectileHomingEnabled = projectileHomingEnabled;
	}

	// 设置导弹的速度
	public void setProjectileSpeed(final int projectileSpeed) {
		this.projectileSpeed = projectileSpeed;
	}

	// 发射导弹并规避命中目标
	@Override
	public void launch(final CSimulation simulation, final CUnit unit, final AbilityTarget target, final float damage,
			final CUnitAttackListener attackListener) {
		// 调攻击监听 发起攻击 onLaunch
		attackListener.onLaunch();
		// 创建攻击投射物
		simulation.createProjectile(unit, unit.getX(), unit.getY(), (float) Math.toRadians(unit.getFacing()), this,
				target, damage, 0, attackListener);
	}

	// 投射物命中目标时调用 对目标造成伤害
	public void doDamage(final CSimulation cSimulation, final CUnit source, final AbilityTarget target,
			final float damage, final float x, final float y, final int bounceIndex,
			final CUnitAttackListener attackListener) {
		// 获取目标
		final CWidget widget = target.visit(AbilityTargetWidgetVisitor.INSTANCE);
		if (widget != null) {
			// onAttack 攻击前伤害监听器处理结果
			CUnitAttackPreDamageListenerDamageModResult modDamage = runPreDamageListeners(cSimulation, source, target, damage);
			// onDamage 伤害处理，返回真实伤害
			float damageDealt = widget.damage(cSimulation, source, true, true, getAttackType(), getWeaponType().getDamageType(), getWeaponSound(), modDamage.computeFinalDamage(), modDamage.getBonusDamage());
			// onHit 攻击后伤害监听器处理
			runPostDamageListeners(cSimulation, source, target, damageDealt);
			// 调攻击监听 发起攻击 onHit
			attackListener.onHit(target, damage);
		}
	}
}
