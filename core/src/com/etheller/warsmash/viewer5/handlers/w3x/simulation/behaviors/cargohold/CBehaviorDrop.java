package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.cargohold;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityCargoHold;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityDrop;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
/**
 * 货仓--卸载行为
 */
public class CBehaviorDrop extends CAbstractRangedBehavior {
	private final CAbilityDrop ability;
	private int lastDropTick = 0;

	/**
	 * 构造函数，初始化单位和掉落能力。
	 *
	 * @param unit   关联的单位
	 * @param ability 相关掉落能力
	 */
	public CBehaviorDrop(final CUnit unit, final CAbilityDrop ability) {
		super(unit);
		this.ability = ability;
	}

	/**
	 * 重置行为，同时指定目标。
	 *
	 * @param target 目标点
	 * @return 当前行为对象
	 */
	public CBehaviorDrop reset(final AbilityPointTarget target) {
		innerReset(target, false);
		return this;
	}

	@Override
	/**
	 * 检查单位是否在施法范围内。
	 *
	 * @param simulation 当前模拟环境
	 * @return 是否在范围内
	 */
	public boolean isWithinRange(final CSimulation simulation) {
		final float castRange = this.ability.getCastRange();
		// 是否可以到达目标范围
		return this.unit.canReach(this.target, castRange);
	}

	@Override
	/**
	 * 到达目标范围，处理掉落逻辑。
	 *
	 * @param simulation 当前模拟环境
	 * @param withinFacingWindow 是否在面对窗口内
	 * @return 更新后的行为对象
	 */
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		// 获取当前有效帧
		final int gameTurnTick = simulation.getGameTurnTick();
		// 计算上一次卸载的时间间隔
		final int deltaTicks = gameTurnTick - this.lastDropTick;
		// 获取货仓技能
		final CAbilityCargoHold cargoData = this.unit.getCargoData();
		// 如果货仓已经是空的，则直接执行一个一个命令行为
		if (cargoData.isEmpty()) {
			return this.unit.pollNextOrderBehavior(simulation);
		}
		// TODO i do a nonstandard Math.ceil() here to make this one feel a bit slower
		// 卸载的冷却时间
		final float durationTicks = (int) Math.ceil(cargoData.getDuration() / WarsmashConstants.SIMULATION_STEP_TIME);
		// 过了卸载冷却时间
		if (deltaTicks >= durationTicks) {
			// 卸载第一个单位
			cargoData.dropUnitByIndex(simulation, unit, 0);
			// 计入卸载时间帧
			this.lastDropTick = gameTurnTick;
		}
		return this;
	}

	@Override
	/**
	 * 当目标无效时更新行为。
	 *
	 * @param simulation 当前模拟环境
	 * @return 更新后的行为对象
	 */
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		// 执行下一个命令
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	/**
	 * 检查目标是否仍然有效。
	 *
	 * @param simulation 当前模拟环境
	 * @return 目标是否有效
	 */
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return true;
	}

	@Override
	/**
	 * 移动前重置行为。
	 *
	 * @param simulation 当前模拟环境
	 */
	protected void resetBeforeMoving(final CSimulation simulation) {
	}

	@Override
	/**
	 * 开始行为。
	 *
	 * @param game 当前游戏环境
	 */
	public void begin(final CSimulation game) {
	}

	@Override
	/**
	 * 结束行为。
	 *
	 * @param game 当前游戏环境
	 * @param interrupted 是否被中断
	 */
	public void end(final CSimulation game, final boolean interrupted) {
	}

	@Override
	/**
	 * 结束移动。
	 *
	 * @param game 当前游戏环境
	 * @param interrupted 是否被中断
	 */
	public void endMove(final CSimulation game, final boolean interrupted) {
	}

	@Override
	/**
	 * 获取高亮订单 ID。
	 *
	 * @return 高亮订单 ID
	 */
	public int getHighlightOrderId() {
		return OrderIds.unloadall;
	}

	@Override
	/**
	 * 检查行为是否可被打断。
	 *
	 * @return 是否可打断
	 */
	public boolean interruptable() {
		return true;
	}
}

