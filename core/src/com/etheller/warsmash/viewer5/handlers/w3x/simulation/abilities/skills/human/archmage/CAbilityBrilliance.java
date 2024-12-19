package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CAbilityAuraBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffAuraBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
// 辉煌光辉 能加快周围友军单位的魔法值恢复速度。|n|n|cffffcc00等级 1|r -能缓慢地加快周围友军的魔法值恢复速度。|n|cffffcc00等级 2|r -能稍快地加快周围友军的魔法值恢复速度。|n|cffffcc00等级 3|r -能迅速地加快周围友军的魔法值恢复速度。
public class CAbilityBrilliance extends CAbilityAuraBase {
	// 魔法值恢复速度增加
	private float manaRegenerationIncrease;
	// 是否为百分比加成
	private boolean percentBonus;

	public CAbilityBrilliance(final int handleId, final War3ID code, final War3ID alias) {
		super(handleId, code, alias);
	}
	/**
	 * 从游戏编辑器中的技能对象填充光环数据
	 * @param worldEditorAbility 游戏编辑器中的技能对象
	 * @param level 技能等级
	 */
	@Override
	public void populateAuraData(final GameObject worldEditorAbility, final int level) {
		// 魔法值恢复速度增加
		this.manaRegenerationIncrease = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		// 是否为百分比加成
		this.percentBonus = worldEditorAbility.getFieldAsBoolean(AbilityFields.DATA_B + level, 0);
	}
	/**
	 * 创建光环效果
	 * @param handleId 光环句柄ID
	 * @param source 光环来源单位
	 * @param enumUnit 目标单位
	 * @return 创建的CBuffAuraBase对象
	 */
	@Override
	protected CBuffAuraBase createBuff(final int handleId, final CUnit source, final CUnit enumUnit) {
		// 根据是否为百分比加成计算光环效果值，并创建CBuffBrilliance对象
		// !this.percentBonus ? this.manaRegenerationIncrease : 当前防御值 * this.manaRegenerationIncrease
		return new CBuffBrilliance(handleId, getBuffId(), !this.percentBonus ? this.manaRegenerationIncrease
				: (enumUnit.getCurrentDefenseDisplay() * this.manaRegenerationIncrease));
	}
}
