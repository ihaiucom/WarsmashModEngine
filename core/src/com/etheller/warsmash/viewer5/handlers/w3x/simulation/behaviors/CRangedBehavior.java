package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

// 定义一个接口CRangedBehavior，继承自CBehavior
public interface CRangedBehavior extends CBehavior {
	// 判断当前状态是否在范围内
	boolean isWithinRange(final CSimulation simulation);

	// 结束移动，参数表示游戏实例和是否中断
	void endMove(CSimulation game, boolean interrupted);

	// 获取目标对象
	AbilityTarget getTarget();
}
