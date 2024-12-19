package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
// 凤凰火焰 等级数据
public class CAbilityTypePhoenixFireLevelData extends CAbilityTypeLevelData {
    // 初始伤害值
    private final float initialDamage;
    // 每秒伤害值
    private final float damagePerSecond;
    // 影响范围
    private final float areaOfEffect;
    // 冷却时间
    private final float cooldown;
    // 持续时间
    private final float duration;

    /**
     * 构造函数，初始化凤凰火焰等级数据
     *
     * @param targetsAllowed 允许的目标类型集合
     * @param initialDamage  初始伤害值
     * @param damagePerSecond 每秒伤害值
     * @param areaOfEffect    影响范围
     * @param cooldown        冷却时间
     * @param duration        持续时间
     */
    public CAbilityTypePhoenixFireLevelData(EnumSet<CTargetType> targetsAllowed, float initialDamage,
                                           float damagePerSecond, float areaOfEffect, float cooldown, float duration) {
        super(targetsAllowed);
        this.initialDamage = initialDamage;
        this.damagePerSecond = damagePerSecond;
        this.areaOfEffect = areaOfEffect;
        this.cooldown = cooldown;
        this.duration = duration;
    }

    /**
     * 获取初始伤害值
     *
     * @return 初始伤害值
     */
    public float getInitialDamage() {
        return this.initialDamage;
    }

    /**
     * 获取每秒伤害值
     *
     * @return 每秒伤害值
     */
    public float getDamagePerSecond() {
        return this.damagePerSecond;
    }

    /**
     * 获取影响范围
     *
     * @return 影响范围
     */
    public float getAreaOfEffect() {
        return this.areaOfEffect;
    }

    /**
     * 获取冷却时间
     *
     * @return 冷却时间
     */
    public float getCooldown() {
        return this.cooldown;
    }

    /**
     * 获取持续时间
     *
     * @return 持续时间
     */
    public float getDuration() {
        return this.duration;
    }

}

