package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.Arrays;

import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;

/**
 * Stores some gameplay constants at runtime in a java object (symbol table) to
 * maybe be faster than a map.
 */
public class CGameplayConstants {
    // 允许攻击朝向目标的角度范围一半值
    private final float attackHalfAngle;
   // CGameplayConstants.java

    // 伤害加成表，可能用于计算不同情况下的伤害增加
    private final float[][] damageBonusTable;

    // 最大碰撞半径，可能用于物理碰撞检测
    private final float maxCollisionRadius;

    // 衰减时间，可能用于某些效果的衰减计算
    private final float decayTime;

    // 骨骼衰减时间，特定于骨骼的衰减时间
    private final float boneDecayTime;

    // 消散时间，可能用于效果或物体的消散
    private final float dissipateTime;

    // 子弹死亡时间，可能用于控制子弹的存在时间
    private final float bulletDeathTime;

    // 足够接近的范围，可能用于判断物体是否足够接近以触发某些事件
    private final float closeEnoughRange;

    // 游戏中的黎明时间，以游戏小时计
    private final float dawnTimeGameHours;

    // 游戏中的黄昏时间，以游戏小时计
    private final float duskTimeGameHours;

    // 游戏日时长，以小时计
    private final float gameDayHours;

    // 游戏日长度，可能用于计算游戏内时间流逝
    private final float gameDayLength;

    // 结构物衰减时间，可能用于建筑物或其他结构的衰减
    private final float structureDecayTime;

    // 建筑角度，可能用于建筑物的方向或布局
    private final float buildingAngle;

    // 根角度，可能与游戏中的某些根节点或基准角度有关
    private final float rootAngle;

    // 雾闪光时间，可能用于雾中的视觉效果或效果持续时间
    private final float fogFlashTime;

    // 死亡揭示半径，可能用于角色死亡时揭示周围区域
    private final float dyingRevealRadius;

    // 雾中攻击揭示半径，可能用于在雾中攻击时揭示的区域
    private final float foggedAttackRevealRadius;

    // 防御护甲，可能用于计算防御效果或减少伤害
    private final float defenseArmor;


    private final float etherealDamageBonusSpells; // 虚无法术伤害
    private final float etherealDamageBonusMagic; // 虚无魔法伤害
    private final boolean etherealDamageBonusAlly; // 虚无是否受伤害

    // 该类用于定义角色在魔法免疫下的各种抗性属性
    private final boolean magicImmuneResistsDamage;  // 魔法免疫抵抗伤害
    private final boolean magicImmuneResistsLeech;   // 魔法免疫抵抗吸取效果
    private final boolean magicImmuneResistsThorns;   // 魔法免疫抵抗荆棘效果
    private final boolean magicImmuneResistsUltimates; // 魔法免疫抵抗终极技能


    // 英雄最大复活所需金币数
    private final int heroMaxReviveCostGold;
    // 英雄最大复活所需木材数
    private final int heroMaxReviveCostLumber;
    // 英雄最大复活时间（单位：秒）
    private final int heroMaxReviveTime;

    // 英雄最大觉醒所需金币数
    private final int heroMaxAwakenCostGold;
    // 英雄最大觉醒所需木材数
    private final int heroMaxAwakenCostLumber;


    /**
    * 英雄复活时所需的初始魔法值
    */
    private final float heroReviveManaStart;

    /**
    * 英雄复活时魔法值的增长因子
    */
    private final float heroReviveManaFactor;

    /**
    * 英雄复活时生命值的增长因子
    */
    private final float heroReviveLifeFactor;

    /**
    * 英雄觉醒时所需的初始魔法值
    */
    private final float heroAwakenManaStart;

    /**
    * 英雄觉醒时魔法值的增长因子
    */
    private final float heroAwakenManaFactor;

    /**
    * 英雄觉醒时生命值的增长因子
    */
    private final float heroAwakenLifeFactor;

    // 英雄经验范围
    private final int heroExpRange;

