package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;

public enum CMapControl implements CHandle {
    // 枚举值定义
    USER,          // 用户控制
    COMPUTER,      // 计算机控制
    RESCUEABLE,    // 可救援
    NEUTRAL,       // 中立
    CREEP,         // 爬行单位
    NONE;          // 无控制

    // 静态方法，返回枚举的所有值
    public static CMapControl[] VALUES = values();

    /**
     * 实现CHandle接口的方法，返回枚举常量的序数
     * @return 枚举常量的序数
     */
    @Override
    public int getHandleId() {
        return ordinal();
    }
}

