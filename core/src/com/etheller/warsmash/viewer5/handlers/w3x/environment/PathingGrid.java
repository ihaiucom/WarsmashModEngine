package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.parsers.w3x.wpm.War3MapWpm;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWorldCollision;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CPlayerFogOfWar;

public class PathingGrid {
	// 定义一个空白路径图像，用于在路径规划中表示不可通行的区域
	// 1x1像素的大小，ARGB类型表示包含透明度信息
	public static final BufferedImage BLANK_PATHING = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

	// 创建一个映射，用于将字符串表示的移动类型转换为MovementType枚举类型
	// 这样做可以方便地根据字符串查找对应的移动类型，提高代码的可读性和可维护性
	private static final Map<String, MovementType> movetpToMovementType = new HashMap<>();

	static {
		for (final MovementType movementType : MovementType.values()) {
			if (!movementType.typeKey.isEmpty()) {
				movetpToMovementType.put(movementType.typeKey, movementType);
			}
		}
	}

	// 路径网格数组，存储路径信息
	private final short[] pathingGrid;

	// 动态路径覆盖数组，用于建筑物和树木等动态障碍物
	private final short[] dynamicPathingOverlay;

	// 路径网格大小数组，存储每个维度上的网格大小
	private final int[] pathingGridSizes;

	// 中心偏移数组，用于调整路径网格的中心位置
	private final float[] centerOffset;

	// 动态路径地图实例列表，存储所有动态路径地图的实例
	private final List<RemovablePathingMapInstance> dynamicPathingInstances;

	public PathingGrid(final War3MapWpm terrainPathing, final float[] centerOffset) {
		// 设置中心偏移量
		this.centerOffset = centerOffset;
		// 获取地形路径规划的网格
		this.pathingGrid = terrainPathing.getPathing();
		// 获取地形路径规划的大小
		this.pathingGridSizes = terrainPathing.getSize();
		// 初始化动态路径覆盖数组，长度与路径规划网格相同
		this.dynamicPathingOverlay = new short[this.pathingGrid.length];
		// 初始化动态路径规划实例列表
		this.dynamicPathingInstances = new ArrayList<>();

	}

	// this blit function is basically copied from HiveWE, maybe remember to mention
	// that in credits as well:
	// https://github.com/stijnherfst/HiveWE/blob/master/Base/PathingMap.cpp
	private void blitPathingOverlayTexture(final float positionX, final float positionY, final int rotationInput,
										   final BufferedImage pathingTextureTga, boolean blocksVision) {
		// 计算旋转后的角度，确保角度在0-359度之间
		final int rotation = (rotationInput + 450) % 360;
		// 根据旋转角度确定纹理的宽和高
		final int divW = ((rotation % 180) != 0) ? pathingTextureTga.getHeight() : pathingTextureTga.getWidth();
		final int divH = ((rotation % 180) != 0) ? pathingTextureTga.getWidth() : pathingTextureTga.getHeight();
		// 遍历纹理图像的每个像素
		for (int j = 0; j < pathingTextureTga.getHeight(); j++) {
			for (int i = 0; i < pathingTextureTga.getWidth(); i++) {
				int x = i;
				int y = j;

				// 根据旋转角度调整像素坐标
				switch (rotation) {
					case 90:
						x = pathingTextureTga.getHeight() - 1 - j;
						y = i;
						break;
					case 180:
						x = pathingTextureTga.getWidth() - 1 - i;
						y = pathingTextureTga.getHeight() - 1 - j;
						break;
					case 270:
						x = j;
						y = pathingTextureTga.getWidth() - 1 - i;
						break;
				}
				// 计算像素在网格中的位置
				final int xx = (getCellX(positionX) + x) - (divW / 2);
				final int yy = (getCellY(positionY) + y) - (divH / 2);

				// 检查计算出的位置是否在网格范围内
				if ((xx < 0) || (xx > (this.pathingGridSizes[0] - 1)) || (yy < 0)
						|| (yy > (this.pathingGridSizes[1] - 1))) {
					continue;
				}

				// 获取像素的颜色值，并根据颜色值设置路径标志
				final int rgb = pathingTextureTga.getRGB(i, pathingTextureTga.getHeight() - 1 - j);
				byte data = 0;
				if ((rgb & 0xFF) > 127) { // 蓝
					data |= PathingFlags.UNBUILDABLE; // 不可建造的区域
				}
				if (((rgb & 0xFF00) >>> 8) > 127) { // 绿
					data |= PathingFlags.UNFLYABLE; // 不可飞行的区域
				}
				if (((rgb & 0xFF0000) >>> 16) > 127) { // 红
					data |= PathingFlags.UNWALKABLE | PathingFlags.UNSWIMABLE | (blocksVision ? PathingFlags.BLOCKVISION : 0); // 不可行走、不可飞行、阻挡视线的区域
				}
				// 更新动态路径覆盖数组
				this.dynamicPathingOverlay[(yy * this.pathingGridSizes[0]) + xx] |= data;
			}
		}
	}

