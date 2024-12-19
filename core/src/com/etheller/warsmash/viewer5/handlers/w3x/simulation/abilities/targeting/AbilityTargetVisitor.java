package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
// 定义能力目标访问者的接口
public interface AbilityTargetVisitor<T> {
	T accept(AbilityPointTarget target);

	T accept(CUnit target);

	T accept(CDestructable target);

	T accept(CItem target);

	// 简单的转换
	AbilityTargetVisitor<CUnit> UNIT = new AbilityTargetVisitor<CUnit>() {

		@Override
		public CUnit accept(AbilityPointTarget target) {
			return null;
		}

		@Override
		public CUnit accept(CUnit target) {
			return target;
		}

		@Override
		public CUnit accept(CDestructable target) {
			return null;
		}

		@Override
		public CUnit accept(CItem target) {
			return null;
		}
	};

	AbilityTargetVisitor<CDestructable> DESTRUCTABLE = new AbilityTargetVisitor<CDestructable>() {

		@Override
		public CDestructable accept(AbilityPointTarget target) {
			return null;
		}

		@Override
		public CDestructable accept(CUnit target) {
			return null;
		}

		@Override
		public CDestructable accept(CDestructable target) {
			return target;
		}

		@Override
		public CDestructable accept(CItem target) {
			return null;
		}
	};

	AbilityTargetVisitor<AbilityPointTarget> POINT = new AbilityTargetVisitor<AbilityPointTarget>() {

		@Override
		public AbilityPointTarget accept(AbilityPointTarget target) {
			return target;
		}

		@Override
		public AbilityPointTarget accept(CUnit target) {
			return null;
		}

		@Override
		public AbilityPointTarget accept(CDestructable target) {
			return null;
		}

		@Override
		public AbilityPointTarget accept(CItem target) {
			return null;
		}
	};

	AbilityTargetVisitor<CItem> ITEM = new AbilityTargetVisitor<CItem>() {

		@Override
		public CItem accept(AbilityPointTarget target) {
			return null;
		}

		@Override
		public CItem accept(CUnit target) {
			return null;
		}

		@Override
		public CItem accept(CDestructable target) {
			return null;
		}

		@Override
		public CItem accept(CItem target) {
			return target;
		}
	};
}
