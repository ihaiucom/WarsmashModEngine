package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CPlayerStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CPlayerStateListener.CPlayerStateNotifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit.QueueItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CBasePlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderNoTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderTargetPoint;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderTargetWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CFogModifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CPlayerFogOfWar;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
public class CPlayer extends CBasePlayer { // CPlayer类，继承自CBasePlayer类
	private final CRace race; // 种族
	private final float[] startLocation; // 起始位置
	private int gold; // 金钱
	private int lumber; // 木材
	private int heroTokens; // 英雄代币
	private int foodCap; // 食物上限
	private int foodUsed; // 已使用食物
	private int foodCapCeiling = 101; // 食物上限天花板
	private final Map<War3ID, Integer> rawcodeToTechtreeUnlocked = new HashMap<>(); // 解锁科技树
	private final Map<War3ID, Integer> rawcodeToTechtreeInProgress = new HashMap<>(); // 进行中的科技树
	private final Map<War3ID, Integer> rawcodeToTechtreeMaxAllowed = new HashMap<>(); // 最大允许科技树
	private final List<CUnit> heroes = new ArrayList<>(); // 英雄列表
	private final List<CFogModifier> fogModifiers = new ArrayList<>(); // 雾气修饰器列表
	private final EnumMap<JassGameEventsWar3, List<CPlayerEvent>> eventTypeToEvents = new EnumMap<>(
			JassGameEventsWar3.class); // 事件类型到事件的映射

	// 玩家状态数据
	private boolean givesBounty = false; // 是否给予赏金
	private boolean alliedVictory = false; // 是否盟友胜利
	private int gameResult; // 游戏结果
	private int placed; // 放置数量
	private boolean observerOnDeath; // 死亡时是否成为观察者
	private boolean observer; // 是否观察者
	private boolean unfollowable; // 是否不可跟随
	private int goldUpkeepRate; // 金钱维护率
	private int lumberUpkeepRate; // 木材维护率
	private int goldGathered; // 收集的金钱
	private int lumberGathered; // 收集的木材
	private boolean noCreepSleep; // 是否没有小怪休眠

	// if you use triggers for this then the transient tag here becomes really
	// questionable -- it already was -- but I meant for those to inform us
	// which fields shouldn't be persisted if we do game state save later
	private transient CPlayerStateNotifier stateNotifier = new CPlayerStateNotifier(); // 玩家状态通知器
	private float handicapXP = 1.0f; // XP障碍
	private float handicap = 0.9f; // 障碍
	private final CPlayerFogOfWar fogOfWar; // 玩家视野

	public CPlayer(final CRace race, final float[] startLocation, final CBasePlayer configPlayer,
			final CPlayerFogOfWar fogOfWar) { // 构造函数，初始化玩家属性
		super(configPlayer);
		this.race = race;
		this.startLocation = startLocation;
		// Below: 32x32 cells to find the number of 128x128 cells
		this.fogOfWar = fogOfWar;
	}

	public CPlayerFogOfWar getFogOfWar() { // 获取玩家的视野
		return this.fogOfWar;
	}

	public void setAlliance(final CPlayer other, final CAllianceType alliance, final boolean flag) { // 设置联盟
		setAlliance(other.getId(), alliance, flag);
	}

	public CRace getRace() { // 获取种族
		return this.race;
	}

	public int getGold() { // 获取金钱
		return this.gold;
	}

	public int getLumber() { // 获取木材
		return this.lumber;
	}

	public int getHeroTokens() { // 获取英雄代币
		return this.heroTokens;
	}

	public int getFoodCap() { // 获取食物上限
		return this.foodCap;
	}

	public int getFoodUsed() { // 获取已使用食物
		return this.foodUsed;
	}

	public int getFoodCapCeiling() { // 获取食物上限天花板
		return this.foodCapCeiling;
	}

	public float[] getStartLocation() { // 获取起始位置
		return this.startLocation;
	}

