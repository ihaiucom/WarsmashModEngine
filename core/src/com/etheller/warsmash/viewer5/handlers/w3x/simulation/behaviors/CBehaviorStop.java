package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

// 停止行为：播放待机动画，检测可以自动施法的技能或者检测自动攻击的目标，或者执行下一个指令的行为
public class CBehaviorStop implements CBehavior {

	private final CUnit unit;

	public CBehaviorStop(final CUnit unit) {
		this.unit = unit;
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.stop;
	}

	@Override
	public CBehavior update(final CSimulation game) {
		// 检测自动施法技能施法，如果有可以自动施法的技能就返回当前行为（施法技能指令的行为）
		if (this.unit.autoAcquireTargets(game, false)) {
			return this.unit.getCurrentBehavior();
		}
		// 处理下一个指令行为 或者 默认行为
		return this.unit.pollNextOrderBehavior(game);
	}

	@Override
	public void begin(final CSimulation game) {
		// 如果不是正在建造或升级，播放待机动画
		if (!this.unit.isConstructingOrUpgrading()) {
			this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f,
					true);
		}
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {

	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}

}
