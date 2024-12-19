package com.etheller.interpreter.ast.scope.variableevent;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
/**
 * 表示一个变量事件的类
 */
public class VariableEvent {
	// 存储触发器对象的引用
	private final Trigger trigger;
	// 存储限制操作的枚举值
	private final CLimitOp limitOp;
	// 存储与限制操作进行比较的双精度值
	private final double doubleValue;


	/**
	 * 构造函数，用于初始化VariableEvent实例
	 *
	 * @param trigger 触发器
	 * @param limitOp 限制操作
	 * @param doubleValue 与之比较的双精度值
	 */
	public VariableEvent(final Trigger trigger, final CLimitOp limitOp, final double doubleValue) {
		this.trigger = trigger;
		this.limitOp = limitOp;
		this.doubleValue = doubleValue;
	}

	/**
	 * 获取当前事件的触发器
	 *
	 * @return 触发器
	 */
	public Trigger getTrigger() {
		return this.trigger;
	}

	/**
	 * 获取当前事件的限制操作
	 *
	 * @return 限制操作
	 */
	public CLimitOp getLimitOp() {
		return this.limitOp;
	}

	/**
	 * 获取当前事件的双精度值
	 *
	 * @return 双精度值
	 */
	public double getDoubleValue() {
		return this.doubleValue;
	}

	/**
	 * 判断给定的实际值是否与事件的双精度值匹配
	 *
	 * @param realValue 实际值
	 * @return 是否匹配
	 */
	public boolean isMatching(final double realValue) {
		// 根据 limitOp 的值进行比较
		switch (this.limitOp) {
			// 如果 limitOp 是 EQUAL，则检查 doubleValue 是否等于 realValue
			case EQUAL:
				// 这里可能需要使用一个极小值（epsilon）来比较两个浮点数是否相等，以避免由于浮点数精度问题导致的错误比较
				// 但是，代码中没有指定默认的 epsilon 值，因此这可能是一个潜在的问题
				return this.doubleValue == realValue;
			// 如果 limitOp 是 GREATER_THAN，则检查 realValue 是否大于 doubleValue
			case GREATER_THAN:
				return realValue > this.doubleValue;
			// 如果 limitOp 是 GREATER_THAN_OR_EQUAL，则检查 realValue 是否大于或等于 doubleValue
			case GREATER_THAN_OR_EQUAL:
				return realValue >= this.doubleValue;
			// 如果 limitOp 是 LESS_THAN，则检查 realValue 是否小于 doubleValue
			case LESS_THAN:
				return realValue < this.doubleValue;
			// 如果 limitOp 是 LESS_THAN_OR_EQUAL，则检查 realValue 是否小于或等于 doubleValue
			case LESS_THAN_OR_EQUAL:
				return realValue <= this.doubleValue;
			// 如果 limitOp 是 NOT_EQUAL，则检查 realValue 是否不等于 doubleValue
			case NOT_EQUAL:
				return realValue != this.doubleValue;
			// 如果 limitOp 不是上述任何一种情况，则抛出异常
			default:
				throw new IllegalStateException();
		}

	}

	/**
	 * 触发事件，并在全局作用域中排队
	 *
	 * @param globalScope 全局作用域
	 */
	public void fire(final GlobalScope globalScope) {
		// 创建一个新的 TriggerExecutionScope 实例，用于执行触发器
		final TriggerExecutionScope triggerScope = new TriggerExecutionScope(this.trigger);
		// 将触发器加入到全局作用域的队列中，等待执行
		globalScope.queueTrigger(null, null, this.trigger, triggerScope, triggerScope);

	}
}
