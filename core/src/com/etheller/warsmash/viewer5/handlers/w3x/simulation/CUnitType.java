package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CPrimaryAttribute;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CRegenType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CUpgradeClass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;
/**
 * 表示单位类型的类，包含单位的各项属性。
 * 该类用于快速查找单位类型的值，可能无法根据单位实例进行更改。
 */
public class CUnitType {
	// 名称
	private final String name;
	// 单位的继承名称
	private final String legacyName;
	// 单位的类型标识
	private final War3ID typeId;
	// 单位的最大生命值
	private final int maxLife;
	// 生命值再生速度
	private final float lifeRegen;
	// 法力值再生速度
	private final float manaRegen;
	// 生命再生类型
	private final CRegenType lifeRegenType;
	// 初始法力值
	private final int manaInitial;
	// 最大法力值
	private final int manaMaximum;
	// 单位的移动速度
	private final int speed;
	// 单位的防御值
	private final int defense;
	// 默认自动施法能力
	private final War3ID defaultAutocastAbility;
	// 单位可以使用的能力列表
	private final List<War3ID> abilityList;
	// 是否为建筑物
	private final boolean building;
	// 单位的移动类型
	private final PathingGrid.MovementType movementType;
	// 默认飞行高度
	private final float defaultFlyingHeight;
	// 碰撞体积大小
	private final float collisionSize;
	// 单位的分类集合
	private final EnumSet<CUnitClassification> classifications;
	// 单位的攻击列表
	private final List<CUnitAttack> attacks;

	// 能用的普攻数量
	private final int attacksEnabled;
	// 护甲类型， 用于音效
	private final String armorType; // used for audio
	// 是否可以升起
	private final boolean raise;
	// 是否可以腐烂
	private final boolean decay;
	// 防御类型
	private final CDefenseType defenseType;
	// 碰撞高度
	private final float impactZ;
	// 死亡时间
	private final float deathTime;

	//TODO：这可能不应该存储为游戏状态，即，它真的是
	//游戏数据？我们能用更干净的方式储存它吗？
	// TODO: this should probably not be stored as game state, i.e., is it really
	// game data? can we store it in a cleaner way?
	// 建筑寻路像素图
	private final BufferedImage buildingPathingPixelMap;
	// 目标类型
	private final EnumSet<CTargetType> targetedAs;
	// 默认采集范围
	private final float defaultAcquisitionRange;
	// 最小攻击范围
	private final float minimumAttackRange;
	// 该类用于存储和管理建筑和单位的信息和状态
	private final List<War3ID> structuresBuilt;
	// 存储训练过的单位ID列表
	private final List<War3ID> unitsTrained;
	// 可用研究的ID列表
	private final List<War3ID> researchesAvailable;
	// 已使用升级的ID列表
	private final List<War3ID> upgradesUsed;
	// 将升级类别映射到其类型的枚举映射
	private final EnumMap<CUpgradeClass, War3ID> upgradeClassToType;
	// 升级目标的ID列表
	private final List<War3ID> upgradesTo;
	// 售出的物品ID列表
	private final List<War3ID> itemsSold;
	// 制作的物品ID列表
	private final List<War3ID> itemsMade;
	// 单位种族
	private final CUnitRace unitRace;
	// 金币成本
	private final int goldCost;
	// 木材成本
	private final int lumberCost;
	// 已使用的食物数量
	private final int foodUsed;
	// 制作的食物数量
	private final int foodMade;
	// 建造时间
	private final int buildTime;
	// 受阻碍的建筑路径类型集合
	private final EnumSet<CBuildingPathingType> preventedPathingTypes;
	// 所需路径类型集合
	private final EnumSet<CBuildingPathingType> requiredPathingTypes;
	// 移动窗口：和目标点角度小于该值才可用移动，否则需要等转向到目标方向
	private final float propWindow;
	// 转向速度
	private final float turnRate;
	// 单位类型要求列表
	private final List<CUnitTypeRequirement> requirements;
	// 等级
	private final int level;

