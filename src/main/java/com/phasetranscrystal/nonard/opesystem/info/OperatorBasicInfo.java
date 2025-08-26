package com.phasetranscrystal.nonard.opesystem.info;

import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Range;

import java.util.List;

/**
 * @param gender              性别
 * @param combatExp           战斗经验/年
 * @param comingFrom          来自地区
 * @param birthday            生日<月,日>
 * @param camp                阵营
 * @param race                种族
 * @param height              身高/cm
 * @param infected            是否为感染者
 * @param tags                干员定位标签
 * @param physicalStrength    物理强度
 * @param mobility            战场机动
 * @param endurance           生理耐受
 * @param tacticalAcumen      战术规划
 * @param combatSkill         战斗技巧
 * @param oriartsAssimilation 源石技艺适应性
 * @param cellOriAssim        体细胞源石融合率/%
 * @param bloodOricrysDensity 血液源石结晶密度/ u/L
 */
public record OperatorBasicInfo(Gender gender,
                                float combatExp, ResourceLocation comingFrom, IntIntPair birthday,
                                ResourceLocation camp, ResourceLocation race, int height, boolean infected,
                                List<ResourceLocation> tags,
                                Grade physicalStrength,
                                Grade mobility,
                                Grade endurance,
                                Grade tacticalAcumen,
                                Grade combatSkill,
                                Grade oriartsAssimilation,
                                @Range(from = 0, to = 100) int cellOriAssim,
                                @Range(from = 0, to = Long.MAX_VALUE) float bloodOricrysDensity) {


    public enum Gender {
        MALE,
        FEMALE,
        MECHANICAL
    }

    public enum Grade {
        EXCELLENT(5),
        GREAT(4),
        STANDARD(3),
        AVERAGE(2),
        DEFECT(1),
        UNKNOWN(-1);

        public final int index;

        Grade(int index) {
            this.index = index;
        }

        public boolean isGreaterOrEqual(Grade other) {
            return index != -1 && index >= other.index;
        }

        public boolean isWorseOrEqual(Grade other) {
            return index != -1 && index <= other.index;
        }
    }
}
