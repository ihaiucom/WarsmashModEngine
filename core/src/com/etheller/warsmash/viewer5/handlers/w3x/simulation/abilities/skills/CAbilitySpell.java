package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CAliasedLevelingAbility;
/**
 * 技能接口：populate方法用于读取技能参数配置。
 */
public interface CAbilitySpell extends CAliasedLevelingAbility {
    /**
     * 根据给定的游戏对象和等级填充能力施法的相关数据。
     *
     * @param worldEditorAbility 代表能力施法的游戏对象
     * @param level 施法的等级
     */
    void populate(final GameObject worldEditorAbility, final int level);
}

