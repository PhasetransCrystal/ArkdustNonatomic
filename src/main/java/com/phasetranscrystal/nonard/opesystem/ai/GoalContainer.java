package com.phasetranscrystal.nonard.opesystem.ai;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.goal.GoalSelector;

import java.util.List;

public interface GoalContainer {

    boolean goalImmutable();

    List<INamedGoal> getNormalGoals();

    List<INamedGoal> getTargetGoals();

    GoalSelector getNormalGoalSelector();

    GoalSelector getTargetGoalSelector();

    default void addNormalGoal(INamedGoal goal) {
        if (goalImmutable()) return;
        this.getNormalGoals().add(goal);
        this.getNormalGoalSelector().addGoal(goal.priority(), goal.goal());
    };

    default void addTargetGoal(INamedGoal goal) {
        if (goalImmutable()) return;
        this.getTargetGoals().add(goal);
        this.getTargetGoalSelector().addGoal(goal.priority(), goal.goal());
    };

    default void removeNormalGoal(ResourceLocation goal) {
        if (goalImmutable()) return;
        for (INamedGoal t : this.getNormalGoals()) {
            if (t.test(goal)) {
                this.getNormalGoals().remove(t);
                this.getNormalGoalSelector().removeGoal(t.goal());
            }
        }
    };

    default void removeTargetGoal(ResourceLocation goal) {
        if (goalImmutable()) return;

        for (INamedGoal t : this.getTargetGoals()) {
            if (t.test(goal)) {
                this.getTargetGoals().remove(t);
                this.getTargetGoalSelector().removeGoal(t.goal());
            }
        }
    };
}
