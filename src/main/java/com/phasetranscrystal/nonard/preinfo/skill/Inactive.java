package com.phasetranscrystal.nonard.preinfo.skill;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Inactive<T extends LivingEntity> {
    public static final Inactive<?> EMPTY = Builder.create().build();

    public final Consumer<SkillData<T>> chargeChanged;
    public final Consumer<SkillData<T>> energyChanged;
    public final Consumer<SkillData<T>> onStart;
    public final Consumer<SkillData<T>> onEnd;
    public final Consumer<SkillData<T>> reachReady;
    public final Consumer<SkillData<T>> reachStop;
    public final ImmutableMap<Class<? extends Event>, BiConsumer<? extends Event, SkillData<T>>> listeners;

    public Inactive(Builder<T> builder) {
        this.chargeChanged = builder.chargeChanged;
        this.energyChanged = builder.energyChanged;
        this.onStart = builder.onStart;
        this.onEnd = builder.onEnd;
        this.reachReady = builder.reachReady;
        this.reachStop = builder.reachStop;
        this.listeners = ImmutableMap.copyOf(builder.listeners);
    }
    public static class Builder<T extends LivingEntity> {
        public final Consumer<SkillData<T>> NO_ACTION = data -> {
        };

        protected Consumer<SkillData<T>> chargeChanged = NO_ACTION;
        protected Consumer<SkillData<T>> energyChanged = NO_ACTION;
        protected Consumer<SkillData<T>> onStart = NO_ACTION;
        protected Consumer<SkillData<T>> onEnd = NO_ACTION;
        protected Consumer<SkillData<T>> reachReady = NO_ACTION;
        protected Consumer<SkillData<T>> reachStop = NO_ACTION;
        protected HashMap<Class<? extends Event>, BiConsumer<? extends Event, SkillData<T>>> listeners = new HashMap<>();

        public static <T extends LivingEntity> Builder<T> create() {
            return new Builder<>();
        }

        public Builder<T> start(Consumer<SkillData<T>> consumer) {
            onStart = consumer;
            return this;
        }

        public Builder<T> chargeChanged(Consumer<SkillData<T>> consumer) {
            this.chargeChanged = consumer;
            return this;
        }

        public Builder<T> energyChanged(Consumer<SkillData<T>> consumer) {
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
