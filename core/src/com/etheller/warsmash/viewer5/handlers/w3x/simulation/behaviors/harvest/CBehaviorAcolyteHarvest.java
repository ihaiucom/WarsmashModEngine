package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityAcolyteHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityBlightedGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
/**
 * 采集--侍金采集金矿行为
 */
public class CBehaviorAcolyteHarvest extends CAbstractRangedBehavior {
	private final CAbilityAcolyteHarvest abilityAcolyteHarvest; // 采集能力
	private boolean harvesting = false; // 是否正在采集
	private float harvestStandX, harvestStandY; // 采集站点坐标

	/**
	 * 构造函数，初始化采集行为
	 * @param unit 执行行为的单位
	 * @param abilityWispHarvest 采集能力
	 */
	public CBehaviorAcolyteHarvest(final CUnit unit, final CAbilityAcolyteHarvest abilityWispHarvest) {
		super(unit);
		this.abilityAcolyteHarvest = abilityWispHarvest;
	}

	/**
	 * 重置采集行为
	 * @param target 目标小部件
	 * @return 当前实例
	 */
	public CBehaviorAcolyteHarvest reset(final CWidget target) {
		innerReset(target, false);
		return this;
	}

	@Override
	/**
	 * 更新采集行为
	 * @param simulation 当前的模拟环境
	 * @param withinFacingWindow 是否在面对窗口内
	 * @return 当前实例
	 */
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		// 如果不是采集状态
		if (!this.harvesting) {
			// 开始采集
			final HarvestStartResult result = onStartHarvesting(simulation);
			// 如果采集被拒绝
			if (result == HarvestStartResult.DENIED) {
				/// 显示错误提示，并返回下一个行为 //那座金矿再也不能养活任何追随者了
				simulation.getCommandErrorListener().showInterfaceError(this.unit.getPlayerIndex(), CommandStringErrorKeys.THAT_GOLD_MINE_CANT_SUPPORT_ANY_MORE_ACOLYTES);
				return this.unit.pollNextOrderBehavior(simulation);
			}
			// 如果采集被接受
			else if (result == HarvestStartResult.ACCEPTED) {
				// 设置正在采集状态
				this.harvesting = true;
			}
			// 等待状态
			else {
				// 播放待机动画
				this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f,
						true);
			}
		}
		// 如果正在采集
		if (this.harvesting) {
			// 如果采集坐标和当前坐标不同
			if ((this.harvestStandX != this.unit.getX()) || (this.harvestStandY != this.unit.getY())) {
				// 移动到采集坐标
				this.unit.setX(this.harvestStandX, simulation.getWorldCollision(), simulation.getRegionManager());
				this.unit.setY(this.harvestStandY, simulation.getWorldCollision(), simulation.getRegionManager());
				simulation.unitRepositioned(this.unit); // dont interpolate, instant jump
			}
			// 播放工作动画
			this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.WORK, 1.0f, true);
		}
		return this;
	}

	/**
	 * 开始采集
	 * @param simulation 当前的模拟环境
	 * @return 采集开始结果
	 */
	private HarvestStartResult onStartHarvesting(final CSimulation simulation) {
		// TODO maybe use visitor instead of cast
		final CUnit targetUnit = (CUnit) this.target;
		// 遍历目标单位的技能，如果是金矿技能
		for (final CAbility ability : targetUnit.getAbilities()) {
			if ((ability instanceof CAbilityBlightedGoldMine) && !ability.isDisabled()) {
				// 金矿技能
				final CAbilityBlightedGoldMine abilityBlightedGoldMine = (CAbilityBlightedGoldMine) ability;
				// 尝试添加矿工
				final int newIndex = abilityBlightedGoldMine.tryAddMiner(this.unit, this);
				// 如果添加失败
				if (newIndex == CAbilityBlightedGoldMine.NO_MINER) {
					// 返回拒绝结果
					return HarvestStartResult.DENIED;
				}

				// 设置采集站点坐标
				final Vector2 minerLoc = abilityBlightedGoldMine.getMinerLoc(newIndex);
				this.harvestStandX = minerLoc.x;
				this.harvestStandY = minerLoc.y;
				// 播放采集音效
				simulation.unitSoundEffectEvent(this.unit, this.abilityAcolyteHarvest.getAlias());
				// 返回接受结果
				return HarvestStartResult.ACCEPTED;
			}
		}
		// 返回等待结果
		return HarvestStartResult.WAITING;
	}

	/**
	 * 停止采集
	 * @param simulation 当前的模拟环境
	 */
	private void onStopHarvesting(final CSimulation simulation) {
		final CUnit targetUnit = (CUnit) this.target;
		// 遍历目标单位的技能，如果是矿工技能
		for (final CAbility ability : targetUnit.getAbilities()) {
			if (ability instanceof CAbilityBlightedGoldMine) {
				// 移除矿工的方法
				((CAbilityBlightedGoldMine) ability).removeMiner(this);
			}

		}
	}

	@Override
	/**
	 * 当目标无效时更新行为
	 * @param simulation 当前的模拟环境
	 * @return 当前单位的下一个行为
	 */
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		if (this.harvesting) {
			onStopHarvesting(simulation);
			this.harvesting = false;
		}
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	/**
	 * 检查目标是否仍然有效
	 * @param simulation 当前的模拟环境
	 * @return 目标是否有效
	 */
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return this.target.visit(AbilityTargetStillAliveVisitor.INSTANCE);
	}

	@Override
	/**
	 * 在移动前重置
	 * @param simulation 当前的模拟环境
	 */
	protected void resetBeforeMoving(final CSimulation simulation) {
		if (this.harvesting) {
			onStopHarvesting(simulation);
			this.harvesting = false;
		}
	}

	@Override
	/**
	 * 检查单位是否在范围内
	 * @param simulation 当前的模拟环境
	 * @return 是否在范围内
	 */
	public boolean isWithinRange(final CSimulation simulation) {
		return this.unit.canReach(this.target, this.abilityAcolyteHarvest.getCastRange());
	}

	@Override
	/**
	 * 移动结束时的逻辑
	 * @param game 当前的游戏环境
	 * @param interrupted 是否被打断
	 */
	public void endMove(final CSimulation game, final boolean interrupted) {

	}

	@Override
	/**
	 * 行为开始时的逻辑
	 * @param game 当前的游戏环境
	 */
	public void begin(final CSimulation game) {

	}

	@Override
	/**
	 * 行为结束时的逻辑
	 * @param game 当前的游戏环境
	 * @param interrupted 是否被打断
	 */
	public void end(final CSimulation game, final boolean interrupted) {
		if (this.harvesting) {
			onStopHarvesting(game);
			this.harvesting = false;
		}
	}

	@Override
	/**
	 * 获取高亮顺序 ID
	 * @return 高亮顺序 ID
	 */
	public int getHighlightOrderId() {
		return OrderIds.acolyteharvest;
	}

	private static enum HarvestStartResult {
		WAITING, // 等待
		DENIED, // 拒绝
		ACCEPTED // 接受
	};

	@Override
	/**
	 * 检查行为是否可被打断
	 * @return 是否可被打断
	 */
	public boolean interruptable() {
		return true;
	}
}

