package com.phasetranscrystal.nonard.migrate.nona_quench.damage;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public record DamageSourceContext(boolean actuallyDamage, Table<DefenceLayer, ModifyType, Double> damageTypeContent,
                                  List<Consumer<LivingDamageEvent.Pre>> extraModifier,
                                  List<Consumer<LivingDamageEvent.Post>> feedback) {
    public DamageSourceContext(boolean actuallyDamage, Table<DefenceLayer, ModifyType, Double> damageTypeContent,
                               List<Consumer<LivingDamageEvent.Pre>> extraModifier, List<Consumer<LivingDamageEvent.Post>> feedback) {
        this.actuallyDamage = actuallyDamage;
        this.damageTypeContent =
                damageTypeContent instanceof ImmutableTable<DefenceLayer, ModifyType, Double> immutable ? immutable : ImmutableTable.copyOf(damageTypeContent);
        this.extraModifier = List.copyOf(extraModifier);
        this.feedback = List.copyOf(feedback);
    }


    public enum DefenceLayer {
        STANDARD_DEFENCE(false),    //标准防御，伤害数值减量。
        SPELL_DEFENCE(true),        //法术防御层
        HARD_DEFENCE(true),         //硬防层
        SOFT_DEFENCE(true),         //软防层
        RESILIENCE_DEFENCE(false);  //韧性防御，伤害比例减免

        public final boolean percentCalculate;

        DefenceLayer(boolean percentCalculate) {
            this.percentCalculate = percentCalculate;
        }
    }

    public enum ModifyType {
        PENETRATE,          //穿透，即多少伤害无法被这一层预吸收。
        INJURY              //损伤，即造成的耐久损耗的增加比例。
    }

    public static class Builder {
        private boolean actuallyDamage;
        private Table<DamageSourceContext.DefenceLayer, DamageSourceContext.ModifyType, Double> damageTypeContent;
        private List<Consumer<LivingDamageEvent.Pre>> extraModifier = new ArrayList<>();
        private List<Consumer<LivingDamageEvent.Post>> feedback = new ArrayList<>();
        private boolean contentLocked = false;

        public Builder() {
            this.damageTypeContent = HashBasedTable.create();
        }

        public Builder(DamageSourceContext context, boolean contentLocked) {
            this.actuallyDamage = context.actuallyDamage;
            this.damageTypeContent = contentLocked ? context.damageTypeContent : HashBasedTable.create(context.damageTypeContent);
            this.extraModifier.addAll(context.extraModifier);
            this.feedback.addAll(context.feedback);
            this.contentLocked = contentLocked;
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

        public Builder addFeedback(Consumer<LivingDamageEvent.Post> feedback) {
            this.feedback.add(feedback);
            return this;
        }

        public Builder addAllFeedbacks(Collection<Consumer<LivingDamageEvent.Post>> feedbacks) {
            this.feedback.addAll(feedbacks);
            return this;
        }

        public DamageSourceContext build() {
            // 如果内容未被锁定，创建不可变副本
            Table<DamageSourceContext.DefenceLayer, DamageSourceContext.ModifyType, Double> finalContent =
                    contentLocked ? damageTypeContent : ImmutableTable.copyOf(damageTypeContent);

            return new DamageSourceContext(
                    actuallyDamage,
                    finalContent,
                    extraModifier,
                    feedback
            );
        }
    }
}
