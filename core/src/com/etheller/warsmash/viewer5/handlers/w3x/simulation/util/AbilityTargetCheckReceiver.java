package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

/**
 * 表示能够接收目标检查结果的接口。
 * @param <TARGET_TYPE> 目标类型的泛型参数
 */
public interface AbilityTargetCheckReceiver<TARGET_TYPE> {

	/**
	 * 目标检查通过时调用的方法。
	 * @param target 被检查的目标
	 */
	void targetOk(TARGET_TYPE target);

	/**
	 * 当不是一个活跃能力时调用的方法。
	 */
	void notAnActiveAbility();

	/**
	 * 当订单ID不被接受时调用的方法。
	 */
	void orderIdNotAccepted();

	/**
	 * 当目标检查失败时调用的方法。
	 * @param commandStringErrorKey 错误的命令字符串键
	 */
	void targetCheckFailed(String commandStringErrorKey);

	/**
	 * 定义团队类型的枚举。
	 */
	public static enum TeamType {
		ALLIED, // 盟友
		ENEMY, // 敌人
		PLAYER_UNITS, // 玩家单位
		CONTROL,  // 控制单位
		NEUTRAL; // 中立单位
	}

	/**
	 * 定义目标类型的枚举。
	 */
	public static enum TargetType {
		UNIT, // 单个单位
		POINT, // 坐标点
		UNIT_OR_POINT,  // 单个单位或坐标点
		NO_TARGET // 没有目标
	}

}

