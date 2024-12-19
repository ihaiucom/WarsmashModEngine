package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills;

import java.util.EnumSet;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
/**
 * 技能抽象类: 读取技能配置参数、检测冷却、魔法值是否能使用技能
 */
public abstract class CAbilitySpellBase extends AbstractGenericSingleIconNoSmartActiveAbility implements CAbilitySpell {
	// 魔法值消耗
	private int manaCost;
	// 施法距离
	private float castRange;
	// 冷却时间
	private float cooldown;
	private float castingTime; // 施法时间
	private EnumSet<CTargetType> targetsAllowed; // 允许的目标类型
	private PrimaryTag castingPrimaryTag; // 主要标签
	private EnumSet<SecondaryTag> castingSecondaryTags; // 次要标签集合
	private float duration; // 施法持续时间
	private float heroDuration; // 英雄施法持续时间
	private War3ID code; // 施法代码


	/**
	 * CAbilitySpellBase构造函数，初始化法术能力的句柄ID和别名。
	 * @param handleId 法术能力句柄ID
	 * @param alias 法术能力别名
	 */
	public CAbilitySpellBase(final int handleId, final War3ID alias) {
		super(handleId, alias, alias);
	}

	/**
	 * 填充法术的属性数据。
	 * @param worldEditorAbility 从世界编辑器获取的法术能力对象
	 * @param level 法术能力的等级
	 */
	@Override
	public final void populate(final GameObject worldEditorAbility, final int level) {
		// 当前等级的法力消耗值
		this.manaCost = worldEditorAbility.getFieldAsInteger(AbilityFields.MANA_COST + level, 0);
		// 当前等级的施法距离值
		this.castRange = worldEditorAbility.getFieldAsFloat(AbilityFields.CAST_RANGE + level, 0);
		// 当前等级的冷却时间值
		this.cooldown = worldEditorAbility.getFieldAsFloat(AbilityFields.COOLDOWN + level, 0);
		// 当前等级的施法时间值
		this.castingTime = worldEditorAbility.getFieldAsFloat(AbilityFields.CASTING_TIME + level, 0);
		// 所需等级值
		final int requiredLevel = worldEditorAbility.getFieldAsInteger(AbilityFields.REQUIRED_LEVEL, 0);
		// 解析并设置允许的目标类型集合
		this.targetsAllowed = CTargetType
				.parseTargetTypeSet(worldEditorAbility.getFieldAsList(AbilityFields.TARGETS_ALLOWED + level));

		// 如果所需等级小于6且不是物理或通用类型，则添加非魔法免疫目标类型
		if ((requiredLevel < 6) && !isPhysical() && !isUniversal()) {
			this.targetsAllowed.add(CTargetType.NON_MAGIC_IMMUNE); // 添加非魔法免疫目标类型
		}

		// 如果是物理类型且不是通用类型，则添加非以太目标类型
		if (isPhysical() && !isUniversal()) {
			this.targetsAllowed.add(CTargetType.NON_ETHEREAL); // 添加非以太目标类型
		}

		// 获取动画名称
		final String animNames = worldEditorAbility.getField(AbilityFields.ANIM_NAMES);

		// 初始化主要和次要动画标签集合
		final EnumSet<AnimationTokens.PrimaryTag> primaryTags = EnumSet.noneOf(AnimationTokens.PrimaryTag.class);
		this.castingSecondaryTags = EnumSet.noneOf(AnimationTokens.SecondaryTag.class);

		// 从动画名称中填充主要和次要动画标签
		Sequence.populateTags(primaryTags, this.castingSecondaryTags, animNames);

		// 如果主要动画标签集合为空，则设置为主要标签为null，否则设置为第一个主要标签
		if (primaryTags.isEmpty()) {
			this.castingPrimaryTag = null;
		} else {
			this.castingPrimaryTag = primaryTags.iterator().next();
		}

		// 如果次要动画标签集合为空，则设置为默认的SPELL标签
		if (this.castingSecondaryTags.isEmpty()) {
			this.castingSecondaryTags = SequenceUtils.SPELL;
		}

		// 获取并设置技能持续时间和英雄持续时间
		this.duration = worldEditorAbility.getFieldAsFloat(AbilityFields.DURATION + level, 0);
		this.heroDuration = worldEditorAbility.getFieldAsFloat(AbilityFields.HERO_DURATION + level, 0);

		// 获取并设置技能代码
		this.code = worldEditorAbility.getFieldAsWar3ID(AbilityFields.CODE, -1);

		// 填充其他数据
		populateData(worldEditorAbility, level);

	}

