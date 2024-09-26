package com.phasetranscrystal.nonard.registry;

import com.phasetranscrystal.nonard.Nonard;
import com.phasetranscrystal.nonard.network.C2SKeyInputPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Nonard.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class PayloadRegistry {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                C2SKeyInputPacket.TYPE,
                C2SKeyInputPacket.STREAM_CODEC,
                C2SKeyInputPacket::serverHandler
        );
    }
}
