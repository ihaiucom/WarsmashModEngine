package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeCargoHoldBurrow;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeCargoHoldBurrowLevelData;
// 货物保持 (兽族地洞) 吞噬货物使单位能够容纳别的单位，可以配合装载类技能和卸载类技能的使用。该技能能使单位失去攻击能力直到装载其他单位

public class CAbilityTypeDefinitionCargoHoldBurrow
		extends AbstractCAbilityTypeDefinition<CAbilityTypeCargoHoldBurrowLevelData> implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeCargoHoldBurrowLevelData createLevelData(final GameObject abilityEditorData,
			final int level) {
		final int cargoCapacity = abilityEditorData.getFieldAsInteger(DATA_A + level, 0);
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE + level, 0);
		final float duration = abilityEditorData.getFieldAsFloat(DURATION + level, 0);

//		final int goldCost = abilityEditorData.getFieldAsInteger(GOLD_COST, level);
//		final int lumberCost = abilityEditorData.getFieldAsInteger(LUMBER_COST, level);

		return new CAbilityTypeCargoHoldBurrowLevelData(getTargetsAllowed(abilityEditorData, level), cargoCapacity,
				duration, castRange);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeCargoHoldBurrowLevelData> levelData) {
		return new CAbilityTypeCargoHoldBurrow(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
