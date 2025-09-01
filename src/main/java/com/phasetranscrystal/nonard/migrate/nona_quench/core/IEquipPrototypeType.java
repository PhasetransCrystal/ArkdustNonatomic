package com.phasetranscrystal.nonard.migrate.nona_quench.core;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IEquipPrototypeType<T extends Item & IEquipItem<T>> extends IInfluenceChildProvider, IEquipItem<T>{
    //获取修复材料

    //获取部件加工特性

    //获取perk池

    //创建物品

    ItemStack createStack(RandomSource random);//也许还要加一个context之类的

}
