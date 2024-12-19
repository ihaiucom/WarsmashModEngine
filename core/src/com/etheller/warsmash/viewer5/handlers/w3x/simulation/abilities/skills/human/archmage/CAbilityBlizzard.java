package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage;

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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
// 暴风雪 能召唤出若干次冰片攻击，对目标区域内的单位造成一定的伤害。|n|n|cffffcc00等级 1|r -<AHbz,DataA1>次攻击，每次造成<AHbz,DataB1>点的伤害。|n|cffffcc00等级 2|r -<AHbz,DataA2>次攻击，每次造成<AHbz,DataB2>点的伤害。|n|cffffcc00等级 3|r -<AHbz,DataA3>次攻击，每次造成<AHbz,DataB3>点的伤害。
/**
 * CAbilityBlizzard 类表示一个特定的魔法技能，具有多个波次的伤害效果。
 */
public class CAbilityBlizzard extends CAbilityPointTargetSpellBase {
	// 建筑物伤害减免
	private float buildingReduction;
	// 伤害值
	private float damage;
	// 每秒伤害
	private float damagePerSecond;
	// 每波最大伤害
	private float maximumDamagePerWave;
	// 碎片数量
	private int shardCount;
	// 波数
	private int waveCount;
	// 波次延迟
	private float waveDelay;
	// 效果范围
	private float areaOfEffect;
	// 效果ID
	private War3ID effectId;

	// 当前波次
	private int currentWave;
	// 下一波计时
	private int nextWaveTick;
	// 是否为伤害波次
	private boolean waveForDamage = false;
	// 回收矩形区域
	private final Rectangle recycleRect = new Rectangle();


	/**
	 * 构造函数，初始化魔法能力的句柄和别名。
	 *
	 * @param handleId 能力的句柄ID
	 * @param alias    能力的别名
	 */
	public CAbilityBlizzard(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	/**
	 * 根据给定的世界编辑器能力和等级填充数据。
	 */
	public void populateData(final GameObject worldEditorAbility, final int level) {
		// 获取并设置建筑减少值，该值从worldEditorAbility对象中获取，与等级相关
		this.buildingReduction = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_D + level, 0);

		// 获取并设置伤害值，该值从worldEditorAbility对象中获取，与等级相关
		this.damage = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);

