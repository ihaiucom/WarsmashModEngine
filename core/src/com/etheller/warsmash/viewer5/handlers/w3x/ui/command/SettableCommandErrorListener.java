package com.etheller.warsmash.viewer5.handlers.w3x.ui.command;

import com.etheller.warsmash.util.War3ID;

// 定义一个实现了CommandErrorListener接口的类SettableCommandErrorListener
public class SettableCommandErrorListener implements CommandErrorListener {
    // 声明一个私有的CommandErrorListener类型的成员变量delegate，用于委托处理错误信息
    private CommandErrorListener delegate;

    // 实现CommandErrorListener接口中的showInterfaceError方法
    // 当需要向玩家展示界面错误时调用此方法
    @Override
    public void showInterfaceError(final int playerIndex, final String message) {
        // 将错误信息委托给delegate对象处理
        this.delegate.showInterfaceError(playerIndex, message);
    }

    // 实现CommandErrorListener接口中的showCommandErrorWithoutSound方法
    // 当需要向玩家展示命令错误但不播放声音时调用此方法
    @Override
    public void showCommandErrorWithoutSound(int playerIndex, String message) {
        // 将错误信息委托给delegate对象处理，且不播放声音
        this.delegate.showCommandErrorWithoutSound(playerIndex, message);
    }

    // 定义一个公共方法setDelegate，用于设置delegate对象
    public void setDelegate(final CommandErrorListener delegate) {
        // 将传入的delegate对象赋值给当前类的delegate成员变量
        this.delegate = delegate;
    }

    // 实现CommandErrorListener接口中的showUpgradeCompleteAlert方法
    // 当升级完成需要向玩家展示提示信息时调用此方法
    @Override
    public void showUpgradeCompleteAlert(int playerIndex, War3ID queuedRawcode, int level) {
        // 将升级完成的提示信息委托给delegate对象处理
        this.delegate.showUpgradeCompleteAlert(playerIndex, queuedRawcode, level);
    }
}
