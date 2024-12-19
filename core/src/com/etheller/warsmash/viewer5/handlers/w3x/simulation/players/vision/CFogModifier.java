package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CGameplayConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public abstract class CFogModifier {
    // 雾的更新时间，单位为秒
    public static float FOG_UPDATE_TIME = 1f;

    // 临死单位的视野半径
    public static float DYING_UNIT_VISION_RADIUS = 0;
    // 临死单位的视野半径平方，用于优化计算
    public static float DYING_UNIT_VISION_RADIUS_SQ = 0;
    // 临死单位的视野持续时间
    public static float DYING_UNIT_VISION_DURATION = 0;
    // 攻击单位的视野半径
    public static float ATTACKING_UNIT_VISION_RADIUS = 0;
    // 攻击单位的视野半径平方，用于优化计算
    public static float ATTACKING_UNIT_VISION_RADIUS_SQ = 0;

    /**
     * 设置游戏常量到雾修改器中
     *
     * @param constants 游戏常量对象
     */
    public static void setConstants(CGameplayConstants constants) {
		// 获取濒死单位视野半径
		DYING_UNIT_VISION_RADIUS = constants.getDyingRevealRadius();
		// 计算濒死单位视野半径的平方，用于优化距离计算
		DYING_UNIT_VISION_RADIUS_SQ = DYING_UNIT_VISION_RADIUS * DYING_UNIT_VISION_RADIUS / (CPlayerFogOfWar.GRID_STEP * CPlayerFogOfWar.GRID_STEP);
		// 获取濒死单位视野持续时间
		DYING_UNIT_VISION_DURATION = constants.getFogFlashTime();
		// 获取攻击单位视野半径
		ATTACKING_UNIT_VISION_RADIUS = constants.getFoggedAttackRevealRadius();
		// 计算攻击单位视野半径的平方，用于优化距离计算
		ATTACKING_UNIT_VISION_RADIUS_SQ = ATTACKING_UNIT_VISION_RADIUS * ATTACKING_UNIT_VISION_RADIUS / (CPlayerFogOfWar.GRID_STEP * CPlayerFogOfWar.GRID_STEP);

    }

    /**
     * 设置雾修改器的启用状态
     *
     * @param enabled 是否启用
     */
    public void setEnabled(final boolean enabled) {
    }

    /**
     * 当雾修改器被添加到游戏中时调用
     *
     * @param game  游戏模拟对象
     * @param player 玩家对象
     */
    public void onAdd(final CSimulation game, final CPlayer player) {
    }

    /**
     * 当雾修改器从游戏中移除时调用
     *
     * @param game  游戏模拟对象
     * @param player 玩家对象
     */
    public void onRemove(final CSimulation game, final CPlayer player) {
    }

    /**
     * 更新雾修改器的状态
     *
     * @param game       游戏模拟对象
     * @param player     玩家对象
     * @param pathingGrid 导航网格对象
     * @param fogOfWar   玩家的战争迷雾对象
     */
    public abstract void update(final CSimulation game, final CPlayer player, final PathingGrid pathingGrid, final CPlayerFogOfWar fogOfWar);
}
