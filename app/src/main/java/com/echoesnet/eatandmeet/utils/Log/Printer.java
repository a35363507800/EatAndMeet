package com.echoesnet.eatandmeet.utils.Log;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2017 /5/11
 * @description 打印者的接口
 */
public interface Printer
{
    /**
     * tag.
     *
     * @param var1 the tag
     * @return the printer
     */
    Printer t(String var1);

    /**
     * Init settings.
     *
     * @param var1 tag
     * @return the settings
     */
    Settings init(String var1);

    /**
     * Gets settings.
     *
     * @return the settings
     */
    Settings getSettings();

    /**
     * Write to file.
     *
     * @param msg      the msg
     * @param filePath the file path
     */
    void writeToFile(String msg, String filePath);

    /**
     * Write to default file.
     *
     * @param msg the msg
     */
    void writeToDefaultFile(String msg);

    /**
     * Clear.
     */
    void clear();
}
