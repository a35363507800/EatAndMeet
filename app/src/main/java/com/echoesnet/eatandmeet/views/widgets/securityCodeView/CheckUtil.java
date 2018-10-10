package com.echoesnet.eatandmeet.views.widgets.securityCodeView;

/**
 * 功能：验证码相关工具类
 * */
public class CheckUtil
{
    /**
     * 产生随机数字
     * @return
     */
    public static int [] getCheckNum(){
        int [] tempCheckNum = new int[Config.TEXT_LENGTH];
        for(int i = 0; i < Config.TEXT_LENGTH; i++){
            tempCheckNum[i] = (int) (Math.random() * 10);
        }
        return tempCheckNum;
    }
    /**
     * 随机产生划线的起始点坐标和结束点坐标
     * @param height 传入CheckView的高度值
     * @param width 传入CheckView的宽度值
     * @return 起始点坐标和结束点坐标
     */
    public static int[] getLine(int height, int width){
        int [] tempCheckNum = {0,0,0,0};
        for(int i = 0; i < 4; i+=2){
            tempCheckNum[i] = (int) (Math.random() * width);
            tempCheckNum[i + 1] = (int) (Math.random() * height);
        }
        return tempCheckNum;
    }
    /**
     * 随机产生点的圆心点坐标
     * @param height 传入CheckView的高度值
     * @param width 传入CheckView的宽度值
     * @return
     */
    public static int[] getPoint(int height, int width){
        int [] tempCheckNum = {0,0,0,0};
        tempCheckNum[0] = (int) (Math.random() * width);
        tempCheckNum[1] = (int) (Math.random() * height);
        return tempCheckNum;
    }
    /**
     *  验证是否正确
     * @param userCheck 用户输入的验证码
     * @param checkNum  验证控件产生的随机数
     * @return
     */
    public static boolean checkNum(String userCheck, int[] checkNum){
        if(userCheck.length() != 4 ){
            return false;
        }
        String checkString = "";
        for (int i = 0; i < 4; i++) {
            checkString += checkNum[i];
        }
        if(userCheck.equals(checkString)){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 计算验证码的绘制y点位置
     * @param height 传入CheckView的高度值
     * @param offsetY y轴上的偏移
     * @param textHeight 字体的高度
     * @return
     */
    public static int getDrawY(int height,int offsetY,int textHeight)
    {
        double tempY=Math.random() * (height-textHeight-offsetY*2)+textHeight;
        //Logger.t("验证码").d("y坐标:"+tempY);
//        //如果获得的Y坐标小于偏移量这个加上一个偏移量
//        if(tempY < offsetY)
//        {
//            tempY += offsetY;
//        }
//        //如果坐标加上字体的高度超出了控件的高减去偏移量
//        if ((tempY+textHeight)>(height-offsetY))
//        {
//            tempY
//        }
        return (int)tempY;
    }
}
