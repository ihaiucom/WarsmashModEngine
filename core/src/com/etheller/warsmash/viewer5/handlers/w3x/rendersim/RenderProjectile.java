package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.List;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.IndexedSequence;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetWidgetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

// 渲染投射物效果的类
public class RenderProjectile implements RenderEffect {
	// 用于存储俯仰角的临时四元数
	private static final Quaternion pitchHeap = new Quaternion();

	// 模拟的投射物对象，包含了投射物的位置、速度等信息
	private final CProjectile simulationProjectile;
	// 模型实例，用于渲染投射物的外观
	private final MdxComplexInstance modelInstance;
	// 投射物的当前 X 坐标
	private float x;
	// 投射物的当前 Y 坐标
	private float y;
	// 投射物的当前 Z 坐标
	private float z;
	// 投射物的起始高度
	private final float startingHeight;
	// 投射物轨迹的峰值高度
	private final float arcPeakHeight;
	// 投射物已经飞行的总距离
	private float totalTravelDistance;

	// 目标的高度，包括地面高度、飞行高度和冲击 Z 坐标
	private final float targetHeight;

	// 投射物的当前偏航角，即朝向目标的方向
	private float yaw;

	// 投射物的当前俯仰角
	private float pitch;
	// 标记投射物是否已经完成
	private boolean done = false;
	// 记录投射物死亡后的时间
	private float deathTimeElapsed;


	/**
	 * 构造一个新的 RenderProjectile 对象，用于渲染游戏中的投射物
	 *
	 * @param simulationProjectile 模拟的投射物对象，包含了投射物的位置、速度等信息
	 * @param modelInstance        模型实例，用于渲染投射物的外观
	 * @param z                    投射物的初始 Z 坐标
	 * @param arc                  投射物的弧度参数，用于计算投射物的轨迹
	 * @param war3MapViewer        魔兽地图查看器，用于获取地图信息和渲染相关的参数
	 */
	public RenderProjectile(final CProjectile simulationProjectile, final MdxComplexInstance modelInstance,
							final float z, final float arc, final War3MapViewer war3MapViewer) {
		// 初始化模拟投射物对象
		this.simulationProjectile = simulationProjectile;
		// 初始化模型实例
		this.modelInstance = modelInstance;
		// 设置投射物的初始 X 坐标
		this.x = simulationProjectile.getX();
		// 设置投射物的初始 Y 坐标
		this.y = simulationProjectile.getY();
		// 设置投射物的初始 Z 坐标
		this.z = z;
		// 记录投射物的起始高度
		this.startingHeight = z;

		// 计算投射物的目标 X 坐标
		final float targetX = this.simulationProjectile.getTargetX();
		// 计算投射物的目标 Y 坐标
		final float targetY = this.simulationProjectile.getTargetY();
		// 计算投射物在 X 方向上到目标的距离
		final float dxToTarget = targetX - this.x;
		// 计算投射物在 Y 方向上到目标的距离
		final float dyToTarget = targetY - this.y;
		// 计算投射物到目标的二维距离
		final float d2DToTarget = (float) StrictMath.sqrt((dxToTarget * dxToTarget) + (dyToTarget * dyToTarget));
		// 计算投射物的起始距离，包括到目标的距离和已经飞行的距离
		final float startingDistance = d2DToTarget + this.totalTravelDistance;

		// 获取投射物目标的 Widget 对象，用于获取目标的高度信息
		final CWidget widgetTarget = this.simulationProjectile.getTarget().visit(AbilityTargetWidgetVisitor.INSTANCE);
		// 初始化目标的冲击 Z 坐标和飞行高度
		float impactZ = 0;
		float flyHeight = 0;
		// 如果目标 Widget 对象存在
		if (widgetTarget != null) {
			// 获取目标的冲击 Z 坐标
			impactZ = widgetTarget.getImpactZ();
			// 获取目标的飞行高度
			flyHeight = widgetTarget.getFlyHeight();
		}
		// 计算目标的高度，包括地面高度、飞行高度和冲击 Z 坐标
		this.targetHeight = (war3MapViewer.terrain.getGroundHeight(targetX, targetY) + flyHeight + impactZ);
		// 计算投射物轨迹的峰值高度，它是起始距离的一个比例
		this.arcPeakHeight = arc * startingDistance;
		// 计算投射物的初始偏航角，即朝向目标的方向
		this.yaw = (float) StrictMath.atan2(dyToTarget, dxToTarget);
	}


