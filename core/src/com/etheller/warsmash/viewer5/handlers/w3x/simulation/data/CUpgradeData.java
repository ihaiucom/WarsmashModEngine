package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CGameplayConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitTypeRequirement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CUpgradeClass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectAttackDamage;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectAttackDice;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectAttackRange;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectAttackSpeed;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectDefenseUpgradeBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectHitPointRegen;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectHitPoints;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectHitPointsPcnt;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectManaPoints;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectManaPointsPcnt;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectManaRegen;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectMovementSpeed;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectMovementSpeedPcnt;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectSpellLevel;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectTechMaxAllowed;

public class CUpgradeData {
	private static final String APPLIES_TO_ALL_UNITS = "global"; // replaced from 'glob'
	private static final String CLASS = "class"; // replaced from 'gcls'
	private static final String GOLD_BASE = "goldbase"; // replaced from 'gglb'
	private static final String GOLD_INCREMENT = "goldmod"; // replaced from 'gglm'
	private static final String LEVELS = "maxlevel"; // replaced from 'glvl'
	private static final String LUMBER_BASE = "lumberbase"; // replaced from 'glmb'
	private static final String LUMBER_INCREMENT = "lumbermod"; // replaced from 'glmm'
	private static final String RACE = "race"; // replaced from 'grac'
	private static final String TIME_BASE = "timebase"; // replaced from 'gtib'
	private static final String TIME_INCREMENT = "timemod"; // replaced from 'gtim'
	private static final String TRANSFER_WITH_UNIT_OWNERSHIP = "inherit"; // replaced from 'ginh'
	private static final String REQUIREMENTS = "Requires"; // replaced from 'greq'
	private static final String REQUIREMENTS_LEVELS = "Requiresamount"; // replaced from 'grqc'
	private static final String NAME = "Name"; // replaced from 'gnam'

	private static final String[] EFFECT = { "effect1", "effect2", // replaced from 'gef1'
			"effect3", "effect4", }; // replaced from 'gef3'
	private static final String[] EFFECT_BASE = { "base1", "base2", // replaced from 'gba1'
			"base3", "base4", }; // replaced from 'gba3'
	private static final String[] EFFECT_MOD = { "mod1", "mod2", // replaced from 'gmo1'
			"mod3", "mod4", }; // replaced from 'gmo3'
	private static final String[] EFFECT_CODE = { "code1", "code2", // replaced from 'gco1'
			"code3", "code4", }; // replaced from 'gco3'

	private final CGameplayConstants gameplayConstants;
	private final ObjectData upgradeData;
	private final DataTable standardUpgradeEffectMeta;
	private final Map<War3ID, CUpgradeType> idToType = new HashMap<>();

	public CUpgradeData(final CGameplayConstants gameplayConstants, final ObjectData upgradeData,
			final DataTable standardUpgradeEffectMeta) {
		this.gameplayConstants = gameplayConstants;
		this.upgradeData = upgradeData;
		this.standardUpgradeEffectMeta = standardUpgradeEffectMeta;
	}

	public CUpgradeType getType(final War3ID typeId) {
		final GameObject upgradeType = this.upgradeData.get(typeId);
		if (upgradeType == null) {
			return null;
		}
		return getUpgradeTypeInstance(typeId, upgradeType);
	}

