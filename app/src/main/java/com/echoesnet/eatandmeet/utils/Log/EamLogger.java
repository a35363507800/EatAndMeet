package com.echoesnet.eatandmeet.utils.Log;

/**
 * Created by ben on 2017/5/11.
 */

public final class EamLogger
{
    private static final String DEFAULT_TAG = "EAM_LOGGER";
    private static Printer printer = new LoggerPrinter();

    private EamLogger()
    {
    }

    public static Settings init()
    {
        return init(DEFAULT_TAG);
    }

    public static Settings init(String tag)
    {
        printer = new LoggerPrinter();
        return printer.init(tag);
    }

    public static Printer t(String tag)
    {
        return printer.t(tag);
    }

    public static void writeToFile(String msg, String filePath)
    {
        printer.writeToFile(msg, filePath);
    }

    public static void writeToDefaultFile(String msg)
    {
        writeToFile(msg, null);
    }

    public static void clear()
    {
        printer.clear();
        printer = null;
    }
}