	/**
	 * 检查给定位置和旋转角度下的路径纹理是否允许特定类型的单位通过。
	 *
	 * @param positionX                        单位位置的X坐标
	 * @param positionY                        单位位置的Y坐标
	 * @param rotationInput                    旋转角度输入
	 * @param pathingTextureTga                路径纹理图像
	 * @param preventPathingTypes              需要阻止通行的路径类型集合
	 * @param requirePathingTypes              需要允许通行的路径类型集合
	 * @param cWorldCollision                  世界碰撞检测对象
	 * @param unitToExcludeFromCollisionChecks 需要从碰撞检测中排除的单位
	 * @return 如果路径纹理允许特定类型的单位通过，则返回true，否则返回false
	 */
	public boolean checkPathingTexture(final float positionX, final float positionY, final int rotationInput,
									   BufferedImage pathingTextureTga, final EnumSet<CBuildingPathingType> preventPathingTypes,
									   final EnumSet<CBuildingPathingType> requirePathingTypes, final CWorldCollision cWorldCollision,
									   final CUnit unitToExcludeFromCollisionChecks) {
		// 如果路径纹理为空，则使用空白纹理
		if (pathingTextureTga == null) {
			pathingTextureTga = BLANK_PATHING;
		}
		// 计算实际的旋转角度
		final int rotation = (rotationInput + 450) % 360;
		// 根据旋转角度确定纹理的宽度和高度
		final int divW = ((rotation % 180) != 0) ? pathingTextureTga.getHeight() : pathingTextureTga.getWidth();
		final int divH = ((rotation % 180) != 0) ? pathingTextureTga.getWidth() : pathingTextureTga.getHeight();
		// 初始化区域内的路径类型标志
		short anyPathingTypesInRegion = 0;
		short pathingTypesFillingRegion = (short) 0xFFFF;
		// 遍历路径纹理的每个像素
		for (int j = 0; j < pathingTextureTga.getHeight(); j++) {
			for (int i = 0; i < pathingTextureTga.getWidth(); i++) {
				int x = i;
				int y = j;
				// 根据旋转角度调整坐标
				switch (rotation) {
					case 90:
						x = pathingTextureTga.getHeight() - 1 - j;
						y = i;
						break;
					case 180:
						x = pathingTextureTga.getWidth() - 1 - i;
						y = pathingTextureTga.getHeight() - 1 - j;
						break;
					case 270:
						x = j;
						y = pathingTextureTga.getWidth() - 1 - i;
						break;
				}
				// 计算像素在世界坐标系中的位置
				final int xx = (getCellX(positionX) + x) - (divW / 2);
				final int yy = (getCellY(positionY) + y) - (divH / 2);
				// 检查坐标是否在路径网格内
				if ((xx < 0) || (xx > (this.pathingGridSizes[0] - 1)) || (yy < 0)
						|| (yy > (this.pathingGridSizes[1] - 1))) {
					continue;
				}
				// 获取该位置的路径类型标志
				final short cellPathing = getCellPathing(xx, yy);
				// 更新区域内的路径类型标志
				anyPathingTypesInRegion |= cellPathing;
				pathingTypesFillingRegion &= cellPathing;
			}
		}
		// 计算路径纹理在世界坐标系中的矩形区域
		final float width = pathingTextureTga.getWidth() * 32f;
		final float height = pathingTextureTga.getHeight() * 32f;
		final float offsetX = ((pathingTextureTga.getWidth() % 2) == 1) ? 16f : 0f;
		final float offsetY = ((pathingTextureTga.getHeight() % 2) == 1) ? 16f : 0f;
		final Rectangle pathingMapRectangle = new Rectangle((positionX - (width / 2)) + offsetX,
				(positionY - (height / 2)) + offsetY, width, height);
		// 检查路径纹理区域是否与其他物体发生碰撞
		if (cWorldCollision.intersectsAnythingOtherThan(pathingMapRectangle, unitToExcludeFromCollisionChecks,
				MovementType.AMPHIBIOUS)) {
			System.out.println("intersects amph unit");
			anyPathingTypesInRegion |= PathingFlags.UNBUILDABLE | PathingFlags.UNWALKABLE | PathingFlags.UNSWIMABLE;
		}
		if (cWorldCollision.intersectsAnythingOtherThan(pathingMapRectangle, unitToExcludeFromCollisionChecks,
				MovementType.FLOAT)) {
			System.out.println("intersects float unit");
			anyPathingTypesInRegion |= PathingFlags.UNSWIMABLE;
		}
		if (cWorldCollision.intersectsAnythingOtherThan(pathingMapRectangle, unitToExcludeFromCollisionChecks,
				MovementType.FLY)) {
			System.out.println("intersects fly unit");
			anyPathingTypesInRegion |= PathingFlags.UNFLYABLE;
		}
		if (cWorldCollision.intersectsAnythingOtherThan(pathingMapRectangle, unitToExcludeFromCollisionChecks,
				MovementType.FOOT)) {
			System.out.println("intersects foot unit");
			anyPathingTypesInRegion |= PathingFlags.UNBUILDABLE | PathingFlags.UNWALKABLE;
		}
		// 检查阻止通行的路径类型
		for (final CBuildingPathingType pathingType : preventPathingTypes) {
			if (PathingFlags.isPathingFlag(anyPathingTypesInRegion, pathingType)) {
				return false;
			}
		}
		// 检查需要允许通行的路径类型
		for (final CBuildingPathingType pathingType : requirePathingTypes) {
			if (!PathingFlags.isPathingFlag(pathingTypesFillingRegion, pathingType)) {
				return false;
			}
		}
		// 如果所有检查都通过，则返回true
		return true;
	}

