package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;
/**
 * 表示一个物品的用户界面组件，包含图标、名称、描述和拖动时的图标路径。
 */
public class ItemUI {
    // 物品的图标用户界面组件
    private final IconUI iconUI;
    // 物品的名称
    private final String name;
    // 物品的描述
    private final String description;
    // 物品拖动时的图标路径
    private final String itemIconPathForDragging;

    /**
     * 构造一个新的 ItemUI 对象。
     *
     * @param iconUI                  物品的图标用户界面组件。
     * @param name                    物品的名称。
     * @param description             物品的描述。
     * @param itemIconPathForDragging 物品拖动时的图标路径。
     */
    public ItemUI(final IconUI iconUI, final String name, final String description,
                  final String itemIconPathForDragging) {
        // 初始化物品的图标用户界面组件
        this.iconUI = iconUI;
        // 初始化物品的名称
        this.name = name;
        // 初始化物品的描述
        this.description = description;
        // 初始化物品拖动时的图标路径
        this.itemIconPathForDragging = itemIconPathForDragging;
    }

    /**
     * 获取物品的图标用户界面组件。
     *
     * @return 物品的图标用户界面组件。
     */
    public IconUI getIconUI() {
        return this.iconUI;
    }

    /**
     * 获取物品的名称。
     *
     * @return 物品的名称。
     */
    public String getName() {
        return this.name;
    }

    /**
     * 获取物品的描述。
     *
     * @return 物品的描述。
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 获取物品拖动时的图标路径。
     *
     * @return 物品拖动时的图标路径。
     */
    public String getItemIconPathForDragging() {
        return this.itemIconPathForDragging;
    }
}