	/**
	 * 获取目标的法术持续时间。
	 * @param target 目标小部件
	 * @return 法术持续时间
	 */
	public float getDurationForTarget(final CWidget target) {
		final CUnit unit = target.visit(AbilityTargetVisitor.UNIT); // 获取目标单位
		return getDurationForTarget(unit); // 返回目标单位的持续时间
	}

	/**
	 * 获取目标单位的法术持续时间。
	 * @param targetUnit 目标单位
	 * @return 法术持续时间
	 */
	public float getDurationForTarget(final CUnit targetUnit) {
		// 如果目标单位不为空且是英雄，则返回英雄持续时间
		if ((targetUnit != null) && targetUnit.isHero()) {
			// 返回英雄持续时间的方法
			return getHeroDuration();
		}
		// 如果目标单位不是英雄或为空，则返回普通持续时间
		return getDuration();

	}

	/**
	 * 获取法术的持续时间。
	 * @return 法术持续时间
	 */
	public float getDuration() {
		return duration;
	}

	/**
	 * 获取英雄的法术持续时间。
	 * @return 英雄法术持续时间
	 */
	public float getHeroDuration() {
		return heroDuration;
	}

	/**
	 * 用于填充法术的具体数据，必须在子类中实现。
	 * @param worldEditorAbility 从世界编辑器获取的法术能力对象
	 * @param level 法术能力的等级
	 */
	public abstract void populateData(GameObject worldEditorAbility, int level);

	/**
	 * 执行法术效果，必须在子类中实现。
	 * @param simulation 当前模拟环境
	 * @param caster 施法单位
	 * @param target 目标
	 * @return 是否成功施放法术
	 */
	public abstract boolean doEffect(CSimulation simulation, CUnit caster, AbilityTarget target);

	/**
	 * 执行持续施法Tick，并返回是否结束持续施法。
	 * @param simulation 当前模拟环境
	 * @param caster 施法单位
	 * @param target 目标
	 * @return 是否继续施法
	 */
	public boolean doChannelTick(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		return false;
	}

	/**
	 * 结束施法。
	 * @param game 当前游戏环境
	 * @param unit 施法单位
	 * @param target 目标
	 * @param interrupted 是否被中断
	 */
	public void doChannelEnd(final CSimulation game, final CUnit unit, final AbilityTarget target,
			final boolean interrupted) {
	}

	@Override
	public boolean isToggleOn() {
		return false;
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	/**
	 * 检查单元是否可以使用法术。
	 * @param game 当前游戏环境
	 * @param unit 施法单位
	 * @param orderId 订单ID
	 * @param receiver 能力激活接收器
	 */
	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		// 检查订单ID是否有效并且是自动施放关闭或开启的订单ID
		if ((orderId != 0) && ((orderId == getAutoCastOffOrderId()) || (orderId == getAutoCastOnOrderId()))) {
			// 如果订单ID有效，调用接收器的useOk方法并返回
			receiver.useOk();
			return;
		}

		// 获取技能剩余冷却时间
		final float cooldownRemaining = getCooldownRemaining(game, unit);
		// 如果冷却时间大于0
		if (cooldownRemaining > 0) {
			// 计算并获取技能的冷却时间显示长度
			final float cooldownLengthDisplay = unit.getCooldownLengthDisplayTicks(game, getCode())
				  * WarsmashConstants.SIMULATION_STEP_TIME;
			// 调用接收器的cooldownNotYetReady方法，告知剩余冷却时间和显示长度
			receiver.cooldownNotYetReady(cooldownRemaining, cooldownLengthDisplay);
		} else if (unit.getMana() < this.manaCost) { // 如果单位的魔法值小于技能的魔法消耗
			// 调用接收器的activationCheckFailed方法，告知魔法值不足
			receiver.activationCheckFailed(CommandStringErrorKeys.NOT_ENOUGH_MANA);
		} else {
			// 如果以上条件都不满足，进行内部检查以确定是否可以使用技能
			innerCheckCanUseSpell(game, unit, orderId, receiver);
		}

	}

