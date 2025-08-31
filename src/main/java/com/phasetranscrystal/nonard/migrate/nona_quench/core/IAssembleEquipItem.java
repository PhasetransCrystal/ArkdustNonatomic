package com.phasetranscrystal.nonard.migrate.nona_quench.core;

import net.minecraft.world.item.Item;

public interface IAssembleEquipItem<T extends Item & IAssembleEquipItem<T>> {
    IEquipFrame<T> getFrame();

    T get();

    //获取各部件

    //获取最终影响包

    //获取各阶段的对象：驱动体 原型 成型器

    //获取模组信息

    //获取其它固件信息
}
