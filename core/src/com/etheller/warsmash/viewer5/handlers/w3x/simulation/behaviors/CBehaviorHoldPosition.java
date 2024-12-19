package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

// 静止行为：播放待机动画，检测可以自动施法的技能或者检测自动攻击的目标，或者执行下一个指令的行为
// 和CBehaviorStop差不多，只是自动攻击的时候不能移动
public class CBehaviorHoldPosition implements CBehavior {

	private final CUnit unit;

	public CBehaviorHoldPosition(final CUnit unit) {
		this.unit = unit;
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.holdposition;
	}

	@Override
	public CBehavior update(final CSimulation game) {
		// 自动获取目标的方法： 检测自动施法技能是否攻击目标， 检测普攻是否攻击目标
		if (this.unit.autoAcquireTargets(game, true)) {
			// kind of a hack
			return this.unit.getCurrentBehavior();
		}
		return this.unit.pollNextOrderBehavior(game);
	}

	@Override
	public void begin(final CSimulation game) {
		// 如果不是在建造或升级，则播放站立动画
		if (!this.unit.isConstructingOrUpgrading()) {
			this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f,
					true);
		}
	}

	@Override
	public void end(final CSimulation game, boolean interrupted) {

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
