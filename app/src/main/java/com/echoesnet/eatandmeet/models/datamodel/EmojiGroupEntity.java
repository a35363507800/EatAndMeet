package com.echoesnet.eatandmeet.models.datamodel;


import java.util.List;

/**
 * 一组表情所对应的实体类
 */
public class EmojiGroupEntity
{
    /**
     * 表情数据
     */
    private List<EmojiIcon> emojiconList;
    /**
     * 图片
     */
    private int icon;
    private String iconPath;
    /**
     * 组名
     */
    private String name;
    /**
     * 表情类型
     */
    private EmojiIcon.Type type;

    public EmojiGroupEntity()
    {
    }

    public EmojiGroupEntity(int icon, List<EmojiIcon> emojiconList)
    {
        this.icon = icon;
        this.emojiconList = emojiconList;
        type = EmojiIcon.Type.NORMAL;
    }

    public EmojiGroupEntity(int icon, List<EmojiIcon> emojiconList, EmojiIcon.Type type)
    {
        this.icon = icon;
        this.emojiconList = emojiconList;
        this.type = type;
    }

    public List<EmojiIcon> getEmojiconList()
    {
        return emojiconList;
    }

    public void setEmojiconList(List<EmojiIcon> emojiconList)
    {
        this.emojiconList = emojiconList;
    }

    public int getIcon()
    {
        return icon;
    }

    public void setIcon(int icon)
    {
        this.icon = icon;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public EmojiIcon.Type getType()
    {
        return type;
    }

    public void setType(EmojiIcon.Type type)
    {
        this.type = type;
    }

    public String getIconPath()
    {
        return iconPath;
    }

    public void setIconPath(String iconPath)
    {
        this.iconPath = iconPath;
    }
}
