package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks;

import java.util.EnumSet;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetWidgetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;
// 箭矢(弹射)攻击 CWeaponType.MBOUNCE
// 表示一个可以反弹攻击的导弹单位
public class CUnitAttackMissileBounce extends CUnitAttackMissile {
	private float damageLossFactor; // 伤害损失因子
	private int maximumNumberOfTargets; // 最大目标数量
	private final int areaOfEffectFullDamage; // 区域效果全伤害
	private final EnumSet<CTargetType> areaOfEffectTargets; // 区域效果目标类型集合

	// 构造函数，用于初始化反弹导弹的属性
	public CUnitAttackMissileBounce(final float animationBackswingPoint, final float animationDamagePoint,
			final CAttackType attackType, final float cooldownTime, final int damageBase, final int damageDice,
			final int damageSidesPerDie, final int damageUpgradeAmount, final int range, final float rangeMotionBuffer,
			final boolean showUI, final EnumSet<CTargetType> targetsAllowed, final String weaponSound,
			final CWeaponType weaponType, final float projectileArc, final String projectileArt,
			final boolean projectileHomingEnabled, final int projectileSpeed, final float damageLossFactor,
			final int maximumNumberOfTargets, final int areaOfEffectFullDamage,
			final EnumSet<CTargetType> areaOfEffectTargets) {
		super(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime, damageBase, damageDice,
				damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound,
				weaponType, projectileArc, projectileArt, projectileHomingEnabled, projectileSpeed);
		this.damageLossFactor = damageLossFactor;
		this.maximumNumberOfTargets = maximumNumberOfTargets;
		this.areaOfEffectFullDamage = areaOfEffectFullDamage;
		this.areaOfEffectTargets = areaOfEffectTargets;
	}

	// 复制当前导弹攻击的实例
	@Override
	public CUnitAttack copy() {
		return new CUnitAttackMissileBounce(getAnimationBackswingPoint(), getAnimationDamagePoint(), getAttackType(),
				getCooldownTime(), getDamageBase(), getDamageDice(), getDamageSidesPerDie(), getDamageUpgradeAmount(),
				getRange(), getRangeMotionBuffer(), isShowUI(), getTargetsAllowed(), getWeaponSound(), getWeaponType(),
				getProjectileArc(), getProjectileArt(), isProjectileHomingEnabled(), getProjectileSpeed(),
				this.damageLossFactor, this.maximumNumberOfTargets, this.areaOfEffectFullDamage,
				this.areaOfEffectTargets);
	}

	// 获取伤害损失因子
	public float getDamageLossFactor() {
		return this.damageLossFactor;
	}

	// 获取最大目标数量
	public int getMaximumNumberOfTargets() {
		return this.maximumNumberOfTargets;
	}

	// 设置伤害损失因子
	public void setDamageLossFactor(final float damageLossFactor) {
		this.damageLossFactor = damageLossFactor;
	}

	// 设置最大目标数量
	public void setMaximumNumberOfTargets(final int maximumNumberOfTargets) {
		this.maximumNumberOfTargets = maximumNumberOfTargets;
	}

	// 执行伤害操作，并处理反弹逻辑
	@Override
	public void doDamage(final CSimulation cSimulation, final CUnit source, final AbilityTarget target,
			final float damage, final float x, final float y, final int bounceIndex,
			final CUnitAttackListener attackListener) {
		super.doDamage(cSimulation, source, target, damage, x, y, bounceIndex, attackListener);
		final CWidget widget = target.visit(AbilityTargetWidgetVisitor.INSTANCE);
		if (widget != null) {
			final int nextBounceIndex = bounceIndex + 1;
			if (nextBounceIndex != this.maximumNumberOfTargets) {
				BounceMissileConsumer.INSTANCE.nextBounce(cSimulation, source, widget, this, x, y, damage,
						nextBounceIndex, attackListener);
			}
		}
	}

	// 私有静态内部类，用于处理导弹的反弹逻辑
	private static final class BounceMissileConsumer implements CUnitEnumFunction {
		private static final BounceMissileConsumer INSTANCE = new BounceMissileConsumer();
		private final Rectangle rect = new Rectangle(); // 碰撞矩形
		private CUnitAttackMissileBounce attack; // 当前攻击实例
		private CSimulation simulation; // 模拟环境
		private CUnit source; // 攻击源单位
		private CWidget target; // 攻击目标
		private float x; // 攻击点x坐标
		private float y; // 攻击点y坐标
		private float damage; // 攻击伤害
		private int bounceIndex; // 当前反弹索引
		private CUnitAttackListener attackListener; // 攻击监听器
		private boolean launched = false; // 是否已发射标识

		// 处理下一个反弹攻击
		public void nextBounce(final CSimulation simulation, final CUnit source, final CWidget target,
				final CUnitAttackMissileBounce attack, final float x, final float y, final float damage,
				final int bounceIndex, final CUnitAttackListener attackListener) {
			this.simulation = simulation;
			this.source = source;
			this.target = target;
			this.attack = attack;
			this.x = x;
			this.y = y;
			this.damage = damage;
			this.bounceIndex = bounceIndex;
			this.attackListener = attackListener;
			this.launched = false;
			final float doubleMaxArea = attack.areaOfEffectFullDamage
					+ (this.simulation.getGameplayConstants().getCloseEnoughRange() * 2);
			final float maxArea = doubleMaxArea / 2;
			this.rect.set(x - maxArea, y - maxArea, doubleMaxArea, doubleMaxArea);
			simulation.getWorldCollision().enumUnitsInRect(this.rect, this);

		}

		// 遍历单位并处理可攻击目标
		@Override
		public boolean call(final CUnit enumUnit) {
			if (enumUnit == this.target) {
				return false;
			}
			if (enumUnit.canBeTargetedBy(this.simulation, this.source, this.attack.areaOfEffectTargets)) {
				if (this.launched) {
					throw new IllegalStateException("already launched");
				}
				final float dx = enumUnit.getX() - this.x;
				final float dy = enumUnit.getY() - this.y;
				final float angle = (float) Math.atan2(dy, dx);
				this.simulation.createProjectile(this.source, this.x, this.y, angle, this.attack, enumUnit,
						this.damage * (1.0f - this.attack.damageLossFactor), this.bounceIndex, this.attackListener);
				this.launched = true;
				return true;
			}
			return false;
		}
	}
}