    // 复活基础因子
    private final float reviveBaseFactor;
    // 复活等级因子
    private final float reviveLevelFactor;
    // 复活基础木材因子
    private final float reviveBaseLumberFactor;
    // 复活木材等级因子
    private final float reviveLumberLevelFactor;
    // 复活最大因子
    private final float reviveMaxFactor;
    // 复活时间因子
    private final float reviveTimeFactor;
    // 复活最大时间因子
    private final float reviveMaxTimeFactor;

    // 觉醒基础因子
    private final float awakenBaseFactor;
    // 觉醒等级因子
    private final float awakenLevelFactor;
    // 觉醒基础木材因子
    private final float awakenBaseLumberFactor;
    // 觉醒木材等级因子
    private final float awakenLumberLevelFactor;
    // 觉醒最大因子
    private final float awakenMaxFactor;


    // 最大英雄等级
    private final int maxHeroLevel;
    // 最大单位等级
    private final int maxUnitLevel;
    // 所需英雄经验值数组
    private final int[] needHeroXp;
    // 所需英雄经验值总和数组， 英雄每个等级升级需要的经验值
    private final int[] needHeroXpSum;
    // 给予英雄经验值的数组
    private final int[] grantHeroXp;
    // 给予普通经验值的数组
    private final int[] grantNormalXp;
    // 英雄经验值因子数组
    private final int[] heroFactorXp;
    // 被召唤的击杀因子
    private final float summonedKillFactor;
    // 力量攻击加成
    private final float strAttackBonus;
    // 力量生命值加成
    private final float strHitPointBonus;
    // 力量再生加成
    private final float strRegenBonus;
    // 智力法力加成
    private final float intManaBonus;
    // 智力再生加成
    private final float intRegenBonus;
    // 敏捷防御加成
    private final float agiDefenseBonus;
    // 敏捷基础防御
    private final float agiDefenseBase;
    // 敏捷移动加成
    private final int agiMoveBonus;
    // 敏捷攻击速度加成
    private final float agiAttackSpeedBonus;

    // 定义了英雄升级所需的经验值公式参数
    private final int needHeroXPFormulaA;
    private final int needHeroXPFormulaB;
    private final int needHeroXPFormulaC;

    // 定义了给予英雄的经验值公式参数
    private final int grantHeroXPFormulaA;
    private final int grantHeroXPFormulaB;
    private final int grantHeroXPFormulaC;

    // 定义了给予普通单位的经验值公式参数
    private final int grantNormalXPFormulaA;
    private final int grantNormalXPFormulaB;
    private final int grantNormalXPFormulaC;

    // 定义了英雄能力等级可以跳过的数量
    private final int heroAbilityLevelSkip;

    // 定义了是否全局经验共享
    private final boolean globalExperience;
    // 定义了是否最高等级英雄会吸取经验
    private final boolean maxLevelHeroesDrainExp;
    // 定义了摧毁建筑是否给予经验
    private final boolean buildingKillsGiveExp;

    // 定义了掉落物品的范围
    private final float dropItemRange;
    // 定义了给予物品的范围
    private final float giveItemRange;
    // 定义了拾取物品的范围
    private final float pickupItemRange;
    // 定义了典当物品的范围
    private final float pawnItemRange;
    // 定义了典当物品的比率
    private final float pawnItemRate;


    // 跟随范围，单位可能是游戏内的距离单位
    private final float followRange;
    // 建筑跟随范围，可能与普通单位跟随范围不同
    private final float structureFollowRange;
    // 跟随物品的范围，用于物品被拾取等
    private final float followItemRange;
    // 施法范围缓冲，可能是施法前的准备范围
    private final float spellCastRangeBuffer;

    // 是否相对升级成本，可能影响升级时的成本计算方式
    private final boolean relativeUpgradeCosts;
    // 最小单位速度，游戏中单位的最低移动速度
    private final float minUnitSpeed;
    // 最大单位速度，游戏中单位的最高移动速度
    private final float maxUnitSpeed;
    // 最小建筑速度，可能是指建筑物的建造速度或移动速度
    private final float minBldgSpeed;
    // 最大建筑速度，同上
    private final float maxBldgSpeed;


    // 概述：该类包含关于击中和伤害的属性。
    private final float chanceToMiss;
    // 未命中伤害减免
    private final float missDamageReduction;


