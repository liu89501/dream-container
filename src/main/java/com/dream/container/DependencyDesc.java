package com.dream.container;

public record DependencyDesc(Class<?> clazz, Object instance, String uid)
{
}
