package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeImmolation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeImmolationLevelData;
// 献祭 让恶魔猎手处于火焰的包围之中，并对周围的敌方地面单位造成一定的伤害。|n该技能会持续地消耗魔法值。|n|n|cffffcc00等级 1|r - 每秒<AEim,DataA1>点的伤害。|n|cffffcc00等级 2|r - 每秒<AEim,DataA2>点的伤害。|n|cffffcc00等级 3|r - 每秒<AEim,DataA3>点的伤害。

public class CAbilityTypeDefinitionImmolation extends AbstractCAbilityTypeDefinition<CAbilityTypeImmolationLevelData>
		implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeImmolationLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final float damagePerInterval = abilityEditorData.getFieldAsFloat(DATA_A + level, 0);
		final float manaDrainedPerSecond = abilityEditorData.getFieldAsFloat(DATA_B + level, 0);
		final float bufferManaRequired = abilityEditorData.getFieldAsFloat(DATA_C + level, 0);
		final float areaOfEffect = abilityEditorData.getFieldAsFloat(AREA_OF_EFFECT + level, 0);
		final float duration = abilityEditorData.getFieldAsFloat(DURATION + level, 0);
		final int manaCost = abilityEditorData.getFieldAsInteger(MANA_COST + level, 0);
		final War3ID buffId = getBuffId(abilityEditorData, level);
		return new CAbilityTypeImmolationLevelData(getTargetsAllowed(abilityEditorData, level), bufferManaRequired,
				damagePerInterval, manaDrainedPerSecond, areaOfEffect, manaCost, duration, buffId);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeImmolationLevelData> levelData) {
		return new CAbilityTypeImmolation(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
