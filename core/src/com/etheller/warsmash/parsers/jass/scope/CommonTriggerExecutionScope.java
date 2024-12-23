package com.etheller.warsmash.parsers.jass.scope;

import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidgetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.jass.CAbilityTypeJassDefinition.JassOrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.jass.CAbilityTypeJassDefinition.JassOrderButtonType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.region.CRegion;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.CScriptDialog;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.CScriptDialogButton;
/**
 * CommonTriggerExecutionScope类用于处理触发器的执行上下文，扩展了TriggerExecutionScope。
 */
public class CommonTriggerExecutionScope extends TriggerExecutionScope {
	// 触发该作用域的单元
	private CUnit triggeringUnit;
	// 过滤用的单元
	private CUnit filterUnit;
	// 枚举用的单元
	private CUnit enumUnit;
	// 过滤用的可破坏物
	private CDestructable filterDestructable;
	// 枚举用的可破坏物
	private CDestructable enumDestructable;
	// 过滤用的物品
	private CItem filterItem;
	// 枚举用的物品
	private CItem enumItem;
	// 过滤用的玩家
	private CPlayerJass filterPlayer;
	// 枚举用的玩家
	private CPlayerJass enumPlayer;
	// 即将过期的计时器
	private CTimerJass expiringTimer;
	// 进入区域的单元
	private CUnit enteringUnit;
	// 离开区域的单元
	private CUnit leavingUnit;
	// 触发该作用域的区域
	private CRegion triggeringRegion;
	// 触发该作用域的玩家
	private CPlayerJass triggeringPlayer;
	// 升级的单元
	private CUnit levelingUnit;
	// 学习技能的单元
	private CUnit learningUnit;
	// 已学习的技能ID
	private int learnedSkill;
	// 已学习的技能等级
	private int learnedSkillLevel;
	// 可复活的单元
	private CUnit revivableUnit;
	// 正在复活的单元
	private CUnit revivingUnit;
	// 攻击者单元
	private CUnit attacker;
	// 救援者单元
	private CUnit rescuer;
	// 即将死亡的单元
	private CUnit dyingUnit;
	// 击杀者单元
	private CUnit killingUnit;
	// 腐烂中的单元
	private CUnit decayingUnit;
	// 正在建造结构的单元
	private CUnit constructingStructure;
	// 被取消建造的结构
	private CUnit cancelledStructure;
	// 已建造的结构
	private CUnit constructedStructure;
	// 正在研究的单元
	private CUnit researchingUnit;
	// 已研究的项目ID
	private int researched;
	// 被训练的单元类型
	private int trainedUnitType;
	// 被训练的单元
	private CUnit trainedUnit;
	// 被探测到的单元
	private CUnit detectedUnit;
	// 召唤中的单元
	private CUnit summoningUnit;
	// 被召唤的单元
	private CUnit summonedUnit;
	// 运输单元
	private CUnit transportUnit;
	// 被装载的单元
	private CUnit loadedUnit;
	// 正在出售的单元
	private CUnit sellingUnit;
	// 已出售的单元
	private CUnit soldUnit;
	// 正在购买的单元
	private CUnit buyingUnit;
	// 已出售的物品
	private CUnit soldItem;
	// 正在改变所有权的单元
	private CUnit changingUnit;
	// 改变所有权前的玩家
	private CPlayerJass changingUnitPrevOwner;
	// 操纵物品的单元
	private CUnit manipulatingUnit;
	// 被操纵的物品
	private CItem manipulatedItem;
	// 接受命令的单元
	private CUnit orderedUnit;
	// 发出的命令ID
	private int issuedOrderId;
	// 命令点X坐标
	private float orderPointX;
	// 命令点Y坐标
	private float orderPointY;
	// 命令目标小部件
	private CWidget orderTarget;
	// 命令目标可破坏物
	private CDestructable orderTargetDestructable;
	// 命令目标物品
	private CItem orderTargetItem;
	// 命令目标单元
	private CUnit orderTargetUnit;
	// 触发小部件
	private CWidget triggerWidget;
	// 被点击的对话框
	private CScriptDialog clickedDialog;
	// 被点击的对话框按钮
	private CScriptDialogButton clickedButton;
	// 施放的技能
	private CAbility spellAbility;
	// 施放技能的单元
	private CUnit spellAbilityUnit;
	// 技能目标单元
	private CUnit spellTargetUnit;
	// 技能目标可破坏物
	private CDestructable spellTargetDestructable;
	// 技能目标物品
	private CItem spellTargetItem;
	// 技能目标点
	private AbilityPointTarget spellTargetPoint;
	// 技能ID
	private War3ID spellAbilityId;
	// 技能命令ID（仅限Warsmash）
	private int spellAbilityOrderId;
	// 技能目标类型（仅限Warsmash）
	private JassOrderButtonType spellAbilityTargetType;
	// 技能命令卡（仅限Warsmash）
	private JassOrder spellAbilityOrderCommandCard;

	// 触发事件ID
	private JassGameEventsWar3 triggerEventId;


