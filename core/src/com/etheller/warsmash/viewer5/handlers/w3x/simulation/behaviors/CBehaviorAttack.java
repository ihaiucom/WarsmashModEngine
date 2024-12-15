package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveAndTargetableVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
/**
 * CBehaviorAttack类负责处理单位的攻击行为。
 */
public class CBehaviorAttack extends CAbstractRangedBehavior {

	private int highlightOrderId;
	private final AbilityTargetStillAliveAndTargetableVisitor abilityTargetStillAliveVisitor;

	/**
	 * CBehaviorAttack构造函数
	 * @param unit 相关单位
	 */
	public CBehaviorAttack(final CUnit unit) {
		super(unit);
		this.abilityTargetStillAliveVisitor = new AbilityTargetStillAliveAndTargetableVisitor();
	}

	// 该类包含与单位攻击相关的属性
	private CUnitAttack unitAttack;
	// 发射伤害点的时间
	private int damagePointLaunchTime;
	// 后摆时间
	private int backSwingTime;
	// 当前指令冷却结束时间
	private int thisOrderCooldownEndTime;
	// 攻击监听器
	private CBehaviorAttackListener attackListener;


	/**
	 * 重置攻击行为
	 * @param highlightOrderId 高亮指令ID
	 * @param unitAttack 单位攻击对象
	 * @param target 目标对象
	 * @param disableMove 是否禁用移动
	 * @param attackListener 攻击监听器
	 * @return 当前对象
	 */
	public CBehaviorAttack reset(final int highlightOrderId, final CUnitAttack unitAttack, final AbilityTarget target,
			final boolean disableMove, final CBehaviorAttackListener attackListener) {
		this.highlightOrderId = highlightOrderId;
		this.attackListener = attackListener;
		super.innerReset(target);
		this.unitAttack = unitAttack;
		this.damagePointLaunchTime = 0;
		this.backSwingTime = 0;
		this.thisOrderCooldownEndTime = 0;
		setDisableMove(disableMove);
		return this;
	}

	@Override
	public int getHighlightOrderId() {
		return this.highlightOrderId;
	}

