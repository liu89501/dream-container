package com.dream.container.anno;

import com.dream.container.Params;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToContainer
{
    String uid() default Params.DEFAULT_UID;
}
