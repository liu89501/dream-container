package com.dream.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitializeTemporaryParams
{
    private final ComponentContainer componentContainer;

    private final List<DependenceHandler> dependenceHandlers;

    private DatabaseManager databaseManager;

    private final Container postProcessorContainer;

    private final Map<Class<? extends  Container>, Container> customizeContainers;

    public InitializeTemporaryParams(ComponentContainer componentContainer,
                                     List<Class<? extends DependenceHandler>> depHandlerDefs,
                                     List<Class<? extends Container>> containerClasses,
                                     Class<? extends DatabaseManager> databaseManagerClass) throws Exception
    {
        this.componentContainer = componentContainer;
        this.postProcessorContainer = new ProxyPostProcessorContainer();

        this.dependenceHandlers = new ArrayList<>();

        for (Class<? extends DependenceHandler> handler : depHandlerDefs)
        {
            DependenceHandler dependenceHandler = handler.getConstructor().newInstance();
            dependenceHandlers.add(dependenceHandler);
        }

        if (databaseManagerClass != DatabaseManager.class)
        {
            databaseManager = databaseManagerClass.getConstructor().newInstance();
            databaseManager.initialize();
        }

        this.componentContainer.initializeContainerParam(this);

        customizeContainers = new HashMap<>();

        if (containerClasses != null && containerClasses.size() > 0)
        {
            for (Class<? extends Container> containerClass : containerClasses)
            {
                Container container = containerClass.getConstructor().newInstance();
                container.initializeContainerParam(this);

                customizeContainers.put(containerClass, container);
            }
        }
    }

    public Container getPostProcessorContainer()
    {
        return postProcessorContainer;
    }

    public ComponentContainer getComponentContainer()
    {
        return componentContainer;
    }

    public List<DependenceHandler> getDependenceHandlers()
    {
        return dependenceHandlers;
    }

    public DatabaseManager getDatabaseManager()
    {
        return databaseManager;
    }

    public Map<Class<? extends  Container>, Container> getCustomizeContainers()
    {
        return customizeContainers;
    }
}
