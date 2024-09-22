package com.phasetranscrystal.nonard.skill;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.phasetranscrystal.nonard.Nonard;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Skill<T extends LivingEntity> {
    public static final ResourceLocation NAME = Nonard.location("skill");

    public final int inactiveEnergy, maxCharge, initialEnergy, initialCharge, activeEnergy;

    public final Inactive<T> inactive;
    public final Active<T> active;

    public final Consumer<SkillData<T>> onStart;
    public final Consumer<SkillData<T>> onEnd;
    public final Function<SkillData<T>, Boolean> judge;
    public final BiConsumer<SkillData<T>, Boolean> stateChange;
    public final ImmutableMap<Class<? extends Event>, BiConsumer<? extends Event, SkillData<T>>> listeners;
    public final ImmutableSet<Flag> flags;

    private Skill(Builder<T> builder) {
        this.inactiveEnergy = Math.max(builder.inactiveEnergy, 0);
        this.maxCharge = Math.max(builder.maxCharge, 1);
        this.initialEnergy = Math.clamp(0, inactiveEnergy, builder.initialEnergy);
        this.initialCharge = Math.clamp(0, maxCharge, builder.initialCharge);
        this.activeEnergy = Math.max(builder.activeEnergy, 0);
        this.inactive = builder.inactive.build();
        this.active = builder.active.build();
        this.onStart = builder.onStart;
        this.onEnd = builder.onEnd;
        this.judge = builder.judge;
        this.stateChange = builder.stateChange;
        this.listeners = ImmutableMap.copyOf(builder.listeners);
        this.flags = ImmutableSet.copyOf(builder.flags);
    }

    public static class Builder<T extends LivingEntity> {
        public final Consumer<SkillData<T>> NO_ACTION = data -> {
        };

        public int inactiveEnergy, maxCharge, initialEnergy, initialCharge, activeEnergy;

        protected Inactive.Builder<T> inactive = Inactive.Builder.create();
        protected Active.Builder<T> active = Active.Builder.create();

        protected Consumer<SkillData<T>> onStart = NO_ACTION;
        protected Consumer<SkillData<T>> onEnd = NO_ACTION;
        protected Function<SkillData<T>, Boolean> judge = data -> true;
        protected BiConsumer<SkillData<T>, Boolean> stateChange = (data, bool) -> {
        };
        protected HashMap<Class<? extends Event>, BiConsumer<? extends Event, SkillData<T>>> listeners = new HashMap<>();

        protected HashSet<Flag> flags = new HashSet<>();

        private Builder(int inactiveEnergy, int maxChargeTimes) {
            this.inactiveEnergy = inactiveEnergy;
            this.activeEnergy = inactiveEnergy;
            this.maxCharge = maxChargeTimes;
        }

        private Builder(int inactiveEnergy) {
            this.inactiveEnergy = inactiveEnergy;
            this.activeEnergy = inactiveEnergy;
            this.maxCharge = 1;
        }

        private Builder(int inactiveEnergy, int maxCharge, int initialEnergy, int initialCharge, int activeEnergy) {
            this.inactiveEnergy = inactiveEnergy;
            this.maxCharge = maxCharge;
            this.activeEnergy = activeEnergy;
            this.initialEnergy = initialEnergy;
            this.initialCharge = initialCharge;
        }

        public static <T extends LivingEntity> Builder<T> of(int energyCost, int maxChargeTimes) {
            return new Builder<>(energyCost, maxChargeTimes);
        }

        public static <T extends LivingEntity> Builder<T> of(int energyCost) {
            return new Builder<>(energyCost);
        }

        public static <T extends LivingEntity> Builder<T> of(Skill<T> skill) {
            Builder<T> builder = of(skill.inactiveEnergy, skill.maxCharge, skill.initialEnergy, skill.initialCharge, skill.activeEnergy);
            return builder.copyFrom(skill);
        }

        public static <T extends LivingEntity> Builder<T> of(int energyCost, int maxCharge, int initialEnergy, int initialCharge, int activeEnergy) {
            return new Builder<>(energyCost, maxCharge, initialEnergy, initialCharge, activeEnergy);
        }

        public Builder<T> copyFrom(Skill<T> skill) {
            this.inactive = Inactive.Builder.create(skill.inactive);
            this.active = Active.Builder.create(skill.active);
            this.onStart = skill.onStart;
            this.onEnd = skill.onEnd;
            this.judge = skill.judge;
            this.stateChange = skill.stateChange;
            this.listeners.putAll(skill.listeners);
            return this;
        }

        public Builder<T> push(Consumer<SkillData<T>> consumer) {
            onStart = consumer;
            return this;
        }

        public Builder<T> judge(Function<SkillData<T>, Boolean> judge) {
            this.judge = judge;
            return this;
        }

        public Builder<T> inactive(Consumer<Inactive.Builder<T>> inactive) {
            inactive.accept(this.inactive);
            return this;
        }

        public Builder<T> active(Consumer<Active.Builder<T>> active) {
            active.accept(this.active);
            return this;
        }

        public Builder<T> cleanInactive() {
            this.inactive = Inactive.Builder.create();
            return this;
        }

        public Builder<T> cleanActive() {
            this.active = Active.Builder.create();
            return this;
        }

        public <E extends Event> Builder<T> onEvent(Class<E> clazz, BiConsumer<E, SkillData<T>> consumer) {
            listeners.put(clazz, consumer);
            return this;
        }

        public Builder<T> stateChange(BiConsumer<SkillData<T>, Boolean> consumer) {
            this.stateChange = consumer;
            return this;
        }

        public Builder<T> apply(Consumer<Builder<T>> consumer) {
            consumer.accept(this);
            return this;
        }

        public Builder<T> flag(Flag flag, boolean execute) {
            if (!flags.contains(flag)) {
                flags.add(flag);
                if (execute) {
                    flag.consumer.accept(this);
                }
            }
            return this;
        }

        public Skill<T> pop() {
            return new Skill<>(this);
        }

        public Skill<T> pop(Consumer<SkillData<T>> consumer) {
            onEnd = consumer;
            return pop();
        }
    }

    public enum Flag implements StringRepresentable {
        AUTO_START("auto_start", builder -> builder.inactive.reachReady(SkillData::nextStage)),
        INSTANT_COMPLETE("instant_complete", builder -> builder.activeEnergy = 0),
        PASSIVITY("passivity", builder -> builder.inactiveEnergy = 0),
        TIME_CHARGE("time_charge", builder -> builder.inactive.onTick((event, data) -> data.chargeEnergy(1))),
        ;

        public final String name;
        public final Consumer<Builder<? extends LivingEntity>> consumer;

        Flag(String name, Consumer<Builder<? extends LivingEntity>> consumer) {
            this.name = name;
            this.consumer = consumer;
        }

        Flag(String name) {
            this(name, builder -> {
            });
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }

    public ResourceKey<Skill<?>> getResourceKey() {
        return Registries.SKILL.getResourceKey(this).get();
    }
}
