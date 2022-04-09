package com.dream.container;

import java.util.Map;

public interface ComponentContainer extends Container
{
    void addComponents(Map<Class<?>, Map<String, InstanceDefinition>> newComponents) throws Exception;

    void combineContainer(ComponentContainer... containers);

    void append(Container container);

    InstanceDefinition getComponent(Class<?> componentClass, String uid);

    Map<Class<?>, Map<String, InstanceDefinition>> getComponents();

    Map<String, InstanceDefinition> getComponentsFromParentClass(Class<?> parentClass);
}
