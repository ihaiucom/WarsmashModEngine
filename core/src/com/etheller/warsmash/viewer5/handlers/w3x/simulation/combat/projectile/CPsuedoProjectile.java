package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import java.util.concurrent.atomic.AtomicInteger;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.IntMap;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructableEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
// CPsuedoProjectile类继承自CProjectile类，表示一个步数投射物对象， 可以用于“穿刺”技能
public class CPsuedoProjectile extends CProjectile {

	// 表示回收矩形的静态常量
	private static final Rectangle recycleRect = new Rectangle();

	// 处理能力碰撞投射体的监听器
	private final CAbilityCollisionProjectileListener projectileListener;

	// 最大命中的次数
	private int maxHits = 0;
	// 每个目标的最大命中次数
	private int maxHitsPerTarget = 0;

	// 步进间隔
	private int stepInterval = 1;

	// 下一个动作的时间tick
	private int nextActionTick = 0;

	// 碰撞半径
	private float collisionRadius = 0;

	// 最终碰撞半径
	private float finalCollisionRadius = 0;
	// 起始碰撞半径
	private float startingCollisionRadius = 0;
	// 到目标的距离
	private float distanceToTarget = 0;

	// 碰撞次数映射
	private IntMap<Integer> collisions = new IntMap<>();
	// 已发生的命中次数
	private int hits = 0;

	// 别名ID
	private War3ID alias;
	// 效果类型
	private CEffectType effectType;
	// 效果艺术索引
	private int effectArtIndex;

	// 是否提供计数
	private boolean provideCounts;
	// 每多次（步数，检测次数），创建一个美术特效
	private int artSkip;
	// 当前艺术计数
	private int artCount = 1;

	// x轴方向的增量
	private float dx;
	// y轴方向的增量
	private float dy;
	//
	private int steps;

	// 当前步骤计数，当前检测次数
	private int stepCount = 0;

	// 构造函数，初始化伪投射物的各种属性
	public CPsuedoProjectile(final float x, final float y, final float speed, final float projectileStepInterval, final int projectileArtSkip,
			final AbilityTarget target, boolean homingEnabled, final CUnit source, final War3ID alias,
			final CEffectType effectType, final int effectArtIndex, final int maxHits, final int hitsPerTarget,
			final float startingRadius, final float finalRadius, final CAbilityCollisionProjectileListener projectileListener, boolean provideCounts) {
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

		this.alias = alias;
		this.effectType = effectType;
		this.effectArtIndex = effectArtIndex;

		// 计算目标距离
		final float dtsx = getTargetX() - this.x;
		final float dtsy = getTargetY() - this.y;
		this.distanceToTarget = (float) Math.sqrt((dtsx * dtsx) + (dtsy * dtsy));

		// 计算单位方向向量
		final float d1x = dtsx / this.distanceToTarget;
		final float d1y = dtsy / this.distanceToTarget;
		// 计算检测次数，步数
		if (speed != 0) {
			this.steps = ((int)Math.round(this.distanceToTarget) / (int)speed) + 1;
		}

		// 计算移动距离
		float travelDistance = Math.min(speed, distanceToTarget/this.steps);

		// 计算每步的x轴和y轴方向的位移
		this.dx = d1x * travelDistance;
		this.dy = d1y * travelDistance;

		// 计算步长间隔
		this.stepInterval = (int) (projectileStepInterval / WarsmashConstants.SIMULATION_STEP_TIME);
		this.artSkip = projectileArtSkip;

	}

	// 当投射物击中目标时调用的方法，当前未使用
	@Override
	protected void onHitTarget(CSimulation game) {
		// Not used
	}

	// 判断是否可以击中目标的方法
	protected boolean canHitTarget(CSimulation game, CWidget target) {
		return this.projectileListener.canHitTarget(game, target);
	}

	// 当投射物击中目标时调用的方法
	protected void onHitTarget(CSimulation game, CWidget target) {
		projectileListener.onHit(game, target);
	}

	// 更新投射物状态的方法
	@Override
	public boolean update(final CSimulation game) {
		if (this.nextActionTick == 0) {
			this.nextActionTick = game.getGameTurnTick();
		}

		if (game.getGameTurnTick() >= this.nextActionTick) {

			// 每隔artSkip步数，创建一个美术特效
			artCount = (artCount+1) % artSkip;
			if (artCount == 0) {
				// 创建特效，在当前投射物位置
				game.spawnTemporarySpellEffectOnPoint(this.x, this.y, 0, this.alias, this.effectType, this.effectArtIndex);
			}

			// 计算碰撞半径
			if (this.collisionRadius != this.finalCollisionRadius) {
				final float dtsx = getTargetX() - this.x;
				final float dtsy = getTargetY() - this.y;
				final float c = (float) Math.sqrt((dtsx * dtsx) + (dtsy * dtsy));
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

				this.projectileListener.setUnitTargets(unitCount.get());
				this.projectileListener.setDestructableTargets(destCount.get());
			}


			// 当投射物即将命中时调用的方法
			this.projectileListener.onPreHits(game, loc);

			// 遍历矩形中的可破坏物体
			game.getWorldCollision().enumDestructablesInRect(recycleRect, new CDestructableEnumFunction() {
				@Override
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
						// 处理击中目标的逻辑
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

			// 更新投射物的位置
			this.x = this.x + this.dx;
			this.y = this.y + this.dy;

			// 刷新下次碰撞的时间tick
			this.nextActionTick = game.getGameTurnTick() + this.stepInterval;
			// 更新当前步数
			this.stepCount++;
			// 检测步数是否完成
			this.done |= this.stepCount > this.steps;
		}
		// 命中次数达到最大值时，投射物完成
		this.done |= hits >= maxHits;

		return this.done;
	}
}
