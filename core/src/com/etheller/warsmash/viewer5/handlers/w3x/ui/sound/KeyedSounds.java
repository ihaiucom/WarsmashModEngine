package com.etheller.warsmash.viewer5.handlers.w3x.ui.sound;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSound;

// 定义一个名为KeyedSounds的类，用于管理按键声音的映射
public class KeyedSounds {
    // uiSoundsTable用于存储声音数据的表格
    private final DataTable uiSoundsTable;
    // dataSource是数据源，用于提供声音数据
    private final DataSource dataSource;
    // keyToSound是一个映射，用于将字符串键映射到UnitSound对象
    private final Map<String, UnitSound> keyToSound;

    // 构造函数，初始化uiSoundsTable和dataSource，并创建一个空的keyToSound映射
    public KeyedSounds(final DataTable uiSoundsTable, final DataSource dataSource) {
        this.uiSoundsTable = uiSoundsTable;
        this.dataSource = dataSource;
        this.keyToSound = new HashMap<>();
    }

    // 根据给定的键获取对应的UnitSound对象
    public UnitSound getSound(final String key) {
        // 尝试从映射中获取对应键的声音对象
        UnitSound sound = this.keyToSound.get(key);
        // 如果映射中没有该键的声音对象，则创建一个新的声音对象
        if (sound == null) {
            // 使用dataSource和uiSoundsTable创建声音对象，第二个参数是声音文件的路径或名称
            sound = UnitSound.create(this.dataSource, this.uiSoundsTable, key, "");
            // 将新创建的声音对象添加到映射中
            this.keyToSound.put(key, sound);
        }
        // 返回声音对象
        return sound;
    }
}
