package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityRanged;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityLoad;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorMoveIntoRangeFor.PairAbilityLocator;

// 装载单位行为
public class CBehaviorBoardTransport extends CBehaviorMoveIntoRangeFor implements PairAbilityLocator {

	// 构造函数，初始化类实例
	public CBehaviorBoardTransport(final CUnit unit) {
		super(unit);
	}

	// 重置方法，重置高亮顺序ID和目标部件
	public CBehavior reset(final int higlightOrderId, final CWidget target) {
		return super.reset(higlightOrderId, target, this);
	}

	@Override
	// 从运输单位获取装载能力
	public CAbilityRanged getPartnerAbility(final CSimulation game, final CUnit caster, final CUnit transport,
			final boolean ignoreRange, final boolean ignoreDisabled) {
		return CAbilityLoad.getTransportLoad(game, caster, transport, ignoreRange, ignoreDisabled);
	}
}
