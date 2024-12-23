package com.etheller.interpreter.ast.value;

import com.etheller.interpreter.ast.statement.JassReturnNothingStatement;

// 定义一个名为JassType的接口，用于表示Jass语言中的类型系统
public interface JassType {
    // 定义一个泛型方法visit，接受一个JassTypeVisitor类型的访问者对象，返回一个泛型TYPE的结果
    <TYPE> TYPE visit(JassTypeVisitor<TYPE> visitor);

    // 获取类型的名称，主要用于错误信息中
    String getName();

    // 判断当前类型是否可以从给定的value类型赋值
    boolean isAssignableFrom(JassType value);

    // 判断当前类型是否可以为null
    boolean isNullable();

    // 获取当前类型的null值表示
    JassValue getNullValue();

    // 定义一些Jass语言的内置基本类型
    public static final PrimitiveJassType INTEGER = new PrimitiveJassType("integer", IntegerJassValue.ZERO);
    public static final PrimitiveJassType STRING = new StringJassType("string");
    public static final PrimitiveJassType CODE = new CodeJassType("code");
    public static final PrimitiveJassType REAL = new RealJassType("real", RealJassValue.ZERO);
    public static final PrimitiveJassType BOOLEAN = new PrimitiveJassType("boolean", BooleanJassValue.FALSE);
    public static final PrimitiveJassType NOTHING = new PrimitiveJassType("nothing",
            JassReturnNothingStatement.RETURN_NOTHING_NOTICE);
    public static final PrimitiveJassType DUMMY = new PrimitiveJassType("dummy", DummyJassValue.PAUSE_FOR_SLEEP);
}
