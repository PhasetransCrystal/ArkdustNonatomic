package com.phasetranscrystal.nonard.migrate.ingame_obj;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.phasetranscrystal.nonard.ArkdustNonatomic;
import com.phasetranscrystal.nonard.migrate.ingame_obj.supplier.IGOSupplier;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

public interface IGOExtractor<T> {
    ResourceLocation NAME = ArkdustNonatomic.location("igm_extractor");

    Codec<IGOExtractor<T>> codec();

    Class<T> targetClass();

    Predicate<T> targetPredicate();

    ToDoubleFunction<T> getCountFromTarget();

    default double getCountFromTarget(T obj) {
        return getCountFromTarget().applyAsDouble(obj);
    }

    default boolean isTargetNotEmpty(T obj) {
        return getCountFromTarget(obj) > 0;
    }

    double requestCount();

    boolean canExtractFrom(T root);

    Consumer<IGOSupplier<T>> createExtractionExecutor(T root);

    /**
     * 从资源供应器中提取对象
     *
     * @param supplier 资源的供应器
     * @param greedy   是否为贪婪提取，可能对例如原版桶之类的有数量分层限制的对象有用
     */
    default ExtractResultPreview<T> extract(IGOSupplier<T> supplier, boolean greedy) {
        double count = requestCount(), originCount = count;
        ImmutableMap.Builder<Integer, T> map = new ImmutableMap.Builder<>();

        for (int index = 0; index < supplier.size(); index++) {
            if (!supplier.isVariable(index) || !canExtractFrom(supplier.get(index))) continue;

            Optional<T> value = supplier.extractCount(index, count, greedy);
            if (value.isEmpty()) continue;

            double consumed = getCountFromTarget(value.get());
            if (consumed <= 0) continue;

            count -= consumed;
            map.put(index, value.get());

            if (count <= 0) break;
        }

        //TODO
    }

    default Consumer<IGOSupplier<T>> createExecutor(ImmutableMap<Integer, T> map) {
        return sup -> map.forEach((index, object) -> {

        });
    }

    /**
     * 从提供的资源供应器中提取所需要的内容
     *
     * @param simulate 是否为模拟，如果为true则只计数，不更改供应器内数据
     */
    default double extractFrom(boolean simulate, IGOSupplier<?>... suppliers) {
        List<IGOSupplier<T>> list = new ArrayList<>(suppliers.length);
        ImmutableMap<Class, IGOSupplier.Dispatcher> map = IGObjectsController.IGM_DISPATCHER_MAP.get().getOrDefault(targetClass(), ImmutableMap.of());
        for (IGOSupplier<?> sup : suppliers) {
            if (targetClass().isAssignableFrom(sup.targetClass())) {
                list.add((IGOSupplier<T>) sup);
            } else if (map.containsKey(sup.targetClass())) {
                list.add(map.get(sup.targetClass()).transform(sup));
            }
        }
        return extractedFromDispatched(simulate, list);
    }

    default double extractedFromDispatched(boolean simulate, List<IGOSupplier<T>> suppliers) {
        double remain = 0;
        for (IGOSupplier<T> supplier : suppliers) {
            remain -= extractForm(simulate, remain, supplier);
            if (remain <= 0) {
                return requestCount();
            }
        }
        return remain;
    }

    ;

    default double extractedFromDispatched(boolean simulate, IGOSupplier<T>... suppliers) {
        return extractedFromDispatched(simulate, Arrays.asList(suppliers));
    }
}
