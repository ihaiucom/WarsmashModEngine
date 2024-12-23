package com.etheller.warsmash.units;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.util.StringBundle;
import com.etheller.warsmash.util.WorldEditStrings;

public class StandardObjectData {
	private WorldEditStrings worldEditStrings;
	private DataSource source;

	public StandardObjectData(final DataSource dataSource) {
		this.source = dataSource;
		this.worldEditStrings = new WorldEditStrings(dataSource);
	}

	// 单位相关的数据配置
	public WarcraftData getStandardUnits() {

		final DataTable profile = new DataTable(this.worldEditStrings);
		final DataTable unitAbilities = new DataTable(this.worldEditStrings);
		final DataTable unitBalance = new DataTable(this.worldEditStrings);
		final DataTable unitData = new DataTable(this.worldEditStrings);
		final DataTable unitUI = new DataTable(this.worldEditStrings);
		final DataTable unitWeapons = new DataTable(this.worldEditStrings);

		try {
			profile.readTXT(this.source.getResourceAsStream("Units\\CampaignUnitFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\CampaignUnitStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\HumanUnitFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\HumanUnitStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NeutralUnitFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NeutralUnitStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NightElfUnitFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NightElfUnitStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\OrcUnitFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\OrcUnitStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\UndeadUnitFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\UndeadUnitStrings.txt"), true);

			unitAbilities.readSLK(this.source.getResourceAsStream("Units\\UnitAbilities.slk"));

			unitBalance.readSLK(this.source.getResourceAsStream("Units\\UnitBalance.slk"));

			unitData.readSLK(this.source.getResourceAsStream("Units\\UnitData.slk"));

			unitUI.readSLK(this.source.getResourceAsStream("Units\\UnitUI.slk"));

			unitWeapons.readSLK(this.source.getResourceAsStream("Units\\UnitWeapons.slk"));
			final InputStream unitSkin = this.source.getResourceAsStream("Units\\UnitSkin.txt");
			if (unitSkin != null) {
				profile.readTXT(unitSkin, true);
			}
			final InputStream unitWeaponsFunc = this.source.getResourceAsStream("Units\\UnitWeaponsFunc.txt");
			if (unitWeaponsFunc != null) {
				profile.readTXT(unitWeaponsFunc, true);
			}
			final InputStream unitWeaponsSkin = this.source.getResourceAsStream("Units\\UnitWeaponsSkin.txt");
			if (unitWeaponsSkin != null) {
				profile.readTXT(unitWeaponsSkin, true);
			}
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final WarcraftData units = new WarcraftData(this.worldEditStrings);

		units.add(profile, "Profile", false);
		units.add(unitAbilities, "UnitAbilities", true);
		units.add(unitBalance, "UnitBalance", true);
		units.add(unitData, "UnitData", true);
		units.add(unitUI, "UnitUI", true);
		units.add(unitWeapons, "UnitWeapons", true);

		return units;
	}

	// 物品相关的数据配置
	public WarcraftData getStandardItems() {
		final DataTable profile = new DataTable(this.worldEditStrings);
		final DataTable itemData = new DataTable(this.worldEditStrings);

		try {
			profile.readTXT(this.source.getResourceAsStream("Units\\ItemFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\ItemStrings.txt"), true);
			itemData.readSLK(this.source.getResourceAsStream("Units\\ItemData.slk"));
			final InputStream itemSkin = this.source.getResourceAsStream("Units\\ItemSkin.txt");
			if (itemSkin != null) {
				profile.readTXT(itemSkin, true);
			}
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final WarcraftData units = new WarcraftData(this.worldEditStrings);

		units.add(profile, "Profile", false);
		units.add(itemData, "ItemData", true);

		return units;
	}

	// 可破坏物相关的数据配置
	public WarcraftData getStandardDestructables() {
		final DataTable destructableData = new DataTable(this.worldEditStrings);

		try {
			destructableData.readSLK(this.source.getResourceAsStream("Units\\DestructableData.slk"));
			final InputStream unitSkin = this.source.getResourceAsStream("Units\\DestructableSkin.txt");
			if (unitSkin != null) {
				destructableData.readTXT(unitSkin, true);
			}
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final WarcraftData units = new WarcraftData(this.worldEditStrings);

		units.add(destructableData, "DestructableData", true);

		return units;
	}

	// 装饰物相关的数据配置
	public WarcraftData getStandardDoodads() {

		final DataTable destructableData = new DataTable(this.worldEditStrings);

		try {
			destructableData.readSLK(this.source.getResourceAsStream("Doodads\\Doodads.slk"));
			final InputStream unitSkin = this.source.getResourceAsStream("Doodads\\DoodadSkins.txt");
			if (unitSkin != null) {
				destructableData.readTXT(unitSkin, true);
			}
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final WarcraftData units = new WarcraftData(this.worldEditStrings);

		units.add(destructableData, "DoodadData", true);

		return units;
	}

	public DataTable getStandardUnitMeta() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readSLK(this.source.getResourceAsStream("Units\\UnitMetaData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public DataTable getStandardDestructableMeta() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readSLK(this.source.getResourceAsStream("Units\\DestructableMetaData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public DataTable getStandardDoodadMeta() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readSLK(this.source.getResourceAsStream("Doodads\\DoodadMetaData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	// 技能相关的数据配置
	public WarcraftData getStandardAbilities() {

		final DataTable profile = new DataTable(this.worldEditStrings);
		final DataTable abilityData = new DataTable(this.worldEditStrings);

		try {
			profile.readTXT(this.source.getResourceAsStream("Units\\CampaignAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\CampaignAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\CommonAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\CommonAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\HumanAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\HumanAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NeutralAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NeutralAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NightElfAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NightElfAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\OrcAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\OrcAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\UndeadAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\UndeadAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\ItemAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\ItemAbilityStrings.txt"), true);

			final InputStream unitSkin = this.source.getResourceAsStream("Units\\AbilitySkin.txt");
			if (unitSkin != null) {
				profile.readTXT(unitSkin, true);
			}

			abilityData.readSLK(this.source.getResourceAsStream("Units\\AbilityData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final WarcraftData abilities = new WarcraftData(this.worldEditStrings);

		abilities.add(profile, "Profile", true);
		abilities.add(abilityData, "AbilityData", true);

		return abilities;
	}

	// Buff相关的数据配置
	public WarcraftData getStandardAbilityBuffs() {
		final DataTable profile = new DataTable(this.worldEditStrings);
		final DataTable abilityData = new DataTable(this.worldEditStrings);

		try {
			profile.readTXT(this.source.getResourceAsStream("Units\\CampaignAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\CampaignAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\CommonAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\CommonAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\HumanAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\HumanAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NeutralAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NeutralAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NightElfAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NightElfAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\OrcAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\OrcAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\UndeadAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\UndeadAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\ItemAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\ItemAbilityStrings.txt"), true);

			abilityData.readSLK(this.source.getResourceAsStream("Units\\AbilityBuffData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final WarcraftData abilities = new WarcraftData(this.worldEditStrings);

		abilities.add(profile, "Profile", false);
		abilities.add(abilityData, "AbilityData", true);

		return abilities;
	}

	// 科技相关的数据配置
	public WarcraftData getStandardUpgrades() {
		final DataTable profile = new DataTable(this.worldEditStrings);
		final DataTable upgradeData = new DataTable(this.worldEditStrings);

		try {
			profile.readTXT(this.source.getResourceAsStream("Units\\CampaignUpgradeFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\CampaignUpgradeStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\HumanUpgradeFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\HumanUpgradeStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NeutralUpgradeFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NeutralUpgradeStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NightElfUpgradeFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NightElfUpgradeStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\OrcUpgradeFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\OrcUpgradeStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\UndeadUpgradeFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\UndeadUpgradeStrings.txt"), true);

			upgradeData.readSLK(this.source.getResourceAsStream("Units\\UpgradeData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final WarcraftData units = new WarcraftData(this.worldEditStrings);

		units.add(profile, "Profile", false);
		units.add(upgradeData, "UpgradeData", true);

		return units;
	}

	public DataTable getStandardUpgradeMeta() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readSLK(this.source.getResourceAsStream("Units\\UpgradeMetaData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public DataTable getStandardUpgradeEffectMeta() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readSLK(this.source.getResourceAsStream("Units\\UpgradeEffectMetaData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public DataTable getStandardAbilityMeta() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readSLK(this.source.getResourceAsStream("Units\\AbilityMetaData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public DataTable getStandardAbilityBuffMeta() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readSLK(this.source.getResourceAsStream("Units\\AbilityBuffMetaData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public DataTable getUnitEditorData() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readTXT(this.source.getResourceAsStream("UI\\UnitEditorData.txt"), true);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public DataTable getWorldEditData() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readTXT(this.source.getResourceAsStream("UI\\WorldEditData.txt"), true);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public WorldEditStrings getWorldEditStrings() {
		return this.worldEditStrings;
	}

	// 定义一个内部静态类 WarcraftData，实现 ObjectData 接口
	public static class WarcraftData implements ObjectData {
		// 世界编辑字符串，用于本地化显示
		WorldEditStrings worldEditStrings;
		// 存储数据表的列表
		List<DataTable> tables = new ArrayList<>();
		// 以字符串键值对形式存储数据表的映射
		Map<StringKey, DataTable> tableMap = new HashMap<>();
		// 存储 WarcraftObject 对象的映射，键为单位ID
		Map<StringKey, WarcraftObject> units = new HashMap<>();

		// 构造函数，初始化 WorldEditStrings
		public WarcraftData(final WorldEditStrings worldEditStrings) {
			this.worldEditStrings = worldEditStrings;
		}

		// 实现 ObjectData 接口的 getLocalizedString 方法，获取本地化字符串
		@Override
		public String getLocalizedString(final String key) {
			return this.worldEditStrings.getString(key);
		}

		// 添加数据表到列表和映射中，并根据条件添加 WarcraftObject 到 units 映射中
		public void add(final DataTable data, final String name, final boolean canMake) {
			this.tableMap.put(new StringKey(name), data);
			this.tables.add(data);
			if (canMake) {
				for (final String id : data.keySet()) {
					if (!this.units.containsKey(new StringKey(id))) {
						this.units.put(new StringKey(id), new WarcraftObject(data.get(id).getId(), this));
					}
				}
			}
		}

		// 获取所有数据表的列表
		public List<DataTable> getTables() {
			return this.tables;
		}

		// 设置数据表列表
		public void setTables(final List<DataTable> tables) {
			this.tables = tables;
		}

		// 根据表名获取数据表
		public DataTable getTable(final String tableName) {
			return this.tableMap.get(new StringKey(tableName));
		}

		// 实现 ObjectData 接口的 get 方法，根据ID获取 GameObject
		@Override
		public GameObject get(final String id) {
			return this.units.get(new StringKey(id));
		}

		// 实现 ObjectData 接口的 setValue 方法，设置指定SLK、ID和字段的值
		@Override
		public void setValue(final String slk, final String id, final String field, final java.lang.String value) {
			get(id).setField(slk, field, value);
		}

		// 实现 ObjectData 接口的 keySet 方法，返回所有单位的键集合
		@Override
		public Set<String> keySet() {
			final Set<String> keySet = new HashSet<>();
			for (final StringKey key : this.units.keySet()) {
				keySet.add(key.getString());
			}
			return keySet;
		}

		// 实现 ObjectData 接口的 cloneUnit 方法，克隆指定ID的单位
		@Override
		public void cloneUnit(final String parentId, final String cloneId) {
			for (final DataTable table : this.tables) {
				table.cloneUnit(parentId, cloneId);
			}
			this.units.put(new StringKey(cloneId), new WarcraftObject(cloneId, this));
		}

		// 实现 ObjectData 接口的 inheritFrom 方法，实现属性继承
		@Override
		public void inheritFrom(String childKey, String parentKey) {
			for (final DataTable table : this.tables) {
				table.inheritFrom(childKey, parentKey);
			}
		}
	}

	// 表示魔兽世界对象的类，实现了游戏对象接口
	public static class WarcraftObject implements GameObject {
		String id;
		WarcraftData dataSource;

		// 构造函数，初始化对象ID和数据源
		public WarcraftObject(final String id, final WarcraftData dataSource) {
			this.id = id;
			this.dataSource = dataSource;
		}

		// 设置字段的值，如果索引为-1，则调用重载方法
		public void setField(final String field, final String value, final int index) {
			if (index == -1) {
				setField(field, value);
				return;
			}
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					element.setField(field, value, index);
					return;
				}
			}
		}

		// 重载方法，设置指定表格中的字段值
		@Override
		public void setField(final String slk, final String field, final String value, final int index) {
			if (index == -1) {
				setField(slk, field, value);
				return;
			}
			DataTable slkTable = this.dataSource.getTable(slk);
			if (slkTable == null) {
				this.dataSource.add(new DataTable(StringBundle.EMPTY), slk, false);
				slkTable = this.dataSource.getTable(slk);
			}
			Element element = slkTable.get(this.id);
			if (element == null) {
				element = new LMUnit(this.id, slkTable);
				slkTable.put(this.id, element);
			}
			element.setField(field, value, index);
		}

		// 清空指定字段的字段列表
		@Override
		public void clearFieldList(final String slk, final String field) {
			DataTable slkTable = this.dataSource.getTable(slk);
			if (slkTable == null) {
				this.dataSource.add(new DataTable(StringBundle.EMPTY), slk, false);
				slkTable = this.dataSource.getTable(slk);
			}
			Element element = slkTable.get(this.id);
			if (element == null) {
				element = new LMUnit(this.id, slkTable);
				slkTable.put(this.id, element);
			}
			element.clearFieldList(slk, field);
		}

		// 获取字段的值，如果索引有效
		@Override
		public String getField(final String field, final int index) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					return element.getField(field, index);
				}
			}
			return "";
		}

		// 获取字段的值并返回成列表形式
		@Override
		public List<String> getFieldAsList(final String field) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					return element.getFieldAsList(field);
				}
			}
			return Collections.emptyList();
		}

		// 获取字段的整型值（带索引）
		@Override
		public int getFieldValue(final String field, final int index) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					return element.getFieldValue(field, index);
				}
			}
			return 0;
		}

		// 设置字段的值（不带索引）
		@Override
		public void setField(final String field, final String value) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					element.setField(field, value);
					return;
				}
			}
			final Element element = this.dataSource.tables.get(0).get(this.id);
			element.setField(field, value);
		}

		// 重载设置字段的值（不带索引，目标表格指定）
		@Override
		public void setField(final String slk, final String field, final String value) {
			DataTable slkTable = this.dataSource.getTable(slk);
			if (slkTable == null) {
				this.dataSource.add(new DataTable(StringBundle.EMPTY), slk, false);
				slkTable = this.dataSource.getTable(slk);
			}
			Element element = slkTable.get(this.id);
			if (element == null) {
				element = new LMUnit(this.id, slkTable);
				slkTable.put(this.id, element);
			}
			element.setField(field, value);
		}

		// 获取字段的值，不带索引
		@Override
		public String getField(final String field) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					return element.getField(field);
				}
			}
			return "";
		}

		// 获取字段的整型值（不带索引）
		@Override
		public int getFieldValue(final String field) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					return element.getFieldValue(field);
				}
			}
			return 0;
		}

		// 获取字段的浮点值
		@Override
		public float getFieldFloatValue(final String field) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					return element.getFieldFloatValue(field);
				}
			}
			return 0f;
		}

		// 获取字段的浮点值（带索引）
		@Override
		public float getFieldFloatValue(final String field, final int index) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					return element.getFieldFloatValue(field, index);
				}
			}
			return 0f;
		}

		/*
		 * (non-Javadoc) I'm not entirely sure this is still safe to use
		 *
		 * @see com.hiveworkshop.wc3.units.GameObject#getFieldAsList(java.lang. String)
		 */
		// 获取字段的值并返回成游戏对象列表
		@Override
		public List<? extends GameObject> getFieldAsList(final String field, final ObjectData objectData) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					return element.getFieldAsList(field, objectData);
				}
			}
			return new ArrayList<>();// empty list if not found
		}

		// 获取对象的ID
		@Override
		public String getId() {
			return this.id;
		}

		// 获取对象的数据表
		@Override
		public ObjectData getTable() {
			return this.dataSource;
		}

		// 获取对象的遗留名称
		@Override
		public String getLegacyName() {
			final DataTable dataTable = this.dataSource.tableMap.get(new StringKey("UnitUI"));
			if (dataTable != null) {
				final Element element = dataTable.get(this.id);
				if (element != null) {
					return element.getField("name");
				}
				else {
					return null;
				}
			}
			return null;
		}

		// @Override
		// public String getName() {
		// return dataSource.profile.get(id).getName();
		// }
		// 获取对象的名称，考虑不同字段以确定最终名称
		@Override
		public String getName() {
			String name = getField("Name");
			boolean nameKnown = name.length() >= 1;
			if (!nameKnown && !getField("code").equals(this.id) && (getField("code").length() >= 4)) {
				final WarcraftObject other = (WarcraftObject) this.dataSource.get(getField("code").substring(0, 4));
				if (other != null) {
					name = other.getName();
					nameKnown = true;
				}
			}
			if (!nameKnown && (getField("EditorName").length() > 1)) {
				name = getField("EditorName");
				nameKnown = true;
			}
			if (!nameKnown && (getField("Editorname").length() > 1)) {
				name = getField("Editorname");
				nameKnown = true;
			}
			if (!nameKnown && (getField("BuffTip").length() > 1)) {
				name = getField("BuffTip");
				nameKnown = true;
			}
			if (!nameKnown && (getField("Bufftip").length() > 1)) {
				name = getField("Bufftip");
				nameKnown = true;
			}
			if (nameKnown && name.startsWith("WESTRING")) {
				if (!name.contains(" ")) {
					name = this.dataSource.getLocalizedString(name);
				}
				else {
					final String[] names = name.split(" ");
					name = "";
					for (final String subName : names) {
						if (name.length() > 0) {
							name += " ";
						}
						if (subName.startsWith("WESTRING")) {
							name += this.dataSource.getLocalizedString(subName);
						}
						else {
							name += subName;
						}
					}
				}
				if (name.startsWith("\"") && name.endsWith("\"")) {
					name = name.substring(1, name.length() - 1);
				}
				setField("Name", name);
			}
			if (!nameKnown) {
				name = this.dataSource.getLocalizedString("WESTRING_UNKNOWN") + " '" + getId() + "'";
			}
			if (getField("campaign").startsWith("1") && Character.isUpperCase(getId().charAt(0))) {
				name = getField("Propernames");
				if (name.contains(",")) {
					name = name.split(",")[0];
				}
			}
			String suf = getField("EditorSuffix");
			if ((suf.length() > 0) && !suf.equals("_")) {
				if (suf.startsWith("WESTRING")) {
					suf = this.dataSource.getLocalizedString(suf);
				}
				if (!suf.startsWith(" ")) {
					name += " ";
				}
				name += suf;
			}
			return name;
		}

		BufferedImage storedImage = null;
		String storedImagePath = null;

		// 获取所有键的集合
		@Override
		public Set<String> keySet() {
			final Set<String> keySet = new HashSet<>();
			for (final DataTable table : this.dataSource.tables) {
				keySet.addAll(table.get(this.id).keySet());
			}
			return keySet;
		}
	}


	private StandardObjectData() {
	}
}
