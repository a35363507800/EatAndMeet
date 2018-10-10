package com.echoesnet.eatandmeet.models.bean;

import java.util.List;

/**
 * Created by liuyang on 2017/2/7.
 */

public class LiveRoomMemberBean
{


    /**
     * res : [{"headImg":"http://huisheng.ufile.ucloud.cn/a_13821619472487c9232.jpg","id":"u100014","isGhost":"0"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483961439930.jpg","id":"u101695","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483961288121.jpg","id":"u101408","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483961557768.jpg","id":"u101925","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483961623453.jpg","id":"u102077","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483961161071.jpg","id":"u101119","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483960831573.jpg","id":"u100454","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483961027582.jpg","id":"u100854","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483961392427.jpg","id":"u101590","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483960809330.jpg","id":"u100403","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483961122721.jpg","id":"u101036","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483961002849.jpg","id":"u100840","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483960750807.jpg","id":"u100267","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483961142509.jpg","id":"u101082","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483960839466.jpg","id":"u100471","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483960704255.jpg","id":"u100149","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483961155614.jpg","id":"u101107","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483961696165.jpg","id":"u102242","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483961307615.jpg","id":"u101447","isGhost":"1"},{"headImg":"http://huisheng.ufile.ucloud.cn/test/1483961343505.jpg","id":"u101524","isGhost":"1"}]
     * num : 704
     */

    private int num;
    private List<ResBean> res;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<ResBean> getRes() {
        return res;
    }

    public void setRes(List<ResBean> res) {
        this.res = res;
    }

    public static class ResBean {
        /**
         "headImg":"http://huisheng.ufile.ucloud.cn/test/1493100246741.jpg",
         "id":"101433",
         "isGhost":"1",
         "level":"0",
         "uId":"4dc186f6-f69d-4394-a4c8-eeea35431fef"
         */

        private String headImg;
        private String id;
        private String isGhost;
        private String level;
        private String imuId;
        private String uId;
        private String isVuser;

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

        public String getUid()
        {
            return uId;
        }

        public void setUid(String uid)
        {
            this.uId = uid;
        }

        public String getImuId()
        {
            return imuId;
        }

        public void setImuId(String imuId)
        {
            this.imuId = imuId;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIsGhost() {
            return isGhost;
        }

        public void setIsGhost(String isGhost) {
            this.isGhost = isGhost;
        }

        public String getLevel()
        {
            return level;
        }

        public void setLevel(String level)
        {
            this.level = level;
        }

        @Override
        public String toString()
        {
            return "ResBean{" +
                    "headImg='" + headImg + '\'' +
                    ", id='" + id + '\'' +
                    ", isGhost='" + isGhost + '\'' +
                    ", level='" + level + '\'' +
                    ", imuId='" + imuId + '\'' +
                    ", uid='" + uId + '\'' +
                    '}';
        }
    }

    @Override
    public String toString()
    {
        return "LiveRoomMemberBean{" +
                "num=" + num +
                ", res=" + res +
                '}';
    }
}