	// 是否是 英雄单位
	private final boolean hero;
	private final int startingStrength; // 初始力量
	private final float strengthPerLevel; // 每级力量增长
	private final int startingAgility; // 初始敏捷
	private final float agilityPerLevel; // 每级敏捷增长
	private final int startingIntelligence; // 初始智力
	private final float intelligencePerLevel; // 每级智力增长
	private final CPrimaryAttribute primaryAttribute; // 主要属性
	private final List<War3ID> heroAbilityList; // 英雄技能列表
	private final List<String> heroProperNames; // 英雄正确名称列表
	private final int properNamesCount; // 正确名称计数
	private final boolean canFlee; // 能否逃跑
	private final int priority; // 优先级
	private final boolean revivesHeroes; // 是否复活英雄
	private final int pointValue; // 点数值
	private final List<List<CUnitTypeRequirement>> requirementTiers; // 需求层级列表

	// 施法后摇时间点
	private final float castBackswingPoint;
	// 施法效果时间点
	private final float castPoint;
	private final boolean canBeBuiltOnThem; // 是否可以被建造在其上
	private final boolean canBuildOnMe; // 是否可以建造在其上
	private final int defenseUpgradeBonus; // 防御升级奖励
	private final int sightRadiusDay; // 白天视野半径
	private final int sightRadiusNight; // 夜晚视野半径
	private final boolean extendedLineOfSight; // 是否扩展视野
	private final int goldBountyAwardedBase; // 基础金币奖励
	private final int goldBountyAwardedDice; // 金币奖励掷骰子数
	private final int goldBountyAwardedSides; // 金币奖励骰子面数
	private final int lumberBountyAwardedBase; // 基础木材奖励
	private final int lumberBountyAwardedDice; // 木材奖励掷骰子数
	private final int lumberBountyAwardedSides; // 木材奖励骰子面数
	private final boolean neutralBuildingShowMinimapIcon; // 中立建筑是否显示小地图图标


