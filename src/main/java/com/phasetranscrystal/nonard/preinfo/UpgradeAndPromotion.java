package com.phasetranscrystal.nonard.preinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.phasetranscrystal.nonatomic.core.OperatorInfo;
import org.joml.Math;

/**
 * 该类管理角色的等级和晋升系统，
 * 跟踪经验值并支持等级提升。
 */
public class UpgradeAndPromotion extends OperatorInfo {
    public static final MapCodec<UpgradeAndPromotion> CODEC = RecordCodecBuilder.mapCodec(c -> c.group(
            Codec.INT.xmap(RareLevel::getForStar, RareLevel::getStar).fieldOf("star").forGetter(UpgradeAndPromotion::getRareLevel),
            Codec.INT.fieldOf("level").forGetter(UpgradeAndPromotion::getLevel),
            Codec.INT.fieldOf("promotion").forGetter(UpgradeAndPromotion::getPromotion),
            Codec.INT.fieldOf("savedExp").forGetter(UpgradeAndPromotion::getSavedExp)
    ).apply(c, UpgradeAndPromotion::new));
    public static final int[][] EXP_DATA = {
            {100, 117, 134, 151, 168, 185, 202, 219, 236, 253, 270, 287, 304, 321, 338, 355, 372, 389, 406, 423, 440, 457, 474, 491, 508, 525, 542, 559, 574, 589, 605, 621, 637, 653, 669, 685, 701, 716, 724, 739, 749, 759, 770, 783, 804, 820, 836, 852, 888},
            {120, 172, 224, 276, 328, 380, 432, 484, 536, 588, 640, 692, 744, 796, 848, 900, 952, 1004, 1056, 1108, 1160, 1212, 1264, 1316, 1368, 1420, 1472, 1524, 1576, 1628, 1706, 1784, 1862, 1940, 2018, 2096, 2174, 2252, 2330, 2408, 2584, 2760, 2936, 3112, 3288, 3464, 3640, 3816, 3992, 4168, 4344, 4520, 4696, 4890, 5326, 6019, 6312, 6505, 6838, 7391, 7657, 7823, 8089, 8355, 8621, 8887, 9153, 9419, 9605, 9951, 10448, 10945, 11442, 11939, 12436, 12933, 13430, 13927, 14549},
            {191, 303, 415, 527, 639, 751, 863, 975, 1087, 1199, 1311, 1423, 1535, 1647, 1759, 1871, 1983, 2095, 2207, 2319, 2431, 2543, 2655, 2767, 2879, 2991, 3103, 3215, 3327, 3439, 3602, 3765, 3928, 4091, 4254, 4417, 4580, 4743, 4906, 5069, 5232, 5395, 5558, 5721, 5884, 6047, 6210, 6373, 6536, 6699, 6902, 7105, 7308, 7511, 7714, 7917, 8120, 8323, 8526, 8729, 9163, 9597, 10031, 10465, 10899, 11333, 11767, 12201, 12729, 13069, 13747, 14425, 15103, 15781, 16459, 17137, 17815, 18493, 19171, 19849, 21105, 22361, 23617, 24873, 26129, 27385, 28641, 29897, 31143}
    };

    public final RareLevel rareLevel;
    private int level = 1;
    private int promotion = 0;
    private int savedExp = 0;

    /**
     * 使用指定的稀有等级构造 UpgradeAndPromotion 实例。
     *
     * @param rareLevel 角色的稀有等级
     */
    public UpgradeAndPromotion(RareLevel rareLevel) {
        this.rareLevel = rareLevel;
    }

    private UpgradeAndPromotion(RareLevel rareLevel, int level, int promotion, int savedExp) {
        this.rareLevel = rareLevel;
        this.level = level;
        this.promotion = promotion;
        this.savedExp = savedExp;
    }

    public RareLevel getRareLevel() {
        return rareLevel;
    }

    public int getLevel() {
        return level;
    }

    public int getPromotion() {
        return promotion;
    }

