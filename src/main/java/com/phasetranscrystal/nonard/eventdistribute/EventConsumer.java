package com.phasetranscrystal.nonard.eventdistribute;

import com.phasetranscrystal.nonard.Nonard;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Objects;
import java.util.function.Consumer;

@EventBusSubscriber(modid = Nonard.MOD_ID)
public class EventConsumer {
    public static final Consumer<? extends EntityEvent> consumer = event -> event.getEntity().getData(DataAttachmentRegistry.EVENT_DISTRIBUTE).post(event);

    public static void bootstrapConsumer() {
        addListener(EntityJoinLevelEvent.class);
        addListener(EntityTickEvent.Post.class);

        addListener(LivingIncomingDamageEvent.class);
        addListener(LivingDamageEvent.Pre.class);
        addListener(LivingDamageEvent.Post.class);
        addListener(EntityAttackEvent.Income.class);
        addListener(EntityAttackEvent.Pre.class);
        addListener(EntityAttackEvent.Post.class);
        addListener(LivingDeathEvent.class);
        addListener(EntityKillEvent.Pre.class);
        addListener(EntityKillEvent.Post.class);

        addListener(ItemTossEvent.class);
        addListener(ItemExpireEvent.class);
        addListener(AnimalTameEvent.class);
        addListener(ArmorHurtEvent.class);
        addListener(LivingChangeTargetEvent.class);
        addListener(LivingDestroyBlockEvent.class);
        addListener(LivingEntityUseItemEvent.Start.class);
        addListener(LivingEntityUseItemEvent.Stop.class);
        addListener(LivingEntityUseItemEvent.Finish.class);
        addListener(LivingEquipmentChangeEvent.class);
        addListener(LivingGetProjectileEvent.class);
        addListener(LivingHealEvent.class);
        addListener(MobEffectEvent.Applicable.class);
        addListener(MobEffectEvent.Added.class);
        addListener(MobEffectEvent.Expired.class);
        addListener(MobEffectEvent.Remove.class);
        addListener(AttackEntityEvent.class);
        addListener(CanPlayerSleepEvent.class);
        addListener(CanContinueSleepingEvent.class);
        addListener(CriticalHitEvent.class);
        NeoForge.EVENT_BUS.addListener(ItemEntityPickupEvent.Pre.class, e -> e.getPlayer().getData(DataAttachmentRegistry.EVENT_DISTRIBUTE).post(e));
        NeoForge.EVENT_BUS.addListener(ItemEntityPickupEvent.Post.class, e -> e.getPlayer().getData(DataAttachmentRegistry.EVENT_DISTRIBUTE).post(e));
        addListener(PlayerEvent.BreakSpeed.class);
        addListener(PlayerEvent.HarvestCheck.class);
        addListener(PlayerEvent.Clone.class);
        addListener(PlayerEvent.PlayerLoggedInEvent.class);
        addListener(PlayerEvent.PlayerLoggedOutEvent.class);
        addListener(PlayerInteractEvent.EntityInteractSpecific.class);
        addListener(PlayerInteractEvent.LeftClickBlock.class);
        addListener(PlayerInteractEvent.LeftClickEmpty.class);
        addListener(PlayerInteractEvent.RightClickBlock.class);
        addListener(PlayerInteractEvent.RightClickEmpty.class);
        addListener(PlayerInteractEvent.RightClickItem.class);
        addListener(PlayerWakeUpEvent.class);
        addListener(PlayerXpEvent.LevelChange.class);
        addListener(PlayerXpEvent.PickupXp.class);
        addListener(PlayerXpEvent.XpChange.class);
        NeoForge.EVENT_BUS.addListener(UseItemOnBlockEvent.class, e -> {
            if (e.getPlayer() != null)
                e.getPlayer().getData(DataAttachmentRegistry.EVENT_DISTRIBUTE).post(e);
        });
        addListener(EntityInvulnerabilityCheckEvent.class);
        addListener(EntityMountEvent.class);
        addListener(EntityStruckByLightningEvent.class);
        addListener(EntityTeleportEvent.class);
        addListener(ProjectileImpactEvent.class);
    }

    public static <T extends EntityEvent> void addListener(Class<T> eventType) {
        NeoForge.EVENT_BUS.addListener(eventType, (Consumer<T>) consumer);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void init(EntityJoinLevelEvent event) {
        event.getEntity().getData(DataAttachmentRegistry.EVENT_DISTRIBUTE).init(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void postAttackIncome(LivingIncomingDamageEvent event) {
        Entity entity = event.getSource().getEntity();
        if (entity != null && NeoForge.EVENT_BUS.post(new EntityAttackEvent.Income(entity, event)).isCanceled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void postAttackPre(LivingDamageEvent.Pre event) {
        Entity entity = event.getSource().getEntity();
        if (entity != null) {
            NeoForge.EVENT_BUS.post(new EntityAttackEvent.Pre(entity, event));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void postAttackPost(LivingDamageEvent.Post event) {
        Entity entity = event.getSource().getEntity();
        if (entity != null) {
            NeoForge.EVENT_BUS.post(new EntityAttackEvent.Post(entity, event));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preKill(LivingDeathEvent event) {
        Entity entity = event.getSource().getEntity();
        if (entity != null && NeoForge.EVENT_BUS.post(new EntityKillEvent.Pre(entity, event)).isCanceled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void postKill(LivingDeathEvent event) {
        Entity entity = event.getSource().getEntity();
        if (entity != null) {
            NeoForge.EVENT_BUS.post(new EntityKillEvent.Post(entity, event));
        }
    }

    public abstract static class EntityAttackEvent extends EntityEvent {
        public EntityAttackEvent(Entity entity) {
            super(entity);
        }

        public static class Income extends EntityAttackEvent implements ICancellableEvent {
            public final LivingIncomingDamageEvent origin;

            public Income(Entity entity, LivingIncomingDamageEvent event) {
                super(entity);
                this.origin = event;
            }
        }

        public static class Pre extends EntityAttackEvent {
            public final LivingDamageEvent.Pre origin;

            public Pre(Entity entity, LivingDamageEvent.Pre event) {
                super(entity);
                this.origin = event;
            }
        }

        public static class Post extends EntityAttackEvent {
            public final LivingDamageEvent.Post origin;

            public Post(Entity entity, LivingDamageEvent.Post event) {
                super(entity);
                this.origin = event;
            }
        }
    }

    public abstract static class EntityKillEvent extends EntityEvent {
        public final LivingDeathEvent origin;

        public EntityKillEvent(Entity entity, LivingDeathEvent origin) {
            super(entity);
            this.origin = origin;
        }

        public static class Pre extends EntityKillEvent implements ICancellableEvent {
            public Pre(Entity entity, LivingDeathEvent origin) {
                super(entity, origin);
            }
        }

        public static class Post extends EntityKillEvent {
            public Post(Entity entity, LivingDeathEvent origin) {
                super(entity, origin);
            }
        }
    }
}
