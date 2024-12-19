package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorAcolyteHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;
/**
 * 金矿能力，允许采矿单位在指定范围内进行金矿采集。
 */
public class CAbilityBlightedGoldMine extends CAbilityOverlayedMine {
	public static final int NO_MINER = -1; // 表示没有矿工
	private int goldPerInterval; // 每个间隔获得的金币数量
	private float intervalDuration; // 采集间隔持续时间
	private int maxNumberOfMiners; // 最大矿工数量
	private float radiusOfMiningRing; // 采矿环的半径
	private final CBehaviorAcolyteHarvest[] activeMiners; // 活动矿工数组
	private final Vector2[] minerLocs; // 矿工位置数组
	private int currentActiveMinerCount; // 当前活跃的矿工数量
	private int lastIncomeTick; // 上次收入的tick

	private final List<SimulationRenderComponent> spellEffects = new ArrayList<>(); // 特效列表

	/**
	 * CAbilityBlightedGoldMine构造函数，用于初始化金矿能力的属性。
	 */
	public CAbilityBlightedGoldMine(final int handleId, final War3ID code, final War3ID alias, final int goldPerInterval,
			final float intervalDuration, final int maxNumberOfMiners, final float radiusOfMiningRing) {
		super(handleId, code, alias);
		this.goldPerInterval = goldPerInterval;
		this.intervalDuration = intervalDuration;
		this.maxNumberOfMiners = maxNumberOfMiners;
		this.radiusOfMiningRing = radiusOfMiningRing;
		this.activeMiners = new CBehaviorAcolyteHarvest[maxNumberOfMiners];
		this.minerLocs = new Vector2[maxNumberOfMiners];
	}

	/**
	 * 在单位添加金矿能力时执行的方法，设置矿工的位置并创建特效。
	 */
	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		for (int i = 0; i < this.minerLocs.length; i++) {
			final double angleSize = (StrictMath.PI * 2) / this.maxNumberOfMiners;
			final double thisMinerAngle = (angleSize * i) + (StrictMath.PI / 2);
			final float harvestStandX = unit.getX()
					+ (float) (StrictMath.cos(thisMinerAngle) * this.radiusOfMiningRing);
			final float harvestStandY = unit.getY()
					+ (float) (StrictMath.sin(thisMinerAngle) * this.radiusOfMiningRing);
			this.minerLocs[i] = new Vector2(harvestStandX, harvestStandY);
			final SimulationRenderComponent spellEffect = game.spawnSpellEffectOnPoint(harvestStandX, harvestStandY,
					(float) (StrictMath.toDegrees(thisMinerAngle)), getAlias(), CEffectType.EFFECT, 0);
			this.spellEffects.add(spellEffect);
		}
	}

	/**
	 * 在单位移除金矿能力时执行的方法，清理特效。
	 */
	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		for (final SimulationRenderComponent spellEffect : this.spellEffects) {
			spellEffect.remove();
		}
		this.spellEffects.clear();
	}

	/**
	 * 在单位死亡时执行的方法，清理特效。
	 */
	@Override
	public void onDeath(CSimulation game, CUnit cUnit) {
		super.onDeath(game, cUnit);
		for (final SimulationRenderComponent spellEffect : this.spellEffects) {
			spellEffect.remove();
		}
		this.spellEffects.clear();
	}

	/**
	 * 每个游戏tick执行一次的方法，进行金币的采集和更新逻辑。
	 */
	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
		if (this.currentActiveMinerCount > 0) {
			final float currentInterval = this.intervalDuration
					* (this.maxNumberOfMiners / this.currentActiveMinerCount);
			final int nextIncomeTick = this.lastIncomeTick
					+ (int) (currentInterval / WarsmashConstants.SIMULATION_STEP_TIME);
			final int currentTurnTick = game.getGameTurnTick();
			final CAbilityGoldMinable parentGoldMineAbility = getParentGoldMineAbility();
			final int totalGoldAvailable = parentGoldMineAbility.getGold();
			if ((currentTurnTick >= nextIncomeTick) && (parentGoldMineAbility != null) && (totalGoldAvailable > 0)) {
				this.lastIncomeTick = currentTurnTick;
				final CPlayer player = game.getPlayer(unit.getPlayerIndex());
				final int goldGained = Math.min(totalGoldAvailable, this.goldPerInterval);
				player.addGold(goldGained);
				parentGoldMineAbility.setGold(totalGoldAvailable - goldGained);
				game.unitGainResourceEvent(unit, player.getId(), ResourceType.GOLD, goldGained);
			}
		}
