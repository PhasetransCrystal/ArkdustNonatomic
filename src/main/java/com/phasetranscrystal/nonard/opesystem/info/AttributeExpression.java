package com.phasetranscrystal.nonard.opesystem.info;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.phasetranscrystal.nonard.ArkdustNonatomic;
import com.phasetranscrystal.nonard.migrate.ardcore.ExpressionParser;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record AttributeExpression(List<Modifier> modifiers, List<Basic> basicValues) {
    public static final Logger LOGGER = LogManager.getLogger("ArkdustNona:OpeSource:AttributeExpression");
    public static final Codec<AttributeExpression> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Modifier.CODEC.listOf().fieldOf("modifiers").forGetter(AttributeExpression::modifiers),
            Basic.CODEC.listOf().fieldOf("basic").forGetter(AttributeExpression::basicValues)
    ).apply(instance, AttributeExpression::new));

    public void binding(LivingEntity living, int level) {
        int elite = ArkdustNonatomic.getBasicOperatorInfo().getEliteForLevel(level);
        basicValues.forEach(instance -> {
            Optional.ofNullable(living.getAttribute(instance.attribute))
                    .ifPresentOrElse(i -> i.setBaseValue(instance.calculate(level, elite)),
                            () -> {
                                LOGGER.warn("Trying to bind an attribute while entity don't allow it.");
                                LOGGER.warn("Details: EntityType = {}, BasicValueProvider = {}", living.getType(), instance);
                            });
        });
        modifiers.forEach(instance -> {
            Optional.ofNullable(living.getAttribute(instance.attribute))
                    .ifPresentOrElse(i -> i.addOrUpdateTransientModifier(instance.calculate(level, elite)),
                            () -> {
                                LOGGER.warn("Trying to bind an attribute while entity don't allow it.");
                                LOGGER.warn("Details: EntityType = {}, ValueModifierProvider = {}", living.getType(), instance);
                            });
        });
    }

    public record Basic(Holder<Attribute> attribute, String expression) {
        public static final Codec<Basic> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(Basic::attribute),
                Codec.STRING.fieldOf("expression").forGetter(Basic::expression)
        ).apply(instance, Basic::new));

        public double calculate(int level, int elite) {
            return ExpressionParser.evaluate(expression, Map.of("level", (double) level, "elite", (double) elite));
        }
    }

    public record Modifier(Holder<Attribute> attribute, AttributeModifier.Operation operation,
                           String expression, ResourceLocation id) {
        public static final ResourceLocation DEFAULT_ID = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "operator_basic");
        public static final Codec<Modifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(Modifier::attribute),
                AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(Modifier::operation),
                Codec.STRING.fieldOf("expression").forGetter(Modifier::expression),
                ResourceLocation.CODEC.optionalFieldOf("id", DEFAULT_ID).forGetter(Modifier::id)
        ).apply(instance, Modifier::new));

        public AttributeModifier calculate(int level, int elite) {
            return new AttributeModifier(id,
                    ExpressionParser.evaluate(expression, Map.of("level", (double) level, "elite", (double) elite)),
                    operation
            );
        }

    }
}
