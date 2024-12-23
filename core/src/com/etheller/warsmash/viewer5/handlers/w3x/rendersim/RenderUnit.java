package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.EnumSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSoundset;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.BuildingShadow;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons.CommandButtonListener;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons.CommandCardPopulatingAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbilityGenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;

public class RenderUnit implements RenderWidget {
	// 单位 虚无 半透明 顶点颜色
	// 定义一个半透明的青色常量
	public static final Color ETHEREAL = new Color(0.75f, 1, 0.5f, 0.5f);
	// 定义一个不透明的白色常量
	public static final Color DEFAULT = new Color(1, 1, 1, 1);
	// 定义一个四元数常量，用于临时存储旋转数据
	public static final Quaternion tempQuat = new Quaternion();

	// 定义颜色常量的字符串表示，用于配置文件中的替换
	private static final String RED = "red"; // 替换自 'uclr'
	private static final String GREEN = "green"; // 替换自 'uclg'
	private static final String BLUE = "blue"; // 替换自 'uclb'
	// 定义移动高度的字符串表示，用于配置文件中的替换
	private static final String MOVE_HEIGHT = "moveHeight"; // 替换自 'umvh'
	// 定义方向插值的字符串表示，用于配置文件中的替换
	private static final String ORIENTATION_INTERPOLATION = "orientInterp"; // 替换自 'uori'
	// 定义动画属性的字符串表示，用于配置文件中的替换
	public static final String ANIM_PROPS = "animProps"; // 替换自 'uani'
	// 定义附件动画属性的字符串表示，用于配置文件中的替换
	public static final String ATTACHMENT_ANIM_PROPS = "Attachmentanimprops"; // 替换自 'uaap'
	// 定义混合时间的字符串表示，用于配置文件中的替换
	private static final String BLEND_TIME = "blend"; // 替换自 'uble'
	// 定义建筑声音标签的字符串表示，用于配置文件中的替换
	private static final String BUILD_SOUND_LABEL = "BuildingSoundLabel"; // 替换自 'ubsl'
	// 定义单位选择高度的字符串表示，用于配置文件中的替换
	private static final String UNIT_SELECT_HEIGHT = "selZ"; // 替换自 'uslz'

	// 定义一个浮点数组，用于存储堆叠高度
	private static final float[] heapZ = new float[3];

	// 实例化一个复杂的MDX模型实例
	public MdxComplexInstance instance;
	// 游戏对象行
	public GameObject row;
	// 存储单位位置的数组
	public final float[] location = new float[3];
	// 单位选择时的缩放比例
	public float selectionScale;
	// 单位的声音集
	public UnitSoundset soundset;
	// 单位的肖像模型
	public MdxModel portraitModel;
	// 玩家索引
	public int playerIndex;
	// 模拟单位实例
	private final CUnit simulationUnit;
	// 单位的阴影移动器
	public SplatMover shadow;
	// 建筑阴影实例
	private BuildingShadow buildingShadowInstance;
	// 选择圈移动器
	public SplatMover selectionCircle;
	// 选择预览高亮移动器
	public SplatMover selectionPreviewHighlight;

	// 单位面向的角度
	private float facing;

	// 单位是否在游泳
	private boolean swimming;
	// 单位是否在工作
	private boolean working;

	// 单位是否死亡，默认为否
	private boolean dead = false;

	// 单位动画监听器实现
	private UnitAnimationListenerImpl unitAnimationListenerImpl;
	// 方向插值实例
	private OrientationInterpolation orientationInterpolation;
	// 当前转向速度
	private float currentTurnVelocity = 0;
	// 上次单位响应结束的时间戳
	public long lastUnitResponseEndTimeMillis;
	// 单位是否成为尸体
	private boolean corpse;
	// 单位是否是骨骼尸体
	private boolean boneCorpse;
	// 单位是否是建筑
	private boolean building;
	// 渲染单位类型数据
	private RenderUnitTypeData typeData;
	// 特殊艺术模型
	public MdxModel specialArtModel;
	// 超级喷溅移动器
	public SplatMover uberSplat;
	// 选择高度
	private float selectionHeight;
	// 首选的选中替代渲染单位
	private RenderUnit preferredSelectionReplacement;


