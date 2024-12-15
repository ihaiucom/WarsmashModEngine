package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

// 定义一个监听器接口，用于监听单位攻击事件
public interface CUnitAttackListener {
	// 当攻击发起时调用的方法
	void onLaunch();

	// 当攻击命中目标时调用的方法，传入目标和造成的伤害值
	void onHit(AbilityTarget target, float damage);

	// 一个默认的空实现，用于不需要处理攻击事件的情况
	CUnitAttackListener DO_NOTHING = new CUnitAttackListener() {
		@Override
		public void onLaunch() {
		}

		@Override
		public void onHit(AbilityTarget target, float damage) {
		}
	};
}
