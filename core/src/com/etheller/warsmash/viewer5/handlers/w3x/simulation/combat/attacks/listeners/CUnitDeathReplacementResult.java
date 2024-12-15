package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

// CUnitDeathReplacementResult类用于表示单位在死亡后的复活和转世状态
public class CUnitDeathReplacementResult {
	private boolean reviving; // 表示单位是否正在复活
	private boolean reincarnating; // 表示单位是否正在转世

	// 构造函数，初始化复活和转世状态为false
	public CUnitDeathReplacementResult() {
		this.reviving = false;
		this.reincarnating = false;
	}

	// 获取复活状态
	public boolean isReviving() {
		return reviving;
	}

	// 设置复活状态
	public void setReviving(boolean reviving) {
		this.reviving = reviving;
	}

	// 获取转世状态
	public boolean isReincarnating() {
		return reincarnating;
	}

	// 设置转世状态
	public void setReincarnating(boolean reincarnating) {
		this.reincarnating = reincarnating;
	}
}
