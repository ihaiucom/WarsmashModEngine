package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.nightelf.keeper;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructableEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPointTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimedLife;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
// 自然之力 将一定范围内的树木转化成树人，每个树人具有<efon,realHP>点的生命值和<efon,mindmg1>-<efon,maxdmg1>点的攻击力。可以学会自然之祝福能力。|n|n|cffffcc00能攻击地面单位。|r|n|n|cffffcc00等级 1|r - 召唤<AEfn,DataA1>个树人，持续时间<AEfn,Dur1>秒。|n|cffffcc00等级 2|r - 召唤<AEfn,DataA2>个树人，持续时间<AEfn,Dur2>秒。|n|cffffcc00等级 3|r - 召唤<AEfn,DataA3>个树人，持续时间<AEfn,Dur3>秒。

public class CAbilityForceOfNature extends CAbilityPointTargetSpellBase {

	private final AnyMatchingDestFinder anyMatchingDestFinder;
	private int numberOfSummonedUnits;
	private War3ID summonedUnitId;
	private War3ID buffId;
	private float areaOfEffect;

	public CAbilityForceOfNature(final int handleId, final War3ID alias) {
		super(handleId, alias);
		this.anyMatchingDestFinder = new AnyMatchingDestFinder();
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.forceofnature;
	}

	@Override
	public float getUIAreaOfEffect() {
		return this.areaOfEffect;
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.numberOfSummonedUnits = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_A + level, 0);
		this.summonedUnitId = War3ID.fromString(worldEditorAbility.getFieldAsString(AbilityFields.UNIT_ID + level, 0));
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		game.getWorldCollision().enumDestructablesInRange(target.getX(), target.getY(), this.areaOfEffect,
				this.anyMatchingDestFinder.reset(game, unit));
		if (!this.anyMatchingDestFinder.foundMatch) {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_TREE);
		}
		else {
			super.innerCheckCanTarget(game, unit, orderId, target, receiver);
		}
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		final List<CDestructable> trees = new ArrayList<>();
		simulation.getWorldCollision().enumDestructablesInRange(target.getX(), target.getY(), this.areaOfEffect,
				(enumDest) -> {
					if (enumDest.canBeTargetedBy(simulation, caster, getTargetsAllowed())) {
						trees.add(enumDest);
					}
					return false;
				});
		int summonedUnitCount = 0;
		for (final CDestructable tree : trees) {
			tree.setLife(simulation, 0);

			if (summonedUnitCount < this.numberOfSummonedUnits) {
				final CUnit summonedUnit = simulation.createUnitSimple(this.summonedUnitId, caster.getPlayerIndex(),
						tree.getX(), tree.getY(), simulation.getGameplayConstants().getBuildingAngle());
				summonedUnit.addClassification(CUnitClassification.SUMMONED);
				summonedUnit.add(simulation, new CBuffTimedLife(simulation.getHandleIdAllocator().createId(),
						this.buffId, getDuration(), false));
				summonedUnitCount++;
			}
		}
		return false;
	}

	private class AnyMatchingDestFinder implements CDestructableEnumFunction {
		private CSimulation game;
		private CUnit unit;
		private boolean foundMatch = false;

		public AnyMatchingDestFinder reset(final CSimulation game, final CUnit unit) {
			this.game = game;
			this.unit = unit;
			this.foundMatch = false;
			return this;
		}

		@Override
		public boolean call(final CDestructable enumDest) {
			if (enumDest.canBeTargetedBy(this.game, this.unit, getTargetsAllowed())) {
				this.foundMatch = true;
			}
			return this.foundMatch;
		}
	}
}
