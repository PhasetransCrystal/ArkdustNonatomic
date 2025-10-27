package com.phasetranscrystal.nonard.migrate.nona_quench.meta;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.phasetranscrystal.nonard.migrate.nona_quench.AssembleWeaponType;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;

import java.util.*;

public record EquipAttribute(ResourceLocation id, double minValue, double maxValue, double basicValue) {

    public static final Codec<EquipAttribute> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(EquipAttribute::id),
            Codec.DOUBLE.fieldOf("min").forGetter(EquipAttribute::minValue),
            Codec.DOUBLE.fieldOf("max").forGetter(EquipAttribute::maxValue),
            Codec.DOUBLE.fieldOf("basic").forGetter(EquipAttribute::basicValue)).apply(i, EquipAttribute::new));

    public record Modifier(ResourceLocation id, double value, Stage stage) {

        public static final Codec<Modifier> CODEC = RecordCodecBuilder.create(i -> i.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(Modifier::id),
                Codec.DOUBLE.fieldOf("value").forGetter(Modifier::value),
                Stage.CODEC.fieldOf("stage").forGetter(Modifier::stage)).apply(i, Modifier::new));

        public enum Stage implements StringRepresentable {

            PLUS("p"),
            MULTIPLY_BASE("mb"),
            MULTIPLY_TOTAL("mt");

            public static final Codec<Stage> CODEC = StringRepresentable.fromEnum(Stage::values);
            public final String name;

            Stage(String name) {
                this.name = name;
            }

            @Override
            public String getSerializedName() {
                return this.name;
            }
        }
    }

    public static class Manager {

        public static final Codec<Manager> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Modifier.CODEC.listOf()).xmap(Manager::new, Manager::flat);

        // 原始数据结构：属性ID -> (修饰符ID -> 修饰符)
        private final Map<ResourceLocation, Map<ResourceLocation, Modifier>> attributes = new HashMap<>();

        // 缓存
        private boolean initialized = false;
        private AssembleWeaponType type;// TODO 改为框架
        private Map<ResourceLocation, Double> cachedValues = new HashMap<>();

        public Manager() {}

        public Manager(Map<ResourceLocation, List<Modifier>> flat) {
            flat.forEach((s, modifiers) -> {
                Map<ResourceLocation, Modifier> map = new HashMap<>();
                modifiers.forEach(m -> map.put(m.id, m));
                attributes.put(s, map);
            });
        }

        // 添加修饰符
        public Modifier addModifier(ResourceLocation attributeId, Modifier modifier) {
            if (initialized && containsAttribute(attributeId)) { // TODO 进行属性存在性校验
                Modifier m = attributes.computeIfAbsent(attributeId, k -> new HashMap<>())
                        .put(modifier.id(), modifier);
                recalculate(attributeId); // 修改后使缓存失效
                return m;
            }
            return null;
        }

        // 移除特定修饰符
        public Modifier removeModifier(ResourceLocation attributeId, ResourceLocation modifierId) {
            if (attributes.containsKey(attributeId)) {
                Modifier removed = attributes.get(attributeId).remove(modifierId);
                recalculate(attributeId);
                return removed;
            }
            return null;
        }

        // 清除所有修饰符
        public void clearAllModifiers() {
            attributes.clear();
            recalculate(null);
        }

        // 清除特定属性的修饰符
        public void clearAttributeModifiers(ResourceLocation attributeId) {
            Map<ResourceLocation, Modifier> removed = attributes.remove(attributeId);
            if (removed != null && !removed.isEmpty()) {
                cachedValues.put(attributeId, getAttribute(attributeId).basicValue());
            }
        }

        // 重构缓存
        private void recalculate(@Nullable ResourceLocation attributeId) {
            if (attributeId != null) {
                cachedValues.put(attributeId, calculateValue(getAttribute(attributeId)));
            } else {
                cachedValues.clear();
                for (EquipAttribute id : getAttributes()) {
                    cachedValues.put(id.id(), calculateValue(id));
                }
            }
        }

        public boolean inited() {
            return initialized;
        }

        // 初始化并计算所有属性值
        public void initialize(AssembleWeaponType weapon) {
            this.type = weapon;

            // 清理冗余修饰符（只保留有对应属性的修饰符）
            attributes.keySet().retainAll(getAttributes().stream().map(EquipAttribute::id).toList());

            // 计算并缓存所有属性的最终值
            recalculate(null);

            initialized = true;
        }

        // 获取缓存的计算值
        public double getCachedValue(ResourceLocation attributeId) {
            if (!initialized) {
                return Double.NaN;
            }
            return cachedValues.getOrDefault(attributeId, 0.0);
        }

        // 实际计算逻辑（内部使用）
        private double calculateValue(EquipAttribute attribute) {
            Map<ResourceLocation, Modifier> modifiers = attributes.getOrDefault(attribute.id(), Collections.emptyMap());
            if (modifiers.isEmpty()) {
                return attribute.basicValue();
            }

            double plusSum = 0;
            double multiplySum = 0;
            double multiplyFactor = 1;

            for (Modifier modifier : modifiers.values()) {
                switch (modifier.stage()) {
                    case PLUS -> plusSum += modifier.value();
                    case MULTIPLY_BASE -> multiplySum += modifier.value();
                    case MULTIPLY_TOTAL -> multiplyFactor *= 1 + modifier.value();
                }
            }

            double value = attribute.basicValue() + plusSum;
            value *= (1 + multiplySum);
            value *= multiplyFactor;

            return Math.clamp(attribute.minValue(), attribute.maxValue(), value);
        }

        public EquipAttribute getAttribute(ResourceLocation id) {
            // TODO
            return null;
        }

        public List<EquipAttribute> getAttributes() {
            // TODO
            return List.of();
        }

        public boolean containsAttribute(ResourceLocation location) {
            // TODO
            return inited() && true;
        }

        private Map<ResourceLocation, List<Modifier>> flat() {
            Map<ResourceLocation, List<Modifier>> map = new HashMap<>();
            attributes.forEach((k, v) -> map.put(k, v.values().stream().toList()));
            return map;
        }
    }
}
