package com.phasetranscrystal.nonard.migrate.nona_quench.registry;

import com.phasetranscrystal.nonard.migrate.nona_quench.BreaQuench;
import com.phasetranscrystal.nonard.migrate.nona_quench.NewRegistries;
import com.phasetranscrystal.nonard.migrate.nona_quench.perk.IEquipPerk;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BreaQuenchRegistries {
    public static final DeferredRegister<IEquipPerk> PERK_REGISTER = DeferredRegister.create(NewRegistries.PERKS, BreaQuench.MODID);

    public static final DeferredHolder<IEquipPerk,IEquipPerk.Default> DEFAULT =
            PERK_REGISTER.register("default",IEquipPerk.Default::new);
}
