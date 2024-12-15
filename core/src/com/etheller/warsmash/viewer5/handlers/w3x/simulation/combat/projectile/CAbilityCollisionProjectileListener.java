package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
// 定义一个接口，用于监听投射物碰撞事件
public interface CAbilityCollisionProjectileListener {
	// 当投射物发射时调用的方法
	void onLaunch(CSimulation game, AbilityTarget target);

	// 当投射物即将命中时调用的方法
	void onPreHits(CSimulation game, AbilityPointTarget location);

	// 判断投射物是否可以命中目标的方法
	boolean canHitTarget(CSimulation game, CWidget target);

	// 当投射物命中目标时调用的方法
	void onHit(CSimulation game, AbilityTarget target);

	// 设置单位目标数量的方法
	void setUnitTargets(int units);

	// 设置可破坏目标数量的方法
	void setDestructableTargets(int dests);

	// 设置当前位置的方法
	void setCurrentLocation(AbilityPointTarget loc);

	// 定义一个空实现，用于不需要处理事件的情况
	CAbilityCollisionProjectileListener DO_NOTHING = new CAbilityCollisionProjectileListener() {
		@Override
		public void onLaunch(CSimulation game, AbilityTarget target) {
		}

		@Override
		public void onPreHits(CSimulation game, AbilityPointTarget loc) {
		}

		@Override
		public void onHit(CSimulation game, AbilityTarget target) {
		}

		@Override
		public void setUnitTargets(int units) {
		}

		@Override
		public void setDestructableTargets(int dests) {
		}

		@Override
		public void setCurrentLocation(AbilityPointTarget loc) {
		}

		@Override
		public boolean canHitTarget(CSimulation game, CWidget target) {
			return false;
		}
	};
}
