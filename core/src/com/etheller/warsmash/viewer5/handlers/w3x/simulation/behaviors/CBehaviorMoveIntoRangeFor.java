package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityRanged;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderTargetWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
// 移动到目标范围，装载该目标单位
public class CBehaviorMoveIntoRangeFor extends CAbstractRangedBehavior {

	private int higlightOrderId; // 高亮命令ID
	private PairAbilityLocator pairAbilityLocator; // 配对技能定位器

	// 构造函数，初始化单位
	public CBehaviorMoveIntoRangeFor(final CUnit unit) {
		super(unit);
	}

	// 重置行为，设置高亮命令ID和目标
	public CBehavior reset(final int higlightOrderId, final CWidget target,
			final PairAbilityLocator pairAbilityLocator) {
		this.higlightOrderId = higlightOrderId;
		this.pairAbilityLocator = pairAbilityLocator;
		return innerReset(target);
	}

	// 获取高亮命令ID
	@Override
	public int getHighlightOrderId() {
		return this.higlightOrderId;
	}

	// 检查单位的装载能力是否能够到达目标
	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		// 获取目标单位
		final CUnit targetUnit = this.target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			// 获取装载能力
			final CAbilityRanged partnerAbility = this.pairAbilityLocator.getPartnerAbility(simulation, this.unit,
					targetUnit, false, true);
			// 如果有能力
			if (partnerAbility != null) {
				// 能够到达目标单位
				return this.unit.canReach(this.target, partnerAbility.getCastRange());
			}
		}
		return false;
	}

	// 已经到达目标范围，更新单位行为
	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		// 获取目标单位
		final CUnit targetUnit = this.target.visit(AbilityTargetVisitor.UNIT);
		// 目标单位存在
		if (targetUnit != null) {
			// 获取装载能力
			final CAbilityRanged partnerAbility = this.pairAbilityLocator.getPartnerAbility(simulation, this.unit,
					targetUnit, false, false);
			// 如果有装载能力
			if (partnerAbility != null) {
				// 获取目标单位当前指令
				final COrder currentOrder = targetUnit.getCurrentOrder();
				// 如果当前指令是右键
				final boolean queue = (currentOrder != null) && (currentOrder.getOrderId() == OrderIds.smart);
				// 如果目标当前指令不是右键的 自己当前单位
				if (!((currentOrder instanceof COrderTargetWidget)
						&& (currentOrder.getTarget(simulation) == this.unit))) {
					// 就执行右键命令 右键自己单位
					targetUnit.order(simulation, new COrderTargetWidget(partnerAbility.getHandleId(), OrderIds.smart,
							this.unit.getHandleId(), queue), queue);
					return this.unit.pollNextOrderBehavior(simulation);
				} // else we might be looping the queueing and that's wasteful
			}
		}
		// 播放待机动作
		this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f, false);
		return this;
	}

	// 在目标无效时更新行为
	@Override
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		return this.unit.pollNextOrderBehavior(simulation);
	}

	// 检查目标是否仍然有效
	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return this.target.visit(AbilityTargetStillAliveVisitor.INSTANCE);
	}

	// 开始移动前的重置
	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {

	}

	// 开始行为
	@Override
	public void begin(final CSimulation game) {

	}

	// 结束行为
	@Override
	public void end(final CSimulation game, final boolean interrupted) {

	}

	// 结束移动
	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {

	}

	// 检查行为是否可打断
	@Override
	public boolean interruptable() {
		return true;
	}

	// 配对技能定位器接口
	public static interface PairAbilityLocator {
		CAbilityRanged getPartnerAbility(final CSimulation game, final CUnit caster, final CUnit transport,
				final boolean ignoreRange, final boolean ignoreDisabled);
	}
}
