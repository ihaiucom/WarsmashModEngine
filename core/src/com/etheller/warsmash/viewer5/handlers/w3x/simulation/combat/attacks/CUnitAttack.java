package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackEffectListenerStacking;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPostDamageListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListenerDamageModResult;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListenerPriority;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;

/**
 * 基于单位数据的战斗攻击基类。
 *
 * 原本打算根据武器类型将其拆分为子类，但后来意识到《魔兽争霸III：冰封王座》中的弩车在升级穿刺箭后，
 * 可能同时具有溅射距离效果和区域效果，这表明这些行为并不是互斥的。
 *
 * 经过进一步审查，决定在《魔兽争霸III：冰封王座》中，穿刺箭升级不会与UnitWeapons.slk中的伤害溅射战斗设置交互，
 * 因为许多这些设置并不存在。因此，将尝试尽可能地模拟这些攻击。
 */
public abstract class CUnitAttack {
	// 定义与攻击相关的属性
	// 攻击动画中后摇部分的起始点基准值
	private float animationBackswingPointBase;
	// 这是动画后摇点的实际值，可能会根据某些因素（如攻击速度）进行调整。
	private float animationBackswingPoint;
	// 这是动画伤害点，表示攻击动画中实际造成伤害的时间点。
	private float animationDamagePoint;
	// 攻击的类型
	private CAttackType attackType;
	// 冷却时间的基准值，表示攻击之间的基础冷却时间。
	private final float cooldownTimeBase;
	// 实际的冷却时间，可能会根据某些因素（如攻击速度）进行调整。
	private float cooldownTime;
	// 基础伤害值，表示每次攻击的基础伤害。
	private int damageBase;
	// 伤害骰子数，表示每次攻击中使用的骰子数量。
	private int damageDice;
	// 每个骰子的面数，表示每个骰子可以掷出的最大值。
	private int damageSidesPerDie;
	// 伤害升级量，表示每次升级时增加的伤害量。
	private int damageUpgradeAmount;
	// 攻击范围，表示攻击的有效距离。
	private int range;
	// 范围运动缓冲，表示在攻击范围内允许的额外缓冲距离。
	private float rangeMotionBuffer;
	// 是否显示攻击相关的UI元素。
	private boolean showUI;
	// 允许攻击的目标类型集合
	private EnumSet<CTargetType> targetsAllowed;
	// 武器声音。
	private String weaponSound;
	// 武器的类型
	private CWeaponType weaponType;

	// 该类用于处理攻击速度和伤害加成的属性
	private float agiAttackSpeedBonus; // 角色敏捷值而获得的攻击速度加成。
	private float attackSpeedBonus; // 攻击速度加成，可能是通过其他因素（例如装备、技能等）获得的，而不仅仅是敏捷。
	private int primaryAttributePermanentDamageBonus; // 主属性永久伤害加成
	private int primaryAttributeTemporaryDamageBonus; // 主属性临时伤害加成
	private int permanentDamageBonus; // 永久伤害加成
	private int temporaryDamageBonus; // 临时伤害加成

	private float attackSpeedModifier; // 攻击速度修饰符


	// 定义非堆叠的属性增益
	private Map<String, List<NonStackingStatBuff>> nonStackingFlatBuffs = new HashMap<>(); // 非堆叠的固定增益效果
	private Map<String, List<NonStackingStatBuff>> nonStackingPctBuffs = new HashMap<>(); // 非堆叠的百分比增益效果

	// 计算相关的属性
	private int totalBaseDamage; // 计算出的基础伤害，作为攻击的基础；
	private int totalDamageDice; // 代表额外的随机伤害来源；
	private int minDamageDisplay; // 界面上显示的最小伤害值
	private int maxDamageDisplay; // 界面上显示的最大伤害值
	private int totalTemporaryDamageBonus; // 用于计算和存储临时伤害加成。临时加成可能来源于技能、道具或效果，这些效果在短时间内增加攻击力或伤害。
	private float totalAttackSpeedPercent; // 存储攻击速度的总百分比修饰符。攻击速度影响单位攻击的频率，通常是通过不同的装备、技能和其他因素（如单位的敏捷属性）来调整的。这个修饰符会影响冷却时间和动画时间等。

