package com.dream.container.anno;

import com.dream.container.LaunchParams;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToContainer
{
    String uid() default LaunchParams.DEFAULT_UID;
}
