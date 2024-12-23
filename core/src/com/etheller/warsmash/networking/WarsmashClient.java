package com.etheller.warsmash.networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntIntMap;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderExecutor;

import net.warsmash.networking.udp.OrderedUdpClient;

// WarsmashClient类用于处理与游戏服务器的网络交互
public class WarsmashClient implements ServerToClientListener, GameTurnManager {
	private final OrderedUdpClient udpClient;
	private final War3MapViewer game;
	private final Map<Integer, CPlayerUnitOrderExecutor> indexToExecutor = new HashMap<>();
	private int latestCompletedTurn = -1;
	private int latestLocallyRequestedTurn = -1;
	private final WarsmashClientWriter writer;
	private final Queue<QueuedMessage> queuedMessages = new ArrayDeque<>();
	private final IntIntMap serverSlotToMapSlot;

	// WarsmashClient构造函数，通过服务器地址、端口、游戏对象等参数初始化客户端
	public WarsmashClient(final InetAddress serverAddress, final int udpPort, final War3MapViewer game,
			final long sessionToken, final IntIntMap serverSlotToMapSlot) throws UnknownHostException, IOException {
		this.udpClient = new OrderedUdpClient(serverAddress, udpPort, new WarsmashClientParser(this));
		this.game = game;
		this.writer = new WarsmashClientWriter(this.udpClient, sessionToken);
		this.serverSlotToMapSlot = serverSlotToMapSlot;
	}

	// 获取对应playerIndex的执行者
	private CPlayerUnitOrderExecutor getExecutor(final int serverPlayerIndex) {
		final int mapPlayerIndex = this.serverSlotToMapSlot.get(serverPlayerIndex, -1);
		CPlayerUnitOrderExecutor executor = this.indexToExecutor.get(serverPlayerIndex);
		if (executor == null) {
			executor = new CPlayerUnitOrderExecutor(this.game.simulation, mapPlayerIndex);
			this.indexToExecutor.put(serverPlayerIndex, executor);
		}
		return executor;
	}

	// 启动UDP客户端线程
	public void startThread() {
		new Thread(this.udpClient).start();
	}

	@Override
	// 接受玩家加入请求
	public void acceptJoin(final int playerIndex) {
		System.err.println("acceptJoin " + playerIndex);
		this.game.setLocalPlayerServerSlot(playerIndex);
	}

