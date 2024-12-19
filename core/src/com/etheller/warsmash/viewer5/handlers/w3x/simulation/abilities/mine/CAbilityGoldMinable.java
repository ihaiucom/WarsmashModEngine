package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorHarvest;

// 此接口定义了可以被挖掘的金矿的能力
public interface CAbilityGoldMinable {

	// 获取当前激活的矿工数量
	int getActiveMinerCount();

	// 获取矿的最大挖掘能力
	int getMiningCapacity();

	// 添加一个矿工进行挖掘
	void addMiner(CBehaviorHarvest cBehaviorHarvest);

	// 获取挖掘所需的持续时间
	float getMiningDuration();

	// 获取当前矿中的金子数量
	int getGold();

	// 设置矿中的金子数量
	void setGold(int amount);

	// 判断是否为基础矿
	boolean isBaseMine();
}

