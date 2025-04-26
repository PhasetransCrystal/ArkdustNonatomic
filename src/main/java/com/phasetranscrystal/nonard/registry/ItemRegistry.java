package com.phasetranscrystal.nonard.registry;

import com.phasetranscrystal.nonard.ArkdustNonatomic;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, ArkdustNonatomic.MODID);

}
