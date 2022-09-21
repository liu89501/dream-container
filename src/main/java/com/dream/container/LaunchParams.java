package com.dream.container;

import java.util.Map;

public abstract class LaunchParams
{
    static Map<String, String> LAUNCH_ARGS;

    public static final String DEFAULT_UID = "DEFAULT";

    public static boolean hasLaunchArgs()
    {
        return LAUNCH_ARGS != null;
    }

    public static Map<String, String> getLaunchArgs()
    {
        return LAUNCH_ARGS;
    }

    public static String getLaunchArg(String argName)
    {
        return hasLaunchArgs() ? LAUNCH_ARGS.get(argName) : null;
    }
}
