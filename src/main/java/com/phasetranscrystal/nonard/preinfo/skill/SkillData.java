package com.phasetranscrystal.nonard.preinfo.skill;

import com.phasetranscrystal.nonard.Nonard;
import com.phasetranscrystal.nonard.eventdistribute.DataAttachmentRegistry;
import com.phasetranscrystal.nonard.eventdistribute.EntityEventDistribute;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Math;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SkillData<T extends LivingEntity> {
    public static final Logger LOGGER = LogManager.getLogger("ArkdustNona:Skill/Data");
    public static final ResourceLocation SKILL_BASE_KEY = ResourceLocation.fromNamespaceAndPath(Nonard.MOD_ID, "skill_base");
    public static final ResourceLocation SKILL_INACTIVE_KEY = ResourceLocation.fromNamespaceAndPath(Nonard.MOD_ID, "skill_inactive");
    public static final ResourceLocation SKILL_ACTIVE_KEY = ResourceLocation.fromNamespaceAndPath(Nonard.MOD_ID, "skill_active");

    private int inactiveEnergy;
    private int charge;
    private int activeEnergy = 0;
    private T entity;
    private boolean active = false;
    private boolean enabled = true;
    private int activeTimes = 0;
    public final Skill<T> skill;
    public final HashMap<String, String> cacheData = new HashMap<>();
    private HashSet<String> markCleanKeys = new HashSet<>();
    private HashSet<String> markCleanCacheOnce = new HashSet<>();

    private int delay = 0;
    private Runnable stageChangeSchedule = () -> {
    };
    private final Consumer<EntityTickEvent> ticker = event -> tickerHandler();

    public SkillData(final Skill<T> skill) {
        this.skill = skill;
        this.inactiveEnergy = skill.initialEnergy;
        this.charge = skill.initialCharge;
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

        EntityEventDistribute distribute = entity.getData(DataAttachmentRegistry.EVENT_DISPATCHER);
        distribute.add(EntityTickEvent.class, ticker);
        skill.onStart.accept(this);
        skill.listeners.forEach((clazz, consumer) -> distribute.add(clazz, event -> ((BiConsumer) consumer).accept(event, this), SKILL_BASE_KEY));//原则上这应该得是安全的 但愿吧
        if (isActive()) {
            skill.active.onStart.accept(this);
            if (isInstantComplete()) {
                nextStage();
            } else {
                if (activeEnergy == 0) {
                    skill.active.reachStop.accept(this);
                    if (!isActive()) return;
                }
                skill.active.listeners.forEach((clazz, consumer) -> distribute.add(clazz, event -> ((BiConsumer) consumer).accept(event, this), SKILL_ACTIVE_KEY));
            }
        } else if (!isPassivity()) {
            checkInactive(distribute);
        }
    }

    public boolean nextStage() {
        EntityEventDistribute distribute = entity.getData(DataAttachmentRegistry.EVENT_DISPATCHER);
        if (!active) {
            if (charge > 0 && skill.judge.apply(this)) {
                charge -= 1;

                distribute.removeMarked(SKILL_INACTIVE_KEY);
                skill.inactive.onEnd.accept(this);
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
                        skill.active.listeners.forEach((clazz, consumer) -> distribute.add(clazz, event -> ((BiConsumer) consumer).accept(event, this), SKILL_ACTIVE_KEY));
                    }
                };
                delay = 5;
                return true;
            }
            return false;
        } else {
            distribute.removeMarked(SKILL_ACTIVE_KEY);
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

    private void cacheDataRoll() {
        markCleanKeys.forEach(cacheData::remove);
        markCleanKeys = markCleanCacheOnce;
        markCleanCacheOnce = new HashSet<>();
    }

    private void checkInactive(EntityEventDistribute distribute) {
        skill.inactive.onStart.accept(this);
        if (reachedReady() && skill.maxCharge != 1) {
            skill.inactive.reachReady.accept(this);
        }
        if (!isActive() && reachedInactiveEnd()) {
            skill.inactive.reachStop.accept(this);
        }
        if (!isActive()) {
            skill.inactive.listeners.forEach((clazz, consumer) -> distribute.add(clazz, event -> ((BiConsumer) consumer).accept(event, this), SKILL_INACTIVE_KEY));
        }
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
        // 如果当前的charge已经达到最大值，或者传入的amount小于等于0，或者技能开启中，则返回0
        if (charge >= skill.maxCharge || amount <= 0 || isActive()) return 0;

        // 计算最大可增加的能量
        int maxEnergy = skill.inactiveEnergy * skill.maxCharge;
        int nowa = skill.inactiveEnergy * charge + inactiveEnergy;

        int consumed = Math.min(maxEnergy - nowa, amount);

        inactiveEnergy += consumed;

        // 计算可以增加的charge数量
        boolean flag = false;
        int charge2 = (nowa + consumed) / skill.inactiveEnergy;
        if (charge2 > charge) {
            // 增加charge并更新inactiveEnergy
            inactiveEnergy -= (charge2 - charge) * skill.inactiveEnergy;
            charge = charge2;
            flag = true;
        }

        skill.inactive.energyChanged.accept(this);
        if (flag) {
            skill.inactive.chargeChanged.accept(this);
        }

        return consumed; // 如果没有增加charge，仍然返回消耗的总能量点数
    }

    public int releaseEnergy(int amount, boolean allowBreakCharge) {
        // 计算可提取的最大能量
        int maxAllow = allowBreakCharge ? charge * skill.inactiveEnergy + inactiveEnergy : inactiveEnergy;

        // 如果请求的amount小于等于0，或者当前没有足够的能量，则返回0
        if (amount <= 0 || maxAllow <= 0 || isActive()) return 0;

        // 计算实际可释放的能量
        int totalReleased = Math.min(amount, maxAllow);//>0

        inactiveEnergy -= totalReleased;

        // 如果允许打破charge，则从charge中提取能量
        boolean flag = false;
        if (allowBreakCharge) {
            int charge2 = (maxAllow - totalReleased) / skill.inactiveEnergy;
            if (charge2 < charge) {
                // 减少charge
                inactiveEnergy += (charge - charge2) * skill.inactiveEnergy;
                charge = charge2;
                flag = true;
            }
        }

        skill.inactive.energyChanged.accept(this);
        if (flag) {
            skill.inactive.chargeChanged.accept(this);
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
        if (markAutoClean) {
            if (keepToNextStage) markCleanCacheOnce.add(key);
            else markCleanKeys.add(key);
        }
        return cacheData.put(key, value);
    }


    //---[数据获取 Getter]---
    public int getInactiveEnergy() {
        return inactiveEnergy;
    }

    public int getCharge() {
        return charge;
    }

    public LivingEntity getEntity() {
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
