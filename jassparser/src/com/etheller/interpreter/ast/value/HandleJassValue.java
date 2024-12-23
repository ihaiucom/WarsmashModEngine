package com.etheller.interpreter.ast.value;

// 定义一个名为HandleJassValue的类，实现了JassValue接口
public class HandleJassValue implements JassValue {
    // 定义一个私有的HandleJassType类型的成员变量type，用于存储值的类型
    private final HandleJassType type;
    // 定义一个私有的Object类型的成员变量javaValue，用于存储实际的值
    private final Object javaValue;

    // 构造函数，接收HandleJassType和Object两个参数，分别赋值给type和javaValue
    public HandleJassValue(final HandleJassType type, final Object javaValue) {
        this.type = type;
        this.javaValue = javaValue;
    }

    // 获取值的类型
    public HandleJassType getType() {
        return this.type;
    }

    // 获取实际的值
    public Object getJavaValue() {
        return this.javaValue;
    }

    // 实现JassValue接口中的visit方法，该方法接收一个JassValueVisitor类型的参数，
    // 并调用visitor的accept方法，传入当前对象(this)，返回值类型为泛型TYPE
    @Override
    public <TYPE> TYPE visit(final JassValueVisitor<TYPE> visitor) {
        return visitor.accept(this);
    }

    // 重写toString方法，返回值的类型名称和实际值的字符串表示，中间用冒号分隔
    @Override
    public String toString() {
        return this.type.getName() + ":" + this.javaValue;
    }
}
