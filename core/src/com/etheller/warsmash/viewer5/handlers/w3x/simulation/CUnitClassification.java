package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.HashMap;
import java.util.Map;
/**
 * We think in the original WC3 sourcecode, these are probably referred to as
 * "Unit Types", but the community turn of phrase "Unit Type" has come to refer
 * to what WC3 sourcecode calls "Unit Class", hence this is now named "Unit
 * Classification" instead of "Unit Type" or "Unit Class" to disambiguate. This
 * is consistent with the World Editor naming: "Stats - Unit Classification".
 */
/**
 * 表示单位分类的枚举，包含多个单位类型及其相关属性。
 */
public enum CUnitClassification {
	// 巨型单位
	GIANT("giant", "GiantClass"),
	// 不死单位
	UNDEAD("undead", "UndeadClass"),
	// 被召唤的单位
	SUMMONED("summoned"),
	// 机械单位
	MECHANICAL("mechanical", "MechanicalClass"),
	// 工人
	PEON("peon"),
	// 工兵
	SAPPER("sapper"),
	// 城镇大厅
	TOWNHALL("townhall"),
	// 树木
	TREE("tree"),
	// 守卫
	WARD("ward"),
	// 古老
	ANCIENT("ancient"),
	// 静止
	STANDON("standon"),
	// 中立
	NEUTRAL("neutral"),
	// 牛头人
	TAUREN("tauren", "TaurenClass");

	/**
	 * 存储单位编辑器键与单位分类之间的映射关系。
	 */
	private static final Map<String, CUnitClassification> UNIT_EDITOR_KEY_TO_CLASSIFICATION = new HashMap<>();
	static {
		for (final CUnitClassification unitClassification : values()) {
			UNIT_EDITOR_KEY_TO_CLASSIFICATION.put(unitClassification.getUnitDataKey(), unitClassification);
		}
	}

	private String localeKey;
	private String unitDataKey;
	private String displayName;

	/**
	 * 构造函数，用于创建具有单位数据键和语言键的单位分类。
	 * @param unitDataKey 单位数据键
	 * @param localeKey 语言键
	 */
	private CUnitClassification(final String unitDataKey, final String localeKey) {
		this.unitDataKey = unitDataKey;
		this.localeKey = localeKey;
	}

	/**
	 * 构造函数，用于创建只有单位数据键的单位分类。
	 * @param unitDataKey 单位数据键
	 */
	private CUnitClassification(final String unitDataKey) {
		this.unitDataKey = unitDataKey;
		this.localeKey = null;
	}

	/**
	 * 获取单位数据键。
	 * @return 单位数据键
	 */
	public String getUnitDataKey() {
		return this.unitDataKey;
	}

	/**
	 * 获取语言键。
	 * @return 语言键
	 */
	public String getLocaleKey() {
		return this.localeKey;
	}

	/**
	 * 获取显示名称。
	 * @return 显示名称
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * 设置显示名称。
	 * @param displayName 显示名称
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	/**
	 * 根据单位编辑器键解析单位分类。
	 * @param unitEditorKey 单位编辑器键
	 * @return 对应的单位分类
	 */
	public static CUnitClassification parseUnitClassification(final String unitEditorKey) {
		return UNIT_EDITOR_KEY_TO_CLASSIFICATION.get(unitEditorKey.toLowerCase());
	}
}
