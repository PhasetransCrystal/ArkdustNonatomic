package com.phasetranscrystal.nonard.eventdistribute;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;

import java.util.*;
import java.util.function.Consumer;


/**
 * 实体事件分发器，用于管理和分发事件给相应的监听器。
 */
public class EntityEventDistribute {

    //WARN 不要直接修改这些 请使用下面的add与remove
    public final Multimap<Class<? extends Event>, Consumer<? extends Event>> listeners = HashMultimap.create();
    public final MarkedTreeElement markedListeners = new MarkedTreeElement();
    public final HashMap<Class<? extends Event>, Integer> eventHashCache = new HashMap<>();

    /**
     * 添加带标记的事件监听器。
     *
     * @param event    事件的类
     * @param listener 监听器
     * @param flag     标记
     * @param <T>      事件类型
     * @return 是否成功添加
     */
    public <T extends Event> boolean add(Class<T> event, Consumer<T> listener, ResourceLocation... flag) {
        markedListeners.add(new IdentEvent<>(event, listener), flag);
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
        boolean removedFromMarked = markedListeners.removeIncludeChild(new IdentEvent<>(event, listener));
        return removedFromListeners || removedFromMarked;
    }

    /**
     * 移除所有带指定标记的监听器。
     *
     * @param flag 标记
     */
    public boolean removeMarked(ResourceLocation... flag) {
        if(!markedListeners.contains(flag)) return false;
        markedListeners.remove(flag).forEach(i -> listeners.remove(i.event(),i.listener()));
        return true;
    }

    public boolean removeMarkedEndPoint(ResourceLocation... flag) {
        if(!markedListeners.contains(flag)) return false;
        markedListeners.removeEndPoint(flag).forEach(i -> listeners.remove(i.event(),i.listener()));
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
            listeners.removeAll(event);
            markedListeners.removeIncludeChild(event);
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
        List.copyOf(listeners.get(event.getClass())).forEach(consumer -> ((Consumer<T>) consumer).accept(event));
        eventHashCache.put(event.getClass(), event.hashCode());
    }

}

