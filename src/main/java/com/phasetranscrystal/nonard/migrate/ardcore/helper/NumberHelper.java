package com.phasetranscrystal.nonard.migrate.ardcore.helper;

public class NumberHelper {

    public static int[] createPrefixSumList(int[] origin) {
        int[] prefixSums = origin.clone();
        for (int i = 1; i < origin.length; i++) {
            prefixSums[i] += prefixSums[i - 1];
        }
        return prefixSums;
    }
}
