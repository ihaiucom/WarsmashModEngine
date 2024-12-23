package com.etheller.warsmash.parsers.w3x.w3i;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

/**
 * war3map.w3i - the general map information file.
 */
public class War3MapW3i {
	// 版本号
	private int version;
	// 保存次数
	private int saves;
	// 编辑器版本号
	private int editorVersion;
	// 游戏主版本号
	private int gameVersionMajor;
	// 游戏次版本号
	private int gameVersionMinor;
	// 游戏补丁版本号
	private int gameVersionPatch;
	// 游戏构建版本号
	private int gameVersionBuild;
	// 地图名称
	private String name;
	// 地图作者
	private String author;
	// 地图描述
	private String description;
	// 推荐玩家数
	private String recommendedPlayers;
	// 相机边界数组，用于定义游戏中的相机移动范围
	private final float[] cameraBounds = new float[8];
	// 相机边界补充数组，可能与相机边界配合使用
	private final int[] cameraBoundsComplements = new int[4];
	// 可玩区域大小，定义地图的可玩区域尺寸
	private final int[] playableSize = new int[2];
	// 标志位，可能包含地图的各种属性和设置
	private long flags;
	// 地图使用的瓷砖集，默认为'A'
	private char tileset = 'A';
	// 战役背景
	private int campaignBackground;
	// 加载屏幕模型
	private String loadingScreenModel;
	// 加载屏幕文本
	private String loadingScreenText;
	// 加载屏幕标题
	private String loadingScreenTitle;
	// 加载屏幕副标题
	private String loadingScreenSubtitle;
	// 加载屏幕设置
	private int loadingScreen;
	// 序章屏幕模型
	private String prologueScreenModel;
	// 序章屏幕文本
	private String prologueScreenText;
	// 序章屏幕标题
	private String prologueScreenTitle;
	// 序章屏幕副标题
	private String prologueScreenSubtitle;
	// 是否使用地形雾效
	private int useTerrainFog;
	// 雾高度数组，定义雾的起始和结束高度
	private final float[] fogHeight = new float[2];
	// 雾密度
	private float fogDensity;
	// 雾颜色数组，定义雾的颜色
	private final short[] fogColor = new short[4];
	// 全局天气设置
	private int globalWeather;
	// 声音环境
	private String soundEnvironment;
	// 光照环境瓷砖集
	private char lightEnvironmentTileset;
	// 水面顶点颜色数组，定义水面的颜色
	private final short[] waterVertexColor = new short[4];
	// 未知属性，可能是Lua脚本相关的数据
	private final short[] unknown2ProbablyLua = new short[4];
	// 玩家列表
	private final List<Player> players = new ArrayList<>();
	// 势力列表
	private final List<Force> forces = new ArrayList<>();
	// 升级可用性变更列表
	private final List<UpgradeAvailabilityChange> upgradeAvailabilityChanges = new ArrayList<>();
	// 科技可用性变更列表
	private final List<TechAvailabilityChange> techAvailabilityChanges = new ArrayList<>();
	// 随机单位表列表
	private final List<RandomUnitTable> randomUnitTables = new ArrayList<>();
	// 随机物品表列表
	private final List<RandomItemTable> randomItemTables = new ArrayList<>();


	public War3MapW3i(final LittleEndianDataInputStream stream) throws IOException {
		if (stream != null) {
			load(stream);
		}
	}

