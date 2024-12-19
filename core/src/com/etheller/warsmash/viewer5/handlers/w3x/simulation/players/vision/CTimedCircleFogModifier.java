package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;
public class CTimedCircleFogModifier extends CFogModifier {
    // 定义状态变量，表示雾的状态
    private final byte state;
    // 是否启用雾效果
    private boolean enabled = true;
    // 雾中心的X坐标
    private float myX;
    // 雾中心的Y坐标
    private float myY;
    // 雾的半径
    private float radius;
    // 雾持续的时间
    private float duration;
    // 雾效果结束的回合数
    private int endTurnTick;

    /**
     * 构造函数，初始化雾效果
     * @param fogState 雾的状态
     * @param radius 雾的半径
     * @param x 雾中心的X坐标
     * @param y 雾中心的Y坐标
     * @param duration 雾持续的时间
     */
    public CTimedCircleFogModifier(final CFogState fogState, final float radius, final float x, final float y, final float duration) {
        // 根据雾的状态设置state的值
        switch (fogState) {
            case FOGGED:
                state = 127;
                break;
            case MASKED:
                state = -128;
                break;
            case VISIBLE:
            default:
                state = 0;
                break;
        }
        this.radius = radius;
        this.myX = x;
        this.myY = y;
        this.duration = duration;
    }

    /**
     * 设置雾效果是否启用
     * @param enabled 是否启用
     */
    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 当雾效果被添加到游戏中时调用
     * @param game 游戏实例
     * @param player 玩家实例
     */
    @Override
    public void onAdd(final CSimulation game, final CPlayer player) {
        // 计算雾效果结束的回合数
        this.endTurnTick = (int) Math.floor(game.getGameTurnTick() + (this.duration / WarsmashConstants.SIMULATION_STEP_TIME));
    }

    /**
     * 更新雾效果
     * @param game 游戏实例
     * @param player 玩家实例
     * @param pathingGrid 路径网格实例
     * @param fogOfWar 雾效果实例
     */
    @Override
    public void update(final CSimulation game, final CPlayer player, final PathingGrid pathingGrid, final CPlayerFogOfWar fogOfWar) {
        // 如果雾效果未启用或半径小于等于0，则直接返回
        if (!this.enabled || this.radius <= 0) {
            return;
        }
        // 如果当前回合数大于雾效果结束的回合数，则移除雾效果并返回
        if (game.getGameTurnTick() > endTurnTick) {
            player.removeFogModifer(game, this);
            return;
        }
        // 计算半径的平方
        final float radSq = this.radius * this.radius;
        // 设置雾效果中心点的状态
        fogOfWar.setState(pathingGrid.getFogOfWarIndexX(this.myX), pathingGrid.getFogOfWarIndexY(this.myY), state);

        // 遍历以雾中心为圆心，半径为边长的正方形区域内的所有点
        for (int y = 0; y <= (int) Math.floor(this.radius); y += 128) {
            for (int x = 0; x <= (int) Math.floor(this.radius); x += 128) {
                // 计算点到圆心的距离的平方
                float distance = x * x + y * y;
                // 如果距离小于等于半径的平方，则设置该点的雾状态为可见
                if (distance <= radSq) {
                    fogOfWar.setState(pathingGrid.getFogOfWarIndexX(myX - x),
                            pathingGrid.getFogOfWarIndexY(myY - y), (byte) 0);
                    fogOfWar.setState(pathingGrid.getFogOfWarIndexX(myX - x),
                            pathingGrid.getFogOfWarIndexY(myY + y), (byte) 0);
                    fogOfWar.setState(pathingGrid.getFogOfWarIndexX(myX + x),
                            pathingGrid.getFogOfWarIndexY(myY - y), (byte) 0);
                    fogOfWar.setState(pathingGrid.getFogOfWarIndexX(myX + x),
                            pathingGrid.getFogOfWarIndexY(myY + y), (byte) 0);
                }
            }
        }
    }
}
