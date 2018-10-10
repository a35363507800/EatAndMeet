package com.echoesnet.eatandmeet.activities.liveplay.managers;

public class SlidGift
{
    public static final class GiftRecord
    {
        public String gid;  // 动画唯一识别id
        public String name;
        public String uid;
        public String level;
        public String usrIcon;
        public String disc;
        public String giftImg;
        public String giftNumber;
        public String giftName;
        public String giftType;
        public String giftId;//资源标示
        public String vUser;
        public int giftCountTotal;//叠加数量
        public String mealTotal;
        public String star;
        public String ranking;

        @Override
        public String toString()
        {
            return "GiftRecord{" +
                    "gid='" + gid + '\'' +
                    ", name='" + name + '\'' +
                    ", uid='" + uid + '\'' +
                    ", level='" + level + '\'' +
                    ", usrIcon='" + usrIcon + '\'' +
                    ", disc='" + disc + '\'' +
                    ", giftImg='" + giftImg + '\'' +
                    ", giftNumber='" + giftNumber + '\'' +
                    ", giftName='" + giftName + '\'' +
                    ", giftType='" + giftType + '\'' +
                    ", giftId='" + giftId + '\'' +
                    '}';
        }

        public String getvUser()
        {
            return vUser;
        }
        public int getGiftCountTotal()
        {
            return giftCountTotal;
        }

        public void setGiftCountTotal(int giftCountTotal)
        {
            this.giftCountTotal = giftCountTotal;
        }

    }

    public static final class GiftData
    {
        /**
         * gift : {"gId":"0009","gName":"蓝瘦香菇","gPrice":"5","gType":"0","gUrl":"http://huisheng.ufile.ucloud.cn/14836965171399NhJLT.png","sort":"1"}
         * isGift : 0
         * mealTotal : 5
         * number : 1
         */
        private GiftBean gift;
        private String isGift;
        private String mealTotal;
        private String number;

        public String getvUser()
        {
            return vUser;
        }

        public void setvUser(String vUser)
        {
            this.vUser = vUser;
        }

        private String vUser;
        private String star;
        private String ranking;
        private int countTotal; //礼物叠加数量


        @Override
        public String toString()
        {
            return "GiftData{" +
                    "gift=" + gift +
                    ", isGift='" + isGift + '\'' +
                    ", mealTotal='" + mealTotal + '\'' +
                    ", number='" + number + '\'' +
                    ", countTotal='" + countTotal + '\'' +
                    '}';
        }

        public String getRanking()
        {
            return ranking;
        }

        public void setRanking(String ranking)
        {
            this.ranking = ranking;
        }

        public String getStar()
        {
            return star;
        }

        public void setStar(String star)
        {
            this.star = star;
        }

        public int getCountTotal()
        {
            return countTotal;
        }

        public void setCountTotal(int countTotal)
        {
            this.countTotal = countTotal;
        }

        public GiftBean getGift()
        {
            return gift;
        }

        public void setGift(GiftBean gift)
        {
            this.gift = gift;
        }

        public String getIsGift()
        {
            return isGift;
        }

        public void setIsGift(String isGift)
        {
            this.isGift = isGift;
        }

        public String getMealTotal()
        {
            return mealTotal;
        }

        public void setMealTotal(String mealTotal)
        {
            this.mealTotal = mealTotal;
        }

        public String getNumber()
        {
            return number;
        }

        public void setNumber(String number)
        {
            this.number = number;
        }



        public static class GiftBean
        {

            /**
             * gId : 0009
             * gName : 蓝瘦香菇
             * gPrice : 5
             * gType : 0
             * gUrl : http://huisheng.ufile.ucloud.cn/14836965171399NhJLT.png
             * sort : 1
             */
            private String gId;
            private String gName;
            private String gPrice;
            private String gType;
            private String gUrl;
            private String sort;
            private int countTotal;

            public int getCountTotal()
            {
                return countTotal;
            }

            public void setCountTotal(int countTotal)
            {
                this.countTotal = countTotal;
            }



            public String getGId()
            {
                return gId;
            }

            public void setGId(String gId)
            {
                this.gId = gId;
            }

            public String getGName()
            {
                return gName;
            }

            public void setGName(String gName)
            {
                this.gName = gName;
            }

            public String getGPrice()
            {
                return gPrice;
            }

            public void setGPrice(String gPrice)
            {
                this.gPrice = gPrice;
            }

            public String getGType()
            {
                return gType;
            }

            public void setGType(String gType)
            {
                this.gType = gType;
            }

            public String getGUrl()
            {
                return gUrl;
            }

            public void setGUrl(String gUrl)
            {
                this.gUrl = gUrl;
            }

            public String getSort()
            {
                return sort;
            }

            public void setSort(String sort)
            {
                this.sort = sort;
            }
        }
    }
}
