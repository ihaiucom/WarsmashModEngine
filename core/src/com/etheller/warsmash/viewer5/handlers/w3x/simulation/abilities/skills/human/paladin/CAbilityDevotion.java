package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CAbilityAuraBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffAuraBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;

// 专注光环 为周围友军提供一定额外的护甲。|n|n|cffffcc00等级 1|r - 增加<AHad,DataA1>点的护甲。|n|cffffcc00等级 2|r - 增加<AHad,DataA2>点的护甲。|n|cffffcc00等级 3|r - 增加<AHad,DataA3>点的护甲。
public class CAbilityDevotion extends CAbilityAuraBase {

    // 定义护甲加成值
    private float armorBonus;
    // 定义是否是百分比加成
    private boolean percentBonus;

    /**
     * 构造函数
     * @param handleId 技能句柄ID
     * @param code 技能代码
     * @param alias 技能别名
     */
    public CAbilityDevotion(final int handleId, final War3ID code, final War3ID alias) {
        super(handleId, code, alias);
    }

    /**
     * 填充光环数据
     * @param worldEditorAbility 世界编辑器中的技能对象
     * @param level 技能等级
     */
    @Override
    public void populateAuraData(final GameObject worldEditorAbility, final int level) {
        // 从技能对象中获取护甲加成值
        this.armorBonus = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
        // 从技能对象中获取是否是百分比加成
        this.percentBonus = worldEditorAbility.getFieldAsBoolean(AbilityFields.DATA_B + level, 0);
    }

    /**
     * 创建光环效果
     * @param handleId 技能句柄ID
     * @param source 技能释放者
     * @param enumUnit 受技能影响的单位
     * @return 返回创建的光环效果对象
     */
    @Override
    protected CBuffAuraBase createBuff(final int handleId, final CUnit source, final CUnit enumUnit) {
        // 根据是否是百分比加成计算最终的护甲加成值，并创建光环效果对象
		// !this.percentBonus ? this.armorBonus : 当前防御值 * this.armorBonus
        return new CBuffDevotion(handleId, getBuffId(),
                !this.percentBonus ? this.armorBonus : (enumUnit.getCurrentDefenseDisplay() * this.armorBonus));
    }
}
