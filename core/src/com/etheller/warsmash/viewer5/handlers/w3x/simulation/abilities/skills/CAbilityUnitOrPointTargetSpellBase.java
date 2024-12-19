package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills.CBehaviorTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver.TargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
// 抽象技能基类--有目标（单位或点）
public abstract class CAbilityUnitOrPointTargetSpellBase extends CAbilitySpellBase {
    // 定义一个行为对象，用于处理目标选择和施法行为
    private CBehaviorTargetSpellBase behavior;

    // 构造函数，初始化咒语基类并设置行为对象
    public CAbilityUnitOrPointTargetSpellBase(int handleId, War3ID alias) {
        super(handleId, alias);
    }

    // 当咒语被添加到游戏中时调用，初始化行为对象
    @Override
    public void onAdd(final CSimulation game, final CUnit unit) {
        this.behavior = new CBehaviorTargetSpellBase(unit, this);
    }

    // 开始施法，针对具体的目标
    @Override
    public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
        return this.behavior.reset(target);
    }

    // 开始施法，针对点目标
    @Override
    public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
                           final AbilityPointTarget point) {
        return this.behavior.reset(point);
    }

    // 开始施法，无目标的情况
    @Override
    public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
        return null;
    }

    // 检查是否可以针对某个具体目标施法
    @Override
    protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
                                      final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		// 检查目标是否可以被选为目标
		if (target.canBeTargetedBy(game, unit, getTargetsAllowed(), receiver)) {
			// 如果单位可以移动，或者单位能够在施法范围内到达目标
			if (!unit.isMovementDisabled() || unit.canReach(target, getCastRange())) {
				// 标记目标为有效
				receiver.targetOk(target);
			}
			// 如果单位不能移动且不能在施法范围内到达目标
			else {
				// 标记目标检查失败，原因是目标超出范围
				receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
			}
		}

    }

    // 检查是否可以针对点目标施法
    @Override
    protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
                                      AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		// 检查单位是否可以移动或是否能够到达目标
		if (!unit.isMovementDisabled() || unit.canReach(target, getCastRange())) {
			// 如果可以，通知接收者目标有效
			receiver.targetOk(target);
		} else {
			// 如果不可以，通知接收者目标检查失败，目标超出范围
			receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
		}

    }

    // 检查是否可以在没有目标的情况下施法
    @Override
    protected void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
                                              AbilityTargetCheckReceiver<Void> receiver) {
        receiver.orderIdNotAccepted();
    }

}
