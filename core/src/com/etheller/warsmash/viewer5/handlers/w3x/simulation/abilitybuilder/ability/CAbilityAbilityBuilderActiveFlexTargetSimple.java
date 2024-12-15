package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills.CBehaviorNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills.CBehaviorTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIdUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

@Deprecated
// CAbilityAbilityBuilderActiveFlexTargetSimple类：实现一种灵活目标的法术能力构建器
public class CAbilityAbilityBuilderActiveFlexTargetSimple extends CAbilitySpellBase {
	private CBehavior behavior;

	// 法术等级数据列表
	List<CAbilityTypeAbilityBuilderLevelData> levelData;
	private AbilityBuilderConfiguration config; // 配置
	private Map<String, Object> localStore; // 本地存储
	private int orderId; // 命令ID
	private int autoCastOnId = 0; // 自动施法开启ID
	private int autoCastOffId = 0; // 自动施法关闭ID
	private boolean autocasting = false; // 是否自动施法标志
	private boolean initialized; // 是否初始化标志

	private int castId = 0; // 施法ID

	private boolean targetedSpell = false; // 是否为目标法术
	private boolean pointTarget = false; // 是否为点目标

	// 构造函数：初始化法术能力构建器
	public CAbilityAbilityBuilderActiveFlexTargetSimple(int handleId, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderConfiguration config,
			Map<String, Object> localStore) {
		super(handleId, alias);
		this.levelData = levelData;
		this.config = config;
		this.localStore = localStore;
		orderId = OrderIdUtils.getOrderId(config.getCastId());
		if (config.getAutoCastOnId() != null) {
			autoCastOnId = OrderIdUtils.getOrderId(config.getAutoCastOnId());
		}
		if (config.getAutoCastOffId() != null) {
			autoCastOffId = OrderIdUtils.getOrderId(config.getAutoCastOffId());
		}
	}

	// 设置目标法术属性
	private void setTargeted(CSimulation game, CUnit unit) {
		if (config.getSpecialFields() != null && config.getSpecialFields().getTargetedSpell() != null) {
			boolean result = true;
			for (ABCondition condition : config.getSpecialFields().getTargetedSpell()) {
				result = result && condition.evaluate(game, unit, localStore, castId);
			}
			this.targetedSpell = result;
		}
	}

	// 设置点目标属性
	private void setPointTarget(CSimulation game, CUnit unit) {
		if (config.getSpecialFields() != null && config.getSpecialFields().getPointTargeted() != null) {
			boolean result = true;
			for (ABCondition condition : config.getSpecialFields().getPointTargeted()) {
				result = result && condition.evaluate(game, unit, localStore, castId);
			}
			this.pointTarget = result;
		}
	}

	// 设置法术的行为
	private void setBehavior(final CUnit unit) {
		if (this.targetedSpell) {
			if (this.behavior == null || !(this.behavior instanceof CBehaviorTargetSpellBase)) {
				this.behavior = new CBehaviorTargetSpellBase(unit, this);
			}
		} else {
			if (this.behavior == null || !(this.behavior instanceof CBehaviorNoTargetSpellBase)) {
				this.behavior = new CBehaviorNoTargetSpellBase(unit, this);
			}
		}
	}

	// 检查是否为目标法术
	public boolean isTargetedSpell() {
		return this.targetedSpell;
	}

	// 检查是否为点目标
	public boolean isPointTarget() {
		return this.pointTarget;
	}

	@Override
	// 设置法术等级
	public void setLevel(CSimulation game, CUnit unit, int level) {
		super.setLevel(game, unit, level);
		localStore.put(ABLocalStoreKeys.CURRENTLEVEL, level);
	}

	@Override
	// 填充数据（更新法术状态）
	public void populateData(GameObject worldEditorAbility, int level) {
		if (this.initialized) {
			CSimulation game = (CSimulation) this.localStore.get(ABLocalStoreKeys.GAME);
			CUnit unit = (CUnit) this.localStore.get(ABLocalStoreKeys.THISUNIT);
			this.setTargeted(game, unit);
			this.setPointTarget(game, unit);
			this.setBehavior(unit);
			if (config.getOnLevelChange() != null) {
				for (ABAction action : config.getOnLevelChange()) {
					action.runAction(game, unit, this.localStore, castId);
				}
			}
		}
		this.initialized = true;
	}

	@Override
	// 获取基础命令ID
	public int getBaseOrderId() {
		return this.orderId;
	}

	// 获取法术等级数据列表
	public List<CAbilityTypeAbilityBuilderLevelData> getLevelData() {
		return this.levelData;
	}

	// 获取法术配置
	public AbilityBuilderConfiguration getConfig() {
		return this.config;
	}

	// 获取本地存储
	public Map<String, Object> getLocalStore() {
		return this.localStore;
	}

	@Override
	// 添加法术时调用
	public void onAdd(CSimulation game, CUnit unit) {
		localStore.put(ABLocalStoreKeys.FLEXABILITY, this);
		this.setTargeted(game, unit);
		this.setPointTarget(game, unit);
		this.setBehavior(unit);
		localStore.put(ABLocalStoreKeys.GAME, game);
		localStore.put(ABLocalStoreKeys.THISUNIT, unit);
		if (config.getOnAddAbility() != null) {
			for (ABAction action : config.getOnAddAbility()) {
				action.runAction(game, unit, localStore, castId);
			}
		}
	}

