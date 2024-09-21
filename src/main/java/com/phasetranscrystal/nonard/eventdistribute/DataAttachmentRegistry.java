package com.phasetranscrystal.nonard.eventdistribute;

import com.phasetranscrystal.nonard.Nonard;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class DataAttachmentRegistry {
    public static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Nonard.MOD_ID);

    //我们不需要保存它，因此也没有codec。
    public static final DeferredHolder<AttachmentType<?>,AttachmentType<EntityEventDistribute>> EVENT_DISTRIBUTE =
            REGISTER.register("event_dispatch",() -> AttachmentType.builder(EntityEventDistribute::new).build());
}
