package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityBuildInProgress;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNagaBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNeutralBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNightElfBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityOrcBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityUndeadBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.combat.CAbilityColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop.CAbilityNeutralBuilding;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop.CAbilitySellItems;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.jass.CAbilityJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.root.CAbilityRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityQueue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityReviveHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.upgrade.CAbilityUpgrade;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
/**
 * A visitor for the lowest level inherent types of an ability. It's a bit of a
 * design clash to have the notion of an ability visitor pattern while also
 * having any arbitrary number of "ability types" defined in config files. But
 * the way that we will handle this for now will be with the notion of a generic
 * ability (one whose UI information and behaviors come from a rawcode) versus
 * abilities with engine-level type information (move, stop, attack).
 */
public interface CAbilityVisitor<T> {
	// 接受攻击能力的访问者方法
	T accept(CAbilityAttack ability);

	// 接受移动能力的访问者方法
	T accept(CAbilityMove ability);

	// 接受兽人建筑能力的访问者方法
	T accept(CAbilityOrcBuild ability);

	// 接受人类建筑能力的访问者方法
	T accept(CAbilityHumanBuild ability);

	// 接受不死族建筑能力的访问者方法
	T accept(CAbilityUndeadBuild ability);

	// 接受夜精灵建筑能力的访问者方法
	T accept(CAbilityNightElfBuild ability);

	// 接受通用无操作能力的访问者方法
	T accept(CAbilityGenericDoNothing ability);

	// 接受寒箭能力的访问者方法
	T accept(CAbilityColdArrows ability);

	// 接受娜迦建筑能力的访问者方法
	T accept(CAbilityNagaBuild ability);

	// 接受中立建筑能力的访问者方法
	T accept(CAbilityNeutralBuild ability);

	// 接受正在建设中的能力的访问者方法
	T accept(CAbilityBuildInProgress ability);

	// 接受能力队列的访问者方法
	T accept(CAbilityQueue ability);

	// 接受升级能力的访问者方法
	T accept(CAbilityUpgrade ability);

	// 接受售卖物品能力的访问者方法
	T accept(CAbilitySellItems ability);

	// 接受复活英雄能力的访问者方法
	T accept(CAbilityReviveHero ability);

	// 接受返回资源能力的访问者方法
	T accept(CAbilityReturnResources ability);

	// 接受通用单图标主动能力的访问者方法
	T accept(GenericSingleIconActiveAbility ability);

	// 接受通用单图标被动能力的访问者方法
	T accept(GenericSingleIconPassiveAbility ability);

	// 接受集结能力的访问者方法
	T accept(CAbilityRally ability);

	// 接受通用无图标能力的访问者方法
	T accept(GenericNoIconAbility ability);

	// 接受增益效果的访问者方法
	T accept(CBuff ability);

	// 接受英雄能力的访问者方法
	T accept(CAbilityHero ability);

	// 接受JASS能力的访问者方法
	T accept(CAbilityJass ability);

	// 接受根能力的访问者方法
	T accept(CAbilityRoot ability);

	// 接受中立建筑能力的访问者方法
	T accept(CAbilityNeutralBuilding ability);

	// 接受主动能力构建者的访问者方法
	T accept(AbilityBuilderActiveAbility ability);
}

