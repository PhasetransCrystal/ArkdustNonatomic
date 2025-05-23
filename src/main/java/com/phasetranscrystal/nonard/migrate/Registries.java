package com.phasetranscrystal.nonard.migrate;

import com.phasetranscrystal.nonard.ArkdustNonatomic;
import com.phasetranscrystal.nonard.migrate.ingame_obj_se.extractor.IGOExtractor;
import com.phasetranscrystal.nonard.migrate.ingame_obj_se.supplier.IGOSupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = ArkdustNonatomic.MODID)
public class Registries {
    public static final Registry<IGOSupplier.Dispatcher<?,?>> IGM_SUPPLIER_DISPATCHER = new RegistryBuilder<>(Keys.IGM_SUPPLIER_DISPATCHER).create();
    public static final Registry<IGOExtractor<?>> IGM_SUPPLIER_EXTRACTOR = new RegistryBuilder<>(Keys.IGM_SUPPLIER_EXTRACTOR).create();

    public static class Keys {
        public static final ResourceKey<Registry<IGOSupplier.Dispatcher<?,?>>> IGM_SUPPLIER_DISPATCHER = ResourceKey.createRegistryKey(IGOSupplier.Dispatcher.NAME);
        public static final ResourceKey<Registry<IGOExtractor<?>>> IGM_SUPPLIER_EXTRACTOR = ResourceKey.createRegistryKey(IGOExtractor.NAME);
    }

    @SubscribeEvent
    public static void regis(NewRegistryEvent event){
        event.register(IGM_SUPPLIER_DISPATCHER);
        event.register(IGM_SUPPLIER_EXTRACTOR);
    }
}