	public RenderUnit(final War3MapViewer map, final MdxModel model, final GameObject row, final float x, final float y,
			final float z, final int playerIndex, final UnitSoundset soundset, final MdxModel portraitModel,
			final CUnit simulationUnit, final RenderUnitTypeData typeData, final MdxModel specialArtModel,
			final BuildingShadow buildingShadow, final float selectionCircleScaleFactor, final float animationWalkSpeed,
			final float animationRunSpeed, final float scalingValue) {
		this.simulationUnit = simulationUnit;
		resetRenderUnit(map, model, row, x, y, z, playerIndex, soundset, portraitModel, simulationUnit, typeData,
				specialArtModel, buildingShadow, selectionCircleScaleFactor, animationWalkSpeed, animationRunSpeed,
				scalingValue);

	}

	public void resetRenderUnit(final War3MapViewer map, final MdxModel model, final GameObject row, final float x,
			final float y, final float z, final int playerIndex, final UnitSoundset soundset,
			final MdxModel portraitModel, final CUnit simulationUnit, final RenderUnitTypeData typeData,
			final MdxModel specialArtModel, final BuildingShadow buildingShadow, final float selectionCircleScaleFactor,
			final float animationWalkSpeed, final float animationRunSpeed, final float scalingValue) {
		// 设置角色的肖像模型
		this.portraitModel = portraitModel;
		// 设置角色的类型数据
		this.typeData = typeData;
		// 设置角色的特殊艺术模型
		this.specialArtModel = specialArtModel;
		// 如果存在建筑阴影实例，则移除它
		if (this.buildingShadowInstance != null) {
			this.buildingShadowInstance.remove();
		}
		// 更新建筑阴影实例
		this.buildingShadowInstance = buildingShadow;
		// 如果存在实例，则分离它
		if (this.instance != null) {
			this.instance.detach();
		}
		// 添加一个新的MdxComplexInstance实例
		final MdxComplexInstance instance = (MdxComplexInstance) model.addInstance();

		// 设置角色的位置
		this.location[0] = x;
		this.location[1] = y;
		this.location[2] = z;
		// 移动实例到新位置
		instance.move(this.location);
		// 获取角色的朝向
		this.facing = simulationUnit.getFacing();
		// 将朝向转换为弧度
		final float angle = (float) Math.toRadians(this.facing);
		// 旋转实例以匹配角色的朝向
		instance.rotate(tempQuat.setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, angle));
		// 设置玩家索引
		this.playerIndex = playerIndex & 0xFFFF;
		// 设置实例的队伍颜色
		instance.setTeamColor(this.playerIndex);
		// 设置实例的场景
		instance.setScene(map.worldScene);
		// 创建并设置单位动画监听器
		this.unitAnimationListenerImpl = new UnitAnimationListenerImpl(instance, animationWalkSpeed, animationRunSpeed);
		simulationUnit.setUnitAnimationListener(this.unitAnimationListenerImpl);
		// 获取所需的动画名称
		final String requiredAnimationNames = row.getFieldAsString(ANIM_PROPS, 0);
		// 遍历动画名称，并为每个动画名称添加对应的次要标签
		TokenLoop:
		for (final String animationName : requiredAnimationNames.split(",")) {
			final String upperCaseToken = animationName.toUpperCase();
			for (final SecondaryTag secondaryTag : SecondaryTag.values()) {
				if (upperCaseToken.equals(secondaryTag.name())) {
					this.unitAnimationListenerImpl.addSecondaryTag(secondaryTag);
					continue TokenLoop;
				}
			}
		}

