package com.etheller.warsmash.viewer5.handlers.w3x;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
/**
 * 提供序列操作的工具类
 */
public class SequenceUtils {
	private static final int SECONDARY_TAGS_DECLARED_COUNT = AnimationTokens.SecondaryTag.values().length;
	public static final EnumSet<SecondaryTag> EMPTY = EnumSet.noneOf(SecondaryTag.class);
	public static final EnumSet<SecondaryTag> READY = EnumSet.of(SecondaryTag.READY); // 准备
	public static final EnumSet<SecondaryTag> FLESH = EnumSet.of(SecondaryTag.FLESH); // 估计是强化
	public static final EnumSet<SecondaryTag> TALK = EnumSet.of(SecondaryTag.TALK); // 说话
	public static final EnumSet<SecondaryTag> BONE = EnumSet.of(SecondaryTag.BONE); // 骨头
	public static final EnumSet<SecondaryTag> HIT = EnumSet.of(SecondaryTag.HIT); // 受击
	public static final EnumSet<SecondaryTag> SPELL = EnumSet.of(SecondaryTag.SPELL); // 法术
	public static final EnumSet<SecondaryTag> SPELL_EATTREE = EnumSet.of(SecondaryTag.SPELL, SecondaryTag.EATTREE); // 法术， 吃树
	public static final EnumSet<SecondaryTag> SPELL_THROW = EnumSet.of(SecondaryTag.SPELL, SecondaryTag.THROW); // 法术， 投掷
	public static final EnumSet<SecondaryTag> WORK = EnumSet.of(SecondaryTag.WORK); // 工作
	public static final EnumSet<SecondaryTag> COMPLETE = EnumSet.of(SecondaryTag.COMPLETE); // 完成
	public static final EnumSet<SecondaryTag> FAST = EnumSet.of(SecondaryTag.FAST); // 快速
	public static final EnumSet<SecondaryTag> ALTERNATE = EnumSet.of(SecondaryTag.ALTERNATE); // 交替

	private static final StandSequenceComparator STAND_SEQUENCE_COMPARATOR = new StandSequenceComparator();
	private static final SecondaryTagSequenceComparator SECONDARY_TAG_SEQUENCE_COMPARATOR = new SecondaryTagSequenceComparator(
			STAND_SEQUENCE_COMPARATOR);

	/**
	 * 根据类型过滤序列
	 *
	 * @param type      类型
	 * @param sequences 序列列表
	 * @return 过滤后的序列列表
	 */
	public static List<IndexedSequence> filterSequences(final String type, final List<Sequence> sequences) {
		// 创建一个新的ArrayList来存储过滤后的IndexedSequence对象
		final List<IndexedSequence> filtered = new ArrayList<>();

		// 遍历sequences列表中的每个Sequence对象
		for (int i = 0, l = sequences.size(); i < l; i++) {
			// 获取当前遍历到的Sequence对象
			final Sequence sequence = sequences.get(i);
			// 获取Sequence对象的名称，并去除名称中的"-"，然后转换为小写
			final String name = sequence.getName().split("-")[0].trim().toLowerCase();

			// 如果名称与指定的类型相匹配
			if (name.equals(type)) {
				// 将当前的Sequence对象和它在sequences列表中的索引添加到filtered列表中
				filtered.add(new IndexedSequence(sequence, i));
			}
		}

		// 返回过滤后的序列列表
		return filtered;

	}

	/**
	 * 根据主要标签和次要标签过滤序列
	 *
	 * @param type      主要标签
	 * @param tags      次要标签集合
	 * @param sequences 序列列表
	 * @return 过滤后的序列列表
	 */
	private static List<IndexedSequence> filterSequences(final PrimaryTag type, final EnumSet<SecondaryTag> tags,
			final List<Sequence> sequences) {
		// 创建一个新的ArrayList来存储过滤后的IndexedSequence对象
		final List<IndexedSequence> filtered = new ArrayList<>();

		// 遍历sequences列表中的每个Sequence对象
		for (int i = 0, l = sequences.size(); i < l; i++) {
			// 获取当前遍历到的Sequence对象
			final Sequence sequence = sequences.get(i);
			// 如果序列的主要标签包含指定的类型，或者指定的类型为空
			if ((sequence.getPrimaryTags().contains(type) || (type == null))
					// 并且序列的次要标签包含所有指定的次要标签，并且指定的次要标签也包含序列的所有次要标签
					&& (sequence.getSecondaryTags().containsAll(tags)
					&& tags.containsAll(sequence.getSecondaryTags()))) {
				// 将当前的Sequence对象和它在sequences列表中的索引添加到filtered列表中
				filtered.add(new IndexedSequence(sequence, i));
			}
		}


		return filtered;
	}

