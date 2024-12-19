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
// 召唤豪猪 召唤一只愤怒的豪猪来为你作战。|n持续<ANsq,Dur1>秒。|n|n|cffffcc00等级 1|r - <nqb1,realHP>点生命值, <nqb1,mindmg1>到<nqb1,maxdmg1>点攻击力。|n|n|cffffcc00等级 2|r - <nqb2,realHP>点生命值，<nqb2,mindmg1>到 <nqb2,maxdmg1>点攻击力，有狂热技能。|n|n|cffffcc00等级 3|r - <nqb3,realHP>点生命值，<nqb3,mindmg1>到<nqb3,maxdmg1>点区域伤害，有狂热技能。

public class CAbilitySummonQuilbeast extends CAbilityNoTargetSpellBase {
	private War3ID summonUnitId;
	private int summonUnitCount;
	private War3ID buffId;
	private float areaOfEffect;

	public CAbilitySummonQuilbeast(final int handleId, final War3ID alias) {
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
		return OrderIds.summonquillbeast;
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
