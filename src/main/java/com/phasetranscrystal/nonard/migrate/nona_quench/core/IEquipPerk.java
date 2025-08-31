package com.phasetranscrystal.nonard.migrate.nona_quench.core;

import com.phasetranscrystal.nonard.migrate.nona_quench.Registries;
import com.phasetranscrystal.nonard.migrate.nona_quench.meta.InfluencePack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface IEquipPerk {

    default ResourceLocation getId(){
        return Registries.PERKS.getKey(this);
    }

    boolean canUseOn(IEquipFrame<?> frame);//参数还要加一个原型

    Component createExplain(boolean detailed, int factor);

    default String getNameTransKey(){
        return getId().toLanguageKey("brea.quench.perk","name");
    }

    default ResourceLocation getIconPath(){
        return getId().withPrefix("brea/quench/perk/");
    }

    //maybe u should create a cache? 也许你应该做点缓存?
    InfluencePack.Child createEffect(int factor);

}
