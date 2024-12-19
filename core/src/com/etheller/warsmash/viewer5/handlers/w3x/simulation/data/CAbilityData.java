package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityGenericDoNothing;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityDropInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemExperienceGain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemFigurineSummon;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemHeal;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemLevelGain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemManaBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemManaRegain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemPermanentLifeGain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityEntangledMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.eattree.CAbilityEatTree;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.moonwell.CAbilityMoonWell;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.root.CAbilityEntangleGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage.CAbilityBlizzard;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage.CAbilityBrilliance;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage.CAbilityMassTeleport;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage.CAbilitySummonWaterElemental;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.bloodmage.phoenix.CAbilitySummonPhoenix;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking.CAbilityAvatar;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking.CAbilityThunderBolt;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking.CAbilityThunderClap;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin.CAbilityDevotion;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin.CAbilityDivineShield;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin.CAbilityHolyLight;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin.CAbilityResurrect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.beastmaster.CAbilitySummonGrizzly;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.beastmaster.CAbilitySummonHawk;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.beastmaster.CAbilitySummonQuilbeast;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.darkranger.CAbilityCharm;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.sappers.CAbilityKaboom;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.tinker.CAbilityClusterRockets;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.tinker.CAbilityFactory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.tinker.CAbilityPocketFactory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.nightelf.demonhunter.CAbilityManaBurn;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.nightelf.keeper.CAbilityForceOfNature;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.nightelf.moonpriestess.CAbilitySummonOwlScout;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.nightelf.warden.CAbilityBlink;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.farseer.CAbilityChainLightning;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.farseer.CAbilityFeralSpirit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.taurenchieftain.CAbilityWarStomp;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.undead.deathknight.CAbilityDarkRitual;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.undead.deathknight.CAbilityDeathCoil;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.undead.deathknight.CAbilityDeathPact;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionAcolyteHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionBlight;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionBlightedGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionCargoHold;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionCargoHoldBurrow;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionCargoHoldEntangledMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionCarrionSwarmDummy;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionChannelTest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionDrop;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionGoldMineOverlayed;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionHarvestLumber;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionHumanRepair;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionImmolation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionInvulnerable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionItemAttackBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionItemDefenseBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionItemHeal;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionItemLifeBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionItemManaRegain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionItemPermanentStatGain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionItemStatBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionLoad;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionNeutralBuilding;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionPhoenixFire;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionRepair;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionShopPurchaseItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionShopSharing;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionStandDown;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionWispHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.jass.CAbilityTypeJassDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderDupe;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderParserUtil;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.definitions.impl.CAbilityTypeDefinitionAbilityTemplateBuilder;
public class CAbilityData {
    // 存储能力数据的对象
	private final ObjectData abilityData;
    // 别名到能力类型的映射
	private Map<War3ID, CAbilityType<?>> aliasToAbilityType = new HashMap<>();
    // 代码到能力类型定义的映射
	private final Map<War3ID, CAbilityTypeDefinition> codeToAbilityTypeDefinition = new HashMap<>();

    // 构造函数，初始化能力数据并注册代码
	public CAbilityData(final ObjectData abilityData) {
		this.abilityData = abilityData;
		this.aliasToAbilityType = new HashMap<>();
		registerCodes();
	}

