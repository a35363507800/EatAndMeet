package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by liuyang on 2016/11/10.
 * Refactor by ben
 */

public class TencentIMHttpResult
{
    /**
     * ActionStatus : FAIL
     * ErrorCode : 10010
     * ErrorInfo : this group does not exist
     */

    private String ActionStatus;
    private int ErrorCode;
    private String ErrorInfo;
    /**
     * MsgSeq : 964
     */

    private String MsgSeq;

    public String getActionStatus()
    {
        return ActionStatus;
    }

    public void setActionStatus(String ActionStatus)
    {
        this.ActionStatus = ActionStatus;
    }

    public int getErrorCode()
    {
        return ErrorCode;
    }

    public void setErrorCode(int ErrorCode)
    {
        this.ErrorCode = ErrorCode;
    }

    public String getErrorInfo()
    {
        return ErrorInfo;
    }

    public void setErrorInfo(String ErrorInfo)
    {
        this.ErrorInfo = ErrorInfo;
    }

    public String getMsgSeq()
    {
        return MsgSeq;
    }

    public void setMsgSeq(String MsgSeq)
    {
        this.MsgSeq = MsgSeq;
    }

    //    {"ActionStatus":"FAIL","ErrorCode":10010,"ErrorInfo":"this group does not exist"}
//    {"ActionStatus":"OK","ErrorCode":0,"MsgSeq":964}


    @Override
    public String toString() {
        return "TencentIMHttpResult{" +
                "ActionStatus='" + ActionStatus + '\'' +
                ", ErrorCode=" + ErrorCode +
                ", ErrorInfo='" + ErrorInfo + '\'' +
                ", MsgSeq='" + MsgSeq + '\'' +
                '}';
    }
}