	/**
     * 构造函数，初始化触发器执行上下文
     * @param triggeringTrigger 触发器对象
     */
	public CommonTriggerExecutionScope(final Trigger triggeringTrigger) {
		super(triggeringTrigger);
	}

	/**
     * 构造函数，继承父上下文
     * @param triggeringTrigger 触发器对象
     * @param parentScope 父上下文
     */
	public CommonTriggerExecutionScope(final Trigger triggeringTrigger, final TriggerExecutionScope parentScope) {
		super(triggeringTrigger);
		if (parentScope instanceof CommonTriggerExecutionScope) {
			copyFrom((CommonTriggerExecutionScope) parentScope);
		}
	}

	/**
     * 构造函数，用于从父范围复制数据
     * @param parentScope 父上下文
     */
	public CommonTriggerExecutionScope(final CommonTriggerExecutionScope parentScope) {
		super(parentScope.getTriggeringTrigger());
		copyFrom(parentScope);
	}

	/**
     * 从父范围复制数据
     * @param parentScope 父上下文
     */
	private void copyFrom(final CommonTriggerExecutionScope parentScope) {
		this.triggeringUnit = parentScope.triggeringUnit;
		this.filterUnit = parentScope.filterUnit;
		this.enumUnit = parentScope.enumUnit;
		this.filterDestructable = parentScope.filterDestructable;
		this.enumDestructable = parentScope.enumDestructable;
		this.filterItem = parentScope.filterItem;
		this.enumItem = parentScope.enumItem;
		this.filterPlayer = parentScope.filterPlayer;
		this.enumPlayer = parentScope.enumPlayer;
		this.expiringTimer = parentScope.expiringTimer;
		this.enteringUnit = parentScope.enteringUnit;
		this.leavingUnit = parentScope.leavingUnit;
		this.triggeringRegion = parentScope.triggeringRegion;
		this.triggeringPlayer = parentScope.triggeringPlayer;
		this.levelingUnit = parentScope.levelingUnit;
		this.learningUnit = parentScope.learningUnit;
		this.learnedSkill = parentScope.learnedSkill;
		this.learnedSkillLevel = parentScope.learnedSkillLevel;
		this.revivableUnit = parentScope.revivableUnit;
		this.attacker = parentScope.attacker;
		this.rescuer = parentScope.rescuer;
		this.dyingUnit = parentScope.dyingUnit;
		this.killingUnit = parentScope.killingUnit;
		this.decayingUnit = parentScope.decayingUnit;
		this.constructingStructure = parentScope.constructingStructure;
		this.cancelledStructure = parentScope.cancelledStructure;
		this.constructedStructure = parentScope.constructedStructure;
		this.researchingUnit = parentScope.researchingUnit;
		this.researched = parentScope.researched;
		this.trainedUnitType = parentScope.trainedUnitType;
		this.trainedUnit = parentScope.trainedUnit;
		this.detectedUnit = parentScope.detectedUnit;
		this.summoningUnit = parentScope.summoningUnit;
		this.summonedUnit = parentScope.summonedUnit;
		this.transportUnit = parentScope.transportUnit;
		this.loadedUnit = parentScope.loadedUnit;
		this.sellingUnit = parentScope.sellingUnit;
		this.soldUnit = parentScope.soldUnit;
		this.buyingUnit = parentScope.buyingUnit;
		this.soldItem = parentScope.soldItem;
		this.changingUnit = parentScope.changingUnit;
		this.changingUnitPrevOwner = parentScope.changingUnitPrevOwner;
		this.manipulatingUnit = parentScope.manipulatingUnit;
		this.manipulatedItem = parentScope.manipulatedItem;
		this.orderedUnit = parentScope.orderedUnit;
		this.issuedOrderId = parentScope.issuedOrderId;
		this.orderPointX = parentScope.orderPointX;
		this.orderPointY = parentScope.orderPointY;
		this.orderTarget = parentScope.orderTarget;
		this.orderTargetDestructable = parentScope.orderTargetDestructable;
		this.orderTargetItem = parentScope.orderTargetItem;
		this.orderTargetUnit = parentScope.orderTargetUnit;
		this.triggerWidget = parentScope.triggerWidget;
		this.clickedDialog = parentScope.clickedDialog;
		this.clickedButton = parentScope.clickedButton;
		this.triggerEventId = parentScope.triggerEventId;
	}

	/**
     * 获取枚举单位
     * @return 当前枚举单位
     */
	public CUnit getEnumUnit() {
		return this.enumUnit;
	}

	/**
     * 获取触发单位
     * @return 当前触发单位
     */
	public CUnit getTriggeringUnit() {
		return this.triggeringUnit;
	}

	/**
     * 获取触发小部件
     * @return 当前触发的小部件
     */
	public CWidget getTriggerWidget() {
		return this.triggerWidget;
	}

	/**
     * 获取过滤单位
     * @return 当前过滤单位
     */
	public CUnit getFilterUnit() {
		return this.filterUnit;
	}

	/**
     * 获取过滤的可破坏物体
     * @return 当前过滤的可破坏物体
     */
	public CDestructable getFilterDestructable() {
		return this.filterDestructable;
	}

