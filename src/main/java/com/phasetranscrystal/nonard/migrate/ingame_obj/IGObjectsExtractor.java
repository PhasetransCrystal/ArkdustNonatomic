package com.phasetranscrystal.nonard.migrate.ingame_obj;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.phasetranscrystal.nonard.ArkdustNonatomic;
import com.phasetranscrystal.nonard.migrate.ingame_obj.supplier.IGOSupplier;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IGObjectsExtractor<T> {
    ResourceLocation NAME = ArkdustNonatomic.location("igm_extractor");

    Codec<IGObjectsExtractor<T>> codec();

    Class<T> targetClass();

    Predicate<T> extractTargetPredicate();

    double requestCount();

    ExtractResultPreview<T> extractForm(double countLimit, IGOSupplier<T> extractor);

    double canExtractFrom(T root);

    Consumer<IGOSupplier<T>> createExtractionExecutor(T root);


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
