package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentModel;
// 群体传送 将<AHmt,DataA1>个单位（包括大魔法师在内）传送到一个友军单位或者建筑物旁边。
public class CAbilityMassTeleport extends CAbilityTargetSpellBase {

	private int numberOfUnitsTeleported; // 被传送的单位数量
	private boolean useTeleportClustering; // 是否使用传送聚集
	private float castingDelay; // 施法延迟
	private float areaOfEffect; // 施法范围

	private int channelEndTick; // 施法结束的时间戳
	private SimulationRenderComponentModel sourceAreaEffectRenderComponent; // 源区域效果渲染组件
	private SimulationRenderComponentModel targetAreaEffectRenderComponent; // 目标区域效果渲染组件

	/**
	 * CAbilityMassTeleport构造函数
	 * @param handleId 处理ID
	 * @param alias 技能别名
	 */
	public CAbilityMassTeleport(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.massteleport; // 返回技能的基础指令ID
	}

	@Override
	/**
	 * 填充技能数据
	 * @param worldEditorAbility 世界编辑器中的技能对象
	 * @param level 技能等级
	 */
	public void populateData(final GameObject worldEditorAbility, final int level) {
		// 从世界编辑器能力中获取并设置传送的单位数量
		numberOfUnitsTeleported = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_A + level, 0);

		// 获取并设置是否使用传送聚类
		useTeleportClustering = worldEditorAbility.getFieldAsBoolean(AbilityFields.DATA_C + level, 0);

		// 获取并设置施法延迟
		castingDelay = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);

		// 获取并设置影响区域大小
		areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);

	}

	@Override
	/**
	 * 执行技能效果
	 * @param simulation 游戏模拟
	 * @param caster 施法者单位
	 * @param target 目标
	 * @return 返回是否继续施法
	 */
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		// 设置频道结束的刻度，计算方式为当前游戏刻度加上施法延迟除以模拟步进时间的向上取整
		this.channelEndTick = simulation.getGameTurnTick()
				+ (int) StrictMath.ceil(castingDelay / WarsmashConstants.SIMULATION_STEP_TIME);

		// 在施法者位置生成一个区域效果渲染组件
		sourceAreaEffectRenderComponent = simulation.spawnSpellEffectOnPoint(caster.getX(), caster.getY(), 0,
				getAlias(), CEffectType.AREA_EFFECT, 0);

		// 在目标位置生成一个区域效果渲染组件
		targetAreaEffectRenderComponent = simulation.spawnSpellEffectOnPoint(target.getX(), target.getY(), 0,
				getAlias(), CEffectType.AREA_EFFECT, 0);

		// 在施法者单位上创建一个临时的法术效果
		simulation.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);

		// 访问目标，获取目标单位
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		// 如果目标单位不为空，则暂停目标单位
		if (targetUnit != null) {
			targetUnit.setPaused(true);
		}

		return true;
	}

	@Override
	/**
	 * 持续性施法Tick
	 * @param simulation 游戏模拟
	 * @param caster 施法者单位
	 * @param target 目标
	 * @return 返回是否继续施法
	 */
	public boolean doChannelTick(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		final int gameTurnTick = simulation.getGameTurnTick(); // 获取当前游戏回合数
		if (gameTurnTick >= channelEndTick) { // 如果当前回合数大于等于施法结束回合数
			final List<CUnit> teleportingUnits = new ArrayList<>(); // 创建一个列表用于存储将要被传送的单位
			final float casterX = caster.getX(); // 获取施法者X坐标
			final float casterY = caster.getY(); // 获取施法者Y坐标
			final float targetX = target.getX(); // 获取目标点X坐标
			final float targetY = target.getY(); // 获取目标点Y坐标
			simulation.getWorldCollision().enumUnitsInRange(casterX, casterY, areaOfEffect, (enumUnit) -> { // 枚举施法者周围一定范围内的单位
				if ((enumUnit != caster) && enumUnit.canBeTargetedBy(simulation, caster, getTargetsAllowed())) { // 如果单位不是施法者且可以被施法者选中
					teleportingUnits.add(enumUnit); // 将单位添加到传送列表
				}
				return (teleportingUnits.size() + 1) >= numberOfUnitsTeleported; // 当传送列表中的单位数量达到要传送的单位数量时停止枚举
			});
			// 施法者和将要被传送的单位周围的特效
			for (final CUnit teleportingUnit : teleportingUnits) {
				simulation.spawnSpellEffectOnPoint(teleportingUnit.getX(), teleportingUnit.getY(), 0, getAlias(),
						CEffectType.SPECIAL, 0).remove();
			}
			simulation.spawnSpellEffectOnPoint(casterX, casterY, 0, getAlias(), CEffectType.SPECIAL, 0).remove();
			caster.setPointAndCheckUnstuck(targetX, targetY, simulation); // 将施法者传送到目标点并检查是否卡住
			if (useTeleportClustering) { // 如果使用传送聚类
				for (final CUnit teleportingUnit : teleportingUnits) {
					teleportingUnit.setPointAndCheckUnstuck(targetX, targetY, simulation); // 将单位传送到目标点并检查是否卡住
				}
			} else { // 如果不使用传送聚类
				for (final CUnit teleportingUnit : teleportingUnits) {
					final float offsetX = teleportingUnit.getX() - casterX; // 计算单位相对于施法者的X偏移
					final float offsetY = teleportingUnit.getY() - casterY; // 计算单位相对于施法者的Y偏移
					teleportingUnit.setPointAndCheckUnstuck(targetX + offsetX, targetY + offsetY, simulation); // 将单位传送到相对于目标点的位置并检查是否卡住
				}
			}
			// 传送后施法者和将要被传送的单位周围的特效
			for (final CUnit teleportingUnit : teleportingUnits) {
				simulation.spawnSpellEffectOnPoint(teleportingUnit.getX(), teleportingUnit.getY(), 0, getAlias(),
						CEffectType.SPECIAL, 0).remove();
			}
			simulation.spawnSpellEffectOnPoint(caster.getX(), caster.getY(), 0, getAlias(), CEffectType.SPECIAL, 0)
					.remove();
			return false; // 施法结束，返回false
		}
		return true; // 施法尚未结束，返回true

	}

	@Override
	/**
	 * 施法结束处理
	 * @param game 游戏模拟
	 * @param unit 施法单位
	 * @param target 目标
	 * @param interrupted 是否被打断
	 */
	public void doChannelEnd(final CSimulation game, final CUnit unit, final AbilityTarget target,
			final boolean interrupted) {
		// 移除原区域效果渲染组件
		sourceAreaEffectRenderComponent.remove();
		sourceAreaEffectRenderComponent = null;
		// 移除目标区域效果渲染组件
		targetAreaEffectRenderComponent.remove();
		targetAreaEffectRenderComponent = null;
		// 重置施法者单位的取消暂停状态
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			targetUnit.setPaused(false);
		}
	}
}
