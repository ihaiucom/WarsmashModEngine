package com.etheller.warsmash.viewer5.handlers.w3x.simulation.config;

import java.util.EnumMap;
import java.util.EnumSet;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.ai.AIDifficulty;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapControl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRacePreference;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRacePreferences;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CPlayerSlotState;
/**
 * CBasePlayer类实现了CPlayerJass接口，作为玩家的基类。
 */
public abstract class CBasePlayer implements CPlayerJass {
	private final int id; // 玩家ID
	private String name; // 玩家名称
	private int team; // 玩家所在队伍
	private int startLocationIndex; // 开始位置索引
	private int forcedStartLocationIndex = -1; // 强制开始位置索引
	private int color; // 玩家颜色
	private final CRacePreferences racePrefs; // 种族偏好
	private final EnumSet<CAllianceType>[] alliances; // 联盟类型
	private final EnumMap<CPlayerState, Integer>[] taxRates; // 税率
	private boolean onScoreScreen; // 是否在得分屏幕上
	private boolean raceSelectable; // 种族是否可选择
	private CMapControl mapControl = CMapControl.NONE; // 地图控制
	private CPlayerSlotState slotState = CPlayerSlotState.EMPTY; // 玩家插槽状态
	private AIDifficulty aiDifficulty = null; // AI难度

	/**
	 * 构造函数，复制其他CBasePlayer实例的属性。
	 *
	 * @param other 另一个CBasePlayer对象
	 */
	public CBasePlayer(final CBasePlayer other) {
		this.id = other.id;
		this.name = other.name;
		this.team = other.team;
		this.startLocationIndex = other.startLocationIndex;
		this.forcedStartLocationIndex = other.forcedStartLocationIndex;
		this.color = other.color;
		this.racePrefs = other.racePrefs;
		this.alliances = other.alliances;
		this.taxRates = other.taxRates;
		this.onScoreScreen = other.onScoreScreen;
		this.raceSelectable = other.raceSelectable;
		this.mapControl = other.mapControl;
		this.slotState = other.slotState;
	}

