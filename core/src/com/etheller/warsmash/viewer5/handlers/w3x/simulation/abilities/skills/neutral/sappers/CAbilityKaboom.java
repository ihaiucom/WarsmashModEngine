package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.sappers;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.AutocastType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.CAutocastAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityUnitOrPointTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
//  (地精工兵) 对一定区域造成<Asds,DataB1>点伤害。对付建筑物和数目特别地有效。

public class CAbilityKaboom extends CAbilityUnitOrPointTargetSpellBase implements CAutocastAbility {

	// 完整伤害半径
	private float fullDamageRadius;

	// 完整伤害量
	private float fullDamageAmount;

	// 部分伤害量
	private float partialDamageAmount;

	// 部分伤害半径
	private float partialDamageRadius;

	// 死亡时是否爆炸
	private boolean explodesOnDeath;

	// 建筑伤害因子
	private float buildingDamageFactor;

	// 是否正在爆炸
	private boolean exploding = false;

	// 是否自动施放
	private boolean autoCastOn;


	public CAbilityKaboom(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.selfdestruct;
	}

	@Override
	public int getAutoCastOnOrderId() {
		return OrderIds.selfdestructon;
	}

	@Override
	public int getAutoCastOffOrderId() {
		return OrderIds.selfdestructoff;
	}

	@Override
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		// 如果自动施法开启，则调用内部方法检查目标是否合法
		if (isAutoCastOn()) {
			// 调用内部方法检查目标是否合法，使用基础订单ID
			this.innerCheckCanTarget(game, unit, getBaseOrderId(), target, receiver);
		}
		// 如果自动施法未开启，则调用父类方法智能检查目标是否合法
		else {
			super.innerCheckCanSmartTarget(game, unit, orderId, target, receiver);
		}

	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		fullDamageAmount = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);
		fullDamageRadius = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		partialDamageAmount = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_D + level, 0);
		partialDamageRadius = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_C + level, 0);
		explodesOnDeath = worldEditorAbility.getFieldAsBoolean(AbilityFields.DATA_F + level, 0);
		buildingDamageFactor = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_E + level, 0);

		setCastRange(getCastRange() + 128);
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		exploding = true;
		caster.kill(simulation);
		return false;
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
		if (explodesOnDeath) {
			exploding = true;
		}
		if (exploding) {
			explode(game, cUnit);
		}
	}

	private void explode(final CSimulation simulation, final CUnit caster) {
		final float radius = StrictMath.max(partialDamageRadius, fullDamageRadius); // 计算伤害半径，取partialDamageRadius和fullDamageRadius中的最大值

		simulation.getWorldCollision().enumUnitsInRange(caster.getX(), caster.getY(), radius, (enumUnit) -> { // 枚举在半径范围内的所有单位
			if (enumUnit.canBeTargetedBy(simulation, caster, getTargetsAllowed())) { // 判断单位是否可以被攻击
				float damageAmount; // 初始化伤害值
				if (caster.canReach(enumUnit, fullDamageRadius)) { // 判断攻击者是否能在最大伤害半径内到达目标
					damageAmount = fullDamageAmount; // 如果可以，伤害值为全额伤害
				} else {
					damageAmount = partialDamageAmount; // 如果不可以，伤害值为部分伤害
				}
				if (enumUnit.isBuilding()) { // 如果目标是建筑
					damageAmount *= buildingDamageFactor; // 建筑伤害乘以建筑伤害因子
				}
				enumUnit.damage(simulation, caster, false, true, CAttackType.SPELLS, CDamageType.DEMOLITION, // 对单位造成伤害
						CWeaponSoundTypeJass.WHOKNOWS.name(), damageAmount);
			}
			return false; // 继续枚举下一个单位
		});

	}

	@Override
	public void setAutoCastOn(final CUnit caster, final boolean autoCastOn) {
		this.autoCastOn = autoCastOn;
		caster.setAutocastAbility(autoCastOn ? this : null);
	}

	@Override
	public boolean isAutoCastOn() {
		return autoCastOn;
	}

	@Override
	public void setAutoCastOff() {
		this.autoCastOn = false;
	}

	@Override
	public AutocastType getAutocastType() {
		return AutocastType.NEARESTENEMY;
	}


	@Override
	public void checkCanAutoTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		this.checkCanTarget(game, unit, orderId, target, receiver);
	}

	@Override
	public void checkCanAutoTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanAutoTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}
}
