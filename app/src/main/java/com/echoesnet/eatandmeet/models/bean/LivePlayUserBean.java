package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by ben on 2016/10/25.
 */

public class LivePlayUserBean
{
    //id
    private String identifier;
    //头像
    private String faceUrl;
    private String nicName;
    private String isGhost="0";

    private String Role;
    private String JoinTime;
    private String ShutUpUntil; // 0表示未被禁言，否则为禁言的截止时间
    //private Map<String,String> AppMemberDefinedData;

    public String getFaceUrl()
    {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl)
    {
        this.faceUrl = faceUrl;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }

    public String getJoinTime()
    {
        return JoinTime;
    }

    public void setJoinTime(String joinTime)
    {
        JoinTime = joinTime;
    }


    public String getRole()
    {
        return Role;
    }

    public void setRole(String role)
    {
        Role = role;
    }

    public String getShutUpUntil()
    {
        return ShutUpUntil;
    }

    public void setShutUpUntil(String shutUpUntil)
    {
        ShutUpUntil = shutUpUntil;
    }

    public String getNicName()
    {
        return nicName;
    }

    public void setNicName(String nicName)
    {
        this.nicName = nicName;
    }

    public String getIsGhost()
    {
        return isGhost;
    }

    public void setIsGhost(String isGhost)
    {
        this.isGhost = isGhost;
    }

    @Override
    public String toString()
    {
        return "LivePlayUserBean{" +
                "identifier='" + identifier + '\'' +
                ", faceUrl='" + faceUrl + '\'' +
                ", Role='" + Role + '\'' +
                ", JoinTime='" + JoinTime + '\'' +
                ", ShutUpUntil='" + ShutUpUntil + '\'' +
                ", nicName='" + nicName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof LivePlayUserBean))
            return false;
        LivePlayUserBean bean= (LivePlayUserBean) o;
        return this.getIdentifier().equals(bean.getIdentifier());
    }

    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 17;
        result=PRIME*result+ identifier.hashCode();
        return result;
    }
}
