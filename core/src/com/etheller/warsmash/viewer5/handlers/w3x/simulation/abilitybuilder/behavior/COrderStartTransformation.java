package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderNoTarget;
// COrderStartTransformation类，继承自COrderNoTarget，用于表示订单开始转换的行为
public class COrderStartTransformation extends COrderNoTarget {
	private CBehavior transformBehavior;

	// 构造函数，初始化变换行为和订单ID
	public COrderStartTransformation(final CBehavior transformBehavior, final int orderId) {
		super(0, orderId, false);
		this.transformBehavior = transformBehavior;
	}

	// 开始变换的方法，触发单位的订单事件并返回变换行为
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster) {
		caster.fireOrderEvents(game, this);
		return transformBehavior;
	}

	// 获取目标的方法，此处返回null
	@Override
	public AbilityTarget getTarget(final CSimulation game) {
		return null;
	}

	// 生成hashCode的方法，基于变换行为和订单ID
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.transformBehavior.hashCode();
		result = (prime * result) + this.getOrderId();
		return result;
	}

	// 重写equals方法，用于比较两个COrderStartTransformation对象的相等性
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final COrderStartTransformation other = (COrderStartTransformation) obj;
		if (this.transformBehavior.equals(other.transformBehavior)) {
			return false;
		}
		if (this.getOrderId() != other.getOrderId()) {
			return false;
		}
		return true;
	}

	// 触发事件的方法，单位根据游戏和当前对象触发订单事件
	@Override
	public void fireEvents(final CSimulation game, final CUnit unit) {
		unit.fireOrderEvents(game, this);
	}

}


