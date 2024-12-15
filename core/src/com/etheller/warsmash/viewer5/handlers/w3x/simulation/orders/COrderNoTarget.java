package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ExternStringMsgTargetCheckReceiver;
/**
 * COrderNoTarget类实现了COrder接口，用于处理没有目标的命令。
 */
public class COrderNoTarget implements COrder {
	private final int abilityHandleId; // 能力句柄ID
	private final int orderId; // 命令ID
	private final boolean queued; // 是否排队

	/**
	 * 构造函数，初始化能力句柄ID、命令ID和排队状态。
	 *
	 * @param abilityHandleId 能力句柄ID
	 * @param orderId 命令ID
	 * @param queued 是否排队
	 */
	public COrderNoTarget(final int abilityHandleId, final int orderId, final boolean queued) {
		this.abilityHandleId = abilityHandleId;
		this.orderId = orderId;
		this.queued = queued;
	}

	@Override
	/**
	 * 获取能力句柄ID。
	 *
	 * @return 能力句柄ID
	 */
	public int getAbilityHandleId() {
		return this.abilityHandleId;
	}

	@Override
	/**
	 * 获取命令ID。
	 *
	 * @return 命令ID
	 */
	public int getOrderId() {
		return this.orderId;
	}

	@Override
	/**
	 * 检查命令是否排队。
	 *
	 * @return 如果命令排队则返回true，否则返回false
	 */
	public boolean isQueued() {
		return this.queued;
	}

	@Override
	/**
	 * 开始执行命令，通过能力和施法者进行处理。
	 *
	 * @param game 游戏模拟对象
	 * @param caster 施法者单位
	 * @return 行为对象
	 */
	public CBehavior begin(final CSimulation game, final CUnit caster) {
		// 如果能力句柄ID为0，则返回停止行为
		if (this.abilityHandleId == 0) {
			return caster.getStopBehavior();
		}
		// 获取能力对象
		final CAbility ability = game.getAbility(this.abilityHandleId);
		// 如果能力对象不存在，忽略当前指令，执行指令队列的下一个指令或者默认指令
		if (ability == null) {
			game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(), "NOTEXTERN: No such ability");
			return caster.pollNextOrderBehavior(game);
		}
		// 能力检测该单位是否能执行该指令
		ability.checkCanUse(game, caster, this.orderId, this.abilityActivationReceiver.reset());
		// 可以使用该能力
		if (this.abilityActivationReceiver.isUseOk()) {
			// 能力检测无目标是否可以使用
			final ExternStringMsgTargetCheckReceiver<Void> targetReceiver = (ExternStringMsgTargetCheckReceiver<Void>) targetCheckReceiver;
			ability.checkCanTargetNoTarget(game, caster, this.orderId, targetReceiver);
			// 没有报错，可以执行
			if (targetReceiver.getExternStringKey() == null) {
				// 单位执行命令事件
				caster.fireOrderEvents(game, this);
				// 施放技能
				return ability.beginNoTarget(game, caster, this.orderId);
			}
			// 有报错，显示报错信息，并执行指令队列的下一个指令或者默认指令
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

	@Override
	/**
	 * 获取命令的目标，因该命令没有目标故返回null。
	 *
	 * @param game 游戏模拟对象
	 * @return null
	 */
	public AbilityTarget getTarget(final CSimulation game) {
		return null;
	}

	@Override
	/**
	 * 计算对象的哈希码。
	 *
	 * @return 哈希码
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.abilityHandleId;
		result = (prime * result) + this.orderId;
		result = (prime * result) + (this.queued ? 1231 : 1237);
		return result;
	}

	@Override
	/**
	 * 比较两个对象是否相等。
	 *
	 * @param obj 待比较的对象
	 * @return 如果相等则返回true，否则返回false
	 */
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
		final COrderNoTarget other = (COrderNoTarget) obj;
		if (this.abilityHandleId != other.abilityHandleId) {
			return false;
		}
		if (this.orderId != other.orderId) {
			return false;
		}
		if (this.queued != other.queued) {
			return false;
		}
		return true;
	}

	@Override
	/**
	 * 触发单位的事件。
	 *
	 * @param game 游戏模拟对象
	 * @param unit 单位
	 */
	public void fireEvents(final CSimulation game, final CUnit unit) {
		unit.fireOrderEvents(game, this);
	}

}
