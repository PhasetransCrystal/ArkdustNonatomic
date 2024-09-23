package com.phasetranscrystal.nonard.skill;

import com.google.common.collect.ImmutableMap;
import com.phasetranscrystal.nonard.eventdistribute.EventConsumer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Inactive<T extends LivingEntity> {
    public static final Inactive<?> EMPTY = Builder.create().build();

    public final int delay;
    public final BiConsumer<SkillData<T>, Integer> chargeChanged;
    public final BiConsumer<SkillData<T>, Integer> energyChanged;
    public final Consumer<SkillData<T>> onStart;
    public final Consumer<SkillData<T>> onEnd;
    public final Consumer<SkillData<T>> reachReady;
    public final Consumer<SkillData<T>> reachStop;
    public final ImmutableMap<Class<? extends Event>, BiConsumer<? extends Event, SkillData<T>>> listeners;

    public Inactive(Builder<T> builder) {
        this.delay = Math.max(builder.delay, 0);
        this.chargeChanged = builder.chargeChanged;
        this.energyChanged = builder.energyChanged;
        this.onStart = builder.onStart;
        this.onEnd = builder.onEnd;
        this.reachReady = builder.reachReady;
        this.reachStop = builder.reachStop;
        this.listeners = ImmutableMap.copyOf(builder.listeners);
    }

    public static class Builder<T extends LivingEntity> {
        public int delay = 0;
        public final Consumer<SkillData<T>> NO_ACTION = data -> {
        };

        public BiConsumer<SkillData<T>, Integer> chargeChanged = (data, integer) -> {
        };
        public BiConsumer<SkillData<T>, Integer> energyChanged = (data, integer) -> {
        };
        public Consumer<SkillData<T>> onStart = NO_ACTION;
        public Consumer<SkillData<T>> onEnd = NO_ACTION;
        public Consumer<SkillData<T>> reachReady = NO_ACTION;
        public Consumer<SkillData<T>> reachStop = NO_ACTION;
        public HashMap<Class<? extends Event>, BiConsumer<? extends Event, SkillData<T>>> listeners = new HashMap<>();

        public static <T extends LivingEntity> Builder<T> create() {
            return new Builder<>();
        }

        public static <T extends LivingEntity> Builder<T> create(Inactive<T> root) {
            Builder<T> builder = new Builder<T>();
            builder.chargeChanged = root.chargeChanged;
            builder.energyChanged = root.energyChanged;
            builder.onStart = root.onStart;
            builder.onEnd = root.onEnd;
            builder.reachReady = root.reachReady;
            builder.reachStop = root.reachStop;
            builder.listeners.putAll(root.listeners);
            return builder;
        }

        public Builder<T> start(Consumer<SkillData<T>> consumer) {
            onStart = consumer;
            return this;
        }

        public Builder<T> setDelay(int delay) {
            this.delay = delay;
            return this;
        }

        public Builder<T> chargeChanged(BiConsumer<SkillData<T>, Integer> consumer) {
            this.chargeChanged = consumer;
            return this;
        }

        public Builder<T> energyChanged(BiConsumer<SkillData<T>, Integer> consumer) {
            this.energyChanged = consumer;
            return this;
        }

        public Builder<T> onTick(BiConsumer<EntityTickEvent.Post, SkillData<T>> consumer) {
            listeners.put(EntityTickEvent.Post.class, consumer);
            return this;
        }

        public Builder<T> onHurt(BiConsumer<LivingDamageEvent.Post, SkillData<T>> consumer) {
            listeners.put(LivingDamageEvent.Post.class, consumer);
            return this;
        }

        public Builder<T> onAttack(BiConsumer<EventConsumer.EntityAttackEvent.Post, SkillData<T>> consumer) {
            listeners.put(EventConsumer.EntityAttackEvent.Post.class, consumer);
            return this;
        }

        public Builder<T> onKill(BiConsumer<EventConsumer.EntityKillEvent.Post, SkillData<T>> consumer) {
            listeners.put(EventConsumer.EntityKillEvent.Post.class, consumer);
            return this;
        }


        public <E extends Event> Builder<T> onEvent(Class<E> clazz, BiConsumer<E, SkillData<T>> consumer) {
            listeners.put(clazz, consumer);
            return this;
        }

        public Builder<T> reachReady(Consumer<SkillData<T>> consumer) {
            this.reachReady = consumer;
            return this;
        }

        public Builder<T> reachStop(Consumer<SkillData<T>> consumer) {
            this.reachStop = consumer;
            return this;
        }

        public Builder<T> apply(Consumer<Builder<T>> consumer) {
            consumer.accept(this);
            return this;
        }

        public Builder<T> end(Consumer<SkillData<T>> consumer) {
            onEnd = consumer;
            return this;
        }

        public Inactive<T> build() {
            return new Inactive<>(this);
        }
    }
}
