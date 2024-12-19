package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;

// 等级能力接口
public interface CLevelingAbility extends CAbility {
	// 获取当前等级
	int getLevel();

	// 设置单位的等级
	void setLevel(CSimulation simulation, CUnit unit, int level);
}
