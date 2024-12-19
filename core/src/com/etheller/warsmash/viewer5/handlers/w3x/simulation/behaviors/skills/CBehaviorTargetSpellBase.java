package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveAndTargetableVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
// 技能--有目标施法 行为
public class CBehaviorTargetSpellBase extends CAbstractRangedBehavior {
	protected final CAbilitySpellBase ability; // 施法能力
	private final AbilityTargetStillAliveAndTargetableVisitor stillAliveVisitor; // 检查目标是否仍然有效的访问者
	private int castStartTick = 0; // 施法开始的时间滴答
	private boolean doneEffect = false; // 施法效果是否完成的标志
	private boolean channeling = false; // 是否正在引导施法的标志

	// 构造函数，初始化目标施法行为
	public CBehaviorTargetSpellBase(final CUnit unit, final CAbilitySpellBase ability) {
		super(unit);
		this.ability = ability;
		this.stillAliveVisitor = new AbilityTargetStillAliveAndTargetableVisitor();
	}

	// 重置目标施法行为
	public CBehaviorTargetSpellBase reset(final CWidget target) {
		innerReset(target, false);
		this.castStartTick = 0;
		this.doneEffect = false;
		this.channeling = false;
		return this;
	}

	// 重置目标施法行为
	public CBehaviorTargetSpellBase reset(final AbilityPointTarget target) {
		innerReset(target, false);
		this.castStartTick = 0;
		this.doneEffect = false;
		this.channeling = false;
		return this;
	}

	// 检查目标是否在施法范围内
	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		// 正在持续施法
		if (this.channeling) {
			return true; // 引导开始后不要逃跑
		}
		// 施法距离
		final float castRange = this.ability.getCastRange();
		// 目标是否在施法范围内
		return this.unit.canReach(this.target, castRange);
	}

	// 更新施法行为
	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		// 播放施法动画：
		this.unit.getUnitAnimationListener().playAnimation(false, this.ability.getCastingPrimaryTag(),
				this.ability.getCastingSecondaryTags(), 1.0f, true);
		//记录施法开始的时间戳：
		if (this.castStartTick == 0) {
			this.castStartTick = simulation.getGameTurnTick();
		}
		// 计算自施法开始以来经过的时间滴答数
		final int ticksSinceCast = simulation.getGameTurnTick() - this.castStartTick;
		// 施法效果时间点
		final int castPointTicks =
				(int) (this.unit.getUnitType().getCastPoint() / WarsmashConstants.SIMULATION_STEP_TIME);
		// 施法后摇时间点
		final int backswingTicks =
				(int) (this.unit.getUnitType().getCastBackswingPoint() / WarsmashConstants.SIMULATION_STEP_TIME);
		// 到了时间效果时间点或者后摇时间点
		if ((ticksSinceCast >= castPointTicks) || (ticksSinceCast >= backswingTicks)) {
			boolean wasEffectDone = this.doneEffect;
			boolean wasChanneling = this.channeling;
			// 施法效果还没执行
			if (!wasEffectDone) {
				// 设置施法效果完成标志
				this.doneEffect = true;
				// 检测魔法值是否够消耗，如果够就消耗魔法值，否则返回false
				if (!this.unit.chargeMana(this.ability.getManaCost())) {// 魔法值不够
					// 显示魔法不足的错误提示,并返回下一个命令
					simulation.getCommandErrorListener().showInterfaceError(this.unit.getPlayerIndex(),
							CommandStringErrorKeys.NOT_ENOUGH_MANA);
					return this.unit.pollNextOrderBehavior(simulation);
				}
				// 技能开始冷却
				this.unit.beginCooldown(simulation, this.ability.getCode(), this.ability.getCooldown());
				// 执行施法效果
				this.channeling = this.ability.doEffect(simulation, this.unit, this.target);
				// 如果是持续施法
				if (this.channeling) {
					// 播放持续施法音效
					simulation.unitLoopSoundEffectEvent(this.unit, this.ability.getAlias());
				}
				else {
					// 播放施法音效
					simulation.unitSoundEffectEvent(this.unit, this.ability.getAlias());
				}
			}
			// 执行持续施法Tick，并返回是否结束持续施法
			this.channeling = this.channeling && this.ability.doChannelTick(simulation, this.unit, this.target);
			// 施法效果完成，并且停止持续施法了
			if (wasEffectDone && wasChanneling && !this.channeling) {
				// 结束施法逻辑
				endChannel(simulation, false);
			}
		}
		// 到了后摇时间点，并且没有持续施法了，则结束施法，执行下一个命令
		if ((ticksSinceCast >= backswingTicks) && !this.channeling) {
			return this.unit.pollNextOrderBehavior(simulation);
		}
		return this;
	}

	// 更新无效目标的施法行为
	@Override
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		return this.unit.pollNextOrderBehavior(simulation);
	}

	// 检查目标是否仍然有效
	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		/*
		 * BELOW: "doneEffect" allows us to channel "at" something that died, if you hit
		 * a bug with that, then fix it here
		 */
		return this.doneEffect || this.target.visit(this.stillAliveVisitor.reset(simulation, this.unit,
				this.ability.getTargetsAllowed()));
	}

	// 移动前重置状态
	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {
		this.castStartTick = 0;
	}

	// 开始施法
	@Override
	public void begin(final CSimulation game) {
	}

	// 结束施法
	@Override
	public void end(final CSimulation game, final boolean interrupted) {
		checkEndChannel(game, interrupted);
	}

	// 结束施法移动
	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {
		checkEndChannel(game, interrupted);
	}

	// 检查结束施法
	private void checkEndChannel(final CSimulation game, final boolean interrupted) {
		// 如果正在持续性施法， 就结束施法
		if (this.channeling) {
			this.channeling = false;
			endChannel(game, interrupted);
		}
	}

	// 结束施法逻辑
	private void endChannel(CSimulation game, boolean interrupted) {
		// 停止施法音响
		game.unitStopSoundEffectEvent(this.unit, this.ability.getAlias());
		// 执行施法效果结束逻辑
		this.ability.doChannelEnd(game, this.unit, this.target, interrupted);
	}

	// 获取高亮目标的订单ID
	@Override
	public int getHighlightOrderId() {
		return this.ability.getBaseOrderId();
	}

	// 获取施法能力
	public CAbilitySpellBase getAbility() {
		return this.ability;
	}

	// 检查是否可被打断
	@Override
	public boolean interruptable() {
		return true;
	}
}
