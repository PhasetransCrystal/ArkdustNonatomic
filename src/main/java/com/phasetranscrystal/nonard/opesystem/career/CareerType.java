package com.phasetranscrystal.nonard.opesystem.career;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum CareerType implements StringRepresentable {
    VANGUARD("vanguard"),       // 先锋
    GUARD("guard"),             // 近卫
    DEFENDER("defender"),       // 重装
    SNIPER("sniper"),           // 狙击
    CASTER("caster"),           // 术士
    SUPPORTER("supporter"),     // 辅助
    SPECIALIST("specialist");   // 特种

    public static final List<CareerType> VALUES = List.of(CareerType.values());
    public static final Codec<CareerType> CODEC = StringRepresentable.fromEnum(CareerType::values);

    public final String name;

    CareerType(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}