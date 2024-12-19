package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
// 神圣护甲：给单位添加无敌Buff
public class CAbilityDivineShield extends CAbilityNoTargetSpellBase {

    // 是否可以取消激活的神圣护盾
    private boolean canDeactivate;
    // 神圣护盾的buff ID
    private War3ID buffId;

    /**
     * 构造函数，初始化神圣护盾能力
     * @param handleId 能力的句柄ID
     * @param alias 能力的别名
     */
    public CAbilityDivineShield(final int handleId, final War3ID alias) {
        super(handleId, alias);
    }

    /**
     * 获取神圣护盾的基础命令ID
     * @return 神圣护盾的命令ID
     */
    @Override
    public int getBaseOrderId() {
        return OrderIds.divineshield;
    }

    /**
     * 填充神圣护盾的数据，从世界编辑器能力中获取信息
     * @param worldEditorAbility 世界编辑器中的能力对象
     * @param level 能力的等级
     */
    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        this.canDeactivate = worldEditorAbility.getFieldAsBoolean(AbilityFields.DATA_A + level, 0);
        this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
    }

    /**
     * 执行神圣护盾的效果
     * @param simulation 模拟环境
     * @param caster 施法单位
     * @param target 目标单位，神圣护盾无目标，此参数未使用
     * @return 执行效果后是否改变游戏状态，神圣护盾不改变游戏状态，返回false
     */
    @Override
    public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
        // 施法单位添加神圣护盾buff
        caster.add(simulation,
                new CBuffDivineShield(simulation.getHandleIdAllocator().createId(), this.buffId, getDuration()));
        return false;
    }
}

