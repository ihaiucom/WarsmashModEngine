package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;
// 定义一个枚举类型CMapFlag，实现了CHandle接口
public enum CMapFlag implements CHandle {
    // 枚举常量，表示不同的地图标志
    MAP_FOG_HIDE_TERRAIN, // 雾隐藏地形
    MAP_FOG_MAP_EXPLORED, // 雾中已探索的地图
    MAP_FOG_ALWAYS_VISIBLE, // 雾中始终可见

    MAP_USE_HANDICAPS, // 使用障碍
    MAP_OBSERVERS, // 观察者模式
    MAP_OBSERVERS_ON_DEATH, // 死亡后进入观察者模式

    MAP_FIXED_COLORS, // 固定颜色

    MAP_LOCK_RESOURCE_TRADING, // 锁定资源交易
    MAP_RESOURCE_TRADING_ALLIES_ONLY, // 资源交易仅限盟友

    MAP_LOCK_ALLIANCE_CHANGES, // 锁定联盟变更
    MAP_ALLIANCE_CHANGES_HIDDEN, // 隐藏联盟变更

    MAP_CHEATS, // 开启作弊
    MAP_CHEATS_HIDDEN, // 隐藏作弊

    MAP_LOCK_SPEED, // 锁定速度
    MAP_LOCK_RANDOM_SEED, // 锁定随机种子
    MAP_SHARED_ADVANCED_CONTROL, // 共享高级控制
    MAP_RANDOM_HERO, // 随机英雄
    MAP_RANDOM_RACES, // 随机种族
    MAP_RELOADED; // 地图已重新加载

    // 获取枚举类型的所有值
    public static CMapFlag[] VALUES = values();

    // 根据id获取对应的CMapFlag枚举常量
    public static CMapFlag getById(final int id) {
        for (final CMapFlag type : VALUES) {
            if ((type.getId()) == id) {
                return type;
            }
        }
        return null; // 如果没有找到对应的枚举常量，返回null
    }

    // 获取枚举常量的id，这里使用位移操作，每个枚举常量占用一个位
    public int getId() {
        return 1 << ordinal();
    }

    // 实现CHandle接口的方法，返回枚举常量的id
    @Override
    public int getHandleId() {
        return getId();
    }
}