	/**
	 * 构造函数，通过ID初始化玩家。
	 *
	 * @param id 玩家ID
	 */
	public CBasePlayer(final int id) {
		this.id = id;
		this.name = "null";
		this.alliances = new EnumSet[WarsmashConstants.MAX_PLAYERS];
		this.taxRates = new EnumMap[WarsmashConstants.MAX_PLAYERS];
		this.racePrefs = new CRacePreferences();
		for (int i = 0; i < this.alliances.length; i++) {
			if (i == id) {
				// player is fully allied with self
				this.alliances[i] = EnumSet.allOf(CAllianceType.class);
			}
			else {
				this.alliances[i] = EnumSet.noneOf(CAllianceType.class);
			}
			this.taxRates[i] = new EnumMap<>(CPlayerState.class);
		}
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setOnScoreScreen(final boolean onScoreScreen) {
		this.onScoreScreen = onScoreScreen;
	}

	/**
	 * 检查玩家是否在得分屏幕上。
	 *
	 * @return true如果在得分屏幕上，否则为false
	 */
	public boolean isOnScoreScreen() {
		return this.onScoreScreen; // 返回玩家是否在得分屏幕上的状态
	}

	/**
	 * 设置比赛是否可选。
	 *
	 * @param raceSelectable 比赛是否可选的状态
	 */
	@Override
	public void setRaceSelectable(final boolean raceSelectable) {
		this.raceSelectable = raceSelectable; // 设置比赛是否可选的状态
	}

	/**
	 * 设置玩家所在的队伍。
	 *
	 * @param team 玩家所在的队伍编号
	 */
	@Override
	public void setTeam(final int team) {
		this.team = team; // 设置玩家所在的队伍编号
	}

	/**
	 * 获取玩家所在的队伍。
	 *
	 * @return 玩家所在的队伍编号
	 */
	@Override
	public int getTeam() {
		return this.team; // 返回玩家所在的队伍编号
	}

	/**
	 * 获取玩家的起始位置索引。
	 *
	 * @return 玩家的起始位置索引
	 */
	@Override
	public int getStartLocationIndex() {
		return this.startLocationIndex; // 返回玩家的起始位置索引
	}

	/**
	 * 设置玩家的起始位置索引。
	 *
	 * @param startLocationIndex 玩家的起始位置索引
	 */
	@Override
	public void setStartLocationIndex(final int startLocationIndex) {
		this.startLocationIndex = startLocationIndex; // 设置玩家的起始位置索引
	}

	/**
	 * 设置玩家的颜色。
	 *
	 * @param color 玩家的颜色编号
	 */
	@Override
	public void setColor(final int color) {
		this.color = color; // 设置玩家的颜色编号
	}

	/**
	 * 获取玩家的颜色。
	 *
	 * @return 玩家的颜色编号
	 */
	@Override
	public int getColor() {
		return this.color; // 返回玩家的颜色编号
	}

	/**
	 * 检查是否设置了特定的种族偏好。
	 *
	 * @param racePref 种族偏好
	 * @return true如果设置了该种族偏好，否则为false
	 */
	@Override
	public boolean isRacePrefSet(final CRacePreference racePref) {
		return this.racePrefs.contains(racePref); // 检查种族偏好集合中是否包含指定的种族偏好
	}

	/**
	 * 设置玩家的种族偏好。
	 *
	 * @param racePref 玩家的种族偏好
	 */
	@Override
	public void setRacePref(final CRacePreference racePref) {
		this.racePrefs.clear(); // 清除现有的种族偏好
		this.racePrefs.add(racePref); // 添加新的种族偏好
	}

	/**
	 * 设置与其他玩家之间的联盟关系。
	 *
	 * @param otherPlayerIndex 其他玩家的索引
	 * @param allianceType     联盟类型
	 * @param value            是否建立联盟关系
	 */
	@Override
	public void setAlliance(final int otherPlayerIndex, final CAllianceType allianceType, final boolean value) {
		final EnumSet<CAllianceType> alliancesWithOtherPlayer = this.alliances[otherPlayerIndex];
		if (value) {
			alliancesWithOtherPlayer.add(allianceType); // 添加联盟类型
		} else {
			alliancesWithOtherPlayer.remove(allianceType); // 移除联盟类型
		}
	}

	/**
	 * 检查与其他玩家之间是否存在特定类型的联盟关系。
	 *
	 * @param otherPlayerIndex 其他玩家的索引
	 * @param allianceType     联盟类型
	 * @return true如果存在该联盟关系，否则为false
	 */
	@Override
	public boolean hasAlliance(final int otherPlayerIndex, final CAllianceType allianceType) {
		final EnumSet<CAllianceType> alliancesWithOtherPlayer = this.alliances[otherPlayerIndex];
		return alliancesWithOtherPlayer.contains(allianceType); // 检查是否存在指定的联盟关系
	}

	/**
	 * 强制设置玩家的起始位置。
	 *
	 * @param startLocIndex 玩家的起始位置索引
	 */
	@Override
	public void forceStartLocation(final int startLocIndex) {
		this.forcedStartLocationIndex = startLocIndex; // 设置玩家的强制起始位置索引
	}

	/**
	 * 设置对其他玩家的税率。
	 *
	 * @param otherPlayerIndex 其他玩家的索引
	 * @param whichResource    资源类型
	 * @param rate             税率
	 */
	@Override
	public void setTaxRate(final int otherPlayerIndex, final CPlayerState whichResource, final int rate) {
		this.taxRates[otherPlayerIndex].put(whichResource, rate); // 设置税率
	}

	/**
	 * 设置地图控制器。
	 *
	 * @param mapControl 地图控制器
	 */
	@Override
	public void setController(final CMapControl mapControl) {
		this.mapControl = mapControl; // 设置地图控制器
	}

	/**
	 * 检查比赛是否可选。
	 *
	 * @return true如果比赛可选，否则为false
	 */
	@Override
	public boolean isRaceSelectable() {
		return this.raceSelectable; // 返回比赛是否可选的状态
	}

	/**
	 * 获取地图控制器。
	 *
	 * @return 地图控制器
	 */
	@Override
	public CMapControl getController() {
		return this.mapControl; // 返回地图控制器
	}

	/**
	 * 获取玩家的插槽状态。
	 *
	 * @return 玩家的插槽状态
	 */
	@Override
	public CPlayerSlotState getSlotState() {
		return this.slotState; // 返回玩家的插槽状态
	}

	/**
	 * 设置玩家的插槽状态。
	 *
	 * @param slotState 玩家的插槽状态
	 */
	@Override
	public void setSlotState(final CPlayerSlotState slotState) {
		this.slotState = slotState; // 设置玩家的插槽状态
	}

	/**
	 * 获取AI难度。
	 *
	 * @return AI难度
	 */
	@Override
	public AIDifficulty getAIDifficulty() {
		return this.aiDifficulty; // 返回AI难度
	}

	/**
	 * 设置AI难度。
	 *
	 * @param aiDifficulty AI难度
	 */
	@Override
	public void setAIDifficulty(final AIDifficulty aiDifficulty) {
		this.aiDifficulty = aiDifficulty; // 设置AI难度
	}

	/**
	 * 获取对其他玩家的税率。
	 *
	 * @param otherPlayerIndex 其他玩家的索引
	 * @param whichResource    资源类型
	 * @return 税率，如果未设置则返回0
	 */
	@Override
	public int getTaxRate(final int otherPlayerIndex, final CPlayerState whichResource) {
		final Integer taxRate = this.taxRates[otherPlayerIndex].get(whichResource);
		if (taxRate == null) {
			return 0; // 如果未设置税率，则返回0
		}
		return taxRate; // 返回税率
	}

}
