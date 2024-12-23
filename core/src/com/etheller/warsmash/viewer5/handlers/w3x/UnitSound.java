package com.etheller.warsmash.viewer5.handlers.w3x;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.DataSourceFileHandle;
import com.etheller.warsmash.viewer5.AudioBufferSource;
import com.etheller.warsmash.viewer5.AudioContext;
import com.etheller.warsmash.viewer5.AudioPanner;
import com.etheller.warsmash.viewer5.gl.Extensions;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;

public final class UnitSound {
	// 定义一个静态常量SILENT，表示无声的单位声音，所有参数都设置为0或false
	private static final UnitSound SILENT = new UnitSound(0, 0, 0, 0, 0, 0, false);

	// 声音列表，用于存储当前单位可以播放的所有声音
	private final List<Sound> sounds = new ArrayList<>();

	// 音量，控制声音的响度
	private final float volume;

	// 音调，控制声音的高低
	private final float pitch;

	// 音调变化范围，使声音播放时有随机的音调波动
	private final float pitchVariance;

	// 最小距离，声音在这个距离内是满响度的
	private final float minDistance;

	// 最大距离，声音在这个距离外将不再播放
	private final float maxDistance;

	// 距离截止，超过这个距离声音将开始衰减
	private final float distanceCutoff;

	// 循环播放标志，如果为true，声音将在播放完毕后重新开始播放
	private final boolean looping;

	// 最后播放的声音，用于跟踪和管理声音播放状态
	private Sound lastPlayedSound;

	// 创建一个UnitSound对象的静态方法
	public static UnitSound create(final DataSource dataSource, final DataTable unitAckSounds, final String soundName,
								   final String soundType) {
		// 从unitAckSounds数据表中获取对应soundName和soundType的行元素
		final Element row = unitAckSounds.get(soundName + soundType);
		// 如果找不到对应的行，则返回一个静音的UnitSound对象
		if (row == null) {
			return SILENT;
		}
		// 获取声音文件名列表
		final String fileNames = row.getField("FileNames");
		// 获取声音文件目录基础路径
		String directoryBase = row.getField("DirectoryBase");
		// 确保目录路径以反斜杠结尾
		if ((directoryBase.length() > 1) && !directoryBase.endsWith("\\")) {
			directoryBase += "\\";
		}
		// 获取音量值，并将其标准化到0-1之间
		final float volume = row.getFieldFloatValue("Volume") / 127f;
		// 获取音调值
		final float pitch = row.getFieldFloatValue("Pitch");
		// 获取音调变化值，如果为1.0则设置为0.0
		float pitchVariance = row.getFieldFloatValue("PitchVariance");
		if (pitchVariance == 1.0f) {
			pitchVariance = 0.0f;
		}
		// 获取最小距离
		final float minDistance = row.getFieldFloatValue("MinDistance");
		// 获取最大距离
		final float maxDistance = row.getFieldFloatValue("MaxDistance");
		// 获取距离截止值
		final float distanceCutoff = row.getFieldFloatValue("DistanceCutoff");
		// 获取声音标志，并分割成字符串数组
		final String[] flags = row.getField("Flags").split(",");
		// 初始化循环播放标志为false
		boolean looping = false;
		// 遍历标志数组，检查是否有循环播放标志
		for (final String flag : flags) {
			if ("LOOPING".equals(flag)) {
				looping = true;
			}
		}
		// 创建一个新的UnitSound对象
		final UnitSound sound = new UnitSound(volume, pitch, pitchVariance, minDistance, maxDistance, distanceCutoff,
				looping);
		// 遍历文件名列表，构建完整的文件路径，并创建Sound对象
		for (final String fileName : fileNames.split(",")) {
			final String filePath = directoryBase + fileName;
			final Sound newSound = createSound(dataSource, filePath);
			// 如果成功创建了Sound对象，则将其添加到UnitSound对象的声音列表中
			if (newSound != null) {
				sound.sounds.add(newSound);
			}
		}
		// 返回创建好的UnitSound对象
		return sound;
	}

	// 定义一个静态方法createSound，用于根据数据源和文件路径创建声音对象
	public static Sound createSound(final DataSource dataSource, String filePath) {
		// 查找文件路径中最后一个点的索引位置
		final int lastDotIndex = filePath.lastIndexOf('.');
		// 如果存在点，截取点之前的部分作为新的文件路径
		if (lastDotIndex != -1) {
			filePath = filePath.substring(0, lastDotIndex);
		}
		// 初始化声音对象为null
		Sound newSound = null;
		// 检查数据源中是否存在.wav或.flac格式的文件
		if (dataSource.has(filePath + ".wav") || dataSource.has(filePath + ".flac")) {
			try {
				// 尝试创建声音对象，这里假设使用的是.wav格式的文件
				newSound = Gdx.audio.newSound(new DataSourceFileHandle(dataSource, filePath + ".wav"));
			}
			// 如果创建过程中发生异常，打印异常堆栈信息
			catch (final Exception exc) {
				exc.printStackTrace();
			}
		}
		// 返回创建的声音对象，如果没有成功创建则返回null
		return newSound;
	}


