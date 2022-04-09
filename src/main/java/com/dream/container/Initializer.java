package com.dream.container;

import com.dream.container.anno.*;
import com.dream.container.handler.LaunchArgumentsHandler;
import com.dream.container.config.ConfigContainer;
import com.dream.container.utils.DreamUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class Initializer
{
    private static final String PROTOCOL_FILE = "file";
    private static final String PROTOCOL_JAR = "jar";
    private static final String ARG_NAME_EXEC_PRIORITY = "-exec_priority";

    private static final String MODULE_CONTAINER = "com.dream.container";
    private static final String MODULE_SERVICE = "com.dream.service";

    /**
     *  初始化
     *
     * @param launchClass 调用这个方法的类，应该时main方法所在的类
     * @param args 启动参数
     */
    @SuppressWarnings("unchecked")
    public static void initialize(Class<?> launchClass, String[] args)
    {
        try
        {
            InitializeArgs initializeArgs = launchClass.getAnnotation(InitializeArgs.class);

            InitializeTemporaryParams temporaryParams = new InitializeTemporaryParams(
                    new DefaultComponentContainer(),
                    new DefaultComponentContainer(),
                    DreamUtils.combine(initializeArgs.dependenceHandlers(), LaunchArgumentsHandler.class),
                    DreamUtils.combine(initializeArgs.containers(), ProxyPostProcessorContainer.class, ConfigContainer.class),
                    initializeArgs.databaseManager());

            parseHosting(args);

            List<String> scanPackages = List.of(launchClass.getPackageName(), MODULE_SERVICE, MODULE_CONTAINER);

            for (String scanPackage : scanPackages)
            {
                String packagePath = scanPackage.replace('.', '/');

                URL resource = DreamUtils.getResource(packagePath);

                if (PROTOCOL_FILE.equalsIgnoreCase(resource.getProtocol()))
                {
                    scanJavaFilesByPath(new File(resource.toURI()), scanPackage, temporaryParams);
                }
                else if (PROTOCOL_JAR.equalsIgnoreCase(resource.getProtocol()))
                {
                    JarURLConnection connection = (JarURLConnection)resource.openConnection();
                    scanJavaFilesByJar(connection.getJarFile(), scanPackage, temporaryParams);
                }
            }

            postHandleDependent(temporaryParams);

            LogContainer.LOG.info("container initialized");
        }
        catch (Throwable e)
        {
            LogContainer.LOG.error("initialize error", e);
            System.exit(0);
        }
    }

    private static void parseHosting(String[] args)
    {
        if (args != null && args.length > 0)
        {
            Params.LAUNCH_ARGS = new HashMap<>(16);

            for (int i = 0; i < args.length; i+=2)
            {
                String argName = args[i];
                String argValue = args[i + 1];

                Params.LAUNCH_ARGS.put(argName, argValue);
            }
        }
    }

    private static void scanJavaFilesByPath(File path, String packageName, InitializeTemporaryParams temporaryParams) throws Exception
    {
        if (path == null)
        {
            return;
        }

        File[] files = path.listFiles();

        if (files == null || files.length == 0)
        {
            return;
        }

        for (File file : files)
        {
            if (file.isDirectory())
            {
                scanJavaFilesByPath(file, (packageName + "." + file.getName()), temporaryParams);
            }
            else
            {
                String className = packageName + "." + file.getName().replace(".class", "");

                handleScannedClass(Class.forName(className), temporaryParams);
            }
        }
    }

    private static void scanJavaFilesByJar(JarFile jarFile, String packageName, InitializeTemporaryParams temporaryParams) throws Exception
    {
        Objects.requireNonNull(jarFile);

        String packagePath = packageName.replace('.', '/') + '/';

        List<JarEntry> scannedClasses = jarFile.stream()
                .filter(j -> !j.isDirectory())
                .filter(j -> j.getName().startsWith(packagePath))
                .toList();

        for (JarEntry entry : scannedClasses)
        {
            String classQualifiedName = entry.getName().replace(".class", "").replace('/', '.');

            handleScannedClass(Class.forName(classQualifiedName), temporaryParams);
        }
    }

    private static void handleScannedClass(Class<?> clazz, InitializeTemporaryParams temporaryParams) throws Exception
    {
        if (clazz.isAnnotation() || clazz.isEnum() || clazz.isInterface())
        {
            return;
        }

        if (clazz.isAnnotationPresent(Component.class))
        {
            Component annotation = clazz.getAnnotation(Component.class);
            if (annotation.instant())
            {
                temporaryParams.getInstantComponents().add(clazz);
            }
            else
            {
                temporaryParams.getComponentContainer().add(clazz);
            }
        }
        else
        {
            for (Map.Entry<Class<? extends Container>, Container> entry : temporaryParams.getCustomizeContainers().entrySet())
            {
                if (entry.getValue().canHosting(clazz))
                {
                    entry.getValue().add(clazz);
                    break;
                }
            }
        }
    }

    /**
     * 处理依赖
     */
    @SuppressWarnings("unchecked")
    private static void postHandleDependent(InitializeTemporaryParams temporaryParams) throws Exception
    {
        ComponentContainer componentContainer = temporaryParams.getComponentContainer();
        ComponentContainer instantComponents = temporaryParams.getInstantComponents();

        ComponentContainer pendingHandleContainer = new DefaultComponentContainer();
        pendingHandleContainer.combineContainer(componentContainer, instantComponents);

        Map<Class<? extends Container>, Container> customizeContainers = temporaryParams.getCustomizeContainers();
        for (Map.Entry<Class<? extends Container>, Container> entry : customizeContainers.entrySet())
        {
            pendingHandleContainer.append(entry.getValue());
        }

        List<DependenceHandler> dependenceHandlers = temporaryParams.getDependenceHandlers();

        for (DependenceHandler dependenceHandler : dependenceHandlers)
        {
            dependenceHandler.initializeComponents(instantComponents, componentContainer);
        }

        // 循环过程中添加的component
        Map<Class<?>, Map<String, InstanceDefinition>> loopingPutComponents = new HashMap<>();

        // 等待处理依赖的字段
        Map<Field, Object> pendingProcessDependComponents = new HashMap<>();


        for (Map.Entry<Class<?>, Map<String, InstanceDefinition>> entry : pendingHandleContainer.getComponents().entrySet())
        {
            Class<?> clazz = entry.getKey();
            Map<String, InstanceDefinition> instanceMap = entry.getValue();

            for (Map.Entry<String, InstanceDefinition> instanceDef : instanceMap.entrySet())
            {
                InstanceDefinition definition = instanceDef.getValue();

                for (Field declaredField : clazz.getDeclaredFields())
                {
                    declaredField.setAccessible(true);

                    if (declaredField.isAnnotationPresent(Assign.class))
                    {
                        Class<?> declaredFieldType = declaredField.getType();

                        Object dependenceObject;

                        if (declaredFieldType == ComponentContainer.class)
                        {
                            dependenceObject = componentContainer;
                        }
                        else if (declaredFieldType == DatabaseManager.class)
                        {
                            dependenceObject = temporaryParams.getDatabaseManager();
                        }
                        else if (Container.class.isAssignableFrom(declaredFieldType))
                        {
                            dependenceObject = temporaryParams.getCustomizeContainers().get(declaredFieldType);
                        }
                        else
                        {
                            Assign assign = declaredField.getDeclaredAnnotation(Assign.class);

                            InstanceDefinition dependence = pendingHandleContainer.getComponent(declaredFieldType, assign.uid());

                            if (dependence == null)
                            {
                                pendingProcessDependComponents.put(declaredField, definition.getOriginalInstance());
                                continue;
                            }
                            else
                            {
                                dependenceObject = dependence.getInstance();
                            }
                        }

                        declaredField.set(definition.getOriginalInstance(), dependenceObject);
                    }
                    else
                    {
                        for (DependenceHandler dependenceHandler : dependenceHandlers)
                        {
                            if (dependenceHandler.handle(declaredField, instanceDef.getValue()))
                            {
                                break;
                            }
                        }
                    }
                }

                // 处理方法相关的注解
                for (Method method : entry.getKey().getMethods())
                {
                    if (method.isAnnotationPresent(ToContainer.class))
                    {
                        ToContainer annotation = method.getDeclaredAnnotation(ToContainer.class);

                        Collection<DependencyDesc> listDep;
                        if (Collection.class.isAssignableFrom(method.getReturnType()))
                        {
                            listDep = (Collection<DependencyDesc>) method.invoke(definition.getOriginalInstance());
                        }
                        else
                        {
                            listDep = List.of(new DependencyDesc(
                                    method.getReturnType(), method.invoke(definition.getOriginalInstance()), annotation.uid()));
                        }

                        for (DependencyDesc dependencyDesc : listDep)
                        {
                            InstanceDefinition pushDefinition = new InstanceDefinition();
                            pushDefinition.setOriginalInstance(dependencyDesc.instance());

                            Map<String, InstanceDefinition> insMap = loopingPutComponents.computeIfAbsent(dependencyDesc.clazz(), m -> new HashMap<>());
                            insMap.put(dependencyDesc.uid(), pushDefinition);
                        }
                    }
                    else if (method.isAnnotationPresent(Exec.class))
                    {
                        Exec annotation = method.getDeclaredAnnotation(Exec.class);

                        String levelArgValue = Params.LAUNCH_ARGS.get(ARG_NAME_EXEC_PRIORITY);

                        int level = levelArgValue == null ? 0 : Integer.parseInt(levelArgValue);

                        if (annotation.value().getLevel() > level)
                        {
                            continue;
                        }

                        method.invoke(definition.getOriginalInstance());
                    }
                }
            }
        }

        componentContainer.addComponents(loopingPutComponents);
        pendingHandleContainer.addComponents(loopingPutComponents);

        for (Map.Entry<Field, Object> entry : pendingProcessDependComponents.entrySet())
        {
            Field entryKey = entry.getKey();

            Assign assign = entryKey.getDeclaredAnnotation(Assign.class);

            InstanceDefinition def = pendingHandleContainer.getComponent(entryKey.getType(), assign.uid());

            if (def == null)
            {
                if (assign.require())
                {
                    throw new NullPointerException("not found instance: " + entryKey.getType());
                }
            }
            else
            {
                entryKey.set(entry.getValue(), def.getInstance());
            }
        }
    }
}
