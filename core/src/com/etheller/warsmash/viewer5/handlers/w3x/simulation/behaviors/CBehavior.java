package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;

/**
 * CBehavior 接口定义了游戏中单位行为的基本操作。
 * 这个接口允许实现不同的行为策略，以便在游戏模拟中执行相应的操作。
 */
public interface CBehavior {
	/**
	 * Executes one step of game simulation of the current order, and then returns
	 * the next behavior for the unit after the result of the update cycle.
	 *
	 *执行当前订单的游戏模拟的一个步骤，然后返回
	 *更新周期结果后该单元的下一个行为。
	 *
	 * @return
	 */
	CBehavior update(CSimulation game);

	/**
	 * 开始执行行为的方法，通常用于初始化或预处理操作。
	 *
	 * @param game 当前的游戏模拟实例
	 */
	void begin(CSimulation game);

	/**
	 * 结束行为的方法，用于清理或收尾操作。
	 *
	 * @param game 当前的游戏模拟实例
	 * @param interrupted 是否被中断
	 */
	void end(CSimulation game, boolean interrupted);

	/**
	 * 获取高亮订单 ID的方法，通常用于标记或突出显示特定的行为。
	 *
	 * @return 高亮订单 ID
	 */
	int getHighlightOrderId();

	/**
	 * 检查当前行为是否可以中断的方法。
	 *
	 * @return 如果可中断则返回 true， 否则返回 false
	 */
	boolean interruptable();
	
	CBehaviorCategory getBehaviorCategory();

	/**
	 * 访问者模式方法，允许对CBehavior进行操作。
	 *
	 * @param visitor 行为访问者
	 * @param <T> 返回类型
	 * @return 类型 T 的结果
	 */
	public <T> T visit(CBehaviorVisitor<T> visitor);
}
