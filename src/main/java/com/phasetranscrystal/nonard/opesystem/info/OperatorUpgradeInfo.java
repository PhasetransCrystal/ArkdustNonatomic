package com.phasetranscrystal.nonard.opesystem.info;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OperatorUpgradeInfo {

    // 精英化材料 [精英阶段 -> 所需材料]
    public final Map<Integer, List<ItemStack>> eliteMaterials;//TODO igose

    // 等级数值供应器 (参数: elite, level) -> 属性修改器
    private final AttributeExpression attributeExpression;


    public OperatorUpgradeInfo(Map<Integer, List<ItemStack>> eliteMaterials,
                               AttributeExpression attributeExpression) {

        this.eliteMaterials = ImmutableMap.copyOf(eliteMaterials);
        this.attributeExpression = attributeExpression;
    }

    public List<ItemStack> getEliteMaterials(int targetElite) {
        return eliteMaterials.getOrDefault(targetElite, Collections.emptyList());
    }

    public void binding(LivingEntity entity, int level) {
        attributeExpression.binding(entity, level);
    }
}