	/**
	 * 选择符合类型的随机序列
	 *
	 * @param type      类型
	 * @param sequences 序列列表
	 * @return 随机选择的序列
	 */
	public static IndexedSequence selectSequence(final String type, final List<Sequence> sequences) {
		// 使用指定的类型过滤序列列表，得到一个新的过滤后的序列列表
		final List<IndexedSequence> filtered = filterSequences(type, sequences);

		// 使用 STAND_SEQUENCE_COMPARATOR 对过滤后的序列列表进行排序
		filtered.sort(STAND_SEQUENCE_COMPARATOR);

		// 初始化一个计数器 i 为 0
		int i = 0;
		// 生成一个 0 到 100 之间的随机数 randomRoll
		final double randomRoll = Math.random() * 100;

		// 遍历过滤后的序列列表
		for (final int l = filtered.size(); i < l; i++) {
			// 获取当前索引 i 处的序列
			final Sequence sequence = filtered.get(i).sequence;
			// 获取当前序列的稀有度
			final float rarity = sequence.getRarity();

			// 如果稀有度为 0，则停止循环
			if (rarity == 0) {
				break;
			}

			// 如果随机数 randomRoll 小于 (10 - rarity)，则返回当前序列
			if (randomRoll < (10 - rarity)) {
				return filtered.get(i);
			}
		}

		// 计算过滤后的序列列表中剩余的序列数量
		final int sequencesLeft = filtered.size() - i;
		// 生成一个随机索引 random，其值为 i 加上 0 到 sequencesLeft 之间的一个随机数
		final int random = (int) (i + Math.floor(Math.random() * sequencesLeft));

		// 如果剩余的序列数量小于等于 0，则返回 null
		if (sequencesLeft <= 0) {
			return null; // new IndexedSequence(null, 0);
		}

		// 从过滤后的序列列表中获取随机索引 random 处的序列
		final IndexedSequence sequence = filtered.get(random);

		// 返回获取到的序列
		return sequence;

	}

	/**
	 * 计算目标标签集合与测试标签集合的匹配数量
	 *
	 * @param goalTagSet   目标标签集合
	 * @param tagsToTest   测试标签集合
	 * @return 匹配数量
	 */
	public static int matchCount(final EnumSet<AnimationTokens.SecondaryTag> goalTagSet,
			final EnumSet<AnimationTokens.SecondaryTag> tagsToTest) {
		// 初始化匹配计数器
		int matches = 0;
		// 遍历目标标签集合
		for (final AnimationTokens.SecondaryTag goalTag : goalTagSet) {
			// 如果待测试的标签集合包含当前目标标签，则增加匹配计数
			if (tagsToTest.contains(goalTag)) {
				matches++;
			}
		}
		// 返回匹配的数量
		return matches;

	}

	/**
	 * 计算目标标签集合与测试标签集合的匹配等级
	 *
	 * @param goalTagSet 目标标签集合
	 * @param tagsToTest 测试标签集合
	 * @return 匹配等级
	 */
	public static int matchRank(final EnumSet<AnimationTokens.SecondaryTag> goalTagSet,
								final EnumSet<AnimationTokens.SecondaryTag> tagsToTest) {
		// 初始化匹配等级为0
		int matchRank = 0;
		// 遍历目标标签集合中的每个标签
		for (final AnimationTokens.SecondaryTag goalTag : goalTagSet) {
			// 如果测试标签集合中包含当前目标标签
			if (tagsToTest.contains(goalTag)) {
				// 增加匹配等级，计算方式为：目标标签在枚举中的顺序值（从后往前）+1，再加上200000
				matchRank += (SECONDARY_TAGS_DECLARED_COUNT - goalTag.ordinal()) + 1;
				matchRank += 200000;
			}
		}
		// 返回最终的匹配等级
		return matchRank;
	}


