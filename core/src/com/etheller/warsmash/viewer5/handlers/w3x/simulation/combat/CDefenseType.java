package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

/**
 * 枚举类 CDefenseType 表示不同类型的防御。
 */
public enum CDefenseType implements CodeKeyType {
	SMALL,  //小型防御。
	MEDIUM,  //中型防御。
	LARGE,  //大型防御。
	FORT,  //堡垒防御。
	NORMAL,  //正常防御。
	HERO,  //英雄防御。
	DIVINE,  //神圣防御。
	NONE; //无防御。

	public static CDefenseType[] VALUES = values();

	private String codeKey;

	/**
	 * CDefenseType 枚举构造函数，用于初始化 codeKey 字段。
	 */
	private CDefenseType() {
		this.codeKey = name().charAt(0) + name().substring(1).toLowerCase();
	}

	@Override
	/**
	 * 获取编码键。
	 * @return 返回相应的代码键。
	 */
	public String getCodeKey() {
		return this.codeKey;
	}

	/**
	 * 根据传入的字符串解析出相应的防御类型。
	 * @param typeString 输入的防御类型字符串。
	 * @return 返回相应的 CDefenseType 枚举值。
	 */
	public static CDefenseType parseDefenseType(final String typeString) {
		final String upperCaseTypeString = typeString.toUpperCase();
		if (upperCaseTypeString.equals("HEAVY")) {
			return LARGE;
		}
		if (upperCaseTypeString.trim().isEmpty()) {
			System.err.println("bad");
		}
		return valueOf(upperCaseTypeString);
	}
}
