package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeHumanRepairLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeRepair;
// 更新
// 恢复 使得侍僧能修复建筑物和机械单位。

public class CAbilityTypeDefinitionRepair extends AbstractCAbilityTypeDefinition<CAbilityTypeHumanRepairLevelData>
		implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeHumanRepairLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final float costRatio = abilityEditorData.getFieldAsFloat(DATA_A + level, 0);
		final float timeRatio = abilityEditorData.getFieldAsFloat(DATA_B + level, 0);
		final float navalRangeBonus = abilityEditorData.getFieldAsFloat(DATA_E + level, 0);
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE + level, 0);
		return new CAbilityTypeHumanRepairLevelData(getTargetsAllowed(abilityEditorData, level), navalRangeBonus,
				costRatio, timeRatio, castRange);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeHumanRepairLevelData> levelData) {
		return new CAbilityTypeRepair(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
