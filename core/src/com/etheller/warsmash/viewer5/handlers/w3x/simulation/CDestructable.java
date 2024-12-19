package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.RemovablePathingMapInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderWidget.UnitAnimationListenerImpl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CDestructableBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
// CDestructable类继承自CWidget类，表示一个可破坏的对象
public class CDestructable extends CWidget {

	// 定义一个私有的最终变量destType，表示破坏对象的类型
	private final CDestructableType destType;

	// 定义一个私有的最终变量pathingInstance，表示可移除的寻路地图实例
	private final RemovablePathingMapInstance pathingInstance;

	// 定义一个私有的最终变量pathingInstanceDeath，表示死亡时可移除的寻路地图实例
	private final RemovablePathingMapInstance pathingInstanceDeath;

	// 定义一个私有的UnitAnimationListenerImpl类型的变量unitAnimationListenerImpl，用于监听单位动画
	private UnitAnimationListenerImpl unitAnimationListenerImpl;

	// 定义一个私有的布尔变量invulnerable，表示对象是否无敌
	private boolean invulnerable;

	// 定义一个私有的布尔变量blighted，表示对象是否枯萎
	private boolean blighted;

	// 定义一个私有的Rectangle类型的变量registeredEnumRectangle，表示注册的枚举矩形
	private Rectangle registeredEnumRectangle;

	// 定义一个私有的List<CDestructableBuff>类型的变量buffs，表示破坏对象的增益列表
	private List<CDestructableBuff> buffs;


	// 构造函数，初始化CDestructable对象
	public CDestructable(final int handleId, final float x, final float y, final float life,
			final CDestructableType destTypeInstance, final RemovablePathingMapInstance pathingInstance,
			final RemovablePathingMapInstance pathingInstanceDeath) {
		super(handleId, x, y, life);
		this.destType = destTypeInstance;
		this.pathingInstance = pathingInstance;
		this.pathingInstanceDeath = pathingInstanceDeath;
		if (this.destType.getOcclusionHeight() > 0) {
			this.pathingInstance.setBlocksVision();
		}
	}

	// 获取飞行高度
	@Override
	public float getFlyHeight() {
		return 0;
	}

	// 获取碰撞高度
	@Override
	public float getImpactZ() {
		return 0; // TODO maybe from DestructableType
	}

	// 获取或创建注册的枚举矩形
	public Rectangle getOrCreateRegisteredEnumRectangle() {
		// 如果registeredEnumRectangle为空，则进行以下操作
		if (this.registeredEnumRectangle == null) {
			// 获取路径像素图和死亡路径像素图
			BufferedImage pathingPixelMap = this.destType.getPathingPixelMap();
			BufferedImage pathingDeathPixelMap = this.destType.getPathingDeathPixelMap();

			// 如果路径像素图为空，则使用空白路径像素图
			if (pathingPixelMap == null) {
				pathingPixelMap = PathingGrid.BLANK_PATHING;
			}
			// 如果死亡路径像素图为空，则使用空白路径像素图
			if (pathingDeathPixelMap == null) {
				pathingDeathPixelMap = PathingGrid.BLANK_PATHING;
			}

			// 计算宽度为两个像素图中较大的宽度乘以16
			final float width = Math.max(pathingPixelMap.getWidth() * 16, pathingDeathPixelMap.getWidth() * 16);
			// 计算高度为两个像素图中较高的高度乘以16
			final float height = Math.max(pathingPixelMap.getHeight() * 16, pathingDeathPixelMap.getHeight() * 16);

			// 创建一个新的矩形，中心点为当前位置，宽度和高度为计算出的值
			this.registeredEnumRectangle = new Rectangle(getX() - (width / 2), getY() - (height / 2), width, height);
		}
		// 返回已创建或已存在的registeredEnumRectangle
		return this.registeredEnumRectangle;

	}

	// 对对象造成伤害
	@Override
	public float damage(final CSimulation simulation, final CUnit source, final boolean isAttack,
			final boolean isRanged, final CAttackType attackType, final CDamageType damageType,
			final String weaponSoundType, final float damage) {
		// 如果角色是无敌的，则返回0，表示没有受到伤害
		if (isInvulnerable()) {
			return 0;
		}

		// 记录角色在受到伤害前的死亡状态
		final boolean wasDead = isDead();

		// 减少角色的生命值
		this.life -= damage;

		// 触发破坏事件，通知模拟系统角色受到了伤害
		simulation.destructableDamageEvent(this, weaponSoundType, this.destType.getArmorType());

		// 如果角色在受到伤害前没有死亡，受到伤害后死亡，则调用kill方法
		if (!wasDead && isDead()) {
			kill(simulation);
		}

		// 返回实际受到的伤害值
		return damage;

	}

	// 对对象造成伤害，包含额外伤害
	@Override
	public float damage(final CSimulation simulation, final CUnit source, final boolean isAttack,
			final boolean isRanged, final CAttackType attackType, final CDamageType damageType,
			final String weaponSoundType, final float damage, final float bonusDamage) {
		return this.damage(simulation, source, isAttack, isRanged, attackType, damageType, weaponSoundType,
				damage + bonusDamage);
	}

