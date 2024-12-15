package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.farseer;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentLightning;

public class CAbilityChainLightning extends CAbilityTargetSpellBase {
	private static final float SECONDS_BETWEEN_JUMPS = 0.25f;
	private static final float BOLT_LIFETIME_SECONDS = 2.00f;
	private War3ID lightningIdPrimary;
	private War3ID lightningIdSecondary;
	private float damagePerTarget;
	private float damageReductionPerTarget;
	private int numberOfTargetsHit;
	private float areaOfEffect;

	public CAbilityChainLightning(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.manaburn;
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.lightningIdPrimary = AbstractCAbilityTypeDefinition.getLightningId(worldEditorAbility, level, 0);
		this.lightningIdSecondary = AbstractCAbilityTypeDefinition.getLightningId(worldEditorAbility, level, 1);
		this.damagePerTarget = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		this.damageReductionPerTarget = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_C + level, 0);
		this.numberOfTargetsHit = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_B + level, 0);
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			final SimulationRenderComponentLightning lightning = simulation.createLightning(caster, lightningIdPrimary,
					targetUnit);
			simulation.createTemporarySpellEffectOnUnit(targetUnit, getAlias(), CEffectType.TARGET);
			final int jumpDelayEndTick = simulation.getGameTurnTick()
					+ (int) StrictMath.ceil(SECONDS_BETWEEN_JUMPS / WarsmashConstants.SIMULATION_STEP_TIME);
			final int boltLifetimeEndTick = simulation.getGameTurnTick()
					+ (int) StrictMath.ceil(BOLT_LIFETIME_SECONDS / WarsmashConstants.SIMULATION_STEP_TIME);
			targetUnit.damage(simulation, caster, false, true, CAttackType.SPELLS, CDamageType.LIGHTNING,
					CWeaponSoundTypeJass.WHOKNOWS.name(), damagePerTarget);
			final float remainingDamageJumpMultiplier = 1.0f - damageReductionPerTarget;
			final Set<CUnit> previousTargets = new HashSet<>();
			previousTargets.add(targetUnit);
			simulation.registerEffect(new CEffectChainLightningBolt(boltLifetimeEndTick, jumpDelayEndTick,
					damagePerTarget * remainingDamageJumpMultiplier, remainingDamageJumpMultiplier, caster, targetUnit,
					lightning, getAlias(), lightningIdSecondary, getTargetsAllowed(), this.areaOfEffect,
					this.numberOfTargetsHit - 1, previousTargets));
		}
		return false;
	}

	// 闪电链效果的实现类，实现了CEffect接口
	private static final class CEffectChainLightningBolt implements CEffect {

			// 闪电效果的结束时间
			private final int boltLifetimeEndTick;
			// 每次跳跃后剩余的伤害值
			private final float remainingDamage;
			// 每次跳跃后伤害的减少比例
			private final float remainingDamageJumpMultiplier;
			// 施放者
			private final CUnit caster;
			// 目标单位
			private final CUnit targetUnit;
			// 闪电效果的渲染组件
			private final SimulationRenderComponentLightning boltFx;
			// 闪电链技能的ID
			private final War3ID abilityId;
			// 每次跳跃时使用的闪电效果的ID
			private final War3ID jumpLightningId;
			// 闪电链可以跳跃的目标类型
			private final EnumSet<CTargetType> jumpTargetsAllowed;
			// 闪电链跳跃的半径范围
			private final float jumpRadius;
			// 闪电链剩余的跳跃次数
			private final int remainingJumps;
			// 下一次跳跃的延迟结束的时钟刻度
			private int jumpDelayEndTick;

			// 闪电链跳跃的目标集合
			private final Set<CUnit> previousTargets;

			// 构造函数，初始化闪电链效果的各个属性
			public CEffectChainLightningBolt(final int boltLifetimeEndTick, final int jumpDelayEndTick,
					final float remainingDamage, final float remainingDamageJumpMultiplier, final CUnit caster,
					final CUnit targetUnit, final SimulationRenderComponentLightning boltFx, final War3ID abilityId,
					final War3ID jumpLightningId, final EnumSet<CTargetType> jumpTargetsAllowed, final float jumpRadius,
					final int remainingJumps, final Set<CUnit> previousTargets) {
				this.boltLifetimeEndTick = boltLifetimeEndTick;
				this.jumpDelayEndTick = jumpDelayEndTick;
				this.remainingDamage = remainingDamage;
				this.remainingDamageJumpMultiplier = remainingDamageJumpMultiplier;
				this.caster = caster;
				this.targetUnit = targetUnit;
				this.boltFx = boltFx;
				this.abilityId = abilityId;
				this.jumpLightningId = jumpLightningId;
				this.jumpTargetsAllowed = jumpTargetsAllowed;
				this.jumpRadius = jumpRadius;
				this.remainingJumps = remainingJumps;
				this.previousTargets = previousTargets;
			}

			// 更新闪电链效果，处理跳跃和伤害逻辑
			@Override
			public boolean update(final CSimulation game) {
				// 获取当前游戏时间刻度
				final int gameTurnTick = game.getGameTurnTick();
				// 判断是否满足跳跃条件: 这里判断当前时间是否已经超过了下一次跳跃的延迟时间（jumpDelayEndTick），并且剩余的跳跃次数（remainingJumps）是否大于0。如果满足这两个条件，则执行跳跃逻辑。
				if ((gameTurnTick >= jumpDelayEndTick) && (remainingJumps > 0)) {
					// #查找可能的跳跃目标：这段代码通过遍历目标单位周围一定范围内的所有单位（jumpRadius），找出可以被跳跃到的目标单位（possibleJumpTargets）。条件是这些单位可以被施法者（caster）选中，并且没有被之前的跳跃选中过。
					final List<CUnit> possibleJumpTargets = new ArrayList<>();
					// 查找在目标位置，指定半径范围内的单位
					game.getWorldCollision().enumUnitsInRange(targetUnit.getX(), targetUnit.getY(), jumpRadius,
							(enumUnit) -> {
								if (enumUnit.canBeTargetedBy(game, caster, jumpTargetsAllowed)
										&& !previousTargets.contains(enumUnit)) {
									possibleJumpTargets.add(enumUnit);
								}
								return false;
							});
					// 选择下一个跳跃目标： 如果找到了可能的跳跃目标，则随机选择一个作为下一个跳跃目标（nextJumpTarget）。
					if (!possibleJumpTargets.isEmpty()) {
						// 则随机选择一个作为下一个跳跃目标（nextJumpTarget）。
						final CUnit nextJumpTarget = possibleJumpTargets
								.get(game.getSeededRandom().nextInt(possibleJumpTargets.size()));

						// 创建闪电效果，并注册到下一个跳跃目标的单位身上。
						// 这段代码创建了从当前目标单位到下一个目标单位的闪电效果，并在下一个目标单位上施加临时的技能效果。同时，计算下一次跳跃的延迟时间和闪电效果的结束时间，并对下一个目标单位造成伤害
						final SimulationRenderComponentLightning lightning = game.createLightning(targetUnit,
								jumpLightningId, nextJumpTarget);
						// 创建施法效果, 目标对象
						game.createTemporarySpellEffectOnUnit(nextJumpTarget, abilityId, CEffectType.TARGET);

						// 计算下一次跳跃的延迟时间和闪电效果的结束时间，并对下一个目标单位造成伤害
						final int jumpDelayEndTick = gameTurnTick
								+ (int) StrictMath.ceil(SECONDS_BETWEEN_JUMPS / WarsmashConstants.SIMULATION_STEP_TIME);
						final int boltLifetimeEndTick = gameTurnTick
								+ (int) StrictMath.ceil(BOLT_LIFETIME_SECONDS / WarsmashConstants.SIMULATION_STEP_TIME);
						// 计算跳跃的伤害值
						nextJumpTarget.damage(game, caster, false, true, CAttackType.SPELLS, CDamageType.LIGHTNING,
								CWeaponSoundTypeJass.WHOKNOWS.name(), remainingDamage);
						// 添加到已经处理过的目标集合中
						previousTargets.add(nextJumpTarget);
						// 注册下一个闪电链效果
						game.registerEffect(new CEffectChainLightningBolt(boltLifetimeEndTick, jumpDelayEndTick,
								remainingDamage * remainingDamageJumpMultiplier, remainingDamageJumpMultiplier, caster,
								nextJumpTarget, lightning, abilityId, jumpLightningId, jumpTargetsAllowed, jumpRadius,
								remainingJumps - 1, previousTargets));
					}
					//表示已经处理过了， 将跳跃延迟时间设置为最大值，以防止在同一时间刻度内重复跳跃。
					this.jumpDelayEndTick = Integer.MAX_VALUE;
				}
				final boolean done = gameTurnTick >= boltLifetimeEndTick;
				if (done) {
					boltFx.remove();
				}
				return done;
			}
		}

}
