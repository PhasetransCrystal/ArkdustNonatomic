package com.phasetranscrystal.nonard.opesystem.skill;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

import com.phasetranscrystal.nonard.ArkdustNonatomic;

public record SkillFlags(ResourceLocation key, Object... vars) {

    public String i18nKey() {
        return "arkdust_nona.skill_flags" + key.getNamespace() + "." + key.getPath();
    }

    public String flagNameI18nKey() {
        return i18nKey() + ".name";
    }

    public String flagExplainI18nKey() {
        return i18nKey() + ".explain";
    }

    public String flagNameI18n() {
        return I18n.get(flagNameI18nKey(), vars);
    }

    public String flagExplainI18n() {
        return I18n.get(flagExplainI18nKey(), vars);
    }

    public static final ResourceLocation AUTO_RECOVER = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "auto_recover");// 自动恢复
    public static final ResourceLocation ATTACK_RECOVER = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "attack_recover");// 攻击恢复
    public static final ResourceLocation HURT_RECOVER = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "hurt_recover");// 受击恢复
    public static final ResourceLocation CHARGEABLE = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "chargeable");// 可充能n次

    public static final ResourceLocation AUTO_ACTIVATE = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "auto_activate");// 自动激活
    public static final ResourceLocation MANUAL_ACTIVATE = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "manual_activate");// 手动激活

    public static final ResourceLocation INFINITY = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "infinity");// 持续无限长时间
    public static final ResourceLocation INFINITY_4_N_TIME = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "infinity_4_n_time");// 在n次激活后持续无限长时间
    public static final ResourceLocation COUNTING = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "counting");// 技能激活后计数
    public static final ResourceLocation COST_ALL_CHARGE = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "cost_all_charge");// 消耗所有充能
    public static final ResourceLocation COST_LEAST_N_CHARGE = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "cost_least_n_charge");// 消耗至少n层充能
    public static final ResourceLocation COST_MOST_N_CHARGE = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "cost_most_n_charge");// 消耗至多n层充能

    public static final ResourceLocation PASSIVE = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "passive");// 被动
    public static final ResourceLocation STOPPABLE = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "stoppable");// 可中途暂停
    public static final ResourceLocation STATE_CHANGE = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "state_change");// 形态切换
    public static final ResourceLocation MULTI_STAGE = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "multi_stage");// 多阶段
    public static final ResourceLocation IMMED_FINISH = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "immed_finish");// 瞬间完成

    public static final ResourceLocation ENERGY_RECYCLE = ResourceLocation.fromNamespaceAndPath(ArkdustNonatomic.MODID, "energy_recycle");// 回收部分能量
}
