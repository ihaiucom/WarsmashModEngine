package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMinable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;
// 采集--金矿/树木
public class CBehaviorHarvest extends CAbstractRangedBehavior
		implements AbilityTargetVisitor<CBehavior>, CBehaviorAttackListener {
	private final CAbilityHarvest abilityHarvest;
	private CSimulation simulation;
	private int popoutFromMineTurnTick = 0; // 弹出矿的Tick数

	// 构造函数，初始化单位和收割能力
	public CBehaviorHarvest(final CUnit unit, final CAbilityHarvest abilityHarvest) {
		super(unit);
		this.abilityHarvest = abilityHarvest;
	}

	// 重置行为并设定目标
	public CBehaviorHarvest reset(final CWidget target) {
		innerReset(target, target instanceof CUnit);
		this.abilityHarvest.setLastHarvestTarget(target);
		if (this.popoutFromMineTurnTick != 0) {
			// TODO this check is probably only for debug and should be removed after
			// extensive testing
			throw new IllegalStateException("A unit took action while within a gold mine.");
		}
		return this;
	}

	@Override
	// 判断单位是否在攻击范围内
	public boolean isWithinRange(final CSimulation simulation) {
		return this.unit.canReach(this.target, this.abilityHarvest.getTreeAttack().getRange());
	}

	@Override
	// 获取高亮顺序ID
	public int getHighlightOrderId() {
		return OrderIds.harvest;
	}

	@Override
	// 更新行为并访问目标
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		this.simulation = simulation;
		return this.target.visit(this);
	}

	@Override
	// 接受点目标
	public CBehavior accept(final AbilityPointTarget target) {
		// 如果是坐标点，就执行执行下一个命令行为
		return CBehaviorHarvest.this.unit.pollNextOrderBehavior(this.simulation);
	}

	@Override
	// 接受单位目标 金矿
	public CBehavior accept(final CUnit target) {
		// 获取携带的资源数量为0 或者 携带的资源不是金矿
		if ((this.abilityHarvest.getCarriedResourceAmount() == 0)
				|| (this.abilityHarvest.getCarriedResourceType() != ResourceType.GOLD)) {
			// 遍历目标能力，如果有矿山能力，就尝试挖矿
			for (final CAbility ability : target.getAbilities()) {
				if (ability instanceof CAbilityGoldMinable) {
					final CAbilityGoldMinable abilityGoldMine = (CAbilityGoldMinable) ability;
					// 获取当前激活的矿工数量
					final int activeMiners = abilityGoldMine.getActiveMinerCount();
					// 如果当前矿工数量小于矿工容量
					if (activeMiners < abilityGoldMine.getMiningCapacity()) {
						// 添加一个矿工进行挖掘
						abilityGoldMine.addMiner(this);
						// 矿工 隐藏
						this.unit.setHidden(true);
						// 矿工 无敌
						this.unit.setInvulnerable(true);
						// 矿工 暂停
						this.unit.setPaused(true);
						// 矿工 不能接受命令
						this.unit.setAcceptingOrders(false);
						// 矿工 被弹出金矿 时间帧
						this.popoutFromMineTurnTick = this.simulation.getGameTurnTick()
								+ (int) (abilityGoldMine.getMiningDuration() / WarsmashConstants.SIMULATION_STEP_TIME);
					}
					else {
						// we are stuck waiting to mine, let's make sure we play stand animation
						// 否则等待空位，播放站立动画
						this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY,
								1.0f, true);
					}
					return this;
				}
			}
			// weird invalid target and we have no resources, consider harvesting done
			// 奇怪的无效目标，我们没有资源，考虑收割完成
			if (this.abilityHarvest.getCarriedResourceAmount() == 0) {
				return this.unit.pollNextOrderBehavior(this.simulation);
			}
			else {
				// 把资源送回基地
				return this.abilityHarvest.getBehaviorReturnResources().reset(this.simulation);
			}
		}
		else {
			// we have some GOLD and we're not in a mine (?) lets do a return resources
			// order
			// 否则就先把金矿运送会基地
			return this.abilityHarvest.getBehaviorReturnResources().reset(this.simulation);
		}
	}

	// 从矿中弹出，并处理金矿
	public void popoutFromMine(final int goldMined) {
		this.popoutFromMineTurnTick = 0;
		// 矿工 显示
		this.unit.setHidden(false);
		// 矿工 不无敌
		this.unit.setInvulnerable(false);
		// 矿工 取消暂停
		this.unit.setPaused(false);
		// 矿工 接受命令
		this.unit.setAcceptingOrders(true);
		// 重置不携带任何资源
		dropResources();
		// 设置携带金矿数量
		this.abilityHarvest.setCarriedResources(ResourceType.GOLD, goldMined);
		// 设置矿工动画 第次标签 为金矿
		this.unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.GOLD);
		// 当单位重新定位时调用的方法
		this.simulation.unitRepositioned(this.unit);
	}

	@Override
	// 接受可破坏目标 树木
	public CBehavior accept(final CDestructable target) {
		// 如果携带的资源不是木头 或者 携带的数量 小于 木头容量
		if ((this.abilityHarvest.getCarriedResourceType() != ResourceType.LUMBER)
				|| (this.abilityHarvest.getCarriedResourceAmount() < this.abilityHarvest.getLumberCapacity())) {
			// 攻击树木行为
			return this.abilityHarvest.getBehaviorTreeAttack().reset(getHighlightOrderId(),
					this.abilityHarvest.getTreeAttack(), target, false, this);
		}
		else {
			// we have some LUMBER and we can't carry any more, time to return resources
			// 把资源送回基地
			return this.abilityHarvest.getBehaviorReturnResources().reset(this.simulation);
		}
	}

	@Override
	// 处理命中事件
	public void onHit(final AbilityTarget target, final float damage) {
		if (this.abilityHarvest.getCarriedResourceType() != ResourceType.LUMBER) {
			dropResources();
		}
		this.abilityHarvest.setCarriedResources(ResourceType.LUMBER,
				Math.min(this.abilityHarvest.getCarriedResourceAmount() + this.abilityHarvest.getDamageToTree(),
						this.abilityHarvest.getLumberCapacity()));
		this.unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.LUMBER);
		if (target instanceof CDestructable) {
			if (this.unit.getUnitType().getClassifications().contains(CUnitClassification.UNDEAD)) {
				((CDestructable) target).setBlighted(true);
			}
		}
	}

	@Override
	// 行为启动时调用
	public void onLaunch() {

	}

	@Override
	// 攻击树木 后摇处理
	public CBehavior onFirstUpdateAfterBackswing(final CBehaviorAttack currentAttackBehavior) {
		// 如果采集满了
		if (this.abilityHarvest.getCarriedResourceAmount() >= this.abilityHarvest.getLumberCapacity()) {
			// 送资源回基地
			return this.abilityHarvest.getBehaviorReturnResources().reset(this.simulation);
		}
		return currentAttackBehavior;
	}

	@Override
	// 攻击树木目标无效时，应该是树木被砍到了
	public CBehavior onFinish(final CSimulation game, final CUnit finishingUnit) {
		// 如果采集满了
		if (this.abilityHarvest.getCarriedResourceAmount() >= this.abilityHarvest.getLumberCapacity()) {
			// 送资源回基地
			return this.abilityHarvest.getBehaviorReturnResources().reset(this.simulation);
		}
		// 更新当目标无效时的行为， 查找附件的树木；如果有就继续砍新树木，如果没有就执行下一个命令
		return updateOnInvalidTarget(game);
	}

	@Override
	// 接受物品目标
	public CBehavior accept(final CItem target) {
		// 如果是物品，就执行执行下一个命令行为
		return this.unit.pollNextOrderBehavior(this.simulation);
	}

	@Override
	// 检查目标是否仍然有效
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return this.target.visit(AbilityTargetStillAliveVisitor.INSTANCE);
	}

	@Override
	// 更新当目标无效时的行为， 查找附件的树木；如果有就继续砍新树木，如果没有就执行下一个命令
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		// 目标是可破坏物
		if (this.target instanceof CDestructable) {
			// 查找最近的树
			final CDestructable nearestTree = CBehaviorReturnResources.findNearestTree(this.unit, this.abilityHarvest,
					simulation, this.unit);
			// 找到树，重置行为
			if (nearestTree != null) {
				return reset(nearestTree);
			}
		}
		// 执行下一个命令行为
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	// 移动前重置行为
	protected void resetBeforeMoving(final CSimulation simulation) {

	}

	@Override
	// 开始行为
	public void begin(final CSimulation game) {

	}

	@Override
	// 结束行为
	public void end(final CSimulation game, final boolean interrupted) {

	}

	// 获取弹出矿井的回合时间
	public int getPopoutFromMineTurnTick() {
		return this.popoutFromMineTurnTick;
	}

	// 获取金矿容量
	public int getGoldCapacity() {
		return this.abilityHarvest.getGoldCapacity();
	}

	// 处理资源投放
	private void dropResources() {
		if (this.abilityHarvest.getCarriedResourceType() != null && this.abilityHarvest.getCarriedResourceAmount() > 0) {
			switch (this.abilityHarvest.getCarriedResourceType()) {
			case FOOD: // 食物
				throw new IllegalStateException("Unit used Harvest skill to carry FOOD resource!");
			case GOLD: // 金矿
				// 动画移除次标签 金矿
				this.unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.GOLD);
				break;
			case LUMBER: // 木材
				// 动画移除次标签 木材
				this.unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.LUMBER);
				break;
			}
		}
		// 重置不携带任何资源
		this.abilityHarvest.setCarriedResources(null, 0);
	}

	@Override
	// 结束移动
	public void endMove(final CSimulation game, final boolean interrupted) {

	}

	@Override
	// 判断是否可被打断
	public boolean interruptable() {
		return true;
	}

}
