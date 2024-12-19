package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.test.CAbilityCarrionSwarmDummy;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
// 腐臭蜂群 放出一群蝙蝠和昆虫对一线上的敌人造成一定的伤害。|n|n|cffffcc00等级 1|r - 对每个单位造成<AUcs,DataA1>点的伤害。|n|cffffcc00等级 2|r - 对每个单位造成<AUcs,DataA2>点的伤害。|n|cffffcc00等级 3|r - 对每个单位造成<AUcs,DataA3>点的伤害。

public class CAbilityTypeCarrionSwarmDummy extends CAbilityType<CAbilityTypeCarrionSwarmDummyLevelData> {

	public CAbilityTypeCarrionSwarmDummy(final War3ID alias, final War3ID code,
			final List<CAbilityTypeCarrionSwarmDummyLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeCarrionSwarmDummyLevelData levelData = getLevelData(0);
		return new CAbilityCarrionSwarmDummy(handleId, getCode(), getAlias(), levelData.getCastRange(),
				levelData.getTargetsAllowed());
	}

	@Override
	public void setLevel(final CSimulation game, final CUnit unit, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypeCarrionSwarmDummyLevelData levelData = getLevelData(level - 1);
		final CAbilityCarrionSwarmDummy heroAbility = ((CAbilityCarrionSwarmDummy) existingAbility);
		heroAbility.setCastRange(levelData.getCastRange());
		heroAbility.setTargetsAllowed(levelData.getTargetsAllowed());
		heroAbility.setLevel(game, unit, level);

	}
}
