package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.SingleOrderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop.CAbilityShopPurhaseItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.inventory.CBehaviorDropItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.inventory.CBehaviorGetItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.inventory.CBehaviorGiveItemToHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderNoTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

// 物品栏位数为
// 定义能力库存类，继承自抽象类AbstractGenericNoIconAbility
public class CAbilityInventory extends AbstractGenericNoIconAbility {
	private final boolean canDropItems; // 是否可以丢弃物品
	private final boolean canGetItems; // 是否可以获取物品
	private final boolean canUseItems; // 是否可以使用物品
	private final boolean dropItemsOnDeath; // 死亡时是否丢弃物品
	private final CItem[] itemsHeld; // 持有的物品数组
	private final List<CAbility>[] itemsHeldAbilities; // 持有的物品对应的能力列表
	private CBehaviorGetItem behaviorGetItem; // 获取物品的行为
	private CBehaviorDropItem behaviorDropItem; // 丢弃物品的行为
	private CBehaviorGiveItemToHero behaviorGiveItem; // 给予英雄物品的行为

	// 构造函数，初始化能力库存
	public CAbilityInventory(final int handleId, final War3ID code, final War3ID alias, final boolean canDropItems,
			final boolean canGetItems, final boolean canUseItems, final boolean dropItemsOnDeath,
			final int itemCapacity) {
		super(handleId, code, alias);
		this.canDropItems = canDropItems;
		this.canGetItems = canGetItems;
		this.canUseItems = canUseItems;
		this.dropItemsOnDeath = dropItemsOnDeath;
		this.itemsHeld = new CItem[itemCapacity];
		this.itemsHeldAbilities = new List[itemCapacity];
		for (int i = 0; i < this.itemsHeldAbilities.length; i++) {
			this.itemsHeldAbilities[i] = new ArrayList<>();
		}
	}

