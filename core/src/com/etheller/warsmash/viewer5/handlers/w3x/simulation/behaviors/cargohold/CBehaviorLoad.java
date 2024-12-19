package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.cargohold;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityCargoHold;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityLoad;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveAndTargetableVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
/**
 * 货仓--装载行为
 */
public class CBehaviorLoad extends CAbstractRangedBehavior {
	private final CAbilityLoad ability; // 装载能力
	private final AbilityTargetStillAliveAndTargetableVisitor stillAliveVisitor; // 有效目标检测（活得目标）

	/**
	 * 构造函数，初始化 CBehaviorLoad 对象。
	 *
	 * @param unit 执行行为的单位
	 * @param ability 加载能力
	 */
	public CBehaviorLoad(final CUnit unit, final CAbilityLoad ability) {
		super(unit);
		this.ability = ability;
		this.stillAliveVisitor = new AbilityTargetStillAliveAndTargetableVisitor();
	}

	/**
	 * 重置行为并设置目标。
	 *
	 * @param target 目标小部件
	 * @return 当前行为对象
	 */
	public CBehaviorLoad reset(final CWidget target) {
		innerReset(target, false);
		return this;
	}

	@Override
	/**
	 * 检查单位是否在加载范围内。
	 *
	 * @param simulation 当前模拟情况
	 * @return 如果在范围内，则返回 true
	 */
	public boolean isWithinRange(final CSimulation simulation) {
		// 获取货仓技能
		final CAbilityCargoHold cargoData = this.unit.getCargoData();
		// 获取装载范围
		final float castRange = cargoData.getCastRange();
		// 检测能否到达目标范围
		return this.unit.canReach(this.target, castRange);
	}

	@Override
	/**
	 * 到达目标范围，根据目标状态进行装载。
	 *
	 * @param simulation 当前模拟情况
	 * @param withinFacingWindow 是否在面朝窗口内
	 * @return 下一个行为
	 */
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		// 获取货仓技能
		final CAbilityCargoHold cargoData = this.unit.getCargoData();
		final CUnit targetUnit = (CUnit) this.target;
		// 获取货仓容量剩余空间是否可以装载目标需要的容量
		if (cargoData.hasCapacity(targetUnit.getUnitType().getCargoCapacity())) {
			// 播放声音效果
			simulation.unitSoundEffectEvent(this.unit, cargoData.getAlias());
			// 装载目标
			cargoData.addUnit(this.unit, targetUnit);
			// 隐藏目标
			targetUnit.setHidden(true);
			// 暂停目标
			targetUnit.setPaused(true);
		}
		else {
			// 报错：无法装载目标
			simulation.getCommandErrorListener().showInterfaceError(this.unit.getPlayerIndex(), CommandStringErrorKeys.UNABLE_TO_LOAD_TARGET);
		}
		// 切换到下一个行为
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	/**
	 * 处理无效目标的更新逻辑。
	 *
	 * @param simulation 当前模拟情况
	 * @return 下一个行为
	 */
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	/**
	 * 检查目标是否仍然有效。
	 *
	 * @param simulation 当前模拟情况
	 * @return 如果目标有效，则返回 true
	 */
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return this.target.visit(
				this.stillAliveVisitor.reset(simulation, this.unit, this.unit.getCargoData().getTargetsAllowed()));
	}

	@Override
	/**
	 * 在移动前重置状态，当前不做任何操作。
	 *
	 * @param simulation 当前模拟情况
	 */
	protected void resetBeforeMoving(final CSimulation simulation) {
	}

	@Override
	/**
	 * 行为开始时触发。
	 *
	 * @param game 当前游戏实例
	 */
	public void begin(final CSimulation game) {
	}

	@Override
	/**
	 * 行为结束时触发，可以指定是否被打断。
	 *
	 * @param game 当前游戏实例
	 * @param interrupted 是否被打断
	 */
	public void end(final CSimulation game, final boolean interrupted) {
	}

	@Override
	/**
	 * 移动结束时的处理。
	 *
	 * @param game 当前游戏实例
	 * @param interrupted 是否被打断
	 */
	public void endMove(final CSimulation game, final boolean interrupted) {
	}

	@Override
	/**
	 * 获取高亮顺序 ID。
	 *
	 * @return 高亮顺序 ID
	 */
	public int getHighlightOrderId() {
		return OrderIds.load;
	}

	@Override
	/**
	 * 检查行为是否可被打断。
	 *
	 * @return 如果可打断，返回 true
	 */
	public boolean interruptable() {
		return true;
	}
}