	/**
	 * 将可移除的寻路覆盖纹理绘制到地图上。
	 *
	 * @param positionX         纹理左上角的X坐标
	 * @param positionY         纹理左上角的Y坐标
	 * @param rotationInput     旋转输入值
	 * @param pathingTextureTga 要绘制的寻路纹理图像
	 * @return 创建的RemovablePathingMapInstance实例
	 */
	public RemovablePathingMapInstance blitRemovablePathingOverlayTexture(final float positionX, final float positionY,
																		  final int rotationInput, final BufferedImage pathingTextureTga) {
		// 创建一个新的RemovablePathingMapInstance实例
		final RemovablePathingMapInstance removablePathingMapInstance = new RemovablePathingMapInstance(positionX,
				positionY, rotationInput, pathingTextureTga);
		// 调用blit方法将纹理绘制到地图上
		removablePathingMapInstance.blit();
		// 将新创建的实例添加到动态寻路实例列表中
		this.dynamicPathingInstances.add(removablePathingMapInstance);
		// 返回新创建的实例
		return removablePathingMapInstance;
	}

	/**
	 * 创建一个可移除的寻路覆盖纹理实例。
	 *
	 * @param positionX         纹理左上角的X坐标
	 * @param positionY         纹理左上角的Y坐标
	 * @param rotationInput     旋转输入值
	 * @param pathingTextureTga 要创建的寻路纹理图像
	 * @return 创建的RemovablePathingMapInstance实例
	 */
	public RemovablePathingMapInstance createRemovablePathingOverlayTexture(final float positionX,
																			final float positionY, final int rotationInput, final BufferedImage pathingTextureTga) {
		// 返回一个新的RemovablePathingMapInstance实例
		return new RemovablePathingMapInstance(positionX, positionY, rotationInput, pathingTextureTga);
	}


	/**
	 * 获取路径网格的宽度。
	 *
	 * @return 路径网格的宽度
	 */
	public int getWidth() {
		// 返回路径网格宽度数组的第一个元素，即宽度值
		return this.pathingGridSizes[0];
	}

	/**
	 * 获取路径网格的高度。
	 *
	 * @return 路径网格的高度
	 */
	public int getHeight() {
		// 返回路径网格宽度数组的第二个元素，即高度值
		return this.pathingGridSizes[1];
	}

	/**
	 * 检查指定的坐标是否在路径网格内。
	 *
	 * @param x 横坐标
	 * @param y 纵坐标
	 * @return 如果坐标在网格内返回true，否则返回false
	 */
	public boolean contains(final float x, final float y) {
		// 将浮点坐标转换为网格单元坐标
		final int cellX = getCellX(x);
		final int cellY = getCellY(y);
		// 检查转换后的单元坐标是否在网格范围内
		return (cellX >= 0) && (cellY >= 0) && (cellX < this.pathingGridSizes[0]) && (cellY < this.pathingGridSizes[1]);
	}

