package com.dream.container;

public interface DatabaseManager
{
    void initialize() throws Exception;

    boolean isOpenConnection();

    boolean isUseTransaction();

    void useTransaction();

    void openConnection(boolean autoCommit);

    void openConnection(boolean autoCommit, ArgsTransaction args);

    void commit();

    void rollback();

    void close();
}
