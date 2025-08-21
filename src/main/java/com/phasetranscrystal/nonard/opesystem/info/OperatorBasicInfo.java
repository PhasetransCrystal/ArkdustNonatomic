package com.phasetranscrystal.nonard.opesystem.info;

import net.minecraft.resources.ResourceLocation;

public class OperatorBasicInfo {
    public final ResourceLocation camp;
    public final ResourceLocation race;
    public final Gender gender;


    public enum Gender {
        MALE,
        FEMALE,
        MECHANICAL
    }
}
