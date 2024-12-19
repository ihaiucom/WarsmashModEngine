package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.beastmaster;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimedLife;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
// 召唤战鹰 召唤一只骄傲的战鹰来侦察敌人|n持续<ANsw,Dur1>秒。|n|n|cffffcc00等级 1|r - <nwe1,realHP>点生命值，有真实视域技能。|n|cffffcc00等级 2|r - <nwe2,realHP>点生命值，<nwe2,mindmg1>到<nwe2,maxdmg1>点攻击力，有真实视域技能。|n|cffffcc00等级 3|r - <nwe3,realHP>点生命值，<nwe3,mindmg1>到<nwe3,maxdmg1>点攻击力，有真实视域技能并且隐形。

public class CAbilitySummonHawk extends CAbilityNoTargetSpellBase {
	private War3ID summonUnitId;
	private int summonUnitCount;
	private War3ID buffId;
	private float areaOfEffect;

	public CAbilitySummonHawk(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		final String unitTypeOne = worldEditorAbility.getFieldAsString(AbilityFields.UNIT_ID + level, 0);
		this.summonUnitId = unitTypeOne.length() == 4 ? War3ID.fromString(unitTypeOne) : War3ID.NONE;
		this.summonUnitCount = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_A + level, 0);
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.summonwareagle;
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		final float facing = unit.getFacing();
		final float facingRad = (float) StrictMath.toRadians(facing);
		final float x = unit.getX() + ((float) StrictMath.cos(facingRad) * this.areaOfEffect);
		final float y = unit.getY() + ((float) StrictMath.sin(facingRad) * this.areaOfEffect);
		for (int i = 0; i < this.summonUnitCount; i++) {
			final CUnit summonedUnit = simulation.createUnitSimple(this.summonUnitId, unit.getPlayerIndex(), x, y,
					facing);
			summonedUnit.addClassification(CUnitClassification.SUMMONED);
			summonedUnit.add(simulation,
					new CBuffTimedLife(simulation.getHandleIdAllocator().createId(), this.buffId, getDuration(), true));
			simulation.createTemporarySpellEffectOnUnit(summonedUnit, getAlias(), CEffectType.TARGET);
		}
		return false;
	}

	public War3ID getSummonUnitId() {
		return this.summonUnitId;
	}

	public int getSummonUnitCount() {
		return this.summonUnitCount;
	}

	public War3ID getBuffId() {
		return this.buffId;
	}

	public float getAreaOfEffect() {
		return this.areaOfEffect;
	}

	public void setSummonUnitId(final War3ID summonUnitId) {
		this.summonUnitId = summonUnitId;
	}

	public void setSummonUnitCount(final int summonUnitCount) {
		this.summonUnitCount = summonUnitCount;
	}

	public void setBuffId(final War3ID buffId) {
		this.buffId = buffId;
	}

	public void setAreaOfEffect(final float areaOfEffect) {
		this.areaOfEffect = areaOfEffect;
	}

}
