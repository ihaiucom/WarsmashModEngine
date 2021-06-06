package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.test;

import java.util.EnumSet;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.test.CBehaviorCoupleInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderTargetWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityCoupleInstant extends AbstractGenericSingleIconNoSmartActiveAbility {

	private final War3ID resultingUnitType;
	private final War3ID partnerUnitType;
	private final boolean moveToPartner;
	private final float castRange;
	private final float area;
	private final EnumSet<CTargetType> targetsAllowed;
	private CBehaviorCoupleInstant behaviorCoupleInstant;

	public CAbilityCoupleInstant(final int handleId, final War3ID alias, final War3ID resultingUnitType,
			final War3ID partnerUnitType, final boolean moveToPartner, final float castRange, final float area,
			final EnumSet<CTargetType> targetsAllowed) {
		super(handleId, alias);
		this.resultingUnitType = resultingUnitType;
		this.partnerUnitType = partnerUnitType;
		this.moveToPartner = moveToPartner;
		this.castRange = castRange;
		this.area = area;
		this.targetsAllowed = targetsAllowed;
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.coupleinstant;
	}

	@Override
	public boolean isToggleOn() {
		return false;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behaviorCoupleInstant = new CBehaviorCoupleInstant(unit, this);
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {

	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		// only from engine, not ever allowed by the checks
		if (target instanceof CUnit) {
			return this.behaviorCoupleInstant.reset((CUnit) target);
		}
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		final PossiblePairFinderEnum possiblePairFinder = new PossiblePairFinderEnum(caster);
		game.getWorldCollision().enumUnitsInRect(
				new Rectangle(caster.getX() - this.area, caster.getY() - this.area, this.area * 2, this.area * 2),
				possiblePairFinder);
		final CUnit coupleTarget = possiblePairFinder.pairMatchFound;
		if (coupleTarget == null) {
			game.getCommandErrorListener(caster.getPlayerIndex()).showUnableToFindCoupleTargetError();
			return caster.pollNextOrderBehavior(game);
		}
		coupleTarget.order(game, new COrderTargetWidget(possiblePairFinder.pairMatchAbility.getHandleId(),
				possiblePairFinder.pairMatchAbility.getBaseOrderId(), caster.getHandleId(), false), false);
		return this.behaviorCoupleInstant.reset(coupleTarget);
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.targetOk(target);
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.targetOk(null);
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	public float getCastRange() {
		return this.castRange;
	}

	public float getArea() {
		return this.area;
	}

	public EnumSet<CTargetType> getTargetsAllowed() {
		return this.targetsAllowed;
	}

	public War3ID getResultingUnitType() {
		return this.resultingUnitType;
	}

	private final class PossiblePairFinderEnum implements CUnitEnumFunction {
		private final CUnit unit;
		private CUnit pairMatchFound = null;
		private CAbilityCoupleInstant pairMatchAbility;

		private PossiblePairFinderEnum(final CUnit unit) {
			this.unit = unit;
		}

		@Override
		public boolean call(final CUnit otherUnit) {
			if (otherUnit.getPlayerIndex() == this.unit.getPlayerIndex()) {
				for (final CAbility ability : otherUnit.getAbilities()) {
					if (ability instanceof CAbilityCoupleInstant) {
						final CAbilityCoupleInstant otherCoupleInstant = (CAbilityCoupleInstant) ability;
						if (otherCoupleInstant.partnerUnitType.equals(this.unit.getTypeId())) {
							if (CAbilityCoupleInstant.this.partnerUnitType.equals(otherUnit.getTypeId())) {
								// we're a pair, make sure other unit is not already actively pairing
								if (!(otherUnit.getCurrentBehavior() instanceof CBehaviorCoupleInstant)) {
									if (otherUnit.distance(this.unit) <= CAbilityCoupleInstant.this.area) {
										this.pairMatchFound = otherUnit;
										this.pairMatchAbility = otherCoupleInstant;
										break;
									}
								}
							}
						}
					}
				}
			}
			return this.pairMatchFound != null;
		}
	}

}
