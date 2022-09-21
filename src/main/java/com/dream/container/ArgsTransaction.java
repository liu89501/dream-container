package com.dream.container;

public class ArgsTransaction
{
    private boolean batch;

    public ArgsTransaction() {
    }

    public ArgsTransaction(boolean batch) {
        this.batch = batch;
    }

    public boolean isBatch() {
        return batch;
    }
}
