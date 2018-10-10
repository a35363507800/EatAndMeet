package com.echoesnet.eatandmeet.http4retrofit2.entity4http;

/**
 * Created by liuyang on 2016/11/10.
 * Refactor by ben on 2017/3/30
 * 用于将后台接口返回数据转换为指定类型
 */

public class ResponseResultSkeleton
{
    /**
     * 接口返回结构
     * {"message":"non-standard access","messageJson":"","status":"1"}
     */

    private String messageJson;
    private String message;
    private String status;

    public String getMessageJson()
    {
        return messageJson;
    }

    public void setMessageJson(String messageJson)
    {
        this.messageJson = messageJson;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ResponseResultSkeleton{" +
                "messageJson='" + messageJson + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
