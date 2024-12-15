package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
/**
 * CAbilityView接口定义了能力视图的基本操作和查询方法。
 * 检查技能能否使用
 * 检查目标能否使用
 * 检查单位是否满足条件
 */
public interface CAbilityView extends CHandle {
	/**
	 * 检查单位是否可以使用指定的能力
	 */
	void checkCanUse(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver);

	/**
	 * 检查单位是否可以以指定目标使用能力
	 */
	void checkCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver);

	/**
	 * 检查单位是否可以以指定点目标使用能力
	 */
	void checkCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver);

	/**
	 * 检查单位是否可以在没有目标的情况下使用能力
	 */
	void checkCanTargetNoTarget(CSimulation game, CUnit unit, int orderId, AbilityTargetCheckReceiver<Void> receiver);

	/**
	 * 检查单位的能力要求是否满足
	 */
	void checkRequirementsMet(CSimulation game, CUnit unit, AbilityActivationReceiver receiver);

	/**
	 * 检查单位的要求是否满足
	 */
	boolean isRequirementsMet(CSimulation game, CUnit unit);

	@Override
	/**
	 * 获取处理程序ID
	 */
	int getHandleId();

	/**
	 * 获取能力的别名
	 */
	War3ID getAlias();

	/**
	 * 获取能力的代码
	 */
	War3ID getCode();

	/**
	 * 检查能力是否被禁用
	 */
	boolean isDisabled();

	/**
	 * 检查能力图标是否显示
	 */
	boolean isIconShowing();

	/**
	 * 检查能力是否为永久性
	 */
	boolean isPermanent();

	/**
	 * 检查能力是否为物理性
	 */
	boolean isPhysical();

	/**
	 * 检查能力是否为通用性
	 */
	boolean isUniversal();

	/**
	 * 获取能力的类别
	 */
	CAbilityCategory getAbilityCategory();

	/**
	 * 访问能力的访客模式
	 */
	<T> T visit(CAbilityVisitor<T> visitor);
}

