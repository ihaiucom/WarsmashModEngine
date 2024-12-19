package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.tinker;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPointTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimedLife;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
// 口袋工厂 建造一座能自动生成地精的工厂。这些地精都是人工地精。它们是强大的攻击者，在阵亡之后还能发生爆炸从而对周围的造成一定的伤害。|n|n|cffffcc00等级 1|r – 爆炸具有<Asdg,DataB1> 攻击力。|n|cffffcc00等级 2|r – 爆炸具有<Asd2,DataB1> 攻击力。|n|cffffcc00等级 3|r – 爆炸具有<Asd3,DataB1> 攻击力。|n工厂持续<ANsy,Dur3> 秒。
public class CAbilityPocketFactory extends CAbilityPointTargetSpellBase {
	// 定义工厂单位的ID
	private War3ID factoryUnitId;
	// 定义工厂单位的增益ID
	private War3ID factoryUnitBuffId;
	// 定义弹道速度
	private float projectileSpeed;
	// 定义生成单位的ID
	private War3ID spawnUnitId;
	// 定义牵引范围
	private float leashRange;
	// 定义生成间隔
	private float spawnInterval;
	// 定义生成单位的增益ID
	private War3ID spawnUnitBuffId;
	// 定义生成单位的持续时间
	private float spawnUnitDuration;
	// 定义生成单位的偏移量
	private float spawnUnitOffset;

	/**
	 * 构造函数
	 *
	 * @param handleId 技能句柄ID
	 * @param alias    技能别名
	 */
	public CAbilityPocketFactory(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	/**
	 * 获取基础订单ID
	 *
	 * @return 订单ID
	 */
	@Override
	public int getBaseOrderId() {
		return OrderIds.summonfactory;
	}

	/**
	 * 填充技能数据
	 *
	 * @param worldEditorAbility 世界编辑器技能对象
	 * @param level              技能等级
	 */
	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		// 从世界编辑器技能对象中获取并设置相关数据
		this.factoryUnitId = War3ID.fromString(worldEditorAbility.getFieldAsString(AbilityFields.UNIT_ID + level, 0));
		this.factoryUnitBuffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level, 0);
		this.spawnUnitBuffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level, 1);
		projectileSpeed = worldEditorAbility.getFieldAsFloat(AbilityFields.PROJECTILE_SPEED, 0);

		this.spawnUnitId = War3ID.fromString(worldEditorAbility.getFieldAsString(AbilityFields.DATA_B + level, 0));
		this.leashRange = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_E + level, 0);
		this.spawnInterval = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		this.spawnUnitDuration = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_C + level, 0);
		this.spawnUnitOffset = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_D + level, 0);
	}

	/**
	 * 执行技能效果
	 *
	 * @param simulation 模拟环境
	 * @param caster     施法单位
	 * @param target     目标单位
	 * @return 是否成功执行
	 */
	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		// 创建弹道并设置相关参数
		simulation.createProjectile(caster, getAlias(), caster.getX(), caster.getY(), (float) caster.angleTo(target),
				projectileSpeed, false, target, new CAbilityProjectileListener() {
					@Override
					public void onLaunch(final CSimulation game, final AbilityTarget target) {
						// 弹道发射时的操作
					}

					@Override
					public void onHit(final CSimulation game, final AbilityTarget target) {
						// 弹道命中时的操作
						// 创建工厂单位并设置相关属性
						final CUnit factoryUnit = simulation.createUnitSimple(CAbilityPocketFactory.this.factoryUnitId,
								caster.getPlayerIndex(), target.getX(), target.getY(),
								game.getGameplayConstants().getBuildingAngle());
						factoryUnit.addClassification(CUnitClassification.SUMMONED);
						factoryUnit.add(game, new CBuffTimedLife(game.getHandleIdAllocator().createId(),
								factoryUnitBuffId, getDuration(), false));
						// 创建工厂能力并设置相关属性
						final CAbilityFactory factory = new CAbilityFactory(game.getHandleIdAllocator().createId(),
								War3ID.fromString("ANfy"), War3ID.fromString("ANfy"));
						factory.setLeashRange(leashRange);
						factory.setSpawnUnitId(spawnUnitId);
						factory.setSpawnInterval(spawnInterval);
						factory.setBuffId(spawnUnitBuffId);
						factory.setDuration(spawnUnitDuration);
						factory.setAreaOfEffect(spawnUnitOffset);
						factory.setIconShowing(false);
						// 将工厂能力添加到工厂单位上
						factoryUnit.add(game, factory);

						// 为工厂单位添加集结能力
						factoryUnit.add(game, new CAbilityRally(game.getHandleIdAllocator().createId()));
					}
				});
		return false;
	}
}

