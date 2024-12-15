package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import java.util.concurrent.atomic.AtomicInteger;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.IntMap;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructableEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
// 碰撞弹道类，继承自CProjectile类
public class CCollisionProjectile extends CProjectile {

	// 用于回收的矩形区域
	private static final Rectangle recycleRect = new Rectangle();

	// 弹道监听器
	private final CAbilityCollisionProjectileListener projectileListener;

	// 最大命中次数， maxHits <= 0 表示不限制命中次数
	private int maxHits = 0;
	// 目标之前已经命中的次数
	private int maxHitsPerTarget = 0;

	// 碰撞间隔
	private int collisionInterval = 1;

	// 下一次碰撞的tick
	private int nextCollisionTick = 0;

	// 当前碰撞半径
	private float collisionRadius = 0;

	// 最终碰撞半径
	private float finalCollisionRadius = 0;
	// 初始碰撞半径
	private float startingCollisionRadius = 0;
	// 到目标的距离
	private float distanceToTarget = 0;

	// 碰撞记录
	private IntMap<Integer> collisions = new IntMap<>();
	// 当前命中次数
	private int hits = 0;

	// 是否提供计数
	private boolean provideCounts = false;

	// 构造函数，初始化碰撞弹道
	public CCollisionProjectile(final float x, final float y, final float speed, final AbilityTarget target,
			boolean homingEnabled, final CUnit source, final int maxHits, final int hitsPerTarget,
			final float startingRadius, final float finalRadius, final float collisionInterval,
			final CAbilityCollisionProjectileListener projectileListener, boolean provideCounts) {
		super(x, y, speed, target, homingEnabled, source);
		this.projectileListener = projectileListener;

		this.maxHits = maxHits;
		// maxHits <= 0 表示不限制命中次数
		if (this.maxHits <= 0) {
			this.maxHits = 0;
			this.hits = -1;
		}
		this.maxHitsPerTarget = hitsPerTarget;
		this.startingCollisionRadius = startingRadius;
		this.finalCollisionRadius = finalRadius;

		this.provideCounts = provideCounts;

		// 计算距离
		final float dtsx = getTargetX() - this.x;
		final float dtsy = getTargetY() - this.y;
		this.distanceToTarget = (float) Math.sqrt((dtsx * dtsx) + (dtsy * dtsy));

		// 计算碰撞间隔
		this.collisionInterval = (int) (collisionInterval / WarsmashConstants.SIMULATION_STEP_TIME);
	}

	// 当命中目标时调用的方法，当前未使用
	@Override
	protected void onHitTarget(CSimulation game) {
		// Not used
	}

	// 判断是否可以命中目标
	protected boolean canHitTarget(CSimulation game, CWidget target) {
		return this.projectileListener.canHitTarget(game, target);
	}

	// 当命中目标时调用的方法
	protected void onHitTarget(CSimulation game, CWidget target) {
		projectileListener.onHit(game, target);
	}

