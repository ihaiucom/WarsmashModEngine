package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;
/**
 * 此类实现了 AbilityTargetCheckReceiver 接口，用于检查目标的有效性。
 * @param <TARGET_TYPE> 目标类型的泛型参数
 */
public final class ExternStringMsgTargetCheckReceiver<TARGET_TYPE> implements AbilityTargetCheckReceiver<TARGET_TYPE> {
    private static final ExternStringMsgTargetCheckReceiver<?> INSTANCE = new ExternStringMsgTargetCheckReceiver<>();

    /**
     * 获取 ExternStringMsgTargetCheckReceiver 的单例实例。
     * @param <T> 目标类型的泛型参数
     * @return ExternStringMsgTargetCheckReceiver 的单例实例
     */
    public static <T> ExternStringMsgTargetCheckReceiver<T> getInstance() {
        return (ExternStringMsgTargetCheckReceiver<T>) INSTANCE;
    }

    private TARGET_TYPE target;
    private String externStringKey;

    /**
     * 获取当前的目标。
     * @return 当前的目标
     */
    public TARGET_TYPE getTarget() {
        return this.target;
    }

    /**
     * 获取外部字符串键。
     * @return 外部字符串键
     */
    public String getExternStringKey() {
        return externStringKey;
    }

    /**
     * 重置目标检查接收器的状态。
     * @return 当前实例
     */
    public ExternStringMsgTargetCheckReceiver<TARGET_TYPE> reset() {
        this.target = null;
        this.externStringKey = null;
        return this;
    }

    @Override
    public void targetOk(final TARGET_TYPE target) {
        this.target = target;
    }

    @Override
    public void notAnActiveAbility() {
        this.externStringKey = "";
    }

    @Override
    public void orderIdNotAccepted() {
        this.externStringKey = ""; // no meaningful error
    }

    @Override
    public void targetCheckFailed(String commandStringErrorKey) {
        this.externStringKey = commandStringErrorKey;
    }
}

