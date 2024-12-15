package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.GetInstantTransformationBuffVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABTimedTransformationBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer.AltitudeAdjustmentTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer.DelayTimerTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer.TransformationMorphAnimationTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class TransformationHandler {
	// 设置单位ID的方法
	public static void setUnitID(CSimulation game, Map<String, Object> localStore, CUnit unit, CUnitType newType,
			boolean addAlternateTagAfter, OnTransformationActions actions, CAbility ability) {
		setUnitID(game, localStore, unit, newType, addAlternateTagAfter, actions, ability, false);
	}

	// 设置单位ID的重载方法
	public static void setUnitID(CSimulation game, Map<String, Object> localStore, CUnit unit, CUnitType newType,
			boolean addAlternateTagAfter, OnTransformationActions actions, CAbility ability, boolean updateArt) {
		CPlayer pl = game.getPlayer(unit.getPlayerIndex());
		if (actions != null) {
			pl.setGold(Math.max(pl.getGold() - actions.goldCost, 0));
			pl.setLumber(Math.max(pl.getLumber() - actions.lumberCost, 0));

			if (!addAlternateTagAfter) {
				if (actions.onUntransformActions != null) {
					for (ABAction action : actions.onUntransformActions) {
						action.runAction(game, unit, localStore, 0);
					}
				}
			}
		}

		unit.setTypeId(game, newType.getTypeId(), updateArt);
		pl.setUnitFoodUsed(unit, newType.getFoodUsed());
		pl.setUnitFoodMade(unit, newType.getFoodMade());
		if (addAlternateTagAfter) {
			unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.ALTERNATE);
		} else {
			unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.ALTERNATE);
		}
		game.unitSoundEffectEvent(unit, ability.getAlias());

		if (addAlternateTagAfter && actions != null) {
			if (actions.onTransformActions != null) {
				for (ABAction action : actions.onTransformActions) {
					action.runAction(game, unit, localStore, 0);
				}
			}
		}
	}

	// 播放形态变化动画的方法
	public static void playMorphAnimation(CUnit unit, boolean addAlternateTagAfter) {
		if (addAlternateTagAfter) {
			unit.getUnitAnimationListener().removeSecondaryTagForFutureAnimations(SecondaryTag.ALTERNATE);
			unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.MORPH, SequenceUtils.EMPTY, 1.0f, true);
		} else {
			unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.MORPH, EnumSet.of(SecondaryTag.ALTERNATE),
					1.0f, true);
		}
		unit.getUnitAnimationListener().queueAnimation(PrimaryTag.STAND,
				addAlternateTagAfter ? EnumSet.of(SecondaryTag.ALTERNATE) : SequenceUtils.EMPTY, true);
		if (addAlternateTagAfter) {
			unit.getUnitAnimationListener().addSecondaryTagForFutureAnimations(SecondaryTag.ALTERNATE);
		} else {
			unit.getUnitAnimationListener().removeSecondaryTagForFutureAnimations(SecondaryTag.ALTERNATE);
		}
	}

	// 设置标签的方法
	public static void setTags(CUnit unit, boolean addAlternateTagAfter) {
		if (addAlternateTagAfter) {
			unit.getUnitAnimationListener().addSecondaryTagForFutureAnimations(SecondaryTag.ALTERNATE);
		} else {
			unit.getUnitAnimationListener().removeSecondaryTagForFutureAnimations(SecondaryTag.ALTERNATE);
		}
		unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND,
				addAlternateTagAfter ? EnumSet.of(SecondaryTag.ALTERNATE) : SequenceUtils.EMPTY, 1.0f, true);
	}

	// 开始起飞的方法
	public static void beginTakingOff(CSimulation game, Map<String, Object> localStore, CUnit unit, CUnitType newType,
			OnTransformationActions actions, CAbility ability, boolean addAlternateTagAfter, boolean immediateTakeoff,
			float altitudeAdjustmentDelay, float altitudeAdjustmentDuration) {
		CTimer timer = (CTimer) localStore.get(ABLocalStoreKeys.ACTIVE_ALTITUDE_ADJUSTMENT);
		if (timer != null) {
			game.unregisterTimer(timer);
		}
		timer = (new DelayTimerTimer(
				new AltitudeAdjustmentTimer(game, unit, newType.getDefaultFlyingHeight(), altitudeAdjustmentDuration),
				localStore, altitudeAdjustmentDelay));
		timer.start(game);
		localStore.put(ABLocalStoreKeys.ACTIVE_ALTITUDE_ADJUSTMENT, timer);
		TransformationHandler.setUnitID(game, localStore, unit, newType, addAlternateTagAfter, actions, ability);
		if (immediateTakeoff) {
			TransformationHandler.playMorphAnimation(unit, addAlternateTagAfter);
		} else {
			CTimer t2 = new TransformationMorphAnimationTimer(game, unit, addAlternateTagAfter, altitudeAdjustmentDelay);
			t2.start(game);
			localStore.put(ABLocalStoreKeys.WAITING_ANIMATION, t2);
		}
	}

	// 开始着陆的方法
	public static void beginLanding(CSimulation game, Map<String, Object> localStore, CUnit unit, CUnitType newType, boolean addAlternateTagAfter,
			boolean immediateLanding, float landingDelay, float altitudeAdjustmentDuration) {
		unit.setFacing(225);
		if (immediateLanding) {
			TransformationHandler.playMorphAnimation(unit, addAlternateTagAfter);
		} else {
			CTimer timer = new TransformationMorphAnimationTimer(game, unit, addAlternateTagAfter, landingDelay);
			timer.start(game);
			localStore.put(ABLocalStoreKeys.WAITING_ANIMATION, timer);
		}
		CTimer timer = (CTimer) localStore.get(ABLocalStoreKeys.ACTIVE_ALTITUDE_ADJUSTMENT);
		if (timer != null) {
			game.unregisterTimer(timer);
		}
		timer = new AltitudeAdjustmentTimer(game, unit, newType.getDefaultFlyingHeight(), altitudeAdjustmentDuration);
		timer.start(game);
		localStore.put(ABLocalStoreKeys.ACTIVE_ALTITUDE_ADJUSTMENT, timer);
	}

	// 开始慢速变形的函数
	public static void startSlowTransformation(CSimulation game, Map<String, Object> localStore, CUnit unit,
			CUnitType newType, OnTransformationActions actions, CAbility ability, boolean addAlternateTagAfter,
			boolean takingOff, boolean landing, boolean immediateTakeoff, boolean immediateLanding,
			float altitudeAdjustmentDelay, float landingDelay, float altitudeAdjustmentDuration) {
		CTimer timer = (CTimer) localStore.get(ABLocalStoreKeys.WAITING_ANIMATION);
		if (timer != null) {
			game.unregisterTimer(timer);
		}
		unit.getUnitAnimationListener().queueAnimation(PrimaryTag.STAND,
				addAlternateTagAfter ? SequenceUtils.EMPTY : EnumSet.of(SecondaryTag.ALTERNATE), true);
		if (takingOff || landing) {

			if (takingOff) {
				TransformationHandler.beginTakingOff(game, localStore, unit, newType, actions, ability,
						addAlternateTagAfter, immediateTakeoff, altitudeAdjustmentDelay, altitudeAdjustmentDuration);
			}

			if (landing) {
				TransformationHandler.beginLanding(game, localStore, unit, newType, addAlternateTagAfter, immediateLanding,
						landingDelay, altitudeAdjustmentDuration);
			}
		} else {
			TransformationHandler.playMorphAnimation(unit, addAlternateTagAfter);
		}
	}

	// 完成慢速变形的函数
	public static void finishSlowTransformation(CSimulation game, Map<String, Object> localStore, CUnit unit,
			CUnitType newType, OnTransformationActions actions, AbilityBuilderAbility ability,
			boolean addAlternateTagAfter, boolean permanent, boolean takingOff) {
		if (!takingOff) {
			TransformationHandler.setUnitID(game, localStore, unit, newType, addAlternateTagAfter, actions, ability);
		}
		if (permanent) {
			unit.remove(game, ability);
		}
	}

	// 进行瞬时变形的函数
	public static void instantTransformation(CSimulation game, Map<String, Object> localStore, CUnit unit,
			CUnitType newType, OnTransformationActions actions, AbilityBuilderAbility ability,
			boolean addAlternateTagAfter, boolean permanent, boolean playMorph) {
		setUnitID(game, localStore, unit, newType, addAlternateTagAfter, actions, ability, false);
		if (playMorph) {
			TransformationHandler.playMorphAnimation(unit, addAlternateTagAfter);
		}
		if (permanent) {
			unit.remove(game, ability);
		}
	}

	// 创建慢速变形恢复buff的函数
	public static void createSlowTransformBackBuff(CSimulation game, Map<String, Object> localStore, CUnit unit,
			CUnitType newType, OnTransformationActions actions, AbilityBuilderActiveAbility ability, War3ID buffId,
			boolean addAlternateTagAfter, float transformationTime, float duration, boolean permanent,
			boolean takingOff, boolean landing, boolean immediateTakeoff, boolean immediateLanding,
			float altitudeAdjustmentDelay, float landingDelay, float altitudeAdjustmentDuration) {
		if (addAlternateTagAfter && duration > 0) {
			unit.add(game,
					new ABTimedTransformationBuff(game.getHandleIdAllocator().createId(), localStore, actions,
							buffId == null ? ability.getAlias() : buffId, duration, ability, newType,
							!addAlternateTagAfter, permanent, duration, transformationTime, landingDelay,
							altitudeAdjustmentDelay, altitudeAdjustmentDuration, immediateLanding, immediateTakeoff));
		}
	}

	// 创建瞬时变形恢复buff的函数
	public static void createInstantTransformBackBuff(CSimulation game, Map<String, Object> localStore, CUnit unit,
			CUnitType newType, OnTransformationActions actions, AbilityBuilderAbility ability, War3ID buffId,
			boolean addAlternateTagAfter, float transformationTime, float duration, boolean permanent) {
		if (addAlternateTagAfter && duration > 0) {
			ABBuff thebuff = ability.visit(GetInstantTransformationBuffVisitor.getInstance().reset(game, localStore,
					newType, actions, buffId, addAlternateTagAfter, transformationTime, duration, permanent));
			if (thebuff != null) {
				unit.add(game, thebuff);
			}
		}
	}

	// 处理变换动作的类
	public static class OnTransformationActions {
		private int goldCost; // 黄金成本
		private int lumberCost; // 木材成本
		private Integer foodCost; // 食物成本
		private List<ABAction> onTransformActions; // 变换时的动作列表
		private List<ABAction> onUntransformActions; // 反变换时的动作列表

		// 构造函数：初始化反变换动作列表
		public OnTransformationActions(List<ABAction> onUntransformActions) {
			this.onUntransformActions = onUntransformActions;
		}

		// 构造函数：初始化所有成本和两个动作列表
		public OnTransformationActions(int goldCost, int lumberCost, Integer foodCost,
				List<ABAction> onTransformActions, List<ABAction> onUntransformActions) {
			this.goldCost = goldCost;
			this.lumberCost = lumberCost;
			this.foodCost = foodCost;
			this.onTransformActions = onTransformActions;
			this.onUntransformActions = onUntransformActions;
		}

		// 创建反变换动作列表的方法
		public OnTransformationActions createUntransformActions() {
			return new OnTransformationActions(-goldCost, -lumberCost, foodCost != null ? -foodCost : null, null, onUntransformActions);
		}

		// 设置反变换动作列表的方法
		public void setOnUntransformActions(List<ABAction> onUntransformActions) {
			this.onUntransformActions = onUntransformActions;
		}
	}


}
