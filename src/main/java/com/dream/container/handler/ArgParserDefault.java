package com.dream.container.handler;

import com.dream.container.anno.Component;

@Component(proxy = false, instant = true, uid = "Default")
public class ArgParserDefault implements LaunchArgsParser
{
    @Override
    public Object parse(String argValueString, Class<?> targetClass)
    {
        if (targetClass == Integer.class || targetClass == int.class)
        {
            return Integer.parseInt(argValueString);
        }
        else if (targetClass == Long.class || targetClass == long.class)
        {
            return Long.parseLong(argValueString);
        }
        else if (targetClass == Boolean.class || targetClass == boolean.class)
        {
            return Boolean.parseBoolean(argValueString);
        }

        return null;
    }
}
