package com.phasetranscrystal.nonard.migrate.ingame_obj_se.supplier;

import javax.annotation.Nonnull;
import java.util.List;

public interface IMultiIGOS {
    default boolean isStable() {
        return false;
    }

    @Nonnull
    List<IGOSupplier<?>> getSuppliers();

    @Nonnull
    default <T> List<IGOSupplier<T>> getSuppliers(@Nonnull Class<T> clazz) {
        return getSuppliers().stream().filter(sup -> sup.targetClass() == clazz).map(sup -> (IGOSupplier<T>) sup).toList();
    }

    boolean canAddSupplier(IGOSupplier<?> supplier);

    boolean canRemoveSupplier(IGOSupplier<?> supplier);

    boolean addSupplier(IGOSupplier<?> supplier);

    boolean removeSupplier(IGOSupplier<?> supplier);

    boolean containsSupplier(IGOSupplier<?> supplier);

}
