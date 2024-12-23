package com.etheller.warsmash.parsers.w3x.objectdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.StandardObjectData;
import com.etheller.warsmash.units.StandardObjectData.WarcraftData;
import com.etheller.warsmash.units.collapsed.CollapsedObjectData;
import com.etheller.warsmash.units.custom.ObjectDataChangeEntry;
import com.etheller.warsmash.units.custom.WTS;
import com.etheller.warsmash.units.custom.WTSFile;
import com.etheller.warsmash.units.custom.War3ObjectDataChangeset;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;
import com.google.common.io.LittleEndianDataInputStream;
/**
 * Warcraft3MapRuntimeObjectData 类表示 Warcraft 3 地图的运行时对象数据。
 */
public final class Warcraft3MapRuntimeObjectData {
	private final ObjectData units; // 单位数据
	private final ObjectData items; // 物品数据
	private final ObjectData destructibles; // 可破坏对象数据
	private final ObjectData doodads; // 装饰对象数据
	private final ObjectData abilities; // 能力数据
	private final ObjectData buffs; // 增益效果数据
	private final ObjectData upgrades; // 升级数据
	private final DataTable standardUpgradeEffectMeta; // 标准升级效果元数据
	private final List<ObjectData> datas; // 对象数据列表
	private transient Map<WorldEditorDataType, ObjectData> typeToData = new HashMap<>(); // 数据类型映射
	private final WTS wts; // WTS 数据

	/**
	 * 构造函数，初始化 Warcraft3MapRuntimeObjectData 对象。
	 *
	 * @param units 单位数据
	 * @param items 物品数据
	 * @param destructibles 可破坏对象数据
	 * @param doodads 装饰对象数据
	 * @param abilities 能力数据
	 * @param buffs 增益效果数据
	 * @param upgrades 升级数据
	 * @param standardUpgradeEffectMeta 标准升级效果元数据
	 * @param wts WTS 数据
	 */
	public Warcraft3MapRuntimeObjectData(final ObjectData units, final ObjectData items, final ObjectData destructibles,
			final ObjectData doodads, final ObjectData abilities, final ObjectData buffs, final ObjectData upgrades,
			final DataTable standardUpgradeEffectMeta, final WTS wts) {
		this.units = units;
		this.items = items;
		this.destructibles = destructibles;
		this.doodads = doodads;
		this.abilities = abilities;
		this.buffs = buffs;
		this.upgrades = upgrades;
		this.standardUpgradeEffectMeta = standardUpgradeEffectMeta;
		this.datas = new ArrayList<>();
		this.datas.add(units);
		this.typeToData.put(WorldEditorDataType.UNITS, units);
		this.datas.add(items);
		this.typeToData.put(WorldEditorDataType.ITEM, items);
		this.datas.add(destructibles);
		this.typeToData.put(WorldEditorDataType.DESTRUCTIBLES, destructibles);
		this.datas.add(doodads);
		this.typeToData.put(WorldEditorDataType.DOODADS, doodads);
		this.datas.add(abilities);
		this.typeToData.put(WorldEditorDataType.ABILITIES, abilities);
		this.datas.add(buffs);
		this.typeToData.put(WorldEditorDataType.BUFFS_EFFECTS, buffs);
		this.datas.add(upgrades);
		this.typeToData.put(WorldEditorDataType.UPGRADES, upgrades);
		for (final ObjectData data : this.datas) {
		}
		this.wts = wts;
	}

	/**
	 * 根据数据类型获取相应的数据。
	 *
	 * @param type 数据类型
	 * @return 对应类型的数据
	 */
	public ObjectData getDataByType(final WorldEditorDataType type) {
		return this.typeToData.get(type);
	}

	/**
	 * 获取单位数据。
	 *
	 * @return 单位数据
	 */
	public ObjectData getUnits() {
		return this.units;
	}

	/**
	 * 获取物品数据。
	 *
	 * @return 物品数据
	 */
	public ObjectData getItems() {
		return this.items;
	}