	/**
	 * 获取指定坐标处的路径信息。
	 *
	 * @param x 横坐标
	 * @param y 纵坐标
	 * @return 指定坐标处的路径信息
	 */
	public short getPathing(final float x, final float y) {
		// 获取坐标对应的网格单元坐标
		int cellX = getCellX(x);
		int cellY = getCellY(y);
		// 返回该网格单元的路径信息
		return getCellPathing(cellX, cellY);
	}


	/**
	 * 根据给定的x坐标计算对应的网格单元格的x坐标。
	 *
	 * @param x 用户坐标系中的x坐标
	 * @return 对应的网格单元格的x坐标
	 */
	public int getCellX(final float x) {
		// 计算用户坐标系中的x坐标相对于中心偏移量的位置，并除以每个单元格的宽度（32.0f）得到单元格坐标
		final float userCellSpaceX = (x - this.centerOffset[0]) / 32.0f;
		// 将计算得到的单元格坐标转换为整数类型
		final int cellX = (int) userCellSpaceX;
		// 返回单元格的x坐标
		return cellX;
	}

	/**
	 * 根据给定的y坐标计算对应的网格单元格的y坐标。
	 *
	 * @param y 用户坐标系中的y坐标
	 * @return 对应的网格单元格的y坐标
	 */
	public int getCellY(final float y) {
		// 计算用户坐标系中的y坐标相对于中心偏移量的位置，并除以每个单元格的宽度（32.0f）得到单元格坐标
		final float userCellSpaceY = (y - this.centerOffset[1]) / 32.0f;
		// 将计算得到的单元格坐标转换为整数类型
		final int cellY = (int) userCellSpaceY;
		// 返回单元格的y坐标
		return cellY;
	}

	/**
	 * 根据单元格的X坐标计算世界坐标中的X坐标。
	 *
	 * @param cellX 单元格的X坐标
	 * @return 世界坐标中的X坐标
	 */
	public float getWorldX(final int cellX) {
		return (cellX * 32f) + this.centerOffset[0] + 16f;
	}

	/**
	 * 根据单元格的Y坐标计算世界坐标中的Y坐标。
	 *
	 * @param cellY 单元格的Y坐标
	 * @return 世界坐标中的Y坐标
	 */
	public float getWorldY(final int cellY) {
		return (cellY * 32f) + this.centerOffset[1] + 16f;
	}

	/**
	 * 根据角落的X坐标计算世界坐标中的X坐标。
	 *
	 * @param cornerX 角落的X坐标
	 * @return 世界坐标中的X坐标
	 */
	public float getWorldXFromCorner(final int cornerX) {
		return (cornerX * 32f) + this.centerOffset[0];
	}

	/**
	 * 根据角落的Y坐标计算世界坐标中的Y坐标。
	 *
	 * @param cornerY 角落的Y坐标
	 * @return 世界坐标中的Y坐标
	 */
	public float getWorldYFromCorner(final int cornerY) {
		return (cornerY * 32f) + this.centerOffset[1];
	}


	/**
	 * 根据给定的x坐标计算并返回其所在的网格单元的左上角x坐标。
	 *
	 * @param x 输入的x坐标
	 * @return 网格单元的左上角x坐标
	 */
	public int getCornerX(final float x) {
		// 计算用户空间x坐标相对于中心偏移量的位置，并将其转换为网格单元空间
		final float userCellSpaceX = ((x + 16f) - this.centerOffset[0]) / 32.0f;
		// 将浮点数转换为整数，得到网格单元的x坐标
		final int cellX = (int) userCellSpaceX;
		return cellX;
	}

	/**
	 * 根据给定的y坐标计算并返回其所在的网格单元的左上角y坐标。
	 *
	 * @param y 输入的y坐标
	 * @return 网格单元的左上角y坐标
	 */
	public int getCornerY(final float y) {
		// 计算用户空间y坐标相对于中心偏移量的位置，并将其转换为网格单元空间
		final float userCellSpaceY = ((y + 16f) - this.centerOffset[1]) / 32.0f;
		// 将浮点数转换为整数，得到网格单元的y坐标
		final int cellY = (int) userCellSpaceY;
		return cellY;
	}

