package com.phasetranscrystal.nonard.migrate;

import com.phasetranscrystal.blast.skill.Skill;
import com.phasetranscrystal.nonard.ArkdustNonatomic;
import com.phasetranscrystal.nonard.migrate.igmater_supplier.IGMaterialExtractor;
import com.phasetranscrystal.nonard.migrate.igmater_supplier.IGMaterialSupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = ArkdustNonatomic.MODID)
public class Registries {
    public static final Registry<IGMaterialSupplier.Dispatcher<?,?>> IGM_SUPPLIER_DISPATCHER = new RegistryBuilder<>(Keys.IGM_SUPPLIER_DISPATCHER).create();
    public static final Registry<IGMaterialExtractor<?>> IGM_SUPPLIER_EXTRACTOR = new RegistryBuilder<>(Keys.IGM_SUPPLIER_EXTRACTOR).create();

    public static class Keys {
        public static final ResourceKey<Registry<IGMaterialSupplier.Dispatcher<?,?>>> IGM_SUPPLIER_DISPATCHER = ResourceKey.createRegistryKey(IGMaterialSupplier.Dispatcher.NAME);
        public static final ResourceKey<Registry<IGMaterialExtractor<?>>> IGM_SUPPLIER_EXTRACTOR = ResourceKey.createRegistryKey(IGMaterialExtractor.NAME);
    }

    @SubscribeEvent
    public static void regis(NewRegistryEvent event){
        event.register(IGM_SUPPLIER_DISPATCHER);
        event.register(IGM_SUPPLIER_EXTRACTOR);
    }
}
