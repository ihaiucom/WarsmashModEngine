package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffSlow;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
// 雷霆一击 重击地面，对周围的地面单位造成伤害并减慢其移动速度和攻击速度。|n|n|cffffcc00等级 1|r - <AHtc,DataA1>点伤害，<AHtc,DataC1,%>%的移动速度，<AHtc,DataD1,%>%的攻击速度。|n|cffffcc00等级 2|r - <AHtc,DataA2>点伤害，<AHtc,DataC2,%>%的移动速度，<AHtc,DataD2,%>%的攻击速度。|n|cffffcc00等级 3|r - <AHtc,DataA3>点伤害，<AHtc,DataC3,%>%的移动速度，<AHtc,DataD3,%>%的攻击速度。
public class CAbilityThunderClap extends CAbilityNoTargetSpellBase {

    // 定义了雷霆一击的伤害值
    private float damage;
    // 定义了技能的影响范围
    private float areaOfEffect;
    // 定义了技能附带的增益效果的ID
    private War3ID buffId;
    // 定义了攻击速度减少的百分比
    private float attackSpeedReductionPercent;
    // 定义了移动速度减少的百分比
    private float movementSpeedReductionPercent;

    /**
     * 构造函数，初始化雷霆一击技能
     * @param handleId 技能的句柄ID
     * @param alias 技能的别名
     */
    public CAbilityThunderClap(final int handleId, final War3ID alias) {
        super(handleId, alias);
    }

    /**
     * 获取技能的基础命令ID
     * @return 技能的命令ID
     */
    @Override
    public int getBaseOrderId() {
        return OrderIds.thunderclap;
    }

    /**
     * 从编辑器中填充技能的数据
     * @param worldEditorAbility 编辑器中的技能对象
     * @param level 技能等级
     */
    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        this.damage = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
        this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
        this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
        this.attackSpeedReductionPercent = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_D + level, 0);
        this.movementSpeedReductionPercent = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_C + level, 0);
    }

    /**
     * 执行技能效果
     * @param simulation 模拟环境
     * @param caster 施法单位
     * @param target 目标单位
     * @return 是否成功执行技能
     */
    @Override
    public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
        // 对范围内的单位施加效果
        simulation.getWorldCollision().enumUnitsInRange(caster.getX(), caster.getY(), areaOfEffect, (enumUnit) -> {
            // 如果单位不是盟友且可以被攻击
            if (!enumUnit.isUnitAlly(simulation.getPlayer(caster.getPlayerIndex()))
                    && enumUnit.canBeTargetedBy(simulation, caster, getTargetsAllowed())) {
                // 添加减速效果
                enumUnit.add(simulation,
                        new CBuffSlow(simulation.getHandleIdAllocator().createId(), CAbilityThunderClap.this.buffId,
                                getDurationForTarget(enumUnit), attackSpeedReductionPercent,
                                movementSpeedReductionPercent));
                // 对单位造成伤害
                enumUnit.damage(simulation, caster, false, true, CAttackType.SPELLS, CDamageType.UNIVERSAL,
                        CWeaponSoundTypeJass.WHOKNOWS.name(), CAbilityThunderClap.this.damage);
            }
            return false;
        });
        // 在施法单位上创建临时的法术效果
        simulation.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);
        return false;
    }
}