	private void load(final LittleEndianDataInputStream stream) throws IOException {
		this.version = stream.readInt();
		this.saves = stream.readInt();
		this.editorVersion = stream.readInt();

		if (this.version > 27) {
			this.gameVersionMajor = stream.readInt();
			this.gameVersionMinor = stream.readInt();
			this.gameVersionPatch = stream.readInt();
			this.gameVersionBuild = stream.readInt();
		}

		this.name = ParseUtils.readUntilNull(stream);
		this.author = ParseUtils.readUntilNull(stream);
		this.description = ParseUtils.readUntilNull(stream);
		this.recommendedPlayers = ParseUtils.readUntilNull(stream);
		ParseUtils.readFloatArray(stream, this.cameraBounds);
		ParseUtils.readInt32Array(stream, this.cameraBoundsComplements);
		ParseUtils.readInt32Array(stream, this.playableSize);
		this.flags = ParseUtils.readUInt32(stream);
		this.tileset = (char) stream.read();
		this.campaignBackground = stream.readInt();

		if (this.version > 24) {
			this.loadingScreenModel = ParseUtils.readUntilNull(stream);
		}

		this.loadingScreenText = ParseUtils.readUntilNull(stream);
		this.loadingScreenTitle = ParseUtils.readUntilNull(stream);
		this.loadingScreenSubtitle = ParseUtils.readUntilNull(stream);
		this.loadingScreen = stream.readInt();

		if (this.version > 24) {
			this.prologueScreenModel = ParseUtils.readUntilNull(stream);
		}

		this.prologueScreenText = ParseUtils.readUntilNull(stream);
		this.prologueScreenTitle = ParseUtils.readUntilNull(stream);
		this.prologueScreenSubtitle = ParseUtils.readUntilNull(stream);

		if (this.version > 24) {
			this.useTerrainFog = stream.readInt();
			ParseUtils.readFloatArray(stream, this.fogHeight);
			this.fogDensity = stream.readFloat();
			ParseUtils.readUInt8Array(stream, this.fogColor);
			this.globalWeather = stream.readInt(); // TODO probably war3id, right?
			this.soundEnvironment = ParseUtils.readUntilNull(stream);
			this.lightEnvironmentTileset = (char) stream.read();
			ParseUtils.readUInt8Array(stream, this.waterVertexColor);
		}

		if (this.version > 27) {
			ParseUtils.readUInt8Array(stream, this.unknown2ProbablyLua);
		}
		if (this.version > 30) {
			final long supportedModes = ParseUtils.readUInt32(stream);
			final long gameDataVersion = ParseUtils.readUInt32(stream);
		}

		for (int i = 0, l = stream.readInt(); i < l; i++) {
			final Player player = new Player();

			player.load(stream, this.version);

			this.players.add(player);
		}

		for (int i = 0, l = stream.readInt(); i < l; i++) {
			final Force force = new Force();

			force.load(stream);

			this.forces.add(force);
		}

		if (stream.available() == 1) {
			// some kind of really stupid protected map???
			return;
		}
		if (stream.available() > 0) {
			for (int i = 0, l = stream.readInt(); i < l; i++) {
				final UpgradeAvailabilityChange upgradeAvailabilityChange = new UpgradeAvailabilityChange();

				upgradeAvailabilityChange.load(stream);

				this.upgradeAvailabilityChanges.add(upgradeAvailabilityChange);
			}
		}

		if (stream.available() > 0) {
			for (int i = 0, l = stream.readInt(); i < l; i++) {
				final TechAvailabilityChange techAvailabilityChange = new TechAvailabilityChange();

				techAvailabilityChange.load(stream);

				this.techAvailabilityChanges.add(techAvailabilityChange);
			}
		}

		if (stream.available() > 0) {
			for (int i = 0, l = stream.readInt(); i < l; i++) {
				final RandomUnitTable randomUnitTable = new RandomUnitTable();

				randomUnitTable.load(stream);

				this.randomUnitTables.add(randomUnitTable);
			}
		}

		if (this.version > 24) {
			if (stream.available() > 0) {
				for (int i = 0, l = stream.readInt(); i < l; i++) {
					final RandomItemTable randomItemTable = new RandomItemTable();

					randomItemTable.load(stream);

					this.randomItemTables.add(randomItemTable);
				}
			}
		}
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		stream.writeInt(this.version);
		stream.writeInt(this.saves);
		stream.writeInt(this.editorVersion);

		if (this.version > 27) {
			stream.writeInt(this.gameVersionMajor);
			stream.writeInt(this.gameVersionMinor);
			stream.writeInt(this.gameVersionPatch);
			stream.writeInt(this.gameVersionBuild);
		}

		ParseUtils.writeWithNullTerminator(stream, this.name);
		ParseUtils.writeWithNullTerminator(stream, this.author);
		ParseUtils.writeWithNullTerminator(stream, this.description);
		ParseUtils.writeWithNullTerminator(stream, this.recommendedPlayers);
		ParseUtils.writeFloatArray(stream, this.cameraBounds);
		ParseUtils.writeInt32Array(stream, this.cameraBoundsComplements);
		ParseUtils.writeInt32Array(stream, this.playableSize);
		ParseUtils.writeUInt32(stream, this.flags);
		stream.write((byte) this.tileset);
		stream.writeInt(this.campaignBackground);

		if (this.version > 24) {
			ParseUtils.writeWithNullTerminator(stream, this.loadingScreenModel);
		}

		ParseUtils.writeWithNullTerminator(stream, this.loadingScreenText);
		ParseUtils.writeWithNullTerminator(stream, this.loadingScreenTitle);
		ParseUtils.writeWithNullTerminator(stream, this.loadingScreenSubtitle);
		stream.writeInt(this.loadingScreen);

		if (this.version > 24) {
			ParseUtils.writeWithNullTerminator(stream, this.prologueScreenModel);
		}

		ParseUtils.writeWithNullTerminator(stream, this.prologueScreenText);
		ParseUtils.writeWithNullTerminator(stream, this.prologueScreenTitle);
		ParseUtils.writeWithNullTerminator(stream, this.prologueScreenSubtitle);

		if (this.version > 24) {
			stream.writeInt(this.useTerrainFog);
			ParseUtils.writeFloatArray(stream, this.fogHeight);
			stream.writeFloat(this.fogDensity);
			ParseUtils.writeUInt8Array(stream, this.fogColor);
			stream.writeInt(this.globalWeather); // TODO War3ID???
			ParseUtils.writeWithNullTerminator(stream, this.soundEnvironment);
			stream.write((byte) this.lightEnvironmentTileset);
			ParseUtils.writeUInt8Array(stream, this.waterVertexColor);
		}

		if (this.version > 27) {
			ParseUtils.writeUInt8Array(stream, this.unknown2ProbablyLua);
		}

		ParseUtils.writeUInt32(stream, this.players.size());

		for (final Player player : this.players) {
			player.save(stream);
		}

		ParseUtils.writeUInt32(stream, this.forces.size());

		for (final Force force : this.forces) {
			force.save(stream);
		}

		ParseUtils.writeUInt32(stream, this.upgradeAvailabilityChanges.size());

		for (final UpgradeAvailabilityChange change : this.upgradeAvailabilityChanges) {
			change.save(stream);
		}

		ParseUtils.writeUInt32(stream, this.techAvailabilityChanges.size());

		for (final TechAvailabilityChange change : this.techAvailabilityChanges) {
			change.save(stream);
		}

		ParseUtils.writeUInt32(stream, this.randomUnitTables.size());

		for (final RandomUnitTable table : this.randomUnitTables) {
			table.save(stream);
		}

		if (this.version > 24) {
			ParseUtils.writeUInt32(stream, this.randomItemTables.size());

			for (final RandomItemTable table : this.randomItemTables) {
				table.save(stream);
			}
		}

	}

