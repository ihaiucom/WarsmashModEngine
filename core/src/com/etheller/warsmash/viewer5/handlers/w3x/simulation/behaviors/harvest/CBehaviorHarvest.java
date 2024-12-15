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
// 收割行为
public class CBehaviorHarvest extends CAbstractRangedBehavior
		implements AbilityTargetVisitor<CBehavior>, CBehaviorAttackListener {
	private final CAbilityHarvest abilityHarvest;
	private CSimulation simulation;
	private int popoutFromMineTurnTick = 0;

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
		return CBehaviorHarvest.this.unit.pollNextOrderBehavior(this.simulation);
	}

	@Override
	// 接受单位目标
	public CBehavior accept(final CUnit target) {
		if ((this.abilityHarvest.getCarriedResourceAmount() == 0)
				|| (this.abilityHarvest.getCarriedResourceType() != ResourceType.GOLD)) {
			for (final CAbility ability : target.getAbilities()) {
				if (ability instanceof CAbilityGoldMinable) {
					final CAbilityGoldMinable abilityGoldMine = (CAbilityGoldMinable) ability;
					final int activeMiners = abilityGoldMine.getActiveMinerCount();
					if (activeMiners < abilityGoldMine.getMiningCapacity()) {
						abilityGoldMine.addMiner(this);
						this.unit.setHidden(true);
						this.unit.setInvulnerable(true);
						this.unit.setPaused(true);
						this.unit.setAcceptingOrders(false);
						this.popoutFromMineTurnTick = this.simulation.getGameTurnTick()
								+ (int) (abilityGoldMine.getMiningDuration() / WarsmashConstants.SIMULATION_STEP_TIME);
					}
					else {
						// we are stuck waiting to mine, let's make sure we play stand animation
						this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY,
								1.0f, true);
					}
					return this;
				}
			}
			// weird invalid target and we have no resources, consider harvesting done
			if (this.abilityHarvest.getCarriedResourceAmount() == 0) {
				return this.unit.pollNextOrderBehavior(this.simulation);
			}
			else {
				return this.abilityHarvest.getBehaviorReturnResources().reset(this.simulation);
			}
		}
		else {
			// we have some GOLD and we're not in a mine (?) lets do a return resources
			// order
			return this.abilityHarvest.getBehaviorReturnResources().reset(this.simulation);
		}
	}

	// 从矿中弹出，并处理金矿
	public void popoutFromMine(final int goldMined) {
		this.popoutFromMineTurnTick = 0;
		this.unit.setHidden(false);
		this.unit.setInvulnerable(false);
		this.unit.setPaused(false);
		this.unit.setAcceptingOrders(true);
		dropResources();
		this.abilityHarvest.setCarriedResources(ResourceType.GOLD, goldMined);
		this.unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.GOLD);
		this.simulation.unitRepositioned(this.unit);
	}

	@Override
	// 接受可破坏目标
	public CBehavior accept(final CDestructable target) {
		if ((this.abilityHarvest.getCarriedResourceType() != ResourceType.LUMBER)
				|| (this.abilityHarvest.getCarriedResourceAmount() < this.abilityHarvest.getLumberCapacity())) {
			return this.abilityHarvest.getBehaviorTreeAttack().reset(getHighlightOrderId(),
					this.abilityHarvest.getTreeAttack(), target, false, this);
		}
		else {
			// we have some LUMBER and we can't carry any more, time to return resources
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
	// 在首次更新后执行的行为
	public CBehavior onFirstUpdateAfterBackswing(final CBehaviorAttack currentAttackBehavior) {
		if (this.abilityHarvest.getCarriedResourceAmount() >= this.abilityHarvest.getLumberCapacity()) {
			return this.abilityHarvest.getBehaviorReturnResources().reset(this.simulation);
		}
		return currentAttackBehavior;
	}

	@Override
	// 行为完成时处理
	public CBehavior onFinish(final CSimulation game, final CUnit finishingUnit) {
		if (this.abilityHarvest.getCarriedResourceAmount() >= this.abilityHarvest.getLumberCapacity()) {
			return this.abilityHarvest.getBehaviorReturnResources().reset(this.simulation);
		}
		return updateOnInvalidTarget(game);
	}

	@Override
	// 接受物品目标
	public CBehavior accept(final CItem target) {
		return this.unit.pollNextOrderBehavior(this.simulation);
	}

	@Override
	// 检查目标是否仍然有效
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return this.target.visit(AbilityTargetStillAliveVisitor.INSTANCE);
	}

	@Override
	// 更新当目标无效时的行为
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		if (this.target instanceof CDestructable) {
			// wood
			final CDestructable nearestTree = CBehaviorReturnResources.findNearestTree(this.unit, this.abilityHarvest,
					simulation, this.unit);
			if (nearestTree != null) {
				return reset(nearestTree);
			}
		}
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
			case FOOD:
				throw new IllegalStateException("Unit used Harvest skill to carry FOOD resource!");
			case GOLD:
				this.unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.GOLD);
				break;
			case LUMBER:
				this.unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.LUMBER);
				break;
			}
		}
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