	/**
     * 获取枚举的可破坏物体
     * @return 当前枚举的可破坏物体
     */
	public CDestructable getEnumDestructable() {
		return this.enumDestructable;
	}

	/**
     * 获取过滤物品
     * @return 当前过滤物品
     */
	public CItem getFilterItem() {
		return this.filterItem;
	}

	/**
     * 获取枚举物品
     * @return 当前枚举物品
     */
	public CItem getEnumItem() {
		return this.enumItem;
	}

	/**
     * 获取过滤玩家
     * @return 当前过滤玩家
     */
	public CPlayerJass getFilterPlayer() {
		return this.filterPlayer;
	}

	/**
     * 获取枚举玩家
     * @return 当前枚举玩家
     */
	public CPlayerJass getEnumPlayer() {
		return this.enumPlayer;
	}

	/**
     * 获取过期计时器
     * @return 当前过期计时器
     */
	public CTimerJass getExpiringTimer() {
		return this.expiringTimer;
	}

	/**
     * 获取进入单位
     * @return 当前进入单位
     */
	public CUnit getEnteringUnit() {
		return this.enteringUnit;
	}

	/**
     * 获取离开单位
     * @return 当前离开单位
     */
	public CUnit getLeavingUnit() {
		return this.leavingUnit;
	}

	/**
     * 获取触发区域
     * @return 当前触发区域
     */
	public CRegion getTriggeringRegion() {
		return this.triggeringRegion;
	}

	/**
     * 获取触发玩家
     * @return 当前触发玩家
     */
	public CPlayerJass getTriggeringPlayer() {
		return this.triggeringPlayer;
	}

	/**
     * 获取升级单位
     * @return 当前升级单位
     */
	public CUnit getLevelingUnit() {
		return this.levelingUnit;
	}

	/**
     * 获取学习单位
     * @return 当前学习单位
     */
	public CUnit getLearningUnit() {
		return this.learningUnit;
	}

	/**
     * 获取学到的技能
     * @return 当前学到的技能
     */
	public int getLearnedSkill() {
		return this.learnedSkill;
	}

	/**
     * 获取学到的技能等级
     * @return 当前学到的技能等级
     */
	public int getLearnedSkillLevel() {
		return this.learnedSkillLevel;
	}

	/**
     * 获取可复活的单位
     * @return 当前可复活的单位
     */
	public CUnit getRevivableUnit() {
		return this.revivableUnit;
	}

	/**
     * 获取复活单位
     * @return 当前复活单位
     */
	public CUnit getRevivingUnit() {
		return this.revivingUnit;
	}

	/**
     * 获取攻击者
     * @return 当前攻击者
     */
	public CUnit getAttacker() {
		return this.attacker;
	}

	/**
     * 获取救援者
     * @return 当前救援者
     */
	public CUnit getRescuer() {
		return this.rescuer;
	}

	/**
     * 获取正在死亡的单位
     * @return 当前正在死亡的单位
     */
	public CUnit getDyingUnit() {
		return this.dyingUnit;
	}

	/**
     * 获取杀死单位
     * @return 当前杀死的单位
     */
	public CUnit getKillingUnit() {
		return this.killingUnit;
	}

	/**
     * 获取正在腐烂单位
     * @return 当前正在腐烂的单位
     */
	public CUnit getDecayingUnit() {
		return this.decayingUnit;
	}

	/**
     * 获取正在构造结构
     * @return 当前正在构造的结构
     */
	public CUnit getConstructingStructure() {
		return this.constructingStructure;
	}

	/**
     * 获取已取消的结构
     * @return 当前已取消的结构
     */
	public CUnit getCancelledStructure() {
		return this.cancelledStructure;
	}

	/**
     * 获取已构造的结构
     * @return 当前已构造的结构
     */
	public CUnit getConstructedStructure() {
		return this.constructedStructure;
	}

	/**
     * 获取正在研究单位
     * @return 当前正在研究的单位
     */
	public CUnit getResearchingUnit() {
		return this.researchingUnit;
	}

	/**
     * 获取已研究标识
     * @return 当前已研究的标识
     */
	public int getResearched() {
		return this.researched;
	}

	/**
     * 获取训练单位类型
     * @return 当前训练的单位类型
     */
	public int getTrainedUnitType() {
		return this.trainedUnitType;
	}

	/**
     * 获取训练单位
     * @return 当前训练的单位
     */
	public CUnit getTrainedUnit() {
		return this.trainedUnit;
	}

	/**
     * 获取检测单位
     * @return 当前检测的单位
     */
	public CUnit getDetectedUnit() {
		return this.detectedUnit;
	}

	/**
     * 获取召唤单位
     * @return 当前召唤单位
     */
	public CUnit getSummoningUnit() {
		return this.summoningUnit;
	}

	/**
     * 获取被召唤单位
     * @return 当前被召唤单位
     */
	public CUnit getSummonedUnit() {
		return this.summonedUnit;
	}

	/**
     * 获取运输单位
     * @return 当前运输单位
     */
	public CUnit getTransportUnit() {
		return this.transportUnit;
	}

