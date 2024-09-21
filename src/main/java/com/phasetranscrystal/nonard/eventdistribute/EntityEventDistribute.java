package com.phasetranscrystal.nonard.eventdistribute;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;

import java.util.HashMap;
import java.util.function.Consumer;


/**
 * 实体事件分发器，用于管理和分发事件给相应的监听器。
 */
public class EntityEventDistribute {
    private Entity entity;

    /**
     * 初始化实体事件分发器。
     *
     * @param entity 绑定的生物实体
     */
    public void init(Entity entity) {
        this.entity = entity;
    }

    /**
     * 获取绑定的生物实体。
     *
     * @return 绑定的生物实体
     */
    public Entity getBinding() {
        return entity;
    }

    public final Multimap<Class<? extends Event>, Consumer<? extends Event>> listeners = HashMultimap.create();
    public final Multimap<ResourceLocation, Pair<Class<? extends Event>, Consumer<? extends Event>>> markedListeners = HashMultimap.create();
    public final HashMap<Class<? extends Event>, Integer> eventHashCache = new HashMap<>();

    /**
     * 添加事件监听器。
     *
     * @param event    事件的类
     * @param listener 监听器
     * @param <T>      事件类型
     * @return 是否成功添加
     */
    public <T extends Event> boolean add(Class<T> event, Consumer<T> listener) {
        return listeners.put(event, listener);
    }

    /**
     * 添加带标记的事件监听器。
     *
     * @param event    事件的类
     * @param listener 监听器
     * @param flag     标记
     * @param <T>      事件类型
     * @return 是否成功添加
     */
    public <T extends Event> boolean add(Class<T> event, Consumer<T> listener, ResourceLocation flag) {
        markedListeners.put(flag, Pair.of(event, listener));
        return listeners.put(event, listener);
    }

    /**
     * 移除指定的事件监听器。
     *
     * @param event    事件的类
     * @param listener 监听器
     * @param <T>      事件类型
     * @return 是否成功移除
     */
    public <T extends Event> boolean remove(Class<T> event, Consumer<T> listener) {
        boolean removedFromListeners = listeners.remove(event, listener);
        boolean removedFromMarked = markedListeners.values().removeIf(pair -> pair.getFirst().equals(event) && pair.getSecond().equals(listener));
        return removedFromListeners || removedFromMarked;
    }

    /**
     * 移除所有带指定标记的监听器。
     *
     * @param flag 标记
     * @return 是否成功移除
     */
    public boolean removeMarked(ResourceLocation flag) {
        if (!markedListeners.containsKey(flag)) return false;
        for (Pair<Class<? extends Event>, Consumer<? extends Event>> pair : markedListeners.removeAll(flag)) {
            listeners.remove(pair.getFirst(), pair.getSecond());
        }
        return true;
    }

    /**
     * 移除指定事件类的所有监听器。
     *
     * @param event 事件的类
     * @param <T>   事件类型
     * @return 是否成功移除
     */
    public <T extends Event> boolean removeByEventClass(Class<T> event) {
        if (listeners.containsKey(event)) {
            listeners.removeAll(event).forEach(listener ->
                    markedListeners.values().removeIf(pair -> pair.getFirst().equals(event) && pair.getSecond().equals(listener))
            );
            return true;
        }
        return false;
    }

    /**
     * 消费并处理事件。
     *
     * @param event 要处理的事件
     * @param <T>   事件类型
     */
    public <T extends Event> void post(T event) {
        if (!listeners.containsKey(event.getClass()) ||
                (eventHashCache.containsKey(event.getClass()) && eventHashCache.get(event.getClass()).equals(event.hashCode()))
        ) return;
        listeners.get(event.getClass()).forEach(consumer -> ((Consumer<T>) consumer).accept(event));
        eventHashCache.put(event.getClass(), event.hashCode());
    }
}

