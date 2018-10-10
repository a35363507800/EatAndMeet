package com.echoesnet.eatandmeet.models.bean;

import java.util.List;

/**
 * Created by an on 2017/4/18 0018.
 */

public class FinishTaskBean {

    /**
     * cash : 奖励余额
     * exp : 奖励经验
     * face : 奖励脸蛋
     * gift : [{"icon":"礼物图标","id":"礼物ID","name":"礼物名","num":"礼物数量"}]
     * exp_icon : 经验图标
     * face_icon : 脸蛋图标
     * meal_icon : 饭票图标
     * cash_icon : 余额图标
     * meal : 奖励饭票
     * "nowLevel":"当前等级",
     "nowExp":"当前经验",
     "nextExp":"下一级经验",
     "nextLevel":"下一等级",
     */

    private String cash;
    private String exp;
    private String face;
    private String exp_icon;
    private String face_icon;
    private String meal_icon;
    private String cash_icon;
    private String nowLevel;
    private String nowExp;
    private String nextExp;
    private String nextLevel;
    private String meal;
    private String reward;
    private List<RewardsBean> gift;

    public String getReward()
    {
        return reward;
    }

    public void setReward(String reward)
    {
        this.reward = reward;
    }

    public String getNowLevel()
    {
        return nowLevel;
    }

    public void setNowLevel(String nowLevel)
    {
        this.nowLevel = nowLevel;
    }

    public String getNowExp()
    {
        return nowExp;
    }

    public void setNowExp(String nowExp)
    {
        this.nowExp = nowExp;
    }

    public String getNextExp()
    {
        return nextExp;
    }

    public void setNextExp(String nextExp)
    {
        this.nextExp = nextExp;
    }

    public String getNextLevel()
    {
        return nextLevel;
    }

    public void setNextLevel(String nextLevel)
    {
        this.nextLevel = nextLevel;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getExp_icon() {
        return exp_icon;
    }

    public void setExp_icon(String exp_icon) {
        this.exp_icon = exp_icon;
    }

    public String getFace_icon() {
        return face_icon;
    }

    public void setFace_icon(String face_icon) {
        this.face_icon = face_icon;
    }

    public String getMeal_icon() {
        return meal_icon;
    }

    public void setMeal_icon(String meal_icon) {
        this.meal_icon = meal_icon;
    }

    public String getCash_icon() {
        return cash_icon;
    }

    public void setCash_icon(String cash_icon) {
        this.cash_icon = cash_icon;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public List<RewardsBean> getGift() {
        return gift;
    }

    public void setGift(List<RewardsBean> gift) {
        this.gift = gift;
    }

    public static class RewardsBean
    {
        /**
         * icon : 礼物图标
         * id : 礼物ID
         * name : 礼物名
         * num : 礼物数量
         */

        private String icon;
        private String id;
        private String name;
        private String num;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        @Override
        public String toString() {
            return "RewardsBean{" +
                    "icon='" + icon + '\'' +
                    ", id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", num='" + num + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FinishTaskBean{" +
                "cash='" + cash + '\'' +
                ", exp='" + exp + '\'' +
                ", face='" + face + '\'' +
                ", exp_icon='" + exp_icon + '\'' +
                ", face_icon='" + face_icon + '\'' +
                ", meal_icon='" + meal_icon + '\'' +
                ", cash_icon='" + cash_icon + '\'' +
                ", meal='" + meal + '\'' +
                ", gift=" + gift +
                '}';
    }
}
