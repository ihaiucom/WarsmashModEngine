package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimed;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
// 天神下凡 激活该技能能提高山丘之王<AHav,DataA1>点的护甲，<AHav,DataB1>点的生命值，<AHav,DataC1>点的攻击力并使其对魔法免疫。|n持续<AHav,Dur1>秒。
public class CBuffAvatar extends CBuffTimed {
	// 生命值加成
	private final int hitPointBonus;
	// 伤害加成
	private final int damageBonus;
	// 防御加成
	private final float defenseBonus;

	/**
	 * 构造函数，初始化增益效果
	 *
	 * @param handleId      增益句柄ID
	 * @param alias         增益别名
	 * @param duration      持续时间
	 * @param hitPointBonus 生命值加成量
	 * @param damageBonus   伤害加成量
	 * @param defenseBonus  防御加成量
	 */
	public CBuffAvatar(int handleId, War3ID alias, float duration, int hitPointBonus, int damageBonus,
					   float defenseBonus) {
		super(handleId, alias, alias, duration);
		this.hitPointBonus = hitPointBonus;
		this.damageBonus = damageBonus;
		this.defenseBonus = defenseBonus;
	}

	/**
	 * 判断是否显示计时生命条
	 *
	 * @return 返回true表示显示计时生命条
	 */
	@Override
	public boolean isTimedLifeBar() {
		return true;
	}

	/**
	 * 增益添加时的处理逻辑
	 *
	 * @param game 模拟游戏实例
	 * @param unit 受影响的单位
	 */
	@Override
	protected void onBuffAdd(CSimulation game, CUnit unit) {
		unit.addMaxLifeRelative(game, hitPointBonus); // 增加最大生命值
		unit.setTemporaryDefenseBonus(unit.getTemporaryDefenseBonus() + this.defenseBonus); // 增加临时防御加成
		for (CUnitAttack attack : unit.getUnitSpecificAttacks()) { // 遍历单位的攻击方式
			attack.setTemporaryDamageBonus(attack.getTemporaryDamageBonus() + this.damageBonus); // 增加临时伤害加成
		}
		unit.setMagicImmune(true); // 设置魔法免疫
		unit.getUnitAnimationListener().addSecondaryTag(AnimationTokens.SecondaryTag.ALTERNATE); // 添加动画标签
	}

	/**
	 * 增益移除时的处理逻辑
	 *
	 * @param game 模拟游戏实例
	 * @param unit 受影响的单位
	 */
	@Override
	protected void onBuffRemove(CSimulation game, CUnit unit) {
		unit.addMaxLifeRelative(game, -hitPointBonus); // 减少最大生命值
		unit.setTemporaryDefenseBonus(unit.getTemporaryDefenseBonus() - this.defenseBonus); // 减少临时防御加成
		for (CUnitAttack attack : unit.getUnitSpecificAttacks()) { // 遍历单位的攻击方式
			attack.setTemporaryDamageBonus(attack.getTemporaryDamageBonus() - this.damageBonus); // 减少临时伤害加成
		}
		unit.setMagicImmune(false); // 取消魔法免疫
		unit.getUnitAnimationListener().removeSecondaryTag(AnimationTokens.SecondaryTag.ALTERNATE); // 移除动画标签
	}
}
