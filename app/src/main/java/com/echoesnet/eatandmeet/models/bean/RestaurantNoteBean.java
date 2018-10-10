package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2017/7/15.
 */

public class RestaurantNoteBean
{
    private String noteTitle;
    private String noteContext;
    private String noteEnd;
    private boolean isCollapsed = true;

    public String getNoteTitle()
    {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle)
    {
        this.noteTitle = noteTitle;
    }

    public String getNoteContext()
    {
        return noteContext;
    }

    public void setNoteContext(String noteContext)
    {
        this.noteContext = noteContext;
    }

    public String getNoteEnd()
    {
        return noteEnd;
    }

    public void setNoteEnd(String noteEnd)
    {
        this.noteEnd = noteEnd;
    }

    public boolean isCollapsed()
    {
        return isCollapsed;
    }

    public void setCollapsed(boolean collapsed)
    {
        isCollapsed = collapsed;
    }
}
