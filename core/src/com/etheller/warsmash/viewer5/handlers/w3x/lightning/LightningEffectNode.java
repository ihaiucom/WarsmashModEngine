package com.etheller.warsmash.viewer5.handlers.w3x.lightning;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.etheller.warsmash.viewer5.*;
// 定义一个名为LightningEffectNode的类，继承自BatchedInstance
public class LightningEffectNode extends BatchedInstance {
    // 定义一个LightningEffectNode类型的友元节点
    protected LightningEffectNode friend;
    // 定义一个布尔值，表示闪电效果是否显示
    protected boolean showing;
    // 定义一个布尔值，表示是否是闪电效果的源头
    protected boolean source;
    // 定义一个浮点数，表示纹理动画的位置
    protected float textureAnimationPosition;
    // 定义一个浮点数组，表示闪电效果的颜色（RGBA）
    protected float[] color;
    // 定义一个浮点数，表示闪电效果的剩余生命周期
    protected float lifeSpanRemaining;

    // 构造函数，接收一个LightningEffectModel对象作为参数
    public LightningEffectNode(LightningEffectModel model) {
        // 调用父类的构造函数
        super(model);
        // 初始化颜色数组
        this.color = new float[4];
    }

    // 设置友元节点的方法
    public void setFriend(LightningEffectNode friend) {
        this.friend = friend;
    }

    // 获取显示状态的方法
    public boolean isShowing() {
        return showing;
    }

    // 设置显示状态的方法
    public void setShowing(boolean showing) {
        this.showing = showing;
    }

    // 获取是否是源头的方法
    public boolean isSource() {
        return source;
    }

    // 获取源头节点的方法
    public LightningEffectNode getSource() {
        if (!this.source) {
            return friend;
        }
        return this;
    }

    // 设置是否是源头的方法
    public void setSource(boolean source) {
        this.source = source;
    }

    // 设置颜色的方法，接收一个Color对象作为参数
    public void setColor(Color color) {
        this.color[0] = color.r;
        this.color[1] = color.g;
        this.color[2] = color.b;
        this.color[3] = color.a;
    }

    // 设置颜色的方法，接收四个浮点数作为参数（RGBA）
    public void setColor(float r, float g, float b, float a) {
        this.color[0] = r;
        this.color[1] = g;
        this.color[2] = b;
        this.color[3] = a;
    }

    // 更新动画的方法
    @Override
    public void updateAnimations(float dt) {
        // 如果当前节点显示，友元节点不显示且是源头，则更新友元节点的动画
        if (this.showing && !this.friend.showing && this.friend.source) {
            this.friend.updateAnimations(dt);
        }
        else {
            // 获取模型对象并计算纹理动画位置
            final LightningEffectModel model = (LightningEffectModel) this.model;
            float textureCoordinateSpeed = 3.5f;
            textureAnimationPosition += dt * model.getTexCoordScale() * textureCoordinateSpeed;
            textureAnimationPosition = (((textureAnimationPosition) % 1.0f) + 1.0f) % 1.0f;
            // 如果剩余生命周期大于0，则更新生命周期和透明度
            if(lifeSpanRemaining > 0) {
                lifeSpanRemaining -= dt;
                if(lifeSpanRemaining <= 0) {
                    lifeSpanRemaining = 0;
                }
                color[3] = lifeSpanRemaining / model.getDuration();
            }
        }
    }

    // 清除发射对象的方法（空实现）
    @Override
    public void clearEmittedObjects() {

    }

    // 更新灯光的方法（空实现）
    @Override
    protected void updateLights(Scene scene2) {
    }

    // 渲染不透明物体的方法（空实现）
    @Override
    public void renderOpaque(Matrix4 mvp) {
    }

    // 渲染半透明物体的方法（空实现）
    @Override
    public void renderTranslucent() {

    }

    // 加载资源的方法
    @Override
    public void load() {
        final LightningEffectModel model = (LightningEffectModel) this.model;
        // 复制模型中的颜色到当前节点的颜色数组
        System.arraycopy(model.getColor(), 0, color, 0, color.length);
    }

    // 获取渲染批次的方法
    @Override
    protected RenderBatch getBatch(TextureMapper textureMapper) {
        return new LightningEffectBatch(this.scene, this.model, textureMapper);
    }

    // 设置可替换纹理的方法（抛出异常，表示不支持）
    @Override
    public void setReplaceableTexture(int replaceableTextureId, String replaceableTextureFile) {
        throw new UnsupportedOperationException("NOT API");
    }

    // 设置高清可替换纹理的方法（抛出异常，表示不支持）
    @Override
    public void setReplaceableTextureHD(int replaceableTextureId, String replaceableTextureFile) {
        throw new UnsupportedOperationException("NOT API");
    }

    // 移除灯光的方法（空实现）
    @Override
    protected void removeLights(Scene scene2) {

    }

    // 设置剩余生命周期的方法
    public void setLifeSpanRemaining(float duration) {
        this.lifeSpanRemaining = duration;
    }
}
