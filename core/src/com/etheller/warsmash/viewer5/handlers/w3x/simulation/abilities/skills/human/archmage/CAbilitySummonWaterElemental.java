package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage;

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
// 召唤水元素
public class CAbilitySummonWaterElemental extends CAbilityNoTargetSpellBase {
	// 定义召唤水元素生物的ID
	private War3ID summonUnitId;

	// 定义召唤水元素生物的数量
	private int summonUnitCount;

	// 定义要施加的增益效果的ID
	private War3ID buffId;

	// 定义技能的影响范围
	private float areaOfEffect;


	public CAbilitySummonWaterElemental(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.summonUnitId = War3ID.fromString(worldEditorAbility.getFieldAsString(AbilityFields.UNIT_ID + level, 0));
		this.summonUnitCount = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_A + level, 0);
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.waterelemental;
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		// 获取单位的朝向角度
		final float facing = unit.getFacing();
		// 将朝向角度转换为弧度
		final float facingRad = (float) StrictMath.toRadians(facing);
		// 计算水元素生物的生成位置，基于单位的当前位置和朝向
		final float x = unit.getX() + ((float) StrictMath.cos(facingRad) * areaOfEffect);
		final float y = unit.getY() + ((float) StrictMath.sin(facingRad) * areaOfEffect);
		// 循环创建指定数量的水元素生物
		for (int i = 0; i < summonUnitCount; i++) {
			// 创建一个新的水元素生物单位
			final CUnit summonedUnit = simulation.createUnitSimple(summonUnitId, unit.getPlayerIndex(), x, y, facing);
			// 设置新单位为召唤物分类
			summonedUnit.addClassification(CUnitClassification.SUMMONED);
			// 为新单位添加一个定时生命值增益效果
			summonedUnit.add(simulation,
					new CBuffTimedLife(simulation.getHandleIdAllocator().createId(), buffId, getDuration(), false));
			// 在新单位上创建一个临时的法术效果
			simulation.createTemporarySpellEffectOnUnit(summonedUnit, getAlias(), CEffectType.TARGET);
		}
		// 返回false，表示召唤操作完成
		return false;

	}

	public War3ID getSummonUnitId() {
		return summonUnitId;
	}

	public int getSummonUnitCount() {
		return summonUnitCount;
	}

	public War3ID getBuffId() {
		return buffId;
	}

	public float getAreaOfEffect() {
		return areaOfEffect;
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