    public CGameplayConstants(final DataTable parsedDataTable) {
        final Element miscData = parsedDataTable.get("Misc");
        // TODO use radians for half angle
        this.attackHalfAngle = (float) Math.toDegrees(miscData.getFieldFloatValue("AttackHalfAngle"));
        this.maxCollisionRadius = miscData.getFieldFloatValue("MaxCollisionRadius");
        this.decayTime = miscData.getFieldFloatValue("DecayTime");
        this.boneDecayTime = miscData.getFieldFloatValue("BoneDecayTime");
        this.dissipateTime = miscData.getFieldFloatValue("DissipateTime");
        this.structureDecayTime = miscData.getFieldFloatValue("StructureDecayTime");
        this.bulletDeathTime = miscData.getFieldFloatValue("BulletDeathTime");
        this.closeEnoughRange = miscData.getFieldFloatValue("CloseEnoughRange");

        this.dawnTimeGameHours = miscData.getFieldFloatValue("Dawn");
        this.duskTimeGameHours = miscData.getFieldFloatValue("Dusk");
        this.gameDayHours = miscData.getFieldFloatValue("DayHours");
        this.gameDayLength = miscData.getFieldFloatValue("DayLength");

        this.buildingAngle = miscData.getFieldFloatValue("BuildingAngle");
        this.rootAngle = miscData.getFieldFloatValue("RootAngle");

        this.fogFlashTime = miscData.getFieldFloatValue("FogFlashTime");
        this.dyingRevealRadius = miscData.getFieldFloatValue("DyingRevealRadius");
        this.foggedAttackRevealRadius = miscData.getFieldFloatValue("FoggedAttackRevealRadius");

        final CDefenseType[] defenseTypeOrder = {CDefenseType.SMALL, CDefenseType.MEDIUM, CDefenseType.LARGE,
                CDefenseType.FORT, CDefenseType.NORMAL, CDefenseType.HERO, CDefenseType.DIVINE, CDefenseType.NONE,};
        this.damageBonusTable = new float[CAttackType.values().length][defenseTypeOrder.length];
        for (int i = 0; i < CAttackType.VALUES.length; i++) {
            Arrays.fill(this.damageBonusTable[i], 1.0f);
            final CAttackType attackType = CAttackType.VALUES[i];
            String fieldName = "DamageBonus" + attackType.getDamageKey();
            if (!miscData.hasField(fieldName) && attackType == CAttackType.SPELLS) {
                fieldName = "DamageBonus" + CAttackType.MAGIC.getDamageKey();
            }
            final String damageBonus = miscData.getField(fieldName);
            final String[] damageComponents = damageBonus.split(",");
            for (int j = 0; j < damageComponents.length; j++) {
                if (damageComponents[j].length() > 0) {
                    final CDefenseType defenseType = defenseTypeOrder[j];
                    try {
                        this.damageBonusTable[i][defenseType.ordinal()] = Float.parseFloat(damageComponents[j]);
//						System.out.println(attackType + ":" + defenseType + ": " + damageComponents[j]);
                    } catch (final NumberFormatException e) {
                        throw new RuntimeException(fieldName, e);
                    }
                }
            }
        }

        this.defenseArmor = miscData.getFieldFloatValue("DefenseArmor");

        final String damageBonus = miscData.getField("EtherealDamageBonus");
        final String[] damageComponents = damageBonus.split(",");
        float magBonus = 1;
        float spellBonus = 1;
        for (int j = 0; j < damageComponents.length; j++) {
            if (j == 3) {
                if (damageComponents[j].length() > 0) {
                    try {
                        magBonus = Float.parseFloat(damageComponents[j]);
                    } catch (final NumberFormatException e) {
                        throw new RuntimeException("EtherealDamageBonus", e);
                    }
                }
            } else if (j == 5) {
                if (damageComponents[j].length() > 0) {
                    try {
                        spellBonus = Float.parseFloat(damageComponents[j]);
                    } catch (final NumberFormatException e) {
                        throw new RuntimeException("EtherealDamageBonus", e);
                    }
                }
            }
        }
        this.etherealDamageBonusMagic = magBonus;
        this.etherealDamageBonusSpells = spellBonus;
        this.etherealDamageBonusAlly = miscData.getFieldValue("EtherealDamageBonusAlly") != 0;

        this.magicImmuneResistsDamage = miscData.getFieldValue("MagicImmunesResistDamage") != 0;
        this.magicImmuneResistsLeech = miscData.getFieldValue("MagicImmunesResistLeech") != 0;
        this.magicImmuneResistsThorns = miscData.getFieldValue("MagicImmunesResistThorns") != 0;
        this.magicImmuneResistsUltimates = miscData.getFieldValue("MagicImmunesResistUltimates") != 0;

        this.globalExperience = miscData.getFieldValue("GlobalExperience") != 0;
        this.maxLevelHeroesDrainExp = miscData.getFieldValue("MaxLevelHeroesDrainExp") != 0;
        this.buildingKillsGiveExp = miscData.getFieldValue("BuildingKillsGiveExp") != 0;

        this.heroMaxReviveCostGold = miscData.getFieldValue("HeroMaxReviveCostGold");
        this.heroMaxReviveCostLumber = miscData.getFieldValue("HeroMaxReviveCostLumber");
        this.heroMaxReviveTime = miscData.getFieldValue("HeroMaxReviveTime");

        this.heroMaxAwakenCostGold = miscData.getFieldValue("HeroMaxAwakenCostGold");
        this.heroMaxAwakenCostLumber = miscData.getFieldValue("HeroMaxAwakenCostLumber");

        this.heroReviveManaStart = miscData.getFieldFloatValue("HeroReviveManaStart");
        this.heroReviveManaFactor = miscData.getFieldFloatValue("HeroReviveManaFactor");
        this.heroReviveLifeFactor = miscData.getFieldFloatValue("HeroReviveLifeFactor");
        this.heroAwakenManaStart = miscData.getFieldFloatValue("HeroAwakenManaStart");
        this.heroAwakenManaFactor = miscData.getFieldFloatValue("HeroAwakenManaFactor");
        this.heroAwakenLifeFactor = miscData.getFieldFloatValue("HeroAwakenLifeFactor");

        this.heroExpRange = miscData.getFieldValue("HeroExpRange");

        this.reviveBaseFactor = miscData.getFieldFloatValue("ReviveBaseFactor");
        this.reviveLevelFactor = miscData.getFieldFloatValue("ReviveLevelFactor");
        this.reviveBaseLumberFactor = miscData.getFieldFloatValue("ReviveBaseLumberFactor");
        this.reviveLumberLevelFactor = miscData.getFieldFloatValue("ReviveLumberLevelFactor");
        this.reviveMaxFactor = miscData.getFieldFloatValue("ReviveMaxFactor");
        this.reviveTimeFactor = miscData.getFieldFloatValue("ReviveTimeFactor");
        this.reviveMaxTimeFactor = miscData.getFieldFloatValue("ReviveMaxTimeFactor");

        this.awakenBaseFactor = miscData.getFieldFloatValue("AwakenBaseFactor");
        this.awakenLevelFactor = miscData.getFieldFloatValue("AwakenLevelFactor");
        this.awakenBaseLumberFactor = miscData.getFieldFloatValue("AwakenBaseLumberFactor");
        this.awakenLumberLevelFactor = miscData.getFieldFloatValue("AwakenLumberLevelFactor");
        this.awakenMaxFactor = miscData.getFieldFloatValue("AwakenMaxFactor");

        this.maxHeroLevel = miscData.getFieldValue("MaxHeroLevel");
        this.maxUnitLevel = miscData.getFieldValue("MaxUnitLevel");

        this.needHeroXPFormulaA = miscData.getFieldValue("NeedHeroXPFormulaA");
        this.needHeroXPFormulaB = miscData.getFieldValue("NeedHeroXPFormulaB");
        this.needHeroXPFormulaC = miscData.getFieldValue("NeedHeroXPFormulaC");
        this.grantHeroXPFormulaA = miscData.getFieldValue("GrantHeroXPFormulaA");
        this.grantHeroXPFormulaB = miscData.getFieldValue("GrantHeroXPFormulaB");
        this.grantHeroXPFormulaC = miscData.getFieldValue("GrantHeroXPFormulaC");
        this.grantNormalXPFormulaA = miscData.getFieldValue("GrantNormalXPFormulaA");
        this.grantNormalXPFormulaB = miscData.getFieldValue("GrantNormalXPFormulaB");
        this.grantNormalXPFormulaC = miscData.getFieldValue("GrantNormalXPFormulaC");

        this.needHeroXp = parseTable(miscData.getField("NeedHeroXP"), this.needHeroXPFormulaA, this.needHeroXPFormulaB,
                this.needHeroXPFormulaC, this.maxHeroLevel);
        this.needHeroXpSum = new int[this.needHeroXp.length];
        for (int i = 0; i < this.needHeroXpSum.length; i++) {
            if (i == 0) {
                this.needHeroXpSum[i] = this.needHeroXp[i];
            } else {
                this.needHeroXpSum[i] = this.needHeroXp[i] + this.needHeroXpSum[i - 1];
            }
        }
        this.grantHeroXp = parseTable(miscData.getField("GrantHeroXP"), this.grantHeroXPFormulaA,
                this.grantHeroXPFormulaB, this.grantHeroXPFormulaC, this.maxHeroLevel);
        this.grantNormalXp = parseTable(miscData.getField("GrantNormalXP"), this.grantNormalXPFormulaA,
                this.grantNormalXPFormulaB, this.grantNormalXPFormulaC, this.maxUnitLevel);
        this.heroFactorXp = parseIntArray(miscData.getField("HeroFactorXP"));
        this.summonedKillFactor = miscData.getFieldFloatValue("SummonedKillFactor");
        this.strAttackBonus = miscData.getFieldFloatValue("StrAttackBonus");
        this.strHitPointBonus = miscData.getFieldFloatValue("StrHitPointBonus");
        this.strRegenBonus = miscData.getFieldFloatValue("StrRegenBonus");
        this.intManaBonus = miscData.getFieldFloatValue("IntManaBonus");
        this.intRegenBonus = miscData.getFieldFloatValue("IntRegenBonus");
        this.agiDefenseBonus = miscData.getFieldFloatValue("AgiDefenseBonus");
        this.agiDefenseBase = miscData.getFieldFloatValue("AgiDefenseBase");
        this.agiMoveBonus = miscData.getFieldValue("AgiMoveBonus");
        this.agiAttackSpeedBonus = miscData.getFieldFloatValue("AgiAttackSpeedBonus");

        this.heroAbilityLevelSkip = miscData.getFieldValue("HeroAbilityLevelSkip");

        this.dropItemRange = miscData.getFieldFloatValue("DropItemRange");
        this.giveItemRange = miscData.getFieldFloatValue("GiveItemRange");
        this.pickupItemRange = miscData.getFieldFloatValue("PickupItemRange");
        this.pawnItemRange = miscData.getFieldFloatValue("PawnItemRange");
        this.pawnItemRate = miscData.getFieldFloatValue("PawnItemRate");

        this.followRange = miscData.getFieldFloatValue("FollowRange");
        this.structureFollowRange = miscData.getFieldFloatValue("StructureFollowRange");
        this.followItemRange = miscData.getFieldFloatValue("FollowItemRange");

        this.spellCastRangeBuffer = miscData.getFieldFloatValue("SpellCastRangeBuffer");

        this.relativeUpgradeCosts = miscData.getFieldValue("RelativeUpgradeCost") == 0;

        this.minUnitSpeed = miscData.getFieldFloatValue("MinUnitSpeed");
        this.maxUnitSpeed = miscData.getFieldFloatValue("MaxUnitSpeed");
        this.minBldgSpeed = miscData.getFieldFloatValue("MinBldgSpeed");
        this.maxBldgSpeed = miscData.getFieldFloatValue("MaxBldgSpeed");

        this.chanceToMiss = miscData.getFieldFloatValue("ChanceToMiss");
        this.missDamageReduction = miscData.getFieldFloatValue("MissDamageReduction");
    }
    // 获取攻击半角度
    public float getAttackHalfAngle() {
        return this.attackHalfAngle;
    }

