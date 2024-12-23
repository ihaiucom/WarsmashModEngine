package com.etheller.warsmash.parsers.jass.triggers;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;

// 定义一个名为BoolExprNot的类，实现了TriggerBooleanExpression接口
public class BoolExprNot implements TriggerBooleanExpression {
    // 声明一个私有的TriggerBooleanExpression类型的成员变量operand
    private final TriggerBooleanExpression operand;

    // 构造函数，接收一个TriggerBooleanExpression类型的参数，并将其赋值给成员变量operand
    public BoolExprNot(final TriggerBooleanExpression operand) {
        this.operand = operand;
    }

    // 重写evaluate方法，该方法用于计算布尔表达式的值
    @Override
    public boolean evaluate(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
        // 调用成员变量operand的evaluate方法，并返回其结果
        // 这里实现的是逻辑非操作，但是代码中并没有取反，可能是逻辑非的实现方式不同或者代码不完整
        return this.operand.evaluate(globalScope, triggerScope);
    }
}
