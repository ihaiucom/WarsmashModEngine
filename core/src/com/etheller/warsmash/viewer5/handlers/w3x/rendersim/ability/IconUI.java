package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import com.badlogic.gdx.graphics.Texture;

// 资源：图标、tip、热键等信息
public class IconUI {
	// 定义一个图标对象，用于显示能力的图标
	private final Texture icon;

	// 定义一个禁用状态下的图标对象，当能力不可用时显示
	private final Texture iconDisabled;

	// 定义图标按钮在界面中的X轴位置
	private final int buttonPositionX;

	// 定义图标按钮在界面中的Y轴位置
	private final int buttonPositionY;

	// 定义图标的工具提示信息，通常在鼠标悬停时显示
	private final String toolTip;

	// 定义图标的扩展提示信息，可能包含更详细的描述，通常在用户点击帮助时显示
	private final String uberTip;

	// 定义与图标关联的热键，用户可以通过按下这个键来快速激活相关能力
	private final char hotkey;


	public IconUI(final Texture icon, final Texture iconDisabled, final int buttonPositionX, final int buttonPositionY,
			final String toolTip, final String uberTip, final char hotkey) {
		this.icon = icon;
		this.iconDisabled = iconDisabled;
		this.buttonPositionX = buttonPositionX;
		this.buttonPositionY = buttonPositionY;
		this.toolTip = toolTip;
		this.uberTip = uberTip;
		this.hotkey = hotkey;
	}

	public Texture getIcon() {
		return this.icon;
	}

	public Texture getIconDisabled() {
		return this.iconDisabled;
	}

	public int getButtonPositionX() {
		return this.buttonPositionX;
	}

	public int getButtonPositionY() {
		return this.buttonPositionY;
	}

	public String getToolTip() {
		return this.toolTip;
	}

	public String getUberTip() {
		return this.uberTip;
	}

	public char getHotkey() {
		return this.hotkey;
	}
}
