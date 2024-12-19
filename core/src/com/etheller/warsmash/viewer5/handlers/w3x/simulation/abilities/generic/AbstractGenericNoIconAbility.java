package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;

// 抽象无图标能力
public abstract class AbstractGenericNoIconAbility extends AbstractGenericAliasedAbility implements GenericNoIconAbility {

	/**
	 * 构造函数，初始化AbstractGenericNoIconAbility实例。
	 *
	 * @param handleId 句柄ID
	 * @param code     能力代码
	 * @param alias    别名
	 */
	public AbstractGenericNoIconAbility(final int handleId, final War3ID code, final War3ID alias) {
		super(handleId, code, alias);
	}

	/**
	 * 访问者模式，允许访问者对象访问当前对象。
	 *
	 * @param <T>     泛型类型
	 * @param visitor 访问者对象
	 * @return 访问者对象的返回值
	 */
	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	/**
	 * 判断能力是否为物理能力。
	 *
	 * @return 如果是物理能力返回true，否则返回false
	 */
	@Override
	public boolean isPhysical() {
		return true;
	}

	/**
	 * 判断能力是否为通用能力。
	 *
	 * @return 如果是通用能力返回true，否则返回false
	 */
	@Override
	public boolean isUniversal() {
		return false;
	}

	/**
	 * 获取能力的类别。
	 *
	 * @return 能力类别
	 */
	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.PASSIVE;
	}

}

