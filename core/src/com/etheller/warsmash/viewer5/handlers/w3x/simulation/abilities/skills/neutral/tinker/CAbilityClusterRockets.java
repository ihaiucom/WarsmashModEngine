package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.tinker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPointTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffStun;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
// 火箭群 对某个区域用火箭进行攻击，使目标在<ANcs,Dur1> 秒内处于昏晕状态，并对其造成一定程度的伤害。|n|n|cffffcc00等级 1|r - 35 攻击力。|n|cffffcc00等级 2|r - 65 攻击力。|n|cffffcc00等级 3|r - 100 攻击力。

public class CAbilityClusterRockets extends CAbilityPointTargetSpellBase {
	// 导弹到达延迟时间，单位秒
	private static final float MISSILE_ARRIVAL_DELAY = 1.0f;

	// 每枚导弹发射之间的延迟时间，单位秒
	private static final float PER_ROCKET_DELAY = 0.01f;

	// 用于回收导弹的矩形区域
	private final Rectangle recycleRect = new Rectangle();

	// 建筑物伤害减少量
	private float buildingReduction;

	// 伤害值
	private float damage;

	// 每波最大伤害量
	private float maximumDamagePerWave;

	// 导弹数量
	private int missileCount;

	// 伤害间隔时间
	private float damageInterval;

	// 效果持续时间
	private float effectDuration;

	// 效果影响范围
	private float areaOfEffect;

	// 当前波次
	private int currentWave;

	// 下一波开始的刻度
	private int nextWaveTick;

	// 下一枚导弹发射的刻度
	private int nextMissileTick;

	// 当前导弹索引
	private int currentMissile;

	// 效果持续时间结束的刻度
	private int effectDurationEndTick;

	// 导弹发射结束的刻度
	private int missileLaunchingEndTick;

	// 导弹发射持续时间的刻度
	private int missileLaunchDurationTicks;

	// 增益效果的War3ID
	private War3ID buffId;


