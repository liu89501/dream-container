package com.dream.container;

import com.dream.container.anno.MethodPostProcessor;
import java.util.LinkedList;
import java.util.List;

public class ProxyPostProcessorContainer implements Container
{
    private final List<InstanceDefinition> postProcessors = new LinkedList<>();

    @Override
    public boolean canHosting(Class<?> scannedClass)
    {
        return scannedClass.isAnnotationPresent(MethodPostProcessor.class) && ProxyMethodPostProcessor.class.isAssignableFrom(scannedClass);
    }

    @Override
    public void add(Class<?> scannedClass) throws Exception
    {
        postProcessors.add(new InstanceDefinition(scannedClass.getConstructor().newInstance()));
    }

    @Override
    public List<InstanceDefinition> getInstances()
    {
        return postProcessors;
    }

    @Override
    public void initializeContainerParam(InitializeTemporaryParams temporaryParams)
    {

    }
}
