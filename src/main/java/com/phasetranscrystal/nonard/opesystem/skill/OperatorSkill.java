package com.phasetranscrystal.nonard.opesystem.skill;

import com.phasetranscrystal.blast.skill.Skill;
import com.phasetranscrystal.nonard.migrate.igmater_supplier.IGObjectsExtractor;
import com.phasetranscrystal.nonard.opesystem.OperatorEntity;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.List;
import java.util.function.IntFunction;

public class OperatorSkill<O extends OperatorEntity> {
    public final Skill<O> skill;
    public final int maxLevel;
    //提升到的等级 --> 升级需要的资源
    public final IntFunction<List<IGObjectsExtractor<?>>> extractorsByLevel;
    //技能标签不会直接影响技能的表现(原本有这一设计但是已被移除，我们希望将其转移至技能模板层面)，它只会在ui显示等地方被使用
    protected final HashSet<SkillFlags> skillFlags = new HashSet<>();

    public OperatorSkill(final Skill<O> skill, final int maxLevel, final IntFunction<List<IGObjectsExtractor<?>>> extractorsByLevel) {
        this.skill = skill;
        this.maxLevel = maxLevel;
        this.extractorsByLevel = extractorsByLevel;
    }

    public ResourceLocation skillIconRL(){
        return skill.getResourceKey().location().withPrefix("arkdust_nona/skill_icon/");
    }

    public String skillNameI18nKey(){
        ResourceLocation location = skill.getResourceKey().location();
        return "arkdust_nona.skill." + location.getNamespace() + "." + location.getPath() + ".name";
    }

    public String skillExplainI18nKey(){
        ResourceLocation location = skill.getResourceKey().location();
        return "arkdust_nona.skill." + location.getNamespace() + "." + location.getPath() + ".explain";
    }

    public HashSet<SkillFlags> getSkillFlagsCopied() {
        return new HashSet<>(skillFlags);
    }
}
