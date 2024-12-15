package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public enum CWeaponType {
	// 没有, 代表没有任何武器类型，伤害类型未知，且不具有远程攻击特性。
	NONE(CDamageType.UNKNOWN, false),
	// 近战, 代表普通武器，伤害类型为普通，且不具备远程攻击特性。
	NORMAL(CDamageType.NORMAL, false),
	// 立即, 代表瞬发/远程武器，虽然伤害类型为普通，但可以进行远程攻击。
	INSTANT(CDamageType.NORMAL, true),
	// 炮火, 表示炮火武器，伤害类型为普通，具备远程攻击特性。
	ARTILLERY(CDamageType.NORMAL, true),
	// 炮灰(穿透), 代表直线攻击武器，伤害类型为普通，同时可以进行远程攻击。
	ALINE(CDamageType.NORMAL, true),
	// 箭矢, 代表导弹类武器，伤害类型为普通，具备远程攻击特性。
	MISSILE(CDamageType.NORMAL, true),
	// 箭矢(溅射), 表示溅射伤害武器，伤害类型为普通，且可以远程攻击。
	MSPLASH(CDamageType.NORMAL, true),
	// 箭矢(弹射), 代表反弹攻击武器，伤害类型为普通，并具有远程攻击能力。
	MBOUNCE(CDamageType.NORMAL, true),
	// 箭矢(穿透), 代表线性攻击武器，伤害类型为普通，具备远程攻击特性。
	MLINE(CDamageType.NORMAL, true);

	private CDamageType damageType;
	private boolean ranged; // 是否具有远程攻击能力

	CWeaponType(CDamageType damageType, boolean ranged) {
		this.damageType = damageType;
	}

	public CDamageType getDamageType() {
		return damageType;
	}

	public boolean isRanged() {
		return ranged;
	}

	public void setRanged(boolean ranged) {
		this.ranged = ranged;
	}

	public static CWeaponType parseWeaponType(final String weaponTypeString) {
		return valueOf(weaponTypeString.toUpperCase());
	}

	// 是否支持攻击地面坐标
	public boolean isAttackGroundSupported() {
		// 炮火武器、直线攻击武器 支持攻击地面坐标
		return (this == CWeaponType.ARTILLERY) || (this == CWeaponType.ALINE);
	}
}
