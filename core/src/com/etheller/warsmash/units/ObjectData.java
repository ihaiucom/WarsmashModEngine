package com.etheller.warsmash.units;

import java.util.Set;

import com.etheller.warsmash.util.War3ID;

/**
 * 定义了一个用于操作游戏对象数据的接口。
 * 该接口提供了一系列方法来获取、克隆、继承和设置游戏对象的属性值。
 */
public interface ObjectData {
    /**
     * 根据给定的ID获取游戏对象。
     *
     * @param id 游戏对象的ID。
     * @return 对应的游戏对象，如果不存在则返回null。
     */
    GameObject get(String id);

    /**
     * 根据给定的War3ID获取游戏对象。
     *
     * @param id 游戏对象的War3ID。
     * @return 对应的游戏对象，如果不存在则返回null。
     */
    default GameObject get(final War3ID id) {
        return get(id.asStringValue());
    }

    /**
     * 克隆一个单位，并指定新克隆单位的ID。
     *
     * @param parentId 原单位的ID。
     * @param cloneId  克隆单位的ID。
     */
    void cloneUnit(final String parentId, final String cloneId);

    /**
     * 继承一个对象的属性值到另一个对象。
     *
     * @param childKey 子对象的键。
     * @param parentKey 父对象的键。
     */
    void inheritFrom(String childKey, String parentKey);

    /**
     * 设置SLK文件中指定ID的对象的字段值。
     *
     * @param slk SLK文件的名称。
     * @param id  对象的ID。
     * @param field 字段名称。
     * @param value 字段值。
     */
    void setValue(String slk, String id, String field, String value);

    /**
     * 获取所有游戏对象的键集合。
     *
     * @return 包含所有游戏对象键的集合。
     */
    Set<String> keySet();

    /**
     * 获取本地化字符串。
     *
     * @param key 字符串的键。
     * @return 本地化后的字符串，如果不存在则返回原始键。
     */
    String getLocalizedString(String key);
}
