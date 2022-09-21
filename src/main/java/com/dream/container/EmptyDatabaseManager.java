package com.dream.container;

public class EmptyDatabaseManager implements DatabaseManager {
    @Override
    public void initialize() throws Exception {

    }

    @Override
    public boolean isOpenConnection() {
        return false;
    }

    @Override
    public boolean isUseTransaction() {
        return false;
    }

    @Override
    public void useTransaction() {

    }

    @Override
    public void openConnection(boolean autoCommit) {

    }

    @Override
    public void openConnection(boolean autoCommit, ArgsTransaction args) {

    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public void close() {

    }
}
