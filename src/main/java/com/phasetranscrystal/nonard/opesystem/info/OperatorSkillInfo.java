package com.phasetranscrystal.nonard.opesystem.info;

import com.phasetranscrystal.blast.skill.Skill;
import com.phasetranscrystal.nonard.opesystem.OperatorEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public class OperatorSkillInfo<T extends OperatorEntity> {
    private final int maxSkillLevel;
    private final int[] levelRequest;//length = maxSkillLevel - 1
    private final int[] eliteRequest;
    private final Map<Integer, List<ItemStack>> skillUpgradeRequest;
    private final Skill<T> skill;
    private final MappingExpression valueExpression;


    public OperatorSkillInfo(int maxSkillLevel, int[] levelRequest, int[] eliteRequest, Map<Integer, List<ItemStack>> skillUpgradeRequest, Skill<T> skill, MappingExpression valueExpression) {
        this.maxSkillLevel = maxSkillLevel;
        this.levelRequest = levelRequest;
        this.eliteRequest = eliteRequest;
        this.skillUpgradeRequest = skillUpgradeRequest;
        this.skill = skill;
        this.valueExpression = valueExpression;
    }

    public MappingExpression getValueExpression() {
        return valueExpression;
    }

    public Skill<T> getSkill() {
        return skill;
    }

    public Map<Integer, List<ItemStack>> getSkillUpgradeRequest() {
        return skillUpgradeRequest;
    }

    public int[] getEliteRequest() {
        return eliteRequest;
    }

    public int[] getLevelRequest() {
        return levelRequest;
    }

    public int getMaxSkillLevel() {
        return maxSkillLevel;
    }

}
