package com.phasetranscrystal.nonard.migrate.igmater_supplier;

import com.phasetranscrystal.nonard.ArkdustNonatomic;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface IGMaterialSupplier<T> {
    ResourceLocation NAME = ArkdustNonatomic.location("igm_supplier");

    Class<T> targetClass();

    int length();

    List<T> getCopied();

    T get(int index);

    boolean set(int index, T value);

    interface Dispatcher<F, T> {
        ResourceLocation NAME = ArkdustNonatomic.location("igm_supplier_dispatcher");

        Class<F> originalTargetClass();

        Class<T> resultTargetClass();

        IGMaterialSupplier<T> transform(IGMaterialSupplier<F> obj);
    }
}
