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
/**
 * 表示单位瞬时攻击的类，继承自 CUnitAttack。 CWeaponType.Instant 立即
 */
public class CUnitAttackInstant extends CUnitAttack {
	// 投射物美术效果
	private String projectileArt;

	/**
	 * 构造函数，用于创建 CUnitAttackInstant 实例。
	 *
	 * @param animationBackswingPoint 动画后摆点
	 * @param animationDamagePoint 动画伤害点
	 * @param attackType 攻击类型
	 * @param cooldownTime 冷却时间
	 * @param damageBase 基础伤害
	 * @param damageDice 投掷骰子数量
	 * @param damageSidesPerDie 每个骰子的面数
	 * @param damageUpgradeAmount 伤害升级量
	 * @param range 攻击范围
	 * @param rangeMotionBuffer 范围运动缓冲
	 * @param showUI 是否显示UI
	 * @param targetsAllowed 允许的目标类型
	 * @param weaponSound 武器音效
	 * @param weaponType 武器类型
	 * @param projectileArt 投射物效果
	 */
	public CUnitAttackInstant(final float animationBackswingPoint, final float animationDamagePoint,
			final CAttackType attackType, final float cooldownTime, final int damageBase, final int damageDice,
			final int damageSidesPerDie, final int damageUpgradeAmount, final int range, final float rangeMotionBuffer,
			final boolean showUI, final EnumSet<CTargetType> targetsAllowed, final String weaponSound,
			final CWeaponType weaponType, final String projectileArt) {
		super(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime, damageBase, damageDice,
				damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound,
				weaponType);
		this.projectileArt = projectileArt;
	}

	@Override
	/**
	 * 复制当前攻击实例，返回一个新的 CUnitAttackInstant 实例。
	 */
	public CUnitAttack copy() {
		return new CUnitAttackInstant(getAnimationBackswingPoint(), getAnimationDamagePoint(), getAttackType(),
				getCooldownTime(), getDamageBase(), getDamageDice(), getDamageSidesPerDie(), getDamageUpgradeAmount(),
				getRange(), getRangeMotionBuffer(), isShowUI(), getTargetsAllowed(), getWeaponSound(), getWeaponType(),
				this.projectileArt);
	}

	/**
	 * 获取投射物效果。
	 *
	 * @return 投射物效果
	 */
	public String getProjectileArt() {
		return this.projectileArt;
	}

	/**
	 * 设置投射物效果。
	 *
	 * @param projectileArt 投射物效果
	 */
	public void setProjectileArt(final String projectileArt) {
		this.projectileArt = projectileArt;
	}

	@Override
	/**
	 * 发起攻击，处理攻击逻辑与效果。
	 *
	 * @param simulation 模拟场景
	 * @param unit 攻击单位
	 * @param target 攻击目标
	 * @param damage 伤害值
	 * @param attackListener 攻击监听器
	 */
	public void launch(final CSimulation simulation, final CUnit unit, final AbilityTarget target, final float damage,
			final CUnitAttackListener attackListener) {
		// 调攻击监听 发起攻击 onLaunch
		attackListener.onLaunch();
		// 获取目标
		final CWidget widget = target.visit(AbilityTargetWidgetVisitor.INSTANCE);
		if (widget != null) {
			// 创建瞬间攻击美术效果
			simulation.createInstantAttackEffect(unit, this, widget);
			// onAttack 攻击前伤害监听器处理结果
			CUnitAttackPreDamageListenerDamageModResult modDamage = runPreDamageListeners(simulation, unit, target, damage);
			// onDamage 伤害处理，返回真实伤害
			float damageDealt = widget.damage(simulation, unit, true, true, getAttackType(), getWeaponType().getDamageType(), getWeaponSound(), modDamage.computeFinalDamage(), modDamage.getBonusDamage());
			// onHit 攻击后伤害监听器处理
			runPostDamageListeners(simulation, unit, target, damageDealt);
			// 调攻击监听 发起攻击 onHit
			attackListener.onHit(target, damage);
		}
	}

}
