package com.dream.container.anno;

import java.lang.annotation.*;

/** 扫描到到这个method时会立即执行 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Exec
{
    /**
     * 如果这里指定的优先级低于系统运行时指定的优先级, 那么将不会触发Exec标识的Method
     */
    EExecPriority value() default EExecPriority.LOW;

    /**
     *  是否用一个单独的线程来运行, 一般用于会阻塞的任务, 比如某个网络服务
     */
    //boolean independentThread() default false;
}
