package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeCarrionSwarmDummy;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeCarrionSwarmDummyLevelData;
// 腐臭蜂群 放出一群蝙蝠和昆虫对一线上的敌人造成一定的伤害。|n|n|cffffcc00等级 1|r - 对每个单位造成<AUcs,DataA1>点的伤害。|n|cffffcc00等级 2|r - 对每个单位造成<AUcs,DataA2>点的伤害。|n|cffffcc00等级 3|r - 对每个单位造成<AUcs,DataA3>点的伤害。

public class CAbilityTypeDefinitionCarrionSwarmDummy extends
		AbstractCAbilityTypeDefinition<CAbilityTypeCarrionSwarmDummyLevelData> implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeCarrionSwarmDummyLevelData createLevelData(final GameObject abilityEditorData,
			final int level) {
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE + level, 0);
		return new CAbilityTypeCarrionSwarmDummyLevelData(getTargetsAllowed(abilityEditorData, level), castRange);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeCarrionSwarmDummyLevelData> levelData) {
		return new CAbilityTypeCarrionSwarmDummy(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
