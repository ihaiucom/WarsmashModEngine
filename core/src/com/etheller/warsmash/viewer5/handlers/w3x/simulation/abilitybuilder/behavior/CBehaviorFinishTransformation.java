package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler.OnTransformationActions;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorVisitor;
// CBehaviorFinishTransformation类实现了CBehavior接口，处理单位的变形行为
public class CBehaviorFinishTransformation implements CBehavior {
	private Map<String, Object> localStore;
	private OnTransformationActions actions;
	private CUnit unit;
	private AbilityBuilderActiveAbility ability;
	private CUnitType baseTypeForDuration;
	private CUnitType newType;
	private int visibleOrderId;
	private boolean permanent;
	private float duration;
	private int transformationTickDuration;
	private float altitudeAdjustmentDelay;
	private float altitudeAdjustmentDuration;
	private float landingDelay;

	private boolean immediateLanding;
	private boolean immediateTakeoff;

	private boolean addAlternateTagAfter;

	private boolean takingOff;
	private boolean landing;

	private War3ID buffId;
	private float transformationTime;
	private boolean instantTransformAtDurationEnd;

	private int castStartTick = 0;

	// 构造函数用于初始化变形行为的各项属性
	public CBehaviorFinishTransformation(Map<String, Object> localStore, final CUnit unit,
			AbilityBuilderActiveAbility ability, CUnitType newType, OnTransformationActions actions,
			boolean addAlternateTagAfter, final int visibleOrderId, boolean permanent, float duration,
			float transformationTime, float landingDelay, float altitudeAdjustmentDelay,
			float altitudeAdjustmentDuration, boolean immediateLanding, boolean immediateTakeoff, War3ID buffId,
			CUnitType baseTypeForDuration, boolean instantTransformAtDurationEnd) {
		this.localStore = localStore;
		this.actions = actions;
		this.unit = unit;
		this.ability = ability;
		this.newType = newType;
		this.visibleOrderId = visibleOrderId;
		this.permanent = permanent;
		this.duration = duration;
		this.transformationTime = transformationTime;
		this.transformationTickDuration = Math.round(transformationTime / WarsmashConstants.SIMULATION_STEP_TIME);
		this.altitudeAdjustmentDelay = altitudeAdjustmentDelay;
		this.altitudeAdjustmentDuration = altitudeAdjustmentDuration;
		this.landingDelay = landingDelay;

		this.immediateLanding = immediateLanding;
		this.immediateTakeoff = immediateTakeoff;

		this.addAlternateTagAfter = addAlternateTagAfter;

		this.buffId = buffId;
		this.baseTypeForDuration = baseTypeForDuration;
		this.instantTransformAtDurationEnd = instantTransformAtDurationEnd;

		this.takingOff = unit.getMovementType() != MovementType.FLY && newType.getMovementType() == MovementType.FLY;
		this.landing = unit.getMovementType() == MovementType.FLY && newType.getMovementType() != MovementType.FLY;

		if (this.landing) {
			this.transformationTickDuration += this.landingDelay > 0
					? Math.round(altitudeAdjustmentDuration / WarsmashConstants.SIMULATION_STEP_TIME)
					: 0;
		}
	}

	// 更新方法用于在每个游戏循环中处理变形的状态
	@Override
	public CBehavior update(CSimulation game) {
		if (this.castStartTick == 0) {
			this.castStartTick = game.getGameTurnTick();
			TransformationHandler.startSlowTransformation(game, localStore, unit, newType, actions, ability,
					addAlternateTagAfter, takingOff, landing, immediateTakeoff, immediateLanding,
					altitudeAdjustmentDelay, landingDelay, altitudeAdjustmentDuration);
		}

		final int ticksSinceCast = game.getGameTurnTick() - this.castStartTick;
		if (ticksSinceCast > this.transformationTickDuration) {
			TransformationHandler.finishSlowTransformation(game, localStore, unit, newType, actions, ability,
					addAlternateTagAfter, permanent, takingOff);

			if (instantTransformAtDurationEnd) {
				TransformationHandler.createInstantTransformBackBuff(game, localStore, unit, baseTypeForDuration,
						actions.createUntransformActions(), ability, buffId,
						addAlternateTagAfter, transformationTime, duration, permanent);
			} else {
				TransformationHandler.createSlowTransformBackBuff(game, localStore, unit, baseTypeForDuration,
						actions.createUntransformActions(), ability, buffId,
						addAlternateTagAfter, transformationTime, duration, permanent, takingOff, landing,
						immediateTakeoff, immediateLanding, altitudeAdjustmentDelay, landingDelay,
						altitudeAdjustmentDuration);
			}

			return this.unit.pollNextOrderBehavior(game);
		}
		return this;
	}

	// 开始变形行为的方法
	@Override
	public void begin(CSimulation game) {
	}

	// 结束变形行为的方法，处理被中断的情况
	@Override
	public void end(CSimulation game, boolean interrupted) {
	}

	// 获取高亮显示的订单ID
	@Override
	public int getHighlightOrderId() {
		return visibleOrderId;
	}

	// 判断该行为是否可以被打断
	@Override
	public boolean interruptable() {
		return false;
	}

	// 访问者模式，用于访问该行为的相关操作
	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}

}

