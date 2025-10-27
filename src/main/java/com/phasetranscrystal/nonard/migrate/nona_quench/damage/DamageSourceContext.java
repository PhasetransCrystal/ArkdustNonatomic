package com.phasetranscrystal.nonard.migrate.nona_quench.damage;

import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

// TODO 元素转映部分
public record DamageSourceContext(boolean actuallyDamage, float overpressureFactor, int cooldownTick, int slowdownTick,
                                  Table<DefenceLayer, ModifyType, Double> damageContent,
                                  List<Consumer<LivingDamageEvent.Pre>> preModifier,
                                  List<Consumer<LivingDamageEvent.Pre>> extraModifier,
                                  List<Consumer<LivingDamageEvent.Post>> feedback) {

    public DamageSourceContext(boolean actuallyDamage, float overpressureFactor, int cooldownTick, int slowdownTick,
                               Table<DefenceLayer, ModifyType, Double> damageContent,
                               List<Consumer<LivingDamageEvent.Pre>> preModifier,
                               List<Consumer<LivingDamageEvent.Pre>> extraModifier,
                               List<Consumer<LivingDamageEvent.Post>> feedback) {
        this.actuallyDamage = actuallyDamage;
        this.overpressureFactor = org.joml.Math.clamp(0.0f, 1.0f, overpressureFactor);
        this.cooldownTick = Math.max(0, cooldownTick);
        this.slowdownTick = Math.max(0, slowdownTick);
        this.damageContent = damageContent instanceof ImmutableTable<DefenceLayer, ModifyType, Double> immutable ? immutable : ImmutableTable.copyOf(damageContent);
        this.preModifier = List.copyOf(preModifier);
        this.extraModifier = List.copyOf(extraModifier);
        this.feedback = List.copyOf(feedback);
    }

    public enum DefenceLayer {

        STANDARD_DEFENCE(false),    // 标准防御，伤害数值减量。
        SPELL_DEFENCE(true),        // 法术防御层
        HARD_DEFENCE(true),         // 硬防层
        SOFT_DEFENCE(true),         // 软防层
        RESILIENCE_DEFENCE(false);  // 韧性防御，伤害比例减免

        public final boolean percentCalculate;

        DefenceLayer(boolean percentCalculate) {
            this.percentCalculate = percentCalculate;
        }
    }

    public enum ModifyType {
        PENETRATE,          // 穿透，即多少比例的伤害无法被这一层预吸收。
        INJURY              // 损伤，即造成的耐久损耗的增加比例。
    }

    public static class Builder {

        private int cooldownTick = 9;
        private boolean actuallyDamage;
        private float overpressureFactor = 0;
        private int slowdownTick = 0;
        private Table<DamageSourceContext.DefenceLayer, DamageSourceContext.ModifyType, Double> damageTypeContent;
        private final List<Consumer<LivingDamageEvent.Pre>> preModifier = new ArrayList<>();
        private final List<Consumer<LivingDamageEvent.Pre>> extraModifier = new ArrayList<>();
        private final List<Consumer<LivingDamageEvent.Post>> feedback = new ArrayList<>();
        private boolean contentLocked = false;

        public Builder() {
            this.damageTypeContent = HashBasedTable.create();
        }

        public Builder(DamageSourceContext context, boolean contentLocked) {
            this.actuallyDamage = context.actuallyDamage;
            this.damageTypeContent = contentLocked ? context.damageContent : HashBasedTable.create(context.damageContent);
            this.extraModifier.addAll(context.extraModifier);
            this.feedback.addAll(context.feedback);
            this.contentLocked = contentLocked;
        }

        public Builder setCooldownTick(int tick) {
            this.cooldownTick = tick;
            return this;
        }

        public Builder setSlowdownTick(int tick) {
            this.slowdownTick = tick;
            return this;
        }

        public Builder setOverpressureFactor(float factor) {
            this.overpressureFactor = factor;
            return this;
        }

        public Builder actuallyDamage() {
            this.actuallyDamage = true;
            return this;
        }

        public Builder actuallyDamage(boolean actuallyDamage) {
            this.actuallyDamage = actuallyDamage;
            return this;
        }

        // 添加单个内容项
        public Builder addDamageTypeContent(
                                            DamageSourceContext.DefenceLayer layer,
                                            DamageSourceContext.ModifyType type,
                                            double value) {
            if (!contentLocked) {
                this.damageTypeContent.put(layer, type, value);
            }
            return this;
        }

        // 添加多个内容项
        public Builder addAllDamageTypeContent(
                                               Table<DamageSourceContext.DefenceLayer, DamageSourceContext.ModifyType, Double> content) {
            if (!contentLocked) {
                this.damageTypeContent.putAll(content);
            }
            return this;
        }

        // 替换整个内容并锁定（使用ImmutableTable）
        public Builder setImmutableDamageTypeContent(
                                                     ImmutableTable<DamageSourceContext.DefenceLayer, DamageSourceContext.ModifyType, Double> content) {
            this.damageTypeContent = content;
            this.contentLocked = true; // 设置后锁定
            return this;
        }

        // 复制现有内容并允许修改
        public Builder copyDamageTypeContent(
                                             Table<DamageSourceContext.DefenceLayer, DamageSourceContext.ModifyType, Double> content) {
            this.damageTypeContent = HashBasedTable.create(content);
            return this;
        }

        public Builder addExtraModifier(Consumer<LivingDamageEvent.Pre> modifier) {
            this.extraModifier.add(modifier);
            return this;
        }

        public Builder addAllExtraModifiers(Collection<Consumer<LivingDamageEvent.Pre>> modifiers) {
            this.extraModifier.addAll(modifiers);
            return this;
        }

        public Builder extraMultiply(float value) {
            return addExtraModifier(pre -> pre.setNewDamage(pre.getNewDamage() * value));
        }

        public Builder addPreModifier(Consumer<LivingDamageEvent.Pre> modifier) {
            this.preModifier.add(modifier);
            return this;
        }

        public Builder addAllPreModifiers(Collection<Consumer<LivingDamageEvent.Pre>> modifiers) {
            this.preModifier.addAll(modifiers);
            return this;
        }

        public Builder preMultiply(float value) {
            return addPreModifier(pre -> pre.setNewDamage(pre.getNewDamage() * value));
        }

        public Builder addModifier(Consumer<LivingDamageEvent.Pre> modifier, boolean isPreModifiers) {
            return isPreModifiers ? this.addPreModifier(modifier) : this.addExtraModifier(modifier);
        }

        public Builder addAllModifiers(Collection<Consumer<LivingDamageEvent.Pre>> modifiers, boolean isPreModifiers) {
            return isPreModifiers ? this.addAllPreModifiers(modifiers) : this.addAllExtraModifiers(modifiers);
        }

        public Builder modifyPreModifierList(Consumer<List<Consumer<LivingDamageEvent.Pre>>> listConsumer) {
            listConsumer.accept(this.preModifier);
            return this;
        }

        public Builder modifyExtraModifierList(Consumer<List<Consumer<LivingDamageEvent.Pre>>> listConsumer) {
            listConsumer.accept(this.extraModifier);
            return this;
        }

        public Builder addFeedback(Consumer<LivingDamageEvent.Post> feedback) {
            this.feedback.add(feedback);
            return this;
        }

        public Builder addAllFeedbacks(Collection<Consumer<LivingDamageEvent.Post>> feedbacks) {
            this.feedback.addAll(feedbacks);
            return this;
        }

        public DamageSourceContext build() {
            return new DamageSourceContext(
                    actuallyDamage, overpressureFactor,
                    cooldownTick, slowdownTick,
                    damageTypeContent,
                    preModifier,
                    extraModifier,
                    feedback);
        }
    }
}
