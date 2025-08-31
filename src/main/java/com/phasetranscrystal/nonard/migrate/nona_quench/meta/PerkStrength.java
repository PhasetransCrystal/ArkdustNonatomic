package com.phasetranscrystal.nonard.migrate.nona_quench.meta;

import com.google.common.collect.ImmutableMap;
import com.phasetranscrystal.nonard.migrate.nona_quench.core.IEquipPerk;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public record PerkStrength(int global, Map<IEquipPerk, Integer> extra) {
    public int getStrength(IEquipPerk perk) {
        return Math.max(extra.getOrDefault(perk, 0) + global, 0);
    }

    public static PerkStrength group(Stream<PerkStrength> elems) {
        Map<IEquipPerk, Integer> map = new HashMap<>();
        AtomicInteger global = new AtomicInteger();
       elems.forEach(ele -> {
           global.addAndGet(ele.global);
           ele.extra.forEach((key, value) -> {
               map.put(key, map.getOrDefault(key, 0) + value);
           });
       });
        return new PerkStrength(global.get(), ImmutableMap.copyOf(map));
    }
}
