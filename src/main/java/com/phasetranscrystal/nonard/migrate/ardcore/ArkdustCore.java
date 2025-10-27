package com.phasetranscrystal.nonard.migrate.ardcore;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;

import com.phasetranscrystal.nonard.ArkdustNonatomic;
import com.phasetranscrystal.nonard.migrate.ardcore.econ.Account;

public class ArkdustCore {

    // public static final String MODID = "arkdust_core";
    public static final String MODID = ArkdustNonatomic.MODID;

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static void bootstrap(IEventBus bus) {
        Account.DataAttachment.REGISTER.register(bus);
    }
}
