package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.root;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.root.CAbilityRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
// CBehaviorUproot 类实现了 CBehavior 接口，负责处理单位的起根行为
public class CBehaviorUproot implements CBehavior {
	private final CUnit unit; // 参与起根行为的单位
	private final CAbilityRoot abilityRoot; // 处理起根能力的实例
	private int finishTick; // 完成的时钟周期

	// 构造函数，初始化单位和能力实例
	public CBehaviorUproot(final CUnit unit, final CAbilityRoot abilityRoot) {
		this.unit = unit;
		this.abilityRoot = abilityRoot;
	}

	// 重置函数，重新设置完成的时钟周期
	public CBehavior reset() {
		this.finishTick = -1;
		return this;
	}

	@Override
	// 更新函数，根据游戏状态更新单位的行为
	public CBehavior update(final CSimulation game) {
		final float duration = this.abilityRoot.getOffDuration(); // 获取起根持续时间
		if (this.finishTick == -1) { // 第一次更新，开始起根动画
			this.finishTick = game.getGameTurnTick() + (int) (duration / WarsmashConstants.SIMULATION_STEP_TIME);
			this.unit.getUnitAnimationListener().playAnimationWithDuration(true, PrimaryTag.MORPH, SequenceUtils.EMPTY,
					duration, true);
			this.unit.setAcceptingOrders(false); // 禁止接收指令
		}
		else if (game.getGameTurnTick() >= this.finishTick) { // 动画完成，恢复单位状态
			this.abilityRoot.setRooted(false, this.unit, game);
			this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f,
					true);
			this.unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.ALTERNATE);
			this.unit.setAcceptingOrders(true); // 恢复接收指令
			return this.unit.pollNextOrderBehavior(game); // 返回下一个行为
		}
		return this; // 返回当前行为
	}

	@Override
	// 行为开始时的操作
	public void begin(final CSimulation game) {

	}

	@Override
	// 行为结束时的操作，参数表示是否被中断
	public void end(final CSimulation game, final boolean interrupted) {

	}

	@Override
	// 获取高亮的指令ID
	public int getHighlightOrderId() {
		return OrderIds.unroot;
	}

	@Override
	// 检查行为是否可以被打断
	public boolean interruptable() {
		return true;
	}

	@Override
	// 访问者模式，允许行为访问指定的访问者
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}
}