	public void setGold(final int gold) { // 设置金钱
		this.gold = gold;
		this.stateNotifier.goldChanged();
	}

	public void addGold(final int gold) { // 增加金钱
		setGold(getGold() + gold);
	}

	public void setLumber(final int lumber) { // 设置木材
		this.lumber = lumber;
		this.stateNotifier.lumberChanged();
	}

	public void addLumber(final int lumber) { // 增加木材
		setLumber(getLumber() + lumber);
	}

	public void setHeroTokens(final int heroTokens) { // 设置英雄代币
		this.heroTokens = heroTokens;
		this.stateNotifier.heroTokensChanged();
	}

	public void setFoodCap(final int foodCap) { // 设置食物上限
		this.foodCap = foodCap;
		this.stateNotifier.foodChanged();
	}

	public void setFoodCapCeiling(final int foodCapCeiling) { // 设置食物上限天花板
		this.foodCapCeiling = foodCapCeiling;
		this.stateNotifier.foodChanged();
	}

	public void setFoodUsed(final int foodUsed) { // 设置已使用食物
		this.foodUsed = foodUsed;
		this.stateNotifier.foodChanged();
	}

	public int getTechtreeUnlocked(final War3ID rawcode) { // 获取解锁的科技树
		final Integer techtreeUnlocked = this.rawcodeToTechtreeUnlocked.get(rawcode);
		if (techtreeUnlocked == null) {
			return 0;
		}
		return techtreeUnlocked;
	}

	public int getTechtreeInProgress(final War3ID rawcode) { // 获取进行中的科技树
		final Integer techtreeInProgress = this.rawcodeToTechtreeInProgress.get(rawcode);
		if (techtreeInProgress == null) {
			return 0;
		}
		return techtreeInProgress;
	}

	public int getTechtreeUnlockedOrInProgress(final War3ID rawcode) { // 获取已解锁或进行中的科技树
		return getTechtreeUnlocked(rawcode) + getTechtreeInProgress(rawcode);
	}

	public void addTechtreeUnlocked(CSimulation simulation, final War3ID rawcode) { // 增加解锁的科技树
		// 获取与rawcode关联的技术树解锁状态，如果不存在则默认为null
		final Integer techtreeUnlocked = this.rawcodeToTechtreeUnlocked.get(rawcode);

		// 如果技术树解锁状态为null，表示该rawcode尚未解锁任何技术树项，将其设置为1
		if (techtreeUnlocked == null) {
			this.rawcodeToTechtreeUnlocked.put(rawcode, 1);
		}
		// 如果技术树解锁状态不为null，表示该rawcode已解锁技术树项，将其状态加1
		else {
			this.rawcodeToTechtreeUnlocked.put(rawcode, techtreeUnlocked + 1);
		}

		// 触发能力更新，通知系统该rawcode对应的技术树项已更新
		fireRequirementUpdateForAbilities(simulation, false);

	}

	public void setTechtreeUnlocked(CSimulation simulation, final War3ID rawcode, final int setToLevel) { // 设置解锁的科技树
		int prev = this.getTechtreeUnlocked(rawcode);
		this.rawcodeToTechtreeUnlocked.put(rawcode, setToLevel);
		fireRequirementUpdateForAbilities(simulation, prev > setToLevel);
	}

	public void removeTechtreeUnlocked(CSimulation simulation, final War3ID rawcode) { // 移除解锁的科技树
		final Integer techtreeUnlocked = this.rawcodeToTechtreeUnlocked.get(rawcode);
		if (techtreeUnlocked == null) {
			this.rawcodeToTechtreeUnlocked.put(rawcode, -1);
		}
		else {
			this.rawcodeToTechtreeUnlocked.put(rawcode, techtreeUnlocked - 1);
		}
		fireRequirementUpdateForAbilities(simulation, true);
	}

