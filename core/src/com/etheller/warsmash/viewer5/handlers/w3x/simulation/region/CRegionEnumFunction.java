package com.etheller.warsmash.viewer5.handlers.w3x.simulation.region;

/**
 * CRegionEnumFunction 接口用于定义对区域进行操作的方法。
 */
public interface CRegionEnumFunction {
	/**
	 * Operates on a region, returning true if we should stop execution.
	 *
	 * @param region
	 * @return
	 */
	boolean call(CRegion region);
}

