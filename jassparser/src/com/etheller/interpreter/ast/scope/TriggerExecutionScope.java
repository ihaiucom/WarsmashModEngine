package com.etheller.interpreter.ast.scope;

import com.etheller.interpreter.ast.scope.trigger.Trigger;

// 定义一个名为 TriggerExecutionScope 的类
public class TriggerExecutionScope {
    // 定义一个公共的静态常量 EMPTY，它是 TriggerExecutionScope 类的一个实例，用于表示空的触发执行范围
    public static final TriggerExecutionScope EMPTY = new TriggerExecutionScope(null);

    // 定义一个私有的成员变量 triggeringTrigger，用于存储触发该执行范围的触发器
    private final Trigger triggeringTrigger;

    // 构造函数，接收一个 Trigger 类型的参数，用于初始化 triggeringTrigger 成员变量
    public TriggerExecutionScope(final Trigger triggeringTrigger) {
        this.triggeringTrigger = triggeringTrigger;
    }

    // 定义一个公共的方法 getTriggeringTrigger，用于获取触发该执行范围的触发器
    public Trigger getTriggeringTrigger() {
        return this.triggeringTrigger;
    }

}
