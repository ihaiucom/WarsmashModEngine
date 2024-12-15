package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;
/**
 * 非叠加状态增益类
 */
public class NonStackingStatBuff {
	public static final String ALLOW_STACKING_KEY = "STACK";

	private NonStackingStatBuffType buffType; // 状态增益类型
	private String stackingKey; // 叠加键
	private float value; // 增益值

	/**
	 * 构造函数，初始化非叠加状态增益属性
	 * @param buffType 状态增益类型
	 * @param stackingKey 叠加键
	 * @param value 增益值
	 */
	public NonStackingStatBuff(NonStackingStatBuffType buffType, String stackingKey, float value) {
		super();
		this.buffType = buffType;
		this.stackingKey = stackingKey;
		this.value = value;
	}

	/**
	 * 获取状态增益类型
	 * @return 状态增益类型
	 */
	public NonStackingStatBuffType getBuffType() {
		return buffType;
	}

	/**
	 * 设置状态增益类型
	 * @param buffType 状态增益类型
	 */
	public void setBuffType(NonStackingStatBuffType buffType) {
		this.buffType = buffType;
	}

	/**
	 * 获取叠加键
	 * @return 叠加键
	 */
	public String getStackingKey() {
		return stackingKey;
	}

	/**
	 * 设置叠加键
	 * @param stackingKey 叠加键
	 */
	public void setStackingKey(String stackingKey) {
		this.stackingKey = stackingKey;
	}

	/**
	 * 获取增益值
	 * @return 增益值
	 */
	public float getValue() {
		return value;
	}

	/**
	 * 设置增益值
	 * @param value 增益值
	 */
	public void setValue(float value) {
		this.value = value;
	}
}

