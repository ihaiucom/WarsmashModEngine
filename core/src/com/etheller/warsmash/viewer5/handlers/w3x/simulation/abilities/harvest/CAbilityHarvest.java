package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMinable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackNormal;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;
/**
 * CAbilityHarvest 类代表一种可以收获资源的能力
 */
public class CAbilityHarvest extends AbstractGenericSingleIconActiveAbility {
	private int damageToTree; // 对树的伤害
	private int goldCapacity; // 黄金容量
	private int lumberCapacity; // 木材容量
	private float castRange; // 施法范围
	private float duration; // 持续时间
	private CBehaviorHarvest behaviorHarvest; // 收获行为
	private CBehaviorReturnResources behaviorReturnResources; // 返回资源行为
	private int carriedResourceAmount; // 携带的资源数量
	private ResourceType carriedResourceType; // 携带的资源类型
	private CUnitAttack treeAttack; // 对树的攻击
	private CWidget lastHarvestTarget; // 上一个收获目标
	private CBehaviorAttack behaviorTreeAttack; // 树攻击行为

	/**
	 * CAbilityHarvest 构造函数
	 *
	 * @param handleId      实例ID
	 * @param code          技能代码
	 * @param alias         技能别名
	 * @param damageToTree  对树造成的伤害
	 * @param goldCapacity  黄金存储容量
	 * @param lumberCapacity 木材存储容量
	 * @param castRange     施法范围
	 * @param duration      技能持续时间
	 */
	public CAbilityHarvest(final int handleId, final War3ID code, final War3ID alias, final int damageToTree, final int goldCapacity,
			final int lumberCapacity, final float castRange, final float duration) {
		super(handleId, code, alias);
		this.damageToTree = damageToTree;
		this.goldCapacity = goldCapacity;
		this.lumberCapacity = lumberCapacity;
		this.castRange = castRange;
		this.duration = duration;
	}

	@Override
	/**
	 * 当技能被添加到单位时调用
	 */
	public void onAdd(final CSimulation game, final CUnit unit) {

		this.behaviorTreeAttack = new CBehaviorAttack(unit);
		this.behaviorHarvest = new CBehaviorHarvest(unit, this);
		this.behaviorReturnResources = new CBehaviorReturnResources(unit, this);

		final List<CUnitAttack> unitAttacks = unit.getUnitSpecificAttacks();
		CUnitAttack bestFitTreeAttack = null;
		for (final CUnitAttack attack : unitAttacks) {
			if (attack.getTargetsAllowed().contains(CTargetType.TREE)) {
				bestFitTreeAttack = attack;
			}
		}
		this.treeAttack = new CUnitAttackNormal(
				bestFitTreeAttack == null ? 0.433f : bestFitTreeAttack.getAnimationBackswingPoint(),
				bestFitTreeAttack == null ? 0.433f : bestFitTreeAttack.getAnimationDamagePoint(), CAttackType.NORMAL,
				this.duration, 0, 1, this.damageToTree * 2, 0, (int) this.castRange,
				bestFitTreeAttack == null ? 250 : bestFitTreeAttack.getRangeMotionBuffer(),
				bestFitTreeAttack == null ? false : bestFitTreeAttack.isShowUI(),
				bestFitTreeAttack == null ? EnumSet.of(CTargetType.TREE) : bestFitTreeAttack.getTargetsAllowed(),
				bestFitTreeAttack == null ? "AxeMediumChop" : bestFitTreeAttack.getWeaponSound(),
				bestFitTreeAttack == null ? CWeaponType.NORMAL : bestFitTreeAttack.getWeaponType());
	}