    public int getSavedExp() {
        return savedExp;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, Math.min(rareLevel.maxLevel(promotion), level));
    }

    public void setPromotion(int promotion) {
        this.promotion = Math.max(0, Math.min(rareLevel.maxPromotionLevel(), promotion));
    }

    /**
     * 增加角色的等级，按指定增量增加。
     *
     * @param increment 增加的等级
     * @return 如果成功增加等级则返回 true，如果超出当前阶段最大等级限制则返回 false
     */
    public boolean addLevel(int increment) {
        int newLevel = level + increment;
        if (newLevel > rareLevel.maxLevel(promotion)) {
            return false; // 等级超出当前晋升阶段的最大限制
        }
        level = newLevel;
        return true;
    }

    /**
     * 向角色添加经验值并处理等级提升。
     *
     * @param value 添加的经验值
     * @return 超出最大等级要求的经验值
     */
    public int addExp(int value) {
        savedExp += value;
        int overflow = 0;

        while (savedExp >= getRequiredExpForLevel(level)) {
            int requiredExp = getRequiredExpForLevel(level);
            if (savedExp < requiredExp) break;

            savedExp -= requiredExp; // 扣除所需经验
            level++; // 升级

            if (level > rareLevel.maxLevel(promotion)) {
                level = rareLevel.maxLevel(promotion);
                overflow = savedExp; // 剩余经验为溢出经验
                savedExp = 0; // 清除已保存经验
                break; // 停止升级
            }
        }
        return overflow; // 返回任何溢出的经验
    }

    /**
     * 计算角色累积的总经验值。
     *
     * @return 总经验值
     */
    public int calculateTotalExp() {
        int totalExp = 0;

        // 计算当前晋升阶段之前的所有等级所需的总经验
        for (int p = 0; p < promotion; p++) {
            for (int i = 1; i <= rareLevel.maxLevel(p); i++) {
                totalExp += getRequiredExpForLevel(i);
            }
        }

        // 计算当前晋升阶段之前的等级所需的总经验
        for (int i = 1; i < level; i++) {
            totalExp += getRequiredExpForLevel(i);
        }

        // 加上当前等级的未消耗经验
        totalExp += savedExp;

        return totalExp;
    }

    private int getRequiredExpForLevel(int level) {
        return EXP_DATA[promotion][level - 1];
    }

    @Override
    public MapCodec<? extends OperatorInfo> codec() {
        return CODEC;
    }

    @Override
    public <T extends OperatorInfo> boolean merge(T newData) {
        return false;
    }

    @Override
    public <T extends OperatorInfo> T createExternal() {
        return null;
    }

    @Override
    public UpgradeAndPromotion copy() {
        return new UpgradeAndPromotion(rareLevel, level, promotion, savedExp);
    }

    /**
     * 枚举类，表示不同的稀有等级及其对应的经验要求。
     */
    public enum RareLevel {
        ONE_STAR(1, 30, 0, 0),
        TWO_STAR(2, 50, 0, 0),
        THREE_STAR(3, 50, 55, 0),
        FOUR_STAR(4, 50, 60, 70),
        FIVE_STAR(5, 50, 70, 80),
        SIX_STAR(6, 50, 80, 90);

        public final int star;
        public final int nonPromotionLevel;
        public final int firstPromotionLevel;
        public final int secondPromotionLevel;

        RareLevel(int star, int nonPromotionLevel, int firstPromotionLevel, int secondPromotionLevel) {
            this.star = star;
            this.nonPromotionLevel = nonPromotionLevel;
            this.firstPromotionLevel = firstPromotionLevel;
            this.secondPromotionLevel = secondPromotionLevel;
        }

        public boolean hasFirstPromotion() {
            return firstPromotionLevel > 0;
        }

        public boolean hasSecondPromotion() {
            return secondPromotionLevel > 0;
        }

        public int maxPromotionLevel() {
            return hasFirstPromotion() ? (hasSecondPromotion() ? 2 : 1) : 0;
        }

        public int getStar() {
            return star;
        }

        public int maxLevel(int promotionStage) {
            return switch (promotionStage) {
                case 0 -> nonPromotionLevel;
                case 1 -> firstPromotionLevel;
                case 2 -> secondPromotionLevel;
                default -> throw new IllegalStateException("Unexpected value: " + promotionStage);
            };
        }

        public static RareLevel getForStar(int star) {
            if (star <= 0 || star > 6) throw new IllegalArgumentException("Invalid star: " + star);
            return RareLevel.values()[star - 1];
        }
    }
}
