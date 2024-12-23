package com.etheller.interpreter.ast.value;

// 定义一个泛型接口 JassValueVisitor，其中 TYPE 是泛型参数，代表访问者返回的结果类型
public interface JassValueVisitor<TYPE> {

    // 接受一个 IntegerJassValue 类型的值，并返回 TYPE 类型的结果
    TYPE accept(IntegerJassValue value);

    // 接受一个 RealJassValue 类型的值，并返回 TYPE 类型的结果
    TYPE accept(RealJassValue value);

    // 接受一个 BooleanJassValue 类型的值，并返回 TYPE 类型的结果
    TYPE accept(BooleanJassValue value);

    // 接受一个 StringJassValue 类型的值，并返回 TYPE 类型的结果
    TYPE accept(StringJassValue value);

    // 接受一个 CodeJassValue 类型的值，并返回 TYPE 类型的结果
    TYPE accept(CodeJassValue value);

    // 接受一个 ArrayJassValue 类型的值，并返回 TYPE 类型的结果
    TYPE accept(ArrayJassValue value);

    // 接受一个 HandleJassValue 类型的值，并返回 TYPE 类型的结果
    TYPE accept(HandleJassValue value);

    // 接受一个 DummyJassValue 类型的值，并返回 TYPE 类型的结果
    TYPE accept(DummyJassValue value);
}
