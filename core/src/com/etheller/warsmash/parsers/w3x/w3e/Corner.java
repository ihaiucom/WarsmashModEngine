package com.etheller.warsmash.parsers.w3x.w3e;

import java.io.IOException;

import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

/**
 * A tile corner.
 */
public class Corner {
    // 地面高度
    private float groundHeight;
    // 水面高度
    private float waterHeight;
    // 地图边缘标志
    private int mapEdge;
    // 斜坡标志
    private int ramp;
    // 荒芜标志
    private int blight;
    // 水体标志
    private int water;
    // 边界标志
    private int boundary;
    // 地面纹理
    private int groundTexture;
    // 悬崖变化
    private int cliffVariation;
    // 地面变化
    private int groundVariation;
    // 悬崖纹理
    private int cliffTexture;
    // 层高度
    private int layerHeight;

    // 默认构造函数
    public Corner() {
        // TODO Auto-generated constructor stub
    }

    // 拷贝构造函数
    public Corner(final Corner other) {
        this.groundHeight = other.groundHeight;
        this.waterHeight = other.waterHeight;
        this.mapEdge = other.mapEdge;
        this.ramp = other.ramp;
        this.blight = other.blight;
        this.water = other.water;
        this.boundary = other.boundary;
        this.groundTexture = other.groundTexture;
        this.cliffVariation = other.cliffVariation;
        this.groundVariation = other.groundVariation;
        this.cliffTexture = other.cliffTexture;
        this.layerHeight = other.layerHeight;
    }

    // 从数据流中加载Corner对象的数据
    public void load(final LittleEndianDataInputStream stream) throws IOException {
        this.groundHeight = (stream.readShort() - 8192) / (float) 512;

        final short waterAndEdge = stream.readShort();
        this.waterHeight = ((waterAndEdge & 0x3FFF) - 8192) / (float) 512;
        this.mapEdge = waterAndEdge & 0x4000;

        final short textureAndFlags = ParseUtils.readUInt8(stream);

        this.ramp = textureAndFlags & 0b00010000;
        this.blight = textureAndFlags & 0b00100000;
        this.water = textureAndFlags & 0b01000000;
        this.boundary = textureAndFlags & 0b10000000;

        this.groundTexture = textureAndFlags & 0b00001111;

        final short variation = ParseUtils.readUInt8(stream);

        this.cliffVariation = (variation & 0b11100000) >>> 5;
        this.groundVariation = variation & 0b00011111;

        final short cliffTextureAndLayer = ParseUtils.readUInt8(stream);

        this.cliffTexture = (cliffTextureAndLayer & 0b11110000) >>> 4;
        this.layerHeight = cliffTextureAndLayer & 0b00001111;
    }

    // 将Corner对象的数据保存到数据流中
    public void save(final LittleEndianDataOutputStream stream) throws IOException {
        stream.writeShort((short) ((this.groundHeight * 512f) + 8192f));
        stream.writeShort((short) ((this.waterHeight * 512f) + 8192f + (this.mapEdge << 14)));
        ParseUtils.writeUInt8(stream, (short) ((this.ramp << 4) | (this.blight << 5) | (this.water << 6)
                | (this.boundary << 7) | this.groundTexture));
        ParseUtils.writeUInt8(stream, (short) ((this.cliffVariation << 5) | this.groundVariation));
        ParseUtils.writeUInt8(stream, (short) ((this.cliffTexture << 4) + this.layerHeight));
    }

    // 获取地面高度
    public float getGroundHeight() {
        return this.groundHeight;
    }

    // 获取水面高度
    public float getWaterHeight() {
        return this.waterHeight;
    }

    // 获取地图边缘标志
    public int getMapEdge() {
        return this.mapEdge;
    }

    // 获取斜坡标志
    public int getRamp() {
        return this.ramp;
    }

    // 判断是否有斜坡
    public boolean isRamp() {
        return this.ramp != 0;
    }

    // 设置斜坡标志
    public void setRamp(final int ramp) {
        this.ramp = ramp;
    }

    // 获取荒芜标志
    public int getBlight() {
        return this.blight;
    }

    // 设置荒芜标志
    public boolean setBlight(final boolean flag) {
        final int newBlightValue = flag ? 0b00100000 : 0;
        if (this.blight != newBlightValue) {
            this.blight = newBlightValue;
            return true;
        }
        return false;
    }

    // 获取水体标志
    public int getWater() {
        return this.water;
    }

    // 获取边界标志
    public int getBoundary() {
        return this.boundary;
    }

    // 获取地面纹理
    public int getGroundTexture() {
        return this.groundTexture;
    }

    // 获取悬崖变化
    public int getCliffVariation() {
        return this.cliffVariation;
    }

    // 获取地面变化
    public int getGroundVariation() {
        return this.groundVariation;
    }

    // 获取悬崖纹理
    public int getCliffTexture() {
        return this.cliffTexture;
    }

    // 设置悬崖纹理
    public void setCliffTexture(final int cliffTexture) {
        this.cliffTexture = cliffTexture;
    }

    // 获取层高度
    public int getLayerHeight() {
        return this.layerHeight;
    }

    // 计算最终地面高度
    public float computeFinalGroundHeight() {
        return (this.groundHeight + this.layerHeight) - 2.0f;
    }

    // 计算最终水面高度
    public float computeFinalWaterHeight(final float waterOffset) {
        return this.waterHeight + waterOffset;
    }

    // 设置水面高度
    public void setWaterHeight(float waterHeight) {
        this.waterHeight = waterHeight;
    }
}