	/**
	 * 构造函数，初始化单位类型的各项属性。
	 *
	 * @param name                      单位名称
	 * @param legacyName                单位旧名称
	 * @param typeId                    单位类型ID
	 * @param maxLife                   最大生命值
	 * @param lifeRegen                 生命恢复速度
	 * @param manaRegen                 魔法恢复速度
	 * @param lifeRegenType             生命恢复类型
	 * @param manaInitial               初始魔法值
	 * @param manaMaximum               最大魔法值
	 * @param speed                     移动速度
	 * @param defense                   防御值
	 * @param defaultAutocastAbility    默认自动施法技能
	 * @param abilityList               技能列表
	 * @param isBldg                    是否是建筑
	 * @param movementType              移动类型
	 * @param defaultFlyingHeight       默认飞行高度
	 * @param collisionSize             碰撞体积
	 * @param classifications           单位分类
	 * @param attacks                   攻击列表
	 * @param attacksEnabled            可用的普攻数量
	 * @param armorType                 护甲类型
	 * @param raise                     是否可以升起
	 * @param decay                     是否可以腐烂
	 * @param defenseType               防御类型
	 * @param impactZ                   碰撞高度
	 * @param buildingPathingPixelMap   建筑寻路像素图
	 * @param deathTime                 死亡时间
	 * @param targetedAs                被攻击时的目标类型
	 * @param defaultAcquisitionRange   默认采集范围
	 * @param minimumAttackRange        最小攻击范围
	 * @param structuresBuilt           建造的建筑列表
	 * @param unitsTrained              训练的单位列表
	 * @param researchesAvailable       可用的研究列表
	 * @param upgradesUsed              使用的升级列表
	 * @param upgradeClassToType        升级类到类型的映射
	 * @param upgradesTo                升级到的列表
	 * @param itemsSold                 出售的物品列表
	 * @param itemsMade                 制造的物品列表
	 * @param unitRace                  单位种族
	 * @param goldCost                  黄金成本
	 * @param lumberCost                木材成本
	 * @param foodUsed                  食物消耗
	 * @param foodMade                  食物生产
	 * @param buildTime                 建造时间
	 * @param preventedPathingTypes     禁止的寻路类型
	 * @param requiredPathingTypes      需要的寻路类型
	 * @param propWindow                属性窗口
	 * @param turnRate                  转向速度
	 * @param requirements              需求列表
	 * @param requirementTiers          需求层级列表
	 * @param level                     等级
	 * @param hero                      是否是英雄
	 * @param strength                  初始力量
	 * @param strengthPerLevel          每级力量增长
	 * @param agility                   初始敏捷
	 * @param agilityPerLevel           每级敏捷增长
	 * @param intelligence              初始智力
	 * @param intelligencePerLevel      每级智力增长
	 * @param primaryAttribute          主要属性
	 * @param heroAbilityList           英雄技能列表
	 * @param heroProperNames           英雄名称列表
	 * @param properNamesCount          名称数量
	 * @param canFlee                   是否可以逃跑
	 * @param priority                  优先级
	 * @param revivesHeroes             是否可以复活英雄
	 * @param pointValue                点值
	 * @param castBackswingPoint        施法后摇时间点
	 * @param castPoint                 施法效果时间点
	 * @param canBeBuiltOnThem          是否可以被建造在其上
	 * @param canBuildOnMe              是否可以建造在其上
	 * @param defenseUpgradeBonus       防御升级加成
	 * @param sightRadiusDay            白天视野范围
	 * @param sightRadiusNight          夜晚视野范围
	 * @param extendedLineOfSight       是否扩展视野
	 * @param goldBountyAwardedBase     基础黄金奖励
	 * @param goldBountyAwardedDice     黄金奖励骰子
	 * @param goldBountyAwardedSides    黄金奖励面数
	 * @param lumberBountyAwardedBase   基础木材奖励
	 * @param lumberBountyAwardedDice   木材奖励骰子
	 * @param lumberBountyAwardedSides  木材奖励面数
	 * @param neutralBuildingShowMinimapIcon 中立建筑是否显示小地图图标
	 */
	public CUnitType(final String name, final String legacyName, final War3ID typeId, final int maxLife,
			final float lifeRegen, final float manaRegen, final CRegenType lifeRegenType, final int manaInitial,
			final int manaMaximum, final int speed, final int defense, final War3ID defaultAutocastAbility,
			final List<War3ID> abilityList, final boolean isBldg, final MovementType movementType,
			final float defaultFlyingHeight, final float collisionSize,
			final EnumSet<CUnitClassification> classifications, final List<CUnitAttack> attacks,
			final int attacksEnabled, final String armorType, final boolean raise, final boolean decay,
			final CDefenseType defenseType, final float impactZ, BufferedImage buildingPathingPixelMap,
			final float deathTime, final EnumSet<CTargetType> targetedAs, final float defaultAcquisitionRange,
			final float minimumAttackRange, final List<War3ID> structuresBuilt, final List<War3ID> unitsTrained,
			final List<War3ID> researchesAvailable, final List<War3ID> upgradesUsed,
			final EnumMap<CUpgradeClass, War3ID> upgradeClassToType, final List<War3ID> upgradesTo,
			final List<War3ID> itemsSold, final List<War3ID> itemsMade, final CUnitRace unitRace, final int goldCost,
			final int lumberCost, final int foodUsed, final int foodMade, final int buildTime,
			final EnumSet<CBuildingPathingType> preventedPathingTypes,
			final EnumSet<CBuildingPathingType> requiredPathingTypes, final float propWindow, final float turnRate,
			final List<CUnitTypeRequirement> requirements, final List<List<CUnitTypeRequirement>> requirementTiers,
			final int level, final boolean hero, final int strength, final float strengthPerLevel, final int agility,
			final float agilityPerLevel, final int intelligence, final float intelligencePerLevel,
			final CPrimaryAttribute primaryAttribute, final List<War3ID> heroAbilityList,
			final List<String> heroProperNames, final int properNamesCount, final boolean canFlee, final int priority,
			final boolean revivesHeroes, final int pointValue, final float castBackswingPoint, final float castPoint,
			final boolean canBeBuiltOnThem, final boolean canBuildOnMe, final int defenseUpgradeBonus,
			final int sightRadiusDay, final int sightRadiusNight, final boolean extendedLineOfSight,
			final int goldBountyAwardedBase, final int goldBountyAwardedDice, final int goldBountyAwardedSides,
			final int lumberBountyAwardedBase, final int lumberBountyAwardedDice, final int lumberBountyAwardedSides,
			boolean neutralBuildingShowMinimapIcon) {
		this.name = name;
		this.legacyName = legacyName;
		this.typeId = typeId;
		this.maxLife = maxLife;
		this.lifeRegen = lifeRegen;
		this.manaRegen = manaRegen;
		this.lifeRegenType = lifeRegenType;
		this.manaInitial = manaInitial;
		this.manaMaximum = manaMaximum;
		this.speed = speed;
		this.defense = defense;
		this.defaultAutocastAbility = defaultAutocastAbility;
		this.abilityList = abilityList;
		this.building = isBldg;
		this.movementType = movementType;
		this.defaultFlyingHeight = defaultFlyingHeight;
		this.collisionSize = collisionSize;
		this.classifications = classifications;
		this.attacks = attacks;
		this.attacksEnabled = attacksEnabled;
		this.armorType = armorType;
		this.raise = raise;
		this.decay = decay;
		this.defenseType = defenseType;
		this.impactZ = impactZ;
		this.buildingPathingPixelMap = buildingPathingPixelMap;
		this.deathTime = deathTime;
		this.targetedAs = targetedAs;
		this.defaultAcquisitionRange = defaultAcquisitionRange;
		this.minimumAttackRange = minimumAttackRange;
		this.structuresBuilt = structuresBuilt;
		this.unitsTrained = unitsTrained;
		this.researchesAvailable = researchesAvailable;
		this.upgradesUsed = upgradesUsed;
		this.upgradeClassToType = upgradeClassToType;
		this.upgradesTo = upgradesTo;
		this.itemsSold = itemsSold;
		this.itemsMade = itemsMade;
		this.unitRace = unitRace;
		this.goldCost = goldCost;
		this.lumberCost = lumberCost;
		this.foodUsed = foodUsed;
		this.foodMade = foodMade;
		this.buildTime = buildTime;
		this.preventedPathingTypes = preventedPathingTypes;
		this.requiredPathingTypes = requiredPathingTypes;
		this.propWindow = propWindow;
		this.turnRate = turnRate;
		this.requirements = requirements;
		this.requirementTiers = requirementTiers;
		this.level = level;
		this.hero = hero;
		this.startingStrength = strength;
		this.strengthPerLevel = strengthPerLevel;
		this.startingAgility = agility;
		this.agilityPerLevel = agilityPerLevel;
		this.startingIntelligence = intelligence;
		this.intelligencePerLevel = intelligencePerLevel;
		this.primaryAttribute = primaryAttribute;
		this.heroAbilityList = heroAbilityList;
		this.heroProperNames = heroProperNames;
		this.properNamesCount = properNamesCount;
		this.canFlee = canFlee;
		this.priority = priority;
		this.revivesHeroes = revivesHeroes;
		this.pointValue = pointValue;
		this.castBackswingPoint = castBackswingPoint;
		this.castPoint = castPoint;
		this.canBeBuiltOnThem = canBeBuiltOnThem;
		this.canBuildOnMe = canBuildOnMe;
		this.defenseUpgradeBonus = defenseUpgradeBonus;
		this.sightRadiusDay = sightRadiusDay;
		this.sightRadiusNight = sightRadiusNight;
		this.extendedLineOfSight = extendedLineOfSight;
		this.goldBountyAwardedBase = goldBountyAwardedBase;
		this.goldBountyAwardedDice = goldBountyAwardedDice;
		this.goldBountyAwardedSides = goldBountyAwardedSides;
		this.lumberBountyAwardedBase = lumberBountyAwardedBase;
		this.lumberBountyAwardedDice = lumberBountyAwardedDice;
		this.lumberBountyAwardedSides = lumberBountyAwardedSides;
		this.neutralBuildingShowMinimapIcon = neutralBuildingShowMinimapIcon;
	}

