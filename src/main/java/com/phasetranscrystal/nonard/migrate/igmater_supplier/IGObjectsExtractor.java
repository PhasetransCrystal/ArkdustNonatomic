package com.phasetranscrystal.nonard.migrate.igmater_supplier;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.phasetranscrystal.nonard.ArkdustNonatomic;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface IGObjectsExtractor<T> {
    ResourceLocation NAME = ArkdustNonatomic.location("igm_extractor");

    Codec<IGObjectsExtractor<T>> codec();

    Class<T> targetClass();

    double requestCount();

    double extractForm(boolean simulate,double countLimit, IGObjectsSupplier<T> extractor);

    /**
     * 从提供的资源供应器中提取所需要的内容
     *
     * @param simulate 是否为模拟，如果为true则只计数，不更改供应器内数据
     */
    default double extractFrom(boolean simulate, IGObjectsSupplier<?>... suppliers) {
        List<IGObjectsSupplier<T>> list = new ArrayList<>(suppliers.length);
        ImmutableMap<Class, IGObjectsSupplier.Dispatcher> map = IGObjectsController.IGM_DISPATCHER_MAP.get().getOrDefault(targetClass(), ImmutableMap.of());
        for (IGObjectsSupplier<?> sup : suppliers) {
            if (targetClass().isAssignableFrom(sup.targetClass())) {
                list.add((IGObjectsSupplier<T>) sup);
            } else if (map.containsKey(sup.targetClass())) {
                list.add(map.get(sup.targetClass()).transform(sup));
            }
        }
        return extractedFromDispatched(simulate, list);
    }

    default double extractedFromDispatched(boolean simulate, List<IGObjectsSupplier<T>> suppliers){
        double remain = 0;
        for (IGObjectsSupplier<T> supplier : suppliers) {
            remain -= extractForm(simulate, remain, supplier);
            if (remain <= 0) {
                return requestCount();
            }
        }
        return remain;
    };

    default double extractedFromDispatched(boolean simulate, IGObjectsSupplier<T>... suppliers) {
        return extractedFromDispatched(simulate, Arrays.asList(suppliers));
    }
}
