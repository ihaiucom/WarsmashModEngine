package com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

/**
 * 实现了 CUpgradeEffect 接口，用于处理攻击伤害的升级效果
 */
public class CUpgradeEffectAttackDamage implements CUpgradeEffect {
    // 基础伤害值
    private final int base;
    // 每级伤害增量
    private final int mod;

    /**
     * 构造函数，初始化基础伤害值和每级伤害增量
     *
     * @param base 基础伤害值
     * @param mod  每级伤害增量
     */
    public CUpgradeEffectAttackDamage(final int base, final int mod) {
        this.base = base;
        this.mod = mod;
    }

    /**
     * 将升级效果应用到指定的单位上
     *
     * @param simulation 游戏模拟对象
     * @param unit       要应用升级效果的单位
     * @param level      升级的等级
     */
    @Override
    public void apply(final CSimulation simulation, final CUnit unit, final int level) {
        // 遍历单位的特定攻击列表
        for (final CUnitAttack attack : unit.getUnitSpecificAttacks()) {
            // 设置攻击的基础伤害值，增加升级效果带来的伤害值
            attack.setDamageBase(attack.getDamageBase() + Util.levelValue(base, mod, level - 1));
        }
        // 通知单位攻击属性已改变，可能需要重建选中单位的最小-最大伤害 UI
        unit.notifyAttacksChanged();
    }

    /**
     * 将升级效果从指定的单位上移除
     *
     * @param simulation 游戏模拟对象
     * @param unit       要移除升级效果的单位
     * @param level      升级的等级
     */
    @Override
    public void unapply(final CSimulation simulation, final CUnit unit, final int level) {
        // 遍历单位的特定攻击列表
        for (final CUnitAttack attack : unit.getUnitSpecificAttacks()) {
            // 设置攻击的基础伤害值，减少升级效果带来的伤害值
            attack.setDamageBase(attack.getDamageBase() - Util.levelValue(base, mod, level - 1));
        }
        // 通知单位攻击属性已改变，可能需要重建选中单位的最小-最大伤害 UI
        unit.notifyAttacksChanged();
    }
}
