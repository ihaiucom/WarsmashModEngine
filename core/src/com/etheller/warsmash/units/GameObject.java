package com.etheller.warsmash.units;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.etheller.warsmash.util.War3ID;
/**
 * 游戏对象接口，定义了一系列操作游戏对象字段的方法。
 */
public interface GameObject {

	/**
	 * 设置指定字段的值。
	 * @param field 字段名
	 * @param value 字段值
	 */
	public void setField(String field, String value);

	/**
	 * 设置指定字段的值，对于特定的 SLK。
	 * @param slk SLK 名称
	 * @param field 字段名
	 * @param value 字段值
	 */
	public void setField(String slk, String field, String value);

	/**
	 * 设置指定字段的值，对于特定的 SLK 和索引。
	 * @param slk SLK 名称
	 * @param field 字段名
	 * @param value 字段值
	 * @param index 索引
	 */
	public void setField(String slk, String field, String value, int index);

	/**
	 * 清除指定 SLK 的字段列表。
	 * @param slk SLK 名称
	 * @param field 字段名
	 */
	public void clearFieldList(String slk, String field);

	/**
	 * 获取指定字段的值。
	 * @param field 字段名
	 * @return 字段值
	 */
	public String getField(String field);

	/**
	 * 获取指定字段的值，并作为列表返回。
	 * @param field 字段名
	 * @return 字段列表
	 */
	public List<String> getFieldAsList(String field);

	/**
	 * 获取指定字段在索引位置的值。
	 * @param field 字段名
	 * @param index 索引
	 * @return 字段值
	 */
	public String getField(String field, int index);

	/**
	 * 获取指定字段的整数值。
	 * @param field 字段名
	 * @return 整数值
	 */
	public int getFieldValue(String field);

	/**
	 * 获取指定字段在索引位置的整数值。
	 * @param field 字段名
	 * @param index 索引
	 * @return 整数值
	 */
	public int getFieldValue(String field, int index);

	/**
	 * 获取指定字段的浮点值。
	 * @param field 字段名
	 * @return 浮点值
	 */
	public float getFieldFloatValue(String field);

	/**
	 * 获取指定字段在索引位置的浮点值。
	 * @param field 字段名
	 * @param index 索引
	 * @return 浮点值
	 */
	public float getFieldFloatValue(String field, int index);

	/**
	 * 默认方法：获取指定字段在索引位置的字符串值。
	 * @param field 字段名
	 * @param index 索引
	 * @return 字符串值
	 */
	public default String getFieldAsString(final String field, final int index) {
		return getField(field, index);
	}

	/**
	 * 默认方法：获取指定字段在索引位置的整数值。
	 * @param field 字段名
	 * @param index 索引
	 * @return 整数值
	 */
	public default int getFieldAsInteger(final String field, final int index) {
		return getFieldValue(field, index);
	}

	/**
	 * 默认方法：获取指定字段在索引位置的浮点值。
	 * @param field 字段名
	 * @param index 索引
	 * @return 浮点值
	 */
	public default float getFieldAsFloat(final String field, final int index) {
		return getFieldFloatValue(field, index);
	}

	/**
	 * 默认方法：获取指定字段在索引位置的布尔值。
	 * @param field 字段名
	 * @param index 索引
	 * @return 布尔值
	 */
	public default boolean getFieldAsBoolean(final String field, final int index) {
		return getFieldValue(field, index) != 0;
	}

	/**
	 * 默认方法：获取指定字段在索引位置的 War3ID 值。
	 * @param field 字段名
	 * @param index 索引
	 * @return War3ID
	 */
	public default War3ID getFieldAsWar3ID(final String field, final int index) {
		return War3ID.fromString(getField(field, index));
	}

	/**
	 * 默认方法：读取 SLK 标签的整数值。
	 * @param field 字段名
	 * @return 整数值
	 */
	public default int readSLKTagInt(final String field) {
		return getFieldValue(field);
	}

	/**
	 * 默认方法：读取 SLK 标签的字符串值。
	 * @param field 字段名
	 * @return 字符串值
	 */
	public default String readSLKTag(final String field) {
		return getField(field);
	}

	/**
	 * 默认方法：读取 SLK 标签的浮点值。
	 * @param field 字段名
	 * @return 浮点值
	 */
	public default float readSLKTagFloat(final String field) {
		return getFieldFloatValue(field);
	}

	/**
	 * 默认方法：读取 SLK 标签的布尔值。
	 * @param field 字段名
	 * @return 布尔值
	 */
	public default boolean readSLKTagBoolean(final String field) {
		return getFieldValue(field) != 0;
	}

	/**
	 * 获取指定字段的值，并作为列表返回，结合另一个对象数据。
	 * @param field 字段名
	 * @param objectData 对象数据
	 * @return 字段列表
	 */
	public List<? extends GameObject> getFieldAsList(String field, ObjectData objectData);

	/**
	 * 获取游戏对象的唯一标识符。
	 * @return ID
	 */
	public String getId();

	/**
	 * 获取与游戏对象关联的数据表。
	 * @return 对象数据
	 */
	public ObjectData getTable();

	/**
	 * 获取游戏对象的名称。
	 * @return 名称
	 */
	public String getName();

	/**
	 * 获取游戏对象的遗留名称。
	 * @return 遗留名称
	 */
	public String getLegacyName();

	/**
	 * 获取游戏对象的所有键的集合。
	 * @return 键的集合
	 */
	public Set<String> keySet();

	/**
	 * 空的游戏对象实现，所有方法返回默认值。
	 */
	GameObject EMPTY = new GameObject() {
		@Override
		public void setField(final String field, final String value) {
		}

		@Override
		public void setField(final String slk, final String field, final String value, final int index) {
		}

		@Override
		public void setField(final String slk, final String field, final String value) {
		}

		@Override
		public void clearFieldList(final String slk, final String field) {
		}

		@Override
		public Set<String> keySet() {
			return Collections.emptySet();
		}

		@Override
		public ObjectData getTable() {
			return null;
		}

		@Override
		public String getName() {
			return "<No data>";
		}

		@Override
		public String getId() {
			return "0000";
		}

		@Override
		public int getFieldValue(final String field, final int index) {
			return 0;
		}

		@Override
		public int getFieldValue(final String field) {
			return 0;
		}

		@Override
		public float getFieldFloatValue(final String field) {
			return 0;
		}

		@Override
		public float getFieldFloatValue(final String field, final int index) {
			return 0;
		}

		@Override
		public List<? extends GameObject> getFieldAsList(final String field, final ObjectData objectData) {
			return Collections.emptyList();
		}

		@Override
		public String getField(final String field, final int index) {
			return "";
		}

		@Override
		public String getField(final String field) {
			return "";
		}

		@Override
		public List<String> getFieldAsList(final String field) {
			return Collections.emptyList();
		}

		@Override
		public String getLegacyName() {
			return "custom_0000";
		}
	};

}
