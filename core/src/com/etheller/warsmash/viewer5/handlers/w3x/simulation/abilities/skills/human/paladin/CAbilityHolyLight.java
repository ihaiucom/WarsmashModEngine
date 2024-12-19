package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

// 神圣之光
// 该类表示一个目标施法的神圣光辉技能，继承自CAbilityTargetSpellBase。
public class CAbilityHolyLight extends CAbilityTargetSpellBase {

	// 技能伤害量
	private float healAmount;

	// 构造函数，初始化技能的句柄ID和别名
	public CAbilityHolyLight(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	// 获取基础命令ID
	@Override
	public int getBaseOrderId() {
		return OrderIds.holybolt;
	}

	// 根据游戏对象和技能等级填充技能数据
	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		healAmount = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
	}

	// 检查目标是否可以施加技能
	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit caster, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		// 访问目标对象，获取目标单位
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		// 如果目标单位存在
		if (targetUnit != null) {
			// 判断目标单位是否为不死族
			final boolean undead = targetUnit.getClassifications().contains(CUnitClassification.UNDEAD);
			// 判断目标单位是否为施法者的盟友
			final boolean ally = targetUnit.isUnitAlly(game.getPlayer(caster.getPlayerIndex()));
			// 如果目标单位既不是盟友也不是不死族
			if (undead != ally) {
				// 如果目标单位是盟友且生命值已满
				if (ally && (targetUnit.getLife() >= targetUnit.getMaximumLife())) {
					// 通知技能施放失败，目标单位已处于满血状态
					receiver.targetCheckFailed(CommandStringErrorKeys.ALREADY_AT_FULL_HEALTH);
				}
				// 如果目标单位不是满血状态
				else {
					// 调用父类的方法继续检查目标是否合法
					super.innerCheckCanTarget(game, caster, orderId, target, receiver);
				}
			}
			// 如果目标单位既是盟友又是不死族，或者既不是盟友也不是不死族
			else {
				// 通知技能施放失败，目标单位不符合要求
				receiver.targetCheckFailed(
						CommandStringErrorKeys.MUST_TARGET_FRIENDLY_LIVING_UNITS_OR_ENEMY_UNDEAD_UNITS);
			}
		}
		// 如果目标单位不存在
		else {
			// 通知技能施放失败，必须以单位为目标
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
		}

	}

	// 执行技能效果
	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT); // 访问目标，获取目标单位
	  if (targetUnit != null) { // 如果目标单位不为空
		  if (targetUnit.getClassifications().contains(CUnitClassification.UNDEAD)) { // 如果目标单位是不死族
			  // 对不死族单位造成一半的治疗量作为伤害
			  targetUnit.damage(simulation, caster, false, true, CAttackType.SPELLS, CDamageType.DIVINE, null, healAmount * 0.5f);
		  }
		  else { // 如果目标单位不是不死族
			  // 治疗目标单位
			  targetUnit.heal(simulation, healAmount);
		  }
		  // 在目标单位上创建一个持续性的法术效果，并立即移除
		  simulation.createPersistentSpellEffectOnUnit(targetUnit, getAlias(), CEffectType.TARGET, 0).remove();
	  }
	  return false; // 返回false，表示技能执行完毕

	}
}
