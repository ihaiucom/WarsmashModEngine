package com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
// CTimerJass 类用于实现定时器功能，并处理与 Jass 脚本相关的事件流。
public class CTimerJass extends CTimer implements CHandle {
	private CodeJassValue handlerFunc;
	private final GlobalScope jassGlobalScope;
	private final int handleId;
	private final List<Trigger> eventTriggers = new ArrayList<>();

	// 构造函数，初始化 GlobalScope 和句柄ID
	public CTimerJass(final GlobalScope jassGlobalScope, final int handleId) {
		this.jassGlobalScope = jassGlobalScope;
		this.handleId = handleId;
	}

	// 设置处理函数
	public void setHandlerFunc(final CodeJassValue handlerFunc) {
		this.handlerFunc = handlerFunc;
	}

	@Override
	// 定时器触发时的处理逻辑
	public void onFire(final CSimulation simulation) {
		final CommonTriggerExecutionScope handlerScope = CommonTriggerExecutionScope.expiringTimer(null, this);
		// Snapshotting these values at the top... This is leaky and later I should make
		// a better solution, but I was having a problem with bj_stockUpdateTimer where
		// it would
		// modify its own state while firing, and I put this in as an ideological
		// safeguard so that
		// the handler func cannot append more triggers to ourself or change our
		// behavior when we
		// are only halfway done firing.
		final CodeJassValue handlerFunc = this.handlerFunc;
		final List<Trigger> eventTriggers = this.eventTriggers.isEmpty() ? Collections.emptyList()
				: new ArrayList<>(this.eventTriggers);
		try {
			if (handlerFunc != null) {
				this.jassGlobalScope.createThread(handlerFunc, handlerScope);
			}
		}
		catch (final Exception e) {
			throw new JassException(this.jassGlobalScope, "Exception during jass timer fire", e);
		}
		for (final Trigger trigger : eventTriggers) {
			final CommonTriggerExecutionScope executionScope = CommonTriggerExecutionScope.expiringTimer(trigger, this);
			this.jassGlobalScope.queueTrigger(null, null, trigger, executionScope, executionScope);
		}
	}

	// 添加事件触发器
	public void addEvent(final Trigger trigger) {
		this.eventTriggers.add(trigger);
	}

	// 移除事件触发器
	public void removeEvent(final Trigger trigger) {
		this.eventTriggers.remove(trigger);
	}

	@Override
	// 获取该定时器的句柄ID
	public int getHandleId() {
		return this.handleId;
	}
}