	/**
	 * 根据主要标签和次要标签选择随机序列
	 *
	 * @param type                    主要标签
	 * @param tags                    次要标签集合
	 * @param sequences               序列列表
	 * @param allowRarityVariations   是否允许稀有度变化
	 * @return 随机选择的序列
	 */
	public static IndexedSequence selectSequence(AnimationTokens.PrimaryTag type,
			final EnumSet<AnimationTokens.SecondaryTag> tags, final List<Sequence> sequences,
			final boolean allowRarityVariations) {
		// 过滤出符合给定类型和标签的序列
		List<IndexedSequence> filtered = filterSequences(type, tags, sequences);
		// 定义序列比较器
		final Comparator<IndexedSequence> sequenceComparator = STAND_SEQUENCE_COMPARATOR;

		// 如果过滤后的序列为空，并且标签集合不为空，则尝试使用空标签集合再次过滤
		// if (filtered.isEmpty() && !tags.isEmpty()) {
		//     filtered = filterSequences(type, EMPTY, sequences);
		// }

		// 如果过滤后的序列仍然为空，则尝试找到最佳匹配的标签
		if (filtered.isEmpty()) {
			EnumSet<SecondaryTag> fallbackTags = null;
			int fallbackTagsMatchCount = 0;
			// 遍历所有序列，寻找最佳匹配的标签
			for (int i = 0, l = sequences.size(); i < l; i++) {
				final Sequence sequence = sequences.get(i);
				// 如果序列的主标签包含给定类型或类型为空，则尝试匹配
				if (sequence.getPrimaryTags().contains(type) || (type == null)) {
					final int matchCount = matchRank(tags, sequence.getSecondaryTags());
					// 更新最佳匹配的标签和匹配数量
					if ((matchCount > fallbackTagsMatchCount)
							|| ((matchCount > 0) && (matchCount == fallbackTagsMatchCount)
							&& (fallbackTags.size() > sequence.getSecondaryTags().size()))) {
						fallbackTags = sequence.getSecondaryTags();
						fallbackTagsMatchCount = matchCount;
					}
				}
			}
			// 如果仍未找到匹配的标签，则尝试使用主标签进行匹配
			if (fallbackTags == null) {
				if (type == null) {
					type = PrimaryTag.STAND;
				}
				for (int i = 0, l = sequences.size(); i < l; i++) {
					final Sequence sequence = sequences.get(i);
					// 如果序列的主标签包含给定类型或类型为空，则尝试匹配
					if (sequence.getPrimaryTags().contains(type) || (type == null)) {
						// 更新最佳匹配的标签
						if ((fallbackTags == null) || (sequence.getSecondaryTags().size() < fallbackTags.size())
								|| ((sequence.getSecondaryTags().size() == fallbackTags.size())
								&& (SecondaryTagSequenceComparator.getTagsOrdinal(sequence.getSecondaryTags(),
								tags) > SecondaryTagSequenceComparator.getTagsOrdinal(fallbackTags,
								tags)))) {
							fallbackTags = sequence.getSecondaryTags();
						}
					}
				}
			}
			// 使用找到的最佳匹配标签再次过滤序列
			if (fallbackTags != null) {
				filtered = filterSequences(type, fallbackTags, sequences);
			}
		}

		// 对过滤后的序列进行排序
		filtered.sort(sequenceComparator);

		// 随机选择一个序列
		int i = 0;
		final double randomRoll = Math.random() * 100;
		for (final int l = filtered.size(); i < l; i++) {
			final Sequence sequence = filtered.get(i).sequence;
			final float rarity = sequence.getRarity();

			// 如果序列的稀有度为0，则停止选择
			if (rarity == 0) {
				break;
			}

			// 根据稀有度和随机数决定是否选择当前序列
			if ((randomRoll < (10 - rarity)) && allowRarityVariations) {
				return filtered.get(i);
			}
		}

		// 如果没有找到合适的序列，则随机返回一个
		final int sequencesLeft = filtered.size() - i;
		if (sequencesLeft <= 0) {
			if (filtered.size() > 0) {
				return filtered.get((int) Math.floor(Math.random() * filtered.size()));
			}
			return null; // 如果没有序列可供选择，返回null
		}
		final int random = (int) (i + Math.floor(Math.random() * sequencesLeft));
		final IndexedSequence sequence = filtered.get(random);

		return sequence;

	}

