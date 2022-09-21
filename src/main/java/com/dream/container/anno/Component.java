package com.dream.container.anno;

import com.dream.container.LaunchParams;

import java.lang.annotation.*;

@Inherited
@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Component
{
    boolean proxy() default true;

    String uid() default LaunchParams.DEFAULT_UID;
}
