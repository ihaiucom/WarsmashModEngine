package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.item.CItemTypeJass;
// 表示物品类型的类
public class CItemType {
	// 物品的能力列表
	private final List<War3ID> abilityList;
	// 物品的冷却组
	private final War3ID cooldownGroup;
	// 是否忽略冷却时间
	private final boolean ignoreCooldown;
	// 物品的充能数量
	private final int numberOfCharges;
	// 物品是否被主动使用
	private final boolean activelyUsed;
	// 物品是否易腐烂
	private final boolean perishable;
	// 物品是否在获得时自动使用
	private final boolean useAutomaticallyWhenAcquired;
	// 物品的金币成本
	private final int goldCost;
	// 物品的木材成本
	private final int lumberCost;
	// 物品的最大库存
	private final int stockMax;
	// 物品的库存补充间隔
	private final int stockReplenishInterval;
	// 物品的库存开始延迟
	private final int stockStartDelay;
	// 物品的最大生命值
	private final int maxLife;
	// 物品的护甲类型
	private final String armorType;
	// 物品的等级
	private final int level;
	// 物品的未分类等级
	private final int levelUnclassified;
	// 物品的优先级
	private final int priority;
	// 物品是否可出售
	private final boolean sellable;
	// 物品是否可典当
	private final boolean pawnable;
	// 物品是否在携带者死亡时掉落
	private final boolean droppedWhenCarrierDies;
	// 物品是否可以被丢弃
	private final boolean canBeDropped;
	// 物品是否是转换的有效目标
	private final boolean validTargetForTransformation;
	// 物品是否包含在随机选择中
	private final boolean includeAsRandomChoice;
	// 物品的类别
	private final CItemTypeJass itemClass;

	// 构造函数，初始化物品类型的所有属性
	public CItemType(final List<War3ID> abilityList, final War3ID cooldownGroup, final boolean ignoreCooldown,
			final int numberOfCharges, final boolean activelyUsed, final boolean perishable,
			final boolean useAutomaticallyWhenAcquired, final int goldCost, final int lumberCost, final int stockMax,
			final int stockReplenishInterval, final int stockStartDelay, final int maxLife, final String armorType,
			final int level, final int levelUnclassified, final int priority, final boolean sellable,
			final boolean pawnable, final boolean droppedWhenCarrierDies, final boolean canBeDropped,
			final boolean validTargetForTransformation, final boolean includeAsRandomChoice,
			final CItemTypeJass itemClass) {
		this.abilityList = abilityList;
		this.cooldownGroup = cooldownGroup;
		this.ignoreCooldown = ignoreCooldown;
		this.numberOfCharges = numberOfCharges;
		this.activelyUsed = activelyUsed;
		this.perishable = perishable;
		this.useAutomaticallyWhenAcquired = useAutomaticallyWhenAcquired;
		this.goldCost = goldCost;
		this.lumberCost = lumberCost;
		this.stockMax = stockMax;
		this.stockReplenishInterval = stockReplenishInterval;
		this.stockStartDelay = stockStartDelay;
		this.maxLife = maxLife;
		this.armorType = armorType;
		this.level = level;
		this.levelUnclassified = levelUnclassified;
		this.priority = priority;
		this.sellable = sellable;
		this.pawnable = pawnable;
		this.droppedWhenCarrierDies = droppedWhenCarrierDies;
		this.canBeDropped = canBeDropped;
		this.validTargetForTransformation = validTargetForTransformation;
		this.includeAsRandomChoice = includeAsRandomChoice;
		this.itemClass = itemClass;
	}

	// 获取物品的能力列表
	public List<War3ID> getAbilityList() {
		return this.abilityList;
	}

	// 获取物品的冷却组
	public War3ID getCooldownGroup() {
		return this.cooldownGroup;
	}

	// 判断物品是否忽略冷却时间
	public boolean isIgnoreCooldown() {
		return this.ignoreCooldown;
	}

	// 获取物品的充能数量
	public int getNumberOfCharges() {
		return this.numberOfCharges;
	}

	// 判断物品是否被主动使用
	public boolean isActivelyUsed() {
		return this.activelyUsed;
	}

	// 判断物品是否易腐烂
	public boolean isPerishable() {
		return this.perishable;
	}

	// 判断物品是否在获得时自动使用
	public boolean isUseAutomaticallyWhenAcquired() {
		return this.useAutomaticallyWhenAcquired;
	}

	// 获取物品的金币成本
	public int getGoldCost() {
		return this.goldCost;
	}

	// 获取物品的木材成本
	public int getLumberCost() {
		return this.lumberCost;
	}

	// 获取物品的最大库存
	public int getStockMax() {
		return this.stockMax;
	}

	// 获取物品的库存补充间隔
	public int getStockReplenishInterval() {
		return this.stockReplenishInterval;
	}

	// 获取物品的库存开始延迟
	public int getStockStartDelay() {
		return this.stockStartDelay;
	}

	// 获取物品的最大生命值
	public int getMaxLife() {
		return this.maxLife;
	}

	// 获取物品的护甲类型
	public String getArmorType() {
		return this.armorType;
	}

	// 获取物品的等级
	public int getLevel() {
		return this.level;
	}

	// 获取物品的未分类等级
	public int getLevelUnclassified() {
		return this.levelUnclassified;
	}

	// 获取物品的优先级
	public int getPriority() {
		return this.priority;
	}

	// 判断物品是否可出售
	public boolean isSellable() {
		return this.sellable;
	}

	// 判断物品是否可典当
	public boolean isPawnable() {
		return this.pawnable;
	}

	// 判断物品是否在携带者死亡时掉落
	public boolean isDroppedWhenCarrierDies() {
		return this.droppedWhenCarrierDies;
	}

	// 判断物品是否可以被丢弃
	public boolean isCanBeDropped() {
		return this.canBeDropped;
	}

	// 判断物品是否是转换的有效目标
	public boolean isValidTargetForTransformation() {
		return this.validTargetForTransformation;
	}

	// 判断物品是否包含在随机选择中
	public boolean isIncludeAsRandomChoice() {
		return this.includeAsRandomChoice;
	}

	// 获取物品的类别
	public CItemTypeJass getItemClass() {
		return this.itemClass;
	}
}
