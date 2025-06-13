package com.phasetranscrystal.nonard.opesystem.skill;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.phasetranscrystal.nonard.migrate.ardcore.econ.Account;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.function.Predicate;

public record SkillUpgradeRequirement(@Range(from = 1, to = Integer.MAX_VALUE) int maxLevel,
                                      ImmutableMap<Integer, Element> requirements) {
    public SkillUpgradeRequirement(@Range(from = 1, to = Integer.MAX_VALUE) int maxLevel, Int2ObjectMap<Element> requirements) {
        this(maxLevel, ImmutableMap.copyOf(requirements));
    }

    public SkillUpgradeRequirement(@Range(from = 1, to = Integer.MAX_VALUE) int maxLevel, Int2ObjectFunction<Element> elementProvider){
        this(maxLevel, map(elementProvider, maxLevel));
    }

    public record Element(Account.Requirement.Immut accountRequirement,
                          ImmutableList<Predicate<ItemStack>> itemRequirements) {
        public Element(Account.Requirement accountRequirement, List<Predicate<ItemStack>> itemRequirements) {
            this(accountRequirement.immutable(), ImmutableList.copyOf(itemRequirements));
        }
    }

    private static ImmutableMap<Integer, Element> map(Int2ObjectFunction<Element> func, int maxLevel){
        ImmutableMap.Builder<Integer, Element> builder = ImmutableMap.builder();
        for (int level = 0; level <= maxLevel; level++) {
            builder.put(level, func.apply(level));
        }
        return builder.build();
    }
}
