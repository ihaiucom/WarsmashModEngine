package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;

// 资源返回行为类
public class CBehaviorReturnResources extends CAbstractRangedBehavior implements AbilityTargetVisitor<CBehavior> {
	private final CAbilityHarvest abilityHarvest;  // 采集能力
	private CSimulation simulation;  // 模拟对象

	public CBehaviorReturnResources(final CUnit unit, final CAbilityHarvest abilityHarvest) {
		super(unit);
		this.abilityHarvest = abilityHarvest;  // 初始化采集能力
	}

	// 重置行为
	public CBehavior reset(final CSimulation simulation) {
		// 查找最近的资源返回点
		final CUnit nearestDropoffPoint = findNearestDropoffPoint(simulation);
		if (nearestDropoffPoint == null) {
			// TODO it is unconventional not to return self here
			// 如果没有，就执行下一个行为
			return this.unit.pollNextOrderBehavior(simulation);
		}
		innerReset(nearestDropoffPoint, true);
		return this;
	}

	@Override
	// 判断目标是否在范围内
	public boolean isWithinRange(final CSimulation simulation) {
		// TODO this is probably not what the CloseEnoughRange constant is for
		return this.unit.canReach(this.target, this.unit.getUnitType().getCollisionSize());
	}

	@Override
	// 获取高亮命令ID
	public int getHighlightOrderId() {
		return OrderIds.returnresources;
	}

