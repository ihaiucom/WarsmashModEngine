package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.root;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.eattree.CAbilityEatTree;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttackMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.root.CBehaviorRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.root.CBehaviorUproot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
/**
 * CAbilityRoot类表示一个固根能力，它使单位根植于地面并具有特定的行为和攻击模式。
 */
public class CAbilityRoot extends AbstractGenericSingleIconNoSmartActiveAbility {
	private boolean rooted;

	private int rootedWeaponsAttackBits;
	private int uprootedWeaponsAttackBits;
	private boolean rootedTurning;
	private CDefenseType uprootedDefenseType;
	private float duration;
	private float offDuration;

	private final List<CAbility> rootedAbilities = new ArrayList<>();
	private final List<CAbility> uprootedAbilities = new ArrayList<>();

	private CBehaviorRoot behaviorRoot;
	private CBehaviorUproot behaviorUproot;
	private CBehaviorMove moveBehavior;
	private CBehaviorAttackMove attackMoveBehavior;

	private List<CUnitAttack> rootedAttacks;

	private List<CUnitAttack> uprootedAttacks;

	/**
	 * CAbilityRoot构造函数，初始化能力的基本属性。
	 *
	 * @param handleId                 处理ID
	 * @param code                     能力代码
	 * @param alias                    能力别名
	 * @param rootedWeaponsAttackBits  固根状态下的武器攻击位
	 * @param uprootedWeaponsAttackBits 翻起状态下的武器攻击位
	 * @param rootedTurning            固根时是否允许转向
	 * @param uprootedDefenseType      翻起状态下的防御类型
	 * @param duration                 持续时间
	 * @param offDuration              离开的持续时间
	 */
	public CAbilityRoot(final int handleId, final War3ID code, final War3ID alias, final int rootedWeaponsAttackBits,
			final int uprootedWeaponsAttackBits, final boolean rootedTurning, final CDefenseType uprootedDefenseType,
			final float duration, final float offDuration) {
		super(handleId, code, alias);
		this.rootedWeaponsAttackBits = rootedWeaponsAttackBits;
		this.uprootedWeaponsAttackBits = uprootedWeaponsAttackBits;
		this.rootedTurning = rootedTurning;
		this.uprootedDefenseType = uprootedDefenseType;
		this.duration = duration;
		this.offDuration = offDuration;
	}

	@Override
	public int getBaseOrderId() {
		return this.rooted ? OrderIds.unroot : OrderIds.root;
	}

