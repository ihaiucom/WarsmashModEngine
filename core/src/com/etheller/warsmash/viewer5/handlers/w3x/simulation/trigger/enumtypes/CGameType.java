package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

// 定义一个枚举类型 CGameType，实现了接口 CHandle
public enum CGameType implements CHandle {
    // 枚举值，代表不同的游戏类型
    MELEE, // 近战模式
    FFA, // 自由混战模式
    USE_MAP_SETTINGS, // 使用地图设置模式
    BLIZ, // 未知游戏类型，可能是特定游戏内的类型
    ONE_ON_ONE, // 一对一模式
    TWO_TEAM_PLAY, // 两队对抗模式
    THREE_TEAM_PLAY, // 三队对抗模式
    FOUR_TEAM_PLAY; // 四队对抗模式

    // 静态数组，存储所有的枚举值
    public static CGameType[] VALUES = values();

    // 根据传入的id查找对应的游戏类型，如果没有找到则返回null
    public static CGameType getById(final int id) {
        for (final CGameType type : VALUES) {
            if ((type.getId()) == id) {
                return type;
            }
        }
        return null;
    }

    // 获取枚举值的id，这里使用位移操作，每个枚举值的id是其顺序的2的幂
    public int getId() {
        return 1 << ordinal();
    }

    // 实现接口CHandle的方法，返回枚举值的id
    @Override
    public int getHandleId() {
        return getId();
    }
}
