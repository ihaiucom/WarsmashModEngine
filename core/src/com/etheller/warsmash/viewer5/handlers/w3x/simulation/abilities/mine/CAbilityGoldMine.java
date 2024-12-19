package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
// 表示一种金矿能力的类
public class CAbilityGoldMine extends AbstractGenericNoIconAbility implements CAbilityGoldMinable {
	private int gold; // 当前金矿中的黄金数量
	private float miningDuration; // 挖掘持续时间
	private int miningCapacity; // 挖掘容量
	private final List<CBehaviorHarvest> activeMiners; // 当前活跃的矿工列表
	private boolean wasEmpty; // 是否为空

	// 构造函数，初始化金矿的各种属性
	public CAbilityGoldMine(final int handleId, final War3ID code, final War3ID alias, final int maxGold, final float miningDuration,
			final int miningCapacity) {
		super(handleId, code, alias);
		this.gold = maxGold;
		this.miningDuration = miningDuration;
		this.miningCapacity = miningCapacity;
		this.activeMiners = new ArrayList<>();
		this.wasEmpty = this.activeMiners.isEmpty();
	}

	@Override
	// 添加能力时的处理
	public void onAdd(final CSimulation game, final CUnit unit) {

	}

	@Override
	// 移除能力时的处理
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	@Override
	// 每个游戏时钟周期的处理
	public void onTick(final CSimulation game, final CUnit unit) {
		final boolean empty = this.activeMiners.isEmpty();
		if (empty != this.wasEmpty) {
			if (empty) {
				unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.WORK);
			}
			else {
				unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.WORK);
			}
			this.wasEmpty = empty;
		}
		for (int i = this.activeMiners.size() - 1; i >= 0; i--) {
			final CBehaviorHarvest activeMiner = this.activeMiners.get(i);
			if (game.getGameTurnTick() >= activeMiner.getPopoutFromMineTurnTick()) {

				int goldMined;
				if (this.gold > 0) {
					goldMined = Math.min(this.gold, activeMiner.getGoldCapacity());
					this.gold -= goldMined;
				}
				else {
					goldMined = 0;
				}
				activeMiner.popoutFromMine(goldMined);
				this.activeMiners.remove(i);
			}
		}
		if (this.gold <= 0) {
			unit.setLife(game, 0);
		}
	}

	@Override
	// 开始能力的处理（无目标）
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	@Override
	// 开始能力的处理（带点目标）
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	@Override
	// 开始能力的处理（无目标）
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	@Override
	// 检查能力的目标是否合法
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	// 检查能力的目标是否合法（带点目标）
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	// 检查能力的目标是否合法（无目标）
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	// 检查能力是否可以使用
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();
	}

	@Override
	// 处理取消队列中的能力
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	// 获取当前金矿中的黄金数量
	public int getGold() {
		return this.gold;
	}

	@Override
	// 设置金矿中的黄金数量
	public void setGold(final int gold) {
		this.gold = gold;
	}

	@Override
	// 获取当前活跃矿工的数量
	public int getActiveMinerCount() {
		return this.activeMiners.size();
	}

	@Override
	// 添加矿工到活跃矿工列表
	public void addMiner(final CBehaviorHarvest miner) {
		this.activeMiners.add(miner);
	}

	@Override
	// 获取挖掘容量
	public int getMiningCapacity() {
		return this.miningCapacity;
	}

	@Override
	// 获取挖掘持续时间
	public float getMiningDuration() {
		return this.miningDuration;
	}

	// 设置挖掘容量
	public void setMiningCapacity(final int miningCapacity) {
		this.miningCapacity = miningCapacity;
	}

	// 设置挖掘持续时间
	public void setMiningDuration(final float miningDuration) {
		this.miningDuration = miningDuration;
	}

	@Override
	// 处理单位死亡的情况
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

	@Override
	// 判断是否为基础矿
	public boolean isBaseMine() {
		return true;
	}
}