	/**
	 * 获取单位名称。
	 *
	 * @return 单位名称
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 获取单位旧名称。
	 *
	 * @return 单位旧名称
	 */
	public String getLegacyName() {
		return this.legacyName;
	}

	/**
	 * 获取单位类型ID。
	 *
	 * @return 单位类型ID
	 */
	public War3ID getTypeId() {
		return this.typeId;
	}

	/**
	 * 获取最大生命值。
	 *
	 * @return 最大生命值
	 */
	public int getMaxLife() {
		return this.maxLife;
	}

	/**
	 * 获取生命恢复速度。
	 *
	 * @return 生命恢复速度
	 */
	public float getLifeRegen() {
		return this.lifeRegen;
	}

	/**
	 * 获取魔法恢复速度。
	 *
	 * @return 魔法恢复速度
	 */
	public float getManaRegen() {
		return this.manaRegen;
	}

	/**
	 * 获取生命恢复类型。
	 *
	 * @return 生命恢复类型
	 */
	public CRegenType getLifeRegenType() {
		return this.lifeRegenType;
	}

	/**
	 * 获取初始魔法值。
	 *
	 * @return 初始魔法值
	 */
	public int getManaInitial() {
		return this.manaInitial;
	}

	/**
	 * 获取最大魔法值。
	 *
	 * @return 最大魔法值
	 */
	public int getManaMaximum() {
		return this.manaMaximum;
	}