	/**
	 * 更新投射物的动画和位置
	 *
	 * @param war3MapViewer 魔兽地图查看器
	 * @param deltaTime     时间间隔
	 * @return 是否完成
	 */
	@Override
	public boolean updateAnimations(final War3MapViewer war3MapViewer, final float deltaTime) {
		// 获取之前的完成状态
		final boolean wasDone = this.done;
		// 更新完成状态
		if (this.done = this.simulationProjectile.isDone()) {
			// 获取模型
			final MdxModel model = (MdxModel) this.modelInstance.model;
			// 获取序列
			final List<Sequence> sequences = model.getSequences();
			// 选择死亡序列
			final IndexedSequence sequence = SequenceUtils.selectSequence(PrimaryTag.DEATH, SequenceUtils.EMPTY,
					sequences, true);
			// 如果序列存在且完成状态改变
			if ((sequence != null) && this.done && !wasDone) {
				// 设置序列循环模式为不循环
				this.modelInstance.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
				// 设置序列
				this.modelInstance.setSequence(sequence.index);
			}
		}
		// 如果未完成
		else {
			// 如果序列结束或序列不存在
			if (this.modelInstance.sequenceEnded || (this.modelInstance.sequence == -1)) {
				// 随机选择站立序列
				SequenceUtils.randomStandSequence(this.modelInstance);
			}
		}
		// 获取模拟的X坐标
		final float simX = this.simulationProjectile.getX();
		// 获取模拟的Y坐标
		final float simY = this.simulationProjectile.getY();
		// 计算X方向的位移
		final float simDx = simX - this.x;
		// 计算Y方向的位移
		final float simDy = simY - this.y;
		// 计算位移的平方和
		final float simD = (float) StrictMath.sqrt((simDx * simDx) + (simDy * simDy));
		// 计算速度
		final float speed = StrictMath.min(simD, this.simulationProjectile.getSpeed() * deltaTime);
		// 如果位移大于0
		if (simD > 0) {
			// 更新X坐标
			this.x = this.x + ((speed * simDx) / simD);
			// 更新Y坐标
			this.y = this.y + ((speed * simDy) / simD);
			// 获取目标X坐标
			final float targetX = this.simulationProjectile.getTargetX();
			// 获取目标Y坐标
			final float targetY = this.simulationProjectile.getTargetY();
			// 计算X方向到目标的位移
			final float dxToTarget = targetX - this.x;
			// 计算Y方向到目标的位移
			final float dyToTarget = targetY - this.y;
			// 计算到目标的距离
			final float d2DToTarget = (float) StrictMath.sqrt((dxToTarget * dxToTarget) + (dyToTarget * dyToTarget));
			// 计算起始距离
			final float startingDistance = d2DToTarget + this.totalTravelDistance;
			// 计算起始距离的一半
			final float halfStartingDistance = startingDistance / 2f;

			// 计算目标高度与起始高度的差
			final float dtsz = this.targetHeight - this.startingHeight;
			// 计算高度变化率
			final float d1z = dtsz / (halfStartingDistance * 2);
			// 更新总旅行距离
			this.totalTravelDistance += speed;
			// 计算高度变化
			final float dz = d1z * this.totalTravelDistance;

			// 计算到峰值的距离
			final float distanceToPeak = this.totalTravelDistance - halfStartingDistance;
			// 计算归一化峰值距离
			final float normPeakDist = distanceToPeak / halfStartingDistance;
			// 计算当前高度百分比
			final float currentHeightPercentage = 1 - (normPeakDist * normPeakDist);
			// 计算当前高度的弧高
			final float arcCurrentHeight = currentHeightPercentage * this.arcPeakHeight;
			// 更新Z坐标
			this.z = this.startingHeight + dz + arcCurrentHeight;

			// 如果未完成
			if (!this.done) {
				// 更新偏航角
				this.yaw = (float) StrictMath.atan2(dyToTarget, dxToTarget);

				// 计算斜率
				final float slope = (-2 * (normPeakDist) * this.arcPeakHeight) / halfStartingDistance;
				// 更新俯仰角
				this.pitch = (float) StrictMath.atan2(slope + d1z, 1);
			}
		}
		// 如果完成
		if (this.done) {
			// 俯仰角设置为0
			this.pitch = 0;
			// 更新死亡时间
			this.deathTimeElapsed += deltaTime;
		}

		// 设置模型位置
		this.modelInstance.setLocation(this.x, this.y, this.z);
		// 设置偏航角
		this.modelInstance.localRotation.setFromAxisRad(0, 0, 1, this.yaw);
		// 设置俯仰角
		this.modelInstance.rotate(pitchHeap.setFromAxisRad(0, -1, 0, this.pitch));
		// 通知场景实例移动
		war3MapViewer.worldScene.instanceMoved(this.modelInstance, this.x, this.y);

		// 检查是否完全完成
		final boolean everythingDone = this.simulationProjectile.isDone() && (this.modelInstance.sequenceEnded
				|| (this.deathTimeElapsed >= war3MapViewer.simulation.getGameplayConstants().getBulletDeathTime()));
		// 如果完全完成，移除实例
		if (everythingDone) {
			war3MapViewer.worldScene.removeInstance(this.modelInstance);
		}
		// 返回是否完全完成
		return everythingDone;
	}

}
