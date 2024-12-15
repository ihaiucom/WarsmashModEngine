package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ExternStringMsgTargetCheckReceiver;
// COrderTargetWidget类实现了COrder接口
public class COrderTargetWidget implements COrder {
	private final int abilityHandleId;  // 能力句柄ID
	private final int orderId;           // 订单ID
	private final int targetHandleId;    // 目标句柄ID
	private final boolean queued;         // 是否排队

	// 构造函数，初始化能力句柄ID、订单ID、目标句柄ID和排队状态
	public COrderTargetWidget(final int abilityHandleId, final int orderId, final int targetHandleId,
			final boolean queued) {
		this.abilityHandleId = abilityHandleId;
		this.orderId = orderId;
		this.targetHandleId = targetHandleId;
		this.queued = queued;
	}

	// 获取能力句柄ID
	@Override
	public int getAbilityHandleId() {
		return this.abilityHandleId;
	}

	// 获取订单ID
	@Override
	public int getOrderId() {
		return this.orderId;
	}

	// 获取目标对象
	@Override
	public CWidget getTarget(final CSimulation game) {
		return game.getWidget(this.targetHandleId);
	}

	// 判断是否排队
	@Override
	public boolean isQueued() {
		return this.queued;
	}

	// 开始执行行为的逻辑
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster) {
		// 获取能力对象
		final CAbility ability = game.getAbility(this.abilityHandleId);
		// 如果能力对象不存在，忽略当前指令，执行指令队列的下一个指令或者默认指令
		if (ability == null) {
			game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(), "NOTEXTERN: No such ability");
			return caster.pollNextOrderBehavior(game);
		}
		// 能力检测该单位是否能执行该指令
		ability.checkCanUse(game, caster, this.orderId, abilityActivationReceiver.reset());
		// 可以使用该能力
		if (abilityActivationReceiver.isUseOk()) {
			// 能力检测目标是否可以使用
			final CWidget target = game.getWidget(this.targetHandleId);
			final ExternStringMsgTargetCheckReceiver<CWidget> targetReceiver = (ExternStringMsgTargetCheckReceiver<CWidget>) targetCheckReceiver;
			ability.checkCanTarget(game, caster, this.orderId, target, targetReceiver.reset());
			// 目标可以使用
			if (targetReceiver.getTarget() != null) {
				// 单位执行命令事件
				caster.fireOrderEvents(game, this);
				// 施放技能
				return ability.begin(game, caster, this.orderId, targetReceiver.getTarget());
			}
			// 目标不可用，显示报错信息，并执行指令队列的下一个指令或者默认指令
			else {
				game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(), targetReceiver.getExternStringKey());
				return caster.pollNextOrderBehavior(game);
			}
		}
		// 不能使用该能力，显示报错信息，并执行指令队列的下一个指令或者默认指令
		else {
			game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(),
					this.abilityActivationReceiver.getExternStringKey());
			return caster.pollNextOrderBehavior(game);
		}
	}

	// 计算哈希码
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.abilityHandleId;
		result = (prime * result) + this.orderId;
		result = (prime * result) + (this.queued ? 1231 : 1237);
		result = (prime * result) + this.targetHandleId;
		return result;
	}

	// 比较两个COrderTargetWidget对象是否相等
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final COrderTargetWidget other = (COrderTargetWidget) obj;
		if (this.abilityHandleId != other.abilityHandleId) {
			return false;
		}
		if (this.orderId != other.orderId) {
			return false;
		}
		if (this.queued != other.queued) {
			return false;
		}
		if (this.targetHandleId != other.targetHandleId) {
			return false;
		}
		return true;
	}

	// 触发事件
	@Override
	public void fireEvents(final CSimulation game, final CUnit unit) {
		unit.fireOrderEvents(game, this);
	}
}
