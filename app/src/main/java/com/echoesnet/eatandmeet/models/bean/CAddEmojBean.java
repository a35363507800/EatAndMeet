package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/10/20.
 */

public class CAddEmojBean
{
    private String title;
    private String description;
    private String imgUrl;
    private String emojiFilePath;
    private String emojiZipUrl;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getImgUrl()
    {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl)
    {
        this.imgUrl = imgUrl;
    }

    public String getEmojiFilePath()
    {
        return emojiFilePath;
    }

    public void setEmojiFilePath(String emojiFilePath)
    {
        this.emojiFilePath = emojiFilePath;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getEmojiZipUrl()
    {
        return emojiZipUrl;
    }

    public void setEmojiZipUrl(String emojiZipUrl)
    {
        this.emojiZipUrl = emojiZipUrl;
    }

    @Override
    public String toString()
    {
        return "CAddEmojBean{" +
                "description='" + description + '\'' +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", emojiFilePath='" + emojiFilePath + '\'' +
                ", emojiZipUrl='" + emojiZipUrl + '\'' +
                '}';
    }
}
