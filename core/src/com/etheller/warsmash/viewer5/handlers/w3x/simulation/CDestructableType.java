package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.image.BufferedImage;
import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
/**
 * CDestructableType 类表示可破坏物体的类型，包括名称、生命值、目标类型等属性。
 */
public class CDestructableType {

	private final String name;
	private final float maxLife;
	private final EnumSet<CTargetType> targetedAs;
	private final String armorType;
	private final int buildTime;
	private final float occlusionHeight;
	private final BufferedImage pathingPixelMap;
	private final BufferedImage pathingDeathPixelMap;

	/**
	 * CDestructableType 的构造函数，用于初始化物体类型的属性。
	 *
	 * @param name 物体名称
	 * @param maxLife 物体最大生命值
	 * @param targetedAs 物体被视为目标的类型
	 * @param armorType 物体的护甲类型
	 * @param buildTime 物体的建造时间
	 * @param occlusionHeight 物体的遮挡高度
	 * @param pathingPixelMap 物体的路径像素图
	 * @param pathingDeathPixelMap 物体的死亡路径像素图
	 */
	public CDestructableType(final String name, final float maxLife, final EnumSet<CTargetType> targetedAs,
			final String armorType, final int buildTime, final float occlusionHeight,
			final BufferedImage pathingPixelMap, final BufferedImage pathingDeathPixelMap) {
		this.name = name;
		this.maxLife = maxLife;
		this.targetedAs = targetedAs;
		this.armorType = armorType;
		this.buildTime = buildTime;
		this.occlusionHeight = occlusionHeight;
		this.pathingPixelMap = pathingPixelMap;
		this.pathingDeathPixelMap = pathingDeathPixelMap;
	}

	/**
	 * 获取物体名称。
	 *
	 * @return 物体名称
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 获取物体最大生命值。
	 *
	 * @return 物体最大生命值
	 */
	public float getMaxLife() {
		return this.maxLife;
	}

	/**
	 * 获取物体被视为目标的类型集合。
	 *
	 * @return 目标类型的集合
	 */
	public EnumSet<CTargetType> getTargetedAs() {
		return this.targetedAs;
	}

	/**
	 * 获取物体的护甲类型。
	 *
	 * @return 物体护甲类型
	 */
	public String getArmorType() {
		return this.armorType;
	}

	/**
	 * 获取物体的建造时间。
	 *
	 * @return 建造时间
	 */
	public int getBuildTime() {
		return this.buildTime;
	}

	/**
	 * 获取物体的遮挡高度。
	 *
	 * @return 遮挡高度
	 */
	public float getOcclusionHeight() {
		return occlusionHeight;
	}

	/**
	 * 获取物体的路径像素图。
	 *
	 * @return 路径像素图
	 */
	public BufferedImage getPathingPixelMap() {
		return this.pathingPixelMap;
	}

	/**
	 * 获取物体的死亡路径像素图。
	 *
	 * @return 死亡路径像素图
	 */
	public BufferedImage getPathingDeathPixelMap() {
		return this.pathingDeathPixelMap;
	}
}

