package com.phasetranscrystal.nonard.opesystem.trait;

import com.mojang.serialization.Codec;

public enum TraitType {

    NORMAL,       // 普通
    SAME_CAREER,  // 相同职业
    SAME_BRANCH;  // 相同分支

    public static final Codec<TraitType> CODEC = Codec.STRING.xmap(TraitType::valueOf, TraitType::name);
}
