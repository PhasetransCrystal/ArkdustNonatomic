package com.phasetranscrystal.nonard.migrate.nona_quench.meta;

import com.google.common.collect.ImmutableMap;
import com.phasetranscrystal.nonard.migrate.nona_quench.perk.IEquipPerk;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public record PerkStrength(int global, Map<IEquipPerk, Integer> extra) {
    public static final PerkStrength EMPTY = new PerkStrength(0, ImmutableMap.of());

    public int getStrength(IEquipPerk perk) {
        return Math.max(extra.getOrDefault(perk, 0) + global, 0);
    }

    public PerkStrength merge(PerkStrength another){
        ImmutableMap.Builder<IEquipPerk, Integer> builder = ImmutableMap.builder();
        builder.putAll(extra);
        builder.putAll(another.extra);
        return new PerkStrength(global + another.global, builder.build());
    }

    public Mutable toMutable(){
        return new Mutable(this);
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

    public static class Mutable{
        public int global = 0;
        public final Map<IEquipPerk, Integer> extra = new HashMap<>();
        public Mutable(int global) {
            this.global = global;
        }

        public Mutable(){
        }

        public Mutable(PerkStrength copy){
            this.global = copy.global;
            this.extra.putAll(copy.extra);
        }

        public Mutable add(PerkStrength another){
            this.global += another.global;
            another.extra.forEach((key, value) -> {
                extra.put(key, extra.getOrDefault(key, 0) + value);
            });
            return this;
        }

        public PerkStrength build(){
            return new PerkStrength(global, ImmutableMap.copyOf(extra));
        }

    }
}
