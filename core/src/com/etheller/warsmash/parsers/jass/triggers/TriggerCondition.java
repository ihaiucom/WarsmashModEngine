package com.etheller.warsmash.parsers.jass.triggers;

import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;

// 定义一个触发条件类，用于存储和处理游戏中的触发条件
public class TriggerCondition {
    // 存储布尔表达式，用于评估触发条件是否满足
    private final TriggerBooleanExpression boolexpr;
    // 存储触发器对象，该触发器包含此条件
    private final Trigger trigger;
    // 存储条件的索引，用于标识在触发器中的位置
    private final int conditionIndex;

    // 构造函数，初始化触发条件对象
    public TriggerCondition(final TriggerBooleanExpression boolexpr, final Trigger trigger, final int index) {
        this.boolexpr = boolexpr; // 设置布尔表达式
        this.trigger = trigger; // 设置触发器
        this.conditionIndex = index; // 设置条件索引
    }

    // 获取布尔表达式的方法
    public TriggerBooleanExpression getBoolexpr() {
        return this.boolexpr;
    }

    // 获取触发器对象的方法
    public Trigger getTrigger() {
        return this.trigger;
    }

    // 获取条件索引的方法
    public int getConditionIndex() {
        return this.conditionIndex;
    }
}
