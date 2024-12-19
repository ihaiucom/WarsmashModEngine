package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.variableevent.CLimitOp;
import com.etheller.interpreter.ast.scope.variableevent.VariableEvent;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.RemovablePathingMapInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityCollisionProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CCollisionProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CPsuedoProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CBasePlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CPlayerAPI;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfigStartLoc;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CAbilityData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CDestructableData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CItemData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUpgradeData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CPathfindingProcessor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderExecutor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRaceManagerEntry;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRacePreference;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CFogModifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CPlayerFogOfWar;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.region.CRegionManager;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentLightning;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentModel;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderController;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandErrorListener;

public class CSimulation implements CPlayerAPI {
	// 存储能力数据的常量
	private final CAbilityData abilityData;
	// 存储单位数据的常量
	private final CUnitData unitData;
	// 存储可破坏物体数据的常量
	private final CDestructableData destructableData;
	// 存储物品数据的常量
	private final CItemData itemData;
	// 存储升级数据的常量
	private final CUpgradeData upgradeData;
	// 存储单位列表
	private final List<CUnit> units;
	// 存储新单位列表
	private final List<CUnit> newUnits;
	// 存储已移除单位列表
	private final List<CUnit> removedUnits;
	// 存储可破坏物体列表
	private final List<CDestructable> destructables;
	// 存储物品列表
	private final List<CItem> items;
	// 存储玩家列表
	private final List<CPlayer> players;
	// 默认玩家单位命令执行器列表
	private final List<CPlayerUnitOrderExecutor> defaultPlayerUnitOrderExecutors;
	// 存储投射物效果列表
	private final List<CEffect> projectiles;
	// 存储新投射物效果列表
	private final List<CEffect> newProjectiles;
	// 处理ID分配的分配器
	private final HandleIdAllocator handleIdAllocator;
	// 模拟渲染控制器，标记为瞬态
	private transient final SimulationRenderController simulationRenderController;
	// 游戏当前第几帧
	private int gameTurnTick = 0; // 游戏当前第几帧
	// 路径网格
	private final PathingGrid pathingGrid;
	// 世界碰撞数据
	private final CWorldCollision worldCollision;
	// 路径寻找处理器数组
	private final CPathfindingProcessor[] pathfindingProcessors;
	// 地图版本
	private final int mapVersion;
	// 游戏常量
	private final CGameplayConstants gameplayConstants;
	// 种子随机数生成器
	private final Random seededRandom;
	// 当前游戏日间时间的流逝
	private float currentGameDayTimeElapsed;
	// 句柄ID到单位的映射
	private final Map<Integer, CUnit> handleIdToUnit = new HashMap<>();
	// 句柄ID到可破坏物体的映射
	private final Map<Integer, CDestructable> handleIdToDestructable = new HashMap<>();
	// 句柄ID到物品的映射
	private final Map<Integer, CItem> handleIdToItem = new HashMap<>();
	// 句柄ID到能力的映射
	private final Map<Integer, CAbility> handleIdToAbility = new HashMap<>();
	// 活动计时器链表
	private final LinkedList<CTimer> activeTimers = new LinkedList<>();
	// 新增计时器列表
	private final List<CTimer> addedTimers = new ArrayList<>();
	// 移除计时器列表
	private final List<CTimer> removedTimers = new ArrayList<>();
	// 命令错误监听器，标记为瞬态
	private transient CommandErrorListener commandErrorListener;
	// 区域管理器
	private final CRegionManager regionManager;
	// 日间时间变量事件列表
	private final List<TimeOfDayVariableEvent> timeOfDayVariableEvents = new ArrayList<>();
	// 日间时间是否被暂停
	private boolean timeOfDaySuspended;
	// 当前是否为白天
	private boolean daytime;
	// 被拥有的树木集合，精灵砍树时使用
	private final Set<CDestructable> ownedTreeSet = new HashSet<>(); // 树木所有者(精灵砍树时使用)集合
	// 全局作用域
	private GlobalScope globalScope;

