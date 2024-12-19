package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

// 定义自动施法能力接口
public interface CAutocastAbility {
	// 获取基础指令ID
	int getBaseOrderId();

	// 检查能力是否被禁用
	boolean isDisabled();

	// 获取自动施法类型
	AutocastType getAutocastType();

	// 检查是否开启自动施法
	boolean isAutoCastOn();

	// 设置单位施法的自动施法状态
	void setAutoCastOn(final CUnit caster, final boolean autoCastOn);

	// 关闭自动施法
	void setAutoCastOff();

	// 检查单位是否可以自动瞄准目标
	void checkCanAutoTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver);

	// 检查单位是否可以自动瞄准特定的目标点
	void checkCanAutoTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver);

	// 检查单位是否可以在没有目标的情况下自动施法
	void checkCanAutoTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver);

	// 检查单位是否可以使用指定的能力
	void checkCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver);

}
