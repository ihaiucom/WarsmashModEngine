package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CGameplayConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.AbstractCAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.GetAbilityByRawcodeVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CAbilityData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;
// 英雄能力的类: 学习技能、英雄经验、等级、升级、三维属性（力量、敏捷、智力）计算等
public class CAbilityHero extends AbstractCAbility {
	private Set<War3ID> skillsAvailable; // 可用技能集合
	private int xp; // 经验值
	private int heroLevel; // 英雄等级
	private int skillPoints = 1; // 技能点数

	private HeroStatValue strength; // 力量值
	private HeroStatValue agility; // 敏捷值
	private HeroStatValue intelligence; // 智力值
	private String properName; // 英雄名称
	private boolean awaitingRevive; // 是否等待复活
	private boolean reviving; // 是否正在复活

	// 构造函数，初始化英雄能力
	public CAbilityHero(final int handleId, final List<War3ID> skillsAvailable) {
		super(handleId, War3ID.fromString("AHer"));
		this.skillsAvailable = new LinkedHashSet<>(skillsAvailable);
	}

	// 当单位被添加到游戏中时触发的事件
	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		// 设置英雄等级为 1
		this.heroLevel = 1;
		// 设置经验值为 0
		this.xp = 0;
		// 获取单位配置
		final CUnitType unitType = unit.getUnitType();
		// 设置初始力量、敏捷、智力值，每级增加的力量、敏捷、智力值
		this.strength = new HeroStatValue(unitType.getStartingStrength(), unitType.getStrengthPerLevel());
		this.agility = new HeroStatValue(unitType.getStartingAgility(), unitType.getAgilityPerLevel());
		this.intelligence = new HeroStatValue(unitType.getStartingIntelligence(), unitType.getIntelligencePerLevel());
		// 计算派生属性
		calculateDerivatedFields(game, unit);

		final int properNamesCount = unitType.getProperNamesCount();
		final int nameIndex = properNamesCount > 0 ? game.getSeededRandom().nextInt(properNamesCount) : 0;

