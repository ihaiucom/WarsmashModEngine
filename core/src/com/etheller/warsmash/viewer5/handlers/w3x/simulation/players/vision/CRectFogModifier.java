package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;

public class CRectFogModifier extends CFogModifier {
	// 定义雾的状态，使用byte类型存储
	private final byte state;
	// 定义影响区域，使用Rectangle对象
	private final Rectangle area;
	// 定义是否启用该雾效果
	private boolean enabled = true;

	/**
	 * 构造函数，根据传入的雾状态和区域初始化对象
	 *
	 * @param fogState 雾的状态
	 * @param area     影响区域
	 */
	public CRectFogModifier(final CFogState fogState, final Rectangle area) {
		// 根据不同的雾状态设置state的值
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
		this.area = area;
	}

	/**
	 * 设置是否启用雾效果
	 *
	 * @param enabled 是否启用
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * 更新雾效果，根据当前的游戏状态和玩家位置更新影响区域内的雾状态
	 *
	 * @param game        当前游戏状态
	 * @param player      玩家对象
	 * @param pathingGrid 导航网格
	 * @param fogOfWar    雾战争对象
	 */
	@Override
	public void update(final CSimulation game, final CPlayer player, final PathingGrid pathingGrid, final CPlayerFogOfWar fogOfWar) {
		// 如果雾效果未启用，则直接返回
		if (!this.enabled) {
			return;
		}
		// 计算影响区域的左上角和右下角在导航网格中的索引
		final int xMin = pathingGrid.getFogOfWarIndexX((float) Math.floor(this.area.x));
		final int yMin = pathingGrid.getFogOfWarIndexY((float) Math.floor(this.area.y));
		final int xMax = pathingGrid.getFogOfWarIndexX((float) Math.ceil(this.area.x + this.area.width));
		final int yMax = pathingGrid.getFogOfWarIndexY((float) Math.ceil(this.area.y + this.area.height));
		// 遍历影响区域内的每个格子，设置对应的雾状态
		for (int i = xMin; i <= xMax; i += 1) {
			for (int j = yMin; j <= yMax; j += 1) {
				fogOfWar.setState(i, j, state);
			}
		}
	}
}
