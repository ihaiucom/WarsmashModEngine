package com.etheller.interpreter.ast.value;

// 定义一个名为JassValue的接口，该接口用于表示Jass语言中的值
public interface JassValue {
    // 定义一个泛型方法visit，该方法接受一个JassValueVisitor类型的访问者对象作为参数
    // 并返回一个与访问者对象泛型类型相同的值
    // 这种方法模式常用于实现访问者设计模式，允许在不改变各元素类的前提下定义新的操作
    <TYPE> TYPE visit(JassValueVisitor<TYPE> visitor);
}
