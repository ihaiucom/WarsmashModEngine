package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CGameplayConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitTypeRequirement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.HandleIdAllocator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.GetAbilityByRawcodeVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.CAutocastAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNagaBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNightElfBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityOrcBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityUndeadBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CPrimaryAttribute;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop.CAbilitySellItems;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityQueue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityReviveHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.upgrade.CAbilityUpgrade;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CRegenType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CUpgradeClass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileBounce;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileLine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileSplash;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackNormal;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderController;

public class CUnitData {
	// 初始魔法值
	private static final String MANA_INITIAL_AMOUNT = "mana0"; // replaced from 'umpi'
	// 最大魔法值
	private static final String MANA_MAXIMUM = "manaN"; // replaced from 'umpm'
	// 魔法恢复速度
	private static final String MANA_REGEN = "regenMana"; // replaced from 'umpr'
	// 初始生命值
	private static final String HIT_POINT_MAXIMUM = "HP"; // replaced from 'uhpm'
	// 生命恢复速度
	private static final String HIT_POINT_REGEN = "regenHP"; // replaced from 'uhpr'
	// 生命回复类型
	private static final String HIT_POINT_REGEN_TYPE = "regenType"; // replaced from 'uhrt'
	// 基础移动速度
	private static final String MOVEMENT_SPEED_BASE = "spd"; // replaced from 'umvs'
	// 推进窗口
	private static final String PROPULSION_WINDOW = "propWin"; // replaced from 'uprw'
	// 转向速度
	private static final String TURN_RATE = "turnRate"; // replaced from 'umvr'
	// 是否是建筑
	private static final String IS_BLDG = "isbldg"; // replaced from 'ubdg'
	// 名称
	private static final String NAME = "Name"; // replaced from 'unam'
	// 名称列表
	private static final String PROPER_NAMES = "Propernames"; // replaced from 'upro'
	// 名称数量
	private static final String PROPER_NAMES_COUNT = "nameCount"; // replaced from 'upru'
	// 投射物 起始位置偏移 X
	private static final String PROJECTILE_LAUNCH_X = "launchX"; // replaced from 'ulpx'
	// 投射物 起始位置偏移 Y
	private static final String PROJECTILE_LAUNCH_Y = "launchY"; // replaced from 'ulpy'
	// 投射物 起始位置偏移 Z
	private static final String PROJECTILE_LAUNCH_Z = "launchZ"; // replaced from 'ulpz'
	// 是否可以普攻
	private static final String ATTACKS_ENABLED = "weapsOn"; // replaced from 'uaen'
	// 普攻1 后摇时间点
	private static final String ATTACK1_BACKSWING_POINT = "backSw1"; // replaced from 'ubs1'
	// 普攻1 伤害效果时间点
	private static final String ATTACK1_DAMAGE_POINT = "dmgpt1"; // replaced from 'udp1'

	private static final String ATTACK1_AREA_OF_EFFECT_FULL_DMG = "Farea1"; // 全伤害范围
	private static final String ATTACK1_AREA_OF_EFFECT_HALF_DMG = "Harea1"; // 半伤害范围
	private static final String ATTACK1_AREA_OF_EFFECT_QUARTER_DMG = "Qarea1"; // 四分之一伤害范围
	private static final String ATTACK1_AREA_OF_EFFECT_TARGETS = "splashTargs1"; // 波及目标
	private static final String ATTACK1_ATTACK_TYPE = "atkType1"; // 攻击类型
	private static final String ATTACK1_COOLDOWN = "cool1"; // 冷却时间
	private static final String ATTACK1_DMG_BASE = "dmgplus1"; // 基础伤害
	private static final String ATTACK1_DAMAGE_FACTOR_HALF = "Hfact1"; // 半伤害因子
	private static final String ATTACK1_DAMAGE_FACTOR_QUARTER = "Qfact1"; // 四分之一伤害因子
	private static final String ATTACK1_DAMAGE_LOSS_FACTOR = "damageLoss1"; // 伤害损失因子
	private static final String ATTACK1_DMG_DICE = "dice1"; // 伤害骰子
	private static final String ATTACK1_DMG_SIDES_PER_DIE = "sides1"; // 每个骰子的面数
	private static final String ATTACK1_DMG_SPILL_DIST = "spillDist1"; // 伤害溢出距离
	private static final String ATTACK1_DMG_SPILL_RADIUS = "spillRadius1"; // 伤害溢出半径
	private static final String ATTACK1_DMG_UPGRADE_AMT = "dmgUp1"; // 伤害升级量
	private static final String ATTACK1_TARGET_COUNT = "targCount1"; // 目标数量
	private static final String ATTACK1_PROJECTILE_ARC = "Missilearc"; // 弹道弧度
	private static final String ATTACK1_MISSILE_ART = "Missileart"; // 弹道模型
	private static final String ATTACK1_PROJECTILE_HOMING_ENABLED = "MissileHoming"; // 弹道是否追踪
	private static final String ATTACK1_PROJECTILE_SPEED = "Missilespeed"; // 弹道速度
	private static final String ATTACK1_RANGE = "rangeN1"; // 攻击范围
	private static final String ATTACK1_RANGE_MOTION_BUFFER = "RngBuff1"; // 范围运动缓冲
	private static final String ATTACK1_SHOW_UI = "showUI1"; // 是否显示UI
	private static final String ATTACK1_TARGETS_ALLOWED = "targs1"; // 允许的目标类型
	private static final String ATTACK1_WEAPON_SOUND = "weapType1"; // 武器声音
	private static final String ATTACK1_WEAPON_TYPE = "weapTp1"; // 武器类型

	// 定义了攻击2的各个属性常量
	private static final String ATTACK2_BACKSWING_POINT = "backSw2"; // 攻击2的后摇点
	private static final String ATTACK2_DAMAGE_POINT = "dmgpt2"; // 攻击2的伤害点
	private static final String ATTACK2_AREA_OF_EFFECT_FULL_DMG = "Farea2"; // 攻击2的全伤害范围效果
	private static final String ATTACK2_AREA_OF_EFFECT_HALF_DMG = "Harea2"; // 攻击2的半伤害范围效果
	private static final String ATTACK2_AREA_OF_EFFECT_QUARTER_DMG = "Qarea2"; // 攻击2的四分之一伤害范围效果
	private static final String ATTACK2_AREA_OF_EFFECT_TARGETS = "splashTargs2"; // 攻击2的范围效果目标
	private static final String ATTACK2_ATTACK_TYPE = "atkType2"; // 攻击2的攻击类型
	private static final String ATTACK2_COOLDOWN = "cool2"; // 攻击2的冷却时间
	private static final String ATTACK2_DMG_BASE = "dmgplus2"; // 攻击2的基础伤害
	private static final String ATTACK2_DAMAGE_FACTOR_HALF = "Hfact2"; // 攻击2的半伤害因子
	private static final String ATTACK2_DAMAGE_FACTOR_QUARTER = "Qfact2"; // 攻击2的四分之一伤害因子
	private static final String ATTACK2_DAMAGE_LOSS_FACTOR = "damageLoss2"; // 攻击2的伤害损失因子
	private static final String ATTACK2_DMG_DICE = "dice2"; // 攻击2的伤害骰子
	private static final String ATTACK2_DMG_SIDES_PER_DIE = "sides2"; // 攻击2的每个骰子的面数
	private static final String ATTACK2_DMG_SPILL_DIST = "spillDist2"; // 攻击2的伤害溢出距离
	private static final String ATTACK2_DMG_SPILL_RADIUS = "spillRadius2"; // 攻击2的伤害溢出半径
	private static final String ATTACK2_DMG_UPGRADE_AMT = "dmgUp2"; // 攻击2的伤害升级量
	private static final String ATTACK2_TARGET_COUNT = "targCount2"; // 攻击2的目标数量
	private static final String ATTACK2_PROJECTILE_ARC = "Missilearc"; // 攻击2的弹道弧度
	private static final String ATTACK2_MISSILE_ART = "Missileart"; // 攻击2的弹道模型
	private static final String ATTACK2_PROJECTILE_HOMING_ENABLED = "MissileHoming"; // 攻击2的弹道是否启用追踪
	private static final String ATTACK2_PROJECTILE_SPEED = "Missilespeed"; // 攻击2的弹道速度
	private static final String ATTACK2_RANGE = "rangeN2"; // 攻击2的范围
	private static final String ATTACK2_RANGE_MOTION_BUFFER = "RngBuff2"; // 攻击2的范围运动缓冲
	private static final String ATTACK2_SHOW_UI = "showUI2"; // 攻击2是否显示UI
	private static final String ATTACK2_TARGETS_ALLOWED = "targs2"; // 攻击2允许的目标类型
	private static final String ATTACK2_WEAPON_SOUND = "weapType2"; // 攻击2的武器声音
	private static final String ATTACK2_WEAPON_TYPE = "weapTp2"; // 攻击2的武器类型