	public int getByteLength() {
		int size = 111 + this.name.length() + this.author.length() + this.description.length()
				+ this.recommendedPlayers.length() + this.loadingScreenText.length() + this.loadingScreenTitle.length()
				+ this.loadingScreenSubtitle.length() + this.prologueScreenText.length()
				+ this.prologueScreenTitle.length() + this.prologueScreenSubtitle.length();

		for (final Player player : this.players) {
			size += player.getByteLength();
		}

		for (final Force force : this.forces) {
			size += force.getByteLength();
		}

		size += this.upgradeAvailabilityChanges.size() * 16;

		size += this.techAvailabilityChanges.size() * 8;

		for (final RandomUnitTable table : this.randomUnitTables) {
			size += table.getByteLength();
		}

		if (this.version > 24) {
			size += 36 + this.loadingScreenModel.length() + this.prologueScreenModel.length()
					+ this.soundEnvironment.length();

			for (final RandomItemTable table : this.randomItemTables) {
				size += table.getByteLength();
			}
		}

		return size;
	}

	public int getVersion() {
		return this.version;
	}

	public int getSaves() {
		return this.saves;
	}

	public int getEditorVersion() {
		return this.editorVersion;
	}

	public String getName() {
		return this.name;
	}

	public String getAuthor() {
		return this.author;
	}

