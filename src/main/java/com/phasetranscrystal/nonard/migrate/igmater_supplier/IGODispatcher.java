package com.phasetranscrystal.nonard.migrate.igmater_supplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public record IGODispatcher<F, T>(Class<F> fromClass, Class<T> toClass,
                                  Function<IGObjectsSupplier<F>, IGObjectsSupplier<T>> transformer) implements IGObjectsSupplier.Dispatcher<F, T> {
    @Override
    public Class<F> originalTargetClass() {
        return fromClass;
    }

    @Override
    public Class<T> resultTargetClass() {
        return toClass;
    }

    @Override
    public IGObjectsSupplier<T> transform(IGObjectsSupplier<F> obj) {
        return transformer.apply(obj);
    }

    public List<IGObjectsSupplier<T>> transformAll(List<IGObjectsSupplier<?>> objs) {
        if (objs == null || objs.isEmpty()) {
            return List.of();
        }
        List<IGObjectsSupplier<T>> result = new ArrayList<>();
        objs.stream().filter(c -> c.targetClass().equals(fromClass))
                .map(f -> transformer.apply((IGObjectsSupplier<F>) f))
                .filter(Objects::nonNull)
                .forEach(result::add);
        return result;
    }
}
