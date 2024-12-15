package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;
/**
 * CRace 类实现了 CHandle 接口，表示一个竞赛实体。
 */
public class CRace implements CHandle {
	private int id;

	/**
	 * 构造函数，用于初始化 CRace 实例的 id。
	 * @param id 竞赛的唯一标识符。
	 */
	public CRace(final int id) {
		this.id = id;
	}

	/**
	 * 获取竞赛的唯一标识符。
	 * @return 竞赛的 id。
	 */
	public int getId() {
		return this.id;
	}

	@Override
	/**
	 * 获取句柄的 id，符合 CHandle 接口的要求。
	 * @return 句柄的 id。
	 */
	public int getHandleId() {
		return getId();
	}

	/**
	 * 获取该竞赛的序数，通常用于排序或比较。
	 * @return 竞赛的序数。
	 */
	public int ordinal() {
		return this.id - 1;
	}
}