	/**
	 * 获取可破坏对象数据。
	 *
	 * @return 可破坏对象数据
	 */
	public ObjectData getDestructibles() {
		return this.destructibles;
	}

	/**
	 * 获取装饰对象数据。
	 *
	 * @return 装饰对象数据
	 */
	public ObjectData getDoodads() {
		return this.doodads;
	}

	/**
	 * 获取能力数据。
	 *
	 * @return 能力数据
	 */
	public ObjectData getAbilities() {
		return this.abilities;
	}

	/**
	 * 获取增益效果数据。
	 *
	 * @return 增益效果数据
	 */
	public ObjectData getBuffs() {
		return this.buffs;
	}

	/**
	 * 获取升级数据。
	 *
	 * @return 升级数据
	 */
	public ObjectData getUpgrades() {
		return this.upgrades;
	}

	/**
	 * 获取标准升级效果元数据。
	 *
	 * @return 标准升级效果元数据
	 */
	public DataTable getStandardUpgradeEffectMeta() {
		return this.standardUpgradeEffectMeta;
	}

	/**
	 * 获取对象数据列表。
	 *
	 * @return 对象数据列表
	 */
	public List<ObjectData> getDatas() {
		return this.datas;
	}

	/**
	 * 获取 WTS 数据。
	 *
	 * @return WTS 数据
	 */
	public WTS getWts() {
		return this.wts;
	}

	/**
	 * 从数据源加载 WTS 数据。
	 *
	 * @param dataSource 数据源
	 * @param name 文件名
	 * @return WTS 数据
	 * @throws IOException 读取文件时出现的异常
	 */
	private static WTS loadWTS(final DataSource dataSource, final String name) throws IOException {
		final WTS wts = dataSource.has(name) ? new WTSFile(dataSource.getResourceAsStream(name)) : WTS.DO_NOTHING;
		return wts;
	}

	/**
	 * 从数据源加载默认的 WTS 数据。
	 *
	 * @param dataSource 数据源
	 * @return WTS 数据
	 * @throws IOException 读取文件时出现的异常
	 */
	public static WTS loadWTS(final DataSource dataSource) throws IOException {
		return loadWTS(dataSource, "war3map.wts");
	}

	/**
	 * 从数据源加载战役的 WTS 数据。
	 *
	 * @param dataSource 数据源
	 * @return WTS 数据
	 * @throws IOException 读取文件时出现的异常
	 */
	public static WTS loadCampaignWTS(final DataSource dataSource) throws IOException {
		return loadWTS(dataSource, "war3campaign.wts");
	}

	/**
	 * 从数据源加载 Warcraft3MapRuntimeObjectData。
	 *
	 * @param dataSource 数据源
	 * @param inlineWTS 是否内联 WTS
	 * @return Warcraft3MapRuntimeObjectData 对象
	 * @throws IOException 读取文件时出现的异常
	 */
	public static Warcraft3MapRuntimeObjectData load(final DataSource dataSource, final boolean inlineWTS)
			throws IOException {
		final WTS wts = loadWTS(dataSource);
		return load(dataSource, inlineWTS, wts);
	}

	/**
	 * 从数据源加载 Warcraft3MapRuntimeObjectData。
	 *
	 * @param dataSource 数据源
	 * @param inlineWTS 是否内联 WTS
	 * @param wts WTS 数据
	 * @return Warcraft3MapRuntimeObjectData 对象
	 * @throws IOException 读取文件时出现的异常
	 */
	public static Warcraft3MapRuntimeObjectData load(final DataSource dataSource, final boolean inlineWTS,
			final WTS wts) throws IOException {
		final WTS campaignWTS = loadCampaignWTS(dataSource);
		return load(dataSource, inlineWTS, wts, campaignWTS);
	}

