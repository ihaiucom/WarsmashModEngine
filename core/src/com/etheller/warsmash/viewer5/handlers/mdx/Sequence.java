package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.Bounds;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.hiveworkshop.rms.parsers.mdlx.MdlxExtent;
import com.hiveworkshop.rms.parsers.mdlx.MdlxSequence;
/**
 * 代表一个序列，包含序列的相关信息和标签。
 */
public class Sequence {
	private final MdlxSequence sequence;
	private final Bounds bounds;
	private final EnumSet<AnimationTokens.PrimaryTag> primaryTags = EnumSet.noneOf(AnimationTokens.PrimaryTag.class);
	private final EnumSet<AnimationTokens.SecondaryTag> secondaryTags = EnumSet
			.noneOf(AnimationTokens.SecondaryTag.class);

	/**
	 * 构造函数，初始化序列和边界，并填充标签集合。
	 *
	 * @param sequence MdlxSequence对象，表示要构建的序列。
	 */
	public Sequence(final MdlxSequence sequence) {
		this.sequence = sequence;
		this.bounds = new Bounds();
		final MdlxExtent sequenceExtent = sequence.getExtent();
		this.bounds.fromExtents(sequenceExtent.getMin(), sequenceExtent.getMax(), sequenceExtent.getBoundsRadius());
		populateTags();
	}

	/**
	 * 填充主要标签和次要标签集合。
	 */
	private void populateTags() {
		populateTags(this.primaryTags, this.secondaryTags, this.sequence.name);
	}

	/**
	  * 根据提供的名称字符串，填充主要标签和次要标签集合。
	  * 名称字符串中的每个单词或逗号分隔的标记将被检查，
	  * 并根据其大写形式匹配相应的枚举值，然后添加到对应的集合中。
	  *
	  * @param primaryTags 用于存储匹配的主要标签的集合
	  * @param secondaryTags 用于存储匹配的次要标签的集合
	  * @param name 包含标记的名称字符串
	  */
	 public static void populateTags(final EnumSet<AnimationTokens.PrimaryTag> primaryTags,
			 final EnumSet<AnimationTokens.SecondaryTag> secondaryTags, final String name) {
		 primaryTags.clear(); // 清空主要标签集合
		 secondaryTags.clear(); // 清空次要标签集合
		 TokenLoop: // 标签循环标签，用于跳出外层循环
		 for (final String token : name.split("\\s+|,")) { // 分割名称字符串为单词或逗号分隔的标记
			 final String upperCaseToken = token.toUpperCase(); // 将标记转换为大写形式
			 for (final PrimaryTag primaryTag : PrimaryTag.values()) { // 遍历主要标签枚举值
				 if (upperCaseToken.equals(primaryTag.name())) { // 如果标记匹配主要标签枚举值
					 primaryTags.add(primaryTag); // 添加到主要标签集合
					 continue TokenLoop; // 跳出外层循环，继续处理下一个标记
				 }
			 }
			 for (final SecondaryTag secondaryTag : SecondaryTag.values()) { // 遍历次要标签枚举值
				 if (upperCaseToken.equals(secondaryTag.name())) { // 如果标记匹配次要标签枚举值
					 secondaryTags.add(secondaryTag); // 添加到次要标签集合
					 continue TokenLoop; // 跳出外层循环，继续处理下一个标记
				 }
			 }
			 break; // 如果没有匹配的标签，跳出循环
		 }
	 }

	/**
	 * 获取序列名称。
	 *
	 * @return 返回序列的名称。
	 */
	public String getName() {
		return this.sequence.getName();
	}

	/**
	 * 获取时间间隔。
	 *
	 * @return 返回时间间隔数组。
	 */
	public long[] getInterval() {
		return this.sequence.getInterval();
	}

	/**
	 * 获取移动速度。
	 *
	 * @return 返回移动速度。
	 */
	public float getMoveSpeed() {
		return this.sequence.getMoveSpeed();
	}

	/**
	 * 获取标志位。
	 *
	 * @return 返回标志位。
	 */
	public int getFlags() {
		return this.sequence.getFlags();
	}

	/**
	 * 获取稀有度。
	 *
	 * @return 返回稀有度。
	 */
	public float getRarity() {
		return this.sequence.getRarity();
	}

	/**
	 * 获取同步点。
	 *
	 * @return 返回同步点。
	 */
	public long getSyncPoint() {
		return this.sequence.getSyncPoint();
	}

	/**
	 * 获取边界信息。
	 *
	 * @return 返回边界对象。
	 */
	public Bounds getBounds() {
		return this.bounds;
	}

	/**
	 * 获取范围信息。
	 *
	 * @return 返回MdlxExtent对象。
	 */
	public MdlxExtent getExtent() {
		return this.sequence.getExtent();
	}

	/**
	 * 获取主要标签集合。
	 *
	 * @return 返回主要标签的集合。
	 */
	public EnumSet<AnimationTokens.PrimaryTag> getPrimaryTags() {
		return this.primaryTags;
	}

	/**
	 * 获取次要标签集合。
	 *
	 * @return 返回次要标签的集合。
	 */
	public EnumSet<AnimationTokens.SecondaryTag> getSecondaryTags() {
		return this.secondaryTags;
	}
}
