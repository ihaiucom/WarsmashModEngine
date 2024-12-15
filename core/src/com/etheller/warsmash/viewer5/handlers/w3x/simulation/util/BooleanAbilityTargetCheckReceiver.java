package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

// 布尔能力目标检查接收器类，实现了AbilityTargetCheckReceiver接口，用于处理目标检查的逻辑
public final class BooleanAbilityTargetCheckReceiver<TARGET_TYPE> implements AbilityTargetCheckReceiver<TARGET_TYPE> {
	// 静态实例，用于单例模式
	private static final BooleanAbilityTargetCheckReceiver<?> INSTANCE = new BooleanAbilityTargetCheckReceiver<>();

	// 获取单例实例的方法
	public static <T> BooleanAbilityTargetCheckReceiver<T> getInstance() {
		return (BooleanAbilityTargetCheckReceiver<T>) INSTANCE;
	}

	// 目标是否可用的标志位
	private boolean targetable = false;

	// 检查目标是否可用
	public boolean isTargetable() {
		return this.targetable;
	}

	// 重置目标可用状态
	public BooleanAbilityTargetCheckReceiver<TARGET_TYPE> reset() {
		this.targetable = false;
		return this;
	}

	// 当目标检查通过时调用
	@Override
	public void targetOk(final TARGET_TYPE target) {
		this.targetable = true;
	}

	// 当能力不是活动状态时调用
	@Override
	public void notAnActiveAbility() {
		this.targetable = false;
	}

	// 当订单ID不被接受时调用
	@Override
	public void orderIdNotAccepted() {
		this.targetable = false;
	}

	// 当目标检查失败时调用
	@Override
	public void targetCheckFailed(String commandStringErrorKey) {
		this.targetable = false;
	}
}
