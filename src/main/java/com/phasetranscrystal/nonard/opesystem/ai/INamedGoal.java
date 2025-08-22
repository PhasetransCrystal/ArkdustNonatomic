package com.phasetranscrystal.nonard.opesystem.ai;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.goal.Goal;

public record INamedGoal(ResourceLocation id, int priority, Goal goal) {
    public boolean test(ResourceLocation id) {
        return this.id.equals(id);
    }
}
