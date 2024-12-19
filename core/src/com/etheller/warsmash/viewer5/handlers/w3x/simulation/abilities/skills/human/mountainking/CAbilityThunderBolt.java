package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffStun;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetUnitVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
// 风暴之锤 向目标投掷一巨大的魔法锤，对其造成一定伤害并使其处于眩晕状态。|n|n|cffffcc00等级 1|r - <AHtb,DataA1>点伤害，<AHtb,Dur1>秒眩晕状态。|n|cffffcc00等级 2|r - <AHtb,DataA2>点伤害，<AHtb,Dur2>秒眩晕状态。|n|cffffcc00等级 3|r - <AHtb,DataA3>点伤害，<AHtb,Dur3>秒眩晕状态。
// 霹雳闪电 (中立敌对) 对敌人投掷出一道霹雳闪电将其击晕。
public class CAbilityThunderBolt extends CAbilityTargetSpellBase {

    // 定义伤害值
    private float damage;
    // 定义弹道速度
    private float projectileSpeed;
    // 定义是否启用弹道追踪
    private boolean projectileHomingEnabled;
    // 定义增益效果的ID
    private War3ID buffId;

    // 构造函数
    public CAbilityThunderBolt(final int handleId, final War3ID alias) {
        super(handleId, alias);
    }

    /**
     * 获取基础命令ID
     * @return 命令ID
     */
    @Override
    public int getBaseOrderId() {
        return OrderIds.thunderbolt;
    }

    /**
     * 填充数据
     * @param worldEditorAbility 世界编辑器能力
     * @param level 等级
     */
    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        damage = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
        projectileSpeed = worldEditorAbility.getFieldAsFloat(AbilityFields.PROJECTILE_SPEED, 0);
        projectileHomingEnabled = worldEditorAbility.getFieldAsBoolean(AbilityFields.PROJECTILE_HOMING_ENABLED, 0);
        this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
    }

    /**
     * 执行效果
     * @param simulation 模拟
     * @param caster 施法者
     * @param target 目标
     * @return 是否成功
     */
    @Override
    public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
        final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
        if (targetUnit != null) {
            // 创建弹道
            simulation.createProjectile(targetUnit, getAlias(), caster.getX(), caster.getY(),
                    (float) caster.angleTo(targetUnit), projectileSpeed, projectileHomingEnabled, targetUnit,
                    new CAbilityProjectileListener() {
                        @Override
                        public void onLaunch(final CSimulation game, final AbilityTarget target) {
                            // 弹道发射时的操作
                        }

                        @Override
                        public void onHit(final CSimulation game, final AbilityTarget target) {
                            final CUnit unitTarget = target.visit(AbilityTargetUnitVisitor.INSTANCE);
                            if (unitTarget != null) {
                                // 对目标造成伤害
                                unitTarget.damage(game, caster, false, true, CAttackType.SPELLS, CDamageType.LIGHTNING,
                                        CWeaponSoundTypeJass.WHOKNOWS.name(), damage);
                                // 如果目标未死亡，添加眩晕效果
                                if (!unitTarget.isDead()) {
                                    unitTarget.add(game, new CBuffStun(game.getHandleIdAllocator().createId(),
                                            getBuffId(), getDurationForTarget(unitTarget)));
                                }
                            }
                        }
                    });
        }
        return false;
    }

    // 获取伤害值
    public float getDamage() {
        return damage;
    }

    // 设置伤害值
    public void setDamage(final float damage) {
        this.damage = damage;
    }

    // 获取弹道速度
    public float getProjectileSpeed() {
        return projectileSpeed;
    }

    // 设置弹道速度
    public void setProjectileSpeed(final float projectileSpeed) {
        this.projectileSpeed = projectileSpeed;
    }

    // 是否启用弹道追踪
    public boolean isProjectileHomingEnabled() {
        return projectileHomingEnabled;
    }

    // 设置是否启用弹道追踪
    public void setProjectileHomingEnabled(final boolean projectileHomingEnabled) {
        this.projectileHomingEnabled = projectileHomingEnabled;
    }

    // 获取增益效果ID
    public War3ID getBuffId() {
        return buffId;
    }

    // 设置增益效果ID
    public void setBuffId(final War3ID buffId) {
        this.buffId = buffId;
    }
}