	private CUpgradeType getUpgradeTypeInstance(final War3ID typeId, final GameObject upgradeType) {
		CUpgradeType upgradeTypeInstance = this.idToType.get(typeId);
		if (upgradeTypeInstance == null) {
			final List<CUpgradeEffect> upgradeEffects = new ArrayList<>();
			for (int i = 0; i < EFFECT.length; i++) {
				final String effectMetaKey = EFFECT[i];
				final String effectBaseMetaKey = EFFECT_BASE[i];
				final String effectModMetaKey = EFFECT_MOD[i];
				final String effectCodeMetaKey = EFFECT_CODE[i];

				/* This effectId defines what type of benefit the upgrade will provide */
				final String effectIdString = upgradeType.getFieldAsString(effectMetaKey, 0);
				if (effectIdString.length() == 4) {
					final War3ID effectId = War3ID.fromString(effectIdString);
					// NOTE: maybe a string switch is not performant, if it's a problem maybe change
					// it later but the syntax is pretty nice and the calculation is cached and only
					// runs once per upgrade
					switch (effectId.toString()) {
					case "ratd":
						upgradeEffects
								.add(new CUpgradeEffectAttackDice(upgradeType.getFieldAsInteger(effectBaseMetaKey, 0),
										upgradeType.getFieldAsInteger(effectModMetaKey, 0)));
						break;
					case "rlev":
						final String spellIdField = upgradeType.getFieldAsString(effectCodeMetaKey, 0);
						if (spellIdField.length() == 4) {
							upgradeEffects.add(
									new CUpgradeEffectSpellLevel(upgradeType.getFieldAsInteger(effectBaseMetaKey, 0),
											upgradeType.getFieldAsInteger(effectModMetaKey, 0),
											War3ID.fromString(spellIdField)));
						}
						break;
					case "rhpx":
						upgradeEffects
								.add(new CUpgradeEffectHitPoints(upgradeType.getFieldAsInteger(effectBaseMetaKey, 0),
										upgradeType.getFieldAsInteger(effectModMetaKey, 0)));
						break;
					case "rmnx":
						upgradeEffects
								.add(new CUpgradeEffectManaPoints(upgradeType.getFieldAsInteger(effectBaseMetaKey, 0),
										upgradeType.getFieldAsInteger(effectModMetaKey, 0)));
						break;
					case "rmvx":
						upgradeEffects.add(
								new CUpgradeEffectMovementSpeed(upgradeType.getFieldAsInteger(effectBaseMetaKey, 0),
										upgradeType.getFieldAsInteger(effectModMetaKey, 0)));
						break;
					case "rmnr":
						upgradeEffects
								.add(new CUpgradeEffectManaRegen(upgradeType.getFieldAsFloat(effectBaseMetaKey, 0),
										upgradeType.getFieldAsFloat(effectModMetaKey, 0)));
						break;
					case "rhpo":
						upgradeEffects
								.add(new CUpgradeEffectHitPointsPcnt(upgradeType.getFieldAsFloat(effectBaseMetaKey, 0),
										upgradeType.getFieldAsFloat(effectModMetaKey, 0)));
						break;
					case "rman":
						upgradeEffects
								.add(new CUpgradeEffectManaPointsPcnt(upgradeType.getFieldAsFloat(effectBaseMetaKey, 0),
										upgradeType.getFieldAsFloat(effectModMetaKey, 0)));
						break;
					case "rmov":
						upgradeEffects.add(
								new CUpgradeEffectMovementSpeedPcnt(upgradeType.getFieldAsFloat(effectBaseMetaKey, 0),
										upgradeType.getFieldAsFloat(effectModMetaKey, 0)));
						break;
					case "ratx":
						upgradeEffects
								.add(new CUpgradeEffectAttackDamage(upgradeType.getFieldAsInteger(effectBaseMetaKey, 0),
										upgradeType.getFieldAsInteger(effectModMetaKey, 0)));
						break;
					case "ratr":
						upgradeEffects
								.add(new CUpgradeEffectAttackRange(upgradeType.getFieldAsInteger(effectBaseMetaKey, 0),
										upgradeType.getFieldAsInteger(effectModMetaKey, 0)));
						break;
					case "rats":
						upgradeEffects
								.add(new CUpgradeEffectAttackSpeed(upgradeType.getFieldAsFloat(effectBaseMetaKey, 0),
										upgradeType.getFieldAsFloat(effectModMetaKey, 0)));
						break;
					case "rhpr":
						upgradeEffects
								.add(new CUpgradeEffectHitPointRegen(upgradeType.getFieldAsFloat(effectBaseMetaKey, 0),
										upgradeType.getFieldAsFloat(effectModMetaKey, 0)));
						break;
					case "rtma":
						upgradeEffects.add(
								new CUpgradeEffectTechMaxAllowed(upgradeType.getFieldAsInteger(effectBaseMetaKey, 0),
										War3ID.fromString(upgradeType.getFieldAsString(effectCodeMetaKey, 0))));
						break;
					case "rarm":
						upgradeEffects.add(new CUpgradeEffectDefenseUpgradeBonus());
						break;
					default:
						System.err.println("No such UpgradeEffect: " + effectIdString);
						break;
					}
				}
				else {
					if (!"_".equals(effectIdString)) {
						System.err.println("Not 4 len: " + effectIdString);
					}
				}
			}
			// 检查升级是否适用于所有单位
			final boolean appliesToAllUnits = upgradeType.getFieldAsBoolean(APPLIES_TO_ALL_UNITS, 0);

			// 获取升级的类别字符串
			final String classString = upgradeType.getFieldAsString(CLASS, 0);
			// 解析升级类别字符串为 CUpgradeClass 枚举类型
			final CUpgradeClass upgradeClass = CUpgradeClass.parseUpgradeClass(classString);

			// 获取升级的基础金币成本
			final int goldBase = upgradeType.getFieldAsInteger(GOLD_BASE, 0);
			// 获取升级的金币成本增量
			final int goldIncrement = upgradeType.getFieldAsInteger(GOLD_INCREMENT, 0);

			// 获取升级的最大等级
			final int levelCount = upgradeType.getFieldAsInteger(LEVELS, 0);

			// 获取升级的基础木材成本
			final int lumberBase = upgradeType.getFieldAsInteger(LUMBER_BASE, 0);
			// 获取升级的木材成本增量
			final int lumberIncrement = upgradeType.getFieldAsInteger(LUMBER_INCREMENT, 0);

			// 获取升级适用的种族字符串
			final String raceString = upgradeType.getFieldAsString(RACE, 0);
			// 解析种族字符串为 CUnitRace 枚举类型
			final CUnitRace unitRace = CUnitRace.parseRace(raceString);

			// 获取升级的基础时间成本（以毫秒为单位），并根据游戏速度进行调整
			final int timeBase = (int) Math
					.ceil(upgradeType.getFieldAsInteger(TIME_BASE, 0) * WarsmashConstants.GAME_SPEED_TIME_FACTOR);
			// 获取升级的时间成本增量（以毫秒为单位），并根据游戏速度进行调整
			final int timeIncrement = (int) Math
					.ceil(upgradeType.getFieldAsInteger(TIME_INCREMENT, 0) * WarsmashConstants.GAME_SPEED_TIME_FACTOR);

			// 检查升级是否随单位所有权转移
			final boolean transferWithUnitOwnership = upgradeType.getFieldAsBoolean(TRANSFER_WITH_UNIT_OWNERSHIP, 0);

			// 创建一个列表来存储升级的各个等级
			final List<CUpgradeType.UpgradeLevel> upgradeLevels = new ArrayList<>();
			// 遍历升级的每个等级
			for (int i = 0; i < levelCount; i++) {
				// 初始化后缀字符串
				String suffix = "";
				// 如果不是第一级，则添加后缀
				if (i > 0) {
					suffix = Integer.toString(i);
				}
				// 获取当前等级的需求列表
				final List<String> requirementsTierString = upgradeType.getFieldAsList(REQUIREMENTS + suffix);
				// 获取当前等级的需求数量列表
				final List<String> requirementsLevelsString = upgradeType.getFieldAsList(REQUIREMENTS_LEVELS + suffix);
				// 解析需求字符串和数量字符串为 CUnitTypeRequirement 列表
				final List<CUnitTypeRequirement> tierRequirements = CUnitData.parseRequirements(requirementsTierString,
						requirementsLevelsString);
				// 获取当前等级的名称
				final String levelName = upgradeType.getFieldAsString(NAME, i);
				// 将当前等级的名称和需求添加到升级等级列表中
				upgradeLevels.add(new CUpgradeType.UpgradeLevel(levelName, tierRequirements));
			}

			// 创建一个新的 CUpgradeType 实例，包含所有收集到的信息
			upgradeTypeInstance = new CUpgradeType(typeId, upgradeEffects, appliesToAllUnits, upgradeClass, goldBase,
					goldIncrement, levelCount, lumberBase, lumberIncrement, unitRace, timeBase, timeIncrement,
					transferWithUnitOwnership, upgradeLevels);
			// 将新创建的 CUpgradeType 实例添加到 idToType 映射中，以便后续快速访问
			this.idToType.put(typeId, upgradeTypeInstance);

		}
		return upgradeTypeInstance;
	}

}
