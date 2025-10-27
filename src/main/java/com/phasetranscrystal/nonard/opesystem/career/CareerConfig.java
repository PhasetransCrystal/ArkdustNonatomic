package com.phasetranscrystal.nonard.opesystem.career;

import net.minecraft.resources.ResourceLocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record CareerConfig(ResourceLocation id, CareerType type, List<ResourceLocation> availableTraits) {

    public static final Codec<CareerConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(CareerConfig::id),
                    CareerType.CODEC.fieldOf("type").forGetter(CareerConfig::type),
                    ResourceLocation.CODEC.listOf().fieldOf("availableTraits").forGetter(CareerConfig::availableTraits)).apply(instance, CareerConfig::new));

    public String getTranslationKey() {
        return "arkdust.operator.career." + type.name() + '.' + id.getNamespace() + '.' + id.getPath();
    }
}
