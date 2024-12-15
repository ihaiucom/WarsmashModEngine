package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;
/**
 * CBehaviorAttackListener 接口定义了有关单位攻击行为的监听器。
 */
public interface CBehaviorAttackListener extends CUnitAttackListener {

	// For this function, return the current attack behavior to keep attacking, or
	// else return something else to interrupt it
	/**
	 * 在攻击后摆动的第一次更新时返回当前的攻击行为。
	 */
	CBehavior onFirstUpdateAfterBackswing(CBehaviorAttack currentAttackBehavior);

	/**
	 * 当攻击完成时，返回与游戏和完成单位相关的攻击行为。
	 */
	CBehavior onFinish(CSimulation game, final CUnit finishingUnit);

	/**
	 * 一个不执行任何操作的攻击行为监听器实现。
	 */
	CBehaviorAttackListener DO_NOTHING = new CBehaviorAttackListener() {
		@Override
		public void onHit(final AbilityTarget target, final float damage) {
		}

		@Override
		public void onLaunch() {
		}

		@Override
		public CBehavior onFirstUpdateAfterBackswing(final CBehaviorAttack currentAttackBehavior) {
			return currentAttackBehavior;
		}

		// 执行命令队列里的下一个命令或者默认行为
		@Override
		public CBehavior onFinish(final CSimulation game, final CUnit finishingUnit) {
			return finishingUnit.pollNextOrderBehavior(game);
		}
	};
}

