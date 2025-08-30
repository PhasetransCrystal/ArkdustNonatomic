package com.phasetranscrystal.nonard.migrate.nona_quench.core;

import net.minecraft.world.item.Item;

public interface IAssembleItem<T extends Item & IAssembleItem<T>> {
    IEquipFrame<T> getFrame();

}