	// 是否在攻击范围内
	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		// 获取攻击范围
		float range = this.unitAttack.getRange();
		if (simulation.getGameTurnTick() < this.unit.getCooldownEndTime()) {
			// 攻击范围运动缓冲
			range += this.unitAttack.getRangeMotionBuffer();
		}
		// 能够到达目标范围内， 并且在最小攻击范围外
		return this.unit.canReach(this.target, range)
				&& (this.unit.distance(this.target) >= this.unit.getUnitType().getMinimumAttackRange());
	}

	// 检查目标是否仍然有效
	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		// 单位非禁止攻击， 目标对象能作为目标
		return !this.unit.isDisableAttacks() && this.target.visit(
				this.abilityTargetStillAliveVisitor.reset(simulation, this.unit, this.unitAttack.getTargetsAllowed()));
	}

	// 不在范围内时，如果可以移动，先执行移动到指定范围，这里就是重置移动行为参数
	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {
		// 发射伤害点的时间
		this.damagePointLaunchTime = 0;
		//  当前指令冷却结束时间
		this.thisOrderCooldownEndTime = 0;
	}

	// 更新当目标无效时的行为: 执行命令队列里的下一个命令或者默认行为
	@Override
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		// 执行命令队列里的下一个命令或者默认行为
		return this.attackListener.onFinish(simulation, this.unit);
	}

	@Override
	public CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		// 攻击冷却时间
		final int cooldownEndTime = this.unit.getCooldownEndTime();
		// 获取当前游戏帧
		final int currentTurnTick = simulation.getGameTurnTick();
		if (withinFacingWindow) { //已经转向到了目标方向
			if (this.damagePointLaunchTime != 0) {
				if (currentTurnTick >= this.damagePointLaunchTime) { // 发射伤害点时间
					// 获取攻击伤害值
					final int damage = this.unitAttack.roll(simulation.getSeededRandom());
					AbilityTarget target = this.target;
					if (this.unitAttack.getWeaponType() == CWeaponType.ARTILLERY) { // 炮火
						// NOTE: adding this fixed a bunch of special cases in my code, but
						// maybe we should re-use the point objects and not "new" here for
						// better performance (maybe in a refactor in the future).
						// 如果是火炮，将目标更改为目标点
						target = new AbilityPointTarget(target.getX(), target.getY());
					}
					// 发起攻击处理器
					this.unitAttack.launch(simulation, this.unit, target, damage, this.attackListener);
					// 重置发射伤害点时间
					this.damagePointLaunchTime = 0;
				}
			}
			else if (currentTurnTick >= cooldownEndTime) { // 过了冷却时间
				// 获取攻击冷却时间
				final float cooldownTime = this.unitAttack.getCooldownTime();
				// 获取伤害点时间
				final float animationDamagePoint = this.unitAttack.getAnimationDamagePoint();
				// 获取动画后摇时间
				final float animationBackswingPoint = this.unitAttack.getAnimationBackswingPoint();
				// 冷却时间 帧
				final int a1CooldownSteps = (int) (cooldownTime / WarsmashConstants.SIMULATION_STEP_TIME);
				// 动画后摇时间 帧
				final int a1BackswingSteps = (int) (animationBackswingPoint / WarsmashConstants.SIMULATION_STEP_TIME);
				// 伤害点时间 帧
				final int a1DamagePointSteps = (int) (animationDamagePoint
						/ WarsmashConstants.SIMULATION_STEP_TIME);
				// 设置冷却时间 = 当前游戏时间帧 + 冷却时间 帧
				this.unit.setCooldownEndTime(currentTurnTick + a1CooldownSteps);
				// 当前指令冷却结束时间 = 当前游戏时间帧 + 冷却时间 帧
				this.thisOrderCooldownEndTime = currentTurnTick + a1CooldownSteps;
				// 发射伤害点的时间 = 当前游戏时间帧 + 伤害点时间 帧
				this.damagePointLaunchTime = currentTurnTick + a1DamagePointSteps;
				// 动画后摇时间 = 当前游戏时间帧 + 动画后摇时间 帧
				this.backSwingTime = currentTurnTick + a1DamagePointSteps + a1BackswingSteps;
				// 播放攻击动画, 时长= 伤害点时间 + 动画后摇时间
				this.unit.getUnitAnimationListener().playAnimationWithDuration(true, PrimaryTag.ATTACK,
						SequenceUtils.EMPTY, animationBackswingPoint + animationDamagePoint, true);
				// 将待机动画 添加到动画队列
				this.unit.getUnitAnimationListener().queueAnimation(PrimaryTag.STAND, SequenceUtils.READY, false);
			}
			else if (currentTurnTick >= this.thisOrderCooldownEndTime) { // 指令冷却时间结束， 播放待机动画
				this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.READY, 1.0f,
						false);
			}
		}
		else { // 正在转向到目标方向
			// 播放待机动画
			this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.READY, 1.0f,
					false);
		}

		// 如果有后摇时间，且过了后摇时间
		if ((this.backSwingTime != 0) && (currentTurnTick >= this.backSwingTime)) {
			this.backSwingTime = 0;
			// 继续攻击行为
			return this.attackListener.onFirstUpdateAfterBackswing(this);
		}
		return this;
	}

	@Override
	public void begin(final CSimulation game) {
		// 禁用移动
		if (unit.isMovementDisabled()) {
			// 转向目标方向
			unit.getUnitAnimationListener().lockTurrentFacing(this.target);
		}
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
		// 禁用移动
		if (unit.isMovementDisabled()) {
			// 取消锁定目标方向
			unit.getUnitAnimationListener().clearTurrentFacing();
		}
	}

	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {

	}

	// 攻击行为是可中断的
	@Override
	public boolean interruptable() {
		return true;
	}

}
