package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemAttackBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
// 增加攻击力的物品 攻击之抓 +3

public class CAbilityTypeItemAttackBonus extends CAbilityType<CAbilityTypeItemAttackBonusLevelData> {

	public CAbilityTypeItemAttackBonus(final War3ID alias, final War3ID code,
			final List<CAbilityTypeItemAttackBonusLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeItemAttackBonusLevelData levelData = getLevelData(0);
		return new CAbilityItemAttackBonus(handleId, getCode(), getAlias(), levelData.getDamageBonus());
	}

	@Override
	public void setLevel(final CSimulation game, final CUnit unit, final CLevelingAbility existingAbility, final int level) {

		final CAbilityTypeLevelData levelData = getLevelData(level - 1);
		final CLevelingAbility heroAbility = (existingAbility);

		// TODO ignores fields

		heroAbility.setLevel(game, unit, level);

	}
}
