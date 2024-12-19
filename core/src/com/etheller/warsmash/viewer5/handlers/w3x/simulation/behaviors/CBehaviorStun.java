package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

/**
 * 该类表示一种行为状态：眩晕。
 */
public class CBehaviorStun implements CBehavior {

	private final CUnit unit;

	/**
	 * 构造函数，初始化单位。
	 * @param unit 被眩晕的单位
	 */
	public CBehaviorStun(final CUnit unit) {
		this.unit = unit;
	}

	@Override
	/**
	 * 获取高亮顺序ID。
	 * @return 眩晕的顺序ID
	 */
	public int getHighlightOrderId() {
		return OrderIds.stunned;
	}

	@Override
	/**
	 * 更新当前行为状态。
	 * @param game 当前游戏状态
	 * @return 返回当前行为实例
	 */
	public CBehavior update(final CSimulation game) {
		return this;
	}

	@Override
	/**
	 * 开始该行为状态。
	 * @param game 当前游戏状态
	 */
	public void begin(final CSimulation game) {
		this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f,
				true);
	}

	@Override
	/**
	 * 结束该行为状态。
	 * @param game 当前游戏状态
	 * @param interrupted 是否被打断
	 */
	public void end(final CSimulation game, final boolean interrupted) {

	}

	@Override
	/**
	 * 判断该行为是否可以被打断。
	 * @return 如果可以打断则返回true，否则返回false
	 */
	public boolean interruptable() {
		return false;
	}

	@Override
	/**
	 * 访问者模式，接受访问者。
	 * @param visitor 访问者实例
	 * @return 访问的结果
	 */
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}

}