    // 获取针对特定攻击类型和防御类型的伤害比率
    public float getDamageRatioAgainst(final CAttackType attackType, final CDefenseType defenseType) {
        return this.damageBonusTable[attackType.ordinal()][defenseType.ordinal()];
    }

    // 获取最大碰撞半径
    public float getMaxCollisionRadius() {
        return this.maxCollisionRadius;
    }

    // 获取衰减时间
    public float getDecayTime() {
        return this.decayTime;
    }

    // 获取骨骼衰减时间
    public float getBoneDecayTime() {
        return this.boneDecayTime;
    }

    // 获取消散时间
    public float getDissipateTime() {
        return this.dissipateTime;
    }

    // 获取子弹死亡时间
    public float getBulletDeathTime() {
        return this.bulletDeathTime;
    }

    // 获取足够接近的范围
    public float getCloseEnoughRange() {
        return this.closeEnoughRange;
    }

    // 获取游戏昼夜小时数
    public float getGameDayHours() {
        return this.gameDayHours;
    }

    // 获取游戏昼夜长度
    public float getGameDayLength() {
        return this.gameDayLength;
    }

    // 获取黎明时间对应的游戏小时数
    public float getDawnTimeGameHours() {
        return this.dawnTimeGameHours;
    }

    // 获取黄昏时间对应的游戏小时数
    public float getDuskTimeGameHours() {
        return this.duskTimeGameHours;
    }

