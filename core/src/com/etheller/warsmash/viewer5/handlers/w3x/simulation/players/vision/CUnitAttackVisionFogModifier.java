package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class CUnitAttackVisionFogModifier extends CFogModifier {
	// 定义一个名为CUnitAttackVisionFogModifier的类，继承自CFogModifier类
	private CUnit unit; // 定义一个私有的CUnit类型的成员变量unit
	private int playerIndex; // 定义一个私有的整型成员变量playerIndex

	/**
	 * 构造函数，用于创建CUnitAttackVisionFogModifier对象
	 *
	 * @param unit        CUnit类型的参数，表示要修改视野雾效果的单元
	 * @param playerIndex 整型参数，表示玩家的索引
	 */
	public CUnitAttackVisionFogModifier(final CUnit unit, final int playerIndex) {
		this.unit = unit; // 将传入的unit参数赋值给成员变量unit
		this.playerIndex = playerIndex; // 将传入的playerIndex参数赋值给成员变量playerIndex
	}

	/**
	 * 获取玩家的索引
	 *
	 * @return 返回玩家的索引
	 */
	public int getPlayerIndex() {
		return playerIndex; // 返回成员变量playerIndex的值
	}

	/**
	 * 设置玩家的索引
	 *
	 * @param playerIndex 整型参数，表示要设置的玩家索引
	 */
	public void setPlayerIndex(int playerIndex) {
		this.playerIndex = playerIndex; // 将传入的playerIndex参数赋值给成员变量playerIndex
	}


	@Override
	public void update(final CSimulation game, final CPlayer player, final PathingGrid pathingGrid,
			final CPlayerFogOfWar fogOfWar) {
		if (!this.unit.isDead() && !this.unit.isHidden() && ATTACKING_UNIT_VISION_RADIUS > 0) {
			// 判断单位是否具有飞行能力
			final boolean flying = this.unit.getUnitType().getMovementType() == MovementType.FLY;
			// 获取单位的X坐标
			final float myX = this.unit.getX();
			// 获取单位的Y坐标
			final float myY = this.unit.getY();
			// 如果单位能飞，则Z坐标设为最大整数值，否则设为地形高度
			final int myZ = flying ? Integer.MAX_VALUE : game.getTerrainHeight(myX, myY);
			// 设置战争迷雾状态，将单位所在位置的迷雾清除
			fogOfWar.setState(game.getPathingGrid().getFogOfWarIndexX(myX),
					game.getPathingGrid().getFogOfWarIndexY(myY), (byte) 0);

			// 计算单位所在位置的迷雾索引X坐标
			int myXi = game.getPathingGrid().getFogOfWarIndexX(myX);
			// 计算单位所在位置的迷雾索引Y坐标
			int myYi = game.getPathingGrid().getFogOfWarIndexY(myY);
			// 计算单位攻击视野半径范围内的迷雾索引X坐标最大值
			int maxXi = game.getPathingGrid().getFogOfWarIndexX(myX + ATTACKING_UNIT_VISION_RADIUS);
			// 计算单位攻击视野半径范围内的迷雾索引Y坐标最大值
			int maxYi = game.getPathingGrid().getFogOfWarIndexY(myY + ATTACKING_UNIT_VISION_RADIUS);

			for (int a = 1; a <= Math.max(maxYi - myYi, maxXi - myXi); a++) {
				int distance = a * a;

				if (distance <= ATTACKING_UNIT_VISION_RADIUS_SQ
						&& (flying || !pathingGrid.isBlockVision(myX, myY - (a - 1) * CPlayerFogOfWar.GRID_STEP))
						&& fogOfWar.getState(myXi, myYi - a + 1) == 0
						&& (flying || game.isTerrainWater(myX, myY - a * CPlayerFogOfWar.GRID_STEP)
								|| myZ > game.getTerrainHeight(myX, myY - a * CPlayerFogOfWar.GRID_STEP)
								|| (!game.isTerrainRomp(myX, myY - a * CPlayerFogOfWar.GRID_STEP)
										&& myZ == game.getTerrainHeight(myX, myY - a * CPlayerFogOfWar.GRID_STEP)))) {
					fogOfWar.setState(myXi, myYi - a, (byte) 0);
				}
				if (distance <= ATTACKING_UNIT_VISION_RADIUS_SQ
						&& (flying || !pathingGrid.isBlockVision(myX, myY + (a - 1) * CPlayerFogOfWar.GRID_STEP))
						&& fogOfWar.getState(myXi, myYi + a - 1) == 0
						&& (flying || game.isTerrainWater(myX, myY + a * CPlayerFogOfWar.GRID_STEP)
								|| myZ > game.getTerrainHeight(myX, myY + a * CPlayerFogOfWar.GRID_STEP)
								|| (!game.isTerrainRomp(myX, myY + a * CPlayerFogOfWar.GRID_STEP)
										&& myZ == game.getTerrainHeight(myX, myY + a * CPlayerFogOfWar.GRID_STEP)))) {
					fogOfWar.setState(myXi, myYi + a, (byte) 0);
				}
				if (distance <= ATTACKING_UNIT_VISION_RADIUS_SQ
						&& (flying || !pathingGrid.isBlockVision(myX - (a - 1) * CPlayerFogOfWar.GRID_STEP, myY))
						&& fogOfWar.getState(myXi - a + 1, myYi) == 0
						&& (flying || game.isTerrainWater(myX - a * CPlayerFogOfWar.GRID_STEP, myY)
								|| myZ > game.getTerrainHeight(myX - a * CPlayerFogOfWar.GRID_STEP, myY)
								|| (!game.isTerrainRomp(myX - a * CPlayerFogOfWar.GRID_STEP, myY)
										&& myZ == game.getTerrainHeight(myX - a * CPlayerFogOfWar.GRID_STEP, myY)))) {
					fogOfWar.setState(myXi - a, myYi, (byte) 0);
				}
				if (distance <= ATTACKING_UNIT_VISION_RADIUS_SQ
						&& (flying || !pathingGrid.isBlockVision(myX + (a - 1) * CPlayerFogOfWar.GRID_STEP, myY))
						&& fogOfWar.getState(myXi + a - 1, myYi) == 0
						&& (flying || game.isTerrainWater(myX + a * CPlayerFogOfWar.GRID_STEP, myY)
								|| myZ > game.getTerrainHeight(myX + a * CPlayerFogOfWar.GRID_STEP, myY)
								|| (!game.isTerrainRomp(myX + a * CPlayerFogOfWar.GRID_STEP, myY)
										&& myZ == game.getTerrainHeight(myX + a * CPlayerFogOfWar.GRID_STEP, myY)))) {
					fogOfWar.setState(myXi + a, myYi, (byte) 0);
				}
			}

			for (int y = 1; y <= maxYi - myYi; y++) {
				for (int x = 1; x <= maxXi - myXi; x++) {
					float distance = x * x + y * y;
					if (distance <= ATTACKING_UNIT_VISION_RADIUS_SQ) {
						int xf = x * CPlayerFogOfWar.GRID_STEP;
						int yf = y * CPlayerFogOfWar.GRID_STEP;

						if ((flying || game.isTerrainWater(myX - xf, myY - yf)
								|| myZ > game.getTerrainHeight(myX - xf, myY - yf)
								|| (!game.isTerrainRomp(myX - xf, myY - yf)
										&& myZ == game.getTerrainHeight(myX - xf, myY - yf)))
								&& (flying || !pathingGrid.isBlockVision(myX - xf + CPlayerFogOfWar.GRID_STEP,
										myY - yf + CPlayerFogOfWar.GRID_STEP))
								&& fogOfWar.getState(myXi - x + 1, myYi - y + 1) == 0
								&& (x == y
										|| (x > y && fogOfWar.getState(myXi - x + 1, myYi - y) == 0
												&& (flying || !pathingGrid
														.isBlockVision(myX - xf + CPlayerFogOfWar.GRID_STEP, myY - yf)))
										|| (x < y && fogOfWar.getState(myXi - x, myYi - y + 1) == 0
												&& (flying || !pathingGrid.isBlockVision(myX - xf,
														myY - yf + CPlayerFogOfWar.GRID_STEP))))) {
							fogOfWar.setState(myXi - x, myYi - y, (byte) 0);
						}
						if ((flying || game.isTerrainWater(myX - xf, myY + yf)
								|| myZ > game.getTerrainHeight(myX - xf, myY + yf)
								|| (!game.isTerrainRomp(myX - xf, myY + yf)
										&& myZ == game.getTerrainHeight(myX - xf, myY + yf)))
								&& (flying || !pathingGrid.isBlockVision(myX - xf + CPlayerFogOfWar.GRID_STEP,
										myY + yf - CPlayerFogOfWar.GRID_STEP))
								&& fogOfWar.getState(myXi - x + 1, myYi + y - 1) == 0
								&& (x == y
										|| (x > y && fogOfWar.getState(myXi - x + 1, myYi + y) == 0
												&& (flying || !pathingGrid
														.isBlockVision(myX - xf + CPlayerFogOfWar.GRID_STEP, myY + yf)))
										|| (x < y && fogOfWar.getState(myXi - x, myYi + y - 1) == 0
												&& (flying || !pathingGrid.isBlockVision(myX - xf,
														myY + yf - CPlayerFogOfWar.GRID_STEP))))) {
							fogOfWar.setState(myXi - x, myYi + y, (byte) 0);
						}
						if ((flying || game.isTerrainWater(myX + xf, myY - yf)
								|| myZ > game.getTerrainHeight(myX + xf, myY - yf)
								|| (!game.isTerrainRomp(myX + xf, myY - yf)
										&& myZ == game.getTerrainHeight(myX + xf, myY - yf)))
								&& (flying || !pathingGrid.isBlockVision(myX + xf - CPlayerFogOfWar.GRID_STEP,
										myY - yf + CPlayerFogOfWar.GRID_STEP))
								&& fogOfWar.getState(myXi + x - 1, myYi - y + 1) == 0
								&& (x == y
										|| (x > y && fogOfWar.getState(myXi + x - 1, myYi - y) == 0
												&& (flying || !pathingGrid
														.isBlockVision(myX + xf - CPlayerFogOfWar.GRID_STEP, myY - yf)))
										|| (x < y && fogOfWar.getState(myXi + x, myYi - y + 1) == 0
												&& (flying || !pathingGrid.isBlockVision(myX + xf,
														myY - yf + CPlayerFogOfWar.GRID_STEP))))) {
							fogOfWar.setState(myXi + x, myYi - y, (byte) 0);
						}
						if ((flying || game.isTerrainWater(myX + xf, myY + yf)
								|| myZ > game.getTerrainHeight(myX + xf, myY + yf)
								|| (!game.isTerrainRomp(myX + xf, myY + yf)
										&& myZ == game.getTerrainHeight(myX + xf, myY + yf)))
								&& (flying || !pathingGrid.isBlockVision(myX + xf - CPlayerFogOfWar.GRID_STEP,
										myY + yf - CPlayerFogOfWar.GRID_STEP))
								&& fogOfWar.getState(myXi + x - 1, myYi + y - 1) == 0
								&& (x == y
										|| (x > y && fogOfWar.getState(myXi + x - 1, myYi + y) == 0
												&& (flying || !pathingGrid
														.isBlockVision(myX + xf - CPlayerFogOfWar.GRID_STEP, myY + yf)))
										|| (x < y && fogOfWar.getState(myXi + x, myYi + y - 1) == 0
												&& (flying || !pathingGrid.isBlockVision(myX + xf,
														myY + yf - CPlayerFogOfWar.GRID_STEP))))) {
							fogOfWar.setState(myXi + x, myYi + y, (byte) 0);
						}
					}
				}
			}
		}
	}
}
