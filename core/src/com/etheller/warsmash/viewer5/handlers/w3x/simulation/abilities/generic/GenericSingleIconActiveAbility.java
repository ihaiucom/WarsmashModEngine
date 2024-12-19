package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.Aliased;

// 定义一个通用的单图标主动技能接口，继承自CLevelingAbility，SingleOrderAbility和Aliased接口
public interface GenericSingleIconActiveAbility extends CLevelingAbility, SingleOrderAbility, Aliased {

	// 检查技能是否为切换状态
	boolean isToggleOn();

	// 检查技能是否为自动施放状态
	boolean isAutoCastOn();

	// 获取自动施放开启的订单ID
	int getAutoCastOnOrderId();

	// 获取自动施放关闭的订单ID
	int getAutoCastOffOrderId();

	// 获取技能在用户界面中所需的金钱成本
	int getUIGoldCost();

	// 获取技能在用户界面中所需的木材成本
	int getUILumberCost();

	// 获取技能在用户界面中所需的食物成本
	int getUIFoodCost();

	// 获取技能在用户界面中所需的魔法成本
	int getUIManaCost();

	// 获取当前剩余使用次数
	int getUsesRemaining();

	// 获取技能的有效范围
	float getUIAreaOfEffect();

}