    // 获取结构衰减时间
    public float getStructureDecayTime() {
        return this.structureDecayTime;
    }

    // 获取建筑角度
    public float getBuildingAngle() {
        return this.buildingAngle;
    }

    // 获取根部角度
    public float getRootAngle() {
        return this.rootAngle;
    }

    // 获取雾闪烁时间
    public float getFogFlashTime() {
        return fogFlashTime;
    }

    // 获取死亡揭示半径
    public float getDyingRevealRadius() {
        return dyingRevealRadius;
    }

    // 获取雾中攻击揭示半径
    public float getFoggedAttackRevealRadius() {
        return foggedAttackRevealRadius;
    }

    // 获取防御装甲值
    public float getDefenseArmor() {
        return this.defenseArmor;
    }

    // 获取魔法触发的虚体伤害加成值（咒语）
    public float getEtherealDamageBonusSpells() {
        return etherealDamageBonusSpells;
    }

    // 获取魔法触发的虚体伤害加成值（魔法）
    public float getEtherealDamageBonusMagic() {
        return etherealDamageBonusMagic;
    }

    // 判断是否对盟友的虚体伤害有加成
    public boolean isEtherealDamageBonusAlly() {
        return etherealDamageBonusAlly;
    }

    // 魔法免疫抵抗伤害
    public boolean isMagicImmuneResistsDamage() {
        return magicImmuneResistsDamage;
    }

