package com.dream.container.handler;

public interface LaunchArgsParser
{
    Object parse(String argValueString, Class<?> targetClass);
}
