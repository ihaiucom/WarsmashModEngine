package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;

// 联盟类型
public enum CAllianceType implements CHandle {
	PASSIVE,  //表示被动状态，可能是指该玩家或单位不主动参与战斗或行为。
	HELP_REQUEST,  //表示请求帮助的状态，可能用于发出求援信号。
	HELP_RESPONSE,  //表示响应帮助的状态，可能用于表示其他玩家或单位对求助信号的回应。
	SHARED_XP,  //表示共享经验值，可能指多个玩家或单位在游戏中共同获得经验奖励。
	SHARED_SPELLS,  //表示共享技能，可能允许某些单位或玩家共享各自的技能或魔法。
	SHARED_VISION,  //表示共享视野，允许某些单位或玩家共享彼此的视野信息。
	SHARED_CONTROL,  //表示共享控制，可能允许一个玩家控制另一个玩家的单位。
	SHARED_ADVANCED_CONTROL,  //表示共享高级控制，可能涉及更复杂的控制机制，例如战术协同。
	RESCUABLE,  //表示可救援状态，可能用于表示某个单位可以被其他单位救助。
	SHARED_VISION_FORCED;  //表示强制共享视野，可能是某些特殊条件下强制实施的共享视野。

	public static CAllianceType[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
