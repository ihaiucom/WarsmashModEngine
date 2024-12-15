package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ExternStringMsgAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ExternStringMsgTargetCheckReceiver;

// 定义一个命令接口，包含获取能力句柄ID、命令ID、开始行为、获取目标、是否排队、事件触发等方法
public interface COrder {
	// 获取能力句柄ID
	int getAbilityHandleId();

	// 获取命令ID
	int getOrderId();

	// 开始行为，传入游戏模拟器和施法者； 能力检测是否可以使用该指令，返回对应执行的行为；如果不能使用就显示错误文本，并执行指令队列的下一个指令或者是默认指令
	CBehavior begin(final CSimulation game, CUnit caster);

	// 获取目标，传入游戏模拟器
	AbilityTarget getTarget(CSimulation game);

	// 判断命令是否排队
	boolean isQueued();

	// 目标检查接收器，用于处理目标检查消息
	final ExternStringMsgTargetCheckReceiver<?> targetCheckReceiver = new ExternStringMsgTargetCheckReceiver<>();
	// 能力激活接收器，用于处理能力激活消息
	final ExternStringMsgAbilityActivationReceiver abilityActivationReceiver = new ExternStringMsgAbilityActivationReceiver();

	// 触发事件，传入游戏模拟器和单位
	void fireEvents(CSimulation game, CUnit unit);
}