	/**
	 * 获取移动速度。
	 *
	 * @return 移动速度
	 */
	public int getSpeed() {
		return this.speed;
	}

	/**
	 * 获取防御值。
	 *
	 * @return 防御值
	 */
	public int getDefense() {
		return this.defense;
	}

	/**
	 * 获取默认自动施法技能。
	 *
	 * @return 默认自动施法技能
	 */
	public War3ID getDefaultAutocastAbility() {
		return this.defaultAutocastAbility;
	}

	// 获取能力列表
	public List<War3ID> getAbilityList() {
		return this.abilityList;
	}

	// 获取默认飞行高度
	public float getDefaultFlyingHeight() {
		return this.defaultFlyingHeight;
	}

	// 获取移动类型
	public PathingGrid.MovementType getMovementType() {
		return this.movementType;
	}

	// 获取碰撞大小
	public float getCollisionSize() {
		return this.collisionSize;
	}

	// 判断是否为建筑
	public boolean isBuilding() {
		return this.building;
	}

	// 获取单位分类
	public EnumSet<CUnitClassification> getClassifications() {
		return this.classifications;
	}

	// 获取攻击列表
	public List<CUnitAttack> getAttacks() {
		return this.attacks;
	}

	// 获取启用的攻击数量
	public int getAttacksEnabled() {
		return this.attacksEnabled;
	}

	// 判断是否能升起
	public boolean isRaise() {
		return this.raise;
	}

	// 判断是否会腐烂
	public boolean isDecay() {
		return this.decay;
	}

	// 获取护甲类型
	public String getArmorType() {
		return this.armorType;
	}

	// 获取防御类型
	public CDefenseType getDefenseType() {
		return this.defenseType;
	}

	// 获取影响的Z坐标
	public float getImpactZ() {
		return this.impactZ;
	}

	// 获取建筑路径像素图
	public BufferedImage getBuildingPathingPixelMap() {
		return this.buildingPathingPixelMap;
	}

	// 获取死亡时间
	public float getDeathTime() {
		return this.deathTime;
	}

	// 获取被目标化的类型
	public EnumSet<CTargetType> getTargetedAs() {
		return this.targetedAs;
	}

	// 默认采集范围
	public float getDefaultAcquisitionRange() {
		return this.defaultAcquisitionRange;
	}

	// 获取最小攻击范围
	public float getMinimumAttackRange() {
		return this.minimumAttackRange;
	}

	// 获取建造的结构列表
	public List<War3ID> getStructuresBuilt() {
		return this.structuresBuilt;
	}

	// 获取训练的单位列表
	public List<War3ID> getUnitsTrained() {
		return this.unitsTrained;
	}

	// 获取可研究列表
	public List<War3ID> getResearchesAvailable() {
		return this.researchesAvailable;
	}

	// 获取使用的升级列表
	public List<War3ID> getUpgradesUsed() {
		return this.upgradesUsed;
	}

	// 获取升级类别与类型的映射
	public EnumMap<CUpgradeClass, War3ID> getUpgradeClassToType() {
		return this.upgradeClassToType;
	}

	// 获取升级目标列表
	public List<War3ID> getUpgradesTo() {
		return this.upgradesTo;
	}

	// 获取出售的物品列表
	public List<War3ID> getItemsSold() {
		return this.itemsSold;
	}

	// 获取制作的物品列表
	public List<War3ID> getItemsMade() {
		return this.itemsMade;
	}

	// 获取种族
	public CUnitRace getRace() {
		return this.unitRace;
	}

	// 获取金币成本
	public int getGoldCost() {
		return this.goldCost;
	}

	// 获取木材成本
	public int getLumberCost() {
		return this.lumberCost;
	}

	// 获取使用的食物
	public int getFoodUsed() {
		return this.foodUsed;
	}

	// 获取制造的食物
	public int getFoodMade() {
		return this.foodMade;
	}

	// 获取建造时间
	public int getBuildTime() {
		return this.buildTime;
	}

	// 获取被阻止的路径类型
	public EnumSet<CBuildingPathingType> getPreventedPathingTypes() {
		return this.preventedPathingTypes;
	}

	// 获取所需的路径类型
	public EnumSet<CBuildingPathingType> getRequiredPathingTypes() {
		return this.requiredPathingTypes;
	}

