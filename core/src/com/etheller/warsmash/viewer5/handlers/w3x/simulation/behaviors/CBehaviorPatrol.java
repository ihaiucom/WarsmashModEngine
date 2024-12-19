package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
// 表示巡逻行为的类，实现 CRangedBehavior 接口
public class CBehaviorPatrol implements CRangedBehavior {

	private final CUnit unit; // 单位的引用
	private AbilityPointTarget target; // 当前目标
	private AbilityPointTarget startPoint; // 起始点
	private List<AbilityTarget> targets = new ArrayList<>(); // 巡逻点集合
	private int iter = 1; // 当前巡逻点索引
	private boolean justAutoAttacked = false; // 标志位，判断是否刚刚进行自动攻击

	// 构造函数，传入单位
	public CBehaviorPatrol(final CUnit unit) {
		this.unit = unit;
	}

	// 重置巡逻行为，设置目标和起始点
	public CBehavior reset(final AbilityPointTarget target) {
		targets.clear();
		this.target = target;
		this.startPoint = new AbilityPointTarget(this.unit.getX(), this.unit.getY());
		targets.add(this.startPoint);
		targets.add(target);
		iter = 1;
		return this;
	}

	// 添加巡逻点，支持单位、可破坏物体及目标
	public void addPatrolPoint(final AbilityTarget target) {
		// 检测目标是否是物品
		CItem tarItem = target.visit(AbilityTargetVisitor.ITEM);
		// 是物品就直接添加到巡逻点集合
		if (tarItem != null) {
			targets.add(new AbilityPointTarget(tarItem.getX(), tarItem.getY()));
		} else {
			// 检测目标是否是 可破坏物
			CDestructable tarDest = target.visit(AbilityTargetVisitor.DESTRUCTABLE);
			// 是可破坏物就直接添加到巡逻点集合
			if (tarDest != null) {
				targets.add(new AbilityPointTarget(tarDest.getX(), tarDest.getY()));
			} else {
				targets.add(target);
			}
		}
	}

	// 获取高亮顺序ID
	@Override
	public int getHighlightOrderId() {
		return OrderIds.patrol;
	}

	// 判断单位是否在范围内
	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		// 自动获取目标的方法： 检测自动施法技能是否攻击目标， 检测普攻是否攻击目标
		if (this.justAutoAttacked = this.unit.autoAcquireTargets(simulation, false)) {
			// kind of a hack
			return true;
		}
		return this.unit.distance(this.target.x, this.target.y) <= 16f; // TODO this is not how it was meant to be used
	}

	// 更新巡逻行为
	@Override
	public CBehavior update(final CSimulation simulation) {
		// 进入自动攻击状态，就直接返回当前行为（自动攻击的技能行为或者普攻行为）
		if (this.justAutoAttacked) {
			this.justAutoAttacked = false;
			return this.unit.getCurrentBehavior();
		}

		// 下一个目标点
		iter++;
		if (iter >= this.targets.size()) {
			iter = 0;
		}

		// 寻路点是否是单位
		CUnit tarUnit = this.targets.get(iter).visit(AbilityTargetVisitor.UNIT);
		// 是单位
		if (tarUnit != null) {
			// 目标单位是否是盟友， 是的话就跟随
			if (simulation.getPlayer(unit.getPlayerIndex()).hasAlliance(tarUnit.getPlayerIndex(), CAllianceType.PASSIVE)) {
				unit.getOrderQueue().clear();
				return unit.getFollowBehavior().reset(this.getHighlightOrderId(), tarUnit);
			} else {
				// 否则就获取目标单位位置，作为目标点
				AbilityPointTarget newTar = new AbilityPointTarget(tarUnit.getX(), tarUnit.getY());
				this.targets.set(iter, newTar);
				this.target = newTar;
			}
		} else {
			// 是目标点
			AbilityPointTarget tarPoint = this.targets.get(iter).visit(AbilityTargetVisitor.POINT);
			if (tarPoint != null) {
				this.target = tarPoint;
			}
		}

		// 移动到下一个目标点
		return this.unit.getMoveBehavior().reset(this.target, this, false);
	}

	// 开始巡逻
	@Override
	public void begin(final CSimulation game) {

	}

	// 结束巡逻
	@Override
	public void end(final CSimulation game, final boolean interrupted) {

	}

	// 移动结束处理
	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {

	}

	// 判断是否可被打断
	@Override
	public boolean interruptable() {
		return true;
	}

	// 获取当前目标
	@Override
	public AbilityTarget getTarget() {
		return this.target;
	}

	// 访问者模式的接受方法
	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}
}
