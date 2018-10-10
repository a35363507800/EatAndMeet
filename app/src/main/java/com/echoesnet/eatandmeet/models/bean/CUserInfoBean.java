package com.echoesnet.eatandmeet.models.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.echoesnet.eatandmeet.utils.CommonUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by lc on 2017/7/17 10.
 */

public class CUserInfoBean implements Parcelable
{

//            "appointmentStatus":"约会状态(0:无1：进行中)",
//            "uId":"uId",
    private String age;//年龄
    private String anchorLevel;//"主播等级"
    private String anchorType;//"主播类型 0 全民 1自由 2兼职 3全职"
    private String constellation;//"星座"
    private String education;//"教育程度"
    private String emState;//"用户情感状况（0：未，1：已）
    private String fansNum;//粉丝数
    private String focus;//"是否关注（0：否1：是）
    private String isSayHello;//打招呼还是聊天（空：查看自己，0：聊天1：打招呼）
    private String height;//"用户身高"
    private String id;//id
    private String imuId;//"环信uid"
    private String inBlack;//"是否拉黑"
    private String inWish;//"是否在心愿单（0：否1：是）
    private String rId;//上次出现的餐厅id
    private String rName;//"上次出现的餐厅名"
    private String level;//个人等级
    private String liveSource;//"直播源"
    private String meal;//饭票数
    private String nicName;//昵称
    private String remark;//"备注"
    private String rmFlg;//是否通过实名认证
    private String roomId;//直播间id
    private String sex;//性别
    private String signature;//"签名"
    private String status;//是否正在直播（0：否1：是)
    private String uId;//uid
    private String upUrls;//相册
    private String uphUrl;//头像
    private String birth;//生日
    private String income;//收入
    private String occupation;//职业
    private String ghost;//是否是幽灵用户
    private String isVuser;//是否是大V用户  "0"否；"1"是


    private String appointmentStatus;
    private String focusNum;


    //private Map<String,String> privilege;
    private Privileage privilege;

    public String getIsVuser()
    {
        return isVuser;
    }

    public void setIsVuser(String isVuser)
    {
        this.isVuser = isVuser;
    }

    public String getIsSayHello()
    {
        return isSayHello;
    }

    public void setIsSayHello(String isSayHello)
    {
        this.isSayHello = isSayHello;
    }

    public String getGhost()
    {
        return ghost;
    }

    public void setGhost(String ghost)
    {
        this.ghost = ghost;
    }

    public String getBirth()
    {
        return birth;
    }

    public void setBirth(String birth)
    {
        this.birth = birth;
    }

    public String getOccupation()
    {
        return occupation;
    }

    public void setOccupation(String occupation)
    {
        this.occupation = occupation;
    }

    public String getIncome()
    {
        return income;
    }

    public void setIncome(String income)
    {
        this.income = income;
    }

    public String getMeal()
    {
        return meal;
    }

    public String getConstellation()
    {
        return constellation;
    }

    public void setConstellation(String constellation)
    {
        this.constellation = constellation;
    }

    public static Creator<CUserInfoBean> getCREATOR()
    {
        return CREATOR;
    }

    public void setMeal(String meal)
    {
        this.meal = meal;
    }

    public Privileage getPrivilege()
    {
        return privilege;
    }

    public void setPrivilege(Privileage privilege)
    {
        this.privilege = privilege;
    }

    public List<String> getImgUrls()
    {
        return CommonUtils.strWithSeparatorToList(getUpUrls(),CommonUtils.SEPARATOR);
    }
    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getAppointmentStatus()
    {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus)
    {
        this.appointmentStatus = appointmentStatus;
    }

    public String getFansNum()
    {
        return fansNum;
    }

    public void setFansNum(String fansNum)
    {
        this.fansNum = fansNum;
    }

    public String getFocusNum()
    {
        return focusNum;
    }

    public void setFocusNum(String focusNum)
    {
        this.focusNum = focusNum;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getNicName()
    {
        return nicName;
    }

    public void setNicName(String nicName)
    {
        this.nicName = nicName;
    }


    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }


    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public String getUphUrl()
    {
        return uphUrl;
    }

    public void setUphUrl(String uphUrl)
    {
        this.uphUrl = uphUrl;
    }

    public String getAnchorLevel()
    {
        return anchorLevel;
    }

    public void setAnchorLevel(String anchorLevel)
    {
        this.anchorLevel = anchorLevel;
    }

    public String getAnchorType()
    {
        return anchorType;
    }

    public void setAnchorType(String anchorType)
    {
        this.anchorType = anchorType;
    }

    public String getEducation()
    {
        return education;
    }

    public void setEducation(String education)
    {
        this.education = education;
    }

    public String getEmState()
    {
        return emState;
    }

    public void setEmState(String emState)
    {
        this.emState = emState;
    }

    public String getFocus()
    {
        return focus;
    }

    public void setFocus(String focus)
    {
        this.focus = focus;
    }

    public String getHeight()
    {
        return height;
    }

    public void setHeight(String height)
    {
        this.height = height;
    }

    public String getImuId()
    {
        return imuId;
    }

    public void setImuId(String imuId)
    {
        this.imuId = imuId;
    }

    public String getInBlack()
    {
        return inBlack;
    }

    public void setInBlack(String inBlack)
    {
        this.inBlack = inBlack;
    }

    public String getInWish()
    {
        return inWish;
    }

    public void setInWish(String inWish)
    {
        this.inWish = inWish;
    }

    public String getrId()
    {
        return rId;
    }

    public void setrId(String rId)
    {
        this.rId = rId;
    }

    public String getrName()
    {
        return rName;
    }

