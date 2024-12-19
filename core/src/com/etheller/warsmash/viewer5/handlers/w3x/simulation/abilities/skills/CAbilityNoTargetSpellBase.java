package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills.CBehaviorNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
// 抽象技能基类--无目标
public abstract class CAbilityNoTargetSpellBase extends CAbilitySpellBase {
    // 定义一个无目标技能行为对象
    private CBehaviorNoTargetSpellBase behavior;

    /**
     * 构造函数，初始化无目标技能基类
     * @param handleId 技能句柄ID
     * @param alias 技能别名
     */
    public CAbilityNoTargetSpellBase(int handleId, War3ID alias) {
        super(handleId, alias);
    }

    /**
     * 当技能被添加到游戏中时的操作
     * @param game 游戏模拟对象
     * @param unit 施法单位
     */
    @Override
    public void onAdd(final CSimulation game, final CUnit unit) {
        // 初始化无目标技能行为
        this.behavior = new CBehaviorNoTargetSpellBase(unit, this);
    }

    // 以下三个begin方法重载均返回null，因为是无目标技能

    /**
     * 开始施放技能，无目标版本
     * @param game 游戏模拟对象
     * @param caster 施法者单位
     * @param orderId 命令ID
     * @return 技能行为对象，这里返回null
     */
    @Override
    public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
        return null;
    }

    /**
     * 开始施放技能，无目标版本
     * @param game 游戏模拟对象
     * @param caster 施法者单位
     * @param orderId 命令ID
     * @param point 目标点
     * @return 技能行为对象，这里返回null
     */
    @Override
    public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
            final AbilityPointTarget point) {
        return null;
    }

    /**
     * 开始施放无目标技能
     * @param game 游戏模拟对象
     * @param caster 施法者单位
     * @param orderId 命令ID
     * @return 技能行为对象，这里返回重置后的无目标技能行为
     */
    @Override
    public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
        return this.behavior.reset();
    }

    // 以下三个innerCheckCanTarget方法用于检查目标是否合法，无目标技能均认为目标合法

    /**
     * 检查技能是否可以作用于某个目标
     * @param game 游戏模拟对象
     * @param unit 施法单位
     * @param orderId 命令ID
     * @param target 目标对象
     * @param receiver 目标检查接收器
     */
    @Override
    protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
            AbilityTargetCheckReceiver<CWidget> receiver) {
        receiver.orderIdNotAccepted();
    }

    /**
     * 检查技能是否可以作用于某个点目标
     * @param game 游戏模拟对象
     * @param unit 施法单位
     * @param orderId 命令ID
     * @param target 点目标对象
     * @param receiver 目标检查接收器
     */
    @Override
    protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
            AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
        receiver.orderIdNotAccepted();
    }

    /**
     * 检查无目标技能是否可以施放
     * @param game 游戏模拟对象
     * @param unit 施法单位
     * @param orderId 命令ID
     * @param receiver 目标检查接收器
     */
    @Override
    protected void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
            AbilityTargetCheckReceiver<Void> receiver) {
        receiver.targetOk(null);
    }

}
