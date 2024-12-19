package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.darkranger;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
// 符咒 控制某个敌方单位。|n符咒不能被用在英雄和等级高于<ANch,DataA1>的中立单位上。
// 命令物品
public class CAbilityCharm extends CAbilityTargetSpellBase {
	private int maximumCreepLevel; // 定义最大爬行等级

	// 构造函数
	public CAbilityCharm(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	/**
	 * 填充数据，从worldEditorAbility中获取等级对应的最大爬行等级
	 *
	 * @param worldEditorAbility 世界编辑器能力对象
	 * @param level              等级
	 */
	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.maximumCreepLevel = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_A + level, 0);
	}

	// 获取基础命令ID
	@Override
	public int getBaseOrderId() {
		return OrderIds.charm;
	}

	/**
	 * 内部检查目标是否有效
	 *
	 * @param game     模拟游戏对象
	 * @param unit     单位对象
	 * @param orderId  命令ID
	 * @param target   目标对象
	 * @param receiver 目标检查接收器
	 */
	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
									   final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			// 如果目标单位的等级小于等于最大爬行等级，则调用父类方法进行目标检查
			if (targetUnit.getUnitType().getLevel() <= maximumCreepLevel) {
				super.innerCheckCanTarget(game, unit, orderId, target, receiver);
			} else {
				// 否则，目标检查失败，提示目标生物太强大
				receiver.targetCheckFailed(CommandStringErrorKeys.THAT_CREATURE_IS_TOO_POWERFUL);
			}
		} else {
			// 如果目标单位为空，目标检查失败，提示必须选择一个单位
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
		}
	}

	/**
	 * 执行效果
	 *
	 * @param simulation 模拟对象
	 * @param unit       施法单位
	 * @param target     目标对象
	 * @return 是否成功执行
	 */
	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		// 获取目标单位
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		// 在目标单位上创建临时法术效果
		simulation.createTemporarySpellEffectOnUnit(targetUnit, getAlias(), CEffectType.TARGET);

		// 获取目标单位使用的升级列表
		final List<War3ID> targetUpgradesUsed = targetUnit.getUnitType().getUpgradesUsed();
		// 获取目标单位和施法单位的玩家对象
		final CPlayer targetPlayer = simulation.getPlayer(targetUnit.getPlayerIndex());
		final CPlayer castingUnitPlayer = simulation.getPlayer(unit.getPlayerIndex());
		// 遍历目标单位使用的升级
		for (final War3ID targetUpgradeUsed : targetUpgradesUsed) {
			// 获取升级类型
			final CUpgradeType upgradeType = simulation.getUpgradeData().getType(targetUpgradeUsed);
			// 如果升级随单位所有权转移
			if (upgradeType.isTransferWithUnitOwnership()) {
				// 获取目标单位和施法单位玩家解锁该升级的等级
				final int targetPlayerTechUnlocked = targetPlayer.getTechtreeUnlocked(targetUpgradeUsed);
				final int castingUnitPlayerTechUnlocked = castingUnitPlayer.getTechtreeUnlocked(targetUpgradeUsed);
				// 如果目标单位玩家解锁等级更高，则施法单位玩家也解锁到相同等级
				if (targetPlayerTechUnlocked > castingUnitPlayerTechUnlocked) {
					castingUnitPlayer.setTechResearched(simulation, targetUpgradeUsed, targetPlayerTechUnlocked);
				}
			}
		}
		// 移除目标单位上的玩家升级
		simulation.getUnitData().unapplyPlayerUpgradesToUnit(simulation, targetUnit.getPlayerIndex(),
				targetUnit.getUnitType(), targetUnit);
		// 保存目标单位原来的食物消耗
		final int oldFoodUsed = targetUnit.getFoodUsed();
		// 设置目标单位的食物消耗为0
		targetPlayer.setUnitFoodUsed(targetUnit, 0);
		// 将目标单位的玩家索引更改为施法单位的玩家索引
		targetUnit.setPlayerIndex(simulation, unit.getPlayerIndex(), true);
		// 应用玩家升级到目标单位
		simulation.getUnitData().applyPlayerUpgradesToUnit(simulation, targetUnit.getPlayerIndex(),
				targetUnit.getUnitType(), targetUnit);
		// 恢复目标单位原来的食物消耗
		castingUnitPlayer.setUnitFoodUsed(targetUnit, oldFoodUsed);
		// 命令目标单位停止移动
		targetUnit.order(simulation, OrderIds.stop, null);
		return false;
	}


	// 获取最大爬行等级
	public int getMaximumCreepLevel() {
		return maximumCreepLevel;
	}

	// 设置最大爬行等级
	public void setMaximumCreepLevel(final int maximumCreepLevel) {
		this.maximumCreepLevel = maximumCreepLevel;
	}
}
