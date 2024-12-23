package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import java.util.List;

/**
 * 表示一个特效附着的用户界面组件，包含模型路径和附着点列表。
 */
public class EffectAttachmentUI {
    // 特效模型的路径
    private final String modelPath;
    // 特效附着点的列表
    private final List<String> attachmentPoint;

    /**
     * 构造一个新的 EffectAttachmentUI 对象。
     *
     * @param modelPath       特效模型的路径。
     * @param attachmentPoint 特效附着点的列表。
     */
    public EffectAttachmentUI(final String modelPath, final List<String> attachmentPoint) {
        // 初始化特效模型的路径
        this.modelPath = modelPath;
        // 初始化特效附着点的列表
        this.attachmentPoint = attachmentPoint;
    }

    /**
     * 获取特效模型的路径。
     *
     * @return 特效模型的路径。
     */
    public String getModelPath() {
        return this.modelPath;
    }

    /**
     * 获取特效附着点的列表。
     *
     * @return 特效附着点的列表。
     */
    public List<String> getAttachmentPoint() {
        return this.attachmentPoint;
    }
}
