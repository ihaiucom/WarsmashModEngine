package com.etheller.warsmash.viewer5.handlers.w3x;

import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;

/**
 * 表示一个带有索引的序列。
 *
 * 这个类用于存储一个序列（Sequence）对象及其在序列列表中的索引。
 * 它通常用于在需要跟踪特定序列在列表中的位置时使用。
 */
public class IndexedSequence {
    // 存储序列对象
    public final Sequence sequence;
    // 存储序列在列表中的索引
    public final int index;

    /**
     * 构造一个新的 IndexedSequence 对象。
     *
     * @param sequence 要存储的序列对象。
     * @param index    序列在列表中的索引。
     */
    public IndexedSequence(final Sequence sequence, final int index) {
        this.sequence = sequence;
        this.index = index;
    }
}
