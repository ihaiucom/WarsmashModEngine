package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills.CBehaviorTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
/**
 * 抽象技能基类--有目标（单位/可破坏物/物品）
 */
public abstract class CAbilityTargetSpellBase extends CAbilitySpellBase {
	private CBehaviorTargetSpellBase behavior;

	/**
	 * 构造函数，用于初始化法术能力
	 * @param handleId 处理程序ID
	 * @param alias 别名
	 */
	public CAbilityTargetSpellBase(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	/**
	 * 当单位添加到模拟时，初始化行为
	 * @param game 游戏实例
	 * @param unit 施法单位
	 */
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behavior = new CBehaviorTargetSpellBase(unit, this);
	}

	@Override
	/**
	 * 开始施法，目标为CWidget类型
	 * @param game 游戏实例
	 * @param caster 施法单位
	 * @param orderId 订单ID
	 * @param target 目标小部件
	 * @return CBehavior 实例
	 */
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return this.behavior.reset(target);
	}

	@Override
	/**
	 * 开始施法，目标为AbilityPointTarget类型
	 * @param game 游戏实例
	 * @param caster 施法单位
	 * @param orderId 订单ID
	 * @param point 指定目标点
	 * @return null
	 */
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	@Override
	/**
	 * 开始施法，无目标情况
	 * @param game 游戏实例
	 * @param caster 施法单位
	 * @param orderId 订单ID
	 * @return null
	 */
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	@Override
	/**
	 * 检查目标是否可以被施法单位选中
	 * @param game 游戏实例
	 * @param unit 施法单位
	 * @param orderId 订单ID
	 * @param target 目标小部件
	 * @param receiver 目标检查接收器
	 */
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		// 检查目标是否可以被选为目标
		if (target.canBeTargetedBy(game, unit, getTargetsAllowed(), receiver)) {
			// 如果单位可以移动或者能够到达目标范围，则进行内部检查
			if (!unit.isMovementDisabled() || unit.canReach(target, getCastRange())) {
				// 内部检查是否可以对目标施放法术
				this.innerCheckCanTargetSpell(game, unit, orderId, target, receiver);
			} else {
				// 如果单位不能移动且无法到达目标范围，则目标检查失败
				receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
			}
		}

	}

	/**
	 * 内部检查法术目标是否合法
	 * @param game 游戏实例
	 * @param unit 施法单位
	 * @param orderId 订单ID
	 * @param target 目标小部件
	 * @param receiver 目标检查接收器
	 */
	protected void innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.targetOk(target);
	}

	@Override
	/**
	 * 检查目标是否可以为能力点类型
	 * @param game 游戏实例
	 * @param unit 施法单位
	 * @param orderId 订单ID
	 * @param target 指定目标点
	 * @param receiver 目标检查接收器
	 */
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	/**
	 * 检查无目标情况下的能力调用
	 * @param game 游戏实例
	 * @param unit 施法单位
	 * @param orderId 订单ID
	 * @param receiver 目标检查接收器
	 */
	protected void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

}
