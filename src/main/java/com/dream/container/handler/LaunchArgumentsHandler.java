package com.dream.container.handler;

import com.dream.container.anno.LaunchArg;
import com.dream.container.ComponentContainer;
import com.dream.container.DependenceHandler;
import com.dream.container.InstanceDefinition;
import com.dream.container.Params;
import com.dream.container.LogContainer;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LaunchArgumentsHandler implements DependenceHandler
{
    private final List<LaunchArgsParser> Temporary_Parsers = new LinkedList<>();

    @Override
    public void initializeComponents(ComponentContainer instantComponents, ComponentContainer defaultComponents)
    {
        Map<String, InstanceDefinition> parsers = instantComponents.getComponentsFromParentClass(LaunchArgsParser.class);
        parsers.forEach((k, v) -> Temporary_Parsers.add((LaunchArgsParser)v.getInstance()));
    }

    @Override
    public boolean handle(Field dependenceField, InstanceDefinition ownerInstance) throws Exception
    {
        if (dependenceField.isAnnotationPresent(LaunchArg.class))
        {
            LaunchArg launchArg = dependenceField.getAnnotation(LaunchArg.class);

            String argValue = Params.getLaunchArg(launchArg.value());

            Class<?> fieldType = dependenceField.getType();

            Object fieldValue = null;

            if (fieldType == String.class)
            {
                fieldValue = argValue;
            }
            else
            {
                for (LaunchArgsParser parser : Temporary_Parsers)
                {
                    fieldValue = parser.parse(argValue, fieldType);

                    if (fieldValue != null)
                    {
                        break;
                    }
                }
            }

            if (fieldValue == null)
            {
                LogContainer.LOG.warn("LaunchArg parse may fail -> {} - {}", launchArg.value(), argValue);
            }
            else
            {
                dependenceField.set(ownerInstance.getInstance(), fieldValue);
            }

            return true;
        }

        return false;
    }
}
