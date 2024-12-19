package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
// 攻击速度、移动速度变慢buff
public class CBuffSlow extends CBuffTimed {

    // 攻击速度减少的百分比
    private final float attackSpeedReductionPercent;
    // 移动速度减少的百分比
    private final float moveSpeedReductionPercent;
    // 应用的移动速度减少量
    private int appliedMovementSpeedReduction;

    /**
     * 构造函数，初始化减速效果的参数
     *
     * @param handleId                 效果的唯一标识符
     * @param alias                    效果的别名
     * @param duration                 效果的持续时间
     * @param attackSpeedReductionPercent 攻击速度减少的百分比
     * @param moveSpeedReductionPercent   移动速度减少的百分比
     */
    public CBuffSlow(final int handleId, final War3ID alias, final float duration, final float attackSpeedReductionPercent, final float moveSpeedReductionPercent) {
        super(handleId, alias, alias, duration);
        this.attackSpeedReductionPercent = attackSpeedReductionPercent;
        this.moveSpeedReductionPercent = moveSpeedReductionPercent;
    }

    /**
     * 当效果影响的单位死亡时调用
     *
     * @param game 游戏模拟对象
     * @param cUnit 受影响的单位
     */
    @Override
    public void onDeath(CSimulation game, CUnit cUnit) {
        super.onDeath(game, cUnit);
    }

    /**
     * 当效果被添加到单位上时调用
     *
     * @param game 游戏模拟对象
     * @param unit 受影响的单位
     */
    @Override
    protected void onBuffAdd(final CSimulation game, final CUnit unit) {
        int speed = unit.getSpeed();
        appliedMovementSpeedReduction = (int)StrictMath.floor(moveSpeedReductionPercent * speed);
        unit.setSpeed(speed - appliedMovementSpeedReduction);

        // 减少单位的攻击速度
        for(CUnitAttack attack: unit.getUnitSpecificAttacks()) {
            attack.setAttackSpeedBonus(attack.getAttackSpeedBonus() - attackSpeedReductionPercent);
        }
    }

    /**
     * 当效果从单位上移除时调用
     *
     * @param game 游戏模拟对象
     * @param unit 受影响的单位
     */
    @Override
    protected void onBuffRemove(final CSimulation game, final CUnit unit) {
        // 恢复单位的移动速度
        unit.setSpeed(unit.getSpeed() + appliedMovementSpeedReduction);
        appliedMovementSpeedReduction = 0;

        // 恢复单位的攻击速度
        for(CUnitAttack attack: unit.getUnitSpecificAttacks()) {
            attack.setAttackSpeedBonus(attack.getAttackSpeedBonus() + attackSpeedReductionPercent);
        }
    }

    /**
     * 判断该效果是否显示计时条
     *
     * @return 返回false，表示不显示计时条
     */
    @Override
    public boolean isTimedLifeBar() {
        return false;
    }

}

