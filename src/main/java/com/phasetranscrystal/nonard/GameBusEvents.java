package com.phasetranscrystal.nonard;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = ArkdustNonatomic.MODID)
public class GameBusEvents { //TODO
    @SubscribeEvent
    public static void skillInit(EntityJoinLevelEvent event) {

    }
}
