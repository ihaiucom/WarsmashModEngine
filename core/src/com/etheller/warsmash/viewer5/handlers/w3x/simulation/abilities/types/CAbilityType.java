package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

/**
 * 抽象类 CAbilityType，表示一个能力类型，具有泛型支持能力级别数据。
 */
public abstract class CAbilityType<TYPE_LEVEL_DATA extends CAbilityTypeLevelData> {
	/* alias: defines which ability editor ability to use */
	// 别名，定义了使用哪个能力编辑器的能力
	private final War3ID alias;
	/* code: defines which CAbility class to use */
	// 代码，定义了使用哪个CAbility类
	private final War3ID code;

	private final List<TYPE_LEVEL_DATA> levelData;

	/**
	 * 构造函数，初始化能力类型的别名、代码和级别数据列表。
	 *
	 * @param alias 能力的别名
	 * @param code 能力的代码
	 * @param levelData 能力级别数据的列表
	 */
	public CAbilityType(final War3ID alias, final War3ID code, final List<TYPE_LEVEL_DATA> levelData) {
		this.alias = alias;
		this.code = code;
		this.levelData = levelData;
	}

	/**
	 * 获取能力的别名。
	 *
	 * @return 能力别名
	 */
	public War3ID getAlias() {
		return this.alias;
	}

	/**
	 * 获取能力的代码。
	 *
	 * @return 能力代码
	 */
	public War3ID getCode() {
		return this.code;
	}

	/**
	 * 根据级别获取允许的目标类型。
	 *
	 * @param level 能力的级别
	 * @return 允许的目标类型集合
	 */
	public EnumSet<CTargetType> getTargetsAllowed(final int level) {
		return getLevelData(level).getTargetsAllowed();
	}

	/**
	 * 能力级别数量
	 *
	 * @return 能力级别数量
	 */
	public int getLevelCount() {
		return levelData.size();
	}

	/**
	 * 获取能力级别数据列表。
	 *
	 * @return 能力级别数据列表
	 */
	protected final List<TYPE_LEVEL_DATA> getLevelData() {
		return this.levelData;
	}

	/**
	 * 根据级别获取特定的能力级别数据。
	 *
	 * @param level 能力的级别
	 * @return 指定级别的能力级别数据
	 */
	protected final TYPE_LEVEL_DATA getLevelData(final int level) {
		return this.levelData.get(level);
	}

	/**
	 * 创建一个新的能力实例。
	 *
	 * @param handleId 能力的句柄ID
	 * @return 新创建的能力实例
	 */
	public abstract CAbility createAbility(int handleId);

	/**
	 * 设置能力的级别。
	 *
	 * @param game 模拟游戏对象
	 * @param unit 相关的单位
	 * @param existingAbility 已存在的能力
	 * @param level 新的能力级别
	 */
	public abstract void setLevel(CSimulation game, CUnit unit, CLevelingAbility existingAbility, int level);

}
