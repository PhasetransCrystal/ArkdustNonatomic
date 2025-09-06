package com.phasetranscrystal.nonard.migrate.nona_quench.helper;

import net.minecraft.util.RandomSource;

import java.util.*;
import java.util.function.Supplier;

public class WeightedRandomSelector {
    /**
     * 从带权重的元素Map中随机顺序取出指定数量的元素
     *
     * @param weightedMap 带权重的元素Map（元素不能重复）
     * @param n           需要取出的元素数量
     * @param <T>         元素类型
     * @return 随机顺序的元素列表
     */
    public static <T> List<T> selectWeightedRandom(Map<T, Integer> weightedMap, int n, RandomSource random) {
        // 处理空Map情况
        if (weightedMap == null || weightedMap.isEmpty() || n < 1) {
            return Collections.emptyList();
        }

        // 处理n大于等于集合大小的情况
        if (n >= weightedMap.size()) {
            return List.copyOf(weightedMap.keySet());
        }

        // 将Map转换为可修改的列表
        List<WeightedItem<T>> weightedList = new ArrayList<>();
        for (Map.Entry<T, Integer> entry : weightedMap.entrySet()) {
            weightedList.add(new WeightedItem<>(entry.getKey(), entry.getValue()));
        }

        // 计算总权重
        int totalWeight = weightedList.stream().mapToInt(WeightedItem::getWeight).sum();

        List<T> result = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            // 如果剩余总权重为0（所有元素权重为0），则随机选择一个
            if (totalWeight == 0) {
                int randomIndex = random.nextInt(weightedList.size());
                result.add(weightedList.get(randomIndex).getItem());
                weightedList.remove(randomIndex);
                continue;
            }

            // 生成随机权重值
            int randomWeight = random.nextInt(totalWeight);
            int cumulativeWeight = 0;

            // 找到对应的元素
            Iterator<WeightedItem<T>> iterator = weightedList.iterator();
            while (iterator.hasNext()) {
                WeightedItem<T> item = iterator.next();
                cumulativeWeight += item.getWeight();

                if (randomWeight < cumulativeWeight) {
                    result.add(item.getItem());
                    totalWeight -= item.getWeight(); // 更新剩余总权重
                    iterator.remove(); // 移除已选元素
                    break;
                }
            }
        }

        return result;
    }

    // 内部类：带权重的元素
    private static class WeightedItem<T> {
        private final T item;
        private final int weight;

        public WeightedItem(T item, int weight) {
            this.item = item;
            this.weight = weight;
        }

        public T getItem() {
            return item;
        }

        public int getWeight() {
            return weight;
        }
    }
}