	// 定义了一系列常量，用于表示游戏中的各种属性和类型
	// 这些常量用于替换之前的简写代码，以提高代码的可读性和可维护性

	private static final String CAST_BACKSWING_POINT = "castbsw"; // 替换之前的'ucbs'，表示后摆击点
	private static final String CAST_POINT = "castpt"; // 替换之前的'ucpt'，表示投射点

	private static final String ACQUISITION_RANGE = "acquire"; // 替换之前的'uacq'，表示获取范围
	private static final String MINIMUM_ATTACK_RANGE = "minRange"; // 替换之前的'uamn'，表示最小攻击范围

	private static final String PROJECTILE_IMPACT_Z = "impactZ"; // 替换之前的'uimz'，表示弹丸撞击Z坐标

	private static final String DEATH_TYPE = "deathType"; // 替换之前的'udea'，表示死亡类型
	private static final String ARMOR_TYPE = "armor"; // 替换之前的'uarm'，表示护甲类型

	private static final String DEFENSE = "def"; // 替换之前的'udef'，表示防御
	private static final String DEFENSE_TYPE = "defType"; // 替换之前的'udty'，表示防御类型
	private static final String DEFENSE_UPGRADE_BONUS = "defUp"; // 替换之前的'udup'，表示防御升级加成
	private static final String MOVE_HEIGHT = "moveHeight"; // 替换之前的'umvh'，表示移动高度
	private static final String MOVE_TYPE = "movetp"; // 替换之前的'umvt'，表示移动类型
	private static final String COLLISION_SIZE = "collision"; // 替换之前的'ucol'，表示碰撞大小
	private static final String CLASSIFICATION = "type"; // 替换之前的'utyp'，表示分类
	private static final String DEATH_TIME = "death"; // 替换之前的'udtm'，表示死亡时间
	private static final String TARGETED_AS = "targType"; // 替换之前的'utar'，表示被定位为

	private static final String ABILITIES_DEFAULT_AUTO = "auto"; // 替换之前的'uabi'，表示默认自动能力
	private static final String ABILITIES_NORMAL = "abilList"; // 替换之前的'uabi'，表示普通能力列表
	private static final String ABILITIES_HERO = "heroAbilList"; // 替换之前的'uhab'，表示英雄能力列表

	private static final String STRUCTURES_BUILT = "Builds"; // 替换之前的'ubui'，表示建造的结构
	private static final String UNITS_TRAINED = "Trains"; // 替换之前的'utra'，表示训练的单位
	private static final String RESEARCHES_AVAILABLE = "Researches"; // 替换之前的'ures'，表示可用的研究
	private static final String UPGRADES_USED = "upgrades"; // 替换之前的'upgr'，表示已使用的升级
	private static final String UPGRADES_TO = "Upgrade"; // 替换之前的'uupt'，表示升级到
	private static final String ITEMS_SOLD = "Sellitems"; // 替换之前的'usei'，表示出售的物品
	private static final String ITEMS_MADE = "Makeitems"; // 替换之前的'umki'，表示制作的物品
	private static final String REVIVES_HEROES = "Revive"; // 替换之前的'urev'，表示复活英雄
	private static final String UNIT_RACE = "race"; // 替换之前的'urac'，表示单位种族

	private static final String REQUIRES = "Requires"; // 表示单位建造所需的资源
	private static final String REQUIRES_AMOUNT = "Requiresamount"; // 表示所需资源的数量
	private static final String REQUIRES_TIER_COUNT = "Requirescount"; // 表示所需资源的等级数量
	private static final String[] REQUIRES_TIER_X = { "Requires1", "Requires2", // 表示不同等级的所需资源
			"Requires3", "Requires4", "Requires5", "Requires6",
			"Requires7", "Requires8", "Requires9" };

	private static final String GOLD_COST = "goldcost"; // 表示单位建造所需的金币成本
	private static final String LUMBER_COST = "lumbercost"; // 表示单位建造所需的木材成本
	private static final String BUILD_TIME = "bldtm"; // 表示单位建造所需的时间
	private static final String FOOD_USED = "fused"; // 表示建造单位所消耗的食物
	private static final String FOOD_MADE = "fmade"; // 表示单位能生产的食物

	private static final String REQUIRE_PLACE = "requirePlace"; // 表示单位建造所需的地点
	private static final String PREVENT_PLACE = "preventPlace"; // 表示单位不能建造的地点

	private static final String UNIT_LEVEL = "level"; // 表示单位的等级

	// 表示单位的基础属性
	private static final String STR = "STR"; // 力量
	private static final String STR_PLUS = "STRplus"; // 力量加成
	private static final String AGI = "AGI"; // 敏捷
	private static final String AGI_PLUS = "AGIplus"; // 敏捷加成
	private static final String INT = "INT"; // 智力
	private static final String INT_PLUS = "INTplus"; // 智力加成
	private static final String PRIMARY_ATTRIBUTE = "Primary"; // 主属性

	private static final String CAN_FLEE = "canFlee"; // 表示单位是否能逃跑
	private static final String PRIORITY = "prio"; // 表示单位的优先级

	private static final String POINT_VALUE = "points"; // 表示单位的点数价值

	// 表示单位之间的建造关系
	private static final String CAN_BE_BUILT_ON_THEM = "isBuildOn"; // 表示其他单位能否在此单位上建造
	private static final String CAN_BUILD_ON_ME = "canBuildOn"; // 表示此单位能否在其他单位上建造

	private static final String SIGHT_RADIUS_DAY = "sight"; // 表示单位白天的视野范围
	private static final String SIGHT_RADIUS_NIGHT = "nsight"; // 表示单位夜晚的视野范围
	private static final String EXTENDED_LOS = "fatLOS"; // 表示单位的扩展视线范围

	// 定义了与金币赏金相关的常量，这些常量用于表示基础值、骰子数和面数
	private static final String GOLD_BOUNTY_AWARDED_BASE = "bountyplus"; // 替换自 'ubba'
	private static final String GOLD_BOUNTY_AWARDED_DICE = "bountydice"; // 替换自 'ubdi'
	private static final String GOLD_BOUNTY_AWARDED_SIDES = "bountysides"; // 替换自 'ubsi'

	// 定义了与木材赏金相关的常量，这些常量用于表示基础值、骰子数和面数
	private static final String LUMBER_BOUNTY_AWARDED_BASE = "lumberbountyplus"; // 替换自 'ulba'
	private static final String LUMBER_BOUNTY_AWARDED_DICE = "lumberbountydice"; // 替换自 'ulbd'
	private static final String LUMBER_BOUNTY_AWARDED_SIDES = "lumberbountysides"; // 替换自 'ulbs'

	// 定义了显示中立建筑图标的常量
	private static final String NEUTRAL_BUILDING_SHOW_ICON = "nbmmIcon";

	// 实例变量，包括游戏玩法常量、单位数据、单位ID到单位类型的映射等
	private final CGameplayConstants gameplayConstants;
	private final ObjectData unitData;
	private final Map<War3ID, CUnitType> unitIdToUnitType = new HashMap<>();
	private final Map<String, War3ID> jassLegacyNameToUnitId = new HashMap<>();
	private final CAbilityData abilityData;
	private final CUpgradeData upgradeData;
	private final SimulationRenderController simulationRenderController;


