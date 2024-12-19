package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.undead.deathknight;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
// 黑暗仪式 牺牲一个友军单位来将其一定百分比的生命值转化成巫妖的魔法值。|n|n|cffffcc00等级 1|r - 转化<AUdr,DataA1,%>%的生命值。|n|cffffcc00等级 2|r - 转化<AUdr,DataA2,%>%的生命值。|n|cffffcc00等级 3|r - 转化<AUdr,DataA3,%>%的生命值。

public class CAbilityDarkRitual extends CAbilityDeathPact {

	public CAbilityDarkRitual(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.darkritual;
	}
}