	/**
	 * 获取指定单元格的路径规划值，包括静态和动态覆盖层。
	 *
	 * @param cellX 单元格的X坐标
	 * @param cellY 单元格的Y坐标
	 * @return 返回合并后的路径规划值，如果索引超出范围则返回0
	 */
	public short getCellPathing(final int cellX, final int cellY) {
		final int index = (cellY * this.pathingGridSizes[0]) + cellX; // 计算单元格在数组中的索引
		if (index >= this.pathingGrid.length) { // 检查索引是否超出数组范围
			return 0;
		}
		return (short) (this.pathingGrid[index] | this.dynamicPathingOverlay[index]); // 返回合并后的路径规划值
	}

	/**
	 * 获取指定单元格的永久路径规划值，不包括动态覆盖层。
	 *
	 * @param cellX 单元格的X坐标
	 * @param cellY 单元格的Y坐标
	 * @return 返回单元格的永久路径规划值，如果索引超出范围则返回0
	 */
	private short getCellPermanentPathing(final int cellX, final int cellY) {
		final int index = (cellY * this.pathingGridSizes[0]) + cellX; // 计算单元格在数组中的索引
		if (index >= this.pathingGrid.length) { // 检查索引是否超出数组范围
			return 0;
		}
		return (this.pathingGrid[index]); // 返回单元格的永久路径规划值
	}

	/**
	 * 设置指定单元格的路径规划值。
	 *
	 * @param cellX 单元格的X坐标
	 * @param cellY 单元格的Y坐标
	 * @param pathingValue 要设置的路径规划值
	 */
	public void setCellPathing(final int cellX, final int cellY, final short pathingValue) {
		final int index = (cellY * this.pathingGridSizes[0]) + cellX; // 计算单元格在数组中的索引
		if (index >= this.pathingGrid.length) { // 检查索引是否超出数组范围
			return;
		}
		this.pathingGrid[index] = pathingValue; // 设置单元格的路径规划值
	}

	/**
	 * 设置指定单元格的枯萎状态。
	 * 如果枯萎标志为真，则将单元格的路径设置标志位包括枯萎状态。
	 * 如果枯萎标志为假，则清除单元格的路径设置标志位中的枯萎状态。
	 *
	 * @param cellX    单元格的X坐标
	 * @param cellY    单元格的Y坐标
	 * @param blighted 枯萎状态标志
	 */
	public void setCellBlighted(final int cellX, final int cellY, final boolean blighted) {
		if (blighted) {
			// 设置单元格路径标志位，包括枯萎状态
			setCellPathing(cellX, cellY, (short) (getCellPermanentPathing(cellX, cellY) | PathingFlags.BLIGHTED));
		} else {
			// 清除单元格路径标志位中的枯萎状态
			setCellPathing(cellX, cellY,
					(short) (getCellPermanentPathing(cellX, cellY) & (short) ~PathingFlags.BLIGHTED));
		}
	}

	/**
	 * 根据给定的世界坐标设置枯萎状态。
	 * 首先将世界坐标转换为单元格坐标，然后调用setCellBlighted方法。
	 *
	 * @param x        世界坐标的X值
	 * @param y        世界坐标的Y值
	 * @param blighted 枯萎状态标志
	 */
	public void setBlighted(final float x, final float y, final boolean blighted) {
		// 将世界坐标转换为单元格坐标，并设置枯萎状态
		setCellBlighted(getCellX(x), getCellY(y), blighted);
	}

	/**
	 * 检查指定坐标是否可行走，根据给定的PathingType。
	 *
	 * @param x           坐标的x值
	 * @param y           坐标的y值
	 * @param pathingType 行走类型
	 * @return 如果坐标可行走则返回true，否则返回false
	 */
	public boolean isPathable(final float x, final float y, final PathingType pathingType) {
		return !PathingFlags.isPathingFlag(getPathing(x, y), pathingType.preventionFlag);
	}

	/**
	 * 检查指定坐标是否可行走，根据给定的MovementType。
	 *
	 * @param x           坐标的x值
	 * @param y           坐标的y值
	 * @param pathingType 行走类型
	 * @return 如果坐标可行走则返回true，否则返回false
	 */
	public boolean isPathable(final float x, final float y, final MovementType pathingType) {
		return pathingType.isPathable(getPathing(x, y));
	}

