package com.etheller.interpreter.ast.value;

// 定义一个名为IntegerJassValue的类，实现了JassValue接口
public class IntegerJassValue implements JassValue {
    // 定义一个静态常量ZERO，它是IntegerJassValue的一个实例，值为0
    public static final IntegerJassValue ZERO = new IntegerJassValue(0);

    // 定义一个私有的整型成员变量value，用于存储整数值
    private final int value;

    // 构造函数，接收一个整型参数value，并将其赋值给成员变量
    public IntegerJassValue(final int value) {
        this.value = value;
    }

    // 定义一个公共方法getValue，用于获取成员变量value的值
    public int getValue() {
        return this.value;
    }

    // 实现JassValue接口中的visit方法，该方法接收一个JassValueVisitor对象作为参数
    // 并调用其accept方法，传入当前对象(this)作为参数
    @Override
    public <TYPE> TYPE visit(final JassValueVisitor<TYPE> visitor) {
        return visitor.accept(this);
    }

    // 重写Object类的toString方法，返回成员变量value的字符串表示
    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
}