	public CSimulation(final War3MapConfig config, final int mapVersion, final DataTable miscData,
			final ObjectData parsedUnitData, final ObjectData parsedItemData, final ObjectData parsedDestructableData,
			final ObjectData parsedAbilityData, final ObjectData parsedUpgradeData,
			final DataTable standardUpgradeEffectMeta, final SimulationRenderController simulationRenderController,
			final PathingGrid pathingGrid, final Rectangle entireMapBounds, final Random seededRandom,
			final CommandErrorListener commandErrorListener) {
		// 初始化地图版本和游戏常量
		this.mapVersion = mapVersion;
		this.gameplayConstants = new CGameplayConstants(miscData);
		CFogModifier.setConstants(this.gameplayConstants);
		// 初始化渲染控制器和寻路网格
		this.simulationRenderController = simulationRenderController;
		this.pathingGrid = pathingGrid;
		// 初始化能力数据和升级数据
		this.abilityData = new CAbilityData(parsedAbilityData);
		this.upgradeData = new CUpgradeData(this.gameplayConstants, parsedUpgradeData, standardUpgradeEffectMeta);
		// 初始化单位、建筑、物品和弹道数据
		this.unitData = new CUnitData(this.gameplayConstants, parsedUnitData, this.abilityData, this.upgradeData,
				this.simulationRenderController);
		this.destructableData = new CDestructableData(parsedDestructableData, simulationRenderController);
		this.itemData = new CItemData(parsedItemData);
		// 初始化各种列表
		this.units = new ArrayList<>();
		this.newUnits = new ArrayList<>();
		this.removedUnits = new ArrayList<>();
		this.destructables = new ArrayList<>();
		this.items = new ArrayList<>();
		this.projectiles = new ArrayList<>();
		this.newProjectiles = new ArrayList<>();
		// 初始化ID分配器和世界碰撞检测
		this.handleIdAllocator = new HandleIdAllocator();
		this.worldCollision = new CWorldCollision(entireMapBounds, this.gameplayConstants.getMaxCollisionRadius());
		// 初始化区域管理器和寻路处理器
		this.regionManager = new CRegionManager(entireMapBounds, pathingGrid);
		this.pathfindingProcessors = new CPathfindingProcessor[WarsmashConstants.MAX_PLAYERS];
		for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
			this.pathfindingProcessors[i] = new CPathfindingProcessor(pathingGrid, this.worldCollision);
		}
		// 初始化随机数生成器和玩家列表
		this.seededRandom = seededRandom;
		this.players = new ArrayList<>();
		this.defaultPlayerUnitOrderExecutors = new ArrayList<>();
		// 初始化玩家和默认玩家单位命令执行器
		for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
			final CBasePlayer configPlayer = config.getPlayer(i);
			final War3MapConfigStartLoc startLoc = config.getStartLoc(configPlayer.getStartLocationIndex());
			CRace defaultRace = null;
			// 设置玩家默认种族
			if (configPlayer.isRacePrefSet(WarsmashConstants.RACE_MANAGER.getRandomRacePreference())) {
				final CRaceManagerEntry raceEntry = WarsmashConstants.RACE_MANAGER
						.get(seededRandom.nextInt(WarsmashConstants.RACE_MANAGER.getEntryCount()));
				defaultRace = WarsmashConstants.RACE_MANAGER.getRace(raceEntry.getRaceId());
			}
			else {
				for (int j = 0; j < WarsmashConstants.RACE_MANAGER.getEntryCount(); j++) {
					final CRaceManagerEntry entry = WarsmashConstants.RACE_MANAGER.get(j);
					final CRace race = WarsmashConstants.RACE_MANAGER.getRace(entry.getRaceId());
					final CRacePreference racePreference = WarsmashConstants.RACE_MANAGER
							.getRacePreferenceById(entry.getRacePrefId());
					if (configPlayer.isRacePrefSet(racePreference)) {
						defaultRace = race;
						break;
					}
				}
			}
			// 创建新玩家并设置AI难度
			final CPlayer newPlayer = new CPlayer(defaultRace, new float[] { startLoc.getX(), startLoc.getY() },
					configPlayer, new CPlayerFogOfWar(pathingGrid));
			newPlayer.setAIDifficulty(configPlayer.getAIDifficulty());
			this.players.add(newPlayer);
			this.defaultPlayerUnitOrderExecutors.add(new CPlayerUnitOrderExecutor(this, i));
		}
		// 设置中立玩家名称和状态
		final CPlayer neutralAggressive = this.players.get(this.players.size() - 4);
		neutralAggressive.setName(miscData.getLocalizedString("WESTRING_PLAYER_NA"));
		neutralAggressive.setPlayerState(this, CPlayerState.GIVES_BOUNTY, 1);
		this.players.get(this.players.size() - 3).setName(miscData.getLocalizedString("WESTRING_PLAYER_NV"));
		this.players.get(this.players.size() - 2).setName(miscData.getLocalizedString("WESTRING_PLAYER_NE"));
		final CPlayer neutralPassive = this.players.get(this.players.size() - 1);
		neutralPassive.setName(miscData.getLocalizedString("WESTRING_PLAYER_NP"));
		// 设置玩家之间的联盟关系
		for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
			final CPlayer cPlayer = this.players.get(i);
			cPlayer.setAlliance(neutralPassive, CAllianceType.PASSIVE, true);
			neutralPassive.setAlliance(cPlayer, CAllianceType.PASSIVE, true);
		}
		// 设置命令错误监听器
		this.commandErrorListener = commandErrorListener;
		// 初始化并启动迷雾更新计时器
		final CTimer fogUpdateTimer = new CTimer() {
			@Override
			public void onFire(final CSimulation simulation) {
				updateFogOfWar();
			}
		};
		fogUpdateTimer.setRepeats(true);
		fogUpdateTimer.setTimeoutTime(1.0f);
		fogUpdateTimer.start(this);
	}

	// 获取单位数据
	public CUnitData getUnitData() {
		return this.unitData;
	}

	// 获取升级数据
	public CUpgradeData getUpgradeData() {
		return this.upgradeData;
	}

	// 获取能力数据
	public CAbilityData getAbilityData() {
		return this.abilityData;
	}

	// 获取可破坏物数据
	public CDestructableData getDestructableData() {
		return this.destructableData;
	}

	// 获取物品数据
	public CItemData getItemData() {
		return this.itemData;
	}

	// 获取单位列表
	public List<CUnit> getUnits() {
		return this.units;
	}

	// 获取可破坏物列表
	public List<CDestructable> getDestructables() {
		return this.destructables;
	}

	// 注册计时器
	public void registerTimer(final CTimer timer) {
		this.addedTimers.add(timer);
	}

	// 注销计时器
	public void unregisterTimer(final CTimer timer) {
		this.removedTimers.add(timer);
	}

	// 内部注册计时器
	private void internalRegisterTimer(final CTimer timer) {
		final ListIterator<CTimer> listIterator = this.activeTimers.listIterator(); // 获取活动计时器列表的迭代器
		while (listIterator.hasNext()) { // 遍历活动计时器列表
			final CTimer nextTimer = listIterator.next(); // 获取下一个计时器
			if (nextTimer.getEngineFireTick() > timer.getEngineFireTick()) { // 如果下一个计时器的触发时间大于当前计时器的触发时间
				listIterator.previous(); // 回退到上一个计时器位置
				listIterator.add(timer); // 在当前位置插入新的计时器
				return; // 插入完成后返回
			}
		}
		this.activeTimers.addLast(timer); // 如果所有计时器的触发时间都小于等于当前计时器的触发时间，则将新计时器添加到列表末尾

	}

	// 内部注销计时器
	public void internalUnregisterTimer(final CTimer timer) {
		this.activeTimers.remove(timer);
	}

	// 内部创建单位
	public CUnit internalCreateUnit(final War3ID typeId, final int playerIndex, final float x, final float y,
			final float facing, final BufferedImage buildingPathingPixelMap) {
		// 创建一个新的CUnit实例
		final CUnit unit = this.unitData.create(this, playerIndex, typeId, x, y, facing, buildingPathingPixelMap,
					this.handleIdAllocator);
		// 将新创建的单位添加到newUnits列表中
		this.newUnits.add(unit);
		// 将新单位的handleId与其对应的单位实例存入handleIdToUnit映射中
		this.handleIdToUnit.put(unit.getHandleId(), unit);
		// 返回新创建的单位实例
		return unit;

	}

	// 内部创建可破坏物
	public CDestructable internalCreateDestructable(final War3ID typeId, final float x, final float y,
			final RemovablePathingMapInstance pathingInstance, final RemovablePathingMapInstance pathingInstanceDeath) {
		// 创建一个新的可破坏对象
		final CDestructable dest = this.destructableData.create(this, typeId, x, y, this.handleIdAllocator,
					pathingInstance, pathingInstanceDeath);

		// 将新创建的可破坏对象添加到handleId到可破坏对象的映射中
		this.handleIdToDestructable.put(dest.getHandleId(), dest);

		// 将新创建的可破坏对象添加到世界碰撞检测系统中
		this.worldCollision.addDestructable(dest);

		// 将新创建的可破坏对象添加到可破坏对象列表中
		this.destructables.add(dest);

		// 设置新创建的可破坏对象是否为凋零状态
		dest.setBlighted(dest.checkIsOnBlight(this));

		// 返回新创建的可破坏对象
		return dest;

	}

	// 内部创建物品
	public CItem internalCreateItem(final War3ID alias, final float unitX, final float unitY) {
		// 创建一个新的CItem实例
		final CItem item = this.itemData.create(this, alias, unitX, unitY, this.handleIdAllocator.createId());

		// 将新创建的CItem实例的句柄ID与其对应的实例存入handleIdToItem映射中
		this.handleIdToItem.put(item.getHandleId(), item);

		// 将新创建的CItem实例添加到items集合中
		this.items.add(item);

		// 返回新创建的CItem实例
		return item;

	}

	// 创建物品
	public CItem createItem(final War3ID alias, final float unitX, final float unitY) {
		return this.simulationRenderController.createItem(this, alias, unitX, unitY);
	}

	// 创建单位
	public CUnit createUnit(final War3ID typeId, final int playerIndex, final float x, final float y,
			final float facing) {
		// 创建一个新的单位
		final CUnit createdUnit = this.simulationRenderController.createUnit(this, typeId, playerIndex, x, y, facing);
		// 检查单位是否成功创建
		if (createdUnit != null) {
			// 设置创建的单位
			setupCreatedUnit(createdUnit);
			// 如果单位的碰撞矩形为空，则将其添加到世界碰撞检测中
			if (createdUnit.getCollisionRectangle() == null) {
				this.worldCollision.addUnit(createdUnit);
			}
			// 否则，单位可能已经在默认行为之前被注入到碰撞中了
			// 执行单位的默认行为
			createdUnit.performDefaultBehavior(this);
			// 如果单位是英雄，则触发英雄创建事件
			if (createdUnit.isHero()) {
				heroCreateEvent(createdUnit);
			}
		}
		// 返回创建的单位
		return createdUnit;

	}

	// 创建简单单位
	public CUnit createUnitSimple(final War3ID typeId, final int playerIndex, final float x, final float y,
			final float facing) {
		final CUnit newUnit = createUnit(typeId, playerIndex, x, y, facing); // 创建一个新的单位
		if (newUnit != null) { // 如果新单位创建成功
			final CPlayer player = getPlayer(playerIndex); // 获取对应的玩家对象
			final CUnitType newUnitType = newUnit.getUnitType(); // 获取新单位的类型
			final int foodUsed = newUnitType.getFoodUsed(); // 获取创建该单位所需的粮食
			newUnit.setFoodUsed(foodUsed); // 设置新单位使用的粮食数量
			player.setFoodUsed(player.getFoodUsed() + foodUsed); // 更新玩家已使用的粮食总量
			if (newUnitType.getFoodMade() != 0) { // 如果新单位能生产粮食
				player.setFoodCap(player.getFoodCap() + newUnitType.getFoodMade()); // 更新玩家的食物上限
			}
			player.addTechtreeUnlocked(this, typeId); // 解锁玩家的技术树中的相应项
			// 微调单位位置
			newUnit.setPointAndCheckUnstuck(x, y, this);
			if (!newUnit.isBuilding()) { // 如果新单位不是建筑
				newUnit.getUnitAnimationListener().playAnimation(false, PrimaryTag.BIRTH, SequenceUtils.EMPTY, 1.0f,
					  true); // 播放出生动画
				newUnit.getUnitAnimationListener().queueAnimation(PrimaryTag.STAND, SequenceUtils.EMPTY, true); // 排队播放站立动画
			}
		}
		return newUnit; // 返回新创建的单位对象

	}

	// 创建可破坏物
	public CDestructable createDestructable(final War3ID typeId, final float x, final float y, final float facing,
			final float scale, final int variation) {
		return this.simulationRenderController.createDestructable(typeId, x, y, facing, scale, variation);
	}

	// 创建三维可破坏物
	public CDestructable createDestructableZ(final War3ID typeId, final float x, final float y, final float z,
			final float facing, final float scale, final int variation) {
		return this.simulationRenderController.createDestructableZ(typeId, x, y, z, facing, scale, variation);
	}

	// 根据句柄ID获取单位
	public CUnit getUnit(final int handleId) {
		return this.handleIdToUnit.get(handleId);
	}

	// 根据句柄ID获取能力
	public CAbility getAbility(final int handleId) {
		return this.handleIdToAbility.get(handleId);
	}

	// 当能力被添加到单位时
	protected void onAbilityAddedToUnit(final CUnit unit, final CAbility ability) {
		this.handleIdToAbility.put(ability.getHandleId(), ability);
	}

	// 当能力被从单位移除时
	protected void onAbilityRemovedFromUnit(final CUnit unit, final CAbility ability) {
		this.handleIdToAbility.remove(ability.getHandleId());
	}

	// 创建攻击投射物
	public CAttackProjectile createProjectile(final CUnit source, final float launchX, final float launchY,
			final float launchFacing, final CUnitAttackMissile attack, final AbilityTarget target, final float damage,
			final int bounceIndex, final CUnitAttackListener attackListener) {
		final CAttackProjectile projectile = this.simulationRenderController.createAttackProjectile(this, launchX,
				launchY, launchFacing, source, attack, target, damage, bounceIndex, attackListener);
		this.newProjectiles.add(projectile);
		return projectile;
	}

	// 创建能力投射物
	public CAbilityProjectile createProjectile(final CUnit source, final War3ID spellAlias, final float launchX,
			final float launchY, final float launchFacing, final float speed, final boolean homing,
			final AbilityTarget target, final CAbilityProjectileListener projectileListener) {
		final CAbilityProjectile projectile = this.simulationRenderController.createProjectile(this, launchX, launchY,
				launchFacing, speed, homing, source, spellAlias, target, projectileListener);
		this.newProjectiles.add(projectile);
		projectileListener.onLaunch(this, target);
		return projectile;
	}

	// 创建碰撞投射物
	public CCollisionProjectile createCollisionProjectile(final CUnit source, final War3ID spellAlias,
			final float launchX, final float launchY, final float launchFacing, final float speed, final boolean homing,
			final AbilityTarget target, final int maxHits, final int hitsPerTarget, final float startingRadius,
			final float finalRadius, final float collisionInterval,
			final CAbilityCollisionProjectileListener projectileListener, final boolean provideCounts) {
		final CCollisionProjectile projectile = this.simulationRenderController.createCollisionProjectile(this, launchX,
				launchY, launchFacing, speed, homing, source, spellAlias, target, maxHits, hitsPerTarget,
				startingRadius, finalRadius, collisionInterval, projectileListener, provideCounts);
		this.newProjectiles.add(projectile);
		projectileListener.onLaunch(this, target);
		return projectile;
	}

	// 创建伪投射物
	public CPsuedoProjectile createPseudoProjectile(final CUnit source, final War3ID spellAlias,
			final CEffectType effectType, final int effectArtIndex, final float launchX, final float launchY,
			final float launchFacing, final float speed, final float projectileStepInterval,
			final int projectileArtSkip, final boolean homing, final AbilityTarget target, final int maxHits,
			final int hitsPerTarget, final float startingRadius, final float finalRadius,
			final CAbilityCollisionProjectileListener projectileListener, final boolean provideCounts) {
		final CPsuedoProjectile projectile = this.simulationRenderController.createPseudoProjectile(this, launchX,
				launchY, launchFacing, speed, projectileStepInterval, projectileArtSkip, homing, source, spellAlias,
				effectType, effectArtIndex, target, maxHits, hitsPerTarget, startingRadius, finalRadius,
				projectileListener, provideCounts);
		this.newProjectiles.add(projectile);
		projectileListener.onLaunch(this, target);
		return projectile;
	}

	// 注册效果
	public void registerEffect(final CEffect effect) {
		this.newProjectiles.add(effect);
	}


	// 创建闪电渲染效果
	public SimulationRenderComponentLightning createLightning(final CUnit source, final War3ID lightningId,
			final CUnit target) {
		return this.simulationRenderController.createLightning(this, lightningId, source, target);
	}
	/**
	 * 创建闪电特效
	 *
	 * @param source    闪电的来源单位
	 * @param lightningId 闪电的ID
	 * @param target    闪电的目标单位
	 * @param duration  闪电持续的时间
	 * @return 创建的闪电组件
	 */
	public SimulationRenderComponentLightning createLightning(final CUnit source, final War3ID lightningId,
			final CUnit target, final Float duration) {
		return this.simulationRenderController.createLightning(this, lightningId, source, target, duration);
	}

	/**
	 * 创建技能闪电特效
	 *
	 * @param source       闪电的来源单位
	 * @param lightningId  闪电的ID
	 * @param lightningIndex 闪电的索引
	 * @param target       闪电的目标单位
	 * @return 创建的技能闪电组件
	 */
	public SimulationRenderComponentLightning createAbilityLightning(final CUnit source, final War3ID lightningId,
			final int lightningIndex, final CUnit target) {
		return this.simulationRenderController.createAbilityLightning(this, lightningId, source, target,
				lightningIndex);
	}

	/**
	 * 创建带持续时间的技能闪电特效
	 *
	 * @param source       闪电的来源单位
	 * @param lightningId  闪电的ID
	 * @param lightningIndex 闪电的索引
	 * @param target       闪电的目标单位
	 * @param duration     闪电持续的时间
	 * @return 创建的技能闪电组件
	 */
	public SimulationRenderComponentLightning createAbilityLightning(final CUnit source, final War3ID lightningId,
			final int lightningIndex, final CUnit target, final Float duration) {
		return this.simulationRenderController.createAbilityLightning(this, lightningId, source, target, lightningIndex,
				duration);
	}

	/**
	 * 创建瞬时攻击效果
	 *
	 * @param source  攻击的来源单位
	 * @param attack  瞬时攻击对象
	 * @param target  攻击的目标小部件
	 */
	public void createInstantAttackEffect(final CUnit source, final CUnitAttackInstant attack, final CWidget target) {
		this.simulationRenderController.createInstantAttackEffect(this, source, attack, target);
	}

	/**
	 * 获取路径网格
	 *
	 * @return 路径网格
	 */
	public PathingGrid getPathingGrid() {
		return this.pathingGrid;
	}

	/**
	 * 查找简单的缓慢路径
	 *
	 * @param ignoreIntersectionsWithThisUnit     忽略与此单位的交叉
	 * @param ignoreIntersectionsWithThisSecondUnit 忽略与第二个单位的交叉
	 * @param startX                              起始X坐标
	 * @param startY                              起始Y坐标
	 * @param goal                                 目标点
	 * @param movementType                        移动类型
	 * @param collisionSize                       碰撞大小
	 * @param allowSmoothing                      是否允许平滑
	 * @param queueItem                           路径行为项目
	 */
	public void findNaiveSlowPath(final CUnit ignoreIntersectionsWithThisUnit,
			final CUnit ignoreIntersectionsWithThisSecondUnit, final float startX, final float startY,
			final Point2D.Float goal, final PathingGrid.MovementType movementType, final float collisionSize,
			final boolean allowSmoothing, final CBehaviorMove queueItem) {
		final int playerIndex = queueItem.getUnit().getPlayerIndex();
		this.pathfindingProcessors[playerIndex].findNaiveSlowPath(ignoreIntersectionsWithThisUnit,
				ignoreIntersectionsWithThisSecondUnit, startX, startY, goal, movementType, collisionSize,
				allowSmoothing, queueItem);
	}

	/**
	 * 从路径查找队列中移除单位
	 *
	 * @param behaviorMove 要移除的行为移动
	 */
	public void removeFromPathfindingQueue(final CBehaviorMove behaviorMove) {
		final int playerIndex = behaviorMove.getUnit().getPlayerIndex();
		this.pathfindingProcessors[playerIndex].removeFromPathfindingQueue(behaviorMove);
	}

	/**
	 * 更新战争迷雾状态
	 */
	protected void updateFogOfWar() {
		// 遍历所有玩家，将玩家的可视区域转换为战争迷雾区域，并更新玩家的迷雾修饰符
		for (final CPlayer player : this.players) {
			// 将玩家的可视区域转换为战争迷雾区域
			player.getFogOfWar().convertVisibleToFogged();
			// 更新玩家的迷雾修饰符，传入当前模拟环境实例
			player.updateFogModifiers(this);
		}

		// 遍历所有单位，更新单位的战争迷雾状态
		for (final CUnit unit : this.units) {
			// 更新单位的战争迷雾状态，传入当前模拟环境实例
			unit.updateFogOfWar(this);
		}

	}

	/**
	 * 更新游戏状态
	 * 遍历 unit.update
	 * 遍历 projectiles update
	 * 遍历 计时器
	 * 遍历
	 */
	public void update() {
		final Iterator<CUnit> unitIterator = this.units.iterator(); // 获取单位集合的迭代器
		while (unitIterator.hasNext()) { // 遍历单位集合
			final CUnit unit = unitIterator.next(); // 获取当前单位
			if (unit.update(this)) { // 调用单位的更新方法，如果返回true，表示单位需要被移除
				unitIterator.remove(); // 从集合中移除单位
				for (final CAbility ability : unit.getAbilities()) { // 遍历单位的能力
					this.handleIdToAbility.remove(ability.getHandleId()); // 移除能力映射
				}
				this.handleIdToUnit.remove(unit.getHandleId()); // 从单位映射中移除单位
				this.simulationRenderController.removeUnit(unit); // 通知渲染控制器移除单位
				getPlayerHeroes(unit.getPlayerIndex()).remove(unit); // 从玩家英雄列表中移除单位
				unit.onRemove(this); // 调用单位的移除回调方法
			}
		}
		finishAddingNewUnits(); // 完成新单位的添加

		// 创建一个迭代器用于遍历projectiles集合
		final Iterator<CEffect> projectileIterator = this.projectiles.iterator();
		// 遍历projectiles集合中的每一个CEffect对象
		while (projectileIterator.hasNext()) {
			// 获取当前CEffect对象
			final CEffect projectile = projectileIterator.next();
			// 如果当前CEffect对象的update方法返回true，表示需要移除该对象
			if (projectile.update(this)) {
				// 使用迭代器的remove方法安全地移除当前对象
				projectileIterator.remove();
			}
		}
		// 将newProjectiles集合中的所有元素添加到projectiles集合中
		this.projectiles.addAll(this.newProjectiles);
		// 清空newProjectiles集合，为下一次添加做准备
		this.newProjectiles.clear();
		// 遍历pathfindingProcessors集合中的每一个CPathfindingProcessor对象
		for (final CPathfindingProcessor pathfindingProcessor : this.pathfindingProcessors) {
			// 调用每个CPathfindingProcessor对象的update方法
			pathfindingProcessor.update(this);
		}
		// 增加游戏回合数
		this.gameTurnTick++;

		// 获取增加前的时间
		final float timeOfDayBefore = getGameTimeOfDay();

		// 如果时间没有暂停，则更新当前游戏日已过去的时间
		if (!this.timeOfDaySuspended) {
			this.currentGameDayTimeElapsed = (this.currentGameDayTimeElapsed + WarsmashConstants.SIMULATION_STEP_TIME)
					% this.gameplayConstants.getGameDayLength();
		}

		// 获取增加后的时间
		final float timeOfDayAfter = getGameTimeOfDay();

		// 判断当前是否为白天
		this.daytime = (timeOfDayAfter >= this.gameplayConstants.getDawnTimeGameHours())
				&& (timeOfDayAfter < this.gameplayConstants.getDuskTimeGameHours());

		// 注册新添加的计时器
		for (final CTimer timer : this.addedTimers) {
			internalRegisterTimer(timer);
		}

		// 清空已添加的计时器列表
		this.addedTimers.clear();

		// 注销已移除的计时器
		for (final CTimer timer : this.removedTimers) {
			internalUnregisterTimer(timer);
		}

		// 清空已移除的计时器列表
		this.removedTimers.clear();

		// 创建一个新的计时器集合，用于检查重复添加的计时器
		final Set<CTimer> timers = new HashSet<>();
		for (final CTimer timer : this.activeTimers) {
			if (!timers.add(timer)) {
				throw new IllegalStateException("Duplicate timer add: " + timer);
			}
		}

		// 触发所有到期的计时器
		while (!this.activeTimers.isEmpty() && (this.activeTimers.peek().getEngineFireTick() <= this.gameTurnTick)) {
			this.activeTimers.pop().fire(this);
		}

		// 触发所有因时间变化而匹配的事件
		for (final TimeOfDayVariableEvent timeOfDayEvent : this.timeOfDayVariableEvents) {
			if (!timeOfDayEvent.isMatching(timeOfDayBefore) && timeOfDayEvent.isMatching(timeOfDayAfter)) {
				timeOfDayEvent.fire();
			}
		}

		// 运行全局作用域中的线程
		this.globalScope.runThreads();

	}

	/**
	 * 移除单位
	 *
	 * @param unit 要移除的单位
	 */
	public void removeUnit(final CUnit unit) {
		unit.setHidden(true);
		this.removedUnits.add(unit);
	}

	/**
	 * 完成添加新单位的操作
	 */
	private void finishAddingNewUnits() {
		// 将新单位添加到单位列表中
		this.units.addAll(this.newUnits);
		// 清空新单位列表，因为它们已经被添加
		this.newUnits.clear();

		// 遍历被移除的单位列表
		for (final CUnit unit : this.removedUnits) {
			// 从单位列表中移除该单位
			this.units.remove(unit);

			// 遍历该单位的所有能力
			for (final CAbility ability : unit.getAbilities()) {
				// 移除与该能力相关的ID映射
				this.handleIdToAbility.remove(ability.getHandleId());
			}

			// 移除与该单位相关的ID映射
			this.handleIdToUnit.remove(unit.getHandleId());

			// 从模拟渲染控制器中移除该单位
			this.simulationRenderController.removeUnit(unit);

			// 从玩家英雄列表中移除该单位
			getPlayerHeroes(unit.getPlayerIndex()).remove(unit);

			// 调用单位的onRemove方法，执行单位被移除时的逻辑
			unit.onRemove(this);
		}

		// 清空被移除单位列表，因为它们已经被处理
		this.removedUnits.clear();

	}

	/**
	 * 获取游戏的时间 游戏总共过去的小时数量
	 *
	 * @return 当前时间的游戏时间
	 */
	public float getGameTimeOfDay() {
		// 计算当前游戏日已过去的时间占整个游戏日的百分比，并将其转换为小时数
		// this.currentGameDayTimeElapsed: 当前游戏日已过去的时间
		// this.gameplayConstants.getGameDayLength(): 游戏日的总长度
		// this.gameplayConstants.getGameDayHours(): 游戏日的总小时数
		return (this.currentGameDayTimeElapsed / this.gameplayConstants.getGameDayLength())
		  	* this.gameplayConstants.getGameDayHours();

	}

	/**
	 * 设置游戏的时间
	 *
	 * @param value 要设置的游戏时间
	 */
	public void setGameTimeOfDay(final float value) {
		// 计算自游戏开始以来经过的时间（以游戏天为单位）
		// value 是自上次更新以来的时间增量
		final float elapsed = value / this.gameplayConstants.getGameDayHours();

		// 将经过的时间转换为游戏天的长度单位
		// this.gameplayConstants.getGameDayLength() 返回一个游戏天的长度（以相同的单位为基准）
		this.currentGameDayTimeElapsed = elapsed * this.gameplayConstants.getGameDayLength();

	}

	/**
	 * 获取游戏帧
	 *
	 * @return 获取游戏帧
	 */
	public int getGameTurnTick() {
		return this.gameTurnTick;
	}

	/**
	 * 获取世界碰撞体
	 *
	 * @return 世界碰撞体
	 */
	public CWorldCollision getWorldCollision() {
		return this.worldCollision;
	}

	/**
	 * 获取区域管理器
	 *
	 * @return 区域管理器
	 */
	public CRegionManager getRegionManager() {
		return this.regionManager;
	}

	/**
	 * 获取游戏玩法常量
	 *
	 * @return 游戏玩法常量
	 */
	public CGameplayConstants getGameplayConstants() {
		return this.gameplayConstants;
	}

	/**
	 * 获取随机数生成器
	 *
	 * @return 初始化随机数的对象
	 */
	public Random getSeededRandom() {
		return this.seededRandom;
	}

	/**
	 * 单位受伤事件
	 *
	 * @param damagedUnit  受伤的单位
	 * @param weaponSound   武器音效
	 * @param armorType     装甲类型
	 */
	public void unitDamageEvent(final CUnit damagedUnit, final String weaponSound, final String armorType) {
		this.simulationRenderController.spawnDamageSound(damagedUnit, weaponSound, armorType);
	}

	/**
	 * 可摧毁物体受伤事件
	 *
	 * @param damagedDestructable 受伤的可摧毁物体
	 * @param weaponSound          武器音效
	 * @param armorType            装甲类型
	 */
	public void destructableDamageEvent(final CDestructable damagedDestructable, final String weaponSound,
			final String armorType) {
		this.simulationRenderController.spawnDamageSound(damagedDestructable, weaponSound, armorType);
	}

	/**
	 * 物品受伤事件
	 *
	 * @param damageItem 受伤的物品
	 * @param weaponSound 武器音效
	 * @param armorType   装甲类型
	 */
	public void itemDamageEvent(final CItem damageItem, final String weaponSound, final String armorType) {
		this.simulationRenderController.spawnDamageSound(damageItem, weaponSound, armorType);
	}

	/**
	 * 单位建造事件
	 *
	 * @param constructingUnit    正在建造的单位
	 * @param constructedStructure 已建造的结构
	 */
	public void unitConstructedEvent(final CUnit constructingUnit, final CUnit constructedStructure) {
		this.simulationRenderController.spawnUnitConstructionSound(constructingUnit, constructedStructure);
	}

	/**
	 * 单位升级事件
	 *
	 * @param cUnit        升级的单位
	 * @param upgradeIdType 升级类型的ID
	 */
	public void unitUpgradingEvent(final CUnit cUnit, final War3ID upgradeIdType) {
		this.simulationRenderController.unitUpgradingEvent(cUnit, upgradeIdType);
	}

	/**
	 * 单位取消升级事件
	 *
	 * @param cUnit        取消升级的单位
	 * @param upgradeIdType 升级类型的ID
	 */
	public void unitCancelUpgradingEvent(final CUnit cUnit, final War3ID upgradeIdType) {
		this.simulationRenderController.unitCancelUpgradingEvent(cUnit, upgradeIdType);
	}

	/**
	 * 获取玩家对象
	 *
	 * @param index 玩家索引
	 * @return 指定索引的玩家对象
	 */
	@Override
	public CPlayer getPlayer(final int index) {
		return this.players.get(index);
	}

	/**
	 * 获取默认玩家单位命令执行器
	 *
	 * @param index 玩家索引
	 * @return 默认玩家单位命令执行器
	 */
	public CPlayerUnitOrderExecutor getDefaultPlayerUnitOrderExecutor(final int index) {
		return this.defaultPlayerUnitOrderExecutors.get(index);
	}

	/**
	 * 获取命令错误监听器
	 *
	 * @return 命令错误监听器
	 */
	public CommandErrorListener getCommandErrorListener() {
		return this.commandErrorListener;
	}

	/**
	 * 完成单位建造事件
	 *
	 * @param constructedStructure 已建造的结构
	 */
	public void unitConstructFinishEvent(final CUnit constructedStructure) {
		this.simulationRenderController.spawnUnitConstructionFinishSound(constructedStructure);
	}

	/**
	 * 完成单位升级事件
	 *
	 * @param constructedStructure 已建造的结构
	 */
	public void unitUpgradeFinishEvent(final CUnit constructedStructure) {
		this.simulationRenderController.spawnUnitUpgradeFinishSound(constructedStructure);
	}

	/**
	 * 创建单位死亡爆炸效果
	 *
	 * @param cUnit                    死亡单位
	 * @param explodesOnDeathBuffId    死亡时爆炸的Buff ID
	 */
	public void createDeathExplodeEffect(final CUnit cUnit, final War3ID explodesOnDeathBuffId) {
		this.simulationRenderController.spawnDeathExplodeEffect(cUnit, explodesOnDeathBuffId);
	}

	/**
	 * 获取句柄ID分配器
	 *
	 * @return 句柄ID分配器
	 */
	public HandleIdAllocator getHandleIdAllocator() {
		return this.handleIdAllocator;
	}

	/**
	 * 单位训练事件
	 *
	 * @param trainingUnit 训练中的单位
	 * @param trainedUnit  训练完成的单位
	 */
	public void unitTrainedEvent(final CUnit trainingUnit, final CUnit trainedUnit) {
		this.simulationRenderController.spawnUnitReadySound(trainedUnit);
	}

	/**
	 * 研究完成事件
	 *
	 * @param cUnit         完成研究的单位
	 * @param queuedRawcode 排队的原始代码
	 * @param level        研究级别
	 */
	public void researchFinishEvent(final CUnit cUnit, final War3ID queuedRawcode, final int level) {
		getCommandErrorListener().showUpgradeCompleteAlert(cUnit.getPlayerIndex(), queuedRawcode, level);
	}

	/**
	 * 英雄复活事件
	 *
	 * @param trainingUnit 训练中的单位
	 * @param trainedUnit  复活的单位
	 */
	public void heroReviveEvent(final CUnit trainingUnit, final CUnit trainedUnit) {
		this.simulationRenderController.heroRevived(trainedUnit);
		this.simulationRenderController.spawnUnitReadySound(trainedUnit);
	}


	// 当单位重新定位时调用的方法
	public void unitRepositioned(final CUnit cUnit) {
		this.simulationRenderController.unitRepositioned(cUnit);
	}


	// 获得资源（金币/木材）飘字
	public void unitGainResourceEvent(final CUnit unit, final int playerIndex, final ResourceType resourceType,
			final int amount) {
		switch (resourceType) {
		case GOLD: {
			spawnTextTag(unit, playerIndex, TextTagConfigType.GOLD, amount);
			break;
		}
		case LUMBER: {
			spawnTextTag(unit, playerIndex, TextTagConfigType.LUMBER, amount);
			break;
		}
		}
	}
	/**
	 * 在指定的单位上生成文本标签。
	 * @param unit 目标单位
	 * @param playerIndex 玩家索引
	 * @param type 文本标签类型
	 * @param amount 数量（当message为空时使用）
	 */
	public void spawnTextTag(final CUnit unit, final int playerIndex, final TextTagConfigType type, final int amount) {
		this.simulationRenderController.spawnTextTag(unit, type, amount);
	}

	/**
	 * 在指定的单位上生成带有消息的文本标签。
	 * @param unit 目标单位
	 * @param playerIndex 玩家索引
	 * @param type 文本标签类型
	 * @param message 文本消息
	 */
	public void spawnTextTag(final CUnit unit, final int playerIndex, final TextTagConfigType type,
			final String message) {
		this.simulationRenderController.spawnTextTag(unit, type, message);
	}

	/**
	 * 当单位升级时触发的事件。
	 * @param unit 升级的单位
	 */
	public void unitGainLevelEvent(final CUnit unit) {
		this.players.get(unit.getPlayerIndex()).fireHeroLevelEvents(unit);
		this.simulationRenderController.spawnGainLevelEffect(unit);
	}

	/**
	 * 创建英雄时的事件。
	 * @param hero 新创建的英雄单位
	 */
	public void heroCreateEvent(final CUnit hero) {
		getPlayerHeroes(hero.getPlayerIndex()).add(hero);
	}

	/**
	 * 单位拾取物品时的事件。
	 * @param cUnit 拾取物品的单位
	 * @param item 被拾取的物品
	 */
	public void unitPickUpItemEvent(final CUnit cUnit, final CItem item) {
		this.simulationRenderController.spawnUIUnitGetItemSound(cUnit, item);
	}

	/**
	 * 单位丢弃物品时的事件。
	 * @param cUnit 丢弃物品的单位
	 * @param item 被丢弃的物品
	 */
	public void unitDropItemEvent(final CUnit cUnit, final CItem item) {
		this.simulationRenderController.spawnUIUnitDropItemSound(cUnit, item);
	}

	/**
	 * 获取指定玩家的所有英雄单位。
	 * @param playerIndex 玩家索引
	 * @return 玩家的英雄单位列表
	 */
	public List<CUnit> getPlayerHeroes(final int playerIndex) {
		return this.players.get(playerIndex).getHeroes();
	}

	/**
	 * 当所有单位加载完成后调用。
	 * 初始化新添加的单位，并更新玩家的资源和技术树。
	 */
	public void unitsLoaded() {
		// 完成新单位的添加
		finishAddingNewUnits();

		// 遍历所有单位
		for (final CUnit unit : this.units) {
			// 获取单位所属的玩家
			final CPlayer player = this.players.get(unit.getPlayerIndex());

			// 设置玩家因该单位消耗的食物量
			player.setUnitFoodUsed(unit, unit.getUnitType().getFoodUsed());

			// 设置玩家因该单位生产的食物量
			player.setUnitFoodMade(unit, unit.getUnitType().getFoodMade());

			// 将该单位解锁的技术树节点添加到玩家的记录中
			player.addTechtreeUnlocked(this, unit.getTypeId());
		}

	}

	/**
	 * 根据句柄ID获取对应的UI组件。
	 * @param handleId 句柄ID
	 * @return 对应的UI组件，如果没有找到则返回null
	 */
	public CWidget getWidget(final int handleId) {
		final CUnit unit = this.handleIdToUnit.get(handleId);
		if (unit != null) {
			return unit;
		}
		final CDestructable destructable = this.handleIdToDestructable.get(handleId);
		if (destructable != null) {
			return destructable;
		}
		final CItem item = this.handleIdToItem.get(handleId);
		if (item != null) {
			return item;
		}
		return null;
	}

	/**
	 * 在指定的单位上创建效果。
	 * @param unit 目标单位
	 * @param effectPath 效果文件路径
	 */
	public void createEffectOnUnit(final CUnit unit, final String effectPath) {
		this.simulationRenderController.spawnEffectOnUnit(unit, effectPath);
	}


	// 创建施法效果
	public void createTemporarySpellEffectOnUnit(final CUnit unit, final War3ID alias, final CEffectType effectType) {
		this.simulationRenderController.spawnTemporarySpellEffectOnUnit(unit, alias, effectType);
	}
	/**
	 * 在单位上创建持续的法术效果。
	 *
	 * @param unit 单位对象
	 * @param alias 法术别名
	 * @param effectType 效果类型
	 * @return 持续法术效果模型
	 */
	public SimulationRenderComponentModel createPersistentSpellEffectOnUnit(final CUnit unit, final War3ID alias,
			final CEffectType effectType) {
		return this.simulationRenderController.spawnPersistentSpellEffectOnUnit(unit, alias, effectType);
	}

	/**
	 * 在单位上创建指定索引的持续法术效果。
	 *
	 * @param unit 单位对象
	 * @param alias 法术别名
	 * @param effectType 效果类型
	 * @param index 效果索引
	 * @return 持续法术效果模型
	 */
	public SimulationRenderComponentModel createPersistentSpellEffectOnUnit(final CUnit unit, final War3ID alias,
			final CEffectType effectType, final int index) {
		return this.simulationRenderController.spawnPersistentSpellEffectOnUnit(unit, alias, effectType, index);
	}

	/**
	 * 播放单位的声音效果。
	 *
	 * @param caster 施法单位
	 * @param alias 声音别名
	 * @return 声音效果组件
	 */
	public SimulationRenderComponent unitSoundEffectEvent(final CUnit caster, final War3ID alias) {
		return this.simulationRenderController.spawnAbilitySoundEffect(caster, alias);
	}

	/**
	 * 循环播放单位的声音效果。
	 *
	 * @param caster 施法单位
	 * @param alias 声音别名
	 * @return 声音效果组件
	 */
	public SimulationRenderComponent unitLoopSoundEffectEvent(final CUnit caster, final War3ID alias) {
		return this.simulationRenderController.loopAbilitySoundEffect(caster, alias);
	}

	/**
	 * 停止单位的声音效果。
	 *
	 * @param caster 施法单位
	 * @param alias 声音别名
	 */
	public void unitStopSoundEffectEvent(final CUnit caster, final War3ID alias) {
		this.simulationRenderController.stopAbilitySoundEffect(caster, alias);
	}

	/**
	 * 替换单位的首选选择。
	 *
	 * @param unit 原单位
	 * @param newUnit 新单位
	 */
	public void unitPreferredSelectionReplacement(final CUnit unit, final CUnit newUnit) {
		this.simulationRenderController.unitPreferredSelectionReplacement(unit, newUnit);
	}

	/**
	 * 注册一个与时间相关的触发事件。
	 *
	 * @param globalScope 全局作用域
	 * @param trigger 触发器
	 * @param opcode 操作码
	 * @param doubleValue 双精度值
	 * @return 可移除的触发事件
	 */
	public RemovableTriggerEvent registerTimeOfDayEvent(final GlobalScope globalScope, final Trigger trigger,
			final CLimitOp opcode, final double doubleValue) {
		final TimeOfDayVariableEvent timeOfDayVariableEvent = new TimeOfDayVariableEvent(trigger, opcode, doubleValue,
				globalScope);
		this.timeOfDayVariableEvents.add(timeOfDayVariableEvent);
		return new RemovableTriggerEvent() {
			@Override
			public void remove() {
				CSimulation.this.timeOfDayVariableEvents.remove(timeOfDayVariableEvent);
			}
		};
	}

	/**
	 * 注册一个游戏事件。
	 *
	 * @param globalScope 全局作用域
	 * @param trigger 触发器
	 * @param gameEvent 游戏事件
	 * @return 可移除的触发事件
	 */
	public RemovableTriggerEvent registerGameEvent(final GlobalScope globalScope, final Trigger trigger,
			final JassGameEventsWar3 gameEvent) {
		System.err.println("Game event not yet implemented: " + gameEvent);
		return new RemovableTriggerEvent() {
			@Override
			public void remove() {
			}
		};
	}

	/**
	 * 处理英雄死亡事件。
	 *
	 * @param cUnit 英雄单位
	 */
	public void heroDeathEvent(final CUnit cUnit) {
		this.simulationRenderController.heroDeathEvent(cUnit);
	}

	/**
	 * 处理英雄消散事件。
	 *
	 * @param cUnit 英雄单位
	 */
	public void heroDissipateEvent(final CUnit cUnit) {
		getPlayer(cUnit.getPlayerIndex()).onHeroDeath(cUnit);
	}

	/**
	 * 移除一个物品。
	 *
	 * @param cItem 物品对象
	 */
	public void removeItem(final CItem cItem) {
		cItem.forceDropIfHeld(this);
		cItem.setHidden(true); // TODO fix
		cItem.setLife(this, 0);
	}


	// 在可破坏物上 创建施法特效
	public SimulationRenderComponentModel createSpellEffectOverDestructable(final CUnit source,
			final CDestructable target, final War3ID alias, final float artAttachmentHeight) {
		return this.simulationRenderController.createSpellEffectOverDestructable(source, target, alias,
				artAttachmentHeight);
	}
	/**
	 * 在指定点生成一个法术效果。
	 *
	 * @param x         法术效果生成的x坐标
	 * @param y         法术效果生成的y坐标
	 * @param facing    面向的方向
	 * @param alias     法术的别名
	 * @param effectType 法术效果的类型
	 * @param index     效果的索引
	 * @return 返回生成的法术效果模型
	 */
	public SimulationRenderComponentModel spawnSpellEffectOnPoint(final float x, final float y, final float facing,
																  final War3ID alias, final CEffectType effectType, final int index) {
		return this.simulationRenderController.spawnSpellEffectOnPoint(x, y, facing, alias, effectType, index);
	}

	/**
	 * 在指定点生成一个临时的法术效果。
	 *
	 * @param x         法术效果生成的x坐标
	 * @param y         法术效果生成的y坐标
	 * @param facing    面向的方向
	 * @param alias     法术的别名
	 * @param effectType 法术效果的类型
	 * @param index     效果的索引
	 */
	public void spawnTemporarySpellEffectOnPoint(final float x, final float y, final float facing, final War3ID alias,
												 final CEffectType effectType, final int index) {
		this.simulationRenderController.spawnTemporarySpellEffectOnPoint(x, y, facing, alias, effectType, index);
	}

	/**
	 * 将一个可破坏物标记为拥有。
	 *
	 * @param target 要标记的可破坏物
	 */
	public void tagTreeOwned(final CDestructable target) {
		this.ownedTreeSet.add(target);
	}

	/**
	 * 取消标记一个可破坏物的拥有状态。
	 *
	 * @param target 要取消标记的可破坏物
	 */
	public void untagTreeOwned(final CDestructable target) {
		this.ownedTreeSet.remove(target);
	}

	/**
	 * 检查一个树是否被标记为拥有。
	 *
	 * @param tree 要检查的树
	 * @return 如果树被标记为拥有则返回true，否则返回false
	 */
	public boolean isTreeOwned(final CDestructable tree) {
		return this.ownedTreeSet.contains(tree);
	}


	// TimeOfDayVariableEvent 类继承自 VariableEvent，用于处理与时间相关的变量事件
	private static final class TimeOfDayVariableEvent extends VariableEvent {
		private final GlobalScope globalScope; // 全局作用域

		/**
		 * 构造函数，初始化 TimeOfDayVariableEvent 对象
		 *
		 * @param trigger      触发器
		 * @param limitOp      限制操作
		 * @param doubleValue  双精度值
		 * @param globalScope  全局作用域
		 */
		public TimeOfDayVariableEvent(final Trigger trigger, final CLimitOp limitOp, final double doubleValue,
									  final GlobalScope globalScope) {
			super(trigger, limitOp, doubleValue);
			this.globalScope = globalScope;
		}

		/**
		 * 触发事件
		 */
		public void fire() {
			this.fire(this.globalScope);
		}
	}

	/**
	 * 注册玩家失败事件的触发器，当前未实现
	 *
	 * @param globalScope 全局作用域
	 * @param whichTrigger 触发器
	 * @param whichPlayer 玩家
	 * @return 不执行任何操作的 RemovableTriggerEvent
	 */
	public RemovableTriggerEvent registerEventPlayerDefeat(final GlobalScope globalScope, final Trigger whichTrigger,
														   final CPlayerJass whichPlayer) {
		if (true) {
			throw new UnsupportedOperationException("registerEventPlayerDefeat is NYI");
		}
		return RemovableTriggerEvent.DO_NOTHING;
	}

	/**
	 * 注册玩家胜利事件的触发器，当前未实现
	 *
	 * @param globalScope 全局作用域
	 * @param whichTrigger 触发器
	 * @param whichPlayer 玩家
	 * @return 不执行任何操作的 RemovableTriggerEvent
	 */
	public RemovableTriggerEvent registerEventPlayerVictory(final GlobalScope globalScope, final Trigger whichTrigger,
															final CPlayerJass whichPlayer) {
		if (true) {
			throw new UnsupportedOperationException("registerEventPlayerVictory is NYI");
		}
		return RemovableTriggerEvent.DO_NOTHING;
	}
	/**
	 * 设置所有物品类型槽位的数量，当前版本忽略此调用，因为市场功能尚未实现。
	 *
	 * @param slots 槽位数量
	 */
	public void setAllItemTypeSlots(final int slots) {
		System.err.println(
				"忽略设置所有物品类型槽位为: " + slots + "（市场功能尚未实现）");
	}

	/**
	 * 设置所有单位类型槽位的数量，当前版本忽略此调用，因为市场功能尚未实现。
	 *
	 * @param slots 槽位数量
	 */
	public void setAllUnitTypeSlot(final int slots) {
		System.err.println(
				"忽略设置所有单位类型槽位为: " + slots + "（市场功能尚未实现）");
	}

	/**
	 * 设置是否暂停时间日夜循环。
	 *
	 * @param flag 是否暂停
	 */
	public void setTimeOfDaySuspended(final boolean flag) {
		this.timeOfDaySuspended = flag;
	}

	/**
	 * 判断当前是否为白天。
	 *
	 * @return 如果是白天返回true，否则返回false
	 */
	public boolean isDay() {
		return this.daytime;
	}

	/**
	 * 判断当前是否为夜晚。
	 *
	 * @return 如果是夜晚返回true，否则返回false
	 */
	public boolean isNight() {
		return !this.daytime;
	}

	/**
	 * 设置地图上的枯萎区域。
	 *
	 * @param x      枯萎区域的中心点x坐标
	 * @param y      枯萎区域的中心点y坐标
	 * @param radius 枯萎区域的半径
	 * @param blighted 是否枯萎
	 */
	public void setBlight(final float x, final float y, final float radius, final boolean blighted) {
		this.simulationRenderController.setBlight(x, y, radius, blighted);
	}

	/**
	 * 更新单位的类型。
	 *
	 * @param unit   需要更新类型的单位
	 * @param typeId 新的单位类型ID
	 */
	public void unitUpdatedType(final CUnit unit, final War3ID typeId) {
		this.simulationRenderController.unitUpdatedType(unit, typeId);
	}

	/**
	 * 为新创建的单位设置一些初始属性和能力
	 * 设置能力
	 * 设置科技研究效果
	 * 设建筑物寻路图
	 *
	 * @param unit 新创建的单位
	 */
	private void setupCreatedUnit(final CUnit unit) {
		// 获取单位的类型实例
		final CUnitType unitTypeInstance = unit.getUnitType();
		// 获取单位的初始法力值
		final int manaInitial = unitTypeInstance.getManaInitial();
		// 获取单位的移动速度
		final int speed = unitTypeInstance.getSpeed();
		// 为单位添加默认能力，并设置法力值和速度
		this.unitData.addDefaultAbilitiesToUnit(this, this.handleIdAllocator, unitTypeInstance, true, manaInitial,
				speed, unit);
		// 应用玩家升级到单位
		this.unitData.applyPlayerUpgradesToUnit(this, unit.getPlayerIndex(), unitTypeInstance, unit);
		// 获取单位类型实例的建筑路径像素图
		final BufferedImage buildingPathingPixelMap = unitTypeInstance.getBuildingPathingPixelMap();
		// 如果建筑路径像素图不为空
		if (buildingPathingPixelMap != null) {
			// 为单位重新生成路径实例
			unit.regeneratePathingInstance(this, buildingPathingPixelMap);
		}
	}

	/**
	 * 改变单位的颜色，根据玩家索引。
	 *
	 * @param unit 单位对象
	 * @param playerIndex 玩家索引
	 */
	public void changeUnitColor(final CUnit unit, final int playerIndex) {
		this.simulationRenderController.changeUnitColor(unit, playerIndex);
	}

	/**
	 * 改变单位的顶点颜色。
	 *
	 * @param unit 单位对象
	 * @param color 颜色对象
	 */
	public void changeUnitVertexColor(final CUnit unit, final Color color) {
		this.simulationRenderController.changeUnitVertexColor(unit, color);
	}

	/**
	 * 改变单位的顶点颜色，使用RGB值。
	 *
	 * @param unit 单位对象
	 * @param r 红色分量
	 * @param g 绿色分量
	 * @param b 蓝色分量
	 */
	public void changeUnitVertexColor(final CUnit unit, final float r, final float g, final float b) {
		this.simulationRenderController.changeUnitVertexColor(unit, r, g, b);
	}

	/**
	 * 改变单位的顶点颜色，使用RGBA值。
	 *
	 * @param unit 单位对象
	 * @param r 红色分量
	 * @param g 绿色分量
	 * @param b 蓝色分量
	 * @param a 透明度分量
	 */
	public void changeUnitVertexColor(final CUnit unit, final float r, final float g, final float b, final float a) {
		this.simulationRenderController.changeUnitVertexColor(unit, r, g, b, a);
	}

	/**
	 * 设置全局作用域。
	 *
	 * @param globalScope 全局作用域对象
	 */
	public void setGlobalScope(final GlobalScope globalScope) {
		this.globalScope = globalScope;
	}

	/**
	 * 获取全局作用域。
	 *
	 * @return 全局作用域对象
	 */
	public GlobalScope getGlobalScope() {
		return this.globalScope;
	}

	/**
	 * 获取地形高度。
	 *
	 * @param x X坐标
	 * @param y Y坐标
	 * @return 地形高度
	 */
	public int getTerrainHeight(final float x, final float y) {
		return this.simulationRenderController.getTerrainHeight(x, y);
	}

	/**
	 * 判断地形是否为崎岖的。
	 *
	 * @param x X坐标
	 * @param y Y坐标
	 * @return 如果地形崎岖返回true，否则返回false
	 */
	public boolean isTerrainRomp(final float x, final float y) {
		return this.simulationRenderController.isTerrainRomp(x, y);
	}

	/**
	 * 判断地形是否为水域。
	 *
	 * @param x X坐标
	 * @param y Y坐标
	 * @return 如果地形为水域返回true，否则返回false
	 */
	public boolean isTerrainWater(final float x, final float y) {
		return this.simulationRenderController.isTerrainWater(x, y);
	}

	/**
	 * 获取地图版本。
	 *
	 * @return 地图版本号
	 */
	public int getMapVersion() {
		return this.mapVersion;
	}

	/**
	 * 判断地图是否为《混乱统治》版本。
	 *
	 * @return 如果是《混乱统治》版本返回true，否则返回false
	 */
	public boolean isMapReignOfChaos() {
		return this.mapVersion <= 24;
	}


}
