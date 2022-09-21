package com.dream.container;

public class ProxyPostProcessArgs
{
    private DatabaseManager databaseManager;

    public ProxyPostProcessArgs() {
    }

    public ProxyPostProcessArgs(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
