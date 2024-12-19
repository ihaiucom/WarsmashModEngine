package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;


// 表示处理目标单位访问者的类
public class BehaviorTargetUnitVisitor implements CBehaviorVisitor<CUnit> {
	// 单例实例
	public static final BehaviorTargetUnitVisitor INSTANCE = new BehaviorTargetUnitVisitor();

	// 接受 CBehavior 目标并返回 CUnit
	@Override
	public CUnit accept(CBehavior target) {
		return null;
	}

	// 接受 CRangedBehavior 目标并返回对应的 CUnit
	@Override
	public CUnit accept(CRangedBehavior target) {
		if (target.getTarget() != null) {
			return target.getTarget().visit(AbilityTargetVisitor.UNIT);
		}
		return null;
	}
	
}
