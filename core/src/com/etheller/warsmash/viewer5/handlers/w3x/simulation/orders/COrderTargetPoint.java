package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ExternStringMsgTargetCheckReceiver;
/**
 * COrderTargetPoint 类实现了 COrder 接口，表示一个有目标点的命令订单。
 */
public class COrderTargetPoint implements COrder {
	private final int abilityHandleId; // 能力句柄ID
	private final int orderId; // 订单ID
	private final AbilityPointTarget target; // 目标点
	private final boolean queued; // 是否排队

	/**
	 * 构造函数，用于初始化 COrderTargetPoint 对象。
	 *
	 * @param abilityHandleId 能力句柄ID
	 * @param orderId 订单ID
	 * @param target 目标点
	 * @param queued 是否排队
	 */
	public COrderTargetPoint(final int abilityHandleId, final int orderId, final AbilityPointTarget target,
			final boolean queued) {
		this.abilityHandleId = abilityHandleId;
		this.orderId = orderId;
		this.target = target;
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
	 * 获取订单ID。
	 *
	 * @return 订单ID
	 */
	public int getOrderId() {
		return this.orderId;
	}

	@Override
	/**
	 * 获取目标点。
	 *
	 * @param game 模拟游戏
	 * @return 目标点
	 */
	public AbilityPointTarget getTarget(final CSimulation game) {
		return this.target;
	}

	@Override
	/**
	 * 检查命令是否被排队。
	 *
	 * @return 如果被排队则返回true，否则返回false
	 */
	public boolean isQueued() {
		return this.queued;
	}

	@Override
	/**
	 * 开始执行命令的具体行为。
	 *
	 * @param game 模拟游戏
	 * @param caster 施法单位
	 * @return 行为对象
	 */
	public CBehavior begin(final CSimulation game, final CUnit caster) {
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
			// 能力检测目标是否可以使用
			final ExternStringMsgTargetCheckReceiver<AbilityPointTarget> targetReceiver = (ExternStringMsgTargetCheckReceiver<AbilityPointTarget>) targetCheckReceiver;
			ability.checkCanTarget(game, caster, this.orderId, this.target, targetReceiver);
			// 目标可以使用
			if (targetReceiver.getTarget() != null) {
				// 单位执行命令事件
				caster.fireOrderEvents(game, this);
				// 施放技能
				return ability.begin(game, caster, this.orderId, this.target);
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

	@Override
	/**
	 * 生成对象的哈希码。
	 *
	 * @return 哈希码
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.abilityHandleId;
		result = (prime * result) + this.orderId;
		result = (prime * result) + (this.queued ? 1231 : 1237);
		result = (prime * result) + ((this.target == null) ? 0 : this.target.hashCode());
		return result;
	}

	@Override
	/**
	 * 比较当前对象与另一个对象是否相等。
	 *
	 * @param obj 需要比较的对象
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
		final COrderTargetPoint other = (COrderTargetPoint) obj;
		if (this.abilityHandleId != other.abilityHandleId) {
			return false;
		}
		if (this.orderId != other.orderId) {
			return false;
		}
		if (this.queued != other.queued) {
			return false;
		}
		if (this.target == null) {
			if (other.target != null) {
				return false;
			}
		}
		else if (!this.target.equals(other.target)) {
			return false;
		}
		return true;
	}

	@Override
	/**
	 * 触发事件。
	 *
	 * @param game 模拟游戏
	 * @param unit 单位
	 */
	public void fireEvents(final CSimulation game, final CUnit unit) {
		unit.fireOrderEvents(game, this);
	}

}
