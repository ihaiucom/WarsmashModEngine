package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CUpgradeClass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffect;
// CUpgradeType类表示升级类型，包含相关的属性和方法
public class CUpgradeType {
	private War3ID typeId; // 升级类型的唯一标识
	private List<CUpgradeEffect> upgradeEffects; // 升级效果的列表
	private boolean appliesToAllUnits; // 是否适用于所有单位
	private CUpgradeClass upgradeClass; // 升级类别
	private int goldBase; // 基础金币成本
	private int goldIncrement; // 每级升级增加的金币成本
	private int levelCount; // 升级级数
	private int lumberBase; // 基础木材成本
	private int lumberIncrement; // 每级升级增加的木材成本
	private CUnitRace unitRace; // 适用的单位种族
	private int timeBase; // 基础建造时间
	private int timeIncrement; // 每级升级增加的建造时间
	private boolean transferWithUnitOwnership; // 是否在单位转移所有权时也转移升级
	private final List<UpgradeLevel> levelData; // 升级级别的数据
    /**
     * CUpgradeType构造函数，初始化升级类型的各个属性
     *
     * @param typeId                    升级类型的唯一标识
     * @param upgradeEffects            升级效果的列表
     * @param appliesToAllUnits         是否适用于所有单位
     * @param upgradeClass              升级类别
     * @param goldBase                  基础金币成本
     * @param goldIncrement             每级升级增加的金币成本
     * @param levelCount                升级级数
     * @param lumberBase                基础木材成本
     * @param lumberIncrement           每级升级增加的木材成本
     * @param unitRace                  适用的单位种族
     * @param timeBase                  基础建造时间
     * @param timeIncrement             每级升级增加的建造时间
     * @param transferWithUnitOwnership 是否在单位转移所有权时也转移升级
     * @param levelData                 升级级别的数据
     */
    public CUpgradeType(War3ID typeId, List<CUpgradeEffect> upgradeEffects, boolean appliesToAllUnits,
            CUpgradeClass upgradeClass, int goldBase, int goldIncrement, int levelCount, int lumberBase,
            int lumberIncrement, CUnitRace unitRace, int timeBase, int timeIncrement, boolean transferWithUnitOwnership,
            List<UpgradeLevel> levelData) {
        this.typeId = typeId;
        this.upgradeEffects = upgradeEffects;
        this.appliesToAllUnits = appliesToAllUnits;
        this.upgradeClass = upgradeClass;
        this.goldBase = goldBase;
        this.goldIncrement = goldIncrement;
        this.levelCount = levelCount;
        this.lumberBase = lumberBase;
        this.lumberIncrement = lumberIncrement;
        this.unitRace = unitRace;
        this.timeBase = timeBase;
        this.timeIncrement = timeIncrement;
        this.transferWithUnitOwnership = transferWithUnitOwnership;
        this.levelData = levelData;
    }


	// 获取升级类型的唯一标识
	public War3ID getTypeId() {
		return typeId;
	}

	// 获取升级效果列表
	public List<CUpgradeEffect> getUpgradeEffects() {
		return upgradeEffects;
	}

	// 检查是否适用于所有单位
	public boolean isAppliesToAllUnits() {
		return appliesToAllUnits;
	}

	// 获取升级类别
	public CUpgradeClass getUpgradeClass() {
		return upgradeClass;
	}

	// 获取基础金币成本
	public int getGoldBase() {
		return goldBase;
	}

	// 获取每级升级增加的金币成本
	public int getGoldIncrement() {
		return goldIncrement;
	}

	// 获取升级级数
	public int getLevelCount() {
		return levelCount;
	}

	// 获取基础木材成本
	public int getLumberBase() {
		return lumberBase;
	}

	// 获取每级升级增加的木材成本
	public int getLumberIncrement() {
		return lumberIncrement;
	}

	// 获取适用的单位种族
	public CUnitRace getUnitRace() {
		return unitRace;
	}

	// 获取基础建造时间
	public int getTimeBase() {
		return timeBase;
	}

	// 获取每级升级增加的建造时间
	public int getTimeIncrement() {
		return timeIncrement;
	}

	// 检查是否在单位转移所有权时也转移升级
	public boolean isTransferWithUnitOwnership() {
		return transferWithUnitOwnership;
	}

	// 获取指定索引的升级级别
	public UpgradeLevel getLevel(final int index) {
		if ((index >= 0) && (index < this.levelData.size())) {
			return this.levelData.get(index);
		}
		else {
			return null;
		}
	}

	// 获取建造时间，基于解锁的技术树
	public float getBuildTime(int techtreeUnlocked) {
		return timeBase + (timeIncrement * techtreeUnlocked);
	}

	// 获取升级的金币成本，基于已解锁的数量
	public int getGoldCost(int unlockedCount) {
		return goldBase + (goldIncrement * unlockedCount);
	}

	// 获取升级的木材成本，基于已解锁的数量
	public int getLumberCost(int unlockedCount) {
		return lumberBase + (lumberIncrement * unlockedCount);
	}

	// UpgradeLevel类表示升级的具体级别及其要求
	public static final class UpgradeLevel {
		private final String name; // 升级级别名称
		private final List<CUnitTypeRequirement> requirements; // 该级别的要求

		// UpgradeLevel构造函数，初始化名称和要求
		public UpgradeLevel(String name, List<CUnitTypeRequirement> requirements) {
			this.name = name;
			this.requirements = requirements;
		}

		// 获取升级级别名称
		public String getName() {
			return name;
		}

		// 获取该级别的要求
		public List<CUnitTypeRequirement> getRequirements() {
			return requirements;
		}
	}

	// 应用升级效果，针对单位
	public void apply(CSimulation simulation, CUnit unit, int i) {
		for (CUpgradeEffect upgradeEffect : getUpgradeEffects()) {
			upgradeEffect.apply(simulation, unit, i);
		}
	}

	// 应用升级效果，针对玩家
	public void apply(CSimulation simulation, int playerIndex, int i) {
		for (CUpgradeEffect upgradeEffect : getUpgradeEffects()) {
			upgradeEffect.apply(simulation, playerIndex, i);
		}
	}

	// 取消升级效果，针对单位
	public void unapply(CSimulation simulation, CUnit unit, int i) {
		for (CUpgradeEffect upgradeEffect : getUpgradeEffects()) {
			upgradeEffect.unapply(simulation, unit, i);
		}
	}

	// 取消升级效果，针对玩家
	public void unapply(CSimulation simulation, int playerIndex, int i) {
		for (CUpgradeEffect upgradeEffect : getUpgradeEffects()) {
			upgradeEffect.unapply(simulation, playerIndex, i);
		}
	}
}
