package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;

/**
 * 抽象能力类，提供能力的基本实现，供子类继承使用。
 */
public abstract class AbstractCAbility implements CAbility {

	private final int handleId; // 该类用于表示一个具有唯一标识符和状态的对象
	private byte disabled = 0; // 状态标识符：0表示启用，1表示禁用
	private boolean iconShowing = true; // 图标是否显示的标志
	private boolean permanent = false; // 是否是永久状态的标志

	private War3ID code; // 对象的代码标识


	/**
	 * 构造函数，初始化能力的ID和代码。
	 * @param handleId 能力句柄ID
	 * @param code 能力代码
	 */
	public AbstractCAbility(final int handleId, final War3ID code) {
		this.handleId = handleId;
		this.code = code;
	}

	@Override
	/**
	 * 获取能力句柄ID。
	 * @return 能力句柄ID
	 */
	public final int getHandleId() {
		return this.handleId;
	}

	/**
	 * 获取能力代码。
	 * @return 能力代码
	 */
	public War3ID getCode() {
		return this.code;
	}

	@Override
	/**
	 * 获取能力别名，通常返回能力代码。
	 * @return 能力别名
	 */
	public War3ID getAlias() {
		return this.getCode();
	}

	@Override
	/**
	 * 检查能力是否被禁用。
	 * @return 如果被禁用返回true，否则返回false
	 */
	public final boolean isDisabled() {
		return this.disabled != 0;
	}

	@Override
	/**
	 * 设置能力的禁用状态。
	 * @param disabled 是否禁用
	 * @param type 禁用类型
	 */
	public final void setDisabled(final boolean disabled, CAbilityDisableType type) {
		if (disabled) {
			this.disabled |= type.getMask();
		} else {
			this.disabled &= ~type.getMask();
		}
	}

	@Override
	/**
	 * 检查能力图标是否显示。
	 * @return 如果图标显示返回true，否则返回false
	 */
	public final boolean isIconShowing() {
		return this.iconShowing;
	}

	@Override
	/**
	 * 设置能力图标的显示状态。
	 * @param iconShowing 图标是否显示
	 */
	public final void setIconShowing(final boolean iconShowing) {
		this.iconShowing = iconShowing;
	}

	@Override
	/**
	 * 检查能力是否为永久性。
	 * @return 如果为永久性返回true，否则返回false
	 */
	public boolean isPermanent() {
		return this.permanent;
	}

	@Override
	/**
	 * 设置能力为永久性或非永久性。
	 * @param permanent 是否永久
	 */
	public void setPermanent(final boolean permanent) {
		this.permanent = permanent;
	}

	@Override
	/**
	 * 设置物品的能力，默认实现不执行任何操作。
	 */
	public void setItemAbility(CItem item, int slot) {
		//do nothing
	}

	@Override
	/**
	 * 检查能力是否可以使用，如果被禁用，检查相关需求。
	 * @param game 游戏实例
	 * @param unit 使用能力的单位
	 * @param orderId 订单ID
	 * @param receiver 激活接收者
	 */
	public final void checkCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		if (this.isDisabled()) {
			receiver.disabled();
			this.checkRequirementsMet(game, unit, receiver);
		}
		else {
			innerCheckCanUse(game, unit, orderId, receiver);
		}
	}

	/**
	 * 子类必须实现的抽象方法，检查能力是否可以使用。
	 * @param game 游戏实例
	 * @param unit 使用能力的单位
	 * @param orderId 订单ID
	 * @param receiver 激活接收者
	 */
	protected abstract void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver);

	@Override
	/**
	 * 当单位类型被设置时调用，默认实现不执行任何操作。
	 * @param game 游戏实例
	 * @param cUnit 单位
	 */
	public void onSetUnitType(final CSimulation game, final CUnit cUnit) {
	}

	@Override
	/**
	 * 检查能力的需求是否满足，默认实现不执行任何操作。
	 * @param game 游戏实例
	 * @param unit 单位
	 * @param receiver 激活接收者
	 */
	public void checkRequirementsMet(CSimulation game, CUnit unit, AbilityActivationReceiver receiver) {

	}

	@Override
	/**
	 * 检查要求是否满足，默认总是返回true。
	 * @param game 游戏实例
	 * @param unit 单位
	 * @return 是否满足要求
	 */
	public boolean isRequirementsMet(CSimulation game, CUnit unit) {
		return true;
	}

	@Override
	/**
	 * 当能力被禁用时调用，默认实现不执行任何操作。
	 * @param game 游戏实例
	 * @param unit 单位
	 */
	public void onAddDisabled(CSimulation game, CUnit unit) {
		//do nothing
	}

	@Override
	/**
	 * 当禁用被移除时调用，默认实现不执行任何操作。
	 * @param game 游戏实例
	 * @param unit 单位
	 */
	public void onRemoveDisabled(CSimulation game, CUnit unit) {
		//do nothing
	}
}

