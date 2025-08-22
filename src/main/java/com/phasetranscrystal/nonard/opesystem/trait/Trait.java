package com.phasetranscrystal.nonard.opesystem.trait;

import com.phasetranscrystal.nonard.opesystem.OperatorEntity;
import com.phasetranscrystal.nonard.opesystem.trait.interceptor.TraitInterceptor;

import java.util.ArrayList;
import java.util.List;

public interface Trait {
    TraitConfig get();

    /**
     * 处理干员的特性效果
     * @param operator 要处理的干员
     */
    default void handle(OperatorEntity operator) {
        Context context = new Context(operator, this);

        if (!executeInterceptors(context)) {
            return;
        }

        try {
            doHandle(operator, context);
        } catch (Exception e) {
            System.err.println("Error applying trait: " + context.getTrait().getClass().getSimpleName() +
                    " to operator: " + context.getOperator().getClass().getSimpleName());
            e.printStackTrace();
        }
    }

    /**
     * 实际的特性处理逻辑
     */
    void doHandle(OperatorEntity operator, Context context);

    List<TraitInterceptor> interceptors = new ArrayList<>();

    static void addInterceptor(TraitInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    private static boolean executeInterceptors(Context context) {
        for (TraitInterceptor interceptor : interceptors) {
            interceptor.intercept(context);
            if (context.isCanceled()) {
                return false; // 中断处理
            }
        }
        return true;
    }

     class Context {
        private final OperatorEntity operator;
        private final Trait trait;
        private boolean canceled = false;

        private Context(OperatorEntity operator, Trait trait) {
            this.operator = operator;
            this.trait = trait;
        }

        public OperatorEntity getOperator() {
            return operator;
        }

        public Trait getTrait() {
            return trait;
        }

        public void setCanceled(boolean canceled) {
            this.canceled = canceled;
        }

        public boolean isCanceled() {
            return canceled;
        }
    }
}

