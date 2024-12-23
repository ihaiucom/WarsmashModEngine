package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapRuntimeObjectData;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;

public class AbilityDataUI {
	// Standard ability icon fields
	// 能力（ability）数据界面中不同图标的键名
	private static final String ICON_NORMAL_XY = "Buttonpos"; // 替换自 'abpx'，表示普通状态下的图标位置
	private static final String ICON_NORMAL = "Art"; // 替换自 'aart'，表示普通状态下的图标
	private static final String ICON_TURN_OFF = "Unart"; // 替换自 'auar'，表示关闭状态下的图标
	private static final String ICON_TURN_OFF_XY = "UnButtonpos"; // 替换自 'aubx'，表示关闭状态下的图标位置
	private static final String ICON_RESEARCH = "ResearchArt"; // 替换自 'arar'，表示研究状态下的图标
	private static final String ICON_RESEARCH_XY = "Researchbuttonpos"; // 替换自 'arpx'，表示研究状态下的图标位置

	// 能力提示信息的键名
	private static final String ABILITY_TIP = "Tip"; // 替换自 'atp1'，表示能力的普通提示
	private static final String ABILITY_UBER_TIP = "Ubertip"; // 替换自 'aub1'，表示能力的扩展提示
	private static final String ABILITY_UN_TIP = "Untip"; // 替换自 'aut1'，表示取消能力的普通提示
	private static final String ABILITY_UN_UBER_TIP = "Unubertip"; // 替换自 'auu1'，表示取消能力的扩展提示
	private static final String ABILITY_RESEARCH_TIP = "Researchtip"; // 替换自 'aret'，表示研究能力的普通提示
	private static final String ABILITY_RESEARCH_UBER_TIP = "Researchubertip"; // 替换自 'arut'，表示研究能力的扩展提示

	// 能力效果声音的键名
	private static final String ABILITY_EFFECT_SOUND = "Effectsound"; // 替换自 'aefs'，表示能力的普通效果声音
	private static final String ABILITY_EFFECT_SOUND_LOOPED = "Effectsoundlooped"; // 替换自 'aefl'，表示能力的循环效果声音

	// 能力热键的键名
	private static final String ABILITY_HOTKEY_NORMAL = "Hotkey"; // 替换自 'ahky'，表示能力的普通热键
	private static final String ABILITY_HOTKEY_TURNOFF = "Unhotkey"; // 替换自 'auhk'，表示关闭能力的普通热键
	private static final String ABILITY_HOTKEY_LEARN = "Researchhotkey"; // 替换自 'arhk'，表示学习能力的热键

	// 施法者艺术资源的键名，原键名为'acat'
	private static final String CASTER_ART = "CasterArt";
	// 定义常量字符串数组，表示施法者艺术资源附件点的键名，原键名为'acap'和'aca1'
	private static final String[] CASTER_ART_ATTACHMENT_POINT = {"Casterattach", "Casterattach1"};
	// 施法者艺术资源附件数量的键名，原键名为'acac'
	private static final String CASTER_ART_ATTACHMENT_COUNT = "Casterattachcount";

	// 目标艺术资源的键名，原键名为'atat'
	private static final String TARGET_ART = "TargetArt";
	// 定义常量字符串数组，表示目标艺术资源附件点的键名，原键名为'ata0', 'ata2', 'ata5'
	private static final String[] TARGET_ART_ATTACHMENT_POINT = {"Targetattach", "Targetattach1",
			"Targetattach2", "Targetattach3", "Targetattach4", "Targetattach5"};
	// 目标艺术资源附件数量的键名，原键名为'atac'
	private static final String TARGET_ART_ATTACHMENT_COUNT = "Targetattachcount";

	// 特殊艺术资源的键名，原键名为'asat'
	private static final String SPECIAL_ART = "SpecialArt";
	// 特殊艺术资源附件点的键名，原键名为'aspt'
	private static final String SPECIAL_ART_ATTACHMENT_POINT = "Specialattach";

	// 效果艺术资源的键名，原键名为'aeat'
	private static final String EFFECT_ART = "EffectArt";
	// 范围效果艺术资源的键名，原键名为'aaea'
	private static final String AREA_EFFECT_ART = "Areaeffectart";
	// 导弹艺术资源的键名，原键名为'amat'
	private static final String MISSILE_ART = "Missileart";
	// 导弹弧线的键名，原键名为'amac'
	private static final String MISSILE_ARC = "Missilearc";
	// 闪电效果的键名
	private static final String LIGHTNING_EFFECTS = "LightningEffect";


	// Standard buff icon fields
	// 普通增益效果的图标资源名称，原名为'fart'，现已更正
	private static final String BUFF_ICON_NORMAL = "Buffart";
	// 增益能力的提示信息资源名称，原名为'ftip'，现已更正
	private static final String BUFF_ABILITY_TIP = "Bufftip";
	// 增益能力的超级提示信息资源名称，原名为'fube'，现已更正
	private static final String BUFF_ABILITY_UBER_TIP = "Buffubertip";
	// 增益能力的效果音资源名称，原名为'fefs'，现已更正
	private static final String BUFF_ABILITY_EFFECT_SOUND = "Effectsound";
	// 循环播放的增益能力效果音资源名称，原名为'fefl'，现已更正
	private static final String BUFF_ABILITY_EFFECT_SOUND_LOOPED = "Effectsoundlooped";
	// 定义常量字符串BUFF_TARGET_ART，用于表示目标艺术图的键名，原键名为'ftat'
	private static final String BUFF_TARGET_ART = "TargetArt";

	// 目标艺术图的附着点键名，原键名从'fta0'到'fta5'
	private static final String[] BUFF_TARGET_ART_ATTACHMENT_POINT = {
			"Targetattach", // 替换自'fta0'
			"Targetattach1",
			"Targetattach2",
			"Targetattach3",
			"Targetattach4", // 替换自'fta1'
			"Targetattach5" // 替换自'fta5'
	};

