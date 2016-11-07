package edu.vt.beacon.editor.util;

public class PlatformManager
{
    // FIXME complete method
    public static void initializeApplication()
    {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
    }
    
    // TODO document method
    public static boolean isMacPlatform()
    {
        return System.getProperty("os.name").contains("Mac OS X");
    }
}