	/**
	 * 随机选择站立动画序列
	 *
	 * @param target 目标复杂实例
	 */
	public static void randomStandSequence(final MdxComplexInstance target) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence("stand", sequences);

		if (sequence != null) {
			target.setSequence(sequence.index);
		}
		else {
			target.setSequence(0);
		}
	}

	/**
	 * 随机选择死亡动画序列
	 *
	 * @param target 目标复杂实例
	 */
	public static void randomDeathSequence(final MdxComplexInstance target) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence("death", sequences);

		if (sequence != null) {
			target.setSequence(sequence.index);
		}
		else {
			target.setSequence(0);
		}
	}

	/**
	 * 随机选择行走动画序列
	 *
	 * @param target 目标复杂实例
	 */
	public static void randomWalkSequence(final MdxComplexInstance target) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence("walk", sequences);

		if (sequence != null) {
			target.setSequence(sequence.index);
		}
		else {
			randomStandSequence(target);
		}
	}

	/**
	 * 随机选择出生动画序列
	 *
	 * @param target 目标复杂实例
	 */
	public static void randomBirthSequence(final MdxComplexInstance target) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence("birth", sequences);

		if (sequence != null) {
			target.setSequence(sequence.index);
		}
		else {
			randomStandSequence(target);
		}
	}

	/**
	 * 随机选择肖像动画序列
	 *
	 * @param target 目标复杂实例
	 */
	public static void randomPortraitSequence(final MdxComplexInstance target) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence("portrait", sequences);

		if (sequence != null) {
			target.setSequence(sequence.index);
		}
		else {
			randomStandSequence(target);
		}
	}

	/**
	 * 随机选择肖像对话动画序列
	 *
	 * @param target 目标复杂实例
	 */
	public static void randomPortraitTalkSequence(final MdxComplexInstance target) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence("portrait talk", sequences);

		if (sequence != null) {
			target.setSequence(sequence.index);
		}
		else {
			randomPortraitSequence(target);
		}
	}

	/**
	 * 随机选择指定名称的动画序列
	 *
	 * @param target       目标复杂实例
	 * @param sequenceName 动画序列名称
	 */
	public static void randomSequence(final MdxComplexInstance target, final String sequenceName) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence(sequenceName, sequences);

		if (sequence != null) {
			target.setSequence(sequence.index);
		}
		else {
			randomStandSequence(target);
		}
	}

	/**
	 * 随机选择动画序列
	 *
	 * @param target              目标复杂实例
	 * @param animationName       主要标签
	 * @param allowRarityVariations 是否允许稀有度变化
	 * @return 随机选择的序列
	 */
	public static Sequence randomSequence(final MdxComplexInstance target, final PrimaryTag animationName,
			final boolean allowRarityVariations) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence(animationName, null, sequences, allowRarityVariations);

		if (sequence != null) {
			target.setSequence(sequence.index);
			return sequence.sequence;
		}
		else {
			return null;
		}
	}

	/**
	 * 随机选择动画序列，支持次要标签
	 *
	 * @param target                      目标复杂实例
	 * @param animationName               主要标签
	 * @param secondaryAnimationTags       次要标签集合
	 * @param allowRarityVariations       是否允许稀有度变化
	 * @return 随机选择的序列
	 */
	public static Sequence randomSequence(final MdxComplexInstance target, final PrimaryTag animationName,
			final EnumSet<SecondaryTag> secondaryAnimationTags, final boolean allowRarityVariations) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence(animationName, secondaryAnimationTags, sequences,
				allowRarityVariations);

		if (sequence != null) {
			target.setSequence(sequence.index);
			return sequence.sequence;
		}
		else {
			if ((animationName == null) || (secondaryAnimationTags.size() != 1)
					|| !secondaryAnimationTags.contains(SecondaryTag.SPELL)) {
				return null;
			}
			else {
				return randomSequence(target, null, secondaryAnimationTags, allowRarityVariations);
			}
		}
	}

	/**
	 * 随机选择动画序列
	 *
	 * @param target          目标复杂实例
	 * @param animationName   主要标签
	 * @return 随机选择的序列
	 */
	public static Sequence randomSequence(final MdxComplexInstance target, final PrimaryTag animationName) {
		return randomSequence(target, animationName, EMPTY, false);
	}
}
