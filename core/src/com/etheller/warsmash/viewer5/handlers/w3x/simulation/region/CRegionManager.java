package com.etheller.warsmash.viewer5.handlers.w3x.simulation.region;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.Quadtree;
import com.etheller.warsmash.util.QuadtreeIntersector;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
/**
 * CRegionManager类负责管理区域的添加、移除和检查。
 */
public class CRegionManager {
	private static Rectangle tempRect = new Rectangle();
	private final Quadtree<CRegion> regionTree;
	private final RegionChecker regionChecker = new RegionChecker();
	private final List<CRegion>[][] cellRegions;
	private final PathingGrid pathingGrid;

	/**
	 * CRegionManager构造函数，初始化区域树和单元格区域列表。
	 *
	 * @param entireMapBounds 整个地图的边界
	 * @param pathingGrid      路径网格
	 */
	public CRegionManager(final Rectangle entireMapBounds, final PathingGrid pathingGrid) {
		this.regionTree = new Quadtree<>(entireMapBounds);
		this.cellRegions = new List[pathingGrid.getHeight()][pathingGrid.getWidth()];
		this.pathingGrid = pathingGrid;
	}

	/**
	 * 为指定区域添加矩形。
	 *
	 * @param region 指定的区域
	 * @param rect   矩形区域
	 */
	public void addRectForRegion(final CRegion region, final Rectangle rect) {
		this.regionTree.add(region, rect);
	}

	/**
	 * 从指定区域移除矩形。
	 *
	 * @param region 指定的区域
	 * @param rect   矩形区域
	 */
	public void removeRectForRegion(final CRegion region, final Rectangle rect) {
		this.regionTree.remove(region, rect);
	}

	/**
	 * 调用与给定区域相交的每个区域的枚举函数。
	 * 有时为了性能，算法设计为对同一区域调用枚举函数两次。
	 *
	 * @param area          给定区域
	 * @param enumFunction  区域枚举函数
	 */
	public void checkRegions(final Rectangle area, final CRegionEnumFunction enumFunction) {
		// 使用区域树与给定区域相交，并重置区域检查器
		this.regionTree.intersect(area, this.regionChecker.reset(enumFunction));

		// 如果区域检查器包含复杂区域
		if (this.regionChecker.includesComplex) {
			// 获取区域在网格中的最小和最大X、Y坐标
			final int minX = this.pathingGrid.getCellX(area.x);
			final int minY = this.pathingGrid.getCellY(area.y);
			final int maxX = this.pathingGrid.getCellX(area.x + area.width);
			final int maxY = this.pathingGrid.getCellY(area.y + area.height);

			// 遍历网格中的每个单元格
			for (int x = minX; x <= maxX; x++) {
				for (int y = minY; y <= maxY; y++) {
					// 获取当前点处的区域列表
					final List<CRegion> cellRegionsAtPoint = this.cellRegions[y][x];

					// 如果该点有区域
					if (cellRegionsAtPoint != null) {
						// 遍历区域列表
						for (final CRegion region : cellRegionsAtPoint) {
							// 如果枚举函数对当前区域返回true，则退出方法
							if (enumFunction.call(region)) {
								return;
							}
						}
					}
				}
			}
		}
	}


	/**
	 * RegionChecker类用于检查区域与四叉树的交集。
	 */
	private static final class RegionChecker implements QuadtreeIntersector<CRegion> {
		private CRegionEnumFunction delegate;
		private boolean includesComplex = false;

		/**
		 * 重置RegionChecker并设置回调函数。
		 *
		 * @param delegate 枚举函数
		 * @return 当前RegionChecker实例
		 */
		public RegionChecker reset(final CRegionEnumFunction delegate) {
			this.delegate = delegate;
			return this;
		}

		@Override
		/**
		 * 检查与指定区域相交的区域。
		 *
		 * @param intersectingObject 相交的区域
		 * @return 是否继续检查
		 */
		public boolean onIntersect(final CRegion intersectingObject) {
			if (intersectingObject.isComplexRegion()) {
				this.includesComplex = true;
				// handle this type of region differently
				return false;
			}
			return this.delegate.call(intersectingObject);
		}

	}

