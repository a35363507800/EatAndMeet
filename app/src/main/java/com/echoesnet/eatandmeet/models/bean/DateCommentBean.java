package com.echoesnet.eatandmeet.models.bean;

import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.views.adapters.DateInfoRecycleViewAdapter;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lc on 2017/7/20 13.
 */

public class DateCommentBean
{
//     "status":"0可以约 1约会中",
//             "desc":"描述",
//             "price":"价格",
//             "evaluate":[
//    {
//        "uId":"uId",
//            "nicName":"昵称",
//            "phUrl":"头像",
//            "time":"发布时间",
//            "json":"还是之前那个格式",
//            "level":"等级",
//            "age":"年龄",
//            "sex":"性别"
//    }
//        ]
        private String uId;
        private String nicName;
        private String phUrl;
        private String time;
        private String json;
        private String level;
        private String age;
        private String sex;
        private String isVuser;
        private int starNum;

    public int getStarNum()
    {
        return starNum;
    }

    public void setStarNum()
    {
      int num = 0;
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            final String commentStr = jsonObject.getString("commentStr");
            String commentBeanList = jsonObject.getString("commentBeanList");
            List<StarCommentBean> mgsList = EamApplication.getInstance()
                    .getGsonInstance().fromJson(commentBeanList, new TypeToken<List<StarCommentBean>>()
                    {
                    }.getType());
            for (int i = 0; i < mgsList.size(); i++)
            {
                if (mgsList.get(i).getIsState().equals("true"))
                {
                    num++;
                }
            }
            this.starNum = num;
        } catch (Exception e)
        {
            this.starNum = num;
            e.printStackTrace();
        }

    }

    public String getIsVuser()
    {
        return isVuser;
    }

    public void setIsVuser(String isVuser)
    {
        this.isVuser = isVuser;
    }

    public String getuId()
        {
            return uId;
        }

        public void setuId(String uId)
        {
            this.uId = uId;
        }

        public String getNicName()
        {
            return nicName;
        }

        public void setNicName(String nicName)
        {
            this.nicName = nicName;
        }

        public String getPhUrl()
        {
            return phUrl;
        }

        public void setPhUrl(String phUrl)
        {
            this.phUrl = phUrl;
        }

        public String getTime()
        {
            return time;
        }

        public void setTime(String time)
        {
            this.time = time;
        }

        public String getJson()
        {
            return json;
        }

        public void setJson(String json)
        {
            this.json = json;
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

    @Override
    public String toString()
    {
        return "DateCommentBean{" +
                "uId='" + uId + '\'' +
                ", nicName='" + nicName + '\'' +
                ", phUrl='" + phUrl + '\'' +
                ", time='" + time + '\'' +
                ", json='" + json + '\'' +
                ", level='" + level + '\'' +
                ", age='" + age + '\'' +
                ", sex='" + sex + '\'' +
                ", isVuser='" + isVuser + '\'' +
                '}';
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return super.equals(obj);
    }
}