	/**
	 * 检查考虑碰撞大小的指定坐标是否可行走，根据给定的MovementType。
	 *
	 * @param unitX         单位的x坐标
	 * @param unitY         单位的y坐标
	 * @param pathingType   行走类型
	 * @param collisionSize 碰撞大小
	 * @return 如果坐标考虑碰撞大小后可行走则返回true，否则返回false
	 */
	public boolean isPathable(final float unitX, final float unitY, final MovementType pathingType,
							  final float collisionSize) {
		// 如果碰撞大小为0
		if (collisionSize == 0f) {
			// 如果不包含该单位坐标，则返回false
			if (!contains(unitX, unitY)) {
				return false;
			}
			// 返回该单位坐标路径是否可行
			return pathingType.isPathable(getPathing(unitX, unitY));
		}
		// 遍历单位坐标周围的区域
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				// 计算周围区域的坐标
				final float unitPathingX = unitX + (i * collisionSize);
				final float unitPathingY = unitY + (j * collisionSize);
				// 如果不包含该坐标或者该坐标路径不可行，则返回false
				if (!contains(unitPathingX, unitPathingY)
						|| !pathingType.isPathable(getPathing(unitPathingX, unitPathingY))) {
					return false;
				}
			}
		}
		// 如果所有检查都通过，则返回true
		return true;

	}

	/**
	 * 判断给定的坐标点是否在单位格子内。
	 *
	 * @param queryX        查询点的X坐标
	 * @param queryY        查询点的Y坐标
	 * @param unitX         单位格子的X坐标
	 * @param unitY         单位格子的Y坐标
	 * @param movementType  移动类型（此参数在本方法中未使用，但保留以兼容接口）
	 * @param collisionSize 碰撞大小，用于确定单位格子的范围
	 * @return 如果查询点在单位格子内返回true，否则返回false
	 */
	public boolean isUnitCell(final float queryX, final float queryY, final float unitX, final float unitY,
							  final MovementType movementType, final float collisionSize) {
		// 计算单位格子的最大X和Y坐标
		final float maxX = unitX + collisionSize;
		final float maxY = unitY + collisionSize;
		// 获取查询点所在的单元格X和Y坐标
		final int cellX = getCellX(queryX);
		final int cellY = getCellY(queryY);
		// 遍历以单位格子为中心的区域，步长为32（假设每个单元格的大小为32x32）
		for (float minX = unitX - collisionSize; minX < maxX; minX += 32f) {
			for (float minY = unitY - collisionSize; minY < maxY; minY += 32f) {
				// 获取当前遍历点所在的单元格X和Y坐标
				final int yy = getCellY(minY);
				final int xx = getCellX(minX);
				// 如果查询点和遍历点在同一个单元格内，返回true
				if ((yy == cellY) && (xx == cellX)) {
					return true;
				}
			}
		}
		// 如果遍历完所有相关区域都没有找到相同的单元格，返回false
		return false;
	}


	/**
	 * 检查指定单元格是否可通行。
	 *
	 * @param x             单元格的x坐标
	 * @param y             单元格的y坐标
	 * @param pathingType   移动类型
	 * @param collisionSize 碰撞大小
	 * @return 如果单元格可通行则返回true，否则返回false
	 */
	public boolean isCellPathable(final int x, final int y, final MovementType pathingType, final float collisionSize) {
		return isPathable(getWorldX(x), getWorldY(y), pathingType, collisionSize);
	}

	/**
	 * 检查指定单元格是否可通行。
	 *
	 * @param x           单元格的x坐标
	 * @param y           单元格的y坐标
	 * @param pathingType 移动类型
	 * @return 如果单元格可通行则返回true，否则返回false
	 */
	public boolean isCellPathable(final int x, final int y, final MovementType pathingType) {
		return pathingType.isPathable(getCellPathing(x, y));
	}

	/**
	 * 检查指定的路径标志是否设置。
	 *
	 * @param pathingValue 路径值
	 * @param pathingType  路径类型
	 * @return 如果路径标志未设置则返回true，否则返回false
	 */
	public static boolean isPathingFlag(final short pathingValue, final PathingType pathingType) {
		return !PathingFlags.isPathingFlag(pathingValue, pathingType.preventionFlag);
	}

	/**
	 * 根据字符串获取对应的移动类型。
	 *
	 * @param movetp 移动类型的字符串表示
	 * @return 对应的移动类型
	 */
	public static MovementType getMovementType(final String movetp) {
		return movetpToMovementType.get(movetp);
	}

	/**
	 * 获取迷雾战争中的x坐标索引。
	 *
	 * @param x 用户空间中的x坐标
	 * @return 单元格空间中的x坐标索引
	 */
	public int getFogOfWarIndexX(final float x) {
		final float userCellSpaceX = ((x + (16f * CPlayerFogOfWar.PATHING_RATIO)) - this.centerOffset[0]) / (32f * CPlayerFogOfWar.PATHING_RATIO);
		final int cellX = (int) userCellSpaceX;
		return cellX;
	}

	/**
	 * 获取迷雾战争中的y坐标索引。
	 *
	 * @param y 用户空间中的y坐标
	 * @return 单元格空间中的y坐标索引
	 */
	public int getFogOfWarIndexY(final float y) {
		final float userCellSpaceY = ((y + (16f * CPlayerFogOfWar.PATHING_RATIO)) - this.centerOffset[1]) / (32f * CPlayerFogOfWar.PATHING_RATIO);
		final int cellY = (int) userCellSpaceY;
		return cellY;
	}

	/**
	 * 检查指定单元格是否阻挡视线。
	 *
	 * @param cellX 单元格的x坐标
	 * @param cellY 单元格的y坐标
	 * @return 如果单元格阻挡视线则返回true，否则返回false
	 */
	public boolean isCellBlockVision(final int cellX, final int cellY) {
		final int index = (cellY * this.pathingGridSizes[0]) + cellX;
		if (index < 0 || index >= this.pathingGrid.length) {
			return false;
		}
		return PathingFlags.isPathingFlag(this.dynamicPathingOverlay[index], PathingFlags.BLOCKVISION);
	}

	/**
	 * 检查指定坐标是否阻挡视线。
	 *
	 * @param x 用户空间中的x坐标
	 * @param y 用户空间中的y坐标
	 * @return 如果坐标阻挡视线则返回true，否则返回false
	 */
	public boolean isBlockVision(final float x, final float y) {
		return isCellBlockVision(getCellX(x), getCellY(y));
	}

	// 定义路径标志类型
	public static final class PathingFlags {
		public static short UNWALKABLE = 0x2; // 不可行走的区域
		public static short UNFLYABLE = 0x4; // 不可飞行的区域
		public static short UNBUILDABLE = 0x8; // 不可建造的区域
		public static short BLOCKVISION = 0x10; // 阻挡视线的区域
		public static short BLIGHTED = 0x20; // 荒芜的区域
		public static short UNSWIMABLE = 0x40; // 不可游泳的区域（未确认此标志值是否准确）
		public static short BOUDNARY = 0xF0; // 边界区域


		public static boolean isPathingFlag(final short pathingValue, final int flag) {
			return (pathingValue & flag) != 0;
		}

		public static boolean isPathingFlag(final short pathingValue, final CBuildingPathingType pathingType) {
			switch (pathingType) {
			case BLIGHTED: // 被污染的区域，可能无法建造或行走
				return PathingFlags.isPathingFlag(pathingValue, PathingFlags.BLIGHTED); // 荒芜的区域
			case UNAMPH: // 不能在水上建造的区域
				return PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNWALKABLE) // 不可行走的区域
						&& PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNSWIMABLE);  // 不可游泳的区域（未确认此标志值是否准确）
			case UNBUILDABLE: // 无法建造的区域
				return PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNBUILDABLE); // 不可建造的区域
			case UNFLOAT: // 不能漂浮的区域
				return PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNSWIMABLE); // 不可游泳的区域（未确认此标志值是否准确）
			case UNFLYABLE: // 无法飞行的区域
				return PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNFLYABLE); // 不可飞行的区域
			case UNWALKABLE: // 无法行走的区域
				return PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNWALKABLE);  // 不可行走的区域
			case BLOCKVISION:  // 阻挡视线的区域
				return PathingFlags.isPathingFlag(pathingValue, PathingFlags.BLOCKVISION); // 阻挡视线的区域
			default:
				return false;
			}
		}

		private PathingFlags() {
		}
	}

	// 定义不同的移动类型
	public static enum MovementType {
		// 脚步
		FOOT("foot") {
			/**
			 * 判断该移动类型是否可以通过某个路径值
			 * @param pathingValue 路径值
			 * @return 如果可以通过则返回true，否则返回false
			 */
			@Override
			public boolean isPathable(final short pathingValue) {
				return !PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNWALKABLE);
			}
		},
		// 脚 不考虑碰撞
		FOOT_NO_COLLISION("") {
			@Override
			public boolean isPathable(final short pathingValue) {
				return !PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNWALKABLE);
			}
		},
		// 马
		HORSE("horse") {
			@Override
			public boolean isPathable(final short pathingValue) {
				return !PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNWALKABLE);
			}
		},
		// 飞行
		FLY("fly") {
			@Override
			public boolean isPathable(final short pathingValue) {
				return !PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNFLYABLE);
			}
		},
		// 盘旋，悬停
		HOVER("hover") {
			@Override
			public boolean isPathable(final short pathingValue) {
				return !PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNWALKABLE);
			}
		},
		// 浮动
		FLOAT("float") {
			@Override
			public boolean isPathable(final short pathingValue) {
				return !PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNSWIMABLE);
			}
		},
		// 水陆两用
		AMPHIBIOUS("amph") {
			@Override
			public boolean isPathable(final short pathingValue) {
				return !PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNWALKABLE)
						|| !PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNSWIMABLE);
			}
		},
		// 不可移动
		DISABLED("") {
			@Override
			public boolean isPathable(final short pathingValue) {
				return true;
			}
		};

		private final String typeKey;

		/**
		 * 构造函数
		 *
		 * @param typeKey 移动类型的键值
		 */
		private MovementType(final String typeKey) {
			this.typeKey = typeKey;
		}

		/**
		 * 判断该移动类型是否可以通过某个路径值
		 *
		 * @param pathingValue 路径值
		 * @return 如果可以通过则返回true，否则返回false
		 */
		public abstract boolean isPathable(short pathingValue);
	}


	// 定义一个枚举类型PathingType，表示不同类型的可通行性
	public static enum PathingType {
		// 定义枚举常量，每个常量代表一种可通行性类型，
		// 并关联一个对应的禁止标志（preventionFlag）
		WALKABLE(PathingFlags.UNWALKABLE), // 可行走的类型，关联不可行走的标志
		FLYABLE(PathingFlags.UNFLYABLE),   // 可飞行的类型，关联不可飞行的标志
		BUILDABLE(PathingFlags.UNBUILDABLE), // 可建造的类型，关联不可建造的标志
		SWIMMABLE(PathingFlags.UNSWIMABLE); // 可游泳的类型，关联不可游泳的标志

		// 私有成员变量，存储与每种可通行性类型关联的禁止标志
		private final int preventionFlag;

		// 构造函数，用于初始化枚举常量的preventionFlag值
		private PathingType(final int preventionFlag) {
			this.preventionFlag = preventionFlag;
		}
	}


	public final class RemovablePathingMapInstance {
		// 实例的位置X坐标
		private final float positionX;
		// 实例的位置Y坐标
		private final float positionY;
		// 实例的旋转输入
		private final int rotationInput;
		// 实例的路径纹理图像
		private final BufferedImage pathingTextureTga;
		// 实例是否阻挡视线，默认为false
		private boolean blocksVision = false;

		/**
		 * 构造一个新的可移除路径地图实例
		 *
		 * @param positionX         实例的位置X坐标
		 * @param positionY         实例的位置Y坐标
		 * @param rotationInput     实例的旋转输入
		 * @param pathingTextureTga 实例的路径纹理图像
		 */
		public RemovablePathingMapInstance(final float positionX, final float positionY, final int rotationInput,
										   final BufferedImage pathingTextureTga) {
			this.positionX = positionX;
			this.positionY = positionY;
			this.rotationInput = rotationInput;
			this.pathingTextureTga = pathingTextureTga;
		}

		/**
		 * 将路径覆盖纹理绘制到屏幕上
		 */
		private void blit() {
			blitPathingOverlayTexture(this.positionX, this.positionY, this.rotationInput, this.pathingTextureTga, this.blocksVision);
		}

		/**
		 * 移除当前实例，并更新显示
		 */
		public void remove() {
			PathingGrid.this.dynamicPathingInstances.remove(this);
			Arrays.fill(PathingGrid.this.dynamicPathingOverlay, (short) 0);
			for (final RemovablePathingMapInstance instance : PathingGrid.this.dynamicPathingInstances) {
				instance.blit();
			}
		}

		/**
		 * 添加当前实例到路径网格，并更新显示
		 */
		public void add() {
			PathingGrid.this.dynamicPathingInstances.add(this);
			blit();
		}

		/**
		 * 设置当前实例阻挡视线，并更新显示
		 */
		public void setBlocksVision() {
			this.blocksVision = true;
			blit();
		}
	}

}
