package com.phasetranscrystal.nonard.migrate.nona_quench.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.phasetranscrystal.nonard.migrate.nona_quench.BreaQuench;

public class AttributeRegistry {

    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, BreaQuench.MODID);

    // 常规属性
    public static final DeferredHolder<Attribute, Attribute> CRITICAL_CHANCE = regs("critical_chance", 0, 0, 1),      // 暴击率
            CRITICAL_HIT_FACTOR = regs("critical_hit", 2),                      // 暴击伤害倍率
            WEAKNESS_HIT_FACTOR = regs("weakness_hit", 1.5),                    // 弱点伤害倍率
            RANGE_DAMAGE = regs("range_damage", 1),                             // 远程伤害
            MAGIC_DAMAGE = regs("magic_damage", 1),                             // 魔法伤害
            FIRE_FACTOR = regs("fire_factor", 1),                               // 火力值
            HEALING_FACTOR = regs("healing_factor", 1),                         // 受治疗恢复倍率
            TIME_RECOVERY = regs("time_recovery", 0);                           // 自然恢复(每秒)

    // 枪械
    public static final DeferredHolder<Attribute, Attribute> SHOOTING_SPEED = regs("shooting_speed", 0, 0, 1200, "range_weapon"),                     // 射击速度(次/分钟)
            RELOADING_TIME = regs("reloading_time", 0, "range_weapon"),                                         // 换弹速度(秒)
            DISPERSION_SPEED = regs("dispersion_speed", 0.02, "range_weapon"),                                  // 散布初速度
                                                                                                                // 为子弹添加垂直于发射路径的初速度模拟散布
                                                                                                                // TODO
                                                                                                                // 数值待定
            STABILITY = regs("stability", 0.5, "range_weapon"),                                                 // 稳定性
                                                                                                                // TODO
                                                                                                                // 表现效果待定
            SQUATTING_DISPERSION = regs("squatting_dispersion_factor", 0.5, 0, 1, "range_weapon"),     // 蹲姿散布因子
                                                                                                       // 蹲下时散布初速度将乘以这个值
            STABILITY_FACTOR = regs("stability_factor", 0.5, 0, 1, "range_weapon"),                    // 蹲姿稳定因子
                                                                                                       // 蹲下时稳定性将乘以这个值
            ATTENUATION_START = regs("attenuation_start", 0, "range_weapon"),                                    // 衰减起始距离
                                                                                                                 // 在这一距离后子弹将开始衰减
            ATTENUATION_RATE = regs("attenuation_rate", 0, "range_weapon"),                                      // 衰减速率
                                                                                                                 // 每单位距离伤害衰减的百分比
            AIMING_TIME = regs("aiming_time", 0, "range_weapon"),                                                // 瞄准/开镜时间(秒)
            MAGAZINE_SIZE = regs("magazine_size", 1, 1, Integer.MAX_VALUE, "range_weapon");                   // 弹夹大小

    public static DeferredHolder<Attribute, Attribute> regs(String id, double defaultValue, double min, double max) {
        return REGISTRY.register(id, () -> new RangedAttribute("brea.quench.attribute." + id + ".name", defaultValue, min, max).setSyncable(true));
    }

    public static DeferredHolder<Attribute, Attribute> regs(String id, double defaultValue, double min, double max, String prefix) {
        return REGISTRY.register(id, () -> new RangedAttribute("brea.quench.attribute." + prefix + "." + id + ".name", defaultValue, min, max).setSyncable(true));
    }

    public static DeferredHolder<Attribute, Attribute> regs(String id, double defaultValue) {
        return regs(id, defaultValue, 0, Double.MAX_VALUE);
    }

    public static DeferredHolder<Attribute, Attribute> regs(String id, double defaultValue, String prefix) {
        return regs(id, defaultValue, 0, Double.MAX_VALUE, prefix);
    }
}
