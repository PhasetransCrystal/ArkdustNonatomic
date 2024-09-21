package com.phasetranscrystal.nonard.registry;

import com.phasetranscrystal.nonard.Nonard;
import com.phasetranscrystal.nonard.Registries;
import com.phasetranscrystal.nonard.preinfo.skill.Skill;
import com.phasetranscrystal.nonard.testobjs.ParticlesTest;
import io.github.tt432.eyelib.network.SpawnParticlePacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SkillRegistry {
    public static final DeferredRegister<Skill<?>> REGISTER = DeferredRegister.create(Registries.SKILL, Nonard.MOD_ID);

    public static final DeferredHolder<Skill<?>, Skill<ServerPlayer>> TEST = REGISTER.register("test", () ->
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
                    )
                    .judge(data -> data.getEntity().level().isNight())
                    .active(builder -> builder
                            .start(data -> PacketDistributor.sendToPlayersTrackingChunk(
                                    (ServerLevel) data.getEntity().level(), new ChunkPos(data.getEntity().blockPosition()),
                                    new SpawnParticlePacket("baozi", ParticlesTest.LARGE_SPORE_RING_SPRAY, data.getEntity().getPosition(0).toVector3f())
                            ))
                            .onTick((event, data) -> data.getEntity().displayClientMessage(Component.literal("activeTick"), false))
                            .end(data -> data.getEntity().addDeltaMovement(new Vec3(0, 10, 0)))
                    )
                    .stateChange((data, bool) -> {
                        if (!bool && data.getActiveTimes() == 3) data.disable();
                        else
                            data.getEntity().displayClientMessage(Component.literal("StateChanged: to " + bool + " time " + data.getActiveTimes()), false);
                    })
                    .pop(data -> data.getEntity().displayClientMessage(Component.literal("skill disabled"), false))
    );
}
