package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.IndexedSequence;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

/**
 * 渲染法术效果的类，实现了渲染效果接口。播放动画队列
 */
public class RenderSpellEffect implements RenderEffect {
    // 默认的动画队列，包含出生、站立和死亡动画
    public static final PrimaryTag[] DEFAULT_ANIMATION_QUEUE = {PrimaryTag.BIRTH, PrimaryTag.STAND, PrimaryTag.DEATH};
    // 只包含站立动画的队列
    public static final PrimaryTag[] STAND_ONLY = {PrimaryTag.STAND};
    // 只包含死亡动画的队列
    public static final PrimaryTag[] DEATH_ONLY = {PrimaryTag.DEATH};

    // 当前的序列循环模式
    private SequenceLoopMode sequenceLoopMode;
    // 模型的复杂实例，用于渲染法术效果
    private final MdxComplexInstance modelInstance;
    // 当前的动画队列
    private PrimaryTag[] animationQueue;
    // 所需的动画名称集合
    private final EnumSet<SecondaryTag> requiredAnimationNames;
    // 当前动画队列中的索引
    private int animationQueueIndex;
    // 模型的序列列表
    private final List<Sequence> sequences;
    // 是否在动画完成时销毁模型实例
    private boolean killWhenDone = true;


    /**
     * 构造函数，用于初始化渲染法术效果。
     *
     * @param modelInstance          模型实例
     * @param war3MapViewer          War3地图查看器
     * @param yaw                    旋转角度
     * @param animationQueue         动画队列
     * @param requiredAnimationNames 所需动画名称
     */
    public RenderSpellEffect(final MdxComplexInstance modelInstance, final War3MapViewer war3MapViewer, final float yaw,
                             final PrimaryTag[] animationQueue, final EnumSet<SecondaryTag> requiredAnimationNames) {
        // 设置模型实例
        this.modelInstance = modelInstance;
        // 设置动画队列
        this.animationQueue = animationQueue;
        // 设置所需动画名称列表
        this.requiredAnimationNames = requiredAnimationNames;
        // 获取MDX模型对象
        final MdxModel model = (MdxModel) this.modelInstance.model;
        // 获取模型的所有序列
        this.sequences = model.getSequences();
        // 设置序列循环模式为模型循环
        this.sequenceLoopMode = SequenceLoopMode.MODEL_LOOP;
        // 将序列循环模式应用到模型实例
        this.modelInstance.setSequenceLoopMode(sequenceLoopMode);
        // 设置模型实例的局部旋转，绕Z轴旋转yaw角度
        this.modelInstance.localRotation.setFromAxisRad(0, 0, 1, yaw);
        // 标记当前序列已结束
        this.modelInstance.sequenceEnded = true;
        // 播放下一个动画
        playNextAnimation();
        // 如果当前序列未设置且模型有多个序列，则设置第一个序列并重置动画队列索引
        if ((this.modelInstance.sequence == -1) && (model.getSequences().size() > 0)) {
            this.modelInstance.setSequence(0);
            this.animationQueueIndex = 0;
        }

    }

    /**
     * 更新动画状态的方法。
     *
     * @param war3MapViewer War3地图查看器
     * @param deltaTime     时间增量
     * @return 是否所有动画都已完成
     */
    @Override
    public boolean updateAnimations(final War3MapViewer war3MapViewer, final float deltaTime) {
        // 检查是否所有动画都已完成
        final boolean everythingDone = this.modelInstance.sequenceEnded && this.animationQueueIndex >= this.animationQueue.length;

        // 如果所有动画都已完成
        if (everythingDone) {
            // 如果配置为完成时销毁
            if (this.killWhenDone) {
                // 如果模型实例有父级，则移除父级关系
                if (this.modelInstance.parent != null) {
                    this.modelInstance.setParent(null);
                }
                // 从世界场景中移除模型实例
                war3MapViewer.worldScene.removeInstance(this.modelInstance);
            } else {
                // 如果不是销毁，则重置动画队列索引为0，准备播放下一轮动画
                this.animationQueueIndex = 0;
                // 返回false表示动画尚未完成
                return false;
            }
        }

        // 播放下一个动画
        playNextAnimation();

        // 返回是否所有动画都已完成的状态
        return everythingDone;

    }

    /**
     * 播放下一个动画的方法。
     */
    private void playNextAnimation() {
        while (this.modelInstance.sequenceEnded && (this.animationQueueIndex < this.animationQueue.length)) {
            applySequence();
            this.animationQueueIndex++;
        }
    }

    /**
     * 应用当前序列的方法。
     */
    public void applySequence() {
        // 获取当前动画队列中的标签
        final PrimaryTag tag = this.animationQueue[this.animationQueueIndex];
        // 根据标签和所需动画名称，从模型序列列表中选择合适的序列
        final IndexedSequence sequence = SequenceUtils.selectSequence(tag, requiredAnimationNames, this.sequences,
                true);
        // 如果选择的序列存在且索引有效
        if ((sequence != null) && (sequence.index != -1)) {
            // 如果当前标签是站立（STAND）且序列循环模式不是从不循环（NEVER_LOOP）
            if ((tag == PrimaryTag.STAND) && (sequenceLoopMode != SequenceLoopMode.NEVER_LOOP)) {
                // 设置模型实例的序列循环模式为总是循环（ALWAYS_LOOP）
                this.modelInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
            }
            // 否则
            else {
                // 设置模型实例的序列循环模式为当前的序列循环模式
                this.modelInstance.setSequenceLoopMode(sequenceLoopMode);
            }
            // 设置模型实例的当前序列为选择的序列索引
            this.modelInstance.setSequence(sequence.index);
        }

    }

    /**
     * 设置动画和结束时是否销毁的方法。
     *
     * @param animations   动画列表
     * @param killWhenDone 是否在完成时销毁
     */
    public void setAnimations(final PrimaryTag[] animations, final boolean killWhenDone) {
        this.animationQueue = animations;
        this.animationQueueIndex = 0;
        setKillWhenDone(killWhenDone);
        applySequence();
    }

    /**
     * 设置结束时是否销毁的方法。
     *
     * @param killWhenDone 是否在完成时销毁
     */
    public void setKillWhenDone(final boolean killWhenDone) {
        this.killWhenDone = killWhenDone;
        if (killWhenDone) {
            sequenceLoopMode = SequenceLoopMode.NEVER_LOOP;
            this.modelInstance.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
        } else {
            this.modelInstance.setSequenceLoopMode(sequenceLoopMode);
        }
    }

    /**
     * 设置模型高度的方法。
     *
     * @param height 新的高度
     */
    public void setHeight(final float height) {
        this.modelInstance.setLocation(modelInstance.localLocation.x, modelInstance.localLocation.y, height);
    }
}

