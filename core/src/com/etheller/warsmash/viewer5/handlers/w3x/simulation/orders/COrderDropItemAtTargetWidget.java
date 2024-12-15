package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ExternStringMsgTargetCheckReceiver;
/**
 * 将物品给与目标英雄的指令
 */
public class COrderDropItemAtTargetWidget implements COrder {
	private final int abilityHandleId; // 能力句柄 ID
	private final int orderId; // 订单 ID
	private final int itemHandleId; // 物品句柄 ID
	private final int targetHeroHandleId; // 目标英雄句柄 ID
	private final boolean queued; // 是否排队

	public COrderDropItemAtTargetWidget(final int abilityHandleId, final int orderId, final int itemHandleId,
			final int targetHeroHandleId, final boolean queued) {
		this.abilityHandleId = abilityHandleId;
		this.orderId = orderId;
		this.itemHandleId = itemHandleId;
		this.targetHeroHandleId = targetHeroHandleId;
		this.queued = queued;
	}

	@Override
	/**
	 * 返回能力句柄 ID。
	 */
	public int getAbilityHandleId() {
		return this.abilityHandleId;
	}

	@Override
	/**
	 * 返回订单 ID。
	 */
	public int getOrderId() {
		return this.orderId;
	}

	@Override
	/**
	 * 根据目标英雄句柄 ID 获取目标单位。
	 */
	public CWidget getTarget(final CSimulation game) {
		final CWidget target = game.getWidget(this.targetHeroHandleId);
		return target;
	}

	@Override
	/**
	 * 返回是否排队。
	 */
	public boolean isQueued() {
		return this.queued;
	}

	@Override
	/**
	 * 开始执行丢弃物品的行为。
	 */
	public CBehavior begin(final CSimulation game, final CUnit caster) {
		// 获取 物品栏 能力
		final CAbilityInventory ability = (CAbilityInventory) game.getAbility(this.abilityHandleId);
		// 如果能力对象不存在，忽略当前指令，执行指令队列的下一个指令或者默认指令
		if (ability == null) {
			game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(), "NOTEXTERN: No such ability");
			return caster.pollNextOrderBehavior(game);
		}
		// 能力检测该单位是否能执行该指令
		ability.checkCanUse(game, caster, this.orderId, this.abilityActivationReceiver.reset());
		// 可以使用该能力
		if (this.abilityActivationReceiver.isUseOk()) {
			// 获取物品对象
			final CItem itemToDrop = (CItem) game.getWidget(this.itemHandleId);
			// 获取单位对象
			final CUnit targetHero = (CUnit) game.getWidget(this.targetHeroHandleId);
			final ExternStringMsgTargetCheckReceiver<CWidget> targetReceiver = (ExternStringMsgTargetCheckReceiver<CWidget>) targetCheckReceiver;

			// 能力检测目标是否可以使用
			ability.checkCanTarget(game, caster, this.orderId, targetHero, targetReceiver.reset());
			// 目标可以使用
			if (targetReceiver.getTarget() != null) {
				//开始给予物品给英雄的行为
				return ability.beginDropItem(game, caster, this.orderId, itemToDrop, targetHero);
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
	 * 计算对象的哈希码。
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.abilityHandleId;
		result = (prime * result) + this.itemHandleId;
		result = (prime * result) + this.orderId;
		result = (prime * result) + (this.queued ? 1231 : 1237);
		result = (prime * result) + this.targetHeroHandleId;
		return result;
	}

	@Override
	/**
	 * 判断两个对象是否相等。
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
		final COrderDropItemAtTargetWidget other = (COrderDropItemAtTargetWidget) obj;
		if (this.abilityHandleId != other.abilityHandleId) {
			return false;
		}
		if (this.itemHandleId != other.itemHandleId) {
			return false;
		}
		if (this.orderId != other.orderId) {
			return false;
		}
		if (this.queued != other.queued) {
			return false;
		}
		if (this.targetHeroHandleId != other.targetHeroHandleId) {
			return false;
		}
		return true;
	}

	@Override
	public void fireEvents(final CSimulation game, final CUnit unit) {
		// TODO Auto-generated method stub
	}

}

