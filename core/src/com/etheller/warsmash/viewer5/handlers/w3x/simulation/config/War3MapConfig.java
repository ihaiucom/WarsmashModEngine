package com.etheller.warsmash.viewer5.handlers.w3x.simulation.config;

import java.util.EnumMap;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapControl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapFlag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapPlacement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CGameSpeed;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CGameType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CMapDensity;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CMapDifficulty;
// 定义一个War3MapConfig类，实现了CPlayerAPI接口
public class War3MapConfig implements CPlayerAPI {
    // 地图名称
    private String mapName;
    // 地图描述
    private String mapDescription;
    // 队伍数量
    private int teamCount;
    // 玩家数量
    private int playerCount;
    // 开始位置数组
    private final War3MapConfigStartLoc[] startLocations;
    // 玩家数组
    private final CBasePlayer[] players;
    // 游戏类型支持的映射表
    private final EnumMap<CGameType, Boolean> gameTypeToSupported = new EnumMap<>(CGameType.class);
    // 地图标志启用状态的映射表
    private final EnumMap<CMapFlag, Boolean> mapFlagToEnabled = new EnumMap<>(CMapFlag.class);
    // 地图布局
    private CMapPlacement placement;
    // 游戏速度
    private CGameSpeed gameSpeed;
    // 游戏难度
    private CMapDifficulty gameDifficulty;
    // 资源密度
    private CMapDensity resourceDensity;
    // 生物密度
    private CMapDensity creatureDensity;
    // 选中的游戏类型
    private CGameType gameTypeSelected;

    // 构造函数，初始化开始位置和玩家数组
    public War3MapConfig(final int maxPlayers) {
        this.startLocations = new War3MapConfigStartLoc[maxPlayers];
        this.players = new CBasePlayer[maxPlayers];
        for (int i = 0; i < maxPlayers; i++) {
            this.startLocations[i] = new War3MapConfigStartLoc();
            this.players[i] = new War3MapConfigPlayer(i);
            // 如果玩家索引大于等于最大玩家数减去4，则设置玩家控制为中立
            if (i >= (maxPlayers - 4)) {
                this.players[i].setController(CMapControl.NEUTRAL);
            }
        }
    }

    // 设置地图名称
    public void setMapName(final String mapName) {
        this.mapName = mapName;
    }

    // 获取地图名称
    public String getMapName() {
        return this.mapName;
    }

    // 设置地图描述
    public void setMapDescription(final String mapDescription) {
        this.mapDescription = mapDescription;
    }

    // 获取地图描述
    public String getMapDescription() {
        return this.mapDescription;
    }

    // 设置队伍数量
    public void setTeamCount(final int teamCount) {
        this.teamCount = teamCount;
    }

    // 设置玩家数量
    public void setPlayerCount(final int playerCount) {
        this.playerCount = playerCount;
    }

    // 定义开始位置
    public void defineStartLocation(final int whichStartLoc, final float x, final float y) {
        final War3MapConfigStartLoc startLoc = this.startLocations[whichStartLoc];
        startLoc.setX(x);
        startLoc.setY(y);
    }

    // 获取开始位置
    public War3MapConfigStartLoc getStartLoc(final int whichStartLoc) {
        return this.startLocations[whichStartLoc];
    }

    // 设置游戏类型支持状态
    public void setGameTypeSupported(final CGameType gameType, final boolean supported) {
        this.gameTypeToSupported.put(gameType, supported);
    }

    // 设置地图标志启用状态
    public void setMapFlag(final CMapFlag mapFlag, final boolean set) {
        this.mapFlagToEnabled.put(mapFlag, set);
    }

    // 设置地图布局
    public void setPlacement(final CMapPlacement placement) {
        this.placement = placement;
    }

    // 设置游戏速度
    public void setGameSpeed(final CGameSpeed gameSpeed) {
        this.gameSpeed = gameSpeed;
    }

    // 设置游戏难度
    public void setGameDifficulty(final CMapDifficulty gameDifficulty) {
        this.gameDifficulty = gameDifficulty;
    }

    // 设置资源密度
    public void setResourceDensity(final CMapDensity resourceDensity) {
        this.resourceDensity = resourceDensity;
    }

    // 设置生物密度
    public void setCreatureDensity(final CMapDensity creatureDensity) {
        this.creatureDensity = creatureDensity;
    }

    // 获取队伍数量
    public int getTeamCount() {
        return this.teamCount;
    }

    // 获取玩家数量
    public int getPlayerCount() {
        return this.playerCount;
    }

    // 检查游戏类型是否支持
    public boolean isGameTypeSupported(final CGameType gameType) {
        final Boolean supported = this.gameTypeToSupported.get(gameType);
        return (supported != null) && supported;
    }

    // 获取选中的游戏类型
    public CGameType getGameTypeSelected() {
        return this.gameTypeSelected;
    }

    // 检查地图标志是否启用
    public boolean isMapFlagSet(final CMapFlag mapFlag) {
        final Boolean flag = this.mapFlagToEnabled.get(mapFlag);
        return (flag != null) && flag;
    }

    // 获取地图布局
    public CMapPlacement getPlacement() {
        return this.placement;
    }

    // 获取游戏速度
    public CGameSpeed getGameSpeed() {
        return this.gameSpeed;
    }

    // 获取游戏难度
    public CMapDifficulty getGameDifficulty() {
        return this.gameDifficulty;
    }

    // 获取资源密度
    public CMapDensity getResourceDensity() {
        return this.resourceDensity;
    }

    // 获取生物密度
    public CMapDensity getCreatureDensity() {
        return this.creatureDensity;
    }

    // 获取开始位置的X坐标
    public float getStartLocationX(final int startLocIndex) {
        return this.startLocations[startLocIndex].getX();
    }

    // 获取开始位置的Y坐标
    public float getStartLocationY(final int startLocIndex) {
        return this.startLocations[startLocIndex].getY();
    }

    // 实现CPlayerAPI接口的方法，获取指定索引的玩家
    @Override
    public CBasePlayer getPlayer(final int index) {
        return this.players[index];
    }
}
