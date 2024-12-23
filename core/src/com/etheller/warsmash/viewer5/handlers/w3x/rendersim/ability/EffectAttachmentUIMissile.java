package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import java.util.List;

/**
 * Stores render world missile data. This should probably be renamed later not to have
 * "UI" in the name.
 */
/**
 * 表示一个特效附着的导弹用户界面组件，包含模型路径、附着点列表和弧度。
 */
public class EffectAttachmentUIMissile extends EffectAttachmentUI {
    // 导弹的弧度
    private float arc;

    /**
     * 构造一个新的 EffectAttachmentUIMissile 对象。
     *
     * @param modelPath       特效模型的路径。
     * @param attachmentPoint 特效附着点的列表。
     * @param arc             导弹的弧度。
     */
    public EffectAttachmentUIMissile(String modelPath, List<String> attachmentPoint, float arc) {
        // 调用父类的构造函数来初始化基本属性
        super(modelPath, attachmentPoint);
        // 初始化导弹的弧度
        this.arc = arc;
    }

    /**
     * 获取导弹的弧度。
     *
     * @return 导弹的弧度。
     */
    public float getArc() {
        return arc;
    }
}
