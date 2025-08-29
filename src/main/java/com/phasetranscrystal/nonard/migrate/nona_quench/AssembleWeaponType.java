package com.phasetranscrystal.nonard.migrate.nona_quench;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.phasetranscrystal.nonard.migrate.nona_quench.meta.EquipAttribute;
import com.phasetranscrystal.nonard.migrate.nona_quench.part.PartType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Math;

import java.util.*;

public class AssembleWeaponType {
    public final int width;    //游戏中该武器组装蓝图的宽度
    public final int height;   //游戏中该武器组装蓝图的高度
    public final Table<Integer, Integer, PointChain> parts;
    public final HashSet<PointChain> chains = new HashSet<>();
    public boolean offhandAllowed = false;

    public static class Builder {
        private final int width;    //游戏中该武器组装蓝图的宽度
        private final int height;   //游戏中该武器组装蓝图的高度
        private final Table<Integer, Integer, PointChain> parts;
        private final HashSet<PointChain> chains = new HashSet<>();
        private final HashMap<ResourceLocation, EquipAttribute> attributes = new HashMap<>();
        private boolean offhandAllowed = false;

        public Builder(int blueprintWidth, int blueprintHeight) {
            if (blueprintWidth <= 0 || blueprintHeight <= 0) {
                throw new IllegalArgumentException("width and height must be positive");
            }
            this.width = blueprintWidth;
            this.height = blueprintHeight;
            this.parts = HashBasedTable.create(width, height);
        }

        public Builder addPart(PartType part, ResourceLocation partId, int x, int y) {
            if (x < 0 || y < 0 || x >= width || y >= height) {
                throw new IllegalArgumentException("x and y coordinates must be between 0 and max-1");
            } else if (parts.contains(x, y)) {
                throw new IllegalArgumentException("x and y coordinates must be unique. occupied: " + parts.get(x, y));
            } else {
                PointChain pointChain = new PointChain(part, partId, new Point(x, y));
                parts.put(x, y, pointChain);
                chains.add(pointChain);
            }
            return this;
        }

        public Builder addPart(PartType part, ResourceLocation partId, Point... points) {
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
                    parts.put(x, y, chain);
                }
            }
            chains.add(chain);
            return this;
        }

        public Builder addWeaponAttribute(ResourceLocation id, double minValue, double maxValue, double defaultValue) {
            if (minValue <= maxValue) {
                throw new IllegalArgumentException("max value must be greater than min value");
            }
            defaultValue = Math.clamp(minValue, maxValue, defaultValue);
            this.attributes.put(id, new EquipAttribute(id, minValue, maxValue, defaultValue));
            return this;
        }

        public Builder offhandable() {
            this.offhandAllowed = true;
            return this;
        }

        public Builder offhandAllowed(boolean allowed) {
            this.offhandAllowed = allowed;
            return this;
        }

        public AssembleWeaponType build() {
            return new AssembleWeaponType(parts, offhandAllowed);
        }
    }

    public record Point(int x, int y) {
    }

    public record PointChain(PartType type, ResourceLocation partId, Point... points) {
    }

    private WeaponType(List<PartType> requiredParts, boolean offhandAllowed) {
        this.requiredParts = ImmutableList.copyOf(requiredParts);
        this.offhandAllowed = offhandAllowed;
    }

    public void registerPropertyConverter(ResourceLocation attributeId, PropertyConverter converter) {
        propertyConverters.put(attributeId, converter);
    }

    public float convertMaterialValue(ResourceLocation attributeId, float materialValue) {
        return propertyConverters.getOrDefault(attributeId, v -> v).convert(materialValue);
    }

    @FunctionalInterface
    public interface PropertyConverter {
        float convert(float materialValue);
    }
}
