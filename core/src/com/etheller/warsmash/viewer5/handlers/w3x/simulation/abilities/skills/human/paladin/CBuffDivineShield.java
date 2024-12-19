package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimed;
// 神圣护甲Buff：给单位设置无敌，持续一段时间
public class CBuffDivineShield extends CBuffTimed {
    /**
     * 构造函数，初始化CBuffDivineShield对象
     * @param handleId 技能句柄ID
     * @param alias 技能别名
     * @param duration 技能持续时间
     */
    public CBuffDivineShield(int handleId, War3ID alias, float duration) {
        super(handleId, alias, alias, duration);
    }

    /**
     * 判断是否显示计时生命条
     * @return 返回false，表示不显示计时生命条
     */
    @Override
    public boolean isTimedLifeBar() {
        return false;
    }

    /**
     * 当增益效果添加到单位时调用
     * @param game 模拟游戏对象
     * @param unit 被添加增益效果的单位
     */
    @Override
    protected void onBuffAdd(CSimulation game, CUnit unit) {
        unit.setInvulnerable(true); // 设置单位为无敌状态
    }

    /**
     * 当增益效果从单位移除时调用
     * @param game 模拟游戏对象
     * @param unit 被移除增益效果的单位
     */
    @Override
    protected void onBuffRemove(CSimulation game, CUnit unit) {
        unit.setInvulnerable(false); // 设置单位为非无敌状态
    }
}

