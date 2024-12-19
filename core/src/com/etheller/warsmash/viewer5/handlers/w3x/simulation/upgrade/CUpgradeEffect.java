package com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
/**
 * 定义了一个升级效果的接口，包含了应用和取消应用升级效果的方法
 */
public interface CUpgradeEffect {
    /**
     * 将升级效果应用到指定的单位上
     *
     * @param simulation 游戏模拟对象
     * @param unit       要应用升级效果的单位
     * @param level      升级的等级
     */
    void apply(CSimulation simulation, CUnit unit, int level);

    /**
     * 将升级效果从指定的单位上移除
     *
     * @param simulation 游戏模拟对象
     * @param unit       要移除升级效果的单位
     * @param level      升级的等级
     */
    void unapply(CSimulation simulation, CUnit unit, int level);

    /**
     * 默认的将升级效果应用到指定的玩家上
     *
     * @param simulation 游戏模拟对象
     * @param playerIndex 玩家的索引
     * @param level      升级的等级
     */
    default void apply(CSimulation simulation, int playerIndex, int level) {
    }

    /**
     * 默认的将升级效果从指定的玩家上移除
     *
     * @param simulation 游戏模拟对象
     * @param playerIndex 玩家的索引
     * @param level      升级的等级
     */
    default void unapply(CSimulation simulation, int playerIndex, int level) {
    }

    /**
     * 定义了一个工具类，包含了一些计算升级效果值的方法
     */
    class Util {
        /**
         * 计算整数类型的升级效果值
         *
         * @param base 基础值
         * @param mod  每级的增量
         * @param level 升级的等级
         * @return 计算后的升级效果值
         */
        static int levelValue(int base, int mod, int level) {
            return base + (mod * level);
        }

        /**
         * 计算浮点类型的升级效果值
         *
         * @param base 基础值
         * @param mod  每级的增量
         * @param level 升级的等级
         * @return 计算后的升级效果值
         */
        static float levelValue(float base, float mod, int level) {
            return base + (mod * level);
        }
    }
}
