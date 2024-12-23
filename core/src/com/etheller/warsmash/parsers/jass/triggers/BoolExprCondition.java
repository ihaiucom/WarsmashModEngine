package com.etheller.warsmash.parsers.jass.triggers;

import java.util.Collections;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.interpreter.ast.util.JassSettings;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.BooleanJassValueVisitor;

// 定义一个名为BoolExprCondition的类，实现了TriggerBooleanExpression接口
public class BoolExprCondition implements TriggerBooleanExpression {
    // 定义一个私有的final类型的成员变量，类型为JassFunction，用于存储一个不接受参数且返回布尔值的函数
    private final JassFunction takesNothingReturnsBooleanFunction;

    // 构造函数，接收一个JassFunction类型的参数，该函数返回布尔值
    public BoolExprCondition(final JassFunction returnsBooleanFunction) {
        // 将传入的函数赋值给成员变量
        this.takesNothingReturnsBooleanFunction = returnsBooleanFunction;
    }

    // 实现TriggerBooleanExpression接口中的evaluate方法，用于计算布尔表达式的值
    @Override
    public boolean evaluate(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
        // 定义一个JassValue类型的变量，用于存储函数调用的返回值
        final JassValue booleanJassReturnValue;
        try {
            // 调用成员变量中存储的函数，并传入空的参数列表、全局作用域和触发器作用域
            booleanJassReturnValue = this.takesNothingReturnsBooleanFunction.call(Collections.EMPTY_LIST, globalScope,
                    triggerScope);
        }
        // 捕获函数调用过程中可能抛出的异常
        catch (final Exception e) {
            // 抛出一个新的JassException异常，包含全局作用域和原始异常信息
            throw new JassException(globalScope, "Exception during BoolExprCondition.evaluate()", e);
        }
        // 如果函数返回值为null，并且设置中允许在错误时继续执行，则返回false
        if ((booleanJassReturnValue == null) && JassSettings.CONTINUE_EXECUTING_ON_ERROR) {
            return false;
        }
        // 使用BooleanJassValueVisitor的实例来访问JassValue对象，并获取其布尔值
        final Boolean booleanReturnValue = booleanJassReturnValue.visit(BooleanJassValueVisitor.getInstance());
        // 返回布尔值
        return booleanReturnValue.booleanValue();
    }

}
