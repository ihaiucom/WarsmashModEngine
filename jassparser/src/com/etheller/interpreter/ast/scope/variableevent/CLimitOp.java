package com.etheller.interpreter.ast.scope.variableevent;

import com.etheller.interpreter.ast.util.CHandle;

public enum CLimitOp implements CHandle {
	/**
	 * 小于操作符
	 */
	LESS_THAN,

	/**
	 * 小于或等于操作符
	 */
	LESS_THAN_OR_EQUAL,

	/**
	 * 等于操作符
	 */
	EQUAL,

	/**
	 * 大于或等于操作符
	 */
	GREATER_THAN_OR_EQUAL,

	/**
	 * 大于操作符
	 */
	GREATER_THAN,

	/**
	 * 不等于操作符
	 */
	NOT_EQUAL;

	public static CLimitOp[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
