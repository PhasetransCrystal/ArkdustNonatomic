package com.phasetranscrystal.nonard.util;

/**
 * Trait拦截器接口
 */
@FunctionalInterface
public interface NanoInterceptor<T> {

    /**
     * 拦截Trait处理
     * 
     * @param context 处理上下文
     */
    void intercept(final T context);
}
