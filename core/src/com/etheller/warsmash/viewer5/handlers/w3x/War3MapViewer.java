package com.etheller.warsmash.viewer5.handlers.w3x;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.etheller.warsmash.common.FetchDataTypeName;
import com.etheller.warsmash.common.LoadGenericCallback;
import com.etheller.warsmash.datasources.CompoundDataSource;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.MpqDataSource;
import com.etheller.warsmash.datasources.SubdirDataSource;
import com.etheller.warsmash.networking.GameTurnManager;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.w3x.War3Map;
import com.etheller.warsmash.parsers.w3x.doo.War3MapDoo;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapObjectData;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapRuntimeObjectData;
import com.etheller.warsmash.parsers.w3x.unitsdoo.War3MapUnitsDoo;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.parsers.w3x.wpm.War3MapWpm;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.StandardObjectData;
import com.etheller.warsmash.units.custom.WTS;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.MappedData;
import com.etheller.warsmash.util.Quadtree;
import com.etheller.warsmash.util.QuadtreeIntersector;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.util.WorldEditStrings;
import com.etheller.warsmash.viewer5.CanvasProvider;
import com.etheller.warsmash.viewer5.GenericResource;
import com.etheller.warsmash.viewer5.Grid;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.SceneLightManager;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.WorldScene;
import com.etheller.warsmash.viewer5.gl.WebGL;
import com.etheller.warsmash.viewer5.handlers.AbstractMdxModelViewer;
import com.etheller.warsmash.viewer5.handlers.mdx.Attachment;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler.ShaderEnvironmentType;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxNode;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.tga.TgaFile;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.BuildingShadow;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.GroundTexture;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.PathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.RemovablePathingMapInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.RenderCorner;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.Terrain;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.Terrain.Splat;
import com.etheller.warsmash.viewer5.handlers.w3x.lightning.LightningEffectModel;
import com.etheller.warsmash.viewer5.handlers.w3x.lightning.LightningEffectModelHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.lightning.LightningEffectNode;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderAttackInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderDoodad;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderItem;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderLightningEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderSpellEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnitTypeData;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.BuffUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.EffectAttachmentUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.EffectAttachmentUIMissile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidgetFilterFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityCollisionProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CCollisionProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CPsuedoProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CPlayerFogOfWar;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentLightning;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentLightningMovable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentModel;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderController;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.SettableCommandErrorListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.sound.KeyedSounds;

import mpq.MPQArchive;
import mpq.MPQException;

public class War3MapViewer extends AbstractMdxModelViewer {
	// 定义一个不可变的字符串列表，包含一个元素"origin"
	private static final List<String> ORIGIN_STRING_LIST = Arrays.asList("origin");

	// 定义一个静态整型变量DEBUG_DEPTH，用于调试时的深度控制
	public static int DEBUG_DEPTH = 9999;

	// 定义两个War3ID常量，分别代表英雄原始代码和复活技能的原始代码
	private static final War3ID ABILITY_HERO_RAWCODE = War3ID.fromString("AHer");
	private static final War3ID ABILITY_REVIVE_RAWCODE = War3ID.fromString("Arev");

	// 定义两个颜色常量，分别用于表示木材和金币的占位符颜色
	private static final Color PLACEHOLDER_LUMBER_COLOR = new Color(0.0f, 200f / 255f, 80f / 255f, 1.0f);
	private static final Color PLACEHOLDER_GOLD_COLOR = new Color(1.0f, 220f / 255f, 0f, 1.0f);

	// 定义一系列与游戏单位相关的字符串常量，这些常量用于替换原有的文件名或标识符
	private static final String UNIT_FILE = "file"; // 替换自 'umdl'
	// 定义常量字符串，用于表示特殊的单位艺术效果，原值 'uspa'
	private static final String UNIT_SPECIAL = "Specialart";

	// 定义常量字符串，用于表示超级溅射效果，原值 'uubs'
	private static final String UBER_SPLAT = "uberSplat";

	// 定义常量字符串，用于表示单位阴影效果，原值 'ushu'
	private static final String UNIT_SHADOW = "unitShadow";

	// 定义常量字符串，用于表示单位阴影的X坐标偏移，原值 'ushx'
	private static final String UNIT_SHADOW_X = "shadowX";

	// 定义常量字符串，用于表示单位阴影的Y坐标偏移，原值 'ushy'
	private static final String UNIT_SHADOW_Y = "shadowY";

	// 定义常量字符串，用于表示单位阴影的宽度，原值 'ushw'
	private static final String UNIT_SHADOW_W = "shadowW";

	// 定义常量字符串，用于表示单位阴影的高度，原值 'ushh'
	private static final String UNIT_SHADOW_H = "shadowH";

	// 定义常量字符串，用于表示建筑物的阴影效果，原值 'ushb'
	private static final String BUILDING_SHADOW = "buildingShadow";

	// 定义常量字符串，用于表示单位选择时的缩放比例，原值 'ussc'
	public static final String UNIT_SELECT_SCALE = "scale";

	// 定义常量字符串，用于表示单位的音效集，原值 'usnd'
	private static final String UNIT_SOUNDSET = "unitSound";
	private static final String ITEM_FILE = "file"; // 替换自 'ifil'，表示物品文件
	private static final String UNIT_PATHING = "pathTex"; // 替换自 'upat'，表示单位的寻路贴图
	private static final String DESTRUCTABLE_PATHING = "pathTex"; // 替换自 'bptx'，表示可破坏物的寻路贴图
	private static final String DESTRUCTABLE_PATHING_DEATH = "pathTexDeath"; // 替换自 'bptd'，表示可破坏物死亡时的寻路贴图
	private static final String ELEVATION_SAMPLE_RADIUS = "elevRad"; // 替换自 'uerd'，表示海拔采样半径
	private static final String MAX_PITCH = "maxPitch"; // 替换自 'umxp'，表示最大俯仰角
	private static final String ALLOW_CUSTOM_TEAM_COLOR = "customTeamColor"; // 替换自 'utcc'，表示是否允许自定义队伍颜色
	private static final String TEAM_COLOR = "teamColor"; // 替换自 'utco'，表示队伍颜色
	private static final String MAX_ROLL = "maxRoll"; // 替换自 'umxr'，表示最大翻滚角
	private static final String ANIMATION_RUN_SPEED = "run"; // 替换自 'urun'，表示动画奔跑速度
	private static final String ANIMATION_WALK_SPEED = "walk"; // 替换自 'uwal'，表示动画行走速度
	private static final String MODEL_SCALE = "modelScale"; // 替换自 'usca'，表示模型缩放比例
	// 定义一个War3ID类型的常量sloc，用于存储从字符串"sloc"解析而来的ID
	private static final War3ID sloc = War3ID.fromString("sloc");

	// 定义一个LoadGenericCallback类型的常量stringDataCallback，用于处理字符串数据的回调
	private static final LoadGenericCallback stringDataCallback = new StringDataCallbackImplementation();

	// 定义一个长度为6的float数组rayHeap，用于存储光线相关的临时数据
	private static final float[] rayHeap = new float[6];

	// 定义一个Ray类型的常量gdxRayHeap，用于存储光线的信息
	public static final Ray gdxRayHeap = new Ray();

	// 定义一个Plane类型的常量planeHeap，用于存储平面的信息
	public static final Plane planeHeap = new Plane();

	// 定义一个Vector2类型的常量mousePosHeap，用于存储鼠标位置的临时数据
	private static final Vector2 mousePosHeap = new Vector2();

	// 定义一个Vector3类型的常量normalHeap，用于存储法线向量的临时数据
	private static final Vector3 normalHeap = new Vector3();

	// 定义一个Vector3类型的常量intersectionHeap，用于存储交点位置的临时数据
	public static final Vector3 intersectionHeap = new Vector3();

	// 定义另一个Vector3类型的常量intersectionHeap2，用于存储另一个可能的交点位置的临时数据
	public static final Vector3 intersectionHeap2 = new Vector3();

	// 定义一个Rectangle类型的常量rectangleHeap，用于存储矩形区域的临时数据
	private static final Rectangle rectangleHeap = new Rectangle();

	// 定义一个StreamDataCallbackImplementation类型的常量streamDataCallback，用于处理流数据的回调
	public static final StreamDataCallbackImplementation streamDataCallback = new StreamDataCallbackImplementation();


	// 定义一个WorldScene对象，用于存储游戏世界的场景信息
	public WorldScene worldScene;

	// 标记是否有任何资源已经准备就绪
	public boolean anyReady;

	// 存储地形数据的MappedData对象
	public MappedData terrainData = new MappedData();

	// 存储悬崖类型数据的MappedData对象
	public MappedData cliffTypesData = new MappedData();

	// 存储水域数据的MappedData对象
	public MappedData waterData = new MappedData();

	// 标记地形数据是否已经准备就绪
	public boolean terrainReady;

	// 标记悬崖类型数据是否已经准备就绪
	public boolean cliffsReady;

	// 标记装饰物和可破坏物是否已经加载
	public boolean doodadsAndDestructiblesLoaded;

	// 存储装饰物数据的MappedData对象
	public MappedData doodadsData = new MappedData();

	// 存储装饰物元数据的MappedData对象
	public MappedData doodadMetaData = new MappedData();

	// 存储可破坏物元数据的MappedData对象
	public MappedData destructableMetaData = new MappedData();

	// 存储渲染装饰物的列表
	public List<RenderDoodad> doodads = new ArrayList<>();

	// 存储渲染贴花的列表
	public List<RenderDoodad> decals = new ArrayList<>();

	// 存储地形装饰物的列表
	public List<TerrainDoodad> terrainDoodads = new ArrayList<>();

	// 标记装饰物数据是否已经准备就绪
	public boolean doodadsReady;

	// 标记单位和小物件是否已经加载
	public boolean unitsAndItemsLoaded;

	// 存储单位数据的MappedData对象
	public MappedData unitsData = new MappedData();

	// 存储单位元数据的MappedData对象
	public MappedData unitMetaData = new MappedData();

	// 存储渲染小部件的列表
	public List<RenderWidget> widgets = new ArrayList<>();

	// 存储渲染单位的列表
	public List<RenderUnit> units = new ArrayList<>();

	// 存储渲染效果的列表，例如弹道
	public List<RenderEffect> projectiles = new ArrayList<>();

	// 标记单位数据是否已经准备就绪
	public boolean unitsReady;

	// 存储War3Map对象的引用，用于处理地图文件
	public War3Map mapMpq;

	// 定义一个私有的 DataSource 对象，用于存储游戏数据
	private final DataSource gameDataSource;

	// 定义一个 Terrain 类型的公共变量，用于表示地形信息
	public Terrain terrain;

	// 定义一个公共整型变量 renderPathing，用于控制是否渲染寻路信息，默认值为0（不渲染）
	public int renderPathing = 0;

	// 定义一个公共整型变量 renderLighting，用于控制是否渲染光照信息，默认值为1（渲染）
	public int renderLighting = 1;


	// 存储被选中的喷漆模型键的集合
	private final Set<String> selectedSplatModelKeys = new HashSet<>();

	// 存储被选中的渲染小部件的列表
	public List<RenderWidget> selected = new ArrayList<>();

	// 存储鼠标高亮显示的喷漆模型键的集合
	private final Set<String> mouseHighlightSplatModelKeys = new HashSet<>();

	// 存储鼠标高亮显示的渲染小部件的列表
	private final List<RenderWidget> mouseHighlightWidgets = new ArrayList<>();

	// 存储单位确认声音的数据表
	private DataTable unitAckSoundsTable;

	// 存储单位战斗声音的数据表
	private DataTable unitCombatSoundsTable;

	// 存储杂项数据的数据表
	public DataTable miscData;

	// 存储杂项元素的引用
	private Element misc;

	// 存储键到文本标签配置的映射
	private final Map<String, TextTagConfig> keyToTextTagConfig = new HashMap<>();

	// 存储单位全局字符串的数据表
	private DataTable unitGlobalStrings;

	// 存储UI声音的数据表
	public DataTable uiSoundsTable;

	// 存储闪电效果数据的数据表
	private DataTable lightningDataTable;

	// 存储战争3ID到闪电效果模型的映射
	private Map<War3ID, LightningEffectModel> lightningTypeToModel;

	// 存储确认实例的引用
	private MdxComplexInstance confirmationInstance;

	// 存储DNC单位的实例引用
	public MdxComplexInstance dncUnit;

	// 存储DNC地形的实例引用
	public MdxComplexInstance dncTerrain;

	// 存储DNC目标的实例引用
	public MdxComplexInstance dncTarget;

	// 存储模拟的引用
	public CSimulation simulation;

	// 更新时间
	private float updateTime;


	// for World Editor, I think
	// 定义一个Vector2数组，用于存储游戏中每个玩家的起始位置，数组大小由WarsmashConstants.MAX_PLAYERS常量决定
	public Vector2[] startLocations = new Vector2[WarsmashConstants.MAX_PLAYERS];

	// 创建一个动态阴影管理器实例，用于处理游戏中的动态阴影效果
	private final DynamicShadowManager dynamicShadowManager = new DynamicShadowManager();

	// 创建一个带种子的随机数生成器，种子值为1337L，用于在游戏中生成随机数
	private final Random seededRandom = new Random(1337L);

	// 创建一个HashMap，用于存储文件路径到寻路地图的映射关系
	private final Map<String, BufferedImage> filePathToPathingMap = new HashMap<>();

	// 创建一个ArrayList，用于存储不同大小的选择圈
	private final List<SelectionCircleSize> selectionCircleSizes = new ArrayList<>();

	// 创建一个HashMap，用于存储游戏中的单位到渲染对象的映射关系
	private final Map<CUnit, RenderUnit> unitToRenderPeer = new HashMap<>();
	// 创建一个HashMap，用于存储游戏中的可破坏物到渲染对象的映射关系
	private final Map<CDestructable, RenderDestructable> destructableToRenderPeer = new HashMap<>();
	// 创建一个HashMap，用于存储游戏中的物品到渲染对象的映射关系
	private final Map<CItem, RenderItem> itemToRenderPeer = new HashMap<>();

	// 创建一个HashMap，用于存储游戏单位的ID到类型数据的映射关系
	private final Map<War3ID, RenderUnitTypeData> unitIdToTypeData = new HashMap<>();

	// 定义一个GameUI实例，用于处理游戏的用户界面
	private GameUI gameUI;

	// 定义一个Vector3实例，用于存储游戏中的光照方向
	private Vector3 lightDirection;

	// 四叉树，用于存储可步行对象，以便快速查询和渲染
	private Quadtree<MdxComplexInstance> walkableObjectsTree;

	// 四叉树交叉器，用于找到可步行对象的渲染高度
	private final QuadtreeIntersectorFindsWalkableRenderHeight walkablesIntersector = new QuadtreeIntersectorFindsWalkableRenderHeight();

	// 四叉树交叉器，用于找到可步行对象的碰撞点
	private final QuadtreeIntersectorFindsHitPoint walkablesIntersectionFinder = new QuadtreeIntersectorFindsHitPoint();

	// 四叉树交叉器，用于找到最高的可步行对象
	private final QuadtreeIntersectorFindsHighestWalkable intersectorFindsHighestWalkable = new QuadtreeIntersectorFindsHighestWalkable();

	// 存储UI声音的集合
	private KeyedSounds uiSounds;

	// 本地玩家索引
	private int localPlayerIndex;

	// 命令错误监听器，用于处理命令执行时的错误
	private final SettableCommandErrorListener commandErrorListener;

	// 文本标签列表，用于显示游戏中的文本信息
	public final List<TextTag> textTags = new ArrayList<>();

	// 游戏地图配置
	private final War3MapConfig mapConfig;

	// 游戏回合管理器，用于管理游戏中的回合流程
	private GameTurnManager gameTurnManager;

	// 最近加载的地图信息
	private War3MapW3i lastLoadedMapInformation;

	// 构造函数，初始化 War3MapViewer 对象
	public War3MapViewer(final DataSource dataSource, final CanvasProvider canvas, final War3MapConfig mapConfig,
						 final GameTurnManager gameTurnManager) {
		// 调用父类构造函数，传入数据源和画布提供者
		super(dataSource, canvas);
		// 设置游戏回合管理器
		this.gameTurnManager = gameTurnManager;
		// 设置当前的着色器类型为游戏类型
		MdxHandler.CURRENT_SHADER_TYPE = ShaderEnvironmentType.GAME;
		// 设置游戏数据源
		this.gameDataSource = dataSource;

		// 获取 WebGL 上下文
		final WebGL webGL = this.webGL;

		// 添加 MdxHandler 处理器
		addHandler(new MdxHandler());

		// 设置路径求解器为默认值
		this.wc3PathSolver = PathSolver.DEFAULT;

		// 添加世界场景
		this.worldScene = addWorldScene();

		// 设置动态阴影管理器，如果设置失败则抛出异常
		if (!this.dynamicShadowManager.setup(webGL)) {
			throw new IllegalStateException("FrameBuffer setup failed");
		}

		// 初始化命令错误监听器
		this.commandErrorListener = new SettableCommandErrorListener();
		// 设置地图配置
		this.mapConfig = mapConfig;
	}

