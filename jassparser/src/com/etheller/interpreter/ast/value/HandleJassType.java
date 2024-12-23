package com.etheller.interpreter.ast.value;

import com.etheller.interpreter.ast.value.visitor.SuperTypeVisitor;
// 定义一个名为HandleJassType的类，实现了JassType接口
public class HandleJassType implements JassType {
    // 定义一个HandleJassType类型的成员变量superType，用于表示当前类型的父类型
    private HandleJassType superType;
    // 定义一个String类型的成员变量name，用于存储类型的名称
    private final String name;

    // 构造函数，接收父类型和类型名称作为参数，并初始化成员变量
    public HandleJassType(final HandleJassType superType, final String name) {
        this.superType = superType;
        this.name = name;
    }

    // 实现JassType接口的isAssignableFrom方法，判断当前类型是否可以赋值给传入的valueType类型
    @Override
    public boolean isAssignableFrom(JassType valueType) {
        // 循环判断valueType是否为null
        while (valueType != null) {
            // 如果当前类型与valueType相同，则返回true
            if (this == valueType) {
                return true;
            }
            // 否则，通过visit方法获取valueType的父类型，并重新赋值给valueType
            valueType = valueType.visit(SuperTypeVisitor.getInstance());
        }
        // 如果valueType为null且未找到匹配的类型，则返回false
        return false;
    }

    // 实现JassType接口的getName方法，返回类型的名称
    @Override
    public String getName() {
        return this.name;
    }

    // 获取当前类型的父类型
    public HandleJassType getSuperType() {
        return this.superType;
    }

    // 设置当前类型的父类型
    public void setSuperType(final HandleJassType superType) {
        this.superType = superType;
    }

    // 实现JassType接口的visit方法，接收一个JassTypeVisitor对象，并调用其accept方法
    @Override
    public <TYPE> TYPE visit(final JassTypeVisitor<TYPE> visitor) {
        return visitor.accept(this);
    }

    // 实现JassType接口的isNullable方法，表示当前类型是否可以为null
    @Override
    public boolean isNullable() {
        return true;
    }

    // 实现JassType接口的getNullValue方法，返回当前类型的null值表示
    @Override
    public HandleJassValue getNullValue() {
        return new HandleJassValue(this, null);
    }

}
