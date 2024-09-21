package com.phasetranscrystal.nonard.testobjs;

import com.mojang.serialization.Codec;
import com.phasetranscrystal.nonard.Nonard;
import com.phasetranscrystal.nonard.Registries;
import com.phasetranscrystal.nonard.skill.Skill;
import com.phasetranscrystal.nonard.skill.SkillData;
import io.github.tt432.eyelib.network.SpawnParticlePacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class SkillTest {

    public static void bootstrap(IEventBus bus) {
        SKILL.register(bus);
        ATTACHMENT.register(bus);
        ITEM.register(bus);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, LivingDeathEvent.class, SkillTest::onDeath);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, PlayerEvent.PlayerLoggedInEvent.class, SkillTest::init);
    }

    public static final DeferredRegister<Skill<?>> SKILL = DeferredRegister.create(Registries.SKILL, Nonard.MOD_ID);

    public static final DeferredHolder<Skill<?>, Skill<ServerPlayer>> TEST_SKILL = SKILL.register("test", () ->
            Skill.Builder.<ServerPlayer>of(30, 4)
                    .push(data -> data.getEntity().displayClientMessage(Component.literal("TextSkillInit"), false))
                    .flag(Skill.Flag.INSTANT_COMPLETE, true)
                    .onEvent(EntityTickEvent.Post.class, (event, data) -> {
                        ServerPlayer player = data.getEntity();
                        if (player.serverLevel().getGameTime() % 100 == 0 && player.getHealth() < player.getMaxHealth()) {
                            player.displayClientMessage(Component.literal("You are healed!"), false);
                            player.addEffect(new MobEffectInstance(MobEffects.HEAL, 100, 2));
                        }
                    })
                    .inactive(builder -> builder
                            .onHurt((event, data) -> data.releaseEnergy(1, false))
                            .onAttack((event, data) -> data.chargeEnergy(2))
                            .onKill((event, data) -> data.chargeEnergy(5))
                            .energyChanged((data, i) -> data.getEntity().displayClientMessage(Component.literal("Energy " + (i >= 0 ? "§a+" : "§c-") + i), true))
                            .chargeChanged((data, i) -> {
                                data.getEntity().displayClientMessage(Component.literal("Charge " + (i >= 0 ? "§a+" : "§c-") + i), true);
                                ResourceLocation location = ResourceLocation.fromNamespaceAndPath(Nonard.MOD_ID, "skill_test");
                                data.addAutoCleanAttribute(new AttributeModifier(location, 0.5 * data.getCharge(), AttributeModifier.Operation.ADD_VALUE), Attributes.MOVEMENT_SPEED);
                            })
                            .reachReady(data -> data.getEntity().displayClientMessage(Component.literal("ReachReady!"), false))
                            .reachStop(data -> data.getEntity().displayClientMessage(Component.literal("ReachStop!"), false))
                            .end(data -> {
                                data.putCacheData("charge_consume", data.getCharge() + 1 + "", true, true);
                                data.setCharge(0);
                            })
                    )
                    .judge(data -> data.getEntity().level().isNight())
                    .active(builder -> builder
                            .start(data -> PacketDistributor.sendToPlayersTrackingChunk(
                                    (ServerLevel) data.getEntity().level(), new ChunkPos(data.getEntity().blockPosition()),
                                    new SpawnParticlePacket("baozi", ParticlesTest.LARGE_SPORE_RING_SPRAY, data.getEntity().getPosition(0).toVector3f())
                            ))
                            .onTick((event, data) -> data.getEntity().displayClientMessage(Component.literal("activeTick"), false))
                            .end(data -> {
                                data.getEntity().addDeltaMovement(new Vec3(0, 10, 0));
                                data.getEntity().addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200 * Integer.parseInt(data.getCacheData("charge_consume")), 2));
                            })
                    )
                    .stateChange((data, bool) -> {
                        if (!bool && data.getActiveTimes() == 3) data.requestDisable();
                        else
                            data.getEntity().displayClientMessage(Component.literal("StateChanged: to " + bool + " time " + data.getActiveTimes()), false);
                    })
                    .pop(data -> data.getEntity().displayClientMessage(Component.literal("skill disabled"), false))
    );

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Nonard.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SkillData<ServerPlayer>>> SKILL_ATTACHMENT =
            ATTACHMENT.register("skill", () -> AttachmentType.builder(() -> new SkillData<>(TEST_SKILL.get())).serialize((Codec)SkillData.CODEC).copyOnDeath().build());

    public static final DeferredRegister<Item> ITEM = DeferredRegister.create(net.minecraft.core.registries.Registries.ITEM, Nonard.MOD_ID);

    public static final DeferredHolder<Item, Start> START = ITEM.register("skill_start", Start::new);


    public static void onDeath(LivingDeathEvent event) {
        event.getEntity().getExistingData(SKILL_ATTACHMENT).ifPresent(SkillData::requestDisable);
    }

    public static void init(PlayerEvent.PlayerLoggedInEvent event) {
        event.getEntity().getData(SKILL_ATTACHMENT).bindEntity((ServerPlayer) event.getEntity());
    }

    public static class Start extends Item {
        public Start() {
            super(new Properties());
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
            if (level instanceof ServerLevel server) {
                player.getData(SKILL_ATTACHMENT).nextStage();
            }
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }
    }
}