    public void setrName(String rName)
    {
        this.rName = rName;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getRmFlg()
    {
        return rmFlg;
    }

    public void setRmFlg(String rmFlg)
    {
        this.rmFlg = rmFlg;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    public String getRoomId()
    {
        return roomId;
    }

    public void setRoomId(String roomId)
    {
        this.roomId = roomId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getLiveSource()
    {
        return liveSource;
    }

    public void setLiveSource(String liveSource)
    {
        this.liveSource = liveSource;
    }

    public String getUpUrls()
    {
        return upUrls;
    }

    public void setUpUrls(String upUrls)
    {
        this.upUrls = upUrls;
    }

    public static class Privileage implements Parcelable
    {
        private String id;

        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(this.id);
        }

        public Privileage()
        {
        }

        protected Privileage(Parcel in)
        {
            this.id = in.readString();
        }

        public static final Creator<Privileage> CREATOR = new Creator<Privileage>()
        {
            @Override
            public Privileage createFromParcel(Parcel source)
            {
                return new Privileage(source);
            }

            @Override
            public Privileage[] newArray(int size)
            {
                return new Privileage[size];
            }
        };
    }


    @Override
    public String toString()
    {
        return "CUserInfoBean{" +
                "age='" + age + '\'' +
                ", anchorLevel='" + anchorLevel + '\'' +
                ", anchorType='" + anchorType + '\'' +
                ", constellation='" + constellation + '\'' +
                ", education='" + education + '\'' +
                ", emState='" + emState + '\'' +
                ", fansNum='" + fansNum + '\'' +
                ", focus='" + focus + '\'' +
                ", isSayHello='" + isSayHello + '\'' +
                ", height='" + height + '\'' +
                ", id='" + id + '\'' +
                ", imuId='" + imuId + '\'' +
                ", inBlack='" + inBlack + '\'' +
                ", inWish='" + inWish + '\'' +
                ", rId='" + rId + '\'' +
                ", rName='" + rName + '\'' +
                ", level='" + level + '\'' +
                ", liveSource='" + liveSource + '\'' +
                ", meal='" + meal + '\'' +
                ", nicName='" + nicName + '\'' +
                ", remark='" + remark + '\'' +
                ", rmFlg='" + rmFlg + '\'' +
                ", roomId='" + roomId + '\'' +
                ", sex='" + sex + '\'' +
                ", signature='" + signature + '\'' +
                ", status='" + status + '\'' +
                ", uId='" + uId + '\'' +
                ", upUrls='" + upUrls + '\'' +
                ", uphUrl='" + uphUrl + '\'' +
                ", birth='" + birth + '\'' +
                ", income='" + income + '\'' +
                ", occupation='" + occupation + '\'' +
                ", ghost='" + ghost + '\'' +
                ", isVuser='" + isVuser + '\'' +
                ", appointmentStatus='" + appointmentStatus + '\'' +
                ", focusNum='" + focusNum + '\'' +
                ", privilege=" + privilege +
                '}';
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.age);
        dest.writeString(this.meal);
        dest.writeString(this.appointmentStatus);
        dest.writeString(this.fansNum);
        dest.writeString(this.focusNum);
        dest.writeString(this.id);
        dest.writeString(this.level);
        dest.writeString(this.nicName);
        dest.writeString(this.sex);
        dest.writeString(this.uId);
        dest.writeString(this.uphUrl);
        dest.writeString(this.anchorLevel);
        dest.writeString(this.anchorType);
        dest.writeString(this.education);
        dest.writeString(this.emState);
        dest.writeString(this.focus);
        dest.writeString(this.height);
        dest.writeString(this.imuId);
        dest.writeString(this.inBlack);
        dest.writeString(this.inWish);
        dest.writeString(this.rId);
        dest.writeString(this.rName);
        dest.writeString(this.remark);
        dest.writeString(this.rmFlg);
        dest.writeString(this.signature);
        dest.writeString(this.roomId);
        dest.writeString(this.status);
        dest.writeString(this.liveSource);
        dest.writeString(this.upUrls);
        dest.writeParcelable(this.privilege, flags);
    }

    public CUserInfoBean()
    {
    }

    protected CUserInfoBean(Parcel in)
    {
        this.age = in.readString();
        this.meal = in.readString();
        this.appointmentStatus = in.readString();
        this.fansNum = in.readString();
        this.focusNum = in.readString();
        this.id = in.readString();
        this.level = in.readString();
        this.nicName = in.readString();
        this.sex = in.readString();
        this.uId = in.readString();
        this.uphUrl = in.readString();
        this.anchorLevel = in.readString();
        this.anchorType = in.readString();
        this.education = in.readString();
        this.emState = in.readString();
        this.focus = in.readString();
        this.height = in.readString();
        this.imuId = in.readString();
        this.inBlack = in.readString();
        this.inWish = in.readString();
        this.rId = in.readString();
        this.rName = in.readString();
        this.remark = in.readString();
        this.rmFlg = in.readString();
        this.signature = in.readString();
        this.roomId = in.readString();
        this.status = in.readString();
        this.liveSource = in.readString();
        this.upUrls = in.readString();
        this.privilege = in.readParcelable(Privileage.class.getClassLoader());
    }

    public static final Parcelable.Creator<CUserInfoBean> CREATOR = new Parcelable.Creator<CUserInfoBean>()
    {
        @Override
        public CUserInfoBean createFromParcel(Parcel source)
        {
            return new CUserInfoBean(source);
        }

        @Override
        public CUserInfoBean[] newArray(int size)
        {
            return new CUserInfoBean[size];
        }
    };
}