		// 设置英雄名称
		String properName;
		final List<String> heroProperNames = unitType.getHeroProperNames();
		if (heroProperNames.size() > 0) {
			if (nameIndex < heroProperNames.size()) {
				properName = heroProperNames.get(nameIndex);
			}
			else {
				properName = heroProperNames.get(heroProperNames.size() - 1);
			}
		}
		else {
			properName = WarsmashConstants.DEFAULT_STRING;
		}
		this.properName = properName;
	}

	// 当单位从游戏中移除时触发的事件
	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	// 每个游戏周期触发的事件
	@Override
	public void onTick(final CSimulation game, final CUnit unit) {

	}

	// 取消队列中的动作
	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {

	}

	// 在将技能添加到队列之前进行检查
	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		final War3ID orderIdAsRawtype = new War3ID(orderId);
		learnSkill(game, caster, orderIdAsRawtype);
		return false;
	}

	// 学习技能的方法
	private void learnSkill(final CSimulation game, final CUnit caster, final War3ID skillId) {
		// 获取技能类型，如果技能类型存在
		final CAbilityType<?> abilityType = game.getAbilityData().getAbilityType(skillId);
		if (abilityType != null) {
			// 减少技能点数
			this.skillPoints--;
			// 获取施法者已有的技能，如果不存在则返回 null
			final CLevelingAbility existingAbility = caster
					.getAbility(GetAbilityByRawcodeVisitor.getInstance().reset(skillId));
			if (existingAbility == null) {
				// 如果不存在，创建一个新的技能并添加到施法者身上
				final CAbility newAbility = abilityType.createAbility(game.getHandleIdAllocator().createId());
				caster.add(game, newAbility);
			}
			else {
				// 如果存在，提升已有技能的等级
				abilityType.setLevel(game, caster, existingAbility, existingAbility.getLevel() + 1);
			}
		}
		// 如果技能类型不存在，显示错误信息
		else {
			game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(),
					"NOTEXTERN: Ability is not yet programmed, unable to learn!");
		}

	}

	// 选择英雄技能
	public void selectHeroSkill(final CSimulation game, final CUnit caster, final War3ID skillId) {
		// 创建一个布尔能力激活接收器的实例，该实例是单例模式
		final BooleanAbilityActivationReceiver activationReceiver = BooleanAbilityActivationReceiver.INSTANCE;

		// 检查是否可以使用技能，传入游戏状态、施法者、技能ID以及激活接收器
		checkCanUse(game, caster, skillId.getValue(), activationReceiver);

		// 如果激活接收器返回的状态是ok，表示技能可以使用
		if (activationReceiver.isOk()) {
			// 学习技能，传入游戏状态、施法者和技能ID
			learnSkill(game, caster, skillId);
		}

	}

	// 开始使用技能的行为（无目标）
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	// 开始使用技能的行为（有目标点）
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	// 无目标调用技能
	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	// 检查目标是否可以被选中（CWidget）
	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	// 检查目标是否可以被选中（AbilityPointTarget）
	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	// 检查是否可以无目标使用技能, 检测英雄是否拥有该指令的技能
	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		// 将 orderId 转换为 War3ID 类型
		final War3ID orderIdAsRawtype = new War3ID(orderId);
		// 检查技能是否可用
		if (this.skillsAvailable.contains(orderIdAsRawtype)) {
			// 如果技能可用，调用接收器的 targetOk 方法，传入 null 参数
			receiver.targetOk(null);
		}
		// 如果技能不可用
		else {
			// 调用接收器的 orderIdNotAccepted 方法
			receiver.orderIdNotAccepted();
		}

	}

	// 访问者模式
	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	// 检查技能是否可以使用
	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		// 将 orderId 转换为 War3ID 类型
		final War3ID orderIdAsRawtype = new War3ID(orderId);
		// 检查技能是否可用
		if (this.skillsAvailable.contains(orderIdAsRawtype)) {
			// 如果技能可用，检查是否有足够的技能点数
			if (this.skillPoints > 0) {
				// 获取游戏的能力数据
				final CAbilityData abilityData = game.getAbilityData();
				// 获取单位当前技能的等级
				final int priorLevel = unit.getAbilityLevel(orderIdAsRawtype);
				// 获取英雄升级所需的等级
				final int heroRequiredLevel = abilityData.getHeroRequiredLevel(game, orderIdAsRawtype, priorLevel);
				// 获取能力类型
				final CAbilityType<?> abilityType = abilityData.getAbilityType(orderIdAsRawtype);
				// TODO check abilityType.getRequiredLevel() which api doesn't currently offer!!
				// 如果能力类型为空或当前等级小于能力类型的等级数
				if ((abilityType == null) || (priorLevel < abilityType.getLevelCount())) {
					// 如果英雄等级达到要求
					if (this.heroLevel >= heroRequiredLevel) {
						// 调用接收器的 useOk 方法
						receiver.useOk();
					}
					// 如果英雄等级未达到要求
					else {
						// 调用接收器的 missingHeroLevelRequirement 方法
						receiver.missingHeroLevelRequirement(heroRequiredLevel);
					}
				}
				// 如果技能等级已经达到最大值
				else {
					// 调用接收器的 techtreeMaximumReached 方法
					receiver.techtreeMaximumReached();
				}
			}
			// 如果没有足够的技能点数
			else {
				// 调用接收器的 noHeroSkillPointsAvailable 方法
				receiver.noHeroSkillPointsAvailable();
			}
		}
		// 如果技能不可用
		else {
			// 调用接收器的 useOk 方法
			receiver.useOk();
		}

	}

	// 获取技能点数
	public int getSkillPoints() {
		return this.skillPoints;
	}

	// 设置技能点数
	public void setSkillPoints(final int skillPoints) {
		this.skillPoints = skillPoints;
	}

	// 获取经验值
	public int getXp() {
		return this.xp;
	}

	// 设置经验值
	public void setXp(final int xp) {
		this.xp = xp;
	}

	// 获取英雄等级
	public int getHeroLevel() {
		return this.heroLevel;
	}

	// 获取力量值
	public HeroStatValue getStrength() {
		return this.strength;
	}

	// 获取敏捷值
	public HeroStatValue getAgility() {
		return this.agility;
	}

	// 获取智力值
	public HeroStatValue getIntelligence() {
		return this.intelligence;
	}

	// 获取英雄名称
	public String getProperName() {
		return this.properName;
	}

	// 设置等待复活状态
	public void setAwaitingRevive(final boolean awaitingRevive) {
		this.awaitingRevive = awaitingRevive;
	}

	// 检查是否在等待复活
	public boolean isAwaitingRevive() {
		return this.awaitingRevive;
	}

	// 设置复活状态
	public void setReviving(final boolean reviving) {
		this.reviving = reviving;
	}

	// 检查是否正在复活
	public boolean isReviving() {
		return this.reviving;
	}

	// 英雄升级方法
	private void levelUpHero(final CSimulation simulation, final CUnit unit) {
		// 获取游戏玩法常量
		final CGameplayConstants gameplayConstants = simulation.getGameplayConstants();
		// 当英雄等级小于最大英雄等级，并且经验值大于等于升级所需经验值时，循环执行以下操作
		while ((this.heroLevel < gameplayConstants.getMaxHeroLevel())
				&& (this.xp >= gameplayConstants.getNeedHeroXPSum(this.heroLevel))) {
			// 英雄等级加一
			this.heroLevel++;
			// 技能点加一
			this.skillPoints++;
			// 计算衍生字段
			calculateDerivatedFields(simulation, unit);
			// 触发单位升级事件
			simulation.unitGainLevelEvent(unit);
		}

	}

	// 添加经验值
	public void addXp(final CSimulation simulation, final CUnit unit, final int xp) {
		// 增加英雄的经验值，考虑玩家的经验值系数
		this.xp += xp * simulation.getPlayer(unit.getPlayerIndex()).getHandicapXP();

		// 检查英雄是否满足升级条件，如果满足则升级英雄
		levelUpHero(simulation, unit);

		// 通知单位内部英雄属性发生变化
		unit.internalPublishHeroStatsChanged();

	}

	// 设置经验值（只能增加）
	public void setXp(final CSimulation simulation, final CUnit unit, final int xp) {
		// 计算新的经验值，考虑玩家的经验值 handicap
		final int newXpVal = xp * Math.round(simulation.getPlayer(unit.getPlayerIndex()).getHandicapXP());
		// 如果新的经验值大于当前经验值
		if (newXpVal > this.xp) {
			// 增加英雄的经验值
			addXp(simulation, unit, newXpVal - this.xp);
		}

	}

	// 设置英雄等级
	public void setHeroLevel(final CSimulation simulation, final CUnit unit, final int level,
			final boolean showEffect) {
		// 获取游戏玩法常量
		final CGameplayConstants gameplayConstants = simulation.getGameplayConstants();
		// 计算升级到指定等级所需的总经验值
		final int neededTotalXp = gameplayConstants.getNeedHeroXPSum(level - 1);
		// 如果当前英雄的经验值小于升级所需的总经验值
		if (this.xp < neededTotalXp) {
			// 计算需要增加的经验值
			addXp(simulation, unit, (int) Math
					.ceil((neededTotalXp - this.xp) / simulation.getPlayer(unit.getPlayerIndex()).getHandicapXP()));
		}
		// 如果当前英雄的经验值大于等于升级所需的总经验值
		else {
			// 移除多余的经验值（TODO：实现具体逻辑）
			// remove xp TODO
		}

	}

	// 设置基础力量
	public void setStrengthBase(final CSimulation game, final CUnit unit, final int strengthBase) {
		this.strength.setBase(strengthBase);
		calculateDerivatedFields(game, unit);
	}

	// 设置基础敏捷
	public void setAgilityBase(final CSimulation game, final CUnit unit, final int agilityBase) {
		this.agility.setBase(agilityBase);
		calculateDerivatedFields(game, unit);
	}

	// 设置基础智力
	public void setIntelligenceBase(final CSimulation game, final CUnit unit, final int intelligenceBase) {
		this.intelligence.setBase(intelligenceBase);
		calculateDerivatedFields(game, unit);
	}

	// 添加力量加成
	public void addStrengthBonus(final CSimulation game, final CUnit unit, final int strengthBonus) {
		this.strength.setBonus(this.strength.getBonus() + strengthBonus);
		calculateDerivatedFields(game, unit);
	}

	// 添加敏捷加成
	public void addAgilityBonus(final CSimulation game, final CUnit unit, final int agilityBonus) {
		this.agility.setBonus(this.agility.getBonus() + agilityBonus);
		calculateDerivatedFields(game, unit);
	}

	// 添加智力加成
	public void addIntelligenceBonus(final CSimulation game, final CUnit unit, final int intelligenceBonus) {
		this.intelligence.setBonus(this.intelligence.getBonus() + intelligenceBonus);
		calculateDerivatedFields(game, unit);
	}

	// 添加基础力量
	public void addStrengthBase(final CSimulation game, final CUnit unit, final int strengthBonus) {
		this.strength.setBase(this.strength.getBase() + strengthBonus);
		calculateDerivatedFields(game, unit);
	}

	// 添加基础敏捷
	public void addAgilityBase(final CSimulation game, final CUnit unit, final int agilityBonus) {
		this.agility.setBase(this.agility.getBase() + agilityBonus);
		calculateDerivatedFields(game, unit);
	}

	// 添加基础智力
	public void addIntelligenceBase(final CSimulation game, final CUnit unit, final int intelligenceBonus) {
		this.intelligence.setBase(this.intelligence.getBase() + intelligenceBonus);
		calculateDerivatedFields(game, unit);
	}

	// 获取英雄主属性
	private HeroStatValue getStat(final CPrimaryAttribute attribute) {
		switch (attribute) {
		case AGILITY:
			return this.agility;
		case INTELLIGENCE:
			return this.intelligence;
		default:
		case STRENGTH:
			return this.strength;
		}
	}

	// 计算派生属性
	private void calculateDerivatedFields(final CSimulation game, final CUnit unit) {
		// 游戏常量
		final CGameplayConstants gameplayConstants = game.getGameplayConstants();
		// 获取当前属性值：力量、敏捷、智力
		final int prevStrength = this.strength.getCurrent();
		final int prevAgility = this.agility.getCurrent();
		final int prevIntelligence = this.intelligence.getCurrent();
		// 计算等级后的属性值
		this.strength.calculate(this.heroLevel);
		this.agility.calculate(this.heroLevel);
		this.intelligence.calculate(this.heroLevel);
		// #计算等级后的变化值
		// 力量
		final int currentStrength = this.strength.getCurrent();
		// 力量变化值
		final int deltaStrength = currentStrength - prevStrength;
		// 智力
		final int currentIntelligence = this.intelligence.getCurrent();
		// 智力变化值
		final int deltaIntelligence = currentIntelligence - prevIntelligence;
		// 敏捷
		final int currentAgility = this.agility.getCurrent();
		// 基础敏捷
		final int currentAgilityBase = this.agility.getCurrentBase();
		// 额外敏捷 （装备加成）
		final int currentAgilityBonus = this.agility.getBonus();

		// TODO #主属性
		// 主属性
		final HeroStatValue primaryAttributeStat = getStat(unit.getUnitType().getPrimaryAttribute());
		// 主属性基础值
		final int primaryAttributeBase = primaryAttributeStat.getCurrentBase();
		// 主属性加成值
		final int primaryAttributeBonus = primaryAttributeStat.getBonus();
		// 敏捷攻击速度加成 = 敏捷 * 敏捷转攻速系数
		final float agiAttackSpeedBonus = gameplayConstants.getAgiAttackSpeedBonus() * currentAgility;

		// TODO #遍历普攻 设置伤害加成和攻速加成
		for (final CUnitAttack attack : unit.getUnitSpecificAttacks()) {
			// 主属性固定伤害加成 = 主属性基础值 * 力量转伤害系数
			attack.setPrimaryAttributePermanentDamageBonus(
					(int) (primaryAttributeBase * gameplayConstants.getStrAttackBonus()));
			// 主属性临时伤害加成 = 主属性加成值 * 力量转伤害系数
			attack.setPrimaryAttributeTemporaryDamageBonus(
					(int) (primaryAttributeBonus * gameplayConstants.getStrAttackBonus()));
			// 敏捷攻击速度加成
			attack.setAgilityAttackSpeedBonus(agiAttackSpeedBonus);
		}

		// TODO #生命值
		// 生命值增加值 = 力量变化值 + 力量转生命值系数
		final float hitPointIncrease = gameplayConstants.getStrHitPointBonus() * deltaStrength;
		// 生命上限
		final int oldMaximumLife = unit.getMaximumLife();
		// 当前生命值
		final float oldLife = unit.getLife();
		// 新生命上限 = 生命上限 + 生命值增加值
		final int newMaximumLife = Math.round(oldMaximumLife + hitPointIncrease);
		// 新生命值 = 旧生命值 * 新生命上限 / 旧生命上限
		final float newLife = (oldLife * (newMaximumLife)) / oldMaximumLife;
		// 设置生命值上限
		unit.setMaximumLife(newMaximumLife);
		// 设置生命值
		unit.setLife(game, newLife);

		// TODO #法力值
		// 法力值增加值 = 智力变化值 * 智力转法力值系数
		final float manaPointIncrease = gameplayConstants.getIntManaBonus() * deltaIntelligence;
		// 法力上限
		final int oldMaximumMana = unit.getMaximumMana();
		// 当前法力值
		final float oldMana = unit.getMana();
		// 新法力上限 = 法力上限 + 法力值增加值
		final int newMaximumMana = Math.round(oldMaximumMana + manaPointIncrease);
		// 新法力值 = 旧法力值 * 新法力上限 / 旧法力上限
		final float newMana = (oldMana * (newMaximumMana)) / oldMaximumMana;
		// 设置法力值上限
		unit.setMaximumMana(newMaximumMana);
		// 设置法力值
		unit.setMana(newMana);

		// TODO #敏捷防御
		// 敏捷防御加成 = 基础敏捷 * 敏捷转防御系数 + 基础敏捷防御常量
		final int agilityDefenseBonus = Math.round(
				gameplayConstants.getAgiDefenseBase() + (gameplayConstants.getAgiDefenseBonus() * currentAgilityBase));
		// 设置敏捷防御加成
		unit.setAgilityDefensePermanentBonus(agilityDefenseBonus);
		// 设置敏捷防御临时加成 = 敏捷加成 * 敏捷转防御系数
		unit.setAgilityDefenseTemporaryBonus(gameplayConstants.getAgiDefenseBonus() * currentAgilityBonus);

		// TODO #生命值恢复力、法力值恢复力
		// 设置生命值恢复力 = 力量 * 生命恢复系数
		unit.setLifeRegenStrengthBonus(currentStrength * gameplayConstants.getStrRegenBonus());
		// 设置法力值恢复力 = 智力 * 法力恢复系数
		unit.setManaRegenIntelligenceBonus(currentIntelligence * gameplayConstants.getIntRegenBonus());
	}

	// 重新计算所有属性
	public void recalculateAllStats(final CSimulation game, final CUnit unit) {
		final CGameplayConstants gameplayConstants = game.getGameplayConstants();
		this.strength.calculate(this.heroLevel);
		this.agility.calculate(this.heroLevel);
		this.intelligence.calculate(this.heroLevel);
		final int currentStrength = this.strength.getCurrent();
		final int currentIntelligence = this.intelligence.getCurrent();
		final int currentAgility = this.agility.getCurrent();
		final int currentAgilityBase = this.agility.getCurrentBase();
		final int currentAgilityBonus = this.agility.getBonus();

		final HeroStatValue primaryAttributeStat = getStat(unit.getUnitType().getPrimaryAttribute());
		final int primaryAttributeBase = primaryAttributeStat.getCurrentBase();
		final int primaryAttributeBonus = primaryAttributeStat.getBonus();
		final float agiAttackSpeedBonus = gameplayConstants.getAgiAttackSpeedBonus() * currentAgility;
		for (final CUnitAttack attack : unit.getUnitSpecificAttacks()) {
			attack.setPrimaryAttributePermanentDamageBonus(
					(int) (primaryAttributeBase * gameplayConstants.getStrAttackBonus()));
			attack.setPrimaryAttributeTemporaryDamageBonus(
					(int) (primaryAttributeBonus * gameplayConstants.getStrAttackBonus()));
			attack.setAgilityAttackSpeedBonus(agiAttackSpeedBonus);
		}

		final float hitPointIncrease = gameplayConstants.getStrHitPointBonus() * currentStrength;
		final int oldMaximumLife = unit.getMaximumLife();
		final float oldLife = unit.getLife();
		final int newMaximumLife = Math.round(oldMaximumLife + hitPointIncrease);
		final float newLife = (oldLife * (newMaximumLife)) / oldMaximumLife;
		unit.setMaximumLife(newMaximumLife);
		unit.setLife(game, newLife);

		final float manaPointIncrease = gameplayConstants.getIntManaBonus() * currentIntelligence;
		final int oldMaximumMana = unit.getMaximumMana();
		final float oldMana = unit.getMana();
		final int newMaximumMana = Math.round(oldMaximumMana + manaPointIncrease);
		final float newMana = (oldMana * (newMaximumMana)) / oldMaximumMana;
		unit.setMaximumMana(newMaximumMana);
		unit.setMana(newMana);

		final int agilityDefenseBonus = Math.round(
				gameplayConstants.getAgiDefenseBase() + (gameplayConstants.getAgiDefenseBonus() * currentAgilityBase));
		unit.setAgilityDefensePermanentBonus(agilityDefenseBonus);
		unit.setAgilityDefenseTemporaryBonus(gameplayConstants.getAgiDefenseBonus() * currentAgilityBonus);
		unit.setLifeRegenStrengthBonus(currentStrength * gameplayConstants.getStrRegenBonus());
		unit.setManaRegenIntelligenceBonus(currentIntelligence * gameplayConstants.getIntRegenBonus());
	}

	// 英雄属性值类
	public static final class HeroStatValue {
		private final float perLevelFactor; // 每级增加的属性因子
		private int base; // 基础值
		private int bonus; // 加成值
		private int currentBase; // 当前基础值
		private int current; // 当前值

		// 构造函数，初始化基础值和每级因子
		private HeroStatValue(final int base, final float perLevelFactor) {
			this.base = base;
			this.perLevelFactor = perLevelFactor;
		}

		// 计算当前值
		public void calculate(final int level) {
			this.currentBase = this.base + (int) ((level - 1) * this.perLevelFactor);
			this.current = this.currentBase + this.bonus;
		}

		// 设置基础值
		public void setBase(final int base) {
			this.base = base;
		}

		// 设置加成值
		public void setBonus(final int bonus) {
			this.bonus = bonus;
		}

		// 获取基础值
		public int getBase() {
			return this.base;
		}

		// 获取当前基础值
		public int getCurrentBase() {
			return this.currentBase;
		}

		// 获取加成值
		public int getBonus() {
			return this.bonus;
		}

		// 获取当前值
		public int getCurrent() {
			return this.current;
		}

		// 获取属性值的显示文本
		public String getDisplayText() {
			String text = Integer.toString(this.currentBase);
			if (this.bonus != 0) {
				if (this.bonus > 0) {
					text += "|cFF00FF00 +" + this.bonus + "";
				}
				else {
					text += "|cFFFF0000 " + this.bonus + "";
				}
			}
			return text;
		}
	}

	// 获取可用技能集合
	public Set<War3ID> getSkillsAvailable() {
		return this.skillsAvailable;
	}

	// 设置可用技能集合
	public void setSkillsAvailable(final List<War3ID> skillsAvailable) {
		this.skillsAvailable = new LinkedHashSet<>(skillsAvailable);
	}

	// 处理英雄死亡事件
	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

	// 检查该能力是否是永久的
	@Override
	public boolean isPermanent() {
		return true;
	}

	// 检查该能力是否是物理的
	@Override
	public boolean isPhysical() {
		return false;
	}

	// 检查该能力是否是通用的
	@Override
	public boolean isUniversal() {
		return false;
	}

	// 获取能力类别
	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.CORE;
	}
}