	// 移动窗口：和目标点角度小于该值才可用移动，否则需要等转向到目标方向
	public float getPropWindow() {
		return this.propWindow;
	}

	// 获取转向速度
	public float getTurnRate() {
		return this.turnRate;
	}

	// 获取单位要求列表
	public List<CUnitTypeRequirement> getRequirements() {
		return this.requirements;
	}

	// 根据等级获取要求
	public List<CUnitTypeRequirement> getRequirementsTier(final int tier) {
		final int index = tier - 1;
		if ((index >= 0) && (index < this.requirementTiers.size())) {
			return this.requirementTiers.get(index);
		}
		else {
			return Collections.emptyList();
		}
	}

	// 获取等级
	public int getLevel() {
		return this.level;
	}

	// 是否是英雄
	public boolean isHero() {
		return this.hero;
	}

	// 获取起始力量
	public int getStartingStrength() {
		return this.startingStrength;
	}

	// 获取每级力量增量
	public float getStrengthPerLevel() {
		return this.strengthPerLevel;
	}

	// 获取起始敏捷
	public int getStartingAgility() {
		return this.startingAgility;
	}

	// 获取每级敏捷增量
	public float getAgilityPerLevel() {
		return this.agilityPerLevel;
	}

	// 获取起始智力
	public int getStartingIntelligence() {
		return this.startingIntelligence;
	}

	// 获取每级智力增量
	public float getIntelligencePerLevel() {
		return this.intelligencePerLevel;
	}

	// 获取主要属性
	public CPrimaryAttribute getPrimaryAttribute() {
		return this.primaryAttribute;
	}

	// 获取英雄能力列表
	public List<War3ID> getHeroAbilityList() {
		return this.heroAbilityList;
	}

	// 获取英雄名字列表
	public List<String> getHeroProperNames() {
		return this.heroProperNames;
	}

	// 获取名字总数
	public int getProperNamesCount() {
		return this.properNamesCount;
	}

	// 判断是否可以逃跑
	public boolean isCanFlee() {
		return this.canFlee;
	}

	// 获取优先级
	public int getPriority() {
		return this.priority;
	}

	// 判断是否复活英雄
	public boolean isRevivesHeroes() {
		return this.revivesHeroes;
	}

	// 获取积分值
	public int getPointValue() {
		return this.pointValue;
	}

	// 施法后摇时间点
	public float getCastBackswingPoint() {
		return this.castBackswingPoint;
	}

	// 施法效果时间点
	public float getCastPoint() {
		return this.castPoint;
	}

	// "isCan" - 这些方法名是计算机生成的，给我点宽容
	public boolean isCanBeBuiltOnThem() {
		return this.canBeBuiltOnThem;
	}

	public boolean isCanBuildOnMe() {
		return this.canBuildOnMe;
	}

	// 获取防御升级加成
	public int getDefenseUpgradeBonus() {
		return this.defenseUpgradeBonus;
	}

	// 获取白天可视半径
	public int getSightRadiusDay() {
		return this.sightRadiusDay;
	}

	// 获取夜间可视半径
	public int getSightRadiusNight() {
		return this.sightRadiusNight;
	}

	// 判断是否扩展视野
	public boolean isExtendedLineOfSight() {
		return this.extendedLineOfSight;
	}

	// 获取基础金币奖励
	public int getGoldBountyAwardedBase() {
		return this.goldBountyAwardedBase;
	}

	// 获取金币奖励骰子数
	public int getGoldBountyAwardedDice() {
		return this.goldBountyAwardedDice;
	}

	// 获取金币奖励边数
	public int getGoldBountyAwardedSides() {
		return this.goldBountyAwardedSides;
	}

	// 获取基础木材奖励
	public int getLumberBountyAwardedBase() {
		return this.lumberBountyAwardedBase;
	}

	// 获取木材奖励骰子数
	public int getLumberBountyAwardedDice() {
		return this.lumberBountyAwardedDice;
	}

	// 获取木材奖励边数
	public int getLumberBountyAwardedSides() {
		return this.lumberBountyAwardedSides;
	}

	// 占用货仓容量
	public int getCargoCapacity() {
		return 1;
	}

	// 判断是否在小地图上显示中立建筑图标
	public boolean isNeutralBuildingShowMinimapIcon() {
		return this.neutralBuildingShowMinimapIcon;
	}

}
