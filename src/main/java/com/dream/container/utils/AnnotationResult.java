package com.dream.container.utils;

import java.lang.annotation.Annotation;

public class AnnotationResult<T extends Annotation>
{
    private Annotation annotation;

    private T parentAnnotation;

    public AnnotationResult()
    {
    }

    public AnnotationResult(Annotation annotation, T parentAnnotation)
    {
        this.annotation = annotation;
        this.parentAnnotation = parentAnnotation;
    }

    public Annotation getAnnotation()
    {
        return annotation;
    }

    public T getParentAnnotation()
    {
        return parentAnnotation;
    }
}
