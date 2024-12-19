package com.etheller.warsmash.viewer5.handlers.w3x.simulation.region;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class CRegionTriggerEnter {
    // 全局作用域，用于触发器的全局管理
    private final GlobalScope globalScope;
    // 触发器实例
    private final Trigger trigger;
    // 触发条件表达式
    private final TriggerBooleanExpression filter;

    /**
     * 构造函数，初始化触发器进入区域事件的相关参数
     *
     * @param globalScope 全局作用域
     * @param trigger     触发器实例
     * @param filter      触发条件表达式
     */
    public CRegionTriggerEnter(final GlobalScope globalScope, final Trigger trigger,
                               final TriggerBooleanExpression filter) {
        this.globalScope = globalScope;
        this.trigger = trigger;
        this.filter = filter;
    }

    /**
     * 触发方法，当单位进入某个区域时调用
     *
     * @param unit   进入区域的单位
     * @param region 单位进入的区域
     */
    public void fire(final CUnit unit, final CRegion region) {
        // 创建触发器执行作用域，表示单位进入区域的事件
        final CommonTriggerExecutionScope eventScope = CommonTriggerExecutionScope.unitEnterRegionScope(
                JassGameEventsWar3.EVENT_GAME_ENTER_REGION, this.trigger, TriggerExecutionScope.EMPTY, unit, region);
        // 将触发条件、过滤作用域、触发器实例以及事件作用域加入全局作用域的触发队列
        this.globalScope.queueTrigger(this.filter,
                CommonTriggerExecutionScope.filterScope(TriggerExecutionScope.EMPTY, unit), this.trigger, eventScope,
                eventScope);
    }
}