	public CUnitData(final CGameplayConstants gameplayConstants, final ObjectData unitData,
			final CAbilityData abilityData, final CUpgradeData upgradeData,
			final SimulationRenderController simulationRenderController) {
		this.gameplayConstants = gameplayConstants;
		this.unitData = unitData;
		this.abilityData = abilityData;
		this.upgradeData = upgradeData;
		this.simulationRenderController = simulationRenderController;
	}
	/**
	 * 创建一个新的单位（CUnit 对象）。
	 *
	 * @param simulation 游戏模拟对象。
	 * @param playerIndex 玩家索引。
	 * @param typeId 单位类型 ID。
	 * @param x 单位在地图上的 X 坐标。
	 * @param y 单位在地图上的 Y 坐标。
	 * @param facing 单位的朝向。
	 * @param buildingPathingPixelMap 建筑路径像素图。
	 * @param handleIdAllocator 句柄 ID 分配器。
	 * @return 新创建的单位对象。
	 */
	public CUnit create(final CSimulation simulation, final int playerIndex, final War3ID typeId, final float x,
			final float y, final float facing, final BufferedImage buildingPathingPixelMap,
			final HandleIdAllocator handleIdAllocator) {
		// 从 unitData 中获取单位类型的 GameObject
		final GameObject unitType = this.unitData.get(typeId.asStringValue());
		// 分配一个新的句柄 ID
		final int handleId = handleIdAllocator.createId();

		// 获取单位类型实例，可能会使用建筑路径像素图和 GameObject
		final CUnitType unitTypeInstance = getUnitTypeInstance(typeId, buildingPathingPixelMap, unitType);
		// 获取单位的最大生命值
		final int life = unitTypeInstance.getMaxLife();
		// 获取单位的生命回复速度
		final float lifeRegen = unitTypeInstance.getLifeRegen();
		// 获取单位的初始法力值
		final int manaInitial = unitTypeInstance.getManaInitial();
		// 获取单位的最大法力值
		final int manaMaximum = unitTypeInstance.getManaMaximum();
		// 获取单位的移动速度
		final int speed = unitTypeInstance.getSpeed();

		// 创建一个新的 CUnit 对象，使用分配的句柄 ID、玩家索引、位置、朝向、属性和单位类型实例
		final CUnit unit = new CUnit(handleId, playerIndex, x, y, life, typeId, facing, manaInitial, life, lifeRegen,
				manaMaximum, speed, unitTypeInstance);
		// 返回新创建的单位对象
		return unit;
	}
	/**
	 * 将玩家的升级应用到指定的单位上
	 *
	 * @param simulation 游戏模拟对象
	 * @param playerIndex 玩家索引
	 * @param unitTypeInstance 单位类型实例
	 * @param unit 要应用升级的单位
	 */
	public void applyPlayerUpgradesToUnit(final CSimulation simulation, final int playerIndex,
			final CUnitType unitTypeInstance, final CUnit unit) {
		// 获取指定玩家
		final CPlayer player = simulation.getPlayer(playerIndex);
		// 遍历单位类型实例使用的升级列表
		for (final War3ID upgradeId : unitTypeInstance.getUpgradesUsed()) {
			// 获取玩家对该升级的解锁状态
			final int techtreeUnlocked = player.getTechtreeUnlocked(upgradeId);
			// 如果升级已解锁
			if (techtreeUnlocked > 0) {
				// 从升级数据中获取升级类型
				final CUpgradeType upgradeType = this.upgradeData.getType(upgradeId);
				// 如果升级类型存在
				if (upgradeType != null) {
					// 将升级应用到单位上
					upgradeType.apply(simulation, unit, techtreeUnlocked);
				}
			}
		}
	}

	/**
	 * 从指定单位上移除玩家的升级效果
	 *
	 * @param simulation 游戏模拟对象
	 * @param playerIndex 玩家索引
	 * @param unitTypeInstance 单位类型实例
	 * @param unit 要移除升级效果的单位
	 */
	public void unapplyPlayerUpgradesToUnit(final CSimulation simulation, final int playerIndex,
			final CUnitType unitTypeInstance, final CUnit unit) {
		// 获取指定玩家
		final CPlayer player = simulation.getPlayer(playerIndex);
		// 遍历单位类型实例使用的升级列表
		for (final War3ID upgradeId : unitTypeInstance.getUpgradesUsed()) {
			// 获取玩家对该升级的解锁状态
			final int techtreeUnlocked = player.getTechtreeUnlocked(upgradeId);
			// 如果升级已解锁
			if (techtreeUnlocked > 0) {
				// 从升级数据中获取升级类型
				final CUpgradeType upgradeType = this.upgradeData.getType(upgradeId);
				// 如果升级类型存在
				if (upgradeType != null) {
					// 将升级从单位上移除
					upgradeType.unapply(simulation, unit, techtreeUnlocked);
				}
			}
		}
	}


