package com.etheller.warsmash.parsers.jass.triggers;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;

// 定义一个名为BoolExprAnd的类，实现了TriggerBooleanExpression接口
public class BoolExprAnd implements TriggerBooleanExpression {
    // 定义两个私有成员变量，用于存储布尔表达式的两个操作数
    private final TriggerBooleanExpression operandA;
    private final TriggerBooleanExpression operandB;

    // 构造函数，接收两个TriggerBooleanExpression类型的参数，分别赋值给成员变量operandA和operandB
    public BoolExprAnd(final TriggerBooleanExpression operandA, final TriggerBooleanExpression operandB) {
        this.operandA = operandA;
        this.operandB = operandB;
    }

    // 实现TriggerBooleanExpression接口中的evaluate方法
    @Override
    public boolean evaluate(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
        // 调用两个操作数的evaluate方法，并使用逻辑与运算符'&&'进行连接，返回最终结果
        // 这意味着只有当两个操作数都返回true时，整个表达式才返回true
        return this.operandA.evaluate(globalScope, triggerScope) && this.operandB.evaluate(globalScope, triggerScope);
    }
}
