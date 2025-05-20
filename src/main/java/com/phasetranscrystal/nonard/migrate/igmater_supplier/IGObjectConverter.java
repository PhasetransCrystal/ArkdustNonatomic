package com.phasetranscrystal.nonard.migrate.igmater_supplier;

public interface IGObjectConverter<F, T> {
    Class<F> convertFromClass();

    Class<T> convertToClass();

    IGObjectsSupplier<T> convertFrom(IGObjectsSupplier<F> from);
}
