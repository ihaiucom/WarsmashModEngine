package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills.CBehaviorTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
// 抽象技能基类--有目标（坐标点）
public abstract class CAbilityPointTargetSpellBase extends CAbilitySpellBase {
    // 定义一个行为对象，用于处理目标法术的行为
    private CBehaviorTargetSpellBase behavior;

    // 构造函数，初始化法术的基本属性
    public CAbilityPointTargetSpellBase(int handleId, War3ID alias) {
        super(handleId, alias);
    }

    // 当法术被添加到游戏中时的操作
    @Override
    public void onAdd(final CSimulation game, final CUnit unit) {
        // 创建并初始化行为对象
        this.behavior = new CBehaviorTargetSpellBase(unit, this);
    }

    // 开始一个无目标法术的行为
    @Override
    public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
        return null;
    }

    // 开始一个有点目标法术的行为
    @Override
    public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
                            final AbilityPointTarget point) {
        // 重置行为对象并返回
        return this.behavior.reset(point);
    }

    // 开始一个无目标法术的行为（没有提供点目标）
    @Override
    public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
        return null;
    }

    // 检查是否可以针对一个控件目标
    @Override
    protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
                                      final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
        // 不接受这个命令ID
        receiver.orderIdNotAccepted();
    }

    // 检查是否可以针对一个点目标
    @Override
    protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
                                      AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
        // 如果单位可以移动或者可以到达目标点，则进一步检查法术是否可以针对目标
        if (!unit.isMovementDisabled() || unit.canReach(target, getCastRange())) {
            this.innerCheckCanTargetSpell(game, unit, orderId, target, receiver);
        } else {
            // 目标超出范围，检查失败
            receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
        }
    }

    // 进一步检查法术是否可以针对目标点
    protected void innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
                                           AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
        // 目标点检查通过
        receiver.targetOk(target);
    }

    // 检查是否可以针对无目标
    @Override
    protected void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
                                               AbilityTargetCheckReceiver<Void> receiver) {
        // 不接受这个命令ID
        receiver.orderIdNotAccepted();
    }

}
