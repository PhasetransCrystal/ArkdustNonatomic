package com.phasetranscrystal.nonard.skill;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.phasetranscrystal.nonard.Nonard;
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
            BehaviorRecord.CODEC.fieldOf("behavior").forGetter(SkillData::getBehavior),
            Codec.BOOL.fieldOf("enabled").forGetter(SkillData::isEnabled),
            Registries.SKILL.byNameCodec().fieldOf("skill").forGetter(i -> i.skill),
            Codec.INT.fieldOf("activeTimes").forGetter(SkillData::getActiveTimes),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("cacheData").forGetter(SkillData::getCacheData),
            Codec.STRING.listOf().fieldOf("markCleanKeys").forGetter(i -> i.markCleanKeys.stream().toList()),
            Codec.STRING.listOf().fieldOf("markCleanCacheOnce").forGetter(i -> i.markCleanCacheOnce.stream().toList())
    ).apply(instance, SkillData::new));
    public static final Logger LOGGER = LogManager.getLogger("BreaBlast:Skill/Data");
    public static final ResourceLocation SKILL_BASE_KEY = ResourceLocation.fromNamespaceAndPath(Nonard.MOD_ID, "skill_base");
    public static final ResourceLocation SKILL_BEHAVIOR_KEY = ResourceLocation.fromNamespaceAndPath(Nonard.MOD_ID, "skill_behavior");

    private boolean enabled = true;
    public final Skill<T> skill;
    public final ResourceLocation skillName;
    private T entity;//NOSAVE

    private int inactiveEnergy;
    private int charge;
    private int activeEnergy = 0;
    private BehaviorRecord behavior;//TODO

    private int activeTimes = 0;

    public final Map<String, String> cacheData = new HashMap<>();
    private HashSet<String> markCleanKeys = new HashSet<>();
    private HashSet<String> markCleanCacheOnce = new HashSet<>();

    private final Set<Pair<Holder<Attribute>, ResourceLocation>> attributeCache = new HashSet<>();//NOSAVE

    private int delay = 0;//NOSAVE
    private boolean occupy = false;
    private Runnable stageChangeSchedule = () -> {//NOSAVE
    };
    private final Consumer<EntityTickEvent.Post> ticker = event -> tickerHandler();//FINAL


    public SkillData(final Skill<T> skill) {
        this.skill = skill;
        this.skillName = skill.getResourceKey().location();
        this.inactiveEnergy = skill.initialEnergy;
        this.charge = skill.initialCharge;
        this.behavior = new BehaviorRecord(skill.initBehavior, skill.initWithActive);
        if (skill.initWithActive) {
            this.activeEnergy = skill.activeEnergy;
        }
    }

    @SuppressWarnings("unchecked")
    protected SkillData(int inactiveEnergy, int charge, int activeEnergy, BehaviorRecord active, boolean enabled, Skill<?> skill, int activeTimes, Map<String, String> cacheData, List<String> markClean, List<String> markCleanCacheOnce) {

        if (active.isActive ? skill.actives.containsKey(active.behaviorName) : skill.inactives.containsKey(active.behaviorName)) {
            this.inactiveEnergy = inactiveEnergy;
            this.charge = charge;
            this.activeEnergy = activeEnergy;
            this.behavior = active;
            this.enabled = enabled;
            this.activeTimes = activeTimes;
            this.skill = (Skill<T>) skill;
            this.skillName = skill.getResourceKey().location();
            this.cacheData.putAll(cacheData);
            this.markCleanKeys.addAll(markClean);
            this.markCleanCacheOnce.addAll(markCleanCacheOnce);
        } else {
            LOGGER.error("Unable to find behavior({}), switched to default status.", active);
            this.skill = (Skill<T>) skill;
            this.skillName = skill.getResourceKey().location();
            this.inactiveEnergy = skill.initialEnergy;
            this.charge = skill.initialCharge;
            this.behavior = new BehaviorRecord(skill.initBehavior, skill.initWithActive);
            if (skill.initWithActive) {
                this.activeEnergy = skill.activeEnergy;
            }
        }
    }

    //---[实体绑定初始化]---

    @SuppressWarnings("unchecked,unused")
    public boolean tryCastAnd(LivingEntity entity, Consumer<T> consumer) {
        try {
            T e = (T) entity;
            if (e == null) return false;
            consumer.accept(e);
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public void bindEntity(T entity) {
        if (this.entity != null && !entity.getUUID().equals(this.entity.getUUID())) {
            LOGGER.warn("Entity instance (class={}) already exists. Skipped.", entity.getClass());
            return;
        }

        this.entity = entity;
        if (!enabled) return;

        enable();
    }

    @SuppressWarnings("all")
    private void enable() {
        enabled = true;

        //技能基础行为初始化
        EntityEventDistribute distribute = entity.getData(DataAttachmentRegistry.EVENT_DISTRIBUTE);
        distribute.add(EntityTickEvent.Post.class, ticker, Skill.NAME, skillName, SKILL_BASE_KEY);
        skill.onStart.accept(this);
        skill.listeners.forEach((clazz, consumer) -> distribute.add(clazz, event -> ((BiConsumer) consumer).accept(event, this), Skill.NAME, skillName, SKILL_BASE_KEY));

        if (isActive()) {
            checkActive(distribute);
        } else if (!isPassivity()) {
            checkInactive(distribute);
        }
    }

    public boolean switchToIfNot(boolean toActive, String behavior) {
        if (this.behavior.isActive != toActive || !this.behavior.behaviorName.equals(behavior)) {
            return switchTo(toActive, behavior);
        }
        return false;
    }


    @SuppressWarnings("all")
    public boolean switchTo(boolean toActive, String behavior) {
        if (!enabled || occupy) return false;
        EntityEventDistribute distribute = entity.getData(DataAttachmentRegistry.EVENT_DISTRIBUTE);
        //从非活跃转换为活跃态 进行判定

        if (!isActive() && toActive) {
            if (charge > 0 && skill.judge.applyAsBoolean(this, behavior)) {
                charge--;
                activeTimes++;
            } else {
                return false;
            }
        }

        distribute.removeMarked(Skill.NAME, skillName, SKILL_BEHAVIOR_KEY);
        if (isActive()) {
            skill.actives.get(behavior).onEnd.accept(this);
        } else {
            skill.inactives.get(behavior).onEnd.accept(this);
        }
        attributeCache.forEach(pair -> entity.getAttribute(pair.getFirst()).removeModifier(pair.getSecond()));
        attributeCache.clear();
        cacheDataRoll();


        var record = new BehaviorRecord(behavior, toActive);
        this.behavior = record;

        if (toActive) {
            activeEnergy = skill.activeEnergy;
            skill.stateChange.accept(this, record);
            stageChangeSchedule = () -> checkActive(distribute);
            delay = skill.actives.get(record.behaviorName).delay;

        } else {
            activeEnergy = 0;
            skill.stateChange.accept(this, record);
            if (!isPassivity()) {
                stageChangeSchedule = () -> checkInactive(distribute);
                delay = skill.inactives.get(record.behaviorName).delay;
            }
        }

        if (delay == 0) {
            stageChangeSchedule.run();
            stageChangeSchedule = () -> {
            };
        } else {
            occupy = true;
        }

        return true;
    }

    public boolean requestEnable() {
        if (entity == null || entity.isRemoved() || enabled) return false;
        enable();
        return true;
    }

    public boolean requestDisable() {
        if (!enabled) return false;
        disable();
        return true;
    }


    private boolean disable() {
        EntityEventDistribute distribute = entity.getData(DataAttachmentRegistry.EVENT_DISTRIBUTE);
        distribute.removeMarked(Skill.NAME, skillName);
        skill.onEnd.accept(this);
        attributeCache.forEach(pair -> entity.getAttribute(pair.getFirst()).removeModifier(pair.getSecond()));
        attributeCache.clear();
        enabled = false;
        behavior = new BehaviorRecord(skill.initBehavior, skill.initWithActive);
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

    private void checkInactive(EntityEventDistribute distribute) {
        BehaviorRecord behavior = this.behavior;
        Inactive<T> inactive = skill.inactives.get(behavior.behaviorName);
        inactive.onStart.accept(this);
        if (reachedReady() && skill.maxCharge != 1) {
            inactive.reachReady.accept(this);
        }
        if (!this.behavior.equals(behavior)) return;

        if (reachedInactiveEnd()) {
            inactive.reachStop.accept(this);
        }
        if (!this.behavior.equals(behavior)) return;

        inactive.listeners.forEach((clazz, consumer) -> distribute.add(clazz, event -> ((BiConsumer) consumer).accept(event, this), Skill.NAME, skillName, SKILL_BEHAVIOR_KEY));
        if (inactiveEnergy > 0) inactive.energyChanged.accept(this, inactiveEnergy);
        if (charge > 0) inactive.chargeChanged.accept(this, charge);
    }

    private void checkActive(EntityEventDistribute distribute) {
        var behavior = this.behavior;
        Active<T> active = skill.actives.get(behavior.behaviorName);
        active.onStart.accept(this);
        if (isInstantComplete() || activeEnergy == 0) {
            active.reachStop.accept(this);
        }
        //如果活动状态没有变化 推入事件
        if (this.behavior.equals(behavior)) {
            active.listeners.forEach((clazz, consumer) -> distribute.add(clazz, event -> ((BiConsumer) consumer).accept(event, this), Skill.NAME, skillName, SKILL_BEHAVIOR_KEY));
        }
    }


    //---[状态 State]---

    public boolean reachedReady() {
        return enabled && !behavior.isActive && !isPassivity() && charge > 0;
    }

    public boolean reachedInactiveEnd() {
        return enabled && !behavior.isActive && !isPassivity() && charge >= skill.maxCharge;
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
                var last = stageChangeSchedule;
                occupy = false;
                stageChangeSchedule.run();
                //防止在技能瞬间完成时新的计划被覆盖
                if (stageChangeSchedule == last) {
                    stageChangeSchedule = () -> {
                    };
                }
            }
            delay--;
        }
    }

    public boolean chargeEnergy() {
        return chargeEnergy(1) == 1;
    }

    //    @SuppressWarnings("all")
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

        Inactive<T> inactive = skill.inactives.get(behavior.behaviorName);

        inactive.energyChanged.accept(this, amount);
        if (flag != 0) {
            inactive.chargeChanged.accept(this, flag);
        }

        if (reachReady) inactive.reachReady.accept(this);
        if (reachStop) inactive.reachStop.accept(this);

        return consumed; // 如果没有增加charge，仍然返回消耗的总能量点数
    }

    public int releaseEnergy(int amount, boolean allowBreakCharge) {
        if (!enabled || isActive()) return 0;
        // 计算可提取的最大能量
        int maxAllow = allowBreakCharge ? charge * skill.inactiveEnergy + inactiveEnergy : inactiveEnergy;

        // 如果请求的amount小于等于0，或者当前没有足够的能量，则返回0
        if (amount <= 0 || maxAllow <= 0) return 0;

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

        Inactive<T> inactive = skill.inactives.get(behavior.behaviorName);

        inactive.energyChanged.accept(this, -totalReleased);
        if (flag != 0) {
            inactive.chargeChanged.accept(this, -flag);
        }

        return totalReleased; // 返回实际释放的能量点数
    }

    public int modifyActiveEnergy(int amount) {
        if (amount == 0 || !isActive()) return 0;
        if (amount > 0) {
            int allow = skill.activeEnergy - activeEnergy;
            if (allow <= 0) return 0;
            else {
                allow = Math.min(allow, amount);
                this.activeEnergy += allow;
                return allow;
            }
        } else if (activeEnergy <= 0) {
            return 0;
        } else {
            int allow = Math.min(activeEnergy, -amount);
            this.activeEnergy -= allow;
            if (this.activeEnergy <= 0) skill.actives.get(behavior.behaviorName).reachStop.accept(this);
            return -allow;
        }
    }


    //---[缓存数据 DataCache]---

    public String getCacheData(String key) {
        return cacheData.get(key);
    }

    public int getCacheDataAsInt(String key, int fallback, boolean logIfFailed) {
        String result = cacheData.get(key);
        if (result != null) {
            try {
                return Integer.parseInt(result);
            } catch (Exception ignored) {
            }
        }
        if (logIfFailed) {
            LOGGER.error("Unable to parse cache data:{key={}, value={}} to int. In skill={}, stage={}. Fallback used.",
                    key, result == null ? "null" : ("\"" + result + "\""), behavior.behaviorName, behavior
            );
        }
        return fallback;
    }

    public double getCacheDataAsDouble(String key, int fallback, boolean logIfFailed) {
        String result = cacheData.get(key);
        if (result != null) {
            try {
                return Double.parseDouble(result);
            } catch (Exception ignored) {
            }
        }
        if (logIfFailed) {
            LOGGER.error("Unable to parse cache data:{key={}, value={}} to double. In skill={}, stage={}. Fallback used.",
                    key, result == null ? "null" : ("\"" + result + "\""), behavior.behaviorName, behavior
            );
        }
        return fallback;
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

        instance.addOrUpdateTransientModifier(modifier);
        this.attributeCache.add(Pair.of(type, modifier.id()));
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
        return behavior.isActive;
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

    public BehaviorRecord getBehavior() {
        return behavior;
    }

    public void cacheOnce(String key) {
        markCleanCacheOnce.add(key);
        markCleanKeys.remove(key);
    }

    //下面的设定不会触发能量变动事件
    public void setInactiveEnergy(int inactiveEnergy) {
        this.inactiveEnergy = Math.clamp(0, skill.inactiveEnergy, inactiveEnergy);
    }

    public void setCharge(int charge) {
        this.charge = Math.clamp(0, skill.maxCharge, charge);
    }

    public void setActiveEnergy(int activeEnergy) {
        if (isActive()) {
            this.activeEnergy = Math.clamp(0, skill.activeEnergy, activeEnergy);
        }
    }

    public record BehaviorRecord(String behaviorName, boolean isActive) {
        public static final Codec<BehaviorRecord> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("name").forGetter(BehaviorRecord::behaviorName),
                Codec.BOOL.fieldOf("active").forGetter(BehaviorRecord::isActive)
        ).apply(instance, BehaviorRecord::new));
    }
}
