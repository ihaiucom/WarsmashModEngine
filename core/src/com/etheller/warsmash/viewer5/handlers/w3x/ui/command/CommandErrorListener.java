package com.etheller.warsmash.viewer5.handlers.w3x.ui.command;

import com.etheller.warsmash.util.War3ID;
// 定义一个名为CommandErrorListener的接口，用于处理命令错误相关的事件监听

public interface CommandErrorListener {
    // 显示界面错误的回调方法，接收玩家索引和错误信息作为参数
    void showInterfaceError(int playerIndex, String message);

    // 显示命令错误的方法，但不播放声音，接收玩家索引和错误信息作为参数
    void showCommandErrorWithoutSound(int playerIndex, String message);

    // 显示升级完成的提示信息，接收玩家索引、排队的原始代码（War3ID）和等级作为参数
    void showUpgradeCompleteAlert(int playerIndex, War3ID queuedRawcode, int level);
}
