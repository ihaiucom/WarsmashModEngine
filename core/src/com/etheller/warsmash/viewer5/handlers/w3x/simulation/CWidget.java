package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CWidgetEvent;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;

public abstract class CWidget implements AbilityTarget, CHandle {
	protected static final Rectangle tempRect = new Rectangle(); // 占地区域范围
	private final int handleId; // 唯一标识符
	private float x; // 坐标
	private float y; // 坐标
	protected float life; // 生命值
	private final EnumMap<JassGameEventsWar3, List<CWidgetEvent>> eventTypeToEvents = new EnumMap<>(
			JassGameEventsWar3.class);

	public CWidget(final int handleId, final float x, final float y, final float life) {
		this.handleId = handleId;
		this.x = x;
		this.y = y;
		this.life = life;
	}

	@Override
	public int getHandleId() {
		return this.handleId;
	}

	@Override
	public float getX() {
		return this.x;
	}

	@Override
	public float getY() {
		return this.y;
	}

	public float getLife() {
		return this.life;
	}

	public abstract float getMaxLife();

	protected void setX(final float x) {
		this.x = x;
	}

	protected void setY(final float y) {
		this.y = y;
	}

	public void setLife(final CSimulation simulation, final float life) {
		this.life = life;
	}

	// 伤害处理
	public abstract float damage(final CSimulation simulation, final CUnit source, final boolean isAttack, final boolean isRanged, final CAttackType attackType,
			final CDamageType damageType, final String weaponSoundType, final float damage);

	public abstract float damage(final CSimulation simulation, final CUnit source, final boolean isAttack, final boolean isRanged, final CAttackType attackType,
			final CDamageType damageType, final String weaponSoundType, final float damage, final float bonusDamage);

	// 飞行高度
	public abstract float getFlyHeight();

	// 高度坐标
	public abstract float getImpactZ();

	// 访问者模式
	public abstract <T> T visit(CWidgetVisitor<T> visitor);

	// 是否死亡
	public boolean isDead() {
		return this.life <= 0;
	}

	// 能否作为目标
	public abstract boolean canBeTargetedBy(CSimulation simulation, CUnit source,
											final EnumSet<CTargetType> targetsAllowed, AbilityTargetCheckReceiver<CWidget> receiver);

	public boolean canBeTargetedBy(CSimulation simulation, CUnit source, final EnumSet<CTargetType> targetsAllowed) {
		return canBeTargetedBy(simulation, source, targetsAllowed,
				BooleanAbilityTargetCheckReceiver.<CWidget>getInstance().reset());
	}

	// 与目标的距离
	public double distanceSquaredNoCollision(final AbilityTarget target) {
		return distanceSquaredNoCollision(target.getX(), target.getY());
	}

	public double distanceSquaredNoCollision(final float targetX, final float targetY) {
		final double dx = targetX - getX();
		final double dy = targetY - getY();
		return (dx * dx) + (dy * dy);
	}

	// 不能收到攻击？
	public abstract boolean isInvulnerable();

	// 派发死亡事件
	public void fireDeathEvents(final CSimulation simulation) {
		fireEvents(CommonTriggerExecutionScope::widgetTriggerScope, JassGameEventsWar3.EVENT_WIDGET_DEATH);
	}

	// 获取指定事件类型列表
	private List<CWidgetEvent> getOrCreateEventList(final JassGameEventsWar3 eventType) {
		List<CWidgetEvent> playerEvents = this.eventTypeToEvents.get(eventType);
		if (playerEvents == null) {
			playerEvents = new ArrayList<>();
			this.eventTypeToEvents.put(eventType, playerEvents);
		}
		return playerEvents;
	}

	// 获取指定事件类型列表
	protected List<CWidgetEvent> getEventList(final JassGameEventsWar3 eventType) {
		return this.eventTypeToEvents.get(eventType);
	}

	// 添加事件
	public RemovableTriggerEvent addEvent(final GlobalScope globalScope, final Trigger whichTrigger,
			final JassGameEventsWar3 eventType) {
		final CWidgetEvent playerEvent = new CWidgetEvent(globalScope, this, whichTrigger, eventType, null);
		getOrCreateEventList(eventType).add(playerEvent);
		return playerEvent;
	}

	// 移除事件
	public void removeEvent(final CWidgetEvent playerEvent) {
		final List<CWidgetEvent> eventList = getEventList(playerEvent.getEventType());
		if (eventList != null) {
			eventList.remove(playerEvent);
		}
	}

	// 派发事件
	private void fireEvents(final CommonTriggerExecutionScope.WidgetEventScopeBuilder eventScopeBuilder,
			final JassGameEventsWar3 eventType) {
		final List<CWidgetEvent> eventList = getEventList(eventType);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, eventScopeBuilder.create(eventType, event.getTrigger(), this));
			}
		}
	}

	//添加死亡事件
	public RemovableTriggerEvent addDeathEvent(final GlobalScope globalScope, final Trigger whichTrigger) {
		return addEvent(globalScope, whichTrigger, JassGameEventsWar3.EVENT_WIDGET_DEATH);
	}

	// 计算距离
	public abstract double distance(float x, float y);
}
