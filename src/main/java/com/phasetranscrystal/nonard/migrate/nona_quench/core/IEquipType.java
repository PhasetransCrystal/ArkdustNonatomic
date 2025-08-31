package com.phasetranscrystal.nonard.migrate.nona_quench.core;

import com.google.common.collect.Table;
import com.phasetranscrystal.nonard.migrate.nona_quench.AssembleWeaponType;
import com.phasetranscrystal.nonard.migrate.nona_quench.meta.EquipAttribute;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.*;

public interface IEquipType<I extends Item & IAssembleEquipItem<I>>{//TODO basic equip item

    int blueprintX();

    int blueprintY();

    int moduleGridWidth();

    int moduleGridHeight();

    Map<ResourceLocation, EquipAttribute> equipAttributes();


    Table<Integer, Integer, ResourceLocation> blueprintParts();

    Map<ResourceLocation, AssembleWeaponType.PointChain> getParts();

    boolean slotPosEquipable(ResourceLocation slotFlag);

    IEquipFrame<I> getDefaultFrame();

    ResourceLocation MAIN_HAND = ResourceLocation.fromNamespaceAndPath("minecraft", "main_hand");
    ResourceLocation OFFHAND = ResourceLocation.fromNamespaceAndPath("minecraft", "offhand");
    ResourceLocation HELMET = ResourceLocation.fromNamespaceAndPath("minecraft", "helmet");
    ResourceLocation CHESTPLATE = ResourceLocation.fromNamespaceAndPath("minecraft", "chestplate");
    ResourceLocation LEGGINGS = ResourceLocation.fromNamespaceAndPath("minecraft", "leggings");
    ResourceLocation BOOTS = ResourceLocation.fromNamespaceAndPath("minecraft", "boots");

}
