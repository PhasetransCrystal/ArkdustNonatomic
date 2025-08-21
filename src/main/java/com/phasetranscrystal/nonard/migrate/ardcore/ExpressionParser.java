package com.phasetranscrystal.nonard.migrate.ardcore;

import javax.script.*;
import java.util.Map;
import java.util.function.Function;

public class ExpressionParser {
    public static final ScriptEngine scriptEngine;
    static {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("graal.js");
        if (engine == null) engine = factory.getEngineByName("javascript");
        if (engine == null) throw new IllegalStateException("No script engine available");

        try {
            // 注入完整的Math函数库作为局部变量
            engine.eval("var sqrt = Math.sqrt;");
            engine.eval("var log = Math.log;");
            engine.eval("var pow = Math.pow;");
            engine.eval("var ceil = Math.ceil;");
            engine.eval("var floor = Math.floor;");
            engine.eval("var abs = Math.abs;");
            engine.eval("var exp = Math.exp;");
        } catch (ScriptException e) {
            throw new RuntimeException("函数注入失败", e);
        }

        scriptEngine = engine;
    }

    public static double evaluate(String expression, Map<String, Double> variables) {
        try {
            ScriptContext context = new SimpleScriptContext();
            context.getBindings(ScriptContext.ENGINE_SCOPE).putAll(variables);
            return ((Number) scriptEngine.eval(expression, context)).doubleValue();
        } catch (ScriptException e) {
            throw new IllegalArgumentException("Unable to parse expression: " + expression, e);
        }
    }
}