	// 添加默认能力到单位
	public void addDefaultAbilitiesToUnit(final CSimulation simulation, final HandleIdAllocator handleIdAllocator,
			final CUnitType unitTypeInstance, final boolean resetMana, final int manaInitial, final int speed,
			final CUnit unit) {
		// 移动速度大于0，添加移动能力
		if (speed > 0) {
			unit.add(simulation, new CAbilityMove(handleIdAllocator.createId()));
		}
		// 获取普攻列表
		final List<CUnitAttack> unitSpecificAttacks = new ArrayList<>();
		for (final CUnitAttack attack : unitTypeInstance.getAttacks()) {
			unitSpecificAttacks.add(attack.copy());
		}
		// 设置普攻列表
		unit.setUnitSpecificAttacks(unitSpecificAttacks);
		// 设置能用的普攻列表
		unit.setUnitSpecificCurrentAttacks(
				getEnabledAttacks(unitSpecificAttacks, unitTypeInstance.getAttacksEnabled()));
		// 普攻列表不为空，添加普攻能力
		if (!unit.getCurrentAttacks().isEmpty()) {
			unit.add(simulation, new CAbilityAttack(handleIdAllocator.createId()));
		}
		// 获取单位类型实例可以建造的建筑列表
		final List<War3ID> structuresBuilt = unitTypeInstance.getStructuresBuilt();
		// 如果建筑列表不为空
		if (!structuresBuilt.isEmpty()) {
			// 根据单位类型实例的种族，添加相应的建造能力
			switch (unitTypeInstance.getRace()) {
				// 如果是兽族
				case ORC:
					// 添加兽族建造能力
					unit.add(simulation, new CAbilityOrcBuild(handleIdAllocator.createId(), structuresBuilt));
					break;
				// 如果是人类
				case HUMAN:
					// 添加人类建造能力
					unit.add(simulation, new CAbilityHumanBuild(handleIdAllocator.createId(), structuresBuilt));
					break;
				// 如果是不死族
				case UNDEAD:
					// 添加不死族建造能力
					unit.add(simulation, new CAbilityUndeadBuild(handleIdAllocator.createId(), structuresBuilt));
					break;
				// 如果是暗夜精灵
				case NIGHTELF:
					// 添加暗夜精灵建造能力
					unit.add(simulation, new CAbilityNightElfBuild(handleIdAllocator.createId(), structuresBuilt));
					break;
				// 如果是娜迦族
				case NAGA:
					// 添加娜迦族建造能力
					unit.add(simulation, new CAbilityNagaBuild(handleIdAllocator.createId(), structuresBuilt));
					break;
				// 如果是其他种族
				case CREEPS:
				case CRITTERS:
				case DEMON:
				case OTHER:
					// 添加兽族建造能力
					unit.add(simulation, new CAbilityOrcBuild(handleIdAllocator.createId(), structuresBuilt));
					break;
			}
		}

		// 获取单位类型实例可以训练的单位列表
		final List<War3ID> unitsTrained = unitTypeInstance.getUnitsTrained();
		// 获取单位类型实例可以研究的科技列表
		final List<War3ID> researchesAvailable = unitTypeInstance.getResearchesAvailable();
		// 获取单位类型实例可以升级的列表
		final List<War3ID> upgradesTo = unitTypeInstance.getUpgradesTo();
		// 获取单位类型实例可以出售的物品列表
		final List<War3ID> itemsSold = unitTypeInstance.getItemsSold();
		// 获取单位类型实例可以制造的物品列表
		final List<War3ID> itemsMade = unitTypeInstance.getItemsMade();

		// 如果单位类型实例可以训练单位或研究科技
		if (!unitsTrained.isEmpty() || !researchesAvailable.isEmpty()) {
			// 添加训练和研究队列能力
			unit.add(simulation, new CAbilityQueue(handleIdAllocator.createId(), unitsTrained, researchesAvailable));
		}

		// 如果单位类型实例可以升级
		if (!upgradesTo.isEmpty()) {
			// 添加升级能力
			unit.add(simulation, new CAbilityUpgrade(handleIdAllocator.createId(), upgradesTo));
		}

		// 如果单位类型实例可以出售物品
		if (!itemsSold.isEmpty()) {
			// 添加出售物品能力
			unit.add(simulation, new CAbilitySellItems(handleIdAllocator.createId(), itemsSold));
		}

		// 如果单位类型实例可以制造物品
		if (!itemsMade.isEmpty()) {
			// 添加制造物品能力
			unit.add(simulation, new CAbilitySellItems(handleIdAllocator.createId(), itemsMade));
		}

		// 如果单位类型实例可以复活英雄
		if (unitTypeInstance.isRevivesHeroes()) {
			// 添加复活英雄能力
			unit.add(simulation, new CAbilityReviveHero(handleIdAllocator.createId()));
		}

		// 如果单位类型实例可以训练单位或复活英雄
		if (!unitsTrained.isEmpty() || unitTypeInstance.isRevivesHeroes()) {
			// 添加集结点能力
			unit.add(simulation, new CAbilityRally(handleIdAllocator.createId()));
		}


		// 如果是英雄，添加英雄能力, 设置初始魔法值
		if (unitTypeInstance.isHero()) {
			final List<War3ID> heroAbilityList = unitTypeInstance.getHeroAbilityList();
			// 添加英雄能力
			unit.add(simulation, new CAbilityHero(handleIdAllocator.createId(), heroAbilityList));
			// reset initial mana after the value is adjusted for hero data
			// 在英雄数据调整值后重置初始法力值
			unit.setMana(manaInitial);
		}

		// 遍历单位类型实例的能力列表
		for (final War3ID ability : unitTypeInstance.getAbilityList()) {
			// 通过原始代码获取单位的现有能力
			final CLevelingAbility existingAbility = unit
					.getAbility(GetAbilityByRawcodeVisitor.getInstance().reset(ability));
			// 如果现有能力不存在或不是永久的
			if ((existingAbility == null) || !existingAbility.isPermanent()) {
				// 创建一个新的能力
				final CAbility createAbility = this.abilityData.createAbility(ability, handleIdAllocator.createId());
				// 如果创建的能力不为空
				if (createAbility != null) {
					// 将新能力添加到单位中
					unit.add(simulation, createAbility);
				}
				// 如果该能力是单位类型实例的默认自动施放能力，并且新创建的能力是一个自动施放能力
				if (ability.equals(unitTypeInstance.getDefaultAutocastAbility())
						&& (createAbility instanceof CAutocastAbility)) {
					// 设置自动施放能力为开启状态
					((CAutocastAbility) createAbility).setAutoCastOn(unit, true);
				}
			}
			// 如果现有能力存在且是永久的
			else {
				// 如果该能力是单位类型实例的默认自动施放能力，并且现有能力是一个自动施放能力
				if (ability.equals(unitTypeInstance.getDefaultAutocastAbility())
						&& (existingAbility instanceof CAutocastAbility)) {
					// 设置自动施放能力为开启状态
					((CAutocastAbility) existingAbility).setAutoCastOn(unit, true);
				}
			}
		}

		// 检查单位是否为英雄，并且当前游戏是否为混乱之治（Reign of Chaos）版本
		if (unitTypeInstance.isHero() && simulation.isMapReignOfChaos()
				// 检查单位是否没有物品栏能力
				&& (unit.getFirstAbilityOfType(CAbilityInventory.class) == null)) {
			// 为单位添加物品栏能力，能力 ID 为 "AInv"
			unit.add(simulation,
					simulation.getAbilityData().createAbility(War3ID.fromString("AInv"), handleIdAllocator.createId()));
		}

	}
	/**
	 * 为单位添加缺失的默认能力。
	 *
	 * @param simulation 模拟对象
	 * @param handleIdAllocator 句柄ID分配器
	 * @param unitTypeInstance 单位类型实例
	 * @param resetMana 是否重置法力值
	 * @param manaInitial 初始法力值
	 * @param speed 单位移动速度
	 * @param unit 单位对象
	 */
	public void addMissingDefaultAbilitiesToUnit(final CSimulation simulation,
			final HandleIdAllocator handleIdAllocator, final CUnitType unitTypeInstance, final boolean resetMana,
			final int manaInitial, final int speed, final CUnit unit) {
		// 获取移动能力
		final CAbilityMove preMove = unit.getFirstAbilityOfType(CAbilityMove.class);
		// 如果之前没有移动能力，限制移动速度大于0，添加移动能力
		if ((speed > 0) && (preMove == null)) {
			unit.add(simulation, new CAbilityMove(handleIdAllocator.createId()));
		}
		// 如果之前有移动能力，现在移动速度小于0, 移除移动能力
		if ((speed <= 0) && (preMove != null)) {
			unit.remove(simulation, preMove);
		}
		// 重新设置普攻列表
		final List<CUnitAttack> unitSpecificAttacks = new ArrayList<>();
		for (final CUnitAttack attack : unitTypeInstance.getAttacks()) {
			unitSpecificAttacks.add(attack.copy());
		}
		unit.setUnitSpecificAttacks(unitSpecificAttacks);
		unit.setUnitSpecificCurrentAttacks(
				getEnabledAttacks(unitSpecificAttacks, unitTypeInstance.getAttacksEnabled()));
		if (!unit.getCurrentAttacks().isEmpty()) {
			unit.add(simulation, new CAbilityAttack(handleIdAllocator.createId()));
		}

		// 获取单位类型实例可以建造的建筑列表
		final List<War3ID> structuresBuilt = unitTypeInstance.getStructuresBuilt();
		if (!structuresBuilt.isEmpty()) {
			switch (unitTypeInstance.getRace()) {
			case ORC:
				unit.add(simulation, new CAbilityOrcBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			case HUMAN:
				unit.add(simulation, new CAbilityHumanBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			case UNDEAD:
				unit.add(simulation, new CAbilityUndeadBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			case NIGHTELF:
				unit.add(simulation, new CAbilityNightElfBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			case NAGA:
				unit.add(simulation, new CAbilityNagaBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			case CREEPS:
			case CRITTERS:
			case DEMON:
			case OTHER:
				unit.add(simulation, new CAbilityOrcBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			}
		}
		// 获取单位类型实例可以训练的单位列表
		final List<War3ID> unitsTrained = unitTypeInstance.getUnitsTrained();
		// 获取单位类型实例可以研究的科技列表
		final List<War3ID> researchesAvailable = unitTypeInstance.getResearchesAvailable();
		// 获取单位类型实例可以升级到的单位列表
		final List<War3ID> upgradesTo = unitTypeInstance.getUpgradesTo();
		// 获取单位类型实例可以出售的物品列表
		final List<War3ID> itemsSold = unitTypeInstance.getItemsSold();
		// 获取单位类型实例可以制造的物品列表
		final List<War3ID> itemsMade = unitTypeInstance.getItemsMade();
		// 如果单位可以训练单位或研究科技，则添加相应的能力
		if (!unitsTrained.isEmpty() || !researchesAvailable.isEmpty()) {
			unit.add(simulation, new CAbilityQueue(handleIdAllocator.createId(), unitsTrained, researchesAvailable));
		}
		// 如果单位可以升级，则添加升级能力
		if (!upgradesTo.isEmpty()) {
			unit.add(simulation, new CAbilityUpgrade(handleIdAllocator.createId(), upgradesTo));
		}
		// 如果单位可以出售物品，则添加出售物品能力
		if (!itemsSold.isEmpty()) {
			unit.add(simulation, new CAbilitySellItems(handleIdAllocator.createId(), itemsSold));
		}
		// 如果单位可以制造物品，则添加制造物品能力
		if (!itemsMade.isEmpty()) {
			unit.add(simulation, new CAbilitySellItems(handleIdAllocator.createId(), itemsMade));
		}
		// 如果单位可以复活英雄，则添加复活英雄能力
		if (unitTypeInstance.isRevivesHeroes()) {
			unit.add(simulation, new CAbilityReviveHero(handleIdAllocator.createId()));
		}
		// 如果单位可以训练单位或复活英雄，则添加集结点能力
		if (!unitsTrained.isEmpty() || unitTypeInstance.isRevivesHeroes()) {
			unit.add(simulation, new CAbilityRally(handleIdAllocator.createId()));
		}

		// 如果是英雄
		if (unitTypeInstance.isHero()) {
			final List<War3ID> heroAbilityList = unitTypeInstance.getHeroAbilityList();
			/// 如果已经有英雄能力
			if (unit.getFirstAbilityOfType(CAbilityHero.class) != null) {
				// 重新设置英雄可用技能列表
				final CAbilityHero abil = unit.getFirstAbilityOfType(CAbilityHero.class);
				abil.setSkillsAvailable(heroAbilityList);
				abil.recalculateAllStats(simulation, unit);
			}
			else {
				// 否则 添加英雄能力
				unit.add(simulation, new CAbilityHero(handleIdAllocator.createId(), heroAbilityList));
				// reset initial mana after the value is adjusted for hero data
				unit.setMana(manaInitial);
			}
		}
		// 遍历单位类型实例的能力列表，处理能力添加和自动施法的逻辑
		for (final War3ID ability : unitTypeInstance.getAbilityList()) {
			// 获取之前是否已经添加了能力
			final CLevelingAbility existingAbility = unit
					.getAbility(GetAbilityByRawcodeVisitor.getInstance().reset(ability));
			// 之前没有添加该能力
			if ((existingAbility == null)) {
				// 创建新能力
				final CAbility createAbility = this.abilityData.createAbility(ability, handleIdAllocator.createId());
				if (createAbility != null) {
					unit.add(simulation, createAbility);
				}
				// 如果是默认自动施法能力，且创建的能力是一个自动施法能力
				if (ability.equals(unitTypeInstance.getDefaultAutocastAbility())
						&& (createAbility instanceof CAutocastAbility)) {
					((CAutocastAbility) createAbility).setAutoCastOn(unit, true);
				}
			}
			else {
				// 如果是默认自动施法能力，且之前的能力是一个自动施法能力
				if (ability.equals(unitTypeInstance.getDefaultAutocastAbility())
						&& (existingAbility instanceof CAutocastAbility)) {
					((CAutocastAbility) existingAbility).setAutoCastOn(unit, true);
				}
			}
		}


		// 如果是英雄，添加物品栏能力
		if (unitTypeInstance.isHero() && simulation.isMapReignOfChaos()
				&& (unit.getFirstAbilityOfType(CAbilityInventory.class) == null)) {
			unit.add(simulation,
					simulation.getAbilityData().createAbility(War3ID.fromString("AInv"), handleIdAllocator.createId()));
		}
	}


	private CUnitType getUnitTypeInstance(final War3ID typeId, final BufferedImage buildingPathingPixelMap,
			final GameObject unitType) {
		CUnitType unitTypeInstance = this.unitIdToUnitType.get(typeId);
		if (unitTypeInstance == null) {
			final String legacyName = getLegacyName(unitType);
			final int life = unitType.getFieldAsInteger(HIT_POINT_MAXIMUM, 0);
			final float lifeRegen = unitType.getFieldAsFloat(HIT_POINT_REGEN, 0);
			final CRegenType lifeRegenType = CRegenType
					.parseRegenType(unitType.getFieldAsString(HIT_POINT_REGEN_TYPE, 0));
			final int manaInitial = unitType.getFieldAsInteger(MANA_INITIAL_AMOUNT, 0);
			final int manaMaximum = unitType.getFieldAsInteger(MANA_MAXIMUM, 0);
			final float manaRegen = unitType.getFieldAsFloat(MANA_REGEN, 0);
			final int speed = unitType.getFieldAsInteger(MOVEMENT_SPEED_BASE, 0);
			final int defense = unitType.getFieldAsInteger(DEFENSE, 0);
			final String defaultAutocastAbility = unitType.getFieldAsString(ABILITIES_DEFAULT_AUTO, 0);
			final List<String> abilityListString = unitType.getFieldAsList(ABILITIES_NORMAL);
			final List<String> heroAbilityListString = unitType.getFieldAsList(ABILITIES_HERO);
			final int unitLevel = unitType.getFieldAsInteger(UNIT_LEVEL, 0);
			final int priority = unitType.getFieldAsInteger(PRIORITY, 0);
			final int defenseUpgradeBonus = unitType.getFieldAsInteger(DEFENSE_UPGRADE_BONUS, 0);

			final float moveHeight = unitType.getFieldAsFloat(MOVE_HEIGHT, 0);
			final String movetp = unitType.getFieldAsString(MOVE_TYPE, 0);
			final float collisionSize = unitType.getFieldAsFloat(COLLISION_SIZE, 0);
			final float propWindow = unitType.getFieldAsFloat(PROPULSION_WINDOW, 0);
			final float turnRate = unitType.getFieldAsFloat(TURN_RATE, 0);

			final boolean canFlee = unitType.getFieldAsBoolean(CAN_FLEE, 0);

			final boolean canBeBuiltOnThem = unitType.getFieldAsBoolean(CAN_BE_BUILT_ON_THEM, 0);
			final boolean canBuildOnMe = unitType.getFieldAsBoolean(CAN_BUILD_ON_ME, 0);

			final float strPlus = unitType.getFieldAsFloat(STR_PLUS, 0);
			final float agiPlus = unitType.getFieldAsFloat(AGI_PLUS, 0);
			final float intPlus = unitType.getFieldAsFloat(INT_PLUS, 0);

			final int strength = unitType.getFieldAsInteger(STR, 0);
			final int agility = unitType.getFieldAsInteger(AGI, 0);
			final int intelligence = unitType.getFieldAsInteger(INT, 0);
			final CPrimaryAttribute primaryAttribute = CPrimaryAttribute
					.parsePrimaryAttribute(unitType.getFieldAsString(PRIMARY_ATTRIBUTE, 0));

			final String properNames = unitType.getFieldAsString(PROPER_NAMES, 0);
			final int properNamesCount = unitType.getFieldAsInteger(PROPER_NAMES_COUNT, 0);

			final boolean isBldg = unitType.getFieldAsBoolean(IS_BLDG, 0);
			PathingGrid.MovementType movementType = PathingGrid.getMovementType(movetp);
			if (movementType == null) {
				movementType = MovementType.DISABLED;
			}
			final String unitName = unitType.getFieldAsString(NAME, 0);
			final float acquisitionRange = unitType.getFieldAsFloat(ACQUISITION_RANGE, 0);
			// note: uamn expected type int below, not exactly sure why that decision was
			// made but I'll support it
			final float minimumAttackRange = unitType.getFieldAsInteger(MINIMUM_ATTACK_RANGE, 0);
			final EnumSet<CTargetType> targetedAs = CTargetType
					.parseTargetTypeSet(unitType.getFieldAsList(TARGETED_AS));
			final List<String> classificationStringList = unitType.getFieldAsList(CLASSIFICATION);
			final EnumSet<CUnitClassification> classifications = EnumSet.noneOf(CUnitClassification.class);
			if (!classificationStringList.isEmpty()) {
				for (final String unitEditorKey : classificationStringList) {
					final CUnitClassification unitClassification = CUnitClassification
							.parseUnitClassification(unitEditorKey);
					if (unitClassification != null) {
						classifications.add(unitClassification);
					}
				}
			}
			final List<CUnitAttack> attacks = new ArrayList<>();
			final int attacksEnabled = unitType.getFieldAsInteger(ATTACKS_ENABLED, 0);
			try {
				// attack one
				final float animationBackswingPoint = unitType.getFieldAsFloat(ATTACK1_BACKSWING_POINT, 0);
				final float animationDamagePoint = unitType.getFieldAsFloat(ATTACK1_DAMAGE_POINT, 0);
				final int areaOfEffectFullDamage = unitType.getFieldAsInteger(ATTACK1_AREA_OF_EFFECT_FULL_DMG, 0);
				final int areaOfEffectMediumDamage = unitType.getFieldAsInteger(ATTACK1_AREA_OF_EFFECT_HALF_DMG, 0);
				final int areaOfEffectSmallDamage = unitType.getFieldAsInteger(ATTACK1_AREA_OF_EFFECT_QUARTER_DMG, 0);
				final EnumSet<CTargetType> areaOfEffectTargets = CTargetType
						.parseTargetTypeSet(unitType.getFieldAsList(ATTACK1_AREA_OF_EFFECT_TARGETS));
				final CAttackType attackType = CAttackType
						.parseAttackType(unitType.getFieldAsString(ATTACK1_ATTACK_TYPE, 0));
				final float cooldownTime = unitType.getFieldAsFloat(ATTACK1_COOLDOWN, 0);
				final int damageBase = unitType.getFieldAsInteger(ATTACK1_DMG_BASE, 0);
				final float damageFactorMedium = unitType.getFieldAsFloat(ATTACK1_DAMAGE_FACTOR_HALF, 0);
				final float damageFactorSmall = unitType.getFieldAsFloat(ATTACK1_DAMAGE_FACTOR_QUARTER, 0);
				final float damageLossFactor = unitType.getFieldAsFloat(ATTACK1_DAMAGE_LOSS_FACTOR, 0);
				final int damageDice = unitType.getFieldAsInteger(ATTACK1_DMG_DICE, 0);
				final int damageSidesPerDie = unitType.getFieldAsInteger(ATTACK1_DMG_SIDES_PER_DIE, 0);
				final float damageSpillDistance = unitType.getFieldAsFloat(ATTACK1_DMG_SPILL_DIST, 0);
				final float damageSpillRadius = unitType.getFieldAsFloat(ATTACK1_DMG_SPILL_RADIUS, 0);
				final int damageUpgradeAmount = unitType.getFieldAsInteger(ATTACK1_DMG_UPGRADE_AMT, 0);
				final int maximumNumberOfTargets = unitType.getFieldAsInteger(ATTACK1_TARGET_COUNT, 0);
				final float projectileArc = unitType.getFieldAsFloat(ATTACK1_PROJECTILE_ARC, 0);
				final String projectileArt = unitType.getFieldAsString(ATTACK1_MISSILE_ART, 0);
				final boolean projectileHomingEnabled = unitType.getFieldAsBoolean(ATTACK1_PROJECTILE_HOMING_ENABLED,
						0);
				final int projectileSpeed = unitType.getFieldAsInteger(ATTACK1_PROJECTILE_SPEED, 0);
				final int range = unitType.getFieldAsInteger(ATTACK1_RANGE, 0);
				final float rangeMotionBuffer = unitType.getFieldAsFloat(ATTACK1_RANGE_MOTION_BUFFER, 0);
				final boolean showUI = unitType.getFieldAsBoolean(ATTACK1_SHOW_UI, 0);
				final EnumSet<CTargetType> targetsAllowed = CTargetType
						.parseTargetTypeSet(unitType.getFieldAsList(ATTACK1_TARGETS_ALLOWED));
				final String weaponSound = unitType.getFieldAsString(ATTACK1_WEAPON_SOUND, 0);
				final String weapon_type_temp = unitType.getFieldAsString(ATTACK1_WEAPON_TYPE, 0);
				CWeaponType weaponType = CWeaponType.NONE;
				if (!"_".equals(weapon_type_temp)) {
					weaponType = CWeaponType.parseWeaponType(weapon_type_temp);
				}
				attacks.add(createAttack(animationBackswingPoint, animationDamagePoint, areaOfEffectFullDamage,
						areaOfEffectMediumDamage, areaOfEffectSmallDamage, areaOfEffectTargets, attackType,
						cooldownTime, damageBase, damageFactorMedium, damageFactorSmall, damageLossFactor, damageDice,
						damageSidesPerDie, damageSpillDistance, damageSpillRadius, damageUpgradeAmount,
						maximumNumberOfTargets, projectileArc, projectileArt, projectileHomingEnabled, projectileSpeed,
						range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound, weaponType));
			}
			catch (final Exception exc) {
				System.err.println("Attack 1 failed to parse with: " + exc.getClass() + ":" + exc.getMessage());
			}
			try {
				// attack two
				final float animationBackswingPoint = unitType.getFieldAsFloat(ATTACK2_BACKSWING_POINT, 0);
				final float animationDamagePoint = unitType.getFieldAsFloat(ATTACK2_DAMAGE_POINT, 0);
				final int areaOfEffectFullDamage = unitType.getFieldAsInteger(ATTACK2_AREA_OF_EFFECT_FULL_DMG, 0);
				final int areaOfEffectMediumDamage = unitType.getFieldAsInteger(ATTACK2_AREA_OF_EFFECT_HALF_DMG, 0);
				final int areaOfEffectSmallDamage = unitType.getFieldAsInteger(ATTACK2_AREA_OF_EFFECT_QUARTER_DMG, 0);
				final EnumSet<CTargetType> areaOfEffectTargets = CTargetType
						.parseTargetTypeSet(unitType.getFieldAsList(ATTACK2_AREA_OF_EFFECT_TARGETS));
				final CAttackType attackType = CAttackType
						.parseAttackType(unitType.getFieldAsString(ATTACK2_ATTACK_TYPE, 0));
				final float cooldownTime = unitType.getFieldAsFloat(ATTACK2_COOLDOWN, 0);
				final int damageBase = unitType.getFieldAsInteger(ATTACK2_DMG_BASE, 0);
				final float damageFactorMedium = unitType.getFieldAsFloat(ATTACK2_DAMAGE_FACTOR_HALF, 0);
				final float damageFactorSmall = unitType.getFieldAsFloat(ATTACK2_DAMAGE_FACTOR_QUARTER, 0);
				final float damageLossFactor = unitType.getFieldAsFloat(ATTACK2_DAMAGE_LOSS_FACTOR, 0);
				final int damageDice = unitType.getFieldAsInteger(ATTACK2_DMG_DICE, 0);
				final int damageSidesPerDie = unitType.getFieldAsInteger(ATTACK2_DMG_SIDES_PER_DIE, 0);
				final float damageSpillDistance = unitType.getFieldAsFloat(ATTACK2_DMG_SPILL_DIST, 0);
				final float damageSpillRadius = unitType.getFieldAsFloat(ATTACK2_DMG_SPILL_RADIUS, 0);
				final int damageUpgradeAmount = unitType.getFieldAsInteger(ATTACK2_DMG_UPGRADE_AMT, 0);
				final int maximumNumberOfTargets = unitType.getFieldAsInteger(ATTACK2_TARGET_COUNT, 0);
				float projectileArc = unitType.getFieldAsFloat(ATTACK2_PROJECTILE_ARC, 0);
				String projectileArt = unitType.getFieldAsString(ATTACK2_MISSILE_ART, 0);
				int projectileSpeed = unitType.getFieldAsInteger(ATTACK2_PROJECTILE_SPEED, 0);
				if ("_".equals(projectileArt) || projectileArt.isEmpty()) {
					projectileArt = unitType.getFieldAsString(ATTACK1_MISSILE_ART, 0);
					projectileSpeed = unitType.getFieldAsInteger(ATTACK1_PROJECTILE_SPEED, 0);
					projectileArc = unitType.getFieldAsFloat(ATTACK1_PROJECTILE_ARC, 0);
				}
				final boolean projectileHomingEnabled = unitType.getFieldAsBoolean(ATTACK2_PROJECTILE_HOMING_ENABLED,
						0);
				final int range = unitType.getFieldAsInteger(ATTACK2_RANGE, 0);
				final float rangeMotionBuffer = unitType.getFieldAsFloat(ATTACK2_RANGE_MOTION_BUFFER, 0);
				boolean showUI = unitType.getFieldAsBoolean(ATTACK2_SHOW_UI, 0);
				final EnumSet<CTargetType> targetsAllowed = CTargetType
						.parseTargetTypeSet(unitType.getFieldAsList(ATTACK2_TARGETS_ALLOWED));
				final String weaponSound = unitType.getFieldAsString(ATTACK2_WEAPON_SOUND, 0);
				final String weapon_type_temp = unitType.getFieldAsString(ATTACK2_WEAPON_TYPE, 0);
				CWeaponType weaponType = CWeaponType.NONE;
				if (!"_".equals(weapon_type_temp)) {
					weaponType = CWeaponType.parseWeaponType(weapon_type_temp);
				}
				if (!attacks.isEmpty()) {
					final CUnitAttack otherAttack = attacks.get(0);
					if ((otherAttack.getAttackType() == attackType) && (targetsAllowed.size() == 1)
							&& (targetsAllowed.contains(CTargetType.TREE)
									|| (targetsAllowed.contains(CTargetType.STRUCTURE)
											&& (otherAttack.getDamageBase() == damageBase)
											&& (otherAttack.getDamageSidesPerDie() == damageSidesPerDie)
											&& (otherAttack.getDamageDice() == damageDice)))) {
						showUI = false;
					}
				}
				attacks.add(createAttack(animationBackswingPoint, animationDamagePoint, areaOfEffectFullDamage,
						areaOfEffectMediumDamage, areaOfEffectSmallDamage, areaOfEffectTargets, attackType,
						cooldownTime, damageBase, damageFactorMedium, damageFactorSmall, damageLossFactor, damageDice,
						damageSidesPerDie, damageSpillDistance, damageSpillRadius, damageUpgradeAmount,
						maximumNumberOfTargets, projectileArc, projectileArt, projectileHomingEnabled, projectileSpeed,
						range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound, weaponType));
			}
			catch (final Exception exc) {
				System.err.println("Attack 2 failed to parse with: " + exc.getClass() + ":" + exc.getMessage());
			}
			final List<CUnitAttack> enabledAttacks = getEnabledAttacks(attacks, attacksEnabled);
			final int deathType = unitType.getFieldAsInteger(DEATH_TYPE, 0);
			final boolean raise = (deathType & 0x1) != 0;
			final boolean decay = (deathType & 0x2) != 0;
			final String armorType = unitType.getFieldAsString(ARMOR_TYPE, 0);
			final float impactZ = unitType.getFieldAsFloat(PROJECTILE_IMPACT_Z, 0);
			final CDefenseType defenseType = CDefenseType.parseDefenseType(unitType.getFieldAsString(DEFENSE_TYPE, 0));
			final float deathTime = unitType.getFieldAsFloat(DEATH_TIME, 0);
			final int goldCost = unitType.getFieldAsInteger(GOLD_COST, 0);
			final int lumberCost = unitType.getFieldAsInteger(LUMBER_COST, 0);
			final int buildTime = (int) Math
					.ceil(unitType.getFieldAsInteger(BUILD_TIME, 0) * WarsmashConstants.GAME_SPEED_TIME_FACTOR);
			final int foodUsed = unitType.getFieldAsInteger(FOOD_USED, 0);
			final int foodMade = unitType.getFieldAsInteger(FOOD_MADE, 0);

			final float castBackswingPoint = unitType.getFieldAsFloat(CAST_BACKSWING_POINT, 0);
			final float castPoint = unitType.getFieldAsFloat(CAST_POINT, 0);

			final int pointValue = unitType.getFieldAsInteger(POINT_VALUE, 0);

			final int sightRadiusDay = unitType.getFieldAsInteger(SIGHT_RADIUS_DAY, 0);
			final int sightRadiusNight = unitType.getFieldAsInteger(SIGHT_RADIUS_NIGHT, 0);
			final boolean extendedLineOfSight = unitType.getFieldAsBoolean(EXTENDED_LOS, 0);

			final int goldBountyAwardedBase = unitType.getFieldAsInteger(GOLD_BOUNTY_AWARDED_BASE, 0);
			final int goldBountyAwardedDice = unitType.getFieldAsInteger(GOLD_BOUNTY_AWARDED_DICE, 0);
			final int goldBountyAwardedSides = unitType.getFieldAsInteger(GOLD_BOUNTY_AWARDED_SIDES, 0);

			final int lumberBountyAwardedBase = unitType.getFieldAsInteger(LUMBER_BOUNTY_AWARDED_BASE, 0);
			final int lumberBountyAwardedDice = unitType.getFieldAsInteger(LUMBER_BOUNTY_AWARDED_DICE, 0);
			final int lumberBountyAwardedSides = unitType.getFieldAsInteger(LUMBER_BOUNTY_AWARDED_SIDES, 0);

			final boolean revivesHeroes = unitType.getFieldAsBoolean(REVIVES_HEROES, 0);

			final List<War3ID> unitsTrained = parseIDList(unitType.getFieldAsList(UNITS_TRAINED));

			final List<War3ID> upgradesTo = parseIDList(unitType.getFieldAsList(UPGRADES_TO));

			final List<War3ID> researchesAvailable = parseIDList(unitType.getFieldAsList(RESEARCHES_AVAILABLE));

			final List<War3ID> upgradesUsed = parseIDList(unitType.getFieldAsList(UPGRADES_USED));
			final EnumMap<CUpgradeClass, War3ID> upgradeClassToType = new EnumMap<>(CUpgradeClass.class);
			for (final War3ID upgradeUsed : upgradesUsed) {
				final CUpgradeType upgradeType = this.upgradeData.getType(upgradeUsed);
				if (upgradeType != null) {
					final CUpgradeClass upgradeClass = upgradeType.getUpgradeClass();
					if (upgradeClass != null) {
						upgradeClassToType.put(upgradeClass, upgradeUsed);
					}
				}
			}

			final List<War3ID> structuresBuilt = parseIDList(unitType.getFieldAsList(STRUCTURES_BUILT));

			final List<War3ID> itemsSold = parseIDList(unitType.getFieldAsList(ITEMS_SOLD));
			final List<War3ID> itemsMade = parseIDList(unitType.getFieldAsList(ITEMS_MADE));

			final War3ID defaultAutocastAbilityId;
			if ((defaultAutocastAbility != null) && !defaultAutocastAbility.isEmpty()
					&& !defaultAutocastAbility.equals("_")) {
				defaultAutocastAbilityId = War3ID.fromString(defaultAutocastAbility);
			}
			else {
				defaultAutocastAbilityId = null;
			}
			final List<War3ID> heroAbilityList = parseIDList(heroAbilityListString);
			final List<War3ID> abilityList = parseIDList(abilityListString);

			final List<String> requirementsString = unitType.getFieldAsList(REQUIRES);
			final List<String> requirementsLevelsString = unitType.getFieldAsList(REQUIRES_AMOUNT);
			final List<CUnitTypeRequirement> requirements = parseRequirements(requirementsString,
					requirementsLevelsString);
			final int requirementsTiersCount = unitType.getFieldAsInteger(REQUIRES_TIER_COUNT, 0);
			final List<List<CUnitTypeRequirement>> requirementTiers = new ArrayList<>();
			for (int i = 1; i <= requirementsTiersCount; i++) {
				final List<String> requirementsTierString = unitType.getFieldAsList(REQUIRES_TIER_X[i - 1]);
				final List<CUnitTypeRequirement> tierRequirements = parseRequirements(requirementsTierString,
						Collections.emptyList());
				requirementTiers.add(tierRequirements);
			}

			final EnumSet<CBuildingPathingType> preventedPathingTypes = CBuildingPathingType
					.parsePathingTypeListSet(unitType.getFieldAsString(PREVENT_PLACE, 0));
			final EnumSet<CBuildingPathingType> requiredPathingTypes = CBuildingPathingType
					.parsePathingTypeListSet(unitType.getFieldAsString(REQUIRE_PLACE, 0));

			final String raceString = unitType.getFieldAsString(UNIT_RACE, 0);
			final CUnitRace unitRace = CUnitRace.parseRace(raceString);

			final boolean hero = Character.isUpperCase(typeId.charAt(0));

			final List<String> heroProperNames = Arrays.asList(properNames.split(","));

			final boolean neutralBuildingShowMinimapIcon = unitType.getFieldAsBoolean(NEUTRAL_BUILDING_SHOW_ICON, 0);

			unitTypeInstance = new CUnitType(unitName, legacyName, typeId, life, lifeRegen, manaRegen, lifeRegenType,
					manaInitial, manaMaximum, speed, defense, defaultAutocastAbilityId, abilityList, isBldg,
					movementType, moveHeight, collisionSize, classifications, attacks, attacksEnabled, armorType, raise,
					decay, defenseType, impactZ, buildingPathingPixelMap, deathTime, targetedAs, acquisitionRange,
					minimumAttackRange, structuresBuilt, unitsTrained, researchesAvailable, upgradesUsed,
					upgradeClassToType, upgradesTo, itemsSold, itemsMade, unitRace, goldCost, lumberCost, foodUsed,
					foodMade, buildTime, preventedPathingTypes, requiredPathingTypes, propWindow, turnRate,
					requirements, requirementTiers, unitLevel, hero, strength, strPlus, agility, agiPlus, intelligence,
					intPlus, primaryAttribute, heroAbilityList, heroProperNames, properNamesCount, canFlee, priority,
					revivesHeroes, pointValue, castBackswingPoint, castPoint, canBeBuiltOnThem, canBuildOnMe,
					defenseUpgradeBonus, sightRadiusDay, sightRadiusNight, extendedLineOfSight, goldBountyAwardedBase,
					goldBountyAwardedDice, goldBountyAwardedSides, lumberBountyAwardedBase, lumberBountyAwardedDice,
					lumberBountyAwardedSides, neutralBuildingShowMinimapIcon);
			this.unitIdToUnitType.put(typeId, unitTypeInstance);
			this.jassLegacyNameToUnitId.put(legacyName, typeId);
		}
		return unitTypeInstance;
	}

	// 获取能用的普攻列表
	public static List<CUnitAttack> getEnabledAttacks(final List<CUnitAttack> attacks, final int attacksEnabled) {
		final List<CUnitAttack> enabledAttacks = new ArrayList<>();
		// 检测普攻1是否可用
		if ((attacksEnabled & 0x1) != 0) {
			if (attacks.size() > 0) {
				enabledAttacks.add(attacks.get(0));
			}
		}
		// 检测普攻2是否可用
		if ((attacksEnabled & 0x2) != 0) {
			if (attacks.size() > 1) {
				enabledAttacks.add(attacks.get(1));
			}
		}
		return enabledAttacks;
	}
	// 解析建筑物ID列表
	public static List<War3ID> parseIDList(final List<String> structuresBuiltString) {
		final List<War3ID> structuresBuilt = new ArrayList<>();
		for (final String structuresBuiltStringItem : structuresBuiltString) {
			if (structuresBuiltStringItem.length() == 4) {
				structuresBuilt.add(War3ID.fromString(structuresBuiltStringItem));
			}
		}
		return structuresBuilt;
	}

	// 解析建筑物ID集合
	public static Set<War3ID> parseIDSet(final List<String> structuresBuiltString) {
		final Set<War3ID> structuresBuilt = new HashSet<>();
		for (final String structuresBuiltStringItem : structuresBuiltString) {
			if (structuresBuiltStringItem.length() == 4) {
				structuresBuilt.add(War3ID.fromString(structuresBuiltStringItem));
			}
		}
		return structuresBuilt;
	}

	// 解析单位类型需求
	public static List<CUnitTypeRequirement> parseRequirements(final List<String> requirementsString,
			final List<String> requirementsLevelsString) {
		final List<CUnitTypeRequirement> requirements = new ArrayList<>(); // 初始化要求列表
		for (int i = 0; i < requirementsString.size(); i++) { // 遍历要求字符串列表
		  final String item = requirementsString.get(i); // 获取当前项
		  if (!item.isEmpty() && (item.length() == 4)) { // 检查项是否非空且长度为4
			  int level; // 初始化等级变量
			  if (i < requirementsLevelsString.size()) { // 检查等级字符串列表是否有对应的项
				  if (requirementsLevelsString.get(i).isEmpty()) { // 如果等级字符串为空，则等级默认为1
					  level = 1;
				  } else {
					  try { // 尝试解析等级字符串为整数
						  level = Integer.parseInt(requirementsLevelsString.get(i));
					  } catch (final NumberFormatException exc) { // 如果解析失败，则等级默认为1
						  level = 1;
					  }
				  }
			  } else if (requirementsLevelsString.size() > 0) { // 如果等级字符串列表非空但当前索引超出范围
				  final String requirementLevel = requirementsLevelsString.get(requirementsLevelsString.size() - 1); // 获取最后一个等级字符串
				  if (requirementLevel.isEmpty()) { // 如果最后一个等级字符串为空，则等级默认为1
					  level = 1;
				  } else {
					  try { // 尝试解析最后一个等级字符串为整数
						  level = Integer.parseInt(requirementLevel);
					  } catch (final NumberFormatException exc) { // 如果解析失败，则等级默认为1
						  level = 1;
					  }
				  }
			  } else { // 如果等级字符串列表为空
				  level = 1; // 等级默认为1
			  }
			  requirements.add(new CUnitTypeRequirement(War3ID.fromString(item), level)); // 创建新的要求对象并添加到列表中
		  }
		}
		return requirements; // 返回要求列表

	}

	// 获取单位类型的遗留名称
	private String getLegacyName(final GameObject unitType) {
		return unitType.getLegacyName();
	}

	// 填充英雄属性表
	private static int[] populateHeroStatTable(final int maxHeroLevel, final float statPerLevel) {
		final int[] table = new int[maxHeroLevel];
		float sumBonusAtLevel = 0f;
		for (int i = 0; i < table.length; i++) {
			final float newSumBonusAtLevel = sumBonusAtLevel + statPerLevel;
			if (i == 0) {
				table[i] = (int) newSumBonusAtLevel;
			}
			else {
				table[i] = (int) newSumBonusAtLevel - table[i - 1];
			}
			sumBonusAtLevel = newSumBonusAtLevel;
		}
		return table;
	}

	private CUnitAttack createAttack(final float animationBackswingPoint, final float animationDamagePoint,
			final int areaOfEffectFullDamage, final int areaOfEffectMediumDamage, final int areaOfEffectSmallDamage,
			final EnumSet<CTargetType> areaOfEffectTargets, final CAttackType attackType, final float cooldownTime,
			final int damageBase, final float damageFactorMedium, final float damageFactorSmall,
			final float damageLossFactor, final int damageDice, final int damageSidesPerDie,
			final float damageSpillDistance, final float damageSpillRadius, final int damageUpgradeAmount,
			final int maximumNumberOfTargets, final float projectileArc, final String projectileArt,
			final boolean projectileHomingEnabled, final int projectileSpeed, final int range,
			final float rangeMotionBuffer, final boolean showUI, final EnumSet<CTargetType> targetsAllowed,
			final String weaponSound, final CWeaponType weaponType) {
		final CUnitAttack attack;
		switch (weaponType) {
		case MISSILE: // 箭矢
			attack = new CUnitAttackMissile(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime,
					damageBase, damageDice, damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI,
					targetsAllowed, weaponSound, weaponType, projectileArc, projectileArt, projectileHomingEnabled,
					projectileSpeed);
			break;
		case MBOUNCE: // 箭矢(弹射)
			attack = new CUnitAttackMissileBounce(animationBackswingPoint, animationDamagePoint, attackType,
					cooldownTime, damageBase, damageDice, damageSidesPerDie, damageUpgradeAmount, range,
					rangeMotionBuffer, showUI, targetsAllowed, weaponSound, weaponType, projectileArc, projectileArt,
					projectileHomingEnabled, projectileSpeed, damageLossFactor, maximumNumberOfTargets,
					areaOfEffectFullDamage, areaOfEffectTargets);
			break;
		case MSPLASH: // 箭矢(溅射)
		case ARTILLERY: // 炮火
			attack = new CUnitAttackMissileSplash(animationBackswingPoint, animationDamagePoint, attackType,
					cooldownTime, damageBase, damageDice, damageSidesPerDie, damageUpgradeAmount, range,
					rangeMotionBuffer, showUI, targetsAllowed, weaponSound, weaponType, projectileArc, projectileArt,
					projectileHomingEnabled, projectileSpeed, areaOfEffectFullDamage, areaOfEffectMediumDamage,
					areaOfEffectSmallDamage, areaOfEffectTargets, damageFactorMedium, damageFactorSmall);
			break;
		case MLINE: // 箭矢(穿透)
		case ALINE: // 炮灰(穿透)
			attack = new CUnitAttackMissileLine(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime,
					damageBase, damageDice, damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI,
					targetsAllowed, weaponSound, weaponType, projectileArc, projectileArt, projectileHomingEnabled,
					projectileSpeed, damageSpillDistance, damageSpillRadius);
			break;
		case INSTANT: // 立即
			attack = new CUnitAttackInstant(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime,
					damageBase, damageDice, damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI,
					targetsAllowed, weaponSound, weaponType, projectileArt);
			break;
		default:
		case NORMAL: // 近战
			attack = new CUnitAttackNormal(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime,
					damageBase, damageDice, damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI,
					targetsAllowed, weaponSound, weaponType);
			break;
		}
		return attack;
	}

	// 获取单位推进窗口
	public float getPropulsionWindow(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(PROPULSION_WINDOW, 0);
	}

	// 获取单位转向速率
	public float getTurnRate(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(TURN_RATE, 0);
	}

	// 判断单位是否是建筑
	public boolean isBuilding(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsBoolean(IS_BLDG, 0);
	}

	// 获取单位名称
	public String getName(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getName();
	}

	// 获取单位的最小攻击1伤害
	public int getA1MinDamage(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK1_DMG_BASE, 0)
				+ this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK1_DMG_DICE, 0);
	}

	// 获取单位的最大攻击1伤害
	public int getA1MaxDamage(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK1_DMG_BASE, 0)
				+ (this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK1_DMG_DICE, 0) * this.unitData
						.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK1_DMG_SIDES_PER_DIE, 0));
	}

	// 获取单位的最小攻击2伤害
	public int getA2MinDamage(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK2_DMG_BASE, 0)
				+ this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK2_DMG_DICE, 0);
	}

	// 获取单位的最大攻击2伤害
	public int getA2MaxDamage(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK2_DMG_BASE, 0)
				+ (this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK2_DMG_DICE, 0) * this.unitData
						.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK2_DMG_SIDES_PER_DIE, 0));
	}

	// 获取单位防御
	public int getDefense(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(DEFENSE, 0);
	}

	// 获取单位攻击1的投射物速度
	public int getA1ProjectileSpeed(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK1_PROJECTILE_SPEED, 0);
	}

	// 获取单位攻击1的投射物弧度
	public float getA1ProjectileArc(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(ATTACK1_PROJECTILE_ARC, 0);
	}

	// 获取单位攻击2的投射物速度
	public int getA2ProjectileSpeed(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK2_PROJECTILE_SPEED, 0);
	}

	// 获取单位攻击2的投射物弧度
	public float getA2ProjectileArc(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(ATTACK2_PROJECTILE_ARC, 0);
	}

	// 获取单位攻击1的导弹艺术
	public String getA1MissileArt(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsString(ATTACK1_MISSILE_ART, 0);
	}

	// 获取单位攻击2的导弹艺术
	public String getA2MissileArt(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsString(ATTACK2_MISSILE_ART, 0);
	}

	// 获取单位攻击1的冷却时间
	public float getA1Cooldown(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(ATTACK1_COOLDOWN, 0);
	}

	// 获取单位攻击2的冷却时间
	public float getA2Cooldown(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(ATTACK2_COOLDOWN, 0);
	}

	// 获取单位投射物发射X坐标
	public float getProjectileLaunchX(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(PROJECTILE_LAUNCH_X, 0);
	}

	// 获取单位投射物发射Y坐标
	public float getProjectileLaunchY(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(PROJECTILE_LAUNCH_Y, 0);
	}

	// 获取单位投射物发射Z坐标
	public float getProjectileLaunchZ(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(PROJECTILE_LAUNCH_Z, 0);
	}

	// 获取单位类型
	public CUnitType getUnitType(final War3ID rawcode) {
		final CUnitType unitTypeInstance = this.unitIdToUnitType.get(rawcode);
		if (unitTypeInstance != null) {
			return unitTypeInstance;
		}
		final GameObject unitType = this.unitData.get(rawcode.asStringValue());
		if (unitType == null) {
			return null;
		}
		final BufferedImage buildingPathingPixelMap = this.simulationRenderController
				.getBuildingPathingPixelMap(rawcode);
		return getUnitTypeInstance(rawcode, buildingPathingPixelMap, unitType);
	}

	// 通过Jass遗留名称获取单位类型
	public CUnitType getUnitTypeByJassLegacyName(final String jassLegacyName) {
		final War3ID typeId = this.jassLegacyNameToUnitId.get(jassLegacyName);
		if (typeId == null) {
			// VERY inefficient, but this is a crazy system anyway, they should not be using
			// this!
			System.err.println(
					"We are doing a highly inefficient lookup for a non-cached unit type based on its legacy string ID that I am pretty sure is not used by modding community: "
							+ jassLegacyName);
			for (final String key : this.unitData.keySet()) {
				final GameObject gameObject = this.unitData.get(key);
				if (jassLegacyName.equals(getLegacyName(gameObject).toLowerCase())) {
					return getUnitType(War3ID.fromString(gameObject.getId()));
				}
			}
		}
		return getUnitType(typeId);
	}

}
