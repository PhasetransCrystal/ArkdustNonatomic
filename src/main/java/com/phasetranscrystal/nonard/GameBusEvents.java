package com.phasetranscrystal.nonard;

import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = Nonard.MOD_ID)
public class GameBusEvents { //TODO
    @SubscribeEvent
    public static void skillInit(EntityJoinLevelEvent event) {

    }
}
