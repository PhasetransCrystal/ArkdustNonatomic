package com.phasetranscrystal.nonard.migrate.nona_quench.perk;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.phasetranscrystal.nonard.migrate.nona_quench.NewRegistries;
import com.phasetranscrystal.nonard.migrate.nona_quench.core.IEquipFrame;
import com.phasetranscrystal.nonard.migrate.nona_quench.core.IEquipItem;
import com.phasetranscrystal.nonard.migrate.nona_quench.helper.WeightedRandomSelector;
import com.phasetranscrystal.nonard.migrate.nona_quench.registry.BreaQuenchRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record PerkPool(List<Column> list) {
    public static final Logger LOGGER = LogManager.getLogger("BreaQuench:Perk:PerkPool");
    public static final Supplier<List<IEquipPerk>> EMPTY_PERK_LIST = Suppliers.memoize(() -> ImmutableList.of(BreaQuenchRegistries.DEFAULT.get()));
    public static final Codec<PerkPool> CODEC = Column.CODEC.listOf().xmap(PerkPool::new, PerkPool::list);

    public record Column(ImmutableMap<IEquipPerk, Integer> perkList) {
        public static final Codec<Column> CODEC =
                Codec.mapPair(NewRegistries.PERKS.byNameCodec().fieldOf("perk"), Codec.INT.fieldOf("weight"))
                        .codec().listOf()
                        .xmap(list -> {
                            ImmutableMap.Builder<IEquipPerk, Integer> builder = ImmutableMap.builder();
                            list.forEach(p -> builder.put(p.getFirst(), p.getSecond()));
                            return new Column(builder.build());
                        }, column -> column.perkList.entrySet().stream().map(e -> new Pair<>(e.getKey(), e.getValue())).toList());


        public <T extends Item & IEquipItem<T>> Column filter(IEquipFrame<T> frame, @Nullable IEquipItem<T> item, boolean logging) {
            ImmutableMap.Builder<IEquipPerk, Integer> builder = ImmutableMap.builder();
            perkList.forEach((perk, weight) -> {
                if (perk.canUseOn(frame, item)) {
                    builder.put(perk, weight);
                } else if (logging) {
                    LOGGER.warn("Perk({}) in perk column is not suitable for Frame = {}, Item = {}", perk, frame, item);
                }
            });
            return new Column(builder.build());
        }

        public List<IEquipPerk> generatePerks(int count, RandomSource source) {
            List<IEquipPerk> perks = WeightedRandomSelector.selectWeightedRandom(perkList, count, source);
            return perks.isEmpty() ? EMPTY_PERK_LIST.get() : perks;
        }
    }

    public <T extends Item & IEquipItem<T>> PerkPool filter(IEquipFrame<T> frame, @Nullable IEquipItem<T> item, boolean logging) {
        ImmutableList.Builder<Column> builder = ImmutableList.builder();
        for (Column column : this.list) {
            builder.add(column.filter(frame, item, logging));
        }
        return new PerkPool(builder.build());
    }

    public List<List<IEquipPerk>> generatePerks(int[] count, RandomSource source) {
        boolean flag = false;
        List<List<IEquipPerk>> perks = new ArrayList<>();
        for (int i = 0; i < this.list.size(); i++) {
            flag = flag || i >= count.length;
            perks.add(this.list.get(i).generatePerks(flag ? 1 : count[i], source));
        }
        return perks;
    }


}
