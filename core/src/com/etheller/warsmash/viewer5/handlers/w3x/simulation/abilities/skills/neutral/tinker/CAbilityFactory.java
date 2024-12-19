package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.tinker;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPassiveSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimedLife;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
// 工厂
public class CAbilityFactory extends CAbilityPassiveSpellBase {
    // 定义生成单位的ID
    private War3ID spawnUnitId;
    // 定义牵引范围
    private float leashRange;
    // 定义生成间隔
    private float spawnInterval;
    // 定义增益效果的ID
    private War3ID buffId;

    // 记录上一次生成单位的游戏刻度
    private int lastSpawnTick;

    /**
     * 构造函数
     * @param handleId 技能句柄ID
     * @param code 技能代码
     * @param alias 技能别名
     */
    public CAbilityFactory(final int handleId, final War3ID code, final War3ID alias) {
        super(handleId, code, alias);
    }

    /**
     * 从编辑器中填充技能数据
     * @param worldEditorAbility 编辑器中的技能对象
     * @param level 技能等级
     */
    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        this.spawnUnitId = War3ID.fromString(worldEditorAbility.getFieldAsString(AbilityFields.UNIT_ID + level, 0));
        this.leashRange = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);
        this.spawnInterval = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
        this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
    }

    /**
     * 每一游戏刻度调用的方法
     * @param game 模拟游戏对象
     * @param factory 生成单位的工厂单位
     */
    @Override
    public void onTick(final CSimulation game, final CUnit factory) {
        final int gameTurnTick = game.getGameTurnTick();
        // 检查是否达到了生成新单位的时间间隔
        if (gameTurnTick >= (lastSpawnTick
                + (int) (StrictMath.ceil(this.spawnInterval / WarsmashConstants.SIMULATION_STEP_TIME)))) {

            final float facing = factory.getFacing();
            final float facingRad = (float) StrictMath.toRadians(facing);
            // 计算新单位的生成位置
            final float x = factory.getX() + ((float) StrictMath.cos(facingRad) * getAreaOfEffect());
            final float y = factory.getY() + ((float) StrictMath.sin(facingRad) * getAreaOfEffect());

            // 生成新单位
            final CUnit spawnedUnit = game.createUnitSimple(this.spawnUnitId, factory.getPlayerIndex(), x, y, facing);
            game.unitSoundEffectEvent(factory, getAlias());
            spawnedUnit.addClassification(CUnitClassification.SUMMONED);
            // 给新单位添加增益效果
            spawnedUnit.add(game,
                    new CBuffTimedLife(game.getHandleIdAllocator().createId(), this.buffId, getDuration(), false));
            // 如果有集结点，则命令新单位前往集结点
            final AbilityTarget rallyPoint = factory.getRallyPoint();
            if (rallyPoint != null) {
                spawnedUnit.order(game, OrderIds.smart, rallyPoint);
            }
            // 更新上一次生成单位的游戏刻度
            lastSpawnTick = gameTurnTick;
        }
    }

    // Getter 和 Setter 方法
    public War3ID getSpawnUnitId() {
        return spawnUnitId;
    }

    public void setSpawnUnitId(final War3ID spawnUnitId) {
        this.spawnUnitId = spawnUnitId;
    }

    public float getLeashRange() {
        return leashRange;
    }

    public void setLeashRange(final float leashRange) {
        this.leashRange = leashRange;
    }

    public float getSpawnInterval() {
        return spawnInterval;
    }

    public void setSpawnInterval(final float spawnInterval) {
        this.spawnInterval = spawnInterval;
    }

    public War3ID getBuffId() {
        return buffId;
    }

    public void setBuffId(final War3ID buffId) {
        this.buffId = buffId;
    }
}
