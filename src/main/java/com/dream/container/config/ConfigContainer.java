package com.dream.container.config;

import com.dream.container.Container;
import com.dream.container.InitializeTemporaryParams;
import com.dream.container.InstanceDefinition;
import com.dream.container.anno.Config;
import com.dream.container.utils.DreamUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class ConfigContainer implements Container
{
    private final List<InstanceDefinition> settings = new LinkedList<>();

    @Override
    public boolean canHosting(Class<?> scannedClass)
    {
        return scannedClass.isAnnotationPresent(Config.class);
    }

    @Override
    public void add(Class<?> scannedClass) throws Exception
    {
        Config annotation = scannedClass.getAnnotation(Config.class);

        URL resource = DreamUtils.getResource(annotation.classpath());

        ObjectMapper objectMapper = new ObjectMapper();
        settings.add(new InstanceDefinition(objectMapper.readValue(resource, scannedClass)));
    }

    @Override
    public List<InstanceDefinition> getInstances()
    {
        return settings;
    }

    @Override
    public void initializeContainerParam(InitializeTemporaryParams temporaryParams)
    {
    }
}
