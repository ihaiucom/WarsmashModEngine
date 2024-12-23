package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.IndexedSequence;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

// 渲染瞬发特效的类，播放完可选自动移除
public class RenderAttackInstant implements RenderEffect {
    // 定义一个私有的MdxComplexInstance类型的成员变量modelInstance
    private final MdxComplexInstance modelInstance;

    // 构造函数，接收MdxComplexInstance、War3MapViewer和float类型的yaw参数
    public RenderAttackInstant(final MdxComplexInstance modelInstance, final War3MapViewer war3MapViewer,
                               final float yaw) {
        // 将传入的modelInstance赋值给成员变量
        this.modelInstance = modelInstance;
        // 获取modelInstance中的MdxModel对象
        final MdxModel model = (MdxModel) this.modelInstance.model;
        // 获取模型中的所有序列
        final List<Sequence> sequences = model.getSequences();
        // 选择一个序列，这里选择的是标记为PrimaryTag.DEATH的序列，如果没有则选择空序列
        final IndexedSequence sequence = SequenceUtils.selectSequence(PrimaryTag.DEATH, SequenceUtils.EMPTY, sequences,
                true);
        // 如果找到了合适的序列
        if ((sequence != null) && (sequence.index != -1)) {
            // 设置模型实例的序列循环模式为永不循环
            this.modelInstance.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
            // 设置模型实例当前播放的序列
            this.modelInstance.setSequence(sequence.index);
        }
        // 设置模型实例的局部旋转，绕Z轴旋转yaw角度
        this.modelInstance.localRotation.setFromAxisRad(0, 0, 1, yaw);
    }

    // 实现RenderEffect接口的updateAnimations方法，接收War3MapViewer和float类型的deltaTime参数
    @Override
    public boolean updateAnimations(final War3MapViewer war3MapViewer, final float deltaTime) {
        // 检查模型实例的序列是否已经播放完毕
        final boolean everythingDone = this.modelInstance.sequenceEnded;
        // 如果序列播放完毕
        if (everythingDone) {
            // 从场景中移除模型实例
            war3MapViewer.worldScene.removeInstance(this.modelInstance);
        }
        // 返回序列是否播放完毕的状态
        return everythingDone;
    }
}
