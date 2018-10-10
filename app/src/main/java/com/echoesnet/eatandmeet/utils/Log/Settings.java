package com.echoesnet.eatandmeet.utils.Log;

/**
 * Created by ben on 2017/5/11.
 */

public class Settings
{
    private LogLevel logLevel;

    public Settings()
    {
        this.logLevel = LogLevel.FULL;
    }

    /**
     * 获取log的等级
     * @return
     */
    public LogLevel getLogLevel()
    {
        return this.logLevel;
    }

    /**
     * 设置log的等级
     * @param logLevel
     * @return
     */
    public Settings logLevel(LogLevel logLevel)
    {
        this.logLevel = logLevel;
        return this;
    }
}
