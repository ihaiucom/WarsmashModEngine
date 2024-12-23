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

// 定义一个名为BoolExprFilter的类，实现了TriggerBooleanExpression接口
public class BoolExprFilter implements TriggerBooleanExpression {
    // 声明一个私有的final类型的JassFunction变量，用于存储不接受参数且返回布尔值的函数
    private final JassFunction takesNothingReturnsBooleanFunction;

    // 构造函数，接收一个JassFunction类型的参数，该函数返回布尔值
    public BoolExprFilter(final JassFunction returnsBooleanFunction) {
        // 将传入的函数赋值给类的成员变量
        this.takesNothingReturnsBooleanFunction = returnsBooleanFunction;
    }

    // 实现TriggerBooleanExpression接口的evaluate方法，用于评估布尔表达式
    @Override
    public boolean evaluate(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
        // 声明一个JassValue类型的变量，用于存储函数调用的返回值
        final JassValue booleanJassReturnValue;
        // 如果函数为空，则直接返回false
        if (this.takesNothingReturnsBooleanFunction == null) {
            return false;
        }
        try {
            // 调用函数，并将结果赋值给booleanJassReturnValue
            booleanJassReturnValue = this.takesNothingReturnsBooleanFunction.call(Collections.EMPTY_LIST, globalScope,
                    triggerScope);
        }
        // 如果调用过程中发生异常，则抛出一个JassException异常
        catch (final Exception e) {
            throw new JassException(globalScope, "Exception during BoolExprFilter.evaluate()", e);
        }
        // 如果函数返回值为null，并且设置中允许在错误时继续执行，则返回false
        if ((booleanJassReturnValue == null) && JassSettings.CONTINUE_EXECUTING_ON_ERROR) {
            return false;
        }
        // 访问JassValue对象，将其转换为Boolean类型
        final Boolean booleanReturnValue = booleanJassReturnValue.visit(BooleanJassValueVisitor.getInstance());
        // 返回布尔值
        return booleanReturnValue.booleanValue();
    }

}
