package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

// Buff能力接口
public interface CBuff extends CAliasedLevelingAbility {
    /**
     * 获取单位在游戏中的剩余持续时间。
     *
     * @param game 游戏实例
     * @param unit 单位实例
     * @return 剩余持续时间
     */
    float getDurationRemaining(CSimulation game, CUnit unit);

    /**
     * 获取缓冲效果的最大持续时间。
     *
     * @return 最大持续时间
     */
    float getDurationMax();

    /**
     * 判断该缓冲效果是否具有计时生命条。
     *
     * @return 如果有计时生命条返回true，否则返回false
     */
    boolean isTimedLifeBar();
}
