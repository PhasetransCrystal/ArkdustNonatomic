package com.phasetranscrystal.nonard.opesystem.trait;

import com.phasetranscrystal.nonard.opesystem.OperatorEntity;
import com.phasetranscrystal.nonard.opesystem.ai.INamedGoal;

public record GoalTrait(TraitConfig config, INamedGoal goal, boolean isTarget) implements Trait {

    @Override
    public TraitConfig get() {
        return config;
    }

    @Override
    public void doHandle(OperatorEntity operator, Context context) {
        if (isTarget) {
            operator.addTargetGoal(goal);
        } else {
            operator.addNormalGoal(goal);
        }
    }
}
