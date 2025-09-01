package com.phasetranscrystal.nonard.migrate.nona_quench.core;

import com.phasetranscrystal.nonard.migrate.nona_quench.meta.EquipAttribute;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Map;

public interface IEquipFrame<T extends Item & IEquipItem<T>> extends IInfluenceChildProvider {
    IEquipType<? super T> getEquipType();

    default int getModuleGridWidth() {
        return getEquipType().moduleGridWidth();
    }

    default int getModuleGridHeight() {
        return getEquipType().moduleGridHeight();
    }

    int getMaxLoad();

    default Map<ResourceLocation, EquipAttribute> equipAttributes() {
        return getEquipType().equipAttributes();
    }

    T getTemplateEquipment();


}
