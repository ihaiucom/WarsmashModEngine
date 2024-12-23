package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;

import com.badlogic.gdx.graphics.GL30;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.ImageUtils.AnyExtensionImage;
// 定义地面纹理类
public class GroundTexture {
    // 纹理ID
    public int id;
    // 瓦片ID
    private String tileId;
    // 瓦片大小
    private int tileSize;
    // 是否可建造
    private boolean buildable;
    // 是否扩展
    public boolean extended;

    // 构造函数，初始化地面纹理
    public GroundTexture(final String path, final Element terrainTileInfo, final DataSource dataSource, final GL30 gl) throws IOException {
        // 如果地形瓦片信息不为空，则从中获取瓦片ID和可建造状态
        if(terrainTileInfo != null) {
            tileId = terrainTileInfo.getId();
            String buildableFieldValue = terrainTileInfo.getField("buildable");
            // 如果buildable字段为空，则默认不可建造，否则根据字段值判断
            this.buildable = buildableFieldValue.isEmpty() ? false : Integer.parseInt(buildableFieldValue) == 1;
        } else {
            // 如果地形瓦片信息为空，则默认可建造
            this.buildable = true;
        }
        // 获取图像信息
        final AnyExtensionImage imageInfo = ImageUtils.getAnyExtensionImageFixRGB(dataSource, path, "ground texture: " + tileId);
        // 加载图像
        loadImage(path, gl, imageInfo.getImageData(), imageInfo.isNeedsSRGBFix());
    }

    // 获取是否可建造状态
    public boolean isBuildable() {
        return buildable;
    }

    // 加载图像到纹理
    private void loadImage(final String path, final GL30 gl, final BufferedImage image, final boolean sRGBFix) {
        // 如果图像为空，抛出异常
        if (image == null) {
            throw new IllegalStateException(tileId + ": Missing ground texture: " + path);
        }
        // 获取纹理缓冲区
        final Buffer buffer = ImageUtils.getTextureBuffer(sRGBFix ? ImageUtils.forceBufferedImagesRGB(image) : image);
        // 获取图像宽度和高度
        final int width = image.getWidth();
        final int height = image.getHeight();

        // 计算瓦片大小
        this.tileSize = (int) (height * 0.25);
        // 判断是否扩展
        this.extended = (width > height);

        // 生成纹理ID
        this.id = gl.glGenTexture();
        // 绑定纹理
        gl.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, this.id);
        // 设置纹理参数
        gl.glTexImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, GL30.GL_RGBA8, this.tileSize, this.tileSize,
                this.extended ? 32 : 16, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, null);
        gl.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR_MIPMAP_LINEAR);
        gl.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);

        // 设置像素存储参数
        gl.glPixelStorei(GL30.GL_UNPACK_ROW_LENGTH, width);
        // 将图像数据上传到纹理
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                buffer.position(((y * this.tileSize * width) + (x * this.tileSize)) * 4);
                gl.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, (y * 4) + x, this.tileSize, this.tileSize, 1,
                        GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, buffer);

                // 如果纹理是扩展的，上传额外的图像数据
                if (this.extended) {
                    buffer.position(((y * this.tileSize * width) + ((x + 4) * this.tileSize)) * 4);
                    gl.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, (y * 4) + x + 16, this.tileSize,
                            this.tileSize, 1, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, buffer);
                }
            }
        }
        // 重置像素存储参数
        gl.glPixelStorei(GL30.GL_UNPACK_ROW_LENGTH, 0);
        // 生成Mipmap
        gl.glGenerateMipmap(GL30.GL_TEXTURE_2D_ARRAY);
    }
}