	public void addTechtreeInProgress(final War3ID rawcode) { // 增加进行中的科技树
		final Integer techtreeUnlocked = this.rawcodeToTechtreeInProgress.get(rawcode);
		if (techtreeUnlocked == null) {
			this.rawcodeToTechtreeInProgress.put(rawcode, 1);
		}
		else {
			this.rawcodeToTechtreeInProgress.put(rawcode, techtreeUnlocked + 1);
		}
	}

	public void removeTechtreeInProgress(final War3ID rawcode) { // 移除进行中的科技树
		final Integer techtreeUnlocked = this.rawcodeToTechtreeInProgress.get(rawcode);
		if (techtreeUnlocked == null) {
			this.rawcodeToTechtreeInProgress.put(rawcode, -1);
		}
		else {
			this.rawcodeToTechtreeInProgress.put(rawcode, techtreeUnlocked - 1);
		}
	}

	public void setTechtreeMaxAllowed(final War3ID war3id, final int maximum) { // 设置最大允许科技树
		this.rawcodeToTechtreeMaxAllowed.put(war3id, maximum);
	}

	public int getTechtreeMaxAllowed(final War3ID war3id) { // 获取最大允许科技树
		final Integer maxAllowed = this.rawcodeToTechtreeMaxAllowed.get(war3id);
		if (maxAllowed != null) {
			return maxAllowed;
		}
		return -1;
	}

	public void addStateListener(final CPlayerStateListener listener) { // 添加状态监听器
		this.stateNotifier.subscribe(listener);
	}

	public void removeStateListener(final CPlayerStateListener listener) { // 移除状态监听器
		this.stateNotifier.unsubscribe(listener);
	}

	public void chargeFor(final CUnitType unitType) { // 收取单位费用
		this.lumber -= unitType.getLumberCost();
		this.gold -= unitType.getGoldCost();
		this.stateNotifier.lumberChanged();
		this.stateNotifier.goldChanged();
	}

	public void chargeFor(final CUpgradeType upgradeType) { // 收取升级费用
		final int unlockCount = getTechtreeUnlocked(upgradeType.getTypeId());
		this.lumber -= upgradeType.getLumberCost(unlockCount);
		this.gold -= upgradeType.getGoldCost(unlockCount);
		this.stateNotifier.lumberChanged();
		this.stateNotifier.goldChanged();
	}

	public boolean charge(final int gold, final int lumber) { // 收取费用
		if ((this.lumber >= lumber) && (this.gold >= gold)) {
			this.lumber -= lumber;
			this.gold -= gold;
			this.stateNotifier.lumberChanged();
			this.stateNotifier.goldChanged();
			return true;
		}
		return false;
	}

	public void refundFor(final CUnitType unitType) { // 退款单位费用
		this.lumber += unitType.getLumberCost();
		this.gold += unitType.getGoldCost();
		this.stateNotifier.lumberChanged();
		this.stateNotifier.goldChanged();
	}

	public void refundFor(final CUpgradeType upgradeType) { // 退款升级费用
		final int unlockCount = getTechtreeUnlocked(upgradeType.getTypeId());
		this.lumber += upgradeType.getLumberCost(unlockCount);
		this.gold += upgradeType.getGoldCost(unlockCount);
		this.stateNotifier.lumberChanged();
		this.stateNotifier.goldChanged();
	}

	public void refund(final int gold, final int lumber) { // 退款
		this.gold += gold;
		this.lumber += lumber;
		this.stateNotifier.lumberChanged();
		this.stateNotifier.goldChanged();
	}

	public void setUnitFoodUsed(final CUnit unit, final int foodUsed) { // 设置单位使用的食物
		this.foodUsed += unit.setFoodUsed(foodUsed);
		this.stateNotifier.foodChanged();
	}

	public void setUnitFoodMade(final CUnit unit, final int foodMade) { // 设置单位制造的食物
		this.foodCap += unit.setFoodMade(foodMade);
		this.stateNotifier.foodChanged();
	}