	// 当能力添加到单位时调用
	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behaviorGetItem = new CBehaviorGetItem(unit, this);
		this.behaviorDropItem = new CBehaviorDropItem(unit, this);
		this.behaviorGiveItem = new CBehaviorGiveItemToHero(unit, this);
	}

	// 当能力从单位中移除时调用
	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		for (int i = 0; i < this.itemsHeld.length; i++) {
			if (this.itemsHeld[i] != null) {
				dropItem(game, unit, i, unit.getX(), unit.getY(), false);
			}
		}
	}

	// 每个游戏周期调用
	@Override
	public void onTick(final CSimulation game, final CUnit unit) {

	}

	// 在排队前检查操作是否合法
	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		if ((orderId >= OrderIds.itemdrag00) && (orderId <= OrderIds.itemdrag05)) {
			for (int i = 0; i < this.itemsHeld.length; i++) {
				if (this.itemsHeld[i] == target) {
					final CItem temp = this.itemsHeld[i];
					final List<CAbility> swapList = this.itemsHeldAbilities[i];
					final int dragDropDestinationIndex = orderId - OrderIds.itemdrag00;
					this.itemsHeld[i] = this.itemsHeld[dragDropDestinationIndex];
					this.itemsHeldAbilities[i] = this.itemsHeldAbilities[dragDropDestinationIndex];
					this.itemsHeld[dragDropDestinationIndex] = temp;
					this.itemsHeldAbilities[dragDropDestinationIndex] = swapList;
					return false;
				}
			}
		}
		else if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
			final int slot = orderId - OrderIds.itemuse00;
			final List<CAbility> itemsHeldAbilitiesForSlot = this.itemsHeldAbilities[slot];
			if (!itemsHeldAbilitiesForSlot.isEmpty()) {
				final CAbility cAbility = itemsHeldAbilitiesForSlot.get(0);
				int forwardedOrderId = orderId;
				if (cAbility instanceof SingleOrderAbility) {
					forwardedOrderId = ((SingleOrderAbility) cAbility).getBaseOrderId();
				}
				final boolean checkResult = cAbility.checkBeforeQueue(game, caster, forwardedOrderId, target);
				if (!checkResult) {
					// we will never call begin, so we need to consume a charge of perishables here
					// assuming this is a no-queue instant use perishable... later if we have some
					// other weird case where "check before queue" false is supposed to mean you
					// can't use the skill, then this would consume charges without using it, and
					// that would be stupid but I don't think we will do that since checkCanUse
					// should be failing at that point. So then we should have never called
					// checkBeforeQueue.
					final CItem cItem = this.itemsHeld[slot];
					consumePerishableCharge(game, caster, slot, cItem);
				}
				return checkResult;
			}
		}
		return super.checkBeforeQueue(game, caster, orderId, target);
	}

	// 消耗易耗物品的使用次数
	private void consumePerishableCharge(final CSimulation game, final CUnit caster, final int slot,
			final CItem cItem) {
		final int updatedCharges = cItem.getCharges() - 1;
		if (updatedCharges >= 0) {
			cItem.setCharges(updatedCharges);
			if (updatedCharges == 0) {
				if (cItem.getItemType().isPerishable()) {
					dropItem(game, caster, slot, caster.getX(), caster.getY(), false);
					game.removeItem(cItem);
				}
			}
		}
	}

	// 当从队列中取消时调用
	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {

	}

	// 获取物品容量
	public int getItemCapacity() {
		return this.itemsHeld.length;
	}

	// 获取指定槽位的物品
	public CItem getItemInSlot(final int slotIndex) {
		if ((slotIndex < 0) || (slotIndex >= this.itemsHeld.length)) {
			return null;
		}
		return this.itemsHeld[slotIndex];
	}

	// 检查是否可以在死亡时丢弃物品
	public boolean isDropItemsOnDeath() {
		return this.dropItemsOnDeath;
	}

	// 开始行为
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
			final int slot = orderId - OrderIds.itemuse00;
			final List<CAbility> itemsHeldAbilitiesForSlot = this.itemsHeldAbilities[slot];
			if (!itemsHeldAbilitiesForSlot.isEmpty()) {
				final CAbility ability = itemsHeldAbilitiesForSlot.get(0);
				int forwardedOrderId = orderId;
				if (ability instanceof SingleOrderAbility) {
					forwardedOrderId = ((SingleOrderAbility) ability).getBaseOrderId();
				}
				final CBehavior behavior = ability.begin(game, caster, forwardedOrderId, target);
				final CItem cItem = this.itemsHeld[slot];
				consumePerishableCharge(game, caster, slot, cItem);
				return behavior;
			}
		}
		final CItem targetItem = target.visit(AbilityTargetVisitor.ITEM);
		if (targetItem != null) {
			return this.behaviorGetItem.reset((CItem) target);
		}
		return caster.pollNextOrderBehavior(game);
	}

	// 开始丢弃物品的行为
	public CBehavior beginDropItem(final CSimulation game, final CUnit caster, final int orderId,
			final CItem itemToDrop, final AbilityPointTarget target) {
		return this.behaviorDropItem.reset(itemToDrop, target);
	}

	// 开始给予物品给英雄的行为
	public CBehavior beginDropItem(final CSimulation game, final CUnit caster, final int orderId,
			final CItem itemToDrop, final CUnit targetHero) {
		return this.behaviorGiveItem.reset(itemToDrop, targetHero);
	}

	// 开始行为，使用给定的位置目标
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
			final int slot = orderId - OrderIds.itemuse00;
			final List<CAbility> itemsHeldAbilitiesForSlot = this.itemsHeldAbilities[slot];
			if (!itemsHeldAbilitiesForSlot.isEmpty()) {
				final CAbility ability = itemsHeldAbilitiesForSlot.get(0);
				int forwardedOrderId = orderId;
				if (ability instanceof SingleOrderAbility) {
					forwardedOrderId = ((SingleOrderAbility) ability).getBaseOrderId();
				}
				final CBehavior behavior = ability.begin(game, caster, forwardedOrderId, point);
				final CItem cItem = this.itemsHeld[slot];
				consumePerishableCharge(game, caster, slot, cItem);
				return behavior;
			}
		}
		return caster.pollNextOrderBehavior(game);
	}

	// 开始无目标的行为
	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
			final int slot = orderId - OrderIds.itemuse00;
			final List<CAbility> itemsHeldAbilitiesForSlot = this.itemsHeldAbilities[slot];
			if (!itemsHeldAbilitiesForSlot.isEmpty()) {
				final CAbility ability = itemsHeldAbilitiesForSlot.get(0);
				int forwardedOrderId = orderId;
				if (ability instanceof SingleOrderAbility) {
					forwardedOrderId = ((SingleOrderAbility) ability).getBaseOrderId();
				}
				final CBehavior behavior = ability.beginNoTarget(game, caster, forwardedOrderId);
				final CItem cItem = this.itemsHeld[slot];
				consumePerishableCharge(game, caster, slot, cItem);
				return behavior;
			}
		}
		return caster.pollNextOrderBehavior(game);
	}

	// 检查可以针对指定目标
	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (((orderId == OrderIds.getitem) || (orderId == OrderIds.smart)) && !target.isDead()) {
			if (target instanceof CItem) {
				if (this.canGetItems) {
					final CItem targetItem = (CItem) target;
					if (!targetItem.isHidden()) {
						receiver.targetOk(target);
					}
					else {
						receiver.orderIdNotAccepted();
					}
				} else {
					receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_PICK_UP_THIS_ITEM);
				}
			}
			else {
				receiver.orderIdNotAccepted();
			}
		}
		else {
			if ((orderId >= OrderIds.itemdrag00) && (orderId <= OrderIds.itemdrag05)) {
				if (target instanceof CItem) {
					final int slot = getSlot((CItem) target);
					if (slot != -1) {
						receiver.targetOk(target);
					}
					else {
						receiver.orderIdNotAccepted();
					}
				}
				else {
					receiver.orderIdNotAccepted();
				}
			}
			else if (orderId == OrderIds.dropitem) {
				if (target instanceof CUnit) {
					final CUnit hero = (CUnit) target;
					if (game.getPlayer(hero.getPlayerIndex()).hasAlliance(unit.getPlayerIndex(), CAllianceType.PASSIVE)
							&& (hero != unit)) {
						if (hero.getInventoryData() != null) {
							receiver.targetOk(target);
						}
						else if (hero.getFirstAbilityOfType(CAbilityShopPurhaseItem.class) != null) {
							receiver.targetOk(target);
						}
						else {
							receiver.orderIdNotAccepted();
						}
					}
					else {
						receiver.orderIdNotAccepted();
					}
				}
				else {
					receiver.orderIdNotAccepted();
				}
			}
			else {
				if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
					final int slot = orderId - OrderIds.itemuse00;
					final List<CAbility> itemsHeldAbilitiesForSlot = this.itemsHeldAbilities[slot];
					if (!itemsHeldAbilitiesForSlot.isEmpty()) {
						final CAbility ability = itemsHeldAbilitiesForSlot.get(0);
						int forwardedOrderId = orderId;
						if (ability instanceof SingleOrderAbility) {
							forwardedOrderId = ((SingleOrderAbility) ability).getBaseOrderId();
						}
						ability.checkCanTarget(game, unit, forwardedOrderId, target, receiver);
					}
					else {
						receiver.orderIdNotAccepted();
					}
				}
				else {
					receiver.orderIdNotAccepted();
				}
			}
		}
	}

	// 根据目标获取槽位
	public int getSlot(final CItem target) {
		int slot = -1;
		for (int i = 0; i < this.itemsHeld.length; i++) {
			if (this.itemsHeld[i] == target) {
				slot = i;
			}
		}
		return slot;
	}

	// 检查可以针对指定目标，带有位置参数
	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (orderId != OrderIds.dropitem) {
			if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
				final int slot = orderId - OrderIds.itemuse00;
				final List<CAbility> itemsHeldAbilitiesForSlot = this.itemsHeldAbilities[slot];
				if (!itemsHeldAbilitiesForSlot.isEmpty()) {
					final CAbility ability = itemsHeldAbilitiesForSlot.get(0);
					int forwardedOrderId = orderId;
					if (ability instanceof SingleOrderAbility) {
						forwardedOrderId = ((SingleOrderAbility) ability).getBaseOrderId();
					}
					ability.checkCanTarget(game, unit, forwardedOrderId, target, receiver);
				}
				else {
					receiver.orderIdNotAccepted();
				}
			}
			else {
				receiver.orderIdNotAccepted();
			}
		}
		else {
			receiver.targetOk(target);
		}
	}

	// 检查可以在无目标情况下使用
	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
			final int slot = orderId - OrderIds.itemuse00;
			final List<CAbility> itemsHeldAbilitiesForSlot = this.itemsHeldAbilities[slot];
			if (!itemsHeldAbilitiesForSlot.isEmpty()) {
				final CAbility ability = itemsHeldAbilitiesForSlot.get(0);
				int forwardedOrderId = orderId;
				if (ability instanceof SingleOrderAbility) {
					forwardedOrderId = ((SingleOrderAbility) ability).getBaseOrderId();
				}
				ability.checkCanTargetNoTarget(game, unit, forwardedOrderId, receiver);
			}
			else {
				receiver.orderIdNotAccepted();
			}
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	// 检查是否可以使用能力
	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
			if (this.canUseItems) {
				final int slot = orderId - OrderIds.itemuse00;
				if (this.itemsHeldAbilities[slot].size() < 1) {
					receiver.notAnActiveAbility();
				}
				else {
					final List<CAbility> itemsHeldAbilitiesForSlot = this.itemsHeldAbilities[slot];
					if (!itemsHeldAbilitiesForSlot.isEmpty()) {
						final CAbility ability = itemsHeldAbilitiesForSlot.get(0);
						int forwardedOrderId = orderId;
						if (ability instanceof SingleOrderAbility) {
							forwardedOrderId = ((SingleOrderAbility) ability).getBaseOrderId();
						}
						ability.checkCanUse(game, unit, forwardedOrderId, receiver);
					}
					else {
						receiver.notAnActiveAbility();
					}
				}
			}
			else {
				receiver.activationCheckFailed(CommandStringErrorKeys.UNABLE_TO_USE_THIS_ITEM);
			}
		}
		else if(orderId == OrderIds.dropitem && !this.canDropItems) {
			receiver.activationCheckFailed(CommandStringErrorKeys.UNABLE_TO_DROP_THIS_ITEM);
		} else {
			receiver.useOk();
		}
	}

	// 给予物品给英雄的方法，带有用户UI音效选项
	public int giveItem(final CSimulation simulation, final CUnit hero, final CItem item,
			final boolean playUserUISounds) {
		return giveItem(simulation, hero, item, 0, playUserUISounds);
	}

	/**
	 * 尝试将指定物品给予英雄，并返回添加物品的槽位或-1表示未找到可用槽位
	 *
	 * @param item
	 * @return
	 */
	public int giveItem(final CSimulation simulation, final CUnit hero, final CItem item, final int slotPreference,
			final boolean playUserUISounds) {
		if ((item != null) && !item.isDead() && !item.isHidden()) {
			final CItemType itemType = item.getItemType();
			if (this.canUseItems && itemType.isUseAutomaticallyWhenAcquired()) {
				if (itemType.isActivelyUsed()) {
					item.setLife(simulation, 0);
					// TODO when we give unit ability here, then use ability
					final List<CAbility> addedAbilities = new ArrayList<>();
					for (final War3ID abilityId : item.getItemType().getAbilityList()) {
						final CAbilityType<?> abilityType = simulation.getAbilityData().getAbilityType(abilityId);
						if (abilityType != null) {
							final CAbility abilityFromItem = abilityType
									.createAbility(simulation.getHandleIdAllocator().createId());
							abilityFromItem.setIconShowing(false);
							abilityFromItem.setItemAbility(item, -1);
							hero.add(simulation, abilityFromItem);
							if (abilityFromItem instanceof SingleOrderAbility) {
								final int baseOrderId = ((SingleOrderAbility) abilityFromItem).getBaseOrderId();
								hero.order(simulation,
										new COrderNoTarget(abilityFromItem.getHandleId(), baseOrderId, false), false);
							}
							addedAbilities.add(abilityFromItem);
						}
					}
					hero.onPickUpItem(simulation, item, true);
					for (final CAbility ability : addedAbilities) {
						hero.remove(simulation, ability);
					}
				}
			}
			else {
				for (int i = 0; i < this.itemsHeld.length; i++) {
					final int itemIndex = (i + slotPreference) % this.itemsHeld.length;
					if (this.itemsHeld[itemIndex] == null) {
						this.itemsHeld[itemIndex] = item;
						item.setHidden(true);
						item.setContainedInventory(this, hero);
						if (this.canUseItems) {
							for (final War3ID abilityId : item.getItemType().getAbilityList()) {
								final CAbilityType<?> abilityType = simulation.getAbilityData()
										.getAbilityType(abilityId);
								if (abilityType != null) {
									final CAbility abilityFromItem = abilityType
											.createAbility(simulation.getHandleIdAllocator().createId());
									abilityFromItem.setIconShowing(false);
									abilityFromItem.setItemAbility(item, itemIndex);
									hero.add(simulation, abilityFromItem);
									this.itemsHeldAbilities[itemIndex].add(abilityFromItem);
								}
							}
						}
						hero.onPickUpItem(simulation, item, true);
						return itemIndex;
					}
				}
				if (playUserUISounds) {
					simulation.getCommandErrorListener().showInterfaceError(hero.getPlayerIndex(), CommandStringErrorKeys.INVENTORY_IS_FULL);
				}
			}
		}
		return -1;
	}

	// 丢弃指定槽位的物品
	public void dropItem(final CSimulation simulation, final CUnit hero, final int slotIndex, final float x,
			final float y, final boolean playUserUISounds) {
		final CItem droppedItem = this.itemsHeld[slotIndex];
		hero.onDropItem(simulation, droppedItem, playUserUISounds);
		this.itemsHeld[slotIndex] = null;
		for (final CAbility ability : this.itemsHeldAbilities[slotIndex]) {
			hero.remove(simulation, ability);
		}
		this.itemsHeldAbilities[slotIndex].clear();
		droppedItem.setHidden(false);
		droppedItem.setContainedInventory(null, null);
		droppedItem.setPointAndCheckUnstuck(x, y, simulation);
	}

	// 根据指定物品必丢弃物品
	public void dropItem(final CSimulation simulation, final CUnit hero, final CItem itemToDrop, final float x,
			final float y, final boolean playUserUISounds) {
		boolean foundItem = false;
		int index = -1;
		for (int i = 0; i < this.itemsHeld.length; i++) {
			if (this.itemsHeld[i] == itemToDrop) {
				this.itemsHeld[i] = null;
				index = i;
				foundItem = true;
			}
		}
		if (foundItem) {
			hero.onDropItem(simulation, itemToDrop, playUserUISounds);
			itemToDrop.setHidden(false);
			itemToDrop.setContainedInventory(null, null);
			for (final CAbility ability : this.itemsHeldAbilities[index]) {
				hero.remove(simulation, ability);
			}
			this.itemsHeldAbilities[index].clear();
			itemToDrop.setPointAndCheckUnstuck(x, y, simulation);
		}
	}

	// 当英雄死亡时调用
	@Override
	public void onDeath(final CSimulation game, final CUnit hero) {
		if (this.dropItemsOnDeath) {
			for (int i = 0; i < this.itemsHeld.length; i++) {
				if (this.itemsHeld[i] != null) {
					dropItem(game, hero, i, hero.getX(), hero.getY(), false);
				}
			}
		}
	}

}

