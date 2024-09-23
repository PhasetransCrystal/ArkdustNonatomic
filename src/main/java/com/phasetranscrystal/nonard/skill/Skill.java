package com.phasetranscrystal.nonard.skill;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.phasetranscrystal.nonard.Nonard;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import org.apache.commons.lang3.function.ToBooleanBiFunction;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.rmi.UnexpectedException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Skill<T extends LivingEntity> {
    public static final ResourceLocation NAME = Nonard.location("skill");

    public final int inactiveEnergy, maxCharge, initialEnergy, initialCharge, activeEnergy;

    public final String initBehavior;
    public final boolean initWithActive;

    public final ImmutableMap<String, Inactive<T>> inactives;
    public final ImmutableMap<String, Active<T>> actives;

    public final Consumer<SkillData<T>> onStart;
    public final Consumer<SkillData<T>> onEnd;
    public final ToBooleanBiFunction<SkillData<T>, String> judge;
    public final BiConsumer<SkillData<T>, SkillData.BehaviorRecord> stateChange;
    public final ImmutableMap<Class<? extends Event>, BiConsumer<? extends Event, SkillData<T>>> listeners;
    public final ImmutableSet<Flag> flags;

    private Skill(Builder<T> builder) throws UnexpectedException {
        this.inactiveEnergy = Math.max(builder.inactiveEnergy, 0);
        this.maxCharge = Math.max(builder.maxCharge, 1);
        this.initialEnergy = Math.clamp(0, inactiveEnergy, builder.initialEnergy);
        this.initialCharge = Math.clamp(0, maxCharge, builder.initialCharge);
        this.activeEnergy = Math.max(builder.activeEnergy, 0);

        this.initBehavior = builder.initBehavior;
        this.initWithActive = builder.initWithActive;

        if (initWithActive) {
            if (!builder.actives.containsKey(initBehavior))
                throw new UnexpectedException("Can't build skill: behavior(name=\"" + initBehavior + "\", active) is required but not found.");
        } else {
            if (!builder.inactives.containsKey(initBehavior))
                throw new UnexpectedException("Can't build skill: behavior(name=\"" + initBehavior + "\", inactive) is required but not found.");
        }

        ImmutableMap.Builder<String, Inactive<T>> inacBuilder = ImmutableMap.builder();
        builder.inactives.forEach((name, b) -> inacBuilder.put(name, b.build()));
        this.inactives = inacBuilder.build();

        ImmutableMap.Builder<String, Active<T>> acBuilder = ImmutableMap.builder();
        builder.actives.forEach((name, b) -> acBuilder.put(name, b.build()));
        this.actives = acBuilder.build();

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

        public String initBehavior = "default";
        public boolean initWithActive = false;

        public HashMap<String, Inactive.Builder<T>> inactives = new HashMap<>();
        public HashMap<String, Active.Builder<T>> actives = new HashMap<>();

        {
            inactives.put("default", Inactive.Builder.create());
            actives.put("default", Active.Builder.create());
        }

        public Consumer<SkillData<T>> onStart = NO_ACTION;
        public Consumer<SkillData<T>> onEnd = NO_ACTION;
        public ToBooleanBiFunction<SkillData<T>, String> judge = (data,behavior) -> true;
        public BiConsumer<SkillData<T>, SkillData.BehaviorRecord> stateChange = (data, behaviorRecord) -> {
        };
        public HashMap<Class<? extends Event>, BiConsumer<? extends Event, SkillData<T>>> listeners = new HashMap<>();

        public HashSet<Flag> flags = new HashSet<>();

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

            skill.inactives.forEach((name, builder) -> this.inactives.put(name, Inactive.Builder.create(builder)));
            skill.actives.forEach((name, builder) -> this.actives.put(name, Active.Builder.create(builder)));

            this.onStart = skill.onStart;
            this.onEnd = skill.onEnd;
            this.judge = skill.judge;
            this.stateChange = skill.stateChange;
            this.listeners.putAll(skill.listeners);
            return this;
        }

        public Builder<T> start(Consumer<SkillData<T>> consumer) {
            onStart = consumer;
            return this;
        }

        public Builder<T> judge(ToBooleanBiFunction<SkillData<T>, String> judge) {
            this.judge = judge;
            return this;
        }

        public Builder<T> inactive(Consumer<Inactive.Builder<T>> inactive) {
            inactive.accept(this.inactives.computeIfAbsent("default", key -> Inactive.Builder.create()));
            return this;
        }

        public Builder<T> active(Consumer<Active.Builder<T>> active) {
            active.accept(this.actives.computeIfAbsent("default", key -> Active.Builder.create()));
            return this;
        }

        public Builder<T> inactive(Consumer<Inactive.Builder<T>> inactive, String name) {
            Inactive.Builder<T> builder = Inactive.Builder.create();
            inactive.accept(builder);
            inactives.put(name, builder);
            return this;
        }

        public Builder<T> active(Consumer<Active.Builder<T>> active, String name) {
            Active.Builder<T> builder = Active.Builder.create();
            active.accept(builder);
            actives.put(name, builder);
            return this;
        }

        public Builder<T> cleanInactive() {
            this.inactives = new HashMap<>();
            this.inactives.put("default", Inactive.Builder.create());
            return this;
        }

        public Builder<T> cleanActive() {
            this.actives = new HashMap<>();
            this.actives.put("default", Active.Builder.create());
            return this;
        }

        public Builder<T> cleanInactive(String name) {
            this.inactives.remove(name);
            return this;
        }

        public Builder<T> cleanActive(String name) {
            this.actives.remove(name);
            return this;
        }

        public <E extends Event> Builder<T> onEvent(Class<E> clazz, BiConsumer<E, SkillData<T>> consumer) {
            listeners.put(clazz, consumer);
            return this;
        }

        public Builder<T> stateChange(BiConsumer<SkillData<T>, SkillData.BehaviorRecord> consumer) {
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

        public Skill<T> end() {
            try {
                return new Skill<>(this);
            } catch (UnexpectedException e) {
                throw new RuntimeException(e);
            }
        }

        public Skill<T> end(Consumer<SkillData<T>> consumer) {
            onEnd = consumer;
            return end();
        }
    }

    public enum Flag implements StringRepresentable {
        AUTO_START("auto_start", builder -> builder.inactives.get("default").reachReady(data -> data.switchTo(true,"default"))),
        INSTANT_COMPLETE("instant_complete", builder -> builder.activeEnergy = 0),
        PASSIVITY("passivity", builder -> builder.inactiveEnergy = 0),
        TIME_CHARGE("time_charge", builder -> builder.inactives.get("default").onTick((event, data) -> data.chargeEnergy(1))),
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
