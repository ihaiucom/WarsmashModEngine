package com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
public abstract class CTimer {
    // 定时器引擎触发时间点
    private int engineFireTick;
    // 定时器计划时间点
    private int scheduleTick;
    // 定时器超时时间
    private float timeoutTime;
    // 暂停后剩余时间
    private float remainingTimeAfterPause;
    // 定时器是否正在运行
    private boolean running = false;
    // 定时器是否重复执行
    private boolean repeats;

    /**
     * 设置定时器的超时时间
     * @param timeoutTime 超时时间
     */
    public void setTimeoutTime(final float timeoutTime) {
        this.timeoutTime = timeoutTime;
    }

    /**
     * 判断定时器是否重复执行
     * @return 是否重复执行
     */
    public boolean isRepeats() {
        return this.repeats;
    }

    /**
     * 判断定时器是否正在运行
     * @return 是否正在运行
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * 获取定时器的超时时间
     * @return 超时时间
     */
    public float getTimeoutTime() {
        return this.timeoutTime;
    }

    /**
     * 启动定时器
     * @param simulation 模拟环境
     */
    public void start(final CSimulation simulation) {
		// 设置计时器运行状态为true
		this.running = true;
		// 获取当前游戏回合的刻度
		final int currentTick = simulation.getGameTurnTick();
		// 将当前刻度设置为计划刻度
		this.scheduleTick = currentTick;
		// 调用内部启动方法，传入超时时间、模拟对象和当前刻度
		innerStart(this.timeoutTime, simulation, currentTick);

    }

    /**
     * 启动一个带有延迟的重复定时器
     * @param simulation 模拟环境
     * @param delay 延迟时间
     */
    public void startRepeatingTimerWithDelay(final CSimulation simulation, final float delay) {
		// 设置计时器运行状态为true，表示计时器正在运行
		this.running = true;
		// 设置计时器重复执行状态为true，表示计时器将在每次到期后重新启动
		this.repeats = true;
		// 获取当前游戏回合的刻度值
		final int currentTick = simulation.getGameTurnTick();
		// 将计时器的预定刻度设置为当前游戏回合的刻度值
		this.scheduleTick = currentTick;
		// 调用内部启动方法，传入延迟时间、模拟对象和当前刻度值，开始计时器的执行
		innerStart(delay, simulation, currentTick);

    }

    /**
     * 内部启动定时器的方法
     * @param timeoutTime 超时时间
     * @param simulation 模拟环境
     * @param currentTick 当前时间点
     */
    private void innerStart(final float timeoutTime, final CSimulation simulation, final int currentTick) {
		// 计算超时时间对应的tick数，通过将超时时间除以每个tick的时间间隔得到
		final int ticks = (int) (timeoutTime / WarsmashConstants.SIMULATION_STEP_TIME);
		// 设置引擎触发tick，为当前tick加上计算得到的ticks数
		this.engineFireTick = currentTick + ticks;
		// 将当前计时器注册到模拟器中，以便在模拟器运行时能够触发计时器
		simulation.registerTimer(this);

    }

    /**
     * 暂停定时器
     * @param simulation 模拟环境
     */
    public void pause(final CSimulation simulation) {
        this.remainingTimeAfterPause = getRemaining(simulation);
        simulation.unregisterTimer(this);
    }

    /**
     * 恢复定时器
     * @param simulation 模拟环境
     */
    public void resume(final CSimulation simulation) {
        // 如果暂停后的剩余时间为0，则开始模拟
        if (this.remainingTimeAfterPause == 0) {
            start(simulation); // 调用start方法开始模拟
            return; // 结束当前方法
        }
        // 获取当前游戏回合数
        final int currentTick = simulation.getGameTurnTick();
        // 调用innerStart方法，传入剩余时间、模拟对象和当前回合数
        innerStart(this.remainingTimeAfterPause, simulation, currentTick);
        // 将暂停后的剩余时间重置为0
        this.remainingTimeAfterPause = 0;

    }

    /**
     * 获取已经过去的时间
     * @param simulation 模拟环境
     * @return 已经过去的时间
     */
    public float getElapsed(final CSimulation simulation) {
        // 获取当前游戏刻度
        final int currentTick = simulation.getGameTurnTick();
        // 计算自调度刻度以来的经过刻度数
        final int elapsedTicks = currentTick - this.scheduleTick;
        // 返回经过时间与超时时间的较大值，确保不会低于设定的超时时间
        return Math.max(elapsedTicks * WarsmashConstants.SIMULATION_STEP_TIME, this.timeoutTime);

    }

    /**
     * 获取剩余时间
     * @param simulation 模拟环境
     * @return 剩余时间
     */
    public float getRemaining(final CSimulation simulation) {
        return this.timeoutTime - getElapsed(simulation);
    }

    /**
     * 设置定时器是否重复执行
     * @param repeats 是否重复执行
     */
    public void setRepeats(final boolean repeats) {
        this.repeats = repeats;
    }

    /**
     * 获取定时器引擎触发时间点
     * @return 定时器引擎触发时间点
     */
    public int getEngineFireTick() {
        return this.engineFireTick;
    }

    /**
     * 定时器触发时的抽象方法，需要子类实现
     * @param simulation 模拟环境
     */
    public abstract void onFire(final CSimulation simulation);

    /**
     * 触发定时器
     * @param simulation 模拟环境
     */
    public void fire(final CSimulation simulation) {
        // 将计时器状态设置为停止
        this.running = false;

        // 获取计时器是否重复的设置
        final boolean repeats = this.repeats;

        // 触发计时器事件
        onFire(simulation);

        // 如果计时器设置为重复，则重新启动计时器
        if (repeats) {
            start(simulation);
        }

    }
}
