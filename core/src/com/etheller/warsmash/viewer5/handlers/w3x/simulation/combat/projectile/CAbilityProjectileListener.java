package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

// 定义一个接口，用于监听投射物的能力事件
public interface CAbilityProjectileListener {
	// 当投射物发射时调用的方法
	void onLaunch(CSimulation game, AbilityTarget target);

	// 当投射物命中时调用的方法
	void onHit(CSimulation game, AbilityTarget target);

	// 一个默认的空实现，用于不需要处理事件时的占位
	CAbilityProjectileListener DO_NOTHING = new CAbilityProjectileListener() {
		@Override
		public void onLaunch(CSimulation game, AbilityTarget target) {
		}

		@Override
		public void onHit(CSimulation game, AbilityTarget target) {
		}
	};
}

