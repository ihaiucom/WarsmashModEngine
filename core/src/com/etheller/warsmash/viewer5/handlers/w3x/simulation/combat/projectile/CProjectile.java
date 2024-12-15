package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;
// 抽象类CProjectile，表示一个投射物，实现了CEffect接口
public abstract class CProjectile implements CEffect {
	protected float x; // 投射物的当前X坐标
	protected float y; // 投射物的当前Y坐标
	private final float initialTargetX; // 初始目标的X坐标
	private final float initialTargetY; // 初始目标的Y坐标
	private final float speed; // 投射物的速度
	private final AbilityTarget target; // 投射物的目标
	private boolean homingEnabled; // 是否启用自动追踪
	protected boolean done; // 投射物是否已完成
	private final CUnit source; // 投射物的来源单位

	// 构造函数，初始化投射物的属性
	public CProjectile(final float x, final float y, final float speed, final AbilityTarget target, boolean homingEnabled,
			final CUnit source) {
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.target = target;
		this.homingEnabled = homingEnabled;
		this.source = source;
		this.initialTargetX = target.getX();
		this.initialTargetY = target.getY();
	}

	// 更新投射物的状态，返回是否已完成
	@Override
	public boolean update(final CSimulation game) {
		// tx 和 ty 分别获取投射物的目标X坐标和Y坐标。
		final float tx = getTargetX();
		final float ty = getTargetY();
		// sx 和 sy 分别获取投射物的当前X坐标和Y坐标。
		final float sx = this.x;
		final float sy = this.y;
		// 计算目标与当前位置的差值
		final float dtsx = tx - sx;
		final float dtsy = ty - sy;
		// c 是目标与当前位置之间的直线距离，通过勾股定理计算得出。
		final float c = (float) Math.sqrt((dtsx * dtsx) + (dtsy * dtsy));

		// 计算单位向量
		final float d1x = dtsx / c;
		final float d1y = dtsy / c;

		// 计算本次移动的距离
		float travelDistance = Math.min(c, this.speed * WarsmashConstants.SIMULATION_STEP_TIME);
		final boolean done = c <= travelDistance;
		if (done) {
			travelDistance = c;
		}

		// 计算本次移动的位移
		final float dx = d1x * travelDistance;
		final float dy = d1y * travelDistance;

		// 更新投射物的位置
		this.x = this.x + dx;
		this.y = this.y + dy;

		// 判断是否击中目标并调用相应方法
		if (done && !this.done) {
			this.onHitTarget(game);
			this.done = true;
		}
		return this.done;
	}

	// 抽象方法，当投射物击中目标时调用
	protected abstract void onHitTarget(CSimulation game);

	// 获取投射物的当前X坐标
	public final float getX() {
		return this.x;
	}

	// 获取投射物的当前Y坐标
	public final float getY() {
		return this.y;
	}

	// 获取投射物的速度
	public final float getSpeed() {
		return this.speed;
	}

	// 获取投射物的来源单位
	public final CUnit getSource() {
		return source;
	}

	// 获取投射物的目标
	public final AbilityTarget getTarget() {
		return this.target;
	}

	// 判断投射物是否已完成
	public final boolean isDone() {
		return this.done;
	}

	// 获取目标的X坐标，如果启用自动追踪则获取实时坐标，否则获取初始坐标
	public final float getTargetX() {
		if (homingEnabled) {
			return this.target.getX();
		}
		else {
			return this.initialTargetX;
		}
	}

	// 获取目标的Y坐标，如果启用自动追踪则获取实时坐标，否则获取初始坐标
	public final float getTargetY() {
		if (homingEnabled) {
			return this.target.getY();
		}
		else {
			return this.initialTargetY;
		}
	}
}

