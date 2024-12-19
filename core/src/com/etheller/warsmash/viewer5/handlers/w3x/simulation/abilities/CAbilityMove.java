package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityLoad;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.*;
// 移动能力，在创建单位时根据移动速度大于0时添加给单位的能力
public class CAbilityMove extends AbstractCAbility {

	// 构造函数，初始化能力移动
	public CAbilityMove(final int handleId) {
		super(handleId, War3ID.fromString("AMov"));
	}

	// 检查单位是否可以使用该能力
	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	// 检查目标是否合法：目标必须是单位
	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		switch (orderId) {
		case OrderIds.smart: // 右键 指令
		case OrderIds.patrol: // 巡逻 指令
		case OrderIds.move: // 移动 指令
			// 如果目标是单位，且不是自己
			if ((target instanceof CUnit) && (target != unit)) {
				// 目标合法
				receiver.targetOk(target);
			}
			else {
				// 目标不合法：必须是单位
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
			}
			break;
		default:
			// 其他指令不支持目标
			receiver.orderIdNotAccepted();
			break;
		}
	}

	// 检查点目标是否合法：坐标点
	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		switch (orderId) {
		case OrderIds.smart:// 右键 指令
		case OrderIds.move:// 移动 指令
		case OrderIds.patrol: // 巡逻 指令
			// 坐标点合法
			receiver.targetOk(target);
			break;
		default:
			// 其他指令不支持点目标
			receiver.orderIdNotAccepted();
			break;
		}
	}

	// 检查没有目标的情况
	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		switch (orderId) {
		case OrderIds.holdposition: // 静止 指令
			// 无目标 合法
			receiver.targetOk(null);
			break;
		default:
			// 其他指令不支持无目标
			receiver.orderIdNotAccepted();
			break;
		}
	}

	// 能力被添加到单位时的操作
	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		// 添加移动行为
		unit.setMoveBehavior(new CBehaviorMove(unit));
		// 添加跟随行为
		unit.setFollowBehavior(new CBehaviorFollow(unit));
		// 添加巡逻行为
		unit.setPatrolBehavior(new CBehaviorPatrol(unit));
		// 添加静止行为
		unit.setHoldPositionBehavior(new CBehaviorHoldPosition(unit));
		// 添加载具运输行为
		unit.setBoardTransportBehavior(new CBehaviorBoardTransport(unit));
	}

	// 能力被移除时的操作
	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	// 每次游戏循环的处理
	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	// 检查执行指令前的条件
	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		return true;
	}

	// 开始执行移动行为（单位目标）
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		// 是否是 右键 指令
		boolean smart = orderId == OrderIds.smart;
		// 获取目标单位
		CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		// 如果目标单位不为空
		if (targetUnit != null) {
			CBehavior behavior = null;
			// 如果是 右键 指令
			if (smart) {
				// 如果目标单位是载具，获取运载能力
				CAbilityLoad transportLoad = CAbilityLoad.getTransportLoad(game, caster, targetUnit, true, true);
				if (transportLoad != null) {
					// 执行运作行为
					behavior = caster.getBoardTransportBehavior().reset(OrderIds.move, targetUnit);
				}
			}
			// 如果没有执行的运载能力
			if (behavior == null) {
				// 执行跟随行为
				behavior = caster.getFollowBehavior()
						.reset(smart ? OrderIds.move : orderId, targetUnit);
				caster.setDefaultBehavior(behavior);
			}
			return behavior;
		}
		//注意：不应该发生，目标总是这个能力的单位
		// NOTE: shouldn't happen, target is always unit for this ability
		return caster.pollNextOrderBehavior(game);
	}

	// 开始执行移动行为（点目标）
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		if (orderId == OrderIds.patrol) { // 巡逻 指令
			// 执行巡逻行为
			final CBehavior patrolBehavior = caster.getPatrolBehavior().reset(point);
			caster.setDefaultBehavior(patrolBehavior);
			return patrolBehavior;
		}
		else {
			// 执行移动行为
			return caster.getMoveBehavior().reset(OrderIds.move, point);
		}
	}

	// 开始执行没有目标的行为
	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		if (orderId == OrderIds.holdposition) { // 静止 指令
			// 执行静止行为
			caster.setDefaultBehavior(caster.getHoldPositionBehavior());
		}
		// 其他指令不支持无目标，执行下一个指令行为
		return caster.pollNextOrderBehavior(game);
	}

	// 访问能力的访问者模式
	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	// 从队列取消指令时的处理
	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	// 单位死亡时的处理
	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

	// 判断能力是否为永久性
	@Override
	public boolean isPermanent() {
		return true;
	}

	// 判断能力是否为物理性
	@Override
	public boolean isPhysical() {
		return false;
	}

	// 判断能力是否为通用性
	@Override
	public boolean isUniversal() {
		return false;
	}

	// 获取能力分类
	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.MOVEMENT;
	}

}
