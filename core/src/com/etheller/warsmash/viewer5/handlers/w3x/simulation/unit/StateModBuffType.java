package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;

public enum StateModBuffType {
	ETHEREAL,  //表示单位处于虚无状态，通常意味着单位处于不可见或无法被攻击的状态。
	RESISTANT,  //表示单位对某种类型的伤害具有抵抗力。
	SLEEPING,  //表示单位处于睡眠状态，可能无法进行行动。
	STUN,  //表示单位被击晕，无法采取任何行动。
	MAGIC_IMMUNE,  //表示单位对魔法攻击免疫。
	SNARED,  //表示单位被束缚，可能减缓或完全限制其移动。
	DISABLE_AUTO_ATTACK,  //表示单位无法进行自动攻击。
	DISABLE_ATTACK,  //表示单位无法进行任何攻击。
	DISABLE_MELEE_ATTACK,  //表示单位无法进行近战攻击。
	DISABLE_RANGED_ATTACK,  //表示单位无法进行远程攻击。
	DISABLE_SPECIAL_ATTACK,  //表示单位无法进行特殊攻击。
	DISABLE_SPELLS,  //表示单位无法施放法术。
	INVULNERABLE,  //表示单位处于无敌状态，无法受到伤害。
}