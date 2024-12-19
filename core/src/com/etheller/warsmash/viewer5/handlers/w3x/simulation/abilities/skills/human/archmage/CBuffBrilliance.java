package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffAuraBase;
// 辉煌光辉Buff, 增加单位法力回复加成
public class CBuffBrilliance extends CBuffAuraBase {
    // 定义一个私有的浮点型变量，用于存储法力回复加成
    private final float manaRegenBonus;

    /**
     * 构造函数，初始化CBuffBrilliance对象
     * @param handleId 技能句柄ID
     * @param alias 技能别名
     * @param manaRegenBonus 法力回复加成值
     */
    public CBuffBrilliance(int handleId, War3ID alias, float manaRegenBonus) {
        super(handleId, alias, alias); // 调用父类构造函数
        this.manaRegenBonus = manaRegenBonus; // 设置法力回复加成值
    }

    /**
     * 当增益效果添加到单位时调用
     * @param game 模拟游戏对象
     * @param unit 被添加增益效果的单位对象
     */
    @Override
    public void onBuffAdd(CSimulation game, CUnit unit) {
        // 增加单位的法力回复加成
        unit.setManaRegenBonus(unit.getManaRegenBonus() + manaRegenBonus);
    }

    /**
     * 当增益效果从单位移除时调用
     * @param game 模拟游戏对象
     * @param unit 被移除增益效果的单位对象
     */
    @Override
    public void onBuffRemove(CSimulation game, CUnit unit) {
        // 移除单位的法力回复加成
        unit.setManaRegenBonus(unit.getManaRegenBonus() - manaRegenBonus);
    }
}
