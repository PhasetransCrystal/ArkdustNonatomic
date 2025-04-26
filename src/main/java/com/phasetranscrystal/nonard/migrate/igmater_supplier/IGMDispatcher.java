package com.phasetranscrystal.nonard.migrate.igmater_supplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public record IGMDispatcher<F, T>(Class<F> fromClass, Class<T> toClass,
                                  Function<IGMaterialSupplier<F>, IGMaterialSupplier<T>> transformer) implements IGMaterialSupplier.Dispatcher<F, T> {
    @Override
    public Class<F> originalTargetClass() {
        return fromClass;
    }

    @Override
    public Class<T> resultTargetClass() {
        return toClass;
    }

    @Override
    public IGMaterialSupplier<T> transform(IGMaterialSupplier<F> obj) {
        return transformer.apply(obj);
    }

    public List<IGMaterialSupplier<T>> transformAll(List<IGMaterialSupplier<?>> objs) {
        if (objs == null || objs.isEmpty()) {
            return List.of();
        }
        List<IGMaterialSupplier<T>> result = new ArrayList<>();
        objs.stream().filter(c -> c.targetClass().equals(fromClass))
                .map(f -> transformer.apply((IGMaterialSupplier<F>) f))
                .filter(Objects::nonNull)
                .forEach(result::add);
        return result;
    }
}
