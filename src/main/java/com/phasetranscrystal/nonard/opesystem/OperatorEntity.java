package com.phasetranscrystal.nonard.opesystem;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;

import com.phasetranscrystal.nonard.opesystem.ai.GoalContainer;
import com.phasetranscrystal.nonard.opesystem.ai.INamedGoal;

import java.util.ArrayList;
import java.util.List;

// TODO
public class OperatorEntity extends com.phasetranscrystal.nonatomic.core.OperatorEntity implements GoalContainer {

    public List<INamedGoal> normalGoals = new ArrayList<>();
    public List<INamedGoal> targetGoals = new ArrayList<>();

    protected OperatorEntity(EntityType<? extends OperatorEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean goalImmutable() {
        return !level().isClientSide;
    }

    @Override
    public List<INamedGoal> getNormalGoals() {
        return normalGoals;
    }

    @Override
    public List<INamedGoal> getTargetGoals() {
        return targetGoals;
    }

    @Override
    public GoalSelector getNormalGoalSelector() {
        return this.goalSelector;
    }

    @Override
    public GoalSelector getTargetGoalSelector() {
        return this.targetSelector;
    }
}
