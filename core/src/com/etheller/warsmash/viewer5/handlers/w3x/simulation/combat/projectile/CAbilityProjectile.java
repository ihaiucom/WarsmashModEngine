package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

// CAbilityProjectile类继承自CProjectile类，表示一个能力投射物
public class CAbilityProjectile extends CProjectile {
	// 投射物监听器，用于处理投射物相关事件
	private final CAbilityProjectileListener projectileListener;

	// 构造函数，初始化投射物的位置、速度、目标、是否启用自动追踪、来源单位以及投射物监听器
	public CAbilityProjectile(final float x, final float y, final float speed, final AbilityTarget target,
			boolean homingEnabled, final CUnit source, final CAbilityProjectileListener projectileListener) {
		super(x, y, speed, target, homingEnabled, source);
		this.projectileListener = projectileListener;
	}

	// 当投射物击中目标时调用的方法，通知监听器投射物已击中目标
	@Override
	protected void onHitTarget(CSimulation game) {
		projectileListener.onHit(game, getTarget());
	}
}

