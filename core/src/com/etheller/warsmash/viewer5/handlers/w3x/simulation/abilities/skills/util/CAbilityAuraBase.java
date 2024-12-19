package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.GetAbilityByRawcodeVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPassiveSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

// 抽象技能基类--光环： 检测范围内的单位符合要求的添加光辉buff
public abstract class CAbilityAuraBase extends CAbilityPassiveSpellBase {
    // 定义了光环的周期性检查时间，单位为秒
    private static final float AURA_PERIODIC_CHECK_TIME = 2.00f;
    // 将周期性检查时间转换为游戏刻度
    private static final int AURA_PERIODIC_CHECK_TIME_TICKS = (int) (Math.ceil(AURA_PERIODIC_CHECK_TIME / WarsmashConstants.SIMULATION_STEP_TIME));
    // 光环效果的buff ID
    private War3ID buffId;
    // 光环效果的渲染组件
    private SimulationRenderComponent fx;
    // 下一次区域检查的时间刻度
    private int nextAreaCheck = 0;

    // 构造函数，初始化能力的基本属性
    public CAbilityAuraBase(final int handleId, final War3ID code, final War3ID alias) {
        super(handleId, code, alias);
    }

    /**
     * 从编辑器中的能力对象填充数据到当前能力实例
     *
     * @param worldEditorAbility 编辑器中的能力对象
     * @param level 能力的等级
     */
    @Override
    public final void populateData(final GameObject worldEditorAbility, final int level) {
        // 设置buffId，通过调用AbstractCAbilityTypeDefinition类的getBuffId方法，传入worldEditorAbility和level参数
        this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);

        /**
         * 填充Aura数据，根据传入的worldEditorAbility和level参数
         *
         * @param worldEditorAbility 世界编辑器能力对象
         * @param level 当前等级
         */
        populateAuraData(worldEditorAbility, level);

    }

    /**
     * 当能力被添加到单位上时调用
     *
     * @param game 模拟游戏实例
     * @param unit 能力被添加到的单位
     */
    @Override
    public void onAdd(final CSimulation game, final CUnit unit) {
        this.fx = game.createPersistentSpellEffectOnUnit(unit, getAlias(), CEffectType.TARGET, 0);
    }

    /**
     * 当能力从单位上移除时调用
     *
     * @param game 模拟游戏实例
     * @param unit 能力被移除的单位
     */
    @Override
    public void onRemove(final CSimulation game, final CUnit unit) {
        this.fx.remove();
    }

    /**
     * 每个游戏刻度调用的方法，用于处理光环效果的周期性检查
     *
     * @param game 模拟游戏实例
     * @param source 能力的发起单位
     */
    @Override
    public void onTick(final CSimulation game, final CUnit source) {
        final int gameTurnTick = game.getGameTurnTick();
        // 如果当前游戏刻度达到了下一次区域检查的时间
        if (gameTurnTick >= nextAreaCheck) {
            // 对指定范围内的单位进行检查
            game.getWorldCollision().enumUnitsInRange(source.getX(), source.getY(), getAreaOfEffect(), (enumUnit) -> {
                // 如果单位可以被当前能力的目标选择逻辑选中
                if (enumUnit.canBeTargetedBy(game, source, getTargetsAllowed())) {
                    // 检查单位是否已经有该光环效果的buff，并根据等级决定是否添加或升级buff
                    final CLevelingAbility existingBuff = enumUnit.getAbility(GetAbilityByRawcodeVisitor.getInstance().reset(getBuffId()));
                    boolean addNewBuff = false;
                    final int level = getLevel();
                    if (existingBuff == null) {
                        addNewBuff = true;
                    } else {
                        if (existingBuff.getLevel() < level) {
                            enumUnit.remove(game, existingBuff);
                            addNewBuff = true;
                        }
                    }
                    // 如果需要添加新的buff
                    if (addNewBuff) {
                        final CBuffAuraBase buff = createBuff(game.getHandleIdAllocator().createId(), source, enumUnit);
                        buff.setAuraSourceUnit(source);
                        buff.setAuraSourceAbility(this);
                        buff.setLevel(game, source, level);
                        enumUnit.add(game, buff);
                    }
                }
                return false;
            });
            // 更新下一次区域检查的时间
            nextAreaCheck = gameTurnTick + AURA_PERIODIC_CHECK_TIME_TICKS;
        }
    }

    /**
     * 创建一个新的光环buff实例
     *
     * @param handleId 新buff的句柄ID
     * @param source 能力的发起单位
     * @param enumUnit 被检查的单位
     * @return 新创建的CBuffAuraBase实例
     */
    protected abstract CBuffAuraBase createBuff(int handleId, CUnit source, CUnit enumUnit);

    /**
     * 从编辑器中的能力对象填充光环相关的数据
     *
     * @param worldEditorAbility 编辑器中的能力对象
     * @param level 能力的等级
     */
    public abstract void populateAuraData(GameObject worldEditorAbility, int level);

    // 获取光环效果的buff ID
    public War3ID getBuffId() {
        return buffId;
    }

    // 设置光环效果的buff ID
    public void setBuffId(final War3ID buffId) {
        this.buffId = buffId;
    }
}
