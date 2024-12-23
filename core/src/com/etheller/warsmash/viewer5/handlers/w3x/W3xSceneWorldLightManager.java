package com.etheller.warsmash.viewer5.handlers.w3x;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.viewer5.SceneLightInstance;
import com.etheller.warsmash.viewer5.SceneLightManager;
import com.etheller.warsmash.viewer5.gl.DataTexture;
import com.etheller.warsmash.viewer5.handlers.mdx.LightInstance;
// 定义一个管理场景中灯光的类，实现了SceneLightManager和W3xSceneLightManager接口
public class W3xSceneWorldLightManager implements SceneLightManager, W3xSceneLightManager {
    // 存储所有的灯光实例
    public final List<LightInstance> lights;
    // 用于存储灯光数据的浮点缓冲区
    private FloatBuffer lightDataCopyHeap;
    // 单位灯光纹理
    private final DataTexture unitLightsTexture;
    // 地形灯光纹理
    private final DataTexture terrainLightsTexture;
    // 视图查看器实例
    private final War3MapViewer viewer;
    // 地形灯光数量
    private int terrainLightCount;
    // 单位灯光数量
    private int unitLightCount;

    // 构造函数，初始化灯光管理器
    public W3xSceneWorldLightManager(final War3MapViewer viewer) {
        this.viewer = viewer;
        this.lights = new ArrayList<>();
        this.unitLightsTexture = new DataTexture(viewer.gl, 4, 4, 1);
        this.terrainLightsTexture = new DataTexture(viewer.gl, 4, 4, 1);
        // 分配直接字节缓冲区用于存储灯光数据
        this.lightDataCopyHeap = ByteBuffer.allocateDirect(16 * 1 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    // 添加灯光实例到管理器
    @Override
    public void add(final SceneLightInstance lightInstance) {
        // TODO: 重新设计以避免类型转换
        final LightInstance mdxLight = (LightInstance) lightInstance;
        this.lights.add(mdxLight);
    }

    // 从管理器中移除灯光实例
    @Override
    public void remove(final SceneLightInstance lightInstance) {
        // TODO: 重新设计以避免类型转换
        final LightInstance mdxLight = (LightInstance) lightInstance;
        this.lights.remove(mdxLight);
    }

    // 更新灯光数据和纹理
    @Override
    public void update() {
        final int numberOfLights = this.lights.size() + 1;
        final int bytesNeeded = numberOfLights * 4 * 16;
        // 如果需要的字节数大于当前缓冲区的容量，则重新分配更大的缓冲区
        if (bytesNeeded > (this.lightDataCopyHeap.capacity() * 4)) {
            this.lightDataCopyHeap = ByteBuffer.allocateDirect(bytesNeeded).order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            this.unitLightsTexture.reserve(4, numberOfLights);
            this.terrainLightsTexture.reserve(4, numberOfLights);
        }

        // 更新单位灯光数据
        this.unitLightCount = 0;
        this.lightDataCopyHeap.clear();
        int offset = 0;
        if (this.viewer.dncUnit != null && !this.viewer.dncUnit.lights.isEmpty()) {
            this.viewer.dncUnit.lights.get(0).bind(0, this.lightDataCopyHeap);
            offset += 16;
            this.unitLightCount++;
        }
        for (final LightInstance light : this.lights) {
            light.bind(offset, this.lightDataCopyHeap);
            offset += 16;
            this.unitLightCount++;
        }
        this.lightDataCopyHeap.limit(offset);
        this.unitLightsTexture.bindAndUpdate(this.lightDataCopyHeap, 4, this.unitLightCount);

        // 更新地形灯光数据
        this.terrainLightCount = 0;
        this.lightDataCopyHeap.clear();
        offset = 0;
        if (this.viewer.dncTerrain != null && !this.viewer.dncTerrain.lights.isEmpty()) {
            this.viewer.dncTerrain.lights.get(0).bind(0, this.lightDataCopyHeap);
            offset += 16;
            this.terrainLightCount++;
        }
        for (final LightInstance light : this.lights) {
            light.bind(offset, this.lightDataCopyHeap);
            offset += 16;
            this.terrainLightCount++;
        }
        this.lightDataCopyHeap.limit(offset);
        this.terrainLightsTexture.bindAndUpdate(this.lightDataCopyHeap, 4, this.terrainLightCount);
    }

    // 获取单位灯光纹理
    @Override
    public DataTexture getUnitLightsTexture() {
        return this.unitLightsTexture;
    }

    // 获取单位灯光数量
    @Override
    public int getUnitLightCount() {
        return this.unitLightCount;
    }

    // 获取地形灯光纹理
    @Override
    public DataTexture getTerrainLightsTexture() {
        return this.terrainLightsTexture;
    }

    // 获取地形灯光数量
    @Override
    public int getTerrainLightCount() {
        return this.terrainLightCount;
    }
}
