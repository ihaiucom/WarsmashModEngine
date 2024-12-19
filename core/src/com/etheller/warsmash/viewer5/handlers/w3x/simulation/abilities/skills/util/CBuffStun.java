package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuffType;

// 眩晕状态Buff
public class CBuffStun extends CBuffTimed {
    // 定义一个状态修改增益对象，用于表示眩晕效果
    private StateModBuff stunBuff;

    /**
     * 构造函数，初始化CBuffStun对象
     * @param handleId 增益句柄ID
     * @param alias 增益别名
     * @param duration 增益持续时间
     */
    public CBuffStun(final int handleId, final War3ID alias, final float duration) {
        super(handleId, alias, alias, duration);
        // 初始化眩晕增益对象
        stunBuff = new StateModBuff(StateModBuffType.STUN, 1);
    }

    /**
     * 当单位死亡时的处理逻辑
     * @param game 模拟游戏对象
     * @param cUnit 单位对象
     */
    @Override
    public void onDeath(CSimulation game, CUnit cUnit) {
        super.onDeath(game, cUnit);
    }

    /**
     * 当增益添加到单位时的处理逻辑
     * @param game 模拟游戏对象
     * @param unit 单位对象
     */
    @Override
    protected void onBuffAdd(final CSimulation game, final CUnit unit) {
        // 给单位添加眩晕状态修改增益
        unit.addStateModBuff(stunBuff);
        // 计算单位状态，考虑眩晕增益
        unit.computeUnitState(game, stunBuff.getBuffType());
    }

    /**
     * 当增益从单位移除时的处理逻辑
     * @param game 模拟游戏对象
     * @param unit 单位对象
     */
    @Override
    protected void onBuffRemove(final CSimulation game, final CUnit unit) {
        // 移除单位的眩晕状态修改增益
        unit.removeStateModBuff(stunBuff);
        // 计算单位状态，不再考虑眩晕增益
        unit.computeUnitState(game, stunBuff.getBuffType());
    }

    /**
     * 判断该增益是否显示计时生命条
     * @return 返回false，表示不显示计时生命条
     */
    @Override
    public boolean isTimedLifeBar() {
        return false;
    }

}
