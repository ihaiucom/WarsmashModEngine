package com.etheller.warsmash.units;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 抽象类 HashedGameObject 实现了 GameObject 接口，用于处理哈希映射的游戏对象。
 */
public abstract class HashedGameObject implements GameObject {
    HashMap<StringKey, List<String>> fields = new HashMap<>();
    String id;
    ObjectData parentTable;

    transient HashMap<String, List<GameObject>> hashedLists = new HashMap<>();

    /**
     * 构造函数，初始化 HashedGameObject 的 id 和 parentTable。
     *
     * @param id    游戏对象的唯一标识符
     * @param table 对象数据表
     */
    public HashedGameObject(final String id, final ObjectData table) {
        this.id = id;
        this.parentTable = table;
    }

    /**
     * 返回字段的数量。
     *
     * @return 字段数量
     */
    public int size() {
        return this.fields.size();
    }

    @Override
    /**
     * 设置字段值。
     */
    public void setField(final String slk, final String field, final String value) {
        setField(field, value);
    }

    /**
     * 设置字段的字符串值。
     *
     * @param field 字段名
     * @param value 字段值
     */
    public void setField(final String field, final String value) {
        final StringKey key = new StringKey(field);
        List<String> list = this.fields.get(key);
        if (list == null) {
            list = new ArrayList<>();
            this.fields.put(key, list);
            list.add(value);
        } else {
            list.set(0, value);
        }
    }

    /**
     * 设置字段的列表值。
     *
     * @param field 字段名
     * @param value 字段值列表
     */
    public void setField(final String field, final List<String> value) {
        final StringKey key = new StringKey(field);
        if (value.isEmpty()) {
            this.fields.remove(key);
        } else {
            this.fields.put(key, value);
        }
    }

    @Override
    /**
     * 获取字段的字符串值。
     *
     * @param field 字段名
     * @return 字段的字符串值
     */
    public String getField(final String field) {
        final String value = "";
        // 检查字段是否存在于哈希表中
        if (this.fields.get(new StringKey(field)) != null) {
            // 获取字段的值列表
            final List<String> list = this.fields.get(new StringKey(field));
            // 创建一个字符串构建器来拼接字符串
            final StringBuilder sb = new StringBuilder();
            // 遍历值列表
            if (list != null) {
                for (final String str : list) {
                    // 如果字符串构建器的长度不为0，则添加逗号作为分隔符
                    if (sb.length() != 0) {
                        sb.append(',');
                    }
                    // 将当前字符串添加到字符串构建器中
                    sb.append(str);
                }
                // 返回拼接后的字符串
                return sb.toString();
            }
        }
        // 如果字段不存在或值列表为空，则返回默认的空字符串
        return value;

    }

    @Override
    /**
     * 获取字段的值列表。
     *
     * @param field 字段名
     * @return 字段的值列表
     */
    public List<String> getFieldAsList(final String field) {
        return this.fields.get(new StringKey(field));
    }

    /**
     * 检查字段是否存在。
     *
     * @param field 字段名
     * @return 如果字段存在，返回 true；否则返回 false
     */
    public boolean hasField(final String field) {
        return this.fields.containsKey(new StringKey(field));
    }

    @Override
    /**
     * 获取字段值并转换为整数。
     *
     * @param field 字段名
     * @return 字段值的整数
     */
    public int getFieldValue(final String field) {
        int i = 0;
        try {
            i = Integer.parseInt(getField(field).trim());
        } catch (final NumberFormatException e) {

        }
        return i;
    }

    @Override
    /**
     * 获取字段值并转换为浮点数。
     *
     * @param field 字段名
     * @return 字段值的浮点数
     */
    public float getFieldFloatValue(final String field) {
        float i = 0;
        try {
            i = Float.parseFloat(getField(field).trim());
        } catch (final NumberFormatException e) {

        }
        return i;
    }

    @Override
    /**
     * 获取字段值并转换为浮点数，指定索引。
     *
     * @param field 字段名
     * @param index 索引
     * @return 字段值的浮点数
     */
    public float getFieldFloatValue(final String field, final int index) {
        return getFieldWithDefaultValue(field, index, 0f);
    }

    /**
     * 获取字段值，指定索引和默认值。
     *
     * @param field        字段名
     * @param index        索引
     * @param defaultValue 默认值
     * @return 字段值的浮点数
     */
    public float getFieldWithDefaultValue(final String field, int index, final float defaultValue) {
        float i = defaultValue;
        {
            if (index < 0) {
                index = 0;
            }
            final List<String> fieldList = this.fields.get(new StringKey(field));
            if (fieldList != null) {
                final List<String> list = fieldList;
                if (list != null) {
                    if (list.size() > index) {
                        try {
                            i = Float.parseFloat(list.get(index).trim());
                        } catch (final NumberFormatException e) {

                        }
                    }
                }
            }
        }
        return i;
    }

    @Override
    /**
     * 设置字段的字符串值，指定索引。
     */
    public void setField(final String slk, final String field, final String value, final int index) {
        setField(field, value, index);
    }

    /**
     * 设置字段的字符串值，指定索引。
     *
     * @param field 字段名
     * @param value 字段值
     * @param index 索引
     */
    public void setField(final String field, final String value, final int index) {
        final StringKey key = new StringKey(field);
        List<String> list = this.fields.get(key);
        if (list == null) {
            if (index == 0) {
                list = new ArrayList<>();
                this.fields.put(key, list);
                list.add(value);
            } else {
                list = new ArrayList<>();
                for (int k = 0; k < index; k++) {
                    list.add("");
                }
                list.add(value);
            }
        } else {
            if (list.size() == index) {
                list.add(value);
            } else {
                for (int k = list.size(); k <= index; k++) {
                    list.add("");
                }
                list.set(index, value);
            }
        }
    }

