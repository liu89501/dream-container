package com.dream.container.anno;

import java.lang.annotation.*;

@Inherited
@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Config
{
    /**
     * 配置文件的classpath
     */
    String classpath();
}
