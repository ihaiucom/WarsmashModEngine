package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.combat.CAbilityColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
// 冰冻冷箭 每次攻击带有冰冻效果，使敌人单位减慢攻击和移动。|n|n|cffffcc00等级 1|r - <AHca,DataB1,%>%攻击速度，<AHca,DataC1,%>%移动速度，持续<AHca,Dur1>秒。|n|cffffcc00等级 2|r - <AHca,DataB2,%>%攻击速度，<AHca,DataC2,%>%移动速度，持续<AHca,Dur2>秒。|n|cffffcc00等级 3|r - <AHca,DataB3,%>%攻击速度，<AHca,DataC3,%>%移动速度，持续<AHca,Dur3>秒。

public class CAbilityTypeColdArrows extends CAbilityType<CAbilityTypeColdArrowsLevelData> {

	public CAbilityTypeColdArrows(final War3ID alias, final War3ID code,
			final List<CAbilityTypeColdArrowsLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		return new CAbilityColdArrows(getCode(), getAlias(), handleId);
	}

	@Override
	public void setLevel(final CSimulation game, final CUnit unit, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypeColdArrowsLevelData levelData = getLevelData(level - 1);
		final CAbilityColdArrows heroAbility = ((CAbilityColdArrows) existingAbility);
		heroAbility.setLevel(game, unit, level);
	}

}
