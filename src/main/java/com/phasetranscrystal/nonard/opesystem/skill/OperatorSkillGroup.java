package com.phasetranscrystal.nonard.opesystem.skill;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.phasetranscrystal.blast.skill.Skill;
import com.phasetranscrystal.nonard.opesystem.ArknightsOperatorType;
import com.phasetranscrystal.nonard.opesystem.OperatorEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OperatorSkillGroup<E extends OperatorEntity> {

    public static final Logger LOGGER = LogManager.getLogger("ArkdustNona:OpeSkill:SkillGroup");
    public final int defaultChoose;
    public final ArknightsOperatorType<E> type;
    public final ImmutableList<OperatorSkill<E>> skills;
    public final BiMap<Skill<E>, OperatorSkill<E>> skillMap;

    public OperatorSkillGroup(int defaultChoose, ArknightsOperatorType<E> operatorType, ImmutableList<OperatorSkill<E>> skills) {
        this.type = operatorType;
        if (skills.isEmpty()) {
            this.skills = skills;
            this.defaultChoose = defaultChoose;
            LOGGER.warn("No operator skills found. At least one skill is required. Instance:{}", this);
            throw new IllegalArgumentException("No operator skills found.");
        }

        if (defaultChoose < 0 || defaultChoose >= skills.size()) {
            LOGGER.error("Invalid default choose value: {}. Skill list size is {}.", defaultChoose, skills.size());
            defaultChoose = 0;
        }

        this.defaultChoose = defaultChoose;
        this.skills = skills;
        ImmutableBiMap.Builder<Skill<E>, OperatorSkill<E>> builder = ImmutableBiMap.builder();
        for (OperatorSkill<E> skill : skills) {
            builder.put(skill.skill, skill);
        }
        this.skillMap = builder.build();
    }

    @SafeVarargs
    public OperatorSkillGroup(int defaultChoose, ArknightsOperatorType<E> operatorType, OperatorSkill<E>... skills) {
        this(defaultChoose, operatorType, ImmutableList.copyOf(skills));
    }

    @Override
    public String toString() {
        return "SkillGroup{" +
                "defaultChoose=" + defaultChoose +
                ", operatorClass=" + type +
                ", skills=" + skills +
                '}';
    }
}
