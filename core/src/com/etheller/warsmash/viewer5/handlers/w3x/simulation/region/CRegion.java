package com.etheller.warsmash.viewer5.handlers.w3x.simulation.region;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
/**
 * 表示一个区域的类，包含与该区域相关的边界和触发器。
 */
public class CRegion {
	private Rectangle currentBounds; // 当前边界
	private boolean complexRegion; // 是否为复杂区域
	private final List<CRegionTriggerEnter> enterTriggers = new ArrayList<>(); // 进入触发器列表
	private final List<CRegionTriggerLeave> leaveTriggers = new ArrayList<>(); // 离开触发器列表

	/**
	 * 添加一个矩形到当前区域。
	 * @param rect 要添加的矩形
	 * @param regionManager 区域管理器
	 */
	public void addRect(final Rectangle rect, final CRegionManager regionManager) {
		// 如果当前边界为空，则创建一个新的矩形边界并添加到区域管理器中
		if (this.currentBounds == null) {
			this.currentBounds = new Rectangle(rect); // 创建新的矩形边界
			regionManager.addRectForRegion(this, this.currentBounds); // 将新边界添加到区域管理器
		}
		// 如果当前边界不为空
		else {
			// 如果当前不是复杂区域
			if (!this.complexRegion) {
				convertToComplexRegionAndAddRect(rect, regionManager); // 转换为复杂区域并添加矩形
			}
			// 如果当前是复杂区域
			else {
				complexRegionAddRect(rect, regionManager); // 在复杂区域中添加矩形
			}
		}

	}

	/**
	 * 清除当前区域中的矩形。
	 * @param rect 要清除的矩形
	 * @param regionManager 区域管理器
	 */
	public void clearRect(final Rectangle rect, final CRegionManager regionManager) {
		// 如果当前边界为空，则直接返回，不进行任何操作
		if (this.currentBounds == null) {
			return;
		}
		// 如果当前区域是复杂区域
		if (this.complexRegion) {
			// 从区域管理器中移除当前区域的矩形
			regionManager.removeRectForRegion(this, this.currentBounds);
			// 移除复杂区域的单元格
			regionManager.removeComplexRegionCells(this, rect);
			// 计算新的最小复杂区域边界
			regionManager.computeNewMinimumComplexRegionBounds(this, this.currentBounds);
			// 将当前区域的矩形重新添加到区域管理器
			regionManager.addRectForRegion(this, this.currentBounds);
		} else {
			// 如果不是复杂区域，则将其标记为复杂区域
			this.complexRegion = true;
			// 添加当前边界的复杂区域单元格
			regionManager.addComplexRegionCells(this, this.currentBounds);
			// 移除指定矩形的复杂区域单元格
			regionManager.removeComplexRegionCells(this, rect);
		}

	}

	/**
	 * 从区域中移除。
	 * @param regionManager 区域管理器
	 */
	public void remove(final CRegionManager regionManager) {
		// 如果当前边界为空，则直接返回，不进行任何操作
		if (this.currentBounds == null) {
			return;
		}
		// 如果当前区域是复杂区域，则从区域管理器中移除该复杂区域的单元格
		if (this.complexRegion) {
			regionManager.removeComplexRegionCells(this, this.currentBounds);
		}
		// 从区域管理器中移除指定区域的矩形
		regionManager.removeRectForRegion(this, this.currentBounds);

	}

	/**
	 * 添加一个单元格到当前区域。
	 * @param x 单元格的x坐标
	 * @param y 单元格的y坐标
	 * @param regionManager 区域管理器
	 */
	public void addCell(final float x, final float y, final CRegionManager regionManager) {
		// 如果当前边界为空
		if (this.currentBounds == null) {
			// 设置复杂区域标志为真
			this.complexRegion = true;
			// 创建一个新的矩形作为当前边界
			this.currentBounds = new Rectangle(x, y, 0, 0);
			// 将当前区域单元添加到区域管理器
			regionManager.addComplexRegionCell(this, x, y, this.currentBounds);
			// 为当前区域添加矩形
			regionManager.addRectForRegion(this, this.currentBounds);
		} else {
			// 如果当前边界不为空，先从区域管理器中移除当前区域的矩形
			regionManager.removeRectForRegion(this, this.currentBounds);
			// 如果当前不是复杂区域
			if (!this.complexRegion) {
				// 添加当前边界的所有复杂区域单元
				regionManager.addComplexRegionCells(this, this.currentBounds);
				// 设置复杂区域标志为真
				this.complexRegion = true;
			}
			// 添加新的区域单元到区域管理器
			regionManager.addComplexRegionCell(this, x, y, this.currentBounds);
			// 再次为当前区域添加矩形
			regionManager.addRectForRegion(this, this.currentBounds);
		}

	}