	// 加载地图中的各种SLK文件数据
	public void loadSLKs(final WorldEditStrings worldEditStrings) throws IOException {
		// 加载地形艺术SLK文件
		final GenericResource terrain = loadMapGeneric("TerrainArt\\Terrain.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		// 加载悬崖类型SLK文件
		final GenericResource cliffTypes = loadMapGeneric("TerrainArt\\CliffTypes.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		// 加载水域SLK文件
		final GenericResource water = loadMapGeneric("TerrainArt\\Water.slk", FetchDataTypeName.SLK,
				stringDataCallback);

		// 当加载完成后，将数据加载到系统中
		this.terrainData.load(terrain.data.toString());
		this.cliffTypesData.load(cliffTypes.data.toString());
		this.waterData.load(water.data.toString());

		// 加载装饰物SLK文件
		final GenericResource doodads = loadMapGeneric("Doodads\\Doodads.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		// 加载装饰物元数据SLK文件
		final GenericResource doodadMetaData = loadMapGeneric("Doodads\\DoodadMetaData.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		// 加载可破坏物数据SLK文件
		final GenericResource destructableData = loadMapGeneric("Units\\DestructableData.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		// 加载可破坏物元数据SLK文件
		final GenericResource destructableMetaData = loadMapGeneric("Units\\DestructableMetaData.slk",
				FetchDataTypeName.SLK, stringDataCallback);

		// 当加载完成后，将数据加载到系统中，并标记装饰物和可破坏物数据已加载
		this.doodadsAndDestructiblesLoaded = true;
		this.doodadsData.load(doodads.data.toString());
		this.doodadMetaData.load(doodadMetaData.data.toString());
		this.doodadsData.load(destructableData.data.toString()); // 这里应该是this.destructableData.load(destructableData.data.toString());
		this.destructableMetaData.load(destructableMetaData.data.toString()); // 这里修正了变量名

		// 加载单位数据SLK文件
		final GenericResource unitData = loadMapGeneric("Units\\UnitData.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		// 加载单位UI SLK文件
		final GenericResource unitUi = loadMapGeneric("Units\\unitUI.slk", FetchDataTypeName.SLK, stringDataCallback);
		// 加载物品数据SLK文件
		final GenericResource itemData = loadMapGeneric("Units\\ItemData.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		// 加载单位元数据SLK文件
		final GenericResource unitMetaData = loadMapGeneric("Units\\UnitMetaData.slk", FetchDataTypeName.SLK,
				stringDataCallback);

		// 加载闪电数据
		loadLightningData(worldEditStrings);

		// 当加载完成后，将数据加载到系统中，并标记单位和物品数据已加载
		this.unitsAndItemsLoaded = true;
		this.unitsData.load(unitData.data.toString());
		this.unitsData.load(unitUi.data.toString());
		this.unitsData.load(itemData.data.toString());
		this.unitMetaData.load(unitMetaData.data.toString());
		// emit loaded

		// 初始化单位确认声音表
		this.unitAckSoundsTable = new DataTable(worldEditStrings);
		// 读取单位确认声音SLK文件
		try (InputStream terrainSlkStream = this.dataSource.getResourceAsStream("UI\\SoundInfo\\UnitAckSounds.slk")) {
			this.unitAckSoundsTable.readSLK(terrainSlkStream);
		}
		// 初始化单位战斗声音表
		this.unitCombatSoundsTable = new DataTable(worldEditStrings);
		// 读取单位战斗声音SLK文件
		try (InputStream terrainSlkStream = this.dataSource
				.getResourceAsStream("UI\\SoundInfo\\UnitCombatSounds.slk")) {
			this.unitCombatSoundsTable.readSLK(terrainSlkStream);
		}
		// 初始化miscData对象，用于存储从不同文件读取的数据
		this.miscData = new DataTable(worldEditStrings);

		// 尝试读取"UI\\MiscData.txt"文件中的数据到miscData对象
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\MiscData.txt")) {
			this.miscData.readTXT(miscDataTxtStream, true);
		}

		// 尝试读取"Units\\MiscData.txt"文件中的数据到miscData对象
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("Units\\MiscData.txt")) {
			this.miscData.readTXT(miscDataTxtStream, true);
		}

		// 尝试读取"Units\\MiscGame.txt"文件中的数据到miscData对象
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("Units\\MiscGame.txt")) {
			this.miscData.readTXT(miscDataTxtStream, true);
		}

		// 尝试读取"UI\\MiscUI.txt"文件中的数据到miscData对象
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\MiscUI.txt")) {
			this.miscData.readTXT(miscDataTxtStream, true);
		}

		// 尝试读取"UI\\SoundInfo\\MiscData.txt"文件中的数据到miscData对象
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\SoundInfo\\MiscData.txt")) {
			this.miscData.readTXT(miscDataTxtStream, true);
		}

		// 如果存在"war3mapMisc.txt"文件，则尝试读取该文件中的数据到miscData对象
		if (this.dataSource.has("war3mapMisc.txt")) {
			try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("war3mapMisc.txt")) {
				this.miscData.readTXT(miscDataTxtStream, true);
			}
		}

		// 从miscData对象中获取Misc字段的数据
		this.misc = this.miscData.get("Misc");

		// TODO: 在资产文件中查找维护费用常量
		// 如果misc对象中没有UpkeepUsage字段，则设置默认值
		if (!this.misc.hasField("UpkeepUsage")) {
			this.misc.setField("UpkeepUsage", "50,80,10000,10000,10000,10000,10000,10000,10000,10000");
		}

		// 如果misc对象中没有UpkeepGoldTax字段，则设置默认值
		if (!this.misc.hasField("UpkeepGoldTax")) {
			this.misc.setField("UpkeepGoldTax", "0.00,0.30,0.60,0.60,0.60,0.60,0.60,0.60,0.60,0.60");
		}

		// 如果misc对象中没有UpkeepLumberTax字段，则设置默认值
		if (!this.misc.hasField("UpkeepLumberTax")) {
			this.misc.setField("UpkeepLumberTax", "0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00");
		}
		// 获取miscData中的"Light"元素
		final Element light = this.miscData.get("Light");
		// 从"Light"元素中获取"Direction"字段的X分量
		final float lightX = light.getFieldFloatValue("Direction", 0);
		// 从"Light"元素中获取"Direction"字段的Y分量
		final float lightY = light.getFieldFloatValue("Direction", 1);
		// 从"Light"元素中获取"Direction"字段的Z分量
		final float lightZ = light.getFieldFloatValue("Direction", 2);
		// 创建一个Vector3对象表示光照方向，并归一化
		this.lightDirection = new Vector3(lightX, lightY, lightZ).nor();

		// 初始化unitGlobalStrings数据表
		this.unitGlobalStrings = new DataTable(worldEditStrings);
		// 尝试打开"Units\\UnitGlobalStrings.txt"文件流
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("Units\\UnitGlobalStrings.txt")) {
			// 读取并解析文件内容到unitGlobalStrings数据表
			this.unitGlobalStrings.readTXT(miscDataTxtStream, true);
		}

		// 获取unitGlobalStrings中的"Categories"元素
		final Element categories = this.unitGlobalStrings.get("Categories");
		// 遍历所有的单位分类
		for (final CUnitClassification unitClassification : CUnitClassification.values()) {
			// 如果单位分类有本地化键
			if (unitClassification.getLocaleKey() != null) {
				// 获取对应的显示名称并设置到单位分类中
				final String displayName = categories.getField(unitClassification.getLocaleKey());
				unitClassification.setDisplayName(displayName);
			}
		}

		// 清空选择圈大小列表
		this.selectionCircleSizes.clear();
		// 获取miscData中的"SelectionCircle"元素
		final Element selectionCircleData = this.miscData.get("SelectionCircle");
		// 获取选择圈的大小数量
		final int selectionCircleNumSizes = selectionCircleData.getFieldValue("NumSizes");
		// 遍历所有的选择圈大小
		for (int i = 0; i < selectionCircleNumSizes; i++) {
			// 构造索引字符串
			final String indexString = i < 10 ? "0" + i : Integer.toString(i);
			// 获取对应大小的选择圈的尺寸
			final float size = selectionCircleData.getFieldFloatValue("Size" + indexString);
			// 获取对应大小的选择圈的纹理
			final String texture = selectionCircleData.getField("Texture" + indexString);
			// 获取对应大小的选择圈的点状纹理
			final String textureDotted = selectionCircleData.getField("TextureDotted" + indexString);
			// 将新的选择圈大小添加到列表中
			this.selectionCircleSizes.add(new SelectionCircleSize(size, texture, textureDotted));
		}

		// 获取选择圈的缩放因子
		this.selectionCircleScaleFactor = selectionCircleData.getFieldFloatValue("ScaleFactor");
		// 获取可行走表面的Z偏移量
		this.imageWalkableZOffset = selectionCircleData.getFieldValue("ImageWalkableZOffset");
		// 解析并获取友方选择圈颜色
		this.selectionCircleColorFriend = parseColor(selectionCircleData, "ColorFriend");
		// 解析并获取中立选择圈颜色
		this.selectionCircleColorNeutral = parseColor(selectionCircleData, "ColorNeutral");
		// 解析并获取敌方选择圈颜色
		this.selectionCircleColorEnemy = parseColor(selectionCircleData, "ColorEnemy");

		// 初始化uiSoundsTable数据表
		this.uiSoundsTable = new DataTable(worldEditStrings);
		// 尝试打开并读取"UI\\SoundInfo\\UISounds.slk"文件到uiSoundsTable数据表
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\SoundInfo\\UISounds.slk")) {
			this.uiSoundsTable.readSLK(miscDataTxtStream);
		}
		// 尝试打开并读取"UI\\SoundInfo\\AmbienceSounds.slk"文件到uiSoundsTable数据表
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\SoundInfo\\AmbienceSounds.slk")) {
			this.uiSoundsTable.readSLK(miscDataTxtStream);
		}
		// 尝试打开并读取"UI\\SoundInfo\\AbilitySounds.slk"文件到uiSoundsTable数据表
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\SoundInfo\\AbilitySounds.slk")) {
			this.uiSoundsTable.readSLK(miscDataTxtStream);
		}

	}

	// 定义一个方法，用于获取与特定键关联的文本标签配置
	private TextTagConfig getTextTagConfig(final String key) {
		// 尝试从映射中获取与键关联的文本标签配置
		TextTagConfig textTagConfig = this.keyToTextTagConfig.get(key);
		// 如果映射中没有找到对应的配置
		if (textTagConfig == null) {
			// 解析新的文本标签配置
			textTagConfig = parseTextTagConfig(this.misc, key);
			// 将新的配置添加到映射中，以便下次可以直接获取
			this.keyToTextTagConfig.put(key, textTagConfig);
		}
		// 返回文本标签配置
		return textTagConfig;
	}

	// 定义一个方法用于解析文本标签配置
	private static TextTagConfig parseTextTagConfig(final Element misc, final String name) {
		// 解析文本颜色，通过拼接字符串name和"TextColor"作为键值从misc元素中获取颜色
		final Color color = parseColor(misc, name + "TextColor");

		// 定义文本速度的键值为name + "TextVelocity"
		final String velocityKey = name + "TextVelocity";

		// 从misc元素中获取文本速度的三个分量值，如果某个分量不存在则默认为0
		final float[] velocity = {
				misc.getFieldFloatValue(velocityKey, 0),
				misc.getFieldFloatValue(velocityKey, 1),
				misc.getFieldFloatValue(velocityKey, 2)
		};

		// 获取文本的生命周期，通过拼接字符串name和"TextLifetime"作为键值从misc元素中获取
		final float lifetime = misc.getFieldFloatValue(name + "TextLifetime");

		// 获取文本淡出的起始时间，通过拼接字符串name和"TextFadeStart"作为键值从misc元素中获取
		final float fadeStart = misc.getFieldFloatValue(name + "TextFadeStart");

		// 返回一个新的TextTagConfig对象，包含了解析出来的颜色、速度、生命周期和淡出起始时间
		return new TextTagConfig(color, velocity, lifetime, fadeStart);
	}

	// 加载闪电效果数据的方法
	private void loadLightningData(final WorldEditStrings worldEditStrings) throws IOException {
		// 初始化闪电数据表
		this.lightningDataTable = new DataTable(worldEditStrings);
		// 尝试打开并读取闪电数据SLK文件
		try (InputStream slkStream = this.dataSource.getResourceAsStream("Splats\\LightningData.slk")) {
			this.lightningDataTable.readSLK(slkStream);
		}
		// 初始化闪电类型到模型的映射
		this.lightningTypeToModel = new HashMap<>();
		// 创建闪电效果模型处理器
		final LightningEffectModelHandler lightningEffectModelHandler = new LightningEffectModelHandler();
		// 加载闪电效果模型处理器
		lightningEffectModelHandler.load(this);
		// 遍历闪电数据表中的所有键
		for (final String key : this.lightningDataTable.keySet()) {
			// 从键中解析出War3ID类型的ID
			final War3ID typeId = War3ID.fromString(key);
			// 获取对应键的元素
			final Element element = this.lightningDataTable.get(key);
			// 构造纹理文件路径
			final String textureFilePath = element.getField("Dir") + "\\" + element.getField("file");
			// 获取元素中的各种属性值
			final float avgSegLen = element.getFieldFloatValue("AvgSegLen");
			final float width = element.getFieldFloatValue("Width");
			final float r = element.getFieldFloatValue("R");
			final float g = element.getFieldFloatValue("G");
			final float b = element.getFieldFloatValue("B");
			final float a = element.getFieldFloatValue("A");
			final float noiseScale = element.getFieldFloatValue("NoiseScale");
			final float texCoordScale = element.getFieldFloatValue("TexCoordScale");
			final float duration = element.getFieldFloatValue("Duration");
			final int version = element.getFieldValue("version");
			// 创建闪电效果模型实例
			final LightningEffectModel lightningEffectModel = new LightningEffectModel(lightningEffectModelHandler,
					this, ".lightning", this.mapPathSolver, "<lightning:" + key + ">", typeId, textureFilePath,
					avgSegLen, width, new float[]{r / 255f, g / 255f, b / 255f, a / 255f}, noiseScale, texCoordScale,
					duration, version);
			// 加载数据到闪电效果模型
			lightningEffectModel.loadData(null, null);
			// 将闪电效果模型添加到映射中
			this.lightningTypeToModel.put(typeId, lightningEffectModel);
		}
	}


	// 解析颜色
	private static Color parseColor(final Element selectionCircleData, final String field) {
		return new Color(selectionCircleData.getFieldFloatValue(field, 1) / 255f,
				selectionCircleData.getFieldFloatValue(field, 2) / 255f,
				selectionCircleData.getFieldFloatValue(field, 3) / 255f,
				selectionCircleData.getFieldFloatValue(field, 0) / 255f);
	}

	// 加载文件资源的方法
	public GenericResource loadMapGeneric(final String path, final FetchDataTypeName dataType,
			final LoadGenericCallback callback) {
		if (this.mapMpq == null) {
			return this.loadGeneric(path, dataType, callback);
		}
		else {
			return this.loadGeneric(path, dataType, callback, this.dataSource);
		}
	}

	// 地图文件
	public static War3Map beginLoadingMap(final DataSource gameDataSource, final String mapFilePath)
			throws IOException {
		if (mapFilePath.startsWith("/") || !gameDataSource.has(mapFilePath)) {
			final File mapFile = new File(mapFilePath);
			if (mapFile.exists()) {
				return new War3Map(gameDataSource, mapFile);
			}
			else {
				throw new IllegalArgumentException("No such map file: " + mapFilePath);
			}
		}
		return new War3Map(gameDataSource, mapFilePath);
	}

	// 地图文件数据表
	public DataTable loadWorldEditData(final War3Map map) {
		final StandardObjectData standardObjectData = new StandardObjectData(map);
		this.worldEditData = standardObjectData.getWorldEditData();
		return this.worldEditData;
	}

	public WTS preloadWTS(final War3Map map) {
		try {
			this.preloadedWTS = Warcraft3MapObjectData.loadWTS(map);
			return this.preloadedWTS;
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	// 创建地图加载器
	public MapLoader createMapLoader(final War3Map war3Map, final War3MapW3i w3iFile, final int localPlayerIndex)
			throws IOException {
		this.localPlayerIndex = localPlayerIndex;
		this.mapMpq = war3Map;
		this.lastLoadedMapInformation = w3iFile;

		return new MapLoader(war3Map, w3iFile, localPlayerIndex);
	}

	// 定义一个方法用于创建闪电效果
	// 参数包括闪电的唯一标识符，起始渲染单元，目标渲染组件
	// 此方法重载了另一个方法，调用了同一个类中的createLightning方法，并传入了额外的null参数
	public SimulationRenderComponentLightning createLightning(final War3ID lightningId,
		 final RenderUnit renderPeerSource, final RenderWidget renderPeerTarget) {
	 // 调用本类中的createLightning方法，传入lightningId, renderPeerSource, renderPeerTarget以及一个null值
	 return this.createLightning(lightningId, renderPeerSource, renderPeerTarget, null);
	}

	// 创建闪电效果的渲染组件
	public SimulationRenderComponentLightning createLightning(final War3ID lightningId,
															  final RenderUnit renderPeerSource, final RenderWidget renderPeerTarget, final Float duration) {
		// 根据提供的闪电ID获取对应的闪电效果模型
		final LightningEffectModel lightningEffectModel = this.lightningTypeToModel.get(lightningId);
		if (lightningEffectModel != null) {
			// 为闪电效果模型添加两个实例，分别代表闪电的起点和终点
			final LightningEffectNode source = (LightningEffectNode) lightningEffectModel.addInstance();
			final LightningEffectNode target = (LightningEffectNode) lightningEffectModel.addInstance();

			// 设置闪电的持续时间
			// 自动游戏内闪电会在下面应用它们内置的持续时间。对于用户代码，我们不在其他任意的（非单位绑定的）createLightning函数中这样做
			// 后续我们可能会想要一个用户API来创建附加到单位的闪电，其持续时间由用户代码指定
			if (duration != null) {
				// 如果用户指定了持续时间，并且持续时间大于等于0，则使用用户指定的持续时间
				if (duration >= 0) {
					source.setLifeSpanRemaining(duration);
					target.setLifeSpanRemaining(duration);
				}
			} else {
				// 如果用户没有指定持续时间，则使用闪电效果模型默认的持续时间
				source.setLifeSpanRemaining(lightningEffectModel.getDuration());
				target.setLifeSpanRemaining(lightningEffectModel.getDuration());
			}

			// 设置起点和终点的关系
			source.setFriend(target);
			target.setFriend(source);
			source.setSource(true); // 标记source为起点

			// 设置起点和终点的父对象和位置
			source.setParent(renderPeerSource.getInstance());
			source.setLocation(0, 0, this.simulation.getUnitData()
					.getProjectileLaunchZ(renderPeerSource.getSimulationUnit().getTypeId()));
			target.setParent(renderPeerTarget.getInstance());
			target.setLocation(0, 0, renderPeerTarget.getSimulationWidget().getImpactZ());

			// 将起点和终点添加到世界场景中
			source.setScene(this.worldScene);
			target.setScene(this.worldScene);

			// 返回一个新的闪电效果渲染组件
			return new RenderLightningEffect(source, target, this);
		}
		// 如果没有找到对应的闪电效果模型，返回一个不做任何事情的默认组件
		return SimulationRenderComponentLightning.DO_NOTHING;
	}

	// 创建一个可移动的闪电效果渲染组件
	public SimulationRenderComponentLightningMovable createLightning(final War3ID lightningId, final float x1,
																	 final float y1, final float z1, final float x2, final float y2, final float z2) {
		// 根据传入的闪电ID获取对应的闪电效果模型
		final LightningEffectModel lightningEffectModel = this.lightningTypeToModel.get(lightningId);
		// 如果模型存在
		if (lightningEffectModel != null) {
			// 添加闪电效果模型的实例作为源点
			final LightningEffectNode source = (LightningEffectNode) lightningEffectModel.addInstance();
			// 添加闪电效果模型的实例作为目标点
			final LightningEffectNode target = (LightningEffectNode) lightningEffectModel.addInstance();
			// 设置源点和目标点为友好的，即它们属于同一闪电效果
			source.setFriend(target);
			target.setFriend(source);
			// 标记源点为闪电的起始点
			source.setSource(true);
			// 设置源点的位置
			source.setLocation(x1, y1, z1);
			// 设置目标点的位置
			target.setLocation(x2, y2, z2);
			// 将源点和目标点添加到世界场景中
			source.setScene(this.worldScene);
			target.setScene(this.worldScene);
			// 返回一个新的闪电效果渲染组件，包含源点和目标点
			return new RenderLightningEffect(source, target, this);
		}
		// 如果模型不存在，返回一个不做任何操作的默认组件
		return SimulationRenderComponentLightningMovable.DO_NOTHING;
	}

	// 定义一个方法，用于在原点生成特效
	public void spawnFxOnOrigin(final RenderUnit renderUnit, final String heroLevelUpArt) {
		// 加载MDX模型，模型文件由heroLevelUpArt指定
		final MdxModel heroLevelUpModel = loadModelMdx(heroLevelUpArt);
		// 如果模型加载成功
		if (heroLevelUpModel != null) {
			// 创建模型的一个实例
			final MdxComplexInstance modelInstance = (MdxComplexInstance) heroLevelUpModel.addInstance();
			// 设置实例的队伍颜色，根据renderUnit的玩家索引
			modelInstance.setTeamColor(renderUnit.playerIndex);

			// 获取renderUnit的模型
			final MdxModel model = (MdxModel) renderUnit.instance.model;
			// 初始化附件索引为-1
			int index = -1;
			// 遍历模型的所有附件
			for (int i = 0; i < model.attachments.size(); i++) {
				// 获取当前附件
				final Attachment attachment = model.attachments.get(i);
				// 如果附件名称以"origin ref"开头，则记录索引并退出循环
				if (attachment.getName().startsWith("origin ref")) {
					index = i;
					break;
				}
			}
			// 如果找到了符合条件的附件，并且条件为真（此处为false，所以不会执行）
			if ((index != -1) && false) {
				// 获取附件节点
				final MdxNode attachment = renderUnit.instance.getAttachment(index);
				// 将模型实例的父节点设置为附件节点
				modelInstance.setParent(attachment);
			}
			// 如果没有找到符合条件的附件或条件为假
			else {
				// 直接设置模型实例的位置为renderUnit的位置
				modelInstance.setLocation(renderUnit.location);
			}

			// 将模型实例添加到世界场景中
			modelInstance.setScene(War3MapViewer.this.worldScene);
			// 随机生成出生序列
			SequenceUtils.randomBirthSequence(modelInstance);
			// 创建一个即时攻击渲染对象，并添加到projectiles列表中
			War3MapViewer.this.projectiles.add(new RenderAttackInstant(modelInstance, War3MapViewer.this,
					(float) Math.toRadians(renderUnit.getSimulationUnit().getFacing())));
		}
	}

	// 获取可破坏地形路径像素图的方法
	// 参数: row - 游戏对象，包含了地形的相关信息
	protected BufferedImage getDestructablePathingPixelMap(final GameObject row) {
		// 从游戏对象中获取路径纹理字段的值，并加载对应的纹理
		return loadPathingTexture(row.getFieldAsString(DESTRUCTABLE_PATHING, 0));
	}

	// 获取可破坏地形死亡路径像素图的方法
	// 参数: row - 游戏对象，包含了地形的相关信息
	protected BufferedImage getDestructablePathingDeathPixelMap(final GameObject row) {
		// 从游戏对象中获取死亡路径纹理字段的值，并加载对应的纹理
		return loadPathingTexture(row.getFieldAsString(DESTRUCTABLE_PATHING_DEATH, 0));
	}

	// 定义一个私有方法用于加载声音资源
	private void loadSounds() {
		// 创建一个新的KeyedSounds对象，用于管理用户界面(UI)的声音
		// 参数this.uiSoundsTable是声音表的引用，用于查找声音文件
		// 参数this.mapMpq是MPQ文件系统的引用，MPQ是一种用于存储游戏资源的压缩文件格式
		this.uiSounds = new KeyedSounds(this.uiSoundsTable, this.mapMpq);
	}


	/**
	 * Loads the map information that should be loaded after UI, such as units, who
	 * need to be able to setup their UI counterparts (icons, etc) for their
	 * abilities while loading. This allows the dynamic creation of units while the
	 * game is playing to better share code with the startup sequence's creation of
	 * units.
	 * 加载UI后应加载的地图信息，如单位、谁
	 * *需要能够为其设置UI对应项（图标等）
	 * *加载时的能力。这允许动态创建单位，同时
	 * *玩游戏是为了更好地与启动序列的创建共享代码
	 * *单位。
	 * @throws IOException
	 */
	public void loadAfterUI() throws IOException {
		// 检查单位和个人物品是否已经加载
		if (this.unitsAndItemsLoaded) {
			// 如果已经加载，则调用方法加载单位和物品数据
			loadUnitsAndItems(this.allObjectData, this.lastLoadedMapInformation);
		} else {
			// 如果未加载，则抛出异常，表示JS转录尚未加载地图且没有JS异步承诺
			throw new IllegalStateException("transcription of JS has not loaded a map and has no JS async promises");
		}

		// 在加载单位完成后，需要更新并创建所有单位阴影的存储阴影信息
		this.terrain.initShadows();
		// 设置战争迷雾数据
		this.terrain.setFogOfWarData(this.simulation.getPlayer(this.localPlayerIndex).getFogOfWar());

		// 创建一个定时器用于更新战争迷雾
		final CTimer fogUpdateTimer = new CTimer() {
			@Override
			public void onFire(final CSimulation simulation) {
				// 定时器触发时的操作（当前为空，可能需要实现具体逻辑）
			}
		};
		// 设置定时器超时时间为1秒
		fogUpdateTimer.setTimeoutTime(1.0f);
		// 设置定时器重复执行
		fogUpdateTimer.setRepeats(true);
		// 启动定时器
		fogUpdateTimer.start(this.simulation);

		// 创建一个定时器用于更新GPU上的战争迷雾数据
		final CTimer fogGpuUpdateTimer = new CTimer() {
			@Override
			public void onFire(final CSimulation simulation) {
				// 更新地形上的战争迷雾数据到GPU
				War3MapViewer.this.terrain.reloadFogOfWarDataToGPU();
				// 更新所有渲染装饰物的战争迷雾
				for (final RenderDoodad doodad : War3MapViewer.this.decals) {
					doodad.updateFog(War3MapViewer.this);
				}
			}
		};
		// 设置定时器超时时间为0.03秒
		fogGpuUpdateTimer.setTimeoutTime(0.03f);
		// 设置定时器重复执行
		fogGpuUpdateTimer.setRepeats(true);
		// 启动定时器
		fogGpuUpdateTimer.start(this.simulation);
	}

	// 加载地图中的装饰物和可破坏物
	private void loadDoodadsAndDestructibles(final Warcraft3MapRuntimeObjectData modifications,
											 final War3MapW3i w3iFile) throws IOException {
		// 应用装饰物的修改文件
		applyModificationFile(this.doodadsData, this.doodadMetaData, modifications.getDoodads(),
				WorldEditorDataType.DOODADS);
		// 应用可破坏物的修改文件
		applyModificationFile(this.doodadsData, this.destructableMetaData, modifications.getDestructibles(),
				WorldEditorDataType.DESTRUCTIBLES);

		// 从W3I文件中读取装饰物数据
		final War3MapDoo doo = this.mapMpq.readDoodads(w3iFile);

		// 遍历所有装饰物
		for (final com.etheller.warsmash.parsers.w3x.doo.Doodad doodad : doo.getDoodads()) {
			// 如果装饰物标志位不为2，则跳过
			if ((doodad.getFlags() & 0x2) == 0) {
				continue;
			}
			// 获取装饰物的ID、变体、位置、朝向、生命值和缩放比例
			final War3ID doodadId = doodad.getId();
			final int doodadVariation = doodad.getVariation();
			final float[] location = doodad.getLocation();
			final float facingRadians = doodad.getAngle();
			final short lifePercent = doodad.getLife();
			final float[] scale = doodad.getScale();
			// 创建装饰物或可破坏物
			createDestructableOrDoodad(doodadId, modifications, doodadVariation, location, facingRadians, lifePercent,
					scale);
		}

		// 处理地形装饰物（Cliff/Terrain doodads）
		for (final com.etheller.warsmash.parsers.w3x.doo.TerrainDoodad doodad : doo.getTerrainDoodads()) {
			// 获取装饰物的数据行
			final GameObject row = modifications.getDoodads().get(doodad.getId());
			// 读取模型文件名
			String file = row.readSLKTag("file");
			// 如果文件名为空，则尝试再次读取
			if ("".equals(file)) {
				file = row.readSLKTag("file");
			}
			// 确保文件名以.mdx结尾
			if (file.toLowerCase().endsWith(".mdl")) {
				file = file.substring(0, file.length() - 4);
			}
			if (!file.toLowerCase().endsWith(".mdx")) {
				file += ".mdx";
			}
			// 加载模型
			final MdxModel model = (MdxModel) load(file, this.mapPathSolver, this.solverParams);

			// 读取地形装饰物的纹理
			final String pathingTexture = row.readSLKTag("pathTex");
			BufferedImage pathingTextureImage = null;
			if ((pathingTexture != null) && (pathingTexture.length() > 0) && !"_".equals(pathingTexture)) {
				// 尝试从缓存中获取纹理图像
				pathingTextureImage = this.filePathToPathingMap.get(pathingTexture.toLowerCase());
				if (pathingTextureImage == null) {
					// 如果缓存中没有，则尝试从MPQ文件中读取
					if (this.mapMpq.has(pathingTexture)) {
						try {
							pathingTextureImage = TgaFile.readTGA(pathingTexture,
									this.mapMpq.getResourceAsStream(pathingTexture));
							this.filePathToPathingMap.put(pathingTexture.toLowerCase(), pathingTextureImage);
						} catch (final Exception exc) {
							exc.printStackTrace();
						}
					}
				}
			}

			// 如果纹理图像存在，则处理地形单元格
			if (pathingTextureImage != null) {
				final int textureWidth = pathingTextureImage.getWidth();
				final int textureHeight = pathingTextureImage.getHeight();
				final int textureWidthTerrainCells = textureWidth / 4;
				final int textureHeightTerrainCells = textureHeight / 4;
				final int minCellX = (int) doodad.getLocation()[0];
				final int minCellY = (int) doodad.getLocation()[1];
				final int maxCellX = (minCellX + textureWidthTerrainCells) - 1;
				final int maxCellY = (minCellY + textureHeightTerrainCells) - 1;
				// 移除地形装饰物下的地形单元格
				for (int j = minCellY; j <= maxCellY; j++) {
					for (int i = minCellX; i <= maxCellX; i++) {
						this.terrain.removeTerrainCellWithoutFlush(i, j);
					}
				}
				this.terrain.flushRemovedTerrainCells();
			}

			// 输出加载信息
			System.out.println("Loading terrain doodad: " + file);
			// 将地形装饰物添加到列表中
			this.terrainDoodads.add(new TerrainDoodad(this, model, row, doodad, pathingTextureImage));
		}

		// 标记装饰物已准备好
		this.doodadsReady = true;
		// 标记至少有一个资源已准备好
		this.anyReady = true;
	}

	// 定义一个方法用于创建游戏中的装饰物（Doodad）
	private void createDoodad(final GameObject row, final int doodadVariation, final float[] location,
							  final float facingRadians, final float[] scale) {
		// 获取装饰物模型，根据装饰物变体和行数据
		final MdxModel model = getDoodadModel(doodadVariation, row);
		// 读取装饰物的最大俯仰角（maxPitch）值
		final float maxPitch = row.readSLKTagFloat("maxPitch");
		// 读取装饰物的最大翻滚角（maxRoll）值
		final float maxRoll = row.readSLKTagFloat("maxRoll");
		// 读取装饰物的默认缩放比例（defScale）值
		final float defScale = row.readSLKTagFloat("defScale");
		// 创建一个新的装饰物渲染对象
		final RenderDoodad renderDoodad = new RenderDoodad(this, model, row, location, scale, facingRadians, maxPitch,
				maxRoll, defScale, doodadVariation);
		// 设置装饰物渲染对象的均匀缩放比例为默认缩放比例
		renderDoodad.instance.uniformScale(defScale);
		// 将装饰物渲染对象添加到装饰物列表中
		this.doodads.add(renderDoodad);
		// 将装饰物渲染对象添加到贴花（Decals）列表中，可能是为了处理渲染顺序或遮挡关系
		this.decals.add(renderDoodad);
	}

	// 创建一个新的可破坏物体实例
	private RenderDestructable createNewDestructable(final War3ID doodadId, final GameObject row, final int doodadVariation, final float[] location, final float facingRadians, final short lifePercent, final float[] scale) {
		BuildingShadow destructableShadow = null; // 可破坏物体的阴影
		RemovablePathingMapInstance destructablePathing = null; // 可破坏物体的路径规划图层
		RemovablePathingMapInstance destructablePathingDeath = null; // 可破坏物体死亡时的路径规划图层
		final MdxModel model = getDoodadModel(doodadVariation, row); // 获取模型

		// 读取模型的最大俯仰角和最大翻滚角
		final float maxPitch = row.readSLKTagFloat("maxPitch");
		final float maxRoll = row.readSLKTagFloat("maxRoll");
		final String shadowString = row.readSLKTag("shadow"); // 读取阴影信息
		// 如果有有效的阴影信息，则添加阴影
		if ((shadowString != null) && (shadowString.length() > 0) && !"_".equals(shadowString)) {
			destructableShadow = this.terrain.addShadow(shadowString, location[0], location[1]);
		}

		// 获取可破坏物体的路径规划像素图
		final BufferedImage destructablePathingPixelMap = getDestructablePathingPixelMap(row);
		// 如果有路径规划像素图，则创建路径规划图层
		if (destructablePathingPixelMap != null) {
			destructablePathing = this.terrain.pathingGrid.createRemovablePathingOverlayTexture(location[0], location[1], (int) Math.toDegrees(facingRadians), destructablePathingPixelMap);
			// 如果生命值大于0，则添加路径规划图层
			if (lifePercent > 0) {
				destructablePathing.add();
			}
		}
		// 获取可破坏物体死亡时的路径规划像素图
		final BufferedImage destructablePathingDeathPixelMap = getDestructablePathingDeathPixelMap(row);
		// 如果有死亡时的路径规划像素图，则创建路径规划图层
		if (destructablePathingDeathPixelMap != null) {
			destructablePathingDeath = this.terrain.pathingGrid.createRemovablePathingOverlayTexture(location[0], location[1], (int) Math.toDegrees(facingRadians), destructablePathingDeathPixelMap);
			// 如果生命值小于等于0，则添加路径规划图层
			if (lifePercent <= 0) {
				destructablePathingDeath.add();
			}
		}
		// 设置位置
		final float x = location[0];
		final float y = location[1];
		// 创建模拟可破坏物体
		final CDestructable simulationDestructable = this.simulation.internalCreateDestructable(War3ID.fromString(row.getId()), x, y, destructablePathing, destructablePathingDeath);
		final float selectionScale = 1.0f; // 选择时的缩放比例
		// 设置生命值
		simulationDestructable.setLife(this.simulation, simulationDestructable.getLife() * (lifePercent / 100f));
		// 创建渲染用的可破坏物体
		final RenderDestructable renderDestructable = new RenderDestructable(this, model, row, location, scale, facingRadians, selectionScale, maxPitch, maxRoll, lifePercent, destructableShadow, simulationDestructable, doodadVariation);
		// 如果物体可行走，则添加到可行走物体树中
		if (row.readSLKTagBoolean("walkable")) {
			final float angle = facingRadians;
			BoundingBox boundingBox = model.bounds.getBoundingBox();
			// 如果没有边界框，则创建一个默认的边界框
			if (boundingBox == null) {
				boundingBox = new BoundingBox(new Vector3(-10, -10, 0), new Vector3(10, 10, 0));
			}
			// 获取旋转后的边界框
			final Rectangle renderDestructableBounds = getRotatedBoundingBox(x, y, angle, boundingBox);
			// 添加到可行走物体树中
			this.walkableObjectsTree.add((MdxComplexInstance) renderDestructable.instance, renderDestructableBounds);
			renderDestructable.walkableBounds = renderDestructableBounds;
		}
		// 添加到小部件和贴花列表中
		this.widgets.add(renderDestructable);
		this.decals.add(renderDestructable);
		// 将模拟可破坏物体和渲染用的可破坏物体关联起来
		this.destructableToRenderPeer.put(simulationDestructable, renderDestructable);
		return renderDestructable; // 返回创建的可破坏物体实例
	}


	// 定义一个方法，用于创建可破坏物或装饰物
	private void createDestructableOrDoodad(final War3ID doodadId, // 装饰物的唯一标识符
											final Warcraft3MapRuntimeObjectData modifications, // 地图对象的修改数据
											final int doodadVariation, // 装饰物的变体
											final float[] location, // 装饰物的位置
											final float facingRadians, // 装饰物的朝向（以弧度为单位）
											final short lifePercent, // 装饰物的生命值百分比
											final float[] scale) { // 装饰物的缩放比例

		// 尝试从装饰物列表中获取对象
		GameObject row = modifications.getDoodads().get(doodadId);
		if (row == null) { // 如果装饰物列表中没有找到对象
			// 尝试从可破坏物列表中获取对象
			row = modifications.getDestructibles().get(doodadId);
			if (row != null) { // 如果可破坏物列表中找到了对象
				// 创建新的可破坏物
				createNewDestructable(doodadId, row, doodadVariation, location, facingRadians, lifePercent, scale);
			}
		} else { // 如果装饰物列表中找到了对象
			// 创建装饰物
			createDoodad(row, doodadVariation, location, facingRadians, scale);
		}
	}


	private MdxModel getDoodadModel(final int doodadVariation, final GameObject row) {
		String file = row.readSLKTag("file");
		final int numVar = row.readSLKTagInt("numVar");

		if (file.endsWith(".mdx") || file.endsWith(".mdl")) {
			file = file.substring(0, file.length() - 4);
		}

		String fileVar = file;

		file += ".mdx";

		if (numVar > 1) {
			fileVar += Math.min(doodadVariation, numVar - 1);
		}

		fileVar += ".mdx";
		// First see if the model is local.
		// Doodads referring to local models may have invalid variations, so if the
		// variation doesn't exist, try without a variation.

		String path;
		if (this.mapMpq.has(fileVar)) {
			path = fileVar;
		}
		else {
			path = file;
		}
		MdxModel model;
		if (this.mapMpq.has(path)) {
			model = (MdxModel) load(path, this.mapPathSolver, this.solverParams);
		}
		else {
			model = (MdxModel) load(fileVar, this.mapPathSolver, this.solverParams);
		}
		return model;
	}

	// 定义一个方法，用于计算旋转后的边界框（BoundingBox）
	private Rectangle getRotatedBoundingBox(final float x, final float y, final float angle,
			final BoundingBox boundingBox) {
		// 获取原始边界框的四个角的坐标
		final float x1 = boundingBox.min.x;
		final float y1 = boundingBox.min.y;
		final float x2 = boundingBox.min.x + boundingBox.getWidth();
		final float y2 = boundingBox.min.y;
		final float x3 = boundingBox.min.x + boundingBox.getWidth();
		final float y3 = boundingBox.min.y + boundingBox.getHeight();
		final float x4 = boundingBox.min.x;
		final float y4 = boundingBox.min.y + boundingBox.getHeight();
		// 计算每个角旋转后的角度和距离
		final float angle1 = (float) StrictMath.atan2(y1, x1) + angle;
		final float len1 = (float) StrictMath.sqrt((x1 * x1) + (y1 * y1));
		final float angle2 = (float) StrictMath.atan2(y2, x2) + angle;
		final float len2 = (float) StrictMath.sqrt((x2 * x2) + (y2 * y2));
		final float angle3 = (float) StrictMath.atan2(y3, x3) + angle;
		final float len3 = (float) StrictMath.sqrt((x3 * x3) + (y3 * y3));
		final float angle4 = (float) StrictMath.atan2(y4, x4) + angle;
		final float len4 = (float) StrictMath.sqrt((x4 * x4) + (y4 * y4));
		// 计算旋转后每个角的新坐标
		final double x1prime = StrictMath.cos(angle1) * len1;
		final double x2prime = StrictMath.cos(angle2) * len2;
		final double x3prime = StrictMath.cos(angle3) * len3;
		final double x4prime = StrictMath.cos(angle4) * len4;
		final double y1prime = StrictMath.sin(angle1) * len1;
		final double y2prime = StrictMath.sin(angle2) * len2;
		final double y3prime = StrictMath.sin(angle3) * len3;
		final double y4prime = StrictMath.sin(angle4) * len4;
		// 找出旋转后四个角中的最小和最大x、y坐标
		final float minX = (float) StrictMath.min(StrictMath.min(x1prime, x2prime), StrictMath.min(x3prime, x4prime));
		final float minY = (float) StrictMath.min(StrictMath.min(y1prime, y2prime), StrictMath.min(y3prime, y4prime));
		final float maxX = (float) StrictMath.max(StrictMath.max(x1prime, x2prime), StrictMath.max(x3prime, x4prime));
		final float maxY = (float) StrictMath.max(StrictMath.max(y1prime, y2prime), StrictMath.max(y3prime, y4prime));
		// 返回一个新的矩形（Rectangle），表示旋转后的边界框
		return new Rectangle(x + minX, y + minY, maxX - minX, maxY - minY);
	}

	private void applyModificationFile(final MappedData doodadsData2, final MappedData doodadMetaData2,
			final ObjectData destructibles, final WorldEditorDataType dataType) {
		// TODO condense ported MappedData from Ghostwolf and MutableObjectData from
		// Retera

	}

	// 加载地图中的单位和物品
	private void loadUnitsAndItems(final Warcraft3MapRuntimeObjectData modifications, final War3MapW3i mapInformation)
			throws IOException {
		// 获取地图文件对象
		final War3Map mpq = this.mapMpq;
		// 初始化单位加载状态为未完成
		this.unitsReady = false;

		// 初始化声音集名称到声音集的映射
		this.soundsetNameToSoundset = new HashMap<>();

		// 检查是否存在单位数据文件，并且配置为从WorldEdit数据加载单位
		if (this.dataSource.has("war3mapUnits.doo") && WarsmashConstants.LOAD_UNITS_FROM_WORLDEDIT_DATA) {
			// 读取单位数据文件
			final War3MapUnitsDoo dooFile = mpq.readUnits(mapInformation);

			// 遍历单位数据文件中的所有单位
			for (final com.etheller.warsmash.parsers.w3x.unitsdoo.Unit unit : dooFile.getUnits()) {
				// 获取单位ID、位置坐标、玩家索引等信息
				final War3ID unitId = unit.getId();
				final float unitX = unit.getLocation()[0];
				final float unitY = unit.getLocation()[1];
				final float unitZ = unit.getLocation()[2];
				final int playerIndex = unit.getPlayer();
				final int customTeamColor = unit.getCustomTeamColor();
				final float unitAngle = unit.getAngle();
				final int editorConfigHitPointPercent = unit.getHitpoints();

				// 创建新的单位对象
				final CWidget widgetCreated = createNewUnit(modifications, unitId, unitX, unitY, playerIndex,
						customTeamColor, unitAngle);
				// 如果创建的对象是单位类型
				if (widgetCreated instanceof CUnit) {
					final CUnit unitCreated = (CUnit) widgetCreated;
					// 如果编辑器配置的命中点百分比大于0，则设置单位的生命值
					if (editorConfigHitPointPercent > 0) {
						unitCreated.setLife(this.simulation,
								unitCreated.getMaximumLife() * (editorConfigHitPointPercent / 100f));
					}
					// 如果单位有金币数量，则设置单位的金币
					if (unit.getGoldAmount() != 0) {
						unitCreated.setGold(unit.getGoldAmount());
					}
				}
			}
		}
		// 标记单位加载完成
		this.simulation.unitsLoaded();

		// 加载地形贴花
		this.terrain.loadSplats();

		// 更新单位加载状态为完成
		this.unitsReady = true;
		// 更新任何资源加载状态为完成
		this.anyReady = true;
	}

	// 创建一个新的游戏单位或物品的渲染对象
	private CWidget createNewUnit(final Warcraft3MapRuntimeObjectData modifications, final War3ID unitId, float unitX,
			float unitY, final int playerIndex, int customTeamColor, final float unitAngle) {
		// 初始化各种可能需要的对象
		// 声明一个UnitSoundset对象，用于存储单位的音效集
		UnitSoundset soundset = null;
		// 声明一个GameObject对象，可能代表游戏中的一个单位或者物体
		GameObject row = null;
		// 声明一个String对象，用于存储文件路径或其他字符串信息
		String path = null;
		// 声明一个Splat对象，可能代表游戏中的单位阴影效果
		Splat unitShadowSplat = null;
		// 声明一个SplatMover对象，可能用于在游戏中动态移动单位阴影效果
		SplatMover unitShadowSplatDynamicIngame = null;
		// 声明一个Splat对象，可能代表游戏中的建筑覆盖效果
		Splat buildingUberSplat = null;
		// 声明一个SplatMover对象，可能用于在游戏中动态移动建筑覆盖效果
		SplatMover buildingUberSplatDynamicIngame = null;
		// 声明一个BufferedImage对象，用于存储建筑的寻路像素图
		BufferedImage buildingPathingPixelMap = null;
		// 声明一个BuildingShadow对象，可能代表建筑物的阴影实例
		BuildingShadow buildingShadowInstance = null;


		// Hardcoded?
		// 定义一个变量 type，用于存储 WorldEditorDataType 类型的值
		WorldEditorDataType type = null;

		// 判断 sloc 是否等于 unitId
		if (sloc.equals(unitId)) {
			// 如果相等，原本的代码中有一些被注释掉的逻辑，这里不再展开

			// 将 type 设置为 null，这行注释中的问号可能是开发者留下的，表示对这里的逻辑不太确定
			// 实际上，这里的 type 被设置为 null 可能是为了表示某种默认状态或者未定义状态
			type = null; /// ??????

			// 将玩家的起始位置设置为一个新的 Vector2 对象，该对象的坐标由 unitX 和 unitY 决定
			this.startLocations[playerIndex] = new Vector2(unitX, unitY);
		}
		else {
			// 尝试从单位修改中获取数据行
			row = modifications.getUnits().get(unitId);
			// 如果单位数据行不存在
			if (row == null) {
				// 尝试从物品修改中获取数据行
				row = modifications.getItems().get(unitId);
				// 如果物品数据行存在
				if (row != null) {
					// 设置数据类型为物品
					type = WorldEditorDataType.ITEM;
					// 获取物品文件路径
					path = row.getFieldAsString(ITEM_FILE, 0);

					// 如果路径以.mdl或.mdx结尾，则去掉后缀
					if (path.toLowerCase().endsWith(".mdl") || path.toLowerCase().endsWith(".mdx")) {
						path = path.substring(0, path.length() - 4);
					}

					// 获取杂项数据
					final Element misc = this.miscData.get("Misc");
					// 获取物品阴影文件名
					final String itemShadowFile = misc.getField("ItemShadowFile");
					// 获取物品阴影的宽度、高度和偏移量
					final int itemShadowWidth = misc.getFieldValue("ItemShadowSize", 0);
					final int itemShadowHeight = misc.getFieldValue("ItemShadowSize", 1);
					final int itemShadowX = misc.getFieldValue("ItemShadowOffset", 0);
					final int itemShadowY = misc.getFieldValue("ItemShadowOffset", 1);
					// 如果存在物品阴影文件且不为默认值"_"
					if ((itemShadowFile != null) && !"_".equals(itemShadowFile)) {
						// 构建阴影纹理路径
						final String texture = "ReplaceableTextures\\Shadows\\" + itemShadowFile + ".blp";
						// 计算阴影的位置和尺寸
						final float shadowX = itemShadowX;
						final float shadowY = itemShadowY;
						final float shadowWidth = itemShadowWidth;
						final float shadowHeight = itemShadowHeight;
						final float x = unitX - shadowX;
						final float y = unitY - shadowY;
						// 如果单位准备就绪
						if (this.unitsReady) {
							// 添加动态游戏内单位阴影
							unitShadowSplatDynamicIngame = this.terrain.addUnitShadowSplat(texture, x, y,
									x + shadowWidth, y + shadowHeight, 3, 0.5f, false);
						} else {
							// 如果地形贴图不包含该阴影纹理
							if (!this.terrain.splats.containsKey(texture)) {
								// 创建新的贴图
								final Splat splat = new Splat();
								splat.opacity = 0.5f;
								this.terrain.splats.put(texture, splat);
							}
							// 添加阴影位置信息
							this.terrain.splats.get(texture).locations
									.add(new float[]{x, y, x + shadowWidth, y + shadowHeight, 3});
							// 设置单位阴影贴图
							unitShadowSplat = this.terrain.splats.get(texture);
						}
					}

					// 为物品路径添加.mdx后缀
					path += ".mdx";
				}
			}
			else {
				// 设置数据类型为UNITS
				type = WorldEditorDataType.UNITS;
				// 获取单位模型路径
				path = getUnitModelPath(row);

				// 获取建筑寻路像素图
				buildingPathingPixelMap = getBuildingPathingPixelMap(row);

				// 获取单位阴影名称
				final String unitShadow = row.getFieldAsString(UNIT_SHADOW, 0);
				// 如果阴影名称不为空且不为默认值"_"，则处理阴影
				if ((unitShadow != null) && !"_".equals(unitShadow)) {
					// 构造阴影贴图路径
					String texture = "ReplaceableTextures\\Shadows\\" + unitShadow + ".blp";
					// 获取阴影位置和尺寸
					final float shadowX = row.getFieldAsFloat(UNIT_SHADOW_X, 0);
					final float shadowY = row.getFieldAsFloat(UNIT_SHADOW_Y, 0);
					final float shadowWidth = row.getFieldAsFloat(UNIT_SHADOW_W, 0);
					final float shadowHeight = row.getFieldAsFloat(UNIT_SHADOW_H, 0);
					// 如果地图文件中没有该阴影贴图，则尝试使用.dds格式作为备选
					if (!this.mapMpq.has(texture)) {
						texture = "ReplaceableTextures\\Shadows\\" + unitShadow + ".dds";
					}
					// 如果地图文件中有该阴影贴图，则添加阴影效果
					if (this.mapMpq.has(texture)) {
						// 计算阴影在地图上的位置
						final float x = unitX - shadowX;
						final float y = unitY - shadowY;
						// 如果单位已准备好，则在游戏中动态添加阴影
						if (this.unitsReady) {
							unitShadowSplatDynamicIngame = this.terrain.addUnitShadowSplat(texture, x, y,
									x + shadowWidth, y + shadowHeight, 3, 0.5f, false);
						}
						// 如果单位未准备好，则在地图数据中添加阴影信息
						else {
							if (!this.terrain.splats.containsKey(texture)) {
								final Splat splat = new Splat();
								splat.opacity = 0.5f;
								this.terrain.splats.put(texture, splat);
							}
							// 添加阴影位置信息
							this.terrain.splats.get(texture).locations
									.add(new float[]{x, y, x + shadowWidth, y + shadowHeight, 3});
							unitShadowSplat = this.terrain.splats.get(texture);
						}
					}
				}

				// 获取单位音效集名称
				final String soundName = row.getFieldAsString(UNIT_SOUNDSET, 0);
				// 尝试从映射中获取音效集
				UnitSoundset unitSoundset = this.soundsetNameToSoundset.get(soundName);
				// 如果音效集不存在，则创建新的音效集并添加到映射中
				if (unitSoundset == null) {
					unitSoundset = new UnitSoundset(this.dataSource, this.unitAckSoundsTable, soundName);
					this.soundsetNameToSoundset.put(soundName, unitSoundset);
				}
				// 设置音效集
				soundset = unitSoundset;


			}
		}

		if (path != null) {
			// 定义一个字符串变量，用于存储从文件中读取的单位特殊艺术路径
			final String unitSpecialArtPath = row.getFieldAsString(UNIT_SPECIAL, 0);

			// 定义一个MdxModel类型的变量，用于存储加载的特殊艺术模型
			MdxModel specialArtModel;

			// 检查单位特殊艺术路径是否不为null且不为空字符串
			if ((unitSpecialArtPath != null) && !unitSpecialArtPath.isEmpty()) {
				try {
					// 尝试加载特殊艺术模型
					specialArtModel = loadModelMdx(unitSpecialArtPath);
				}
				// 如果加载过程中发生异常，捕获异常并打印堆栈跟踪
				catch (final Exception exc) {
					exc.printStackTrace();
					// 将特殊艺术模型设置为null，表示加载失败
					specialArtModel = null;
				}
			} else {
				// 如果单位特殊艺术路径为null或为空字符串，将特殊艺术模型设置为null
				specialArtModel = null;
			}
			// 加载MDX模型文件
			final MdxModel model = loadModelMdx(path);
			// 声明用于存储肖像模型的变量
			MdxModel portraitModel;
			// 构造肖像模型文件的路径，假设肖像模型文件与原模型文件同名，但后缀为"_portrait.mdx"
			final String portraitPath = path.substring(0, path.length() - 4) + "_portrait.mdx";
			// 检查数据源中是否存在对应的肖像模型文件
			if (this.dataSource.has(portraitPath)) {
				// 如果存在，则加载肖像模型
				portraitModel = loadModelMdx(portraitPath);
			} else {
				// 如果不存在，则使用原模型作为肖像模型
				portraitModel = model;
			}
			// 如果类型是单位
			if (type == WorldEditorDataType.UNITS) {
				// 将单位角度转换为度数
				final float angle = (float) Math.toDegrees(unitAngle);
				// 在模拟环境中创建一个新的单位
				final CUnit simulationUnit = this.simulation.internalCreateUnit(War3ID.fromString(row.getId()),
						playerIndex, unitX, unitY, angle, buildingPathingPixelMap);
				// 更新单位的坐标
				unitX = simulationUnit.getX();
				unitY = simulationUnit.getY();
				// 获取单位类型的数据
				final RenderUnitTypeData typeData = getUnitTypeData(unitId, row);
				// 确定单位的自定义队伍颜色
				if (!typeData.isAllowCustomTeamColor() || (customTeamColor == -1)) {
					if (typeData.getTeamColor() != -1) {
						customTeamColor = typeData.getTeamColor();
					} else {
						customTeamColor = playerIndex;
					}
				}
				// 计算单位的Z坐标
				final float unitZ = Math.max(getWalkableRenderHeight(unitX, unitY),
						War3MapViewer.this.terrain.getGroundHeight(unitX, unitY)) + simulationUnit.getFlyHeight();

				// 获取单位的纹理路径
				final String texturePath = typeData.getUberSplat();
				// 获取纹理的缩放值
				final float s = typeData.getUberSplatScaleValue();
				// 如果纹理路径不为空，则添加到游戏中
				if (texturePath != null) {
					if (this.unitsReady) {
						buildingUberSplatDynamicIngame = addUberSplatIngame(unitX, unitY, texturePath, s);
					} else {
						// 如果地形中没有该纹理，则添加
						if (!this.terrain.splats.containsKey(texturePath)) {
							this.terrain.splats.put(texturePath, new Splat());
						}
						// 记录纹理的位置
						final float x = unitX;
						final float y = unitY;
						buildingUberSplat = this.terrain.splats.get(texturePath);
						buildingUberSplat.locations.add(new float[]{x - s, y - s, x + s, y + s, 1});
					}
				}

				// 获取单位的阴影效果
				final String buildingShadow = typeData.getBuildingShadow();
				// 如果有阴影效果，则添加到地形中
				if (buildingShadow != null) {
					buildingShadowInstance = this.terrain.addShadow(buildingShadow, unitX, unitY);
				}

				// 创建一个新的渲染单位
				final RenderUnit renderUnit = new RenderUnit(this, model, row, unitX, unitY, unitZ, customTeamColor,
						soundset, portraitModel, simulationUnit, typeData, specialArtModel, buildingShadowInstance,
						this.selectionCircleScaleFactor, typeData.getAnimationWalkSpeed(),
						typeData.getAnimationRunSpeed(), typeData.getScalingValue());
				// 将模拟单位与渲染单位关联
				this.unitToRenderPeer.put(simulationUnit, renderUnit);
				// 将渲染单位添加到小部件和单位列表中
				this.widgets.add(renderUnit);
				this.units.add(renderUnit);
				// 如果有单位阴影效果，则将其应用到渲染单位
				if (unitShadowSplat != null) {
					unitShadowSplat.unitMapping.add(new Consumer<SplatModel.SplatMover>() {
						@Override
						public void accept(final SplatMover t) {
							renderUnit.shadow = t;
						}
					});
				}
				// 如果有动态游戏中的单位阴影效果，则应用
				if (unitShadowSplatDynamicIngame != null) {
					renderUnit.shadow = unitShadowSplatDynamicIngame;
				}
				// 如果有建筑Uber Splat效果，则将其应用到渲染单位
				if (buildingUberSplat != null) {
					buildingUberSplat.unitMapping.add(new Consumer<SplatModel.SplatMover>() {
						@Override
						public void accept(final SplatMover t) {
							renderUnit.uberSplat = t;
						}
					});
				}
				// 如果有动态游戏中的建筑Uber Splat效果，则应用
				if (buildingUberSplatDynamicIngame != null) {
					renderUnit.uberSplat = buildingUberSplatDynamicIngame;
				}
				// 返回创建的模拟单位
				return simulationUnit;
			}

			else {
				// 创建一个新的模拟物品，使用从文件中读取的ID和坐标
				final CItem simulationItem = this.simulation.internalCreateItem(War3ID.fromString(row.getId()), unitX, unitY);

				// 计算物品的Z坐标，取可行走渲染高度和地形高度的较大值
				final float unitZ = Math.max(getWalkableRenderHeight(unitX, unitY), War3MapViewer.this.terrain.getGroundHeight(unitX, unitY));

				// 创建一个新的渲染项，包含所有必要的信息，如模型、位置、声音集等
				final RenderItem renderItem = new RenderItem(this, model, row, unitX, unitY, unitZ, unitAngle, soundset, portraitModel, simulationItem);

				// 将新的渲染项添加到小部件列表中
				this.widgets.add(renderItem);

				// 将模拟物品和渲染项关联起来，以便于后续访问
				this.itemToRenderPeer.put(simulationItem, renderItem);

				// 如果存在单位阴影涂抹效果，则添加一个消费者，当阴影移动时更新渲染项的阴影
				if (unitShadowSplat != null) {
					unitShadowSplat.unitMapping.add(new Consumer<SplatModel.SplatMover>() {
						@Override
						public void accept(final SplatMover t) {
							renderItem.shadow = t;
						}
					});
				}

				// 如果存在动态游戏内单位阴影涂抹效果，则直接将其设置为渲染项的阴影
				if (unitShadowSplatDynamicIngame != null) {
					renderItem.shadow = unitShadowSplatDynamicIngame;
				}

				// 返回创建的模拟物品
				return simulationItem;

			}
		}
		else {
			System.err.println("Unknown unit ID: " + unitId);
		}
		return null;
	}

	// 定义一个方法，用于在游戏中添加超级溅射效果（Uber Splat）
	// 参数包括单位坐标X，单位坐标Y，纹理路径，以及溅射效果的强度
	public SplatMover addUberSplatIngame(final float unitX, final float unitY, final String texturePath,
										 final float s) {
		// 创建一个SplatMover对象，用于控制溅射效果
		SplatMover buildingUberSplatDynamicIngame;
		// 调用地形对象的addUberSplat方法，添加溅射效果
		// 参数解释：
		// texturePath: 溅射效果的纹理文件路径
		// unitX, unitY: 溅射效果的位置坐标
		// 1: 溅射效果的层级，这里设为1
		// s: 溅射效果的强度
		// 后四个false参数分别代表不同的溅射效果选项，具体含义需参考游戏引擎文档
		buildingUberSplatDynamicIngame = this.terrain.addUberSplat(texturePath, unitX, unitY, 1, s, false, false, false,
				false);
		// 返回创建的SplatMover对象，以便外部调用者可以进一步控制溅射效果
		return buildingUberSplatDynamicIngame;
	}

	// 获取单位模型路径的方法
	public String getUnitModelPath(final GameObject row) {
		String path; // 初始化路径变量

		// 从GameObject中获取单位文件字段的值
		path = row.getFieldAsString(UNIT_FILE, 0);

		// 如果路径以".mdl"或".mdx"结尾，则去掉后缀
		if (path.toLowerCase().endsWith(".mdl") || path.toLowerCase().endsWith(".mdx")) {
			path = path.substring(0, path.length() - 4);
		}

		// 如果文件版本标志为2，并且数据源中存在_V1版本的模型文件
		if ((row.readSLKTagInt("fileVerFlags") == 2) && this.dataSource.has(path + "_V1.mdx")) {
			path += "_V1"; // 在路径后加上"_V1"
		}

		// 添加".mdx"后缀
		path += ".mdx";
		return path; // 返回最终的模型路径
	}


	// 获取建筑物的寻路像素图
	private BufferedImage getBuildingPathingPixelMap(final GameObject row) {
		// 从GameObject中获取寻路纹理的路径
		final String pathingTexture = row.getFieldAsString(UNIT_PATHING, 0);
		// 加载寻路纹理并返回对应的像素图
		final BufferedImage buildingPathingPixelMap = loadPathingTexture(pathingTexture);
		return buildingPathingPixelMap;
	}

	// 加载寻路纹理
	private BufferedImage loadPathingTexture(final String pathingTexture) {
		// 初始化寻路像素图为空
		BufferedImage buildingPathingPixelMap = null;
		// 检查路径是否有效且不为空字符串或下划线
		if ((pathingTexture != null) && (pathingTexture.length() > 0) && !"_".equals(pathingTexture)) {
			// 尝试从缓存中获取寻路像素图
			buildingPathingPixelMap = this.filePathToPathingMap.get(pathingTexture.toLowerCase());
			// 如果缓存中没有，则尝试加载
			if (buildingPathingPixelMap == null) {
				try {
					// 如果是TGA格式的文件
					if (pathingTexture.toLowerCase().endsWith(".tga")) {
						// 使用TgaFile工具类读取TGA文件
						buildingPathingPixelMap = TgaFile.readTGA(pathingTexture,
								this.mapMpq.getResourceAsStream(pathingTexture));
					}
					// 其他格式的文件
					else {
						// 使用try-with-resources确保流在使用后自动关闭
						try (InputStream stream = this.mapMpq.getResourceAsStream(pathingTexture)) {
							// 使用ImageIO工具类读取图片
							buildingPathingPixelMap = ImageIO.read(stream);
							// 打印加载信息
							System.out.println("LOADING BLP PATHING: " + pathingTexture);
						}
					}
					// 将加载的寻路像素图存入缓存
					this.filePathToPathingMap.put(pathingTexture.toLowerCase(), buildingPathingPixelMap);
				}
				// 捕获并打印异常信息
				catch (final Exception exc) {
					System.err.println("Failure to get pathing: " + exc.getClass() + ":" + exc.getMessage());
				}
			}
		}
		// 返回寻路像素图
		return buildingPathingPixelMap;
	}

	// 获取单位类型数据的方法
	public RenderUnitTypeData getUnitTypeData(final War3ID key, GameObject row) {
		// 尝试从缓存中获取单位类型数据
		RenderUnitTypeData unitTypeData = this.unitIdToTypeData.get(key);
		// 如果缓存中没有找到，则需要创建新的单位类型数据
		if (unitTypeData == null) {
			// 如果传入的row为空，则尝试从所有对象数据中获取
			if (row == null) {
				row = this.allObjectData.getUnits().get(key);
				// 如果仍然找不到，抛出异常
				if (row == null) {
					throw new IllegalStateException("getUnitTypeData(" + key + ") : No such unit type");
				}
			}
			// 获取单位类型的uberSplat信息
			final String uberSplat = row.getFieldAsString(UBER_SPLAT, 0);
			String uberSplatTexturePath = null;
			float uberSplatScaleValue = 0.0f;
			if (uberSplat != null) {
				final Element uberSplatInfo = this.terrain.uberSplatTable.get(uberSplat);
				if (uberSplatInfo != null) {
					// 构建uberSplat纹理路径
					uberSplatTexturePath = uberSplatInfo.getField("Dir") + "\\" + uberSplatInfo.getField("file")
							+ ".blp";
					// 获取uberSplat缩放值
					uberSplatScaleValue = uberSplatInfo.getFieldFloatValue("Scale");
				}
			}
			// 获取建筑阴影信息
			String buildingShadow = row.getFieldAsString(BUILDING_SHADOW, 0);
			if ("_".equals(buildingShadow)) {
				buildingShadow = null;
			}
			// 初始化附件动画名称集合
			final EnumSet<SecondaryTag> requiredAnimationNamesForAttachments = EnumSet.noneOf(SecondaryTag.class);
			// 获取附件动画属性字符串
			final String requiredAnimationNamesForAttachmentsString = row
					.getFieldAsString(RenderUnit.ATTACHMENT_ANIM_PROPS, 0);
			// 解析附件动画名称字符串，并添加到集合中
			TokenLoop:
			for (final String animationName : requiredAnimationNamesForAttachmentsString.split(",")) {
				final String upperCaseToken = animationName.toUpperCase();
				for (final SecondaryTag secondaryTag : SecondaryTag.values()) {
					if (upperCaseToken.equals(secondaryTag.name())) {
						requiredAnimationNamesForAttachments.add(secondaryTag);
						continue TokenLoop;
					}
				}
			}
			// 创建新的单位类型数据对象
			unitTypeData = new RenderUnitTypeData(row.getFieldAsFloat(MAX_PITCH, 0), row.getFieldAsFloat(MAX_ROLL, 0),
					row.getFieldAsFloat(ELEVATION_SAMPLE_RADIUS, 0), row.getFieldAsBoolean(ALLOW_CUSTOM_TEAM_COLOR, 0),
					row.getFieldAsInteger(TEAM_COLOR, 0), row.getFieldAsFloat(ANIMATION_RUN_SPEED, 0),
					row.getFieldAsFloat(ANIMATION_WALK_SPEED, 0), row.getFieldAsFloat(MODEL_SCALE, 0), buildingShadow,
					uberSplatTexturePath, uberSplatScaleValue, requiredAnimationNamesForAttachments);
			// 将新的单位类型数据存入缓存
			this.unitIdToTypeData.put(key, unitTypeData);
		}
		// 返回单位类型数据
		return unitTypeData;
	}


	public RenderUnitTypeData getUnitTypeData(final War3ID key) {
		return getUnitTypeData(key, null);
	}

	@Override
	// 更新方法，处理游戏中的各种更新逻辑
	public void update() {
		// 如果有任何准备好的元素，则进行更新
		if (this.anyReady) {
			// 获取自上次渲染以来的时间差
			final float deltaTime = Gdx.graphics.getDeltaTime();
			// 更新地形
			this.terrain.update(deltaTime);

			// 调用父类的更新方法
			super.update();

			// 更新文本标签，如果文本标签不再需要则移除
			final Iterator<TextTag> textTagIterator = this.textTags.iterator();
			while (textTagIterator.hasNext()) {
				if (textTagIterator.next().update(deltaTime)) {
					textTagIterator.remove();
				}
			}

			// 更新所有渲染小部件的动画
			for (final RenderWidget unit : this.widgets) {
				unit.updateAnimations(this);
			}

			// 更新所有投射物的动画，如果投射物不再需要则移除
			final Iterator<RenderEffect> projectileIterator = this.projectiles.iterator();
			while (projectileIterator.hasNext()) {
				final RenderEffect projectile = projectileIterator.next();
				if (projectile.updateAnimations(this, Gdx.graphics.getDeltaTime())) {
					projectileIterator.remove();
				}
			}

			// 更新所有渲染装饰物的动画
			for (final RenderDoodad item : this.doodads) {
				final ModelInstance instance = item.instance;
				// 如果实例是复杂的MdxComplexInstance，则尝试更新其动画序列
				if (instance instanceof MdxComplexInstance) {
					final MdxComplexInstance mdxComplexInstance = (MdxComplexInstance) instance;
					// 如果当前序列未设置或已结束，尝试随机选择一个新的序列
					if ((mdxComplexInstance.sequence == -1) || (mdxComplexInstance.sequenceEnded
							&& ((item.getAnimation() != AnimationTokens.PrimaryTag.DEATH)
							|| (((MdxModel) mdxComplexInstance.model).sequences.get(mdxComplexInstance.sequence)
							.getFlags() == 0)))) {
						SequenceUtils.randomSequence(mdxComplexInstance, item.getAnimation(), SequenceUtils.EMPTY,
								true);
					}
				}
			}

			// 获取未处理的原始时间差
			final float rawDeltaTime = Gdx.graphics.getRawDeltaTime();
			// 累加更新时间
			this.updateTime += rawDeltaTime;
			// 如果累积的更新时间达到了模拟步进时间，则进行游戏逻辑更新
			while (this.updateTime >= WarsmashConstants.SIMULATION_STEP_TIME) {
				// 如果最新的完成回合大于等于模拟的游戏回合，则更新模拟并通知回合完成
				if (this.gameTurnManager.getLatestCompletedTurn() >= this.simulation.getGameTurnTick()) {
					this.updateTime -= WarsmashConstants.SIMULATION_STEP_TIME;
					this.simulation.update();
					this.gameTurnManager.turnCompleted(this.simulation.getGameTurnTick());
				}
				// 如果更新时间过长，则跳过一些帧并重置更新时间
				else {
					if (this.updateTime > (WarsmashConstants.SIMULATION_STEP_TIME * 3)) {
						this.gameTurnManager.framesSkipped(this.updateTime / WarsmashConstants.SIMULATION_STEP_TIME);
						this.updateTime = 0;
					}
					break;
				}
			}

			// 更新日夜循环相关的动态地形、单位和目标
			if (this.dncTerrain != null) {
				this.dncTerrain.setFrameByRatio(
						this.simulation.getGameTimeOfDay() / this.simulation.getGameplayConstants().getGameDayHours());
				this.dncTerrain.update(rawDeltaTime, null);
			}
			if (this.dncUnit != null) {
				this.dncUnit.setFrameByRatio(
						this.simulation.getGameTimeOfDay() / this.simulation.getGameplayConstants().getGameDayHours());
				this.dncUnit.update(rawDeltaTime, null);
			}
			if (this.dncTarget != null) {
				this.dncTarget.setFrameByRatio(
						this.simulation.getGameTimeOfDay() / this.simulation.getGameplayConstants().getGameDayHours());
				this.dncTarget.update(rawDeltaTime, null);
			}
		}
	}


	@Override
	public void render() {
		if (this.anyReady) {
			final Scene worldScene = this.worldScene;

			startFrame();
			worldScene.startFrame();
			if (DEBUG_DEPTH > 0) {
				worldScene.renderOpaque(this.dynamicShadowManager, this.webGL);
			}
			if (DEBUG_DEPTH > 1) {
				this.terrain.renderGround(this.dynamicShadowManager);
			}
			if (DEBUG_DEPTH > 2) {
				this.terrain.renderCliffs();
			}
			if (DEBUG_DEPTH > 3) {
				worldScene.renderOpaque();
			}
			if (DEBUG_DEPTH > 4) {
				this.terrain.renderUberSplats(false);
			}
			if (DEBUG_DEPTH > 5) {
				this.terrain.renderWater();
			}
			if (DEBUG_DEPTH > 6) {
				worldScene.renderTranslucent();
			}
			if (DEBUG_DEPTH > 7) {
				this.terrain.renderUberSplats(true);
			}

			final List<Scene> scenes = this.scenes;
			for (final Scene scene : scenes) {
				if (scene != worldScene) {
					scene.startFrame();
					if (DEBUG_DEPTH > 8) {
						scene.renderOpaque();
					}
					if (DEBUG_DEPTH > 9) {
						scene.renderTranslucent();
					}
				}
			}

			final int glGetError = Gdx.gl.glGetError();
			if (glGetError != GL20.GL_NO_ERROR) {
				throw new IllegalStateException("GL ERROR: " + glGetError);
			}
		}
	}

	// 取消选择的方法，会清除所有选中的地形贴花模型和单位的选择状态
	public void deselect() {
		// 遍历所有选中的地形贴花模型的键值
		for (final String key : this.selectedSplatModelKeys) {
			// 从地形中移除对应的贴花模型
			this.terrain.removeSplatBatchModel(key);
		}
		// 遍历所有选中的单位
		for (final RenderWidget unit : this.selected) {
			// 取消单位的选择圈
			unit.unassignSelectionCircle();
		}
		// 清空选中的地形贴花模型的键值集合
		this.selectedSplatModelKeys.clear();
		// 清空选中的单位集合
		this.selected.clear();
	}

	// 取消选择单个单位的方法
	public void doUnselectUnit(final RenderWidget widget) {
		// 尝试从选中的单位集合中移除指定的单位
		if (this.selected.remove(widget)) {
			// 如果成功移除，则取消该单位的选择圈
			widget.unassignSelectionCircle();
		}
	}

	// 选择单位的方法，传入一个包含RenderWidget对象的列表
	public void doSelectUnit(final List<RenderWidget> units) {
		// 取消之前的选择
		deselect();
		// 如果没有单位被选中，则直接返回
		if (units.isEmpty()) {
			return;
		}

		// 创建一个新的HashMap来存储不同纹理的选择圈
		final Map<String, Terrain.Splat> splats = new HashMap<String, Terrain.Splat>();
		// 遍历所有选中的单位
		for (final RenderWidget unit : units) {
			// 如果单位的选择圈比例大于0，则进行处理
			if (unit.getSelectionScale() > 0) {
				String allyKey = "n:"; // 默认盟友键值
				final float selectionSize = unit.getSelectionScale(); // 获取选择圈的大小
				String path = null; // 选择圈纹理路径
				// 遍历选择圈大小列表，找到对应的选择圈纹理
				for (int i = 0; i < this.selectionCircleSizes.size(); i++) {
					final SelectionCircleSize selectionCircleSize = this.selectionCircleSizes.get(i);
					if ((selectionSize < selectionCircleSize.size) || (i == (this.selectionCircleSizes.size() - 1))) {
						path = selectionCircleSize.texture;
						break;
					}
				}
				// 确保纹理路径以.blp结尾
				if (!path.toLowerCase().endsWith(".blp")) {
					path += ".blp";
				}
				// 如果单位是RenderUnit类型，检查与本地玩家的关系
				if (unit instanceof RenderUnit) {
					final int selectedUnitPlayerIndex = ((RenderUnit) unit).getSimulationUnit().getPlayerIndex();
					final CPlayer localPlayer = this.simulation.getPlayer(this.localPlayerIndex);
					// 根据关系设置不同的盟友键值
					if (!localPlayer.hasAlliance(selectedUnitPlayerIndex, CAllianceType.PASSIVE)) {
						allyKey = "e:";
					} else if (localPlayer.hasAlliance(selectedUnitPlayerIndex, CAllianceType.SHARED_CONTROL)) {
						allyKey = "f:";
					}
				}
				// 如果单位显示选择圈在水上，则修改路径
				path = allyKey + path;
				if (unit.isShowSelectionCircleAboveWater()) {
					path = path + ":abovewater";
				}
				// 获取选择圈模型
				final SplatModel splatModel = this.terrain.getSplatModel("selection:" + path);
				// 如果模型存在，则创建选择圈实例并分配给单位
				if (splatModel != null) {
					final float x = unit.getX();
					final float y = unit.getY();
					final SplatMover splatInstance = splatModel.add(x - (selectionSize / 2), y - (selectionSize / 2),
							x + (selectionSize / 2), y + (selectionSize / 2), 5, this.terrain.centerOffset);
					unit.assignSelectionCircle(splatInstance);
					// 如果单位隐藏，则隐藏选择圈
					if (unit.getInstance().hidden()) {
						splatInstance.hide();
					}
				} else {
					// 如果模型不存在，则添加到HashMap中
					if (!splats.containsKey(path)) {
						splats.put(path, new Splat());
					}
					// 记录选择圈的位置信息
					final float x = unit.getX();
					final float y = unit.getY();
					System.out.println("Selecting a unit at " + x + "," + y);
					if (unit.isShowSelectionCircleAboveWater()) {
						splats.get(path).aboveWater = true;
					}
					splats.get(path).locations.add(new float[]{x - (selectionSize / 2), y - (selectionSize / 2),
							x + (selectionSize / 2), y + (selectionSize / 2), 5});
					// 分配选择圈实例给单位
					splats.get(path).unitMapping.add(new Consumer<SplatModel.SplatMover>() {
						@Override
						public void accept(final SplatMover t) {
							unit.assignSelectionCircle(t);
							if (unit.getInstance().hidden()) {
								t.hide();
							}
						}
					});
				}
				// 将单位添加到选中单位列表
				this.selected.add(unit);
			}
		}
		// 遍历HashMap，创建并添加选择圈模型到地形
		for (final Map.Entry<String, Terrain.Splat> entry : splats.entrySet()) {
			final String path = entry.getKey();
			String filePath = path.substring(2);
			// 如果路径包含":abovewater"，则移除
			if (filePath.endsWith(":abovewater")) {
				filePath = filePath.substring(0, filePath.length() - 11);
			}
			final String allyKey = path.substring(0, 2);
			final Splat locations = entry.getValue();
			// 创建选择圈模型
			final SplatModel model = new SplatModel(Gdx.gl30, (Texture) load(filePath, PathSolver.DEFAULT, null),
					locations.locations, this.terrain.centerOffset, locations.unitMapping, true, false, true,
					locations.aboveWater);
			// 根据盟友键值设置颜色
			switch (allyKey) {
				case "e:":
					model.color[0] = this.selectionCircleColorEnemy.r;
					model.color[1] = this.selectionCircleColorEnemy.g;
					model.color[2] = this.selectionCircleColorEnemy.b;
					model.color[3] = this.selectionCircleColorEnemy.a;
					break;
				case "f:":
					model.color[0] = this.selectionCircleColorFriend.r;
					model.color[1] = this.selectionCircleColorFriend.g;
					model.color[2] = this.selectionCircleColorFriend.b;
					model.color[3] = this.selectionCircleColorFriend.a;
					break;
				default:
					model.color[0] = this.selectionCircleColorNeutral.r;
					model.color[1] = this.selectionCircleColorNeutral.g;
					model.color[2] = this.selectionCircleColorNeutral.b;
					model.color[3] = this.selectionCircleColorNeutral.a;
					break;
			}
			// 添加选择圈模型到地形
			this.terrain.addSplatBatchModel("selection:" + path, model);
			// 记录已添加的选择圈模型键值
			this.selectedSplatModelKeys.add("selection:" + path);
		}
	}


	public void clearUnitMouseOverHighlight(final RenderWidget unit) {
		this.mouseHighlightWidgets.remove(unit);
		unit.getSelectionPreviewHighlight().destroy(Gdx.gl30, this.terrain.centerOffset);
		unit.unassignSelectionPreviewHighlight();
	}

	public void clearUnitMouseOverHighlight() {
		for (final String modelKey : this.mouseHighlightSplatModelKeys) {
			this.terrain.removeSplatBatchModel(modelKey);
		}
		for (final RenderWidget widget : this.mouseHighlightWidgets) {
			widget.unassignSelectionPreviewHighlight();
		}
		this.mouseHighlightSplatModelKeys.clear();
		this.mouseHighlightWidgets.clear();
	}

	public void showUnitMouseOverHighlight(final RenderWidget unit) {
		final Map<String, Terrain.Splat> splats = new HashMap<String, Terrain.Splat>();
		if (unit.getSelectionScale() > 0) {
			String allyKey = "n:";
			final float selectionSize = unit.getSelectionScale();
			String path = null;
			for (int i = 0; i < this.selectionCircleSizes.size(); i++) {
				final SelectionCircleSize selectionCircleSize = this.selectionCircleSizes.get(i);
				if ((selectionSize < selectionCircleSize.size) || (i == (this.selectionCircleSizes.size() - 1))) {
					path = selectionCircleSize.texture;
					break;
				}
			}
			if (!path.toLowerCase().endsWith(".blp")) {
				path += ".blp";
			}
			if (unit instanceof RenderUnit) {
				final CUnit simulationUnit = ((RenderUnit) unit).getSimulationUnit();
				final int selectedUnitPlayerIndex = simulationUnit.getPlayerIndex();
				final CPlayer localPlayer = this.simulation.getPlayer(this.localPlayerIndex);
				if (!localPlayer.hasAlliance(selectedUnitPlayerIndex, CAllianceType.PASSIVE)) {
					allyKey = "e:";
				}
				else if (localPlayer.hasAlliance(selectedUnitPlayerIndex, CAllianceType.SHARED_CONTROL)) {
					allyKey = "f:";
				}
			}
			path = allyKey + path;
			if (unit.isShowSelectionCircleAboveWater()) {
				path = path + ":abovewater";
			}
			final SplatModel splatModel = this.terrain.getSplatModel("mouseover:" + path);
			if (splatModel != null) {
				final float x = unit.getX();
				final float y = unit.getY();
				final SplatMover splatInstance = splatModel.add(x - (selectionSize / 2), y - (selectionSize / 2),
						x + (selectionSize / 2), y + (selectionSize / 2), 4, this.terrain.centerOffset);
				unit.assignSelectionPreviewHighlight(splatInstance);
				if (unit.getInstance().hidden()) {
					splatInstance.hide();
				}
			}
			else {
				if (!splats.containsKey(path)) {
					splats.put(path, new Splat());
				}
				final float x = unit.getX();
				final float y = unit.getY();
				if (unit.isShowSelectionCircleAboveWater()) {
					splats.get(path).aboveWater = true;
				}
				splats.get(path).locations.add(new float[] { x - (selectionSize / 2), y - (selectionSize / 2),
						x + (selectionSize / 2), y + (selectionSize / 2), 4 });
				splats.get(path).unitMapping.add(new Consumer<SplatModel.SplatMover>() {
					@Override
					public void accept(final SplatMover t) {
						unit.assignSelectionPreviewHighlight(t);
						if (unit.getInstance().hidden()) {
							t.hide();
						}
					}
				});
			}
		}
		this.mouseHighlightWidgets.add(unit);
		for (final Map.Entry<String, Terrain.Splat> entry : splats.entrySet()) {
			final String path = entry.getKey();
			String filePath = path.substring(2);
			if (filePath.endsWith(":abovewater")) {
				filePath = filePath.substring(0, filePath.length() - 11);
			}
			final String allyKey = path.substring(0, 2);
			final Splat locations = entry.getValue();
			final SplatModel model = new SplatModel(Gdx.gl30, (Texture) load(filePath, PathSolver.DEFAULT, null),
					locations.locations, this.terrain.centerOffset, locations.unitMapping, true, false, true,
					locations.aboveWater);
			switch (allyKey) {
			case "e:":
				model.color[0] = this.selectionCircleColorEnemy.r;
				model.color[1] = this.selectionCircleColorEnemy.g;
				model.color[2] = this.selectionCircleColorEnemy.b;
				model.color[3] = this.selectionCircleColorEnemy.a * 0.5f;
				break;
			case "f:":
				model.color[0] = this.selectionCircleColorFriend.r;
				model.color[1] = this.selectionCircleColorFriend.g;
				model.color[2] = this.selectionCircleColorFriend.b;
				model.color[3] = this.selectionCircleColorFriend.a * 0.5f;
				break;
			default:
				model.color[0] = this.selectionCircleColorNeutral.r;
				model.color[1] = this.selectionCircleColorNeutral.g;
				model.color[2] = this.selectionCircleColorNeutral.b;
				model.color[3] = this.selectionCircleColorNeutral.a * 0.5f;
				break;
			}
			this.mouseHighlightSplatModelKeys.add("mouseover:" + path);
			this.terrain.addSplatBatchModel("mouseover:" + path, model);
		}
	}

	public void getClickLocation(final Vector3 out, final int screenX, final int screenY,
			final boolean intersectWithWater, final boolean pushResultZAboveWater) {
		final float[] ray = rayHeap;
		mousePosHeap.set(screenX, screenY);
		this.worldScene.camera.screenToWorldRay(ray, mousePosHeap);
		gdxRayHeap.set(ray[0], ray[1], ray[2], ray[3] - ray[0], ray[4] - ray[1], ray[5] - ray[2]);
		gdxRayHeap.direction.nor();// needed for libgdx
		if (intersectWithWater) {
			RenderMathUtils.intersectRayTriangles(gdxRayHeap, this.terrain.softwareWaterAndGroundMesh.vertices,
					this.terrain.softwareWaterAndGroundMesh.indices, 3, out);
		}
		else {
			RenderMathUtils.intersectRayTriangles(gdxRayHeap, this.terrain.softwareGroundMesh.vertices,
					this.terrain.softwareGroundMesh.indices, 3, out);
		}
		rectangleHeap.set(Math.min(out.x, gdxRayHeap.origin.x), Math.min(out.y, gdxRayHeap.origin.y),
				Math.abs(out.x - gdxRayHeap.origin.x), Math.abs(out.y - gdxRayHeap.origin.y));
		this.walkableObjectsTree.intersect(rectangleHeap, this.walkablesIntersectionFinder.reset(gdxRayHeap));
		if (this.walkablesIntersectionFinder.found) {
			out.set(this.walkablesIntersectionFinder.intersection);
		}
		else {
			final float oldZ = out.z;
			out.z = Math.max(getWalkableRenderHeight(out.x, out.y), this.terrain.getGroundHeight(out.x, out.y));

			if (pushResultZAboveWater) {
				out.z = Math.max(out.z, oldZ);
				final short pathing = this.simulation.getPathingGrid().getPathing(out.x, out.y);
				if (PathingGrid.isPathingFlag(pathing, PathingType.SWIMMABLE)
						&& !PathingGrid.isPathingFlag(pathing, PathingType.WALKABLE)) {
					out.z = Math.max(out.z, this.terrain.getWaterHeight(out.x, out.y));
				}
			}
		}
	}

	public void getClickLocationOnZPlane(final Vector3 out, final int screenX, final int screenY, final float worldZ) {
		final float[] ray = rayHeap;
		mousePosHeap.set(screenX, screenY);
		this.worldScene.camera.screenToWorldRay(ray, mousePosHeap);
		gdxRayHeap.set(ray[0], ray[1], ray[2], ray[3] - ray[0], ray[4] - ray[1], ray[5] - ray[2]);
		gdxRayHeap.direction.nor();// needed for libgdx
		planeHeap.set(0, 0, -1, worldZ);
		Intersector.intersectRayPlane(gdxRayHeap, planeHeap, out);
	}

	public void showConfirmation(final Vector3 position, final float red, final float green, final float blue) {
		this.confirmationInstance.show();
		this.confirmationInstance.setSequence(0);
		this.confirmationInstance.setLocation(position);
		this.worldScene.instanceMoved(this.confirmationInstance, position.x, position.y);
		this.confirmationInstance.vertexColor[0] = red;
		this.confirmationInstance.vertexColor[1] = green;
		this.confirmationInstance.vertexColor[2] = blue;
	}

	public RenderWidget rayPickUnit(final float x, final float y) {
		return this.rayPickUnit(x, y, CWidgetFilterFunction.ACCEPT_ALL);
	}

	public RenderWidget rayPickUnit(final float x, final float y, final CWidgetFilterFunction filter) {
		final float[] ray = rayHeap;
		mousePosHeap.set(x, y);
		this.worldScene.camera.screenToWorldRay(ray, mousePosHeap);
		gdxRayHeap.set(ray[0], ray[1], ray[2], ray[3] - ray[0], ray[4] - ray[1], ray[5] - ray[2]);
		gdxRayHeap.direction.nor();// needed for libgdx

		RenderWidget entity = null;
		intersectionHeap2.set(ray[3], ray[4], ray[5]);
		for (final RenderWidget unit : this.widgets) {
			final MdxComplexInstance instance = unit.getInstance();
			if (instance.shown() && instance.isVisible(this.worldScene.camera)
					&& instance.intersectRayWithCollisionSimple(gdxRayHeap, intersectionHeap)) {
				if (filter.call(unit.getSimulationWidget())) {
					final float groundHeight = this.terrain.getGroundHeight(intersectionHeap.x, intersectionHeap.y);
					if (intersectionHeap.z > groundHeight) {
						if ((entity == null) && !unit.isIntersectedOnMeshAlways()) {
							entity = unit;
						}
						else {
							if (instance.intersectRayWithMeshSlow(gdxRayHeap, intersectionHeap)) {
								if (intersectionHeap.z > this.terrain.getGroundHeight(intersectionHeap.x,
										intersectionHeap.y)) {
									this.worldScene.camera.worldToCamera(intersectionHeap, intersectionHeap);
									if ((entity == null) || (intersectionHeap.z < intersectionHeap2.z)) {
										entity = unit;
										intersectionHeap2.set(intersectionHeap);
									}
								}
							}
						}
					}
				}
			}
		}
		return entity;
	}

	private static final class MappedDataCallbackImplementation implements LoadGenericCallback {
		@Override
		public Object call(final InputStream data) {
			final StringBuilder stringBuilder = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(data, "utf-8"))) {
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
					stringBuilder.append("\n");
				}
			}
			catch (final UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
			return new MappedData(stringBuilder.toString());
		}
	}

	private static final class StringDataCallbackImplementation implements LoadGenericCallback {
		@Override
		public Object call(final InputStream data) {
			if (data == null) {
				System.err.println("data null");
			}
			final StringBuilder stringBuilder = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(data, "utf-8"))) {
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
					stringBuilder.append("\n");
				}
			}
			catch (final UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
			return stringBuilder.toString();
		}
	}

	private static final class StreamDataCallbackImplementation implements LoadGenericCallback {
		@Override
		public Object call(final InputStream data) {
			return data;
		}
	}

	public static final class SolverParams {
		public char tileset;
		public boolean reforged;
		public boolean hd;
	}

	public static final class CliffInfo {
		public List<float[]> locations = new ArrayList<>();
		public List<Integer> textures = new ArrayList<>();
	}

	private static final int MAXIMUM_ACCEPTED = 1 << 30;
	private float selectionCircleScaleFactor;
	private DataTable worldEditData;
	private WorldEditStrings worldEditStrings;
	private Warcraft3MapRuntimeObjectData allObjectData;
	private AbilityDataUI abilityDataUI;
	private Map<String, UnitSoundset> soundsetNameToSoundset;
	public int imageWalkableZOffset;
	private WTS preloadedWTS;

	private Color selectionCircleColorFriend;

	private Color selectionCircleColorNeutral;

	private Color selectionCircleColorEnemy;

	private int localPlayerServerSlot;

	private final Rectangle tempBlightRect = new Rectangle();

	/**
	 * Returns a power of two size for the given target capacity.
	 */
	private static final int pow2GreaterThan(final int capacity) {
		int numElements = capacity - 1;
		numElements |= numElements >>> 1;
		numElements |= numElements >>> 2;
		numElements |= numElements >>> 4;
		numElements |= numElements >>> 8;
		numElements |= numElements >>> 16;
		return numElements < 0 ? 1 : numElements >= MAXIMUM_ACCEPTED ? MAXIMUM_ACCEPTED : numElements + 1;
	}

	public void standOnRepeat(final MdxComplexInstance instance) {
		instance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		SequenceUtils.randomStandSequence(instance);
	}

	private static final class SelectionCircleSize {
		private final float size;
		private final String texture;
		private final String textureDotted;

		public SelectionCircleSize(final float size, final String texture, final String textureDotted) {
			this.size = size;
			this.texture = texture;
			this.textureDotted = textureDotted;
		}
	}

	public void setDayNightModels(final String terrainDNCFile, final String unitDNCFile) {
		final MdxModel terrainDNCModel = loadModelMdx(terrainDNCFile);
		this.dncTerrain = (MdxComplexInstance) terrainDNCModel.addInstance();
		this.dncTerrain.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		this.dncTerrain.setSequence(0);
		final MdxModel unitDNCModel = loadModelMdx(unitDNCFile);
		this.dncUnit = (MdxComplexInstance) unitDNCModel.addInstance();
		this.dncUnit.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		this.dncUnit.setSequence(0);
		final MdxModel targetDNCModel = loadModelMdx(
				"Environment\\DNC\\DNCLordaeron\\DNCLordaeronTarget\\DNCLordaeronTarget.mdl");
		this.dncTarget = (MdxComplexInstance) targetDNCModel.addInstance();
		this.dncTarget.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		this.dncTarget.setSequence(0);
	}

	public static String mdx(String mdxPath) {
		if (mdxPath.toLowerCase().endsWith(".mdl")) {
			mdxPath = mdxPath.substring(0, mdxPath.length() - 4);
		}
		if (!mdxPath.toLowerCase().endsWith(".mdx")) {
			mdxPath += ".mdx";
		}
		return mdxPath;
	}

	public static String mdl(String mdxPath) {
		if (mdxPath.toLowerCase().endsWith(".mdx")) {
			mdxPath = mdxPath.substring(0, mdxPath.length() - 4);
		}
		if (!mdxPath.toLowerCase().endsWith(".mdl")) {
			mdxPath += ".mdl";
		}
		return mdxPath;
	}

	public String blp(String iconPath) {
		final int lastDotIndex = iconPath.lastIndexOf('.');
		if (lastDotIndex != -1) {
			iconPath = iconPath.substring(0, lastDotIndex);
		}
		if (!iconPath.toLowerCase().endsWith(".blp")) {
			iconPath += ".blp";
		}
		return iconPath;
	}

	@Override
	// 定义一个方法用于创建场景灯光管理器
	public SceneLightManager createLightManager(final boolean simple) {
		// 如果参数simple为true，则创建一个简单的场景灯光管理器
		if (simple) {
			return new W3xScenePortraitLightManager(this, this.lightDirection);
		}
		// 如果参数simple为false，则创建一个复杂的场景灯光管理器
		else {
			return new W3xSceneWorldLightManager(this);
		}
	}


	@Override
	public WorldEditStrings getWorldEditStrings() {
		return this.worldEditStrings;
	}

	public void setGameUI(final GameUI gameUI) {
		this.gameUI = gameUI;
		this.abilityDataUI = new AbilityDataUI(this.allObjectData, gameUI, this);
	}

	public GameUI getGameUI() {
		return this.gameUI;
	}

	public AbilityDataUI getAbilityDataUI() {
		return this.abilityDataUI;
	}

	public KeyedSounds getUiSounds() {
		return this.uiSounds;
	}

	public Warcraft3MapRuntimeObjectData getAllObjectData() {
		return this.allObjectData;
	}

	// 定义一个方法，用于获取指定坐标(x, y)处可行走区域的渲染高度
	public float getWalkableRenderHeight(final float x, final float y) {
		// 使用walkableObjectsTree的intersect方法来找出所有与点(x, y)相交的可行走对象
		// 这里将相交检测的结果存储在walkablesIntersector对象中
		this.walkableObjectsTree.intersect(x, y, this.walkablesIntersector.reset(x, y));
		// 返回相交检测结果中记录的最高z值，即渲染高度
		return this.walkablesIntersector.z;
	}


	public MdxComplexInstance getHighestWalkableUnder(final float x, final float y) {
		this.walkableObjectsTree.intersect(x, y, this.intersectorFindsHighestWalkable.reset(x, y));
		return this.intersectorFindsHighestWalkable.highestInstance;
	}

	public int getLocalPlayerIndex() {
		return this.localPlayerIndex;
	}

	public RenderUnit getRenderPeer(final CUnit unit) {
		return this.unitToRenderPeer.get(unit);
	}

	public RenderDestructable getRenderPeer(final CDestructable dest) {
		return this.destructableToRenderPeer.get(dest);
	}

	public RenderItem getRenderPeer(final CItem item) {
		return this.itemToRenderPeer.get(item);
	}

	/**
	 * 获取指定受损可破坏物的渲染对象
	 *
	 * @param damagedDestructable 受损的可破坏物
	 * @return 渲染对象，如果没有找到对应的渲染对象，则返回 null
	 */
	public RenderWidget getRenderPeer(final CWidget damagedDestructable) {
		// 尝试从 unitToRenderPeer 映射中获取受损可破坏物的渲染对象
		RenderWidget damagedWidget = War3MapViewer.this.unitToRenderPeer.get(damagedDestructable);
		// 如果没有找到，尝试从 destructableToRenderPeer 映射中获取
		if (damagedWidget == null) {
			damagedWidget = War3MapViewer.this.destructableToRenderPeer.get(damagedDestructable);
		}
		// 如果还是没有找到，尝试从 itemToRenderPeer 映射中获取
		if (damagedWidget == null) {
			damagedWidget = War3MapViewer.this.itemToRenderPeer.get(damagedDestructable);
		}
		// 返回最终找到的渲染对象，如果没有找到，则返回 null
		return damagedWidget;
	}


	private static final class QuadtreeIntersectorFindsWalkableRenderHeight
			implements QuadtreeIntersector<MdxComplexInstance> {
		private float z;
		private final Ray ray = new Ray();
		private final Vector3 intersection = new Vector3();

		private QuadtreeIntersectorFindsWalkableRenderHeight reset(final float x, final float y) {
			this.z = -Float.MAX_VALUE;
			this.ray.set(x, y, 4096, 0, 0, -8192);
			return this;
		}

		@Override
		public boolean onIntersect(final MdxComplexInstance intersectingObject) {
			if (intersectingObject.intersectRayWithCollision(this.ray, this.intersection, true, true)) {
				this.z = Math.max(this.z, this.intersection.z);
			}
			return false;
		}
	}

	private static final class QuadtreeIntersectorFindsHighestWalkable
			implements QuadtreeIntersector<MdxComplexInstance> {
		private float z;
		private final Ray ray = new Ray();
		private final Vector3 intersection = new Vector3();
		private MdxComplexInstance highestInstance;

		private QuadtreeIntersectorFindsHighestWalkable reset(final float x, final float y) {
			this.z = -Float.MAX_VALUE;
			this.ray.set(x, y, 4096, 0, 0, -8192);
			this.highestInstance = null;
			return this;
		}

		@Override
		public boolean onIntersect(final MdxComplexInstance intersectingObject) {
			if (intersectingObject.intersectRayWithCollision(this.ray, this.intersection, true, true)) {
				if (this.intersection.z > this.z) {
					this.z = this.intersection.z;
					this.highestInstance = intersectingObject;
				}
			}
			return false;
		}
	}

	private static final class QuadtreeIntersectorFindsHitPoint implements QuadtreeIntersector<MdxComplexInstance> {
		private Ray ray;
		private final Vector3 intersection = new Vector3();
		private boolean found;

		private QuadtreeIntersectorFindsHitPoint reset(final Ray ray) {
			this.ray = ray;
			this.found = false;
			return this;
		}

		@Override
		public boolean onIntersect(final MdxComplexInstance intersectingObject) {
			if (intersectingObject.intersectRayWithCollision(this.ray, this.intersection, true, true)) {
				this.found = true;
				return true;
			}
			return false;
		}
	}

	public void add(final TextTag textTag) {
		this.textTags.add(textTag);
	}

	public SettableCommandErrorListener getCommandErrorListener() {
		return this.commandErrorListener;
	}

	public War3MapConfig getMapConfig() {
		return this.mapConfig;
	}

	public void setLocalPlayerIndex(final int playerIndex) {
		// TODO this is HACKY to not do this on INIT, but it is a cheese way to try to
		// do the networking for now!!!
		this.localPlayerIndex = playerIndex;
	}

	public void setLocalPlayerServerSlot(final int localPlayerServerSlot) {
		this.localPlayerServerSlot = localPlayerServerSlot;
	}

	public void setGameTurnManager(final GameTurnManager gameTurnManager) {
		this.gameTurnManager = gameTurnManager;
	}

	/**
	 * 在指定单位上生成特效
	 *
	 * @param unit       要添加特效的单位
	 * @param alias      特效的别名
	 * @param effectType 特效类型
	 * @param index      特效索引
	 * @return 返回生成的特效对象，如果生成失败则返回 null
	 */
	public RenderSpellEffect spawnSpellEffectOnUnitEx(final CUnit unit, final War3ID alias,
													  final CEffectType effectType, final int index) {
		// 获取指定别名和类型的特效附件 UI
		final EffectAttachmentUI effectAttachmentUI = getEffectAttachmentUI(alias, effectType, index);
		// 如果特效附件 UI 不存在，则返回 null
		if (effectAttachmentUI == null) {
			return null;
		}
		// 获取特效的模型路径
		final String modelPath = effectAttachmentUI.getModelPath();
		// 获取特效的附件点列表
		final List<String> attachmentPoint = effectAttachmentUI.getAttachmentPoint();
		// 在单位上添加指定路径的特效
		final RenderSpellEffect specialEffect = addSpecialEffectTarget(modelPath, unit, attachmentPoint);
		// 返回生成的特效对象
		return specialEffect;
	}


	/**
	 * 在指定单位上生成特效
	 *
	 * @param unit       要添加特效的单位
	 * @param alias      特效的别名
	 * @param effectType 特效类型
	 * @return 返回生成的特效对象列表，如果生成失败则返回 null
	 */
	public List<RenderSpellEffect> spawnSpellEffectOnUnitEx(final CUnit unit, final War3ID alias,
															final CEffectType effectType) {
		// 获取指定别名和类型的特效附件 UI 列表
		final List<EffectAttachmentUI> effectAttachmentUI = getEffectAttachmentUIList(alias, effectType);
		// 如果特效附件 UI 列表不存在，则返回 null
		if (effectAttachmentUI == null) {
			return null;
		}
		// 创建一个新的列表来存储生成的特效对象
		final List<RenderSpellEffect> renderEffects = new ArrayList<>();
		// 遍历特效附件 UI 列表
		for (final EffectAttachmentUI effect : effectAttachmentUI) {
			// 获取特效的模型路径
			final String modelPath = effect.getModelPath();
			// 获取特效的附件点列表
			final List<String> attachmentPoint = effect.getAttachmentPoint();
			// 在单位上添加指定路径的特效
			final RenderSpellEffect specialEffect = addSpecialEffectTarget(modelPath, unit, attachmentPoint);
			// 如果特效生成成功，则将其添加到列表中
			if (specialEffect != null) {
				renderEffects.add(specialEffect);
			}
		}
		// 返回生成的特效对象列表
		return renderEffects;
	}


	/**
	 * 在指定的点上生成一个法术效果，并返回一个 RenderSpellEffect 对象，用于控制和操作生成的法术效果
	 *
	 * @param x          法术效果的 X 坐标
	 * @param y          法术效果的 Y 坐标
	 * @param facing     法术效果的朝向
	 * @param alias      法术效果的别名
	 * @param effectType 法术效果的类型
	 * @param index      法术效果的索引
	 * @return 返回一个 RenderSpellEffect 对象，用于控制和操作生成的法术效果。如果生成失败，则返回 null
	 */
	public RenderSpellEffect spawnSpellEffectEx(final float x, final float y, final float facing, final War3ID alias,
												final CEffectType effectType, final int index) {
		// 获取指定别名和类型的法术效果的附件 UI 信息
		final EffectAttachmentUI effectAttachmentUI = getEffectAttachmentUI(alias, effectType, index);
		// 如果没有找到对应的附件 UI 信息，则返回 null
		if (effectAttachmentUI == null) {
			return null;
		}
		// 获取法术效果的模型路径
		final String modelPath = effectAttachmentUI.getModelPath();
		// 获取法术效果的附件点列表
		final List<String> attachmentPoint = effectAttachmentUI.getAttachmentPoint();
		// 在指定位置生成一个法术效果，并设置其朝向
		final RenderSpellEffect specialEffect = addSpecialEffect(modelPath, x, y, (float) StrictMath.toRadians(facing));
		// 返回生成的法术效果对象
		return specialEffect;
	}


	public List<EffectAttachmentUI> getEffectAttachmentUIList(final War3ID alias, final CEffectType effectType) {
		final AbilityUI abilityUI = War3MapViewer.this.abilityDataUI.getUI(alias);
		List<EffectAttachmentUI> effectAttachmentUI = null;
		if (abilityUI != null) {
			switch (effectType) {
			case EFFECT:
				effectAttachmentUI = abilityUI.getEffectArt();
				break;
			case TARGET:
				effectAttachmentUI = abilityUI.getTargetArt();
				break;
			case CASTER:
				effectAttachmentUI = abilityUI.getCasterArt();
				break;
			case SPECIAL:
				effectAttachmentUI = abilityUI.getSpecialArt();
				break;
			case AREA_EFFECT:
				effectAttachmentUI = abilityUI.getAreaEffectArt();
				break;
			case MISSILE:
				effectAttachmentUI = new ArrayList<>(abilityUI.getMissileArt());
				break;
			default:
				throw new IllegalArgumentException("Unsupported effect type: " + effectType);
			}
		}
		else {
			final BuffUI buffUI = War3MapViewer.this.abilityDataUI.getBuffUI(alias);
			if (buffUI != null) {
				switch (effectType) {
				case EFFECT:
					effectAttachmentUI = buffUI.getEffectArt();
					break;
				case TARGET:
					effectAttachmentUI = buffUI.getTargetArt();
					break;
				case SPECIAL:
					effectAttachmentUI = buffUI.getSpecialArt();
					break;
				case MISSILE:
					effectAttachmentUI = buffUI.getMissileArt();
					break;
				default:
					throw new IllegalArgumentException("Unsupported effect type: " + effectType);
				}
			}
			else {
				return null;
			}
		}
		return effectAttachmentUI;
	}

	/**
	 * 获取指定别名和类型的法术效果的附件 UI 信息
	 *
	 * @param alias      法术效果的别名
	 * @param effectType 法术效果的类型
	 * @param index      法术效果的索引
	 * @return 返回一个 EffectAttachmentUI 对象，用于控制和操作生成的法术效果。如果获取失败，则返回 null
	 */
	public EffectAttachmentUI getEffectAttachmentUI(final War3ID alias, final CEffectType effectType, final int index) {
		// 获取指定别名的能力 UI 信息
		final AbilityUI abilityUI = War3MapViewer.this.abilityDataUI.getUI(alias);
		EffectAttachmentUI effectAttachmentUI = null;
		// 如果能力 UI 信息存在
		if (abilityUI != null) {
			// 根据法术效果的类型获取相应的附件 UI 信息
			switch (effectType) {
				case EFFECT:
					// 获取效果艺术的附件 UI 信息
					effectAttachmentUI = abilityUI.getEffectArt(index);
					break;
				case TARGET:
					// 获取目标艺术的附件 UI 信息
					effectAttachmentUI = abilityUI.getTargetArt(index);
					break;
				case CASTER:
					// 获取施法者艺术的附件 UI 信息
					effectAttachmentUI = abilityUI.getCasterArt(index);
					break;
				case SPECIAL:
					// 获取特殊艺术的附件 UI 信息
					effectAttachmentUI = abilityUI.getSpecialArt(index);
					break;
				case AREA_EFFECT:
					// 获取区域效果艺术的附件 UI 信息
					effectAttachmentUI = abilityUI.getAreaEffectArt(index);
					break;
				case MISSILE:
					// 获取导弹艺术的附件 UI 信息
					effectAttachmentUI = abilityUI.getMissileArt(index);
					break;
				default:
					// 如果法术效果的类型不支持，则抛出异常
					throw new IllegalArgumentException("Unsupported effect type: " + effectType);
			}
		}
		// 如果能力 UI 信息不存在
		else {
			// 获取指定别名的 BUFF UI 信息
			final BuffUI buffUI = War3MapViewer.this.abilityDataUI.getBuffUI(alias);
			// 如果 BUFF UI 信息存在
			if (buffUI != null) {
				// 根据法术效果的类型获取相应的附件 UI 信息
				switch (effectType) {
					case EFFECT:
						// 获取效果艺术的附件 UI 信息
						effectAttachmentUI = buffUI.getEffectArt(index);
						break;
					case TARGET:
						// 获取目标艺术的附件 UI 信息
						effectAttachmentUI = buffUI.getTargetArt(index);
						break;
					case SPECIAL:
						// 获取特殊艺术的附件 UI 信息
						effectAttachmentUI = buffUI.getSpecialArt(index);
						break;
					case MISSILE:
						// 获取导弹艺术的附件 UI 信息
						effectAttachmentUI = buffUI.getMissileArt(index);
						break;
					default:
						// 如果法术效果的类型不支持，则抛出异常
						throw new IllegalArgumentException("Unsupported effect type: " + effectType);
				}
			}
			// 如果 BUFF UI 信息不存在，则返回 null
			else {
				return null;
			}
		}
		// 返回获取到的附件 UI 信息
		return effectAttachmentUI;
	}


	/**
	 * 获取指定别名的能力 UI 信息中的闪电效果列表
	 *
	 * @param alias 能力的别名
	 * @return 闪电效果列表，如果能力 UI 信息不存在，则返回 null
	 */
	public List<War3ID> getLightningEffectList(final War3ID alias) {
		// 获取指定别名的能力 UI 信息
		final AbilityUI abilityUI = War3MapViewer.this.abilityDataUI.getUI(alias);
		// 如果能力 UI 信息存在，则返回闪电效果列表
		return abilityUI.getLightningEffects();
	}

	/**
	 * 在指定目标小部件上添加一个特殊效果
	 *
	 * @param modelName       模型名称，用于指定要添加的特殊效果的模型
	 * @param targetWidget    目标小部件，可以是单位、物品或可破坏物
	 * @param attachPointName 附件点名称，用于指定特殊效果在目标小部件上的附着位置
	 * @return 返回添加的特殊效果对象，如果添加失败则返回 null
	 */
	public RenderSpellEffect addSpecialEffectTarget(final String modelName, final CWidget targetWidget,
													final String attachPointName) {
		// 将附件点名称分割成字符串列表，并调用另一个重载的 addSpecialEffectTarget 方法
		return addSpecialEffectTarget(modelName, targetWidget, Arrays.asList(attachPointName.split("\\s+")));
	}

	/**
	 * 在指定目标小部件上添加一个特殊效果
	 *
	 * @param modelName        模型名称，用于指定要添加的特殊效果的模型
	 * @param targetWidget     目标小部件，可以是单位、物品或可破坏物
	 * @param attachPointNames 附件点名称列表，用于指定特殊效果在目标小部件上的附着位置
	 * @return 返回添加的特殊效果对象，如果添加失败则返回 null
	 */
	public RenderSpellEffect addSpecialEffectTarget(final String modelName, final CWidget targetWidget,
													List<String> attachPointNames) {
		// 如果目标小部件是一个单位
		if (targetWidget instanceof CUnit) {
			// 如果附件点名称列表为空或只有一个空字符串，则将其设置为默认的原点字符串列表
			if (attachPointNames.isEmpty() || ((attachPointNames.size() == 1) && attachPointNames.get(0).isEmpty())) {
				attachPointNames = ORIGIN_STRING_LIST;
			}
			// 获取目标单位的渲染单元
			final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer.get(targetWidget);
			// 如果渲染单元为空，则抛出一个空指针异常
			if (renderUnit == null) {
				final NullPointerException nullPointerException = new NullPointerException(
						"renderUnit is null! targetWidget is \"" + ((CUnit) targetWidget).getUnitType().getName()
								+ "\", attachPointName=\"" + attachPointNames + "\"");
				// 如果启用了调试模式，则抛出异常
				if (WarsmashConstants.ENABLE_DEBUG) {
					throw nullPointerException;
				}
				// 否则，打印异常堆栈跟踪
				else {
					nullPointerException.printStackTrace();
				}
			}
			// 加载指定名称的模型
			final MdxModel spawnedEffectModel = loadModelMdx(modelName);
			// 如果模型加载成功
			if (spawnedEffectModel != null) {
				// 为加载的模型创建一个复杂实例
				final MdxComplexInstance modelInstance = (MdxComplexInstance) spawnedEffectModel.addInstance();
				float yaw = 0;
				// 如果渲染单元不为空
				if (renderUnit != null) {
					// 设置模型实例的团队颜色
					modelInstance.setTeamColor(renderUnit.playerIndex);
					{
						// 获取渲染单元的模型
						final MdxModel model = (MdxModel) renderUnit.instance.model;
						int index = -1;
						int bestFitAttachmentNameLength = Integer.MAX_VALUE;
						// 遍历模型的附件列表
						for (int i = 0; i < model.attachments.size(); i++) {
							final Attachment attachment = model.attachments.get(i);
							boolean match = true;
							// 遍历附件点名称列表
							for (final String attachmentPointNameToken : attachPointNames) {
								// 如果附件名称不包含附件点名称，则不匹配
								if (!attachment.getName().contains(attachmentPointNameToken)) {
									match = false;
								}
							}
							// 获取附件名称的长度
							final int attachmentNameLength = attachment.getName().length();
							// 如果匹配且附件名称长度小于最佳匹配附件名称长度，则更新索引和最佳匹配长度
							if (match && (attachmentNameLength < bestFitAttachmentNameLength)) {
								index = i;
								bestFitAttachmentNameLength = attachmentNameLength;
							}
						}
						// 如果找到了匹配的附件
						if (index != -1) {
							// 分离模型实例
							modelInstance.detach();
							// 获取渲染单元的附件
							final MdxNode attachment = renderUnit.instance.getAttachment(index);
							// 设置模型实例的父节点为附件
							modelInstance.setParent(attachment);
							// 设置模型实例的位置为原点
							modelInstance.setLocation(0, 0, 0);
						}
						// 如果没有找到匹配的附件
						else {
							// TODO This is not consistent with War3, is it? Should look nice though.
							// 设置模型实例的位置为渲染单元的位置
							modelInstance.setLocation(renderUnit.location);
							// 获取渲染单元的朝向并转换为弧度
							yaw = (float) Math.toRadians(renderUnit.getSimulationUnit().getFacing());
						}
					}
				}
				// 如果渲染单元为空
				else {
					// 设置模型实例的位置为原点
					modelInstance.setLocation(0, 0, 0);
				}
				// 将模型实例添加到世界场景中
				modelInstance.setScene(War3MapViewer.this.worldScene);
				// 创建一个新的 RenderSpellEffect 对象，用于控制和操作生成的特殊效果
				final EnumSet<SecondaryTag> requiredAnimationNamesForAttachments = renderUnit == null
						? SequenceUtils.EMPTY
						: renderUnit.getTypeData().getRequiredAnimationNamesForAttachments();
				final RenderSpellEffect renderAttackInstant = new RenderSpellEffect(modelInstance, War3MapViewer.this,
						yaw, RenderSpellEffect.DEFAULT_ANIMATION_QUEUE, requiredAnimationNamesForAttachments);
				// 将特殊效果添加到项目列表中
				War3MapViewer.this.projectiles.add(renderAttackInstant);
				// 返回添加的特殊效果对象
				return renderAttackInstant;
			}
		}
		// 如果目标小部件是一个物品
		else if (targetWidget instanceof CItem) {
			// TODO this is stupid api, who would do this?
			// 抛出一个不支持的操作异常
			throw new UnsupportedOperationException("API for addSpecialEffectTarget() on item is NYI");
		}
		// 如果目标小部件是一个可破坏物
		else if (targetWidget instanceof CDestructable) {
			// TODO this is stupid api, who would do this?
			// 抛出一个不支持的操作异常
			throw new UnsupportedOperationException("API for addSpecialEffectTarget() on destructable is NYI");
		}
		// 如果目标小部件不是上述类型，则返回 null
		return null;
	}


	/**
	 * 在指定的位置生成一个特殊效果，并返回一个 RenderSpellEffect 对象，用于控制和操作生成的特殊效果
	 *
	 * @param modelName 特殊效果的模型名称
	 * @param x         特殊效果的 X 坐标
	 * @param y         特殊效果的 Y 坐标
	 * @param yaw       特殊效果的偏航角
	 * @return 返回一个 RenderSpellEffect 对象，用于控制和操作生成的特殊效果。如果生成失败，则返回 null
	 */
	public RenderSpellEffect addSpecialEffect(final String modelName, final float x, final float y, final float yaw) {
		// 加载指定名称的模型
		final MdxModel spawnedEffectModel = loadModelMdx(modelName);
		// 如果模型加载成功
		if (spawnedEffectModel != null) {
			// 为加载的模型创建一个复杂实例
			final MdxComplexInstance modelInstance = (MdxComplexInstance) spawnedEffectModel.addInstance();
			{
				// 设置模型实例的位置，确保其位于可行走的渲染高度或地面高度之上
				modelInstance.setLocation(x, y,
						Math.max(getWalkableRenderHeight(x, y), this.terrain.getGroundHeight(x, y)));
			}
			// 将模型实例添加到世界场景中
			modelInstance.setScene(War3MapViewer.this.worldScene);
			// 创建一个新的 RenderSpellEffect 对象，用于控制和操作生成的特殊效果
			final RenderSpellEffect renderAttackInstant = new RenderSpellEffect(modelInstance, War3MapViewer.this, yaw,
					RenderSpellEffect.DEFAULT_ANIMATION_QUEUE, SequenceUtils.EMPTY);
			// 将新创建的特殊效果添加到 projectiles 列表中
			War3MapViewer.this.projectiles.add(renderAttackInstant);
			// 返回新创建的特殊效果对象
			return renderAttackInstant;
		}
		// 如果模型加载失败，则返回 null
		return null;
	}


	public MdxModel loadModelMdx(final String path) {
		return loadModelMdx(this.dataSource, this, path, this.mapPathSolver, this.solverParams);
	}

	public static MdxModel loadModelMdx(final DataSource dataSource, final ModelViewer modelViewer, final String path,
			final PathSolver pathSolver, final Object solverParams) {
		if ("".equals(path)) {
			return null;
		}
		final String mdxPath = mdx(path);
		if (dataSource.has(mdxPath)) {
			return (MdxModel) modelViewer.load(mdxPath, pathSolver, solverParams);
		}
		else {
			final String mdlPath = mdl(mdxPath);
			if (dataSource.has(mdlPath)) {
				return (MdxModel) modelViewer.load(mdlPath, pathSolver, solverParams);
			}
		}
		return (MdxModel) modelViewer.load(mdxPath, pathSolver, solverParams);
	}

	// 设置指定位置及其周围区域的枯萎状态
	public void setBlight(float whichLocationX, float whichLocationY, final float radius, final boolean blighted) {
		// 将世界坐标转换为地形网格单元坐标
		final int cellX = this.terrain.get128CellX(whichLocationX);
		final int cellY = this.terrain.get128CellY(whichLocationY);
		// 将网格单元坐标转换回世界坐标
		whichLocationX = this.terrain.get128WorldCoordinateFromCellX(cellX);
		whichLocationY = this.terrain.get128WorldCoordinateFromCellY(cellY);
		// 创建一个以指定位置为中心，半径为边长的矩形区域
		final Rectangle blightRectangle = new Rectangle(whichLocationX - radius, whichLocationY - radius, radius * 2,
				radius * 2);
		// 计算矩形区域的右下角坐标
		final float blightRectangleMaxX = blightRectangle.x + blightRectangle.width;
		final float blightRectangleMaxY = blightRectangle.y + blightRectangle.height;
		// 计算半径的平方，用于后续的距离比较
		final float rSquared = radius * radius;
		// 标记数据是否发生变化
		boolean changedData = false;
		// 遍历矩形区域内的每个地形网格单元
		for (float x = blightRectangle.x; x < blightRectangleMaxX; x += 128.0f) {
			for (float y = blightRectangle.y; y < blightRectangleMaxY; y += 128.0f) {
				// 计算当前位置与中心点的距离的平方
				final float dx = x - whichLocationX;
				final float dy = y - whichLocationY;
				final float distSquared = (dx * dx) + (dy * dy);
				// 如果距离小于等于半径，则处理该区域
				if (distSquared <= rSquared) {
					// 获取当前位置的地形角点信息
					final RenderCorner corner = this.terrain.getCorner(x, y);
					// 获取当前位置的地表纹理
					final GroundTexture currentTex = this.terrain.groundTextures.get(corner.getGroundTexture());
					// 如果角点不为空且地表纹理可建造，则设置枯萎状态
					if ((corner != null) && currentTex.isBuildable()) {
						changedData |= corner.setBlight(blighted);
					} else {
						continue;
					}
					// 遍历当前位置周围的路径网格单元
					for (float pathX = -64; pathX < 64; pathX += 32f) {
						for (float pathY = -64; pathY < 64; pathY += 32f) {
							// 计算路径网格单元的中心点坐标
							final float blightX = x + pathX + 16;
							final float blightY = y + pathY + 16;
							// 如果路径网格单元在路径网格中，则设置其枯萎状态
							if (this.simulation.getPathingGrid().contains(blightX, blightY)) {
								this.simulation.getPathingGrid().setBlighted(blightX, blightY, blighted);
							}
						}
					}
				}
			}
		}
		// 如果数据发生变化，则更新地形纹理
		if (changedData) {
			final int cellMinX = this.terrain.get128CellX(blightRectangle.x);
			final int cellMinY = this.terrain.get128CellY(blightRectangle.y);
			final int cellMaxX = this.terrain.get128CellX(blightRectangleMaxX);
			final int cellMaxY = this.terrain.get128CellY(blightRectangleMaxY);
			// 创建一个以网格单元坐标为单位的矩形区域
			final Rectangle blightRectangleCellUnits = this.tempBlightRect.set(cellMinX, cellMinY, cellMaxX - cellMinX,
					cellMaxY - cellMinY);
			// 更新地形纹理
			this.terrain.updateGroundTextures(blightRectangleCellUnits);
		}

		// 如果设置为枯萎状态，则处理世界碰撞中的可破坏物
		if (blighted) {
			this.simulation.getWorldCollision().enumDestructablesInRect(blightRectangle,
					TreeBlightingCallback.INSTANCE.reset(this.simulation));
		}
	}


	public CPlayerFogOfWar getFogOfWar() {
		return this.simulation.getPlayer(this.localPlayerIndex).getFogOfWar();
	}

	/**
	 * 根据前一个雾的颜色状态和当前状态，计算出一个新的颜色值，用于实现视线颜色的渐变效果。
	 *
	 * @param lastFogStateColor 前一个雾的颜色状态，byte类型
	 * @param state             当前的颜色状态，byte类型
	 * @return 计算后的新颜色值，byte类型
	 */
	public static byte fadeLineOfSightColor(final byte lastFogStateColor, final byte state) {
		// 将lastFogStateColor转换为无符号的short类型，以便进行计算
		final short prevValue = (short) (lastFogStateColor & 0xFF);
		// 将state转换为无符号的short类型，以便进行计算
		final short newValue = (short) (state & 0xFF);
		// 计算新旧颜色值的差值
		final short delta = (short) (newValue - prevValue);
		// 计算应用的变化幅度，最大值为9
		final short appliedMagnitude = (short) Math.min(9, Math.abs(delta));

		// 根据差值的正负，决定是增加还是减少颜色值，并确保结果在byte范围内
		return (byte) ((prevValue + (appliedMagnitude * (delta < 0 ? -1 : 1))) & 0xFF);
	}


	public final class MapLoader {
		private final LinkedList<LoadMapTask> loadMapTasks = new LinkedList<>();
		private int startingTaskCount;

		private char tileset;
		private DataSource tilesetSource;

		private War3MapW3e terrainData;

		private War3MapWpm terrainPathing;

		private MapLoader(final War3Map war3Map, final War3MapW3i w3iFile, final int localPlayerIndex) {
			final PathSolver wc3PathSolver = War3MapViewer.this.wc3PathSolver;

			this.loadMapTasks.add(() -> {
				this.tileset = 'A';
				this.tileset = w3iFile.getTileset();

				try {
					// Slightly complex. Here's the theory:
					// 1.) Copy map into RAM
					// 2.) Setup a Data Source that will read assets
					// from either the map or the game, giving the map priority.
					SeekableByteChannel sbc;
					final CompoundDataSource compoundDataSource = war3Map.getCompoundDataSource();
					if (WarsmashConstants.FIX_FLAT_FILES_TILESET_LOADING) {
						this.tilesetSource = new CompoundDataSource(Arrays.asList(compoundDataSource,
								new SubdirDataSource(compoundDataSource, this.tileset + ".mpq/")));
					}
					else {
						try (InputStream mapStream = compoundDataSource.getResourceAsStream(this.tileset + ".mpq")) {
							if (mapStream == null) {
								this.tilesetSource = new CompoundDataSource(Arrays.asList(compoundDataSource,
										new SubdirDataSource(compoundDataSource, this.tileset + ".mpq/"),
										new SubdirDataSource(compoundDataSource,
												"_tilesets/" + this.tileset + ".w3mod/")));
							}
							else {
								final byte[] mapData = IOUtils.toByteArray(mapStream);
								sbc = new SeekableInMemoryByteChannel(mapData);
								final DataSource internalMpqContentsDataSource = new MpqDataSource(new MPQArchive(sbc),
										sbc);
								this.tilesetSource = new CompoundDataSource(
										Arrays.asList(compoundDataSource, internalMpqContentsDataSource));
							}
						}
						catch (final IOException exc) {
							this.tilesetSource = new CompoundDataSource(Arrays.asList(compoundDataSource,
									new SubdirDataSource(compoundDataSource, this.tileset + ".mpq/"),
									new SubdirDataSource(compoundDataSource, "_tilesets/" + this.tileset + ".w3mod/")));
						}
					}
				}
				catch (final MPQException e) {
					throw new RuntimeException(e);
				}
			});

			this.loadMapTasks.add(() -> {
				setDataSource(this.tilesetSource);
			});
			this.loadMapTasks.add(() -> {
				War3MapViewer.this.worldEditStrings = new WorldEditStrings(War3MapViewer.this.dataSource);
			});
			this.loadMapTasks.add(() -> {
				loadSLKs(War3MapViewer.this.worldEditStrings);
			});

			this.loadMapTasks.add(() -> {
				War3MapViewer.this.solverParams.tileset = Character.toLowerCase(this.tileset);
			});

			this.loadMapTasks.add(() -> {
				this.terrainData = War3MapViewer.this.mapMpq.readEnvironment();
			});

			this.loadMapTasks.add(() -> {
				this.terrainPathing = War3MapViewer.this.mapMpq.readPathing();
			});

			this.loadMapTasks.add(() -> {
				War3MapViewer.this.terrain = new Terrain(this.terrainData, this.terrainPathing, w3iFile,
						War3MapViewer.this.webGL, War3MapViewer.this.dataSource, War3MapViewer.this.worldEditStrings,
						War3MapViewer.this, War3MapViewer.this.worldEditData);
			});

			this.loadMapTasks.add(() -> {
				final float[] centerOffset = this.terrainData.getCenterOffset();
				final int[] mapSize = this.terrainData.getMapSize();

				War3MapViewer.this.terrainReady = true;
				War3MapViewer.this.anyReady = true;
				War3MapViewer.this.cliffsReady = true;

				// Override the grid based on the map.
				War3MapViewer.this.worldScene.grid = new Grid(centerOffset[0], centerOffset[1],
						(mapSize[0] * 128) - 128, (mapSize[1] * 128) - 128, 16 * 128, 16 * 128);
			});

			this.loadMapTasks.add(() -> {
				final MdxModel confirmation = (MdxModel) load("UI\\Feedback\\Confirmation\\Confirmation.mdx",
						PathSolver.DEFAULT, null);
				War3MapViewer.this.confirmationInstance = (MdxComplexInstance) confirmation.addInstance();
				War3MapViewer.this.confirmationInstance
						.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP_AND_HIDE_WHEN_DONE);
				War3MapViewer.this.confirmationInstance.setSequence(0);
				War3MapViewer.this.confirmationInstance.setScene(War3MapViewer.this.worldScene);
			});

			this.loadMapTasks.add(() -> {
				if (War3MapViewer.this.preloadedWTS != null) {
					War3MapViewer.this.allObjectData = War3MapViewer.this.mapMpq
							.readModifications(War3MapViewer.this.preloadedWTS);
				}
				else {
					War3MapViewer.this.allObjectData = War3MapViewer.this.mapMpq.readModifications();
				}
			});
			this.loadMapTasks.add(() -> {
				War3MapViewer.this.simulation = new CSimulation(War3MapViewer.this.mapConfig, w3iFile.getVersion(),
						War3MapViewer.this.miscData, War3MapViewer.this.allObjectData.getUnits(),
						War3MapViewer.this.allObjectData.getItems(),
						War3MapViewer.this.allObjectData.getDestructibles(),
						War3MapViewer.this.allObjectData.getAbilities(), War3MapViewer.this.allObjectData.getUpgrades(),
						War3MapViewer.this.allObjectData.getStandardUpgradeEffectMeta(),
						new SimulationRenderController() {
							private final Map<String, UnitSound> keyToCombatSound = new HashMap<>();

							@Override
							/**
							 * 创建一个攻击弹道物体，用于模拟单位的攻击行为
							 *
							 * @param simulation 模拟对象，包含了游戏世界的状态和逻辑
							 * @param launchX 发射点的X坐标
							 * @param launchY 发射点的Y坐标
							 * @param launchFacing 发射方向
							 * @param source 攻击的源单位
							 * @param unitAttack 攻击的具体信息，如弹道速度、弧度等
							 * @param target 攻击的目标
							 * @param damage 攻击造成的伤害值
							 * @param bounceIndex 反弹索引，用于确定弹道物体的反弹次数
							 * @param attackListener 攻击监听器，用于处理攻击事件
							 * @return 创建的攻击弹道物体实例
							 */
							public CAttackProjectile createAttackProjectile(final CSimulation simulation,
																			final float launchX, final float launchY, final float launchFacing,
																			final CUnit source, final CUnitAttackMissile unitAttack, final AbilityTarget target,
																			final float damage, final int bounceIndex,
																			final CUnitAttackListener attackListener) {

								// 获取源单位的类型ID
								final War3ID typeId = source.getTypeId();
								// 获取弹道物体的速度
								final int projectileSpeed = unitAttack.getProjectileSpeed();
								// 获取弹道物体的弧度
								final float projectileArc = unitAttack.getProjectileArc();
								// 获取弹道物体的模型路径
								final String missileArt = unitAttack.getProjectileArt();
								// 获取弹道物体发射时的X坐标偏移
								final float projectileLaunchX = simulation.getUnitData().getProjectileLaunchX(typeId);
								// 获取弹道物体发射时的Y坐标偏移
								final float projectileLaunchY = simulation.getUnitData().getProjectileLaunchY(typeId);
								// 获取弹道物体发射时的Z坐标偏移
								final float projectileLaunchZ = simulation.getUnitData().getProjectileLaunchZ(typeId);

								// 计算弹道物体的朝向
								final float facing = launchFacing;
								final float sinFacing = (float) Math.sin(facing);
								final float cosFacing = (float) Math.cos(facing);
								// 计算弹道物体的实际发射位置
								final float x = launchX + (projectileLaunchY * cosFacing)
										+ (projectileLaunchX * sinFacing);
								final float y = (launchY + (projectileLaunchY * sinFacing))
										- (projectileLaunchX * cosFacing);

								// 计算弹道物体的发射高度
								final float height = War3MapViewer.this.terrain.getGroundHeight(x, y)
										+ source.getFlyHeight() + projectileLaunchZ;
								// 创建模拟攻击弹道物体
								final CAttackProjectile simulationAttackProjectile = new CAttackProjectile(x, y,
										projectileSpeed, target, source, damage, unitAttack, bounceIndex,
										attackListener);

								// 加载弹道物体的模型
								final MdxModel model = loadModelMdx(missileArt);
								if (model != null) {
									// 创建模型的实例
									final MdxComplexInstance modelInstance = (MdxComplexInstance) model.addInstance();
									// 设置模型的队伍颜色
									modelInstance.setTeamColor(getRenderPeer(source).playerIndex);
									// 将模型实例添加到场景中
									modelInstance.setScene(War3MapViewer.this.worldScene);
									// 根据弹道物体的反弹索引设置模型的动画序列
									if (bounceIndex == 0) {
										SequenceUtils.randomBirthSequence(modelInstance);
									} else {
										SequenceUtils.randomStandSequence(modelInstance);
									}
									// 设置模型实例的位置
									modelInstance.setLocation(x, y, height);
									// 创建渲染攻击弹道物体
									final RenderProjectile renderAttackProjectile = new RenderProjectile(
											simulationAttackProjectile, modelInstance, height, projectileArc,
											War3MapViewer.this);

									// 将渲染攻击弹道物体添加到弹道物体列表中
									War3MapViewer.this.projectiles.add(renderAttackProjectile);
								}

								// 返回模拟攻击弹道物体
								return simulationAttackProjectile;
							}


							@Override
							/**
							 * 创建一个投射物，用于模拟单位的攻击行为
							 *
							 * @param cSimulation 模拟对象，包含了游戏世界的状态和逻辑
							 * @param launchX 发射点的X坐标
							 * @param launchY 发射点的Y坐标
							 * @param launchFacing 发射方向
							 * @param projectileSpeed 投射物的速度
							 * @param homing 是否追踪目标
							 * @param source 攻击的源单位
							 * @param spellAlias 攻击的法术别名
							 * @param target 攻击的目标
							 * @param projectileListener 投射物监听器，用于处理投射物事件
							 * @return 创建的投射物实例
							 */
							public CAbilityProjectile createProjectile(final CSimulation cSimulation,
																	   final float launchX, final float launchY, final float launchFacing,
																	   final float projectileSpeed, final boolean homing, final CUnit source,
																	   final War3ID spellAlias, final AbilityTarget target,
																	   final CAbilityProjectileListener projectileListener) {

								// 获取源对象的类型ID
								final War3ID typeId = source.getTypeId();

								// 获取与法术别名对应的UI数据
								final AbilityUI spellDataUI = War3MapViewer.this.abilityDataUI.getUI(spellAlias);

								// 获取法术的导弹艺术资源，如果没有则默认为空
								final EffectAttachmentUIMissile abilityMissileArt = spellDataUI.getMissileArt(0);

								// 获取导弹模型的路径，如果没有导弹艺术资源则默认为空字符串
								final String modelPath = abilityMissileArt == null ? "" : abilityMissileArt.getModelPath();

								// 获取导弹的抛物线弧度，如果没有导弹艺术资源则默认为0
								final float projectileArc = abilityMissileArt == null ? 0 : abilityMissileArt.getArc();

								// 再次获取导弹模型的路径，如果没有导弹艺术资源则默认为空字符串
								// 这里似乎与modelPath重复，可能是代码冗余或错误
								final String missileArt = abilityMissileArt == null ? "" : abilityMissileArt.getModelPath();

								// 获取发射点的X坐标
								final float projectileLaunchX = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchX(typeId);

								// 获取发射点的Y坐标
								final float projectileLaunchY = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchY(typeId);

								// 获取发射点的Z坐标
								final float projectileLaunchZ = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchZ(typeId);

								// 获取发射方向的角度
								final float facing = launchFacing;

								// 计算发射方向的正弦值
								final float sinFacing = (float) Math.sin(facing);

								// 计算发射方向的余弦值
								final float cosFacing = (float) Math.cos(facing);

								// 根据发射方向和发射点的坐标计算导弹的初始X坐标
								final float x = launchX + (projectileLaunchY * cosFacing)
										+ (projectileLaunchX * sinFacing);

								// 根据发射方向和发射点的坐标计算导弹的初始Y坐标
								final float y = (launchY + (projectileLaunchY * sinFacing))
										- (projectileLaunchX * cosFacing);

								// 计算导弹的初始高度，包括地形高度和源对象的飞行高度
								final float height = War3MapViewer.this.terrain.getGroundHeight(x, y)
										+ source.getFlyHeight() + projectileLaunchZ;

								// 创建一个模拟导弹能力的对象
								final CAbilityProjectile simulationAbilityProjectile = new CAbilityProjectile(x, y,
										projectileSpeed, target, homing, source, projectileListener);

								// 加载导弹模型
								final MdxModel model = loadModelMdx(missileArt);

								// 创建模型的实例
								final MdxComplexInstance modelInstance = (MdxComplexInstance) model.addInstance();

								// 获取源对象的渲染对等体
								final RenderUnit renderPeer = getRenderPeer(source);

								// 设置模型实例的队伍颜色
								modelInstance.setTeamColor(renderPeer.playerIndex);

								// 将模型实例添加到场景中
								modelInstance.setScene(War3MapViewer.this.worldScene);

								// 随机生成模型实例的出生序列
								SequenceUtils.randomBirthSequence(modelInstance);

								// 设置模型实例的位置
								modelInstance.setLocation(x, y, height);

								// 创建一个渲染导弹的对象
								final RenderProjectile renderProjectile = new RenderProjectile(
										simulationAbilityProjectile, modelInstance, height, projectileArc,
										War3MapViewer.this);

								// 将渲染导弹对象添加到项目列表中
								War3MapViewer.this.projectiles.add(renderProjectile);

								// 返回模拟导弹能力的对象
								return simulationAbilityProjectile;

							}

							@Override
							/**
							 * 创建一个碰撞弹丸，用于模拟单位的攻击行为
							 *
							 * @param cSimulation 模拟对象，包含了游戏世界的状态和逻辑
							 * @param launchX 发射点的X坐标
							 * @param launchY 发射点的Y坐标
							 * @param launchFacing 发射方向
							 * @param projectileSpeed 投射物的速度
							 * @param homing 是否追踪目标
							 * @param source 攻击的源单位
							 * @param spellAlias 攻击的法术别名
							 * @param target 攻击的目标
							 * @param maxHits 最大命中次数
							 * @param hitsPerTarget 每次命中的目标数
							 * @param startingRadius 起始半径
							 * @param finalRadius 最终半径
							 * @param collisionInterval 碰撞间隔
							 * @param projectileListener 投射物监听器，用于处理投射物事件
							 * @param provideCounts 是否提供计数
							 * @return 创建的碰撞弹丸实例
							 */
							public CCollisionProjectile createCollisionProjectile(final CSimulation cSimulation,
																				  final float launchX, final float launchY, final float launchFacing,
																				  final float projectileSpeed, final boolean homing, final CUnit source,
																				  final War3ID spellAlias, final AbilityTarget target, final int maxHits,
																				  final int hitsPerTarget, final float startingRadius, final float finalRadius,
																				  final float collisionInterval,
																				  final CAbilityCollisionProjectileListener projectileListener,
																				  final boolean provideCounts) {

								// 获取发射单位的类型ID
								final War3ID typeId = source.getTypeId();
								// 获取技能对应的UI数据
								final AbilityUI spellDataUI = War3MapViewer.this.abilityDataUI.getUI(spellAlias);
								// 获取技能对应的导弹艺术效果
								final EffectAttachmentUIMissile abilityMissileArt = spellDataUI.getMissileArt(0);
								// 获取导弹的弧度
								final float projectileArc = abilityMissileArt == null ? 0 : abilityMissileArt.getArc();
								// 获取导弹模型的路径
								final String missileArt = abilityMissileArt == null ? ""
										: abilityMissileArt.getModelPath();
								// 获取单位发射弹丸时的X坐标偏移
								final float projectileLaunchX = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchX(typeId);
								// 获取单位发射弹丸时的Y坐标偏移
								final float projectileLaunchY = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchY(typeId);
								// 获取单位发射弹丸时的Z坐标偏移
								final float projectileLaunchZ = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchZ(typeId);

								// 计算弹丸的发射方向
								final float facing = launchFacing;
								final float sinFacing = (float) Math.sin(facing);
								final float cosFacing = (float) Math.cos(facing);
								// 计算弹丸的实际发射位置
								final float x = launchX + (projectileLaunchY * cosFacing)
										+ (projectileLaunchX * sinFacing);
								final float y = (launchY + (projectileLaunchY * sinFacing))
										- (projectileLaunchX * cosFacing);

								// 计算弹丸的实际发射高度
								final float height = War3MapViewer.this.terrain.getGroundHeight(x, y)
										+ source.getFlyHeight() + projectileLaunchZ;
								// 创建一个模拟的碰撞弹丸对象
								final CCollisionProjectile simulationAbilityProjectile = new CCollisionProjectile(x, y,
										projectileSpeed, target, homing, source, maxHits, hitsPerTarget, startingRadius,
										finalRadius, collisionInterval, projectileListener, provideCounts);

								// 加载导弹模型
								final MdxModel model = loadModelMdx(missileArt);
								// 创建模型的实例
								final MdxComplexInstance modelInstance = (MdxComplexInstance) model.addInstance();

								// 获取发射单位的渲染对象
								final RenderUnit renderPeer = getRenderPeer(source);
								// 设置模型实例的队伍颜色
								modelInstance.setTeamColor(renderPeer.playerIndex);
								// 将模型实例添加到场景中
								modelInstance.setScene(War3MapViewer.this.worldScene);
								// 随机生成模型实例的出生序列
								SequenceUtils.randomBirthSequence(modelInstance);
								// 设置模型实例的位置
								modelInstance.setLocation(x, y, height);
								// 创建一个渲染弹丸对象
								final RenderProjectile renderProjectile = new RenderProjectile(
										simulationAbilityProjectile, modelInstance, height, projectileArc,
										War3MapViewer.this);

								// 将渲染弹丸对象添加到弹丸列表中
								War3MapViewer.this.projectiles.add(renderProjectile);

								// 返回创建的碰撞弹丸对象
								return simulationAbilityProjectile;
							}


							@Override
							/**
							 * 创建一个伪投射物，用于模拟单位的攻击行为
							 *
							 * @param cSimulation 模拟对象，包含了游戏世界的状态和逻辑
							 * @param launchX 发射点的X坐标
							 * @param launchY 发射点的Y坐标
							 * @param launchFacing 发射方向
							 * @param projectileSpeed 投射物的速度
							 * @param projectileStepInterval 投射物的步长间隔
							 * @param projectileArtSkip 投射物的艺术跳过帧数
							 * @param homing 是否追踪目标
							 * @param source 攻击的源单位
							 * @param spellAlias 攻击的法术别名
							 * @param effectType 效果类型
							 * @param effectArtIndex 效果艺术索引
							 * @param target 攻击的目标
							 * @param maxHits 最大命中次数
							 * @param hitsPerTarget 每次命中的目标数
							 * @param startingRadius 起始半径
							 * @param finalRadius 最终半径
							 * @param projectileListener 投射物监听器，用于处理投射物事件
							 * @param provideCounts 是否提供计数
							 * @return 创建的伪投射物实例
							 */
							public CPsuedoProjectile createPseudoProjectile(final CSimulation cSimulation,
																			final float launchX, final float launchY, final float launchFacing,
																			final float projectileSpeed, final float projectileStepInterval,
																			final int projectileArtSkip, final boolean homing, final CUnit source,
																			final War3ID spellAlias, final CEffectType effectType, final int effectArtIndex,
																			final AbilityTarget target, final int maxHits, final int hitsPerTarget,
																			final float startingRadius, final float finalRadius,
																			final CAbilityCollisionProjectileListener projectileListener,
																			final boolean provideCounts) {


								// 获取发射单位的类型ID
								final War3ID typeId = source.getTypeId();
								// 获取该单位类型对应的弹道发射偏移X坐标
								final float projectileLaunchX = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchX(typeId);
								// 获取该单位类型对应的弹道发射偏移Y坐标
								final float projectileLaunchY = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchY(typeId);

								// 使用发射方向计算正弦和余弦值
								final float facing = launchFacing;
								final float sinFacing = (float) Math.sin(facing);
								final float cosFacing = (float) Math.cos(facing);
								// 计算弹道的实际发射位置
								final float x = launchX + (projectileLaunchY * cosFacing)
										+ (projectileLaunchX * sinFacing);
								final float y = (launchY + (projectileLaunchY * sinFacing))
										- (projectileLaunchX * cosFacing);

								// 创建并返回一个新的伪弹道物体实例
								final CPsuedoProjectile simulationAbilityProjectile = new CPsuedoProjectile(x, y,
										projectileSpeed, projectileStepInterval, projectileArtSkip, target, homing,
										source, spellAlias, effectType, effectArtIndex, maxHits, hitsPerTarget,
										startingRadius, finalRadius, projectileListener, provideCounts);

								return simulationAbilityProjectile;
							}


							@Override
							/**
							 * 在指定的点上生成一个即时攻击效果，并返回一个 RenderAttackInstant 对象，用于控制和操作生成的即时攻击效果
							 *
							 * @param cSimulation 模拟对象
							 * @param source 攻击源单位
							 * @param unitAttack 即时攻击对象
							 * @param target 目标单位
							 */
							public void createInstantAttackEffect(final CSimulation cSimulation, final CUnit source,
																  final CUnitAttackInstant unitAttack, final CWidget target) {
								// 获取攻击源单位的类型ID
								final War3ID typeId = source.getTypeId();

								// 获取攻击的导弹艺术名称
								final String missileArt = unitAttack.getProjectileArt();
								// 获取攻击源单位的弹道发射偏移X坐标
								final float projectileLaunchX = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchX(typeId);
								// 获取攻击源单位的弹道发射偏移Y坐标
								final float projectileLaunchY = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchY(typeId);
								// 获取攻击源单位的朝向（弧度制）
								final float facing = (float) Math.toRadians(source.getFacing());
								// 计算攻击源单位朝向的正弦值
								final float sinFacing = (float) Math.sin(facing);
								// 计算攻击源单位朝向的余弦值
								final float cosFacing = (float) Math.cos(facing);
								// 计算攻击源单位的X坐标
								final float x = source.getX() + (projectileLaunchY * cosFacing)
										+ (projectileLaunchX * sinFacing);
								// 计算攻击源单位的Y坐标
								final float y = (source.getY() + (projectileLaunchY * sinFacing))
										- (projectileLaunchX * cosFacing);

								// 获取目标单位的X坐标
								final float targetX = target.getX();
								// 获取目标单位的Y坐标
								final float targetY = target.getY();
								// 计算攻击源单位到目标单位的角度
								final float angleToTarget = (float) Math.atan2(targetY - y, targetX - x);

								// 计算目标单位的高度
								final float height = War3MapViewer.this.terrain.getGroundHeight(targetX, targetY)
										+ target.getFlyHeight() + target.getImpactZ();

								// 加载指定名称的模型
								final MdxModel model = loadModelMdx(missileArt);
								// 为加载的模型创建一个复杂实例
								final MdxComplexInstance modelInstance = (MdxComplexInstance) model.addInstance();
								// 设置模型实例的团队颜色
								modelInstance.setTeamColor(source.getPlayerIndex());
								// 随机播放模型的出生序列
								SequenceUtils.randomBirthSequence(modelInstance);
								// 设置模型实例的位置
								modelInstance.setLocation(targetX, targetY, height);
								// 将模型实例添加到世界场景中
								modelInstance.setScene(War3MapViewer.this.worldScene);
								// 创建一个新的 RenderAttackInstant 对象，用于控制和操作生成的即时攻击效果
								War3MapViewer.this.projectiles
										.add(new RenderAttackInstant(modelInstance, War3MapViewer.this, angleToTarget));
							}


							@Override
							/**
							 * 在指定的源单位和目标单位之间创建一个闪电效果，并返回一个 SimulationRenderComponentLightning 对象，用于控制和操作生成的闪电效果
							 *
							 * @param simulation 模拟对象
							 * @param lightningId 闪电效果的 ID
							 * @param source 源单位
							 * @param target 目标单位
							 * @return 返回一个 SimulationRenderComponentLightning 对象，用于控制和操作生成的闪电效果。如果创建失败，则返回 null
							 */
							public SimulationRenderComponentLightning createLightning(final CSimulation simulation,
																					  final War3ID lightningId, final CUnit source, final CUnit target) {
								// 获取源单位的渲染对象
								final RenderUnit renderPeerSource = War3MapViewer.this.getRenderPeer(source);
								// 获取目标单位的渲染对象
								final RenderWidget renderPeerTarget = War3MapViewer.this.getRenderPeer(target);
								// 创建并返回一个新的闪电效果对象
								return War3MapViewer.this.createLightning(lightningId, renderPeerSource, renderPeerTarget);
							}


							@Override
							/**
							 * 在指定的源单位和目标单位之间创建一个闪电效果，并返回一个 SimulationRenderComponentLightning 对象，用于控制和操作生成的闪电效果
							 *
							 * @param simulation 模拟对象
							 * @param lightningId 闪电效果的 ID
							 * @param source 源单位
							 * @param target 目标单位
							 * @param duration 闪电效果的持续时间
							 * @return 返回一个 SimulationRenderComponentLightning 对象，用于控制和操作生成的闪电效果。如果创建失败，则返回 null
							 */
							public SimulationRenderComponentLightning createLightning(final CSimulation simulation,
																					  final War3ID lightningId, final CUnit source, final CUnit target,
																					  final Float duration) {
								// 获取源单位的渲染对象
								final RenderUnit renderPeerSource = War3MapViewer.this.getRenderPeer(source);
								// 获取目标单位的渲染对象
								final RenderWidget renderPeerTarget = War3MapViewer.this.getRenderPeer(target);
								// 创建并返回一个新的闪电效果对象
								return War3MapViewer.this.createLightning(lightningId, renderPeerSource,
										renderPeerTarget, duration);
							}


							@Override
							/**
							 * 在指定的源单位和目标单位之间创建一个闪电效果，并返回一个 SimulationRenderComponentLightning 对象，用于控制和操作生成的闪电效果
							 *
							 * @param simulation 模拟对象
							 * @param lightningId 闪电效果的 ID
							 * @param source 源单位
							 * @param target 目标单位
							 * @param index 闪电效果列表中的索引
							 * @return 返回一个 SimulationRenderComponentLightning 对象，用于控制和操作生成的闪电效果。如果创建失败，则返回 null
							 */
							public SimulationRenderComponentLightning createAbilityLightning(
									final CSimulation simulation, final War3ID lightningId, final CUnit source, final CUnit target, final int index) {
								// 获取源单位的渲染对象
								final RenderUnit renderPeerSource = War3MapViewer.this.getRenderPeer(source);
								// 获取目标单位的渲染对象
								final RenderWidget renderPeerTarget = War3MapViewer.this.getRenderPeer(target);
								// 获取闪电效果列表
								final List<War3ID> lightnings = getLightningEffectList(lightningId);
								// 创建并返回一个新的闪电效果对象
								return War3MapViewer.this.createLightning(lightnings.get(index), renderPeerSource, renderPeerTarget);
							}


							@Override
							/**
							 * 在指定的源单位和目标单位之间创建一个闪电效果，并返回一个 SimulationRenderComponentLightning 对象，用于控制和操作生成的闪电效果
							 *
							 * @param simulation 模拟对象
							 * @param lightningId 闪电效果的 ID
							 * @param source 源单位
							 * @param target 目标单位
							 * @param index 闪电效果列表中的索引
							 * @param duration 闪电效果的持续时间
							 * @return 返回一个 SimulationRenderComponentLightning 对象，用于控制和操作生成的闪电效果。如果创建失败，则返回 null
							 */
							public SimulationRenderComponentLightning createAbilityLightning(
									final CSimulation simulation, final War3ID lightningId, final CUnit source, final CUnit target,
									final int index, final Float duration) {
								// 获取源单位的渲染对象
								final RenderUnit renderPeerSource = War3MapViewer.this.getRenderPeer(source);
								// 获取目标单位的渲染对象
								final RenderWidget renderPeerTarget = War3MapViewer.this.getRenderPeer(target);
								// 获取闪电效果列表
								final List<War3ID> lightnings = getLightningEffectList(lightningId);
								// 创建并返回一个新的闪电效果对象
								return War3MapViewer.this.createLightning(lightnings.get(index), renderPeerSource,
										renderPeerTarget, duration);
							}


							@Override
							/**
							 * 在指定的受损可破坏物上播放伤害声音
							 *
							 * @param damagedDestructable 受损的可破坏物
							 * @param weaponSound 武器声音
							 * @param armorType 护甲类型
							 */
							public void spawnDamageSound(final CWidget damagedDestructable, final String weaponSound,
														 final String armorType) {
								// 获取受损可破坏物的渲染对象
								final RenderWidget damagedWidget = War3MapViewer.this
										.getRenderPeer(damagedDestructable);
								// 如果受损可破坏物的渲染对象为空，则返回
								if (damagedWidget == null) {
									return;
								}
								// 创建一个键，用于从缓存中获取或存储战斗声音
								final String key = weaponSound + armorType;
								// 从缓存中获取战斗声音
								UnitSound combatSound = this.keyToCombatSound.get(key);
								// 如果战斗声音不存在，则创建一个新的战斗声音
								if (combatSound == null) {
									// 从数据源和单位战斗声音表中创建战斗声音
									combatSound = UnitSound.create(War3MapViewer.this.dataSource,
											War3MapViewer.this.unitCombatSoundsTable, weaponSound, armorType);
									// 将新创建的战斗声音存储在缓存中
									this.keyToCombatSound.put(key, combatSound);
								}
								// 在世界场景的音频上下文中播放战斗声音，并指定受损可破坏物的位置和高度
								combatSound.play(War3MapViewer.this.worldScene.audioContext, damagedDestructable.getX(),
										damagedDestructable.getY(), damagedWidget.getZ());
							}


							@Override
							/**
							 * 在指定的正在建造的单位和已建造的建筑之间播放建造声音
							 *
							 * @param constructingUnit 正在建造的单位
							 * @param constructedStructure 已建造的建筑
							 */
							public void spawnUnitConstructionSound(final CUnit constructingUnit,
																   final CUnit constructedStructure) {
								// 从 uiSounds 中获取建造建筑的声音
								final UnitSound constructingBuilding = War3MapViewer.this.uiSounds
										.getSound(War3MapViewer.this.gameUI.getSkinField("ConstructingBuilding"));
								// 如果声音不为空，则播放声音
								if (constructingBuilding != null) {
									// 在世界场景的音频上下文中播放建造建筑的声音，并指定已建造建筑的渲染对象
									constructingBuilding.playUnitResponse(War3MapViewer.this.worldScene.audioContext,
											War3MapViewer.this.unitToRenderPeer.get(constructedStructure));
								}
							}


							@Override
							/**
							 * 处理单位升级事件，当单位开始升级时，它会更新单位的动画属性
							 *
							 * @param unit 正在升级的单位
							 * @param upgradeIdType 升级的 ID 类型
							 */
							public void unitUpgradingEvent(final CUnit unit, final War3ID upgradeIdType) {
								// 从 allObjectData 的 units 中获取升级对象
								final GameObject upgrade = War3MapViewer.this.allObjectData.getUnits()
										.get(upgradeIdType);

								// 从 allObjectData 的 units 中获取单位的原始类型对象
								final GameObject originalUnit = War3MapViewer.this.allObjectData.getUnits()
										.get(unit.getTypeId());

								// 获取原始单位的动画属性字符串
								final String originalRequiredAnimationNames = originalUnit.getFieldAsString(RenderUnit.ANIM_PROPS, 0);

								// 遍历原始单位的动画属性字符串，移除所有与升级相关的次要标签
								TokenLoop:
								for (final String animationName : originalRequiredAnimationNames.split(",")) {
									final String upperCaseToken = animationName.toUpperCase();
									for (final SecondaryTag secondaryTag : SecondaryTag.values()) {
										if (upperCaseToken.equals(secondaryTag.name())) {
											unit.getUnitAnimationListener().removeSecondaryTag(secondaryTag);
											continue TokenLoop;
										}
									}
								}

								// 获取升级单位的动画属性字符串
								final String requiredAnimationNames = upgrade.getFieldAsString(RenderUnit.ANIM_PROPS, 0);

								// 遍历升级单位的动画属性字符串，添加所有与升级相关的次要标签
								TokenLoop:
								for (final String animationName : requiredAnimationNames.split(",")) {
									final String upperCaseToken = animationName.toUpperCase();
									for (final SecondaryTag secondaryTag : SecondaryTag.values()) {
										if (upperCaseToken.equals(secondaryTag.name())) {
											unit.getUnitAnimationListener().addSecondaryTag(secondaryTag);
											continue TokenLoop;
										}
									}
								}
							}


							@Override
							/**
							 * 处理单位升级取消事件，当单位取消升级时，它会更新单位的动画属性
							 *
							 * @param unit 正在取消升级的单位
							 * @param upgradeIdType 升级的 ID 类型
							 */
							public void unitCancelUpgradingEvent(final CUnit unit, final War3ID upgradeIdType) {
								// 从 allObjectData 的 units 中获取升级对象
								final GameObject upgrade = War3MapViewer.this.allObjectData.getUnits()
										.get(upgradeIdType);

								// TODO this should be behind some auto lookup so it isn't copied from
								// RenderUnit class:
								// 获取升级单位的动画属性字符串
								final String requiredAnimationNames = upgrade.getFieldAsString(RenderUnit.ANIM_PROPS,
										0);
								// 遍历升级单位的动画属性字符串，移除所有与升级相关的次要标签
								TokenLoop:
								for (final String animationName : requiredAnimationNames.split(",")) {
									final String upperCaseToken = animationName.toUpperCase();
									for (final SecondaryTag secondaryTag : SecondaryTag.values()) {
										if (upperCaseToken.equals(secondaryTag.name())) {
											// 移除单位动画监听器中的次要标签
											unit.getUnitAnimationListener().removeSecondaryTag(secondaryTag);
											continue TokenLoop;
										}
									}
								}

								// 获取原始单位的动画属性字符串
								final String originalRequiredAnimationNames = War3MapViewer.this.allObjectData
										.getUnits().get(unit.getTypeId()).getFieldAsString(RenderUnit.ANIM_PROPS, 0);
								// 遍历原始单位的动画属性字符串，添加所有与升级相关的次要标签
								TokenLoop:
								for (final String animationName : originalRequiredAnimationNames.split(",")) {
									final String upperCaseToken = animationName.toUpperCase();
									for (final SecondaryTag secondaryTag : SecondaryTag.values()) {
										if (upperCaseToken.equals(secondaryTag.name())) {
											// 添加单位动画监听器中的次要标签
											unit.getUnitAnimationListener().addSecondaryTag(secondaryTag);
											continue TokenLoop;
										}
									}
								}
							}


							@Override
							/**
							 * 从游戏中移除一个单位，并清理与该单位相关的所有渲染和模拟资源
							 *
							 * @param unit 要移除的单位
							 */
							public void removeUnit(final CUnit unit) {
								// 从 unitToRenderPeer 映射中移除单位，并获取对应的渲染单元
								final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer.remove(unit);
								// 从 widgets 列表中移除渲染单元
								War3MapViewer.this.widgets.remove(renderUnit);
								// 从 units 列表中移除渲染单元
								War3MapViewer.this.units.remove(renderUnit);
								// 从世界场景中移除渲染单元的实例
								War3MapViewer.this.worldScene.removeInstance(renderUnit.instance);
								// 调用渲染单元的 onRemove 方法，执行移除后的清理工作
								renderUnit.onRemove(War3MapViewer.this);
							}


							@Override
							/**
							 * 从游戏中移除一个可破坏物，并清理与该可破坏物相关的所有渲染和模拟资源
							 *
							 * @param dest 要移除的可破坏物
							 */
							public void removeDestructable(final CDestructable dest) {
								// 从 destructableToRenderPeer 映射中移除可破坏物，并获取对应的渲染可破坏物
								final RenderDestructable renderPeer = War3MapViewer.this.destructableToRenderPeer.remove(dest);
								// 从世界场景中移除渲染可破坏物的实例
								War3MapViewer.this.worldScene.removeInstance(renderPeer.instance);
								// 如果渲染可破坏物的可步行边界不为空，则从可步行对象树中移除
								if (renderPeer.walkableBounds != null) {
									War3MapViewer.this.walkableObjectsTree.remove(
											(MdxComplexInstance) renderPeer.instance, renderPeer.walkableBounds);
								}
							}


							@Override
							/**
							 * 获取指定建筑的路径像素图
							 *
							 * @param rawcode 建筑的原始代码
							 * @return 返回建筑的路径像素图，如果建筑不存在，则返回 null
							 */
							public BufferedImage getBuildingPathingPixelMap(final War3ID rawcode) {
								// 从 allObjectData 的 units 中获取建筑对象
								final GameObject building = War3MapViewer.this.allObjectData.getUnits().get(rawcode);
								// 如果建筑对象存在，则获取其路径像素图
								if (building != null) {
									// 调用 War3MapViewer 的 getBuildingPathingPixelMap 方法获取路径像素图
									return War3MapViewer.this.getBuildingPathingPixelMap(building);
								}
								// 如果建筑对象不存在，则返回 null
								return null;
							}


							@Override
							/**
							 * 获取指定可破坏物的死亡路径像素图
							 *
							 * @param rawcode 可破坏物的原始代码
							 * @return 返回可破坏物的死亡路径像素图，如果可破坏物不存在，则返回 null
							 */
							public BufferedImage getDestructablePathingDeathPixelMap(final War3ID rawcode) {
								// 从 allObjectData 的 destructibles 中获取可破坏物对象
								final GameObject destructable = War3MapViewer.this.allObjectData.getDestructibles().get(rawcode);
								// 如果可破坏物对象存在，则获取其死亡路径像素图
								if (destructable != null) {
									// 调用 War3MapViewer 的 getDestructablePathingDeathPixelMap 方法获取死亡路径像素图
									return War3MapViewer.this.getDestructablePathingDeathPixelMap(destructable);
								}
								// 如果可破坏物对象不存在，则返回 null
								return null;
							}

							@Override
							/**
							 * 获取指定可破坏物的路径像素图
							 *
							 * @param rawcode 可破坏物的原始代码
							 * @return 返回可破坏物的路径像素图，如果可破坏物不存在，则返回 null
							 */
							public BufferedImage getDestructablePathingPixelMap(final War3ID rawcode) {
								// 从 allObjectData 的 destructibles 中获取可破坏物对象
								final GameObject destructable = War3MapViewer.this.allObjectData.getDestructibles().get(rawcode);
								// 如果可破坏物对象存在，则获取其路径像素图
								if (destructable != null) {
									// 调用 War3MapViewer 的 getDestructablePathingPixelMap 方法获取路径像素图
									return War3MapViewer.this.getDestructablePathingPixelMap(destructable);
								}
								// 如果可破坏物对象不存在，则返回 null
								return null;
							}


							@Override
							/**
							 * 在指定的已建造建筑上播放建造完成声音
							 *
							 * @param constructedStructure 已建造的建筑
							 */
							public void spawnUnitConstructionFinishSound(final CUnit constructedStructure) {
								// 从 uiSounds 中获取建造完成的声音
								final UnitSound constructingBuilding = War3MapViewer.this.uiSounds
										.getSound(War3MapViewer.this.gameUI.getSkinField("JobDoneSound"));
								// 如果声音不为空，则播放声音
								if (constructingBuilding != null) {
									// 在世界场景的音频上下文中播放建造完成的声音，并指定已建造建筑的渲染对象
									constructingBuilding.playUnitResponse(War3MapViewer.this.worldScene.audioContext,
											War3MapViewer.this.unitToRenderPeer.get(constructedStructure));
								}
							}


							@Override
							/**
							 * 在指定的已建造建筑上播放建造完成声音
							 *
							 * @param constructedStructure 已建造的建筑
							 */
							public void spawnUnitUpgradeFinishSound(final CUnit constructedStructure) {
								// 从 uiSounds 中获取建造完成的声音
								final UnitSound constructingBuilding = War3MapViewer.this.uiSounds
										.getSound(War3MapViewer.this.gameUI.getSkinField("UpgradeComplete"));
								// 获取已建造建筑的渲染单元
								final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer
										.get(constructedStructure);
								// 如果声音不为空，且渲染单元的玩家索引与本地玩家索引相同，则播放声音
								if ((constructingBuilding != null) && (renderUnit.getSimulationUnit()
										.getPlayerIndex() == War3MapViewer.this.localPlayerIndex)) {
									// 在世界场景的音频上下文中播放建造完成的声音，并指定已建造建筑的位置和高度
									constructingBuilding.play(War3MapViewer.this.worldScene.audioContext,
											constructedStructure.getX(), constructedStructure.getY(),
											renderUnit.getZ());
								}
							}


							@Override
							/**
							 * 在游戏中创建一个新的单位
							 *
							 * @param simulation 模拟对象
							 * @param typeId 单位类型ID
							 * @param playerIndex 玩家索引
							 * @param x 单位的X坐标
							 * @param y 单位的Y坐标
							 * @param facing 单位的朝向（以弧度为单位）
							 * @return 返回新创建的单位对象
							 */
							public CUnit createUnit(final CSimulation simulation, final War3ID typeId,
													final int playerIndex, final float x, final float y, final float facing) {
								// 调用 createNewUnit 方法创建一个新的单位，并将其转换为 CUnit 类型
								return (CUnit) createNewUnit(War3MapViewer.this.allObjectData, typeId, x, y,
										playerIndex, playerIndex, (float) Math.toRadians(facing));
							}


							@Override
							/**
							 * 在游戏中创建一个新的可破坏物
							 *
							 * @param typeId 可破坏物类型ID
							 * @param x 可破坏物的X坐标
							 * @param y 可破坏物的Y坐标
							 * @param facing 可破坏物的朝向（以弧度为单位）
							 * @param scale 可破坏物的缩放比例
							 * @param variation 可破坏物的变化索引
							 * @return 返回新创建的可破坏物对象
							 */
							public CDestructable createDestructable(final War3ID typeId, final float x, final float y,
																	final float facing, final float scale, final int variation) {
								// 调用 createDestructableZ 方法创建一个新的可破坏物，并指定其Z坐标为地面高度或可步行高度的最大值
								return createDestructableZ(typeId, x, y,
										Math.max(getWalkableRenderHeight(x, y),
												War3MapViewer.this.terrain.getGroundHeight(x, y)),
										facing, scale, variation);
							}


							@Override
							/**
							 * 在游戏中创建一个新的可破坏物，并指定其Z坐标
							 *
							 * @param typeId 可破坏物类型ID
							 * @param x 可破坏物的X坐标
							 * @param y 可破坏物的Y坐标
							 * @param z 可破坏物的Z坐标
							 * @param facing 可破坏物的朝向（以弧度为单位）
							 * @param scale 可破坏物的缩放比例
							 * @param variation 可破坏物的变化索引
							 * @return 返回新创建的可破坏物对象
							 */
							public CDestructable createDestructableZ(final War3ID typeId, final float x, final float y,
																	 final float z, final float facing, final float scale, final int variation) {
								// 从 allObjectData 的 destructibles 中获取可破坏物对象
								final GameObject row = War3MapViewer.this.allObjectData.getDestructibles().get(typeId);
								// 创建一个包含位置信息的三维数组
								final float[] location3d = {x, y, z};
								// 创建一个包含缩放比例信息的三维数组
								final float[] scale3d = {scale, scale, scale};
								// 调用 createNewDestructable 方法创建一个新的可破坏物，并指定其位置、朝向、生命值和缩放比例
								final RenderDestructable newDestructable = createNewDestructable(typeId, row, variation,
										location3d, (float) Math.toRadians(facing), (short) 100, scale3d);
								// 返回新创建的可破坏物的模拟对象
								return newDestructable.getSimulationDestructable();
							}


							@Override
							/**
							 * 在游戏中创建一个新的物品
							 *
							 * @param simulation 模拟对象
							 * @param typeId 物品类型ID
							 * @param x 物品的X坐标
							 * @param y 物品的Y坐标
							 * @return 返回新创建的物品对象
							 */
							public CItem createItem(final CSimulation simulation, final War3ID typeId, final float x,
													final float y) {
								// 调用 createNewUnit 方法创建一个新的物品，并将其转换为 CItem 类型
								return (CItem) createNewUnit(War3MapViewer.this.allObjectData, typeId, x, y, -1, -1,
										(float) Math.toRadians(War3MapViewer.this.simulation.getGameplayConstants()
												.getBuildingAngle()));
							}


							@Override
							/**
							 * 在游戏中创建一个新的单位死亡爆炸效果
							 *
							 * @param source 死亡的单位
							 * @param explodesOnDeathBuffId 死亡时爆炸的 Buff ID
							 */
							public void spawnDeathExplodeEffect(final CUnit source,
																final War3ID explodesOnDeathBuffId) {
								// 获取死亡单位的渲染对象
								final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer.get(source);
								// 初始化模型实例为 null
								MdxComplexInstance modelInstance = null;
								// 如果 explodesOnDeathBuffId 不为 null
								if (explodesOnDeathBuffId != null) {
									// 获取对应的效果附件 UI
									final EffectAttachmentUI effectAttachmentUI = getEffectAttachmentUI(
											explodesOnDeathBuffId, CEffectType.EFFECT, 0);
									// 获取模型路径
									final String modelPath = effectAttachmentUI.getModelPath();
									// 加载模型
									final MdxModel spawnedEffectModel = loadModelMdx(modelPath);
									// 如果模型加载成功
									if (spawnedEffectModel != null) {
										// 添加模型实例
										modelInstance = (MdxComplexInstance) spawnedEffectModel.addInstance();
									}
								}
								// 如果模型实例为 null 且渲染单位的特殊艺术模型不为 null
								if ((modelInstance == null) && (renderUnit.specialArtModel != null)) {
									// 使用特殊艺术模型添加实例
									modelInstance = (MdxComplexInstance) renderUnit.specialArtModel.addInstance();
								}
								// 如果模型实例不为 null
								if (modelInstance != null) {
									// 设置模型实例的队伍颜色
									modelInstance.setTeamColor(source.getPlayerIndex());
									// 设置模型实例的位置
									modelInstance.setLocation(renderUnit.location);
									// 将模型实例添加到世界场景中
									modelInstance.setScene(War3MapViewer.this.worldScene);
									// 随机播放出生序列
									SequenceUtils.randomBirthSequence(modelInstance);
									// 将渲染攻击瞬间添加到 projectile 列表中
									War3MapViewer.this.projectiles.add(new RenderAttackInstant(modelInstance,
											War3MapViewer.this,
											(float) Math.toRadians(renderUnit.getSimulationUnit().getFacing())));
								}
							}


							@Override
							/**
							 * 在游戏中创建一个新的单位升级效果
							 *
							 * @param source 升级的单位
							 */
							public void spawnGainLevelEffect(final CUnit source) {
								// 获取英雄能力的 UI 界面
								final AbilityUI heroUI = War3MapViewer.this.abilityDataUI.getUI(ABILITY_HERO_RAWCODE);
								// 获取升级单位的渲染对象
								final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer.get(source);
								// 获取英雄升级的艺术效果路径
								final String heroLevelUpArt = heroUI.getCasterArt(0).getModelPath();
								// TODO: 使用 addSpellEffectTarget 方法
								// 在单位的原点位置生成特效
								spawnFxOnOrigin(renderUnit, heroLevelUpArt);
							}


							@Override
							/**
							 * 处理英雄复活事件
							 *
							 * @param source 复活的英雄单位
							 */
							public void heroRevived(final CUnit source) {
								// 获取复活技能的 UI 界面
								final AbilityUI reviveUI = War3MapViewer.this.abilityDataUI.getUI(ABILITY_REVIVE_RAWCODE);
								// 获取复活英雄的渲染对象
								final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer.get(source);
								// 关闭渲染单元的附加覆盖网格模式
								renderUnit.instance.additiveOverrideMeshMode = false;
								// 设置渲染单元的顶点透明度为 1.0f
								renderUnit.instance.setVertexAlpha(1.0f);
								// 获取复活英雄的玩家对象
								final CPlayer player = War3MapViewer.this.simulation.getPlayer(source.getPlayerIndex());
								// 获取英雄复活的艺术效果路径
								final String heroReviveArt = reviveUI.getTargetArt(player.getRace().ordinal()).getModelPath();
								// TODO: 使用 addSpellEffectTarget 方法
								// 在单位的原点位置生成特效
								spawnFxOnOrigin(renderUnit, heroReviveArt);
								// 获取英雄单位的数据行
								final GameObject row = War3MapViewer.this.allObjectData.getUnits().get(source.getTypeId());

								// 重新创建单位的阴影，这可能是必要的

								// 获取单位阴影的文件名
								final String unitShadow = row.getFieldAsString(UNIT_SHADOW, 0);
								// 获取单位的 X 坐标
								final float unitX = source.getX();
								// 获取单位的 Y 坐标
								final float unitY = source.getY();
								// 如果单位阴影文件名不为空且不等于 "_"
								if ((unitShadow != null) && !"_".equals(unitShadow)) {
									// 构建阴影纹理的路径
									final String texture = "ReplaceableTextures\\Shadows\\" + unitShadow + ".blp";
									// 获取阴影的 X 偏移量
									final float shadowX = row.getFieldAsFloat(UNIT_SHADOW_X, 0);
									// 获取阴影的 Y 偏移量
									final float shadowY = row.getFieldAsFloat(UNIT_SHADOW_Y, 0);
									// 获取阴影的宽度
									final float shadowWidth = row.getFieldAsFloat(UNIT_SHADOW_W, 0);
									// 获取阴影的高度
									final float shadowHeight = row.getFieldAsFloat(UNIT_SHADOW_H, 0);
									// 如果地图 MPQ 文件中存在该纹理
									if (War3MapViewer.this.mapMpq.has(texture)) {
										// 计算阴影的 X 坐标
										final float x = unitX - shadowX;
										// 计算阴影的 Y 坐标
										final float y = unitY - shadowY;
										// 在地形上添加单位阴影贴图
										renderUnit.shadow = War3MapViewer.this.terrain.addUnitShadowSplat(texture, x, y, x + shadowWidth, y + shadowHeight, 3, 0.5f, false);
									}
									// 如果地图 MPQ 文件中不存在该纹理，但存在其 DDS 格式的备份
									else if (War3MapViewer.this.mapMpq.has(texture.replace(".blp", ".dds"))) {
										// 计算阴影的 X 坐标
										final float x = unitX - shadowX;
										// 计算阴影的 Y 坐标
										final float y = unitY - shadowY;
										// 在地形上添加单位阴影贴图
										renderUnit.shadow = War3MapViewer.this.terrain.addUnitShadowSplat(texture.replace(".blp", ".dds"), x, y, x + shadowWidth, y + shadowHeight, 3, 0.5f, false);
									}
								}
							}


							@Override
							/**
							 * 处理英雄死亡事件
							 *
							 * @param source 死亡的英雄单位
							 */
							public void heroDeathEvent(final CUnit source) {
								// 获取死亡英雄的渲染对象
								final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer.get(source);
								// 开启渲染单元的附加覆盖网格模式
								renderUnit.instance.additiveOverrideMeshMode = true;
							}


							@Override
							/**
							 * 在指定单位上生成特效
							 *
							 * @param unit 要添加特效的单位
							 * @param effectPath 特效的路径
							 */
							public void spawnEffectOnUnit(final CUnit unit, final String effectPath) {
								// 在单位上添加指定路径的特效
								final RenderSpellEffect spellEffect = addSpecialEffectTarget(effectPath, unit, "");
								// 如果特效添加成功
								if (spellEffect != null) {
									// 设置特效在完成后自动销毁
									spellEffect.setKillWhenDone(true);
								}
							}


							@Override
							/**
							 * 在指定单位上生成临时特效
							 *
							 * @param unit 要添加特效的单位
							 * @param alias 特效的别名
							 * @param effectType 特效类型
							 */
							public void spawnTemporarySpellEffectOnUnit(final CUnit unit, final War3ID alias,
																		final CEffectType effectType) {
								// 在单位上添加指定别名和类型的临时特效
								final RenderSpellEffect spellEffect = spawnSpellEffectOnUnitEx(unit, alias, effectType, 0);
								// 如果特效添加成功
								if (spellEffect != null) {
									// 设置特效在完成后自动销毁
									spellEffect.setKillWhenDone(true);
								}
							}


							@Override
							/**
							 * 在指定单位上生成持久的法术效果，并返回一个 SimulationRenderComponentModel 对象，用于控制和操作生成的法术效果
							 *
							 * @param unit 要添加法术效果的单位
							 * @param alias 法术效果的别名
							 * @param effectType 法术效果的类型
							 * @return 返回一个 SimulationRenderComponentModel 对象，用于控制和操作生成的法术效果，如果生成失败则返回 SimulationRenderComponentModel.DO_NOTHING
							 */
							public SimulationRenderComponentModel spawnPersistentSpellEffectOnUnit(final CUnit unit,
																								   final War3ID alias, final CEffectType effectType) {
								// 在单位上添加指定别名和类型的持久法术效果
								final List<RenderSpellEffect> specialEffects = spawnSpellEffectOnUnitEx(unit, alias,
										effectType);
								// 如果法术效果生成失败或为空，则返回 SimulationRenderComponentModel.DO_NOTHING
								if ((specialEffects == null) || specialEffects.isEmpty()) {
									return SimulationRenderComponentModel.DO_NOTHING;
								}
								// 返回一个新的 SimulationRenderComponentModel 对象，用于控制和操作生成的法术效果
								return new SimulationRenderComponentModel() {
									/**
									 * 移除所有生成的法术效果
									 */
									@Override
									public void remove() {
										// 遍历所有生成的法术效果，并设置其动画为死亡动画，以移除它们
										for (final RenderSpellEffect effect : specialEffects) {
											effect.setAnimations(RenderSpellEffect.DEATH_ONLY, true);
										}
									}

									/**
									 * 设置所有生成的法术效果的高度
									 *
									 * @param height 要设置的高度
									 */
									@Override
									public void setHeight(final float height) {
										// 遍历所有生成的法术效果，并设置其高度
										for (final RenderSpellEffect effect : specialEffects) {
											effect.setHeight(height);
										}
									}
								};
							}


							@Override
							/**
							 * 在指定单位上生成持久的法术效果，并返回一个 SimulationRenderComponentModel 对象，用于控制和操作生成的法术效果
							 *
							 * @param unit 要添加法术效果的单位
							 * @param alias 法术效果的别名
							 * @param effectType 法术效果的类型
							 * @param index 法术效果的索引
							 * @return 返回一个 SimulationRenderComponentModel 对象，用于控制和操作生成的法术效果，如果生成失败则返回 SimulationRenderComponentModel.DO_NOTHING
							 */
							public SimulationRenderComponentModel spawnPersistentSpellEffectOnUnit(final CUnit unit,
																								   final War3ID alias, final CEffectType effectType, final int index) {
								// 在单位上添加指定别名、类型和索引的持久法术效果
								final RenderSpellEffect specialEffect = spawnSpellEffectOnUnitEx(unit, alias, effectType, index);
								// 如果法术效果生成失败或为空，则返回 SimulationRenderComponentModel.DO_NOTHING
								if (specialEffect == null) {
									return SimulationRenderComponentModel.DO_NOTHING;
								}
								// 返回一个新的 SimulationRenderComponentModel 对象，用于控制和操作生成的法术效果
								return new SimulationRenderComponentModel() {
									/**
									 * 移除所有生成的法术效果
									 */
									@Override
									public void remove() {
										// 设置法术效果的动画为死亡动画，以移除它们
										specialEffect.setAnimations(RenderSpellEffect.DEATH_ONLY, true);
									}

									/**
									 * 设置所有生成的法术效果的高度
									 *
									 * @param height 要设置的高度
									 */
									@Override
									public void setHeight(final float height) {
										// 设置法术效果的高度
										specialEffect.setHeight(height);
									}
								};
							}


							@Override
							/**
							 * 在指定的可破坏物目标上创建一个法术效果，并返回一个 SimulationRenderComponentModel 对象，用于控制和操作生成的法术效果
							 *
							 * @param source 法术效果的源单位
							 * @param target 要添加法术效果的可破坏物目标
							 * @param alias 法术效果的别名
							 * @param artAttachmentHeight 法术效果的艺术附件高度
							 * @return 返回一个 SimulationRenderComponentModel 对象，用于控制和操作生成的法术效果，如果生成失败则返回 null
							 */
							public SimulationRenderComponentModel createSpellEffectOverDestructable(final CUnit source,
																									final CDestructable target, final War3ID alias, final float artAttachmentHeight) {
								// 获取指定别名的能力 UI
								final AbilityUI abilityUI = War3MapViewer.this.abilityDataUI.getUI(alias);
								// 获取目标艺术的模型路径
								final String effectPath = abilityUI.getTargetArt(0).getModelPath();
								// TODO 使用 addSpellEffectTarget 可能更好？
								// 获取可破坏物的渲染对象
								final RenderDestructable renderDestructable = War3MapViewer.this.destructableToRenderPeer
										.get(target);
								// 加载模型路径对应的模型
								final MdxModel spawnedEffectModel = loadModelMdx(effectPath);
								// 如果模型加载成功
								if (spawnedEffectModel != null) {
									// 为加载的模型创建一个复杂实例
									final MdxComplexInstance modelInstance = (MdxComplexInstance) spawnedEffectModel
											.addInstance();
									// 设置模型实例的团队颜色
									modelInstance.setTeamColor(War3MapViewer.this.simulation
											.getPlayer(source.getPlayerIndex()).getColor());
									// 计算起始高度
									final float startingHeight = renderDestructable.getZ() + artAttachmentHeight;
									// 设置模型实例的位置
									modelInstance.setLocation(renderDestructable.getX(), renderDestructable.getY(),
											startingHeight);
									// 将模型实例添加到世界场景中
									modelInstance.setScene(War3MapViewer.this.worldScene);
									// 创建一个新的 RenderSpellEffect 对象，用于控制和操作生成的法术效果
									final RenderSpellEffect renderAttackInstant = new RenderSpellEffect(modelInstance,
											War3MapViewer.this, 0, RenderSpellEffect.STAND_ONLY, SequenceUtils.EMPTY);
									// 设置法术效果的动画为站立动画，并立即播放
									renderAttackInstant.setAnimations(RenderSpellEffect.STAND_ONLY, false);
									// 将法术效果添加到项目列表中
									War3MapViewer.this.projectiles.add(renderAttackInstant);
									// 返回一个新的 SimulationRenderComponentModel 对象，用于控制和操作生成的法术效果
									return new SimulationRenderComponentModel() {
										/**
										 * 移除所有生成的法术效果
										 */
										@Override
										public void remove() {
											// 设置法术效果的动画为死亡动画，以移除它们
											renderAttackInstant.setAnimations(RenderSpellEffect.DEATH_ONLY, true);
										}

										/**
										 * 设置所有生成的法术效果的高度
										 *
										 * @param height 要设置的高度
										 */
										@Override
										public void setHeight(final float height) {
											// 设置法术效果的高度
											renderAttackInstant.setHeight(startingHeight + height);
										}
									};
								}
								// 如果模型加载失败，则返回 null
								return null;
							}


							@Override
							/**
							 * 在指定的点上生成一个法术效果，并返回一个 SimulationRenderComponentModel 对象，用于控制和操作生成的法术效果
							 *
							 * @param x 法术效果的 X 坐标
							 * @param y 法术效果的 Y 坐标
							 * @param facing 法术效果的朝向
							 * @param alias 法术效果的别名
							 * @param effectType 法术效果的类型
							 * @param index 法术效果的索引
							 * @return 返回一个 SimulationRenderComponentModel 对象，用于控制和操作生成的法术效果。如果生成失败，则返回 SimulationRenderComponentModel.DO_NOTHING
							 */
							public SimulationRenderComponentModel spawnSpellEffectOnPoint(final float x, final float y,
																						  final float facing, final War3ID alias, final CEffectType effectType,
																						  final int index) {
								// 尝试生成一个法术效果
								final RenderSpellEffect specialEffect = spawnSpellEffectEx(x, y, facing, alias,
										effectType, index);
								// 如果生成失败
								if (specialEffect == null) {
									// 返回一个空的操作对象
									return SimulationRenderComponentModel.DO_NOTHING;
								}
								// 返回一个新的操作对象
								return new SimulationRenderComponentModel() {
									/**
									 * 设置法术效果的高度
									 *
									 * @param height 法术效果的高度
									 */
									@Override
									public void setHeight(final float height) {
										// 设置法术效果的高度为地面高度加上指定的高度
										specialEffect.setHeight(Math.max(getWalkableRenderHeight(x, y),
												War3MapViewer.this.terrain.getGroundHeight(x, y)) + height);
									}

									/**
									 * 移除法术效果
									 */
									@Override
									public void remove() {
										// 设置法术效果的动画为死亡动画，并开始播放
										specialEffect.setAnimations(RenderSpellEffect.DEATH_ONLY, true);
									}
								};
							}


							@Override
							/**
							 * 在指定的点上生成一个临时的法术效果，并返回一个 RenderSpellEffect 对象，用于控制和操作生成的法术效果
							 *
							 * @param x 法术效果的 X 坐标
							 * @param y 法术效果的 Y 坐标
							 * @param facing 法术效果的朝向
							 * @param alias 法术效果的别名
							 * @param effectType 法术效果的类型
							 * @param index 法术效果的索引
							 * @return 返回一个 RenderSpellEffect 对象，用于控制和操作生成的法术效果。如果生成失败，则返回 null
							 */
							public void spawnTemporarySpellEffectOnPoint(final float x, final float y,
																		 final float facing, final War3ID alias, final CEffectType effectType,
																		 final int index) {
								// 尝试生成一个法术效果
								final RenderSpellEffect specialEffect = spawnSpellEffectEx(x, y, facing, alias,
										effectType, index);
								// 如果生成成功
								if (specialEffect != null) {
									// 设置法术效果在完成后自动销毁
									specialEffect.setKillWhenDone(true);
								}
							}


							@Override
							/**
							 * 播放训练单位准备就绪的声音
							 *
							 * @param trainedUnit 训练完成的单位
							 */
							public void spawnUnitReadySound(final CUnit trainedUnit) {
								// 如果训练单位的玩家索引与本地玩家索引相同
								if (trainedUnit.getPlayerIndex() == War3MapViewer.this.localPlayerIndex) {
									// 获取训练单位的渲染对象
									final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(trainedUnit);
									// 播放单位声音集中的准备就绪声音
									renderPeer.soundset.ready
											.playUnitResponse(War3MapViewer.this.worldScene.audioContext, renderPeer);
								}
							}


							@Override
							/**
							 * 当单位的位置发生变化时调用此方法
							 *
							 * @param cUnit 位置发生变化的单位
							 */
							public void unitRepositioned(final CUnit cUnit) {
								// 获取与模拟单位对应的渲染单位
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(cUnit);
								// 调用渲染单位的 repositioned 方法，通知其位置已更新
								renderPeer.repositioned(War3MapViewer.this);
							}


							@Override
							// 当单位类型更新时调用此方法
							public void unitUpdatedType(final CUnit simulationUnit, final War3ID typeId) {
								// 获取与模拟单位对应的渲染单位
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(simulationUnit);
								// 获取单位类型的数据行
								final GameObject row = War3MapViewer.this.allObjectData.getUnits().get(typeId);
								// 获取单位模型的路径
								final String path = getUnitModelPath(row);

								// 检查是否有特殊艺术模型路径，如果有则尝试加载
								final String unitSpecialArtPath = row.getFieldAsString(UNIT_SPECIAL, 0);
								MdxModel specialArtModel;
								if ((unitSpecialArtPath != null) && !unitSpecialArtPath.isEmpty()) {
									try {
										specialArtModel = loadModelMdx(unitSpecialArtPath);
									} catch (final Exception exc) {
										exc.printStackTrace();
										specialArtModel = null;
									}
								} else {
									specialArtModel = null;
								}

								// 加载单位模型
								final MdxModel model = loadModelMdx(path);
								// 获取单位肖像模型的路径，并尝试加载
								MdxModel portraitModel;
								final String portraitPath = path.substring(0, path.length() - 4) + "_portrait.mdx";
								if (War3MapViewer.this.dataSource.has(portraitPath)) {
									portraitModel = loadModelMdx(portraitPath);
								} else {
									portraitModel = model;
								}

								// 计算单位的朝向角度
								final float angle = (float) Math.toDegrees(simulationUnit.getFacing());
								// 获取单位类型的数据
								final RenderUnitTypeData typeData = getUnitTypeData(typeId, row);
								// 获取玩家的自定义队伍颜色
								final int customTeamColor = War3MapViewer.this.simulation
										.getPlayer(simulationUnit.getPlayerIndex()).getColor();
								// 计算单位的渲染位置
								final float unitX = simulationUnit.getX();
								final float unitY = simulationUnit.getY();
								final float unitZ = Math.max(getWalkableRenderHeight(unitX, unitY),
										War3MapViewer.this.terrain.getGroundHeight(unitX, unitY))
										+ simulationUnit.getFlyHeight();

								// 处理单位的音效集
								UnitSoundset soundset = null;
								// 处理建筑阴影
								BuildingShadow buildingShadowInstance = null;
								final String buildingShadow = row.getFieldAsString(BUILDING_SHADOW, 0);
								if ((buildingShadow != null) && !"_".equals(buildingShadow)) {
									buildingShadowInstance = War3MapViewer.this.terrain.addShadow(buildingShadow, unitX,
											unitY);
								}

								// 如果渲染单位已有阴影，则先销毁
								if (renderPeer.shadow != null) {
									renderPeer.shadow.destroy(Gdx.gl30, War3MapViewer.this.terrain.centerOffset);
									renderPeer.shadow = null;
								}
								// 处理单位阴影
								final String unitShadow = row.getFieldAsString(UNIT_SHADOW, 0);
								if ((unitShadow != null) && !"_".equals(unitShadow)) {
									String texture = "ReplaceableTextures\\Shadows\\" + unitShadow + ".blp";
									// 如果找不到阴影纹理，则尝试使用.dds格式
									if (!War3MapViewer.this.mapMpq.has(texture)) {
										texture = "ReplaceableTextures\\Shadows\\" + unitShadow + ".dds";
									}
									// 如果找到了阴影纹理，则添加阴影
									if (War3MapViewer.this.mapMpq.has(texture)) {
										final float x = unitX - row.getFieldAsFloat(UNIT_SHADOW_X, 0);
										final float y = unitY - row.getFieldAsFloat(UNIT_SHADOW_Y, 0);
										renderPeer.shadow = War3MapViewer.this.terrain.addUnitShadowSplat(texture, x, y,
												x + row.getFieldAsFloat(UNIT_SHADOW_W, 0), y + row.getFieldAsFloat(UNIT_SHADOW_H, 0), 3, 0.5f, false);
									}
								}

								// 处理单位音效集
								final String soundName = row.getFieldAsString(UNIT_SOUNDSET, 0);
								UnitSoundset unitSoundset = War3MapViewer.this.soundsetNameToSoundset.get(soundName);
								if (unitSoundset == null) {
									unitSoundset = new UnitSoundset(War3MapViewer.this.dataSource,
											War3MapViewer.this.unitAckSoundsTable, soundName);
									War3MapViewer.this.soundsetNameToSoundset.put(soundName, unitSoundset);
								}
								soundset = unitSoundset;

								// 重置渲染单位的所有属性
								renderPeer.resetRenderUnit(War3MapViewer.this, model, row, unitX, unitY, unitZ,
										renderPeer.playerIndex, soundset, portraitModel, simulationUnit, typeData,
										specialArtModel, buildingShadowInstance,
										War3MapViewer.this.selectionCircleScaleFactor, typeData.getAnimationWalkSpeed(),
										typeData.getAnimationRunSpeed(), typeData.getScalingValue());
							}


							@Override
							// 玩家颜色
							public void changeUnitColor(final CUnit unit, final int playerIndex) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(unit);
								renderPeer.setPlayerColor(
										War3MapViewer.this.simulation.getPlayer(playerIndex).getColor());
							}

							@Override
							// 虚无等顶点换色
							public void changeUnitVertexColor(final CUnit unit, final Color color) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(unit);
								renderPeer.setVertexColoring(color);
							}

							@Override
							public void changeUnitVertexColor(final CUnit unit, final float r, final float g,
									final float b) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(unit);
								renderPeer.setVertexColoring(r, g, b);
							}

							@Override
							public void changeUnitVertexColor(final CUnit unit, final float r, final float g,
									final float b, final float a) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(unit);
								renderPeer.setVertexColoring(r, g, b, a);
							}

							@Override
							public void spawnTextTag(final CUnit unit, final TextTagConfigType configType,
									final int displayAmount) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(unit);
								final TextTagConfig textTagConfig = getTextTagConfig(configType.getKey());
								String text;
								switch (configType) {
									case GOLD:
									case GOLD_BOUNTY:
									case LUMBER:
									case LUMBER_BOUNTY:
									case XP: {
										text = "+" + displayAmount;
										break;
									}
									case MISS_TEXT:
										text = "miss!";
										break;
									default:
									case MANA_BURN:
									case CRITICAL_STRIKE:
									case SHADOW_STRIKE:
									case BASH: {
										text = displayAmount + "!";
										break;
									}
								}
								War3MapViewer.this.textTags.add(
										new TextTag(
												new Vector3(renderPeer.location),
												text,
												textTagConfig.getColor(),
												textTagConfig.getLifetime(),
												textTagConfig.getFadeStart()));
							}

							@Override
							// 定义一个方法spawnTextTag，用于在游戏地图上生成文本标签
							public void spawnTextTag(final CUnit unit, final TextTagConfigType configType,
													 final String message) {
								// 获取与CUnit对象对应的渲染单元
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(unit);
								// 根据配置类型获取文本标签的配置信息
								final TextTagConfig textTagConfig = getTextTagConfig(configType.getKey());
								// 在War3MapViewer的文本标签集合中添加一个新的文本标签
								// 文本标签的位置基于渲染单元的位置，内容为用户提供的消息，颜色、生命周期和淡出开始时间来自配置
								War3MapViewer.this.textTags.add(
										new TextTag(
												new Vector3(renderPeer.location),
												message,
												textTagConfig.getColor(),
												textTagConfig.getLifetime(),
												textTagConfig.getFadeStart()));
							}


							@Override
							// 定义一个方法，用于在玩家获取物品时播放音效
							public void spawnUIUnitGetItemSound(final CUnit cUnit, final CItem item) {
								// 从单位到渲染对象的映射中获取当前单位的渲染对象
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(cUnit);
								// 检查当前玩家是否是获取物品的单位所在的玩家
								if (localPlayerIndex == renderPeer.getSimulationUnit().getPlayerIndex()) {
									// 如果是，播放获取物品的音效
									// 获取音效对象，参数为音效的名称
									War3MapViewer.this.uiSounds.getSound("ItemGet").play(
											// 播放音效，参数包括音频上下文、音效播放的位置（x、y、z坐标）
											War3MapViewer.this.worldScene.audioContext, renderPeer.getX(),
											renderPeer.getY(), renderPeer.getZ());
								}
							}


							@Override
							// 定义一个方法，用于在游戏中的单位生成时播放物品掉落的音效
							public void spawnUIUnitDropItemSound(final CUnit cUnit, final CItem item) {
								// 从单位到渲染对象的映射中获取当前单位的渲染对象
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(cUnit);
								// 检查本地玩家索引是否与渲染对象的模拟单位所属的玩家索引相同
								if (localPlayerIndex == renderPeer.getSimulationUnit().getPlayerIndex()) {
									// 如果相同，则播放物品掉落的音效
									War3MapViewer.this.uiSounds.getSound("ItemDrop").play(
											War3MapViewer.this.worldScene.audioContext, // 音频上下文
											renderPeer.getX(), // 渲染对象的X坐标
											renderPeer.getY(), // 渲染对象的Y坐标
											renderPeer.getZ()  // 渲染对象的Z坐标
									);
								}
							}


							@Override
							// 定义一个方法，用于在战斗模拟中生成能力音效效果
							public SimulationRenderComponent spawnAbilitySoundEffect(final CUnit caster,
																					 final War3ID alias) {
								// 获取与施法单位对应的渲染单元
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(caster);
								// 获取与能力别名对应的能力UI
								final AbilityUI abilityUi = War3MapViewer.this.abilityDataUI.getUI(alias);
								// 如果能力UI为空或者能力没有设置音效，则不执行任何操作
								if ((abilityUi == null) || (abilityUi.getEffectSound() == null)) {
									return SimulationRenderComponent.DO_NOTHING;
								}
								// 播放能力音效，并获取音效ID
								final long soundId = War3MapViewer.this.uiSounds.getSound(abilityUi.getEffectSound())
										.play(War3MapViewer.this.worldScene.audioContext, renderPeer.getX(),
												renderPeer.getY(), renderPeer.getZ());
								// 如果音效ID为-1，表示播放失败，不执行任何操作
								if (soundId == -1) {
									return SimulationRenderComponent.DO_NOTHING;
								}
								// 返回一个匿名内部类实例，实现了SimulationRenderComponent接口
								// 该实例包含了一个remove方法，用于停止音效播放
								return new SimulationRenderComponent() {
									@Override
									public void remove() {
										// 当需要移除音效时，停止对应的循环播放音效
										War3MapViewer.this.uiSounds.getSound(abilityUi.getEffectSoundLooped())
												.stop(soundId);
									}
								};
							}


							@Override
							// 定义一个名为loopAbilitySoundEffect的方法，该方法接收两个参数：caster（施法单位）和alias（技能别名）
							public SimulationRenderComponent loopAbilitySoundEffect(final CUnit caster,
																					final War3ID alias) {
								// 从单位到渲染对象的映射中获取与caster对应的渲染对象
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(caster);
								// 从技能数据UI映射中获取与alias对应的技能UI对象
								final AbilityUI abilityUi = War3MapViewer.this.abilityDataUI.getUI(alias);
								// 如果技能UI对象中的循环音效为空，则返回DO_NOTHING，表示不执行任何操作
								if (abilityUi.getEffectSound() == null) {
									return SimulationRenderComponent.DO_NOTHING;
								}
								// 播放技能的循环音效，并获取播放的音效ID
								final long soundId = War3MapViewer.this.uiSounds
										.getSound(abilityUi.getEffectSoundLooped())
										.play(War3MapViewer.this.worldScene.audioContext, renderPeer.getX(),
												renderPeer.getY(), renderPeer.getZ(), true);
								// 如果音效ID为-1，表示播放失败，返回DO_NOTHING
								if (soundId == -1) {
									return SimulationRenderComponent.DO_NOTHING;
								}
								// 返回一个新的SimulationRenderComponent对象，该对象在被移除时会停止播放音效
								return new SimulationRenderComponent() {
									@Override
									public void remove() {
										// 当这个SimulationRenderComponent对象被移除时，停止之前播放的音效
										War3MapViewer.this.uiSounds.getSound(abilityUi.getEffectSoundLooped())
												.stop(soundId);
									}
								};
							}


							@Override
							// 停止特定单位施放能力的音效效果
							public void stopAbilitySoundEffect(final CUnit caster, final War3ID alias) {
								// 获取与施法单位对应的渲染单元
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(caster);
								// 获取与能力别名对应的能力UI
								final AbilityUI abilityUi = War3MapViewer.this.abilityDataUI.getUI(alias);
								// 检查能力是否有循环播放的音效
								if (abilityUi.getEffectSoundLooped() != null) {
									// TODO: 下面的代码可能会停止所有实例的声音，这是愚蠢的且无效的
									// 更好的做法是保持对声音实例的概念
									// 停止循环播放的音效
									War3MapViewer.this.uiSounds.getSound(abilityUi.getEffectSoundLooped()).stop();
								}
							}


							@Override
							// 定义一个方法，用于设置单位的首选选择替代
							public void unitPreferredSelectionReplacement(final CUnit oldUnit, final CUnit newUnit) {
								// 从单位到渲染对等体的映射中获取旧单位的渲染对等体
								final RenderUnit oldRenderPeer = War3MapViewer.this.unitToRenderPeer.get(oldUnit);
								// 从单位到渲染对等体的映射中获取新单位的渲染对等体
								final RenderUnit newRenderPeer = War3MapViewer.this.unitToRenderPeer.get(newUnit);
								// 设置旧单位的渲染对等体的首选选择替代为新单位的渲染对等体
								oldRenderPeer.setPreferredSelectionReplacement(newRenderPeer);
							}


							@Override
							// 定义一个名为setBlight的方法，用于设置地图上的枯萎效果
							// 参数x和y表示枯萎效果的中心点坐标
							// 参数radius表示枯萎效果的半径
							// 参数blighted是一个布尔值，表示是否应用枯萎效果
							public void setBlight(final float x, final float y, final float radius,
												  final boolean blighted) {
								// 调用War3MapViewer类的setBlight方法，传入相同的参数，以实现枯萎效果的设置
								War3MapViewer.this.setBlight(x, y, radius, blighted);
							}


							@Override
							// 定义一个方法，用于获取指定坐标(x, y)的地形高度
							public int getTerrainHeight(final float x, final float y) {
								// 调用地形对象的方法，获取坐标(x, y)对应的地形角落对象
								final RenderCorner corner = War3MapViewer.this.terrain.getCorner(x, y);
								// 如果地形角落对象为空，则返回默认高度999；否则返回该角落对象对应的高度层
								return corner == null ? 999 : corner.getLayerHeight();
							}


							@Override
							// 定义一个方法isTerrainRomp，用于判断指定坐标(x, y)的地形是否具有romp属性
							public boolean isTerrainRomp(final float x, final float y) {
								// 获取指定坐标(x, y)的地形角点信息
								final RenderCorner corner = War3MapViewer.this.terrain.getCorner(x, y);
								// 如果角点信息为空，则返回false，表示该坐标没有地形或者地形不具备romp属性
								// 如果角点信息不为空，则返回角点的romp属性值
								return corner == null ? false : corner.romp;
							}


							@Override
							// 定义一个方法，用于判断地图上指定坐标(x, y)的地形是否为水
							public boolean isTerrainWater(final float x, final float y) {
								// 获取坐标(x, y)处的地形角落对象
								final RenderCorner corner = War3MapViewer.this.terrain.getCorner(x, y);
								// 如果角落对象为空，说明坐标(x, y)不在地图上，返回false
								// 如果角落对象不为空，检查该角落的水属性是否不为0，如果不为0表示是水，返回true，否则返回false
								return corner == null ? false : corner.getWater() != 0;
							}

						},
						War3MapViewer.this.terrain.pathingGrid, War3MapViewer.this.terrain.getEntireMap(),
						War3MapViewer.this.seededRandom, War3MapViewer.this.commandErrorListener);
			});

			this.loadMapTasks.add(() -> {
				War3MapViewer.this.walkableObjectsTree = new Quadtree<>(War3MapViewer.this.terrain.getEntireMap());
				if (War3MapViewer.this.doodadsAndDestructiblesLoaded) {
					loadDoodadsAndDestructibles(War3MapViewer.this.allObjectData, w3iFile);
				}
				else {
					throw new IllegalStateException(
							"transcription of JS has not loaded a map and has no JS async promises");
				}
			});

			this.loadMapTasks.add(() -> {
				loadSounds();
			});

			this.loadMapTasks.add(() -> {
				War3MapViewer.this.terrain.createWaves();
			});

			this.startingTaskCount = this.loadMapTasks.size();
		}

		public boolean process() throws IOException {
			final LoadMapTask nextTask = this.loadMapTasks.pollFirst();
			nextTask.run();
			return this.loadMapTasks.isEmpty();
		}

		public void addLoadTask(final LoadMapTask task) {
			this.loadMapTasks.add(task);
		}

		public float getCompletionRatio() {
			return 1.0f - (this.loadMapTasks.size() / (float) this.startingTaskCount);
		}
	}

	public static interface LoadMapTask {
		void run() throws IOException;
	}
}
