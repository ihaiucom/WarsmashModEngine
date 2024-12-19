package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemPermanentStatGain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
// 能提高智力的物品 智力之书
// 能增加力量的物品
// 能增加敏捷度的物品
// 能提高英雄三个属性的物品

public class CAbilityTypeItemPermanentStatGain extends CAbilityType<CAbilityTypeItemStatBonusLevelData> {

	public CAbilityTypeItemPermanentStatGain(final War3ID alias, final War3ID code,
			final List<CAbilityTypeItemStatBonusLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeItemStatBonusLevelData levelData = getLevelData(0);
		return new CAbilityItemPermanentStatGain(handleId, getCode(), getAlias(), levelData.getStrength(), levelData.getAgility(),
				levelData.getIntelligence());
	}

	@Override
	public void setLevel(final CSimulation game, final CUnit unit, final CLevelingAbility existingAbility, final int level) {

		final CAbilityTypeLevelData levelData = getLevelData(level - 1);
		final CLevelingAbility heroAbility = (existingAbility);

		// TODO ignores fields

		heroAbility.setLevel(game, unit, level);

	}
}
