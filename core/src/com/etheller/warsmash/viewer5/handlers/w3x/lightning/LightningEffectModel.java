package com.etheller.warsmash.viewer5.handlers.w3x.lightning;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.*;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
// 定义一个名为LightningEffectModel的类，继承自Model类，并指定泛型参数为LightningEffectModelHandler
public class LightningEffectModel extends Model<LightningEffectModelHandler> {
    // 定义类的私有成员变量
    private final War3ID typeId; // 类型ID
    private final String textureFilePath; // 纹理文件路径
    private final float avgSegLen; // 平均线段长度
    private final float width; // 宽度
    private final float[] color; // 颜色数组
    private final float noiseScale; // 噪声比例
    private final float texCoordScale; // 纹理坐标比例
    private final float duration; // 持续时间
    private final int version; // 版本号
    public int elementBuffer; // 元素缓冲区
    protected Texture texture; // 纹理对象

    // 构造函数，用于初始化LightningEffectModel对象
    public LightningEffectModel(LightningEffectModelHandler handler, ModelViewer viewer, String extension,
                                PathSolver pathSolver, String fetchUrl, War3ID typeId, String textureFilePath,
                                float avgSegLen, float width, float[] color, float noiseScale, float texCoordScale,
                                float duration, int version) {
        // 调用父类构造函数
        super(handler, viewer, extension, pathSolver, fetchUrl);
        // 初始化成员变量
        this.typeId = typeId;
        this.textureFilePath = textureFilePath;
        this.avgSegLen = avgSegLen;
        this.width = width;
        this.color = color;
        this.noiseScale = noiseScale;
        this.texCoordScale = texCoordScale;
        this.duration = duration;
        this.version = version;
    }

    // 获取类型ID的方法
    public War3ID getTypeId() {
        return typeId;
    }

    // 获取纹理文件路径的方法
    public String getTextureFilePath() {
        return textureFilePath;
    }

    // 获取纹理对象的方法
    public Texture getTexture() {
        return texture;
    }

    // 获取平均线段长度的方法
    public float getAvgSegLen() {
        return avgSegLen;
    }

    // 获取宽度的方法
    public float getWidth() {
        return width;
    }

    // 获取颜色数组的方法
    public float[] getColor() {
        return color;
    }

    // 获取噪声比例的方法
    public float getNoiseScale() {
        return noiseScale;
    }

    // 获取纹理坐标比例的方法
    public float getTexCoordScale() {
        return texCoordScale;
    }

    // 获取持续时间的方法
    public float getDuration() {
        return duration;
    }

    // 获取版本号的方法
    public int getVersion() {
        return version;
    }

    // 创建模型实例的方法，重写父类的方法
    @Override
    protected ModelInstance createInstance(int type) {
        return new LightningEffectNode(this);
    }

    // 延迟加载的方法，目前为空实现
    @Override
    protected void lateLoad() {

    }

    // 加载资源的方法，重写父类的方法
    @Override
    protected void load(InputStream src, Object options) {
        String path = textureFilePath;
        // 如果纹理文件路径为空，则使用默认的白色纹理
        if ("".equals(path)) {
            path = "Textures\\white.blp";
        }

        try {
            // 加载纹理
            this.texture = (Texture) viewer.load(path, pathSolver, solverParams);
            this.texture.setWrapS(true);

            // 设置模型的边界
            this.bounds.fromExtents(new float[]{-width, -width, -width}, new float[]{width, width, width}, width);

            // 获取OpenGL对象
            GL20 gl = Gdx.gl;
            // 生成元素缓冲区
            elementBuffer = gl.glGenBuffer();
            // 绑定元素缓冲区到GL_ELEMENT_ARRAY_BUFFER目标
            gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, elementBuffer);
        }
        catch (Exception exc) {
            // 打印异常信息
            exc.printStackTrace();
            // 加载默认的白色纹理
            this.texture = (Texture) viewer.load("Textures\\white.bl3", pathSolver, solverParams);
        }
    }

    // 错误处理方法，打印异常信息
    @Override
    protected void error(Exception e) {
        e.printStackTrace();
    }
}
