package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

/**
 * 能力禁用类型
 */
public enum CAbilityDisableType {
	// 定义一个枚举类，代表不同的操作或状态
	REQUIREMENTS((byte) 1),
	// 表示建造操作
	CONSTRUCTION((byte) 2),
	// 表示传送操作
	TRANSFORMATION((byte) 4),
	// 表示触发操作
	TRIGGER((byte) 8),
	// 表示攻击被禁用的状态
	ATTACKDISABLED((byte) 16);


	private byte mask;
	
	CAbilityDisableType(byte i) {
		this.mask = i;
	}
	
	public byte getMask() {
		return mask;
	}

}
