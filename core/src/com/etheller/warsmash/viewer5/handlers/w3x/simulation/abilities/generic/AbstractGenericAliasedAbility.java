package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.AbstractCAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

// 通用别名有等级的能力抽象类
public abstract class AbstractGenericAliasedAbility extends AbstractCAbility implements CLevelingAbility {
	private final War3ID alias; // 别名
	private int level = 1; // 当前等级

	// 构造函数，初始化处理ID、代码和别名
	public AbstractGenericAliasedAbility(final int handleId, final War3ID code, final War3ID alias) {
		super(handleId, code);
		this.alias = alias;
	}

	@Override
	// 在队列之前检查能力是否可以被施放
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		return true;
	}

	// 获取能力的别名
	public War3ID getAlias() {
		return this.alias;
	}

	@Override
	// 获取当前能力的等级
	public final int getLevel() {
		return this.level;
	}

	@Override
	// 设置能力的等级
	public void setLevel(CSimulation simulation, CUnit unit, final int level) {
		this.level = level;
	}
}
