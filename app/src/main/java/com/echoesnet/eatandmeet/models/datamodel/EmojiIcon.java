package com.echoesnet.eatandmeet.models.datamodel;

public class EmojiIcon
{
    public EmojiIcon()
    {
    }

    /**
     * constructor
     *
     * @param icon-      resource id of the icon
     * @param emojiText- text of emoji icon
     */
    public EmojiIcon(int icon, String emojiText)
    {
        this.icon = icon;
        this.emojiText = emojiText;
        this.type = Type.NORMAL;
    }

    /**
     * constructor
     *
     * @param icon      - resource id of the icon
     * @param emojiText - text of emoji icon
     * @param type      - normal or big
     */
    public EmojiIcon(int icon, String emojiText, Type type)
    {
        this.icon = icon;
        this.emojiText = emojiText;
        this.type = type;
    }

    public EmojiIcon(int icon, String emojiText, String emojiId, Type type)
    {
        this.icon = icon;
        this.emojiText = emojiText;
        this.identityCode = emojiId;
        this.type = type;
    }

    /**
     * constructor
     *
     * @param iconPath  - resource id of the icon
     * @param emojiText - text of emoji icon
     * @param type      - normal or big
     */
    public EmojiIcon(String iconPath, String emojiText, Type type)
    {
        this.iconPath = iconPath;
        this.emojiText = emojiText;
        this.type = type;
    }


    /**
     * identity code
     */
    private String identityCode;

    /**
     * static icon resource id
     */
    private int icon;

    /**
     * dynamic icon resource id
     */
    private int bigIcon;

    /**
     * text of emoji, could be null for big icon
     */
    private String emojiText;

    /**
     * name of emoji icon
     */
    private String name;

    /**
     * normal or big
     */
    private Type type;

    /**
     * path of icon
     */
    private String iconPath;

    /**
     * path of big icon
     */
    private String bigIconPath;

    /**
     * icon的网络地址
     */
    private String bitIconNetPath;


    /**
     * get the resource id of the icon
     *
     * @return
     */
    public int getIcon()
    {
        return icon;
    }


    /**
     * set the resource id of the icon
     *
     * @param icon
     */
    public void setIcon(int icon)
    {
        this.icon = icon;
    }


    /**
     * get the resource id of the big icon
     *
     * @return
     */
    public int getBigIcon()
    {
        return bigIcon;
    }


    /**
     * set the resource id of the big icon
     *
     * @return
     */
    public void setBigIcon(int dynamicIcon)
    {
        this.bigIcon = dynamicIcon;
    }


    /**
     * get text of emoji icon
     *
     * @return
     */
    public String getEmojiText()
    {
        return emojiText;
    }


    /**
     * set text of emoji icon
     *
     * @param emojiText
     */
    public void setEmojiText(String emojiText)
    {
        this.emojiText = emojiText;
    }

    /**
     * get name of emoji icon
     *
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * set name of emoji icon
     *
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * get type
     *
     * @return
     */
    public Type getType()
    {
        return type;
    }


    /**
     * set type
     *
     * @param type
     */
    public void setType(Type type)
    {
        this.type = type;
    }


    /**
     * get icon path
     *
     * @return
     */
    public String getIconPath()
    {
        return iconPath;
    }


    /**
     * set icon path
     *
     * @param iconPath
     */
    public void setIconPath(String iconPath)
    {
        this.iconPath = iconPath;
    }


    /**
     * get path of big icon
     *
     * @return
     */
    public String getBigIconPath()
    {
        return bigIconPath;
    }


    /**
     * set path of big icon
     *
     * @param bigIconPath
     */
    public void setBigIconPath(String bigIconPath)
    {
        this.bigIconPath = bigIconPath;
    }

    /**
     * get identity code
     *
     * @return
     */
    public String getIdentityCode()
    {
        return identityCode;
    }

    public String getBitIconNetPath()
    {
        return bitIconNetPath;
    }

    public void setBitIconNetPath(String bitIconNetPath)
    {
        this.bitIconNetPath = bitIconNetPath;
    }

    /**
     * set identity code
     *
     * @param identityId
     */
    public void setIdentityCode(String identityCode)
    {
        this.identityCode = identityCode;
    }

    public static final String newEmojiText(int codePoint)
    {
        if (Character.charCount(codePoint) == 1)
        {
            return String.valueOf(codePoint);
        }
        else
        {
            return new String(Character.toChars(codePoint));
        }
    }


    public enum Type
    {
        /**
         * normal icon, can be input one or more in edit view
         */
        NORMAL,
        /**
         * 小海豚 表情
         */
        NORMAL_AS_EXPRESSION,
        /**
         * big icon, send out directly when your press it
         */
        BIG_EXPRESSION
    }

    @Override
    public String toString()
    {
        return "EmojiIcon{" +
                "identityCode='" + identityCode + '\'' +
                ", icon=" + icon +
                ", bigIcon=" + bigIcon +
                ", emojiText='" + emojiText + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", iconPath='" + iconPath + '\'' +
                ", bigIconPath='" + bigIconPath + '\'' +
                ", bitIconNetPath='" + bitIconNetPath + '\'' +
                '}';
    }
}
