package com.etheller.warsmash.viewer5.handlers.w3x;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.SceneLightInstance;
import com.etheller.warsmash.viewer5.SceneLightManager;
import com.etheller.warsmash.viewer5.StaticSceneLightInstance;
import com.etheller.warsmash.viewer5.gl.DataTexture;
// 定义一个W3xScenePortraitLightManager类，实现了SceneLightManager和W3xSceneLightManager接口
public class W3xScenePortraitLightManager implements SceneLightManager, W3xSceneLightManager {
    // 定义一个ModelViewer对象，用于处理模型的渲染
    private final ModelViewer viewer;
    // 定义一个SceneLightInstance对象的列表，用于存储场景中的光源实例
    public final List<SceneLightInstance> lights;
    // 定义一个FloatBuffer对象，用于存储光源数据的副本
    private FloatBuffer lightDataCopyHeap;
    // 定义一个DataTexture对象，用于存储单位光源的纹理
    private final DataTexture unitLightsTexture;
    // 定义一个整型变量，用于记录单位光源的数量
    private int unitLightCount;

    // 构造函数，接收一个ModelViewer对象作为参数
    public W3xScenePortraitLightManager(final ModelViewer viewer) {
        this.viewer = viewer; // 初始化ModelViewer对象
        this.lights = new ArrayList<>(); // 初始化光源实例列表
        this.unitLightsTexture = new DataTexture(viewer.gl, 4, 4, 1); // 初始化单位光源纹理
        this.lightDataCopyHeap = ByteBuffer.allocateDirect(16 * 1 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); // 初始化光源数据副本缓冲区
    }

    // 构造函数，接收一个ModelViewer对象和一个Vector3对象作为参数
    public W3xScenePortraitLightManager(final ModelViewer viewer, final Vector3 lightDirection) {
        this(viewer); // 调用另一个构造函数初始化对象
        add(StaticSceneLightInstance.createDefault(lightDirection)); // 添加默认光源实例
    }

    // 实现SceneLightManager接口的add方法，用于添加光源实例
    @Override
    public void add(final SceneLightInstance lightInstance) {
        this.lights.add(lightInstance); // 将光源实例添加到列表中
    }

    // 实现SceneLightManager接口的remove方法，用于移除光源实例
    @Override
    public void remove(final SceneLightInstance lightInstance) {
        this.lights.remove(lightInstance); // 从列表中移除光源实例
    }

    // 实现SceneLightManager接口的update方法，用于更新光源数据
    @Override
    public void update() {
        final int numberOfLights = this.lights.size() + 1; // 计算光源总数
        final int bytesNeeded = numberOfLights * 4 * 16; // 计算所需字节数
        // 如果所需字节数大于当前缓冲区容量，则重新分配缓冲区并更新纹理
        if (bytesNeeded > (this.lightDataCopyHeap.capacity() * 4)) {
            this.lightDataCopyHeap = ByteBuffer.allocateDirect(bytesNeeded).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.unitLightsTexture.reserve(4, numberOfLights);
        }

        this.unitLightCount = 0; // 重置单位光源计数
        this.lightDataCopyHeap.clear(); // 清空光源数据缓冲区
        int offset = 0;
        // 遍历光源实例列表，绑定光源数据并更新计数
        for (final SceneLightInstance light : this.lights) {
            light.bind(offset, this.lightDataCopyHeap);
            offset += 16;
            this.unitLightCount++;
        }
        this.lightDataCopyHeap.limit(offset); // 设置缓冲区限制
        // 更新单位光源纹理
        this.unitLightsTexture.bindAndUpdate(this.lightDataCopyHeap, 4, this.unitLightCount);
    }

    // 实现SceneLightManager接口的getUnitLightsTexture方法，返回单位光源纹理
    @Override
    public DataTexture getUnitLightsTexture() {
        return this.unitLightsTexture;
    }

    // 实现SceneLightManager接口的getUnitLightCount方法，返回单位光源数量
    @Override
    public int getUnitLightCount() {
        return this.unitLightCount;
    }

    // 实现SceneLightManager接口的getTerrainLightsTexture方法，返回地形光源纹理（本类中未实现）
    @Override
    public DataTexture getTerrainLightsTexture() {
        return null;
    }

    // 实现SceneLightManager接口的getTerrainLightCount方法，返回地形光源数量（本类中未实现）
    @Override
    public int getTerrainLightCount() {
        return 0;
    }
}
