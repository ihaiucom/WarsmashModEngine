package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityBuildInProgress;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNagaBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNeutralBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNightElfBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityOrcBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityUndeadBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.combat.CAbilityColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.*;
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
 * 根据能力ID编号获得对应的等级能力 CLevelingAbility。
 */
public class GetAbilityByRawcodeVisitor implements CAbilityVisitor<CLevelingAbility> {
	private static final GetAbilityByRawcodeVisitor INSTANCE = new GetAbilityByRawcodeVisitor();

	/**
	 * 获取 GetAbilityByRawcodeVisitor 的单例实例。
	 * @return 单例实例
	 */
	public static GetAbilityByRawcodeVisitor getInstance() {
		return INSTANCE;
	}

	// 召集能力的原始代码
	private static final War3ID RALLY_RAWCODE = War3ID.fromString("Aral");
	private War3ID rawcode;

	/**
	 * 重置当前访客的原始代码。
	 * @param rawcode 原始代码
	 * @return 当前实例
	 */
	public GetAbilityByRawcodeVisitor reset(final War3ID rawcode) {
		this.rawcode = rawcode;
		return this;
	}

	@Override
	/**
	 * 处理 CAbilityAttack 类型的能力。
	 * @param ability 能力实例
	 * @return null
	 */
	public CLevelingAbility accept(final CAbilityAttack ability) {
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityMove 类型的能力。
	 * @param ability 能力实例
	 * @return null
	 */
	public CLevelingAbility accept(final CAbilityMove ability) {
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityOrcBuild 类型的能力。
	 * @param ability 能力实例
	 * @return null
	 */
	public CLevelingAbility accept(final CAbilityOrcBuild ability) {
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityHumanBuild 类型的能力。
	 * @param ability 能力实例
	 * @return null
	 */
	public CLevelingAbility accept(final CAbilityHumanBuild ability) {
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityUndeadBuild 类型的能力。
	 * @param ability 能力实例
	 * @return null
	 */
	public CLevelingAbility accept(final CAbilityUndeadBuild ability) {
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityNightElfBuild 类型的能力。
	 * @param ability 能力实例
	 * @return null
	 */
	public CLevelingAbility accept(final CAbilityNightElfBuild ability) {
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityGenericDoNothing 类型的能力，检查原始代码是否匹配。
	 * @param ability 能力实例
	 * @return 匹配的能力或 null
	 */
	public CLevelingAbility accept(final CAbilityGenericDoNothing ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityColdArrows 类型的能力，检查原始代码是否匹配。
	 * @param ability 能力实例
	 * @return 匹配的能力或 null
	 */
	public CLevelingAbility accept(final CAbilityColdArrows ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityNagaBuild 类型的能力。
	 * @param ability 能力实例
	 * @return null
	 */
	public CLevelingAbility accept(final CAbilityNagaBuild ability) {
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityNeutralBuild 类型的能力。
	 * @param ability 能力实例
	 * @return null
	 */
	public CLevelingAbility accept(final CAbilityNeutralBuild ability) {
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityBuildInProgress 类型的能力。
	 * @param ability 能力实例
	 * @return null
	 */
	public CLevelingAbility accept(final CAbilityBuildInProgress ability) {
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityQueue 类型的能力。
	 * @param ability 能力实例
	 * @return null
	 */
	public CLevelingAbility accept(final CAbilityQueue ability) {
		return null;
	}

	@Override
	/**
	 * 处理 CAbilitySellItems 类型的能力。
	 * @param ability 能力实例
	 * @return null
	 */
	public CLevelingAbility accept(final CAbilitySellItems ability) {
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityUpgrade 类型的能力。
	 * @param ability 能力实例
	 * @return null
	 */
	public CLevelingAbility accept(final CAbilityUpgrade ability) {
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityReviveHero 类型的能力。
	 * @param ability 能力实例
	 * @return null
	 */
	public CLevelingAbility accept(final CAbilityReviveHero ability) {
		return null;
	}

	@Override
	/**
	 * 处理 AbilityBuilderActiveAbility 类型的能力，检查原始代码是否匹配。
	 * @param ability 能力实例
	 * @return 匹配的能力或 null
	 */
	public CLevelingAbility accept(final AbilityBuilderActiveAbility ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	/**
	 * 处理 GenericSingleIconActiveAbility 类型的能力，检查原始代码是否匹配。
	 * @param ability 能力实例
	 * @return 匹配的能力或 null
	 */
	public CLevelingAbility accept(final GenericSingleIconActiveAbility ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	/**
	 * 处理 GenericSingleIconPassiveAbility 类型的能力，检查原始代码是否匹配。
	 * @param ability 能力实例
	 * @return 匹配的能力或 null
	 */
	public CLevelingAbility accept(GenericSingleIconPassiveAbility ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityRoot 类型的能力，检查原始代码是否匹配。
	 * @param ability 能力实例
	 * @return 匹配的能力或 null
	 */
	public CLevelingAbility accept(final CAbilityRoot ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	/**
	 * 召集能力
	 * @param ability 能力实例
	 * @return 匹配的能力或 null
	 */
	public CLevelingAbility accept(final CAbilityRally ability) {
		if (this.rawcode.equals(RALLY_RAWCODE)) {
			return ability;
		}
		return null;
	}

	@Override
	/**
	 * 处理 GenericNoIconAbility 类型的能力，检查原始代码是否匹配。
	 * @param ability 能力实例
	 * @return 匹配的能力或 null
	 */
	public CLevelingAbility accept(final GenericNoIconAbility ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	/**
	 * 处理 CBuff 类型的能力，检查原始代码是否匹配。
	 * @param ability 能力实例
	 * @return 匹配的能力或 null
	 */
	public CLevelingAbility accept(final CBuff ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityReturnResources 类型的能力，检查原始代码是否匹配。
	 * @param ability 能力实例
	 * @return 匹配的能力或 null
	 */
	public CLevelingAbility accept(final CAbilityReturnResources ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityHero 类型的能力。
	 * @param ability 能力实例
	 * @return null
	 */
	public CLevelingAbility accept(final CAbilityHero ability) {
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityJass 类型的能力，检查原始代码是否匹配。
	 * @param ability 能力实例
	 * @return 匹配的能力或 null
	 */
	public CLevelingAbility accept(final CAbilityJass ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	/**
	 * 处理 CAbilityNeutralBuilding 类型的能力，检查原始代码是否匹配。
	 * @param ability 能力实例
	 * @return 匹配的能力或 null
	 */
	public CLevelingAbility accept(final CAbilityNeutralBuilding ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}
}

