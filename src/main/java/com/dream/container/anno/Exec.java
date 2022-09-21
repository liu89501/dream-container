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

    EExecRunPriority runPriority() default EExecRunPriority.DEFAULT;
}
