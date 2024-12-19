package com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;

public class CTimerNativeEvent extends CTimer {
    // 全局作用域对象，用于触发事件
    private final GlobalScope jassGlobalScope;
    // 触发器对象，定义了何时触发事件
    private final Trigger trigger;

    /**
     * 构造函数，初始化全局作用域和触发器
     * @param jassGlobalScope 全局作用域对象
     * @param trigger 触发器对象
     */
    public CTimerNativeEvent(final GlobalScope jassGlobalScope, final Trigger trigger) {
        this.jassGlobalScope = jassGlobalScope;
        this.trigger = trigger;
    }

    /**
     * 当计时器触发时调用的方法
     * @param simulation 模拟环境对象
     */
    @Override
    public void onFire(final CSimulation simulation) {
        // 创建触发器执行作用域
        final TriggerExecutionScope triggerScope = new TriggerExecutionScope(this.trigger);
        // 将触发器加入到全局作用域的事件队列中
        this.jassGlobalScope.queueTrigger(null, null, this.trigger, triggerScope, triggerScope);
    }

}

