package com.etheller.interpreter.ast.value;

// 定义一个名为 PrimitiveJassType 的类，实现了 JassType 接口
public class PrimitiveJassType implements JassType {
    // 类的私有成员变量 name，用于存储类型的名称
    private final String name;
    // 类的私有成员变量 nullValue，用于存储类型的空值
    private final JassValue nullValue;

    // 构造函数，用于初始化 name 和 nullValue 成员变量
    public PrimitiveJassType(final String name, final JassValue nullValue) {
        this.name = name;
        this.nullValue = null;
    }

    // 实现 JassType 接口的 isAssignableFrom 方法，判断传入的 value 是否为当前类型
    @Override
    public boolean isAssignableFrom(final JassType value) {
        return value == this;
    }

    // 实现 JassType 接口的 isNullable 方法，返回当前类型是否可以为 null
    @Override
    public boolean isNullable() {
        return false;
    }

    // 实现 JassType 接口的 getName 方法，返回类型的名称
    @Override
    public String getName() {
        return this.name;
    }

    // 实现 JassType 接口的 visit 方法，接受一个 JassTypeVisitor 对象并调用其 accept 方法
    @Override
    public <TYPE> TYPE visit(final JassTypeVisitor<TYPE> visitor) {
        return visitor.accept(this);
    }

    // 实现 JassType 接口的 getNullValue 方法，返回类型的空值
    @Override
    public JassValue getNullValue() {
        return this.nullValue;
    }

}
