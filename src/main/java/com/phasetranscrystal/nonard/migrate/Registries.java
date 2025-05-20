package com.phasetranscrystal.nonard.migrate;

import com.phasetranscrystal.nonard.ArkdustNonatomic;
import com.phasetranscrystal.nonard.migrate.igmater_supplier.IGObjectsExtractor;
import com.phasetranscrystal.nonard.migrate.igmater_supplier.IGObjectsSupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = ArkdustNonatomic.MODID)
public class Registries {
    public static final Registry<IGObjectsSupplier.Dispatcher<?,?>> IGM_SUPPLIER_DISPATCHER = new RegistryBuilder<>(Keys.IGM_SUPPLIER_DISPATCHER).create();
    public static final Registry<IGObjectsExtractor<?>> IGM_SUPPLIER_EXTRACTOR = new RegistryBuilder<>(Keys.IGM_SUPPLIER_EXTRACTOR).create();

    public static class Keys {
        public static final ResourceKey<Registry<IGObjectsSupplier.Dispatcher<?,?>>> IGM_SUPPLIER_DISPATCHER = ResourceKey.createRegistryKey(IGObjectsSupplier.Dispatcher.NAME);
        public static final ResourceKey<Registry<IGObjectsExtractor<?>>> IGM_SUPPLIER_EXTRACTOR = ResourceKey.createRegistryKey(IGObjectsExtractor.NAME);
    }

    @SubscribeEvent
    public static void regis(NewRegistryEvent event){
        event.register(IGM_SUPPLIER_DISPATCHER);
        event.register(IGM_SUPPLIER_EXTRACTOR);
    }
}