    // 判断是否抵抗吸血魔法
    public boolean isMagicImmuneResistsLeech() {
        return magicImmuneResistsLeech;
    }

    // 判断是否抵抗荆棘魔法
    public boolean isMagicImmuneResistsThorns() {
        return magicImmuneResistsThorns;
    }

    // 判断是否抵抗最终魔法
    public boolean isMagicImmuneResistsUltimates() {
        return magicImmuneResistsUltimates;
    }


    // 此类包含与英雄能力、经验和复活等相关的方法。
    public boolean isGlobalExperience() {
        return this.globalExperience;
    }

    // 检查最大等级英雄是否会消耗经验值。
    public boolean isMaxLevelHeroesDrainExp() {
        return this.maxLevelHeroesDrainExp;
    }

    // 检查建筑击杀是否给予经验值。
    public boolean isBuildingKillsGiveExp() {
        return this.buildingKillsGiveExp;
    }

    // 获取英雄能力等级跳过的值。
    public int getHeroAbilityLevelSkip() {
        return this.heroAbilityLevelSkip;
    }

    // 获取英雄经验的范围值。
    public int getHeroExpRange() {
        return this.heroExpRange;
    }

    // 获取最大英雄等级。
    public int getMaxHeroLevel() {
        return this.maxHeroLevel;
    }

    // 获取最大单位等级。
    public int getMaxUnitLevel() {
        return this.maxUnitLevel;
    }

    // 获取召唤物击杀因子的值。
    public float getSummonedKillFactor() {
        return this.summonedKillFactor;
    }

