package com.phasetranscrystal.nonard.migrate.nona_quench;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = BreaQuench.MODID)
public class ModBusEventConsumer {

    @SubscribeEvent
    public static void newRegistryEvent(NewRegistryEvent event){
        event.register(NewRegistries.PERKS);
    }


}
