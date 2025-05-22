package com.phasetranscrystal.nonard.migrate.ingame_obj.supplier;

import com.phasetranscrystal.nonard.ArkdustNonatomic;
import com.phasetranscrystal.nonard.migrate.ingame_obj.ExtractResultPreview;
import com.phasetranscrystal.nonard.migrate.ingame_obj.IGOExtractor;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public interface IGOSupplier<T> {
    ResourceLocation NAME = ArkdustNonatomic.location("igm_supplier");

    Class<T> targetClass();

    int size();

    IGOSupplier<T> createSnapshot();

    T get(int index);

    boolean set(int index, T value);

    boolean setCount(int index, double count);

    //return: object remain that can't add in. empty means no remained.
    Optional<T> add(int index, T value);

    double addCount(int index, double count);

    //return: extracted object. empty means nothing extracted.
    Optional<T> extractCount(int index, double count, boolean greedy);


    boolean isVariable();

    default boolean isVariable(int index) {
        return isVariable();
    }


    default boolean isSnapshot() {
        return false;
    }

    default boolean isSnapshotOf(IGOSupplier<T> supplier) {
        return false;
    }


    boolean checkAvailability(ExtractResultPreview<T> resultPreview);

    void bootstrapResultPreview(ExtractResultPreview<T> resultPreview);

    void addChangeFeedback(IGOSupplier<T> supplier);

    void boostrapChange();

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
