package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;

// 地图放置方式枚举
public enum CMapPlacement implements CHandle {
    // 枚举值，表示地图放置的方式
    RANDOM,          // 随机放置
    FIXED,           // 固定位置放置
    USE_MAP_SETTINGS,// 使用地图设置中的放置方式
    TEAMS_TOGETHER;  // 队伍一起放置

    // 静态数组，存储所有的枚举值
    public static CMapPlacement[] VALUES = values();

    // 实现接口中的方法，返回枚举值在枚举中的序号
    @Override
    public int getHandleId() {
        return ordinal();
    }
}
