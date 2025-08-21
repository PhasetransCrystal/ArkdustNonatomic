package com.phasetranscrystal.nonard.opesystem.info;

import com.phasetranscrystal.nonard.migrate.ardcore.ExpressionParser;
import com.phasetranscrystal.nonard.migrate.ardcore.helper.NumberHelper;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class GeneralOperatorInfo {

    public static final Logger LOGGER = LogManager.getLogger("ArkdustNona:BasicInfo");

    private final String lmbExpression;
    private final int[] lmbRequest;
    private final int[] lmbPSum;
    private final String expExpression;
    private final int[] expRequest;
    private final int[] expPSum;
    private final int maxElite;
    private final int[] eliteLevelContent;
    private final int[] eliteMaxLevel;
    private final int maxSkillLevel;
    private final int[] skillLevelRequest;
    private final Int2ObjectMap<List<ItemStack>> skillUpgradeRequest; //TODO igose


    public GeneralOperatorInfo(String lmbExpression, String expExpression, int maxElite, int[] eliteLevelContent, int maxSkillLevel, int[] skillLevelRequest, Int2ObjectMap<List<ItemStack>> skillUpgradeRequest) {
//        if((maxElite + 1) != eliteLevelContent.length) {
//            LOGGER.error("maxElite + 1 not equals eliteLevelContent's length. ArdNona init crashed.");
//            LOGGER.error("最大精英化阶段+1 与 精英阶段等级容量数组 长度不符。ArdNona初始化错误。");
//            LOGGER.error("Details: maxElite = {}, eliteLevelContent ");
//            throw new IllegalArgumentException("maxElite must equals eliteLevelContent's length");
//        }
//        if(maxSkillLevel != skillLevelRequest.length) {
//            throw new IllegalArgumentException("maxSkillLevel must equals skillLevelRequest's length");
//        }
        this.lmbExpression = lmbExpression;
        this.expExpression = expExpression;
        this.maxElite = maxElite;
        this.eliteLevelContent = eliteLevelContent;
        this.eliteMaxLevel = NumberHelper.createPrefixSumList(eliteLevelContent);
        this.maxSkillLevel = maxSkillLevel;
        this.skillLevelRequest = skillLevelRequest;
        this.skillUpgradeRequest = skillUpgradeRequest;

        this.lmbRequest = new int[getMaxLevel() - 1];
        this.expRequest = new int[getMaxLevel() - 1];
        this.lmbRequest[0] = this.expRequest[0] = 0;
        int elite = 0;
        for (int i = 2; i <= getMaxLevel(); i++) {
            if (i > eliteMaxLevel[elite]) elite++;
            lmbRequest[i - 1] = calculateLMB(i, elite);
            expRequest[i - 1] = calculateExp(i, elite);
        }
        this.lmbPSum = NumberHelper.createPrefixSumList(lmbRequest);
        this.expPSum = NumberHelper.createPrefixSumList(expRequest);
    }


    public String getLmbExpression() {
        return lmbExpression;
    }

    public String getExpExpression() {
        return expExpression;
    }

    public int getMaxElite() {
        return maxElite;
    }

    public int getEliteLevelContent(int elite) {
        return elite >= 0 && elite < getMaxElite() ? eliteLevelContent[elite] : -1;
    }

    public int getEliteMaxLevel(int elite) {
        return elite < 0 ? 0 : eliteMaxLevel[elite > getMaxElite() ? getMaxElite() - 1 : elite];
    }

    public int getMaxLevel() {
        return eliteMaxLevel[eliteMaxLevel.length - 1];
    }

    public int getMaxSkillLevel() {
        return maxSkillLevel;
    }

    public int getSkillLevelRequest(int level) {
        return level >= 1 && level <= getMaxSkillLevel() ? skillLevelRequest[level - 1] : -1;
    }

    public List<ItemStack> getSkillUpgradeRequest(int level) {
        return level >= 1 && level <= getMaxSkillLevel() ? skillUpgradeRequest.get(level - 1).stream().map(ItemStack::copy).toList() : List.of();
    }

    //返回升至本级需要的资源量
    public int calculateExp(int level, int elite) {
        return (int) ExpressionParser.evaluate(expExpression, Map.of("level", (double) level, "elite", (double) elite));
    }

    public int calculateLMB(int level, int elite) {
        return (int) ExpressionParser.evaluate(lmbExpression, Map.of("level", (double) level, "elite", (double) elite));
    }

    public int getLMBRequest(int level) {
        return level >= 1 && level <= getMaxLevel() ? lmbRequest[level - 1] : -1;
    }

    public int getExpRequest(int level) {
        return level >= 1 && level <= getMaxLevel() ? expRequest[level - 1] : -1;
    }

    public int getLMBRequest(int fromLevel, int toLevel) {
        toLevel = Math.clamp(toLevel, 1, getMaxLevel());
        fromLevel = Math.clamp(fromLevel, 1, toLevel);
        return lmbPSum[toLevel - 1] - lmbPSum[fromLevel - 1];
    }

    public int getExpRequest(int fromLevel, int toLevel) {
        toLevel = Math.clamp(toLevel, 1, getMaxLevel());
        fromLevel = Math.clamp(fromLevel, 1, toLevel);
        return expPSum[toLevel - 1] - expPSum[fromLevel - 1];
    }

    public int getEliteForLevel(int level){
        if(level < 0 || level > getMaxLevel()) return -1;
        for (int i = 0; i <= getMaxElite(); i++) {
            if (level <= getEliteMaxLevel(i)) return i;
        }
        return -1;
    }

}
