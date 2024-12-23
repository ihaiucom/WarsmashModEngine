package com.etheller.interpreter.ast.scope.trigger;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;

// 定义一个名为TriggerBooleanExpression的接口，用于在特定作用域内评估布尔表达式
public interface TriggerBooleanExpression {
    // evaluate方法接收两个参数：一个是全局作用域globalScope，另一个是触发器执行作用域triggerScope
    // 它返回一个布尔值，表示在给定作用域内布尔表达式的计算结果
    boolean evaluate(GlobalScope globalScope, TriggerExecutionScope triggerScope);
}