		// 如果行数据不为空，则进行以下操作
		if (row != null) {
			// 更新堆叠Z值
			heapZ[2] = simulationUnit.getFlyHeight();
			// 调整位置Z值以包含堆叠高度
			this.location[2] += heapZ[2];
			// 移动实例到新的堆叠位置
			instance.move(heapZ);
			String red;
			String green;
			String blue;
			red = RED;
			green = GREEN;
			blue = BLUE;
			// 设置实例的顶点颜色
			instance.setVertexColor(new float[]{(row.getFieldAsInteger(red, 0)) / 255f,
					(row.getFieldAsInteger(green, 0)) / 255f, (row.getFieldAsInteger(blue, 0)) / 255f});
			// 设置实例的统一缩放
			instance.uniformScale(scalingValue);

			// 设置选择时的缩放比例
			this.selectionScale = row.getFieldAsFloat(War3MapViewer.UNIT_SELECT_SCALE, 0) * selectionCircleScaleFactor;
			// 设置选择时的高度
			this.selectionHeight = row.getFieldAsFloat(UNIT_SELECT_HEIGHT, 0);
			// 获取方向插值的序号
			int orientationInterpolationOrdinal = row.getFieldAsInteger(ORIENTATION_INTERPOLATION, 0);
			// 确保方向插值序号在有效范围内
			if ((orientationInterpolationOrdinal < 0)
					|| (orientationInterpolationOrdinal >= OrientationInterpolation.VALUES.length)) {
				orientationInterpolationOrdinal = 0;
			}
			// 设置方向插值
			this.orientationInterpolation = OrientationInterpolation.VALUES[orientationInterpolationOrdinal];

			// 设置混合时间
			final float blendTime = row.getFieldAsFloat(BLEND_TIME, 0);
			instance.setBlendTime(blendTime * 1000.0f);
		}

