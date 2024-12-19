package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttackMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
/**
 * 普攻能力
 * 在创建单位时，检测普攻列表不为空时创建添加到单位的能力列表中里
 */
public class CAbilityAttack extends AbstractCAbility {

	/**
	 * 构造函数，用于初始化攻击能力
	 *
	 * @param handleId 能力的句柄ID
	 */
	public CAbilityAttack(final int handleId) {
		super(handleId, War3ID.fromString("Aatk"));
	}

	/**
	 * 检查单位是否可以使用此攻击能力
	 *
	 * @param game    当前游戏实例
	 * @param unit    发起攻击的单位
	 * @param orderId 攻击命令ID
	 * @param receiver 能力激活接收者
	 */
	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		//如果 普攻列表是空的
		if (unit.getCurrentAttacks().isEmpty()) {
			// 不能使用
			receiver.disabled();
		}
		else {
			// 能使用
			receiver.useOk();
		}
	}

	/**
	 * 检测能否攻击目标 ：（单位/可破坏物/物品）
	 *
	 * @param game    当前游戏实例
	 * @param unit    发起攻击的单位
	 * @param orderId 攻击命令ID
	 * @param target  攻击目标
	 * @param receiver 目标检查接收者
	 */
	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (target == unit) { // 目标是自己，不能攻击自己
			receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_SELF);
			return; // no attacking self ever
		}
		// TODO #右键指令情况下 检测目标是否可以攻击
		if (orderId == OrderIds.smart) { // 右键点击指令
			if (target instanceof CUnit) { // 目标是单位
				// 检查是盟友，就不可以攻击
				if (game.getPlayer(unit.getPlayerIndex()).hasAlliance(((CUnit) target).getPlayerIndex(),
						CAllianceType.PASSIVE)) {
					// 不能攻击 盟友
					receiver.orderIdNotAccepted();
					return;
				}
			}
			else if (target instanceof CDestructable) { // 目标是可破坏物
				// fall thru to below
			}
			else { // 右键的物品 不能攻击
				receiver.orderIdNotAccepted(); // 不可以
				return;
			}
		}
		if ((orderId == OrderIds.smart) || (orderId == OrderIds.attack)) { // 右键点击指令或者普通攻击指令
			boolean canTarget = false;
			CUnitAttack lastUnavailableAttack = null;
			// 遍历普攻列表
			for (final CUnitAttack attack : unit.getCurrentAttacks()) {
				// 检测目标是否可以攻击
				if (target.canBeTargetedBy(game, unit, attack.getTargetsAllowed())) {
					// TODO #获取单位目标， 检测单位状态是否可以攻击
					CUnit tarU = target.visit(AbilityTargetVisitor.UNIT);
					if (tarU != null) {
						// TODO 虚无单位，非魔法攻击不能打
						if (tarU.isUnitType(CUnitTypeJass.ETHEREAL) && attack.getAttackType() != CAttackType.MAGIC && attack.getAttackType() != CAttackType.SPELLS) {
							receiver.targetCheckFailed(CommandStringErrorKeys.ETHEREAL_UNITS_CAN_ONLY_BE_HIT_BY_SPELLS_AND_MAGIC_DAMAGE);
						// TODO 魔法免疫单位，魔法攻击不能打
						} else if (tarU.isUnitType(CUnitTypeJass.MAGIC_IMMUNE) && attack.getAttackType() == CAttackType.MAGIC && game.getGameplayConstants().isMagicImmuneResistsDamage()) {
							receiver.targetCheckFailed(CommandStringErrorKeys.THAT_UNIT_IS_IMMUNE_TO_MAGIC);
						} else {
							canTarget = true;
						}
					} else {
						canTarget = true;
					}
					break;
				} else {
					// 记录最后一个不可攻击的普攻
					lastUnavailableAttack = attack;
				}
			}
			// 能攻击目标
			if (canTarget) {
				// 设置能攻击目标
				receiver.targetOk(target);
			}
			// 不能攻击目标
			else {
				// 检测到目标不可攻击的 普攻
				if(lastUnavailableAttack != null) {
					// a check known to fail, so it will populate our receiver
					//一个已知失败的检查，因此它将填充我们的接收器
					// 再一次调用目标检查
					target.canBeTargetedBy(game, unit, lastUnavailableAttack.getTargetsAllowed(), receiver);
				} else {
					//设置检测错误： 必须用此动作瞄准一个单位
					receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
				}
			}
		}
		else {
			// 设置不能攻击目标
			receiver.orderIdNotAccepted();
		}
	}

	/**
	 * 在队列之前进行检查
	 *
	 * @param game    当前游戏实例
	 * @param caster  发起能力的单位
	 * @param orderId 能力命令ID
	 * @param target  能力目标
	 * @return 是否可以进行操作
	 */
	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		return true;
	}

	/**
	 * 检测能否攻击目标： 坐标点
	 *
	 * @param game    当前游戏实例
	 * @param unit    发起攻击的单位
	 * @param orderId 攻击命令ID
	 * @param target  点目标
	 * @param receiver 目标检查接收者
	 */
	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		switch (orderId) {
		case OrderIds.attack: // 攻击指令
			receiver.targetOk(target); // 可以
			break;
		case OrderIds.attackground: // 攻击地面指令
			boolean allowAttackGround = false;
			// 检查普攻是否有支持攻击地面的普攻
			for (final CUnitAttack attack : unit.getCurrentAttacks()) {
				if (attack.getWeaponType().isAttackGroundSupported()) {
					allowAttackGround = true;
					break;
				}
			}
			// 检测到有可攻击地面的普攻
			if (allowAttackGround) {
				receiver.targetOk(target); // 可以
			}
			else {
				receiver.orderIdNotAccepted();  // 不可以
			}
			break;
		default:
			receiver.orderIdNotAccepted(); // 不可以
			break;
		}
	}

	/**
	 * 检测能否攻击目标：无目标
	 *
	 * @param game    当前游戏实例
	 * @param unit    发起能力的单位
	 * @param orderId 能力命令ID
	 * @param receiver 目标检查接收者
	 */
	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		// 不可攻击，必须有目标
		receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
	}

	/**
	 * 当单位添加此能力时的处理
	 *
	 * @param game 当前游戏实例
	 * @param unit 添加了能力的单位
	 */
	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		// 创建 普攻行为
		unit.setAttackBehavior(new CBehaviorAttack(unit));
		// 如果没有禁止移动
		if (!unit.isMovementDisabled()) {
			// 创建 攻击移动行为
			unit.setAttackMoveBehavior(new CBehaviorAttackMove(unit));
		}
	}

	/**
	 * 当单位移除此能力时的处理
	 *
	 * @param game 当前游戏实例
	 * @param unit 移除能力的单位
	 */
	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	/**
	 * 每个游戏循环的更新处理
	 *
	 * @param game 当前游戏实例
	 * @param unit 处理更新的单位
	 */
	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	/**
	 * 开始攻击行为： 目标为单位/可破坏物/物品
	 *
	 * @param game    当前游戏实例
	 * @param caster  发起攻击的单位
	 * @param orderId 攻击命令ID
	 * @param target  攻击目标
	 * @return 攻击行为
	 */
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		CBehavior behavior = null;
		// 遍历普攻列表
		for (final CUnitAttack attack : caster.getCurrentAttacks()) {
			// 检测目标是否可以攻击
			if (target.canBeTargetedBy(game, caster, attack.getTargetsAllowed())) {
				// 设置攻击行为参数
				behavior = caster.getAttackBehavior().reset(OrderIds.attack, attack, target, false,
						CBehaviorAttackListener.DO_NOTHING);
				break;
			}
		}

		// 没有可用的普攻
		if (behavior == null) {
			// 执行移动行为
			behavior = caster.getMoveBehavior().reset(OrderIds.attack, target);
		}
		return behavior;
	}

	/**
	 * 开始目标为点的攻击行为： 目标为坐标点
	 *
	 * @param game    当前游戏实例
	 * @param caster  发起攻击的单位
	 * @param orderId 攻击命令ID
	 * @param point   点目标
	 * @return 攻击行为
	 */
	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		switch (orderId) {
		case OrderIds.attack: // 攻击指令
			// 如果攻击移动行为不存在，就执行下一个指令行为
			if (caster.getAttackMoveBehavior() == null) {
				return caster.pollNextOrderBehavior(game);
			}
			// 否则，设置攻击移动行为为默认行为
			caster.setDefaultBehavior(caster.getAttackMoveBehavior());
			// 移动到目标点
			return caster.getAttackMoveBehavior().reset(point);
		case OrderIds.attackground: // 攻击地面指令
			CBehavior behavior = null;
			// 遍历普攻列表
			for (final CUnitAttack attack : caster.getCurrentAttacks()) {
				// 检测是否有支持攻击地面的普攻
				if (attack.getWeaponType().isAttackGroundSupported()) {
					// 设置攻击行为参数
					behavior = caster.getAttackBehavior().reset(OrderIds.attackground, attack, point, false,
							CBehaviorAttackListener.DO_NOTHING);
					break;
				}
			}
			// 没有可用的普攻
			if (behavior == null) {
				// 执行移动行为
				behavior = caster.getMoveBehavior().reset(OrderIds.attackground, point);
			}
			return behavior;
		default:
			// 执行下一个指令行为
			return caster.pollNextOrderBehavior(game);
		}
	}

	/**
	 * 开始无目标的攻击行为，执行下一个指令行为
	 *
	 * @param game    当前游戏实例
	 * @param caster  发起攻击的单位
	 * @param orderId 攻击命令ID
	 * @return 攻击行为
	 */
	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		// 执行下一个指令行为
		return caster.pollNextOrderBehavior(game);
	}

	/**
	 * 访问能力的访问器
	 *
	 * @param visitor 访问器
	 * @return 访问结果
	 */
	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	/**
	 * 从队列中取消能力的处理
	 *
	 * @param game    当前游戏实例
	 * @param unit    取消能力的单位
	 * @param orderId 能力命令ID
	 */
	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	/**
	 * 处理单位死亡的事件
	 *
	 * @param game   当前游戏实例
	 * @param cUnit  死亡的单位
	 */
	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

	/**
	 * 判断是否为物理攻击
	 *
	 * @return 是否为物理攻击
	 */
	@Override
	public boolean isPhysical() {
		return true;
	}

	/**
	 * 判断是否为通用攻击
	 *
	 * @return 是否为通用攻击
	 */
	@Override
	public boolean isUniversal() {
		return false;
	}

	/**
	 * 获取能力类别：攻击
	 *
	 * @return 能力类别
	 */
	@Override
	public CAbilityCategory getAbilityCategory() {
		// 分类：攻击
		return CAbilityCategory.ATTACK;
	}

}

