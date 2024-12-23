package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;

public class RenderUnitTypeData {
	// 定义最大俯仰角，用于控制渲染单元在垂直方向上的最大旋转角度
	private final float maxPitch;

	// 定义最大翻滚角，用于控制渲染单元在水平方向上的最大旋转角度
	private final float maxRoll;

	// 定义采样半径，可能用于渲染时的某些效果计算，如阴影、光照等
	private final float sampleRadius;

	// 定义是否允许自定义队伍颜色，用于控制是否可以使用非默认的队伍颜色
	private final boolean allowCustomTeamColor;

	// 定义队伍颜色，当不允许自定义时使用默认的队伍颜色值
	private final int teamColor;

	// 定义动画奔跑速度，控制渲染单元在奔跑状态下的动画播放速度
	private final float animationRunSpeed;

	// 定义缩放值，用于控制渲染单元的大小
	private final float scalingValue;

	// 定义动画行走速度，控制渲染单元在行走状态下的动画播放速度
	private final float animationWalkSpeed;

	// 定义建筑阴影，可能是用于渲染建筑时的阴影效果设置
	private final String buildingShadow;

	// 定义超级溅射效果，可能是某种特殊的视觉效果
	private final String uberSplat;

	// 定义超级溅射效果的缩放值，用于控制超级溅射效果的大小
	private final float uberSplatScaleValue;

	// 定义附件所需的动画名称集合，用于确保某些附件有正确的动画效果
	private final EnumSet<SecondaryTag> requiredAnimationNamesForAttachments;


	public RenderUnitTypeData(final float maxPitch, final float maxRoll, final float sampleRadius,
			final boolean allowCustomTeamColor, final int teamColor, final float animationRunSpeed,
			final float animationWalkSpeed, final float scalingValue, final String buildingShadow,
			final String uberSplat, final float uberSplatScaleValue,
			final EnumSet<SecondaryTag> requiredAnimationNamesForAttachments) {
		this.maxPitch = maxPitch;
		this.maxRoll = maxRoll;
		this.sampleRadius = sampleRadius;
		this.allowCustomTeamColor = allowCustomTeamColor;
		this.teamColor = teamColor;
		this.animationRunSpeed = animationRunSpeed;
		this.animationWalkSpeed = animationWalkSpeed;
		this.scalingValue = scalingValue;
		this.buildingShadow = buildingShadow;
		this.uberSplat = uberSplat;
		this.uberSplatScaleValue = uberSplatScaleValue;
		this.requiredAnimationNamesForAttachments = requiredAnimationNamesForAttachments;
	}

	public float getMaxPitch() {
		return this.maxPitch;
	}

	public float getMaxRoll() {
		return this.maxRoll;
	}

	public float getElevationSampleRadius() {
		return this.sampleRadius;
	}

	public boolean isAllowCustomTeamColor() {
		return this.allowCustomTeamColor;
	}

	public int getTeamColor() {
		return this.teamColor;
	}

	public float getAnimationRunSpeed() {
		return this.animationRunSpeed;
	}

	public float getAnimationWalkSpeed() {
		return this.animationWalkSpeed;
	}

	public float getScalingValue() {
		return this.scalingValue;
	}

	public String getBuildingShadow() {
		return this.buildingShadow;
	}

	public String getUberSplat() {
		return this.uberSplat;
	}

	public float getUberSplatScaleValue() {
		return this.uberSplatScaleValue;
	}

	public EnumSet<SecondaryTag> getRequiredAnimationNamesForAttachments() {
		return requiredAnimationNamesForAttachments;
	}
}
