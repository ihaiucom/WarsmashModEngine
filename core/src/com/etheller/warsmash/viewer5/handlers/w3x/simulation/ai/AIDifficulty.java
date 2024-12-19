package com.etheller.warsmash.viewer5.handlers.w3x.simulation.ai;

import com.etheller.interpreter.ast.util.CHandle;
public enum AIDifficulty implements CHandle {
    // 枚举值定义了AI的难度级别
    NEWBIE,    // 新手级别
    NORMAL,    // 普通级别
    INSANE;    // 极难级别

    // 静态数组存储了所有的枚举值，方便遍历和使用
    public static AIDifficulty[] VALUES = values();

    /**
     * 实现CHandle接口的方法，返回枚举常量的序数
     * @return 枚举常量的序数
     */
    @Override
    public int getHandleId() {
        return ordinal();
    }
}