	public String getDescription() {
		return this.description;
	}

	public String getRecommendedPlayers() {
		return this.recommendedPlayers;
	}

	public float[] getCameraBounds() {
		return this.cameraBounds;
	}

	public int[] getCameraBoundsComplements() {
		return this.cameraBoundsComplements;
	}

	public int[] getPlayableSize() {
		return this.playableSize;
	}

	public long getFlags() {
		return this.flags;
	}

	public char getTileset() {
		return this.tileset;
	}

	public int getCampaignBackground() {
		return this.campaignBackground;
	}

	public String getLoadingScreenModel() {
		return this.loadingScreenModel;
	}

	public String getLoadingScreenText() {
		return this.loadingScreenText;
	}

	public String getLoadingScreenTitle() {
		return this.loadingScreenTitle;
	}

	public String getLoadingScreenSubtitle() {
		return this.loadingScreenSubtitle;
	}

	public int getLoadingScreen() {
		return this.loadingScreen;
	}

	public String getPrologueScreenModel() {
		return this.prologueScreenModel;
	}

	public String getPrologueScreenText() {
		return this.prologueScreenText;
	}

	public String getPrologueScreenTitle() {
		return this.prologueScreenTitle;
	}

	public String getPrologueScreenSubtitle() {
		return this.prologueScreenSubtitle;
	}

	public int getUseTerrainFog() {
		return this.useTerrainFog;
	}

	public float[] getFogHeight() {
		return this.fogHeight;
	}

	public float getFogDensity() {
		return this.fogDensity;
	}

	public short[] getFogColor() {
		return this.fogColor;
	}

	public int getGlobalWeather() {
		return this.globalWeather;
	}

	public String getSoundEnvironment() {
		return this.soundEnvironment;
	}

	public char getLightEnvironmentTileset() {
		return this.lightEnvironmentTileset;
	}

	public short[] getWaterVertexColor() {
		return this.waterVertexColor;
	}

	public short[] getUnknown2() {
		return this.unknown2ProbablyLua;
	}

	public List<Player> getPlayers() {
		return this.players;
	}

	public List<Force> getForces() {
		return this.forces;
	}

	public List<UpgradeAvailabilityChange> getUpgradeAvailabilityChanges() {
		return this.upgradeAvailabilityChanges;
	}

	public List<TechAvailabilityChange> getTechAvailabilityChanges() {
		return this.techAvailabilityChanges;
	}

	public List<RandomUnitTable> getRandomUnitTables() {
		return this.randomUnitTables;
	}

	public List<RandomItemTable> getRandomItemTables() {
		return this.randomItemTables;
	}

	public int getGameVersionMajor() {
		return this.gameVersionMajor;
	}

	public int getGameVersionMinor() {
		return this.gameVersionMinor;
	}

	public boolean hasFlag(final int mapFlag) {
		return (this.flags & mapFlag) != 0;
	}
}
