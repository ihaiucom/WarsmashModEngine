package com.etheller.warsmash.units;

import java.util.LinkedHashMap;

/**
 * 用于存储和管理单位的数据。
 */
public class LMUnit extends Element {

    /**
     * 构造一个新的 LMUnit 对象。
     *
     * @param id   单位的唯一标识符。
     * @param table 包含单位数据的数据表。
     */
    public LMUnit(final String id, final DataTable table) {
        // 调用父类的构造函数来初始化 ID 和数据表
        super(id, table);
        // 创建一个新的 LinkedHashMap 来存储字段
        this.fields = new LinkedHashMap<>();
    }

}