		// 获取并设置每秒伤害值，该值从worldEditorAbility对象中获取，与等级相关
		this.damagePerSecond = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_E + level, 0);

		// 获取并设置每波最大伤害值，该值从worldEditorAbility对象中获取，与等级相关
		this.maximumDamagePerWave = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_F + level, 0);

		// 获取并设置碎片数量，该值从worldEditorAbility对象中获取，与等级相关
		this.shardCount = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_C + level, 0);

		// 获取并设置波数，该值从worldEditorAbility对象中获取，与等级相关
		this.waveCount = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_A + level, 0);

		// 获取并设置施法延迟时间
		this.waveDelay = getCastingTime();
		// 通常不使用施法时间字段，将其设置为0
		setCastingTime(0);

		// 获取并设置影响区域大小，该值从worldEditorAbility对象中获取，与等级相关
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);

		// 获取并设置效果ID，通过AbstractCAbilityTypeDefinition类的getEffectId方法从worldEditorAbility对象中获取，与等级相关
		this.effectId = AbstractCAbilityTypeDefinition.getEffectId(worldEditorAbility, level);

	}

	@Override
	/**
	 * 获取此能力的基本订单ID。
	 */
	public int getBaseOrderId() {
		return OrderIds.blizzard;
	}

	@Override
	/**
	 * 执行魔法效果，并初始化当前波次和时间。
	 */
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		// 初始化当前波次为0
		this.currentWave = 0;
		// 设置是否因伤害而改变波次的标志为false
		this.waveForDamage = false;
		// 计算下一波开始的时间点，基于当前游戏刻度加上波次延迟除以模拟步长时间的向上取整值
		this.nextWaveTick = simulation.getGameTurnTick()
			  + (int) StrictMath.ceil(this.waveDelay / WarsmashConstants.SIMULATION_STEP_TIME);
		// 返回true表示初始化成功
		return true;

	}

	@Override
	/**
	 * 每个频道时间 tick 的处理方法，用于处理波次伤害逻辑。
	 */
	public boolean doChannelTick(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		// 检查当前游戏回合是否达到了下一波的触发点
		if (simulation.getGameTurnTick() >= this.nextWaveTick) {
		  // 定义波次延迟时间
		  final float waveDelay;
		  // 如果当前波次是为了造成伤害
		  if (this.waveForDamage) {
			  // 增加当前波次计数
			  this.currentWave++;
			  // 设置波次延迟时间
			  waveDelay = this.waveDelay;
			  // 重置波次为非伤害波次
			  this.waveForDamage = false;
			  // 创建一个列表来存储可被伤害的单位
			  final List<CUnit> damageTargets = new ArrayList<>();
			  // 枚举指定矩形区域内的所有单位
			  simulation.getWorldCollision()
					  .enumUnitsInRect(this.recycleRect.set(target.getX() - this.areaOfEffect,
							  target.getY() - this.areaOfEffect, this.areaOfEffect * 2, this.areaOfEffect * 2),
							  new CUnitEnumFunction() {
								  // 判断单位是否可以作为伤害目标
								  @Override
								  public boolean call(final CUnit possibleTarget) {
									  if (possibleTarget.canReach(target, CAbilityBlizzard.this.areaOfEffect)
											  && possibleTarget.canBeTargetedBy(simulation, caster,
													  getTargetsAllowed())) {
										  damageTargets.add(possibleTarget);
									  }
									  return false;
								  }
							  });
			  // 计算每个目标应受到的伤害值
			  float damagePerTarget = this.damage;
			  // 如果总伤害超过每波最大伤害，则平均分配伤害
			  if ((damagePerTarget * damageTargets.size()) > maximumDamagePerWave) {
				  damagePerTarget = maximumDamagePerWave / damageTargets.size();
			  }
			  // 计算建筑单位应受到的伤害值
			  final float damagePerTargetBuilding = damagePerTarget * (buildingReduction);
			  // 对每个伤害目标造成伤害
			  for (final CUnit damageTarget : damageTargets) {
				  float thisTargetDamage;
				  // 如果目标是建筑，则使用建筑伤害值
				  if (damageTarget.isBuilding()) {
					  thisTargetDamage = damagePerTargetBuilding;
				  }
				  // 否则使用普通伤害值
				  else {
					  thisTargetDamage = damagePerTarget;
				  }
				  // 造成伤害
				  damageTarget.damage(simulation, caster, false, true, CAttackType.SPELLS, CDamageType.COLD,
						  CWeaponSoundTypeJass.WHOKNOWS.name(), thisTargetDamage);
			  }
		  }
		  // 如果当前波次不是为了造成伤害
		  else {
			  // 获取带种子的随机数生成器
			  final Random seededRandom = simulation.getSeededRandom();
			  // 对每个碎片进行处理
			  for (int i = 0; i < this.shardCount; i++) {
				  // 生成随机角度
				  final float randomAngle = seededRandom.nextFloat((float) (StrictMath.PI * 2));
				  // 生成随机距离
				  final float randomDistance = seededRandom.nextFloat() * this.areaOfEffect;
				  // 在指定点生成法术效果
				  simulation.spawnSpellEffectOnPoint(
						  target.getX() + ((float) StrictMath.cos(randomAngle) * randomDistance),
						  target.getY() + ((float) StrictMath.sin(randomAngle) * randomDistance), 0, this.effectId,
						  CEffectType.EFFECT, 0).remove();
				  // 播放音效
				  simulation.unitSoundEffectEvent(caster, this.effectId);
			  }
			  // 设置波次延迟时间
			  waveDelay = 0.80f;
			  // 设置下一波为伤害波次
			  this.waveForDamage = true;
		  }
		  // 计算下一波的触发时间
		  this.nextWaveTick = simulation.getGameTurnTick()
				  + (int) StrictMath.ceil(waveDelay / WarsmashConstants.SIMULATION_STEP_TIME);
		}
		// 返回当前波次是否小于总波次数
		return this.currentWave < this.waveCount;

	}

	@Override
	/**
	 * 获取用户界面显示的作用范围。
	 */
	public float getUIAreaOfEffect() {
		return this.areaOfEffect;
	}

}
