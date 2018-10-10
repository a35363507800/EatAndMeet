/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.echoesnet.eatandmeet.models.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.echoesnet.eatandmeet.utils.CommonUtils;

public class EaseUser implements Parcelable
{
    protected String initialLetter;//initial letter for nickname
    private String username;
    protected String avatar;//头像地址 of the user
    private String uId;
    private String id;
    private int mealTotal;
    private String userType = "0";
    private String tag;
    private String level;
    private String age;
    private String sex;
    private String nickName;
    private String remark;//备注
    private String isVuser = "";//大V

    public EaseUser(String username)
    {
        this.username = username;
    }

    public String getInitialLetter()
    {
        if (initialLetter == null)
        {
            CommonUtils.setUserInitialLetter(this);
        }
        return initialLetter;
    }

    public void setInitialLetter(String initialLetter)
    {
        this.initialLetter = initialLetter;
    }


    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public String getUserType()
    {
        return userType;
    }

    public void setUserType(String userType)
    {
        this.userType = userType;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public int getMealTotal()
    {
        return mealTotal;
    }

    public void setMealTotal(int mealTotal)
    {
        this.mealTotal = mealTotal;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getIsVuser()
    {
        return isVuser;
    }

    public void setIsVuser(String isVuser)
    {
        this.isVuser = isVuser;
    }

    @Override
    public int hashCode()
    {
        return 17 * getUsername().hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof EaseUser))
        {
            return false;
        }
        return getUsername().equals(((EaseUser) o).getUsername());
    }

    @Override
    public String toString()
    {
        return "EaseUser{" +
                "initialLetter='" + initialLetter + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", uId='" + uId + '\'' +
                ", id='" + id + '\'' +
                ", mealTotal=" + mealTotal +
                ", userType='" + userType + '\'' +
                ", tag='" + tag + '\'' +
                ", level='" + level + '\'' +
                ", age='" + age + '\'' +
                ", sex='" + sex + '\'' +
                ", nickName='" + nickName + '\'' +
                ", remark='" + remark + '\'' +
                ", isVuser='" + isVuser + '\'' +
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
        dest.writeString(this.initialLetter);
        dest.writeString(this.username);
        dest.writeString(this.avatar);
        dest.writeString(this.uId);
        dest.writeString(this.id);
        dest.writeInt(this.mealTotal);
        dest.writeString(this.userType);
        dest.writeString(this.tag);
        dest.writeString(this.level);
        dest.writeString(this.age);
        dest.writeString(this.sex);
        dest.writeString(this.nickName);
        dest.writeString(this.isVuser);
        dest.writeString(this.remark);
    }

    protected EaseUser(Parcel in)
    {
        this.initialLetter = in.readString();
        this.username = in.readString();
        this.avatar = in.readString();
        this.uId = in.readString();
        this.id = in.readString();
        this.mealTotal = in.readInt();
        this.userType = in.readString();
        this.tag = in.readString();
        this.level = in.readString();
        this.age = in.readString();
        this.sex = in.readString();
        this.nickName = in.readString();
        this.isVuser = in.readString();
        this.remark = in.readString();
    }

    public static final Parcelable.Creator<EaseUser> CREATOR = new Parcelable.Creator<EaseUser>()
    {
        @Override
        public EaseUser createFromParcel(Parcel source)
        {
            return new EaseUser(source);
        }

        @Override
        public EaseUser[] newArray(int size)
        {
            return new EaseUser[size];
        }
    };

    public String getUsername()
    {
        return username;
    }
}
