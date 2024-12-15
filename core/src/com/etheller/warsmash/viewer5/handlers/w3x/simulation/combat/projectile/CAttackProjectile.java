package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;

// 普通攻击型投射物类，继承自CProjectile类
public class CAttackProjectile extends CProjectile {
	// 投射物造成的伤害值
	private final float damage;
	// 单位攻击导弹对象
	private final CUnitAttackMissile unitAttack;
	// 弹跳索引
	private final int bounceIndex;
	// 攻击监听器
	private final CUnitAttackListener attackListener;

	// 构造函数，初始化投射物的各项属性
	public CAttackProjectile(final float x, final float y, final float speed, final AbilityTarget target,
			final CUnit source, final float damage, final CUnitAttackMissile unitAttack, final int bounceIndex,
			final CUnitAttackListener attackListener) {
		super(x, y, speed, target, unitAttack.isProjectileHomingEnabled(), source);
		this.damage = damage;
		this.unitAttack = unitAttack;
		this.bounceIndex = bounceIndex;
		this.attackListener = attackListener;
	}

	// 当投射物击中目标时调用的方法
	@Override
	protected void onHitTarget(CSimulation game) {
		this.unitAttack.doDamage(game, getSource(), getTarget(), this.damage, getX(), getY(), this.bounceIndex,
				this.attackListener);
	}

	// 获取单位攻击导弹对象
	public CUnitAttackMissile getUnitAttack() {
		return this.unitAttack;
	}
}

