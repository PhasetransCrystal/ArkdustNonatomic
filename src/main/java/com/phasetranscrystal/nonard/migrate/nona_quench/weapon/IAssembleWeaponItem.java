package com.phasetranscrystal.nonard.migrate.nona_quench.weapon;

import com.phasetranscrystal.nonard.migrate.nona_quench.core.IAssembleEquipItem;
import com.phasetranscrystal.nonard.migrate.nona_quench.damage.DamageSourceContext;
import net.minecraft.world.item.Item;

public interface IAssembleWeaponItem<T extends Item & IAssembleEquipItem<T>> extends IAssembleEquipItem<T> {

    DamageSourceContext getDamageContext();

    //对于攻击的具体表现等内容的覆写应由类实现
    //见：Minecraft.java:1680
}
