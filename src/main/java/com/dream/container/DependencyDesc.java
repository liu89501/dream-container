package com.dream.container;

public class DependencyDesc
{
    public Class<?> clazz;
    public Object instance;
    public String uid;

    public DependencyDesc() {
    }

    public DependencyDesc(Class<?> clazz, Object instance, String uid) {
        this.clazz = clazz;
        this.instance = instance;
        this.uid = uid;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getInstance() {
        return instance;
    }

    public String getUid() {
        return uid;
    }
}
