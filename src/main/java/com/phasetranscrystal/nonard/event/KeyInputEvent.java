package com.phasetranscrystal.nonard.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.EntityEvent;

public class KeyInputEvent extends EntityEvent {
    private final ServerPlayer player;
    private final int key;
    private final int action;
    private final int modifiers;

    public KeyInputEvent(ServerPlayer player, int key, int modifiers, int action) {
        super(player);
        this.player = player;
        this.key = key;
        this.action = action;
        this.modifiers = modifiers;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public int getKey() {
        return key;
    }

    public int getAction() {
        return action;
    }

    public int getModifiers() {
        return modifiers;
    }
}
