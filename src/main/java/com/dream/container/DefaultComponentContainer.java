package com.dream.container;

import com.dream.container.anno.Component;
import com.dream.container.utils.DreamUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultComponentContainer implements ComponentContainer
{
    private final Map<Class<?>, Map<String, InstanceDefinition>> components = new HashMap<>();

    private InitializeTemporaryParams temporaryParams;

    @Override
    public boolean canHosting(Class<?> scannedClass)
    {
        return scannedClass.isAnnotationPresent(Component.class);
    }

    @Override
    public void add(Class<?> scannedComponentClass) throws Exception
    {
        Component component = scannedComponentClass.getAnnotation(Component.class);

        Object instance = scannedComponentClass.getDeclaredConstructor().newInstance();

        InstanceDefinition definition = new InstanceDefinition();
        definition.setOriginalInstance(instance);

        if (component.proxy())
        {
            Object proxyInstance = DreamUtils.createProxyObject(scannedComponentClass,
                    new ProxyHandle(instance, temporaryParams.getPostProcessorContainer(), temporaryParams.getDatabaseManager()));

            definition.setProxyInstance(proxyInstance);
        }

        handleAddContainer(scannedComponentClass, component.uid(), definition);
    }

    @Override
    public List<InstanceDefinition> getInstances()
    {
        List<InstanceDefinition> instances = new LinkedList<>();
        for (Map.Entry<Class<?>, Map<String, InstanceDefinition>> entry : components.entrySet())
        {
            instances.addAll(entry.getValue().values());
        }
        return instances;
    }

    @Override
    public void addComponents(Map<Class<?>, Map<String, InstanceDefinition>> newComponents)
    {
        components.putAll(newComponents);
    }

    @Override
    public void combineContainer(ComponentContainer... containers)
    {
        if (containers != null)
        {
            for (ComponentContainer container : containers)
            {
                components.putAll(container.getComponents());
            }
        }
    }

    @Override
    public void append(Container container)
    {
        for (InstanceDefinition instance : container.getInstances())
        {
            HashMap<String, InstanceDefinition> def = new HashMap<>();
            def.put(LaunchParams.DEFAULT_UID, instance);

            components.put(instance.getInstance().getClass(), def);
        }
    }

    @Override
    public InstanceDefinition getComponent(Class<?> componentClass, String uid)
    {
        Map<String, InstanceDefinition> definitionMap = components.get(componentClass);

        // 没找到的话就尝试找接口对应的实例
        if (definitionMap == null)
        {
            if (!componentClass.isInterface())
            {
                Class<?>[] interfaces = componentClass.getInterfaces();

                for (Class<?> interfaceClass : interfaces)
                {
                    definitionMap = components.get(interfaceClass);

                    if (definitionMap != null)
                    {
                        break;
                    }
                }
            }
        }

        // 再没找到就直接循环所有Components
        if (definitionMap == null)
        {
            for (Map.Entry<Class<?>, Map<String, InstanceDefinition>> entry : components.entrySet())
            {
                if (componentClass.isAssignableFrom(entry.getKey()))
                {
                    definitionMap = entry.getValue();
                    break;
                }
            }
        }

        if (definitionMap != null)
        {
            return definitionMap.get(uid);
        }
        return null;
    }

    @Override
    public Map<String, InstanceDefinition> getComponentsFromParentClass(Class<?> parentClass)
    {
        Map<String, InstanceDefinition> matchComponents = new HashMap<>();

        for (Map.Entry<Class<?>, Map<String, InstanceDefinition>> entry : components.entrySet())
        {
            Class<?> clazz = entry.getKey();

            if (parentClass.isAssignableFrom(clazz))
            {
                matchComponents.putAll(entry.getValue());
            }
        }

        return matchComponents;
    }

    @Override
    public void initializeContainerParam(InitializeTemporaryParams temporaryParams)
    {
        this.temporaryParams = temporaryParams;
    }

    @Override
    public Map<Class<?>, Map<String, InstanceDefinition>> getComponents()
    {
        return components;
    }


    private void handleAddContainer(Class<?> componentClass, String uid, InstanceDefinition def)
    {
        Map<String, InstanceDefinition> ins = components.computeIfAbsent(componentClass, k -> new HashMap<>());
        ins.put(uid, def);
    }

}
