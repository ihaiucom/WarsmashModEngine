package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;
// Buff状态修改器
public class StateModBuff {

	// 状态类型枚举
	private StateModBuffType buffType;
	// 值，1表示是，0表示否
	private float value;

	/**
	 * 构造函数，用于创建StateModBuff对象
	 * @param buffType 状态效果类型
	 * @param value 状态效果的值
	 */
	public StateModBuff(StateModBuffType buffType, float value) {
		super();
		this.buffType = buffType;
		this.value = value;
	}

	/**
	 * 获取状态效果类型
	 * @return 状态效果类型
	 */
	public StateModBuffType getBuffType() {
		return buffType;
	}

	/**
	 * 设置状态效果类型
	 * @param buffType 新的状态效果类型
	 */
	public void setBuffType(StateModBuffType buffType) {
		this.buffType = buffType;
	}

	/**
	 * 获取状态效果的值
	 * @return 状态效果的值
	 */
	public float getValue() {
		return value;
	}

	/**
	 * 设置状态效果的值
	 * @param value 新的状态效果的值
	 */
	public void setValue(float value) {
		this.value = value;
	}
}
