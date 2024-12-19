package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeColdArrowsLevelData;
// 冰冻冷箭 每次攻击带有冰冻效果，使敌人单位减慢攻击和移动。|n|n|cffffcc00等级 1|r - <AHca,DataB1,%>%攻击速度，<AHca,DataC1,%>%移动速度，持续<AHca,Dur1>秒。|n|cffffcc00等级 2|r - <AHca,DataB2,%>%攻击速度，<AHca,DataC2,%>%移动速度，持续<AHca,Dur2>秒。|n|cffffcc00等级 3|r - <AHca,DataB3,%>%攻击速度，<AHca,DataC3,%>%移动速度，持续<AHca,Dur3>秒。

public class CAbilityTypeDefinitionColdArrows extends AbstractCAbilityTypeDefinition<CAbilityTypeColdArrowsLevelData> {

	@Override
	protected CAbilityTypeColdArrowsLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		return new CAbilityTypeColdArrowsLevelData(getTargetsAllowed(abilityEditorData, level));
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeColdArrowsLevelData> levelData) {
		return new CAbilityTypeColdArrows(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
