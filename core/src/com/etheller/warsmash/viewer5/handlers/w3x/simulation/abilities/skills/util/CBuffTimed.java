package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractCBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;
/**
 * 持续时间Buff抽象类
 */
public abstract class CBuffTimed extends AbstractCBuff {
	// 特效
	private SimulationRenderComponent fx;

	// 持续时间
	private final float duration;

	// 效果失效的刻度或者时间点
	private int expireTick;


	/**
	 * 构造函数，用于初始化 CBuffTimed 对象。
	 *
	 * @param handleId 处理 ID
	 * @param code 增益代码
	 * @param alias 增益别名
	 * @param duration 增益持续时间
	 */
	public CBuffTimed(final int handleId, final War3ID code, final War3ID alias, final float duration) {
		super(handleId, code, alias);
		// 持续时间
		this.duration = duration;
	}

	/**
	 * 抽象方法，在增益添加时调用。
	 *
	 * @param game 当前游戏实例
	 * @param unit 增益施加的单位
	 */
	protected abstract void onBuffAdd(final CSimulation game, final CUnit unit);

	/**
	 * 抽象方法，在增益移除时调用。
	 *
	 * @param game 当前游戏实例
	 * @param unit 增益施加的单位
	 */
	protected abstract void onBuffRemove(final CSimulation game, final CUnit unit);

	@Override
	/**
	 * 增益添加时的处理逻辑，负责创建增益效果并计算到期时间。
	 */
	public void onAdd(final CSimulation game, final CUnit unit) {
		// 当添加增益效果时调用此方法
		onBuffAdd(game, unit);

		// 在指定单位上创建一个持续的法术效果
		// getAlias() 返回效果的别名
		// CEffectType.TARGET 表示效果作用于目标
		this.fx = game.createPersistentSpellEffectOnUnit(unit, getAlias(), CEffectType.TARGET);

		// 计算增益效果的持续时间（以游戏刻为单位）
		// this.duration 是增益效果的总持续时间
		// WarsmashConstants.SIMULATION_STEP_TIME 是每个游戏刻的时间长度
		final int durationTicks = (int) (this.duration / WarsmashConstants.SIMULATION_STEP_TIME);

		// 设置增益效果的过期时间（游戏刻）
		// game.getGameTurnTick() 获取当前游戏刻
		// this.expireTick 是增益效果过期的游戏刻
		this.expireTick = game.getGameTurnTick() + durationTicks;

	}

	@Override
	/**
	 * 增益移除时的处理逻辑，负责移除增益效果。
	 */
	public void onRemove(final CSimulation game, final CUnit unit) {
		onBuffRemove(game, unit);
		this.fx.remove();
	}

	@Override
	/**
	 * 每个游戏时间步的处理，检查增益是否过期。
	 */
	public void onTick(final CSimulation game, final CUnit caster) {
		// 获取当前游戏刻度
		final int currentTick = game.getGameTurnTick();
		// 如果当前刻度大于或等于此效果的过期刻度
		if (currentTick >= this.expireTick) {
			// 从游戏中移除此效果
			caster.remove(game, this);
		}

	}

	@Override
	/**
	 * 单位死亡时的处理，移除增益。
	 */
	public void onDeath(final CSimulation game, final CUnit cUnit) {
		cUnit.remove(game, this);
	}

	@Override
	/**
	 * 取消增益队列时的处理。
	 */
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	/**
	 * 开始无目标的增益效果。
	 */
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	@Override
	/**
	 * 开始对目标点的增益效果。
	 */
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	@Override
	/**
	 * 开始无目标的增益效果（不带目标）。
	 */
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	@Override
	/**
	 * 检查目标是否合法，处理无目标情况。
	 */
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	/**
	 * 检查目标是否合法，处理带目标点的情况。
	 */
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	/**
	 * 检查无目标情况的合法性。
	 */
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	/**
	 * 内部检查增益的可用性。
	 */
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();
	}

	@Override
	/**
	 * 返回增益的最大持续时间。
	 */
	public float getDurationMax() {
		return this.duration;
	}

	@Override
	/**
	 * 返回增益剩余的持续时间。
	 */
	public float getDurationRemaining(final CSimulation game, final CUnit unit) {
		// 获取当前游戏刻度
		  final int currentTick = game.getGameTurnTick();
		  // 计算剩余时间，确保不会是负数
		  final int remaining = Math.max(0, this.expireTick - currentTick);
		  // 返回剩余时间乘以模拟步进时间，得到剩余时间的毫秒表示
		  return remaining * WarsmashConstants.SIMULATION_STEP_TIME;
	}
}
