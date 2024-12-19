package com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;

public class CTimerSleepAction extends CTimer {
    // 定义一个私有的final类型的成员变量sleepingThread，用于存储正在休眠的线程
    private final JassThread sleepingThread;

    /**
     * 构造函数，接收一个JassThread对象作为参数，并将其赋值给成员变量sleepingThread
     * @param sleepingThread 正在休眠的线程对象
     */
    public CTimerSleepAction(final JassThread sleepingThread) {
        this.sleepingThread = sleepingThread;
    }

    /**
     * 当定时器触发时调用的方法
     * @param simulation 模拟环境对象
     */
    @Override
    public void onFire(final CSimulation simulation) {
        // 将正在休眠的线程状态设置为非休眠状态
        this.sleepingThread.setSleeping(false);
    }
}
