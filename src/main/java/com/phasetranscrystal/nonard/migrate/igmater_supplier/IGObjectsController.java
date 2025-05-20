package com.phasetranscrystal.nonard.migrate.igmater_supplier;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.phasetranscrystal.nonard.migrate.Registries;

import java.util.HashMap;
import java.util.Map;

public class IGObjectsController {
    //Map<toClass,Map<fromClass,dispatcher>>
    @SuppressWarnings("all")
    public static final Supplier<ImmutableMap<Class, ImmutableMap<Class, IGObjectsSupplier.Dispatcher>>> IGM_DISPATCHER_MAP =
            Suppliers.memoize(() -> {
                Map<Class,Map<Class, IGObjectsSupplier.Dispatcher>> map = new HashMap<>();
                for (IGObjectsSupplier.Dispatcher<?, ?> dispatcher : Registries.IGM_SUPPLIER_DISPATCHER) {
                    map.putIfAbsent(dispatcher.resultTargetClass(),new HashMap<>()).put(dispatcher.resultTargetClass(),dispatcher);
                }
                ImmutableMap.Builder<Class,ImmutableMap<Class, IGObjectsSupplier.Dispatcher>> builder = ImmutableMap.builder();
                map.forEach((c, m) -> builder.put(c,ImmutableMap.copyOf(m)));
                return builder.build();
            });
}