	@Override
	/**
	 * 当技能从单位中移除时调用
	 */
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	@Override
	/**
	 * 每个游戏循环调用一次
	 */
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	/**
	 * 开始一个有目标的行为
	 */
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return this.behaviorHarvest.reset(target);
	}

	@Override
	/**
	 * 开始一个有点目标的行为
	 */
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	/**
	 * 开始一个无目标的行为
	 */
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		if (isToggleOn() && (orderId == OrderIds.returnresources)) {
			return this.behaviorReturnResources.reset(game);
		}
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	/**
	 * 获取基础命令ID
	 */
	public int getBaseOrderId() {
		return isToggleOn() ? OrderIds.returnresources : OrderIds.harvest;
	}

	@Override
	/**
	 * 检查技能是否开关状态为打开
	 */
	public boolean isToggleOn() {
		return this.carriedResourceAmount > 0;
	}

	@Override
	/**
	 * 内部检查是否可以使用技能
	 */
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	@Override
	/**
	 * 内部检查目标是否合法
	 */
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (target instanceof CUnit) {
			if(this.goldCapacity <= 0){
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_TREE);
				return;
			}
			final CUnit targetUnit = (CUnit) target;
			for (final CAbility ability : targetUnit.getAbilities()) {
				if (ability instanceof CAbilityGoldMinable) {
					receiver.targetOk(target);
					return;
				}
				else if ((this.carriedResourceType != null) && (ability instanceof CAbilityReturnResources)) {
					final CAbilityReturnResources abilityReturn = (CAbilityReturnResources) ability;
					if (abilityReturn.accepts(this.carriedResourceType)) {
						receiver.targetOk(target);
						return;
					}
				}
			}
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_RESOURCES);
		}
		else if (target instanceof CDestructable) {
			if(this.lumberCapacity <= 0){
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_GOLD_MINE);
				return;
			}
			if (target.canBeTargetedBy(game, unit, this.treeAttack.getTargetsAllowed(), receiver)) {
				receiver.targetOk(target);
			}
			// else receiver called by "canBeTargetedBy"
		}
		else {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_RESOURCES);
		}
	}

	@Override
	/**
	 * 内部检查是否可以进行智能目标
	 */
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		innerCheckCanTarget(game, unit, orderId, target, receiver);
	}

	@Override
	/**
	 * 内部检查是否可以进行目标选择
	 */
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	/**
	 * 内部检查无目标情况下的目标选择
	 */
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	/**
	 * 内部检查无目标状态
	 */
	protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		if ((orderId == OrderIds.returnresources) && isToggleOn()) {
			receiver.targetOk(null);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	/**
	 * 获取对树的伤害
	 */
	public int getDamageToTree() {
		return this.damageToTree;
	}

	/**
	 * 获取黄金容量
	 */
	public int getGoldCapacity() {
		return this.goldCapacity;
	}

	/**
	 * 获取木材容量
	 */
	public int getLumberCapacity() {
		return this.lumberCapacity;
	}

	/**
	 * 获取携带的资源数量
	 */
	public int getCarriedResourceAmount() {
		return this.carriedResourceAmount;
	}

	/**
	 * 获取携带的资源类型
	 */
	public ResourceType getCarriedResourceType() {
		return this.carriedResourceType;
	}

	/**
	 * 设置携带的资源
	 */
	public void setCarriedResources(final ResourceType carriedResourceType, final int carriedResourceAmount) {
		this.carriedResourceType = carriedResourceType;
		this.carriedResourceAmount = carriedResourceAmount;
	}

	/**
	 * 获取收获行为
	 */
	public CBehaviorHarvest getBehaviorHarvest() {
		return this.behaviorHarvest;
	}

	/**
	 * 获取返回资源行为
	 */
	public CBehaviorReturnResources getBehaviorReturnResources() {
		return this.behaviorReturnResources;
	}

	/**
	 * 获取树攻击
	 */
	public CUnitAttack getTreeAttack() {
		return this.treeAttack;
	}

	/**
	 * 设置最后的收获目标
	 */
	public void setLastHarvestTarget(final CWidget lastHarvestTarget) {
		this.lastHarvestTarget = lastHarvestTarget;
	}

	/**
	 * 获取最后的收获目标
	 */
	public CWidget getLastHarvestTarget() {
		return this.lastHarvestTarget;
	}

	@Override
	/**
	 * 从队列中取消命令时调用
	 */
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	/**
	 * 获取树攻击行为
	 */
	public CBehaviorAttack getBehaviorTreeAttack() {
		return this.behaviorTreeAttack;
	}

	/**
	 * 设置对树的伤害
	 */
	public void setDamageToTree(final int damageToTree) {
		this.damageToTree = damageToTree;
	}

	/**
	 * 设置黄金容量
	 */
	public void setGoldCapacity(final int goldCapacity) {
		this.goldCapacity = goldCapacity;
	}

	/**
	 * 设置木材容量
	 */
	public void setLumberCapacity(final int lumberCapacity) {
		this.lumberCapacity = lumberCapacity;
	}

	/**
	 * 设置施法范围
	 */
	public void setCastRange(final float castRange) {
		this.castRange = castRange;
	}

	/**
	 * 设置持续时间
	 */
	public void setDuration(final float duration) {
		this.duration = duration;
	}

	@Override
	/**
	 * 检查技能是否为物理攻击
	 */
	public boolean isPhysical() {
		return true;
	}

	@Override
	/**
	 * 检查技能是否为通用技能
	 */
	public boolean isUniversal() {
		return false;
	}

	@Override
	/**
	 * 获取技能类别
	 */
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.CORE;
	}
}