	@Override
	// 执行法术效果
	public boolean doEffect(CSimulation simulation, CUnit unit, AbilityTarget target) {
		this.castId++;
		if (this.config.getOnBeginCasting() != null) {
			if (this.isTargetedSpell()) {
				if (this.isPointTarget() && target instanceof AbilityPointTarget) {
					localStore.put(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + this.castId, target);
				} else if (!this.isPointTarget()) {
					final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
					this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId, targetUnit);
					final CDestructable targetDest = target.visit(AbilityTargetVisitor.DESTRUCTABLE);
					this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId, targetDest);
					final CItem targetItem = target.visit(AbilityTargetVisitor.ITEM);
					this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId, targetItem);
				}
			}

			for (ABAction action : this.config.getOnBeginCasting()) {
				action.runAction(simulation, unit, this.localStore, castId);
			}

			if (this.isTargetedSpell()) {
				if (this.isPointTarget()) {
					this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + castId);
				} else if (!this.isPointTarget()) {
					this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId);
					this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId);
					this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId);
				}
			}
		}
		return false;
	}

	@Override
	// 检查是否可以使用法术
	protected void innerCheckCanUseSpell(CSimulation game, CUnit unit, int orderId,
			AbilityActivationReceiver receiver) {
		if (config.getExtraCastConditions() != null) {
			boolean result = true;
			for (ABCondition condition : config.getExtraCastConditions()) {
				result = result && condition.evaluate(game, unit, localStore, castId);
			}
			if (result) {
				receiver.useOk();
			} else {
				String failReason = (String) localStore.get(ABLocalStoreKeys.CANTUSEREASON);
				if (failReason != null) {
					receiver.activationCheckFailed(failReason);
				} else {
					receiver.unknownReasonUseNotOk();
				}
			}
		} else {
			receiver.useOk();
		}
	}

	// 检查是否可以对目标施法
	protected void innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		if (this.config.getExtraTargetConditions() != null) {
			final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
			this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId, targetUnit);
			final CDestructable targetDest = target.visit(AbilityTargetVisitor.DESTRUCTABLE);
			this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId, targetDest);
			final CItem targetItem = target.visit(AbilityTargetVisitor.ITEM);
			this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId, targetItem);
			boolean result = true;
			for (ABCondition condition : config.getExtraTargetConditions()) {
				result = result && condition.evaluate(game, unit, localStore, castId);
			}
			if (result) {
				receiver.targetOk(targetUnit);
			} else {
				String failReason = (String) localStore.get(ABLocalStoreKeys.CANTUSEREASON);
				if (failReason != null) {
					receiver.targetCheckFailed(failReason);
				} else {
					receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_THIS_UNIT);
				}
			}
		} else {
			receiver.targetOk(target);
		}
	}

	// 检查是否可以对点目标施法
	protected void innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (this.config.getExtraTargetConditions() != null) {
			localStore.put(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + this.castId, target);
			boolean result = true;
			for (ABCondition condition : config.getExtraTargetConditions()) {
				result = result && condition.evaluate(game, unit, localStore, castId);
			}
			if (result) {
				receiver.targetOk(target);
			} else {
				String failReason = (String) localStore.get(ABLocalStoreKeys.CANTUSEREASON);
				if (failReason != null) {
					receiver.targetCheckFailed(failReason);
				} else {
					receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_THERE);
				}
			}
			localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + this.castId);
		} else {
			receiver.targetOk(target);
		}
	}

	@Override
	// 获取自动施法开启命令ID
	public int getAutoCastOnOrderId() {
		return this.autoCastOnId;
	}

	@Override
	// 获取自动施法关闭命令ID
	public int getAutoCastOffOrderId() {
		return this.autoCastOffId;
	}

	@Override
	// 检查是否自动施法开启
	public boolean isAutoCastOn() {
		return this.autocasting;
	}

	@Override
	// 设置自动施法
	public void setAutoCastOn(final CUnit caster, final boolean autoCastOn) {
		this.autocasting = autoCastOn;
	}

	@Override
	// 开始施法（针对目标）
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target) {
		if (this.isTargetedSpell() && !this.isPointTarget()) {
			if (this.behavior instanceof CBehaviorTargetSpellBase) {
				return ((CBehaviorTargetSpellBase) this.behavior).reset(target);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	// 开始施法（针对点目标）
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, AbilityPointTarget point) {
		if (this.isTargetedSpell() && this.isPointTarget()) {
			if (this.behavior instanceof CBehaviorTargetSpellBase) {
				return ((CBehaviorTargetSpellBase) this.behavior).reset(point);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	// 开始无目标施法
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId) {
		if (!this.isTargetedSpell()) {
			if (this.behavior instanceof CBehaviorNoTargetSpellBase) {
				return ((CBehaviorNoTargetSpellBase) this.behavior).reset();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	// 检查是否可以对目标施法
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		if (this.isTargetedSpell() && !this.isPointTarget()) {
			if (target.canBeTargetedBy(game, unit, getTargetsAllowed(), receiver)) {
				if (!unit.isMovementDisabled() || unit.canReach(target, getCastRange())) {
					this.innerCheckCanTargetSpell(game, unit, orderId, target, receiver);
				} else {
					receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
				}
			}
		} else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	// 检查是否可以对点目标施法
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (this.isTargetedSpell() && this.isPointTarget()) {
			if (!unit.isMovementDisabled() || unit.canReach(target, getCastRange())) {
				this.innerCheckCanTargetSpell(game, unit, orderId, target, receiver);
			} else {
				receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
			}
		} else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	// 检查是否可以进行无目标施法
	protected void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		if (!this.isTargetedSpell()) {
			receiver.targetOk(null);
		} else {
			receiver.orderIdNotAccepted();
		}
	}

}
