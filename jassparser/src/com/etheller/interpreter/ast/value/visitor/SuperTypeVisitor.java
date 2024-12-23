package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.JassTypeVisitor;
import com.etheller.interpreter.ast.value.PrimitiveJassType;

// 定义一个名为 SuperTypeVisitor 的类，实现了 JassTypeVisitor 接口，并指定了泛型类型为 HandleJassType
public class SuperTypeVisitor implements JassTypeVisitor<HandleJassType> {
    // 创建一个 SuperTypeVisitor 类的静态实例，该实例是单例模式，确保整个系统中只有一个实例
    private static final SuperTypeVisitor INSTANCE = new SuperTypeVisitor();

    // 提供一个公共的静态方法，用于获取 SuperTypeVisitor 类的单例实例
    public static SuperTypeVisitor getInstance() {
        return INSTANCE;
    }

    // 实现 JassTypeVisitor 接口的 accept 方法，用于处理 PrimitiveJassType 类型的对象
    // 由于 PrimitiveJassType 是基本类型，它没有超类型，因此这里返回 null
    @Override
    public HandleJassType accept(final PrimitiveJassType primitiveType) {
        return null;
    }

    // 实现 JassTypeVisitor 接口的 accept 方法，用于处理 ArrayJassType 类型的对象
    // 数组类型的超类型是其元素类型的超类型构成的数组，但此处简化处理直接返回 null
    @Override
    public HandleJassType accept(final ArrayJassType arrayType) {
        return null;
    }

    // 实现 JassTypeVisitor 接口的 accept 方法，用于处理 HandleJassType 类型的对象
    // 这里通过调用 HandleJassType 对象的 getSuperType 方法来获取其超类型，并返回
    @Override
    public HandleJassType accept(final HandleJassType type) {
        return type.getSuperType();
    }

}
