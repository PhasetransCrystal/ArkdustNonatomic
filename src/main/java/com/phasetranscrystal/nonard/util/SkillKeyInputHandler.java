package com.phasetranscrystal.nonard.util;

import com.phasetranscrystal.blast.skill.SkillData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * 用于处理技能按键输入的函数接口。
 */
@FunctionalInterface
public interface SkillKeyInputHandler<T extends Entity> {
    void accept(SkillData<T> data, int key, int action, int modifiers);
}