	/**
	 * 添加复杂区域单元格。
	 *
	 * @param region         复杂区域
	 * @param currentBounds  当前边界矩形
	 */
	public void addComplexRegionCells(final CRegion region, final Rectangle currentBounds) {
		// 获取当前边界的左上角单元格坐标
		final int minX = this.pathingGrid.getCellX(currentBounds.x);
		final int minY = this.pathingGrid.getCellY(currentBounds.y);
		// 获取当前边界的右下角单元格坐标
		final int maxX = this.pathingGrid.getCellX(currentBounds.x + currentBounds.width);
		final int maxY = this.pathingGrid.getCellY(currentBounds.y + currentBounds.height);

		// 遍历当前边界覆盖的所有单元格
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				// 获取当前单元格的区域列表
				List<CRegion> list = this.cellRegions[y][x];
				// 如果列表为空，则创建一个新的列表
				if (list == null) {
					this.cellRegions[y][x] = list = new ArrayList<>();
				}
				// 将当前区域添加到单元格的区域列表中
				list.add(region);
			}
		}

	}

	/**
	 * 从复杂区域单元格中移除指定区域。
	 *
	 * @param region         复杂区域
	 * @param currentBounds  当前边界矩形
	 */
	public void removeComplexRegionCells(final CRegion region, final Rectangle currentBounds) {
		// 获取当前边界框左上角的单元格坐标
		final int minX = this.pathingGrid.getCellX(currentBounds.x);
		final int minY = this.pathingGrid.getCellY(currentBounds.y);
		// 获取当前边界框右下角的单元格坐标
		final int maxX = this.pathingGrid.getCellX(currentBounds.x + currentBounds.width);
		final int maxY = this.pathingGrid.getCellY(currentBounds.y + currentBounds.height);

		// 遍历边界框覆盖的所有单元格
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				// 获取当前单元格的区域列表
				final List<CRegion> list = this.cellRegions[y][x];
				// 如果区域列表不为空，则从中移除指定的区域
				if (list != null) {
					list.remove(region);
				}
			}
		}

	}

	/**
	 * 计算新的最小复杂区域边界。
	 *
	 * @param region                复杂区域
	 * @param complexRegionBounds   复杂区域边界
	 */
	public void computeNewMinimumComplexRegionBounds(final CRegion region, final Rectangle complexRegionBounds) {
		// 获取复杂区域的边界坐标转换为网格坐标后的最小和最大X、Y值
		final int minX = this.pathingGrid.getCellX(complexRegionBounds.x);
		final int minY = this.pathingGrid.getCellY(complexRegionBounds.y);
		final int maxX = this.pathingGrid.getCellX(complexRegionBounds.x + complexRegionBounds.width);
		final int maxY = this.pathingGrid.getCellY(complexRegionBounds.y + complexRegionBounds.height);

		// 初始化新的边界值，用于存储计算后的世界坐标系中的最小和最大X、Y值
		float newMinX = this.pathingGrid.getWorldX(this.pathingGrid.getWidth() - 1);
		float newMaxX = this.pathingGrid.getWorldX(0);
		float newMinY = this.pathingGrid.getWorldY(this.pathingGrid.getHeight() - 1);
		float newMaxY = this.pathingGrid.getWorldY(0);

		// 遍历网格中的每个单元格
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				// 获取当前单元格中的区域列表
				final List<CRegion> list = this.cellRegions[y][x];
				// 如果列表不为空且包含目标区域
				if (list != null && list.contains(region)) {
					// 获取当前单元格的世界坐标
					final float worldX = this.pathingGrid.getWorldX(x);
					final float worldY = this.pathingGrid.getWorldY(y);
					// 计算当前区域的世界坐标系中的边界值
					final float wMinX = worldX - 16f;
					final float wMinY = worldY - 16f;
					final float wMaxX = worldX + 15f;
					final float wMaxY = worldY + 15f;
					// 更新新的边界值
					if (wMinX < newMinX) {
						newMinX = wMinX;
					}
					if (wMinY < newMinY) {
						newMinY = wMinY;
					}
					if (wMaxX > newMaxX) {
						newMaxX = wMaxX;
					}
					if (wMaxY > newMaxY) {
						newMaxY = wMaxY;
					}
				}
			}
		}
		// 设置复杂区域的新边界值
		complexRegionBounds.set(newMinX, newMinY, newMaxX - newMinX, newMaxY - newMinY);

	}

	/**
	 * 添加复杂区域单元格。
	 *
	 * @param region         复杂区域
	 * @param x              当前X坐标
	 * @param y              当前Y坐标
	 * @param boundsToUpdate 更新边界的矩形
	 */
	public void addComplexRegionCell(final CRegion region, final float x, final float y,
			final Rectangle boundsToUpdate) {
		// 获取x坐标对应的网格单元的x索引
		final int cellX = this.pathingGrid.getCellX(x);
		// 获取y坐标对应的网格单元的y索引
		final int cellY = this.pathingGrid.getCellY(y);
		// 获取对应网格单元的区域列表
		List<CRegion> list = this.cellRegions[cellY][cellX];
		// 如果区域列表为空，则初始化一个新的ArrayList
		if (list == null) {
			this.cellRegions[cellY][cellX] = list = new ArrayList<>();
		}
		// 将新的区域添加到列表中
		list.add(region);
		// 获取网格单元对应的世界的x坐标
		final float worldX = this.pathingGrid.getWorldX(cellX);
		// 获取网格单元对应的世界的y坐标
		final float worldY = this.pathingGrid.getWorldY(cellY);
		// 计算更新边界的最小x坐标
		final float wMinX = worldX - 16f;
		// 计算更新边界的最小y坐标
		final float wMinY = worldY - 16f;
		// 合并新的边界到待更新边界中，边界大小为31x31
		boundsToUpdate.merge(tempRect.set(wMinX, wMinY, 31f, 31f));

	}

	/**
	 * 清除复杂区域单元格。
	 *
	 * @param region         复杂区域
	 * @param x              当前X坐标
	 * @param y              当前Y坐标
	 * @param boundsToUpdate 更新边界的矩形
	 */
	public void clearComplexRegionCell(final CRegion region, final float x, final float y,
			final Rectangle boundsToUpdate) {
		final int cellX = this.pathingGrid.getCellX(x);
		final int cellY = this.pathingGrid.getCellY(y);
		final List<CRegion> list = this.cellRegions[cellY][cellX];
		if (list != null) {
			list.remove(region);
		}
		computeNewMinimumComplexRegionBounds(region, boundsToUpdate);
	}

	/**
	 * 检查点是否在复杂区域内。
	 *
	 * @param region 复杂区域
	 * @param x      X坐标
	 * @param y      Y坐标
	 * @return 是否在复杂区域内
	 */
	public boolean isPointInComplexRegion(final CRegion region, final float x, final float y) {
		// 获取路径网格中指定坐标的单元格X坐标
		final int cellX = this.pathingGrid.getCellX(x);
		// 获取路径网格中指定坐标的单元格Y坐标
		final int cellY = this.pathingGrid.getCellY(y);
		// 获取对应单元格的区域列表
		final List<CRegion> list = this.cellRegions[cellY][cellX];
		// 如果区域列表不为空，检查列表中是否包含指定的区域
		if (list != null) {
			return list.contains(region);
		}
		// 如果区域列表为空或未找到指定区域，返回false
		return false;

	}

	/**
	 * 处理单位进入区域的事件。
	 *
	 * @param unit   进入区域的单位
	 * @param region 目标区域
	 */
	public void onUnitEnterRegion(final CUnit unit, final CRegion region) {
		for (final CRegionTriggerEnter enterTrigger : region.getEnterTriggers()) {
			enterTrigger.fire(unit, region);
		}
	}

	/**
	 * 处理单位离开区域的事件。
	 *
	 * @param unit   离开区域的单位
	 * @param region 目标区域
	 */
	public void onUnitLeaveRegion(final CUnit unit, final CRegion region) {
		for (final CRegionTriggerLeave leaveTrigger : region.getLeaveTriggers()) {
			leaveTrigger.fire(unit, region);
		}
	}

}