	/**
	 * 内部方法，检查施法单位是否可以使用法术。
	 * @param game 当前游戏环境
	 * @param unit 施法单位
	 * @param orderId 订单ID
	 * @param receiver 能力激活接收器
	 */
	protected void innerCheckCanUseSpell(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	// 魔法值消耗
	/**
	 * 获取法术的魔法值消耗。
	 * @return 魔法值消耗
	 */
	public int getManaCost() {
		return this.manaCost;
	}

	@Override
	public int getUIManaCost() {
		return this.manaCost;
	}

	/**
	 * 获取法术的施法距离。
	 * @return 施法距离
	 */
	public float getCastRange() {
		return this.castRange;
	}

	// 冷却时间
	/**
	 * 获取法术的冷却时间。
	 * @return 冷却时间
	 */
	public float getCooldown() {
		return this.cooldown;
	}

	/**
	 * 获取施法时间。
	 * @return 施法时间
	 */
	public float getCastingTime() {
		return this.castingTime;
	}

	/**
	 * 获取允许的目标类型集合。
	 * @return 允许的目标类型集合
	 */
	public EnumSet<CTargetType> getTargetsAllowed() {
		return this.targetsAllowed;
	}

	/**
	 * 获取施法单位的冷却剩余时间。
	 * @param game 当前游戏环境
	 * @param caster 施法单位
	 * @return 冷却剩余时间
	 */
	public float getCooldownRemaining(final CSimulation game, final CUnit caster) {
		return getCooldownRemaining(game, caster, getCode());
	}

	/**
	 * 静态方法，获取施法单位的冷却剩余时间。
	 * @param game 当前游戏环境
	 * @param caster 施法单位
	 * @param code 法术能力代码
	 * @return 冷却剩余时间
	 */
	public static float getCooldownRemaining(final CSimulation game, final CUnit caster, final War3ID code) {
		return caster.getCooldownRemainingTicks(game, code) * WarsmashConstants.SIMULATION_STEP_TIME;
	}

	/**
	 * 设置法术的魔法值消耗。
	 * @param manaCost 魔法值消耗
	 */
	public void setManaCost(final int manaCost) {
		this.manaCost = manaCost;
	}

	/**
	 * 设置法术的施法距离。
	 * @param castRange 施法距离
	 */
	public void setCastRange(final float castRange) {
		this.castRange = castRange;
	}

	/**
	 * 设置法术的冷却时间。
	 * @param cooldown 冷却时间
	 */
	public void setCooldown(final float cooldown) {
		this.cooldown = cooldown;
	}

	/**
	 * 设置法术的施法时间。
	 * @param castingTime 施法时间
	 */
	public void setCastingTime(final float castingTime) {
		this.castingTime = castingTime;
	}

	/**
	 * 设置允许的目标类型集合。
	 * @param targetsAllowed 允许的目标类型集合
	 */
	public void setTargetsAllowed(final EnumSet<CTargetType> targetsAllowed) {
		this.targetsAllowed = targetsAllowed;
	}

	// 动作主标签
	/**
	 * 获取动作的主标签。
	 * @return 动作主标签
	 */
	public PrimaryTag getCastingPrimaryTag() {
		return this.castingPrimaryTag;
	}

	/**
	 * 设置动作的主标签。
	 * @param castingPrimaryTag 动作主标签
	 */
	public void setCastingPrimaryTag(final PrimaryTag castingPrimaryTag) {
		this.castingPrimaryTag = castingPrimaryTag;
	}

	// 动作次标签
	/**
	 * 获取动作的次标签集合。
	 * @return 动作次标签集合
	 */
	public EnumSet<SecondaryTag> getCastingSecondaryTags() {
		return this.castingSecondaryTags;
	}

	/**
	 * 设置动作的次标签集合。
	 * @param castingSecondaryTags 动作次标签集合
	 */
	public void setCastingSecondaryTags(final EnumSet<SecondaryTag> castingSecondaryTags) {
		this.castingSecondaryTags = castingSecondaryTags;
	}

	/**
	  * 获取能力的唯一标识符。
	  *
	  * @return 能力的War3ID
	  */
	 @Override
	 public War3ID getCode() {
		 return code;
	 }

	 /**
	  * 判断能力是否为通用能力。
	  *
	  * @return 如果是通用能力返回true，否则返回false
	  */
	 @Override
	 public boolean isUniversal() {
		 return false;
	 }

	 /**
	  * 判断能力是否为物理能力。
	  *
	  * @return 如果是物理能力返回true，否则返回false
	  */
	 @Override
	 public boolean isPhysical() {
		 return false;
	 }

	 /**
	  * 获取能力的类别。
	  *
	  * @return 能力的类别，此处为SPELL
	  */
	 @Override
	 public CAbilityCategory getAbilityCategory() {
		 return CAbilityCategory.SPELL;
	 }

}