	// 目标艺术图附着点数量的键名，原键名为'ftac'
	private static final String BUFF_TARGET_ART_ATTACHMENT_COUNT = "Targetattachcount";
	// 特殊艺术图的键名，原键名为'fsat'
	private static final String BUFF_SPECIAL_ART = "SpecialArt";
	// 特殊艺术图的附着点键名，原键名为'fspt'
	private static final String BUFF_SPECIAL_ART_ATTACHMENT_POINT = "Specialattach";
	// 效果艺术图的键名，原键名为'feat'
	private static final String BUFF_EFFECT_ART = "EffectArt";
	// 效果艺术图的附着点键名，原键名为'feft'
	private static final String BUFF_EFFECT_ART_ATTACHMENT_POINT = "Effectattach";
	// 导弹艺术图的键名，原键名为'fmat'
	private static final String BUFF_MISSILE_ART = "Missileart";


	// 单位图标在界面中的位置
	private static final String UNIT_ICON_NORMAL_XY = "Buttonpos"; // 替换自 'ubpx'
	// 单位图标的正常状态下的图像资源
	private static final String UNIT_ICON_NORMAL = "Art"; // 替换自 'uico'
	// 单位的普通提示信息
	private static final String UNIT_TIP = "Tip"; // 替换自 'utip'
	// 单位的复活提示信息
	private static final String UNIT_REVIVE_TIP = "Revivetip"; // 替换自 'utpr'
	// 单位的觉醒提示信息
	private static final String UNIT_AWAKEN_TIP = "Awakentip"; // 替换自 'uawt'
	// 单位的超级提示信息
	private static final String UNIT_UBER_TIP = "Ubertip"; // 替换自 'utub'
	// 单位的热键绑定信息
	private static final String UNIT_HOTKEY = "Hotkey"; // 替换自 'uhot'

	// 物品图标的正常位置坐标
	private static final String ITEM_ICON_NORMAL_XY = "Buttonpos"; // 替换自 'ubpx'
	// 物品图标的正常图像
	private static final String ITEM_ICON_NORMAL = "Art"; // 替换自 'iico'
	// 物品的提示信息
	private static final String ITEM_TIP = "Tip"; // 替换自 'utip'
	// 物品的扩展提示信息
	private static final String ITEM_UBER_TIP = "Ubertip"; // 替换自 'utub'
	// 物品的描述信息
	private static final String ITEM_DESCRIPTION = "Description"; // 替换自 'ides'
	// 物品的热键绑定
	private static final String ITEM_HOTKEY = "Hotkey"; // 替换自 'uhot'


	// 升级图标在UI中的位置
	private static final String UPGRADE_ICON_NORMAL_XY = "Buttonpos"; // 替换自 'gbpx'
	// 普通状态下的升级图标资源名称
	private static final String UPGRADE_ICON_NORMAL = "Art"; // 替换自 'gar1'
	// 升级的最大等级
	private static final String UPGRADE_LEVELS = "maxlevel"; // 替换自 'glvl'
	// 升级时的提示信息
	private static final String UPGRADE_TIP = "Tip"; // 替换自 'gtp1'
	// 升级时的超级提示信息（可能是更详细的提示）
	private static final String UPGRADE_UBER_TIP = "Ubertip"; // 替换自 'gub1'
	// 升级操作的热键
	private static final String UPGRADE_HOTKEY = "Hotkey"; // 替换自 'ghk1'
 

	// 定义一个映射表，用于存储技能代码（War3ID）到技能UI界面的映射
	 private final Map<War3ID, AbilityUI> rawcodeToUI = new HashMap<>();

	 // 定义一个映射表，用于存储增益代码（War3ID）到增益UI界面的映射
	 private final Map<War3ID, BuffUI> rawcodeToBuffUI = new HashMap<>();

	 // 定义一个映射表，用于存储单位代码（War3ID）到单位图标UI界面的映射
	 private final Map<War3ID, UnitIconUI> rawcodeToUnitUI = new HashMap<>();

	 // 定义一个映射表，用于存储物品代码（War3ID）到物品UI界面的映射
	 private final Map<War3ID, ItemUI> rawcodeToItemUI = new HashMap<>();

	 // 定义一个映射表，用于存储升级代码（War3ID）到图标UI界面列表的映射
	 private final Map<War3ID, List<IconUI>> rawcodeToUpgradeUI = new HashMap<>();

	 // 定义一系列图标UI界面，分别对应不同的游戏操作或状态
	 private final IconUI moveUI; // 移动
	 private final IconUI stopUI; // 停止
	 private final IconUI holdPosUI; // 保持位置
	 private final IconUI patrolUI; // 巡逻
	 private final IconUI attackUI; // 攻击
	 private final IconUI attackGroundUI; // 攻击地面
	 private final IconUI buildHumanUI; // 建造人类建筑
	 private final IconUI buildOrcUI; // 建造兽族建筑
	 private final IconUI buildNightElfUI; // 建造暗夜精灵建筑
	 private final IconUI buildUndeadUI; // 建造亡灵建筑
	 private final IconUI buildNeutralUI; // 建造中立建筑
	 private final IconUI buildNagaUI; // 建造娜迦建筑
	 private final IconUI cancelUI; // 取消
	 private final IconUI cancelBuildUI; // 取消建造
	 private final IconUI cancelTrainUI; // 取消训练
	 private final IconUI rallyUI; // 集结
	 private final IconUI selectSkillUI; // 选择技能
	 private final IconUI neutralInteractUI; // 中立交互

	 // 定义一个字符串，用于表示禁用状态的前缀
	 private final String disabledPrefix;


