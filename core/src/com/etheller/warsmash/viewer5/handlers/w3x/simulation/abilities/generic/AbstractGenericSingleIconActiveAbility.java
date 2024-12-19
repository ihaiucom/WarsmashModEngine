package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
/**
 * 单个图标主动技能抽象类：包含自动释放开关指令
 */
public abstract class AbstractGenericSingleIconActiveAbility extends AbstractGenericAliasedAbility
		implements GenericSingleIconActiveAbility {
	/**
	 * 构造函数，初始化技能的标识符和别名
	 * @param handleId 处理ID
	 * @param code 技能代码
	 * @param alias 技能别名
	 */
	public AbstractGenericSingleIconActiveAbility(final int handleId, final War3ID code, final War3ID alias) {
		super(handleId, code, alias);
	}

	@Override
	/**
	 * 在将技能排入队列之前进行检查
	 * @param game 目前的游戏实例
	 * @param caster 施法单位
	 * @param orderId 订单ID
	 * @param target 目标
	 * @return 检查结果
	 */
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		final int autoCastOnOrderId = getAutoCastOnOrderId(); // 获取自动施放开启的命令ID
		final int autoCastOffOrderId = getAutoCastOffOrderId(); // 获取自动施放关闭的命令ID

		// 检查传入的orderId是否为0，如果不为0，则进一步检查其是否为自动施放开启或关闭的命令ID
		if (orderId != 0) {
			if (orderId == autoCastOnOrderId) {
				setAutoCastOn(caster, true); // 如果是自动施放开启命令，则设置自动施放为开启状态
				return false; // 并返回false，表示命令已处理
			}
			else if (orderId == autoCastOffOrderId) {
				setAutoCastOn(caster, false); // 如果是自动施放关闭命令，则设置自动施放为关闭状态
				return false; // 并返回false，表示命令已处理
			}
			else {
				return super.checkBeforeQueue(game, caster, orderId, target); // 如果不是自动施放命令，则调用父类方法检查队列
			}
		}
		else {
			return super.checkBeforeQueue(game, caster, orderId, target); // 如果OrderId为0，则直接调用父类方法检查队列
		}

	}

	@Override
	/**
	 * 检查单位是否可以作为目标
	 * @param game 目前的游戏实例
	 * @param unit 检查的单位
	 * @param orderId 订单ID
	 * @param target 目标小部件
	 * @param receiver 目标检查接收器
	 */
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		// 检查订单ID是否与基础订单ID相同，如果是，则调用innerCheckCanTarget方法
		if (orderId == getBaseOrderId()) {
			// 检查游戏中的单位是否能够针对特定目标
			innerCheckCanTarget(game, unit, orderId, target, receiver);
		}
		// 检查订单ID是否为智能订单ID
		else if (orderId == OrderIds.smart) {
			// 调用innerCheckCanSmartTarget方法，进行智能目标检查
			innerCheckCanSmartTarget(game, unit, orderId, target, receiver);
		}
		// 如果订单ID既不是基础订单ID也不是智能订单ID，则通知接收者订单ID不被接受
		else {
			receiver.orderIdNotAccepted();
		}

	}

	/**
	 * 抽象方法，检查单位是否可以作为目标（具体实现由子类定义）
	 */
	protected abstract void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver);

	/**
	 * 抽象方法，智能目标检查（具体实现由子类定义）
	 */
	protected abstract void innerCheckCanSmartTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver);

	@Override
	/**
	 * 检查可以作为目标的点目标
	 * @param game 目前的游戏实例
	 * @param unit 检查的单位
	 * @param orderId 订单ID
	 * @param target 目标点
	 * @param receiver 目标检查接收器
	 */
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		// 检查订单ID是否与基础订单ID匹配，如果匹配则调用内部方法检查目标是否有效
		if (orderId == getBaseOrderId()) {
			// 调用内部方法检查普通目标是否有效
			innerCheckCanTarget(game, unit, orderId, target, receiver);
		}
		// 如果订单ID是智能订单ID，则调用内部方法检查智能目标是否有效
		else if (orderId == OrderIds.smart) {
			// 调用内部方法检查智能目标是否有效
			innerCheckCanSmartTarget(game, unit, orderId, target, receiver);
		}
		// 如果订单ID既不是基础订单ID也不是智能订单ID，则通知接收者订单ID不被接受
		else {
			receiver.orderIdNotAccepted();
		}

	}

	/**
	 * 抽象方法，检查单位是否可以作为点目标（具体实现由子类定义）
	 */
	protected abstract void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver);

	/**
	 * 抽象方法，智能点目标检查（具体实现由子类定义）
	 */
	protected abstract void innerCheckCanSmartTarget(CSimulation game, CUnit unit, int orderId,
			AbilityPointTarget target, AbilityTargetCheckReceiver<AbilityPointTarget> receiver);

	@Override
	/**
	 * 检查可以不带目标的情况
	 * @param game 目前的游戏实例
	 * @param unit 检查的单位
	 * @param orderId 订单ID
	 * @param receiver 目标检查接收器
	 */
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		// 如果orderId不为0，并且等于自动取消施法OrderId或者等于自动开始施法OrderId，则调用receiver的targetOk方法
		if ((orderId != 0) && ((orderId == getAutoCastOffOrderId()) || (orderId == getAutoCastOnOrderId()))) {
			receiver.targetOk(null);
		}
		// 如果OrderId等于基础OrderId，则调用innerCheckCanTargetNoTarget方法进行内部检查
		else if (orderId == getBaseOrderId()) {
			innerCheckCanTargetNoTarget(game, unit, orderId, receiver);
		}
		// 如果以上条件都不满足，则调用receiver的orderIdNotAccepted方法
		else {
			receiver.orderIdNotAccepted();
		}

	}

	/**
	 * 抽象方法，检查没有目标的情况（具体实现由子类定义）
	 */
	protected abstract void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver);

	@Override
	/**
	 * 接待访问者以进行能力访问
	 * @param visitor 访问者
	 * @return 访问结果
	 */
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	/**
	 * 获取UI金钱消耗
	 * @return 金钱消耗
	 */
	public int getUIGoldCost() {
		return 0;
	}

	@Override
	/**
	 * 获取UI木材消耗
	 * @return 木材消耗
	 */
	public int getUILumberCost() {
		return 0;
	}

	@Override
	/**
	 * 获取UI食物消耗
	 * @return 食物消耗
	 */
	public int getUIFoodCost() {
		return 0;
	}

	@Override
	/**
	 * 获取UI魔法消耗
	 * @return 魔法消耗
	 */
	public int getUIManaCost() {
		return 0;
	}

	@Override
	/**
	 * 获取剩余使用次数
	 * @return 剩余次数
	 */
	public int getUsesRemaining() {
		return -1;
	}

	@Override
	/**
	 * 获取技能的范围效果
	 * @return 范围效果
	 */
	public float getUIAreaOfEffect() {
		return Float.NaN;
	}

	@Override
	/**
	 * 检查技能是否自动施放
	 * @return 是否自动施放
	 */
	public boolean isAutoCastOn() {
		return false;
	}

	@Override
	/**
	 * 获取自动施放开启的订单ID
	 * @return 订单ID
	 */
	public int getAutoCastOnOrderId() {
		return 0;
	}

	@Override
	/**
	 * 获取自动施放关闭的订单ID
	 * @return 订单ID
	 */
	public int getAutoCastOffOrderId() {
		return 0;
	}

	/**
	 * 设置单位的自动施放状态
	 * @param unit 施法单位
	 * @param autoCastOn 是否自动施放
	 */
	public void setAutoCastOn(final CUnit unit, final boolean autoCastOn) {
	}

	@Override
	/**
	 * 当单位死亡时的处理逻辑
	 * @param game 目前的游戏实例
	 * @param cUnit 死亡的单位
	 */
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

}
