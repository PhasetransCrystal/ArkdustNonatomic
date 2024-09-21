package com.phasetranscrystal.nonard.skill;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.phasetranscrystal.nonard.Nonard;
import com.phasetranscrystal.nonard.Registries;
import com.phasetranscrystal.nonard.eventdistribute.DataAttachmentRegistry;
import com.phasetranscrystal.nonard.eventdistribute.EntityEventDistribute;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Math;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SkillData<T extends LivingEntity> {
    public static final Codec<SkillData<? extends LivingEntity>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.fieldOf("inactiveEnergy").forGetter(SkillData::getInactiveEnergy),
            Codec.INT.fieldOf("charge").forGetter(SkillData::getCharge),
            Codec.INT.fieldOf("activeEnergy").forGetter(SkillData::getActiveEnergy),
            Codec.BOOL.fieldOf("active").forGetter(SkillData::isActive),
            Codec.BOOL.fieldOf("enabled").forGetter(SkillData::isEnabled),
            Registries.SKILL.byNameCodec().fieldOf("skill").forGetter(i -> i.skill),
            Codec.INT.fieldOf("activeTimes").forGetter(SkillData::getActiveTimes),
            Codec.unboundedMap(Codec.STRING,Codec.STRING).fieldOf("cacheData").forGetter(SkillData::getCacheData),
            Codec.STRING.listOf().fieldOf("markCleanKeys").forGetter(i -> i.markCleanKeys.stream().toList()),
            Codec.STRING.listOf().fieldOf("markCleanCacheOnce").forGetter(i -> i.markCleanCacheOnce.stream().toList())
    ).apply(instance,SkillData::new));
    public static final Logger LOGGER = LogManager.getLogger("ArkdustNona:Skill/Data");
    public static final ResourceLocation SKILL_BASE_KEY = ResourceLocation.fromNamespaceAndPath(Nonard.MOD_ID, "skill_base");
    public static final ResourceLocation SKILL_INACTIVE_KEY = ResourceLocation.fromNamespaceAndPath(Nonard.MOD_ID, "skill_inactive");
    public static final ResourceLocation SKILL_ACTIVE_KEY = ResourceLocation.fromNamespaceAndPath(Nonard.MOD_ID, "skill_active");

    private int inactiveEnergy;
    private int charge;
    private int activeEnergy = 0;
    private T entity;//NOSAVE
    private boolean active = false;
    private boolean enabled = true;
    public final Skill<T> skill;
    private int activeTimes = 0;
    public final Map<String, String> cacheData;
    private HashSet<String> markCleanKeys = new HashSet<>();
    private HashSet<String> markCleanCacheOnce = new HashSet<>();

    private int delay = 0;//NOSAVE
    private Runnable stageChangeSchedule = () -> {//NOSAVE
    };
    private final Consumer<EntityTickEvent> ticker = event -> tickerHandler();//FINAL

    private final Set<Pair<Holder<Attribute>, ResourceLocation>> attributeCache = new HashSet<>();//NOSAVE


    public SkillData(final Skill<T> skill) {
        this.skill = skill;
        this.inactiveEnergy = skill.initialEnergy;
        this.charge = skill.initialCharge;
        this.cacheData = new HashMap<>();
    }

    protected SkillData(int inactiveEnergy, int charge, int activeEnergy, boolean active, boolean enabled, Skill<?> skill , int activeTimes, Map<String,String> cacheData, List<String> markClean, List<String> markCleanCacheOnce) {
        this.inactiveEnergy = inactiveEnergy;
        this.charge = charge;
        this.activeEnergy = activeEnergy;
        this.active = active;
        this.enabled = enabled;
        this.activeTimes = activeTimes;
        this.skill = (Skill<T>) skill;
        this.cacheData = cacheData;
        this.markCleanKeys.addAll(markClean);
        this.markCleanCacheOnce.addAll(markCleanCacheOnce);
    }

    public boolean tryCastAnd(LivingEntity entity, Consumer<T> consumer) {
        try {
            consumer.accept((T) entity);
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public void bindEntity(T entity) {
        if (this.entity != null) {
            LOGGER.warn("Entity instance (class={}) already exists. Skipped.", entity.getClass());
            return;
        }

        this.entity = entity;
        if (!enabled) return;

        enable();
    }

    public boolean nextStage() {
        if (!enabled || isPassivity()) return false;
        EntityEventDistribute distribute = entity.getData(DataAttachmentRegistry.EVENT_DISTRIBUTE);
        if (!active) {
            if (charge > 0 && skill.judge.apply(this)) {
                charge -= 1;

                distribute.removeMarked(dedicateResourceLocation(SKILL_INACTIVE_KEY));
                skill.inactive.onEnd.accept(this);
                attributeCache.forEach(pair -> entity.getAttribute(pair.getFirst()).removeModifier(pair.getSecond()));
                attributeCache.clear();
                cacheDataRoll();

                active = true;
                activeTimes++;
                stageChangeSchedule = () -> {
                    activeEnergy = skill.activeEnergy;
                    skill.stateChange.accept(this, true);
                    skill.active.onStart.accept(this);
                    if (isInstantComplete()) {
                        nextStage();
                    } else {
                        skill.active.listeners.forEach((clazz, consumer) -> distribute.add(clazz, event -> ((BiConsumer) consumer).accept(event, this), dedicateResourceLocation(SKILL_ACTIVE_KEY)));
                    }
                };
                delay = 5;
                return true;
            }
            return false;
        } else {
            distribute.removeMarked(dedicateResourceLocation(SKILL_ACTIVE_KEY));
            skill.active.onEnd.accept(this);
            cacheDataRoll();
            activeEnergy = 0;

            active = false;
            stageChangeSchedule = () -> {
                skill.stateChange.accept(this, false);
                checkInactive(distribute);
            };
            delay = 10;
            return true;
        }
    }

    public boolean requestEnable(){
        if (entity == null || entity.isRemoved() || enabled) return false;
        enable();
        return true;
    }

    public boolean requestDisable(){
        if (!enabled) return false;
        disable();
        return true;
    }

    private void enable() {
        EntityEventDistribute distribute = entity.getData(DataAttachmentRegistry.EVENT_DISTRIBUTE);
        distribute.add(EntityTickEvent.class, ticker, dedicateResourceLocation(SKILL_BASE_KEY));
        skill.onStart.accept(this);
        skill.listeners.forEach((clazz, consumer) -> distribute.add(clazz, event -> ((BiConsumer) consumer).accept(event, this), dedicateResourceLocation(SKILL_BASE_KEY)));//原则上这应该得是安全的 但愿吧
        if (isPassivity()) return;
        if (isActive()) {
            skill.active.onStart.accept(this);
            if (isInstantComplete()) {
                nextStage();
            } else {
                if (activeEnergy == 0) {
                    skill.active.reachStop.accept(this);
                    if (!isActive()) return;
                }
                skill.active.listeners.forEach((clazz, consumer) -> distribute.add(clazz, event -> ((BiConsumer) consumer).accept(event, this), dedicateResourceLocation(SKILL_ACTIVE_KEY)));
            }
        } else checkInactive(distribute);
    }

    private boolean disable() {
        EntityEventDistribute distribute = entity.getData(DataAttachmentRegistry.EVENT_DISTRIBUTE);
        if (!isPassivity()) {
            if (isActive()) {
                distribute.removeMarked(dedicateResourceLocation(SKILL_ACTIVE_KEY));
                skill.active.onEnd.accept(this);
            } else {
                distribute.removeMarked(dedicateResourceLocation(SKILL_INACTIVE_KEY));
                skill.inactive.onEnd.accept(this);
            }
        }
        distribute.removeMarked(dedicateResourceLocation(SKILL_BASE_KEY));
        skill.onEnd.accept(this);
        attributeCache.forEach(pair -> entity.getAttribute(pair.getFirst()).removeModifier(pair.getSecond()));
        attributeCache.clear();
        enabled = false;
        active = false;
        this.inactiveEnergy = skill.initialEnergy;
        this.charge = skill.initialCharge;
        activeTimes = 0;
        markCleanCacheOnce.clear();
        markCleanKeys.clear();
        cacheData.clear();
        return true;
    }

    private void cacheDataRoll() {
        markCleanKeys.forEach(cacheData::remove);
        markCleanKeys = markCleanCacheOnce;
        markCleanCacheOnce = new HashSet<>();
    }

    //非被动
    private void checkInactive(EntityEventDistribute distribute) {
        skill.inactive.onStart.accept(this);
        if (reachedReady() && skill.maxCharge != 1) {
            skill.inactive.reachReady.accept(this);
        }
        if (isActive()) return;

        if (reachedInactiveEnd()) {
            skill.inactive.reachStop.accept(this);
        }
        if (isActive()) return;

        skill.inactive.listeners.forEach((clazz, consumer) -> distribute.add(clazz, event -> ((BiConsumer) consumer).accept(event, this), dedicateResourceLocation(SKILL_INACTIVE_KEY)));
        if (inactiveEnergy > 0) skill.inactive.energyChanged.accept(this,inactiveEnergy);
        if (charge > 0) skill.inactive.chargeChanged.accept(this,charge);
    }


    //---[状态 State]---

    public boolean reachedReady() {
        return enabled && !active && !isPassivity() && charge > 0;
    }

    public boolean reachedInactiveEnd() {
        return enabled && !active && !isPassivity() && charge >= skill.maxCharge;
    }

    public boolean isPassivity() {
        return skill.inactiveEnergy == 0;
    }

    public boolean isInstantComplete() {
        return skill.activeEnergy == 0;
    }

    private void tickerHandler() {
        if (delay >= 1) {
            if (delay == 1) {
                stageChangeSchedule.run();
                stageChangeSchedule = () -> {
                };
            }
            delay--;
        }
    }

    public boolean chargeEnergy() {
        return chargeEnergy(1) == 1;
    }

    public int chargeEnergy(int amount) {
        if (!enabled) return 0;
        // 如果当前的charge已经达到最大值，或者传入的amount小于等于0，或者技能开启中，则返回0
        if (charge >= skill.maxCharge || amount <= 0 || isActive()) return 0;

        // 计算最大可增加的能量
        int maxEnergy = skill.inactiveEnergy * skill.maxCharge;
        int nowa = skill.inactiveEnergy * charge + inactiveEnergy;

        int consumed = Math.min(maxEnergy - nowa, amount);

        inactiveEnergy += consumed;

        boolean reachReady = false;
        boolean reachStop = false;

        // 计算可以增加的charge数量
        int flag = 0;
        int charge2 = (nowa + consumed) / skill.inactiveEnergy;
        if (charge2 > charge) {
            // 增加charge并更新inactiveEnergy
            if (charge == 0) {
                if (skill.maxCharge != 1) reachReady = true;
                else reachStop = true;
            } else if (charge2 == skill.maxCharge) {
                reachStop = true;
            }
            flag = charge2 - charge;
            inactiveEnergy -= flag * skill.inactiveEnergy;
            charge = charge2;
        }

        skill.inactive.energyChanged.accept(this, amount);
        if (flag != 0) {
            skill.inactive.chargeChanged.accept(this, flag);
        }

        if (reachReady) skill.inactive.reachReady.accept(this);
        if (reachStop) skill.inactive.reachStop.accept(this);

        return consumed; // 如果没有增加charge，仍然返回消耗的总能量点数
    }

    public int releaseEnergy(int amount, boolean allowBreakCharge) {
        if (!enabled) return 0;
        // 计算可提取的最大能量
        int maxAllow = allowBreakCharge ? charge * skill.inactiveEnergy + inactiveEnergy : inactiveEnergy;

        // 如果请求的amount小于等于0，或者当前没有足够的能量，则返回0
        if (amount <= 0 || maxAllow <= 0 || isActive()) return 0;

        // 计算实际可释放的能量
        int totalReleased = Math.min(amount, maxAllow);//>0

        inactiveEnergy -= totalReleased;

        // 如果允许打破charge，则从charge中提取能量
        int flag = 0;
        if (allowBreakCharge) {
            int charge2 = (maxAllow - totalReleased) / skill.inactiveEnergy;
            if (charge2 < charge) {
                // 减少charge
                flag = charge - charge2;
                inactiveEnergy += flag * skill.inactiveEnergy;
                charge = charge2;
            }
        }

        skill.inactive.energyChanged.accept(this, -totalReleased);
        if (flag != 0) {
            skill.inactive.chargeChanged.accept(this, -flag);
        }

        return totalReleased; // 返回实际释放的能量点数
    }


    //---[缓存数据 DataCache]---

    public String getCacheData(String key) {
        return cacheData.get(key);
    }

    public Map<String, String> getCacheData() {
        return cacheData;
    }

    public String putCacheData(String key, String value, boolean markAutoClean, boolean keepToNextStage) {
        if (!enabled || entity == null) return null;
        if (markAutoClean) {
            if (keepToNextStage) markCleanCacheOnce.add(key);
            else markCleanKeys.add(key);
        }
        return cacheData.put(key, value);
    }

    @SuppressWarnings("null")
    public boolean addAutoCleanAttribute(AttributeModifier modifier, Holder<Attribute> type) {
        AttributeInstance instance;
        if (!enabled || entity == null || (instance = entity.getAttribute(type)) == null) return false;

        if (instance.hasModifier(modifier.id())) {
            instance.addOrUpdateTransientModifier(modifier);
            this.attributeCache.add(Pair.of(type, modifier.id()));
        } else {
            instance.addTransientModifier(modifier);
        }
        return true;
    }


    //---[数据获取 Getter]---
    public int getInactiveEnergy() {
        return inactiveEnergy;
    }

    public int getCharge() {
        return charge;
    }

    public T getEntity() {
        return entity;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getActiveTimes() {
        return activeTimes;
    }

    public int getActiveEnergy() {
        return activeEnergy;
    }

    public ResourceLocation dedicateResourceLocation(ResourceLocation origin) {
        ResourceLocation loc = skill.getResourceKey().location();
        return origin.withSuffix("_" + loc.getNamespace() + "_" + loc.getPath());
    }

    //下面的设定不会触发能量变动事件
    public void setInactiveEnergy(int inactiveEnergy) {
        this.inactiveEnergy = Math.clamp(0, skill.inactiveEnergy, inactiveEnergy);
    }

    public void setCharge(int charge) {
        this.charge = Math.clamp(0, skill.maxCharge, charge);
    }

    public void setActiveEnergy(int activeEnergy) {
        if (active) {
            this.activeEnergy = Math.clamp(0, skill.activeEnergy, activeEnergy);
        }
    }
}
