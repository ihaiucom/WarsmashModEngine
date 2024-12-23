package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.gl.Extensions;
import com.etheller.warsmash.viewer5.gl.WebGL;
public class DynamicShadowManager {
    // 静态变量，用于标记是否启用阴影映射
    public static boolean IS_SHADOW_MAPPING = false;

    // 用于存储阴影向量的向量
    private final Vector3 shadowVector = new Vector3();
    // 深度投影矩阵
    private final Matrix4 depthProjectionMatrix = new Matrix4();
    // 深度视图矩阵
    private final Matrix4 depthViewMatrix = new Matrix4();
    // 深度模型矩阵
    private final Matrix4 depthModelMatrix = new Matrix4();
    // 深度MVP矩阵
    private final Matrix4 depthMVP = new Matrix4();
    // 偏移矩阵
    private final Matrix4 biasMatrix = new Matrix4();
    // 深度偏移MVP矩阵
    private final Matrix4 depthBiasMVP = new Matrix4();

    // 设置阴影映射的准备工作
    public boolean setup(final WebGL webGL) {
        final GL30 gl = Gdx.gl30;
        // 生成并绑定帧缓冲区
        this.framebufferName = gl.glGenFramebuffer();
        gl.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.framebufferName);

        // 生成深度纹理并设置其参数
        this.depthTexture = gl.glGenTexture();
        gl.glBindTexture(GL30.GL_TEXTURE_2D, this.depthTexture);
        gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT16, 1024, 1024, 0, GL30.GL_DEPTH_COMPONENT,
                GL30.GL_FLOAT, null);
        gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
        gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
        gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
        // 将深度纹理附加到帧缓冲区
        Extensions.dynamicShadowExtension.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                this.depthTexture, 0);

        // 不绘制颜色缓冲区
        Extensions.dynamicShadowExtension.glDrawBuffer(GL30.GL_NONE);

        // 检查帧缓冲区是否完整
        if (gl.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            return false;
        }
        // 解绑帧缓冲区
        Gdx.gl30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        return true;
    }

    // 准备阴影矩阵
    public Matrix4 prepareShadowMatrix() {
        final Vector3 lightInvDir = this.shadowVector;
        // 设置光源方向的逆方向
        lightInvDir.set(500f, 2000, 2000);

        // 计算光源视角下的MVP矩阵
        this.depthProjectionMatrix.setToOrtho(-10, 10, -10, 10, -10, 20);
        this.depthViewMatrix.set(this.depthProjectionMatrix);
        this.depthViewMatrix.setToLookAt(lightInvDir, Vector3.Zero, RenderMathUtils.VEC3_UNIT_Y);
        this.depthModelMatrix.idt();
        this.depthMVP.set(this.depthProjectionMatrix).mul(this.depthViewMatrix).mul(this.depthModelMatrix);

        // 设置偏移矩阵
        this.biasMatrix.val[Matrix4.M00] = 0.5f;
        this.biasMatrix.val[Matrix4.M10] = 0.0f;
        this.biasMatrix.val[Matrix4.M20] = 0.0f;
        this.biasMatrix.val[Matrix4.M30] = 0.5f;
        this.biasMatrix.val[Matrix4.M01] = 0.0f;
        this.biasMatrix.val[Matrix4.M11] = 0.5f;
        this.biasMatrix.val[Matrix4.M21] = 0.0f;
        this.biasMatrix.val[Matrix4.M31] = 0.5f;
        this.biasMatrix.val[Matrix4.M02] = 0.0f;
        this.biasMatrix.val[Matrix4.M12] = 0.0f;
        this.biasMatrix.val[Matrix4.M22] = 0.5f;
        this.biasMatrix.val[Matrix4.M32] = 0.5f;
        this.biasMatrix.val[Matrix4.M03] = 0.0f;
        this.biasMatrix.val[Matrix4.M13] = 0.0f;
        this.biasMatrix.val[Matrix4.M23] = 0.0f;
        this.biasMatrix.val[Matrix4.M33] = 1.0f;
        // 计算深度偏移MVP矩阵
        this.depthBiasMVP.set(this.biasMatrix).mul(this.depthMVP);

        return this.depthMVP;
    }

    // 开始阴影映射
    public void beginShadowMap(final WebGL webGL) {
        IS_SHADOW_MAPPING = true;
        // 绑定帧缓冲区
        Gdx.gl30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.framebufferName);
        // 将深度纹理附加到帧缓冲区
        Extensions.dynamicShadowExtension.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                this.depthTexture, 0);
        // 不绘制颜色缓冲区
        Extensions.dynamicShadowExtension.glDrawBuffer(GL30.GL_NONE);
        // 设置视口大小
        Gdx.gl30.glViewport(0, 0, 1024, 1024);
    }

    // 获取深度偏移MVP矩阵
    public Matrix4 getDepthBiasMVP() {
        return this.depthBiasMVP;
    }

    // 结束阴影映射
    public void endShadowMap() {
        IS_SHADOW_MAPPING = false;
        // 解绑帧缓冲区
        Gdx.gl30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    // 获取深度纹理
    public int getDepthTexture() {
        return this.depthTexture;
    }

    // 顶点着色器代码
    public static final String vertexShader = "#version 330 core\r\n" + //
            "\r\n" + //
            "// Input vertex data, different for all executions of this shader.\r\n" + //
            "layout(location = 0) in vec3 vertexPosition_modelspace;\r\n" + //
            "\r\n" + //
            "// Values that stay constant for the whole mesh.\r\n" + //
            "uniform mat4 depthMVP;\r\n" + //
            "\r\n" + //
            "void main(){\r\n" + //
            " gl_Position =  depthMVP * vec4(vertexPosition_modelspace,1);\r\n" + //
            "}";

    // 片段着色器代码
    public static final String fragmentShader = "#version 330 core\r\n" + //
            "\r\n" + //
            "// Ouput data\r\n" + //
            "layout(location = 0) out float fragmentdepth;\r\n" + //
            "\r\n" + //
            "void main(){\r\n" + //
            "    // Not really needed, OpenGL does it anyway\r\n" + //
            "    fragmentdepth = gl_FragCoord.z;\r\n" + //
            "}";

    // 深度纹理ID
    private int depthTexture;
    // 帧缓冲区名称
    private int framebufferName;
}
