package com.dream.container;

import java.util.List;

public interface Container
{
    boolean canHosting(Class<?> scannedClass);

    void add(Class<?> scannedClass) throws Exception;

    List<InstanceDefinition> getInstances();

    void initializeContainerParam(InitializeTemporaryParams temporaryParams);
}
