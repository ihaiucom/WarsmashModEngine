package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractCBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

// 抽象光环Buff基类：给单位添加、移除效果；tick检测单位是否在光辉范围内，如果不在就移除
public abstract class CBuffAuraBase extends AbstractCBuff {
    // 定义光环效果的衰减时间（秒）
    private static final float AURA_BUFF_DECAY_TIME = 2.00f;
    // 将衰减时间转换为游戏刻度
    private static final int AURA_BUFF_DECAY_TIME_TICKS =
            (int) (Math.ceil(AURA_BUFF_DECAY_TIME / WarsmashConstants.SIMULATION_STEP_TIME));
    // 光环效果的渲染组件
    private SimulationRenderComponent fx;
    // 光环效果的来源单位
    private CUnit auraSourceUnit;
    // 光环效果的来源技能
    private CAbilityAuraBase auraSourceAbility;
    // 下一次检查游戏刻度的计时器
    private int nextCheckTick = 0;

    // 构造函数，初始化光环效果
    public CBuffAuraBase(int handleId, final War3ID code, War3ID alias) {
        super(handleId, code, alias);
    }

    // 设置光环效果的来源单位
    public void setAuraSourceUnit(CUnit auraSourceUnit) {
        this.auraSourceUnit = auraSourceUnit;
    }

    // 设置光环效果的来源技能
    public void setAuraSourceAbility(CAbilityAuraBase auraSourceAbility) {
        this.auraSourceAbility = auraSourceAbility;
    }

    // 当光环效果被添加到单位上时调用
    protected abstract void onBuffAdd(final CSimulation game, final CUnit unit);

    // 当光环效果从单位上移除时调用
    protected abstract void onBuffRemove(final CSimulation game, final CUnit unit);

    // 当光环效果被添加时执行的操作
    @Override
    public void onAdd(final CSimulation game, final CUnit unit) {
        onBuffAdd(game, unit);
        // 创建并显示光环效果的渲染组件
        this.fx = game.createPersistentSpellEffectOnUnit(unit, getAlias(), CEffectType.TARGET, 0);
    }

    // 当光环效果被移除时执行的操作
    @Override
    public void onRemove(final CSimulation game, final CUnit unit) {
        onBuffRemove(game, unit);
        // 移除光环效果的渲染组件
        this.fx.remove();
    }

    // 每个游戏刻度执行的操作
    @Override
    public void onTick(CSimulation game, CUnit unit) {
        int gameTurnTick = game.getGameTurnTick();
        // 检查是否到了下一次检查的时间
        if (gameTurnTick >= nextCheckTick) {
            // 如果来源单位无法到达目标单位，则移除光环效果
            if (!auraSourceUnit.canReach(unit, auraSourceAbility.getAreaOfEffect())) {
                unit.remove(game, this);
            }
            // 更新下一次检查的时间
            nextCheckTick = gameTurnTick + AURA_BUFF_DECAY_TIME_TICKS;
        }
    }

    // 当单位死亡时执行的操作
    @Override
    public void onDeath(CSimulation game, CUnit cUnit) {
        // 移除单位上的光环效果
        cUnit.remove(game, this);
    }

    // 当光环效果从队列中取消时执行的操作
    @Override
    public void onCancelFromQueue(CSimulation game, CUnit unit, int orderId) {
    }

    // 以下方法均为未实现的方法，返回null或调用接收器方法表示不支持的操作

    @Override
    public CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target) {
        return null;
    }

    @Override
    public CBehavior begin(CSimulation game, CUnit caster, int orderId, AbilityPointTarget point) {
        return null;
    }

    @Override
    public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId) {
        return null;
    }

    @Override
    public void checkCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
                               AbilityTargetCheckReceiver<CWidget> receiver) {
        receiver.notAnActiveAbility();
    }

    @Override
    public void checkCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
                               AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
        receiver.notAnActiveAbility();
    }

    @Override
    public void checkCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
                                       AbilityTargetCheckReceiver<Void> receiver) {
        receiver.notAnActiveAbility();
    }

    // 检查技能是否可以被使用
    @Override
    protected void innerCheckCanUse(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver) {
        receiver.notAnActiveAbility();
    }

    // 获取光环效果剩余持续时间
    @Override
    public float getDurationRemaining(CSimulation game, final CUnit unit) {
        return 0;
    }

    // 获取光环效果的最大持续时间
    @Override
    public float getDurationMax() {
        return 0;
    }

    // 是否显示计时生命条
    @Override
    public boolean isTimedLifeBar() {
        return false;
    }
}

