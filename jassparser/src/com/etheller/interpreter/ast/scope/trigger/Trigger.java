package com.etheller.interpreter.ast.scope.trigger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.JassValue;
// 定义一个触发器类，实现了CHandle接口
public class Trigger implements CHandle {
    // 静态变量，用于生成唯一的handleId，后续会被删除
    private static int STUPID_STATIC_TRIGGER_COUNT_DELETE_THIS_LATER = 452354453;
    // 触发器的唯一标识符
    private final int handleId = STUPID_STATIC_TRIGGER_COUNT_DELETE_THIS_LATER++;
    // 存储触发条件的列表
    private final List<TriggerBooleanExpression> conditions = new ArrayList<>();
    // 存储触发后执行动作的列表
    private final List<JassFunction> actions = new ArrayList<>();
    // 触发条件被评估的次数
    private int evalCount;
    // 触发动作被执行的次数
    private int execCount;
    // 触发器是否启用
    private boolean enabled = true;
    // 用于评估的触发执行范围
    private transient final TriggerExecutionScope triggerExecutionScope = new TriggerExecutionScope(this);
    // 是否在睡眠时等待
    private boolean waitOnSleeps = true;

    // 添加一个动作到动作列表，并返回其在列表中的索引
    public int addAction(final JassFunction function) {
        final int index = this.actions.size();
        this.actions.add(function);
        return index;
    }

    // 添加一个动作到动作列表，支持CodeJassValue类型的动作，并返回其在列表中的索引
    public int addAction(final CodeJassValue function) {
        final int index = this.actions.size();
        this.actions.add(new JassThreadActionFunc(function));
        return index;
    }

    // 添加一个条件到条件列表，并返回其在列表中的索引
    public int addCondition(final TriggerBooleanExpression boolexpr) {
        final int index = this.conditions.size();
        this.conditions.add(boolexpr);
        return index;
    }

    // 从条件列表中移除指定的条件
    public void removeCondition(final TriggerBooleanExpression boolexpr) {
        this.conditions.remove(boolexpr);
    }

    // 根据索引移除条件列表中的条件
    public void removeConditionAtIndex(final int conditionIndex) {
        this.conditions.remove(conditionIndex);
    }

    // 清空条件列表
    public void clearConditions() {
        this.conditions.clear();
    }

    // 获取触发条件被评估的次数
    public int getEvalCount() {
        return this.evalCount;
    }

    // 获取触发动作被执行的次数
    public int getExecCount() {
        return this.execCount;
    }

    // 评估所有条件，如果所有条件都为真，则返回true
    public boolean evaluate(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
        for (final TriggerBooleanExpression condition : this.conditions) {
            if (!condition.evaluate(globalScope, triggerScope)) {
                return false;
            }
        }
        return true;
    }

    // 执行所有动作
    public void execute(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
        if (!this.enabled) {
            return;
        }
        for (final JassFunction action : this.actions) {
            try {
                action.call(Collections.emptyList(), globalScope, triggerScope);
            }
            catch (final Exception e) {
                throw new JassException(globalScope, "Exception during Trigger action execute", e);
            }
        }
    }

    // 获取触发器是否启用
    public boolean isEnabled() {
        return this.enabled;
    }

    // 设置触发器是否启用
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    // 销毁触发器，目前为空实现
    public void destroy() {

    }

    // 重置触发器，清空动作和条件列表，重置计数器和启用状态
    public void reset() {
        this.actions.clear();
        this.conditions.clear();
        this.evalCount = 0;
        this.execCount = 0;
        this.enabled = true;
        this.waitOnSleeps = true;
    }

    // 设置是否在睡眠时等待
    public void setWaitOnSleeps(final boolean waitOnSleeps) {
        this.waitOnSleeps = waitOnSleeps;
    }

    // 获取是否在睡眠时等待的状态
    public boolean isWaitOnSleeps() {
        return this.waitOnSleeps;
    }

    // 实现CHandle接口的方法，返回触发器的handleId
    @Override
    public int getHandleId() {
        return this.handleId;
    }

    // 内部类，实现了JassFunction接口，用于处理特定的动作
    private final class JassThreadActionFunc implements JassFunction {
        private final CodeJassValue codeJassValue;

        // 构造函数，接收一个CodeJassValue类型的参数
        public JassThreadActionFunc(final CodeJassValue codeJassValue) {
            this.codeJassValue = codeJassValue;
        }

        // 实现JassFunction接口的call方法，执行特定的动作
        @Override
        public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
                final TriggerExecutionScope triggerScope) {
            final JassThread triggerThread = globalScope.createThread(this.codeJassValue, triggerScope);
            globalScope.runThreadUntilCompletion(triggerThread);
            // 如果设置为在睡眠时等待，并且线程还未完成，则将线程加入队列
            if (isWaitOnSleeps() && (triggerThread.instructionPtr != -1)) {
                globalScope.queueThread(triggerThread);
            }
            return null;
        }

    }
}