    @Override
    /**
     * 清空字段列表。
     */
    public void clearFieldList(final String slk, final String field) {
        this.fields.remove(new StringKey(field));
    }

    @Override
    /**
     * 获取字段值，指定索引。
     *
     * @param field 字段名
     * @param index 索引
     * @return 字段值的字符串
     */
    public String getField(final String field, int index) {
        if (index < 0) {
            index = 0;
        }
        String value = "";
        if (this.fields.get(new StringKey(field)) != null) {
            final List<String> list = this.fields.get(new StringKey(field));
            if (list != null) {
                if (list.size() > index) {
                    value = list.get(index);
                } else if (list.size() > 0) {
                    value = list.get(list.size() - 1);
                }
            }
        }
        return value;
    }

    @Override
    /**
     * 获取字段值并转换为整数，指定索引。
     *
     * @param field 字段名
     * @param index 索引
     * @return 字段值的整数
     */
    public int getFieldValue(final String field, final int index) {
        return getFieldWithDefaultValue(field, index, 0);
    }

    /**
     * 获取字段值，指定索引和默认值，返回整数类型。
     *
     * @param field        字段名
     * @param index        索引
     * @param defaultValue 默认值
     * @return 字段值的整数
     */
    public int getFieldWithDefaultValue(final String field, int index, final int defaultValue) {
        int i = defaultValue;
        {
            if (index < 0) {
                index = 0;
            }
            final List<String> fieldList = this.fields.get(new StringKey(field));
            if (fieldList != null) {
                final List<String> list = fieldList;
                if (list != null) {
                    if (list.size() > index) {
                        try {
                            i = Integer.parseInt(list.get(index).trim());
                        } catch (final NumberFormatException e) {
                            try {
                                i = (int) Float.parseFloat(list.get(index).trim());
                            } catch (final NumberFormatException e2) {

                            }
                        }
                    }
                }
            }
        }
        return i;
    }

    @Override
    /**
     * 获取字段作为游戏对象列表。
     *
     * @param field 字段名
     * @param parentTable 父对象数据表
     * @return 游戏对象列表
     */
    public List<GameObject> getFieldAsList(final String field, final ObjectData parentTable) {
        // 创建一个名为 fieldAsList 的 ArrayList 来存储游戏对象
        List<GameObject> fieldAsList;
        // 初始化 fieldAsList
        fieldAsList = new ArrayList<>();

        // 从当前游戏对象中获取指定字段（field）的值，并将其存储在 stringList 中
        final String stringList = getField(field);

        // 使用逗号作为分隔符，将 stringList 分割成字符串数组，并存储在 listAsArray 中
        final String[] listAsArray = stringList.split(",");

        // 检查 listAsArray 是否不为空，并且长度大于 0
        if ((listAsArray != null) && (listAsArray.length > 0)) {
            // 遍历 listAsArray 中的每个字符串元素
            for (final String buildingId : listAsArray) {
                // 尝试从父表（parentTable）中获取与当前 buildingId 对应的游戏对象
                final GameObject referencedUnit = parentTable.get(buildingId);
                // 如果找到了对应的游戏对象
                if (referencedUnit != null) {
                    // 将该游戏对象添加到 fieldAsList 中
                    fieldAsList.add(referencedUnit);
                }
            }
        }

        // 返回包含所有找到的游戏对象的 fieldAsList
        return fieldAsList;

    }

    @Override
    /**
     * 返回游戏对象的字符串表示。
     *
     * @return 游戏对象的名称
     */
    public String toString() {
        return getField("Name");
    }

    @Override
    /**
     * 获取游戏对象的唯一标识符。
     *
     * @return 游戏对象的 id
     */
    public String getId() {
        return this.id;
    }

    @Override
    /**
     * 获取游戏对象的名称。
     *
     * @return 游戏对象的名称
     */
    public String getName() {
        String name = getField("Name");
        boolean nameKnown = name.length() >= 1;
        if (!nameKnown && !getField("code").equals(this.id) && (getField("code").length() >= 4)) {
            final GameObject other = this.parentTable.get(getField("code").substring(0, 4));
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
                name = this.parentTable.getLocalizedString(name);
            } else {
                final String[] names = name.split(" ");
                name = "";
                for (final String subName : names) {
                    if (name.length() > 0) {
                        name += " ";
                    }
                    if (subName.startsWith("WESTRING")) {
                        name += this.parentTable.getLocalizedString(subName);
                    } else {
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
            name = this.parentTable.getLocalizedString("WESTRING_UNKNOWN") + " '" + getId() + "'";
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
                suf = this.parentTable.getLocalizedString(suf);
            }
            if (!suf.startsWith(" ")) {
                name += " ";
            }
            name += suf;
        }
        return name;
    }

    /**
     * 将指定的 parentId 添加到字段列表中。
     *
     * @param parentId 父 ID
     * @param list     字段名
     */
    public void addToList(final String parentId, final String list) {
        String parentField = getField(list);
        if (!parentField.contains(parentId)) {
            parentField = parentField + "," + parentId;
            setField(list, parentField);
        }
    }

    @Override
    /**
     * 获取对象数据表。
     *
     * @return 对象数据表
     */
    public ObjectData getTable() {
        return this.parentTable;
    }

    @Override
    /**
     * 获取字段的键集合。
     *
     * @return 字段键的集合
     */
    public Set<String> keySet() {
        final Set<String> keySet = new HashSet<>();
        for (final StringKey key : this.fields.keySet()) {
            keySet.add(key.getString());
        }
        return keySet;
    }
}