	@Override
	// 发出目标指令
	public void issueTargetOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final int targetHandleId, final boolean queue) {
		final CPlayerUnitOrderExecutor executor = getExecutor(playerIndex);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				final int currentServerTurnInProgress = WarsmashClient.this.latestCompletedTurn + 1;
				if (currentServerTurnInProgress > WarsmashClient.this.latestLocallyRequestedTurn) {
					WarsmashClient.this.queuedMessages.add(new QueuedMessage(currentServerTurnInProgress) {
						@Override
						public void run() {
							executor.issueTargetOrder(unitHandleId, abilityHandleId, orderId, targetHandleId, queue);
						}
					});
				}
				else if (currentServerTurnInProgress == WarsmashClient.this.latestLocallyRequestedTurn) {
					executor.issueTargetOrder(unitHandleId, abilityHandleId, orderId, targetHandleId, queue);
				}
				else {
					System.err.println("Turn tick system mismatch: " + currentServerTurnInProgress + " < "
							+ WarsmashClient.this.latestLocallyRequestedTurn);
				}
			}
		});
	}

	@Override
	// 发出点指令
	public void issuePointOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final float x, final float y, final boolean queue) {
		final CPlayerUnitOrderExecutor executor = getExecutor(playerIndex);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				final int currentServerTurnInProgress = WarsmashClient.this.latestCompletedTurn + 1;
				if (currentServerTurnInProgress > WarsmashClient.this.latestLocallyRequestedTurn) {
					WarsmashClient.this.queuedMessages.add(new QueuedMessage(currentServerTurnInProgress) {
						@Override
						public void run() {
							executor.issuePointOrder(unitHandleId, abilityHandleId, orderId, x, y, queue);
						}
					});
				}
				else if (currentServerTurnInProgress == WarsmashClient.this.latestLocallyRequestedTurn) {
					executor.issuePointOrder(unitHandleId, abilityHandleId, orderId, x, y, queue);
					;
				}
				else {
					System.err.println("Turn tick system mismatch: " + currentServerTurnInProgress + " < "
							+ WarsmashClient.this.latestLocallyRequestedTurn);
				}
			}
		});

	}

	@Override
	// 发出在指定点掉落物品的指令
	public void issueDropItemAtPointOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final int targetHandleId, final float x, final float y, final boolean queue) {
		final CPlayerUnitOrderExecutor executor = getExecutor(playerIndex);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				final int currentServerTurnInProgress = WarsmashClient.this.latestCompletedTurn + 1;
				if (currentServerTurnInProgress > WarsmashClient.this.latestLocallyRequestedTurn) {
					WarsmashClient.this.queuedMessages.add(new QueuedMessage(currentServerTurnInProgress) {
						@Override
						public void run() {
							executor.issueDropItemAtPointOrder(unitHandleId, abilityHandleId, orderId, targetHandleId,
									x, y, queue);
						}
					});
				}
				else if (currentServerTurnInProgress == WarsmashClient.this.latestLocallyRequestedTurn) {
					executor.issueDropItemAtPointOrder(unitHandleId, abilityHandleId, orderId, targetHandleId, x, y,
							queue);
				}
				else {
					System.err.println("Turn tick system mismatch: " + currentServerTurnInProgress + " < "
							+ WarsmashClient.this.latestLocallyRequestedTurn);
				}
			}
		});
	}

	@Override
	// 发出在目标上掉落物品的指令
	public void issueDropItemAtTargetOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final int targetHandleId, final int targetHeroHandleId, final boolean queue) {
		final CPlayerUnitOrderExecutor executor = getExecutor(playerIndex);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				final int currentServerTurnInProgress = WarsmashClient.this.latestCompletedTurn + 1;
				if (currentServerTurnInProgress > WarsmashClient.this.latestLocallyRequestedTurn) {
					WarsmashClient.this.queuedMessages.add(new QueuedMessage(currentServerTurnInProgress) {
						@Override
						public void run() {
							executor.issueDropItemAtTargetOrder(unitHandleId, abilityHandleId, orderId, targetHandleId,
									targetHeroHandleId, queue);
						}
					});
				}
				else if (currentServerTurnInProgress == WarsmashClient.this.latestLocallyRequestedTurn) {
					executor.issueDropItemAtTargetOrder(unitHandleId, abilityHandleId, orderId, targetHandleId,
							targetHeroHandleId, queue);
				}
				else {
					System.err.println("Turn tick system mismatch: " + currentServerTurnInProgress + " < "
							+ WarsmashClient.this.latestLocallyRequestedTurn);
				}
			}
		});
	}

	@Override
	// 发出立即指令
	public void issueImmediateOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final boolean queue) {
		final CPlayerUnitOrderExecutor executor = getExecutor(playerIndex);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				final int currentServerTurnInProgress = WarsmashClient.this.latestCompletedTurn + 1;
				if (currentServerTurnInProgress > WarsmashClient.this.latestLocallyRequestedTurn) {
					WarsmashClient.this.queuedMessages.add(new QueuedMessage(currentServerTurnInProgress) {
						@Override
						public void run() {
							executor.issueImmediateOrder(unitHandleId, abilityHandleId, orderId, queue);
						}
					});
				}
				else if (currentServerTurnInProgress == WarsmashClient.this.latestLocallyRequestedTurn) {
					executor.issueImmediateOrder(unitHandleId, abilityHandleId, orderId, queue);
				}
				else {
					System.err.println("Turn tick system mismatch: " + currentServerTurnInProgress + " < "
							+ WarsmashClient.this.latestLocallyRequestedTurn);
				}
			}
		});
	}

	@Override
	// 取消单位的训练项目
	public void unitCancelTrainingItem(final int playerIndex, final int unitHandleId, final int cancelIndex) {
		final CPlayerUnitOrderExecutor executor = getExecutor(playerIndex);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				final int currentServerTurnInProgress = WarsmashClient.this.latestCompletedTurn + 1;
				if (currentServerTurnInProgress > WarsmashClient.this.latestLocallyRequestedTurn) {
					WarsmashClient.this.queuedMessages.add(new QueuedMessage(currentServerTurnInProgress) {
						@Override
						public void run() {
							executor.unitCancelTrainingItem(unitHandleId, cancelIndex);
						}
					});
				}
				else if (currentServerTurnInProgress == WarsmashClient.this.latestLocallyRequestedTurn) {
					executor.unitCancelTrainingItem(unitHandleId, cancelIndex);
				}
				else {
					System.err.println("Turn tick system mismatch: " + currentServerTurnInProgress + " < "
							+ WarsmashClient.this.latestLocallyRequestedTurn);
				}
			}
		});
	}

	@Override
	// 发出GUI事件
	public void issueGuiPlayerEvent(final int playerIndex, final int eventId) {
		final CPlayerUnitOrderExecutor executor = getExecutor(playerIndex);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				final int currentServerTurnInProgress = WarsmashClient.this.latestCompletedTurn + 1;
				if (currentServerTurnInProgress > WarsmashClient.this.latestLocallyRequestedTurn) {
					WarsmashClient.this.queuedMessages.add(new QueuedMessage(currentServerTurnInProgress) {
						@Override
						public void run() {
							executor.issueGuiPlayerEvent(eventId);
						}
					});
				}
				else if (currentServerTurnInProgress == WarsmashClient.this.latestLocallyRequestedTurn) {
					executor.issueGuiPlayerEvent(eventId);
				}
				else {
					System.err.println("Turn tick system mismatch: " + currentServerTurnInProgress + " < "
							+ WarsmashClient.this.latestLocallyRequestedTurn);
				}
			}
		});
	}

	@Override
	// 游戏回合完成
	public void finishedTurn(final int gameTurnTick) {
		if (WarsmashConstants.VERBOSE_LOGGING) {
			System.out.println("finishedTurn " + gameTurnTick);
		}
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				WarsmashClient.this.latestCompletedTurn = gameTurnTick;
			}
		});
	}

	@Override
	// 回合完成处理
	public void turnCompleted(final int gameTurnTick) {
		if (WarsmashConstants.VERBOSE_LOGGING) {
			System.out.println("turnCompleted " + gameTurnTick);
		}
		this.writer.finishedTurn(gameTurnTick);
		this.writer.send();
		this.latestLocallyRequestedTurn = gameTurnTick;
		while (!this.queuedMessages.isEmpty()
				&& (this.queuedMessages.peek().messageTurnTick == this.latestLocallyRequestedTurn)) {
			this.queuedMessages.poll().run();
		}
		if (!this.queuedMessages.isEmpty()) {
			System.out.println("stopped with " + this.queuedMessages.peek().messageTurnTick + " != "
					+ this.latestLocallyRequestedTurn);
		}
	}

	@Override
	// 开始游戏
	public void startGame() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				WarsmashClient.this.game.setGameTurnManager(WarsmashClient.this);
			}
		});
	}

	@Override
	// 跳过帧处理
	public void framesSkipped(final float skippedCount) {
		this.writer.framesSkipped((int) skippedCount);
		this.writer.send();
	}

	@Override
	// 心跳包处理
	public void heartbeat() {
		// Not doing anything here at the moment. The act of the server sending us that
		// packet
		// will let the middle layer UDP system know to re-request any lost packets
		// based
		// on the heartbeat seq no. But at app layer, here, we can ignore it.
		System.out.println("got heartbeat() from server");
	}

	@Override
	// 获取最新完成的回合
	public int getLatestCompletedTurn() {
		return this.latestCompletedTurn;
	}

	// 获取客户端写入器
	public WarsmashClientWriter getWriter() {
		return this.writer;
	}

	// QueuedMessage类用于管理需要排队执行的消息
	private static abstract class QueuedMessage implements Runnable {
		private final int messageTurnTick;

		// QueuedMessage构造函数，初始化消息回合
		public QueuedMessage(final int messageTurnTick) {
			this.messageTurnTick = messageTurnTick;
		}

		public final int getMessageTurnTick() {
			return this.messageTurnTick;
		}

		@Override
		public abstract void run();
	}
}

