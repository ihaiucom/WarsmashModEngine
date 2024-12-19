package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills;

import java.util.EnumSet;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbilityGenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
// 抽象技能基类--被动技能
public abstract class CAbilityPassiveSpellBase extends AbilityGenericSingleIconPassiveAbility implements CAbilitySpell {
    // 定义了被动技能的基本属性
    private float castRange; // 施法范围
    private float areaOfEffect; // 效果范围
    private EnumSet<CTargetType> targetsAllowed; // 允许的目标类型
    private float duration; // 持续时间
    private float heroDuration; // 英雄持续时间
    private War3ID code; // 技能代码

    // 构造函数，初始化技能的基本信息
    public CAbilityPassiveSpellBase(final int handleId, final War3ID code, final War3ID alias) {
        super(code, alias, handleId);
    }

    /**
     * 从游戏编辑器中填充技能的数据
     * @param worldEditorAbility 游戏编辑器中的技能对象
     * @param level 技能等级
     */
    @Override
    public final void populate(final GameObject worldEditorAbility, final int level) {
    	// 设置技能的施法范围，从worldEditorAbility中获取对应等级的施法范围值，如果获取失败则默认为0
        this.castRange = worldEditorAbility.getFieldAsFloat(AbilityFields.CAST_RANGE + level, 0);

        // 设置技能的影响范围，从worldEditorAbility中获取对应等级的影响范围值，如果获取失败则默认为0
        this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);

        // 设置技能允许的目标类型，通过AbstractCAbilityTypeDefinition类获取对应等级的目标类型
        this.targetsAllowed = AbstractCAbilityTypeDefinition.getTargetsAllowed(worldEditorAbility, level);

        // 设置技能的持续时间，从worldEditorAbility中获取对应等级的持续时间值，如果获取失败则默认为0
        this.duration = worldEditorAbility.getFieldAsFloat(AbilityFields.DURATION + level, 0);

        // 设置技能对英雄的持续时间，从worldEditorAbility中获取对应等级的英雄持续时间值，如果获取失败则默认为0
        this.heroDuration = worldEditorAbility.getFieldAsFloat(AbilityFields.HERO_DURATION + level, 0);

        // 设置技能的代码标识，从worldEditorAbility中获取技能代码，如果获取失败则默认为-1
        this.code = worldEditorAbility.getFieldAsWar3ID(AbilityFields.CODE, -1);


        populateData(worldEditorAbility, level); // 填充其他特定数据
    }

    /**
     * 获取目标对象的持续时间
     * @param target 目标对象
     * @return 持续时间
     */
    public float getDurationForTarget(final CWidget target) {
        final CUnit unit = target.visit(AbilityTargetVisitor.UNIT);
        return getDurationForTarget(unit);
    }

    /**
     * 获取单位目标的持续时间
     * @param targetUnit 单位目标
     * @return 持续时间
     */
    public float getDurationForTarget(final CUnit targetUnit) {
        if ((targetUnit != null) && targetUnit.isHero()) {
            return getHeroDuration();
        }
        return getDuration();
    }

    // 获取普通持续时间
    public float getDuration() {
        return duration;
    }

    // 获取英雄持续时间
    public float getHeroDuration() {
        return heroDuration;
    }

    // 抽象方法，由子类实现以填充特定数据
    public abstract void populateData(GameObject worldEditorAbility, int level);

    // 获取施法范围
    public float getCastRange() {
        return this.castRange;
    }

    // 获取效果范围
    public float getAreaOfEffect() {
        return areaOfEffect;
    }

    // 获取允许的目标类型
    public EnumSet<CTargetType> getTargetsAllowed() {
        return this.targetsAllowed;
    }

    // 设置施法范围
    public void setCastRange(final float castRange) {
        this.castRange = castRange;
    }

    // 设置效果范围
    public void setAreaOfEffect(final float areaOfEffect) {
        this.areaOfEffect = areaOfEffect;
    }

    // 设置允许的目标类型
    public void setTargetsAllowed(final EnumSet<CTargetType> targetsAllowed) {
        this.targetsAllowed = targetsAllowed;
    }

    // 设置持续时间
    public void setDuration(final float duration) {
        this.duration = duration;
    }

    // 设置英雄持续时间
    public void setHeroDuration(final float heroDuration) {
        this.heroDuration = heroDuration;
    }

    // 获取技能代码
    public War3ID getCode() {
        return code;
    }
}
