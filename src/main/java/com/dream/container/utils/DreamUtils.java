package com.dream.container.utils;

import com.dream.container.anno.Component;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;

public interface DreamUtils
{
    static Component queryComponentAnnotation(Class<?> clazz)
    {
        if (clazz.isAnnotationPresent(Component.class))
        {
            return clazz.getDeclaredAnnotation(Component.class);
        }

        Annotation[] declaredAnnotations = clazz.getDeclaredAnnotations();

        for (Annotation declaredAnnotation : declaredAnnotations)
        {
            Component component = queryComponentAnnotation(declaredAnnotation.annotationType());

            if (component != null)
            {
                return component;
            }
        }

        return null;
    }


    @SuppressWarnings("unchecked")
    static <T> T createProxyObject(Class<T> clazz, Callback callbackHandler)
    {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(callbackHandler);
        return (T)enhancer.create();
    }

    static URL getResource(String resource)
    {
        List<ClassLoader> classLoaders = List.of(
                ClassLoader.getSystemClassLoader(),
                DreamUtils.class.getClassLoader(),
                Thread.currentThread().getContextClassLoader()
        );

        Optional<URL> firstResource = classLoaders.stream()
                .map(e -> e.getResource(resource))
                .filter(Objects::nonNull)
                .findFirst();

        return firstResource.orElseThrow();
    }

    static boolean isCommonType(Class<?> clazz)
    {
        return String.class == clazz || Integer.class == clazz ||
                Boolean.class == clazz || Double.class == clazz ||
                Long.class == clazz || Byte.class == clazz ||
                Float.class == clazz || Short.class == clazz ||
                Character.class == clazz || clazz.isPrimitive();
    }

    @SuppressWarnings("unchecked")
    static <T> List<T> combine(T[] l, T... r)
    {
        // 垃圾java 垃圾泛型 垃圾！！！ 一点都不如C++的 template

        ArrayList<T> arrayList = new ArrayList<>();

        arrayList.addAll(Arrays.asList(l));
        arrayList.addAll(Arrays.asList(r));

        return arrayList;
    }
}
