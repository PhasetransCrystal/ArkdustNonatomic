package com.phasetranscrystal.nonard.migrate.igmater_supplier;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.phasetranscrystal.nonard.ArkdustNonatomic;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface IGMaterialExtractor<T> {
    ResourceLocation NAME = ArkdustNonatomic.location("igm_extractor");

    Codec<IGMaterialExtractor<T>> codec();

    Class<T> targetClass();

    double requestCount();

    double extractForm(boolean simulate,double countLimit, IGMaterialSupplier<T> extractor);

    /**
     * 从提供的资源供应器中提取所需要的内容
     *
     * @param simulate 是否为模拟，如果为true则只计数，不更改供应器内数据
     */
    default double extractFrom(boolean simulate, IGMaterialSupplier<?>... suppliers) {
        List<IGMaterialSupplier<T>> list = new ArrayList<>(suppliers.length);
        ImmutableMap<Class, IGMaterialSupplier.Dispatcher> map = IGMaterialController.IGM_DISPATCHER_MAP.get().getOrDefault(targetClass(), ImmutableMap.of());
        for (IGMaterialSupplier<?> sup : suppliers) {
            if (targetClass().isAssignableFrom(sup.targetClass())) {
                list.add((IGMaterialSupplier<T>) sup);
            } else if (map.containsKey(sup.targetClass())) {
                list.add(map.get(sup.targetClass()).transform(sup));
            }
        }
        return extractedFromDispatched(simulate, list);
    }

    default double extractedFromDispatched(boolean simulate, List<IGMaterialSupplier<T>> suppliers){
        double remain = 0;
        for (IGMaterialSupplier<T> supplier : suppliers) {
            remain -= extractForm(simulate, remain, supplier);
            if (remain <= 0) {
                return requestCount();
            }
        }
        return remain;
    };

    default double extractedFromDispatched(boolean simulate, IGMaterialSupplier<T>... suppliers) {
        return extractedFromDispatched(simulate, Arrays.asList(suppliers));
    }

    ;
}