	public AbilityDataUI(final Warcraft3MapRuntimeObjectData allObjectData, final GameUI gameUI,
			final War3MapViewer viewer) {
		// 获取所有对象数据中的能力数据
		final ObjectData abilityData = allObjectData.getAbilities();
		// 获取所有对象数据中的增益效果数据
		final ObjectData buffData = allObjectData.getBuffs();
		// 获取所有对象数据中的单位数据
		final ObjectData unitData = allObjectData.getUnits();
		// 获取所有对象数据中的物品数据
		final ObjectData itemData = allObjectData.getItems();
		// 获取所有对象数据中的升级数据
		final ObjectData upgradeData = allObjectData.getUpgrades();
		// 获取游戏UI皮肤中命令按钮禁用状态的图片路径
		this.disabledPrefix = gameUI.getSkinField("CommandButtonDisabledArtPath");

		// 遍历能力数据键值对
		for (final String alias : abilityData.keySet()) {
			// 获取能力类型数据对象
			final GameObject abilityTypeData = abilityData.get(alias);

			// 尝试获取不同状态下的图标路径
			final String iconResearchPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(ICON_RESEARCH, 0));
			final String iconNormalPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(ICON_NORMAL, 0));
			final String iconTurnOffPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(ICON_TURN_OFF, 0));

			// 获取图标快捷键
			final char iconHotkey = getHotkey(abilityTypeData, ABILITY_HOTKEY_NORMAL);
			final char iconTurnOffHotkey = getHotkey(abilityTypeData, ABILITY_HOTKEY_TURNOFF);

			// 获取研究提示信息
			final String iconResearchTip = abilityTypeData.getFieldAsString(ABILITY_RESEARCH_TIP, 0);
			final String iconResearchUberTip = parseUbertip(allObjectData,
					abilityTypeData.getFieldAsString(ABILITY_RESEARCH_UBER_TIP, 0));
			final char iconResearchHotkey = getHotkey(abilityTypeData, ABILITY_HOTKEY_LEARN);

			// 获取图标位置信息
			final int iconResearchX = abilityTypeData.getFieldAsInteger(ICON_RESEARCH_XY, 0);
			final int iconResearchY = abilityTypeData.getFieldAsInteger(ICON_RESEARCH_XY, 1);
			final int iconNormalX = abilityTypeData.getFieldAsInteger(ICON_NORMAL_XY, 0);
			final int iconNormalY = abilityTypeData.getFieldAsInteger(ICON_NORMAL_XY, 1);
			final int iconTurnOffX = abilityTypeData.getFieldAsInteger(ICON_TURN_OFF_XY, 0);
			final int iconTurnOffY = abilityTypeData.getFieldAsInteger(ICON_TURN_OFF_XY, 1);

			// 加载图标纹理
			final Texture iconResearch = gameUI.loadTexture(iconResearchPath);
			final Texture iconResearchDisabled = gameUI.loadTexture(disable(iconResearchPath, this.disabledPrefix));
			final Texture iconNormal = gameUI.loadTexture(iconNormalPath);
			final Texture iconNormalDisabled = gameUI.loadTexture(disable(iconNormalPath, this.disabledPrefix));
			final Texture iconTurnOff = gameUI.loadTexture(iconTurnOffPath);
			final Texture iconTurnOffDisabled = gameUI.loadTexture(disable(iconTurnOffPath, this.disabledPrefix));

			// 创建图标UI列表
			final List<IconUI> turnOffIconUIs = new ArrayList<>();
			final List<IconUI> normalIconUIs = new ArrayList<>();

			// 获取能力等级
			final int levels = Math.max(1, abilityTypeData.getFieldAsInteger(AbilityFields.LEVELS, 0));

			// 根据能力等级创建图标UI
			for (int i = 0; i < levels; i++) {
				// 获取提示信息
				final String iconTip = abilityTypeData.getFieldAsString(ABILITY_TIP, i);
				final String iconUberTip = parseUbertip(allObjectData,
						abilityTypeData.getFieldAsString(ABILITY_UBER_TIP, i));
				final String iconTurnOffTip = abilityTypeData.getFieldAsString(ABILITY_UN_TIP, i);
				final String iconTurnOffUberTip = parseUbertip(allObjectData,
						abilityTypeData.getFieldAsString(ABILITY_UN_UBER_TIP, i));

				// 添加图标UI到列表
				normalIconUIs.add(new IconUI(iconNormal, iconNormalDisabled, iconNormalX, iconNormalY, iconTip,
						iconUberTip, iconHotkey));
				turnOffIconUIs.add(new IconUI(iconTurnOff, iconTurnOffDisabled, iconTurnOffX, iconTurnOffY,
						iconTurnOffTip, iconTurnOffUberTip, iconTurnOffHotkey));
			}
			// 创建一个列表来存储施法者的艺术资源
			final List<EffectAttachmentUI> casterArt = new ArrayList<>();
			// 获取施法者艺术资源的路径列表
			final List<String> casterArtPaths = abilityTypeData.getFieldAsList(CASTER_ART);
			// 获取施法者艺术资源的附件数量，默认为0
			final int casterAttachmentCount = abilityTypeData.getFieldAsInteger(CASTER_ART_ATTACHMENT_COUNT, 0);
			// 计算施法者艺术资源附件索引的最大值
			final int casterAttachmentIndexMax = Math.min(casterAttachmentCount - 1, casterArtPaths.size() - 1);
			// 计算施法者艺术资源附件的迭代次数
			final int casterIteratorCount = Math.max(casterAttachmentCount, casterArtPaths.size());
			// 遍历施法者艺术资源附件
			for (int i = 0; i < casterIteratorCount; i++) {
				// 获取当前索引下的模型路径，确保索引不越界
				final String modelPath = casterArtPaths.get(Math.max(0, Math.min(i, casterAttachmentIndexMax)));
				// 尝试获取附件点键
				final String attachmentPointKey = tryGet(CASTER_ART_ATTACHMENT_POINT, i);
				// 获取附件点列表
				final List<String> attachmentPoints = abilityTypeData.getFieldAsList(attachmentPointKey);
				// 创建EffectAttachmentUI对象并添加到施法者艺术资源列表中
				casterArt.add(new EffectAttachmentUI(modelPath, attachmentPoints));
			}

			// 创建一个列表来存储目标的艺术资源
			final List<EffectAttachmentUI> targetArt = new ArrayList<>();
			// 获取目标艺术资源的路径列表
			final List<String> targetArtPaths = abilityTypeData.getFieldAsList(TARGET_ART);
			// 获取目标艺术资源的附件数量，默认为0
			final int targetAttachmentCount = abilityTypeData.getFieldAsInteger(TARGET_ART_ATTACHMENT_COUNT, 0);
			// 计算目标艺术资源附件索引的最大值
			final int targetAttachmentIndexMax = Math.min(targetAttachmentCount - 1, targetArtPaths.size() - 1);
			// 计算目标艺术资源附件的迭代次数
			final int targetIteratorCount = Math.max(targetAttachmentCount, targetArtPaths.size());
			// 遍历目标艺术资源附件
			for (int i = 0; i < targetIteratorCount; i++) {
				// 获取当前索引下的模型路径，确保索引不越界
				final String modelPath = targetArtPaths.get(Math.max(0, Math.min(i, targetAttachmentIndexMax)));
				// 尝试获取附件点键
				final String attachmentPointKey = tryGet(TARGET_ART_ATTACHMENT_POINT, i);
				// 获取附件点列表
				final List<String> attachmentPoints = abilityTypeData.getFieldAsList(attachmentPointKey);
				// 创建EffectAttachmentUI对象并添加到目标艺术资源列表中
				targetArt.add(new EffectAttachmentUI(modelPath, attachmentPoints));
			}

			// 创建一个用于存储特殊艺术效果的列表
			final List<EffectAttachmentUI> specialArt = new ArrayList<>();
			// 从能力类型数据中获取特殊艺术效果的路径，并以逗号分隔转换为列表
			final List<String> specialArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(SPECIAL_ART, 0).split(","));
			// 遍历特殊艺术效果路径列表
			for (int i = 0; i < specialArtPaths.size(); i++) {
				// 获取当前路径
				final String modelPath = specialArtPaths.get(i);
				// 从能力类型数据中获取特殊艺术效果的附着点，并以逗号分隔转换为列表
				final List<String> attachmentPoints = Arrays
						.asList(abilityTypeData.getFieldAsString(SPECIAL_ART_ATTACHMENT_POINT, 0).split(","));
				// 创建一个新的EffectAttachmentUI对象，并添加到特殊艺术效果列表中
				specialArt.add(new EffectAttachmentUI(modelPath, attachmentPoints));
			}

			// 创建一个用于存储效果艺术效果的列表
			final List<EffectAttachmentUI> effectArt = new ArrayList<>();
			// 从能力类型数据中获取效果艺术效果的路径，并以逗号分隔转换为列表
			final List<String> effectArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(EFFECT_ART, 0).split(","));
			// 遍历效果艺术效果路径列表
			for (int i = 0; i < effectArtPaths.size(); i++) {
				// 获取当前路径
				final String modelPath = effectArtPaths.get(i);
				// TODO: 如果这个与增益效果等一起使用，可能会因为使用能力元数据在增益元数据上而破坏，这在很多方面都不好
				// 从能力类型数据中读取SLK标签中的Effectattach字段
				final String effectAttach = abilityTypeData.readSLKTag("Effectattach");
				// 如果Effectattach字段为空或不存在，则使用空列表，否则将其以逗号分隔转换为列表
				final List<String> attachmentPoints = ((effectAttach == null) || effectAttach.isEmpty())
						? Collections.emptyList()
						: Arrays.asList(effectAttach);
				// 创建一个新的EffectAttachmentUI对象，并添加到效果艺术效果列表中
				effectArt.add(new EffectAttachmentUI(modelPath, attachmentPoints));
			}


			// 创建一个用于存储区域效果艺术图的列表
			final List<EffectAttachmentUI> areaEffectArt = new ArrayList<>();
			// 从能力类型数据中获取区域效果艺术图的路径，并以逗号分隔转换为列表
			final List<String> areaEffectArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(AREA_EFFECT_ART, 0).split(","));
			// 遍历路径列表，为每个路径创建一个新的EffectAttachmentUI对象并添加到areaEffectArt列表中
			for (final String areaEffectArtPath : areaEffectArtPaths) {
				areaEffectArt.add(new EffectAttachmentUI(areaEffectArtPath, Collections.emptyList()));
			}

			// 创建一个用于存储导弹效果艺术图的列表
			final List<EffectAttachmentUIMissile> missileArt = new ArrayList<>();
			// 从能力类型数据中获取导弹效果艺术图的路径，并以逗号分隔转换为列表
			final List<String> missileArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(MISSILE_ART, 0).split(","));

			// 从能力类型数据中获取导弹的弧度值
			final float missileArc = abilityTypeData.getFieldAsFloat(MISSILE_ARC, 0);
			// 遍历导弹艺术图路径列表，为每个路径创建一个新的EffectAttachmentUIMissile对象并添加到missileArt列表中
			for (final String missileArtPath : missileArtPaths) {
				missileArt.add(new EffectAttachmentUIMissile(missileArtPath, Collections.emptyList(), missileArc));
			}

			// 从能力类型数据中获取闪电效果名称的字符串，并以逗号分隔转换为列表
			final List<String> LightningEffectList = Arrays
					.asList(abilityTypeData.getFieldAsString(LIGHTNING_EFFECTS, 0).split(","));
			// 创建一个用于存储War3ID对象的列表，这些对象代表不同的闪电效果
			final List<War3ID> LightningEffects = new ArrayList<>();

			// 遍历闪电效果名称列表，将非空且非空白的名称转换为War3ID对象并添加到LightningEffects列表中
			for (final String lightning : LightningEffectList) {
				if ((lightning != null) && !lightning.isBlank()) {
					LightningEffects.add(War3ID.fromString(lightning));
				}
			}

			// 从能力类型数据中获取效果声音和循环效果声音的字符串
			final String effectSound = abilityTypeData.getFieldAsString(ABILITY_EFFECT_SOUND, 0);
			final String effectSoundLooped = abilityTypeData.getFieldAsString(ABILITY_EFFECT_SOUND_LOOPED, 0);

			// 将别名转换为War3ID对象，并创建一个新的AbilityUI对象，将所有收集到的数据作为参数传递
			// 然后将这个AbilityUI对象与对应的War3ID对象一起存储到rawcodeToUI映射中
			this.rawcodeToUI.put(War3ID.fromString(alias),
					new AbilityUI(
							new IconUI(iconResearch, iconResearchDisabled, iconResearchX, iconResearchY,
									iconResearchTip, iconResearchUberTip, iconResearchHotkey),
							normalIconUIs, turnOffIconUIs, casterArt, targetArt, specialArt, effectArt, areaEffectArt,
							missileArt, LightningEffects, effectSound, effectSoundLooped));

		}

		for (final String alias : buffData.keySet()) {
			// TODO pretty sure that in WC3 the buffs and abilities are stored in the same
			// table, but I was already using an object editor tab emulator that I wrote
			// previously and so it has these divided...
			// 从buffData中获取能力类型数据
			final GameObject abilityTypeData = buffData.get(alias);

			// 尝试获取能力的普通图标路径
			final String iconNormalPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(BUFF_ICON_NORMAL, 0));
			// 获取能力的提示信息
			final String iconTip = abilityTypeData.getFieldAsString(BUFF_ABILITY_TIP, 0);
			// 解析能力的超级提示信息
			final String iconUberTip = parseUbertip(allObjectData,
					abilityTypeData.getFieldAsString(BUFF_ABILITY_UBER_TIP, 0));
			// 加载能力的普通图标纹理
			final Texture iconNormal = gameUI.loadTexture(iconNormalPath);
			// 加载能力的普通图标纹理（禁用状态）
			final Texture iconNormalDisabled = gameUI.loadTexture(disable(iconNormalPath, this.disabledPrefix));

			// 初始化目标艺术效果列表
			final List<EffectAttachmentUI> targetArt = new ArrayList<>();
			// 获取目标艺术效果的路径列表
			final List<String> targetArtPaths = abilityTypeData.getFieldAsList(BUFF_TARGET_ART);
			// 获取目标附件的数量
			final int targetAttachmentCount = abilityTypeData.getFieldAsInteger(BUFF_TARGET_ART_ATTACHMENT_COUNT, 0);
			// 计算目标附件索引的最大值
			final int targetAttachmentIndexMax = Math.min(targetAttachmentCount - 1, targetArtPaths.size() - 1);
			// 计算目标迭代器的数量
			final int targetIteratorCount = Math.max(targetAttachmentCount, targetArtPaths.size());
			// 遍历目标艺术效果路径列表，创建EffectAttachmentUI对象并添加到目标艺术效果列表中
			for (int i = 0; i < targetIteratorCount; i++) {
				final String modelPath = targetArtPaths.isEmpty() ? ""
						: targetArtPaths.get(Math.max(0, Math.min(i, targetAttachmentIndexMax)));
				final String attachmentPointKey = tryGet(BUFF_TARGET_ART_ATTACHMENT_POINT, i);
				final List<String> attachmentPoints = abilityTypeData.getFieldAsList(attachmentPointKey);
				targetArt.add(new EffectAttachmentUI(modelPath, attachmentPoints));
			}

			// 初始化特殊艺术效果列表
			final List<EffectAttachmentUI> specialArt = new ArrayList<>();
			// 获取特殊艺术效果的路径列表
			final List<String> specialArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(BUFF_SPECIAL_ART, 0).split(","));
			// 遍历特殊艺术效果路径列表，创建EffectAttachmentUI对象并添加到特殊艺术效果列表中
			for (int i = 0; i < specialArtPaths.size(); i++) {
				final String modelPath = specialArtPaths.get(i);
				final List<String> attachmentPoints = Arrays
						.asList(abilityTypeData.getFieldAsString(BUFF_SPECIAL_ART_ATTACHMENT_POINT, 0).split(","));
				specialArt.add(new EffectAttachmentUI(modelPath, attachmentPoints));
			}

			// 初始化效果艺术效果列表
			final List<EffectAttachmentUI> effectArt = new ArrayList<>();
			// 获取效果艺术效果的路径列表
			final List<String> effectArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(BUFF_EFFECT_ART, 0).split(","));
			// 遍历效果艺术效果路径列表，创建EffectAttachmentUI对象并添加到效果艺术效果列表中
			for (int i = 0; i < effectArtPaths.size(); i++) {
				final String modelPath = effectArtPaths.get(i);
				// TODO: 如果这个与buffs或其他东西一起使用，可能会因为使用能力元数据在buff元数据上而破坏，这在很多方面都是不好的
				final String effectAttach = abilityTypeData.readSLKTag("Effectattach");
				final List<String> attachmentPoints = ((effectAttach == null) || effectAttach.isEmpty())
						? Collections.emptyList()
						: Arrays.asList(effectAttach);
				effectArt.add(new EffectAttachmentUI(modelPath, attachmentPoints));
			}

			// 初始化导弹艺术效果列表
			final List<EffectAttachmentUI> missileArt = new ArrayList<>();
			// 获取导弹艺术效果的路径列表
			final List<String> missileArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(BUFF_MISSILE_ART, 0).split(","));
			// 遍历导弹艺术效果路径列表，创建EffectAttachmentUI对象并添加到导弹艺术效果列表中
			for (final String missileArtPath : missileArtPaths) {
				missileArt.add(new EffectAttachmentUI(missileArtPath, Collections.emptyList()));
			}

			// 获取能力效果声音
			final String effectSound = abilityTypeData.getFieldAsString(BUFF_ABILITY_EFFECT_SOUND, 0);
			// 获取循环的能力效果声音
			final String effectSoundLooped = abilityTypeData.getFieldAsString(BUFF_ABILITY_EFFECT_SOUND_LOOPED, 0);

			// 将能力元数据转换为BuffUI对象，并存储到rawcodeToBuffUI映射中
			this.rawcodeToBuffUI.put(War3ID.fromString(alias),
					new BuffUI(new IconUI(iconNormal, iconNormalDisabled, 0, 0, iconTip, iconUberTip, '\0'), targetArt,
							specialArt, effectArt, missileArt, effectSound, effectSoundLooped));

		}
		for (final String alias : unitData.keySet()) {
			// 获取单位数据中与别名对应的游戏对象
			final GameObject abilityTypeData = unitData.get(alias);
			// 尝试从游戏UI中获取能力的普通图标路径
			final String iconNormalPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(UNIT_ICON_NORMAL, 0));
			// 获取能力图标的X坐标
			final int iconNormalX = abilityTypeData.getFieldAsInteger(UNIT_ICON_NORMAL_XY, 0);
			// 获取能力图标的Y坐标
			final int iconNormalY = abilityTypeData.getFieldAsInteger(UNIT_ICON_NORMAL_XY, 1);
			// 获取能力的普通提示信息
			final String iconTip = abilityTypeData.getFieldAsString(UNIT_TIP, 0);
			// 获取能力的复活提示信息
			final String reviveTip = abilityTypeData.getFieldAsString(UNIT_REVIVE_TIP, 0);
			// 获取能力的觉醒提示信息
			final String awakenTip = abilityTypeData.getFieldAsString(UNIT_AWAKEN_TIP, 0);
			// 解析能力的超级提示信息
			final String iconUberTip = parseUbertip(allObjectData, abilityTypeData.getFieldAsString(UNIT_UBER_TIP, 0));
			// 获取能力图标的快捷键
			final char iconHotkey = getHotkey(abilityTypeData, UNIT_HOTKEY);
			// 加载能力的普通图标纹理
			final Texture iconNormal = gameUI.loadTexture(iconNormalPath);
			// 加载能力的禁用状态下的图标纹理
			final Texture iconNormalDisabled = gameUI.loadTexture(disable(iconNormalPath, this.disabledPrefix));
			// 将单位图标UI添加到rawcodeToUnitUI映射中
			this.rawcodeToUnitUI.put(War3ID.fromString(alias), new UnitIconUI(iconNormal, iconNormalDisabled,
				   iconNormalX, iconNormalY, iconTip, iconUberTip, iconHotkey, reviveTip, awakenTip));

		}
		for (final String alias : itemData.keySet()) {
			// 从itemData中获取与alias对应的GameObject对象
			final GameObject abilityTypeData = itemData.get(alias);
			// 尝试从游戏UI中获取能力图标的正常状态路径
			final String iconNormalPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(ITEM_ICON_NORMAL, 0));
			// 获取能力图标在正常状态下的X坐标
			final int iconNormalX = abilityTypeData.getFieldAsInteger(ITEM_ICON_NORMAL_XY, 0);
			// 获取能力图标在正常状态下的Y坐标
			final int iconNormalY = abilityTypeData.getFieldAsInteger(ITEM_ICON_NORMAL_XY, 1);
			// 获取能力图标的提示信息
			final String iconTip = abilityTypeData.getFieldAsString(ITEM_TIP, 0);
			// 解析能力图标的超级提示信息
			final String iconUberTip = parseUbertip(allObjectData, abilityTypeData.getFieldAsString(ITEM_UBER_TIP, 0));
			// 获取能力图标的描述信息
			final String iconDescription = abilityTypeData.getFieldAsString(ITEM_DESCRIPTION, 0);
			// 获取能力图标的快捷键
			final char iconHotkey = getHotkey(abilityTypeData, ITEM_HOTKEY);
			// 加载能力图标的正常状态纹理
			final Texture iconNormal = gameUI.loadTexture(iconNormalPath);
			// 加载能力图标的禁用状态纹理
			final Texture iconNormalDisabled = gameUI.loadTexture(disable(iconNormalPath, this.disabledPrefix));
			// 将解析后的ItemUI对象存入rawcodeToItemUI映射中，以War3ID作为键
			this.rawcodeToItemUI.put(War3ID.fromString(alias),
					new ItemUI(
							new IconUI(iconNormal, iconNormalDisabled, iconNormalX, iconNormalY, iconTip,
									iconUberTip, iconHotkey),
							abilityTypeData.getName(), iconDescription, iconNormalPath));

		}
		for (final String alias : upgradeData.keySet()) {
			// 从upgradeData中获取与alias对应的GameObject对象
			final GameObject upgradeTypeData = upgradeData.get(alias);
			// 获取该对象的升级等级，如果没有则默认为0
			final int upgradeLevels = upgradeTypeData.getFieldAsInteger(UPGRADE_LEVELS, 0);
			// 获取图标在正常状态下的X坐标
			final int iconNormalX = upgradeTypeData.getFieldAsInteger(UPGRADE_ICON_NORMAL_XY, 0);
			// 获取图标在正常状态下的Y坐标
			final int iconNormalY = upgradeTypeData.getFieldAsInteger(UPGRADE_ICON_NORMAL_XY, 1);
			// 创建一个列表用于存储每个等级对应的图标UI
			final List<IconUI> upgradeIconsByLevel = new ArrayList<>();

			// 遍历每个升级等级
			for (int upgradeLevelValue = 0; upgradeLevelValue < upgradeLevels; upgradeLevelValue++) {
				// 获取当前等级的图标提示信息
				final String iconTip = upgradeTypeData.getFieldAsString(UPGRADE_TIP, upgradeLevelValue);
				// 解析当前等级的图标超级提示信息
				final String iconUberTip = parseUbertip(allObjectData,
						upgradeTypeData.getFieldAsString(UPGRADE_UBER_TIP, upgradeLevelValue));
				// 获取当前等级图标的正常状态路径
				final String iconNormalPath = gameUI
						.trySkinField(upgradeTypeData.getFieldAsString(UPGRADE_ICON_NORMAL, upgradeLevelValue));
				// 获取当前等级图标的快捷键
				final char iconHotkey = getHotkey(upgradeTypeData, UPGRADE_HOTKEY, upgradeLevelValue);
				// 加载图标的正常状态纹理
				final Texture iconNormal = gameUI.loadTexture(iconNormalPath);
				// 加载图标的禁用状态纹理
				final Texture iconNormalDisabled = gameUI.loadTexture(disable(iconNormalPath, this.disabledPrefix));
				// 创建IconUI对象并添加到列表中
				upgradeIconsByLevel.add(new IconUI(iconNormal, iconNormalDisabled, iconNormalX, iconNormalY, iconTip,
						iconUberTip, iconHotkey));
			}

			// 将alias对应的图标UI列表存入rawcodeToUpgradeUI映射中
			this.rawcodeToUpgradeUI.put(War3ID.fromString(alias), upgradeIconsByLevel);

		}
		this.moveUI = createBuiltInIconUI(gameUI, "CmdMove", this.disabledPrefix);
		this.stopUI = createBuiltInIconUI(gameUI, "CmdStop", this.disabledPrefix);
		this.holdPosUI = createBuiltInIconUI(gameUI, "CmdHoldPos", this.disabledPrefix);
		this.patrolUI = createBuiltInIconUI(gameUI, "CmdPatrol", this.disabledPrefix);
		this.attackUI = createBuiltInIconUI(gameUI, "CmdAttack", this.disabledPrefix);
		this.buildHumanUI = createBuiltInIconUI(gameUI, "CmdBuildHuman", this.disabledPrefix);
		this.buildOrcUI = createBuiltInIconUI(gameUI, "CmdBuildOrc", this.disabledPrefix);
		this.buildNightElfUI = createBuiltInIconUI(gameUI, "CmdBuildNightElf", this.disabledPrefix);
		this.buildUndeadUI = createBuiltInIconUI(gameUI, "CmdBuildUndead", this.disabledPrefix);
		this.buildNagaUI = createBuiltInIconUISplit(gameUI, "CmdBuildNaga", "CmdBuildOrc",
				abilityData.get(War3ID.fromString("AGbu")), this.disabledPrefix);
		this.buildNeutralUI = createBuiltInIconUI(gameUI, "CmdBuild", this.disabledPrefix);
		this.attackGroundUI = createBuiltInIconUI(gameUI, "CmdAttackGround", this.disabledPrefix);
		this.cancelUI = createBuiltInIconUI(gameUI, "CmdCancel", this.disabledPrefix);
		this.cancelBuildUI = createBuiltInIconUI(gameUI, "CmdCancelBuild", this.disabledPrefix);
		this.cancelTrainUI = createBuiltInIconUI(gameUI, "CmdCancelTrain", this.disabledPrefix);
		this.rallyUI = createBuiltInIconUI(gameUI, "CmdRally", this.disabledPrefix);
		this.selectSkillUI = createBuiltInIconUI(gameUI, "CmdSelectSkill", this.disabledPrefix);
		this.neutralInteractUI = getUI(War3ID.fromString("Anei")).getOnIconUI(0);
	}

	private static String parseUbertip(final Warcraft3MapRuntimeObjectData allObjectData, final String originalText) {
		String tooltipText = originalText;
		int openBracketIndex = tooltipText.indexOf('<');
		int closeBracketIndex = tooltipText.indexOf('>');
		while ((openBracketIndex < closeBracketIndex) && (openBracketIndex != -1)) {
			final String textBefore = tooltipText.substring(0, openBracketIndex);
			final String textAfter = tooltipText.substring(closeBracketIndex + 1);
			final String codeText = tooltipText.substring(openBracketIndex + 1, closeBracketIndex);
			final String[] codeTextParts = codeText.split(",");
			String valueText = "";
			boolean percent = false;
			if (((codeTextParts.length == 2)
					|| ((codeTextParts.length == 3) && (percent = "%".equals(codeTextParts[2]))))) {
				final String rawcode = codeTextParts[0];
				GameObject unit = allObjectData.getUnits().get(rawcode);
				if (unit == null) {
					unit = allObjectData.getItems().get(rawcode);
				}
				if (unit == null) {
					unit = allObjectData.getAbilities().get(rawcode);
				}
				if (unit != null) {
					if (percent) {
						valueText = Integer.toString((int) (unit.readSLKTagFloat(codeTextParts[1]) * 100f));
					}
					else {
						valueText = unit.readSLKTag(codeTextParts[1]);
					}
				}
				else {
					valueText = codeText + "{missing}";
				}
			}

			// TODO less java.lang.String memory allocation here could be achieved using one
			// string builder for all loop iterations
			tooltipText = textBefore + valueText + textAfter;

			openBracketIndex = tooltipText.indexOf('<');
			closeBracketIndex = tooltipText.indexOf('>');
		}
		return tooltipText;
	}

	private char getHotkey(final GameObject abilityTypeData, final String abilityHotkeyNormal) {
		return getHotkey(abilityTypeData, abilityHotkeyNormal, 0);
	}

	private char getHotkey(final GameObject abilityTypeData, final String abilityHotkeyNormal, final int index) {
		final String iconHotkeyString = abilityTypeData.getFieldAsString(abilityHotkeyNormal, index);
		final char itemHotkey = getHotkeyChar(iconHotkeyString);
		return itemHotkey;
	}

	private IconUI createBuiltInIconUI(final GameUI gameUI, final String key, final String disabledPrefix) {
		final Element builtInAbility = gameUI.getSkinData().get(key);
		final String iconPath = gameUI.trySkinField(builtInAbility.getField("Art"));
		final Texture icon = gameUI.loadTexture(iconPath);
		final Texture iconDisabled = gameUI.loadTexture(disable(iconPath, disabledPrefix));
		final int buttonPositionX = builtInAbility.getFieldValue("Buttonpos", 0);
		final int buttonPositionY = builtInAbility.getFieldValue("Buttonpos", 1);
		final String tip = builtInAbility.getField("Tip");
		final String uberTip = builtInAbility.getField("UberTip");
		final String hotkeyString = builtInAbility.getField("Hotkey");
		final char hotkey = getHotkeyChar(hotkeyString);
		return new IconUI(icon, iconDisabled, buttonPositionX, buttonPositionY, tip, uberTip, hotkey);
	}

	private IconUI createBuiltInIconUISplit(final GameUI gameUI, final String key, final String funckey,
			final GameObject worldEditorObject, final String disabledPrefix) {
		final Element builtInAbility = gameUI.getSkinData().get(key);
		final Element builtInAbilityFunc = gameUI.getSkinData().get(funckey);
		String iconPath = gameUI.trySkinField(builtInAbilityFunc.getField("Art"));
		final String worldEditorValue = worldEditorObject.getField("Art");
		if (worldEditorValue.length() > 0) {
			iconPath = worldEditorValue;
		}
		final Texture icon = gameUI.loadTexture(iconPath);
		final Texture iconDisabled = gameUI.loadTexture(disable(iconPath, disabledPrefix));
		final int buttonPositionX = builtInAbilityFunc.getFieldValue("Buttonpos", 0);
		final int buttonPositionY = builtInAbilityFunc.getFieldValue("Buttonpos", 1);
		final String tip = builtInAbility.getField("Tip");
		final String uberTip = builtInAbility.getField("UberTip");
		final String hotkeyString = builtInAbility.getField("Hotkey");
		final char hotkey = getHotkeyChar(hotkeyString);
		return new IconUI(icon, iconDisabled, buttonPositionX, buttonPositionY, tip, uberTip, hotkey);
	}

	private char getHotkeyChar(final String hotkeyString) {
		if (hotkeyString.length() > 1) {
			boolean anyNonDigit = false;
			for (int i = 0; i < hotkeyString.length(); i++) {
				if (!Character.isDigit(hotkeyString.charAt(i))) {
					anyNonDigit = true;
				}
			}
			if (!anyNonDigit) {
				final int hotkeyInt = Integer.parseInt(hotkeyString);
				if (hotkeyInt == 512) {
					return WarsmashConstants.SPECIAL_ESCAPE_KEYCODE;
				}
				return (char) hotkeyInt;
			}
			else {
				String resultStr;
				try {
					resultStr = new String(hotkeyString.getBytes(), "utf-8");
				}
				catch (final UnsupportedEncodingException e) {
					e.printStackTrace();
					return '\0';
				}
				final char result = resultStr.charAt(0);
				System.out.println("weird hotkey: " + result);
				return result;
			}
		}
		return hotkeyString.length() > 0 ? hotkeyString.charAt(0) : '\0';
	}

	public AbilityUI getUI(final War3ID rawcode) {
		return this.rawcodeToUI.get(rawcode);
	}

	public BuffUI getBuffUI(final War3ID rawcode) {
		return this.rawcodeToBuffUI.get(rawcode);
	}

	public UnitIconUI getUnitUI(final War3ID rawcode) {
		return this.rawcodeToUnitUI.get(rawcode);
	}

	public ItemUI getItemUI(final War3ID rawcode) {
		return this.rawcodeToItemUI.get(rawcode);
	}

	public IconUI getUpgradeUI(final War3ID rawcode, final int level) {
		final List<IconUI> upgradeUI = this.rawcodeToUpgradeUI.get(rawcode);
		if (upgradeUI != null) {
			if (level < upgradeUI.size()) {
				return upgradeUI.get(level);
			}
			else {
				return upgradeUI.get(upgradeUI.size() - 1);
			}
		}
		return null;
	}

	public static String disable(final String path, final String disabledPrefix) {
		final int slashIndex = path.lastIndexOf('\\');
		String name = path;
		if (slashIndex != -1) {
			name = path.substring(slashIndex + 1);
		}
		return disabledPrefix + "DIS" + name;
	}

	public IconUI getMoveUI() {
		return this.moveUI;
	}

	public IconUI getStopUI() {
		return this.stopUI;
	}

	public IconUI getHoldPosUI() {
		return this.holdPosUI;
	}

	public IconUI getPatrolUI() {
		return this.patrolUI;
	}

	public IconUI getAttackUI() {
		return this.attackUI;
	}

	public IconUI getAttackGroundUI() {
		return this.attackGroundUI;
	}

	public IconUI getBuildHumanUI() {
		return this.buildHumanUI;
	}

	public IconUI getBuildNightElfUI() {
		return this.buildNightElfUI;
	}

	public IconUI getBuildOrcUI() {
		return this.buildOrcUI;
	}

	public IconUI getBuildUndeadUI() {
		return this.buildUndeadUI;
	}

	public IconUI getBuildNagaUI() {
		return this.buildNagaUI;
	}

	public IconUI getBuildNeutralUI() {
		return this.buildNeutralUI;
	}

	public IconUI getCancelUI() {
		return this.cancelUI;
	}

	public IconUI getCancelBuildUI() {
		return this.cancelBuildUI;
	}

	public IconUI getCancelTrainUI() {
		return this.cancelTrainUI;
	}

	public IconUI getRallyUI() {
		return this.rallyUI;
	}

	public IconUI getSelectSkillUI() {
		return this.selectSkillUI;
	}

	public IconUI getNeutralInteractUI() {
		return this.neutralInteractUI;
	}

	public String getDisabledPrefix() {
		return this.disabledPrefix;
	}

	private String tryGet(final String[] ids, final int index) {
		if ((index >= 0) && (index < ids.length)) {
			return ids[index];
		}
		return ids[ids.length - 1];
	}
}
