package com.phasetranscrystal.nonard.opesystem.skill;

import net.minecraft.resources.ResourceLocation;

import com.google.common.collect.ImmutableSet;
import com.phasetranscrystal.blast.skill.Skill;
import com.phasetranscrystal.nonard.opesystem.OperatorEntity;

import java.util.HashSet;

public class OperatorSkill<O extends OperatorEntity> {

    public final Skill<O> skill;
    // 提升到的等级 --> 升级需要的资源
    public final SkillUpgradeRequirement extractorsByLevel;
    // 技能标签不会直接影响技能的表现(原本有这一设计但是已被移除，我们希望将其转移至技能模板层面)，它只会在ui显示等地方被使用
    public final ImmutableSet<SkillFlags> skillFlags;

    public OperatorSkill(final Skill<O> skill, SkillUpgradeRequirement extractorsByLevel, SkillFlags... skillFlags) {
        this.skill = skill;
        this.extractorsByLevel = extractorsByLevel;
        this.skillFlags = ImmutableSet.copyOf(skillFlags);
    }

    public int getMaxSkillLevel() {
        return extractorsByLevel.maxLevel();
    }

    public ResourceLocation skillIconRL() {
        return skill.getResourceKey().location().withPrefix("arkdust_nona/skill_icon/");
    }

    public String skillNameI18nKey() {
        ResourceLocation location = skill.getResourceKey().location();
        return "arkdust.nona.skill." + location.getNamespace() + "." + location.getPath() + ".name";
    }

    public String skillExplainI18nKey() {
        ResourceLocation location = skill.getResourceKey().location();
        return "arkdust.nona.skill." + location.getNamespace() + "." + location.getPath() + ".explain";
    }

    public HashSet<SkillFlags> getSkillFlagsCopied() {
        return new HashSet<>(skillFlags);
    }
}
