package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

// 生命结束时播放爆炸效果，移除该buff时杀死该单位
public class CBuffTimedLife extends CBuffTimed {

    // 是否在效果结束时爆炸
    private final boolean explode;

    /**
     * 构造函数，初始化CBuffTimedLife对象
     *
     * @param handleId 效果的句柄ID
     * @param alias    效果的别名
     * @param duration 效果的持续时间
     * @param explode  效果结束时是否爆炸
     */
    public CBuffTimedLife(final int handleId, final War3ID alias, final float duration, boolean explode) {
        super(handleId, alias, alias, duration);
        // 是否在效果结束时爆炸
        this.explode = explode;
    }

    /**
     * 当效果被添加到单位上时调用
     *
     * @param game 游戏模拟对象
     * @param unit 被添加效果的单位对象
     */
    @Override
    protected void onBuffAdd(final CSimulation game, final CUnit unit) {
        if (this.explode) {
            // 如果效果结束时需要爆炸，则设置单位死亡时爆炸
            unit.setExplodesOnDeath(true);
            // 设置爆炸效果的ID为当前效果的别名
            unit.setExplodesOnDeathBuffId(getAlias());
        }
    }

    /**
     * 当效果从单位上移除时调用
     *
     * @param game 游戏模拟对象
     * @param unit 被移除效果的单位对象
     */
    @Override
    protected void onBuffRemove(final CSimulation game, final CUnit unit) {
        // 效果移除时杀死单位
        unit.kill(game);
    }

    /**
     * 判断该效果是否显示计时条
     *
     * @return 如果显示计时条则返回true，否则返回false
     */
    @Override
    public boolean isTimedLifeBar() {
        return true;
    }
}
