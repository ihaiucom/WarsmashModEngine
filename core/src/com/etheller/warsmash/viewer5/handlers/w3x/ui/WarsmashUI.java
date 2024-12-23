package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import com.badlogic.gdx.audio.Music;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.GameCameraManager;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandErrorListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.CScriptDialog;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.CScriptDialogButton;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.CTimerDialog;

// 定义一个名为WarsmashUI的接口，它继承自CommandErrorListener和WarsmashBaseUI接口
public interface WarsmashUI extends CommandErrorListener, WarsmashBaseUI {

    // 创建一个脚本对话框的方法，需要传入一个全局作用域对象
    CScriptDialog createScriptDialog(GlobalScope globalScope);

    // 清除指定对话框内容的方法
    void clearDialog(CScriptDialog dialog);

    // 销毁指定对话框的方法
    void destroyDialog(CScriptDialog dialog);

    // 在指定的脚本对话框中创建一个按钮的方法，需要传入对话框对象、按钮文本和热键
    CScriptDialogButton createScriptDialogButton(CScriptDialog dialog, String buttonText, char hotkeyInt);

    // 获取游戏摄像头管理器的方法
    GameCameraManager getCameraManager();

    // 播放音乐的方法，需要传入音乐字段、是否随机播放和音乐索引
    Music playMusic(String musicField, boolean random, int index);

    // 获取UI场景的方法
    Scene getUiScene();

    // 创建一个计时器对话框的方法，需要传入一个计时器对象
    CTimerDialog createTimerDialog(CTimer timer);

    // 移除指定单位的方法
    void removedUnit(CUnit whichUnit);

    // 移除指定物品的方法
    void removedItem(CItem whichItem);

    // 在屏幕上显示定时文本的方法，需要传入文本位置、持续时间和消息内容
    void displayTimedText(float x, float y, float duration, String message);
}

