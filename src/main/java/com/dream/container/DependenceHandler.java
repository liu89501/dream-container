package com.dream.container;

import java.lang.reflect.Field;

public interface DependenceHandler
{
    boolean handle(Field dependenceField, InstanceDefinition ownerInstance) throws Exception;

    /**
     * 初始化相关的组件容器参数
     * NOTE: 此时Container中的对象 Assign 标识的字段还没有分配
     * @param defaultComponents     默认组件容器
     */
    void initializeComponents(ComponentContainer defaultComponents);
}
