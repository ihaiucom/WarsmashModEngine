package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

// 定义一个名为 RenderEffect 的接口，用于处理渲染效果的更新
public interface RenderEffect {
    // 定义一个方法 updateAnimations，该方法用于更新动画效果
    // 参数 war3MapViewer 是一个 War3MapViewer 对象，提供了地图查看器的相关功能
    // 参数 deltaTime 是一个 float 类型的值，表示自上次更新以来的时间差，用于动画的平滑过渡
    boolean updateAnimations(final War3MapViewer war3MapViewer, float deltaTime);
}
