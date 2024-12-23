package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import com.badlogic.gdx.graphics.Texture;

/**
 * 表示一个单位图标的用户界面组件，继承自 IconUI 类，并添加了复活和觉醒提示功能。
 */
public class UnitIconUI extends IconUI {
    // 存储复活提示的字符串
    private final String reviveTip;
    // 存储觉醒提示的字符串
    private final String awakenTip;

    /**
     * 构造一个新的 UnitIconUI 对象。
     *
     * @param icon            单位图标的纹理。
     * @param iconDisabled    单位图标禁用时的纹理。
     * @param buttonPositionX 按钮在 X 轴上的位置。
     * @param buttonPositionY 按钮在 Y 轴上的位置。
     * @param toolTip         工具提示文本。
     * @param uberTip         超级提示文本。
     * @param hotkey          热键字符。
     * @param reviveTip       复活提示文本。
     * @param awakenTip       觉醒提示文本。
     */
    public UnitIconUI(final Texture icon, final Texture iconDisabled, final int buttonPositionX,
                      final int buttonPositionY, final String toolTip, final String uberTip, final char hotkey,
                      final String reviveTip, final String awakenTip) {
        // 调用父类的构造函数来初始化基本属性
        super(icon, iconDisabled, buttonPositionX, buttonPositionY, toolTip, uberTip, hotkey);
        // 初始化复活提示字段
        this.reviveTip = reviveTip;
        // 初始化觉醒提示字段
        this.awakenTip = awakenTip;
    }

    /**
     * 获取复活提示文本。
     *
     * @return 复活提示文本。
     */
    public String getReviveTip() {
        return this.reviveTip;
    }

    /**
     * 获取觉醒提示文本。
     *
     * @return 觉醒提示文本。
     */
    public String getAwakenTip() {
        return this.awakenTip;
    }
}
