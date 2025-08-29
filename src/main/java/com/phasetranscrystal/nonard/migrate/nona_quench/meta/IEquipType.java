package com.phasetranscrystal.nonard.migrate.nona_quench.meta;

import com.google.common.collect.Table;
import com.phasetranscrystal.nonard.migrate.nona_quench.AssembleWeaponType;
import net.minecraft.world.item.Item;

import java.util.List;

public interface IEquipType<I extends Item> {//TODO basic equip item
    int blueprintX();

    int blueprintY();

    Table<Integer, Integer, AssembleWeaponType.PointChain> blueprintParts();

    List<AssembleWeaponType.PointChain> getParts();


}
