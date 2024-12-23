package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.IntIntMap;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.PathingFlags;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.RemovablePathingMapInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitStateListener.CUnitStateNotifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityDisableType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.GetAbilityByRawcodeVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.AutocastType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.CAutocastAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityBuildInProgress;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityCargoHold;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbilityGenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop.CAbilityNeutralBuilding;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMinable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityOverlayedMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.root.CAbilityRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABGenericTimedBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABTimedTickingPausedBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.BehaviorTargetUnitVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttackMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorBoardTransport;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorFollow;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorHoldPosition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorPatrol;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorStop;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorStun;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build.AbilityDisableWhileUpgradingVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CRegenType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackDamageTakenListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackDamageTakenModificationListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackDamageTakenModificationListenerDamageModResult;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackEvasionListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackFinalDamageTakenModificationListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPostDamageListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListenerPriority;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementEffectPriority;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementResult;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementStacking;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDefaultAccuracyCheckListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDefaultEtherealDamageModListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDefaultLifestealListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDefaultMagicImmuneDamageModListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDefaultSleepListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDefaultThornsListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderNoTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderTargetPoint;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderTargetWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CPlayerFogOfWar;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CUnitAttackVisionFogModifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CUnitDeathVisionFogModifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.region.CRegion;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.region.CRegionEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.region.CRegionManager;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.state.CUnitState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CWidgetEvent;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingFx;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuffType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class CUnit extends CWidget {
	private static RegionCheckerImpl regionCheckerImpl = new RegionCheckerImpl();

	private War3ID typeId; // 单位的类型标识符（ID
	private float facing; // degrees 单位的朝向，单位的朝向以角度表示（0° 到 360°）
	private float mana; // 当前的魔法值
	private int baseMaximumLife; // 单位的基础生命值上限
	private int maximumLife; // 单位的当前生命值上限
	private float lifeRegen; // 单位的生命恢复速率
	private float lifeRegenStrengthBonus; // 单位的力量属性等因素造成的生命恢复加成。
	private float lifeRegenBonus; // 单位的生命恢复加成。
	private float manaRegen; // 单位的魔法恢复速率
	private float manaRegenIntelligenceBonus; // 单位的智力属性等因素造成的魔法恢复加成。
	private float manaRegenBonus; // 单位的魔法恢复加成。
	private int baseMaximumMana; // 单位的基础魔法值上限
	private int maximumMana; // 单位的当前魔法值上限
	private int speed; // 单位的速度
	private int agilityDefensePermanentBonus; // 单位的永久敏捷属性加成
	private float agilityDefenseTemporaryBonus; // 单位的临时敏捷属性加成
	private int permanentDefenseBonus; // 单位的永久防御属性加成
	private float temporaryDefenseBonus; // 单位的临时防御属性加成


	private float totalTemporaryDefenseBonus; // 单位的总临时防御属性加成

	private int speedBonus; // 单位的速度加成

	// 反伤
	private CUnitDefaultThornsListener flatThornsListener = null;
	// 反伤 %
	private CUnitDefaultThornsListener percentThornsListener = null;
	// 生命偷取
	private CUnitDefaultLifestealListener lifestealListener = null;

	// 保存状态修改增益效果的列表
	private final List<StateModBuff> stateModBuffs = new ArrayList<>();
	// 存储非叠加状态增益效果的映射
	private final Map<NonStackingStatBuffType, Map<String, List<NonStackingStatBuff>>> nonStackingBuffs = new HashMap<>();
	// 存储非叠加特效的映射
	private final Map<String, List<NonStackingFx>> nonStackingFx = new HashMap<>();
	// 存储非叠加显示增益效果的映射
	private final Map<String, List<CBuff>> nonStackingDisplayBuffs = new HashMap<>();

	// 当前防御显示值
	private int currentDefenseDisplay;
	// 当前防御值
	private float currentDefense;
	// 每次迭代的基础生命回复值
	private float baseLifeRegenPerTick;
	// 当前生命回复值
	private float currentLifeRegenPerTick;
	// 当前法力回复值
	private float currentManaRegenPerTick;
	// 防御类型
	private CDefenseType defenseType;


	// 普攻冷却时间
	private int cooldownEndTime = 0;
	private float flyHeight;
	private int playerIndex;

	private final List<CAbility> abilities = new ArrayList<>();
	private final List<CAbility> disabledAbilities = new ArrayList<>();

	// 当前正在执行的行为
	private CBehavior currentBehavior;
	// 等待执行的指令队列
	private final Queue<COrder> orderQueue = new LinkedList<>();
	// 定义一个私有的CUnitType类型的变量unitType，用于存储单位的类型
	private CUnitType unitType;

	// 定义一个私有的Rectangle类型的变量collisionRectangle，用于存储单位的碰撞矩形
	private Rectangle collisionRectangle;

	// 定义一个私有的RemovablePathingMapInstance类型的变量pathingInstance，用于存储单位的寻路地图实例
	private RemovablePathingMapInstance pathingInstance;

	// 定义一个私有的、不可变的EnumSet<CUnitClassification>类型的变量classifications，初始化为空集合，用于存储单位的分类
	private final EnumSet<CUnitClassification> classifications = EnumSet.noneOf(CUnitClassification.class);

	// 定义一个私有的int类型的变量deathTurnTick，用于存储单位死亡的回合数
	private int deathTurnTick;

	private boolean raisable; // 可穿戴
	private boolean decays; // 衰减 腐烂
	private boolean corpse; // 尸体
	private boolean boneCorpse; // 骨骸
	private boolean falseDeath; // 虚假死亡

	private transient CUnitAnimationListener unitAnimationListener;

	// if you use triggers for this then the transient tag here becomes really
	// questionable -- it already was -- but I meant for those to inform us
	// which fields shouldn't be persisted if we do game state save later
	private transient CUnitStateNotifier stateNotifier = new CUnitStateNotifier();
	private transient List<StateListenerUpdate> stateListenersUpdates = new ArrayList<>();
	// 采集范围
	private float acquisitionRange;
	// 自动攻击目标查找器
	private transient static AutoAttackTargetFinderEnum autoAttackTargetFinderEnum = new AutoAttackTargetFinderEnum();
	// 自动施法目标查找器
	private transient static AutocastTargetFinderEnum autocastTargetFinderEnum = new AutocastTargetFinderEnum();
	// 当前自动施法的技能
	private transient CAutocastAbility autocastAbility = null;

	private transient CBehaviorMove moveBehavior; // 移动行为
	private transient CBehaviorAttack attackBehavior; // 攻击行为
	private transient CBehaviorAttackMove attackMoveBehavior; // 攻击并移动行为
	private transient CBehaviorFollow followBehavior; // 跟随行为
	private transient CBehaviorPatrol patrolBehavior; // 巡逻行为
	private transient CBehaviorStop stopBehavior; // 停止行为
	private transient CBehaviorHoldPosition holdPositionBehavior; // 停留位置行为
	private transient CBehaviorBoardTransport boardTransportBehavior; // 乘坐行为
	// 表示当前是否正在构建中
	 private boolean constructing = false;

	 // 表示构建过程是否暂停
	 private boolean constructingPaused = false;

	 // 表示结构状态，具体含义需结合上下文
	 private boolean structure;

	 // 表示升级ID类型，War3ID是一个类，具体含义需结合上下文
	 private War3ID upgradeIdType = null;

	 // 表示构建进度，范围从0.0到1.0
	 private float constructionProgress;

	 // 表示是否隐藏
	 private boolean hidden = false;

	 // 表示是否暂停
	 private boolean paused = false;

	 // 表示是否接受命令
	 private boolean acceptingOrders = true;

	 // 表示是否无敌
	 private boolean invulnerable = false;

	 // 表示是否魔法免疫
	 private boolean magicImmune = false;

	 // 表示是否抵抗某种效果
	 private boolean resistant = false;

	// 自动攻击
	private boolean autoAttack = true;
	private boolean moveDisabled = false;
	private CBehavior defaultBehavior; // 当前行为
	private CBehavior interruptedDefaultBehavior; // 被打断的行为
	private CBehavior interruptedBehavior; // 被打断的行为
	private COrder lastStartedOrder = null; // 最后执行的命令
	// 定义一个CUnit类型的私有变量workerInside，用于存储当前正在工作的单位
	 private CUnit workerInside;

	 // 定义一个War3ID类型的数组buildQueue，大小为WarsmashConstants.BUILD_QUEUE_SIZE，用于存储建造队列中的单位ID
	 private final War3ID[] buildQueue = new War3ID[WarsmashConstants.BUILD_QUEUE_SIZE];

	 // 定义一个QueueItemType类型的数组buildQueueTypes，大小为WarsmashConstants.BUILD_QUEUE_SIZE，用于存储建造队列中的单位类型
	 private final QueueItemType[] buildQueueTypes = new QueueItemType[WarsmashConstants.BUILD_QUEUE_SIZE];

	 // 定义一个布尔类型的私有变量queuedUnitFoodPaid，用于标记是否已经支付了队列中单位的资源费用
	 private boolean queuedUnitFoodPaid;

	 // 定义一个AbilityTarget类型的私有变量rallyPoint，用于存储集结点的位置
	 private AbilityTarget rallyPoint;

	 // 定义一个整型变量foodMade，用于记录产生的食物总量
	 private int foodMade;

	 // 定义一个整型变量foodUsed，用于记录使用的食物总量
	 private int foodUsed;

	 // 定义一个整型变量triggerEditorCustomValue，用于存储触发器编辑器的自定义值
	 private int triggerEditorCustomValue;


	private List<CUnitAttack> unitSpecificAttacks; // 普攻列表
	private List<CUnitAttack> unitSpecificCurrentAttacks; // 当前能用的普攻列表
	private boolean disableAttacks; // 禁用普攻
	private final CUnitAttackVisionFogModifier attackFogMod;

	private final Map<CUnitAttackPreDamageListenerPriority, List<CUnitAttackPreDamageListener>> preDamageListeners = new HashMap<>(); // onAttack 攻击受伤前处理器列表
	private final List<CUnitAttackPostDamageListener> postDamageListeners = new ArrayList<>(); // onHit 受击 处理器列表
	private final List<CUnitAttackDamageTakenModificationListener> damageTakenModificationListeners = new ArrayList<>(); // onDamage 伤害修改器列表
	private final List<CUnitAttackFinalDamageTakenModificationListener> finalDamageTakenModificationListeners = new ArrayList<>(); // onDamage 最终伤害修改器列表
	private final List<CUnitAttackDamageTakenListener> damageTakenListeners = new ArrayList<>(); // onDamage 伤害处理器列表
	private final Map<CUnitDeathReplacementEffectPriority, List<CUnitDeathReplacementEffect>> deathReplacementEffects = new HashMap<>(); // onDeath 死亡替换效果列表
	private final List<CUnitAttackEvasionListener> evasionListeners = new ArrayList<>(); // 闪避 监听

	private transient Set<CRegion> containingRegions = new LinkedHashSet<>();
	private transient Set<CRegion> priorContainingRegions = new LinkedHashSet<>();

	// 建造过程中是否消耗工人
	private boolean constructionConsumesWorker;

	// 该单位死亡时是否爆炸
	private boolean explodesOnDeath;

	// 死亡时爆炸效果的War3ID
	private War3ID explodesOnDeathBuffId;

	// 冷却到期时间
	private final IntIntMap rawcodeToCooldownExpireTime = new IntIntMap();
	// 冷却开始时间
	private final IntIntMap rawcodeToCooldownStartTime = new IntIntMap();

	/**
	 * CUnit类构造函数，用于创建一个新的CUnit实例。
	 *
	 * @param handleId 单位的唯一句柄ID
	 * @param playerIndex 玩家索引
	 * @param x 单位在地图上的X坐标
	 * @param y 单位在地图上的Y坐标
	 * @param life 单位的当前生命值
	 * @param typeId 单位的类型ID
	 * @param facing 单位的朝向角度
	 * @param mana 单位的当前魔法值
	 * @param maximumLife 单位的最大生命值
	 * @param lifeRegen 单位的生命恢复率
	 * @param maximumMana 单位的最大魔法值
	 * @param speed 单位的移动速度
	 * @param unitType 单位的类型信息
	 */
	public CUnit(final int handleId, final int playerIndex, final float x, final float y, final float life,
			final War3ID typeId, final float facing, final float mana, final int maximumLife, final float lifeRegen,
			final int maximumMana, final int speed, final CUnitType unitType) {
		super(handleId, x, y, life);
		// 设置玩家索引
		this.playerIndex = playerIndex;
		// 设置单位类型ID
		this.typeId = typeId;
		// 设置面向方向
		this.facing = facing;
		// 设置当前法力值
		this.mana = mana;
		// 设置基础最大生命值
		this.baseMaximumLife = maximumLife;
		// 设置当前最大生命值
		this.maximumLife = maximumLife;
		// 设置生命恢复速率
		this.lifeRegen = lifeRegen;
		// 设置法力恢复速率，从单位类型获取
		this.manaRegen = unitType.getManaRegen();
		// 设置基础最大法力值
		this.baseMaximumMana = maximumMana;
		// 设置当前最大法力值
		this.maximumMana = maximumMana;
		// 设置移动速度
		this.speed = speed;
		// 设置飞行高度，从单位类型获取默认飞行高度
		this.flyHeight = unitType.getDefaultFlyingHeight();
		// 设置单位类型
		this.unitType = unitType;
		// 设置防御类型，从单位类型获取
		this.defenseType = unitType.getDefenseType();
		// 添加单位类型的所有分类
		this.classifications.addAll(unitType.getClassifications());
		// 设置获取范围，从单位类型获取默认获取范围
		this.acquisitionRange = unitType.getDefaultAcquisitionRange();
		// 判断是否为建筑
		this.structure = unitType.isBuilding();
		// 设置停止行为
		this.stopBehavior = new CBehaviorStop(this);
		// 设置默认行为为停止行为
		this.defaultBehavior = this.stopBehavior;
		// 判断是否可复活
		this.raisable = unitType.isRaise();
		// 判断是否衰减
		this.decays = unitType.isDecay();
		// 初始化非堆叠增益效果
		initializeNonStackingBuffs();
		// 初始化监听器列表
		initializeListenerLists();
		// 添加伤害前监听器，用于检查攻击准确性
		addPreDamageListener(CUnitAttackPreDamageListenerPriority.ACCURACY, new CUnitDefaultAccuracyCheckListener());
		// 创建攻击视野雾气修改器
		this.attackFogMod = new CUnitAttackVisionFogModifier(this, playerIndex);
		// 计算所有派生字段
		computeAllDerivedFields();

	}


	// 执行默认行为
	public void performDefaultBehavior(final CSimulation game) {
		// 如果当前行为不为空，则结束当前行为
		if (this.currentBehavior != null) {
			// 调用当前行为的结束方法，并传入游戏实例和true表示正常结束
			this.currentBehavior.end(game, true);
		}
		// 将当前行为设置默认行为
		this.currentBehavior = this.defaultBehavior;
		// 开始默认行为
		this.currentBehavior.begin(game);

	}

	// 该方法用于重新生成单位的寻路实例
	public void regeneratePathingInstance(final CSimulation game, final BufferedImage buildingPathingPixelMap) {
		// 获取单位当前的X坐标和Y坐标
		float unitX = getX();
		float unitY = getY();

		// 将单位的坐标对齐到64的倍数上，这是因为寻路网格通常是64x64的
		unitX = (float) Math.floor(unitX / 64f) * 64f;
		unitY = (float) Math.floor(unitY / 64f) * 64f;

		// 如果建筑寻路像素图的宽度的一半是奇数，则将单位的X坐标向右移动32个单位
		if (((buildingPathingPixelMap.getWidth() / 2) % 2) == 1) {
			unitX += 32f;
		}

		// 如果建筑寻路像素图的高度的一半是奇数，则将单位的Y坐标向下移动32个单位
		if (((buildingPathingPixelMap.getHeight() / 2) % 2) == 1) {
			unitY += 32f;
		}

		// 使用游戏中的寻路网格，将建筑寻路像素图作为可移除的寻路覆盖纹理绘制到指定坐标
		this.pathingInstance = game.getPathingGrid().blitRemovablePathingOverlayTexture(unitX, unitY,
				270 /* 无旋转，面向前方 */, buildingPathingPixelMap);

		// 更新单位的X坐标和Y坐标
		setX(unitX);
		setY(unitY);
	}

	// 获取生命回复加成值
	public float getLifeRegenBonus() {
		return this.lifeRegenBonus; // 返回当前生命回复加成值
	}

	// 设置生命回复强度加成，并重新计算派生字段
	public void setLifeRegenStrengthBonus(final float lifeRegenStrengthBonus) {
		this.lifeRegenStrengthBonus = lifeRegenStrengthBonus; // 设置新的生命回复强度加成值
		computeDerivedFields(NonStackingStatBuffType.HPGEN); // 重新计算与生命回复相关的派生字段
	}

	// 定义一个方法，用于给单位添加非堆叠的状态增益效果
	public void addNonStackingStatBuff(final NonStackingStatBuff buff) {
		// TODO #如果buff的类型是ALLATK 所有攻击 (近战攻击和远程攻击)
		if (buff.getBuffType() == NonStackingStatBuffType.ALLATK) {
			// 获取近战攻击的非堆叠状态buff映射
			Map<String, List<NonStackingStatBuff>> buffKeyMap = this.nonStackingBuffs
					.get(NonStackingStatBuffType.MELEEATK);
			// 如果映射不存在，则创建一个新的映射并放入nonStackingBuffs中
			if (buffKeyMap == null) {
				buffKeyMap = new HashMap<>();
				this.nonStackingBuffs.put(NonStackingStatBuffType.MELEEATK, buffKeyMap);
			}
			// 获取当前buff的堆叠键对应的buff列表
			List<NonStackingStatBuff> theList = buffKeyMap.get(buff.getStackingKey());
			// 如果列表不存在，则创建一个新的列表并放入映射中
			if (theList == null) {
				theList = new ArrayList<>();
				buffKeyMap.put(buff.getStackingKey(), theList);
			}

			// 重复上述步骤，但这次是为了远程攻击的非堆叠状态buff映射
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.RNGDATK);
			if (buffKeyMap == null) {
				buffKeyMap = new HashMap<>();
				this.nonStackingBuffs.put(NonStackingStatBuffType.RNGDATK, buffKeyMap);
			}
			theList = buffKeyMap.get(buff.getStackingKey());
			if (theList == null) {
				theList = new ArrayList<>();
				buffKeyMap.put(buff.getStackingKey(), theList);
			}

			// 将当前的buff添加到列表中
			theList.add(buff);
		}

		// TODO #攻击百分比
		else if (buff.getBuffType() == NonStackingStatBuffType.ALLATKPCT) {
			Map<String, List<NonStackingStatBuff>> buffKeyMap = this.nonStackingBuffs
					.get(NonStackingStatBuffType.MELEEATKPCT);
			if (buffKeyMap == null) {
				buffKeyMap = new HashMap<>();
				this.nonStackingBuffs.put(NonStackingStatBuffType.MELEEATKPCT, buffKeyMap);
			}
			List<NonStackingStatBuff> theList = buffKeyMap.get(buff.getStackingKey());
			if (theList == null) {
				theList = new ArrayList<>();
				buffKeyMap.put(buff.getStackingKey(), theList);
			}
			theList.add(buff);

			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.RNGDATKPCT);
			if (buffKeyMap == null) {
				buffKeyMap = new HashMap<>();
				this.nonStackingBuffs.put(NonStackingStatBuffType.RNGDATKPCT, buffKeyMap);
			}
			theList = buffKeyMap.get(buff.getStackingKey());
			if (theList == null) {
				theList = new ArrayList<>();
				buffKeyMap.put(buff.getStackingKey(), theList);
			}
			theList.add(buff);
		}
		// TODO #其他类型的buff
		else {
			Map<String, List<NonStackingStatBuff>> buffKeyMap = this.nonStackingBuffs.get(buff.getBuffType());
			if (buffKeyMap == null) {
				buffKeyMap = new HashMap<>();
				this.nonStackingBuffs.put(buff.getBuffType(), buffKeyMap);
			}
			List<NonStackingStatBuff> theList = buffKeyMap.get(buff.getStackingKey());
			if (theList == null) {
				theList = new ArrayList<>();
				buffKeyMap.put(buff.getStackingKey(), theList);
			}
			theList.add(buff);
		}
		computeDerivedFields(buff.getBuffType());
	}

	// 从单位的非堆叠状态增益效果映射中移除指定的非堆叠状态增益效果
	public void removeNonStackingStatBuff(final NonStackingStatBuff buff) {
		if (buff.getBuffType() == NonStackingStatBuffType.ALLATK) {
			Map<String, List<NonStackingStatBuff>> buffKeyMap = this.nonStackingBuffs
					.get(NonStackingStatBuffType.MELEEATK);
			try {
				buffKeyMap.get(buff.getStackingKey()).remove(buff);
			}
			catch (final Exception e) {
				System.err.println(e.getLocalizedMessage());
				System.err.println(e.getStackTrace().toString());
				System.err.println("From: " + getTypeId().asStringValue());
			}

			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.RNGDATK);
			try {
				buffKeyMap.get(buff.getStackingKey()).remove(buff);
			}
			catch (final Exception e) {
				System.err.println(e.getLocalizedMessage());
				System.err.println(e.getStackTrace().toString());
				System.err.println("From: " + getTypeId().asStringValue());
			}
		}
		else if (buff.getBuffType() == NonStackingStatBuffType.ALLATKPCT) {
			Map<String, List<NonStackingStatBuff>> buffKeyMap = this.nonStackingBuffs
					.get(NonStackingStatBuffType.MELEEATKPCT);
			try {
				buffKeyMap.get(buff.getStackingKey()).remove(buff);
			}
			catch (final Exception e) {
				System.err.println(e.getLocalizedMessage());
				System.err.println(e.getStackTrace().toString());
				System.err.println("From: " + getTypeId().asStringValue());
			}

			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.RNGDATKPCT);
			try {
				buffKeyMap.get(buff.getStackingKey()).remove(buff);
			}
			catch (final Exception e) {
				System.err.println(e.getLocalizedMessage());
				System.err.println(e.getStackTrace().toString());
				System.err.println("From: " + getTypeId().asStringValue());
			}
		}
		else {
			final Map<String, List<NonStackingStatBuff>> buffKeyMap = this.nonStackingBuffs.get(buff.getBuffType());
			try {
				buffKeyMap.get(buff.getStackingKey()).remove(buff);
			}
			catch (final Exception e) {
				System.err.println(e.getLocalizedMessage());
				System.err.println(e.getStackTrace().toString());
				System.err.println("From: " + getTypeId().asStringValue());
			}
		}
		computeDerivedFields(buff.getBuffType());
	}

	/**
	 * 向状态修改增益列表中添加一个新的监听器。
	 * 如果列表中不包含该监听器，则将其添加到列表的开头。
	 *
	 * @param listener 要添加的状态修改增益监听器
	 */
	public void addStateModBuff(final StateModBuff listener) {
		if (!this.stateModBuffs.contains(listener)) {
			this.stateModBuffs.add(0, listener);
		}
	}

	/**
	 * 从状态修改增益列表中移除指定的监听器。
	 *
	 * @param listener 要移除的状态修改增益监听器
	 */
	public void removeStateModBuff(final StateModBuff listener) {
		this.stateModBuffs.remove(listener);
	}

	/**
	 * 移除所有指定类型的状态修改增益监听器。
	 * 从列表的末尾开始向前遍历，以避免在遍历过程中修改列表导致的索引错位问题。
	 *
	 * @param type 要移除的状态修改增益监听器的类型
	 */
	public void removeAllStateModBuffs(final StateModBuffType type) {
		for (int i = this.stateModBuffs.size() - 1; i >= 0; i--) {
			if (this.stateModBuffs.get(i).getBuffType() == type) {
				this.stateModBuffs.remove(i);
			}
		}
	}


	public void computeUnitState(final CSimulation game, final StateModBuffType type) {
		switch (type) {
		// 禁用攻击
		case DISABLE_ATTACK:
		// 禁用近战攻击
		case DISABLE_MELEE_ATTACK:
		// 禁用远程攻击
		case DISABLE_RANGED_ATTACK:
		// 禁用特殊攻击
		case DISABLE_SPECIAL_ATTACK:
		// 禁用法术
		case DISABLE_SPELLS:
		// 使角色处于虚无状态，不受物理伤害
		case ETHEREAL:

			// 禁用攻击
			boolean isDisableAttack = false;
			// 禁用近战攻击
			boolean isDisableMeleeAttack = false;
			// 禁用远程攻击
			boolean isDisableRangedAttack = false;
			// 禁用特殊攻击
			boolean isDisableSpecialAttack = false;
			// 禁用法术
			boolean isDisableSpells = false;
			// 虚无态标志
			boolean isEthereal = false;

			// 遍历状态效果增益列表
			for (final StateModBuff buff : this.stateModBuffs) {
				// 检查增益类型是否为禁止攻击
				if (buff.getBuffType() == StateModBuffType.DISABLE_ATTACK) {
					// 如果增益值不为0，则设置禁止攻击标志为true
					if (buff.getValue() != 0) {
						isDisableAttack = true;
					}
				}
				// 检查增益类型是否为禁止近战攻击
				if (buff.getBuffType() == StateModBuffType.DISABLE_MELEE_ATTACK) {
					// 如果增益值不为0，则设置禁止近战攻击标志为true
					if (buff.getValue() != 0) {
						isDisableMeleeAttack = true;
					}
				}
				// 检查增益类型是否为禁止远程攻击
				if (buff.getBuffType() == StateModBuffType.DISABLE_RANGED_ATTACK) {
					// 如果增益值不为0，则设置禁止远程攻击标志为true
					if (buff.getValue() != 0) {
						isDisableRangedAttack = true;
					}
				}
				// 检查增益类型是否为禁止特殊攻击
				if (buff.getBuffType() == StateModBuffType.DISABLE_SPECIAL_ATTACK) {
					// 如果增益值不为0，则设置禁止特殊攻击标志为true
					if (buff.getValue() != 0) {
						isDisableSpecialAttack = true;
					}
				}
				// 检查增益类型是否为禁止使用法术
				if (buff.getBuffType() == StateModBuffType.DISABLE_SPELLS) {
					// 如果增益值不为0，则设置禁止使用法术标志为true
					if (buff.getValue() != 0) {
						isDisableSpells = true;
					}
				}
				// 检查增益类型是否为虚无状态
				if (buff.getBuffType() == StateModBuffType.ETHEREAL) {
					// 如果增益值不为0，则设置虚无状态标志为true
					if (buff.getValue() != 0) {
						isEthereal = true;
					}
				}
			}

//			CAbility attack = this.getFirstAbilityOfType(CAbilityAttack.class);
//			if (attack != null) {
//				attack.setDisabled(isDisableAttack, CAbilityDisableType.ATTACKDISABLED);
//			}

			// 遍历当前对象的所有能力
			for (final CAbility ability : this.abilities) {
				// 如果满足以下任一条件，则将能力设置为禁用状态
				if (
					// 如果攻击被禁用或者是虚无状态，并且能力类别是攻击
						((isDisableAttack || isEthereal) && (ability.getAbilityCategory() == CAbilityCategory.ATTACK))
								// 如果法术被禁用，并且能力类别是法术且不是物理攻击
								|| (isDisableSpells && (ability.getAbilityCategory() == CAbilityCategory.SPELL) && !ability.isPhysical())
								// 如果是虚无状态，并且是物理攻击，同时能力类别是法术或核心
								|| (isEthereal && ability.isPhysical() && ((ability.getAbilityCategory() == CAbilityCategory.SPELL) || (ability.getAbilityCategory() == CAbilityCategory.CORE)))
				) {
					// 设置能力为禁用状态，禁用类型为攻击禁用
					ability.setDisabled(true, CAbilityDisableType.ATTACKDISABLED);
				} else {
					// 否则，设置能力为启用状态
					ability.setDisabled(false, CAbilityDisableType.ATTACKDISABLED);
				}
			}

			final List<CUnitAttack> newAttackList = new ArrayList<CUnitAttack>();
			for (int i = 0; i < this.unitSpecificAttacks.size(); i++) {
				final CUnitAttack attack = this.unitSpecificAttacks.get(i);
				// 检查是否应该添加攻击到新攻击列表
				if (/* 攻击类型是否启用 */ ((getUnitType().getAttacksEnabled() & (i + 1)) != 0) &&
						// 检查攻击是否未被禁用
						!isDisableAttack &&
						// 检查近战攻击是否未被禁用，或者武器类型不是普通类型
						(!isDisableMeleeAttack || !attack.getWeaponType().equals(CWeaponType.NORMAL)) &&
						// 检查远程攻击是否未被禁用，或者武器类型是普通类型
						(!isDisableRangedAttack || attack.getWeaponType().equals(CWeaponType.NORMAL)) &&
						// 检查特殊攻击是否未被禁用，或者目标不是单一的树
						(!isDisableSpecialAttack || !((attack.getTargetsAllowed().size() == 1) &&
								attack.getTargetsAllowed().equals(EnumSet.of(CTargetType.TREE))))) {
					// 如果所有条件都满足，则添加攻击到新列表
					newAttackList.add(attack);
				}


			}
			// 设置当前可用攻击列表
			setUnitSpecificCurrentAttacks(newAttackList);

			// 通知攻击列表已更改
			notifyAttacksChanged();

			// 检测禁用技能列表
			checkDisabledAbilities(game, isDisableAttack);

			// 虚无状态
			if (isEthereal) {
				// 添加伤害修改器
				if (!this.damageTakenModificationListeners.contains(CUnitDefaultEtherealDamageModListener.INSTANCE)) {
					addDamageTakenModificationListener(CUnitDefaultEtherealDamageModListener.INSTANCE);
				}
				// 修改角色顶点颜色 虚无 半透明
				game.changeUnitVertexColor(this, RenderUnit.ETHEREAL);
				// Disable physical skills
//				for (CAbility ability : this.abilities) {
//					if (ability.isPhysical()) {
//						ability.setDisabled(true);
//					}
//				}
			}
			else {
				// 移除伤害修改器
				if (this.damageTakenModificationListeners.contains(CUnitDefaultEtherealDamageModListener.INSTANCE)) {
					removeDamageTakenModificationListener(CUnitDefaultEtherealDamageModListener.INSTANCE);
					// 恢复默认颜色
					game.changeUnitVertexColor(this, RenderUnit.DEFAULT);
				}
				// Enable physical skills
//				for (CAbility ability : this.abilities) {
//					if (ability.isPhysical()) {
//						ability.setDisabled(false);
//					}
//				}
			}
			break;
		// 禁用自动攻击
		case DISABLE_AUTO_ATTACK:
			boolean isDisableAutoAttack = false;
			for (final StateModBuff buff : this.stateModBuffs) {
				if (buff.getBuffType() == StateModBuffType.DISABLE_AUTO_ATTACK) {
					if (buff.getValue() != 0) {
						isDisableAutoAttack = true;
					}
				}
			}
			this.autoAttack = !isDisableAutoAttack;
			break;
		// 魔法攻击免疫
		case MAGIC_IMMUNE:
			// 检查单位是否对魔法免疫
			boolean isMagicImmune = false;
			// 遍历状态修改增益列表
			for (final StateModBuff buff : this.stateModBuffs) {
				// 如果增益类型是魔法免疫
				if (buff.getBuffType() == StateModBuffType.MAGIC_IMMUNE) {
					// 并且增益值不为0，则单位对魔法免疫
					if (buff.getValue() != 0) {
						isMagicImmune = true;
					}
				}
			}
			// 设置单位的魔法免疫状态
			setMagicImmune(isMagicImmune);
			// 如果单位对魔法免疫
			if (isMagicImmune) {
				// 并且没有添加默认的魔法免疫伤害修改监听器
				if (!this.finalDamageTakenModificationListeners
						.contains(CUnitDefaultMagicImmuneDamageModListener.INSTANCE)) {
					// 添加默认的魔法免疫伤害修改监听器
					addFinalDamageTakenModificationListener(CUnitDefaultMagicImmuneDamageModListener.INSTANCE);
				}
			} else {
				// 如果单位不对魔法免疫，并且已经添加了默认的魔法免疫伤害修改监听器
				if (this.finalDamageTakenModificationListeners
						.contains(CUnitDefaultMagicImmuneDamageModListener.INSTANCE)) {
					// 移除默认的魔法免疫伤害修改监听器
					removeFinalDamageTakenModificationListener(CUnitDefaultMagicImmuneDamageModListener.INSTANCE);
				}
			}

			break;
		// 检查是否具有抗性状态
		case RESISTANT:
			// 初始化isResistant标志为false
			boolean isResistant = false;
			// 遍历所有状态效果增益
			for (final StateModBuff buff : this.stateModBuffs) {
				// 如果增益类型是抗性
				if (buff.getBuffType() == StateModBuffType.RESISTANT) {
					// 并且增益值不为0
					if (buff.getValue() != 0) {
						// 设置isResistant为true
						isResistant = true;
					}
				}
			}
			// 更新抗性状态
			this.resistant = isResistant;
			break;

		case SLEEPING: // 睡眠状态
		case STUN: // 眩晕状态
			// 检查是否有睡眠或眩晕状态效果
			boolean isSleeping = false;
			boolean isStun = false;
			for (final StateModBuff buff : this.stateModBuffs) {
				// 如果状态效果是睡眠且值不为0，则设置isSleeping为true
				if (buff.getBuffType() == StateModBuffType.SLEEPING) {
					if (buff.getValue() != 0) {
						isSleeping = true;
					}
				}
				// 如果状态效果是眩晕且值不为0，则设置isStun为true
				if (buff.getBuffType() == StateModBuffType.STUN) {
					if (buff.getValue() != 0) {
						isStun = true;
					}
				}
			}

			// 如果存在睡眠或眩晕状态
			if (isSleeping || isStun) {
				// 如果当前行为为空或者不是眩晕状态，则中断当前行为
				if ((this.currentBehavior == null)
						|| (this.currentBehavior.getHighlightOrderId() != OrderIds.stunned)) {
					if (this.currentBehavior != null) {
						// 保存中断的行为
						this.interruptedBehavior = this.currentBehavior;
						this.interruptedDefaultBehavior = this.defaultBehavior;
					}
					// 设置当前行为为眩晕行为
					this.currentBehavior = new CBehaviorStun(this);
					this.currentBehavior.begin(game);
					setDefaultBehavior(this.currentBehavior);
					this.stateNotifier.ordersChanged();
				}
			} else {
				// 如果当前行为是眩晕状态，则恢复默认行为并获取下一个行为
				if ((this.currentBehavior != null)
						&& (this.currentBehavior.getHighlightOrderId() == OrderIds.stunned)) {
					// 恢复中断的行为
					setDefaultBehavior(this.interruptedDefaultBehavior);
					this.currentBehavior = pollNextOrderBehavior(game);
					this.interruptedBehavior = null;
					this.interruptedDefaultBehavior = null;
					this.stateNotifier.ordersChanged();
				}
			}


			// 如果单位处于睡眠状态
			if (isSleeping) {
				// 如果伤害监听器列表中不包含默认睡眠监听器实例，则添加
				if (!this.damageTakenListeners.contains(CUnitDefaultSleepListener.INSTANCE)) {
					addDamageTakenListener(CUnitDefaultSleepListener.INSTANCE);
				}
			} else {
				// 如果单位不处于睡眠状态，并且伤害监听器列表中包含默认睡眠监听器实例，则移除
				if (this.damageTakenListeners.contains(CUnitDefaultSleepListener.INSTANCE)) {
					removeDamageTakenListener(CUnitDefaultSleepListener.INSTANCE);
				}
			}

			break;
		//缠绕 束缚
		case SNARED:
			// 初始化一个布尔变量isSnared，用于标记是否被缠绕
			boolean isSnared = false;
			// 遍历当前对象的状态修改增益列表
			for (final StateModBuff buff : this.stateModBuffs) {
				// 如果增益类型是缠绕
				if (buff.getBuffType() == StateModBuffType.SNARED) {
					// 如果缠绕的值不为0，则表示被缠绕
					if (buff.getValue() != 0) {
						isSnared = true;
					}
				}
			}
			// 如果被缠绕
			if (isSnared) {
				// 设置飞行高度为0
				setFlyHeight(0);
				// 并且禁用移动
				this.moveDisabled = true;
			} else {
				// 如果没有被缠绕且移动被禁用
				if (this.moveDisabled) {
					// 恢复默认飞行高度
					setFlyHeight(this.unitType.getDefaultFlyingHeight());
					// 并且启用移动
					this.moveDisabled = false;
				}
			}
			break;
		// 无敌
		case INVULNERABLE:
			// 初始化无敌状态标志为false
			boolean isInvuln = false;
			// 遍历所有的状态修改增益效果
			for (final StateModBuff buff : this.stateModBuffs) {
				// 如果增益效果类型为无敌
				if (buff.getBuffType() == StateModBuffType.INVULNERABLE) {
					// 并且增益效果的值不为0
					if (buff.getValue() != 0) {
						// 设置无敌状态标志为true
						isInvuln = true;
					}
				}
			}
			// 根据无敌状态标志设置是否无敌
			setInvulnerable(isInvuln);
			// 通知状态变化
			this.stateNotifier.abilitiesChanged();

			break;
		default:
			break;
		}
	}

	public void computeDerivedFields(final NonStackingStatBuffType type) {
		Map<String, List<NonStackingStatBuff>> buffKeyMap;
		switch (type) {
		case DEF:
		case DEFPCT:
			this.currentDefenseDisplay = this.unitType.getDefense() + this.agilityDefensePermanentBonus
					+ this.permanentDefenseBonus;

			float totalNSDefBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.DEF);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSDefBuff += buffForKey;
			}
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.DEFPCT);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSDefBuff += buffForKey * this.currentDefenseDisplay;
			}

			this.totalTemporaryDefenseBonus = this.temporaryDefenseBonus + this.agilityDefenseTemporaryBonus
					+ totalNSDefBuff;
			this.currentDefense = this.currentDefenseDisplay + this.totalTemporaryDefenseBonus;
			break;
		case HPGEN:
		case HPGENPCT:
		case MAXHPGENPCT:
			float totalNSHPGenBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.HPGEN);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSHPGenBuff += buffForKey;
			}
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.HPGENPCT);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSHPGenBuff += buffForKey * (this.lifeRegen + this.lifeRegenBonus + this.lifeRegenStrengthBonus);
			}
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MAXHPGENPCT);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSHPGenBuff += buffForKey * this.maximumLife;
			}
			this.currentLifeRegenPerTick = (this.lifeRegen + this.lifeRegenBonus + this.lifeRegenStrengthBonus
					+ totalNSHPGenBuff) * WarsmashConstants.SIMULATION_STEP_TIME;
			break;
		case MPGEN:
		case MPGENPCT:
		case MAXMPGENPCT:
			float totalNSMPGenBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MPGEN);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMPGenBuff += buffForKey;
			}
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MPGENPCT);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMPGenBuff += buffForKey
						* (this.manaRegen + this.manaRegenBonus + this.manaRegenIntelligenceBonus);
			}
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MAXMPGENPCT);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMPGenBuff += buffForKey * this.maximumMana;
			}
			this.currentManaRegenPerTick = (this.manaRegen + this.manaRegenBonus + this.manaRegenIntelligenceBonus
					+ totalNSMPGenBuff) * WarsmashConstants.SIMULATION_STEP_TIME;
			break;
		case MVSPDPCT:
		case MVSPD:
			float totalNSMvSpdBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MVSPD);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMvSpdBuff += buffForKey;
			}
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MVSPDPCT);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMvSpdBuff += buffForKey * this.speed;
			}
			this.speedBonus = Math.round(totalNSMvSpdBuff);
			break;
		case MELEEATK:
		case MELEEATKPCT:
			for (final CUnitAttack attack : getUnitSpecificAttacks()) {
				if ((attack.getWeaponType() != null) && (attack.getWeaponType() == CWeaponType.NORMAL)) {
					attack.setNonStackingFlatBuffs(this.nonStackingBuffs.get(NonStackingStatBuffType.MELEEATK));
					attack.setNonStackingPctBuffs(this.nonStackingBuffs.get(NonStackingStatBuffType.MELEEATKPCT));
					attack.computeDerivedFields();
				}
			}
			notifyAttacksChanged();
			break;
		case RNGDATK:
		case RNGDATKPCT:
			for (final CUnitAttack attack : getUnitSpecificAttacks()) {
				if ((attack.getWeaponType() != null) && (attack.getWeaponType() != CWeaponType.NORMAL)) {
					attack.setNonStackingFlatBuffs(this.nonStackingBuffs.get(NonStackingStatBuffType.RNGDATK));
					attack.setNonStackingPctBuffs(this.nonStackingBuffs.get(NonStackingStatBuffType.RNGDATKPCT));
					attack.computeDerivedFields();
				}
			}
			notifyAttacksChanged();
			break;
		case ALLATK:
		case ALLATKPCT:
			for (final CUnitAttack attack : getUnitSpecificAttacks()) {
				if ((attack.getWeaponType() != null) && (attack.getWeaponType() == CWeaponType.NORMAL)) {
					attack.setNonStackingFlatBuffs(this.nonStackingBuffs.get(NonStackingStatBuffType.MELEEATK));
					attack.setNonStackingPctBuffs(this.nonStackingBuffs.get(NonStackingStatBuffType.MELEEATKPCT));
					attack.computeDerivedFields();
				}
			}
			for (final CUnitAttack attack : getUnitSpecificAttacks()) {
				if ((attack.getWeaponType() != null) && (attack.getWeaponType() != CWeaponType.NORMAL)) {
					attack.setNonStackingFlatBuffs(this.nonStackingBuffs.get(NonStackingStatBuffType.RNGDATK));
					attack.setNonStackingPctBuffs(this.nonStackingBuffs.get(NonStackingStatBuffType.RNGDATKPCT));
					attack.computeDerivedFields();
				}
			}
			notifyAttacksChanged();
			break;
		case ATKSPD:
			float totalNSAtkSpdBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.ATKSPD);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSAtkSpdBuff += buffForKey;
			}
			for (final CUnitAttack attack : getUnitSpecificAttacks()) {
				attack.setAttackSpeedModifier(totalNSAtkSpdBuff);
			}
			notifyAttacksChanged();
			break;
		case HPSTEAL:
			float totalNSVampBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.HPSTEAL);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSVampBuff += buffForKey;
			}
			if (this.lifestealListener != null) {
				this.lifestealListener.setAmount(totalNSVampBuff);
			}
			else {
				this.lifestealListener = new CUnitDefaultLifestealListener(totalNSVampBuff);
				addPostDamageListener(this.lifestealListener);
			}
			break;
		case THORNS:
			float totalNSThornsBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.THORNS);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSThornsBuff += buffForKey;
			}
			if (this.flatThornsListener != null) {
				this.flatThornsListener.setAmount(totalNSThornsBuff);
			}
			else {
				this.flatThornsListener = new CUnitDefaultThornsListener(false, totalNSThornsBuff);
				addDamageTakenListener(this.flatThornsListener);
			}
			break;
		case THORNSPCT:
			float totalNSThornsPctBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.THORNSPCT);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSThornsPctBuff += buffForKey;
			}
			if (this.percentThornsListener != null) {
				this.percentThornsListener.setAmount(totalNSThornsPctBuff);
			}
			else {
				this.percentThornsListener = new CUnitDefaultThornsListener(true, totalNSThornsPctBuff);
				addDamageTakenListener(this.percentThornsListener);
			}
			break;
		case MAXHPPCT:
		case MAXHP:
			float totalNSMaxHPBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MAXHP);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMaxHPBuff += buffForKey;
			}
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MAXHPPCT);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMaxHPBuff += buffForKey * this.baseMaximumLife;
			}
			final int newMaxLife = this.baseMaximumLife + Math.round(totalNSMaxHPBuff);
			if (newMaxLife > this.maximumLife) {
				this.life += newMaxLife - this.maximumLife;
				this.maximumLife = newMaxLife;
			}
			else {
				this.maximumLife = newMaxLife;
				this.life = Math.min(this.life, this.maximumLife);
			}
			break;
		case MAXMPPCT:
		case MAXMP:
			float totalNSMaxMPBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MAXMP);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMaxMPBuff += buffForKey;
			}
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MAXMPPCT);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					}
					else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						}
						else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMaxMPBuff += buffForKey * this.baseMaximumMana;
			}
			final int newMaxMana = this.baseMaximumMana + Math.round(totalNSMaxMPBuff);
			if (newMaxMana > this.maximumMana) {
				this.mana += newMaxMana - this.maximumMana;
				this.maximumMana = newMaxMana;
			}
			else {
				this.maximumMana = newMaxMana;
				this.mana = Math.min(this.mana, this.maximumMana);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 为指定单位添加一个非堆叠的法术效果
	 *
	 * @param game        游戏模拟对象
	 * @param stackingKey 堆叠键，用于标识法术效果的类型
	 * @param id          法术效果的 ID
	 * @param target      法术效果的目标类型
	 * @return 返回新添加的非堆叠法术效果对象
	 */
	public NonStackingFx addNonStackingFx(final CSimulation game, final String stackingKey, final War3ID id,
										  final CEffectType target) {
		// 获取指定堆叠键的现有法术效果列表
		List<NonStackingFx> existingArts = this.nonStackingFx.get(stackingKey);
		// 创建一个新的非堆叠法术效果对象
		final NonStackingFx newFx = new NonStackingFx(stackingKey, id);
		// 如果现有法术效果列表为空
		if (existingArts == null) {
			// 创建一个新的列表
			existingArts = new ArrayList<>();
			// 将新列表添加到非堆叠法术效果映射中
			this.nonStackingFx.put(stackingKey, existingArts);
		}
		// 如果现有法术效果列表为空
		if (existingArts.isEmpty()) {
			// 在游戏中为单位创建一个持久的法术效果
			final SimulationRenderComponent fx = game.createPersistentSpellEffectOnUnit(this, id, target);
			// 将新创建的法术效果设置为新法术效果对象的艺术效果
			newFx.setArt(fx);
		}
		// 如果现有法术效果列表不为空
		else {
			// 将现有法术效果列表中的第一个法术效果设置为新法术效果对象的艺术效果
			newFx.setArt(existingArts.iterator().next().getArt());
		}
		// 将新法术效果对象添加到现有法术效果列表中
		existingArts.add(newFx);
		// 返回新添加的非堆叠法术效果对象
		return newFx;
	}

	/**
	 * 移除指定单位的非堆叠法术效果
	 *
	 * @param game 游戏模拟对象
	 * @param fx   要移除的非堆叠法术效果对象
	 */
	public void removeNonStackingFx(final CSimulation game, final NonStackingFx fx) {
		// 获取指定堆叠键的现有法术效果列表
		final List<NonStackingFx> existingArts = this.nonStackingFx.get(fx.getStackingKey());
		// 如果现有法术效果列表不为空
		if (existingArts != null) {
			// 从现有法术效果列表中移除指定的法术效果对象
			existingArts.remove(fx);
			// 如果现有法术效果列表为空
			if (existingArts.isEmpty()) {
				// 移除法术效果的艺术表现
				fx.getArt().remove();
			}
		}
	}

	/**
	 * 为指定单位添加一个非堆叠的显示 buff
	 *
	 * @param game        游戏模拟对象
	 * @param stackingKey 堆叠键，用于标识 buff 的类型
	 * @param buff        要添加的 buff 对象
	 */
	public void addNonStackingDisplayBuff(final CSimulation game, final String stackingKey, final CBuff buff) {
		// 获取指定堆叠键的现有 buff 列表
		List<CBuff> existingBuffs = this.nonStackingDisplayBuffs.get(stackingKey);
		// 如果现有 buff 列表为空
		if (existingBuffs == null) {
			// 创建一个新的列表
			existingBuffs = new ArrayList<>();
			// 将新列表添加到非堆叠显示 buff 映射中
			this.nonStackingDisplayBuffs.put(stackingKey, existingBuffs);
		}
		// 如果现有 buff 列表为空
		if (existingBuffs.isEmpty()) {
			// 在游戏中为单位添加 buff
			this.add(game, buff);
		}
		// 如果现有 buff 列表不为空
		else {
			// 获取单位当前的 buff，其类型与要添加的 buff 相同
			final CBuff currentBuff = this.getFirstAbilityOfType(buff.getClass());
			// 如果当前 buff 不存在
			if (currentBuff == null) {
				// 清空现有 buff 列表
				existingBuffs.clear();
				// 在游戏中为单位添加新 buff
				this.add(game, buff);
			}
			// 如果当前 buff 存在
			else {
				// 如果新 buff 的等级大于或等于当前 buff 的等级
				if (buff.getLevel() >= currentBuff.getLevel()) {
					// 从游戏中移除当前 buff
					this.remove(game, currentBuff);
					// 在游戏中添加新 buff
					this.add(game, buff);
				}
			}
		}
		// 将新 buff 添加到现有 buff 列表中
		existingBuffs.add(buff);
	}

	/**
	 * 移除指定单位的非堆叠显示 buff
	 *
	 * @param game        游戏模拟对象
	 * @param stackingKey 堆叠键，用于标识 buff 的类型
	 * @param buff        要移除的 buff 对象
	 */
	public void removeNonStackingDisplayBuff(final CSimulation game, final String stackingKey, final CBuff buff) {
		// 获取指定堆叠键的现有 buff 列表
		final List<CBuff> existingBuffs = this.nonStackingDisplayBuffs.get(stackingKey);
		// 如果现有 buff 列表不为空
		if (existingBuffs != null) {
			// 从现有 buff 列表中移除指定的 buff 对象
			existingBuffs.remove(buff);
			// 获取单位当前的 buff，其类型与要移除的 buff 相同
			CBuff currentBuff = this.getFirstAbilityOfType(buff.getClass());
			// 如果当前 buff 是要移除的 buff
			if (currentBuff == buff) {
				// 从游戏中移除当前 buff
				this.remove(game, currentBuff);
				// 如果现有 buff 列表不为空
				if (!existingBuffs.isEmpty()) {
					currentBuff = null;
					// 遍历现有 buff 列表，找到等级最高的 buff
					for (final CBuff iterBuff : existingBuffs) {
						if ((currentBuff == null) || (currentBuff.getLevel() < iterBuff.getLevel())) {
							currentBuff = iterBuff;
						}
					}
					// 在游戏中添加找到的等级最高的 buff
					this.add(game, currentBuff);
				}
			}
		}
	}


	private void initializeNonStackingBuffs() {
		this.nonStackingBuffs.put(NonStackingStatBuffType.ATKSPD, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.DEF, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.DEFPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.HPGEN, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.HPGENPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MAXHPGENPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.HPSTEAL, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MELEEATK, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MELEEATKPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MPGEN, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MPGENPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MAXMPGENPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MVSPDPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MVSPD, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.RNGDATK, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.RNGDATKPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.THORNS, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.THORNSPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MAXHP, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MAXHPPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MAXMP, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MAXMPPCT, new HashMap<>(1));
	}

	private void initializeListenerLists() {
		for (final CUnitAttackPreDamageListenerPriority priority : CUnitAttackPreDamageListenerPriority.values()) {
			this.preDamageListeners.put(priority, new ArrayList<>());
		}
		for (final CUnitDeathReplacementEffectPriority priority : CUnitDeathReplacementEffectPriority.values()) {
			this.deathReplacementEffects.put(priority, new ArrayList<>());
		}
	}

	private void computeAllDerivedFields() {
		this.baseLifeRegenPerTick = this.lifeRegen * WarsmashConstants.SIMULATION_STEP_TIME;
		computeDerivedFields(NonStackingStatBuffType.DEF);
		computeDerivedFields(NonStackingStatBuffType.HPGEN);
		computeDerivedFields(NonStackingStatBuffType.MPGEN);
		if (getUnitSpecificAttacks() != null) {
			computeDerivedFields(NonStackingStatBuffType.ALLATK);
			computeDerivedFields(NonStackingStatBuffType.ATKSPD);
		}
		computeDerivedFields(NonStackingStatBuffType.HPSTEAL);
		computeDerivedFields(NonStackingStatBuffType.MVSPD);
		computeDerivedFields(NonStackingStatBuffType.THORNS);
		computeDerivedFields(NonStackingStatBuffType.THORNSPCT);
		computeDerivedFields(NonStackingStatBuffType.MAXHP);
		computeDerivedFields(NonStackingStatBuffType.MAXMP);
	}

	private void computeAllUnitStates(final CSimulation game) {
		this.computeUnitState(game, StateModBuffType.INVULNERABLE);
		this.computeUnitState(game, StateModBuffType.ETHEREAL);
		this.computeUnitState(game, StateModBuffType.DISABLE_AUTO_ATTACK);
		this.computeUnitState(game, StateModBuffType.MAGIC_IMMUNE);
		this.computeUnitState(game, StateModBuffType.RESISTANT);
		this.computeUnitState(game, StateModBuffType.SNARED);
	}
	/**
	 * 设置智力对法力回复的加成。
	 * @param manaRegenIntelligenceBonus 智力对法力回复的加成值
	 */
	public void setManaRegenIntelligenceBonus(final float manaRegenIntelligenceBonus) {
		this.manaRegenIntelligenceBonus = manaRegenIntelligenceBonus;
		// 计算派生字段
		computeDerivedFields(NonStackingStatBuffType.MPGEN);
	}

	/**
	 * 设置生命回复加成。
	 * @param lifeRegenBonus 生命回复加成值
	 */
	public void setLifeRegenBonus(final float lifeRegenBonus) {
		this.lifeRegenBonus = lifeRegenBonus;
		// 计算派生字段
		computeDerivedFields(NonStackingStatBuffType.HPGEN);
	}

	/**
	 * 设置法力回复加成。
	 * @param manaRegenBonus 法力回复加成值
	 */
	public void setManaRegenBonus(final float manaRegenBonus) {
		this.manaRegenBonus = manaRegenBonus;
		// 计算派生字段
		computeDerivedFields(NonStackingStatBuffType.MPGEN);
	}

	/**
	 * 设置基础法力回复值。
	 * @param manaRegen 基础法力回复值
	 */
	public void setManaRegen(final float manaRegen) {
		this.manaRegen = manaRegen;
		// 计算派生字段
		computeDerivedFields(NonStackingStatBuffType.MPGEN);
	}

	/**
	 * 获取法力回复加成值。
	 * @return 法力回复加成值
	 */
	public float getManaRegenBonus() {
		return this.manaRegenBonus;
	}

	/**
	 * 获取基础法力回复值。
	 * @return 基础法力回复值
	 */
	public float getManaRegen() {
		return this.manaRegen;
	}

	/**
	 * 设置永久敏捷防御加成。
	 * @param agilityDefensePermanentBonus 永久敏捷防御加成值
	 */
	public void setAgilityDefensePermanentBonus(final int agilityDefensePermanentBonus) {
		this.agilityDefensePermanentBonus = agilityDefensePermanentBonus;
		// 计算派生字段
		computeDerivedFields(NonStackingStatBuffType.DEF);
	}

	/**
	 * 设置临时敏捷防御加成。
	 * @param agilityDefenseTemporaryBonus 临时敏捷防御加成值
	 */
	public void setAgilityDefenseTemporaryBonus(final float agilityDefenseTemporaryBonus) {
		this.agilityDefenseTemporaryBonus = agilityDefenseTemporaryBonus;
		// 计算派生字段
		computeDerivedFields(NonStackingStatBuffType.DEF);
	}

	/**
	 * 设置永久防御加成。
	 * @param permanentDefenseBonus 永久防御加成值
	 */
	public void setPermanentDefenseBonus(final int permanentDefenseBonus) {
		this.permanentDefenseBonus = permanentDefenseBonus;
		// 计算派生字段
		computeDerivedFields(NonStackingStatBuffType.DEF);
	}

	/**
	 * 获取永久防御加成。
	 * @return 永久防御加成值
	 */
	public int getPermanentDefenseBonus() {
		return this.permanentDefenseBonus;
	}

	/**
	 * 设置临时防御加成。
	 * @param temporaryDefenseBonus 临时防御加成值
	 */
	public void setTemporaryDefenseBonus(final float temporaryDefenseBonus) {
		this.temporaryDefenseBonus = temporaryDefenseBonus;
		// 计算派生字段
		computeDerivedFields(NonStackingStatBuffType.DEF);
	}

	/**
	 * 获取临时防御加成。
	 * @return 临时防御加成值
	 */
	public float getTemporaryDefenseBonus() {
		return this.temporaryDefenseBonus;
	}

	/**
	 * 获取总临时防御加成。
	 * @return 总临时防御加成值
	 */
	public float getTotalTemporaryDefenseBonus() {
		return this.totalTemporaryDefenseBonus;
	}

	/**
	 * 获取当前防御显示值。
	 * @return 当前防御显示值
	 */
	public int getCurrentDefenseDisplay() {
		return this.currentDefenseDisplay;
	}

	/**
	 * 设置单位动画监听器。
	 * @param unitAnimationListener 单位动画监听器
	 */
	public void setUnitAnimationListener(final CUnitAnimationListener unitAnimationListener) {
		this.unitAnimationListener = unitAnimationListener;
		// 播放动画
		this.unitAnimationListener.playAnimation(true, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f, true);
	}

	/**
	 * 获取单位动画监听器。
	 * @return 单位动画监听器
	 */
	public CUnitAnimationListener getUnitAnimationListener() {
		return this.unitAnimationListener;
	}

	public void add(final CSimulation simulation, final CAbility ability) {
		 // 检查能力是否满足要求，如果不满足，则禁用该能力
		 if (!ability.isRequirementsMet(simulation, this)) {
			 ability.setDisabled(true, CAbilityDisableType.REQUIREMENTS);
		 }
		 // 如果能力被禁用
		 if (ability.isDisabled()) {
			 // 将禁用的能力添加到禁用列表
			 this.disabledAbilities.add(ability);
			 // 将能力添加到总的能力列表
			 this.abilities.add(ability);
			 // 通知模拟对象有新能力被添加到单位
			 simulation.onAbilityAddedToUnit(this, ability);
			 // 调用能力的禁用添加回调
			 ability.onAddDisabled(simulation, this);
			 // 通知状态变化
			 this.stateNotifier.abilitiesChanged();
		 } else {
			 // 如果能力未被禁用，直接添加到总的能力列表
			 this.abilities.add(ability);
			 // 通知模拟对象有新能力被添加到单位
			 simulation.onAbilityAddedToUnit(this, ability);
			 // 调用能力的添加回调
			 ability.onAddDisabled(simulation, this);
			 // 调用能力的正常添加回调
			 ability.onAdd(simulation, this);
			 // 通知状态变化
			 this.stateNotifier.abilitiesChanged();
		 }
	}

	/**
	 * 向单位添加能力（Buff）。
	 * 如果该能力已存在且是定时能力，则更新其过期时间。
	 * 否则，添加新能力，并通知模拟器和能力本身。
	 *
	 * @param simulation 当前模拟环境
	 * @param ability    要添加的能力（Buff）
	 */
	public void add(final CSimulation simulation, final CBuff ability) {
		// 如果能力已存在且是定时能力类型
		if (this.abilities.contains(ability) && (ability instanceof ABGenericTimedBuff)) {
			// 更新定时能力的过期时间
			((ABGenericTimedBuff) ability).updateExpiration(simulation, this);
		} else {
			// 添加新能力
			this.abilities.add(ability);
			// 通知模拟器有新能力被添加到单位
			simulation.onAbilityAddedToUnit(this, ability);
			// 通知能力本身被添加
			ability.onAdd(simulation, this);
			// 通知状态变化
			this.stateNotifier.abilitiesChanged();
		}
	}


	/**
	  * 移除一个能力（CAbility）从单位中。
	  * 如果这个能力已经被禁用，则同时从禁用能力列表中移除，并通知模拟器和能力对象。
	  * 如果这个能力没有被禁用，则只从能力列表中移除，并通知模拟器和能力对象。
	  *
	  * @param simulation 当前的模拟器实例
	  * @param ability 要移除的能力对象
	  */
	 public void remove(final CSimulation simulation, final CAbility ability) {
		if (this.disabledAbilities.contains(ability)) {
			this.abilities.remove(ability); // 从能力列表中移除
			this.disabledAbilities.remove(ability); // 从禁用能力列表中移除
			simulation.onAbilityRemovedFromUnit(this, ability); // 通知模拟器能力被移除
			ability.onRemoveDisabled(simulation, this); // 通知能力对象它已被禁用并移除
			this.stateNotifier.abilitiesChanged(); // 通知状态变化
		}
		else {
			this.abilities.remove(ability); // 从能力列表中移除
			simulation.onAbilityRemovedFromUnit(this, ability); // 通知模拟器能力被移除
			ability.onRemove(simulation, this); // 通知能力对象它已被移除
			ability.onRemoveDisabled(simulation, this); // 通知能力对象它已被禁用
			this.stateNotifier.abilitiesChanged(); // 通知状态变化
		}
	 }

	 /**
	  * 移除一个增益效果（CBuff）从单位中。
	  * 只从能力列表中移除，并通知模拟器和增益效果对象。
	  *
	  * @param simulation 当前的模拟器实例
	  * @param ability 要移除的增益效果对象
	  */
	 public void remove(final CSimulation simulation, final CBuff ability) {
		this.abilities.remove(ability); // 从能力列表中移除
		simulation.onAbilityRemovedFromUnit(this, ability); // 通知模拟器增益效果被移除
		ability.onRemove(simulation, this); // 通知增益效果对象它已被移除
		this.stateNotifier.abilitiesChanged(); // 通知状态变化
	 }

	/**
	 * 检查并更新能力的禁用状态。
	 *
	 * @param simulation 当前的模拟环境。
	 * @param disable     是否禁用能力。
	 */
	public void checkDisabledAbilities(final CSimulation simulation, final boolean disable) {
		if (disable) {
			// 遍历所有能力
			for (final CAbility ability : this.abilities) {
				// 如果能力的要求未被满足，则禁用该能力
				if (!ability.isRequirementsMet(simulation, this)) {
					ability.setDisabled(true, CAbilityDisableType.REQUIREMENTS);
				}
				// 如果能力已被禁用且未被记录在禁用能力列表中，则进行记录并调用onRemove方法
				if (ability.isDisabled() && !this.disabledAbilities.contains(ability)) {
					// System.err.println("Disabling ability: " + ability.getAlias().asStringValue());
					this.disabledAbilities.add(ability);
					ability.onRemove(simulation, this);
				}
			}
		} else {
			// 创建禁用能力列表的副本，以防在迭代过程中修改原列表
			for (final CAbility ability : new ArrayList<>(this.disabledAbilities)) {
				// 如果能力的要求已被满足，则启用该能力
				if (ability.isRequirementsMet(simulation, this)) {
					ability.setDisabled(false, CAbilityDisableType.REQUIREMENTS);
				}
				// 如果能力未被禁用，则调用onAdd方法并从禁用能力列表中移除
				if (!ability.isDisabled()) {
					ability.onAdd(simulation, this);
					this.disabledAbilities.remove(ability);
				}
			}
		}
	}


	public War3ID getTypeId() {
		return this.typeId;
	}

	/**
	 * @return facing in DEGREES
	 */
	public float getFacing() {
		return this.facing;
	}
	// 获取当前法力值
	public float getMana() {
		return this.mana;
	}

	// 获取最大法力值
	public int getMaximumMana() {
		return this.maximumMana;
	}

	// 获取最大生命值
	public int getMaximumLife() {
		return this.maximumLife;
	}


	public void setTypeId(final CSimulation game, final War3ID typeId) {
		setTypeId(game, typeId, true);
	}

	public void setTypeId(final CSimulation game, final War3ID typeId, final boolean updateArt) {
		game.getWorldCollision().removeUnit(this);
		final CPlayer player = game.getPlayer(this.playerIndex);
		player.removeTechtreeUnlocked(game, this.typeId);
		this.typeId = typeId;
		player.addTechtreeUnlocked(game, this.typeId);
		final float lifeRatio = this.maximumLife == 0 ? 1 : this.life / this.maximumLife;
		final float manaRatio = this.maximumMana == 0 ? Float.NaN : this.mana / this.maximumMana;
		final CUnitType previousUnitType = getUnitType();
		this.unitType = game.getUnitData().getUnitType(typeId);
		this.maximumMana = this.unitType.getManaMaximum();
		this.maximumLife = this.unitType.getMaxLife();
		this.life = lifeRatio * this.maximumLife;
		this.lifeRegen = this.unitType.getLifeRegen();
		this.manaRegen = this.unitType.getManaRegen();
		if (updateArt) {
			this.flyHeight = this.unitType.getDefaultFlyingHeight();
		}
		this.speed = this.unitType.getSpeed();
		this.classifications.clear();
		this.classifications.addAll(this.unitType.getClassifications());
		this.defenseType = this.unitType.getDefenseType();
		this.acquisitionRange = this.unitType.getDefaultAcquisitionRange();
		this.structure = this.unitType.isBuilding();
		this.raisable = this.unitType.isRaise();
		this.decays = this.unitType.isDecay();
		final List<War3ID> sharedAbilities = new ArrayList<War3ID>(previousUnitType.getAbilityList());
		sharedAbilities.addAll(previousUnitType.getHeroAbilityList());
		final List<War3ID> newIds = new ArrayList<War3ID>(this.unitType.getAbilityList());
		newIds.addAll(this.unitType.getHeroAbilityList());
		sharedAbilities.retainAll(newIds); // TODO Seems wasteful, but need to avoid messing up heros on transform
		final List<CAbility> persistedAbilities = new ArrayList<>();
		final List<CAbility> removedAbilities = new ArrayList<>();
		for (final CAbility ability : this.abilities) {
			if (!ability.isPermanent() && !sharedAbilities.contains(ability.getAlias())
					&& !(ability.getAbilityCategory() == CAbilityCategory.BUFF)) {
				ability.onRemove(game, this);
				game.onAbilityRemovedFromUnit(this, ability);
				removedAbilities.add(ability);
			}
			else {
				persistedAbilities.add(ability);
			}
		}
		for (final CAbility removed : removedAbilities) {
			this.abilities.remove(removed); // TODO remove inefficient O(N) search
		}
		game.unitUpdatedType(this, typeId);
		game.getUnitData().addMissingDefaultAbilitiesToUnit(game, game.getHandleIdAllocator(), this.unitType, false, -1,
				this.speed, this);
		{
			// Remove and add the persisted abilities, so that some stuff like move and
			// attack are "first" in the end resulting list. This is "dumb" and a better
			// design later might be worth considering; it fixes a bug where otherwise
			// `Chaos` used on a worker into another near identical worker was changing the
			// ability order used by "smart" such that the unit would prefer Attack over
			// Harvest
			for (final CAbility persisted : persistedAbilities) {
				this.abilities.remove(persisted);
			}
			for (final CAbility persisted : persistedAbilities) {
				this.abilities.add(persisted);
			}
		}
		if (Float.isNaN(manaRatio)) {
			this.mana = this.unitType.getManaInitial();
		}
		else {
			this.mana = manaRatio * this.maximumMana;
		}
		game.getWorldCollision().addUnit(this);
		for (final CAbility ability : persistedAbilities) {
			ability.onSetUnitType(game, this);
			game.onAbilityAddedToUnit(this, ability);
		}
		computeAllDerivedFields();
		computeAllUnitStates(game);
	}

	public void setFacing(final float facing) {
		// java modulo output can be negative, but not if we
		// force positive and modulo again
		this.facing = ((facing % 360) + 360) % 360;
	}

	// 设置魔法值
	public void setMana(final float mana) {
		this.mana = mana;
		this.stateNotifier.manaChanged();
	}
	// 设置最大生命值
	public void setMaximumLife(final int maximumLife) {
		this.baseMaximumLife = maximumLife;
		computeDerivedFields(NonStackingStatBuffType.MAXHPPCT);
		computeDerivedFields(NonStackingStatBuffType.MAXHPGENPCT);
	}

	// 添加相对最大生命值
	public void addMaxLifeRelative(final CSimulation game, final int hitPointBonus) {
		final int oldMaximumLife = getMaximumLife();
		final float oldLife = getLife();
		final int newMaximumLife = oldMaximumLife + hitPointBonus;
		final float newLife = (oldLife * newMaximumLife) / oldMaximumLife;
		setMaximumLife(newMaximumLife);
		setLife(game, newLife);
	}

	// 设置最大魔法值
	public void setMaximumMana(final int maximumMana) {
		this.baseMaximumMana = maximumMana;
		computeDerivedFields(NonStackingStatBuffType.MAXMPPCT);
		computeDerivedFields(NonStackingStatBuffType.MAXMPGENPCT);
	}

	// 设置速度
	public void setSpeed(final int speed) {
		this.speed = speed;
		computeDerivedFields(NonStackingStatBuffType.MVSPD);
	}

	// 获取速度
	public int getSpeed() {
		return this.speed + this.speedBonus;
	}


	/**
	 * Updates one tick of simulation logic and return true if it's time to remove
	 * this unit from the game.
	 */
	public boolean update(final CSimulation game) {
		// 遍历所有的状态监听器更新
		for (final StateListenerUpdate update : this.stateListenersUpdates) {
			// 根据更新的类型执行不同的操作
			switch (update.getUpdateType()) {
			  case ADD:
				  // 如果是添加操作，则订阅监听器
				  /**
				   * 订阅一个新的状态监听器。
				   * @param listener 要订阅的状态监听器
				   */
				  this.stateNotifier.subscribe(update.listener);
				  break;
			  case REMOVE:
				  // 如果是移除操作，则取消订阅监听器
				  /**
				   * 取消订阅一个已有的状态监听器。
				   * @param listener 要取消订阅的状态监听器
				   */
				  this.stateNotifier.unsubscribe(update.listener);
				  break;
			}
		}
		// 清除状态监听器更新
		this.stateListenersUpdates.clear();

		// 如果单位已死亡
		if (isDead()) {
			// 如果不是假死亡
			if (!this.falseDeath) {
				// 获取当前游戏回合数
				final int gameTurnTick = game.getGameTurnTick();

				// 如果单位还没有变成尸体
				if (!this.corpse) {
					// 如果有碰撞矩形，从世界碰撞系统中移除单位，避免在迭代时写入导致的问题
					if (this.collisionRectangle != null) {
						game.getWorldCollision().removeUnit(this);
					}

					// 如果当前回合数大于死亡回合数加上死亡时间
					if (gameTurnTick > (this.deathTurnTick
							+ (int) (this.unitType.getDeathTime() / WarsmashConstants.SIMULATION_STEP_TIME))) {
						// 单位变成尸体
						this.corpse = true;

						// 如果单位不能复活，变成骨头尸体并立即开始最终阶段
						if (!isRaisable()) {
							this.boneCorpse = true;
						}

						// 如果不是英雄单位，并且不会腐烂，死亡动画结束后删除单位
						if (!this.unitType.isHero() && !isDecays()) {
							return true;
						}

						// 如果是英雄单位，触发英雄死亡事件
						else {
							game.heroDeathEvent(this);
						}

						// 更新死亡回合数
						this.deathTurnTick = gameTurnTick;
					}
				}
				// 如果单位已经是尸体但不是骨头尸体
				else if (!this.boneCorpse) {
					// 如果当前回合数大于死亡回合数加上腐烂时间
					if (game.getGameTurnTick() > (this.deathTurnTick + (int) (game.getGameplayConstants().getDecayTime()
							/ WarsmashConstants.SIMULATION_STEP_TIME))) {
						// 单位变成骨头尸体
						this.boneCorpse = true;
						// 更新死亡回合数

						this.deathTurnTick = gameTurnTick;

						// 如果单位可以复活，重新添加到世界碰撞系统中
						if (isRaisable()) {
							game.getWorldCollision().addUnit(this);
						}
					}
				}
				// 如果单位已经是骨头尸体，并且当前回合数大于死亡回合数加上结束腐烂时间
				else if (game.getGameTurnTick() > (this.deathTurnTick
						+ (int) (getEndingDecayTime(game) / WarsmashConstants.SIMULATION_STEP_TIME))) {
					// 如果是英雄单位，并且没有等待复活，隐藏单位，设置等待复活状态，并触发英雄消散事件
					if (this.unitType.isHero() && !getHeroData().isAwaitingRevive()) {
						setHidden(true);
						getHeroData().setAwaitingRevive(true);
						game.heroDissipateEvent(this);
					}
					// 返回false表示单位应该被移除
					return false;
				}
			}
		}
		else {
			// 如果单位没有暂停
			if (!this.paused) {
				// 检查当前对象的集结点是否不是自身，并且集结点是一个CUnit实例
				// 同时，检查该集结点是否已经死亡
				if ((this.rallyPoint != this) && (this.rallyPoint instanceof CUnit)
					// 如果条件满足，则将当前对象设置为其自身的集结点
					&& ((CUnit) this.rallyPoint).isDead()) {
					// 设置集结点为自身
					setRallyPoint(this);
				}
				// 如果正在建造中
				if (this.constructing) {
					// 如果没有暂停建造，则增加建造进度
					if (!this.constructingPaused) {
						this.constructionProgress += WarsmashConstants.SIMULATION_STEP_TIME;
					}
					// 定义建造时间变量
					final int buildTime;
					// // 判断是否在升级中
					final boolean upgrading = isUpgrading();
					if (!upgrading) {
						// 如果不是升级，则获取当前单位的建造时间
						buildTime = this.unitType.getBuildTime();
						// 如果没有暂停建造，则根据建造进度增加生命值
						if (!this.constructingPaused) {
							final float healthGain = (WarsmashConstants.SIMULATION_STEP_TIME / buildTime)
									* (this.maximumLife * (1.0f - WarsmashConstants.BUILDING_CONSTRUCT_START_LIFE));
							setLife(game, Math.min(this.life + healthGain, this.maximumLife));
						}
					}
					else {
						// 如果是升级，则获取升级后的单位的建造时间
						buildTime = game.getUnitData().getUnitType(this.upgradeIdType).getBuildTime();
					}
					// 如果建造进度达到或超过建造时间，则完成建造或升级
					if (this.constructionProgress >= buildTime) {
						// 重置建造相关状态
						this.constructing = false;
						this.constructingPaused = false;
						this.constructionProgress = 0;
						// 如果建造消耗工人，则移除工人
						if (this.constructionConsumesWorker) {
							if (this.workerInside != null) {
								game.removeUnit(this.workerInside);
								this.workerInside = null;
							}
						}
						else {
							// 否则弹出工人
							popoutWorker(game);
						}
						// 更新能力状态
						final Iterator<CAbility> abilityIterator = this.abilities.iterator();
						// 遍历能力迭代器中的所有能力
						while (abilityIterator.hasNext()) {
							  // 获取当前能力对象
							  final CAbility ability = abilityIterator.next();
							  // 判断当前能力是否是正在构建中的能力
							  if (ability instanceof CAbilityBuildInProgress) {
								  // 如果是，则从迭代器中移除该能力
								  abilityIterator.remove();
							  } else {
								  // 如果不是，则启用该能力，并设置禁用类型为CONSTRUCTION
								  ability.setDisabled(false, CAbilityDisableType.CONSTRUCTION);
								  // 设置能力的图标显示为true，即显示图标
								  ability.setIconShowing(true);
							  }
						}

						// 检查游戏中禁用的能力
						checkDisabledAbilities(game, false);

						// 获取当前玩家对象
						final CPlayer player = game.getPlayer(this.playerIndex);

						// 如果正在升级
						if (upgrading) {
							  // 如果单位类型制作的食物不为0
							  if (this.unitType.getFoodMade() != 0) {
								  // 减少玩家的食物上限
								  player.setFoodCap(player.getFoodCap() - this.unitType.getFoodMade());
							  }
							  // 设置新的单位类型ID
							  setTypeId(game, this.upgradeIdType);
							  // 清空升级ID类型
							  this.upgradeIdType = null;
						}

						// 如果单位类型制作的食物不为0
						if (this.unitType.getFoodMade() != 0) {
							  // 增加玩家的食物上限
							  player.setFoodCap(player.getFoodCap() + this.unitType.getFoodMade());
						}

						// 移除玩家正在进行的科技树进度
						player.removeTechtreeInProgress(this.unitType.getTypeId());
						// 添加玩家解锁的科技树节点
						player.addTechtreeUnlocked(game, this.unitType.getTypeId());

						// 如果不是升级
						if (!upgrading) {
							  // 触发单位建造完成事件
							  game.unitConstructFinishEvent(this);
							  // 触发建造完成事件
							  fireConstructFinishEvents(game);
						} else {
							  // 触发单位升级完成事件
							  game.unitUpgradeFinishEvent(this);
						}

						// 如果正在升级或者总是执行
						if (upgrading || true) {
							  // 播放单位站立动画
							  getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f, true);
						}

						// 通知状态变化
						this.stateNotifier.ordersChanged();

					}
				}
				else {
					final War3ID queuedRawcode = this.buildQueue[0];
					if (queuedRawcode != null) {
						// queue step forward
						if (this.queuedUnitFoodPaid) {
							this.constructionProgress += WarsmashConstants.SIMULATION_STEP_TIME;
						}
						else {
							if (this.buildQueueTypes[0] == QueueItemType.UNIT) {
								final CPlayer player = game.getPlayer(this.playerIndex);
								final CUnitType trainedUnitType = game.getUnitData().getUnitType(queuedRawcode);
								if (trainedUnitType.getFoodUsed() != 0) {
									final int newFoodUsed = player.getFoodUsed() + trainedUnitType.getFoodUsed();
									if (newFoodUsed <= player.getFoodCap()) {
										player.setFoodUsed(newFoodUsed);
										this.queuedUnitFoodPaid = true;
									}
								}
								else {
									this.queuedUnitFoodPaid = true;
								}
							}
							else if (this.buildQueueTypes[0] == QueueItemType.SACRIFICE) {
								this.queuedUnitFoodPaid = true;
							}
							else if (this.buildQueueTypes[0] == QueueItemType.HERO_REVIVE) {
								final CPlayer player = game.getPlayer(this.playerIndex);
								final CUnitType trainedUnitType = game.getUnit(queuedRawcode.getValue()).getUnitType();
								final int newFoodUsed = player.getFoodUsed() + trainedUnitType.getFoodUsed();
								if (newFoodUsed <= player.getFoodCap()) {
									player.setFoodUsed(newFoodUsed);
									this.queuedUnitFoodPaid = true;
								}
							}
							else {
								this.queuedUnitFoodPaid = true;
								System.err.println(
										"Unpaid food for non unit queue item ???? Attempting to correct this by setting paid=true");
							}
						}
						if (this.buildQueueTypes[0] == QueueItemType.UNIT) {
							final CUnitType trainedUnitType = game.getUnitData().getUnitType(queuedRawcode);
							if (this.constructionProgress >= trainedUnitType.getBuildTime()) {
								this.constructionProgress = 0;
								final CUnit trainedUnit = game.createUnit(queuedRawcode, this.playerIndex, getX(),
										getY(), game.getGameplayConstants().getBuildingAngle());
								// dont add food cost to player 2x
								trainedUnit.setFoodUsed(trainedUnitType.getFoodUsed());
								final CPlayer player = game.getPlayer(this.playerIndex);
								player.setUnitFoodMade(trainedUnit, trainedUnitType.getFoodMade());
								player.removeTechtreeInProgress(queuedRawcode);
								player.addTechtreeUnlocked(game, queuedRawcode);
								fireTrainFinishEvents(game, trainedUnit);
								// nudge the trained unit out around us
								trainedUnit.nudgeAround(game, this);
								game.unitTrainedEvent(this, trainedUnit);
								if (this.rallyPoint != null) {
									final int rallyOrderId = OrderIds.smart;
									this.rallyPoint.visit(UseAbilityOnTargetByIdVisitor.INSTANCE.reset(game,
											trainedUnit, rallyOrderId));
								}
								for (int i = 0; i < (this.buildQueue.length - 1); i++) {
									setBuildQueueItem(game, i, this.buildQueue[i + 1], this.buildQueueTypes[i + 1]);
								}
								setBuildQueueItem(game, this.buildQueue.length - 1, null, null);
								this.stateNotifier.queueChanged();
							}
						}
						else if (this.buildQueueTypes[0] == QueueItemType.SACRIFICE) {
							final CUnitType trainedUnitType = game.getUnitData().getUnitType(queuedRawcode);
							if (this.constructionProgress >= trainedUnitType.getBuildTime()) {
								this.constructionProgress = 0;
								final CUnit trainedUnit = game.createUnit(queuedRawcode, this.playerIndex, getX(),
										getY(), game.getGameplayConstants().getBuildingAngle());

								game.removeUnit(getWorkerInside());

								// dont add food cost to player 2x
								trainedUnit.setFoodUsed(trainedUnitType.getFoodUsed());
								final CPlayer player = game.getPlayer(this.playerIndex);
								player.setUnitFoodMade(trainedUnit, trainedUnitType.getFoodMade());
								player.removeTechtreeInProgress(queuedRawcode);
								player.addTechtreeUnlocked(game, queuedRawcode);
								fireTrainFinishEvents(game, trainedUnit);
								// nudge the trained unit out around us
								trainedUnit.nudgeAround(game, this);
								game.unitTrainedEvent(this, trainedUnit);
								if (this.rallyPoint != null) {
									final int rallyOrderId = OrderIds.smart;
									this.rallyPoint.visit(UseAbilityOnTargetByIdVisitor.INSTANCE.reset(game,
											trainedUnit, rallyOrderId));
								}
								for (int i = 0; i < (this.buildQueue.length - 1); i++) {
									setBuildQueueItem(game, i, this.buildQueue[i + 1], this.buildQueueTypes[i + 1]);
								}
								setBuildQueueItem(game, this.buildQueue.length - 1, null, null);
								this.stateNotifier.queueChanged();
							}
						}
						else if (this.buildQueueTypes[0] == QueueItemType.HERO_REVIVE) {
							final CUnit revivingHero = game.getUnit(queuedRawcode.getValue());
							final CUnitType trainedUnitType = revivingHero.getUnitType();
							final CGameplayConstants gameplayConstants = game.getGameplayConstants();
							if (this.constructionProgress >= gameplayConstants.getHeroReviveTime(
									trainedUnitType.getBuildTime(), revivingHero.getHeroData().getHeroLevel())) {
								this.constructionProgress = 0;
								revivingHero.getHeroData().setReviving(false);
								revivingHero.getHeroData().setAwaitingRevive(false);
								revivingHero.corpse = false;
								revivingHero.boneCorpse = false;
								revivingHero.deathTurnTick = 0;
								revivingHero.setX(getX());
								revivingHero.setY(getY());
								game.getWorldCollision().addUnit(revivingHero);
								revivingHero.setPoint(getX(), getY(), game.getWorldCollision(),
										game.getRegionManager());
								revivingHero.setHidden(false);
								revivingHero.setLife(game,
										revivingHero.getMaximumLife() * gameplayConstants.getHeroReviveLifeFactor());
								revivingHero.setMana(
										(revivingHero.getMaximumMana() * gameplayConstants.getHeroReviveManaFactor())
												+ (gameplayConstants.getHeroReviveManaStart()
														* trainedUnitType.getManaInitial()));
								// dont add food cost to player 2x
								revivingHero.setFoodUsed(trainedUnitType.getFoodUsed());
								final CPlayer player = game.getPlayer(this.playerIndex);
								player.setUnitFoodMade(revivingHero, trainedUnitType.getFoodMade());
								// NOTE: Dont "add techtree unlocked" here, because hero doesn't lose that
								// status upon death
								// nudge the trained unit out around us
								revivingHero.nudgeAround(game, this);
								game.unitRepositioned(revivingHero); // dont blend animation
								game.heroReviveEvent(this, revivingHero);
								if (this.rallyPoint != null) {
									final int rallyOrderId = OrderIds.smart;
									this.rallyPoint.visit(UseAbilityOnTargetByIdVisitor.INSTANCE.reset(game,
											revivingHero, rallyOrderId));
								}
								for (int i = 0; i < (this.buildQueue.length - 1); i++) {
									setBuildQueueItem(game, i, this.buildQueue[i + 1], this.buildQueueTypes[i + 1]);
								}
								setBuildQueueItem(game, this.buildQueue.length - 1, null, null);
								this.stateNotifier.queueChanged();
							}
						}
						else if (this.buildQueueTypes[0] == QueueItemType.RESEARCH) {
							final CUpgradeType trainedUnitType = game.getUpgradeData().getType(queuedRawcode);
							// TODO the "getBuildTime" math below probably would be better served to have
							// been cached, for performance, since we are in the update method. But maybe it
							// doens't matter.
							final CPlayer player = game.getPlayer(this.playerIndex);
							final int techtreeUnlocked = player.getTechtreeUnlocked(queuedRawcode);
							if (this.constructionProgress >= trainedUnitType.getBuildTime(techtreeUnlocked)) {
								this.constructionProgress = 0;
								for (int i = 0; i < (this.buildQueue.length - 1); i++) {
									setBuildQueueItem(game, i, this.buildQueue[i + 1], this.buildQueueTypes[i + 1]);
								}
								setBuildQueueItem(game, this.buildQueue.length - 1, null, null);
								player.removeTechtreeInProgress(queuedRawcode);
								player.addTechResearched(game, queuedRawcode, 1);
								fireResearchFinishEvents(game, queuedRawcode);
								game.researchFinishEvent(this, queuedRawcode,
										player.getTechtreeUnlocked(queuedRawcode));
								this.stateNotifier.queueChanged();
							}
						}
					}
					if ((this.life < this.maximumLife) || (this.currentLifeRegenPerTick < 0)) {
						final CRegenType lifeRegenType = getUnitType().getLifeRegenType();
						boolean active = false;
						switch (lifeRegenType) {
						case ALWAYS:
							active = true;
							break;
						case DAY:
							active = game.isDay();
							break;
						case NIGHT:
							active = game.isNight();
							break;
						case BLIGHT:
							active = PathingFlags.isPathingFlag(game.getPathingGrid().getPathing(getX(), getY()),
									PathingFlags.BLIGHTED);
							break;
						default:
							active = false;
						}
						if (active) {
							float lifePlusRegen = this.life + this.currentLifeRegenPerTick;
							if (lifePlusRegen > this.maximumLife) {
								lifePlusRegen = this.maximumLife;
							}
							this.life = lifePlusRegen;
							this.stateNotifier.lifeChanged();
						}
						else {
							float lifePlusRegen = (this.life + this.currentLifeRegenPerTick)
									- this.baseLifeRegenPerTick;
							if (lifePlusRegen > this.maximumLife) {
								lifePlusRegen = this.maximumLife;
							}
							this.life = lifePlusRegen;
							this.stateNotifier.lifeChanged();
						}
					}
					if ((this.mana < this.maximumMana) || (this.currentManaRegenPerTick < 0)) {
						float manaPlusRegen = this.mana + this.currentManaRegenPerTick;
						if (manaPlusRegen > this.maximumMana) {
							manaPlusRegen = this.maximumMana;
						}
						this.mana = Math.max(manaPlusRegen, 0);
						this.stateNotifier.manaChanged();
					}
					if (this.currentBehavior != null) {
						// 获取当前行为 的目标单位
						final CUnit target = this.currentBehavior.visit(BehaviorTargetUnitVisitor.INSTANCE);
						// 如果目标不为空且当前玩家与目标玩家没有共享视野联盟
						if ((target != null) && !game.getPlayer(this.playerIndex).hasAlliance(target.getPlayerIndex(), CAllianceType.SHARED_VISION)) {
							// 如果攻击雾效果影响的玩家索引不等于目标玩家索引
							if (this.attackFogMod.getPlayerIndex() != target.getPlayerIndex()) {
								// 移除当前攻击雾效果影响的玩家的雾效果
								game.getPlayer(this.attackFogMod.getPlayerIndex()).removeFogModifer(game, this.attackFogMod);
								// 设置攻击雾效果影响的玩家索引为目标玩家索引
								this.attackFogMod.setPlayerIndex(target.getPlayerIndex());
								// 给目标玩家添加雾效果
								game.getPlayer(target.getPlayerIndex()).addFogModifer(game, this.attackFogMod);
							}
						} else {
							// 如果攻击雾效果影响的玩家索引不等于当前玩家索引
							if (this.attackFogMod.getPlayerIndex() != this.playerIndex) {
								// 移除当前攻击雾效果影响的玩家的雾效果
								game.getPlayer(this.attackFogMod.getPlayerIndex()).removeFogModifer(game, this.attackFogMod);
								// 设置攻击雾效果影响的玩家索引为当前玩家索引
								this.attackFogMod.setPlayerIndex(this.playerIndex);
							}
						}


						final CBehavior lastBehavior = this.currentBehavior;
						final int lastBehaviorHighlightOrderId = lastBehavior.getHighlightOrderId();
						this.currentBehavior = this.currentBehavior.update(game);
						if (lastBehavior != this.currentBehavior) {
							lastBehavior.end(game, false);
							if (this.currentBehavior != null) {
								this.currentBehavior.begin(game);
							}
						}
						if ((this.currentBehavior != null)
								&& (this.currentBehavior.getHighlightOrderId() != lastBehaviorHighlightOrderId)) {
							this.stateNotifier.ordersChanged();
						}
					}
					else {
						// 检测自动施法技能自动施法
						// check to auto acquire targets
						autoAcquireTargets(game, this.moveDisabled);
					}
//					for (CAbility ability : new ArrayList<>(this.abilities)) {
//						ability.onTick(game, this);
//					}
					for (int i = this.abilities.size() - 1; i >= 0; i--) {
						// okay if it removes self from this during onTick() because of reverse
						// iteration order
						this.abilities.get(i).onTick(game, this);
					}
				}
			}
			else if (!this.constructing) {
				// Paused units only allow passives to function. Buffs don't tick (except a few)
				// Base and bonus life/mana regen function, but regen from Str/Int doesn't
				if ((this.life < this.maximumLife)
						|| ((this.currentLifeRegenPerTick - this.lifeRegenStrengthBonus) < 0)) {
					final CRegenType lifeRegenType = getUnitType().getLifeRegenType();
					boolean active = false;
					switch (lifeRegenType) {
					case ALWAYS:
						active = true;
						break;
					case DAY:
						active = game.isDay();
						break;
					case NIGHT:
						active = game.isNight();
						break;
					case BLIGHT:
						active = PathingFlags.isPathingFlag(game.getPathingGrid().getPathing(getX(), getY()),
								PathingFlags.BLIGHTED);
						break;
					default:
						active = false;
					}
					if (active) {
						float lifePlusRegen = (this.life + this.currentLifeRegenPerTick)
								- (this.lifeRegenStrengthBonus * WarsmashConstants.SIMULATION_STEP_TIME);
						if (lifePlusRegen > this.maximumLife) {
							lifePlusRegen = this.maximumLife;
						}
						this.life = lifePlusRegen;
						this.stateNotifier.lifeChanged();
					}
				}
				if ((this.mana < this.maximumMana)
						|| ((this.currentManaRegenPerTick - this.manaRegenIntelligenceBonus) < 0)) {
					float manaPlusRegen = (this.mana + this.currentManaRegenPerTick)
							- (this.manaRegenIntelligenceBonus * WarsmashConstants.SIMULATION_STEP_TIME);
					if (manaPlusRegen > this.maximumMana) {
						manaPlusRegen = this.maximumMana;
					}
					this.mana = Math.max(manaPlusRegen, 0);
					this.stateNotifier.manaChanged();
				}

				for (int i = this.abilities.size() - 1; i >= 0; i--) {
					// okay if it removes self from this during onTick() because of reverse
					// iteration order
					if ((this.abilities.get(i) instanceof AbilityGenericSingleIconPassiveAbility)
							|| (this.abilities.get(i) instanceof ABTimedTickingPausedBuff)) {
						this.abilities.get(i).onTick(game, this);
					}
				}
			}
		}
		return false;

	}

	 /**
	  * 将工人从建筑中移出，并恢复其正常状态。
	  * 如果建筑消耗工人，则更新玩家的食物使用量。
	  *
	  * @param game 当前的游戏模拟对象
	  */
	 private void popoutWorker(final CSimulation game) {
		 // 检查是否有工人在建筑内
		 if (this.workerInside != null) {
			 // 恢复工人的状态
			 this.workerInside.setInvulnerable(false); // 设置工人可受伤
			 this.workerInside.setHidden(false); // 设置工人可见
			 this.workerInside.setPaused(false); // 设置工人不暂停
			 // 让工人在游戏中移动
			 this.workerInside.nudgeAround(game, this);
			 // 如果建筑消耗工人，则更新玩家的食物使用量
			 if (this.constructionConsumesWorker) {
				 game.getPlayer(this.workerInside.getPlayerIndex())
					 .setUnitFoodUsed(this.workerInside,
									this.workerInside.getUnitType().getFoodUsed());
			 }
			 // 清除建筑内的工人引用
			 this.workerInside = null;
		 }
	 }


	// 自动获取目标的方法： 检测自动施法技能是否攻击目标， 检测普攻是否攻击目标
	public boolean autoAcquireTargets(final CSimulation game, final boolean disableMove) {
		// 调用自动获取施法目标的方法；查找目标，找到目标就执行技能指令
		final boolean autocast = autoAcquireAutocastTargets(game, disableMove);
		if (!autocast) {
			// 如果没有自动施法且开启了自动攻击
			if (this.autoAttack) {
				// 查找可攻击的目标，并将目标设置给攻击行为，启动攻击行为
				return autoAcquireAttackTargets(game, disableMove);
			}
		}
		// 返回自动施法的结果
		return autocast;
	}

	/**
	 * 自动获取自动施法目标； 如果找到目标， 执行技能指令
	 *
	 * @param game           游戏实例
	 * @param disableMove    是否禁用移动
	 * @return              是否成功下达命令
	 */
	public boolean autoAcquireAutocastTargets(final CSimulation game, final boolean disableMove) {
		// 当前自动技能存在，并且是可用的
		if ((this.autocastAbility != null) && !this.autocastAbility.isDisabled()) {
			// 自动技能的目标类型： 没有有效目标的状态
			if (this.autocastAbility.getAutocastType() == AutocastType.NOTARGET) {
				final BooleanAbilityTargetCheckReceiver<Void> booleanTargetReceiver = BooleanAbilityTargetCheckReceiver
						.<Void>getInstance().reset();
				// 检查单位是否可以在没有目标的情况下自动施法
				this.autocastAbility.checkCanAutoTargetNoTarget(game, this, this.autocastAbility.getBaseOrderId(),
						booleanTargetReceiver);
				// 可以使用
				if (booleanTargetReceiver.isTargetable()) {
					// 执行技能的指令
					return this.order(game, this.autocastAbility.getBaseOrderId(), null);
				}
			}
			// 自动技能的目标类型： 不为无
			else if (this.autocastAbility.getAutocastType() != AutocastType.NONE) {
				if (this.collisionRectangle != null) {
					tempRect.set(this.collisionRectangle);
				}
				else {
					tempRect.set(getX(), getY(), 0, 0);
				}
				final float halfSize = this.acquisitionRange;
				tempRect.x -= halfSize;
				tempRect.y -= halfSize;
				tempRect.width += halfSize * 2;
				tempRect.height += halfSize * 2;
				// 查找目标
				game.getWorldCollision().enumUnitsInRect(tempRect,
						autocastTargetFinderEnum.reset(game, this, this.autocastAbility, disableMove));
				if (autocastTargetFinderEnum.currentUnitTarget != null) {
					// 执行技能的指令
					this.order(game, this.autocastAbility.getBaseOrderId(), autocastTargetFinderEnum.currentUnitTarget);
					return true;
				}
			}
		}
		return false;
	}


	// 查找可攻击的目标，并将目标设置给攻击行为，启动攻击行为
	public boolean autoAcquireAttackTargets(final CSimulation game, final boolean disableMove) {
		// 普攻列表不为空，且当前单位不是工人
		if (!getCurrentAttacks().isEmpty() && !this.unitType.getClassifications().contains(CUnitClassification.PEON)) {
			if (this.collisionRectangle != null) {
				tempRect.set(this.collisionRectangle);
			}
			else {
				tempRect.set(getX(), getY(), 0, 0);
			}
			// 采集范围半径
			final float halfSize = this.acquisitionRange;
			tempRect.x -= halfSize;
			tempRect.y -= halfSize;
			tempRect.width += halfSize * 2;
			tempRect.height += halfSize * 2;
			// 查找可攻击的目标，并将目标设置给攻击行为，启动攻击行为
			game.getWorldCollision().enumUnitsInRect(tempRect,
					autoAttackTargetFinderEnum.reset(game, this, disableMove));
			// 是否找到攻击目标
			return autoAttackTargetFinderEnum.foundAnyTarget;
		}
		return false;
	}


	public float getEndingDecayTime(final CSimulation game) {
		if (isBuilding()) {
			return game.getGameplayConstants().getStructureDecayTime();
		}
		if (this.unitType.isHero()) {
			return game.getGameplayConstants().getDissipateTime();
		}
		return game.getGameplayConstants().getBoneDecayTime();
	}

	// 检查指令是否是队列，是立即执行，还是添加到队列
	public void order(final CSimulation game, final COrder order, final boolean queue) {
		if (isDead()) {
			return;
		}

		if (order != null) {
			final CAbility ability = game.getAbility(order.getAbilityHandleId());

			if (ability != null) {
				if (!getAbilities().contains(ability)) {
					// not allowed to use ability of other unit...
					//不允许使用其他单位的能力。。。
					return;
				}
				// Allow the ability to response to the order without actually placing itself in
				// the queue, nor modifying (interrupting) the queue.
				//允许在不实际放置自己的情况下对订单进行响应的能力
				//也不修改（中断）队列。
				if (!ability.checkBeforeQueue(game, this, order.getOrderId(), order.getTarget(game))) {
					// 该段代码可能涉及一个待处理的TODO任务，
					// 需要检查网络请求是否在调用checkBeforeQueue之前进行了checkCanUse的验证。
					// TODO is this a possible bug vector that the network request doesn't
					// checkCanUse like the UI before checkBeforeQueue is called??

					order.fireEvents(game, this);
					this.stateNotifier.ordersChanged();
					return;
				}
			}
		}

		// TODO #如果是右键指令，并且上一个也是右键指令，就跳过
		if ((this.lastStartedOrder != null) && this.lastStartedOrder.equals(order)
				&& (this.lastStartedOrder.getOrderId() == OrderIds.smart)) { // 右键点击
			// I skip your spammed move orders, TODO this will probably break some repeat
			// attack order or something later
			//我跳过你的垃圾邮件移动订单，TODO这可能会打破一些重复
			//攻击命令或稍后发生的事情
			return;
		}

		// TODO #不加入队列的指令，清空之前排队的指令
		// !queue：这表示当前的指令不是要入队，而是要立即执行。
		// !this.acceptingOrders：这检查单位是否接受新指令。如果单位不接受指令，条件成立。
		// 如果当前存在行为（currentBehavior），并且该行为是不可中断的（interruptable()返回false），则条件也成立。
		if (!queue && (!this.acceptingOrders
				|| ((this.currentBehavior != null) && !this.currentBehavior.interruptable()))) {
			// 处理现有指令：取消队列中的指令：
			for (final COrder queuedOrder : this.orderQueue) {
				if (queuedOrder != null) {
					final int abilityHandleId = queuedOrder.getAbilityHandleId();
					final CAbility ability = game.getAbility(abilityHandleId);
					ability.onCancelFromQueue(game, this, queuedOrder.getOrderId());
				}
			}
			// 清空现有的指令队列，以确保不会再执行旧的指令。
			this.orderQueue.clear();
			// 将新的指令添加到指令队列中。
			this.orderQueue.add(order);
			// 调用状态通知者，发出指令发生变化的通知，以便更新相关的状态或UI（用户界面）。
			this.stateNotifier.ordersChanged();
			this.stateNotifier.waypointsChanged();
		}
		// TODO #将指令加入到队列
		// 这一行检查当前指令是否需要入队(queue为真)。
		// 另外，确认当前行为既不是停止行为(stopBehavior)，也不是保持位置行为(holdPositionBehavior)。这一条件的核心在于确保单位正在执行一些可进行队列操作的行为。
		else if (queue && (this.currentBehavior != this.stopBehavior)
				&& (this.currentBehavior != this.holdPositionBehavior)) {
			// 检查当前指令是否是巡逻指令。
			if (order.getOrderId() == OrderIds.patrol) {
				// 进一步检查默认行为是否为巡逻行为。
				if (this.defaultBehavior == this.patrolBehavior) {
					// 如果默认行为是巡逻，则将新的巡逻点添加到巡逻行为中，调用addPatrolPoint方法。
					this.patrolBehavior.addPatrolPoint(order.getTarget(game));
				}
				else {
					// 如果默认行为不是巡逻，将当前的巡逻指令添加到指令队列中(this.orderQueue.add(order))，以等待执行。
					this.orderQueue.add(order);
				}
			}
			else {
				// 无论当前指令是什么，如果它不是巡逻指令，直接将该指令添加到指令队列中。
				this.orderQueue.add(order);
			}
			// 这一行调用状态通知者，通知系统方向点发生了变化。这通常会引发进一步的状态更新或者更新UI等操作。
			this.stateNotifier.waypointsChanged();
		}
		// TODO #停止当前行为，执行新命令，并情况指令队列
		else {
			// 这行代码将单位的默认行为设置为停止行为（stopBehavior）。这意味着在处理新指令之前，单位将不再执行任何其他活动。
			setDefaultBehavior(this.stopBehavior);
			// 结束当前行为:如果当前有行为正在进行，则调用 end 方法结束该行为，传入的参数 true 可能表示以某种强制方式结束（具体含义可能根据上下文而有所不同）。
			if (this.currentBehavior != null) {
				this.currentBehavior.end(game, true);
			}
			// 开始新指令的行为: 这里调用 beginOrder 方法开始新的指令行为，将当前行为更新为新的行为。此时，order 是指当前要执行的指令。
			this.currentBehavior = beginOrder(game, order);
			if (this.currentBehavior != null) {
				// 如果成功获取到新的行为，调用 begin 方法来开始这个行为。
				this.currentBehavior.begin(game);
			}
			// 处理已排队的指令:这段代码遍历当前的指令队列（orderQueue），并取消队列中每一个指令的执行。对于每个非空的指令调用其对应的能力的 onCancelFromQueue 方法，表示这些指令不再执行。
			for (final COrder queuedOrder : this.orderQueue) {
				if (queuedOrder != null) {
					final int abilityHandleId = queuedOrder.getAbilityHandleId();
					final CAbility ability = game.getAbility(abilityHandleId);
					ability.onCancelFromQueue(game, this, queuedOrder.getOrderId());
				}
			}
			// 清空指令队列，确保没有残留的指令。
			this.orderQueue.clear();
			// 最后，调用状态通知者 (stateNotifier) 以通知系统指令和路径点（waypoints）已经发生变化。这通常用于更新用户界面或其它与状态相关的逻辑。
			this.stateNotifier.ordersChanged();
			this.stateNotifier.waypointsChanged();
		}
	}

	// 检查指令 是否有对应的能力 能处理
	public boolean order(final CSimulation simulation, final int orderId, final AbilityTarget target) {
		// 如果是停止指令：直接创建指令处理
		if (orderId == OrderIds.stop) {
			order(simulation, new COrderNoTarget(0, orderId, false), false);
			return true;
		}
		// 遍历所有能力，找到能处理该指令的能力
		for (final CAbility ability : this.abilities) {
			final BooleanAbilityActivationReceiver activationReceiver = BooleanAbilityActivationReceiver.INSTANCE;
			// 检查能力是否可以使用
			ability.checkCanUse(simulation, this, orderId, activationReceiver);
			if (activationReceiver.isOk()) {
				// 无目标的指令， 能力检测是否可以处理无目标指令，如果可以，则创建指令处理
				if (target == null) {
					final BooleanAbilityTargetCheckReceiver<Void> booleanTargetReceiver = BooleanAbilityTargetCheckReceiver
							.<Void>getInstance().reset();
					// 检查无目标是否可以使用
					ability.checkCanTargetNoTarget(simulation, this, orderId, booleanTargetReceiver);
					if (booleanTargetReceiver.isTargetable()) {
						order(simulation, new COrderNoTarget(ability.getHandleId(), orderId, false), false);
						return true;
					}
				}
				// 有目标的指令， 能力检测是否可以处理该目标，如果可以，则创建指令处理
				else {
					// 处理不同的目标类型进行处理
					final boolean targetable = target.visit(new AbilityTargetVisitor<Boolean>() {
						// 处理点目标
						@Override
						public Boolean accept(final AbilityPointTarget target) {
							final BooleanAbilityTargetCheckReceiver<AbilityPointTarget> booleanTargetReceiver = BooleanAbilityTargetCheckReceiver
									.<AbilityPointTarget>getInstance().reset();
							ability.checkCanTarget(simulation, CUnit.this, orderId, target, booleanTargetReceiver);
							final boolean pointTargetable = booleanTargetReceiver.isTargetable();
							if (pointTargetable) {
								order(simulation, new COrderTargetPoint(ability.getHandleId(), orderId, target, false),
										false);
							}
							return pointTargetable;
						}

						// 处理单位、可破坏物、物品等目标
						public Boolean acceptWidget(final CWidget target) {
							final BooleanAbilityTargetCheckReceiver<CWidget> booleanTargetReceiver = BooleanAbilityTargetCheckReceiver
									.<CWidget>getInstance().reset();
							ability.checkCanTarget(simulation, CUnit.this, orderId, target, booleanTargetReceiver);
							final boolean widgetTargetable = booleanTargetReceiver.isTargetable();
							if (widgetTargetable) {
								order(simulation, new COrderTargetWidget(ability.getHandleId(), orderId,
										target.getHandleId(), false), false);
							}
							return widgetTargetable;
						}

						@Override
						public Boolean accept(final CUnit target) {
							return acceptWidget(target);
						}

						@Override
						public Boolean accept(final CDestructable target) {
							return acceptWidget(target);
						}

						@Override
						public Boolean accept(final CItem target) {
							return acceptWidget(target);
						}
					});
					if (targetable) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// 执行指令begin方法，获取指令的行为，并设置最后执行的指令
	private CBehavior beginOrder(final CSimulation game, final COrder order) {
		this.lastStartedOrder = order;
		CBehavior nextBehavior;
		if (order != null) {
			nextBehavior = order.begin(game, this);
		}
		else {
			nextBehavior = this.defaultBehavior;
		}
		return nextBehavior;
	}
	// 获取当前行为
	public CBehavior getCurrentBehavior() {
		return this.currentBehavior;
	}

	// 获取能力列表
	public List<CAbility> getAbilities() {
		return this.abilities;
	}

	// 使用访问者模式遍历能力并返回访问结果
	public <T> T getAbility(final CAbilityVisitor<T> visitor) {
		// 遍历此对象的能力列表，并使用访问者模式访问每个能力
		for (final CAbility ability : this.abilities) {
			final T visited = ability.visit(visitor);
			if (visited != null) {
				return visited; // 如果访问结果不为null，则返回访问结果
			}
		}
		return null; // 如果所有能力均未返回有效的访问结果，则返回null

	}


	// 获取指定类型的第一个能力
	public <T extends CAbility> T getFirstAbilityOfType(final Class<T> cAbilityClass) {
		for (final CAbility ability : this.abilities) {
			if (cAbilityClass.isAssignableFrom(ability.getClass())) {
				return (T) ability;
			}
		}
		return null;
	}


	public void setCooldownEndTime(final int cooldownEndTime) {
		this.cooldownEndTime = cooldownEndTime;
	}

	// 普攻冷却时间
	public int getCooldownEndTime() {
		return this.cooldownEndTime;
	}

	@Override
	public float getFlyHeight() {
		return this.flyHeight;
	}

	public void setFlyHeight(final float flyHeight) {
		this.flyHeight = flyHeight;
	}

	public int getPlayerIndex() {
		return this.playerIndex;
	}

	public void setPlayerIndex(final CSimulation simulation, final int playerIndex, final boolean changeColor) {
		this.playerIndex = playerIndex;
		if (changeColor) {
			simulation.changeUnitColor(this, playerIndex);
		}
	}

	public CUnitType getUnitType() {
		return this.unitType;
	}

	public void setCollisionRectangle(final Rectangle collisionRectangle) {
		this.collisionRectangle = collisionRectangle;
	}

	public Rectangle getCollisionRectangle() {
		return this.collisionRectangle;
	}

	public void setX(final float newX, final CWorldCollision collision, final CRegionManager regionManager) {
		final float prevX = getX();
		setX(newX);
		collision.translate(this, newX - prevX, 0);
		checkRegionEvents(regionManager);
	}

	public void setY(final float newY, final CWorldCollision collision, final CRegionManager regionManager) {
		final float prevY = getY();
		setY(newY);
		collision.translate(this, 0, newY - prevY);
		checkRegionEvents(regionManager);
	}

	// 传送到指定坐标：会传送到检测能附近没有建筑物的地方
	public void setPointAndCheckUnstuck(final float newX, final float newY, final CSimulation game) {
		final CWorldCollision collision = game.getWorldCollision(); // 获取游戏世界碰撞检测对象
		final PathingGrid pathingGrid = game.getPathingGrid(); // 获取寻路网格对象

		float outputX = newX, outputY = newY; // 初始化输出坐标
		int checkX = 0; // 初始化检查点X坐标
		int checkY = 0; // 初始化检查点Y坐标
		float collisionSize; // 碰撞大小

		// 如果是建筑并且有建筑寻路像素图，则设置临时矩形的大小为建筑寻路像素图的宽高乘以32
		if (isBuilding() && (this.unitType.getBuildingPathingPixelMap() != null)) {
			tempRect.setSize(this.unitType.getBuildingPathingPixelMap().getWidth() * 32,
					this.unitType.getBuildingPathingPixelMap().getHeight() * 32);
			collisionSize = tempRect.getWidth() / 2; // 设置碰撞大小为临时矩形宽度的一半
		}
		// 如果有碰撞矩形，则设置临时矩形为碰撞矩形，并获取单位类型的碰撞大小
		else if (this.collisionRectangle != null) {
			tempRect.set(this.collisionRectangle);
			collisionSize = this.unitType.getCollisionSize();
		}
		// 否则设置临时矩形的大小为16x16，并获取单位类型的碰撞大小
		else {
			tempRect.setSize(16, 16);
			collisionSize = this.unitType.getCollisionSize();
		}

		// 循环300次寻找合适的坐标
		for (int i = 0; i < 300; i++) {
			final float centerX = newX + (checkX * 64); // 计算中心点X坐标
			final float centerY = newY + (checkY * 64); // 计算中心点Y坐标
			tempRect.setCenter(centerX, centerY); // 设置临时矩形的中心点

			// 如果中心点不与任何其他物体碰撞并且寻路网格可通行，则设置输出坐标并跳出循环
			if (!collision.intersectsAnythingOtherThan(tempRect, this, getMovementType())
					&& pathingGrid.isPathable(centerX, centerY, getMovementType(), collisionSize)) {
				outputX = centerX;
				outputY = centerY;
				break;
			}

			// 计算角度并更新检查点坐标
			final double angle = (((int) Math.floor(Math.sqrt((4 * i) + 1)) % 4) * Math.PI) / 2;
			checkX -= (int) Math.cos(angle);
			checkY -= (int) Math.sin(angle);
		}

		// 设置新的点坐标，并通知游戏单位已重新定位
		setPoint(outputX, outputY, collision, game.getRegionManager());
		game.unitRepositioned(this);

	}

	public void setPoint(final float newX, final float newY, final CWorldCollision collision,
			final CRegionManager regionManager) {
		final float prevX = getX();
		final float prevY = getY();
		setX(newX);
		setY(newY);
		collision.translate(this, newX - prevX, newY - prevY);
		checkRegionEvents(regionManager);
	}

	/**
	 * 检查区域事件的方法，用于处理单位与区域的交互。
	 * 该方法首先交换当前包含区域和之前的包含区域集合，然后清空当前包含区域集合。
	 * 接着，使用区域管理器检查区域，并处理单位离开区域的事件。
	 *
	 * @param regionManager 区域管理器实例，用于检查区域和处理单位与区域的交互。
	 */
	private void checkRegionEvents(final CRegionManager regionManager) {
		// 交换当前包含区域和之前的包含区域集合
		final Set<CRegion> temp = this.containingRegions;
		this.containingRegions = this.priorContainingRegions;
		this.priorContainingRegions = temp;
		// 清空当前包含区域集合
		this.containingRegions.clear();
		// 使用区域管理器检查区域，传入碰撞矩形或默认矩形，以及重置后的区域检查器
		regionManager.checkRegions(
				this.collisionRectangle == null ? tempRect.set(getX(), getY(), 0, 0) : this.collisionRectangle,
				regionCheckerImpl.reset(this, regionManager));
		// 遍历之前的包含区域集合
		for (final CRegion region : this.priorContainingRegions) {
			// 如果当前包含区域集合不包含该区域，则单位离开了该区域
			if (!this.containingRegions.contains(region)) {
				// 调用区域管理器的onUnitLeaveRegion方法处理单位离开区域的事件
				regionManager.onUnitLeaveRegion(this, region);
			}
		}
	}

	/**
	 * 获取当前对象的所有分类。
	 *
	 * @return 当前对象的分类集合
	 */
	public EnumSet<CUnitClassification> getClassifications() {
		return this.classifications;
	}

	/**
	 * 向当前对象的分类集合中添加一个新的分类。
	 *
	 * @param unitClassification 要添加的分类
	 */
	public void addClassification(final CUnitClassification unitClassification) {
		this.classifications.add(unitClassification);
	}


	public float getDefense() {
		return this.currentDefense;
	}

	@Override
	public float getImpactZ() {
		return this.unitType.getImpactZ();
	}

	public double angleTo(final AbilityTarget target) {
		final double dx = target.getX() - getX();
		final double dy = target.getY() - getY();
		return StrictMath.atan2(dy, dx);
	}

	public double distance(final AbilityTarget target) {
		double dx = StrictMath.abs(target.getX() - getX());
		double dy = StrictMath.abs(target.getY() - getY());
		final float thisCollisionSize = this.unitType.getCollisionSize();
		float targetCollisionSize;
		if (target instanceof CUnit) {
			final CUnitType targetUnitType = ((CUnit) target).getUnitType();
			targetCollisionSize = targetUnitType.getCollisionSize();
		}
		else {
			targetCollisionSize = 0; // TODO destructable collision size here
		}
		if (dx < 0) {
			dx = 0;
		}
		if (dy < 0) {
			dy = 0;
		}

		double groundDistance = StrictMath.sqrt((dx * dx) + (dy * dy)) - thisCollisionSize - targetCollisionSize;
		if (groundDistance < 0) {
			groundDistance = 0;
		}
		return groundDistance;
	}

	@Override
	public double distance(final float x, final float y) {
		return distance((double)x, (double)y);
	}

	public double distance(final double x, final double y) {
		double dx = Math.abs(x - getX());
		double dy = Math.abs(y - getY());
		final float thisCollisionSize = this.unitType.getCollisionSize();
		if (dx < 0) {
			dx = 0;
		}
		if (dy < 0) {
			dy = 0;
		}

		double groundDistance = StrictMath.sqrt((dx * dx) + (dy * dy)) - thisCollisionSize;
		if (groundDistance < 0) {
			groundDistance = 0;
		}
		return groundDistance;
	}

	public boolean checkForMiss(final CSimulation simulation, final CUnit source, final boolean isAttack,
			final boolean isRanged, final CAttackType attackType, final CDamageType damageType, final float damage,
			final float bonusDamage) {
		boolean miss = false;
		if (isAttack) {
			for (final CUnitAttackEvasionListener listener : this.evasionListeners) {
				miss = miss || listener.onAttack(simulation, source, this, isAttack, isRanged, damageType);
			}
		}
		return miss;
	}

	@Override
	public float damage(final CSimulation simulation, final CUnit source, final boolean isAttack,
			final boolean isRanged, final CAttackType attackType, final CDamageType damageType,
			final String weaponSoundType, final float damage) {
		return this.damage(simulation, source, isAttack, isRanged, attackType, damageType, weaponSoundType, damage, 0);
	}

	@Override
	public float damage(final CSimulation simulation, final CUnit source, final boolean isAttack,
			final boolean isRanged, final CAttackType attackType, final CDamageType damageType,
			final String weaponSoundType, final float damage, final float bonusDamage) {
		final boolean wasDead = isDead();
		if (wasDead) {
			return 0;
		}
		float trueDamage = 0;
		// 非 无敌状态
		if (!this.invulnerable) {

			// 创建伤害修改结构体
			final CUnitAttackDamageTakenModificationListenerDamageModResult result = new CUnitAttackDamageTakenModificationListenerDamageModResult(
					damage, bonusDamage);
			// onDamage 伤害修改
			for (final CUnitAttackDamageTakenModificationListener listener : this.damageTakenModificationListeners) {
				listener.onDamage(simulation, source, this, isAttack, isRanged, attackType, damageType, result);
			}

			// 查询 攻击和防御 的伤害比例
			final float damageRatioFromArmorClass = simulation.getGameplayConstants().getDamageRatioAgainst(attackType,
					getDefenseType());
			final float damageRatioFromDefense;
			final float defense = this.currentDefense;
			if (damageType != CDamageType.NORMAL) {
				damageRatioFromDefense = 1.0f;
			}
			else if (defense >= 0) {
				damageRatioFromDefense = 1f - ((defense * simulation.getGameplayConstants().getDefenseArmor())
						/ (1 + (simulation.getGameplayConstants().getDefenseArmor() * defense)));
			}
			else {
				damageRatioFromDefense = 2f
						- (float) StrictMath.pow(1f - simulation.getGameplayConstants().getDefenseArmor(), -defense);
			}
			// 真实伤害
			trueDamage = damageRatioFromArmorClass * damageRatioFromDefense * result.computeFinalDamage();

			// onDamage 最终伤害修改
			for (final CUnitAttackFinalDamageTakenModificationListener listener : new ArrayList<>(
					this.finalDamageTakenModificationListeners)) {
				trueDamage = listener.onDamage(simulation, source, this, isAttack, isRanged, attackType, damageType,
						trueDamage);
			}

			final boolean wasAboveMax = this.life > this.maximumLife;
			this.life -= trueDamage;
			if ((result.computeFinalDamage() < 0) && !wasAboveMax && (this.life > this.maximumLife)) {
				// NOTE wasAboveMax is for that weird life drain power to drain above max... to
				// be honest that's a crazy mechanic anyway so I didn't test whether it works
				// yet
				this.life = this.maximumLife;
			}
			this.stateNotifier.lifeChanged();
		}
		// onDamage 伤害监听
		for (final CUnitAttackDamageTakenListener listener : new ArrayList<>(this.damageTakenListeners)) {
			listener.onDamage(simulation, source, this, isAttack, isRanged, damageType, damage, bonusDamage,
					trueDamage);
		}
		// 伤害音响事件
		simulation.unitDamageEvent(this, weaponSoundType, this.unitType.getArmorType());
		// 检查是否 死亡
		if (!this.invulnerable && isDead()) {
			if (!wasDead) {
				// 击杀
				kill(simulation, source);
			}
		}
		else {
			if ((this.currentBehavior == null)
					|| ((this.currentBehavior == this.defaultBehavior) && this.currentBehavior.interruptable())) {
				boolean foundMatchingReturnFireAttack = false;
				if (!simulation.getPlayer(getPlayerIndex()).hasAlliance(source.getPlayerIndex(), CAllianceType.PASSIVE)
						&& !this.unitType.getClassifications().contains(CUnitClassification.PEON)) {
					for (final CUnitAttack attack : getCurrentAttacks()) {
						if (source.canBeTargetedBy(simulation, this, attack.getTargetsAllowed())) {
							this.currentBehavior = getAttackBehavior().reset(OrderIds.attack, attack, source, false,
									CBehaviorAttackListener.DO_NOTHING);
							this.currentBehavior.begin(simulation);
							foundMatchingReturnFireAttack = true;
							break;
						}
					}
				}
				if (!foundMatchingReturnFireAttack && this.unitType.isCanFlee() && !isMovementDisabled()
						&& (this.moveBehavior != null) && (this.playerIndex != source.getPlayerIndex())) {
					final double angleTo = source.angleTo(this);
					final int distanceToFlee = getSpeed();
					this.currentBehavior = this.moveBehavior.reset(OrderIds.move,
							new AbilityPointTarget((float) (getX() + (distanceToFlee * StrictMath.cos(angleTo))),
									(float) (getY() + (distanceToFlee * StrictMath.sin(angleTo)))));
					this.currentBehavior.begin(simulation);
				}
			}
		}
		return trueDamage;
	}

	// 击杀
	private void kill(final CSimulation simulation, final CUnit source) {
		if (this.currentBehavior != null) {
			this.currentBehavior.end(simulation, true);
		}
		this.currentBehavior = null;

		final CUnitDeathReplacementResult result = new CUnitDeathReplacementResult();
		CUnitDeathReplacementStacking allowContinue = new CUnitDeathReplacementStacking();
		// 死亡替换效果处理器
		for (final CUnitDeathReplacementEffectPriority priority : CUnitDeathReplacementEffectPriority.values()) {
			if (allowContinue.isAllowStacking()) { // 是否允许堆叠
				for (final CUnitDeathReplacementEffect effect : this.deathReplacementEffects.get(priority)) {
					if (allowContinue.isAllowSamePriorityStacking()) {
						allowContinue = effect.onDeath(simulation, this, source, result);
					}
				}
			}
		}
		// 复活状态
		if (result.isReviving()) {
			return;
		}

		this.orderQueue.clear();
		killPathingInstance();
		popoutWorker(simulation);
		for (int i = this.abilities.size() - 1; i >= 0; i--) {
			// okay if it removes self from this during onDeath() because of reverse
			// iteration order
			this.abilities.get(i).onDeath(simulation, this);
		}
		// 为当前玩家添加死亡视觉雾效果
		simulation.getPlayer(this.playerIndex).addFogModifer(simulation, new CUnitDeathVisionFogModifier(this));

		// 如果source不为空，则为source玩家也添加死亡视觉雾效果
		if (source != null) {
			simulation.getPlayer(source.getPlayerIndex()).addFogModifer(simulation, new CUnitDeathVisionFogModifier(this));
		}

		// 如果攻击雾效果的玩家索引与当前玩家索引不同
		if (this.attackFogMod.getPlayerIndex() != this.playerIndex) {
			// 获取攻击雾效果对应的玩家对象，并移除其雾效果
			simulation.getPlayer(this.attackFogMod.getPlayerIndex()).removeFogModifer(simulation, this.attackFogMod);
			// 将攻击雾效果的玩家索引设置为当前玩家索引
			this.attackFogMod.setPlayerIndex(this.playerIndex);
		}


		if (result.isReincarnating()) {
			return;
		}

		if (this.constructing) {
			simulation.createDeathExplodeEffect(this, this.explodesOnDeathBuffId);
		}
		else {
			this.deathTurnTick = simulation.getGameTurnTick();
		}

		final CPlayer player = simulation.getPlayer(this.playerIndex);
		if (this.foodMade != 0) {
			player.setUnitFoodMade(this, 0);
		}
		if (this.foodUsed != 0) {
			player.setUnitFoodUsed(this, 0);
		}
		if (getHeroData() == null) {
			if (this.constructing) {
				player.removeTechtreeInProgress(this.unitType.getTypeId());
			}
			else {
				player.removeTechtreeUnlocked(simulation, this.unitType.getTypeId());
			}
		}
		// else its a hero and techtree "remains unlocked" which is currently meaning
		// the "limit of 1" remains limited

		// Award hero experience
		if (source != null) {
			final CPlayer sourcePlayer = simulation.getPlayer(source.getPlayerIndex());
			if (!sourcePlayer.hasAlliance(this.playerIndex, CAllianceType.PASSIVE)) {
				if (player.getPlayerState(simulation, CPlayerState.GIVES_BOUNTY) > 0) {
					int goldBountyAwarded = this.unitType.getGoldBountyAwardedBase();
					final int goldBountyAwardedDice = this.unitType.getGoldBountyAwardedDice();
					final int goldBountyAwardedSides = this.unitType.getGoldBountyAwardedSides();
					for (int i = 0; i < goldBountyAwardedDice; i++) {
						final int singleRoll = goldBountyAwardedSides == 0 ? 0
								: simulation.getSeededRandom().nextInt(goldBountyAwardedSides);
						goldBountyAwarded += singleRoll + 1;
					}
					if (goldBountyAwarded > 0) {
						sourcePlayer.addGold(goldBountyAwarded);
						simulation.unitGainResourceEvent(this, sourcePlayer.getId(), ResourceType.GOLD,
								goldBountyAwarded);
					}
					int lumberBountyAwarded = this.unitType.getLumberBountyAwardedBase();
					final int lumberBountyAwardedDice = this.unitType.getLumberBountyAwardedDice();
					final int lumberBountyAwardedSides = this.unitType.getLumberBountyAwardedSides();
					for (int i = 0; i < lumberBountyAwardedDice; i++) {
						lumberBountyAwarded += simulation.getSeededRandom().nextInt(lumberBountyAwardedSides) + 1;
					}
					if (lumberBountyAwarded > 0) {
						sourcePlayer.addLumber(lumberBountyAwarded);
						simulation.unitGainResourceEvent(this, sourcePlayer.getId(), ResourceType.LUMBER,
								lumberBountyAwarded);
					}
				}
				final CGameplayConstants gameplayConstants = simulation.getGameplayConstants();
				if (gameplayConstants.isBuildingKillsGiveExp() || !source.isBuilding()) {
					final CUnit killedUnit = this;
					final CAbilityHero killedUnitHeroData = getHeroData();
					final boolean killedUnitIsAHero = killedUnitHeroData != null;
					int availableAwardXp;
					if (killedUnitIsAHero) {
						availableAwardXp = gameplayConstants.getGrantHeroXP(killedUnitHeroData.getHeroLevel());
					}
					else {
						availableAwardXp = gameplayConstants.getGrantNormalXP(this.unitType.getLevel());
					}
					final List<CUnit> xpReceivingHeroes = new ArrayList<>();
					final int heroExpRange = gameplayConstants.getHeroExpRange();
					simulation.getWorldCollision().enumUnitsInRect(new Rectangle(getX() - heroExpRange,
							getY() - heroExpRange, heroExpRange * 2, heroExpRange * 2), new CUnitEnumFunction() {
								@Override
								public boolean call(final CUnit unit) {
									if ((unit.distance(killedUnit) <= heroExpRange)
											&& sourcePlayer.hasAlliance(unit.getPlayerIndex(), CAllianceType.SHARED_XP)
											&& unit.isHero() && !unit.isDead()) {
										xpReceivingHeroes.add(unit);
									}
									return false;
								}
							});
					if (xpReceivingHeroes.isEmpty()) {
						if (gameplayConstants.isGlobalExperience()) {
							for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
								if (sourcePlayer.hasAlliance(i, CAllianceType.SHARED_XP)) {
									xpReceivingHeroes.addAll(simulation.getPlayerHeroes(i));
								}
							}
						}
					}
					for (final CUnit receivingHero : xpReceivingHeroes) {
						final CAbilityHero heroData = receivingHero.getHeroData();
						heroData.addXp(simulation, receivingHero,
								(int) (availableAwardXp * (1f / xpReceivingHeroes.size())
										* gameplayConstants.getHeroFactorXp(heroData.getHeroLevel())));
					}
				}
			}
		}
		fireDeathEvents(simulation);
		final List<CWidgetEvent> eventList = getEventList(JassGameEventsWar3.EVENT_UNIT_DEATH);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitDeathScope(JassGameEventsWar3.EVENT_UNIT_DEATH,
						event.getTrigger(), this, source));
			}
		}
		simulation.getPlayer(this.playerIndex).fireUnitDeathEvents(this, source);
		if (isExplodesOnDeath()) {
			setHidden(true);
			simulation.createDeathExplodeEffect(this, this.explodesOnDeathBuffId);
			simulation.removeUnit(this);
		}
	}

	public void killPathingInstance() {
		if (this.pathingInstance != null) {
			this.pathingInstance.remove();
			this.pathingInstance = null;
		}
	}

	public void kill(final CSimulation simulation) {
		if (!isDead()) {
			setLife(simulation, 0f);
		}
	}

	// 能否到达指定目标范围内
	public boolean canReach(final AbilityTarget target, final float range) {
		final double distance = distance(target);
		if (target instanceof CUnit) {
			final CUnit targetUnit = (CUnit) target;
			final CUnitType targetUnitType = targetUnit.getUnitType();
			// 如果是建筑，并且建筑修改寻路图存在
			if (targetUnit.isBuilding() && (targetUnitType.getBuildingPathingPixelMap() != null)) {
				final BufferedImage buildingPathingPixelMap = targetUnitType.getBuildingPathingPixelMap();
				final float targetX = target.getX();
				final float targetY = target.getY();
				//
				if (canReachToPathing(range, targetUnit.getFacing(), buildingPathingPixelMap, targetX, targetY)) {
					return true;
				}
			}
		}
		else if (target instanceof CDestructable) {
			final CDestructable targetDest = (CDestructable) target;
			final CDestructableType targetDestType = targetDest.getDestType();
			final BufferedImage pathingPixelMap = targetDest.isDead() ? targetDestType.getPathingDeathPixelMap()
					: targetDestType.getPathingPixelMap();
			final float targetX = target.getX();
			final float targetY = target.getY();
			if ((pathingPixelMap != null) && canReachToPathing(range, 270, pathingPixelMap, targetX, targetY)) {
				return true;
			}
		}
		return distance <= range;
	}

	public boolean canReach(final float x, final float y, final float range) {
		return distance(x, y) <= range; // TODO use dist squared for performance
	}
	// 检查是否可以到达指定路径的函数
	public boolean canReachToPathing(final float range, final float rotationForPathing,
			final BufferedImage buildingPathingPixelMap, final float targetX, final float targetY) {
		if (buildingPathingPixelMap == null) {
			return canReach(targetX, targetY, range);
		}
		final int rotation = ((int) rotationForPathing + 450) % 360;
		final float relativeOffsetX = getX() - targetX;
		final float relativeOffsetY = getY() - targetY;
		final int gridWidth = (rotation % 180) != 0 ? buildingPathingPixelMap.getHeight()
				: buildingPathingPixelMap.getWidth();
		final int gridHeight = (rotation % 180) != 0 ? buildingPathingPixelMap.getWidth()
				: buildingPathingPixelMap.getHeight();
		final int relativeGridX = (int) Math.floor(relativeOffsetX / 32f) + (gridWidth / 2);
		final int relativeGridY = (int) Math.floor(relativeOffsetY / 32f) + (gridHeight / 2);
		final int rangeInCells = (int) Math.floor(range / 32f) + 1;
		final int rangeInCellsSquare = rangeInCells * rangeInCells;
		int minCheckX = relativeGridX - rangeInCells;
		int minCheckY = relativeGridY - rangeInCells;
		int maxCheckX = relativeGridX + rangeInCells;
		int maxCheckY = relativeGridY + rangeInCells;
		if ((minCheckX < gridWidth) && (maxCheckX >= 0)) {
			if ((minCheckY < gridHeight) && (maxCheckY >= 0)) {
				if (minCheckX < 0) {
					minCheckX = 0;
				}
				if (minCheckY < 0) {
					minCheckY = 0;
				}
				if (maxCheckX > (gridWidth - 1)) {
					maxCheckX = gridWidth - 1;
				}
				if (maxCheckY > (gridHeight - 1)) {
					maxCheckY = gridHeight - 1;
				}
				for (int checkX = minCheckX; checkX <= maxCheckX; checkX++) {
					for (int checkY = minCheckY; checkY <= maxCheckY; checkY++) {
						final int dx = relativeGridX - checkX;
						final int dy = relativeGridY - checkY;
						if (((dx * dx) + (dy * dy)) <= rangeInCellsSquare) {
							if (((getRGBFromPixelData(buildingPathingPixelMap, checkX, checkY, rotation)
									& 0xFF0000) >>> 16) > 127) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}


	private int getRGBFromPixelData(final BufferedImage buildingPathingPixelMap, final int checkX, final int checkY,
			final int rotation) {

		// Below: y is downwards (:()
		int x;
		int y;
		switch (rotation) {
		case 90:
			x = checkY;
			y = buildingPathingPixelMap.getWidth() - 1 - checkX;
			break;
		case 180:
			x = buildingPathingPixelMap.getWidth() - 1 - checkX;
			y = buildingPathingPixelMap.getHeight() - 1 - checkY;
			break;
		case 270:
			x = buildingPathingPixelMap.getHeight() - 1 - checkY;
			y = checkX;
			break;
		default:
		case 0:
			x = checkX;
			y = checkY;
		}
		return buildingPathingPixelMap.getRGB(x, buildingPathingPixelMap.getHeight() - 1 - y);
	}

	public void addStateListener(final CUnitStateListener listener) {
		this.stateListenersUpdates.add(new StateListenerUpdate(listener, StateListenerUpdateType.ADD));
	}

	public void removeStateListener(final CUnitStateListener listener) {
		this.stateListenersUpdates.add(new StateListenerUpdate(listener, StateListenerUpdateType.REMOVE));
	}

	public boolean isCorpse() {
		return this.corpse;
	}

	public boolean isBoneCorpse() {
		return this.boneCorpse;
	}

	// 检查目标类型集合能否作为目标
	@Override
	public boolean canBeTargetedBy(final CSimulation simulation, final CUnit source,
			final EnumSet<CTargetType> targetsAllowed, final AbilityTargetCheckReceiver<CWidget> receiver) {
		// 检查是否尝试对自己使用技能，如果目标不允许是自己且当前对象与来源相同，则目标检查失败
		if ((this == source) && targetsAllowed.contains(CTargetType.NOTSELF)
			  && !targetsAllowed.contains(CTargetType.SELF)) {
		  receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_SELF); // 报告错误：无法对自己使用技能
		  return false; // 返回false，表示目标检查未通过
		}
		// 检查是否尝试对非自己控制的单位使用技能，如果目标允许的是玩家单位且来源的玩家索引与当前对象不同，则目标检查失败
		if (targetsAllowed.contains(CTargetType.PLAYERUNITS) && (source.getPlayerIndex() != getPlayerIndex())) {
		  receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_ONE_OF_YOUR_OWN_UNITS); // 报告错误：必须对您自己的单位使用技能
		  return false; // 返回false，表示目标检查未通过
		}
		// 检查是否尝试对魔法免疫的单位使用魔法，如果目标允许的是非魔法免疫单位且当前对象是魔法免疫的，则目标检查失败
		if (targetsAllowed.contains(CTargetType.NON_MAGIC_IMMUNE) && isMagicImmune()) {
		  receiver.targetCheckFailed(CommandStringErrorKeys.THAT_UNIT_IS_IMMUNE_TO_MAGIC); // 报告错误：该单位对魔法免疫
		  return false; // 返回false，表示目标检查未通过
		}
		// 检查目标是否合法
		if (targetsAllowed.containsAll(this.unitType.getTargetedAs()) // 目标类型包含所有允许的目标类型
			  || (!targetsAllowed.contains(CTargetType.GROUND) // 或者目标类型不包含地面、建筑和空中目标
					  && !targetsAllowed.contains(CTargetType.STRUCTURE)
					  && !targetsAllowed.contains(CTargetType.AIR))) {
		  final int sourcePlayerIndex = source.getPlayerIndex(); // 获取源玩家索引
		  final CPlayer sourcePlayer = simulation.getPlayer(sourcePlayerIndex); // 获取源玩家对象
		  // 检查目标是否为敌人或盟友
		  if (!targetsAllowed.contains(CTargetType.ENEMIES)
				  || !sourcePlayer.hasAlliance(this.playerIndex, CAllianceType.PASSIVE) // 如果目标不是敌人，或者源玩家与当前玩家不是被动联盟
				  || targetsAllowed.contains(CTargetType.FRIEND)) {
			  // 检查目标是否为友军或敌人
			  if (!targetsAllowed.contains(CTargetType.FRIEND)
					  || sourcePlayer.hasAlliance(this.playerIndex, CAllianceType.PASSIVE) // 如果目标不是友军，或者源玩家与当前玩家是被动联盟
					  || targetsAllowed.contains(CTargetType.ENEMIES)) {
				  // 检查目标是否为机械或有机
				  if (!targetsAllowed.contains(CTargetType.MECHANICAL)
						  || this.unitType.getClassifications().contains(CUnitClassification.MECHANICAL)) {
					  // 检查目标是否为有机或非机械
					  if (!targetsAllowed.contains(CTargetType.ORGANIC)
							  || !this.unitType.getClassifications().contains(CUnitClassification.MECHANICAL)) {
						  // 检查目标是否为古代或非古代
						  if (!targetsAllowed.contains(CTargetType.ANCIENT)
								  || this.unitType.getClassifications().contains(CUnitClassification.ANCIENT)) {
							  // 检查目标是否为非古代或古代
							  if (!targetsAllowed.contains(CTargetType.NONANCIENT)
									  || !this.unitType.getClassifications().contains(CUnitClassification.ANCIENT)) {
								  final boolean invulnerable = isInvulnerable(); // 检查单位是否无敌
								  // 检查目标是否为脆弱或无敌
								  if ((!invulnerable && (targetsAllowed.contains(CTargetType.VULNERABLE)
										  || !targetsAllowed.contains(CTargetType.INVULNERABLE)))
										  || (invulnerable && targetsAllowed.contains(CTargetType.INVULNERABLE))) {
									  // 检查目标是否为英雄或非英雄
									  if (!targetsAllowed.contains(CTargetType.HERO) || (getHeroData() != null)) {
										  if (!targetsAllowed.contains(CTargetType.NONHERO)
												  || (getHeroData() == null)) {
											  // 检查单位是否死亡
											  if (isDead()) {
												  // 检查单位是否可复活、是否腐烂、是否为骨骸尸体
												  if (isRaisable() && isDecays() && isBoneCorpse()) {
													  if (targetsAllowed.contains(CTargetType.DEAD)) {
														  return true; // 目标合法
													  } else {
														  receiver.targetCheckFailed(
																  CommandStringErrorKeys.TARGET_MUST_BE_LIVING); // 目标必须是活的
													  }
												  } else {
													  receiver.targetCheckFailed(
															  CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION); // 必须目标此动作的单位
												  }
											  } else {
												  if (!targetsAllowed.contains(CTargetType.DEAD)
														  || targetsAllowed.contains(CTargetType.ALIVE)) {
													  return true; // 目标合法
												  } else {
													  receiver.targetCheckFailed(
															  CommandStringErrorKeys.MUST_TARGET_A_CORPSE); // 必须目标尸体
												  }
											  }
										  } else {
											  receiver.targetCheckFailed(
													  CommandStringErrorKeys.UNABLE_TO_TARGET_HEROES); // 无法目标英雄
										  }
									  } else {
										  receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_HERO); // 必须目标英雄
									  }
								  } else {
									  if (invulnerable) {
										  receiver.targetCheckFailed(
												  CommandStringErrorKeys.THAT_TARGET_IS_INVULNERABLE); // 目标是无敌的
									  } else {
										  receiver.targetCheckFailed(
												  CommandStringErrorKeys.UNABLE_TO_TARGET_THIS_UNIT); // 无法目标此单位
									  }
								  }
							  } else {
								  receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_ANCIENTS); // 无法目标古代单位
							  }
						  } else {
							  receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_AN_ANCIENT); // 必须目标古代单位
						  }
					  } else {
						  receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_ORGANIC_UNITS); // 必须目标有机单位
					  }
				  } else {
					  receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_ORGANIC_UNITS); // 无法目标有机单位
				  }
			  } else {
				  receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_FRIENDLY_UNIT); // 必须目标友军单位
			  }
		  } else {
			  receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_AN_ENEMY_UNIT); // 必须目标敌军单位
		  }
		}
		else {
			// 检查目标单位类型是否符合要求
			if (this.unitType.getTargetedAs().contains(CTargetType.GROUND) // 如果单位类型包含地面目标
				&& !targetsAllowed.contains(CTargetType.GROUND)) { // 但不允许攻击地面目标
					receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_GROUND_UNITS); // 目标检查失败，无法攻击地面单位
			}
			else if (this.unitType.getTargetedAs().contains(CTargetType.STRUCTURE) // 如果单位类型包含建筑目标
				&& !targetsAllowed.contains(CTargetType.STRUCTURE)) { // 但不允许攻击建筑
					receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_BUILDINGS); // 目标检查失败，无法攻击建筑
			}
			else if (this.unitType.getTargetedAs().contains(CTargetType.AIR) // 如果单位类型包含空中目标
				&& !targetsAllowed.contains(CTargetType.AIR)) { // 但不允许攻击空中目标
					receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_AIR_UNITS); // 目标检查失败，无法攻击空中单位
			}
			else if (this.unitType.getTargetedAs().contains(CTargetType.WARD) // 如果单位类型包含守卫目标
				&& !targetsAllowed.contains(CTargetType.WARD)) { // 但不允许攻击守卫
					receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_WARDS); // 目标检查失败，无法攻击守卫
			}
			// 检查是否必须攻击特定类型的单位
			else if (targetsAllowed.contains(CTargetType.GROUND)) { // 如果必须攻击地面单位
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_GROUND_UNIT); // 目标检查失败，必须攻击一个地面单位
			}
			else if (targetsAllowed.contains(CTargetType.STRUCTURE)) { // 如果必须攻击建筑
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_BUILDING); // 目标检查失败，必须攻击一个建筑
			}
			else if (targetsAllowed.contains(CTargetType.AIR)) { // 如果必须攻击空中单位
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_AN_AIR_UNIT); // 目标检查失败，必须攻击一个空中单位
			}
			else if (targetsAllowed.contains(CTargetType.WARD)) { // 如果必须攻击守卫
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_WARD); // 目标检查失败，必须攻击一个守卫
			}
			else { // 如果没有符合条件的目标类型
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_THIS_UNIT); // 目标检查失败，无法攻击这个单位
			}

		}
		return false;
	}

	// 是否禁止移动
	public boolean isMovementDisabled() {
		return (this.moveBehavior == null) || this.moveDisabled;
		// TODO this used to directly return the state of whether our unit was a
		// building. Will it be a problem that I changed it?
		// I was trying to fix attack move on stationary units which was crashing
	}
	// 判断在水上移动是否被允许
	public boolean isMovementOnWaterAllowed() {
		return !isMovementDisabled() && getMovementType().isPathable((short) ~PathingFlags.UNSWIMABLE);
	}

	// 获取单位的移动类型
	public MovementType getMovementType() {
		if (isMovementDisabled()) {
			return MovementType.DISABLED;
		}
		return getUnitType().getMovementType(); // later maybe it has unit instance override for windwalk, so this
												// wrapper exists to later mod
	}

	// 获取单位的获取范围
	public float getAcquisitionRange() {
		return this.acquisitionRange;
	}

	// 设置单位的获取范围
	public void setAcquisitionRange(final float acquisitionRange) {
		this.acquisitionRange = acquisitionRange;
	}

	// 设置单位的当前生命值
	public void setCurrentHp(final CSimulation game, final float hpValue) {
		setLife(game, Math.min(hpValue, getMaximumLife()));
	}

	// 恢复单位的生命值
	public void heal(final CSimulation game, final float lifeToRegain) {
		setLife(game, Math.min(getLife() + lifeToRegain, getMaximumLife()));
	}

	// 恢复单位的法力值
	public void restoreMana(final CSimulation game, final float manaToRegain) {
		setMana(Math.min(getMana() + manaToRegain, getMaximumMana()));
	}

	// 复活单位
	public void resurrect(final CSimulation simulation) {
		// 移除当前单位从世界碰撞检测中
		simulation.getWorldCollision().removeUnit(this);

		// 重置当前单位为非尸体状态
		this.corpse = false;
		// 重置当前单位为非骨骼尸体状态
		this.boneCorpse = false;
		// 重置死亡回合数
		this.deathTurnTick = 0;
		// 重置死亡时不爆炸
		this.explodesOnDeath = false;
		// 重置死亡爆炸增益ID为空
		this.explodesOnDeathBuffId = null;

		// 设置当前单位生命值为最大生命值
		setLife(simulation, getMaximumLife());

		// 将当前单位重新添加到世界碰撞检测中
		simulation.getWorldCollision().addUnit(this);

		// 更新单位状态，清除一些旧状态
		simulation.unitUpdatedType(this, this.typeId);

		// 播放站立动画
		this.unitAnimationListener.playAnimation(true, PrimaryTag.STAND, SequenceUtils.EMPTY, 0.0f, true);

	}

	// 自动施法目标查找器枚举类：检测目标是否是技能能攻击的单位
	private static final class AutocastTargetFinderEnum implements CUnitEnumFunction {
		// 目标检查接收器实例
		private final static BooleanAbilityTargetCheckReceiver<CWidget> targetCheckReceiver = BooleanAbilityTargetCheckReceiver
				.<CWidget>getInstance();
		private CSimulation game; // 游戏实例
		private CUnit source; // 来源单位
		private CAutocastAbility ability; // 自动施法能力
		private boolean disableMove; // 禁用移动标志
		private AutocastType type; // 自动施法类型

		private CUnit currentUnitTarget; // 当前单位目标
		private double comparisonValue; // 比较值

		// 重置方法，初始化相关参数
		private AutocastTargetFinderEnum reset(final CSimulation game, final CUnit source,
				final CAutocastAbility ability, final boolean disableMove) {
			this.game = game;
			this.source = source;
			this.ability = ability;
			this.disableMove = disableMove;
			this.type = ability.getAutocastType();

			this.currentUnitTarget = null;
			this.comparisonValue = Double.NaN;
			return this;
		}

		// 调用接口方法，执行目标查找逻辑
		@Override
		public boolean call(final CUnit unit) {
			// 自动类型不为无
			if (this.type != AutocastType.NONE) {
				switch (this.type) {
				case ATTACKINGALLY: // 攻击友方单位的情况
				case ATTACKINGENEMY: // 攻击敌方单位的情况
					// 目标单位有当前行为，且行为为远程行为
					if ((unit.getCurrentBehavior() != null) && (unit.getCurrentBehavior() instanceof CRangedBehavior)) {
						// 获取目标远程行为
						final CRangedBehavior rbeh = (CRangedBehavior) unit.getCurrentBehavior();
						// 目标行为是攻击行为
						if (rbeh.getHighlightOrderId() == OrderIds.attack) {
							// 获取目标单位正在攻击的目标
							final AbilityTarget target = rbeh.getTarget();
							// 获取目标攻击的单位
							final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
							// 目标正在攻击的单位不为空
							if (targetUnit != null) {
								if (((this.type == AutocastType.ATTACKINGALLY) // 攻击友方单位的情况
										&& this.game.getPlayer(this.source.getPlayerIndex())
												.hasAlliance(targetUnit.getPlayerIndex(), CAllianceType.PASSIVE)) // 目标正在攻击友方单位
										|| ((this.type == AutocastType.ATTACKINGENEMY) // 在攻击敌方单位
												&& !this.game.getPlayer(this.source.getPlayerIndex()).hasAlliance(
														targetUnit.getPlayerIndex(), CAllianceType.PASSIVE))) { // 目标正在攻击不是敌方单位
									targetCheckReceiver.reset();
									// 检查单位是否可以自动瞄准目标
									this.ability.checkCanAutoTarget(this.game, this.source,
											this.ability.getBaseOrderId(), unit, targetCheckReceiver);
									// 目标可以自动瞄准
									if (targetCheckReceiver.isTargetable()) {
										// 当前单位目标为空, 设置当前目标为该单位，并设置与目标的距离作为和下一个目标比较
										if (this.currentUnitTarget == null) {
											this.currentUnitTarget = unit;
											this.comparisonValue = this.source.distance(unit);
										}
										else {
											// 否则 与之前的最近目标距离作比较，如果距离更近，则设置为当前目标
											final double dist = this.source.distance(unit);
											if (dist < this.comparisonValue) {
												this.currentUnitTarget = unit;
												this.comparisonValue = dist;
											}
										}
									}
								}
							}
						}
					}
					break;
				case ATTACKTARGETING: // 正在攻击目标的情况
					// 目标单位不是盟友，且不是死亡状态，且不是无敌状态，且不是休眠状态
					if (!this.game.getPlayer(this.source.getPlayerIndex()).hasAlliance(unit.getPlayerIndex(),
							CAllianceType.PASSIVE) && !unit.isDead() && !unit.isInvulnerable()
							&& !unit.isUnitType(CUnitTypeJass.SLEEPING)) {
						// 遍历自己的攻击列表
						for (final CUnitAttack attack : this.source.getCurrentAttacks()) {
							// 能够到达攻击目标范围， 目标单位能作为目标， 且攻击目标距离大于等于最小攻击距离
							if (this.source.canReach(unit, this.source.acquisitionRange)
									&& unit.canBeTargetedBy(this.game, this.source, attack.getTargetsAllowed())
									&& (this.source.distance(unit) >= this.source.getUnitType()
											.getMinimumAttackRange())) {
								targetCheckReceiver.reset();
								// 检查单位是否可以自动瞄准目标
								this.ability.checkCanAutoTarget(this.game, this.source, this.ability.getBaseOrderId(),
										unit, targetCheckReceiver);
								// 目标可以自动瞄准
								if (targetCheckReceiver.isTargetable()) {
									// 当前单位目标为空, 设置当前目标为该单位，并设置与目标的距离作为和下一个目标比较
									if (this.currentUnitTarget == null) {
										this.currentUnitTarget = unit;
										this.comparisonValue = this.source.distance(unit);
									}
									else {
										// 否则 与之前的最近目标距离作比较，如果距离更近，则设置为当前目标
										final double dist = this.source.distance(unit);
										if (dist < this.comparisonValue) {
											this.currentUnitTarget = unit;
											this.comparisonValue = dist;
										}
									}
								}
							}
						}
					}

					break;
				case HIGESTHP: // 最高生命值目标
					targetCheckReceiver.reset();
					// 检查单位是否可以自动瞄准目标
					this.ability.checkCanAutoTarget(this.game, this.source, this.ability.getBaseOrderId(), unit,
							targetCheckReceiver);
					// 目标可以自动瞄准
					if (targetCheckReceiver.isTargetable()) {
						// 当前单位目标为空, 设置当前目标为该单位，并设置目标的血条比例值作为和下一个目标比较
						if (this.currentUnitTarget == null) {
							this.currentUnitTarget = unit;
							this.comparisonValue = unit.getLife() / unit.getMaximumLife();
						}
						else {
							// 否则 与之前的最大血统值做比较，如果比之前的更高，则设置为当前目标
							final double ratio = unit.getLife() / unit.getMaximumLife();
							if (ratio > this.comparisonValue) {
								this.currentUnitTarget = unit;
								this.comparisonValue = ratio;
							}
						}
					}
					break;
				case LOWESTHP: // 最低生命值目标
					targetCheckReceiver.reset();
					// 检查单位是否可以自动瞄准目标
					this.ability.checkCanAutoTarget(this.game, this.source, this.ability.getBaseOrderId(), unit,
							targetCheckReceiver);
					// 目标可以自动瞄准
					if (targetCheckReceiver.isTargetable()) {
						if (unit.getLife() < unit.getMaximumLife()) {
							// 当前单位目标为空, 设置当前目标为该单位，并设置目标的血条比例值作为和下一个目标比较
							if (this.currentUnitTarget == null) {
								this.currentUnitTarget = unit;
								this.comparisonValue = unit.getLife() / unit.getMaximumLife();
							}
							else {
								// 否则 与之前的最小血统值做比较，如果比之前的更低，则设置为当前目标
								final double ratio = unit.getLife() / unit.getMaximumLife();
								if (ratio < this.comparisonValue) {
									this.currentUnitTarget = unit;
									this.comparisonValue = ratio;
								}
							}
						}
					}
					break;
				case NEARESTVALID: // 最近的有效目标
					targetCheckReceiver.reset();
					// 检查单位是否可以自动瞄准目标
					this.ability.checkCanAutoTarget(this.game, this.source, this.ability.getBaseOrderId(), unit,
							targetCheckReceiver);
					// 目标可以自动瞄准
					if (targetCheckReceiver.isTargetable()) {
						// 当前单位目标为空, 设置当前目标为该单位，并设置与目标的距离作为和下一个目标比较
						if (this.currentUnitTarget == null) {
							this.currentUnitTarget = unit;
							this.comparisonValue = this.source.distance(unit);
						}
						else {
							// 否则 与之前的最近目标距离作比较，如果距离更近，则设置为当前目标
							final double dist = this.source.distance(unit);
							if (dist < this.comparisonValue) {
								this.currentUnitTarget = unit;
								this.comparisonValue = dist;
							}
						}
					}
					break;
				case NEARESTENEMY: // 最近的敌方目标
					targetCheckReceiver.reset();
					// 检查单位是否可以自动瞄准目标
					this.ability.checkCanAutoTarget(this.game, this.source, this.ability.getBaseOrderId(), unit,
							targetCheckReceiver);
					// 目标可以自动瞄准，且目标不是友方
					if (targetCheckReceiver.isTargetable() && !this.game.getPlayer(this.source.getPlayerIndex())
							.hasAlliance(unit.getPlayerIndex(), CAllianceType.PASSIVE)) {
						// 当前单位目标为空, 设置当前目标为该单位，并设置与目标的距离作为和下一个目标比较
						if (this.currentUnitTarget == null) {
							this.currentUnitTarget = unit;
							this.comparisonValue = this.source.distance(unit);
						}
						else {
							// 否则 与之前的最近目标距离作比较，如果距离更近，则设置为当前目标
							final double dist = this.source.distance(unit);
							if (dist < this.comparisonValue) {
								this.currentUnitTarget = unit;
								this.comparisonValue = dist;
							}
						}
					}
					break;
				case NONE: // 无目标
				case NOTARGET: // 没有目标
				default:
					break;

				}
			}

			return false; // 最终返回 false
		}
	}

	// 查找可攻击的目标，并将目标设置给攻击行为，启动攻击行为
	private static final class AutoAttackTargetFinderEnum implements CUnitEnumFunction {
		private CSimulation game;
		private CUnit source;
		private boolean disableMove;
		private boolean foundAnyTarget;

		// 重置函数，初始化游戏、源单位和其他参数
		private AutoAttackTargetFinderEnum reset(final CSimulation game, final CUnit source,
				final boolean disableMove) {
			this.game = game;
			this.source = source;
			this.disableMove = disableMove;
			this.foundAnyTarget = false;
			return this;
		}

		@Override
		// 调用函数，判断单位是否可作为攻击目标并执行攻击行为
		public boolean call(final CUnit unit) {
			// 攻击行为存在， 并且攻击类型的技能可用
			if ((this.source.getAttackBehavior() != null)
					&& !this.source.getFirstAbilityOfType(CAbilityAttack.class).isDisabled()) {
				// TODO this "attack behavior null" check was added for some weird Root edge
				// case with NE, maybe
				// refactor it later
				// 目标不是同盟，目标没有死亡， 目标不是无敌
				if (!this.game.getPlayer(this.source.getPlayerIndex()).hasAlliance(unit.getPlayerIndex(),
						CAllianceType.PASSIVE) && !unit.isDead() && !unit.isInvulnerable()
						&& !unit.isUnitType(CUnitTypeJass.SLEEPING)) {
					// 遍历攻击列表
					for (final CUnitAttack attack : this.source.getCurrentAttacks()) {
						// 能够到达目标范围， 目标可以被攻击， 攻击距离大于最小攻击距离
						if (this.source.canReach(unit, this.source.acquisitionRange)
								&& unit.canBeTargetedBy(this.game, this.source, attack.getTargetsAllowed())
								&& (this.source.distance(unit) >= this.source.getUnitType().getMinimumAttackRange())) {
							// 目标不是虚无单位，目标不是魔法攻击， 目标不是法术攻击， 目标不是魔法免疫单位， 目标不是魔法免疫单位的魔法攻击， 目标不是魔法免疫单位的法术攻击， 目标不是魔法免疫单位的物理攻击， 目标不是物理免疫单位， 目标不是物理免疫单位的物理攻击， 目标不是物理免疫单位的魔法攻击， 目标不是物理免疫单位的法术攻击， 目标不是物理免疫单位的物理攻击， 目标不是物理免疫单位的物理攻击， 目标不是物理免疫单位的物理攻击， 目标不是物理免疫单位的物理攻击， 目标不是物理免疫单位的物理攻击，
							if (!(unit.isUnitType(CUnitTypeJass.ETHEREAL) // 目标不是虚无单位
									&& (attack.getAttackType() != CAttackType.MAGIC) // 攻击不是魔法攻击
									&& (attack.getAttackType() != CAttackType.SPELLS)) // 攻击不是法术攻击
									&& !(this.game.getGameplayConstants().isMagicImmuneResistsDamage() // 魔法免疫单位的魔法攻击
											&& unit.isUnitType(CUnitTypeJass.MAGIC_IMMUNE)  // 目标是魔法免疫状态
											&& (attack.getAttackType() == CAttackType.MAGIC))) { // 攻击魔法攻击
								// 结束当前行为
								if (this.source.currentBehavior != null) {
									this.source.currentBehavior.end(this.game, false);
								}
								// 重置攻击行为 目标
								this.source.currentBehavior = this.source.getAttackBehavior().reset(OrderIds.attack,
										attack, unit, this.disableMove, CBehaviorAttackListener.DO_NOTHING);
								// 开始攻击行为
								this.source.currentBehavior.begin(this.game);
								// 标记找到攻击目标
								this.foundAnyTarget = true;
								return true;
							}
						}
					}
				}
			}
			return false;
		}
	}

	// 移动行为
	public CBehaviorMove getMoveBehavior() {
		return this.moveBehavior;
	}

	public void setMoveBehavior(final CBehaviorMove moveBehavior) {
		this.moveBehavior = moveBehavior;
	}

	public CBehaviorAttack getAttackBehavior() {
		return this.attackBehavior;
	}

	public void setAttackBehavior(final CBehaviorAttack attackBehavior) {
		this.attackBehavior = attackBehavior;
	}

	public void setAttackMoveBehavior(final CBehaviorAttackMove attackMoveBehavior) {
		this.attackMoveBehavior = attackMoveBehavior;
	}

	public CBehaviorAttackMove getAttackMoveBehavior() {
		return this.attackMoveBehavior;
	}

	public CBehaviorStop getStopBehavior() {
		return this.stopBehavior;
	}

	public void setFollowBehavior(final CBehaviorFollow followBehavior) {
		this.followBehavior = followBehavior;
	}

	public void setPatrolBehavior(final CBehaviorPatrol patrolBehavior) {
		this.patrolBehavior = patrolBehavior;
	}

	public void setHoldPositionBehavior(final CBehaviorHoldPosition holdPositionBehavior) {
		this.holdPositionBehavior = holdPositionBehavior;
	}

	public void setBoardTransportBehavior(final CBehaviorBoardTransport boardTransportBehavior) {
		this.boardTransportBehavior = boardTransportBehavior;
	}

	public CBehaviorFollow getFollowBehavior() {
		return this.followBehavior;
	}

	public CBehaviorPatrol getPatrolBehavior() {
		return this.patrolBehavior;
	}

	public CBehaviorHoldPosition getHoldPositionBehavior() {
		return this.holdPositionBehavior;
	}

	public CBehaviorBoardTransport getBoardTransportBehavior() {
		return this.boardTransportBehavior;
	}

	// 处理下一个指令行为 或者 默认行为
	public CBehavior pollNextOrderBehavior(final CSimulation game) {
		// 如果默认行为与停止行为不相同，则返回当前的默认行为。
		if (this.defaultBehavior != this.stopBehavior) {
			// kind of a stupid hack, meant to align in feel with some behaviors that were
			// observed on War3
			// 这行代码检查当前单位的默认行为（defaultBehavior）与停止行为（stopBehavior）是否相同。这主要用于判断单位当前是否是在执行特定的行为而不是处于停止状态。
			return this.defaultBehavior;
		}
		// 这行代码检查当前是否有一个中断行为（interruptedBehavior），如果有，则意味着单位在执行一个特定的动作被中断了。
		if (this.interruptedBehavior != null) {
			return this.interruptedBehavior;
		}
		// 从指令队列中获取下一个指令：
		final COrder order = this.orderQueue.poll();
		final CBehavior nextOrderBehavior = beginOrder(game, order);
		this.stateNotifier.waypointsChanged();
		return nextOrderBehavior;
	}

	/**
	 * 判断单位是否正在移动。
	 * @return 如果当前行为是移动，则返回true，否则返回false。
	 */
	public boolean isMoving() {
		return getCurrentBehavior() instanceof CBehaviorMove;
	}

	/**
	 * 设置单位是否正在建造中。
	 * @param constructing 如果单位正在建造中，则为true，否则为false。
	 */
	public void setConstructing(final boolean constructing) {
		this.constructing = constructing;
		if (constructing) {
			this.unitAnimationListener.playAnimation(true, PrimaryTag.BIRTH, SequenceUtils.EMPTY, 0.0f, true);
		}
	}

	/**
	 * 设置单位建造是否暂停。
	 * @param constructingPaused 如果建造暂停，则为true，否则为false。
	 */
	public void setConstructingPaused(final boolean constructingPaused) {
		this.constructingPaused = constructingPaused;
	}

	/**
	 * 设置建造进度。
	 * @param constructionProgress 建造进度值。
	 */
	public void setConstructionProgress(final float constructionProgress) {
		this.constructionProgress = constructionProgress;
	}

	/**
	 * 判断单位是否正在建造中。
	 * @return 如果单位正在建造中且没有升级类型，则返回true，否则返回false。
	 */
	public boolean isConstructing() {
		return this.constructing && (this.upgradeIdType == null);
	}

	/**
	 * 判断单位建造是否暂停。
	 * @return 如果建造暂停，则返回true，否则返回false。
	 */
	public boolean isConstructingPaused() {
		return this.constructingPaused;
	}

	/**
	 * 判断单位是否正在升级。
	 * @return 如果单位正在建造中且有升级类型，则返回true，否则返回false。
	 */
	public boolean isUpgrading() {
		return this.constructing && (this.upgradeIdType != null);
	}

	/**
	 * 获取单位的升级类型ID。
	 * @return 单位的升级类型ID。
	 */
	public War3ID getUpgradeIdType() {
		return this.upgradeIdType;
	}

	/**
	 * 判断单位是否正在建造或升级中。
	 * @return 如果单位正在建造中，则返回true，否则返回false。
	 */
	public boolean isConstructingOrUpgrading() {
		return this.constructing;
	}

	/**
	 * 获取建造进度。
	 * @return 建造进度值。
	 */
	public float getConstructionProgress() {
		return this.constructionProgress;
	}

	/**
	 * 设置单位是否隐藏。
	 * @param hidden 如果单位隐藏，则为true，否则为false。
	 */
	public void setHidden(final boolean hidden) {
		this.hidden = hidden;
		this.stateNotifier.hideStateChanged();
	}

	/**
	 * 设置单位是否暂停。
	 * @param paused 如果单位暂停，则为true，否则为false。
	 */
	public void setPaused(final boolean paused) {
		this.paused = paused;
	}

	/**
	 * 判断单位是否暂停。
	 * @return 如果单位暂停，则返回true，否则返回false。
	 */
	public boolean isPaused() {
		return this.paused;
	}

	/**
	 * 设置单位是否接受命令。
	 * @param acceptingOrders 如果单位接受命令，则为true，否则为false。
	 */
	public void setAcceptingOrders(final boolean acceptingOrders) {
		this.acceptingOrders = acceptingOrders;
	}

	/**
	 * 判断单位是否隐藏。
	 * @return 如果单位隐藏，则返回true，否则返回false。
	 */
	public boolean isHidden() {
		return this.hidden;
	}

	/**
	 * 设置单位是否无敌。
	 * @param invulnerable 如果单位无敌，则为true，否则为false。
	 */
	public void setInvulnerable(final boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	/**
	 * 判断单位是否无敌。
	 * @return 如果单位无敌，则返回true，否则返回false。
	 */
	@Override
	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	/**
	 * 设置当前单元内的工作人员。
	 *
	 * @param unit 要设置的工作人员所在的CUnit对象
	 */
	public void setWorkerInside(final CUnit unit) {
		this.workerInside = unit;
	}

	/**
	 * 获取当前单元内的工作人员。
	 *
	 * @return 当前单元内的CUnit对象，如果没有工作人员则返回null
	 */
	public CUnit getWorkerInside() {
		return this.workerInside;
	}
	// 此方法用于调整结构的位置，以避免卡住
	private void nudgeAround(final CSimulation simulation, final CUnit structure) {
		setPointAndCheckUnstuck(structure.getX(), structure.getY(), simulation);
	}

	// 此方法重写用于设置生命值并处理死亡状态
	@Override
	public void setLife(final CSimulation simulation, final float life) {
		final boolean wasDead = isDead();
		super.setLife(simulation, life);
		if (isDead() && !wasDead) {
			kill(simulation, null);
		}
		this.stateNotifier.lifeChanged();
	}
	/**
	 * 将一个建筑或研究项目添加到建造队列中。
	 * 如果队列中有空位，则设置队列项并返回true。
	 * 如果添加的是单位、研究或牺牲项目，则还会更新玩家的技术树。
	 *
	 * @param game         当前游戏实例
	 * @param rawcode      要添加的项目的原始代码
	 * @param queueItemType 要添加的项目的队列类型
	 * @return 如果成功添加到队列则返回true，否则返回false
	 */
	private boolean queue(final CSimulation game, final War3ID rawcode, final QueueItemType queueItemType) {
		for (int i = 0; i < this.buildQueue.length; i++) {
			if (this.buildQueue[i] == null) {
				setBuildQueueItem(game, i, rawcode, queueItemType);
				if ((queueItemType == QueueItemType.UNIT) || (queueItemType == QueueItemType.RESEARCH)
						|| (queueItemType == QueueItemType.SACRIFICE)) {
					final CPlayer player = game.getPlayer(this.playerIndex);
					player.addTechtreeInProgress(rawcode);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取当前的建造队列。
	 *
	 * @return 当前建造队列的数组
	 */
	public War3ID[] getBuildQueue() {
		return this.buildQueue;
	}

	/**
	 * 获取当前建造队列中各项的类型。
	 *
	 * @return 当前建造队列类型的数组
	 */
	public QueueItemType[] getBuildQueueTypes() {
		return this.buildQueueTypes;
	}

	/**
	 * 检查建造队列是否处于激活状态。
	 *
	 * @return 如果建造队列的第一个位置不为空，则返回true，表示队列激活
	 */
	public boolean isBuildQueueActive() {
		return this.buildQueueTypes[0] != null;
	}


	/**
	 * 获取建造队列剩余时间。
	 *
	 * @param simulation 模拟环境对象，用于获取相关数据。
	 * @return 建造队列剩余时间，如果建造队列不活跃则返回0。
	 */
	public float getBuildQueueTimeRemaining(final CSimulation simulation) {
		// 如果建造队列不活跃，直接返回0
		if (!isBuildQueueActive()) {
			return 0;
		}
		// 根据建造队列的第一个元素类型进行处理
		switch (this.buildQueueTypes[0]) {
			case RESEARCH: {
				// 获取研究项目的原始代码
				final War3ID rawcode = this.buildQueue[0];
				// 获取升级类型
				final CUpgradeType trainedUnitType = simulation.getUpgradeData().getType(rawcode);
				// 返回研究项目的剩余建造时间
				return trainedUnitType.getBuildTime(simulation.getPlayer(this.playerIndex).getTechtreeUnlocked(rawcode));
			}
			case SACRIFICE:
			case UNIT: {
				// 获取单位类型
				final CUnitType trainedUnitType = simulation.getUnitData().getUnitType(this.buildQueue[0]);
				// 返回单位的建造时间
				return trainedUnitType.getBuildTime();
			}
			case HERO_REVIVE: {
				// 获取英雄单位
				final CUnit hero = simulation.getUnit(this.buildQueue[0].getValue());
				// 获取英雄的单位类型
				final CUnitType trainedUnitType = hero.getUnitType();
				// 返回英雄复活的剩余时间
				return simulation.getGameplayConstants().getHeroReviveTime(trainedUnitType.getBuildTime(),
						hero.getHeroData().getHeroLevel());
			}
			default:
				// 默认情况下返回0
				return 0;
		}
	}


	public void cancelBuildQueueItem(final CSimulation game, final int cancelIndex) {
		if ((cancelIndex >= 0) && (cancelIndex < this.buildQueueTypes.length)) {
			final QueueItemType cancelledType = this.buildQueueTypes[cancelIndex];
			if (cancelledType != null) {
				// TODO refund here!
				if (cancelIndex == 0) {
					this.constructionProgress = 0.0f;
					switch (cancelledType) {
					case SACRIFICE:
					case RESEARCH: {
						break;
					}
					case UNIT: {
						final CPlayer player = game.getPlayer(this.playerIndex);
						final CUnitType unitType = game.getUnitData().getUnitType(this.buildQueue[cancelIndex]);
						player.setFoodUsed(player.getFoodUsed() - unitType.getFoodUsed());
						break;
					}
					case HERO_REVIVE: {
						final CPlayer player = game.getPlayer(this.playerIndex);
						final CUnitType unitType = game.getUnit(this.buildQueue[cancelIndex].getValue()).getUnitType();
						player.setFoodUsed(player.getFoodUsed() - unitType.getFoodUsed());
						break;
					}
					}
				}
				switch (cancelledType) {
				case RESEARCH: {
					final CPlayer player = game.getPlayer(this.playerIndex);
					final CUpgradeType upgradeType = game.getUpgradeData().getType(this.buildQueue[cancelIndex]);
					player.refundFor(upgradeType);
					player.removeTechtreeInProgress(this.buildQueue[cancelIndex]);
					break;
				}
				case UNIT: {
					final CPlayer player = game.getPlayer(this.playerIndex);
					final CUnitType unitType = game.getUnitData().getUnitType(this.buildQueue[cancelIndex]);
					player.refundFor(unitType);
					player.removeTechtreeInProgress(this.buildQueue[cancelIndex]);
					break;
				}
				case SACRIFICE: {
					final CPlayer player = game.getPlayer(this.playerIndex);
					final CUnitType unitType = game.getUnitData().getUnitType(this.buildQueue[cancelIndex]);
					player.refundFor(unitType);
					player.removeTechtreeInProgress(this.buildQueue[cancelIndex]);

					getWorkerInside().setHidden(false);
					break;
				}
				case HERO_REVIVE: {
					final CPlayer player = game.getPlayer(this.playerIndex);
					final CUnit hero = game.getUnit(this.buildQueue[cancelIndex].getValue());
					final CUnitType unitType = hero.getUnitType();
					final CAbilityHero heroData = hero.getHeroData();
					heroData.setReviving(false);
					final CGameplayConstants gameplayConstants = game.getGameplayConstants();
					player.refund(
							gameplayConstants.getHeroReviveGoldCost(unitType.getGoldCost(), heroData.getHeroLevel()),
							gameplayConstants.getHeroReviveLumberCost(unitType.getLumberCost(),
									heroData.getHeroLevel()));
					break;
				}
				}
				for (int i = cancelIndex; i < (this.buildQueueTypes.length - 1); i++) {
					setBuildQueueItem(game, i, this.buildQueue[i + 1], this.buildQueueTypes[i + 1]);
				}
				setBuildQueueItem(game, this.buildQueue.length - 1, null, null);
				this.stateNotifier.queueChanged();
			}
		}
	}

	public void setBuildQueueItem(final CSimulation game, final int index, final War3ID rawcode,
			final QueueItemType queueItemType) {
		this.buildQueue[index] = rawcode;
		this.buildQueueTypes[index] = queueItemType;
		final CPlayer player = game.getPlayer(this.playerIndex);
		if (index == 0) {
			this.queuedUnitFoodPaid = true;
			if (rawcode != null) {
				if (queueItemType == QueueItemType.UNIT) {
					final CUnitType unitType = game.getUnitData().getUnitType(this.buildQueue[index]);
					if (unitType.getFoodUsed() != 0) {
						final int newFoodUsed = player.getFoodUsed() + unitType.getFoodUsed();
						if (newFoodUsed <= player.getFoodCap()) {
							player.setFoodUsed(newFoodUsed);
						}
						else {
							this.queuedUnitFoodPaid = false;
							game.getCommandErrorListener().showInterfaceError(this.playerIndex,
									CommandStringErrorKeys.NOT_ENOUGH_FOOD);
							player.removeTechtreeInProgress(rawcode);
						}
					}
				}
				else if (queueItemType == QueueItemType.HERO_REVIVE) {
					final CUnitType unitType = game.getUnit(this.buildQueue[index].getValue()).getUnitType();
					if (unitType.getFoodUsed() != 0) {
						final int newFoodUsed = player.getFoodUsed() + unitType.getFoodUsed();
						if (newFoodUsed <= player.getFoodCap()) {
							player.setFoodUsed(newFoodUsed);
						}
						else {
							this.queuedUnitFoodPaid = false;
							game.getCommandErrorListener().showInterfaceError(this.playerIndex,
									CommandStringErrorKeys.NOT_ENOUGH_FOOD);
						}
					}
				}
			}
		}
	}
	// 队列训练单位
	public void queueTrainingUnit(final CSimulation game, final War3ID rawcode) {
		// 如果队列中存在游戏单位，则执行以下操作
		if (queue(game, rawcode, QueueItemType.UNIT)) {
			  // 获取当前玩家对象
			  final CPlayer player = game.getPlayer(this.playerIndex);
			  // 根据原始代码获取单位类型
			  final CUnitType unitType = game.getUnitData().getUnitType(rawcode);
			  // 判断单位类型是否为英雄类型
			  final boolean isHeroType = unitType.isHero();
			  // 如果是英雄类型并且玩家拥有英雄令牌
			  if (isHeroType && (player.getHeroTokens() > 0)) {
				  // 玩家使用一个英雄令牌
				  player.setHeroTokens(player.getHeroTokens() - 1);
			  }
			  // 如果不是英雄类型或者玩家没有英雄令牌
			  else {
				  // 玩家为单位类型充值
				  player.chargeFor(unitType);
			  }
		}

	}

	// 队列牺牲单位
	public void queueSacrificingUnit(final CSimulation game, final War3ID rawcode, final CUnit sacrifice) {
		// 如果队列中存在游戏、原始代码和队列项类型为SACRIFICE的情况，则执行以下操作
		if (queue(game, rawcode, QueueItemType.SACRIFICE)) {
			// 将sacrifice设置为隐藏
			sacrifice.setHidden(true);
			// 将工人设置在sacrifice内部
			setWorkerInside(sacrifice);

			// 获取当前玩家对象
			final CPlayer player = game.getPlayer(this.playerIndex);
			// 根据原始代码获取单位类型
			final CUnitType unitType = game.getUnitData().getUnitType(rawcode);
			// 判断单位类型是否为英雄类型
			final boolean isHeroType = unitType.isHero();
			// 如果是英雄类型且玩家拥有英雄令牌，则减少一个英雄令牌
			if (isHeroType && (player.getHeroTokens() > 0)) {
				player.setHeroTokens(player.getHeroTokens() - 1);
			}
			// 否则，玩家为单位类型充值
			else {
				player.chargeFor(unitType);
			}
		}

	}

	// 队列复活英雄
	public void queueRevivingHero(final CSimulation game, final CUnit hero) {
		if (queue(game, new War3ID(hero.getHandleId()), QueueItemType.HERO_REVIVE)) {
			hero.getHeroData().setReviving(true);
			final CPlayer player = game.getPlayer(this.playerIndex);
			final int heroReviveGoldCost = game.getGameplayConstants()
					.getHeroReviveGoldCost(hero.getUnitType().getGoldCost(), hero.getHeroData().getHeroLevel());
			final int heroReviveLumberCost = game.getGameplayConstants()
					.getHeroReviveLumberCost(hero.getUnitType().getGoldCost(), hero.getHeroData().getHeroLevel());
			player.charge(heroReviveGoldCost, heroReviveLumberCost);
		}
	}

	// 队列研究技术
	public void queueResearch(final CSimulation game, final War3ID rawcode) {
		if (queue(game, rawcode, QueueItemType.RESEARCH)) {
			final CPlayer player = game.getPlayer(this.playerIndex);
			final CUpgradeType upgradeType = game.getUpgradeData().getType(rawcode);
			player.chargeFor(upgradeType);
		}
	}

	// 队列项类型枚举
	public static enum QueueItemType {
		UNIT, // 单位
		RESEARCH, // 研究一个技术或能力。
		HERO_REVIVE, // 复活一个英雄单位
		SACRIFICE; // 牺牲某个单位以取得某种效果。
	}

	// 设置集结点
	public void setRallyPoint(final AbilityTarget target) {
		this.rallyPoint = target;
		this.stateNotifier.rallyPointChanged();
	}

	// 内部发布英雄状态变化
	public void internalPublishHeroStatsChanged() {
		this.stateNotifier.heroStatsChanged();
	}

	// 获取集结点
	public AbilityTarget getRallyPoint() {
		return this.rallyPoint;
	}

	// 集结点提供者接口
	private static interface RallyProvider {
		float getX();

		float getY();
	}

	// 访问者模式的实现
	@Override
	public <T> T visit(final AbilityTargetVisitor<T> visitor) {
		return visitor.accept(this);
	}

	// 访问者模式的实现
	@Override
	public <T> T visit(final CWidgetVisitor<T> visitor) {
		return visitor.accept(this);
	}


	// 根据指令和目标检测能使用的能力，如果有就执行该指令
	private static final class UseAbilityOnTargetByIdVisitor implements AbilityTargetVisitor<Void> {
		private static final UseAbilityOnTargetByIdVisitor INSTANCE = new UseAbilityOnTargetByIdVisitor();
		private CSimulation game;
		private CUnit trainedUnit; // 受训单位
		private int rallyOrderId; // 召集指令

		// 重置访问者的状态
		private UseAbilityOnTargetByIdVisitor reset(final CSimulation game, final CUnit trainedUnit,
				final int rallyOrderId) {
			this.game = game;
			this.trainedUnit = trainedUnit;
			this.rallyOrderId = rallyOrderId;
			return this;
		}

		// 接受AbilityPointTarget类型的目标并执行相应能力
		@Override
		public Void accept(final AbilityPointTarget target) {
			CAbility abilityToUse = null;
			// 遍历单位所有能力
			for (final CAbility ability : this.trainedUnit.getAbilities()) {
				// 检测能力能否使用
				ability.checkCanUse(this.game, this.trainedUnit, this.rallyOrderId,
						BooleanAbilityActivationReceiver.INSTANCE);
				// 如果能使用
				if (BooleanAbilityActivationReceiver.INSTANCE.isOk()) {
					// 检测能力能否使用在该目标上
					final BooleanAbilityTargetCheckReceiver<AbilityPointTarget> targetCheckReceiver = BooleanAbilityTargetCheckReceiver
							.<AbilityPointTarget>getInstance().reset();
					ability.checkCanTarget(this.game, this.trainedUnit, this.rallyOrderId, target, targetCheckReceiver);
					// 如果能使用在该目标
					if (targetCheckReceiver.isTargetable()) {
						// 设置能使用的技能
						abilityToUse = ability;
					}
				}
			}
			// 如果有技能可以使用
			if (abilityToUse != null) {
				// 执行指令
				this.trainedUnit.order(this.game,
						new COrderTargetPoint(abilityToUse.getHandleId(), this.rallyOrderId, target, false), false);
			}
			return null;
		}

		// 接受CUnit类型的目标并执行相应能力
		@Override
		public Void accept(final CUnit targetUnit) {
			return acceptWidget(this.game, this.trainedUnit, this.rallyOrderId, targetUnit);
		}

		// 接受CWidget类型的目标并执行相应能力
		private Void acceptWidget(final CSimulation game, final CUnit trainedUnit, final int rallyOrderId,
				final CWidget target) {
			CAbility abilityToUse = null;
			// 遍历单位所有能力
			for (final CAbility ability : trainedUnit.getAbilities()) {
				// 检测能力能否使用
				ability.checkCanUse(game, trainedUnit, rallyOrderId, BooleanAbilityActivationReceiver.INSTANCE);
				// 如果能使用
				if (BooleanAbilityActivationReceiver.INSTANCE.isOk()) {
					// 检测能力能否使用在该目标上
					final BooleanAbilityTargetCheckReceiver<CWidget> targetCheckReceiver = BooleanAbilityTargetCheckReceiver
							.<CWidget>getInstance().reset();
					ability.checkCanTarget(game, trainedUnit, rallyOrderId, target, targetCheckReceiver);
					// 如果能使用在该目标
					if (targetCheckReceiver.isTargetable()) {
						// 设置能使用的技能
						abilityToUse = ability;
					}
				}
			}
			// 如果有技能可以使用
			if (abilityToUse != null) {
				// 执行指令
				trainedUnit.order(game,
						new COrderTargetWidget(abilityToUse.getHandleId(), rallyOrderId, target.getHandleId(), false),
						false);
			}
			return null;
		}

		// 接受CDestructable类型的目标并执行相应能力
		@Override
		public Void accept(final CDestructable target) {
			return acceptWidget(this.game, this.trainedUnit, this.rallyOrderId, target);
		}

		// 接受CItem类型的目标并执行相应能力
		@Override
		public Void accept(final CItem target) {
			return acceptWidget(this.game, this.trainedUnit, this.rallyOrderId, target);
		}
	}
	// 获取已制作的食物数量
	public int getFoodMade() {
		return this.foodMade;
	}

	// 获取已使用的食物数量
	public int getFoodUsed() {
		return this.foodUsed;
	}

	// 设置已制作的食物数量，并返回制作数量的变化
	public int setFoodMade(final int foodMade) {
		final int delta = foodMade - this.foodMade;
		this.foodMade = foodMade;
		return delta;
	}

	// 设置已使用的食物数量，并返回使用数量的变化
	public int setFoodUsed(final int foodUsed) {
		// 计算并返回食物使用量的变化量
		// foodUsed: 当前的食物使用量
		final int delta = foodUsed - this.foodUsed; // 计算变化量
		this.foodUsed = foodUsed; // 更新当前的食物使用量
		return delta; // 返回变化量

	}

	// 设置默认行为
	public void setDefaultBehavior(final CBehavior defaultBehavior) {
		this.defaultBehavior = defaultBehavior;
	}

	// 获取金矿技能数据
	public CAbilityGoldMinable getGoldMineData() {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityGoldMinable) {
				return (CAbilityGoldMinable) ability;
			}
		}
		return null;
	}

	// 获取叠加金矿技能数据
	public CAbilityOverlayedMine getOverlayedGoldMineData() {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityOverlayedMine) {
				return (CAbilityOverlayedMine) ability;
			}
		}
		return null;
	}

	// 获取金钱数量
	public int getGold() {
		// 遍历当前对象的所有能力
		for (final CAbility ability : this.abilities) {
			// 如果能力是可开采黄金的能力
			if (ability instanceof CAbilityGoldMinable) {
				// 返回这种能力可以开采的黄金数量
				return ((CAbilityGoldMinable) ability).getGold();
			}

			// 如果能力是覆盖型矿场的能力
			if (ability instanceof CAbilityOverlayedMine) {
				// 返回这种能力可以开采的黄金数量
				return ((CAbilityOverlayedMine) ability).getGold();
			}
		}
		// 如果没有任何能力可以开采黄金，则返回0
		return 0;

	}

	// 设置金钱数量
	public void setGold(final int goldAmount) {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityGoldMinable) {
				((CAbilityGoldMinable) ability).setGold(goldAmount);
			}
			if (ability instanceof CAbilityOverlayedMine) {
				((CAbilityOverlayedMine) ability).setGold(goldAmount);
			}
		}
	}

	// 获取订单队列
	public Queue<COrder> getOrderQueue() {
		return this.orderQueue;
	}

	// 获取当前订单
	public COrder getCurrentOrder() {
		return this.lastStartedOrder;
	}

	// 获取英雄技能数据
	public CAbilityHero getHeroData() {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityHero) {
				return (CAbilityHero) ability;
			}
		}
		return null;
	}

	// 获取根技能数据
	public CAbilityRoot getRootData() {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityRoot) {
				return (CAbilityRoot) ability;
			}
		}
		return null;
	}

	// 获取背包技能数据
	public CAbilityInventory getInventoryData() {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityInventory) {
				return (CAbilityInventory) ability;
			}
		}
		return null;
	}

	// 获取中立建筑技能数据
	public CAbilityNeutralBuilding getNeutralBuildingData() {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityNeutralBuilding) {
				return (CAbilityNeutralBuilding) ability;
			}
		}
		return null;
	}

	// 获取货仓技能
	public CAbilityCargoHold getCargoData() {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityCargoHold) {
				return (CAbilityCargoHold) ability;
			}
		}
		return null;
	}

	// 设置所有普攻列表
	public void setUnitSpecificAttacks(final List<CUnitAttack> unitSpecificAttacks) {
		this.unitSpecificAttacks = unitSpecificAttacks;
	}

	// 设置能用的普攻列表
	public void setUnitSpecificCurrentAttacks(final List<CUnitAttack> unitSpecificCurrentAttacks) {
		this.unitSpecificCurrentAttacks = unitSpecificCurrentAttacks;
		computeDerivedFields(NonStackingStatBuffType.ATKSPD);
	}

	// 获取所有普攻列表
	public List<CUnitAttack> getUnitSpecificAttacks() {
		return this.unitSpecificAttacks;
	}

	// 获取能用的普攻列表
	public List<CUnitAttack> getCurrentAttacks() {
		if (this.disableAttacks) {
			return Collections.emptyList();
		}
		if (this.unitSpecificCurrentAttacks != null) {
			return this.unitSpecificCurrentAttacks;
		}
		return Collections.emptyList();
	}

	// 设置是否禁用普攻
	public void setDisableAttacks(final boolean disableAttacks) {
		this.disableAttacks = disableAttacks;
		this.stateNotifier.attacksChanged();
	}

	// 检查是否禁用普攻
	public boolean isDisableAttacks() {
		return this.disableAttacks;
	}

	// 拾取物品时的处理
	public void onPickUpItem(final CSimulation game, final CItem item, final boolean playUserUISounds) {
		this.stateNotifier.inventoryChanged();
		if (playUserUISounds) {
			game.unitPickUpItemEvent(this, item);
		}
		firePickUpItemEvents(game, item);
	}

	// 投放物品时的处理
	public void onDropItem(final CSimulation game, final CItem droppedItem, final boolean playUserUISounds) {
		this.stateNotifier.inventoryChanged();
		if (playUserUISounds) {
			game.unitDropItemEvent(this, droppedItem);
		}
	}

	// 检查单位是否在指定区域内
	public boolean isInRegion(final CRegion region) {
		return this.containingRegions.contains(region);
	}

	@Override
	// 获取最大生命值
	public float getMaxLife() {
		return this.maximumLife;
	}

	// 单位进入区域检测回调
	private static final class RegionCheckerImpl implements CRegionEnumFunction {
		private CUnit unit;
		private CRegionManager regionManager;

		// 重置区域检查器
		public RegionCheckerImpl reset(final CUnit unit, final CRegionManager regionManager) {
			this.unit = unit;
			this.regionManager = regionManager;
			return this;
		}

		@Override
		// 调用检查函数
		public boolean call(final CRegion region) {
			if (this.unit.containingRegions.add(region)) {
				if (!this.unit.priorContainingRegions.contains(region)) {
					this.regionManager.onUnitEnterRegion(this.unit, region);
				}
			}
			return false;
		}

	}

	// 检查单位是否为建筑
	public boolean isBuilding() {
		return this.structure;
	}

	// 设置单位是否为建筑
	public void setStructure(final boolean flag) {
		this.structure = flag;
	}

	// 单位移除时的处理
	public void onRemove(final CSimulation simulation) {
		final CPlayer player = simulation.getPlayer(this.playerIndex);
		if (WarsmashConstants.FIRE_DEATH_EVENTS_ON_REMOVEUNIT) {
			// Firing userspace triggers here causes items to appear around the player bases
			// in melee games.
			// (See "Remove creeps and critters from used start locations" implementation)
			setLife(simulation, 0);
		}
		else {
			if (!isDead()) {
				if (this.constructing) {
					player.removeTechtreeInProgress(this.unitType.getTypeId());
				}
				else {
					player.removeTechtreeUnlocked(simulation, this.unitType.getTypeId());
				}
			} // else techtree was removed upon death
			setHidden(true);
			// setting hidden to let things that refer to this before it gets garbage
			// collected see it as basically worthless
		}
		simulation.getWorldCollision().removeUnit(this);
	}

	private static enum StateListenerUpdateType {
		ADD, REMOVE;
	}

	private static final class StateListenerUpdate {
		private final CUnitStateListener listener;
		private final StateListenerUpdateType updateType;

		// 状态监听器更新
		public StateListenerUpdate(final CUnitStateListener listener, final StateListenerUpdateType updateType) {
			this.listener = listener;
			this.updateType = updateType;
		}

		// 获取状态监听器
		public CUnitStateListener getListener() {
			return this.listener;
		}

		// 获取更新类型
		public StateListenerUpdateType getUpdateType() {
			return this.updateType;
		}
	}

	// 取消升级处理
	public void cancelUpgrade(final CSimulation game) {
		final CPlayer player = game.getPlayer(this.playerIndex);
		player.setUnitFoodUsed(this, this.unitType.getFoodUsed());
		int goldCost, lumberCost;
		final CUnitType newUpgradeUnitType = game.getUnitData().getUnitType(this.upgradeIdType);
		if (game.getGameplayConstants().isRelativeUpgradeCosts()) {
			goldCost = newUpgradeUnitType.getGoldCost() - this.unitType.getGoldCost();
			lumberCost = newUpgradeUnitType.getLumberCost() - this.unitType.getLumberCost();
		}
		else {
			goldCost = newUpgradeUnitType.getGoldCost();
			lumberCost = newUpgradeUnitType.getLumberCost();
		}
		player.refund(goldCost, lumberCost);

		final Iterator<CAbility> abilityIterator = this.abilities.iterator();
		while (abilityIterator.hasNext()) {
			final CAbility ability = abilityIterator.next();
			if (ability instanceof CAbilityBuildInProgress) {
				abilityIterator.remove();
			}
			else {
				ability.setDisabled(false, CAbilityDisableType.CONSTRUCTION);
				ability.setIconShowing(true);
			}
		}
		checkDisabledAbilities(game, false);

		game.unitCancelUpgradingEvent(this, this.upgradeIdType);
		this.upgradeIdType = null;
		this.constructing = false;
		this.constructionProgress = 0;
		this.unitAnimationListener.playAnimation(true, PrimaryTag.STAND, SequenceUtils.EMPTY, 0.0f, true);
	}


	public void beginUpgrade(final CSimulation game, final War3ID rawcode) {
		this.upgradeIdType = rawcode;
		this.constructing = true;
		this.constructionProgress = 0;

		final CPlayer player = game.getPlayer(this.playerIndex);
		final CUnitType newUpgradeUnitType = game.getUnitData().getUnitType(rawcode);
		player.setUnitFoodUsed(this, newUpgradeUnitType.getFoodUsed());
		int goldCost, lumberCost;
		if (game.getGameplayConstants().isRelativeUpgradeCosts()) {
			goldCost = newUpgradeUnitType.getGoldCost() - this.unitType.getGoldCost();
			lumberCost = newUpgradeUnitType.getLumberCost() - this.unitType.getLumberCost();
		}
		else {
			goldCost = newUpgradeUnitType.getGoldCost();
			lumberCost = newUpgradeUnitType.getLumberCost();
		}
		player.charge(goldCost, lumberCost);
		add(game, new CAbilityBuildInProgress(game.getHandleIdAllocator().createId()));
		for (final CAbility ability : getAbilities()) {
			ability.visit(AbilityDisableWhileUpgradingVisitor.INSTANCE);
		}
		checkDisabledAbilities(game, true);
		player.addTechtreeInProgress(rawcode);

		game.unitUpgradingEvent(this, rawcode);
		this.unitAnimationListener.playAnimation(true, PrimaryTag.BIRTH, SequenceUtils.EMPTY, 0.0f, true);
	}

	public void setUnitState(final CSimulation game, final CUnitState whichUnitState, final float value) {
		switch (whichUnitState) {
		case LIFE:
			setLife(game, value);
			break;
		case MANA:
			setMana(value);
			break;
		case MAX_LIFE:
			setMaximumLife((int) value);
			break;
		case MAX_MANA:
			setMaximumMana((int) value);
			break;
		}
	}

	public float getUnitState(final CSimulation game, final CUnitState whichUnitState) {
		switch (whichUnitState) {
		case LIFE:
			return getLife();
		case MANA:
			return getMana();
		case MAX_LIFE:
			return getMaximumLife();
		case MAX_MANA:
			return getMaximumMana();
		}
		return 0;
	}

	public boolean isUnitType(final CUnitTypeJass whichUnitType) {
		switch (whichUnitType) {
		case HERO:
			return isHero();
		case DEAD:
			return isDead();
		case STRUCTURE:
			return isBuilding();

		case FLYING:
			return getUnitType().getTargetedAs().contains(CTargetType.AIR);
		case GROUND:
			return getUnitType().getTargetedAs().contains(CTargetType.GROUND);

		case ATTACKS_FLYING:
			for (final CUnitAttack attack : getCurrentAttacks()) {
				if (attack.getTargetsAllowed().contains(CTargetType.AIR)) {
					return true;
				}
			}
			return false;
		case ATTACKS_GROUND:
			for (final CUnitAttack attack : getCurrentAttacks()) {
				if (attack.getTargetsAllowed().contains(CTargetType.GROUND)) {
					return true;
				}
			}
			return false;

		case MELEE_ATTACKER:
			boolean hasAttacks = false;
			for (final CUnitAttack attack : getCurrentAttacks()) {
				if (attack.getWeaponType() != CWeaponType.NORMAL) {
					return false;
				}
				hasAttacks = true;
			}
			return hasAttacks;

		case RANGED_ATTACKER:
			for (final CUnitAttack attack : getCurrentAttacks()) {
				if (attack.getWeaponType() != CWeaponType.NORMAL) {
					return true;
				}
			}
			return false;

		case GIANT:
			return getUnitType().getClassifications().contains(CUnitClassification.GIANT);
		case SUMMONED:
			return getUnitType().getClassifications().contains(CUnitClassification.SUMMONED);
		case STUNNED:
			return getCurrentBehavior().getHighlightOrderId() == OrderIds.stunned;
		case PLAGUED:
			throw new UnsupportedOperationException(
					"cannot ask engine if unit is plagued: plague is not yet implemented");
		case SNARED:
			boolean isSnared = false;
			for (final StateModBuff buff : this.stateModBuffs) {
				if (buff.getBuffType() == StateModBuffType.SNARED) {
					if (buff.getValue() != 0) {
						isSnared = true;
					}
				}
			}
			return isSnared;
		case UNDEAD:
			return getUnitType().getClassifications().contains(CUnitClassification.UNDEAD);
		case MECHANICAL:
			return getUnitType().getClassifications().contains(CUnitClassification.MECHANICAL);
		case PEON:
			return getUnitType().getClassifications().contains(CUnitClassification.PEON);
		case SAPPER:
			return getUnitType().getClassifications().contains(CUnitClassification.SAPPER);
		case TOWNHALL:
			return getUnitType().getClassifications().contains(CUnitClassification.TOWNHALL);
		case ANCIENT:
			return this.unitType.getClassifications().contains(CUnitClassification.ANCIENT);

		case TAUREN:
			return getUnitType().getClassifications().contains(CUnitClassification.TAUREN);
		case POISONED:
			throw new UnsupportedOperationException(
					"cannot ask engine if unit is poisoned: poison is not yet implemented");
		case POLYMORPHED:
			throw new UnsupportedOperationException(
					"cannot ask engine if unit is POLYMORPHED: POLYMORPHED is not yet implemented");
		case SLEEPING:
			boolean isSleeping = false;
			for (final StateModBuff buff : this.stateModBuffs) {
				if (buff.getBuffType() == StateModBuffType.SLEEPING) {
					if (buff.getValue() != 0) {
						isSleeping = true;
					}
				}
			}
			return isSleeping;
		case RESISTANT:
			return this.resistant;
		case ETHEREAL:
			boolean isEthereal = false;
			for (final StateModBuff buff : this.stateModBuffs) {
				if (buff.getBuffType() == StateModBuffType.ETHEREAL) {
					if (buff.getValue() != 0) {
						isEthereal = true;
					}
				}
			}
			return isEthereal;
		case MAGIC_IMMUNE:
			return isMagicImmune();
		}
		return false;
	}

	public int getTriggerEditorCustomValue() {
		return this.triggerEditorCustomValue;
	}

	public void setTriggerEditorCustomValue(final int triggerEditorCustomValue) {
		this.triggerEditorCustomValue = triggerEditorCustomValue;
	}

	public static String maybeMeaningfulName(final CUnit unit) {
		if (unit == null) {
			return "null";
		}
		return unit.getUnitType().getName();
	}

	// 冷却改变事件
	public void fireCooldownsChangedEvent() {
		this.stateNotifier.ordersChanged();
	}

	public int getAbilityLevel(final War3ID abilityId) {
		final CLevelingAbility ability = getAbility(GetAbilityByRawcodeVisitor.getInstance().reset(abilityId));
		if (ability == null) {
			return 0;
		}
		else {
			return ability.getLevel();
		}
	}

	// 检测魔法值是否够消耗，如果够就消耗魔法值，否则返回false
	public boolean chargeMana(final int manaCost) {
		if (this.mana >= manaCost) {
			setMana(this.mana - manaCost);
			return true;
		}
		return false;
	}

	public void firePickUpItemEvents(final CSimulation game, final CItem item) {
		final List<CWidgetEvent> eventList = getEventList(JassGameEventsWar3.EVENT_UNIT_PICKUP_ITEM);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitPickupItemScope(
						JassGameEventsWar3.EVENT_UNIT_PICKUP_ITEM, event.getTrigger(), this, item));
			}
		}
		game.getPlayer(this.playerIndex).firePickUpItemEvents(this, item, game);
	}

	// 无目标 指令 事件
	public void fireOrderEvents(final CSimulation game, final COrderNoTarget order) {
		// 获取无目标事件列表
		final List<CWidgetEvent> eventList = getEventList(JassGameEventsWar3.EVENT_UNIT_ISSUED_ORDER);
		if (eventList != null) {
			// 遍历事件列表，执行事件
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitOrderScope(JassGameEventsWar3.EVENT_UNIT_ISSUED_ORDER,
						event.getTrigger(), this, order.getOrderId()));
			}
		}
		// 获取Player执行事件列表
		game.getPlayer(this.playerIndex).fireOrderEvents(this, game, order);
	}

	// 坐标点 指令 事件
	public void fireOrderEvents(final CSimulation game, final COrderTargetPoint order) {
		final List<CWidgetEvent> eventList = getEventList(JassGameEventsWar3.EVENT_UNIT_ISSUED_POINT_ORDER);
		if (eventList != null) {
			final AbilityPointTarget target = order.getTarget(game);
			for (final CWidgetEvent event : eventList) {
				event.fire(this,
						CommonTriggerExecutionScope.unitOrderPointScope(
								JassGameEventsWar3.EVENT_UNIT_ISSUED_POINT_ORDER, event.getTrigger(), this,
								order.getOrderId(), target.x, target.y));
			}
		}
		game.getPlayer(this.playerIndex).fireOrderEvents(this, game, order);
	}

	// 单位/可破坏物/物品 指令 事件
	public void fireOrderEvents(final CSimulation game, final COrderTargetWidget order) {
		final List<CWidgetEvent> eventList = getEventList(JassGameEventsWar3.EVENT_UNIT_ISSUED_TARGET_ORDER);
		if (eventList != null) {
			final CWidget target = order.getTarget(game);
			for (final CWidgetEvent event : eventList) {
				event.fire(this,
						CommonTriggerExecutionScope.unitOrderTargetScope(
								JassGameEventsWar3.EVENT_UNIT_ISSUED_TARGET_ORDER, event.getTrigger(), this,
								order.getOrderId(), target));
			}
		}
		game.getPlayer(this.playerIndex).fireOrderEvents(this, game, order);
	}

	public void fireConstructFinishEvents(final CSimulation game) {
		final CUnit constructingUnit = this.workerInside; // TODO incorrect for human/undead/ancient, etc, needs work
		final List<CWidgetEvent> eventList = getEventList(JassGameEventsWar3.EVENT_UNIT_CONSTRUCT_FINISH);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitConstructFinishScope(
						JassGameEventsWar3.EVENT_UNIT_CONSTRUCT_FINISH, event.getTrigger(), this, constructingUnit));
			}
		}
		game.getPlayer(this.playerIndex).fireConstructFinishEvents(this, game, constructingUnit);
	}

	public void fireTrainFinishEvents(final CSimulation game, final CUnit trainedUnit) {
		final List<CWidgetEvent> eventList = getEventList(JassGameEventsWar3.EVENT_UNIT_TRAIN_FINISH);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitTrainFinishScope(
						JassGameEventsWar3.EVENT_UNIT_TRAIN_FINISH, event.getTrigger(), this, trainedUnit));
			}
		}
		game.getPlayer(this.playerIndex).fireTrainFinishEvents(this, game, trainedUnit);
	}

	public void fireResearchFinishEvents(final CSimulation game, final War3ID researched) {
		final List<CWidgetEvent> eventList = getEventList(JassGameEventsWar3.EVENT_UNIT_RESEARCH_FINISH);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitResearchFinishScope(
						JassGameEventsWar3.EVENT_UNIT_RESEARCH_FINISH, event.getTrigger(), this, researched));
			}
		}
		game.getPlayer(this.playerIndex).fireResearchFinishEvents(this, game, researched);
	}
	/**
	 * 判断当前单位是否为英雄。
	 * @return 如果当前单位是英雄，则返回true；否则返回false。
	 */
	public boolean isHero() {
		// 检查获取英雄数据的方法返回值是否不为null，以确定当前单位是否为英雄
		return getHeroData() != null; // 未来可能需要用更好的性能来实现这个方法
	}

	/**
	 * 判断当前单位是否为指定玩家的盟友。
	 * @param whichPlayer 需要检查的玩家对象。
	 * @return 如果当前单位是指定玩家的盟友，则返回true；否则返回false。
	 */
	public boolean isUnitAlly(final CPlayer whichPlayer) {
		// 检查指定玩家是否与当前玩家存在被动同盟关系
		return whichPlayer.hasAlliance(getPlayerIndex(), CAllianceType.PASSIVE);
	}
	/**
	 * 如果可能，检查这是否是一个工人并派遣它去工作。
	 *
	 * @param game 当前游戏实例
	 * @param defaultResourceType 默认资源类型，如果没有携带资源则使用此类型
	 * @return 工人工作的资源类型，如果没有工人可以工作则返回null
	 */
	public ResourceType backToWork(final CSimulation game, final ResourceType defaultResourceType) {
		// 遍历所有能力，检查是否有采集能力
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityHarvest) {
				final CAbilityHarvest abilityHarvest = (CAbilityHarvest) ability;
				final int carriedResourceAmount = abilityHarvest.getCarriedResourceAmount();
				final ResourceType carriedResourceType = abilityHarvest.getCarriedResourceType();

				// 如果携带了资源
				if (carriedResourceAmount != 0) {
					switch (carriedResourceType) {
						case GOLD:
							// 如果携带的金子数量达到容量上限，返回基地
							if (carriedResourceAmount >= abilityHarvest.getGoldCapacity()) {
								abilityHarvest.getBehaviorReturnResources().reset(game);
								this.order(game, OrderIds.returnresources,
										abilityHarvest.getBehaviorReturnResources().findNearestDropoffPoint(game));
							}
							// 否则继续采集金子
							else {
								this.order(game, OrderIds.harvest, CBehaviorReturnResources.findNearestMine(this, game));
							}
							return ResourceType.GOLD;
						case LUMBER:
							// 如果携带的木材数量达到容量上限，返回基地
							if (carriedResourceAmount >= abilityHarvest.getLumberCapacity()) {
								abilityHarvest.getBehaviorReturnResources().reset(game);
								this.order(game, OrderIds.returnresources,
										abilityHarvest.getBehaviorReturnResources().findNearestDropoffPoint(game));
							}
							// 否则继续采集木材
							else {
								this.order(game, OrderIds.harvest, CBehaviorReturnResources.findNearestTree(this,
										abilityHarvest, game, abilityHarvest.getLastHarvestTarget()));
							}
							return ResourceType.LUMBER;
						default:
							// 抛出异常，不支持的资源类型
							throw new IllegalStateException(
									"Worker was carrying a resource of unsupported type: " + carriedResourceType);
					}
				}
				// 如果没有携带资源但之前有资源类型
				else if (carriedResourceType != null) {
					if (carriedResourceType == ResourceType.GOLD) {
						this.order(game, OrderIds.harvest, CBehaviorReturnResources.findNearestMine(this, game));
						return ResourceType.GOLD;
					}
					else if (carriedResourceType == ResourceType.LUMBER) {
						this.order(game, OrderIds.harvest, CBehaviorReturnResources.findNearestTree(this,
								abilityHarvest, game, abilityHarvest.getLastHarvestTarget()));
						return ResourceType.LUMBER;
					}
				}
				// 如果没有携带资源且没有之前的资源类型，使用默认资源类型
				else if (defaultResourceType != null) {
					if (((defaultResourceType == ResourceType.GOLD) || (abilityHarvest.getLumberCapacity() == 0))
							&& (abilityHarvest.getGoldCapacity() > 0)) {
						this.order(game, OrderIds.harvest, CBehaviorReturnResources.findNearestMine(this, game));
						return ResourceType.GOLD;
					}
					else if (abilityHarvest.getLumberCapacity() > 0) {
						this.order(game, OrderIds.harvest, CBehaviorReturnResources.findNearestTree(this,
								abilityHarvest, game, abilityHarvest.getLastHarvestTarget()));
						return ResourceType.LUMBER;
					}
				}
			}
		}

		// 如果没有工人可以工作，返回null
		return null;
	}


	public void notifyAttacksChanged() {
		this.stateNotifier.attacksChanged();
	}

	public void notifyOrdersChanged() {
		this.stateNotifier.ordersChanged();
	}

	public void setConstructionConsumesWorker(final boolean constructionConsumesWorker) {
		this.constructionConsumesWorker = constructionConsumesWorker;
	}

	public boolean isConstructionConsumesWorker() {
		return this.constructionConsumesWorker;
	}

	public CDefenseType getDefenseType() {
		return this.defenseType;
	}

	public void setDefenseType(final CDefenseType defenseType) {
		this.defenseType = defenseType;
	}

	public void updateFogOfWar(final CSimulation game) {
		// 如果单位没有死亡且没有被隐藏
		if (!isDead() && !this.hidden) {
			// 根据当前是白天还是夜晚，获取单位的视野半径
			final float sightRadius = game.isDay() ? this.unitType.getSightRadiusDay()
					: this.unitType.getSightRadiusNight();
			// 如果视野半径大于0
			if (sightRadius > 0) {
				// 计算视野半径的平方除以网格步长的平方，用于后续的距离判断
				final float radSq = (sightRadius * sightRadius)
						/ (CPlayerFogOfWar.GRID_STEP * CPlayerFogOfWar.GRID_STEP);
				// 获取玩家的战争迷雾对象
				final CPlayerFogOfWar fogOfWar = game.getPlayer(this.playerIndex).getFogOfWar();
				// 判断单位是否为飞行单位
				final boolean flying = getUnitType().getMovementType() == MovementType.FLY;
				// 获取单位的坐标和高度信息
				final float myX = getX();
				final float myY = getY();
				final int myZ = flying ? Integer.MAX_VALUE : game.getTerrainHeight(myX, myY);
				// 获取寻路网格对象
				final PathingGrid pathingGrid = game.getPathingGrid();
				// 设置当前位置的战争迷雾状态为可见
				fogOfWar.setState(pathingGrid.getFogOfWarIndexX(myX), pathingGrid.getFogOfWarIndexY(myY), (byte) 0);

				// 计算视野范围内的网格坐标
				final int myXi = pathingGrid.getFogOfWarIndexX(myX);
				final int myYi = pathingGrid.getFogOfWarIndexY(myY);
				final int maxXi = pathingGrid.getFogOfWarIndexX(myX + sightRadius);
				final int maxYi = pathingGrid.getFogOfWarIndexY(myY + sightRadius);
				// 遍历视野范围内的网格，更新战争迷雾状态
				for (int a = 1; a <= Math.max(maxYi - myYi, maxXi - myXi); a++) {
					// 计算当前方格到中心点的平方距离
					final int distance = a * a;

					// 检测上方： 检查是否在视野范围内且不是障碍物，并更新迷雾状态
					if ((distance <= radSq) // 如果距离小于等于半径的平方
							&& (flying || !pathingGrid.isBlockVision(myX, myY - ((a - 1) * CPlayerFogOfWar.GRID_STEP))) // 如果飞行或者下方没有障碍物阻挡视野
							&& (fogOfWar.getState(myXi, (myYi - a) + 1) == 0) // 如果目标位置的迷雾状态为0（即可见）
							&& (flying || game.isTerrainWater(myX, myY - (a * CPlayerFogOfWar.GRID_STEP)) // 如果飞行或者下方是水域
										|| (myZ > game.getTerrainHeight(myX, myY - (a * CPlayerFogOfWar.GRID_STEP)))  // 或者高度高于地形
										|| (!game.isTerrainRomp(myX, myY - (a * CPlayerFogOfWar.GRID_STEP)) && (myZ == game.getTerrainHeight(myX, myY - (a * CPlayerFogOfWar.GRID_STEP)))))) // 不是崎岖的地形，且高度相同
					{
						// 更新迷雾状态为可见
						fogOfWar.setState(myXi, myYi - a, (byte) 0);
					}

					// 检测下方
					if ((distance <= radSq)
							&& (flying || !pathingGrid.isBlockVision(myX, myY + ((a - 1) * CPlayerFogOfWar.GRID_STEP))) // 如果飞行或者下方没有障碍物阻挡视野
							&& (fogOfWar.getState(myXi, (myYi + a) - 1) == 0) // 如果目标位置的迷雾状态为0（即可见）
							&& (flying || game.isTerrainWater(myX, myY + (a * CPlayerFogOfWar.GRID_STEP))  // 如果飞行或者下方是水域
									|| (myZ > game.getTerrainHeight(myX, myY + (a * CPlayerFogOfWar.GRID_STEP))) // 或者高度高于地形
									|| (!game.isTerrainRomp(myX, myY + (a * CPlayerFogOfWar.GRID_STEP)) && (myZ == game
											.getTerrainHeight(myX, myY + (a * CPlayerFogOfWar.GRID_STEP)))))) {
						fogOfWar.setState(myXi, myYi + a, (byte) 0);
					}

					// 检测左方
					if ((distance <= radSq)
							&& (flying || !pathingGrid.isBlockVision(myX - ((a - 1) * CPlayerFogOfWar.GRID_STEP), myY))
							&& (fogOfWar.getState((myXi - a) + 1, myYi) == 0)
							&& (flying || game.isTerrainWater(myX - (a * CPlayerFogOfWar.GRID_STEP), myY)
									|| (myZ > game.getTerrainHeight(myX - (a * CPlayerFogOfWar.GRID_STEP), myY))
									|| (!game.isTerrainRomp(myX - (a * CPlayerFogOfWar.GRID_STEP), myY) && (myZ == game
											.getTerrainHeight(myX - (a * CPlayerFogOfWar.GRID_STEP), myY))))) {
						fogOfWar.setState(myXi - a, myYi, (byte) 0);
					}

					// 检测右方
					if ((distance <= radSq)
							&& (flying || !pathingGrid.isBlockVision(myX + ((a - 1) * CPlayerFogOfWar.GRID_STEP), myY))
							&& (fogOfWar.getState((myXi + a) - 1, myYi) == 0)
							&& (flying || game.isTerrainWater(myX + (a * CPlayerFogOfWar.GRID_STEP), myY)
									|| (myZ > game.getTerrainHeight(myX + (a * CPlayerFogOfWar.GRID_STEP), myY))
									|| (!game.isTerrainRomp(myX + (a * CPlayerFogOfWar.GRID_STEP), myY) && (myZ == game
											.getTerrainHeight(myX + (a * CPlayerFogOfWar.GRID_STEP), myY))))) {
						fogOfWar.setState(myXi + a, myYi, (byte) 0);
					}
				}

				for (int y = 1; y <= (maxYi - myYi); y++) {
					for (int x = 1; x <= (maxXi - myXi); x++) {
						final float distance = (x * x) + (y * y);
						if (distance <= radSq) {
							final int xf = x * CPlayerFogOfWar.GRID_STEP;
							final int yf = y * CPlayerFogOfWar.GRID_STEP;

							// 左上方
							if ((flying || game.isTerrainWater(myX - xf, myY - yf)
									|| (myZ > game.getTerrainHeight(myX - xf, myY - yf))
									|| (!game.isTerrainRomp(myX - xf, myY - yf)
											&& (myZ == game.getTerrainHeight(myX - xf, myY - yf))))
									&& (flying || !pathingGrid.isBlockVision((myX - xf) + CPlayerFogOfWar.GRID_STEP,
											(myY - yf) + CPlayerFogOfWar.GRID_STEP))
									&& (fogOfWar.getState((myXi - x) + 1, (myYi - y) + 1) == 0)
									&& ((x == y)
											|| ((x > y) && (fogOfWar.getState((myXi - x) + 1, myYi - y) == 0)
													&& (flying || !pathingGrid.isBlockVision(
															(myX - xf) + CPlayerFogOfWar.GRID_STEP, myY - yf)))
											|| ((x < y) && (fogOfWar.getState(myXi - x, (myYi - y) + 1) == 0)
													&& (flying || !pathingGrid.isBlockVision(myX - xf,
															(myY - yf) + CPlayerFogOfWar.GRID_STEP))))) {
								fogOfWar.setState(myXi - x, myYi - y, (byte) 0);
							}
							// 左下方
							if ((flying || game.isTerrainWater(myX - xf, myY + yf)
									|| (myZ > game.getTerrainHeight(myX - xf, myY + yf))
									|| (!game.isTerrainRomp(myX - xf, myY + yf)
											&& (myZ == game.getTerrainHeight(myX - xf, myY + yf))))
									&& (flying || !pathingGrid.isBlockVision((myX - xf) + CPlayerFogOfWar.GRID_STEP,
											(myY + yf) - CPlayerFogOfWar.GRID_STEP))
									&& (fogOfWar.getState((myXi - x) + 1, (myYi + y) - 1) == 0)
									&& ((x == y)
											|| ((x > y) && (fogOfWar.getState((myXi - x) + 1, myYi + y) == 0)
													&& (flying || !pathingGrid.isBlockVision(
															(myX - xf) + CPlayerFogOfWar.GRID_STEP, myY + yf)))
											|| ((x < y) && (fogOfWar.getState(myXi - x, (myYi + y) - 1) == 0)
													&& (flying || !pathingGrid.isBlockVision(myX - xf,
															(myY + yf) - CPlayerFogOfWar.GRID_STEP))))) {
								fogOfWar.setState(myXi - x, myYi + y, (byte) 0);
							}
							// 右上方
							if ((flying || game.isTerrainWater(myX + xf, myY - yf)
									|| (myZ > game.getTerrainHeight(myX + xf, myY - yf))
									|| (!game.isTerrainRomp(myX + xf, myY - yf)
											&& (myZ == game.getTerrainHeight(myX + xf, myY - yf))))
									&& (flying || !pathingGrid.isBlockVision((myX + xf) - CPlayerFogOfWar.GRID_STEP,
											(myY - yf) + CPlayerFogOfWar.GRID_STEP))
									&& (fogOfWar.getState((myXi + x) - 1, (myYi - y) + 1) == 0)
									&& ((x == y)
											|| ((x > y) && (fogOfWar.getState((myXi + x) - 1, myYi - y) == 0)
													&& (flying || !pathingGrid.isBlockVision(
															(myX + xf) - CPlayerFogOfWar.GRID_STEP, myY - yf)))
											|| ((x < y) && (fogOfWar.getState(myXi + x, (myYi - y) + 1) == 0)
													&& (flying || !pathingGrid.isBlockVision(myX + xf,
															(myY - yf) + CPlayerFogOfWar.GRID_STEP))))) {
								fogOfWar.setState(myXi + x, myYi - y, (byte) 0);
							}
							// 右下方
							if ((flying || game.isTerrainWater(myX + xf, myY + yf)
									|| (myZ > game.getTerrainHeight(myX + xf, myY + yf))
									|| (!game.isTerrainRomp(myX + xf, myY + yf)
											&& (myZ == game.getTerrainHeight(myX + xf, myY + yf))))
									&& (flying || !pathingGrid.isBlockVision((myX + xf) - CPlayerFogOfWar.GRID_STEP,
											(myY + yf) - CPlayerFogOfWar.GRID_STEP))
									&& (fogOfWar.getState((myXi + x) - 1, (myYi + y) - 1) == 0)
									&& ((x == y)
											|| ((x > y) && (fogOfWar.getState((myXi + x) - 1, myYi + y) == 0)
													&& (flying || !pathingGrid.isBlockVision(
															(myX + xf) - CPlayerFogOfWar.GRID_STEP, myY + yf)))
											|| ((x < y) && (fogOfWar.getState(myXi + x, (myYi + y) - 1) == 0)
													&& (flying || !pathingGrid.isBlockVision(myX + xf,
															(myY + yf) - CPlayerFogOfWar.GRID_STEP))))) {
								fogOfWar.setState(myXi + x, myYi + y, (byte) 0);
							}
						}
					}
				}
			}
		}
	}

	public void setExplodesOnDeath(final boolean explodesOnDeath) {
		this.explodesOnDeath = explodesOnDeath;
	}

	public void setExplodesOnDeathBuffId(final War3ID explodesOnDeathBuffId) {
		this.explodesOnDeathBuffId = explodesOnDeathBuffId;
	}

	public boolean isExplodesOnDeath() {
		return this.explodesOnDeath;
	}

	public List<CUnitAttackPreDamageListener> getPreDamageListenersForPriority(
			final CUnitAttackPreDamageListenerPriority priority) {
		return this.preDamageListeners.get(priority);
	}

	public void addPreDamageListener(final CUnitAttackPreDamageListenerPriority priority,
			final CUnitAttackPreDamageListener listener) {
		List<CUnitAttackPreDamageListener> list = this.preDamageListeners.get(priority);
		if (list == null) {
			list = new ArrayList<>();
		}
		list.add(0, listener);
	}

	public void removePreDamageListener(final CUnitAttackPreDamageListenerPriority priority,
			final CUnitAttackPreDamageListener listener) {
		final List<CUnitAttackPreDamageListener> list = this.preDamageListeners.get(priority);
		if (list != null) {
			list.remove(listener);
		}
	}

	public List<CUnitAttackPostDamageListener> getPostDamageListeners() {
		return this.postDamageListeners;
	}

	public void addPostDamageListener(final CUnitAttackPostDamageListener listener) {
		this.postDamageListeners.add(0, listener);
	}

	public void removePostDamageListener(final CUnitAttackPostDamageListener listener) {
		this.postDamageListeners.remove(listener);
	}

	public void addDamageTakenModificationListener(final CUnitAttackDamageTakenModificationListener listener) {
		this.damageTakenModificationListeners.add(0, listener);
	}

	public void removeDamageTakenModificationListener(final CUnitAttackDamageTakenModificationListener listener) {
		this.damageTakenModificationListeners.remove(listener);
	}

	public void addFinalDamageTakenModificationListener(
			final CUnitAttackFinalDamageTakenModificationListener listener) {
		this.finalDamageTakenModificationListeners.add(0, listener);
	}

	public void removeFinalDamageTakenModificationListener(
			final CUnitAttackFinalDamageTakenModificationListener listener) {
		this.finalDamageTakenModificationListeners.remove(listener);
	}

	public void addDamageTakenListener(final CUnitAttackDamageTakenListener listener) {
		this.damageTakenListeners.add(0, listener);
	}

	public void removeDamageTakenListener(final CUnitAttackDamageTakenListener listener) {
		this.damageTakenListeners.remove(listener);
	}

	// 获取 死亡替换 效果处理器 列表
	public List<CUnitDeathReplacementEffect> getDeathReplacementEffectsForPriority(
			final CUnitDeathReplacementEffectPriority priority) {
		return this.deathReplacementEffects.get(priority);
	}

	// 添加 死亡替换 效果处理器
	public void addDeathReplacementEffect(final CUnitDeathReplacementEffectPriority priority,
			final CUnitDeathReplacementEffect listener) {
		List<CUnitDeathReplacementEffect> list = this.deathReplacementEffects.get(priority);
		if (list == null) {
			list = new ArrayList<>();
		}
		list.add(0, listener);
	}

	// 移除 死亡替换 效果处理器
	public void removeDeathReplacementEffect(final CUnitDeathReplacementEffectPriority priority,
			final CUnitDeathReplacementEffect listener) {
		final List<CUnitDeathReplacementEffect> list = this.deathReplacementEffects.get(priority);
		if (list != null) {
			list.remove(listener);
		}
	}

	// 添加 闪避 处理器
	public void addEvasionListener(final CUnitAttackEvasionListener listener) {
		this.evasionListeners.add(0, listener);
	}

	// 移除 闪避 处理器
	public void removeEvasionListener(final CUnitAttackEvasionListener listener) {
		this.evasionListeners.remove(listener);
	}

	// 开始冷却
	public void beginCooldown(final CSimulation game, final War3ID abilityId, final float cooldownDuration) {
		// 获取游戏当前帧数
		final int gameTurnTick = game.getGameTurnTick();
		// 设置技能冷却到期时间帧
		this.rawcodeToCooldownExpireTime.put(abilityId.getValue(),
				gameTurnTick + (int) StrictMath.ceil(cooldownDuration / WarsmashConstants.SIMULATION_STEP_TIME));
		// 设置技能冷却开始时间
		this.rawcodeToCooldownStartTime.put(abilityId.getValue(), gameTurnTick);
		// 触发技能冷却变化事件
		fireCooldownsChangedEvent();
	}
	// 获取冷却剩余的时间（以游戏的滴答数为单位）
	public int getCooldownRemainingTicks(final CSimulation game, final War3ID abilityId) {
		final int expireTime = this.rawcodeToCooldownExpireTime.get(abilityId.getValue(), -1); // 获取能力ID对应的冷却结束时间，如果不存在则返回-1
		final int gameTurnTick = game.getGameTurnTick(); // 获取当前游戏回合数
		if ((expireTime == -1) || (expireTime <= gameTurnTick)) { // 如果冷却结束时间为-1或者已经过了冷却时间
			return 0; // 返回0表示技能已经准备好，可以立即使用
		}
		return expireTime - gameTurnTick; // 返回剩余的冷却时间

	}

	// 获取冷却时间长度（以游戏的滴答数为单位）
	public int getCooldownLengthDisplayTicks(final CSimulation game, final War3ID abilityId) {
		final int startTime = this.rawcodeToCooldownStartTime.get(abilityId.getValue(), -1);
		final int expireTime = this.rawcodeToCooldownExpireTime.get(abilityId.getValue(), -1);
		if ((startTime == -1) || (expireTime == -1)) {
			return 0;
		}
		return expireTime - startTime;
	}

	// 检查对象是否可提升
	public boolean isRaisable() {
		return this.raisable;
	}

	// 检查对象是否会衰减
	public boolean isDecays() {
		return this.decays;
	}

	// 设置对象是否可提升
	public void setRaisable(final boolean raisable) {
		this.raisable = raisable;
	}

	// 设置对象是否会衰减
	public void setDecays(final boolean decays) {
		this.decays = decays;
	}

	// 设置对象是否免疫魔法
	public void setMagicImmune(final boolean magicImmune) {
		this.magicImmune = magicImmune;
	}

	// 检查对象是否免疫魔法
	public boolean isMagicImmune() {
		return this.magicImmune;
	}

	// 检查对象是否处于假死状态
	public boolean isFalseDeath() {
		return this.falseDeath;
	}

	// 设置对象的假死状态
	public void setFalseDeath(final boolean falseDeath) {
		this.falseDeath = falseDeath;
	}

	// 设置对象的自动施法能力
	public void setAutocastAbility(final CAutocastAbility autocastAbility) {
		if (this.autocastAbility != null) {
			this.autocastAbility.setAutoCastOff();
		}
		this.autocastAbility = autocastAbility;
	}

	// 检查对象是否对指定玩家可见
	public boolean isVisible(final CSimulation simulation, final int toPlayerIndex) {
		// 检查当前单位是否能看到目标玩家
		if ((toPlayerIndex == this.playerIndex) && // 如果目标玩家是当前玩家自己
				((simulation.isDay() ? this.unitType.getSightRadiusDay() // 并且是白天，获取白天的视野半径
						: this.unitType.getSightRadiusNight()) > 0)) { // 或者是夜晚，获取夜晚的视野半径，并检查是否大于0
			return true; // 如果视野半径大于0，表示能看到，返回true
		}

		final CPlayer toPlayer = simulation.getPlayer(toPlayerIndex); // 获取目标玩家对象
		final byte fogState = toPlayer.getFogOfWar().getState(simulation.getPathingGrid(), getX(), getY()); // 获取目标玩家战争迷雾状态
		if (fogState == 0) { // 如果战争迷雾状态为0，表示没有迷雾遮挡
			return true; // 表示能看到，返回true
		}
		return false; // 其他情况表示看不到，返回false

	}

}
