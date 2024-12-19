package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

// 复活
public class CAbilityResurrect extends CAbilityNoTargetSpellBase {
    private float areaOfEffect; // 魔法影响范围
    private int numberOfCorpsesRaised; // 可复活的尸体数量

    // 构造函数
    public CAbilityResurrect(final int handleId, final War3ID alias) {
        super(handleId, alias);
    }

    // 获取基础命令ID
    @Override
    public int getBaseOrderId() {
        return OrderIds.resurrection;
    }

    // 填充数据，从worldEditorAbility中获取复活的尸体数量和魔法影响范围
    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        this.numberOfCorpsesRaised = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_A + level, 0);
        this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
    }

    // 执行魔法效果
    @Override
    public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
        final List<CUnit> unitsToResurrect = new ArrayList<>(numberOfCorpsesRaised); // 存储将要复活的单位
        // 枚举范围内的尸体
        simulation.getWorldCollision().enumCorpsesInRange(caster.getX(), caster.getY(), this.areaOfEffect,
                (enumUnit) -> {
                    if (unitsToResurrect.size() < numberOfCorpsesRaised) { // 如果还未达到复活数量上限
                        if (enumUnit.canBeTargetedBy(simulation, caster, getTargetsAllowed())) { // 判断尸体是否可以被复活
                            unitsToResurrect.add(enumUnit); // 添加到复活列表
                        }
                        return false; // 继续枚举
                    }
                    else {
                        return true; // 达到复活数量上限，停止枚举
                    }
                });
        // 复活所有选中的单位
        for (final CUnit unit : unitsToResurrect) {
            unit.resurrect(simulation); // 复活单位
            simulation.createTemporarySpellEffectOnUnit(unit, getAlias(), CEffectType.TARGET); // 在单位上创建临时魔法效果
        }
        simulation.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER); // 在施法者上创建临时魔法效果
        return false; // 魔法效果执行完毕
    }
}

