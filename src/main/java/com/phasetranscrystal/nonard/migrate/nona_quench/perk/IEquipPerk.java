package com.phasetranscrystal.nonard.migrate.nona_quench.perk;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import com.phasetranscrystal.nonard.migrate.nona_quench.NewRegistries;
import com.phasetranscrystal.nonard.migrate.nona_quench.core.IEquipFrame;
import com.phasetranscrystal.nonard.migrate.nona_quench.core.IEquipItem;
import com.phasetranscrystal.nonard.migrate.nona_quench.meta.InfluencePack;

public interface IEquipPerk {

    default ResourceLocation getId() {
        return NewRegistries.PERKS.getKey(this);
    }

    <T extends Item & IEquipItem<T>> boolean canUseOn(IEquipFrame<T> frame, IEquipItem<T> prototypeType);

    Component createExplain(boolean detailed, int factor);

    default String getNameTransKey() {
        return getId().toLanguageKey("brea.quench.perk", "name");
    }

    default ResourceLocation getIconPath() {
        return getId().withPrefix("brea/quench/perk/");
    }

    // maybe u should create a cache? 也许你应该做点缓存?
    InfluencePack.Child createEffect(int factor);

    class Default implements IEquipPerk {

        @Override
        public <T extends Item & IEquipItem<T>> boolean canUseOn(IEquipFrame<T> frame, IEquipItem<T> prototypeType) {
            return true;
        }

        @Override
        public Component createExplain(boolean detailed, int factor) {
            return Component.translatable(getNameTransKey());
        }

        @Override
        public InfluencePack.Child createEffect(int factor) {
            return InfluencePack.Child.EMPTY;
        }
    }
}
