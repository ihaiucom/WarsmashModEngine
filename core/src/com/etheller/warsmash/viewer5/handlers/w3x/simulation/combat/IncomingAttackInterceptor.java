package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

// 用于拦截即将发起的攻击的接口
public interface IncomingAttackInterceptor {
	/**
	 * Called when an attack is about to launch. Returns false if the project will not be sent
	 * 当攻击即将发动时调用。如果不发送项目，则返回false
	 *
	 * @param attackingUnit
	 * @param attack
	 * @param targetWidth
	 * @return
	 */
	boolean onLaunch(CUnit attackingUnit, CUnitAttack attack, CWidget targetWidth);
}
