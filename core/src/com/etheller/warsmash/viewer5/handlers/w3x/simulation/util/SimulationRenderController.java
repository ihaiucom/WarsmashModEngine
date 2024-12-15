package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import java.awt.image.BufferedImage;

import com.badlogic.gdx.graphics.Color;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
// 模拟渲染控制器接口，定义了创建和管理游戏对象、特效、声音等的方法
public interface SimulationRenderController {

	// 创建攻击投射物
	CAttackProjectile createAttackProjectile(CSimulation simulation, float launchX, float launchY, float launchFacing,
			CUnit source, CUnitAttackMissile attack, AbilityTarget target, float damage, int bounceIndex,
			CUnitAttackListener attackListener);

	// 创建能力投射物
	CAbilityProjectile createProjectile(CSimulation cSimulation, float launchX, float launchY, float launchFacing,
			float speed, boolean homing, CUnit source, War3ID spellAlias, AbilityTarget target,
			CAbilityProjectileListener projectileListener);

	// 创建碰撞投射物
	CCollisionProjectile createCollisionProjectile(CSimulation cSimulation, float launchX, float launchY,
			float launchFacing, float projectileSpeed, boolean homing, CUnit source, War3ID spellAlias,
			AbilityTarget target, int maxHits, int hitsPerTarget, float startingRadius, float finalRadius,
			float collisionInterval, CAbilityCollisionProjectileListener projectileListener, boolean provideCounts);

	// 创建伪投射物
	CPsuedoProjectile createPseudoProjectile(CSimulation cSimulation, float launchX, float launchY, float launchFacing,
			float projectileSpeed, float projectileStepInterval, int projectileArtSkip, boolean homing, CUnit source, War3ID spellAlias,
			CEffectType effectType, int effectArtIndex, AbilityTarget target, int maxHits, int hitsPerTarget,
			float startingRadius, float finalRadius, CAbilityCollisionProjectileListener projectileListener, boolean provideCounts);

	// 创建闪电效果
	SimulationRenderComponentLightning createLightning(CSimulation simulation, War3ID lightningId, CUnit source,
													   CUnit target);

	// 创建带有持续时间的闪电效果
	SimulationRenderComponentLightning createLightning(CSimulation simulation, War3ID lightningId, CUnit source,
													   CUnit target, Float duration);

	// 创建能力闪电效果
	SimulationRenderComponentLightning createAbilityLightning(CSimulation simulation, War3ID lightningId, CUnit source,
			CUnit target, int index);

	// 创建带有持续时间的能力闪电效果
	SimulationRenderComponentLightning createAbilityLightning(CSimulation simulation, War3ID lightningId, CUnit source,
			CUnit target, int index, Float duration);

	// 创建单位
	CUnit createUnit(CSimulation simulation, final War3ID typeId, final int playerIndex, final float x, final float y,
			final float facing);

	// 创建物品
	CItem createItem(CSimulation simulation, final War3ID typeId, final float x, final float y);

	// 创建可破坏物
	CDestructable createDestructable(War3ID typeId, float x, float y, float facing, float scale, int variation);

	// 创建带有Z轴的可破坏物
	CDestructable createDestructableZ(War3ID typeId, float x, float y, float z, float facing, float scale,
			int variation);

	// 创建即时攻击效果
	void createInstantAttackEffect(CSimulation cSimulation, CUnit source, CUnitAttackInstant attack, CWidget target);

	// 播放伤害声音
	void spawnDamageSound(CWidget damagedDestructable, String weaponSound, String armorType);

	// 播放单位建造声音
	void spawnUnitConstructionSound(CUnit constructingUnit, CUnit constructedStructure);

	// 移除单位
	void removeUnit(CUnit unit);

	// 移除可破坏物
	void removeDestructable(CDestructable dest);

	// 获取建筑路径像素图
	BufferedImage getBuildingPathingPixelMap(War3ID rawcode);

	// 获取可破坏物路径像素图
	BufferedImage getDestructablePathingPixelMap(War3ID rawcode);

	// 获取可破坏物死亡路径像素图
	BufferedImage getDestructablePathingDeathPixelMap(War3ID rawcode);

