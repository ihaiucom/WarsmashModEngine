package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

/**
 * 能力添加到单位时或者移除从单位移除能力 调用的事件接口
 * onTick 单位 刷新时
 * onDeath 单位 死亡时
 * onSetUnitType 单位 类型改变时
 * onCancelFromQueue 单位 取消技能时
 *
 */
public interface CAbility extends CAbilityView {
	/* should fire when ability added to unit */
	// 当技能被添加到单位时应触发
	void onAddDisabled(CSimulation game, CUnit unit);

	/* should fire when ability added to unit only if the ability is not disabled at the time */
	// 只有在技能未被禁用时，技能被添加到单位时应触发
	void onAdd(CSimulation game, CUnit unit);

	/* should fire when ability removed from unit only if the ability is not disabled at the time */
	// 只有在技能未被禁用时，技能从单位移除时应触发
	void onRemove(CSimulation game, CUnit unit);

	/* should fire when ability removed from unit */
	// 当技能从单位移除时应触发
	void onRemoveDisabled(CSimulation game, CUnit unit);

	// 每个游戏循环中定期调用
	void onTick(CSimulation game, CUnit unit);

	// 当单位死亡时调用
	void onDeath(CSimulation game, CUnit cUnit);

	/*
	 * should fire for "permanent" abilities that are kept across unit type change
	 * 应当在单位类型改变时触发“永久性”技能
	 */
	void onSetUnitType(CSimulation game, CUnit cUnit);

	// 当从技能队列中取消技能时调用
	void onCancelFromQueue(CSimulation game, CUnit unit, int orderId);

	/* return false to not do anything, such as for toggling autocast */
	// 返回false则不执行任何操作，如切换自动施法
	boolean checkBeforeQueue(CSimulation game, CUnit caster, int orderId, AbilityTarget target);

	// 开始施放技能，目标为CWidget类型
	CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target);

	// 开始施放技能，目标为AbilityPointTarget类型
	CBehavior begin(CSimulation game, CUnit caster, int orderId, AbilityPointTarget point);

	// 无目标施放技能
	CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId);

	// 设置技能的禁用状态
	void setDisabled(boolean disabled, CAbilityDisableType type);

	// 设置图标显示状态
	void setIconShowing(boolean iconShowing);

	// 设置技能为永久性
	void setPermanent(boolean permanent);

	// 设置物品技能及其槽位
	void setItemAbility(CItem item, int slot);
}