	/**
	 * 构造函数，初始化CUnitAttack对象。
	 *
	 * @param animationBackswingPoint 动画后摇点
	 * @param animationDamagePoint 动画伤害点
	 * @param attackType 攻击类型
	 * @param cooldownTime 冷却时间
	 * @param damageBase 基础伤害
	 * @param damageDice 伤害骰子数
	 * @param damageSidesPerDie 每个骰子的面数
	 * @param damageUpgradeAmount 伤害升级量
	 * @param range 攻击范围
	 * @param rangeMotionBuffer 范围运动缓冲
	 * @param showUI 是否显示UI
	 * @param targetsAllowed 允许攻击的目标类型集合
	 * @param weaponSound 武器声音
	 * @param weaponType 武器类型
	 */
	public CUnitAttack(final float animationBackswingPoint, final float animationDamagePoint,
			final CAttackType attackType, final float cooldownTime, final int damageBase, final int damageDice,
			final int damageSidesPerDie, final int damageUpgradeAmount, final int range, final float rangeMotionBuffer,
			final boolean showUI, final EnumSet<CTargetType> targetsAllowed, final String weaponSound,
			final CWeaponType weaponType) {
		this.animationBackswingPointBase = animationBackswingPoint;
		this.animationDamagePoint = animationDamagePoint;
		this.attackType = attackType;
		this.cooldownTimeBase = cooldownTime;
		this.damageBase = damageBase;
		this.damageDice = damageDice;
		this.damageSidesPerDie = damageSidesPerDie;
		this.damageUpgradeAmount = damageUpgradeAmount;
		this.range = range;
		this.rangeMotionBuffer = rangeMotionBuffer;
		this.showUI = showUI;
		this.targetsAllowed = targetsAllowed;
		this.weaponSound = weaponSound;
		this.weaponType = weaponType;
		computeDerivedFields();
	}
	/**
	 * 复制构造函数，从另一个CUnitAttack对象复制数据。
	 *
	 * @param other 要复制的CUnitAttack对象
	 */
	public CUnitAttack(final CUnitAttack other) {
		this.animationBackswingPointBase = other.animationBackswingPointBase;
		this.animationDamagePoint = other.animationDamagePoint;
		this.attackType = other.attackType;
		this.cooldownTimeBase = other.cooldownTimeBase;
		this.damageBase = other.damageBase;
		this.damageDice = other.damageDice;
		this.damageSidesPerDie = other.damageSidesPerDie;
		this.damageUpgradeAmount = other.damageUpgradeAmount;
		this.range = other.range;
		this.rangeMotionBuffer = other.rangeMotionBuffer;
		this.showUI = other.showUI;
		this.targetsAllowed = other.targetsAllowed;
		this.weaponSound = other.weaponSound;
		this.weaponType = other.weaponType;

		this.agiAttackSpeedBonus = other.agiAttackSpeedBonus;
		this.attackSpeedBonus = other.attackSpeedBonus;
		this.primaryAttributePermanentDamageBonus = other.primaryAttributePermanentDamageBonus;
		this.primaryAttributeTemporaryDamageBonus = other.primaryAttributeTemporaryDamageBonus;
		this.permanentDamageBonus = other.permanentDamageBonus;
		this.temporaryDamageBonus = other.temporaryDamageBonus;
		computeDerivedFields();
	}
	/**
	 * 抽象方法，复制当前CUnitAttack对象。
	 *
	 * @return 复制的CUnitAttack对象
	 */
	public abstract CUnitAttack copy();
	/**
	 * 计算派生字段，更新相关计算结果。
	 */
	public void computeDerivedFields() {
		// 计算总基础伤害和显示的最小最大伤害
		// 总基础伤害 = 基础伤害值 + 主属性永久伤害加成 + 永久伤害加成
		this.totalBaseDamage = this.damageBase + this.primaryAttributePermanentDamageBonus + this.permanentDamageBonus;
		// 总随机伤害
		this.totalDamageDice = this.damageDice;
		// 界面显示的最小伤害 = 总基础伤害 + 总随机伤害
		this.minDamageDisplay = this.totalBaseDamage + this.totalDamageDice;
		// 界面显示的最小伤害 = 总基础伤害 + 总随机伤害 * 骰子的面数
		this.maxDamageDisplay = this.totalBaseDamage + (this.totalDamageDice * this.damageSidesPerDie);

		// 计算非叠加的攻击增益
		int totalNSAtkBuff = 0;
		for (final String key : this.nonStackingFlatBuffs.keySet()) {
			float buffForKey = 0;
			for (final NonStackingStatBuff buff : this.nonStackingFlatBuffs.get(key)) {
				if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) { // 允许堆叠的效果
					buffForKey += buff.getValue();
				}
				else {
					buffForKey = Math.max(buffForKey, buff.getValue()); // 非堆叠的效果，取最大值
				}
			}
			totalNSAtkBuff += buffForKey;
		}

