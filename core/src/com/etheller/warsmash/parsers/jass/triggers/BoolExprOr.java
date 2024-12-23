package com.etheller.warsmash.parsers.jass.triggers;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;

// 定义一个名为BoolExprOr的类，实现了TriggerBooleanExpression接口
public class BoolExprOr implements TriggerBooleanExpression {
    // 定义两个私有成员变量，用于存储布尔表达式的两个操作数
    private final TriggerBooleanExpression operandA;
    private final TriggerBooleanExpression operandB;

    // 构造函数，接收两个TriggerBooleanExpression类型的参数，分别赋值给成员变量operandA和operandB
    public BoolExprOr(final TriggerBooleanExpression operandA, final TriggerBooleanExpression operandB) {
        this.operandA = operandA;
        this.operandB = operandB;
    }

    // 重写evaluate方法，该方法用于计算布尔表达式的值
    @Override
    public boolean evaluate(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
        // 返回两个操作数evaluate方法的结果进行逻辑或运算的结果
        // 如果任一操作数的evaluate方法返回true，则整个表达式的结果为true
        return this.operandA.evaluate(globalScope, triggerScope) || this.operandB.evaluate(globalScope, triggerScope);
    }
}