	public void onHeroDeath(final CUnit hero) { // 英雄死亡事件
		this.stateNotifier.heroDeath();
		firePlayerUnitEvents(hero, CommonTriggerExecutionScope::playerHeroRevivableScope,
				JassGameEventsWar3.EVENT_PLAYER_HERO_REVIVABLE);
	}

	private void firePlayerUnitEvents(final CUnit hero,
			final CommonTriggerExecutionScope.UnitEventScopeBuilder eventScopeBuilder,
			final JassGameEventsWar3 eventType) { // 触发玩家单位事件
		final List<CPlayerEvent> eventList = getEventList(eventType);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(hero, eventScopeBuilder.create(eventType, event.getTrigger(), hero));
			}
		}
	}

	public void firePlayerEvents(final CommonTriggerExecutionScope.PlayerEventScopeBuilder eventScopeBuilder,
			final JassGameEventsWar3 eventType) { // 触发玩家事件
		final List<CPlayerEvent> eventList = getEventList(eventType);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(this, eventScopeBuilder.create(eventType, event.getTrigger(), this));
			}
		}
	}

	public List<CUnit> getHeroes() { // 获取英雄列表
		return this.heroes;
	}

	public int getHeroCount(final CSimulation game, final boolean includeInProgress) { // 获取英雄数量
		if (!includeInProgress) {
			return this.heroes.size();
		}
		else {
			int heroInProgressCount = 0;
			for (final Map.Entry<War3ID, Integer> entry : this.rawcodeToTechtreeInProgress.entrySet()) {
				final CUnitType unitType = game.getUnitData().getUnitType(entry.getKey());
				if ((unitType != null) && unitType.isHero()) {
					heroInProgressCount += entry.getValue();
				}
			}
			return this.heroes.size() + heroInProgressCount;
		}
	}

	public void fireHeroLevelEvents(final CUnit hero) { // 触发英雄等级事件
		firePlayerUnitEvents(hero, CommonTriggerExecutionScope::playerHeroRevivableScope,
				JassGameEventsWar3.EVENT_PLAYER_HERO_LEVEL);
	}

	public void fireUnitDeathEvents(final CUnit dyingUnit, final CUnit killingUnit) { // 触发单位死亡事件
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_DEATH);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(dyingUnit, CommonTriggerExecutionScope.unitDeathScope(
						JassGameEventsWar3.EVENT_PLAYER_UNIT_DEATH, event.getTrigger(), dyingUnit, killingUnit));
			}
		}
	}

	public void fireOrderEvents(final CUnit unit, final CSimulation game, final COrderNoTarget orderNoTarget) { // 触发无目标指令事件
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_ISSUED_ORDER);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(unit,
						CommonTriggerExecutionScope.unitOrderScope(JassGameEventsWar3.EVENT_PLAYER_UNIT_ISSUED_ORDER,
								event.getTrigger(), unit, orderNoTarget.getOrderId()));
			}
		}
	}

	public void fireOrderEvents(final CUnit unit, final CSimulation game, final COrderTargetPoint order) { // 触发目标点指令事件
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_ISSUED_POINT_ORDER);
		if (eventList != null) {
			final AbilityPointTarget target = order.getTarget(game);
			for (final CPlayerEvent event : eventList) {
				event.fire(unit,
						CommonTriggerExecutionScope.unitOrderPointScope(
								JassGameEventsWar3.EVENT_PLAYER_UNIT_ISSUED_POINT_ORDER, event.getTrigger(), unit,
								order.getOrderId(), target.x, target.y));
			}
		}
	}

	public void fireOrderEvents(final CUnit unit, final CSimulation game, final COrderTargetWidget order) { // 触发目标单位指令事件
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_ISSUED_TARGET_ORDER);
		if (eventList != null) {
			final CWidget target = order.getTarget(game);
			for (final CPlayerEvent event : eventList) {
				event.fire(unit,
						CommonTriggerExecutionScope.unitOrderTargetScope(
								JassGameEventsWar3.EVENT_PLAYER_UNIT_ISSUED_TARGET_ORDER, event.getTrigger(), unit,
								order.getOrderId(), target));
			}
		}
	}

	public void fireConstructFinishEvents(final CUnit unit, final CSimulation game, final CUnit constructingUnit) { // 触发建造完成事件
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_CONSTRUCT_FINISH);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(unit,
						CommonTriggerExecutionScope.unitConstructFinishScope(
								JassGameEventsWar3.EVENT_PLAYER_UNIT_CONSTRUCT_FINISH, event.getTrigger(), unit,
								constructingUnit));
			}
		}
	}

	public void fireTrainFinishEvents(final CUnit unit, final CSimulation game, final CUnit trainedUnit) { // 触发训练完成事件
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_TRAIN_FINISH);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(unit, CommonTriggerExecutionScope.unitTrainFinishScope(
						JassGameEventsWar3.EVENT_PLAYER_UNIT_TRAIN_FINISH, event.getTrigger(), unit, trainedUnit));
			}
		}
	}

	public void fireResearchFinishEvents(final CUnit unit, final CSimulation game, final War3ID researched) { // 触发研究完成事件
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_RESEARCH_FINISH);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(unit, CommonTriggerExecutionScope.unitResearchFinishScope(
						JassGameEventsWar3.EVENT_PLAYER_UNIT_RESEARCH_FINISH, event.getTrigger(), unit, researched));
			}
		}
	}

	public void firePickUpItemEvents(final CUnit unit, final CItem item, final CSimulation game) { // 触发拾取物品事件
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_PICKUP_ITEM);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(unit, CommonTriggerExecutionScope.unitPickupItemScope(
						JassGameEventsWar3.EVENT_PLAYER_UNIT_PICKUP_ITEM, event.getTrigger(), unit, item));
			}
		}
	}

	private List<CPlayerEvent> getOrCreateEventList(final JassGameEventsWar3 eventType) { // 获取或创建事件列表
		List<CPlayerEvent> playerEvents = this.eventTypeToEvents.get(eventType);
		if (playerEvents == null) {
			playerEvents = new ArrayList<>();
			this.eventTypeToEvents.put(eventType, playerEvents);
		}
		return playerEvents;
	}

	private List<CPlayerEvent> getEventList(final JassGameEventsWar3 eventType) { // 获取事件列表
		return this.eventTypeToEvents.get(eventType);
	}

	@Override
	public RemovableTriggerEvent addEvent(final GlobalScope globalScope, final Trigger whichTrigger,
			final JassGameEventsWar3 eventType) { // 添加事件
		final CPlayerEvent playerEvent = new CPlayerEvent(globalScope, this, whichTrigger, eventType, null);
		getOrCreateEventList(eventType).add(playerEvent);
		return playerEvent;
	}

	public RemovableTriggerEvent addUnitEvent(final GlobalScope globalScope, final Trigger whichTrigger,
			final JassGameEventsWar3 eventType, final TriggerBooleanExpression filter) { // 添加单位事件
		final CPlayerEvent playerEvent = new CPlayerEvent(globalScope, this, whichTrigger, eventType, filter);
		getOrCreateEventList(eventType).add(playerEvent);
		return playerEvent;
	}

	@Override
	public void removeEvent(final CPlayerEvent playerEvent) { // 移除事件
		final List<CPlayerEvent> eventList = getEventList(playerEvent.getEventType());
		if (eventList != null) {
			eventList.remove(playerEvent);
		}
	}

	public void setPlayerState(final CSimulation simulation, final CPlayerState whichPlayerState, final int value) { // 设置玩家状态
		switch (whichPlayerState) {
		case GAME_RESULT:
			this.gameResult = value;
			break;
		case RESOURCE_GOLD:
			setGold(value);
			break;
		case RESOURCE_LUMBER:
			setLumber(value);
			break;
		case RESOURCE_HERO_TOKENS:
			setHeroTokens(value);
			break;
		case RESOURCE_FOOD_CAP:
			setFoodCap(value);
			break;
		case RESOURCE_FOOD_USED:
			setFoodUsed(value);
			break;
		case FOOD_CAP_CEILING:
			setFoodCapCeiling(value);
			break;
		case ALLIED_VICTORY:
			this.alliedVictory = (value != 0);
			break;
		case GIVES_BOUNTY:
			this.givesBounty = (value != 0);
			break;
		case PLACED:
			this.placed = value;
		case OBSERVER_ON_DEATH:
			this.observerOnDeath = (value != 0);
		case OBSERVER:
			this.observer = (value != 0);
		case UNFOLLOWABLE:
			this.unfollowable = (value != 0);
		case GOLD_UPKEEP_RATE:
			this.goldUpkeepRate = value;
			break;
		case LUMBER_UPKEEP_RATE:
			this.lumberUpkeepRate = value;
			break;
		case GOLD_GATHERED:
			this.goldGathered = value;
			break;
		case LUMBER_GATHERED:
			this.goldGathered = value;
			break;
		case NO_CREEP_SLEEP:
			this.noCreepSleep = (value != 0);
			break;
		default:
			break;
		}
	}

	public int getPlayerState(final CSimulation simulation, final CPlayerState whichPlayerState) { // 获取玩家状态
		switch (whichPlayerState) {
		case GAME_RESULT:
			return this.gameResult;
		case RESOURCE_GOLD:
			return getGold();
		case RESOURCE_LUMBER:
			return getLumber();
		case RESOURCE_HERO_TOKENS:
			return getHeroTokens();
		case RESOURCE_FOOD_CAP:
			return getFoodCap();
		case RESOURCE_FOOD_USED:
			return getFoodUsed();
		case FOOD_CAP_CEILING:
			return getFoodCapCeiling();
		case ALLIED_VICTORY:
			return this.alliedVictory ? 1 : 0;
		case GIVES_BOUNTY:
			return this.givesBounty ? 1 : 0;
		case PLACED:
			return this.placed;
		case OBSERVER_ON_DEATH:
			return this.observerOnDeath ? 1 : 0;
		case OBSERVER:
			return this.observer ? 1 : 0;
		case UNFOLLOWABLE:
			return this.unfollowable ? 1 : 0;
		case GOLD_UPKEEP_RATE:
			return this.goldUpkeepRate;
		case LUMBER_UPKEEP_RATE:
			return this.lumberUpkeepRate;
		case GOLD_GATHERED:
			return this.goldGathered;
		case LUMBER_GATHERED:
			return this.lumberGathered;
		case NO_CREEP_SLEEP:
			return this.noCreepSleep ? 1 : 0;
		default:
			return 0;
		}
	}

	public boolean isObserver() { // 判断是否为观察者
		return this.observer;
	}

	public boolean isTechtreeAllowedByMax(final War3ID techtree) { // 判断科技树是否允许最多
		final int techtreeMaxAllowed = getTechtreeMaxAllowed(techtree);
		if (techtreeMaxAllowed >= 0) {
			if (getTechtreeUnlockedOrInProgress(techtree) >= techtreeMaxAllowed) {
				return false;
			}
		}
		return true;
	}

	public void setHandicapXP(final float handicapXP) { // 设置XP障碍
		this.handicapXP = handicapXP;
	}

	public float getHandicapXP() { // 获取XP障碍
		return this.handicapXP;
	}

	public void setHandicap(final float handicap) { // 设置障碍
		this.handicap = handicap;
	}

	public float getHandicap() { // 获取障碍
		return this.handicap;
	}

	public void fireAbilityEffectEventsTarget(final CAbility spellAbility, final CUnit spellAbilityUnit,
			final CUnit spellTargetUnit, final War3ID alias) { // 触发技能效果事件（目标）
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_EFFECT);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(spellAbilityUnit,
						CommonTriggerExecutionScope.unitSpellEffectTargetScope(
								JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_EFFECT, event.getTrigger(), spellAbility,
								spellAbilityUnit, spellTargetUnit, alias));
			}
		}
	}

	public void fireAbilityEffectEventsPoint(final CAbility spellAbility, final CUnit spellAbilityUnit,
			final AbilityPointTarget abilityPointTarget, final War3ID alias) { // 触发技能效果事件（点）
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_EFFECT);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(spellAbilityUnit,
						CommonTriggerExecutionScope.unitSpellEffectPointScope(
								JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_EFFECT, event.getTrigger(), spellAbility,
								spellAbilityUnit, abilityPointTarget, alias));
			}
		}
	}

	public void addTechResearched(final CSimulation simulation, final War3ID techIdRawcodeId, final int levels) { // 增加研究科技
		final int previousUnlockCount = getTechtreeUnlocked(techIdRawcodeId);
		if (levels != 0) {
			final int setToLevel = previousUnlockCount + levels;
			setTechToLevel(simulation, techIdRawcodeId, setToLevel);
		}
		fireRequirementUpdateForAbilities(simulation, false);
	}

	public void setTechResearched(final CSimulation simulation, final War3ID techIdRawcodeId, final int setToLevel) { // 设置已研究科技
		final int previousUnlockCount = getTechtreeUnlocked(techIdRawcodeId);
		if ((setToLevel > previousUnlockCount) || (setToLevel < previousUnlockCount)) {
			setTechToLevel(simulation, techIdRawcodeId, setToLevel);
		}
		fireRequirementUpdateForAbilities(simulation, false);
	}

	private void setTechToLevel(final CSimulation simulation, final War3ID techIdRawcodeId, final int setToLevel) { // 设置科技等级
		final int previousLevel = getTechtreeUnlocked(techIdRawcodeId);
		setTechtreeUnlocked(simulation, techIdRawcodeId, setToLevel);
		// terminate in progress upgrades of this kind for player
		final CUpgradeType upgradeType = simulation.getUpgradeData().getType(techIdRawcodeId);
		if (upgradeType != null) {
			for (final CUnit unit : simulation.getUnits()) {
				if (unit.getPlayerIndex() == getId()) {
					if (unit.isBuildQueueActive() && (unit.getBuildQueueTypes()[0] == QueueItemType.RESEARCH)
							&& (unit.getBuildQueue()[0].getValue() == techIdRawcodeId.getValue())) {
						unit.cancelBuildQueueItem(simulation, 0);
					}
					if (unit.getUnitType().getUpgradesUsed().contains(techIdRawcodeId)) {
						if (previousLevel != 0) {
							upgradeType.unapply(simulation, unit, previousLevel);
						}
						if (setToLevel != 0) {
							upgradeType.apply(simulation, unit, setToLevel);
						}
					}
				}
			}
			if (previousLevel != 0) {
				upgradeType.unapply(simulation, getId(), previousLevel);
			}
			if (setToLevel != 0) {
				upgradeType.apply(simulation, getId(), setToLevel);
			}
		}
	}

	public void addFogModifer(final CSimulation game, final CFogModifier fogModifier) { // 增加雾气修饰器
		this.fogModifiers.add(fogModifier);
		fogModifier.onAdd(game, this);
	}

	public void removeFogModifer(final CSimulation game, final CFogModifier fogModifier) { // 移除雾气修饰器
		this.fogModifiers.remove(fogModifier);
		fogModifier.onRemove(game, this);
	}

	public void updateFogModifiers(final CSimulation game) { // 更新雾气修饰器
		for (int i = this.fogModifiers.size() - 1; i >= 0; i--) {
			this.fogModifiers.get(i).update(game, this, game.getPathingGrid(), this.fogOfWar);
		}
	}

	public void fireRequirementUpdateForAbilities(final CSimulation simulation, final boolean disable) { // 触发能力需求更新
		for(CUnit unit : simulation.getUnits()) {
			if (unit.getPlayerIndex() == this.getId()) {
				unit.checkDisabledAbilities(simulation, disable);
			}
		}
	}
}