	/**
     * 获取加载单位
     * @return 当前加载单位
     */
	public CUnit getLoadedUnit() {
		return this.loadedUnit;
	}

	/**
     * 获取销售单位
     * @return 当前销售单位
     */
	public CUnit getSellingUnit() {
		return this.sellingUnit;
	}

	/**
     * 获取已售单位
     * @return 当前已售单位
     */
	public CUnit getSoldUnit() {
		return this.soldUnit;
	}

	/**
     * 获取购买单位
     * @return 当前购买单位
     */
	public CUnit getBuyingUnit() {
		return this.buyingUnit;
	}

	/**
     * 获取已售物品
     * @return 当前已售物品
     */
	public CUnit getSoldItem() {
		return this.soldItem;
	}

	/**
     * 获取改变单位
     * @return 当前改变的单位
     */
	public CUnit getChangingUnit() {
		return this.changingUnit;
	}

	/**
     * 获取改变单位的前任拥有者
     * @return 当前改变单位的前任拥有者
     */
	public CPlayerJass getChangingUnitPrevOwner() {
		return this.changingUnitPrevOwner;
	}

	/**
     * 获取操控单位
     * @return 当前操控单位
     */
	public CUnit getManipulatingUnit() {
		return this.manipulatingUnit;
	}

	/**
     * 获取被操控物品
     * @return 当前被操控的物品
     */
	public CItem getManipulatedItem() {
		return this.manipulatedItem;
	}

	/**
     * 获取指令单位
     * @return 当前指令单位
     */
	public CUnit getOrderedUnit() {
		return this.orderedUnit;
	}

	/**
     * 获取已发出指令ID
     * @return 当前已发出的指令ID
     */
	public int getIssuedOrderId() {
		return this.issuedOrderId;
	}

	/**
     * 获取订单X坐标
     * @return 当前订单的X坐标
     */
	public float getOrderPointX() {
		return this.orderPointX;
	}

	/**
     * 获取订单Y坐标
     * @return 当前订单的Y坐标
     */
	public float getOrderPointY() {
		return this.orderPointY;
	}

	/**
     * 获取订单目标
     * @return 当前订单的目标
     */
	public CWidget getOrderTarget() {
		return this.orderTarget;
	}

	/**
     * 获取订单目标可破坏物体
     * @return 当前订单目标的可破坏物体
     */
	public CDestructable getOrderTargetDestructable() {
		return this.orderTargetDestructable;
	}

	/**
     * 获取订单目标物品
     * @return 当前订单目标的物品
     */
	public CItem getOrderTargetItem() {
		return this.orderTargetItem;
	}

	/**
     * 获取订单目标单位
     * @return 当前订单目标的单位
     */
	public CUnit getOrderTargetUnit() {
		return this.orderTargetUnit;
	}

	/**
     * 获取点击按钮
     * @return 当前点击的按钮
     */
	public CScriptDialogButton getClickedButton() {
		return this.clickedButton;
	}

	/**
     * 获取点击的对话框
     * @return 当前点击的对话框
     */
	public CScriptDialog getClickedDialog() {
		return this.clickedDialog;
	}

	/**
     * 获取触发事件ID
     * @return 当前触发事件的ID
     */
	public JassGameEventsWar3 getTriggerEventId() {
		return this.triggerEventId;
	}

	/**
     * 获取法术能力单位
     * @return 当前法术能力的单位
     */
	public CUnit getSpellAbilityUnit() {
		return this.spellAbilityUnit;
	}

	/**
     * 获取法术目标单位
     * @return 当前法术的目标单位
     */
	public CUnit getSpellTargetUnit() {
		return this.spellTargetUnit;
	}

	/**
     * 获取法术目标点
     * @return 当前法术的目标点
     */
	public AbilityPointTarget getSpellTargetPoint() {
		return this.spellTargetPoint;
	}

	/**
     * 获取法术能力ID
     * @return 当前法术能力的ID
     */
	public War3ID getSpellAbilityId() {
		return this.spellAbilityId;
	}

	/**
     * 获取法术能力的订单ID
     * @return 当前法术能力的订单ID
     */
	public int getSpellAbilityOrderId() {
		return this.spellAbilityOrderId;
	}

	/**
     * 获取法术能力目标类型
     * @return 当前法术能力的目标类型
     */
	public JassOrderButtonType getSpellAbilityTargetType() {
		return this.spellAbilityTargetType;
	}

	/**
     * 获取法术能力指令卡
     * @return 当前法术能力的指令卡
     */
	public JassOrder getSpellAbilityOrderCommandCard() {
		return this.spellAbilityOrderCommandCard;
	}

