package com.phasetranscrystal.nonard.opesystem.career;

import com.mojang.serialization.Codec;

public enum CareerType {
    VANGUARD,    // 先锋
    GUARD,       // 近卫
    DEFENDER,    // 重装
    SNIPER,      // 狙击
    CASTER,      // 术士
    SUPPORTER,   // 辅助
    SPECIALIST;  // 特种

    public static final Codec<CareerType> CODEC = Codec.STRING.xmap(CareerType::valueOf, CareerType::name);
}