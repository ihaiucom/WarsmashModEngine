package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.nightelf.moonpriestess;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage.CAbilitySummonWaterElemental;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
// 侦察 能召唤出一头用来侦察地图的猫头鹰。|n能看见隐形单位。|n|n|cffffcc00等级 1|r -消耗<AEst,Cost1>点魔法值来召唤出一头猫头鹰。|n|cffffcc00等级 2|r -消耗<AEst,Cost2>点魔法值来召唤出一头猫头鹰。|n|cffffcc00等级 3|r -消耗<AEst,Cost3>点魔法值来召唤出一头猫头鹰。

public class CAbilitySummonOwlScout extends CAbilitySummonWaterElemental {
	public CAbilitySummonOwlScout(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.scout;
	}

}