	@Override
	// 更新行为
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		this.simulation = simulation;
		return this.target.visit(this);
	}

	@Override
	// 处理点目标
	public CBehavior accept(final AbilityPointTarget target) {
		// 如果是坐标点，就执行执行下一个命令行为
		return CBehaviorReturnResources.this.unit.pollNextOrderBehavior(this.simulation);
	}

	@Override
	// 处理单位目标 资源返回点（金矿/木材厂）
	public CBehavior accept(final CUnit target) {
		// 遍历目标单位的能力，查找有资源返回能力的单位
		for (final CAbility ability : target.getAbilities()) {
			if (ability instanceof CAbilityReturnResources) {
				final CAbilityReturnResources abilityReturnResources = (CAbilityReturnResources) ability;
				// 资源返回点能接收采集的资源类型
				if (abilityReturnResources.accepts(this.abilityHarvest.getCarriedResourceType())) {
					// 获取玩家对象
					final CPlayer player = this.simulation.getPlayer(this.unit.getPlayerIndex());
					CWidget nextTarget = null;
					switch (this.abilityHarvest.getCarriedResourceType()) {
					case FOOD: // 食物
						throw new IllegalStateException("Unit used Harvest skill to carry FOOD resource!");
					case GOLD: // 金币
						// 新增玩家金币
						player.setGold(player.getGold() + this.abilityHarvest.getCarriedResourceAmount());
						// 移除矿工动画次标签 金矿
						this.unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.GOLD);
						// 检测最后采集的对象是否还有效，如果有效下一个目标还是他
						if ((this.abilityHarvest.getLastHarvestTarget() != null) && this.abilityHarvest
								.getLastHarvestTarget().visit(AbilityTargetStillAliveVisitor.INSTANCE)) {
							nextTarget = this.abilityHarvest.getLastHarvestTarget();
						}
						else {
							// 否则查找新的 附近金矿
							nextTarget = findNearestMine(this.unit, this.simulation);
						}
						break;
					case LUMBER: // 木材
						// 新增玩家木材
						player.setLumber(player.getLumber() + this.abilityHarvest.getCarriedResourceAmount());
						// 移除矿工动画次标签 木材
						this.unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.LUMBER);
						// 检测最后采集的对象是否在
						if (this.abilityHarvest.getLastHarvestTarget() != null) {
							// 检测最后采集的树木是否还有效，如果有效下一个目标还是他
							if (this.abilityHarvest.getLastHarvestTarget()
									.visit(AbilityTargetStillAliveVisitor.INSTANCE)) {
								nextTarget = this.abilityHarvest.getLastHarvestTarget();
							}
							else {
								// 否则查找新的 附近树木
								nextTarget = findNearestTree(this.unit, this.abilityHarvest, this.simulation,
										this.abilityHarvest.getLastHarvestTarget());
							}
						}
						else {
							// 否则查找新的 附近树木
							nextTarget = findNearestTree(this.unit, this.abilityHarvest, this.simulation, this.unit);
						}
						break;
					}
					// 播放获得资源飘字
					this.simulation.unitGainResourceEvent(this.unit, player.getId(),
							this.abilityHarvest.getCarriedResourceType(),
							this.abilityHarvest.getCarriedResourceAmount());
					// 重置携带的资源为0
					this.abilityHarvest.setCarriedResources(this.abilityHarvest.getCarriedResourceType(), 0);
					// 如果目标存在，就继续采集行为
					if (nextTarget != null) {
						return this.abilityHarvest.getBehaviorHarvest().reset(nextTarget);
					}
					// 如果没有，就执行下一个行为
					return this.unit.pollNextOrderBehavior(this.simulation);
				}
			}
		}
		return this;
	}

	@Override
	// 处理破坏物目标
	public CBehavior accept(final CDestructable target) {
		// TODO cut trees!
		// 如果是破坏物目标，就执行执行下一个命令行为
		return this.unit.pollNextOrderBehavior(this.simulation);
	}

	@Override
	// 处理物品目标
	public CBehavior accept(final CItem target) {
		// 如果是物品目标，就执行执行下一个命令行为
		return this.unit.pollNextOrderBehavior(this.simulation);
	}

	@Override
	// 检查目标是否仍然有效
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return this.target.visit(AbilityTargetStillAliveVisitor.INSTANCE);
	}

	@Override
	// 在目标无效时更新行为
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		// 查找最近的资源返回点
		final CUnit nearestDropoff = findNearestDropoffPoint(simulation);
		if (nearestDropoff != null) {
			this.target = nearestDropoff;
			return this;
		}
		// 如果没有，就执行下一个行为
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	// 移动前重置
	protected void resetBeforeMoving(final CSimulation simulation) {

	}

	// 查找最近的资源返回点
	public CUnit findNearestDropoffPoint(final CSimulation simulation) {
		CUnit nearestDropoffPoint = null;
		double nearestDropoffDistance = Float.MAX_VALUE;
		for (final CUnit unit : simulation.getUnits()) {
			// 目标单位所属玩家相同
			if (unit.getPlayerIndex() == this.unit.getPlayerIndex()) {
				// 目标还活着
				if (unit.visit(AbilityTargetStillAliveVisitor.INSTANCE)) {
					boolean acceptedUnit = false;
					// 遍历目标单位的能力，查找有资源返回能力的单位
					for (final CAbility ability : unit.getAbilities()) {
						if (ability instanceof CAbilityReturnResources) {
							// 资源接收能力
							final CAbilityReturnResources abilityReturnResources = (CAbilityReturnResources) ability;
							// 资源接收能力能接收资源类型
							if (abilityReturnResources.accepts(this.abilityHarvest.getCarriedResourceType())) {
								acceptedUnit = true;
								break;
							}
						}
					}
					if (acceptedUnit) {
						// TODO maybe use distance squared, problem is that we're using this
						// inefficient more complex distance function on unit
						// 比较最近距离的返回点
						final double distance = unit.distanceSquaredNoCollision(this.unit);
						if (distance < nearestDropoffDistance) {
							nearestDropoffDistance = distance;
							nearestDropoffPoint = unit;
						}
					}
				}
			}
		}
		return nearestDropoffPoint;
	}

	// 查找最近的矿点
	public static CUnit findNearestMine(final CUnit worker, final CSimulation simulation) {
		CUnit nearestMine = null;
		double nearestMineDistance = Float.MAX_VALUE;
		for (final CUnit unit : simulation.getUnits()) {
			boolean acceptedUnit = false;
			// 查找有金矿能力的单位
			for (final CAbility ability : unit.getAbilities()) {
				if (ability instanceof CAbilityGoldMine) {
					acceptedUnit = true;
					break;
				}
			}
			if (acceptedUnit) {
				// TODO maybe use distance squared, problem is that we're using this
				// inefficient more complex distance function on unit
				final double distance = unit.distanceSquaredNoCollision(worker);
				if (distance < nearestMineDistance) {
					nearestMineDistance = distance;
					nearestMine = unit;
				}
			}
		}
		return nearestMine;
	}

	// 查找最近的树
	public static CDestructable findNearestTree(final CUnit worker, final CAbilityHarvest abilityHarvest,
			final CSimulation simulation, final CWidget toObject) {
		CDestructable nearestMine = null;
		double nearestMineDistance = Float.MAX_VALUE;
		for (final CDestructable unit : simulation.getDestructables()) {
			if (!unit.isDead()
					&& unit.canBeTargetedBy(simulation, worker, abilityHarvest.getTreeAttack().getTargetsAllowed())) {
				// TODO maybe use distance squared, problem is that we're using this
				// inefficient more complex distance function on unit
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
	// 行为开始时的处理
	public void begin(final CSimulation game) {

	}

	@Override
	// 行为结束时的处理
	public void end(final CSimulation game, final boolean interrupted) {

	}

	@Override
	// 结束移动时的处理
	public void endMove(final CSimulation game, final boolean interrupted) {

	}

	@Override
	// 判断行为是否可被打断
	public boolean interruptable() {
		return true;
	}

}
