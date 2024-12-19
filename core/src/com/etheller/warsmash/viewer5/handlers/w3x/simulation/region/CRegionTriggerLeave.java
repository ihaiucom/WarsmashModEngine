package com.etheller.warsmash.viewer5.handlers.w3x.simulation.region;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
public class CRegionTriggerLeave {
    // 全局作用域，用于触发器的全局操作
    private final GlobalScope globalScope;
    // 触发器实例
    private final Trigger trigger;
    // 触发器过滤条件
    private final TriggerBooleanExpression filter;

    /**
     * 构造函数，初始化触发器离开区域事件
     *
     * @param globalScope 全局作用域
     * @param trigger     触发器实例
     * @param filter      触发器过滤条件
     */
    public CRegionTriggerLeave(final GlobalScope globalScope, final Trigger trigger,
                               final TriggerBooleanExpression filter) {
        this.globalScope = globalScope;
        this.trigger = trigger;
        this.filter = filter;
    }

    /**
     * 触发离开区域事件
     *
     * @param unit   单位实例
     * @param region 区域实例
     */
    public void fire(final CUnit unit, final CRegion region) {
        // 创建事件作用域，表示单位离开区域的事件
        final CommonTriggerExecutionScope eventScope = CommonTriggerExecutionScope.unitLeaveRegionScope(
                JassGameEventsWar3.EVENT_GAME_LEAVE_REGION, this.trigger, TriggerExecutionScope.EMPTY, unit, region);
        // 将触发器加入全局作用域的队列中，等待执行
        this.globalScope.queueTrigger(this.filter,
                CommonTriggerExecutionScope.filterScope(TriggerExecutionScope.EMPTY, unit), this.trigger, eventScope,
                eventScope);
    }
}