//		final boolean empty = this.activeMiners.isEmpty();
//		if (empty != this.wasEmpty) {
//			if (empty) {
//				unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.WORK);
//			}
//			else {
//				unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.WORK);
//			}
//			this.wasEmpty = empty;
//		}
//		for (int i = this.activeMiners.size() - 1; i >= 0; i--) {
//			final CBehaviorHarvest activeMiner = this.activeMiners.get(i);
//			if (game.getGameTurnTick() >= activeMiner.getPopoutFromMineTurnTick()) {
//
//				final int goldMined = Math.min(this.gold, activeMiner.getGoldCapacity());
//				this.gold -= goldMined;
//				if (this.gold <= 0) {
//					unit.setLife(game, 0);
//				}
//				activeMiner.popoutFromMine(goldMined);
//				this.activeMiners.remove(i);
//			}
//		}
	}

	/**
	 * 开始一个无目标的行为。
	 */
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	/**
	 * 开始一个有目标的行为。
	 */
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	/**
	 * 开始一个没有目标的行为。
	 */
	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	/**
	 * 检查单位是否可以作为目标。
	 */
	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	/**
	 * 检查单位的目标点是否有效。
	 */
	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	/**
	 * 检查没有目标时的有效性。
	 */
	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	/**
	 * 检查能力的使用条件。
	 */
	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();
	}

	/**
	 * 从队列中取消订单时执行的方法。
	 */
	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	/**
	 * 尝试添加矿工的方法，返回矿工的索引。
	 */
	public int tryAddMiner(final CUnit acolyte, final CBehaviorAcolyteHarvest behaviorAcolyteHarvest) {
		if (behaviorAcolyteHarvest == null) {
			throw new NullPointerException();
		}
		int minerIndex = NO_MINER;
		double minerDistSq = Float.MAX_VALUE;
		// 遍历矿工位置数组，找到距离目标矿工位置最近的空位
		for (int i = 0; i < this.activeMiners.length; i++) {
			// 如果当前位置没有矿工，则使用该位置
			if (this.activeMiners[i] == null) {
				final double thisMineDistSq = acolyte.distanceSquaredNoCollision(this.minerLocs[i].x,
						this.minerLocs[i].y);
				if (thisMineDistSq < minerDistSq) {
					minerIndex = i;
					minerDistSq = thisMineDistSq;
				}
			}
		}
		if (minerIndex != NO_MINER) {
			this.activeMiners[minerIndex] = behaviorAcolyteHarvest;
			this.currentActiveMinerCount++;
		}
		return minerIndex;
	}

	/**
	 * 移除矿工的方法。
	 */
	public void removeMiner(final CBehaviorAcolyteHarvest behaviorAcolyteHarvest) {
		if (behaviorAcolyteHarvest == null) {
			throw new NullPointerException();
		}
		for (int i = 0; i < this.activeMiners.length; i++) {
			if (this.activeMiners[i] == behaviorAcolyteHarvest) {
				this.activeMiners[i] = null;
				this.currentActiveMinerCount--;
			}
		}
	}

	/**
	 * 获取最大矿工数量的方法。
	 */
	public int getMaxNumberOfMiners() {
		return this.maxNumberOfMiners;
	}

	/**
	 * 获取采矿环半径的方法。
	 */
	public float getRadiusOfMiningRing() {
		return this.radiusOfMiningRing;
	}

	/**
	 * 设置每个间隔获得的金币数量的方法。
	 */
	public void setGoldPerInterval(final int goldPerInterval) {
		this.goldPerInterval = goldPerInterval;
	}

	/**
	 * 设置采集间隔持续时间的方法。
	 */
	public void setIntervalDuration(final float intervalDuration) {
		this.intervalDuration = intervalDuration;
	}

	/**
	 * 设置最大矿工数量的方法。
	 */
	public void setMaxNumberOfMiners(final int maxNumberOfMiners) {
		this.maxNumberOfMiners = maxNumberOfMiners;
	}

	/**
	 * 设置采矿环的半径的方法。
	 */
	public void setRadiusOfMiningRing(final float radiusOfMiningRing) {
		this.radiusOfMiningRing = radiusOfMiningRing;
	}

	/**
	 * 获取指定索引的矿工位置的方法。
	 */
	public Vector2 getMinerLoc(final int index) {
		return this.minerLocs[index];
	}
}
