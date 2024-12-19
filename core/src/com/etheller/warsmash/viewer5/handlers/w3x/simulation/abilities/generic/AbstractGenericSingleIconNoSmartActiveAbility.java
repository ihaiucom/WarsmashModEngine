package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

// 单个图标主动技能抽象类，不支持右键目标
public abstract class AbstractGenericSingleIconNoSmartActiveAbility extends AbstractGenericSingleIconActiveAbility {

	// 构造函数，用于初始化处理ID、代码和别名
	public AbstractGenericSingleIconNoSmartActiveAbility(final int handleId, final War3ID code, final War3ID alias) {
		super(handleId, code, alias);
	}

	// 覆盖方法，检查是否可以进行智能目标（针对能力点目标）
	@Override
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	// 覆盖方法，检查是否可以进行智能目标（针对小部件目标）
	@Override
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

}