	@Override
	public boolean isToggleOn() {
		return this.rooted;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		// 添加固根能力时清空能力列表并进行初始化。
		this.uprootedAbilities.clear();
		this.rootedAbilities.clear();
		for (final CAbility ability : unit.getAbilities()) {
			if ((ability instanceof CAbilityMove) || (ability instanceof CAbilityEatTree)) {
				this.uprootedAbilities.add(ability);
			}
			else if ((ability instanceof CAbilityAttack) || (ability instanceof CAbilityRoot)) {
			}
			else {
				this.rootedAbilities.add(ability);
			}
		}
		this.rootedAttacks = CUnitData.getEnabledAttacks(unit.getUnitSpecificAttacks(), this.rootedWeaponsAttackBits);
		this.uprootedAttacks = CUnitData.getEnabledAttacks(unit.getUnitSpecificAttacks(),
				this.uprootedWeaponsAttackBits);

		this.behaviorRoot = new CBehaviorRoot(unit, this);
		this.behaviorUproot = new CBehaviorUproot(unit, this);
		this.moveBehavior = unit.getMoveBehavior();
		unit.setMoveBehavior(null);
		this.attackMoveBehavior = unit.getAttackMoveBehavior();
		unit.setAttackMoveBehavior(null);
		this.rooted = true;
		for (final CAbility ability : this.uprootedAbilities) {
			unit.remove(game, ability);
		}
		unit.setFacing(game.getGameplayConstants().getRootAngle());
		unit.setUnitSpecificCurrentAttacks(this.rootedAttacks);
		unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.ALTERNATE);
	}

	@Override
	public void onSetUnitType(final CSimulation game, final CUnit unit) {
		// 单位类型变化时更新能力列表和攻击模式。
		this.uprootedAbilities.clear();
		this.rootedAbilities.clear();
		for (final CAbility ability : unit.getAbilities()) {
			if ((ability instanceof CAbilityMove) || (ability instanceof CAbilityEatTree)) {
				this.uprootedAbilities.add(ability);
			}
			else if ((ability instanceof CAbilityAttack) || (ability instanceof CAbilityRoot)) {
			}
			else {
				this.rootedAbilities.add(ability);
			}
		}
		this.rootedAttacks = CUnitData.getEnabledAttacks(unit.getUnitSpecificAttacks(), this.rootedWeaponsAttackBits);
		this.uprootedAttacks = CUnitData.getEnabledAttacks(unit.getUnitSpecificAttacks(),
				this.uprootedWeaponsAttackBits);
		this.moveBehavior = unit.getMoveBehavior();
		this.attackMoveBehavior = unit.getAttackMoveBehavior();

		if (this.rooted) {
			for (final CAbility ability : this.uprootedAbilities) {
				unit.remove(game, ability);
			}
			unit.setMoveBehavior(null);
			unit.setAttackMoveBehavior(null);
			unit.setFacing(game.getGameplayConstants().getRootAngle());
			unit.setUnitSpecificCurrentAttacks(this.rootedAttacks);
			unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.ALTERNATE);
		}
		else {
			for (final CAbility ability : this.rootedAbilities) {
				unit.remove(game, ability);
			}
			unit.setMoveBehavior(this.moveBehavior);
			unit.setAttackMoveBehavior(this.attackMoveBehavior);
			unit.setUnitSpecificCurrentAttacks(this.uprootedAttacks);
		}

	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		// TODO reset unit settings here
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
		// 每个游戏回合的更新逻辑。
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
		// 取消队列中能力的逻辑。
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		// 开始固根能力的逻辑。
		if (!this.rooted && (orderId == OrderIds.root)) {
			return this.behaviorRoot.reset(point);
		}
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		// 开始翻起能力的逻辑。
		if (this.rooted && (orderId == OrderIds.unroot)) {
			return this.behaviorUproot.reset();
		}
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		// 检查是否可以以目标点为目标。
		if (!this.rooted && (orderId == OrderIds.root)) {
			receiver.targetOk(target);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		// 检查是否可以执行无目标的能力。
		if (this.rooted && (orderId == OrderIds.unroot)) {
			receiver.targetOk(null);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		// 检查能力是否可以使用。
		if (unit.isBuildQueueActive()) {
			receiver.disabled();
		}
		else {
			receiver.useOk();
		}
	}

	/**
	 * 判断单位是否处于固根状态。
	 *
	 * @return true如果单位处于固根状态，否则为false。
	 */
	public boolean isRooted() {
		return this.rooted;
	}

	/**
	 * 设置单位的固根状态，并执行相关逻辑。
	 *
	 * @param rooted 指定的固根状态
	 * @param unit   受影响的单位
	 * @param game   当前游戏实例
	 */
	public void setRooted(final boolean rooted, final CUnit unit, final CSimulation game) {
		final boolean rooting = !this.rooted && rooted;
		final boolean uprooting = this.rooted && !rooted;
		this.rooted = rooted;
		if (rooting) {
			game.getWorldCollision().removeUnit(unit);
			for (final CAbility ability : this.uprootedAbilities) {
				unit.remove(game, ability);
			}
			for (final CAbility ability : this.rootedAbilities) {
				unit.add(game, ability);
			}
			unit.setMoveBehavior(null);
			unit.setAttackMoveBehavior(null);
			unit.setUnitSpecificCurrentAttacks(this.rootedAttacks);
			unit.setDefenseType(unit.getUnitType().getDefenseType());
			unit.setStructure(true);
			unit.regeneratePathingInstance(game, unit.getUnitType().getBuildingPathingPixelMap());
			game.getWorldCollision().addUnit(unit);
		}
		else if (uprooting) {
			game.getWorldCollision().removeUnit(unit);
			for (final CAbility ability : this.rootedAbilities) {
				unit.remove(game, ability);
			}
			for (final CAbility ability : this.uprootedAbilities) {
				unit.add(game, ability);
			}
			unit.setMoveBehavior(this.moveBehavior);
			unit.setAttackMoveBehavior(this.attackMoveBehavior);
			unit.setUnitSpecificCurrentAttacks(this.uprootedAttacks);
			unit.setDefenseType(this.uprootedDefenseType);
			unit.setStructure(false);
			unit.killPathingInstance();
			game.getWorldCollision().addUnit(unit);
		}
	}

	public int getRootedWeaponsAttackBits() {
		return this.rootedWeaponsAttackBits;
	}

	public void setRootedWeaponsAttackBits(final int rootedWeaponsAttackBits) {
		this.rootedWeaponsAttackBits = rootedWeaponsAttackBits;
	}

	public int getUprootedWeaponsAttackBits() {
		return this.uprootedWeaponsAttackBits;
	}

	public void setUprootedWeaponsAttackBits(final int uprootedWeaponsAttackBits) {
		this.uprootedWeaponsAttackBits = uprootedWeaponsAttackBits;
	}

	public boolean isRootedTurning() {
		return this.rootedTurning;
	}

	public void setRootedTurning(final boolean rootedTurning) {
		this.rootedTurning = rootedTurning;
	}

	public CDefenseType getUprootedDefenseType() {
		return this.uprootedDefenseType;
	}

	public void setUprootedDefenseType(final CDefenseType uprootedDefenseType) {
		this.uprootedDefenseType = uprootedDefenseType;
	}

	public float getDuration() {
		return this.duration;
	}

	public void setDuration(final float duration) {
		this.duration = duration;
	}

	public float getOffDuration() {
		return this.offDuration;
	}

	public void setOffDuration(final float offDuration) {
		this.offDuration = offDuration;
	}

	public List<CAbility> getRootedAbilities() {
		return this.rootedAbilities;
	}

	public List<CAbility> getUprootedAbilities() {
		return this.uprootedAbilities;
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public boolean isPhysical() {
		return false;
	}

	@Override
	public boolean isUniversal() {
		return false;
	}

	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.CORE;
	}

}
