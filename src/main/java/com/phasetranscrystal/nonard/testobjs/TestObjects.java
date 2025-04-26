package com.phasetranscrystal.nonard.testobjs;

import com.phasetranscrystal.nonard.ArkdustNonatomic;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestObjects {
    public static void bootstrap(IEventBus bus){
        ITEMS.register(bus);
        SkillTest.bootstrap(bus);
    }


    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, ArkdustNonatomic.MODID);

    public static final DeferredHolder<Item,ParticlesTest.Emitter> EMITTER = ITEMS.register("emitter", ParticlesTest.Emitter::new);
}
