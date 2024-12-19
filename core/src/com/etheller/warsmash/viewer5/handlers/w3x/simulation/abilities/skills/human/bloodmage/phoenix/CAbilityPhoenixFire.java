package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.bloodmage.phoenix;

import java.util.EnumSet;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;
// 代表凤凰火能力的类，继承自抽象通用无图标能力
public class CAbilityPhoenixFire extends AbstractGenericNoIconAbility {

	private float initialDamage; // 初始伤害
	private float damagePerSecond; // 每秒伤害
	private float areaOfEffect; // 影响范围
	private float cooldown; // 冷却时间
	private float duration; // 持续时间
	private EnumSet<CTargetType> targetsAllowed; // 允许的目标类型

	private int lastAttackTurnTick; // 上一次攻击的回合Tick

	// 构造函数，初始化凤凰火能力的属性
	public CAbilityPhoenixFire(final int handleId, final War3ID code, final War3ID alias, final float initialDamage,
			final float damagePerSecond, final float areaOfEffect, final float cooldown, final float duration,
			final EnumSet<CTargetType> targetsAllowed) {
		super(handleId, code, alias);
		this.initialDamage = initialDamage;
		this.damagePerSecond = damagePerSecond;
		this.areaOfEffect = areaOfEffect;
		this.cooldown = cooldown;
		this.duration = duration;
		this.targetsAllowed = targetsAllowed;
	}

	// 当能力添加到单位时触发
	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
	}

	// 当能力从单位移除时触发
	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	// 每个Tick调用，处理能力的逻辑
	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
		// 计算技能冷却时间，单位为游戏刻度
		final int cooldownTicks = (int) (this.cooldown / WarsmashConstants.SIMULATION_STEP_TIME);
		// 获取当前游戏刻度
		final int gameTurnTick = game.getGameTurnTick();
		// 判断当前游戏刻度是否大于上次攻击刻度加上冷却时间
		if (gameTurnTick > (this.lastAttackTurnTick + cooldownTicks)) {
			// 枚举指定矩形区域内的所有单位
			game.getWorldCollision().enumUnitsInRect(new Rectangle(unit.getX() - this.areaOfEffect,
					unit.getY() - this.areaOfEffect, this.areaOfEffect * 2, this.areaOfEffect * 2), enumUnit -> {
				// 判断当前单位是否能到达目标单位，并且目标单位是否能被攻击
				if (unit.canReach(enumUnit, this.areaOfEffect)
						&& enumUnit.canBeTargetedBy(game, unit, this.targetsAllowed)) {
					// 发起攻击
					unit.getCurrentAttacks().get(0).launch(game, unit, enumUnit, this.initialDamage,
							CUnitAttackListener.DO_NOTHING);
					// 更新上次攻击刻度
					this.lastAttackTurnTick = gameTurnTick;
				}
				// 返回false表示继续枚举下一个单位
				return false;
			});
		}

	}

	// 当单位死亡时触发
	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {

	}

	// 当能力从队列中取消时触发
	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {

	}

	// 开始施放能力，无目标情况下的处理
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	// 开始施放能力，目标为点时的处理
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	// 开始施放能力，无目标时的处理
	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	// 检查目标是否可以被选中
	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.notAnActiveAbility();
	}

	// 检查点目标是否可以被选中
	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.notAnActiveAbility();
	}

	// 检查无目标情况下是否可以被选中
	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.notAnActiveAbility();
	}

	// 检查能力是否可以使用
	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();
	}

	// 获取初始伤害
	public float getInitialDamage() {
		return this.initialDamage;
	}

	// 获取每秒伤害
	public float getDamagePerSecond() {
		return this.damagePerSecond;
	}

	// 获取影响范围
	public float getAreaOfEffect() {
		return this.areaOfEffect;
	}

	// 获取冷却时间
	public float getCooldown() {
		return this.cooldown;
	}

	// 获取持续时间
	public float getDuration() {
		return this.duration;
	}

	// 获取允许的目标类型
	public EnumSet<CTargetType> getTargetsAllowed() {
		return this.targetsAllowed;
	}

	// 设置初始伤害
	public void setInitialDamage(final float initialDamage) {
		this.initialDamage = initialDamage;
	}

	// 设置每秒伤害
	public void setDamagePerSecond(final float damagePerSecond) {
		this.damagePerSecond = damagePerSecond;
	}

	// 设置影响范围
	public void setAreaOfEffect(final float areaOfEffect) {
		this.areaOfEffect = areaOfEffect;
	}

	// 设置冷却时间
	public void setCooldown(final float cooldown) {
		this.cooldown = cooldown;
	}

	// 设置持续时间
	public void setDuration(final float duration) {
		this.duration = duration;
	}

	// 设置允许的目标类型
	public void setTargetsAllowed(final EnumSet<CTargetType> targetsAllowed) {
		this.targetsAllowed = targetsAllowed;
	}

}
