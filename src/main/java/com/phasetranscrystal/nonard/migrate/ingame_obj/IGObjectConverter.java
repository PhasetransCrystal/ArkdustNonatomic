package com.phasetranscrystal.nonard.migrate.ingame_obj;

import com.phasetranscrystal.nonard.migrate.ingame_obj.supplier.IGOSupplier;

public interface IGObjectConverter<F, T> {
    Class<F> convertFromClass();

    Class<T> convertToClass();

    IGOSupplier<T> convertFrom(IGOSupplier<F> from);
}
