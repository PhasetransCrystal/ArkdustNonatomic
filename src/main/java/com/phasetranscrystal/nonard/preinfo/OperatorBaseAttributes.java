package com.phasetranscrystal.nonard.preinfo;

import com.phasetranscrystal.nonard.registry.AttributeTypeRegistry;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

public class OperatorBaseAttributes {
    public static final Holder<Attribute>[] ATTRIBUTES = new Holder[]{Attributes.MAX_HEALTH, Attributes.ATTACK_DAMAGE, Attributes.ATTACK_SPEED, Attributes.ARMOR, AttributeTypeRegistry.MAGIC_RESISTANCE};

    public final double[] values;

    public OperatorBaseAttributes(OperatorBaseAttributes from) {
        values = from.values;
    }

    public OperatorBaseAttributes(double health, double damage, double attackSpeed, double armor, double magicResistance) {
        this.values = new double[]{health, damage, attackSpeed, armor, magicResistance};
    }

    public double getMagicResistance() {
        return values[4];
    }

    public double getArmor() {
        return values[3];
    }

    public double getAttackSpeed() {
        return values[2];
    }

    public double getDamage() {
        return values[1];
    }

    public double getHealth() {
        return values[0];
    }

    public void configureFor(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event) {
        for (int i = 0; i < 6; i++) {
            if (!event.has(entityType, ATTRIBUTES[i])) {
                event.add(entityType, ATTRIBUTES[i], values[i]);
            }
        }
    }

    public static class Builder {
        private double health;
        private double damage;
        private double attackSpeed;
        private double armor;
        private double magicResistance;

        public static Builder create() {
            return new Builder(0, 0, 0, 0, 0);
        }

        public static Builder of(OperatorBaseAttributes existingAttributes) {
            return new Builder(
                    existingAttributes.getHealth(),
                    existingAttributes.getDamage(),
                    existingAttributes.getAttackSpeed(),
                    existingAttributes.getArmor(),
                    existingAttributes.getMagicResistance()
            );
        }

        public Builder(double health, double damage, double attackSpeed, double armor, double magicResistance) {
            // 默认构造空白内容
            this.health = health;
            this.damage = damage;
            this.attackSpeed = attackSpeed;
            this.armor = armor;
            this.magicResistance = magicResistance;
        }

        public Builder setHealth(double health) {
            this.health = health;
            return this;
        }

        public Builder modifyHealth(Double2DoubleFunction modifier) {
            this.health = modifier.applyAsDouble(this.health);
            return this;
        }

        public Builder setDamage(double damage) {
            this.damage = damage;
            return this;
        }

        public Builder modifyDamage(Double2DoubleFunction modifier) {
            this.damage = modifier.applyAsDouble(this.damage);
            return this;
        }

        public Builder setAttackSpeed(double attackSpeed) {
            this.attackSpeed = attackSpeed;
            return this;
        }

        public Builder modifyAttackSpeed(Double2DoubleFunction modifier) {
            this.attackSpeed = modifier.applyAsDouble(this.attackSpeed);
            return this;
        }

        public Builder setArmor(double armor) {
            this.armor = armor;
            return this;
        }

        public Builder modifyArmor(Double2DoubleFunction modifier) {
            this.armor = modifier.applyAsDouble(this.armor);
            return this;
        }

        public Builder setMagicResistance(double magicResistance) {
            this.magicResistance = magicResistance;
            return this;
        }

        public Builder modifyMagicResistance(Double2DoubleFunction modifier) {
            this.magicResistance = modifier.applyAsDouble(this.magicResistance);
            return this;
        }

        public OperatorBaseAttributes build() {
            return new OperatorBaseAttributes(health, damage, attackSpeed, armor, magicResistance);
        }
    }

}
