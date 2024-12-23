package com.etheller.warsmash.parsers.jass.triggers;

import java.util.ArrayList;

import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

// 定义一个名为UnitGroup的类，它继承自ArrayList<CUnit>并实现了CHandle接口
public class UnitGroup extends ArrayList<CUnit> implements CHandle {
    // 定义一个私有整型变量handleId，用于存储句柄ID
    private int handleId;

    // 构造函数，接收一个整型参数handleId，并将其赋值给类的成员变量handleId
    public UnitGroup(int handleId) {
        this.handleId = handleId;
    }

    // 重写CHandle接口中的getHandleId方法，返回当前对象的handleId
    @Override
    public int getHandleId() {
        return handleId;
    }

}