	public CAbilityClusterRockets(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.buildingReduction = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_E + level, 0);
		this.damage = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		this.maximumDamagePerWave = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_D + level, 0);
		this.missileCount = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_C + level, 0);

		this.damageInterval = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);
		this.effectDuration = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_F + level, 0);
		setCastingTime(0); // dont use the casting time field normally
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.clusterrockets;
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		// 获取当前游戏回合的刻度
		final int gameTurnTick = simulation.getGameTurnTick();
		// 初始化当前波次为0
		this.currentWave = 0;
		// 初始化当前导弹为0
		this.currentMissile = 0;
		// 计算导弹到达所需的刻度数，向上取整
		final int missileTravelTicks = (int) StrictMath
				.ceil(MISSILE_ARRIVAL_DELAY / WarsmashConstants.SIMULATION_STEP_TIME);
		// 计算效果持续时间所需的刻度数，向上取整
		int durationTicks = (int) StrictMath.ceil(effectDuration / WarsmashConstants.SIMULATION_STEP_TIME);
		// 计算下一波开始的刻度
		this.nextWaveTick = gameTurnTick + missileTravelTicks;
		// 初始化下一枚导弹发射的刻度
		this.nextMissileTick = gameTurnTick;
		// 计算效果结束的刻度
		this.effectDurationEndTick = nextWaveTick + durationTicks;

		// 计算预期发射的导弹数量，向上取整
		final int expectedMissileCount = (int) StrictMath.ceil((effectDuration) / PER_ROCKET_DELAY);
		// 初始化导弹发射结束的刻度
		missileLaunchingEndTick = gameTurnTick + durationTicks;
		// 如果预期的导弹数量大于实际导弹数量
		if (expectedMissileCount > this.missileCount) {
			// 计算缩短后的持续时间刻度数，基于实际导弹数量和每枚火箭的延迟时间
			final int shortenedDurationTicks = this.missileCount
					* (int) StrictMath.ceil(PER_ROCKET_DELAY / WarsmashConstants.SIMULATION_STEP_TIME);
			// 更新导弹发射结束的刻度
			missileLaunchingEndTick = gameTurnTick + shortenedDurationTicks;
			// 更新持续时间刻度数
			durationTicks = shortenedDurationTicks;
		}
		// 设置导弹发射持续时间的刻度数
		missileLaunchDurationTicks = durationTicks;

		return true;
	}

	@Override
	public boolean doChannelTick(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		final int gameTurnTick = simulation.getGameTurnTick(); // 获取当前游戏回合的刻度
		if ((gameTurnTick >= this.nextMissileTick) && (this.currentMissile < this.missileCount)) { // 检查是否到了发射下一枚导弹的时机且导弹数量未超过限制
			final Random seededRandom = simulation.getSeededRandom(); // 获取一个种子随机数生成器
			final float elapsedTimeRatio = 1.0f
					- ((missileLaunchingEndTick - gameTurnTick) / (float) missileLaunchDurationTicks); // 计算从开始发射导弹到当前的已过去时间比例
			final float targetingAngle = (float) ((unit.angleTo(target) + StrictMath.PI)
					- (elapsedTimeRatio * (StrictMath.PI * 2))); // 计算导弹的目标角度

			final float targetingX = target.getX() + ((float) StrictMath.cos(targetingAngle) * areaOfEffect * 0.5f); // 计算目标点的X坐标
			final float targetingY = target.getY() + ((float) StrictMath.sin(targetingAngle) * areaOfEffect * 0.5f); // 计算目标点的Y坐标
			final float randomAngle = seededRandom.nextFloat((float) (StrictMath.PI * 2)); // 生成一个随机的角度
			final float randomDistance = seededRandom.nextFloat() * this.areaOfEffect * 0.25f; // 生成一个随机的距离
			final float missileLandX = targetingX + ((float) StrictMath.cos(randomAngle) * randomDistance); // 计算导弹落点的X坐标
			final float missileLandY = targetingY + ((float) StrictMath.sin(randomAngle) * randomDistance); // 计算导弹落点的Y坐标
			final AbilityPointTarget missileLandPoint = new AbilityPointTarget(missileLandX, missileLandY); // 创建一个导弹落点目标
			final double angleToLandPoint = unit.angleTo(missileLandPoint); // 计算单位到导弹落点的角度
			final double distance = unit.distance(missileLandPoint); // 计算单位到导弹落点的距离
			double speed = distance / MISSILE_ARRIVAL_DELAY; // 计算导弹的速度
			if (speed < simulation.getGameplayConstants().getMinUnitSpeed()) { // 如果计算出的速度小于游戏中的最小单位速度
				speed = simulation.getGameplayConstants().getMinUnitSpeed(); // 则将速度设置为最小单位速度
			}
			simulation.createProjectile(unit, getAlias(), unit.getX(), unit.getY(), (float) angleToLandPoint, // 创建导弹
					(float) speed, false, missileLandPoint, CAbilityProjectileListener.DO_NOTHING);
			this.nextMissileTick = gameTurnTick // 更新下一次发射导弹的时间刻度
					+ (int) StrictMath.ceil(PER_ROCKET_DELAY / WarsmashConstants.SIMULATION_STEP_TIME);
			currentMissile++; // 增加已发射导弹的数量
		}

		if (gameTurnTick >= this.nextWaveTick) { // 如果当前游戏回合大于等于下一波攻击的时间
			this.currentWave++; // 增加当前波次
			final List<CUnit> damageTargets = new ArrayList<>(); // 创建一个列表来存储受损目标

			// 枚举矩形区域内的所有单位，并检查它们是否可以作为攻击目标
			simulation.getWorldCollision()
					.enumUnitsInRect(this.recycleRect.set(target.getX() - this.areaOfEffect,
									target.getY() - this.areaOfEffect, this.areaOfEffect * 2, this.areaOfEffect * 2),
							new CUnitEnumFunction() {
								@Override
								public boolean call(final CUnit possibleTarget) {
									// 如果可能的目标可以到达目标位置，并且可以被攻击，则添加到受损目标列表中
									if (possibleTarget.canReach(target, CAbilityClusterRockets.this.areaOfEffect)
											&& possibleTarget.canBeTargetedBy(simulation, unit, getTargetsAllowed())) {
										damageTargets.add(possibleTarget);
									}
									return false; // 继续枚举其他单位
								}
							});

			if (currentWave == 1) { // 如果是第一波攻击
				// 对所有受损目标施加眩晕效果
				for (final CUnit damageTarget : damageTargets) {
					damageTarget.add(simulation, new CBuffStun(simulation.getHandleIdAllocator().createId(), buffId,
							getDurationForTarget(damageTarget)));
				}
			} else { // 如果不是第一波攻击
				float damagePerTarget = this.damage; // 计算每个目标的伤害值
				// 如果总伤害超过每波最大伤害，则按比例减少每个目标的伤害
				if ((damagePerTarget * damageTargets.size()) > maximumDamagePerWave) {
					damagePerTarget = maximumDamagePerWave / damageTargets.size();
				}
				final float damagePerTargetBuilding = damagePerTarget * (buildingReduction); // 计算对建筑物的伤害值

				// 对所有受损目标造成伤害
				for (final CUnit damageTarget : damageTargets) {
					float thisTargetDamage;
					if (damageTarget.isBuilding()) { // 如果目标是建筑物
						thisTargetDamage = damagePerTargetBuilding; // 使用对建筑物的伤害值
					} else { // 如果目标是单位
						thisTargetDamage = damagePerTarget; // 使用普通伤害值
					}
					// 对目标造成伤害
					damageTarget.damage(simulation, unit, false, true, CAttackType.SPELLS, CDamageType.FIRE,
							CWeaponSoundTypeJass.WHOKNOWS.name(), thisTargetDamage);
				}
			}
			// 计算下一波攻击的时间
			this.nextWaveTick = gameTurnTick
					+ (int) StrictMath.ceil(damageInterval / WarsmashConstants.SIMULATION_STEP_TIME);
		}

		return gameTurnTick < effectDurationEndTick;
	}

	@Override
	public float getUIAreaOfEffect() {
		return this.areaOfEffect;
	}

}