		// 计算非叠加的百分比攻击增益
		int totalNSAtkPctBuff = 0;
		for (final String key : this.nonStackingPctBuffs.keySet()) {
			Float buffForKey = null;
			for (final NonStackingStatBuff buff : this.nonStackingPctBuffs.get(key)) {
				if (buffForKey == null) {
					buffForKey = buff.getValue();
				}
				else {
					if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) { // 允许堆叠的效果
						buffForKey += buff.getValue();
					}
					else {
						buffForKey = Math.max(buffForKey, buff.getValue()); // 非堆叠的效果，取最大值
					}
				}
			}
			if (buffForKey == null) {
				continue;
			}
			// 百分比加成的计算方式为：基础伤害 * 百分比加成 / 100 + 1，取整。
			// 总基础伤害 * Buff伤害百分比 + (总随机伤害 * (1 + 骰子的面数)) / 2 * Buff伤害百分比
			int otherAtkBonus = (int) (this.totalBaseDamage * buffForKey)
					+ (int) Math.ceil(((this.totalDamageDice * (1 + this.damageSidesPerDie)) / 2) * buffForKey);
			if (otherAtkBonus == 0) {
				otherAtkBonus = (int) (buffForKey / Math.abs(buffForKey));
			}

			if (otherAtkBonus <= 0) {
				otherAtkBonus = Math.max(otherAtkBonus, -1 * this.minDamageDisplay);
			}
			totalNSAtkPctBuff += otherAtkBonus;
		}

		// 确保最小和最大伤害显示不为负数
		if (this.minDamageDisplay < 0) {
			this.minDamageDisplay = 0;
		}
		if (this.maxDamageDisplay < 0) {
			this.maxDamageDisplay = 0;
		}

		// 计算总临时伤害增益和攻击速度相关参数
		// 总临时伤害 = 主属性临时伤害加成 + 临时伤害加成 + 非堆叠的固定加成 + 非堆叠的百分比加成
		this.totalTemporaryDamageBonus = this.primaryAttributeTemporaryDamageBonus + this.temporaryDamageBonus
				+ totalNSAtkBuff + totalNSAtkPctBuff;
		// 总攻击速度加成 = 敏捷值速度加成 + 攻击速度加成 + 攻击速度修改
		final float totalAttackSpeedBonus = this.agiAttackSpeedBonus + this.attackSpeedBonus + this.attackSpeedModifier;
		// 攻击速度百分比 = 1 + 总攻击速度加成
		float totalAttackSpeedPercent = 1.0f + totalAttackSpeedBonus;
		// TODO there might be a gameplay constants value for this instead of 0.0001,
		// didn't look
		if (totalAttackSpeedPercent <= 0.0001f) {
			totalAttackSpeedPercent = 0.0001f;
		}
		// 冷却时间 = 基础冷却时间 / 总攻击速度百分比
		this.cooldownTime = this.cooldownTimeBase / totalAttackSpeedPercent;
		// 总攻击速度百分比
		this.totalAttackSpeedPercent = totalAttackSpeedPercent;
		// 动画后摇点的实际值 = 动画后摇点基准值 / 总攻击速度百分比
		this.animationBackswingPoint = this.animationBackswingPointBase / totalAttackSpeedPercent;

	}

	// 获取动画后摆点
	public float getAnimationBackswingPoint() {
		return this.animationBackswingPoint;
	}

	// 获取动画伤害点
	public float getAnimationDamagePoint() {
		return this.animationDamagePoint;
	}

	// 获取攻击类型
	public CAttackType getAttackType() {
		return this.attackType;
	}

	// 获取冷却时间
	public float getCooldownTime() {
		return this.cooldownTime;
	}

	// 获取基础伤害
	public int getDamageBase() {
		return this.damageBase;
	}

	// 获取伤害骰子数量
	public int getDamageDice() {
		return this.damageDice;
	}

	// 获取每骰子的伤害面数
	public int getDamageSidesPerDie() {
		return this.damageSidesPerDie;
	}

	// 获取伤害升级金额
	public int getDamageUpgradeAmount() {
		return this.damageUpgradeAmount;
	}

	// 获取攻击范围
	public int getRange() {
		return this.range;
	}

	// 获取范围运动缓冲
	public float getRangeMotionBuffer() {
		return this.rangeMotionBuffer;
	}

	// 获取是否显示用户界面
	public boolean isShowUI() {
		return this.showUI;
	}

	// 允许攻击的目标类型集合
	public EnumSet<CTargetType> getTargetsAllowed() {
		return this.targetsAllowed;
	}

	// 获取武器音效
	public String getWeaponSound() {
		return this.weaponSound;
	}

	// 获取武器类型
	public CWeaponType getWeaponType() {
		return this.weaponType;
	}

	// 获取最小伤害显示
	public int getMinDamageDisplay() {
		return this.minDamageDisplay;
	}

	// 获取最大伤害显示
	public int getMaxDamageDisplay() {
		return this.maxDamageDisplay;
	}

	// 设置主属性永久伤害加成
	public void setPrimaryAttributePermanentDamageBonus(final int primaryAttributeDamageBonus) {
		this.primaryAttributePermanentDamageBonus = primaryAttributeDamageBonus;
		computeDerivedFields();
	}

	// 设置主属性临时伤害加成
	public void setPrimaryAttributeTemporaryDamageBonus(final int primaryAttributeDamageBonus) {
		this.primaryAttributeTemporaryDamageBonus = primaryAttributeDamageBonus;
		computeDerivedFields();
	}

	// 设置永久伤害加成
	public void setPermanentDamageBonus(final int permanentDamageBonus) {
		this.permanentDamageBonus = permanentDamageBonus;
		computeDerivedFields();
	}

	// 设置临时伤害加成
	public void setTemporaryDamageBonus(final int temporaryDamageBonus) {
		this.temporaryDamageBonus = temporaryDamageBonus;
		computeDerivedFields();
	}

	// 获取攻击速度修饰符
	public float getAttackSpeedModifier() {
		return this.attackSpeedModifier;
	}

	// 设置攻击速度修饰符
	public void setAttackSpeedModifier(final float attackSpeedModifier) {
		this.attackSpeedModifier = Math.min(attackSpeedModifier, 4);
		computeDerivedFields();
	}

	// 获取非叠加的平坦增益
	public Map<String, List<NonStackingStatBuff>> getNonStackingFlatBuffs() {
		return this.nonStackingFlatBuffs;
	}

	// 设置非叠加的平坦增益
	public void setNonStackingFlatBuffs(final Map<String, List<NonStackingStatBuff>> nonStackingFlatBuffs) {
		this.nonStackingFlatBuffs = nonStackingFlatBuffs;
	}

	// 获取非叠加的百分比增益
	public Map<String, List<NonStackingStatBuff>> getNonStackingPctBuffs() {
		return this.nonStackingPctBuffs;
	}

	// 设置非叠加的百分比增益
	public void setNonStackingPctBuffs(final Map<String, List<NonStackingStatBuff>> nonStackingPctBuffs) {
		this.nonStackingPctBuffs = nonStackingPctBuffs;
	}

	// 设置敏捷攻击速度加成
	public void setAgilityAttackSpeedBonus(final float agiAttackSpeedBonus) {
		this.agiAttackSpeedBonus = agiAttackSpeedBonus;
		computeDerivedFields();
	}

	// 设置攻击速度加成
	public void setAttackSpeedBonus(final float attackSpeedBonus) {
		this.attackSpeedBonus = attackSpeedBonus;
		computeDerivedFields();
	}

	// 获取攻击速度加成
	public float getAttackSpeedBonus() {
		return this.attackSpeedBonus;
	}

	// 获取主属性永久伤害加成
	public int getPrimaryAttributePermanentDamageBonus() {
		return this.primaryAttributePermanentDamageBonus;
	}

	// 获取主属性临时伤害加成
	public int getPrimaryAttributeTemporaryDamageBonus() {
		return this.primaryAttributeTemporaryDamageBonus;
	}

	// 获取永久伤害加成
	public int getPermanentDamageBonus() {
		return this.permanentDamageBonus;
	}

	// 获取临时伤害加成
	public int getTemporaryDamageBonus() {
		return this.temporaryDamageBonus;
	}

	// 获取总伤害骰子数量
	public int getTotalDamageDice() {
		return this.totalDamageDice;
	}

	// 获取总基础伤害
	public int getTotalBaseDamage() {
		return this.totalBaseDamage;
	}

	// 获取总临时伤害加成
	public int getTotalTemporaryDamageBonus() {
		return this.totalTemporaryDamageBonus;
	}

	// 获取总攻击速度百分比
	public float getTotalAttackSpeedPercent() {
		return this.totalAttackSpeedPercent;
	}


	// 抽象方法，启动攻击，接受模拟、单位、目标、伤害值和攻击监听器作为参数
	public abstract void launch(CSimulation simulation, CUnit unit, AbilityTarget target, float damage,
			CUnitAttackListener attackListener);

	// 计算伤害值，返回总伤害
	public int roll(final Random seededRandom) {
		int damage = getTotalBaseDamage(); // 获取基础伤害
		final int dice = getTotalDamageDice(); // 获取骰子数量
		final int sidesPerDie = getDamageSidesPerDie(); // 获取每个骰子面的数量
		for (int i = 0; i < dice; i++) {
			final int singleRoll = sidesPerDie == 0 ? 0 : seededRandom.nextInt(sidesPerDie); // 根据骰子面数进行随机投掷
			damage += singleRoll + 1; // 累加投掷结果
		}
		return damage + getTotalTemporaryDamageBonus(); // 返回最终伤害值
	}

	// onAttack 执行攻击前监听器并计算伤害修改结果
	public CUnitAttackPreDamageListenerDamageModResult runPreDamageListeners(final CSimulation simulation,
			final CUnit attacker, final AbilityTarget target, final float damage) {
		// 创建 攻击前处理结果 结构体
		final CUnitAttackPreDamageListenerDamageModResult result = new CUnitAttackPreDamageListenerDamageModResult(
				damage);
		// 攻击处理器是否运行堆叠
		CUnitAttackEffectListenerStacking allowContinue = new CUnitAttackEffectListenerStacking();

		// onAttack 攻击前处理器优先级排序
		for (final CUnitAttackPreDamageListenerPriority priority : CUnitAttackPreDamageListenerPriority.values()) {
			if (allowContinue.isAllowStacking()) { // 允许堆叠
				// onAttack 攻击前 处理器
				for (final CUnitAttackPreDamageListener listener : attacker
						.getPreDamageListenersForPriority(priority)) {
					if (allowContinue.isAllowSamePriorityStacking()) {
						allowContinue = listener.onAttack(simulation, attacker, target, this.weaponType,
								this.attackType, this.weaponType.getDamageType(), result);
					}
				}
			}
		}
		// 如果丢失
		if (result.isMiss()) {
			if (this.weaponType == CWeaponType.ARTILLERY) { // 火炮
				// 火炮，减少伤害比例
				result.setDamageMultiplier(simulation.getGameplayConstants().getMissDamageReduction());
			}
			else if (this.weaponType == CWeaponType.MSPLASH) { //溅射伤害武器
				// 溅射伤害武器，减少伤害比例
				result.setDamageMultiplier(simulation.getGameplayConstants().getMissDamageReduction());
				// 飘字： miss 丢失
				simulation.spawnTextTag(attacker, attacker.getPlayerIndex(), TextTagConfigType.CRITICAL_STRIKE, "miss");
			}
			else {
				// 其他武器，伤害直接全部为0
				result.setBaseDamage(0);
				result.setBonusDamage(0);
				result.setDamageMultiplier(0);
				// 飘字： miss 丢失
				simulation.spawnTextTag(attacker, attacker.getPlayerIndex(), TextTagConfigType.CRITICAL_STRIKE, "miss"); // TODO
																															// Technically
																															// cheating
																															// here
			}
		}

		// 没有丢失，伤害加成比例不等于1和0
		if (!result.isMiss() && (result.getDamageMultiplier() != 1) && (result.getDamageMultiplier() != 0)) {
			// 飘字：暴击 伤害值
			simulation.spawnTextTag(attacker, attacker.getPlayerIndex(), TextTagConfigType.CRITICAL_STRIKE,
					Math.round(result.computeFinalDamage()));
		}
		//额外伤害不为0
		else if (result.getBonusDamage() != 0) {
			// 飘字 重击 飘字
			simulation.spawnTextTag(attacker, attacker.getPlayerIndex(), TextTagConfigType.BASH,
					Math.round(result.getBonusDamage()));
		}
		return result;
	}

	// 执行攻击后监听器
	public void runPostDamageListeners(final CSimulation simulation, final CUnit attacker, final AbilityTarget target,
			final float actualDamage) {
		// onHit 攻击后处理器
		for (final CUnitAttackPostDamageListener listener : attacker.getPostDamageListeners()) {
			listener.onHit(simulation, attacker, target, actualDamage);

		}
	}

}
