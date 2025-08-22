package com.phasetranscrystal.nonard.opesystem.trait;

import com.phasetranscrystal.nonard.opesystem.OperatorEntity;
import com.phasetranscrystal.nonard.opesystem.ai.INamedGoal;

public record TargetTrait(TraitConfig config , INamedGoal target) implements Trait {

    @Override
    public TraitConfig get() {
        return config;
    }

    @Override
    public void doHandle(OperatorEntity operator, TraitContext context) {
    }
}