	/**
	 * 从数据源加载 Warcraft3MapRuntimeObjectData。
	 *
	 * @param dataSource 数据源
	 * @param inlineWTS 是否内联 WTS
	 * @param wts WTS 数据
	 * @param campaignWTS 战役 WTS 数据
	 * @return Warcraft3MapRuntimeObjectData 对象
	 * @throws IOException 读取文件时出现的异常
	 */
	public static Warcraft3MapRuntimeObjectData load(final DataSource dataSource, final boolean inlineWTS,
			final WTS wts, final WTS campaignWTS) throws IOException {

		final StandardObjectData standardObjectData = new StandardObjectData(dataSource);
		final WarcraftData standardUnits = standardObjectData.getStandardUnits();
		final WarcraftData standardItems = standardObjectData.getStandardItems();
		final WarcraftData standardDoodads = standardObjectData.getStandardDoodads();
		final WarcraftData standardDestructables = standardObjectData.getStandardDestructables();
		final WarcraftData abilities = standardObjectData.getStandardAbilities();
		final WarcraftData standardAbilityBuffs = standardObjectData.getStandardAbilityBuffs();
		final WarcraftData standardUpgrades = standardObjectData.getStandardUpgrades();

		final DataTable standardUnitMeta = standardObjectData.getStandardUnitMeta();
		final DataTable standardDoodadMeta = standardObjectData.getStandardDoodadMeta();
		final DataTable standardDestructableMeta = standardObjectData.getStandardDestructableMeta();
		final DataTable abilityMeta = standardObjectData.getStandardAbilityMeta();
		final DataTable standardAbilityBuffMeta = standardObjectData.getStandardAbilityBuffMeta();
		final DataTable standardUpgradeMeta = standardObjectData.getStandardUpgradeMeta();
		final DataTable standardUpgradeEffectMeta = standardObjectData.getStandardUpgradeEffectMeta();

		final War3ObjectDataChangeset unitChangeset = new War3ObjectDataChangeset('u');
		final War3ObjectDataChangeset itemChangeset = new War3ObjectDataChangeset('t');
		final War3ObjectDataChangeset doodadChangeset = new War3ObjectDataChangeset('d');
		final War3ObjectDataChangeset destructableChangeset = new War3ObjectDataChangeset('b');
		final War3ObjectDataChangeset abilityChangeset = new War3ObjectDataChangeset('a');
		final War3ObjectDataChangeset buffChangeset = new War3ObjectDataChangeset('h');
		final War3ObjectDataChangeset upgradeChangeset = new War3ObjectDataChangeset('q');

		if (dataSource.has("war3map.w3u")) {
			unitChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3map.w3u")), wts,
					inlineWTS);
			// push unit changes to items.... as a Reign of Chaos support...
			Iterator<Entry<War3ID, ObjectDataChangeEntry>> entryIterator = unitChangeset.getOriginal().iterator();
			while (entryIterator.hasNext()) {
				final Entry<War3ID, ObjectDataChangeEntry> entry = entryIterator.next();
				final String rawcodeString = entry.toString();
				final String oldIdString = entry.getValue().getOldId().toString();
				if ((standardUnits.get(oldIdString) == null) && (standardItems.get(oldIdString) != null)) {
					itemChangeset.getOriginal().put(entry.getKey(), entry.getValue());
					entryIterator.remove();
				}
			}
			entryIterator = unitChangeset.getCustom().iterator();
			while (entryIterator.hasNext()) {
				final Entry<War3ID, ObjectDataChangeEntry> entry = entryIterator.next();
				final String rawcodeString = entry.toString();
				final String oldIdString = entry.getValue().getOldId().toString();
				if ((standardUnits.get(oldIdString) == null) && (standardItems.get(oldIdString) != null)) {
					itemChangeset.getCustom().put(entry.getKey(), entry.getValue());
					entryIterator.remove();
				}
			}
		}
		if (dataSource.has("war3mapSkin.w3u")) {
			unitChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3mapSkin.w3u")), wts,
					inlineWTS);
		}
		if (dataSource.has("war3campaign.w3u")) {
			unitChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3campaign.w3u")),
					campaignWTS, inlineWTS);
		}
		// ================== REMOVE LATER =====================
		if (dataSource.has("war3mod.w3u")) {
			unitChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3mod.w3u")), wts,
					inlineWTS);
		}
		// =====================================================
		if (dataSource.has("war3map.w3t")) {
			itemChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3map.w3t")), wts,
					inlineWTS);
		}
		if (dataSource.has("war3mapSkin.w3t")) {
			itemChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3mapSkin.w3t")), wts,
					inlineWTS);
		}
		if (dataSource.has("war3campaign.w3t")) {
			itemChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3campaign.w3t")),
					campaignWTS, inlineWTS);
		}
		if (dataSource.has("war3map.w3d")) {
			doodadChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3map.w3d")), wts,
					inlineWTS);
		}
		if (dataSource.has("war3campaign.w3d")) {
			doodadChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3campaign.w3d")),
					campaignWTS, inlineWTS);
		}
		if (dataSource.has("war3map.w3b")) {
			destructableChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3map.w3b")),
					wts, inlineWTS);
		}
		if (dataSource.has("war3mapSkin.w3b")) {
			destructableChangeset.load(
					new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3mapSkin.w3b")), wts, inlineWTS);
		}
		if (dataSource.has("war3campaign.w3b")) {
			destructableChangeset.load(
					new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3campaign.w3b")), campaignWTS,
					inlineWTS);
		}
		if (dataSource.has("war3map.w3a")) {
			abilityChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3map.w3a")), wts,
					inlineWTS);
		}
		if (dataSource.has("war3mapSkin.w3a")) {
			abilityChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3mapSkin.w3a")),
					wts, inlineWTS);
		}
		if (dataSource.has("war3campaign.w3a")) {
			abilityChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3campaign.w3a")),
					campaignWTS, inlineWTS);
		}
		if (dataSource.has("war3map.w3h")) {
			buffChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3map.w3h")), wts,
					inlineWTS);
		}
		if (dataSource.has("war3mapSkin.w3h")) {
			buffChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3mapSkin.w3h")), wts,
					inlineWTS);
		}
		if (dataSource.has("war3campaign.w3h")) {
			buffChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3campaign.w3h")),
					campaignWTS, inlineWTS);
		}
		if (dataSource.has("war3map.w3q")) {
			upgradeChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3map.w3q")), wts,
					inlineWTS);
		}
		if (dataSource.has("war3mapSkin.w3q")) {
			upgradeChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3mapSkin.w3q")),
					wts, inlineWTS);
		}
		if (dataSource.has("war3campaign.w3q")) {
			upgradeChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3campaign.w3q")),
					campaignWTS, inlineWTS);
		}

		final WorldEditStrings worldEditStrings = standardObjectData.getWorldEditStrings();
		CollapsedObjectData.apply(worldEditStrings, WorldEditorDataType.UNITS, standardUnits, standardUnitMeta,
				unitChangeset);
		CollapsedObjectData.apply(worldEditStrings, WorldEditorDataType.ITEM, standardItems, standardUnitMeta,
				itemChangeset);
		CollapsedObjectData.apply(worldEditStrings, WorldEditorDataType.DOODADS, standardDoodads, standardDoodadMeta,
				doodadChangeset);
		CollapsedObjectData.apply(worldEditStrings, WorldEditorDataType.DESTRUCTIBLES, standardDestructables,
				standardDestructableMeta, destructableChangeset);
		CollapsedObjectData.apply(worldEditStrings, WorldEditorDataType.ABILITIES, abilities, abilityMeta,
				abilityChangeset);
		CollapsedObjectData.apply(worldEditStrings, WorldEditorDataType.BUFFS_EFFECTS, standardAbilityBuffs,
				standardAbilityBuffMeta, buffChangeset);
		CollapsedObjectData.apply(worldEditStrings, WorldEditorDataType.UPGRADES, standardUpgrades, standardUpgradeMeta,
				upgradeChangeset);

		return new Warcraft3MapRuntimeObjectData(standardUnits, standardItems, standardDestructables, standardDoodads,
				abilities, standardAbilityBuffs, standardUpgrades, standardUpgradeEffectMeta, wts);
	}
}