	/**
	 * 清除区域中的一个单元格。
	 * @param x 单元格的x坐标
	 * @param y 单元格的y坐标
	 * @param regionManager 区域管理器
	 */
	public void clearCell(final float x, final float y, final CRegionManager regionManager) {
		// 如果当前边界为空，则直接返回，不进行任何操作
		if (this.currentBounds == null) {
			return;
		}
		// 否则，执行以下操作
		else {
			// 从区域管理器中移除当前对象的当前边界矩形
			regionManager.removeRectForRegion(this, this.currentBounds);
			// 如果当前不是复杂区域
			if (!this.complexRegion) {
				// 添加复杂区域单元到区域管理器
				regionManager.addComplexRegionCells(this, this.currentBounds);
				// 将当前区域标记为复杂区域
				this.complexRegion = true;
			}
			// 清除指定位置的复杂区域单元
			regionManager.clearComplexRegionCell(this, x, y, this.currentBounds);
			// 重新添加当前边界矩形到区域管理器
			regionManager.addRectForRegion(this, this.currentBounds);
		}

	}

	/**
	 * 处理复杂区域内的矩形添加。
	 * @param rect 要添加的矩形
	 * @param regionManager 区域管理器
	 */
	private void complexRegionAddRect(final Rectangle rect, final CRegionManager regionManager) {
		// 移除当前区域
		regionManager.removeRectForRegion(this, this.currentBounds);
		// 添加复杂区域单元
		regionManager.addComplexRegionCells(this, rect);
		// 合并当前边界和新的矩形区域
		this.currentBounds = this.currentBounds.merge(rect);
		// 为合并后的区域添加矩形
		regionManager.addRectForRegion(this, this.currentBounds);

	}

	/**
	 * 将当前区域转换为复杂区域并添加矩形。
	 * @param rect 要添加的矩形
	 * @param regionManager 区域管理器
	 */
	private void convertToComplexRegionAndAddRect(final Rectangle rect, final CRegionManager regionManager) {
		// 移除当前区域
		regionManager.removeRectForRegion(this, this.currentBounds);
		// 标记当前区域为复杂区域
		this.complexRegion = true;
		// 添加复杂区域的单元格
		regionManager.addComplexRegionCells(this, this.currentBounds);
		// 添加新矩形的单元格到复杂区域
		regionManager.addComplexRegionCells(this, rect);
		// 合并当前边界和新矩形
		this.currentBounds = this.currentBounds.merge(rect);
		// 更新当前区域的矩形边界
		regionManager.addRectForRegion(this, this.currentBounds);

	}

	/**
	 * 获取当前边界。
	 * @return 当前边界矩形
	 */
	public Rectangle getCurrentBounds() {
		return this.currentBounds;
	}

	/**
	 * 设置当前边界。
	 * @param currentBounds 新的当前边界矩形
	 */
	public void setCurrentBounds(final Rectangle currentBounds) {
		this.currentBounds = currentBounds;
	}

	/**
	 * 检查是否为复杂区域。
	 * @return 如果是复杂区域则返回true
	 */
	public boolean isComplexRegion() {
		return this.complexRegion;
	}

	/**
	 * 设置复杂区域标志。
	 * @param complexRegion 新的复杂区域标志
	 */
	public void setComplexRegion(final boolean complexRegion) {
		this.complexRegion = complexRegion;
	}

	/**
	 * 检查给定的坐标是否在区域内。
	 * @param x 坐标x
	 * @param y 坐标y
	 * @param regionManager 区域管理器
	 * @return 如果坐标在区域内则返回true
	 */
	public boolean contains(final float x, final float y, final CRegionManager regionManager) {
		// 检查当前对象是否包含指定的点 (x, y)
		if (this.currentBounds == null) {
			// 如果当前边界为空，直接返回false，表示不包含该点
			return false;
		}
		if (this.complexRegion) {
			// 如果当前区域是复杂区域，调用regionManager的isPointInComplexRegion方法检查点是否在区域内
			/**
			 * 检查指定点是否在复杂区域内
			 * @param region 当前区域对象
			 * @param x 点的横坐标
			 * @param y 点的纵坐标
			 * @return 如果点在区域内返回true，否则返回false
			 */
			return regionManager.isPointInComplexRegion(this, x, y);
		}
		// 如果不是复杂区域，直接使用currentBounds的contains方法检查点是否在边界内
		return this.currentBounds.contains(x, y);

	}

	/**
	 * 获取进入触发器列表。
	 * @return 进入触发器列表
	 */
	public List<CRegionTriggerEnter> getEnterTriggers() {
		return this.enterTriggers;
	}

	/**
	 * 获取离开触发器列表。
	 * @return 离开触发器列表
	 */
	public List<CRegionTriggerLeave> getLeaveTriggers() {
		return this.leaveTriggers;
	}

	/**
	 * 添加进入触发器，并返回可移除事件。
	 * @param triggerEnter 进入触发器
	 * @return 可移除触发器事件
	 */
	public RemovableTriggerEvent add(final CRegionTriggerEnter triggerEnter) {
		this.enterTriggers.add(triggerEnter);
		return new RemovableTriggerEvent() {
			@Override
			public void remove() {
				CRegion.this.enterTriggers.remove(triggerEnter);
			}
		};
	}

	/**
	 * 添加离开触发器，并返回可移除事件。
	 * @param leaveTrigger 离开触发器
	 * @return 可移除触发器事件
	 */
	public RemovableTriggerEvent add(final CRegionTriggerLeave leaveTrigger) {
		this.leaveTriggers.add(leaveTrigger);
		return new RemovableTriggerEvent() {
			@Override
			public void remove() {
				CRegion.this.leaveTriggers.remove(leaveTrigger);
			}
		};
	}
}