	// 更新弹道状态
	@Override
	public boolean update(final CSimulation game) {
		if (this.nextCollisionTick == 0) {
			this.nextCollisionTick = game.getGameTurnTick();
		}

		// 计算目标与当前位置的差值
		final float dtsx = getTargetX() - this.x;
		final float dtsy = getTargetY() - this.y;
		// c 是目标与当前位置之间的直线距离，通过勾股定理计算得出。
		final float c = (float) Math.sqrt((dtsx * dtsx) + (dtsy * dtsy));

		// 计算单位向量
		final float d1x = dtsx / c;
		final float d1y = dtsy / c;

		// 计算本次移动的距离
		float travelDistance = Math.min(c, this.getSpeed() * WarsmashConstants.SIMULATION_STEP_TIME);
		this.done = c <= travelDistance;
		if (this.done) {
			travelDistance = c;
		}

		// 计算本次移动的位移
		final float dx = d1x * travelDistance;
		final float dy = d1y * travelDistance;

		// 更新投射物的位置
		this.x = this.x + dx;
		this.y = this.y + dy;

		if (game.getGameTurnTick() >= this.nextCollisionTick) {
			// 计算碰撞半径
			if (this.collisionRadius != this.finalCollisionRadius) {
				// 碰撞半径插值
				this.collisionRadius = this.startingCollisionRadius
						+ (this.finalCollisionRadius - this.startingCollisionRadius)
								* (1 - (c / this.distanceToTarget));
			}
			// 通知监听当前投射物位置
			AbilityPointTarget loc = new AbilityPointTarget(this.x, this.y);
			this.projectileListener.setCurrentLocation(loc);
			// 刷新矩形区域
			recycleRect.set(this.getX() - collisionRadius, this.getY() - collisionRadius, collisionRadius * 2,
					collisionRadius * 2);

			// 如果统计数量
			if (provideCounts ) {
				// 可破坏物计算器
				AtomicInteger destCount = new AtomicInteger(0);
				// 单位计算器
				AtomicInteger unitCount = new AtomicInteger(0);

				// 遍历矩形中的可破坏物体，并根据碰撞规则进行处理
				game.getWorldCollision().enumDestructablesInRect(recycleRect, new CDestructableEnumFunction() {
					@Override
					public boolean call(CDestructable enumDestructable) {
						if (hits < maxHits && collisions.get(enumDestructable.getHandleId(), 0) < maxHitsPerTarget
								&& enumDestructable.distance(loc.getX(), loc.getY()) < collisionRadius && canHitTarget(game, enumDestructable)) {
							// 计数器加1
							destCount.incrementAndGet();
						}
						return false;
					}
				});

				// 遍历指定矩形区域中的所有单位，并根据碰撞规则进行处理
				game.getWorldCollision().enumUnitsInRect(recycleRect, new CUnitEnumFunction() {
					@Override
					public boolean call(final CUnit enumUnit) {
						if (hits < maxHits && collisions.get(enumUnit.getHandleId(), 0) < maxHitsPerTarget
								&& enumUnit.canReach(loc, collisionRadius) && canHitTarget(game, enumUnit)) {
							// 计数器加1
							unitCount.incrementAndGet();
						}
						return false;
					}
				});

				// 设置目标数量
				this.projectileListener.setUnitTargets(unitCount.get());
				this.projectileListener.setDestructableTargets(destCount.get());
			}

			// 当投射物即将命中时调用的方法
			this.projectileListener.onPreHits(game, loc);


			// 遍历矩形中的可破坏物体
			game.getWorldCollision().enumDestructablesInRect(recycleRect, new CDestructableEnumFunction() {
				@Override
				// 当调用此方法时，对每个可破坏物体进行处理
				public boolean call(CDestructable enumDestructable) {
					// 命中数量 < 最大命中次数
					// 目标之前命中的次数 < 目标可以命中的最大次数
					// 距离目标 < 碰撞半径
					// 判断是否可以命中目标
					if (hits < maxHits && collisions.get(enumDestructable.getHandleId(), 0) < maxHitsPerTarget
							&& enumDestructable.distance(loc.getX(), loc.getY()) < collisionRadius && canHitTarget(game, enumDestructable)) {
						// 处理击中目标的逻辑
						onHitTarget(game, enumDestructable);
						// 如果限制命中次数，更新当前已经命中的次数
						if (maxHits > 0) {
							hits++;
						}
						// 更新目标被命中次数
						collisions.put(enumDestructable.getHandleId(), collisions.get(enumDestructable.getHandleId(), 0) + 1);
					}
					return false;
				}
			});

			// 遍历指定矩形区域中的所有单位，并根据碰撞规则进行处理
			game.getWorldCollision().enumUnitsInRect(recycleRect, new CUnitEnumFunction() {
				@Override
				public boolean call(final CUnit enumUnit) {
					// 命中数量 < 最大命中次数
					// 目标之前命中的次数 < 目标可以命中的最大次数
					// 距离目标 < 碰撞半径
					// 判断是否可以命中目标
					if (hits < maxHits && collisions.get(enumUnit.getHandleId(), 0) < maxHitsPerTarget
							&& enumUnit.canReach(loc, collisionRadius) && canHitTarget(game, enumUnit)) {
						// 处理命中目标
						onHitTarget(game, enumUnit);
						// 如果限制命中次数，更新当前已经命中的次数
						if (maxHits > 0) {
							hits++;
						}
						// 更新目标被命中次数
						collisions.put(enumUnit.getHandleId(), collisions.get(enumUnit.getHandleId(), 0) + 1);
					}
					return false;
				}
			});


			// 刷新下次碰撞的时间tick
			this.nextCollisionTick = game.getGameTurnTick() + this.collisionInterval;
		}
		this.done |= hits >= maxHits;

		return this.done;
	}
}

