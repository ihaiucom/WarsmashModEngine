package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;

// 抽象Buff基类
public abstract class AbstractCBuff extends AbstractGenericAliasedAbility implements CBuff {

    /**
     * 构造函数，初始化AbstractCBuff对象。
     *
     * @param handleId 句柄ID
     * @param code     战争3ID代码
     * @param alias    别名战争3ID
     */
    public AbstractCBuff(final int handleId, final War3ID code, final War3ID alias) {
        super(handleId, code, alias);
    }

    /**
     * 访问者模式方法，允许访问者对象访问当前对象。
     *
     * @param <T>       泛型类型
     * @param visitor   访问者对象
     * @return 访问者对象的返回值
     */
    @Override
    public <T> T visit(final CAbilityVisitor<T> visitor) {
        return visitor.accept(this);
    }

    /**
     * 判断能力是否为物理类型。
     *
     * @return 如果是物理类型返回false
     */
    @Override
    public boolean isPhysical() {
        return false;
    }

    /**
     * 判断能力是否为通用类型。
     *
     * @return 如果是通用类型返回false
     */
    @Override
    public boolean isUniversal() {
        return false;
    }

    /**
     * 获取能力的类别。
     *
     * @return 能力类别为BUFF
     */
    @Override
    public CAbilityCategory getAbilityCategory() {
        return CAbilityCategory.BUFF;
    }

}
