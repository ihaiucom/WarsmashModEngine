package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.root;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.root.CAbilityRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
/**
 * CBehaviorRoot类，继承自CAbstractRangedBehavior，表示一种根状态行为。
 */
public class CBehaviorRoot extends CAbstractRangedBehavior {
	private final CAbilityRoot abilityRoot;
	private int rootStartTick;
	private int rootFinishTick;

	/**
	 * 构造函数，初始化CBehaviorRoot实例。
	 *
	 * @param unit      相关的单位
	 * @param abilityRoot 关联的根能力
	 */
	public CBehaviorRoot(final CUnit unit, final CAbilityRoot abilityRoot) {
		super(unit);
		this.abilityRoot = abilityRoot;
	}

	/**
	 * 重置行为状态。
	 *
	 * @param pointTarget 目标点
	 * @return 返回重置后的行为
	 */
	public CAbstractRangedBehavior reset(final AbilityPointTarget pointTarget) {
		this.rootStartTick = -1;
		this.rootFinishTick = -1;
		return this.innerReset(pointTarget);
	}

	@Override
	/**
	 * 检查单位是否在目标范围内。
	 *
	 * @param simulation 当前的仿真状态
	 * @return 如果单位在范围内，则返回true
	 */
	public boolean isWithinRange(final CSimulation simulation) {
//		return ((AbilityPointTarget)target).dst2(unit.getX(), unit.getY()) <= 0.1;
		return this.unit.canReach(this.target.getX(), this.target.getY(), 0);
	}

	@Override
	/**
	 * 移动结束时的处理。
	 *
	 * @param game        当前的游戏仿真
	 * @param interrupted 是否被打断
	 */
	public void endMove(final CSimulation game, final boolean interrupted) {
	}

	@Override
	/**
	 * 行为开始时的处理。
	 *
	 * @param game 当前的游戏仿真
	 */
	public void begin(final CSimulation game) {
	}

	@Override
	/**
	 * 行为结束时的处理。
	 *
	 * @param game        当前的游戏仿真
	 * @param interrupted 是否被打断
	 */
	public void end(final CSimulation game, final boolean interrupted) {
	}

	@Override
	/**
	 * 获取高亮的订单ID。
	 *
	 * @return 高亮订单ID
	 */
	public int getHighlightOrderId() {
		return OrderIds.root;
	}

	@Override
	/**
	 * 更新行为状态。
	 *
	 * @param simulation          当前的仿真状态
	 * @param withinFacingWindow  是否在面对窗口内
	 * @return 返回更新后的行为
	 */
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		final float duration = this.abilityRoot.getDuration(); // 获取能力的持续时间
		if (this.rootStartTick == -1) { // 如果根开始刻度未设置
			// 加上动画奶酪的一半秒，短暂的延迟
			this.unit.setPoint(this.target.getX(), this.target.getY(), simulation.getWorldCollision(),
					simulation.getRegionManager()); // 设置单位位置
			this.rootStartTick = simulation.getGameTurnTick() // 计算并设置根开始刻度
					+ (int) (duration / WarsmashConstants.SIMULATION_STEP_TIME);
		} else if (simulation.getGameTurnTick() >= this.rootStartTick) { // 如果当前游戏刻度大于等于根开始刻度
			if (this.rootFinishTick == -1) { // 如果根结束刻度未设置
				this.unit.setFacing(simulation.getGameplayConstants().getRootAngle()); // 设置单位朝向
				this.abilityRoot.setRooted(true, this.unit, simulation); // 设置单位为扎根状态
				this.rootFinishTick = simulation.getGameTurnTick() // 计算并设置根结束刻度
						+ (int) (duration / WarsmashConstants.SIMULATION_STEP_TIME);
				this.unit.setAcceptingOrders(false); // 设置单位不接受命令
				this.unit.getUnitAnimationListener().playAnimationWithDuration(true, PrimaryTag.MORPH,
						SequenceUtils.EMPTY, duration, true); // 播放变形动画
			} else if (simulation.getGameTurnTick() >= this.rootFinishTick) { // 如果当前游戏刻度大于等于根结束刻度
				this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f,
						true); // 播放站立动画
				this.unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.ALTERNATE); // 添加次要标签
				this.unit.setAcceptingOrders(true); // 设置单位接受命令
				return this.unit.pollNextOrderBehavior(simulation); // 返回下一个命令行为
			}
		}

		return this; // 返回当前行为对象

	}

	@Override
	/**
	 * 在目标无效时更新行为。
	 *
	 * @param simulation 当前的仿真状态
	 * @return 返回下一个行为
	 */
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	/**
	 * 检查目标是否仍然有效。
	 *
	 * @param simulation 当前的仿真状态
	 * @return 如果目标有效，则返回true
	 */
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return true;
	}

	@Override
	/**
	 * 移动前重置状态。
	 *
	 * @param simulation 当前的仿真状态
	 */
	protected void resetBeforeMoving(final CSimulation simulation) {
	}

	@Override
	/**
	 * 是否可以被打断。
	 *
	 * @return 如果可以被打断，则返回true
	 */
	public boolean interruptable() {
		return true;
	}

}