    // 获取力量攻击加成值。
    public float getStrAttackBonus() {
        return this.strAttackBonus;
    }

    // 获取力量生命值加成值。
    public float getStrHitPointBonus() {
        return this.strHitPointBonus;
    }

    // 获取力量生命回复加成值。
    public float getStrRegenBonus() {
        return this.strRegenBonus;
    }

    // 获取智力魔法值加成值。
    public float getIntManaBonus() {
        return this.intManaBonus;
    }

    // 获取智力生命回复加成值。
    public float getIntRegenBonus() {
        return this.intRegenBonus;
    }

    // 获取敏捷防御加成值。
    public float getAgiDefenseBonus() {
        return this.agiDefenseBonus;
    }

    // 获取敏捷基础防御值。
    public float getAgiDefenseBase() {
        return this.agiDefenseBase;
    }

    // 获取敏捷移动加成值。
    public int getAgiMoveBonus() {
        return this.agiMoveBonus;
    }

    // 获取敏捷攻击速度加成值。
    public float getAgiAttackSpeedBonus() {
        return this.agiAttackSpeedBonus;
    }

    // 根据等级获取英雄经验因子的值。
    public float getHeroFactorXp(final int level) {
        return getTableValue(this.heroFactorXp, level) / 100f;
    }

    // 根据等级获取所需英雄经验值。
    public int getNeedHeroXP(final int level) {
        return getTableValue(this.needHeroXp, level);
    }

    // 根据等级获取所需英雄经验值的总和。
    public int getNeedHeroXPSum(final int level) {
        return getTableValue(this.needHeroXpSum, level);
    }

    // 根据等级获取授予的英雄经验值。
    public int getGrantHeroXP(final int level) {
        return getTableValue(this.grantHeroXp, level);
    }

    // 根据等级获取授予的普通经验值。
    public int getGrantNormalXP(final int level) {
        return getTableValue(this.grantNormalXp, level);
    }

    // 获取掉落物品的范围值。
    public float getDropItemRange() {
        return this.dropItemRange;
    }

    // 获取拾取物品的范围值。
    public float getPickupItemRange() {
        return this.pickupItemRange;
    }

    // 获取给予物品的范围值。
    public float getGiveItemRange() {
        return this.giveItemRange;
    }

    // 获取典当物品的范围值。
    public float getPawnItemRange() {
        return this.pawnItemRange;
    }

    // 获取典当物品的比率值。
    public float getPawnItemRate() {
        return this.pawnItemRate;
    }

    // 获取跟随范围的值。
    public float getFollowRange() {
        return this.followRange;
    }

    // 获取结构物体的跟随范围值。
    public float getStructureFollowRange() {
        return this.structureFollowRange;
    }

    // 获取跟随物品的范围值。
    public float getFollowItemRange() {
        return this.followItemRange;
    }

    // 获取施法范围缓冲值。
    public float getSpellCastRangeBuffer() {
        return this.spellCastRangeBuffer;
    }

    // 计算英雄复活所需的黄金成本。
    public int getHeroReviveGoldCost(final int originalCost, final int level) {
        final int goldRevivalCost = (int) (originalCost
                * (this.reviveBaseFactor + (this.reviveLevelFactor * (level - 1))));
        return Math.min(goldRevivalCost, (int) (originalCost * this.reviveMaxFactor));
    }

    // 计算英雄复活所需的木材成本。
    public int getHeroReviveLumberCost(final int originalCost, final int level) {
        final int lumberRevivalCost = (int) (originalCost
                * (this.reviveBaseLumberFactor + (this.reviveLumberLevelFactor * (level - 1))));
        return Math.min(lumberRevivalCost, (int) (originalCost * this.reviveMaxFactor));
    }

    // 计算英雄复活所需的时间。
    public int getHeroReviveTime(final int originalTime, final int level) {
        final int revivalTime = (int) (originalTime * level * this.reviveTimeFactor);
        return Math.min(revivalTime, (int) (originalTime * this.reviveMaxTimeFactor));
    }

    /**
     * 获取英雄复活时的生命值因子。
     *
     * @return 英雄复活时的生命值因子
     */
    public float getHeroReviveLifeFactor() {
        return this.heroReviveLifeFactor;
    }