	// 杀死对象
	private void kill(final CSimulation simulation) {
		// 如果pathingInstance不为空，则移除它
		if (this.pathingInstance != null) {
			this.pathingInstance.remove();
		}
		// 如果pathingInstanceDeath不为空，则添加它
		if (this.pathingInstanceDeath != null) {
			this.pathingInstanceDeath.add();
		}
		// 如果buffs不为空，则逆序遍历buffs列表
		if (this.buffs != null) {
			for (int i = this.buffs.size() - 1; i >= 0; i--) {
				// 在onDeath()方法中移除自身是可以的，因为是逆序迭代
				this.buffs.get(i).onDeath(simulation, this);
			}
		}
		// 触发死亡事件
		fireDeathEvents(simulation);

	}

	// 设置对象的生命值
	@Override
	public void setLife(final CSimulation simulation, final float life) {
		final boolean wasDead = isDead();
		super.setLife(simulation, life);
		if (isDead() && !wasDead) {
			kill(simulation);
		}
	}

	// 检查对象是否可以被指定目标类型选中
	@Override
	public boolean canBeTargetedBy(final CSimulation simulation, final CUnit source,
			final EnumSet<CTargetType> targetsAllowed, AbilityTargetCheckReceiver<CWidget> receiver) {
		if (targetsAllowed.containsAll(this.destType.getTargetedAs())) {
			if (isDead()) {
				if (targetsAllowed.contains(CTargetType.DEAD)) {
					return true;
				}
				receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_MUST_BE_LIVING);
			} else {
				if (!targetsAllowed.contains(CTargetType.DEAD) || targetsAllowed.contains(CTargetType.ALIVE)) {
					return true;
				}
				receiver.targetCheckFailed(CommandStringErrorKeys.SOMETHING_IS_BLOCKING_THAT_TREE_STUMP);
			}
		} else {
			if (this.destType.getTargetedAs().contains(CTargetType.TREE)
					&& !targetsAllowed.contains(CTargetType.TREE)) {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_TREES);
			} else if (this.destType.getTargetedAs().contains(CTargetType.DEBRIS)
					&& !targetsAllowed.contains(CTargetType.DEBRIS)) {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_DEBRIS);
			} else if (this.destType.getTargetedAs().contains(CTargetType.WALL)
					&& !targetsAllowed.contains(CTargetType.WALL)) {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_WALLS);
			} else if (this.destType.getTargetedAs().contains(CTargetType.BRIDGE)
					&& !targetsAllowed.contains(CTargetType.BRIDGE)) {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_BRIDGES);
			} else {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_THIS_UNIT);
			}
		}
		return false;
	}

	// 接受AbilityTargetVisitor的访问
	@Override
	public <T> T visit(final AbilityTargetVisitor<T> visitor) {
		return visitor.accept(this);
	}

	// 接受CWidgetVisitor的访问
	@Override
	public <T> T visit(final CWidgetVisitor<T> visitor) {
		return visitor.accept(this);
	}

	// 获取对象的类型
	public CDestructableType getDestType() {
		return this.destType;
	}

	// 设置对象的动画监听器
	public void setUnitAnimationListener(final UnitAnimationListenerImpl unitAnimationListenerImpl) {
		this.unitAnimationListenerImpl = unitAnimationListenerImpl;
	}

	// 获取对象的最大生命值
	@Override
	public float getMaxLife() {
		return this.destType.getMaxLife();
	}

	// 设置对象是否无敌
	public void setInvulnerable(final boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	// 检查对象是否无敌
	@Override
	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	// 设置对象是否被瘟疫感染
	public void setBlighted(final boolean blighted) {
		this.blighted = blighted;
	}

	// 检查对象是否被瘟疫感染
	public boolean isBlighted() {
		return this.blighted;
	}

	// 检查对象是否在瘟疫区域
	public boolean checkIsOnBlight(final CSimulation game) {
		return !game.getPathingGrid().checkPathingTexture(getX(), getY(), 0, this.destType.getPathingPixelMap(),
				EnumSet.of(CBuildingPathingType.BLIGHTED), EnumSet.noneOf(CBuildingPathingType.class),
				game.getWorldCollision(), null);
	}

	// 计算对象与指定坐标点的距离
	@Override
	public double distance(final float x, final float y) {
		return StrictMath.sqrt(distanceSquaredNoCollision(x, y));
	}

	// 添加Buff效果
	public void add(final CSimulation simulation, final CDestructableBuff buff) {
		if (this.buffs == null) {
			this.buffs = new ArrayList<>();
		}
		this.buffs.add(buff);
		buff.onAdd(simulation, this);
	}

	// 移除Buff效果
	public void remove(final CSimulation simulation, final CDestructableBuff buff) {
		this.buffs.remove(buff);
		buff.onRemove(simulation, this);
	}
}
