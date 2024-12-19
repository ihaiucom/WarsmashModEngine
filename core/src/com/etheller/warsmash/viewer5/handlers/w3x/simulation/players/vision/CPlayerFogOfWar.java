package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import java.nio.ByteBuffer;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
public class CPlayerFogOfWar {
    // 定义路径规划的比率常量
    public static final int PATHING_RATIO = 4;
    // 定义网格步长常量
    public static final int GRID_STEP = PATHING_RATIO * 32;
    // 定义迷雾缓冲区的宽度
    private final int width;
    // 定义迷雾缓冲区的高度
    private final int height;
    // 定义迷雾缓冲区
    private final ByteBuffer fogOfWarBuffer;

    /**
     * 构造函数，初始化迷雾缓冲区
     * @param pathingGrid 路径规划网格
     */
    public CPlayerFogOfWar(final PathingGrid pathingGrid) {
        // 计算迷雾缓冲区的宽度和高度
        width = (pathingGrid.getWidth() / PATHING_RATIO) + 1;
        height = (pathingGrid.getHeight() / PATHING_RATIO) + 1;
        // 计算迷雾缓冲区的长度
        final int fogOfWarBufferLen = width * height;
        // 分配直接字节缓冲区
        this.fogOfWarBuffer = ByteBuffer.allocateDirect(fogOfWarBufferLen);
        // 清空缓冲区
        fogOfWarBuffer.clear();
        // 填充缓冲区为-1
        while (fogOfWarBuffer.hasRemaining()) {
            fogOfWarBuffer.put((byte) -1);
        }
        // 再次清空缓冲区
        fogOfWarBuffer.clear();
    }

    // 获取迷雾缓冲区的宽度
    public int getWidth() {
        return width;
    }

    // 获取迷雾缓冲区的高度
    public int getHeight() {
        return height;
    }

    // 获取迷雾缓冲区
    public ByteBuffer getFogOfWarBuffer() {
        return fogOfWarBuffer;
    }

    /**
     * 根据坐标获取迷雾状态
     * @param pathingGrid 路径规划网格
     * @param x 坐标x
     * @param y 坐标y
     * @return 迷雾状态
     */
    public byte getState(final PathingGrid pathingGrid, final float x, final float y) {
        final int indexX = pathingGrid.getFogOfWarIndexX(x);
        final int indexY = pathingGrid.getFogOfWarIndexY(y);
        return getState(indexX, indexY);
    }

    /**
     * 根据索引获取迷雾状态
     * @param indexX 索引x
     * @param indexY 索引y
     * @return 迷雾状态
     */
    public byte getState(final int indexX, final int indexY) {
        final int index = (indexY * getWidth()) + indexX;
        // 检查索引是否有效
        if ((index >= 0) && (index < fogOfWarBuffer.capacity())) {
            return fogOfWarBuffer.get(index);
        }
        return 0;
    }

    /**
     * 根据坐标设置迷雾状态
     * @param pathingGrid 路径规划网格
     * @param x 坐标x
     * @param y 坐标y
     * @param fogOfWarState 迷雾状态
     */
    public void setState(final PathingGrid pathingGrid, final float x, final float y, final byte fogOfWarState) {
        final int indexX = pathingGrid.getFogOfWarIndexX(x);
        final int indexY = pathingGrid.getFogOfWarIndexY(y);
        setState(indexX, indexY, fogOfWarState);
    }

    /**
     * 根据索引设置迷雾状态
     * @param indexX 索引x
     * @param indexY 索引y
     * @param fogOfWarState 迷雾状态
     */
    public void setState(final int indexX, final int indexY, final byte fogOfWarState) {
        final int writeIndex = (indexY * getWidth()) + indexX;
        // 检查索引是否有效
        if ((writeIndex >= 0) && (writeIndex < fogOfWarBuffer.capacity())) {
            fogOfWarBuffer.put(writeIndex, fogOfWarState);
        }
    }

    /**
     * 将所有可见区域转换为迷雾状态
     */
    public void convertVisibleToFogged() {
        for (int i = 0; i < fogOfWarBuffer.capacity(); i++) {
            if (fogOfWarBuffer.get(i) == 0) {
                fogOfWarBuffer.put(i, (byte) 127);
            }
        }
    }
}
