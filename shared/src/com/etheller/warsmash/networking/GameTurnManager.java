package com.etheller.warsmash.networking;

// 定义一个名为GameTurnManager的接口，用于管理游戏回合
public interface GameTurnManager {
    // 获取最新完成的回合数
    int getLatestCompletedTurn();

    // 标记一个回合已经完成，并传入当前回合的游戏刻度
    void turnCompleted(int gameTurnTick);

    // 当跳过了一些帧时调用，传入跳过的帧数
    void framesSkipped(float skippedCount);

    // 定义一个名为PAUSED的GameTurnManager实例，表示游戏处于暂停状态
    GameTurnManager PAUSED = new GameTurnManager() {
        // 在暂停状态下，最新完成的回合数返回最小整数值
        @Override
        public int getLatestCompletedTurn() {
            return Integer.MIN_VALUE;
        }

        // 当收到回合完成的信号时，在控制台输出错误信息，表示不应该在暂停状态下完成回合
        @Override
        public void turnCompleted(final int gameTurnTick) {
            System.err.println("got turnCompleted(" + gameTurnTick + ") while paused !!");
        }

        // 在暂停状态下，跳过帧数不做任何处理
        @Override
        public void framesSkipped(final float skippedCount) {
        }
    };

    // 定义一个名为LOCAL的GameTurnManager实例，表示游戏处于本地玩家控制状态
    GameTurnManager LOCAL = new GameTurnManager() {
        // 在本地玩家控制状态下，最新完成的回合数返回最大整数值
        @Override
        public int getLatestCompletedTurn() {
            return Integer.MAX_VALUE;
        }

        // 在本地玩家控制状态下，回合完成不做任何处理
        @Override
        public void turnCompleted(final int gameTurnTick) {
        }

        // 在本地玩家控制状态下，跳过帧数不做任何处理
        @Override
        public void framesSkipped(final float skippedCount) {
        }
    };
}

