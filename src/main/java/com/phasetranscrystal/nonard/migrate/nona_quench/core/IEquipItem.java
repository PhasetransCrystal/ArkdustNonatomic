package com.phasetranscrystal.nonard.migrate.nona_quench.core;

import com.phasetranscrystal.nonard.migrate.nona_quench.meta.InfluencePack;
import net.minecraft.world.item.Item;

public interface IEquipItem<T extends Item & IEquipItem<T>> {
    IEquipFrame<T> getFrame();

    T get();

    //获取各部件(子类)

    //获取最终影响包
    InfluencePack getInfluence();

    //获取各阶段的对象：驱动体 原型 成型器(子类)

    //获取模组信息(子类)

    //获取其它固件信息(子类)
}
