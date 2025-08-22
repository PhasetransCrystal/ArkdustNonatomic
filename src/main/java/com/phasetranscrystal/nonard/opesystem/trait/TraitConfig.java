package com.phasetranscrystal.nonard.opesystem.trait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record TraitConfig(ResourceLocation id, TraitType type) {
    public static final Codec<TraitConfig> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(TraitConfig::id),
            TraitType.CODEC.fieldOf("type").forGetter(TraitConfig::type)
        ).apply(instance, TraitConfig::new)
    );

    public String getTranslationKey() {
        return "arkdust.trait"+ '.' + id.getNamespace() + '.' + id.getPath();
    }
}