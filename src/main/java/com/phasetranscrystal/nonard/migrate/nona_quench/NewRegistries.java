package com.phasetranscrystal.nonard.migrate.nona_quench;

import com.phasetranscrystal.nonard.migrate.nona_quench.perk.IEquipPerk;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class NewRegistries {
    public static final Registry<IEquipPerk> PERKS = new RegistryBuilder<>(Keys.PERKS).sync(true).create();

    public static class Keys{
        public static final ResourceKey<Registry<IEquipPerk>> PERKS =
                ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(BreaQuench.MODID,"perks"));
    }
}
