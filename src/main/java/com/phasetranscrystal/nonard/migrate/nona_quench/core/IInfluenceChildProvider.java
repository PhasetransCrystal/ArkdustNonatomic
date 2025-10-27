package com.phasetranscrystal.nonard.migrate.nona_quench.core;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import com.phasetranscrystal.nonard.migrate.nona_quench.meta.InfluencePack;

public interface IInfluenceChildProvider {

    InfluencePack.Child getInfluenceChild(ItemStack stack, LivingEntity entity);
}
