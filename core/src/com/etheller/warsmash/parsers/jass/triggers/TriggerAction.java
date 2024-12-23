package com.etheller.warsmash.parsers.jass.triggers;

import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.value.CodeJassValue;

// 定义一个名为TriggerAction的类
public class TriggerAction {
    // 定义三个私有成员变量，分别是触发器trigger、动作函数actionFunc和动作索引actionIndex
    private final Trigger trigger; // 触发器对象
    private final CodeJassValue actionFunc; // 动作函数对象
    private final int actionIndex; // 动作索引，用于标识特定的动作

    // 构造函数，用于创建TriggerAction对象
    public TriggerAction(final Trigger trigger, final CodeJassValue actionFunc, final int actionIndex) {
        this.trigger = trigger; // 初始化触发器对象
        this.actionFunc = actionFunc; // 初始化动作函数对象
        this.actionIndex = actionIndex; // 初始化动作索引
    }

    // 获取触发器对象的方法
    public Trigger getTrigger() {
        return this.trigger;
    }

    // 获取动作函数对象的方法
    public CodeJassValue getActionFunc() {
        return this.actionFunc;
    }

    // 获取动作索引的方法
    public int getActionIndex() {
        return this.actionIndex;
    }
}
