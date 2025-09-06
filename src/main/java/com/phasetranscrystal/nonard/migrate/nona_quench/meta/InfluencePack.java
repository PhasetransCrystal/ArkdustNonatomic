package com.phasetranscrystal.nonard.migrate.nona_quench.meta;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.phasetranscrystal.horiz.EntityEventDistribute;
import com.phasetranscrystal.horiz.Horiz;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class InfluencePack {

    public static final ResourceLocation ROOT = ResourceLocation.fromNamespaceAndPath("nona_quench", "equipments");
    public final ResourceLocation root;
    //先都private 需要的时候再打洞
    public final PerkStrength perkStrength;
    private final ResourceLocation[] path;
    private final Map<Holder<Attribute>, TriNum> attribute;
    private final Map<ResourceLocation, TriNum> equipAttribute;
    private final Map<Class<Event>, Consumer<Event>> listeners;

    public InfluencePack(List<ResourceLocation> path,
                         PerkStrength perkStrength,
                         Map<Holder<Attribute>, TriNum> attribute,
                         Map<ResourceLocation, TriNum> equipAttribute,
                         Map<Class<Event>, Consumer<Event>> listeners) {
        this.path = path.toArray(new ResourceLocation[0]);
        this.root = combine(path);
        this.perkStrength = perkStrength;
        this.attribute = attribute;
        this.equipAttribute = equipAttribute;
        this.listeners = listeners;
    }
    //  brea:quench / slotFlag / path

    public void binding(LivingEntity entity) {

        attribute.forEach((atr, tri) -> {
            Optional.ofNullable(entity.getAttribute(atr)).ifPresent(ins -> {
                if (tri.get1() != 0)
                    ins.addOrReplacePermanentModifier(new AttributeModifier(root.withSuffix("stage1"), tri.get1(), AttributeModifier.Operation.ADD_VALUE));
                if (tri.get2() != 0)
                    ins.addOrReplacePermanentModifier(new AttributeModifier(root.withSuffix("stage2"), tri.get2(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                if (tri.get3() != 1)
                    ins.addOrReplacePermanentModifier(new AttributeModifier(root.withSuffix("stage3"), tri.get3(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            });
        });

        EntityEventDistribute distribute = entity.getData(Horiz.EVENT_DISTRIBUTE);

        listeners.forEach((event, consumer) -> addEventListener(event, consumer, distribute));
    }

    private <T extends Event> void addEventListener(Class<T> clazz, Consumer<? extends Event> consumer, EntityEventDistribute distribute) {
        distribute.add(clazz, (Consumer<T>) consumer, path);
    }

    public boolean binding(ItemStack stack) {
        EquipAttribute.Manager manager = null; //TODO
        if (!manager.inited()) return false;
        equipAttribute.forEach((atr, tri) -> {
            if (tri.get1() != 0)
                manager.addModifier(atr, new EquipAttribute.Modifier(root.withSuffix("stage1"), tri.get1(), EquipAttribute.Modifier.Stage.PLUS));
            if (tri.get2() != 0)
                manager.addModifier(atr, new EquipAttribute.Modifier(root.withSuffix("stage2"), tri.get2(), EquipAttribute.Modifier.Stage.MULTIPLY_BASE));
            if (tri.get3() != 1)
                manager.addModifier(atr, new EquipAttribute.Modifier(root.withSuffix("stage3"), tri.get3(), EquipAttribute.Modifier.Stage.MULTIPLY_TOTAL));
        });
        return true;
    }

    public void debind(LivingEntity entity) {
        attribute.keySet().stream().map(entity::getAttribute)
                .filter(Objects::nonNull)
                .forEach(ins -> {
                    ins.removeModifier(root.withSuffix("stage1"));
                    ins.removeModifier(root.withSuffix("stage2"));
                    ins.removeModifier(root.withSuffix("stage3"));
                });
    }

    public boolean debing(ItemStack stack) {
        EquipAttribute.Manager manager = null; //TODO
        if (!manager.inited()) return false;
        equipAttribute.keySet().forEach(atr -> {
            manager.removeModifier(atr, root.withSuffix("stage1"));
            manager.removeModifier(atr, root.withSuffix("stage2"));
            manager.removeModifier(atr, root.withSuffix("stage3"));
        });
        return true;
    }

    public record Child(Multimap<Holder<Attribute>, AttributeModifier> attributeModifiers,
                        Multimap<Class<? extends Event>, Consumer<? extends Event>> listeners,
                        Multimap<ResourceLocation, EquipAttribute.Modifier> modifiers,
                        PerkStrength perkStrength) {
        //Render attach todo
        //Entity AI insert todo

        public static final Child EMPTY = new Child(ImmutableMultimap.of(), ImmutableMultimap.of(), ImmutableMultimap.of(), PerkStrength.EMPTY);

        public Child merge(Collection<Child> children){
            ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> attributeModifiers =  ImmutableMultimap.builder();
            ImmutableMultimap.Builder<Class<? extends Event>, Consumer<? extends Event>> listeners =  ImmutableMultimap.builder();
            ImmutableMultimap.Builder<ResourceLocation, EquipAttribute.Modifier> modifiers =  ImmutableMultimap.builder();
            PerkStrength.Mutable mutable = this.perkStrength.toMutable();
            for(Child child : children){
                attributeModifiers.putAll(child.attributeModifiers);
                listeners.putAll(child.listeners);
                modifiers.putAll(child.modifiers);
                mutable.add(child.perkStrength);
            }
            return new Child(attributeModifiers.build(), listeners.build(), modifiers.build(), mutable.build());
        }
    }

    public static class Builder {
        public final List<ResourceLocation> path;
        private List<Child> children = new ArrayList<>();

        public Builder(ResourceLocation path) {
            this.path = List.of(path);
        }

        public Builder(ResourceLocation... path) {
            this.path = List.of(path);
        }

        public Builder(List<ResourceLocation> path) {
            this.path = List.copyOf(path);
        }

        public Builder addChild(Child child) {
            children.add(child);
            return this;
        }

        public Builder addChildren(Collection<Child> children) {
            this.children.addAll(children);
            return this;
        }

        public InfluencePack build() {
            Map<Holder<Attribute>, TriNum> attributes = new HashMap<>();
            Map<ResourceLocation, TriNum> equipAtr = new HashMap<>();
            Multimap<Class<? extends Event>, Consumer<? extends Event>> events = HashMultimap.create();

            for (Child child : children) {
                child.attributeModifiers.forEach((key, value) -> {
                    TriNum triNum = attributes.computeIfAbsent(key, k -> new TriNum());
                    switch (value.operation()) {
                        case ADD_VALUE -> triNum.add1(value.amount());
                        case ADD_MULTIPLIED_BASE -> triNum.add2(value.amount());
                        case ADD_MULTIPLIED_TOTAL -> triNum.add3(value.amount());
                    }
                });

                child.modifiers.forEach((key, value) -> {
                    TriNum triNum = equipAtr.computeIfAbsent(key, k -> new TriNum());
                    switch (value.stage()) {
                        case PLUS -> triNum.add1(value.value());
                        case MULTIPLY_BASE -> triNum.add2(value.value());
                        case MULTIPLY_TOTAL -> triNum.add3(value.value());
                    }
                });

                events.putAll(child.listeners);
            }

            Map<Class<Event>, Consumer<Event>> listeners = new HashMap<>();
            events.keySet().forEach(key -> {
                List<Consumer<? extends Event>> consumers = List.copyOf(events.get(key));
                listeners.put((Class<Event>) key, e -> consumers.forEach(c -> ((Consumer<Event>) c).accept(e)));
            });

            PerkStrength ps = PerkStrength.group(children.stream().map(Child::perkStrength));

            return new InfluencePack(path, ps, attributes, equipAtr, listeners);
        }
    }

    private static class TriNum {
        public double v1 = 0, v2 = 0, v3 = 1;

        public void add1(double value) {
            v1 += value;
        }

        public void add2(double value) {
            v2 += value;
        }

        public void add3(double value) {
            v3 *= 1 + value;
        }

        public void add(double v1, double v2, double v3) {
            this.v1 += v1;
            this.v2 += v2;
            this.v3 *= 1 + v3;
        }

        public double get1() {
            return v1;
        }

        public double get2() {
            return v2;
        }

        public double get3() {
            return v3;
        }
    }

    public static ResourceLocation combine(List<ResourceLocation> rls) {
        if (rls.isEmpty()) return ResourceLocation.fromNamespaceAndPath("minecraft", "empty");
        else if (rls.size() == 1) return rls.getFirst();
        else {
            ResourceLocation rl = rls.get(0);
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < rls.size(); i++) {
                builder.append("/").append(rls.get(i).getNamespace()).append("/").append(rls.get(i).getPath());
            }
            return rl.withSuffix(builder.toString());
        }
    }

    public List<ResourceLocation> path() {
        return List.of(path);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (InfluencePack) obj;
        return Objects.equals(this.path, that.path) &&
                Objects.equals(this.attribute, that.attribute) &&
                Objects.equals(this.equipAttribute, that.equipAttribute) &&
                Objects.equals(this.listeners, that.listeners);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, attribute, equipAttribute, listeners);
    }

    @Override
    public String toString() {
        return "InfluencePack[" +
                "path=" + path + ", " +
                "attribute=" + attribute + ", " +
                "equipAttribute=" + equipAttribute + ", " +
                "listeners=" + listeners + ']';
    }

}
