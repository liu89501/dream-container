package com.dream.container;

import java.util.Map;

public abstract class Params
{
    static Map<String, String> LAUNCH_ARGS;

    public static final String DEFAULT_UID = "DEFAULT";

    public static Map<String, String> getLaunchArgs()
    {
        return LAUNCH_ARGS;
    }

    public static String getLaunchArg(String argName)
    {
        return LAUNCH_ARGS.get(argName);
    }
}
