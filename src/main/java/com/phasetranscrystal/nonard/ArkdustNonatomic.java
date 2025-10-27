package com.phasetranscrystal.nonard;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import com.phasetranscrystal.nonard.opesystem.ArkOpeHandler;
import com.phasetranscrystal.nonard.opesystem.info.GeneralOperatorInfo;
import com.phasetranscrystal.nonard.registry.AttributeTypeRegistry;
import com.phasetranscrystal.nonard.testobjs.TestObjects;
import com.phasetranscrystal.nonatomic.GameBusConsumer;

@Mod(ArkdustNonatomic.MODID)
public class ArkdustNonatomic {

    public static final String MODID = "arkdust_nona";
    private static GeneralOperatorInfo generalOperatorInfo;

    public ArkdustNonatomic(IEventBus modEventBus, ModContainer modContainer) {
        ATTACHMENT_REG.register(modEventBus);
        GameBusConsumer.registerHandlerEvents(s -> s.overworld().getData(DATA.get()));

        AttributeTypeRegistry.REGISTER.register(modEventBus);

        TestObjects.bootstrap(modEventBus);

        // TODO bootstrap config loading
        generalOperatorInfo = null;
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_REG = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ArkOpeHandler.WorldAttach>> DATA = ATTACHMENT_REG.register("operator_infos", () -> AttachmentType.builder(ArkOpeHandler.WorldAttach::new).serialize(ArkOpeHandler.WorldAttach.CODEC).build());

    public static GeneralOperatorInfo getBasicOperatorInfo() {
        return generalOperatorInfo;
    }
}
