package com.phasetranscrystal.nonard.migrate.nona_quench;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import com.google.common.collect.*;
import com.phasetranscrystal.nonard.migrate.nona_quench.core.IEquipFrame;
import com.phasetranscrystal.nonard.migrate.nona_quench.core.IEquipItem;
import com.phasetranscrystal.nonard.migrate.nona_quench.core.IEquipType;
import com.phasetranscrystal.nonard.migrate.nona_quench.meta.EquipAttribute;
import com.phasetranscrystal.nonard.migrate.nona_quench.part.PartType;
import org.joml.Math;

import java.util.*;

public class AssembleWeaponType<T extends Item & IEquipItem<T>> implements IEquipType<T> {// TODO

    public final Class<T> itemClass;
    public final int blueprintWidth;    // 游戏中该武器组装蓝图的宽度
    public final int blueprintHeight;   // 游戏中该武器组装蓝图的高度
    public final int moduleGridWidth;
    public final int moduleGridHeight;
    public final Table<Integer, Integer, ResourceLocation> parts;
    public final Map<ResourceLocation, PointChain> chains;
    public final Map<ResourceLocation, EquipAttribute> attributes;
    public final boolean offhandAllowed;

    public AssembleWeaponType(Class<T> itemClass, int blueprintWidth, int blueprintHeight, int moduleGridWidth, int moduleGridHeight,
                              Table<Integer, Integer, ResourceLocation> parts, Map<ResourceLocation, PointChain> chains,
                              Map<ResourceLocation, EquipAttribute> attributes, boolean offhandAllowed) {
        this.itemClass = itemClass;
        this.blueprintWidth = blueprintWidth;
        this.blueprintHeight = blueprintHeight;
        this.moduleGridWidth = moduleGridWidth;
        this.moduleGridHeight = moduleGridHeight;
        this.parts = ImmutableTable.copyOf(parts);
        this.chains = ImmutableMap.copyOf(chains);
        this.attributes = ImmutableMap.copyOf(attributes);
        this.offhandAllowed = offhandAllowed;
    }

    @Override
    public int blueprintX() {
        return blueprintWidth;
    }

    @Override
    public int blueprintY() {
        return blueprintHeight;
    }

    @Override
    public int moduleGridWidth() {
        return moduleGridWidth;
    }

    @Override
    public int moduleGridHeight() {
        return moduleGridHeight;
    }

    @Override
    public Map<ResourceLocation, EquipAttribute> equipAttributes() {
        return attributes;
    }

    @Override
    public Table<Integer, Integer, ResourceLocation> blueprintParts() {
        return parts;
    }

    @Override
    public Map<ResourceLocation, PointChain> getParts() {
        return chains;
    }

    @Override
    public boolean slotPosEquipable(ResourceLocation slotFlag) {
        return slotFlag.equals(MAIN_HAND) || (slotFlag.equals(MAIN_HAND) && offhandAllowed);
    }

    @Override
    public IEquipFrame<T> getDefaultFrame() {// TODO
        return null;
    }

    public static class Builder<T extends Item & IEquipItem<T>> {

        private final Class<T> basicClass;
        private final int width;    // 游戏中该武器组装蓝图的宽度
        private final int height;   // 游戏中该武器组装蓝图的高度
        private int moduleGridWidth;
        private int moduleGridHeight;
        private final Table<Integer, Integer, ResourceLocation> parts;
        private final HashMap<ResourceLocation, PointChain> chains = new HashMap<>();
        private final HashMap<ResourceLocation, EquipAttribute> attributes = new HashMap<>();
        private boolean offhandAllowed = false;

        public Builder(int blueprintWidth, int blueprintHeight, int moduleGridWidth, int moduleGridHeight, Class<T> basicClass) {
            if (blueprintWidth <= 0 || blueprintHeight <= 0) {
                throw new IllegalArgumentException("blueprint's width and height must be positive");
            } else if (moduleGridWidth <= 0 || moduleGridHeight <= 0) {
                throw new IllegalArgumentException("module grid's width and height must be positive");
            }
            this.basicClass = basicClass;
            this.width = blueprintWidth;
            this.height = blueprintHeight;
            this.moduleGridWidth = moduleGridWidth;
            this.moduleGridHeight = moduleGridHeight;
            this.parts = HashBasedTable.create(width, height);
        }

        public Builder<T> addPart(PartType part, ResourceLocation partId, int x, int y) {
            if (chains.containsKey(partId)) {
                throw new IllegalArgumentException("part id already exist");
            } else if (x < 0 || y < 0 || x >= width || y >= height) {
                throw new IllegalArgumentException("x and y coordinates must be between 0 and max-1");
            } else if (parts.contains(x, y)) {
                throw new IllegalArgumentException("x and y coordinates must be unique. occupied: " + parts.get(x, y));
            } else {
                PointChain pointChain = new PointChain(part, partId, new Point(x, y));
                parts.put(x, y, partId);
                chains.put(partId, pointChain);
            }
            return this;
        }

        public Builder<T> addPart(PartType part, ResourceLocation partId, Point... points) {
            if (chains.containsKey(partId)) {
                throw new IllegalArgumentException("part id already exist");
            }
            int x, y;
            PointChain chain = new PointChain(part, partId, points);
            for (Point p : points) {
                x = p.x;
                y = p.y;
                if (x < 0 || y < 0 || x >= width || y >= height) {
                    throw new IllegalArgumentException("x and y coordinates must be between 0 and max-1");
                } else if (parts.contains(x, y)) {
                    throw new IllegalArgumentException("x and y coordinates must be unique. occupied: " + parts.get(x, y));
                } else {
                    parts.put(x, y, partId);
                }
            }
            chains.put(partId, chain);
            return this;
        }

        public Builder<T> addWeaponAttribute(ResourceLocation id, double minValue, double maxValue, double defaultValue) {
            if (minValue <= maxValue) {
                throw new IllegalArgumentException("max value must be greater than min value");
            }
            defaultValue = Math.clamp(minValue, maxValue, defaultValue);
            this.attributes.put(id, new EquipAttribute(id, minValue, maxValue, defaultValue));
            return this;
        }

        public Builder<T> offhandable() {
            this.offhandAllowed = true;
            return this;
        }

        public Builder<T> setModuleGridDefaultSize(int x, int y) {
            if (x <= 0 || y <= 0) {
                throw new IllegalArgumentException("module grid's width and length must be positive");
            }
            this.moduleGridWidth = x;
            this.moduleGridHeight = y;
            return this;
        }

        public Builder<T> offhandAllowed(boolean allowed) {
            this.offhandAllowed = allowed;
            return this;
        }

        public AssembleWeaponType<T> build() {
            return new AssembleWeaponType<T>(this.basicClass, this.width, this.height, this.moduleGridWidth, this.moduleGridHeight, parts, chains, attributes, offhandAllowed);
        }
    }

    public record Point(int x, int y) {}

    public record PointChain(PartType type, ResourceLocation partId, Point... points) {}
}
