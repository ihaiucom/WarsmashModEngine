package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import com.etheller.warsmash.util.SubscriberSetNotifier;

public interface CUnitStateListener {
	// 生命改变
	void lifeChanged(); // hp (current) changes

	// 法力改变
	void manaChanged();

	// 命令队列改变
	void ordersChanged();

	// 队列变化时调用的方法
	void queueChanged();

	// 集结点变化时调用的方法
	void rallyPointChanged();

	// 路径点变化时调用的方法
	void waypointsChanged();

	// 英雄状态变化时调用的方法
	void heroStatsChanged();

	// 背包变化时调用的方法
	void inventoryChanged();

	// 攻击变化时调用的方法
	void attacksChanged();

	// 能力变化时调用的方法
	void abilitiesChanged();

    // 隐藏状态变化时调用的方法
    void hideStateChanged();


    public static final class CUnitStateNotifier extends SubscriberSetNotifier<CUnitStateListener>
			implements CUnitStateListener {
		// 生命改变
		@Override
		public void lifeChanged() {
			for (final CUnitStateListener listener : set) {
				listener.lifeChanged();
			}
		}

		// 法力改变
		@Override
		public void manaChanged() {
			for (final CUnitStateListener listener : set) {
				listener.manaChanged();
			}
		}

		// 命令队列改变
		@Override
		public void ordersChanged() {
			for (final CUnitStateListener listener : set) {
				listener.ordersChanged();
			}
		}

		// 队列改变
		@Override
		public void queueChanged() {
			for (final CUnitStateListener listener : set) {
				listener.queueChanged();
			}
		}

		// 可能是 录像回复 的焦点改变
		@Override
		public void rallyPointChanged() {
			for (final CUnitStateListener listener : set) {
				listener.rallyPointChanged();
			}
		}

		// 路点改变
		@Override
		public void waypointsChanged() {
			for (final CUnitStateListener listener : set) {
				listener.waypointsChanged();
			}
		}

		// 英雄属性改变
		@Override
		public void heroStatsChanged() {
			for (final CUnitStateListener listener : set) {
				listener.heroStatsChanged();
			}
		}

		// 物品栏改变
		@Override
		public void inventoryChanged() {
			for (final CUnitStateListener listener : set) {
				listener.inventoryChanged();
			}
		}

		// 能否攻击状态改变
		@Override
		public void attacksChanged() {
			for (final CUnitStateListener listener : set) {
				listener.attacksChanged();
			}
		}

		// 技能改变
		@Override
		public void abilitiesChanged() {
			for (final CUnitStateListener listener : set) {
				listener.abilitiesChanged();
			}
		}

		// 隐藏状态改变
		@Override
		public void hideStateChanged() {
			for (final CUnitStateListener listener : set) {
				listener.hideStateChanged();
			}
		}
	}
}
