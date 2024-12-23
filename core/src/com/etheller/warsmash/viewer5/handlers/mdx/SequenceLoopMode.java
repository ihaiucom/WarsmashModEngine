package com.etheller.warsmash.viewer5.handlers.mdx;

/**
 * 定义了 MDX 模型动画序列的循环模式。
 */
public enum SequenceLoopMode {
    /**
     * 动画序列从不循环。
     */
    NEVER_LOOP,
    /**
     * 动画序列在模型的生命周期内循环。
     */
    MODEL_LOOP,
    /**
     * 动画序列总是循环，即使模型的生命周期结束。
     */
    ALWAYS_LOOP,
    /**
     * 动画序列从不循环，并且在完成后隐藏模型。
     * 这通常用于生成的特效。
     */
    NEVER_LOOP_AND_HIDE_WHEN_DONE,
    /**
     * 动画序列循环到下一个动画。
     * 这在 Arthas vs Illidan 技术演示中使用。
     */
    LOOP_TO_NEXT_ANIMATION;
}
