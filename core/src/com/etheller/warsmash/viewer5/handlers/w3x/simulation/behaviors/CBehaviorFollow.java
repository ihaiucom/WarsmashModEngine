package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;
/**
 * CBehaviorFollow 类实现了一种跟随行为的行为逻辑。
 */
public class CBehaviorFollow extends CAbstractRangedBehavior {

	private int higlightOrderId;
	private boolean justAutoAttacked = false;

	/**
	 * 构造函数，初始化跟随行为。
	 * @param unit 关联的单位
	 */
	public CBehaviorFollow(final CUnit unit) {
		super(unit);
	}

	/**
	 * 重置行为，设置高亮顺序ID并恢复内置状态。
	 * @param higlightOrderId 高亮顺序ID
	 * @param target 目标小部件
	 * @return 更新后的行为
	 */
	public CBehavior reset(final int higlightOrderId, final CWidget target) {
		this.higlightOrderId = higlightOrderId;
		return innerReset(target);
	}

	@Override
	/**
	 * 获取当前高亮顺序ID。
	 * @return 高亮顺序ID
	 */
	public int getHighlightOrderId() {
		return this.higlightOrderId;
	}

	@Override
	/**
	 * 检查单位是否在攻击范围内。
	 * @param simulation 当前仿真状态
	 * @return 是否在范围内
	 */
	public boolean isWithinRange(final CSimulation simulation) {
		// 自动获取目标的方法： 检测自动施法技能是否攻击目标， 检测普攻是否攻击目标
		if (this.justAutoAttacked = this.unit.autoAcquireTargets(simulation, false)) {
			return true;
		}
		// 能否到达指定目标范围内
		return this.unit.canReach(this.target, this.unit.getAcquisitionRange());
	}

	@Override
	/**
	 * 已经到达范围内，更新逻辑
	 * @param simulation 当前仿真状态
	 * @param withinFacingWindow 是否在面向窗口内
	 * @return 当前行为
	 */
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {

		// 播放待机动画
		this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f, false);
		return this;
	}

	@Override
	/**
	 * 处理无效目标的更新。
	 * @param simulation 当前仿真状态
	 * @return 下一个行为
	 */
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		// 目标无效的情况， 执行停止行为
		this.unit.setDefaultBehavior(this.unit.getStopBehavior());
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	/**
	 * 检查目标是否仍然有效。
	 * @param simulation 当前仿真状态
	 * @return 目标是否仍然有效
	 */
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		// 移动行为调 isWithinRange 方法， 检测到可以自动攻击；切换会寻路行为时，这里又重置移动行为目标
		if (this.justAutoAttacked) {
			this.justAutoAttacked = false;
			this.unit.getMoveBehavior().reset(this.target, this, false);
		}
		return this.target.visit(AbilityTargetStillAliveVisitor.INSTANCE);
	}

	@Override
	/**
	 * 移动前的重置操作。
	 * @param simulation 当前仿真状态
	 */
	protected void resetBeforeMoving(final CSimulation simulation) {

	}

	@Override
	/**
	 * 开始行为时的操作。
	 * @param game 当前游戏状态
	 */
	public void begin(final CSimulation game) {

	}

	@Override
	/**
	 * 行为结束时的操作。
	 * @param game 当前游戏状态
	 * @param interrupted 是否被中断
	 */
	public void end(final CSimulation game, final boolean interrupted) {

	}

	@Override
	/**
	 * 结束移动时的操作。
	 * @param game 当前游戏状态
	 * @param interrupted 是否被中断
	 */
	public void endMove(final CSimulation game, final boolean interrupted) {

	}

	@Override
	/**
	 * 检查行为是否可被中断。
	 * @return 是否可中断
	 */
	public boolean interruptable() {
		return true;
	}

}

