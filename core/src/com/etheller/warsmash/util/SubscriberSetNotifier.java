package com.etheller.warsmash.util;

import java.util.HashSet;
import java.util.Set;

// 抽象类，提供订阅者集合通知的功能
public abstract class SubscriberSetNotifier<LISTENER_TYPE> {
	protected final Set<LISTENER_TYPE> set; // bad for iteration but there
											// should never be a dude subscribed
											// 2x

	// 构造函数，初始化订阅者集合
	public SubscriberSetNotifier() {
		this.set = new HashSet<>();
	}

	// 订阅方法，添加监听器到集合中
	public final void subscribe(final LISTENER_TYPE listener) {
		this.set.add(listener);
	}

	// 退订方法，从集合中移除监听器
	public final void unsubscribe(final LISTENER_TYPE listener) {
		this.set.remove(listener);
	}

}
