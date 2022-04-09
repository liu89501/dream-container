package com.dream.container.anno;

import com.dream.container.Container;
import com.dream.container.DatabaseManager;
import com.dream.container.DependenceHandler;

import java.lang.annotation.*;

@Inherited
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InitializeArgs
{
    Class<? extends DatabaseManager> databaseManager() default DatabaseManager.class;

    Class<? extends DependenceHandler>[] dependenceHandlers() default {};

    Class<? extends Container>[] containers() default {};
}
