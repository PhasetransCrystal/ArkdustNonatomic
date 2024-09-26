package com.phasetranscrystal.nonard.eventdistribute;

import com.phasetranscrystal.nonard.Nonard;
import com.phasetranscrystal.nonard.event.KeyInputEvent;
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

import java.util.function.Consumer;

@EventBusSubscriber(modid = Nonard.MOD_ID)
public class EventConsumer {
    public static final Consumer<EntityEvent> consumer = event -> event.getEntity().getExistingData(DataAttachmentRegistry.EVENT_DISTRIBUTE).ifPresent(d -> d.post(event));

    public static void bootstrapConsumer() {
//        addListener(EntityJoinLevelEvent.class);
        //todo 需要添加一种方法用来增加额外的事件，而不用修改这个类
        addListener(KeyInputEvent.class);

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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void init(EntityJoinLevelEvent event) {
        NeoForge.EVENT_BUS.post(new GatherEntityDistributeEvent(event.getEntity()));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void postAttackIncome(LivingIncomingDamageEvent event) {
        boolean cancelFlag = false;
        if (event.getSource().getEntity() != null)
            cancelFlag = NeoForge.EVENT_BUS.post(new EntityAttackEvent.Income(event.getSource().getEntity(), event, false)).isCanceled();
        if (!event.getSource().isDirect() && event.getSource().getDirectEntity() != null)
            cancelFlag = NeoForge.EVENT_BUS.post(new EntityAttackEvent.Income(event.getSource().getDirectEntity(), event, true)).isCanceled() || cancelFlag;

        event.setCanceled(cancelFlag);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void postAttackPre(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() != null) {
            NeoForge.EVENT_BUS.post(new EntityAttackEvent.Pre(event.getSource().getEntity(), event, false));
        }
        if(!event.getSource().isDirect() && event.getSource().getDirectEntity() != null) {
            NeoForge.EVENT_BUS.post(new EntityAttackEvent.Pre(event.getSource().getDirectEntity(), event, true));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void postAttackPost(LivingDamageEvent.Post event) {
        if (event.getSource().getEntity() != null) {
            NeoForge.EVENT_BUS.post(new EntityAttackEvent.Post(event.getSource().getEntity(), event, false));
        }
        if(!event.getSource().isDirect() && event.getSource().getDirectEntity() != null) {
            NeoForge.EVENT_BUS.post(new EntityAttackEvent.Post(event.getSource().getDirectEntity(), event, true));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() != null) {
            NeoForge.EVENT_BUS.post(new EntityKillEvent.Pre(event.getSource().getEntity(), event, false));
        }
        if(!event.getSource().isDirect() && event.getSource().getDirectEntity() != null) {
            NeoForge.EVENT_BUS.post(new EntityKillEvent.Pre(event.getSource().getDirectEntity(), event, true));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void postKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() != null) {
            NeoForge.EVENT_BUS.post(new EntityKillEvent.Post(event.getSource().getEntity(), event, false));
        }
        if(!event.getSource().isDirect() && event.getSource().getDirectEntity() != null) {
            NeoForge.EVENT_BUS.post(new EntityKillEvent.Post(event.getSource().getDirectEntity(), event, true));
        }
    }

    public abstract static class EntityAttackEvent extends EntityEvent {
        public final boolean isInBetweenEntity;

        public EntityAttackEvent(Entity entity, boolean isInBetweenEntity) {
            super(entity);
            this.isInBetweenEntity = isInBetweenEntity;
        }

        public static class Income extends EntityAttackEvent implements ICancellableEvent {
            public final LivingIncomingDamageEvent origin;

            public Income(Entity entity, LivingIncomingDamageEvent event, boolean isInBetweenEntity) {
                super(entity, isInBetweenEntity);
                this.origin = event;
            }
        }

        public static class Pre extends EntityAttackEvent {
            public final LivingDamageEvent.Pre origin;

            public Pre(Entity entity, LivingDamageEvent.Pre event, boolean isInBetweenEntity) {
                super(entity, isInBetweenEntity);
                this.origin = event;
            }
        }

        public static class Post extends EntityAttackEvent {
            public final LivingDamageEvent.Post origin;

            public Post(Entity entity, LivingDamageEvent.Post event, boolean isInBetweenEntity) {
                super(entity, isInBetweenEntity);
                this.origin = event;
            }
        }
    }

    public abstract static class EntityKillEvent extends EntityEvent {
        public final LivingDeathEvent origin;
        public final boolean isInBetweenEntity;

        public EntityKillEvent(Entity entity, LivingDeathEvent origin, boolean isInBetweenEntity) {
            super(entity);
            this.origin = origin;
            this.isInBetweenEntity = isInBetweenEntity;
        }

        public static class Pre extends EntityKillEvent implements ICancellableEvent {
            public Pre(Entity entity, LivingDeathEvent origin, boolean isInBetweenEntity) {
                super(entity, origin, isInBetweenEntity);
            }
        }

        public static class Post extends EntityKillEvent {
            public Post(Entity entity, LivingDeathEvent origin, boolean isInBetweenEntity) {
                super(entity, origin, isInBetweenEntity);
            }
        }
    }

    public static class GatherEntityDistributeEvent extends EntityEvent {
        public GatherEntityDistributeEvent(Entity entity) {
            super(entity);
        }
    }
}