	// 播放单位建造完成声音
	void spawnUnitConstructionFinishSound(CUnit constructedStructure);

	// 播放单位升级完成声音
	void spawnUnitUpgradeFinishSound(CUnit constructedStructure);

	// 播放死亡爆炸效果
	void spawnDeathExplodeEffect(CUnit cUnit, War3ID explodesOnDeathBuffId);

	// 播放获得等级效果
	void spawnGainLevelEffect(CUnit cUnit);

	// 播放单位准备就绪声音
	void spawnUnitReadySound(CUnit trainedUnit);

	// 单位重新定位
	void unitRepositioned(CUnit cUnit);

	// 显示文本标签
	void spawnTextTag(CUnit unit, TextTagConfigType configType, int displayAmount);

	// 显示带有消息的文本标签
	void spawnTextTag(CUnit unit, TextTagConfigType configType, String message);

	// 在单位上播放特效
	void spawnEffectOnUnit(CUnit unit, String effectPath);

	// 在单位上播放临时法术效果
	void spawnTemporarySpellEffectOnUnit(CUnit unit, War3ID alias, CEffectType effectType);

	// 在单位上播放持久法术效果
	SimulationRenderComponentModel spawnPersistentSpellEffectOnUnit(CUnit unit, War3ID alias, CEffectType effectType);

	// 在单位上播放带有索引的持久法术效果
	SimulationRenderComponentModel spawnPersistentSpellEffectOnUnit(CUnit unit, War3ID alias, CEffectType effectType, int index);

	// 在指定点上播放法术效果
	SimulationRenderComponentModel spawnSpellEffectOnPoint(float x, float y, float facing, War3ID alias,
			CEffectType effectType, int index);

	// 在指定点上播放临时法术效果
	void spawnTemporarySpellEffectOnPoint(float x, float y, float facing, War3ID alias,
			CEffectType effectType, int index);

	// 播放单位获取物品声音
	void spawnUIUnitGetItemSound(CUnit cUnit, CItem item);

	// 播放单位丢弃物品声音
	void spawnUIUnitDropItemSound(CUnit cUnit, CItem item);

	// 播放能力声音效果
	SimulationRenderComponent spawnAbilitySoundEffect(CUnit caster, War3ID alias);

	// 循环播放能力声音效果
	SimulationRenderComponent loopAbilitySoundEffect(CUnit caster, War3ID alias);

	// 停止能力声音效果
	void stopAbilitySoundEffect(CUnit caster, War3ID alias);

	// 替换单位的首选选择
	void unitPreferredSelectionReplacement(CUnit unit, CUnit newUnit);

	// 英雄复活事件
	void heroRevived(CUnit trainedUnit);

	// 英雄死亡事件
	void heroDeathEvent(CUnit cUnit);

	// 在可破坏物上创建法术效果
	SimulationRenderComponentModel createSpellEffectOverDestructable(CUnit source, CDestructable target, War3ID alias,
			float artAttachmentHeight);

	// 单位升级事件
	void unitUpgradingEvent(CUnit unit, War3ID upgradeIdType);

	// 单位取消升级事件
	void unitCancelUpgradingEvent(CUnit unit, War3ID upgradeIdType);

	// 设置瘟疫区域
	void setBlight(float x, float y, float radius, boolean blighted);

	// 更新单位类型
	void unitUpdatedType(CUnit unit, War3ID typeId);

	// 更改单位颜色
	void changeUnitColor(CUnit unit, int playerIndex);

	// 更改单位顶点颜色
	void changeUnitVertexColor(CUnit unit, Color color);

	// 更改单位顶点颜色
	void changeUnitVertexColor(CUnit unit, float r, float g, float b);

	// 更改单位顶点颜色
	void changeUnitVertexColor(CUnit unit, float r, float g, float b, float a);

	// 获取地形高度
	int getTerrainHeight(float x, float y);

	// 判断地形是否为岩石
	boolean isTerrainRomp(float x, float y);

	// 判断地形是否为水域
	boolean isTerrainWater(float x, float y);

}

