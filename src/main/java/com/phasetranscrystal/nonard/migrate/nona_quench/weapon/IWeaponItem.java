package com.phasetranscrystal.nonard.migrate.nona_quench.weapon;

import net.minecraft.world.item.Item;

import com.phasetranscrystal.nonard.migrate.nona_quench.core.IEquipItem;
import com.phasetranscrystal.nonard.migrate.nona_quench.damage.DamageSourceContext;

public interface IWeaponItem<T extends Item & IEquipItem<T>> extends IEquipItem<T> {

    DamageSourceContext getDamageContext();

    // 对于攻击的具体表现等内容的覆写应由类实现
    // 见：Minecraft.java:1680
}
