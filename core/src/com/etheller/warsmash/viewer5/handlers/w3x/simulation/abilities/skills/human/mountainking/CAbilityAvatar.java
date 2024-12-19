package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
// 天神下凡 激活该技能能提高山丘之王<AHav,DataA1>点的护甲，<AHav,DataB1>点的生命值，<AHav,DataC1>点的攻击力并使其对魔法免疫。|n持续<AHav,Dur1>秒。
public class CAbilityAvatar extends CAbilityNoTargetSpellBase {
	// 定义avatar增益效果的唯一标识符
	private static final War3ID AVATAR_BUFF = War3ID.fromString("BHav");

	// 定义增益效果的属性：生命值、伤害和防御
	private int hitPointBonus;
	private int damageBonus;
	private float defenseBonus;

	// 构造函数，初始化能力句柄ID和别名
	public CAbilityAvatar(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	// 获取基础订单ID，用于标识这个能力的唯一性
	@Override
	public int getBaseOrderId() {
		return OrderIds.avatar;
	}

	// 从编辑器中填充数据，根据等级设置增益效果的具体数值
	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		hitPointBonus = (int) StrictMath.floor(worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0));
		damageBonus = (int) StrictMath.floor(worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_C + level, 0));
		defenseBonus = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		// 设置施法时的主要动画标签
		setCastingPrimaryTag(AnimationTokens.PrimaryTag.MORPH);
	}

	// 执行能力效果，对施法者单位施加增益效果
	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		// 在施法者单位上创建临时的法术效果
		simulation.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);
		// 给施法者单位添加增益效果
		caster.add(simulation, new CBuffAvatar(simulation.getHandleIdAllocator().createId(), AVATAR_BUFF, getDuration(),
				hitPointBonus, damageBonus, defenseBonus));
		// 返回false表示这个能力不需要选择目标
		return false;
	}
}
