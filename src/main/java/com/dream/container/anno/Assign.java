package com.dream.container.anno;

import com.dream.container.Params;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Assign
{
    String uid() default Params.DEFAULT_UID;

    boolean require() default true;
}