    /**
     * 获取英雄复活时的魔法值因子。
     *
     * @return 英雄复活时的魔法值因子
     */
    public float getHeroReviveManaFactor() {
        return this.heroReviveManaFactor;
    }

    /**
     * 获取英雄复活时魔法值开始恢复的数值。
     *
     * @return 英雄复活时魔法值开始恢复的数值
     */
    public float getHeroReviveManaStart() {
        return this.heroReviveManaStart;
    }

    /**
     * 判断升级成本是否是相对的。
     *
     * @return 如果升级成本是相对的，则返回true，否则返回false
     */
    public boolean isRelativeUpgradeCosts() {
        return this.relativeUpgradeCosts;
    }

    /**
     * 获取最小单位速度。
     *
     * @return 最小单位速度
     */
    public float getMinUnitSpeed() {
        return minUnitSpeed;
    }

    /**
     * 获取最大单位速度。
     *
     * @return 最大单位速度
     */
    public float getMaxUnitSpeed() {
        return maxUnitSpeed;
    }

    /**
     * 获取最小建筑速度。
     *
     * @return 最小建筑速度
     */
    public float getMinBldgSpeed() {
        return minBldgSpeed;
    }

    /**
     * 获取最大建筑速度。
     *
     * @return 最大建筑速度
     */
    public float getMaxBldgSpeed() {
        return maxBldgSpeed;
    }

    /**
     * 获取未命中的几率。
     *
     * @return 未命中的几率
     */
    public float getChanceToMiss() {
        return chanceToMiss;
    }


    // 未命中伤害减免
    public float getMissDamageReduction() {
        return missDamageReduction;
    }

    /**
     * 根据等级从数组中获取对应的值。
     * 如果等级小于等于0，则返回0。
     * 如果等级大于数组长度，则将等级设置为数组长度。
     *
     * @param table 整数数组
     * @param level 等级
     * @return 对应等级的值
     */
    private static int getTableValue(final int[] table, int level) {
        if (level <= 0) {
            return 0;
        }
        if (level > table.length) {
            level = table.length;
        }
        return table[level - 1];
    }

    /*
     * This incorporates the function "f(x)" documented both on
     * http://classic.battle.net/war3/basics/heroes.shtml and also on MiscGame.txt.
     */

    /**
     * 解析字符串并根据给定的公式生成一个整数数组。
     * 字符串以逗号分隔，每个部分转换为整数存储在数组中。
     * 如果字符串部分的数量少于数组大小，则使用给定的公式计算缺失的值。
     *
     * @param txt        以逗号分隔的字符串
     * @param formulaA   公式A的系数
     * @param formulaB   公式B的系数
     * @param formulaC   公式C的常数项
     * @param tableSize  数组大小
     * @return 生成的整数数组
     */
    private static int[] parseTable(final String txt, final int formulaA, final int formulaB, final int formulaC,
                                    final int tableSize) {
        final String[] splitTxt = txt.split(",");
        final int[] result = new int[tableSize];
        for (int i = 0; i < tableSize; i++) {
            if (i < splitTxt.length) {
                result[i] = Integer.parseInt(splitTxt[i]);
            } else {
                result[i] = (formulaA * result[i - 1]) + (formulaB * i) + formulaC;
            }
        }
        return result;
    }


    /**
     * 将逗号分隔的字符串转换为整数数组。
     *
     * @param txt 逗号分隔的字符串
     * @return 转换后的整数数组
     * @throws NumberFormatException 如果字符串中的任何部分不能转换为整数，则抛出此异常
     */
    private static int[] parseIntArray(final String txt) {
        // 使用逗号分割字符串
        final String[] splitTxt = txt.split(",");
        // 创建一个与分割后字符串数组长度相同的整数数组
        final int[] result = new int[splitTxt.length];
        // 遍历分割后的字符串数组
        for (int i = 0; i < splitTxt.length; i++) {
            // 将每个字符串元素转换为整数并存入结果数组
            result[i] = Integer.parseInt(splitTxt[i]);
        }
        // 返回转换后的整数数组
        return result;
    }

}
