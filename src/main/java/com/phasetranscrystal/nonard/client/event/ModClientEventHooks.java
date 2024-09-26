package com.phasetranscrystal.nonard.client.event;

import net.neoforged.neoforge.common.NeoForge;

public class ModClientEventHooks {
    public static boolean onPreKeyboardPress(int key, int scanCode, int action, int modifiers) {
        PreKeyInputEvent event = new PreKeyInputEvent(key, scanCode, action, modifiers);
        NeoForge.EVENT_BUS.post(event);
        return event.isCanceled();
    }
}
