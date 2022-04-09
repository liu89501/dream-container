package com.dream.container.anno;

import com.dream.container.Params;

import java.lang.annotation.*;

@Inherited
@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Component
{
    boolean proxy() default true;

    String uid() default Params.DEFAULT_UID;

    /**
     * 在什么阶段分配这个Component
     */
    @Deprecated
    EAssignPhase assignPhase() default EAssignPhase.Default;

    /**
     * 如果为true 表示生命周期到容器初始化完毕时就截止了, 一般用于只需要在初始化阶段存在的Component,
     * 比如使用 ToContainer 这种方式添加对象到容器时可以将Component的 instant 设为true,
     * 凡是不需要在项目运行期间获取的Component都可以将这个选项设置为true
     */
    boolean instant() default false;
}