    // 注册各种能力的代码和相应的能力类型定义
	private void registerCodes() {
		// ----Human----
		// Paladin: 圣骑士
		// 神圣之光
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHhb"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityHolyLight(handleId, alias)));
		// 神圣护甲
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHds"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityDivineShield(handleId, alias)));
		// 专注光环 为周围友军提供一定额外的护甲。|n|n|cffffcc00等级 1|r - 增加<AHad,DataA1>点的护甲。|n|cffffcc00等级 2|r - 增加<AHad,DataA2>点的护甲。|n|cffffcc00等级 3|r - 增加<AHad,DataA3>点的护甲。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHad"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityDevotion(handleId, alias, alias)));
		// 复活
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHre"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityResurrect(handleId, alias)));
		// Archmage 大魔法师
		// 召唤水元素
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHwe"), new CAbilityTypeDefinitionSpellBase(
				(handleId, alias) -> new CAbilitySummonWaterElemental(handleId, alias)));
		// 暴风雪 能召唤出若干次冰片攻击，对目标区域内的单位造成一定的伤害。|n|n|cffffcc00等级 1|r -<AHbz,DataA1>次攻击，每次造成<AHbz,DataB1>点的伤害。|n|cffffcc00等级 2|r -<AHbz,DataA2>次攻击，每次造成<AHbz,DataB2>点的伤害。|n|cffffcc00等级 3|r -<AHbz,DataA3>次攻击，每次造成<AHbz,DataB3>点的伤害。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHbz"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityBlizzard(handleId, alias)));
		// 辉煌光辉 能加快周围友军单位的魔法值恢复速度。|n|n|cffffcc00等级 1|r -能缓慢地加快周围友军的魔法值恢复速度。|n|cffffcc00等级 2|r -能稍快地加快周围友军的魔法值恢复速度。|n|cffffcc00等级 3|r -能迅速地加快周围友军的魔法值恢复速度。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHab"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityBrilliance(handleId, alias, alias)));
		// 群体传送 将<AHmt,DataA1>个单位（包括大魔法师在内）传送到一个友军单位或者建筑物旁边。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHmt"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityMassTeleport(handleId, alias)));
		// Mountain King: 山丘之王
		// 风暴之锤 向目标投掷一巨大的魔法锤，对其造成一定伤害并使其处于眩晕状态。|n|n|cffffcc00等级 1|r - <AHtb,DataA1>点伤害，<AHtb,Dur1>秒眩晕状态。|n|cffffcc00等级 2|r - <AHtb,DataA2>点伤害，<AHtb,Dur2>秒眩晕状态。|n|cffffcc00等级 3|r - <AHtb,DataA3>点伤害，<AHtb,Dur3>秒眩晕状态。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHtb"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityThunderBolt(handleId, alias)));
		// 雷霆一击 重击地面，对周围的地面单位造成伤害并减慢其移动速度和攻击速度。|n|n|cffffcc00等级 1|r - <AHtc,DataA1>点伤害，<AHtc,DataC1,%>%的移动速度，<AHtc,DataD1,%>%的攻击速度。|n|cffffcc00等级 2|r - <AHtc,DataA2>点伤害，<AHtc,DataC2,%>%的移动速度，<AHtc,DataD2,%>%的攻击速度。|n|cffffcc00等级 3|r - <AHtc,DataA3>点伤害，<AHtc,DataC3,%>%的移动速度，<AHtc,DataD3,%>%的攻击速度。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHtc"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityThunderClap(handleId, alias)));
		// 霹雳闪电 (中立敌对) 对敌人投掷出一道霹雳闪电将其击晕。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANfb"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityThunderBolt(handleId, alias)));
		// 天神下凡 激活该技能能提高山丘之王<AHav,DataA1>点的护甲，<AHav,DataB1>点的生命值，<AHav,DataC1>点的攻击力并使其对魔法免疫。|n持续<AHav,Dur1>秒。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHav"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityAvatar(handleId, alias)));

		// Blood Mage: 血法师
		// 凤凰火焰
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Apxf"), new CAbilityTypeDefinitionPhoenixFire());
		// 召唤 火凤凰
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHpx"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilitySummonPhoenix(handleId, alias)));

		// ----Orc---- 兽族
		// 野兽幽魂 召唤出<AOsf,DataB1>头幽狼来为你战斗。|n持续<AOsf,Dur1>秒。|n|n|cffffcc00等级 1|r -<osw1,realHP>点生命值，<osw1,mindmg1>-<osw1,maxdmg1>点的攻击力。|n|cffffcc00等级 2|r -<osw2,realHP>点生命值，<osw2,mindmg1>-<osw2,maxdmg1>点攻击力，且具有致命一击技能。|n|cffffcc00等级 3|r -<osw3,realHP>点生命值，<osw3,mindmg1>-<osw3,maxdmg1>点攻击力，且具有致命一击和隐形技能。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AOsf"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityFeralSpirit(handleId, alias)));
		// 闪电链
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AOcl"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityChainLightning(handleId, alias)));

		// Tauren Chieftain 牛头人酋长
		// 战争践踏 重击地面，对周围的地面单位造成一定的伤害。|n|n|cffffcc00等级 1|r - <AOws,DataA1>点伤害，<AOws,Dur1>秒眩晕效果。|n|cffffcc00等级 2|r - <AOws,DataA2>点伤害，<AOws,Dur2>秒眩晕效果。|n|cffffcc00等级 3|r - <AOws,DataA3>点伤害，<AOws,Dur3>秒眩晕效果。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AOws"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityWarStomp(handleId, alias)));

		// Burrow: 地洞
		// 货物保持 (兽族地洞) 吞噬货物使单位能够容纳别的单位，可以配合装载类技能和卸载类技能的使用。该技能能使单位失去攻击能力直到装载其他单位
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Abun"), new CAbilityTypeDefinitionCargoHoldBurrow());
		// 卸载苦工 使得地洞内的苦工重新回到自己的工作岗位上。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Astd"), new CAbilityTypeDefinitionStandDown());

		// ----Night Elf---- 暗夜精灵
		// Keeper 丛林守护者
		// 自然之力 将一定范围内的树木转化成树人，每个树人具有<efon,realHP>点的生命值和<efon,mindmg1>-<efon,maxdmg1>点的攻击力。可以学会自然之祝福能力。|n|n|cffffcc00能攻击地面单位。|r|n|n|cffffcc00等级 1|r - 召唤<AEfn,DataA1>个树人，持续时间<AEfn,Dur1>秒。|n|cffffcc00等级 2|r - 召唤<AEfn,DataA2>个树人，持续时间<AEfn,Dur2>秒。|n|cffffcc00等级 3|r - 召唤<AEfn,DataA3>个树人，持续时间<AEfn,Dur3>秒。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AEfn"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityForceOfNature(handleId, alias)));

		// Demon Hunter: 恶魔猎手
		// 献祭 让恶魔猎手处于火焰的包围之中，并对周围的敌方地面单位造成一定的伤害。|n该技能会持续地消耗魔法值。|n|n|cffffcc00等级 1|r - 每秒<AEim,DataA1>点的伤害。|n|cffffcc00等级 2|r - 每秒<AEim,DataA2>点的伤害。|n|cffffcc00等级 3|r - 每秒<AEim,DataA3>点的伤害。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AEim"), new CAbilityTypeDefinitionImmolation());
		//法力燃烧 射出一道能量波来消耗掉目标单位一定的魔法值，目标单位的魔法值在燃烧的过程中,也会对其造成同等数量的伤害值。|n|n|cffffcc00等级 1|r - 消耗掉目标<AEmb,DataA1>点魔法。|n|cffffcc00等级 2|r - 消耗掉目标<AEmb,DataA2>点魔法。|n|cffffcc00等级 3|r - 消耗掉目标<AEmb,DataA3>点魔法。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AEmb"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityManaBurn(handleId, alias)));

		// Moon Priestess 月之女祭司
		// 侦察 能召唤出一头用来侦察地图的猫头鹰。|n能看见隐形单位。|n|n|cffffcc00等级 1|r -消耗<AEst,Cost1>点魔法值来召唤出一头猫头鹰。|n|cffffcc00等级 2|r -消耗<AEst,Cost2>点魔法值来召唤出一头猫头鹰。|n|cffffcc00等级 3|r -消耗<AEst,Cost3>点魔法值来召唤出一头猫头鹰。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AEst"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilitySummonOwlScout(handleId, alias)));

		// Warden 守望者
		// 闪烁 能让守望者瞬间移动一段距离，从而逃离战场或者快速加入战斗。|n|n|cffffcc00等级 1|r -<AEbl,Cool1>秒魔法施放间隔时间，消耗<AEbl,Cost1>点魔法。|n|cffffcc00等级 2|r -<AEbl,Cool2>秒魔法施放间隔时间，消耗<AEbl,Cost2>点魔法。|n|cffffcc00等级 3|r -<AEbl,Cool3>秒魔法施放间隔时间，消耗<AEbl,Cost3>点魔法。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AEbl"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityBlink(handleId, alias)));

		// ----Undead---- 亡灵
		// Death Knight 死亡骑士
		// 死亡缠绕 能治疗友军的某个不死单位或者伤害敌人的某个单位。|n|n|cffffcc00等级 1|r - 恢复<AUdc,DataA1>点生命值。|n|cffffcc00等级 2|r - 恢复<AUdc,DataA2>点生命值。|n|cffffcc00等级 3|r - 恢复<AUdc,DataA3>点生命值。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AUdc"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityDeathCoil(handleId, alias)));
		// 死亡契约 杀死一个友军单位，将其一定百分比的生命值转成死亡骑士的生命值。|n|n|cffffcc00等级 1|r - 转化<AUdp,DataB1,%>%。|n|cffffcc00等级 2|r -转化 <AUdp,DataB2,%>%。|n|cffffcc00等级 3|r - 转化<AUdp,DataB3,%>%。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AUdp"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityDeathPact(handleId, alias)));

		// Light 巫妖
		// 黑暗仪式 牺牲一个友军单位来将其一定百分比的生命值转化成巫妖的魔法值。|n|n|cffffcc00等级 1|r - 转化<AUdr,DataA1,%>%的生命值。|n|cffffcc00等级 2|r - 转化<AUdr,DataA2,%>%的生命值。|n|cffffcc00等级 3|r - 转化<AUdr,DataA3,%>%的生命值。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AUdr"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityDarkRitual(handleId, alias)));

		// Entangled Mine:
		//  (缠绕金矿)  让某个小精灵进入金矿。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aenc"),
				new CAbilityTypeDefinitionCargoHoldEntangledMine());
		// 缠绕金矿 在小精灵采集金矿之前，你必须先将金矿缠绕。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aent"), new CAbilityTypeDefinitionSpellBase(
				(handleId, alias) -> new CAbilityEntangleGoldMine(handleId, alias)));
		// 缠绕金矿技能
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aegm"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityEntangledMine(handleId, alias, alias)));

		// Ancients: 远古守护者
		// 吞食树木 吞食一棵树木以在<Aeat,Dur1>秒内恢复<Aeat,DataC1>点的生命值。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aeat"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityEatTree(handleId, alias)));

		// Moon Well 月亮井
		// 补充魔法和生命值 恢复一个目标单位的魔法和生命值。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Ambt"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityMoonWell(handleId, alias)));
		// ----Neutral---- 中立
		// Dark Ranger: 黑暗游侠
		// 符咒 控制某个敌方单位。|n符咒不能被用在英雄和等级高于<ANch,DataA1>的中立单位上。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANch"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityCharm(handleId, alias)));
		// 命令物品
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIco"), // Item Command
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityCharm(handleId, alias)));
		// Tinker
		// 火箭群 对某个区域用火箭进行攻击，使目标在<ANcs,Dur1> 秒内处于昏晕状态，并对其造成一定程度的伤害。|n|n|cffffcc00等级 1|r - 35 攻击力。|n|cffffcc00等级 2|r - 65 攻击力。|n|cffffcc00等级 3|r - 100 攻击力。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANcs"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityClusterRockets(handleId, alias)));
		// 工厂
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANfy"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityFactory(handleId, alias, alias)));
		// 口袋工厂 建造一座能自动生成地精的工厂。这些地精都是人工地精。它们是强大的攻击者，在阵亡之后还能发生爆炸从而对周围的造成一定的伤害。|n|n|cffffcc00等级 1|r – 爆炸具有<Asdg,DataB1> 攻击力。|n|cffffcc00等级 2|r – 爆炸具有<Asd2,DataB1> 攻击力。|n|cffffcc00等级 3|r – 爆炸具有<Asd3,DataB1> 攻击力。|n工厂持续<ANsy,Dur3> 秒。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANsy"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityPocketFactory(handleId, alias)));
		//  (地精工兵) 对一定区域造成<Asds,DataB1>点伤害。对付建筑物和数目特别地有效。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Asds"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityKaboom(handleId, alias)));

		// Beastmaster 驯兽师
		// 召唤熊 召唤一头威力强大的熊来攻击你的敌人。|n持续<ANsg,Dur1>秒。|n|n|cffffcc00等级 1|r - <ngz1,realHP>点生命值，<ngz1,mindmg1>到<ngz1,maxdmg1>点攻击力。|n|n|cffffcc00等级 2|r - <ngz2,realHP>点生命值，<ngz2,mindmg1>到<ngz2,maxdmg1>点攻击力，具有重击技能。|n|n|cffffcc00等级 3|r - <ngz3,realHP>点生命值，<ngz3,mindmg1>到<ngz3,maxdmg1>点攻击力，具有重击和闪烁的技能。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANsg"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilitySummonGrizzly(handleId, alias)));
		// 召唤豪猪 召唤一只愤怒的豪猪来为你作战。|n持续<ANsq,Dur1>秒。|n|n|cffffcc00等级 1|r - <nqb1,realHP>点生命值, <nqb1,mindmg1>到<nqb1,maxdmg1>点攻击力。|n|n|cffffcc00等级 2|r - <nqb2,realHP>点生命值，<nqb2,mindmg1>到 <nqb2,maxdmg1>点攻击力，有狂热技能。|n|n|cffffcc00等级 3|r - <nqb3,realHP>点生命值，<nqb3,mindmg1>到<nqb3,maxdmg1>点区域伤害，有狂热技能。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANsq"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilitySummonQuilbeast(handleId, alias)));
		// 召唤战鹰 召唤一只骄傲的战鹰来侦察敌人|n持续<ANsw,Dur1>秒。|n|n|cffffcc00等级 1|r - <nwe1,realHP>点生命值，有真实视域技能。|n|cffffcc00等级 2|r - <nwe2,realHP>点生命值，<nwe2,mindmg1>到<nwe2,maxdmg1>点攻击力，有真实视域技能。|n|cffffcc00等级 3|r - <nwe3,realHP>点生命值，<nwe3,mindmg1>到<nwe3,maxdmg1>点攻击力，有真实视域技能并且隐形。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANsw"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilitySummonHawk(handleId, alias)));

		// 冰冻冷箭 每次攻击带有冰冻效果，使敌人单位减慢攻击和移动。|n|n|cffffcc00等级 1|r - <AHca,DataB1,%>%攻击速度，<AHca,DataC1,%>%移动速度，持续<AHca,Dur1>秒。|n|cffffcc00等级 2|r - <AHca,DataB2,%>%攻击速度，<AHca,DataC2,%>%移动速度，持续<AHca,Dur2>秒。|n|cffffcc00等级 3|r - <AHca,DataB3,%>%攻击速度，<AHca,DataC3,%>%移动速度，持续<AHca,Dur3>秒。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHca"), new CAbilityTypeDefinitionColdArrows());
		// 金矿能力
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Agld"), new CAbilityTypeDefinitionGoldMine());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Agl2"), new CAbilityTypeDefinitionGoldMineOverlayed());
		// 闹鬼金矿技能
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Abgm"), new CAbilityTypeDefinitionBlightedGoldMine());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Abli"), new CAbilityTypeDefinitionBlight());
		//  采集(侍僧采集黄金)
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aaha"), new CAbilityTypeDefinitionAcolyteHarvest());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Artn"), new CAbilityTypeDefinitionReturnResources());
		//  采集(黄金和木材)
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Ahar"), new CAbilityTypeDefinitionHarvest());
		// 采集 (小精灵能采集黄金和木材)
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Awha"), new CAbilityTypeDefinitionWispHarvest());
		// 采集 (采集木材)
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Ahrl"), new CAbilityTypeDefinitionHarvestLumber());
		// 通魔 可作为大多数主动技能的模板，可以被魔法护盾护身符抵挡
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANcl"), new CAbilityTypeDefinitionChannelTest());
		// 腐臭蜂群 放出一群蝙蝠和昆虫对一线上的敌人造成一定的伤害。|n|n|cffffcc00等级 1|r - 对每个单位造成<AUcs,DataA1>点的伤害。|n|cffffcc00等级 2|r - 对每个单位造成<AUcs,DataA2>点的伤害。|n|cffffcc00等级 3|r - 对每个单位造成<AUcs,DataA3>点的伤害。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AUcs"), new CAbilityTypeDefinitionCarrionSwarmDummy());
		// 物品栏  (英雄)
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AInv"), new CAbilityTypeDefinitionInventory());
		// 修理 修理建筑物和机械单位，需要消耗资源。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Arep"), new CAbilityTypeDefinitionHumanRepair());
		// 更新
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aren"), new CAbilityTypeDefinitionRepair());
		// 恢复 使得侍僧能修复建筑物和机械单位。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Arst"), new CAbilityTypeDefinitionRepair());
		// 无敌的 (中立)
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Avul"), new CAbilityTypeDefinitionInvulnerable());
		// 商店购买物品
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Apit"), new CAbilityTypeDefinitionShopPurchaseItem());
		// 选择英雄
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aneu"), new CAbilityTypeDefinitionNeutralBuilding());
		// 共享商店，联盟建筑物。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aall"), new CAbilityTypeDefinitionShopSharing());
		//this.codeToAbilityTypeDefinition.put(War3ID.fromString("Acoi"), new CAbilityTypeDefinitionCoupleInstant());
		this.codeToAbilityTypeDefinition.put(CAbilityItemHeal.CODE, new CAbilityTypeDefinitionItemHeal());
		this.codeToAbilityTypeDefinition.put(CAbilityItemManaRegain.CODE, new CAbilityTypeDefinitionItemManaRegain());
		// 增加攻击力的物品 攻击之抓 +3
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIat"), new CAbilityTypeDefinitionItemAttackBonus());
		// 增加攻击力的物品 攻击之抓 +10
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIab"), new CAbilityTypeDefinitionItemStatBonus());
		// 能提高智力的物品 智力之书
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIim"),
				new CAbilityTypeDefinitionItemPermanentStatGain());
		// 能增加力量的物品
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIsm"),
				new CAbilityTypeDefinitionItemPermanentStatGain());
		// 能增加敏捷度的物品
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIam"),
				new CAbilityTypeDefinitionItemPermanentStatGain());
		// 能提高英雄三个属性的物品
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIxm"),
				new CAbilityTypeDefinitionItemPermanentStatGain());
		// 能增加魔法回复速度的物品 （较小的） 死亡面罩
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIde"), new CAbilityTypeDefinitionItemDefenseBonus());
		// 能增加血量回复速度的物品
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIml"), new CAbilityTypeDefinitionItemLifeBonus());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AImm"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityItemManaBonus(handleId, alias)));
		// 能召唤骷髅战士的物品
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIfs"), new CAbilityTypeDefinitionSpellBase(
				(handleId, alias) -> new CAbilityItemFigurineSummon(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AImi"), new CAbilityTypeDefinitionSpellBase(
				(handleId, alias) -> new CAbilityItemPermanentLifeGain(handleId, alias)));
		// 能获取经验值的物品
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIem"), new CAbilityTypeDefinitionSpellBase(
				(handleId, alias) -> new CAbilityItemExperienceGain(handleId, alias)));
		// 能提高等级的物品
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIlm"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityItemLevelGain(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Acar"), new CAbilityTypeDefinitionCargoHold());
		// 装载  (地精飞艇) 装载一个指定的友方地面单位。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aloa"), new CAbilityTypeDefinitionLoad());
		// 卸载  (地精飞艇) 在指定区域卸载全部单位。
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Adro"), new CAbilityTypeDefinitionDrop());
		// 立刻卸载  (被缠绕的金矿)
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Adri"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityDropInstant(handleId, alias, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aroo"), new CAbilityTypeDefinitionRoot());

		System.err.println("========================================================================");
		System.err.println("Starting to load ability builder");
		System.err.println("========================================================================");

		AbilityBuilderParserUtil.loadAbilityBuilderFiles(behavior -> {
			if (behavior.getType().equals(AbilityBuilderType.TEMPLATE)) {
				for (AbilityBuilderDupe dupe : behavior.getIds()) {
					this.codeToAbilityTypeDefinition.put(War3ID.fromString(dupe.getId()),
							new CAbilityTypeDefinitionAbilityTemplateBuilder(behavior));
				}
			} else {
				for (AbilityBuilderDupe dupe : behavior.getIds()) {
					AbilityBuilderConfiguration config = new AbilityBuilderConfiguration(behavior, dupe);
					this.codeToAbilityTypeDefinition.put(War3ID.fromString(config.getId()),
							config.createDefinition());
				}
			}
		});

		System.err.println("========================================================================");
		System.err.println("registered abilities");
		System.err.println("========================================================================");

	}

    // 注册JASS类型能力定义
	public void registerJassType(final War3ID war3id, final CAbilityTypeJassDefinition whichAbilityType) {
		this.codeToAbilityTypeDefinition.put(war3id, whichAbilityType);
	}

    // 获取指定别名的能力类型
	public CAbilityType<?> getAbilityType(final War3ID alias) {
		// 尝试从缓存中获取别名对应的能力类型
		CAbilityType<?> abilityType = this.aliasToAbilityType.get(alias);

		// 如果缓存中没有找到对应的能力类型
		if (abilityType == null) {
			// 从能力数据中获取别名对应的游戏对象
			final GameObject gameObject = this.abilityData.get(alias);

			// 如果能力数据中也没有找到对应的游戏对象，则返回 null
			if (gameObject == null) {
				return null;
			}

			// 从游戏对象的 SLK 标签中读取代码
			final War3ID code = War3ID.fromString(gameObject.readSLKTag("code"));

			// 从能力类型定义中获取代码对应的能力类型定义
			final CAbilityTypeDefinition abilityTypeDefinition = this.codeToAbilityTypeDefinition.get(code);

			// 如果找到了对应的能力类型定义
			if (abilityTypeDefinition != null) {
				// 根据别名和游戏对象创建新的能力类型实例
				abilityType = abilityTypeDefinition.createAbilityType(alias, gameObject);

				// 将新创建的能力类型实例添加到缓存中
				this.aliasToAbilityType.put(alias, abilityType);
			}
		}

		// 返回最终获取到的能力类型
		return abilityType;

	}

    // 获取英雄所需的能力等级
	public int getHeroRequiredLevel(final CSimulation game, final War3ID alias, final int currentLevelOfAbility) {
		// TODO maybe use CAbilityType for this to avoid hashtable lookups and just do
		// fast symbol table resolution.
		// (i.e. like all other fields of CAbilityType). For now I didn't bother because
		// I wanted to just have this working.

		// 获取别名为alias的游戏对象的可变实例
		final GameObject mutableGameObject = this.abilityData.get(alias);
		// 获取游戏对象的字段"REQUIRED_LEVEL_SKIP"的值，如果不存在则默认为0
		int levelSkip = mutableGameObject.getFieldAsInteger(AbilityFields.REQUIRED_LEVEL_SKIP, 0);
		// 如果levelSkip为0，则使用游戏常量中的默认值
		if (levelSkip == 0) {
			levelSkip = game.getGameplayConstants().getHeroAbilityLevelSkip();
		}
		// 获取游戏对象的字段"REQUIRED_LEVEL"的值，如果不存在则默认为0
		final int baseRequiredLevel = mutableGameObject.getFieldAsInteger(AbilityFields.REQUIRED_LEVEL, 0);
		// 计算并返回能力的基础所需等级加上因当前能力等级而跳过的等级总数
		return baseRequiredLevel + (currentLevelOfAbility * levelSkip);
	}

    // 创建一个新的能力实例
	public CAbility createAbility(final War3ID abilityId, final int handleId) {
		// 获取能力类型，根据abilityId
		final CAbilityType<?> abilityType = getAbilityType(abilityId);
		// 如果能力类型不为空，则创建并返回对应的能力实例
		if (abilityType != null) {
			return abilityType.createAbility(handleId);
		}
		// 如果能力类型为空，则返回一个不做任何事情的默认能力实例
		return new CAbilityGenericDoNothing(abilityId, abilityId, handleId);

	}
}

