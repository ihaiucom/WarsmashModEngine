package com.etheller.interpreter.ast.value;

// 定义一个泛型接口 JassTypeVisitor，用于访问不同类型的 Jass 类型
public interface JassTypeVisitor<TYPE> {

    // 接受一个 PrimitiveJassType 类型的参数，并返回一个泛型类型的值
    TYPE accept(PrimitiveJassType primitiveType);

    // 接受一个 ArrayJassType 类型的参数，并返回一个泛型类型的值
    TYPE accept(ArrayJassType arrayType);

    // 接受一个 HandleJassType 类型的参数，并返回一个泛型类型的值
    TYPE accept(HandleJassType type);
}
