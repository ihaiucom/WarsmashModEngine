package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

public enum CUpgradeClass {
	ARMOR,  //可能表示与防御或护甲相关的升级。
	ARTILLERY,  //可能表示与炮兵或远程攻击相关的升级。
	MELEE,  //可能表示与近战攻击相关的升级。
	RANGED,  //可能表示与远程攻击相关的升级。
	CASTER;  //可能表示与施法、魔法相关的升级。

	public static CUpgradeClass parseUpgradeClass(final String upgradeClassString) {
		try {
			return valueOf(upgradeClassString.toUpperCase());
		}
		catch (Exception exc) {
			return null;
		}
	}
}
