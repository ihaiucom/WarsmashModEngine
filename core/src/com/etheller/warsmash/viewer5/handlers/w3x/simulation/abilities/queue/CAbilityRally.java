package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.AbstractCAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
/**
 * CAbilityRally类实现了一个单位的集结能力。
 */
public class CAbilityRally extends AbstractCAbility implements CLevelingAbility {

	/**
	 * 构造函数，创建一个CAbilityRally实例。
	 *
	 * @param handleId 能力的唯一标识符
	 */
	public CAbilityRally(final int handleId) {
		super(handleId, War3ID.fromString("ARal"));
	}

	/**
	 * 当能力被添加到单位时执行的逻辑。
	 *
	 * @param game 当前的游戏模拟
	 * @param unit 施放此能力的单位
	 */
	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		unit.setRallyPoint(unit);
	}

	/**
	 * 当能力从单位移除时触发的逻辑。
	 *
	 * @param game 当前的游戏模拟
	 * @param unit 施放此能力的单位
	 */
	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	/**
	 * 每个游戏周期调用，用于更新单位状态。
	 *
	 * @param game 当前的游戏模拟
	 * @param unit 施放此能力的单位
	 */
	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	/**
	 * 内部方法，用于检查能力是否可以使用。
	 *
	 * @param game 当前的游戏模拟
	 * @param unit 施放此能力的单位
	 * @param orderId 命令ID
	 * @param receiver 能力激活接收器
	 */
	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	/**
	 * 检查目标是否有效（对于CWidget类型）。
	 *
	 * @param game 当前的游戏模拟
	 * @param unit 施放此能力的单位
	 * @param orderId 命令ID
	 * @param target 目标小部件
	 * @param receiver 目标检查接收器
	 */
	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		switch (orderId) {
		case OrderIds.smart:
		case OrderIds.setrally:
			receiver.targetOk(target);
			break;
		default:
			receiver.orderIdNotAccepted();
			break;
		}
	}

	/**
	 * 检查目标是否有效（对于AbilityPointTarget类型）。
	 *
	 * @param game 当前的游戏模拟
	 * @param unit 施放此能力的单位
	 * @param orderId 命令ID
	 * @param target 目标点
	 * @param receiver 目标检查接收器
	 */
	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		switch (orderId) {
		case OrderIds.smart:
		case OrderIds.setrally:
			receiver.targetOk(target);
			break;
		default:
			receiver.orderIdNotAccepted();
			break;
		}
	}

	/**
	 * 检查无目标情况的有效性。
	 *
	 * @param game 当前的游戏模拟
	 * @param unit 施放此能力的单位
	 * @param orderId 命令ID
	 * @param receiver 目标检查接收器
	 */
	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	/**
	 * 在排队之前进行检查。
	 *
	 * @param game 当前的游戏模拟
	 * @param caster 施放此能力的单位
	 * @param orderId 命令ID
	 * @param target 目标
	 * @return 返回是否可以继续
	 */
	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		return true;
	}

	/**
	 * 开始执行能力（目标为CWidget）。
	 *
	 * @param game 当前的游戏模拟
	 * @param caster 施放此能力的单位
	 * @param orderId 命令ID
	 * @param target 目标小部件
	 * @return 返回行为
	 */
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		caster.setRallyPoint(target);
		return caster.pollNextOrderBehavior(game);
	}

	/**
	 * 开始执行能力（目标为AbilityPointTarget）。
	 *
	 * @param game 当前的游戏模拟
	 * @param caster 施放此能力的单位
	 * @param orderId 命令ID
	 * @param point 目标点
	 * @return 返回行为
	 */
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		caster.setRallyPoint(point);
		return caster.pollNextOrderBehavior(game);
	}

	/**
	 * 开始执行能力（无目标）。
	 *
	 * @param game 当前的游戏模拟
	 * @param caster 施放此能力的单位
	 * @param orderId 命令ID
	 * @return 返回行为
	 */
	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	/**
	 * 访问能力的访问者模式。
	 *
	 * @param visitor 访问者
	 * @return 返回访问者的结果
	 */
	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	/**
	 * 获取基础命令ID。
	 *
	 * @return 返回基础命令ID
	 */
	public int getBaseOrderId() {
		return OrderIds.setrally;
	}

	/**
	 * 当能力从队列取消时执行的逻辑。
	 *
	 * @param game 当前的游戏模拟
	 * @param unit 施放此能力的单位
	 * @param orderId 命令ID
	 */
	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	/**
	 * 获取能力级别。
	 *
	 * @return 返回当前级别
	 */
	@Override
	public int getLevel() {
		return 1; // TODO maybe less hacky solution
	}

	/**
	 * 设置能力级别。
	 *
	 * @param game 当前的游戏模拟
	 * @param unit 施放此能力的单位
	 * @param level 要设置的级别
	 */
	@Override
	public void setLevel(final CSimulation game, final CUnit unit, final int level) {
	}

	/**
	 * 当单位死亡时调用的逻辑。
	 *
	 * @param game 当前的游戏模拟
	 * @param cUnit 死亡的单位
	 */
	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

	/**
	 * 检查能力是否为物理能力。
	 *
	 * @return 返回是否为物理能力
	 */
	@Override
	public boolean isPhysical() {
		return false;
	}

	/**
	 * 检查能力是否为通用能力。
	 *
	 * @return 返回是否为通用能力
	 */
	@Override
	public boolean isUniversal() {
		return true;
	}

	/**
	 * 获取能力类别。
	 *
	 * @return 返回能力类别
	 */
	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.CORE;
	}

}

