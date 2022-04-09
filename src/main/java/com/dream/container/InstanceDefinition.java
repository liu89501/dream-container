package com.dream.container;

public class InstanceDefinition
{
    private Object proxyInstance;

    /** instance 被代理时 这个就表示被代理之前的实例 否则这个就是空的 */
    private Object originalInstance;

    public InstanceDefinition()
    {
    }

    public InstanceDefinition(Object instance)
    {
        this.proxyInstance = instance;
        this.originalInstance = instance;
    }

    public InstanceDefinition(Object proxyInstance, Object originalInstance)
    {
        this.proxyInstance = proxyInstance;
        this.originalInstance = originalInstance;
    }

    public Object getInstance()
    {
        return proxyInstance == null ? originalInstance : proxyInstance;
    }

    public Object getOriginalInstance()
    {
        return originalInstance;
    }

    public void setOriginalInstance(Object originalInstance)
    {
        this.originalInstance = originalInstance;
    }

    public Object getProxyInstance()
    {
        return proxyInstance;
    }

    public void setProxyInstance(Object proxyInstance)
    {
        this.proxyInstance = proxyInstance;
    }
}
