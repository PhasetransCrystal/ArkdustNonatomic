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

public class Active<T extends LivingEntity> {
    public static final Active<?> EMPTY = Builder.create().build();

    public final Consumer<SkillData<T>> onStart;
    public final Consumer<SkillData<T>> onEnd;
    public final Consumer<SkillData<T>> reachStop;
    public final ImmutableMap<Class<? extends Event>, BiConsumer<? extends Event, SkillData<T>>> listeners;

    public Active(Builder<T> builder) {
        this.onStart = builder.onStart;
        this.onEnd = builder.onEnd;
        this.reachStop = builder.reachStop;
        this.listeners = ImmutableMap.copyOf(builder.listeners);
    }

    public static class Builder<T extends LivingEntity> {
        public final Consumer<SkillData<T>> NO_ACTION = data -> {
        };

        protected Consumer<SkillData<T>> onStart = NO_ACTION;
        protected Consumer<SkillData<T>> onEnd = NO_ACTION;
        protected Consumer<SkillData<T>> reachStop = NO_ACTION;
        protected HashMap<Class<? extends Event>, BiConsumer<? extends Event, SkillData<T>>> listeners = new HashMap<>();

        public static <T extends LivingEntity> Builder<T> create() {
            return new Builder<>();
        }

        public Builder<T> start(Consumer<SkillData<T>> consumer) {
            onStart = consumer;
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

        public Builder<T> onAttack(BiConsumer<EventConsumer.EntityAttackEvent.Post, SkillData<T>> consumer){
            listeners.put(EventConsumer.EntityAttackEvent.Post.class, consumer);
            return this;
        }

        public Builder<T> onKill(BiConsumer<EventConsumer.EntityKillEvent.Post, SkillData<T>> consumer){
            listeners.put(EventConsumer.EntityKillEvent.Post.class, consumer);
            return this;
        }

        public <E extends Event> Builder<T> onEvent(Class<E> clazz, BiConsumer<E, SkillData<T>> consumer) {
            listeners.put(clazz, consumer);
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

        public Active<T> build() {
            return new Active<>(this);
        }
    }
}