		// 更新实例
		this.instance = instance;
		// 更新行数据
		this.row = row;
		// 设置音效集
		this.soundset = soundset;
		// 设置是否为建筑
		this.building = simulationUnit.isBuilding();
	}

	// 定义一个方法，用于填充命令卡
	public void populateCommandCard(final CSimulation game, final GameUI gameUI,
									final CommandButtonListener commandButtonListener, final AbilityDataUI abilityDataUI,
									final int subMenuOrderId, final boolean multiSelect, final int localPlayerIndex) {
		// 创建一个命令卡填充访问者实例，并重置其状态
		final CommandCardPopulatingAbilityVisitor commandCardPopulatingVisitor = CommandCardPopulatingAbilityVisitor.INSTANCE
				.reset(game, gameUI, this.simulationUnit, commandButtonListener, abilityDataUI, subMenuOrderId,
						multiSelect, localPlayerIndex);
		// 遍历模拟单元的所有能力
		for (final CAbility ability : this.simulationUnit.getAbilities()) {
			// 如果模拟单元没有暂停，或者能力是增益效果，或者是单一图标被动能力，则访问该能力
			if (!this.simulationUnit.isPaused() || (ability instanceof CBuff)
					|| (ability instanceof AbilityGenericSingleIconPassiveAbility)) {
				ability.visit(commandCardPopulatingVisitor);
			}
		}
	}

	@Override
	public void updateAnimations(final War3MapViewer map) {
		final boolean wasHidden = this.instance.hidden();
		if (this.simulationUnit.isHidden()
				|| !this.simulationUnit.isVisible(map.simulation, map.getLocalPlayerIndex())) {
			if (!wasHidden) {
				if (this.selectionCircle != null) {
					this.selectionCircle.hide();
				}
				if (this.selectionPreviewHighlight != null) {
					this.selectionPreviewHighlight.hide();
				}
				if (this.shadow != null) {
					this.shadow.hide();
				}
			}
			this.instance.hide();
			return;
		}
		else {
			this.instance.show();
			if (wasHidden) {
				if (this.selectionCircle != null) {
					this.selectionCircle.show(map.terrain.centerOffset);
				}
				if (this.selectionPreviewHighlight != null) {
					this.selectionPreviewHighlight.show(map.terrain.centerOffset);
				}
				if (this.shadow != null) {
					this.shadow.show(map.terrain.centerOffset);
				}
				repositioned(map);
			}
		}
		final float prevX = this.location[0];
		final float prevY = this.location[1];
		final float simulationX = this.simulationUnit.getX();
		final float simulationY = this.simulationUnit.getY();
		final float deltaTime = Gdx.graphics.getDeltaTime();
		final float simDx = simulationX - this.location[0];
		final float simDy = simulationY - this.location[1];
		final float distanceToSimulation = (float) Math.sqrt((simDx * simDx) + (simDy * simDy));
		final int speed = this.simulationUnit.getSpeed();
		final float speedDelta = speed * deltaTime;
		if ((distanceToSimulation > speedDelta) && (deltaTime < 1.0)) {
			// The 1.0 here says that after 1 second of lag, units just teleport to show
			// where they actually are
			this.location[0] += (speedDelta * simDx) / distanceToSimulation;
			this.location[1] += (speedDelta * simDy) / distanceToSimulation;
		}
		else {
			this.location[0] = simulationX;
			this.location[1] = simulationY;
		}
		final float dx = this.location[0] - prevX;
		final float dy = this.location[1] - prevY;
		final float groundHeight;
		final MovementType movementType = this.simulationUnit.getMovementType();
		final short terrainPathing = map.terrain.pathingGrid.getPathing(this.location[0], this.location[1]);
		boolean swimming = (movementType == MovementType.AMPHIBIOUS)
				&& PathingGrid.isPathingFlag(terrainPathing, PathingGrid.PathingType.SWIMMABLE)
				&& !PathingGrid.isPathingFlag(terrainPathing, PathingGrid.PathingType.WALKABLE);
		final boolean working = this.simulationUnit.getBuildQueueTypes()[0] != null;
		final float groundHeightTerrain = map.terrain.getGroundHeight(this.location[0], this.location[1]);
		float groundHeightTerrainAndWater;
		MdxComplexInstance currentWalkableUnder;
		final boolean standingOnWater = (swimming) || (movementType == MovementType.FLOAT)
				|| (movementType == MovementType.FLY) || (movementType == MovementType.HOVER);
		if (standingOnWater) {
			groundHeightTerrainAndWater = Math.max(groundHeightTerrain,
					map.terrain.getWaterHeight(this.location[0], this.location[1]));
		}
		else {
			// land units will have their feet pass under the surface of the water
			groundHeightTerrainAndWater = groundHeightTerrain;
		}
		if (movementType == MovementType.FLOAT) {
			// boats cant go on bridges
			groundHeight = groundHeightTerrainAndWater;
			currentWalkableUnder = null;
		}
		else {
			currentWalkableUnder = map.getHighestWalkableUnder(this.location[0], this.location[1]);
			War3MapViewer.gdxRayHeap.set(this.location[0], this.location[1], 4096, 0, 0, -8192);
			if ((currentWalkableUnder != null)
					&& currentWalkableUnder.intersectRayWithCollision(War3MapViewer.gdxRayHeap,
							War3MapViewer.intersectionHeap, true, true)
					&& (War3MapViewer.intersectionHeap.z > groundHeightTerrainAndWater)) {
				groundHeight = War3MapViewer.intersectionHeap.z;
				swimming = false; // Naga Royal Guard should slither across a bridge, not swim in rock
			}
			else {
				groundHeight = groundHeightTerrainAndWater;
				currentWalkableUnder = null;
			}
		}
		if (swimming && !this.swimming) {
			this.unitAnimationListenerImpl.addSecondaryTag(AnimationTokens.SecondaryTag.SWIM);
		}
		else if (!swimming && this.swimming) {
			this.unitAnimationListenerImpl.removeSecondaryTag(AnimationTokens.SecondaryTag.SWIM);
		}
		if (working && !this.working) {
			this.unitAnimationListenerImpl.addSecondaryTag(AnimationTokens.SecondaryTag.WORK);
		}
		else if (!working && this.working) {
			this.unitAnimationListenerImpl.removeSecondaryTag(AnimationTokens.SecondaryTag.WORK);
		}
		this.swimming = swimming;
		this.working = working;
		final boolean dead = this.simulationUnit.isDead();
		final boolean corpse = this.simulationUnit.isCorpse();
		final boolean boneCorpse = this.simulationUnit.isBoneCorpse();
		final boolean building = this.simulationUnit.isBuilding();
		if (dead) {
			if (!this.dead) {
				this.unitAnimationListenerImpl.playAnimation(true, PrimaryTag.DEATH, SequenceUtils.EMPTY, 1.0f, true);
				removeSplats(map);
			}
		}
		else if (building != this.building) {
			if (building) {
				if (this.shadow != null) {
					this.shadow.hide();
				}
				createBuildingDecalSplats(map);
			}
			else {
				if (this.shadow != null) {
					this.shadow.show(map.terrain.centerOffset);
				}
				removeBuildingDecalSplats(map);
			}
			this.building = building;
		}
		if (boneCorpse && !this.boneCorpse) {
			if (this.simulationUnit.getUnitType().isHero()) {
				this.unitAnimationListenerImpl.playAnimationWithDuration(true, PrimaryTag.DISSIPATE,
						SequenceUtils.EMPTY, this.simulationUnit.getEndingDecayTime(map.simulation), true);
			}
			else {
				this.unitAnimationListenerImpl.playAnimationWithDuration(true, PrimaryTag.DECAY, SequenceUtils.BONE,
						this.simulationUnit.getEndingDecayTime(map.simulation), true);
			}
		}
		else if (corpse && !this.corpse) {
			this.unitAnimationListenerImpl.playAnimationWithDuration(true, PrimaryTag.DECAY, SequenceUtils.FLESH,
					map.simulation.getGameplayConstants().getDecayTime(), true);
		}
		this.dead = dead;
		this.corpse = corpse;
		this.boneCorpse = boneCorpse;
		this.location[2] = this.simulationUnit.getFlyHeight() + groundHeight;
		final float selectionCircleHeight = this.selectionHeight + groundHeight;
		this.instance.moveTo(this.location);
		float simulationFacing = this.simulationUnit.getFacing();
		if (simulationFacing < 0) {
			simulationFacing += 360;
		}
		float renderFacing = this.facing;
		if (renderFacing < 0) {
			renderFacing += 360;
		}
		float facingDelta = simulationFacing - renderFacing;
		if (facingDelta < -180) {
			facingDelta = 360 + facingDelta;
		}
		if (facingDelta > 180) {
			facingDelta = -360 + facingDelta;
		}
		final float absoluteFacingDelta = Math.abs(facingDelta);
		final float turningSign = Math.signum(facingDelta);

		final float absoluteFacingDeltaRadians = (float) Math.toRadians(absoluteFacingDelta);
		float acceleration;
		final boolean endPhase = (absoluteFacingDeltaRadians <= this.orientationInterpolation.getEndingAccelCutoff())
				&& ((this.currentTurnVelocity * turningSign) > 0);
		if (endPhase) {
			this.currentTurnVelocity = (1
					- ((this.orientationInterpolation.getEndingAccelCutoff() - absoluteFacingDeltaRadians)
							/ this.orientationInterpolation.getEndingAccelCutoff()))
					* (this.orientationInterpolation.getMaxVelocity()) * turningSign;
		}
		else {
			acceleration = this.orientationInterpolation.getStartingAcceleration() * turningSign;
			this.currentTurnVelocity = this.currentTurnVelocity + acceleration;
		}
		if ((this.currentTurnVelocity * turningSign) > this.orientationInterpolation.getMaxVelocity()) {
			this.currentTurnVelocity = this.orientationInterpolation.getMaxVelocity() * turningSign;
		}
		float angleToAdd = (float) ((Math.toDegrees(this.currentTurnVelocity) * deltaTime) / 0.03f);

		if (absoluteFacingDelta < Math.abs(angleToAdd)) {
			angleToAdd = facingDelta;
			this.currentTurnVelocity = 0.0f;
		}
		this.facing = (((this.facing + angleToAdd) % 360) + 360) % 360;
		this.instance.setLocalRotation(tempQuat.setFromAxis(RenderMathUtils.VEC3_UNIT_Z, this.facing));

		final float facingRadians = (float) Math.toRadians(this.facing);
		final float maxPitch = (float) Math.toRadians(this.typeData.getMaxPitch());
		final float maxRoll = (float) Math.toRadians(this.typeData.getMaxRoll());
		final float sampleRadius = this.typeData.getElevationSampleRadius();
		float pitch, roll;
		final float pitchSampleForwardX = this.location[0] + (sampleRadius * (float) Math.cos(facingRadians));
		final float pitchSampleForwardY = this.location[1] + (sampleRadius * (float) Math.sin(facingRadians));
		final float pitchSampleBackwardX = this.location[0] - (sampleRadius * (float) Math.cos(facingRadians));
		final float pitchSampleBackwardY = this.location[1] - (sampleRadius * (float) Math.sin(facingRadians));
		final double leftOfFacingAngle = facingRadians + (Math.PI / 2);
		final float rollSampleForwardX = this.location[0] + (sampleRadius * (float) Math.cos(leftOfFacingAngle));
		final float rollSampleForwardY = this.location[1] + (sampleRadius * (float) Math.sin(leftOfFacingAngle));
		final float rollSampleBackwardX = this.location[0] - (sampleRadius * (float) Math.cos(leftOfFacingAngle));
		final float rollSampleBackwardY = this.location[1] - (sampleRadius * (float) Math.sin(leftOfFacingAngle));
		final float pitchSampleGroundHeight1;
		final float pitchSampleGroundHeight2;
		final float rollSampleGroundHeight1;
		final float rollSampleGroundHeight2;
		if (currentWalkableUnder != null) {
			pitchSampleGroundHeight1 = getGroundHeightSample(groundHeight, currentWalkableUnder, pitchSampleBackwardX,
					pitchSampleBackwardY);
			pitchSampleGroundHeight2 = getGroundHeightSample(groundHeight, currentWalkableUnder, pitchSampleForwardX,
					pitchSampleForwardY);
			rollSampleGroundHeight1 = getGroundHeightSample(groundHeight, currentWalkableUnder, rollSampleBackwardX,
					rollSampleBackwardY);
			rollSampleGroundHeight2 = getGroundHeightSample(groundHeight, currentWalkableUnder, rollSampleForwardX,
					rollSampleForwardY);
		}
		else {
			final float pitchGroundHeight1 = map.terrain.getGroundHeight(pitchSampleBackwardX, pitchSampleBackwardY);
			final float pitchGroundHeight2 = map.terrain.getGroundHeight(pitchSampleForwardX, pitchSampleForwardY);
			final float rollGroundHeight1 = map.terrain.getGroundHeight(rollSampleBackwardX, rollSampleBackwardY);
			final float rollGroundHeight2 = map.terrain.getGroundHeight(rollSampleForwardX, rollSampleForwardY);
			if (standingOnWater) {
				pitchSampleGroundHeight1 = Math.max(pitchGroundHeight1,
						map.terrain.getWaterHeight(pitchSampleBackwardX, pitchSampleBackwardY));
				pitchSampleGroundHeight2 = Math.max(pitchGroundHeight2,
						map.terrain.getWaterHeight(pitchSampleForwardX, pitchSampleForwardY));
				rollSampleGroundHeight1 = Math.max(rollGroundHeight1,
						map.terrain.getWaterHeight(rollSampleBackwardX, rollSampleBackwardY));
				rollSampleGroundHeight2 = Math.max(rollGroundHeight2,
						map.terrain.getWaterHeight(rollSampleForwardX, rollSampleForwardY));
			}
			else {
				pitchSampleGroundHeight1 = pitchGroundHeight1;
				pitchSampleGroundHeight2 = pitchGroundHeight2;
				rollSampleGroundHeight1 = rollGroundHeight1;
				rollSampleGroundHeight2 = rollGroundHeight2;
			}
		}
		pitch = Math.max(-maxPitch, Math.min(maxPitch,
				(float) Math.atan2(pitchSampleGroundHeight2 - pitchSampleGroundHeight1, sampleRadius * 2)));
		roll = Math.max(-maxRoll, Math.min(maxRoll,
				(float) Math.atan2(rollSampleGroundHeight2 - rollSampleGroundHeight1, sampleRadius * 2)));
		this.instance.rotate(tempQuat.setFromAxisRad(RenderMathUtils.VEC3_UNIT_Y, -pitch));
		this.instance.rotate(tempQuat.setFromAxisRad(RenderMathUtils.VEC3_UNIT_X, roll));

		map.worldScene.instanceMoved(this.instance, this.location[0], this.location[1]);
		if (this.shadow != null) {
			this.shadow.move(dx, dy, map.terrain.centerOffset);
			this.shadow.setHeightAbsolute(currentWalkableUnder != null, groundHeight + map.imageWalkableZOffset);
		}
		if (this.selectionCircle != null) {
			this.selectionCircle.move(dx, dy, map.terrain.centerOffset);
			this.selectionCircle.setHeightAbsolute(
					(currentWalkableUnder != null)
							|| ((movementType == MovementType.FLY) || (movementType == MovementType.HOVER)),
					selectionCircleHeight + map.imageWalkableZOffset);
		}
		if (this.selectionPreviewHighlight != null) {
			this.selectionPreviewHighlight.move(dx, dy, map.terrain.centerOffset);
			this.selectionPreviewHighlight.setHeightAbsolute(
					(currentWalkableUnder != null)
							|| ((movementType == MovementType.FLY) || (movementType == MovementType.HOVER)),
					selectionCircleHeight + map.imageWalkableZOffset);
		}
		this.unitAnimationListenerImpl.update();
		if (!dead && this.simulationUnit.isConstructingOrUpgrading()) {
			this.instance.setFrameByRatio(
					this.simulationUnit.getConstructionProgress() / this.simulationUnit.getUnitType().getBuildTime());
		}
	}

	private void removeSplats(final War3MapViewer map) {
		if (this.shadow != null) {
			this.shadow.destroy(Gdx.gl30, map.terrain.centerOffset);
			this.shadow = null;
		}
		removeBuildingDecalSplats(map);
		if (this.selectionCircle != null) {
			this.selectionCircle.destroy(Gdx.gl30, map.terrain.centerOffset);
			this.selectionCircle = null;
		}
		if (this.selectionPreviewHighlight != null) {
			this.selectionPreviewHighlight.destroy(Gdx.gl30, map.terrain.centerOffset);
			this.selectionPreviewHighlight = null;
		}
	}

	public void removeBuildingDecalSplats(final War3MapViewer map) {
		if (this.buildingShadowInstance != null) {
			this.buildingShadowInstance.remove();
			this.buildingShadowInstance = null;
		}
		if (this.uberSplat != null) {
			this.uberSplat.destroy(Gdx.gl30, map.terrain.centerOffset);
			this.uberSplat = null;
		}
	}

	public void createBuildingDecalSplats(final War3MapViewer map) {
		final float unitX = this.simulationUnit.getX();
		final float unitY = this.simulationUnit.getY();
		if (this.buildingShadowInstance == null) {
			final String buildingShadow = this.typeData.getBuildingShadow();
			if (buildingShadow != null) {
				this.buildingShadowInstance = map.terrain.addShadow(buildingShadow, unitX, unitY);
			}
		}
		if (this.uberSplat == null) {
			final String uberSplatTexturePath = this.typeData.getUberSplat();
			if (uberSplatTexturePath != null) {
				this.uberSplat = map.addUberSplatIngame(unitX, unitY, uberSplatTexturePath,
						this.typeData.getUberSplatScaleValue());
			}
		}
	}

	private float getGroundHeightSample(final float groundHeight, final MdxComplexInstance currentWalkableUnder,
			final float sampleX, final float sampleY) {
		final float sampleGroundHeight;
		War3MapViewer.gdxRayHeap.origin.x = sampleX;
		War3MapViewer.gdxRayHeap.origin.y = sampleY;
		if (currentWalkableUnder.intersectRayWithCollision(War3MapViewer.gdxRayHeap, War3MapViewer.intersectionHeap,
				true, true)) {
			sampleGroundHeight = War3MapViewer.intersectionHeap.z;
		}
		else {
			sampleGroundHeight = groundHeight;
		}
		return sampleGroundHeight;
	}

	public CUnit getSimulationUnit() {
		return this.simulationUnit;
	}

	public EnumSet<AnimationTokens.SecondaryTag> getSecondaryAnimationTags() {
		return this.unitAnimationListenerImpl.secondaryAnimationTags;
	}

	/**
	 * 当单位的位置发生变化时调用此方法
	 *
	 * @param map 地图视图对象
	 */
	public void repositioned(final War3MapViewer map) {
		// 获取单位之前的 X 坐标
		final float prevX = this.location[0];
		// 获取单位之前的 Y 坐标
		final float prevY = this.location[1];
		// 获取单位当前的 X 坐标
		final float simulationX = this.simulationUnit.getX();
		// 获取单位当前的 Y 坐标
		final float simulationY = this.simulationUnit.getY();
		// 计算单位在 X 方向上的位移
		final float dx = simulationX - prevX;
		// 计算单位在 Y 方向上的位移
		final float dy = simulationY - prevY;
		// 如果阴影对象存在，则移动阴影
		if (this.shadow != null) {
			this.shadow.move(dx, dy, map.terrain.centerOffset);
		}
		// 如果选择圆圈对象存在，则移动选择圆圈
		if (this.selectionCircle != null) {
			this.selectionCircle.move(dx, dy, map.terrain.centerOffset);
		}
		// 如果选择预览高亮对象存在，则移动选择预览高亮
		if (this.selectionPreviewHighlight != null) {
			this.selectionPreviewHighlight.move(dx, dy, map.terrain.centerOffset);
		}
		// 更新单位的当前位置
		this.location[0] = this.simulationUnit.getX();
		this.location[1] = this.simulationUnit.getY();
	}


	@Override
	public MdxComplexInstance getInstance() {
		return this.instance;
	}

	@Override
	public CWidget getSimulationWidget() {
		return this.simulationUnit;
	}

	@Override
	public boolean isIntersectedOnMeshAlways() {
		return this.simulationUnit.isBuilding();
	}

	@Override
	public float getSelectionScale() {
		return this.selectionScale;
	}

	@Override
	public float getX() {
		return this.location[0];
	}

	@Override
	public float getY() {
		return this.location[1];
	}

	@Override
	public float getZ() {
		return this.location[2];
	}

	@Override
	public void unassignSelectionCircle() {
		this.selectionCircle = null;
	}

	@Override
	public void assignSelectionCircle(final SplatMover t) {
		this.selectionCircle = t;
	}

	@Override
	public void unassignSelectionPreviewHighlight() {
		this.selectionPreviewHighlight = null;
	}

	@Override
	public void assignSelectionPreviewHighlight(final SplatMover t) {
		this.selectionPreviewHighlight = t;
	}

	@Override
	public boolean isSelectable(final CSimulation simulation, final int byPlayer) {
		return this.simulationUnit.isVisible(simulation, byPlayer); // later needs locust
	}

	@Override
	public SplatMover getSelectionPreviewHighlight() {
		return this.selectionPreviewHighlight;
	}

	public void onRemove(final War3MapViewer map) {
		removeSplats(map);
	}

	public void setPreferredSelectionReplacement(final RenderUnit preferredSelectionReplacement) {
		this.preferredSelectionReplacement = preferredSelectionReplacement;
	}

	public RenderUnit getPreferredSelectionReplacement() {
		return this.preferredSelectionReplacement;
	}

	@Override
	public SplatMover getSelectionCircle() {
		return this.selectionCircle;
	}

	public boolean groupsWith(final RenderUnit selectedUnit) {
		return this.simulationUnit.getUnitType() == selectedUnit.getSimulationUnit().getUnitType();
	}

	public void setPlayerColor(final int ordinal) {
		this.playerIndex = ordinal;
		getInstance().setTeamColor(ordinal);
	}

	public float getFacing() {
		return this.facing;
	}

	public void setFacing(final float facing) {
		this.facing = facing;
	}

	@Override
	public boolean isShowSelectionCircleAboveWater() {
		return this.simulationUnit.isMovementOnWaterAllowed()
				|| (this.simulationUnit.getMovementType() == MovementType.HOVER)
				|| (this.simulationUnit.getMovementType() == MovementType.FLY);
	}

	public RenderUnitTypeData getTypeData() {
		return this.typeData;
	}

	public void setVertexColoring(Color color) {
		this.instance.setVertexColor(color);
	}

	public void setVertexColoring(float r, float g, float b) {
		final float[] color = new float[] { r, g, b };
		this.instance.setVertexColor(color);
	}

	public void setVertexColoring(float r, float g, float b, float a) {
		final float[] color = new float[] { r, g, b, a };
		this.instance.setVertexColor(color);
	}
}
