package com.dream.container.anno;

public enum EExecPriority
{
    LOW(1),
    MIDDLE(2),
    HIGH(3);

    final int level;

    EExecPriority(int level)
    {
        this.level = level;
    }

    public int getLevel()
    {
        return level;
    }
}
