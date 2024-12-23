package com.etheller.warsmash.parsers.w3x.w3i;

import java.io.IOException;

import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

/**
 * A player.
 */
public class Player {
	// 玩家ID
	private int id;
	// 玩家类型
	private int type;
	// 玩家种族
	private int race;
	// 是否固定起始位置
	private int isFixedStartPosition;
	// 玩家名称
	private String name;
	// 玩家起始位置的坐标数组，包含两个浮点数，分别代表X和Y坐标
	private final float[] startLocation = new float[2];
	// 盟友低优先级标志位
	private long allyLowPriorities;
	// 盟友高优先级标志位
	private long allyHighPriorities;
	// 敌人低优先级标志位
	private long enemyLowPrioritiesFlags;
	// 敌人高优先级标志位
	private long enemyHighPrioritiesFlags;


	public void load(final LittleEndianDataInputStream stream, final int version) throws IOException {
		this.id = (int) ParseUtils.readUInt32(stream);
		this.type = stream.readInt();
		this.race = stream.readInt();
		this.isFixedStartPosition = stream.readInt();
		this.name = ParseUtils.readUntilNull(stream);
		ParseUtils.readFloatArray(stream, this.startLocation);
		this.allyLowPriorities = ParseUtils.readUInt32(stream);
		this.allyHighPriorities = ParseUtils.readUInt32(stream);
		if (version > 30) {
			this.enemyLowPrioritiesFlags = ParseUtils.readUInt32(stream);
			this.enemyHighPrioritiesFlags = ParseUtils.readUInt32(stream);
		}
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, this.id);
		stream.writeInt(this.type);
		stream.writeInt(this.race);
		stream.writeInt(this.isFixedStartPosition);
		ParseUtils.writeWithNullTerminator(stream, this.name);
		ParseUtils.writeFloatArray(stream, this.startLocation);
		ParseUtils.writeUInt32(stream, this.allyLowPriorities);
		ParseUtils.writeUInt32(stream, this.allyHighPriorities);
	}

	public int getByteLength() {
		return 33 + this.name.length();
	}

	public int getId() {
		return this.id;
	}

	public int getType() {
		return this.type;
	}

	public int getRace() {
		return this.race;
	}

	public int getIsFixedStartPosition() {
		return this.isFixedStartPosition;
	}

	public String getName() {
		return this.name;
	}

	public float[] getStartLocation() {
		return this.startLocation;
	}

	public long getAllyLowPriorities() {
		return this.allyLowPriorities;
	}

	public long getAllyHighPriorities() {
		return this.allyHighPriorities;
	}

	public long getEnemyLowPrioritiesFlags() {
		return this.enemyLowPrioritiesFlags;
	}

	public long getEnemyHighPrioritiesFlags() {
		return this.enemyHighPrioritiesFlags;
	}
}
