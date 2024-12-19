package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpell;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;

public class CAbilityTypeDefinitionSpellBase implements CAbilityTypeDefinition {
    // 定义一个AbilityConstructor类型的私有变量abilityConstructor，用于构造能力
    private final AbilityConstructor abilityConstructor;

    /**
     * 构造函数，用于初始化abilityConstructor
     *
     * @param abilityConstructor 用于构造能力的AbilityConstructor实例
     */
    public CAbilityTypeDefinitionSpellBase(final AbilityConstructor abilityConstructor) {
        this.abilityConstructor = abilityConstructor;
    }

    /**
     * 创建一个新的CAbilityType实例
     *
     * @param alias            能力的别名
     * @param abilityEditorData 能力的编辑器数据
     * @return 新创建的CAbilityType实例
     */
    @Override
    public CAbilityType<?> createAbilityType(final War3ID alias, final GameObject abilityEditorData) {
        // 创建一个空的CAbilityTypeLevelData列表
        final List<CAbilityTypeLevelData> emptyLevelDatas = new ArrayList<>();
        // 从abilityEditorData中获取能力的等级数
        final int levels = abilityEditorData.getFieldAsInteger(AbilityFields.LEVELS, 0);
        // 遍历等级数，为每个等级添加一个空的CAbilityTypeLevelData实例
        for (int i = 0; i < levels; i++) {
            // 注意：目前，这个列表的大小被用于英雄能力的最大等级
            emptyLevelDatas.add(null);
        }
        // 创建一个新的CAbilityType实例，使用alias作为别名，使用AbilityFields.CODE字段作为代码，并使用emptyLevelDatas作为等级数据
        return new CAbilityType<CAbilityTypeLevelData>(alias,
                abilityEditorData.getFieldAsWar3ID(AbilityFields.CODE, -1), emptyLevelDatas) {
            /**
             * 创建一个新的CAbilitySpell实例
             *
             * @param handleId 能力的句柄ID
             * @return 新创建的CAbilitySpell实例
             */
            @Override
            public CAbility createAbility(final int handleId) {
                // 使用abilityConstructor创建一个新的CAbilitySpell实例
                final CAbilitySpell spellAbility = CAbilityTypeDefinitionSpellBase.this.abilityConstructor
                        .create(handleId, getAlias());
                // 使用abilityEditorData填充spellAbility的属性
                spellAbility.populate(abilityEditorData, 1);
                // 返回创建的spellAbility实例
                return spellAbility;
            }

            /**
             * 设置能力的等级
             *
             * @param game          游戏实例
             * @param unit          拥有该能力的单位
             * @param existingAbility 现有的能力实例
             * @param level         要设置的等级
             */
            @Override
            public void setLevel(final CSimulation game, final CUnit unit, final CLevelingAbility existingAbility, final int level) {
                // 设置现有能力的等级
                existingAbility.setLevel(game, unit, level);
                // 使用abilityEditorData填充现有能力的属性
                ((CAbilitySpell) existingAbility).populate(abilityEditorData, level);
            }
        };
    }

    /**
     * 定义一个静态内部接口AbilityConstructor，用于创建CAbilitySpell实例
     */
    public static interface AbilityConstructor {
        /**
         * 创建一个新的CAbilitySpell实例
         *
         * @param handleId 能力的句柄ID
         * @param alias    能力的别名
         * @return 新创建的CAbilitySpell实例
         */
        CAbilitySpell create(int handleId, War3ID alias);
    }
}
