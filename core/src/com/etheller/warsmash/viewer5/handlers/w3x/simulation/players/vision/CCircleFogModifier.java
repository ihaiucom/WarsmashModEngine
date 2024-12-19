package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;

public class CCircleFogModifier extends CFogModifier {
	private final byte state; // 雾的状态
	private boolean enabled = true; // 是否启用雾效果
	private float myX; // 圆心的X坐标
	private float myY; // 圆心的Y坐标
	private float radius; // 雾的半径

	/**
	 * 构造函数，根据传入的雾状态、半径和圆心坐标初始化雾效果
	 *
	 * @param fogState 雾的状态
	 * @param radius   雾的半径
	 * @param x        圆心的X坐标
	 * @param y        圆心的Y坐标
	 */
	public CCircleFogModifier(final CFogState fogState, final float radius, final float x, final float y) {
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
	}

	/**
	 * 设置雾效果是否启用
	 *
	 * @param enabled 是否启用
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * 更新雾效果
	 *
	 * @param game        游戏实例
	 * @param player      玩家实例
	 * @param pathingGrid 路径网格实例
	 * @param fogOfWar    雾效果实例
	 */
	@Override
	public void update(final CSimulation game, final CPlayer player, final PathingGrid pathingGrid, final CPlayerFogOfWar fogOfWar) {
		if (!this.enabled || this.radius <= 0) {
			return; // 如果雾效果未启用或半径小于等于0，则不进行更新
		}
		final float radSq = this.radius * this.radius; // 计算半径的平方
		fogOfWar.setState(pathingGrid.getFogOfWarIndexX(this.myX), pathingGrid.getFogOfWarIndexY(this.myY), state); // 设置圆心位置的雾状态

		// 遍历圆内的格子，更新雾状态
		for (int y = 0; y <= (int) Math.floor(this.radius); y += 128) {
			for (int x = 0; x <= (int) Math.floor(this.radius); x += 128) {
				float distance = x * x + y * y; // 计算当前点到圆心的距离的平方
				if (distance <= radSq) { // 如果在圆内
					// 更新四个对称点的雾状态
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
