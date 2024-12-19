package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
// 技能--无目标施法 行为
public class CBehaviorNoTargetSpellBase implements CBehavior {
	protected final CUnit unit; // 施法单位
	protected final CAbilitySpellBase ability; // 施法能力
	private int castStartTick = 0; // 开始施法的时间戳
	private boolean doneEffect = false; // 施法效果是否完成
	private boolean channeling = true; // 是否正在引导施法

	// 构造函数，初始化施法单位和能力
	public CBehaviorNoTargetSpellBase(final CUnit unit, final CAbilitySpellBase ability) {
		this.unit = unit;
		this.ability = ability;
	}

	// 重置施法状态
	public CBehaviorNoTargetSpellBase reset() {
		this.castStartTick = 0;
		this.doneEffect = false;
		this.channeling = true;
		return this;
	}

	// 更新施法状态
	@Override
	public CBehavior update(final CSimulation simulation) {
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
		final int castPointTicks = (int) (this.unit.getUnitType().getCastPoint()
				/ WarsmashConstants.SIMULATION_STEP_TIME);
		// 施法后摇时间点
		final int backswingTicks = (int) (this.unit.getUnitType().getCastBackswingPoint()
				/ WarsmashConstants.SIMULATION_STEP_TIME);
		// 到了时间效果时间点或者后摇时间点
		if ((ticksSinceCast >= castPointTicks) || (ticksSinceCast >= backswingTicks)) {
			boolean wasEffectDone = this.doneEffect;
			boolean wasChanneling = this.channeling;
			// 施法效果还没执行
			if (!wasEffectDone) {
				// 设置施法效果完成标志
				this.doneEffect = true;
				// 检测魔法值是否够消耗，如果够就消耗魔法值，否则返回false
				if (!this.unit.chargeMana(this.ability.getManaCost())) { // 魔法值不够
					// 显示魔法不足的错误提示,并返回下一个命令
					simulation.getCommandErrorListener().showInterfaceError(this.unit.getPlayerIndex(), CommandStringErrorKeys.NOT_ENOUGH_MANA);
					return this.unit.pollNextOrderBehavior(simulation);
				}
				// 技能开始冷却
				this.unit.beginCooldown(simulation, this.ability.getCode(), this.ability.getCooldown());
				// 执行施法效果
				this.channeling = this.ability.doEffect(simulation, this.unit, null);
				// 如果是持续施法
				if (this.channeling) {
					// 播放持续施法音效
					simulation.unitLoopSoundEffectEvent(this.unit, this.ability.getAlias());
				} else {
					// 播放施法音效
					simulation.unitSoundEffectEvent(this.unit, this.ability.getAlias());
				}
			}
			// 执行持续施法Tick，并返回是否结束持续施法
			this.channeling = this.channeling && this.ability.doChannelTick(simulation, this.unit, null);
			// 施法效果完成，并且停止持续施法了
			if (wasEffectDone && wasChanneling && !this.channeling) {
				// 停止施法音响
				simulation.unitStopSoundEffectEvent(this.unit, this.ability.getAlias());
			}
		}
		// 到了后摇时间点，并且没有持续施法了，则结束施法，执行下一个命令
		if ((ticksSinceCast >= backswingTicks) && !this.channeling) {
			return this.unit.pollNextOrderBehavior(simulation);
		}
		return this;
	}

	// 开始施法的准备，暂未实现
	@Override
	public void begin(final CSimulation game) {
	}

	// 结束施法，处理被中断的情况
	@Override
	public void end(final CSimulation game, final boolean interrupted) {
	}

	// 获取高亮顺序ID
	@Override
	public int getHighlightOrderId() {
		return this.ability.getBaseOrderId();
	}

	// 获取施法能力
	public CAbilitySpellBase getAbility() {
		return this.ability;
	}

	// 检查施法是否可被中断
	@Override
	public boolean interruptable() {
		return true;
	}

	// 访问者模式的实现
	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}
}
