package com.phasetranscrystal.nonard.registry;

import com.phasetranscrystal.nonard.Nonard;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

//@Deprecated
public class AttributeTypeRegistry {
    public static final DeferredRegister<Attribute> REGISTER = DeferredRegister.create(Registries.ATTRIBUTE, Nonard.MOD_ID);
//    public static final DeferredHolder<Attribute, Attribute> EXPENDED_HEALTH = REGISTER.register("entity.max_health",
//            () -> new RangedAttribute("attribute.name.nonard.entity.max_health", 1.0, 1.0, Double.MAX_VALUE).setSyncable(true)
//    );
//    public static final DeferredHolder<Attribute, Attribute> EXPENDED_DAMAGE = REGISTER.register("entity.damage",
//            () -> new RangedAttribute("attribute.name.nonard.entity.damage", 0.0, 0.0, Double.MAX_VALUE).setSyncable(true)
//    );
//    public static final DeferredHolder<Attribute, Attribute> EXPENDED_ARMOR = REGISTER.register("entity.armor",
//            () -> new RangedAttribute("attribute.name.nonard.entity.armor", 0.0, 0.0, Double.MAX_VALUE).setSyncable(true)
//    );
    public static final DeferredHolder<Attribute, Attribute> MAGIC_RESISTANCE = REGISTER.register("entity.magic_resistance",
            () -> new RangedAttribute("attribute.name.nonard.entity.magic_resistance", 0.0, 0.0, 1).setSyncable(true)
    );
}
