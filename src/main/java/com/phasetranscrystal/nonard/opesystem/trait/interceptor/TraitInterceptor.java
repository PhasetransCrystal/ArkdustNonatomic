package com.phasetranscrystal.nonard.opesystem.trait.interceptor;

import com.phasetranscrystal.nonard.opesystem.trait.Trait;
import com.phasetranscrystal.nonard.util.NanoInterceptor;

/**
 * Trait拦截器接口
 */
@FunctionalInterface
public interface TraitInterceptor extends NanoInterceptor<Trait.Context> {

    /**
     * 拦截Trait处理
     * 
     * @param context 处理上下文
     */
    void intercept(Trait.Context context);
}
