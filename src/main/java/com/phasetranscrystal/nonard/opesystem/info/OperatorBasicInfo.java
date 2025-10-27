package com.phasetranscrystal.nonard.opesystem.info;

import net.minecraft.resources.ResourceLocation;

import it.unimi.dsi.fastutil.ints.IntIntPair;
import org.jetbrains.annotations.Range;

import java.util.List;

/**
 * @param gender              性别
 * @param combatExp           战斗经验/年
 * @param comingFrom          来自地区
 * @param birthday            生日<月,日>
 * @param faction             阵营
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
                                ResourceLocation faction, ResourceLocation race, int height, boolean infected,
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

    public static class Region {
        // public static final ResourceLocation
    }

    public static class Factions {

        public static final ResourceLocation AEGIR = of("aegir"),                    // 阿戈尔
                BABEL = of("babel"),                    // 巴别塔
                BOLIVAR = of("bolivar"),                // 玻利瓦尔
                COLUMBIA = of("columbia"),              // 哥伦比亚
                HIGASHI = of("higashi"),                // 东国
                IBERIA = of("iberia"),                  // 伊比利亚
                KARLAN_TRADE = of("karlan_trade"),      // 喀兰贸易
                KAZIMIERZ = of("kazimierz"),            // 卡西米尔
                LATERANO = of("laterano"),              // 拉特兰
                LEITHANIEN = of("leithanien"),          // 莱塔尼亚
                MINOS = of("minos"),                    // 米诺斯
                RHODES_ISLAND = of("rhodes_island"),    // 罗德岛
                RIM_BILLITON = of("rim_billiton"),      // 雷姆必拓
                SAMI = of("sami"),                      // 萨米
                SARGON = of("sargon"),                  // 萨尔贡
                SIRACUSA = of("siracusa"),              // 叙拉古
                URSUS = of("ursus"),                    // 乌萨斯
                VICTORIA = of("victoria"),              // 维多利亚
                YAN = of("yan");                        // 大炎

        public static final ResourceLocation ELITE_OP = of("rhodes_island/elite_op"),                // 精英干员
                OP_A4 = of("rhodes_island/op_a4"),                      // A4行动组
                OP_RESERVE_A1 = of("rhodes_island/op_reserve_a1"),      // A1行动预备组
                OP_RESERVE_A4 = of("rhodes_island/op_reserve_a4"),      // A4行动预备组
                OP_RESERVE_A6 = of("rhodes_island/op_reserve_a6"),      // A6行动预备组
                SWEEP = of("rhodes_island/sweep");                      // S.W.E.E.P.

        public static final ResourceLocation SUI = of("yan/sui"),                                    // 岁
                LUNGMEN = of("yan/lungmen"),                            // 龙门
                LEES_DETECTIVE_AGENCY = of("yan/lungmen/lees_detective_agency"),            // 鲤氏侦探事务所
                LUNGMEN_GUARD_DEPARTMENT = of("yan/lungmen/lungmen_guard_department"),      // 龙门近卫局
                PENGUIN_LOGISTICS = of("yan/lungmen/penguin_logistics");// 企鹅物流

        public static final ResourceLocation ABYSSAL_HUNTERS = of("aegir/abyssal_hunters");          // 深海猎人

        public static final ResourceLocation RHINE_LAB = of("columbia/rhine_lab"),                   // 莱茵生命
                SIESTA = of("columbia/siesta"),                         // 汐斯塔
                BLACKSTEEL = of("columbia/blacksteel");                 // 黑钢国际

        public static final ResourceLocation DUBLINN = of("victoria/dublinn"),                       // 塔拉-都柏林 / 深池
                GLASGOW = of("victoria/glasgow");                       // 格拉斯哥帮

        public static final ResourceLocation FOLLOWERS = of("kazimierz/followers"),                  // 使徒
                PINUS_SYLVESTRIS = of("kazimierz/pinus_sylvestris");    // 雪松骑士团

        public static final ResourceLocation CHIAVES_GANG = of("siracusa/chiaves_gang");             // 贾维团伙

        public static final ResourceLocation URSUS_STUDENT_SELF_GOVERNING_GROUP = of("ursus/ursus_student_self_governing_group"); // 乌萨斯学生自治团

        public static final ResourceLocation TEAM_RAINBOW = of("team_rainbow");                     // 彩虹小队

        public static final ResourceLocation REUNION_MOVEMENT = of("reunion_movement");             // 整合运动
    }

    public static class Race {

        // 神话生物种族
        public static final ResourceLocation CERBERUS = of("cerberus"),          // 刻柏洛斯
                DRACO = of("draco"),                // 龙
                HIPPOGRYPH = of("hippogryph"),      // 骏鹰
                KITSUNE = of("kitsune"),            // 天狐
                KUKULKAN = of("kukulkan"),          // 羽蛇
                KYLIN = of("kylin"),                // 麒麟
                LUNG = of("lung"),                  // 龙
                NIGHTZMORA = of("nightzmora"),      // 夜魔
                PEGASUS = of("pegasus"),            // 天马
                UNICORN = of("unicorn");            // 独角兽

        // 传说种族
        public static final ResourceLocation ELF = of("elf"),                    // 精灵
                FERANMUT = of("feranmut");          // 巨兽

        // 泰拉大陆种族
        public static final ResourceLocation AEGIR = of("aegir"),                // 阿戈尔
                ANATY = of("anaty"),                // 阿纳缇
                ANURA = of("anura"),                // 阿努拉
                ARCHOSAURIA = of("archosauria"),    // 祖
                ASLAN = of("aslan"),                // 阿斯兰
                CAPRINAE = of("caprinae"),          // 卡普里尼
                CAUTUS = of("cautus"),              // 卡特斯
                CERATO = of("cerato"),              // 卡普里尼
                ELAFIA = of("elafia"),              // 埃拉菲亚
                FELINE = of("feline"),              // 菲林
                FORTE = of("forte"),                // 丰蹄
                ITRA = of("itra"),                  // 伊特拉
                KURANTA = of("kuranta"),            // 库兰塔
                LIBERI = of("liberi"),              // 黎博利
                LUPO = of("lupo"),                  // 鲁珀
                MANTICORE = of("manticore"),        // 曼提柯
                PERRO = of("perro"),                // 佩洛
                PETRAM = of("petram"),              // 皮加索斯
                PHIDIA = of("phidia"),              // 斐迪亚
                PILOSA = of("pilosa"),              // 匹洛斯
                REPROBA = of("reproba"),            // 瑞柏巴
                SAVRA = of("savra"),                // 萨弗拉
                URSUS = of("ursus"),                // 乌萨斯
                VOUIVRE = of("vouivre"),            // 瓦伊凡
                VULPO = of("vulpo"),                // 沃尔珀
                ZALAK = of("zalak");                // 札拉克

        // 特殊种族
        public static final ResourceLocation ANASA = of("anasa"),                // 阿纳萨
                DURIN = of("durin"),                // 杜林
                ONI = of("oni"),                    // 鬼
                SANKTA = of("sankta"),              // 萨科塔
                SARKAZ = of("sarkaz");              // 萨卡兹

        // 基础种族分类
        public static final ResourceLocation HUMANOID = of("humanoid");          // 人形
    }

    private static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath("arknights", path);
    }
}
