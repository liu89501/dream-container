package com.dream.container.anno;

import java.lang.annotation.*;

/**
 * 代理类的逻辑之后的处理
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodPostProcessor
{
}