	/**
	 * 构造一个新的 UnitSound 对象，用于表示一个单位的声音
	 *
	 * @param volume         声音的音量，范围从 0.0 到 1.0
	 * @param pitch          声音的音调，范围从 0.0 到 1.0
	 * @param pitchVariation 声音音调的变化量，范围从 0.0 到 1.0
	 * @param minDistance    声音开始衰减的最小距离
	 * @param maxDistance    声音完全衰减的最大距离
	 * @param distanceCutoff 声音衰减的截止距离
	 * @param looping        是否循环播放声音
	 */
	public UnitSound(final float volume, final float pitch, final float pitchVariation, final float minDistance,
					 final float maxDistance, final float distanceCutoff, final boolean looping) {
		// 将传入的参数赋值给类的成员变量
		this.volume = volume;
		this.pitch = pitch;
		this.pitchVariance = pitchVariation;
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
		this.distanceCutoff = distanceCutoff;
		this.looping = looping;
	}

	/**
	 * 播放单位响应声音
	 *
	 * @param audioContext 音频上下文
	 * @param unit         渲染单位
	 * @return 是否成功播放
	 */
	public boolean playUnitResponse(final AudioContext audioContext, final RenderUnit unit) {
		// 调用另一个重载的 playUnitResponse 方法，随机选择一个声音索引
		return playUnitResponse(audioContext, unit, (int) (Math.random() * this.sounds.size()));
	}

	/**
	 * 播放单位响应声音
	 *
	 * @param audioContext 音频上下文
	 * @param unit         渲染单位
	 * @param index        声音索引
	 * @return 是否成功播放
	 */
	public boolean playUnitResponse(final AudioContext audioContext, final RenderUnit unit, final int index) {
		// 获取当前时间
		final long millisTime = TimeUtils.millis();
		// 如果当前时间小于单位上次响应结束时间，则不播放
		if (millisTime < unit.lastUnitResponseEndTimeMillis) {
			return false;
		}
		// 调用 play 方法播放声音，随机生成一个位置和一个声音索引
		if (play(audioContext, unit.location[0], unit.location[1], unit.location[2], index) != -1) {
			// 获取播放声音的持续时间
			final float duration = Extensions.audio.getDuration(this.lastPlayedSound);
			// 更新单位上次响应结束时间
			unit.lastUnitResponseEndTimeMillis = millisTime + (long) (1000 * duration);
			return true;
		}
		return false;
	}

	/**
	 * 播放声音
	 *
	 * @param audioContext 音频上下文
	 * @param x            x 坐标
	 * @param y            y 坐标
	 * @param z            z 坐标
	 * @param loopOverride 是否循环播放
	 * @return 播放的声音 ID
	 */
	public long play(final AudioContext audioContext, final float x, final float y, final float z, final boolean loopOverride) {
		// 调用另一个重载的 play 方法，随机选择一个声音索引
		return play(audioContext, x, y, z, (int) (Math.random() * this.sounds.size()), loopOverride);
	}

	/**
	 * 播放声音
	 *
	 * @param audioContext 音频上下文
	 * @param x            x 坐标
	 * @param y            y 坐标
	 * @param z            z 坐标
	 * @return 播放的声音 ID
	 */
	public long play(final AudioContext audioContext, final float x, final float y, final float z) {
		// 调用另一个重载的 play 方法，随机选择一个声音索引
		return play(audioContext, x, y, z, (int) (Math.random() * this.sounds.size()));
	}

	/**
	 * 播放声音
	 *
	 * @param audioContext 音频上下文
	 * @param x            x 坐标
	 * @param y            y 坐标
	 * @param z            z 坐标
	 * @param index        声音索引
	 * @return 播放的声音 ID
	 */
	public long play(final AudioContext audioContext, final float x, final float y, final float z, final int index) {
		// 调用另一个重载的 play 方法，传入 null 作为 loopOverride 参数
		return play(audioContext, x, y, z, index, null);
	}


	public long play(final AudioContext audioContext, final float x, final float y, final float z, final int index, final Boolean loopOverride) {
		if (this.sounds.isEmpty()) {
			return -1;
		}

		if (audioContext == null) {
			return -1;
		}
		final AudioPanner panner = audioContext.createPanner();
		final AudioBufferSource source = audioContext.createBufferSource();

		// Panner settings
		panner.setPosition(x, y, z);
		panner.setDistances(this.distanceCutoff, this.minDistance);
		panner.connect(audioContext.destination);

		// Source.
		source.buffer = this.sounds.get(index);
		source.connect(panner);

		// Make a sound.
		long soundId = -1;
		if (loopOverride == null) {
			soundId = source.start(0, this.volume,
					(this.pitch + ((float) Math.random() * this.pitchVariance * 2)) - this.pitchVariance, this.looping);
		} else {
			soundId = source.start(0, this.volume,
					(this.pitch + ((float) Math.random() * this.pitchVariance * 2)) - this.pitchVariance, loopOverride);
		}
		this.lastPlayedSound = source.buffer;
		return soundId;
	}

	public int getSoundCount() {
		return this.sounds.size();
	}

	public Sound getLastPlayedSound() {
		return this.lastPlayedSound;
	}

	public void stop() {
		for (final Sound sound : this.sounds) {
			sound.stop();
		}
	}

	public void stop(long soundId) {
		//如果调用长于1的列表，由于启动时使用的随机索引，这可能会出错？
		//不确定每个源的ID是否唯一
		// This may misbehave if called for a list longer than 1, due to the random index used when starting?
		// Not sure if IDs are unique per source
		for (final Sound sound : this.sounds) {
			sound.stop(soundId);
		}
	}
}