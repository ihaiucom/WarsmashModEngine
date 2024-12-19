package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffAuraBase;
// 专注光环Buff, 为周围友军提供一定额外的护甲。
public class CBuffDevotion extends CBuffAuraBase {
	// 护甲加成值
	private final float armorBonus;

	public CBuffDevotion(int handleId, War3ID alias, float armorBonus) {
		super(handleId, alias, alias);
		this.armorBonus = armorBonus;
	}

	@Override
	public void onBuffAdd(CSimulation game, CUnit unit) {
		// 增加单位的临时防御加成值
		unit.setTemporaryDefenseBonus(unit.getTemporaryDefenseBonus() + armorBonus);
	}

	@Override
	public void onBuffRemove(CSimulation game, CUnit unit) {
		// 减少单位的临时防御加成值
		unit.setTemporaryDefenseBonus(unit.getTemporaryDefenseBonus() - armorBonus);
	}
}
