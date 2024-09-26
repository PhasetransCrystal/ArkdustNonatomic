package com.phasetranscrystal.nonard.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;

public class ModEventHook {
    public static void onKeyInput(ServerPlayer player, int key, int modifiers, int action) {
        NeoForge.EVENT_BUS.post(new KeyInputEvent(player, key, action, modifiers));
    }
}
