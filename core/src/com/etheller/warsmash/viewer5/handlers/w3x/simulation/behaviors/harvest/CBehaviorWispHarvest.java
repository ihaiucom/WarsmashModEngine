package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityWispHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;
// 采集--精灵采集树木行为
public class CBehaviorWispHarvest extends CAbstractRangedBehavior {
	private int lastIncomeTick;
	private final CAbilityWispHarvest abilityWispHarvest;
	private boolean harvesting = false;
	private SimulationRenderComponent spellEffectOverDestructable;

	// 构造函数，初始化单位和采集能力
	public CBehaviorWispHarvest(final CUnit unit, final CAbilityWispHarvest abilityWispHarvest) {
		super(unit);
		this.abilityWispHarvest = abilityWispHarvest;
	}

	// 重置行为的方法
	public CBehaviorWispHarvest reset(final CWidget target) {
		innerReset(target, false);
		return this;
	}

	@Override
	// 更新行为的方法，处理采集逻辑
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		// 如果单位位置不在目标位置，则移动到目标位置
		if ((target.getX() != unit.getX()) || (target.getY() != unit.getY())) {
			unit.setX(target.getX(), simulation.getWorldCollision(), simulation.getRegionManager());
			unit.setY(target.getY(), simulation.getWorldCollision(), simulation.getRegionManager());
			simulation.unitRepositioned(unit); // dont interpolate, instant jump
		}
		// 获取游戏当前帧数
		final int gameTurnTick = simulation.getGameTurnTick();
		// 如果采集周期到了，则给予收益
		if ((gameTurnTick - lastIncomeTick) >= abilityWispHarvest.getPeriodicIntervalLengthTicks()) {
			lastIncomeTick = gameTurnTick;
			// 获取玩家对象
			final CPlayer player = simulation.getPlayer(this.unit.getPlayerIndex());
			// 给玩家新增木头资源
			player.setLumber(player.getLumber() + this.abilityWispHarvest.getLumberPerInterval());
			// 飘字效果
			simulation.unitGainResourceEvent(this.unit, player.getId(), ResourceType.LUMBER,
					abilityWispHarvest.getLumberPerInterval());
		}
		// 如果不在采集涨停，则开始采集
		if (!harvesting) {
			onStartHarvesting(simulation);
			harvesting = true;
		}
		return this;
	}

	// 开始采集的方法
	private void onStartHarvesting(final CSimulation simulation) {
		// 添加 动画次标签 木材
		unit.getUnitAnimationListener().addSecondaryTag(AnimationTokens.SecondaryTag.LUMBER);
		// 播放音效
		simulation.unitLoopSoundEffectEvent(unit, abilityWispHarvest.getAlias());
		// TODO maybe use visitor instead of cast
		// 在可破坏物上 创建施法特效
		spellEffectOverDestructable = simulation.createSpellEffectOverDestructable(this.unit,
				(CDestructable) this.target, abilityWispHarvest.getAlias(),
				abilityWispHarvest.getArtAttachmentHeight());
		// 设置树木被占用
		simulation.tagTreeOwned((CDestructable) target);
	}

	// 停止采集的方法
	private void onStopHarvesting(final CSimulation simulation) {
		// 移除 动画次标签 木材
		unit.getUnitAnimationListener().removeSecondaryTag(AnimationTokens.SecondaryTag.LUMBER);
		// 停止音效
		simulation.unitStopSoundEffectEvent(unit, abilityWispHarvest.getAlias());
		// 树木占用状态移除
		simulation.untagTreeOwned((CDestructable) target);
		// TODO maybe use visitor instead of cast
		// 移除树木上的施法特效
		if (spellEffectOverDestructable != null) {
			spellEffectOverDestructable.remove();
			spellEffectOverDestructable = null;
		}
	}

	@Override
	// 更新无效目标的行为
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		// 目标是可破坏物
		if (this.target instanceof CDestructable) {
			// 正在采集
			if (harvesting) {
				// 停止采集
				onStopHarvesting(simulation);
				harvesting = false;
			}
			// 重新寻找最近的树木
			final CDestructable nearestTree = findNearestTree(this.unit, this.abilityWispHarvest, simulation,
					this.unit);
			if (nearestTree != null) {
				return reset(nearestTree);
			}
		}
		// 如果没有新的目标，就执行下一个行为
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	// 检查目标是否仍然有效
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		// 目标是可破坏物
		if (this.target instanceof CDestructable) {
			// 如果没有在采集，但是树木被占用，则不再有效
			if (!harvesting && simulation.isTreeOwned((CDestructable) this.target)) {
				return false;
			}
		}
		// 目标还活着
		return this.target.visit(AbilityTargetStillAliveVisitor.INSTANCE);
	}

	@Override
	// 移动前重置状态
	protected void resetBeforeMoving(final CSimulation simulation) {
		// 如果正在采集，则停止采集
		if (harvesting) {
			onStopHarvesting(simulation);
			harvesting = false;
		}
	}

	@Override
	// 检查是否在范围内
	public boolean isWithinRange(final CSimulation simulation) {
		return this.unit.canReach(this.target, 0);
	}

	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {

	}

	@Override
	public void begin(final CSimulation game) {

	}

	@Override
	// 结束行为时的逻辑
	public void end(final CSimulation game, final boolean interrupted) {
		// 如果正在采集，则停止采集
		if (harvesting) {
			onStopHarvesting(game);
			harvesting = false;
		}
	}

	@Override
	// 获取高亮顺序ID
	public int getHighlightOrderId() {
		return OrderIds.wispharvest;
	}

	// 查找最近的树木
	public static CDestructable findNearestTree(final CUnit worker, final CAbilityWispHarvest abilityHarvest,
			final CSimulation simulation, final CWidget toObject) {
		CDestructable nearestMine = null;
		double nearestMineDistance = abilityHarvest.getCastRange() * abilityHarvest.getCastRange();
		for (final CDestructable unit : simulation.getDestructables()) {
			if (!unit.isDead() && !simulation.isTreeOwned(unit)
					&& unit.canBeTargetedBy(simulation, worker, CAbilityWispHarvest.TREE_ALIVE_TYPE_ONLY)) {
				final double distance = unit.distanceSquaredNoCollision(toObject);
				if (distance < nearestMineDistance) {
					nearestMineDistance = distance;
					nearestMine = unit;
				}
			}
		}
		return nearestMine;
	}

	@Override
	// 检查行为是否可以被打断
	public boolean interruptable() {
		return true;
	}
}
