package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting;

import com.badlogic.gdx.math.Vector2;
// 表示能力目标的类，继承自Vector2并实现AbilityTarget接口
public class AbilityPointTarget extends Vector2 implements AbilityTarget {

	// 默认构造函数
	public AbilityPointTarget() {
		super();
	}

	// 以坐标x和y创建能力目标的构造函数
	public AbilityPointTarget(final float x, final float y) {
		super(x, y);
	}

	// 以Vector2对象创建能力目标的构造函数
	public AbilityPointTarget(final Vector2 v) {
		super(v);
	}

	@Override
	// 返回当前对象的x坐标
	public float getX() {
		return this.x;
	}

	@Override
	// 返回当前对象的y坐标
	public float getY() {
		return this.y;
	}

	@Override
	// 接受一个AbilityTargetVisitor访问者并返回结果
	public <T> T visit(final AbilityTargetVisitor<T> visitor) {
		return visitor.accept(this);
	}

}

