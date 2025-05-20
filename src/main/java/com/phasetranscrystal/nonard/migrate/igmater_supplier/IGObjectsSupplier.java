package com.phasetranscrystal.nonard.migrate.igmater_supplier;

import com.phasetranscrystal.nonard.ArkdustNonatomic;
import net.minecraft.resources.ResourceLocation;

public interface IGObjectsSupplier<T> {
    ResourceLocation NAME = ArkdustNonatomic.location("igm_supplier");

    Class<T> targetClass();

    int size();

    IGObjectsSupplier<T> createSnapshot();

    T get(int index);

    boolean set(int index, T value);

    boolean isVariable();

    default boolean isVariable(int index) {
        return isVariable();
    }

    boolean isSnapshot();

    interface Converter<F, T> {
        ResourceLocation NAME = ArkdustNonatomic.location("igm_supplier_dispatcher");

        Class<F> originalTargetClass();

        Class<T> resultTargetClass();

        IGObjectsSupplier<T> transform(IGObjectsSupplier<F> obj);
    }

//    // 组供应器
//    public record SupplierGroup(List<IGOSupplier<?>> suppliers) {
//        public <T> List<IGOSupplier<T>> findSuppliers(Class<T> type) {
//            return suppliers.stream()
//                    .flatMap(s -> ConversionRegistry.findConversion(s, type).stream())
//                    .collect(Collectors.toList());
//        }
//    }
}
