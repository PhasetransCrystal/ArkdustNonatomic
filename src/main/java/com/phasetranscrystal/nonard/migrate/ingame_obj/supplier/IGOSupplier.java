package com.phasetranscrystal.nonard.migrate.ingame_obj.supplier;

import com.phasetranscrystal.nonard.ArkdustNonatomic;
import com.phasetranscrystal.nonard.migrate.ingame_obj.ExtractResultPreview;
import net.minecraft.resources.ResourceLocation;

public interface IGOSupplier<T> {
    ResourceLocation NAME = ArkdustNonatomic.location("igm_supplier");

    Class<T> targetClass();

    int size();

    IGOSupplier<T> createSnapshot();

    T get(int index);

    boolean set(int index, T value);

    boolean isVariable();

    default boolean isVariable(int index) {
        return isVariable();
    }

    boolean isSnapshot();

    void bindExtractResultPreview(ExtractResultPreview<T> resultPreview);

    interface Converter<F, T> {
        ResourceLocation NAME = ArkdustNonatomic.location("igm_supplier_dispatcher");

        Class<F> originalTargetClass();

        Class<T> resultTargetClass();

        IGOSupplier<T> transform(IGOSupplier<F> obj);
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