	/**
     * 过滤范围方法
     * @param parentScope 父上下文
     * @param filterUnit 过滤单位
     * @return 过滤后的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope filterScope(final TriggerExecutionScope parentScope,
			final CUnit filterUnit) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(parentScope.getTriggeringTrigger(),
				parentScope);
		scope.filterUnit = filterUnit;
		return scope;
	}

	/**
     * 枚举范围方法
     * @param parentScope 父上下文
     * @param enumUnit 枚举单位
     * @return 枚举后的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope enumScope(final TriggerExecutionScope parentScope, final CUnit enumUnit) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(parentScope.getTriggeringTrigger(),
				parentScope);
		scope.enumUnit = enumUnit;
		return scope;
	}

	/**
     * 过滤范围方法
     * @param parentScope 父上下文
     * @param filterItem 过滤物品
     * @return 过滤后的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope filterScope(final TriggerExecutionScope parentScope,
			final CItem filterItem) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(parentScope.getTriggeringTrigger(),
				parentScope);
		scope.filterItem = filterItem;
		return scope;
	}

	/**
     * 枚举范围方法
     * @param parentScope 父上下文
     * @param enumItem 枚举物品
     * @return 枚举后的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope enumScope(final TriggerExecutionScope parentScope, final CItem enumItem) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(parentScope.getTriggeringTrigger(),
				parentScope);
		scope.enumItem = enumItem;
		return scope;
	}

	/**
     * 过滤范围方法
     * @param parentScope 父上下文
     * @param filterDestructable 过滤的可破坏物体
     * @return 过滤后的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope filterScope(final TriggerExecutionScope parentScope,
			final CDestructable filterDestructable) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(parentScope.getTriggeringTrigger(),
				parentScope);
		scope.filterDestructable = filterDestructable;
		return scope;
	}

	/**
     * 枚举范围方法
     * @param parentScope 父上下文
     * @param enumDestructable 枚举的可破坏物体
     * @return 枚举后的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope enumScope(final TriggerExecutionScope parentScope,
			final CDestructable enumDestructable) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(parentScope.getTriggeringTrigger(),
				parentScope);
		scope.enumDestructable = enumDestructable;
		return scope;
	}

	/**
     * 过滤范围方法
     * @param parentScope 父上下文
     * @param filterPlayer 过滤玩家
     * @return 过滤后的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope filterScope(final TriggerExecutionScope parentScope,
			final CPlayerJass filterPlayer) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(parentScope.getTriggeringTrigger(),
				parentScope);
		scope.filterPlayer = filterPlayer;
		return scope;
	}

	/**
     * 枚举范围方法
     * @param parentScope 父上下文
     * @param enumPlayer 枚举玩家
     * @return 枚举后的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope enumScope(final TriggerExecutionScope parentScope,
			final CPlayerJass enumPlayer) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(parentScope.getTriggeringTrigger(),
				parentScope);
		scope.enumPlayer = enumPlayer;
		return scope;
	}

	/**
     * 创建过期计时器范围
     * @param trigger 触发器
     * @param cTimerJass 计时器对象
     * @return 包含计时器的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope expiringTimer(final Trigger trigger, final CTimerJass cTimerJass) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.expiringTimer = cTimerJass;
		return scope;
	}

	/**
     * 创建单位进入区域范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param parentScope 父上下文
     * @param enteringUnit 进入单位
     * @param triggeringRegion 触发区域
     * @return 包含进入单位信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope unitEnterRegionScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final TriggerExecutionScope parentScope, final CUnit enteringUnit,
			final CRegion triggeringRegion) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, parentScope);
		scope.enteringUnit = enteringUnit;
		scope.triggeringUnit = enteringUnit;
		scope.triggeringRegion = triggeringRegion;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	/**
     * 创建单位离开区域范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param parentScope 父上下文
     * @param leavingUnit 离开单位
     * @param triggeringRegion 触发区域
     * @return 包含离开单位信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope unitLeaveRegionScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final TriggerExecutionScope parentScope, final CUnit leavingUnit,
			final CRegion triggeringRegion) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, parentScope);
		scope.leavingUnit = leavingUnit;
		scope.triggeringUnit = leavingUnit;
		scope.triggeringRegion = triggeringRegion;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	/**
     * 创建玩家英雄升级范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param hero 英雄单位
     * @return 包含英雄单位信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope playerHeroLevelScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CUnit hero) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.triggeringUnit = hero;
		scope.levelingUnit = hero;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	/**
     * 创建玩家英雄复活范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param hero 英雄单位
     * @return 包含可复活英雄单位信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope playerHeroRevivableScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CUnit hero) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.triggeringUnit = hero;
		scope.revivableUnit = hero;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	/**
     * 创建单位死亡范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param dyingUnit 死亡单位
     * @param killingUnit 杀死单位
     * @return 包含死亡单位信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope unitDeathScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CUnit dyingUnit, final CUnit killingUnit) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.dyingUnit = dyingUnit;
		scope.triggerWidget = dyingUnit;
		scope.triggeringUnit = dyingUnit;
		scope.killingUnit = killingUnit;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	/**
     * 创建小部件触发范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param triggerWidget 小部件
     * @return 包含小部件信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope widgetTriggerScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CWidget triggerWidget) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.triggerWidget = triggerWidget;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	/**
     * 创建触发对话框范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param clickedDialog 点击的对话框
     * @param clickedButton 点击的按钮
     * @return 包含对话框信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope triggerDialogScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CScriptDialog clickedDialog, final CScriptDialogButton clickedButton) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.clickedDialog = clickedDialog;
		scope.clickedButton = clickedButton;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	/**
     * 创建单位拾取物品范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param orderedUnit 下令单位
     * @param whichItem 拾取的物品
     * @return 包含拾取物品信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope unitPickupItemScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CUnit orderedUnit, final CItem whichItem) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.triggerWidget = orderedUnit;
		scope.triggeringUnit = orderedUnit;
		scope.triggerEventId = triggerEventId;
		scope.manipulatedItem = whichItem;
		scope.manipulatingUnit = orderedUnit;
		return scope;
	}

	/**
     * 创建单位指令范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param orderedUnit 下令单位
     * @param issuedOrderId 发出的指令ID
     * @return 包含指令信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope unitOrderScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CUnit orderedUnit, final int issuedOrderId) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.triggerWidget = orderedUnit;
		scope.triggeringUnit = orderedUnit;
		scope.orderedUnit = orderedUnit;
		scope.triggerEventId = triggerEventId;
		scope.issuedOrderId = issuedOrderId;
		return scope;
	}

	/**
     * 创建单位指令点范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param orderedUnit 下令单位
     * @param issuedOrderId 发出的指令ID
     * @param orderPointX 订单X坐标
     * @param orderPointY 订单Y坐标
     * @return 包含指令点信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope unitOrderPointScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CUnit orderedUnit, final int issuedOrderId, final float orderPointX,
			final float orderPointY) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.triggerWidget = orderedUnit;
		scope.triggeringUnit = orderedUnit;
		scope.orderedUnit = orderedUnit;
		scope.triggerEventId = triggerEventId;
		scope.issuedOrderId = issuedOrderId;
		scope.orderPointX = orderPointX;
		scope.orderPointY = orderPointY;
		return scope;
	}

	/**
     * 创建单位指令目标范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param orderedUnit 下令单位
     * @param issuedOrderId 发出的指令ID
     * @param target 目标小部件
     * @return 包含目标信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope unitOrderTargetScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CUnit orderedUnit, final int issuedOrderId, final CWidget target) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.triggerWidget = orderedUnit;
		scope.triggeringUnit = orderedUnit;
		scope.orderedUnit = orderedUnit;
		scope.triggerEventId = triggerEventId;
		scope.issuedOrderId = issuedOrderId;
		scope.orderTarget = target;
		target.visit(new CWidgetVisitor<Void>() {
			@Override
			public Void accept(final CUnit target) {
				scope.orderTargetUnit = target;
				return null;
			}

			@Override
			public Void accept(final CDestructable target) {
				scope.orderTargetDestructable = target;
				return null;
			}

			@Override
			public Void accept(final CItem target) {
				scope.orderTargetItem = target;
				return null;
			}
		});
		return scope;
	}

	/**
     * 创建单位建造完成范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param constructedStructure 已构造结构
     * @param constructingUnit 正在构造的单位
     * @return 包含结构信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope unitConstructFinishScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CUnit constructedStructure, final CUnit constructingUnit) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.triggerWidget = constructedStructure;
		scope.triggeringUnit = constructedStructure;
		scope.constructedStructure = constructedStructure;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	/**
     * 创建单位训练完成范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param trainingUnit 训练单位
     * @param trainedUnit 训练出的单位
     * @return 包含训练信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope unitTrainFinishScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CUnit trainingUnit, final CUnit trainedUnit) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.triggerWidget = trainingUnit;
		scope.triggeringUnit = trainingUnit;
		scope.trainedUnit = trainedUnit;
		scope.trainedUnitType = trainedUnit.getUnitType().getTypeId().getValue();
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	/**
     * 创建单位研究完成范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param researchingUnit 正在研究的单位
     * @param researched 研究的ID
     * @return 包含研究信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope unitResearchFinishScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CUnit researchingUnit, final War3ID researched) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.triggerWidget = researchingUnit;
		scope.triggeringUnit = researchingUnit;
		scope.researchingUnit = researchingUnit;
		scope.researched = researched.getValue();
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	/**
     * 创建单位法术效果目标范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param spellAbility 法术能力
     * @param spellAbilityUnit 法术能力单位
     * @param targetUnit 目标单位
     * @param spellAbilityId 法术能力ID
     * @return 包含法术目标信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope unitSpellEffectTargetScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CAbility spellAbility, final CUnit spellAbilityUnit, final CUnit targetUnit,
			final War3ID spellAbilityId) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.spellAbility = spellAbility;
		scope.spellAbilityUnit = spellAbilityUnit;
		scope.triggeringUnit = spellAbilityUnit;
		scope.spellTargetUnit = targetUnit;
		scope.spellAbilityId = spellAbilityId;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	/**
     * 创建单位法术效果点范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param spellAbility 法术能力
     * @param spellAbilityUnit 法术能力单位
     * @param targetPoint 目标点
     * @param spellAbilityId 法术能力ID
     * @return 包含法术点信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope unitSpellEffectPointScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CAbility spellAbility, final CUnit spellAbilityUnit,
			final AbilityPointTarget targetPoint, final War3ID spellAbilityId) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.spellAbility = spellAbility;
		scope.spellAbilityUnit = spellAbilityUnit;
		scope.triggeringUnit = spellAbilityUnit;
		scope.spellTargetPoint = targetPoint;
		scope.spellAbilityId = spellAbilityId;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	/**
     * 创建玩家事件触发范围
     * @param triggerEventId 触发事件ID
     * @param trigger 触发器
     * @param player 玩家对象
     * @return 包含玩家信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope guiPlayerEventTriggerScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CPlayerJass player) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.triggeringPlayer = player;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	/**
     * 创建法术目标范围
     * @param spellAbility 法术能力
     * @param spellAbilityUnit 法术能力单位
     * @param targetUnit 目标单位
     * @param spellAbilityId 法术能力ID
     * @param spellAbilityOrderId 法术能力订单ID
     * @param spellAbilityTargetType 法术能力目标类型
     * @param spellAbilityOrderCommandCard 法术能力指令卡
     * @return 包含法术目标信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope jassAbilityTargetScope(final CAbility spellAbility,
			final CUnit spellAbilityUnit, final CUnit targetUnit, final War3ID spellAbilityId,
			final int spellAbilityOrderId, final JassOrderButtonType spellAbilityTargetType,
			final JassOrder spellAbilityOrderCommandCard) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(null, TriggerExecutionScope.EMPTY);
		scope.spellAbility = spellAbility;
		scope.spellAbilityUnit = spellAbilityUnit;
		scope.triggeringUnit = spellAbilityUnit;
		scope.spellTargetUnit = targetUnit;
		scope.spellAbilityId = spellAbilityId;
		scope.spellAbilityOrderId = spellAbilityOrderId;
		scope.spellAbilityTargetType = spellAbilityTargetType;
		return scope;
	}

	/**
     * 创建法术目标物品范围
     * @param spellAbility 法术能力
     * @param spellAbilityUnit 法术能力单位
     * @param targetItem 目标物品
     * @param spellAbilityId 法术能力ID
     * @param spellAbilityOrderId 法术能力订单ID
     * @param spellAbilityTargetType 法术能力目标类型
     * @param spellAbilityOrderCommandCard 法术能力指令卡
     * @return 包含法术目标物品信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope jassAbilityTargetScope(final CAbility spellAbility,
			final CUnit spellAbilityUnit, final CItem targetItem, final War3ID spellAbilityId,
			final int spellAbilityOrderId, final JassOrderButtonType spellAbilityTargetType,
			final JassOrder spellAbilityOrderCommandCard) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(null, TriggerExecutionScope.EMPTY);
		scope.spellAbility = spellAbility;
		scope.spellAbilityUnit = spellAbilityUnit;
		scope.triggeringUnit = spellAbilityUnit;
		scope.spellTargetItem = targetItem;
		scope.spellAbilityId = spellAbilityId;
		scope.spellAbilityOrderId = spellAbilityOrderId;
		scope.spellAbilityTargetType = spellAbilityTargetType;
		return scope;
	}

	/**
     * 创建法术目标可破坏物体范围
     * @param spellAbility 法术能力
     * @param spellAbilityUnit 法术能力单位
     * @param targetDestructable 目标可破坏物体
     * @param spellAbilityId 法术能力ID
     * @param spellAbilityOrderId 法术能力订单ID
     * @param spellAbilityTargetType 法术能力目标类型
     * @param spellAbilityOrderCommandCard 法术能力指令卡
     * @return 包含法术目标可破坏物体信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope jassAbilityTargetScope(final CAbility spellAbility,
			final CUnit spellAbilityUnit, final CDestructable targetDestructable, final War3ID spellAbilityId,
			final int spellAbilityOrderId, final JassOrderButtonType spellAbilityTargetType,
			final JassOrder spellAbilityOrderCommandCard) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(null, TriggerExecutionScope.EMPTY);
		scope.spellAbility = spellAbility;
		scope.spellAbilityUnit = spellAbilityUnit;
		scope.triggeringUnit = spellAbilityUnit;
		scope.spellTargetDestructable = targetDestructable;
		scope.spellAbilityId = spellAbilityId;
		scope.spellAbilityOrderId = spellAbilityOrderId;
		scope.spellAbilityTargetType = spellAbilityTargetType;
		return scope;
	}

	/**
     * 创建法术目标点范围
     * @param spellAbility 法术能力
     * @param spellAbilityUnit 法术能力单位
     * @param targetPoint 目标点
     * @param spellAbilityId 法术能力ID
     * @param spellAbilityOrderId 法术能力订单ID
     * @param spellAbilityTargetType 法术能力目标类型
     * @param spellAbilityOrderCommandCard 法术能力指令卡
     * @return 包含法术目标点信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope jassAbilityPointScope(final CAbility spellAbility,
			final CUnit spellAbilityUnit, final AbilityPointTarget targetPoint, final War3ID spellAbilityId,
			final int spellAbilityOrderId, final JassOrderButtonType spellAbilityTargetType,
			final JassOrder spellAbilityOrderCommandCard) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(null, TriggerExecutionScope.EMPTY);
		scope.spellAbility = spellAbility;
		scope.spellAbilityUnit = spellAbilityUnit;
		scope.triggeringUnit = spellAbilityUnit;
		scope.spellTargetPoint = targetPoint;
		scope.spellAbilityId = spellAbilityId;
		scope.spellAbilityOrderId = spellAbilityOrderId;
		scope.spellAbilityTargetType = spellAbilityTargetType;
		return scope;
	}

	/**
     * 创建没有目标的法术范围
     * @param spellAbility 法术能力
     * @param spellAbilityUnit 法术能力单位
     * @param spellAbilityId 法术能力ID
     * @param spellAbilityOrderId 法术能力订单ID
     * @param spellAbilityTargetType 法术能力目标类型
     * @param spellAbilityOrderCommandCard 法术能力指令卡
     * @return 包含没有目标法术信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope jassAbilityNoTargetScope(final CAbility spellAbility,
			final CUnit spellAbilityUnit, final War3ID spellAbilityId, final int spellAbilityOrderId,
			final JassOrderButtonType spellAbilityTargetType, final JassOrder spellAbilityOrderCommandCard) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(null, TriggerExecutionScope.EMPTY);
		scope.spellAbility = spellAbility;
		scope.spellAbilityUnit = spellAbilityUnit;
		scope.triggeringUnit = spellAbilityUnit;
		scope.spellAbilityId = spellAbilityId;
		scope.spellAbilityOrderId = spellAbilityOrderId;
		scope.spellAbilityTargetType = spellAbilityTargetType;
		scope.spellAbilityOrderCommandCard = spellAbilityOrderCommandCard;
		return scope;
	}

	/**
     * 创建基本法术范围
     * @param spellAbility 法术能力
     * @param spellAbilityUnit 法术能力单位
     * @param spellAbilityId 法术能力ID
     * @return 包含基本法术信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope jassAbilityBasicScope(final CAbility spellAbility,
			final CUnit spellAbilityUnit, final War3ID spellAbilityId) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(null, TriggerExecutionScope.EMPTY);
		scope.spellAbility = spellAbility;
		scope.spellAbilityUnit = spellAbilityUnit;
		scope.triggeringUnit = spellAbilityUnit;
		scope.spellAbilityId = spellAbilityId;
		return scope;
	}

	/**
     * 创建基本法术范围
     * @param spellAbility 法术能力
     * @param spellAbilityUnit 法术能力单位
     * @param spellAbilityId 法术能力ID
     * @param spellAbilityOrderId 法术能力订单ID
     * @return 包含基本法术信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope jassAbilityBasicScope(final CAbility spellAbility,
			final CUnit spellAbilityUnit, final War3ID spellAbilityId, final int spellAbilityOrderId) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(null, TriggerExecutionScope.EMPTY);
		scope.spellAbility = spellAbility;
		scope.spellAbilityUnit = spellAbilityUnit;
		scope.triggeringUnit = spellAbilityUnit;
		scope.spellAbilityId = spellAbilityId;
		scope.spellAbilityOrderId = spellAbilityOrderId;
		return scope;
	}

	/**
     * 创建基本法术范围
     * @param spellAbility 法术能力
     * @param spellAbilityUnit 法术能力单位
     * @param spellAbilityId 法术能力ID
     * @param spellAbilityOrderId 法术能力订单ID
     * @param spellAbilityTargetType 法术能力目标类型
     * @param spellAbilityOrderCommandCard 法术能力指令卡
     * @return 包含基本法术信息的CommonTriggerExecutionScope
     */
	public static CommonTriggerExecutionScope jassAbilityBasicScope(final CAbility spellAbility,
			final CUnit spellAbilityUnit, final War3ID spellAbilityId, final int spellAbilityOrderId,
			final JassOrderButtonType spellAbilityTargetType, final JassOrder spellAbilityOrderCommandCard) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(null, TriggerExecutionScope.EMPTY);
		scope.spellAbility = spellAbility;
		scope.spellAbilityUnit = spellAbilityUnit;
		scope.triggeringUnit = spellAbilityUnit;
		scope.spellAbilityId = spellAbilityId;
		scope.spellAbilityOrderId = spellAbilityOrderId;
		scope.spellAbilityTargetType = spellAbilityTargetType;
		scope.spellAbilityOrderCommandCard = spellAbilityOrderCommandCard;
		return scope;
	}

	/**
     * 单位事件范围构建器接口
     */
	public static interface UnitEventScopeBuilder {
		CommonTriggerExecutionScope create(JassGameEventsWar3 triggerEventId, Trigger trigger, CUnit unit);
	}

	/**
     * 小部件事件范围构建器接口
     */
	public static interface WidgetEventScopeBuilder {
		CommonTriggerExecutionScope create(JassGameEventsWar3 triggerEventId, Trigger trigger, CWidget unit);
	}

	/**
     * 玩家事件范围构建器接口
     */
	public static interface PlayerEventScopeBuilder {
		CommonTriggerExecutionScope create(JassGameEventsWar3 triggerEventId, Trigger trigger, CPlayerJass player);
	}
}
