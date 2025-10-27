package com.phasetranscrystal.nonard.opesystem.info;

import com.mojang.serialization.Codec;
import com.phasetranscrystal.nonard.migrate.ardcore.ExpressionParser;

import java.util.HashMap;
import java.util.Map;

public record MappingExpression(Map<String, String> expressions) {

    public static final Codec<MappingExpression> CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING).xmap(MappingExpression::new, MappingExpression::expressions);

    public Map<String, Double> calculate(int level) {
        Map<String, Double> map = new HashMap<>();
        expressions.forEach((key, expression) -> {
            map.put(key, ExpressionParser.evaluate(expression, Map.of("level", (double) level)));
        });
        return map;
    }
}
