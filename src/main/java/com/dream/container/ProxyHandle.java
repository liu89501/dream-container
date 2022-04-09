package com.dream.container;

import com.dream.container.anno.Transaction;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

public record ProxyHandle(Object superInstance, Container proxyProcessorContainer, DatabaseManager databaseManager) implements InvocationHandler
{
    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable
    {
        // 属于父类的并且未重写的方法不处理
        if (method.getDeclaringClass() != superInstance.getClass())
        {
            return method.invoke(superInstance, objects);
        }

        if (databaseManager.isOpenConnection())
        {
            if (!databaseManager.isUseTransaction() && method.isAnnotationPresent(Transaction.class))
            {
                databaseManager.useTransaction();
            }

            return method.invoke(superInstance, objects);

        }
        else
        {
            Transaction transaction = method.getDeclaredAnnotation(Transaction.class);
            boolean useAnnotation = transaction != null;

            databaseManager.openConnection(false, new ArgsTransaction(useAnnotation && transaction.batch()));

            if (useAnnotation)
            {
                databaseManager.useTransaction();
            }
        }

        try
        {
            Object returnValue = method.invoke(superInstance, objects);
            databaseManager.commit();

            return returnValue;
        }
        catch (Throwable e)
        {
            databaseManager.rollback();
            throw e;
        }
        finally
        {
            try
            {
                for (InstanceDefinition instanceDefinition : proxyProcessorContainer.getInstances())
                {
                    ProxyMethodPostProcessor postProcessor = (ProxyMethodPostProcessor) instanceDefinition.getInstance();
                    postProcessor.postProcess(new ProxyPostProcessArgs(databaseManager));
                }
            }
            catch (Throwable e)
            {
                LogContainer.LOG.error("postProcessors error", e);
            }

            databaseManager.close();
        }
    }
}
