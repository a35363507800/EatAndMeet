package com.echoesnet.eatandmeet.models.datamodel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.echoesnet.eatandmeet.models.bean.RestaurantBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wangben on 2016/4/26.
 */
public class Restaurant
{
    private  RestaurantBean restaurantInstance;

    public Restaurant()
    {
        restaurantInstance=new RestaurantBean();
    }

    public RestaurantBean getRestaurantInstance()
    {
        return restaurantInstance;
    }

    public void FadeData()
    {
        List<String> discounts=new ArrayList<>();
        discounts.add("满100减15;(周一到周五)");
        discounts.add("满100减1;(周六周日)");
        discounts.add("满100减15;(周六周日)");
        discounts.add("满100减10;(周六周日)");
        restaurantInstance.setDisCountItems(discounts);

/*        List<BigVcommentBean> bigvComments=new ArrayList<>();
        BigVcommentBean bb1=new BigVcommentBean();
        bb1.setNickName("王犇");
        bb1.setTitle("屌丝程序员");
        bb1.setRating(4);
        bb1.setComment("这家餐厅贼好吃，老板请客");
        bb1.setUserHeadImg("http://d.hiphotos.baidu.com/image/h%3D360/sign=6405cc77271f95cab9f594b0f9177fc5/72f082025aafa40fb8f407d4a964034f78f0198d.jpg");
        List<String> comImgs=new ArrayList<>();
        comImgs.add("http://img1.imgtn.bdimg.com/it/u=1070823144,225578467&fm=11&gp=0.jpg");
        comImgs.add("http://img1.imgtn.bdimg.com/it/u=4071088157,1484785116&fm=21&gp=0.jpg");
        comImgs.add("http://img0.imgtn.bdimg.com/it/u=1286381819,487000981&fm=21&gp=0.jpg");
        comImgs.add("http://img2.imgtn.bdimg.com/it/u=303078485,2362113963&fm=21&gp=0.jpg");
        bb1.setCommentImgUrlLst(comImgs);

        BigVcommentBean bb2=new BigVcommentBean();
        bb2.setNickName("大伟");
        bb2.setTitle("优秀程序员");
        bb2.setRating(4);
        bb2.setComment("这家餐厅贼好吃，老板请客");
        bb2.setUserHeadImg("http://d.hiphotos.baidu.com/image/h%3D360/sign=6405cc77271f95cab9f594b0f9177fc5/72f082025aafa40fb8f407d4a964034f78f0198d.jpg");
        List<String> comImgs2=new ArrayList<>();
        comImgs2.add("http://d.hiphotos.baidu.com/image/h%3D360/sign=6405cc77271f95cab9f594b0f9177fc5/72f082025aafa40fb8f407d4a964034f78f0198d.jpg");
        bb2.setCommentImgUrlLst(comImgs2);
        bigvComments.add(bb1);
        bigvComments.add(bb2);
        restaurantInstance.setBigVs(bigvComments);*/

/*        List<CommonUserCommentBean> loserComments=new ArrayList<>();
        CommonUserCommentBean loser1=new CommonUserCommentBean();
        loser1.setEvaNicName("王犇");
        loser1.setrStar(4);
        loser1.setComment("这家餐厅贼好吃，老板请客");
        loser1.setEvaImg("http://c.hiphotos.baidu.com/image/h%3D360/sign=6f506fce9b25bc31345d079e6ede8de7/8694a4c27d1ed21b4c5194e5af6eddc451da3f11.jpg");
        List<String> comImgs3=new ArrayList<>();
        comImgs3.add("http://img1.imgtn.bdimg.com/it/u=1070823144,225578467&fm=11&gp=0.jpg");
        comImgs3.add("http://img1.imgtn.bdimg.com/it/u=4071088157,1484785116&fm=21&gp=0.jpg");
        comImgs3.add("http://img0.imgtn.bdimg.com/it/u=1286381819,487000981&fm=21&gp=0.jpg");
        comImgs3.add("http://img2.imgtn.bdimg.com/it/u=303078485,2362113963&fm=21&gp=0.jpg");
        loser1.setCommentImgUrlLst(comImgs3);

        CommonUserCommentBean loser2=new CommonUserCommentBean();
        loser2.setEvaNicName("大伟");
        loser2.setrStar(4);
        loser2.setComment("这家餐厅贼好吃，老板请客这家餐厅贼好吃，老板请客这家餐厅贼好吃，老板请客这家餐厅贼好吃，老板请客" +
                "这家餐厅贼好吃，老板请客这家餐厅贼好吃，老板请客这家餐厅贼好吃，老板请客这家餐厅贼好吃，老板请客这家餐厅贼好吃，老板请客" +
                "这家餐厅贼好吃，老板请客这家餐厅贼好吃，老板请客");
        loser2.setEvaImg("http://d.hiphotos.baidu.com/image/h%3D360/sign=6405cc77271f95cab9f594b0f9177fc5/72f082025aafa40fb8f407d4a964034f78f0198d.jpg");
        List<String> comImgs4=new ArrayList<>();
        comImgs4.add("http://img2.imgtn.bdimg.com/it/u=303078485,2362113963&fm=21&gp=0.jpg");
        loser2.setCommentImgUrlLst(comImgs4);

        loserComments.add(loser1);
        loserComments.add(loser2);
        restaurantInstance.setEvaList(loserComments);*/

/*        /*//********************设置餐桌假数据**************************
        List<TableBean> lstTable=new ArrayList<>();
        TableBean te1=new TableBean();
        te1.setrId("001");
        te1.setTableId("1");
        te1.setX(20);
        te1.setY(30);
        te1.setW(150);
        te1.setH(150);
        te1.setTableType("6");
        te1.setStat(0);
//        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.table_n_2p); //getRemoteBitmap("http://img3.imgtn.bdimg.com/it/u=1288260193,1973663318&fm=21&gp=0.jpg");
//        te1.setBitImg(bitmap);
        lstTable.add(te1);

        TableBean te2=new TableBean();
        te2.setrId("001");
        te2.setTableId("2");
        te2.setX(220);
        te2.setY(30);
        te2.setW(150);
        te2.setH(150);
        te2.setTableType("6");
        te2.setStat(0);
//        Bitmap bitmap2=BitmapFactory.decodeResource(context.getResources(), R.drawable.table_n_2p);
//        te2.setBitImg(bitmap2);
        lstTable.add(te2);

        TableBean te3=new TableBean();
        te3.setrId("001");
        te3.setTableId("3");
        te3.setX(420);
        te3.setY(30);
        te3.setW(150);
        te3.setH(150);
        //te3.setAngle(25.0f);
        te3.setTableType("2");
        te3.setStat(0);
//        Bitmap bitmap3=BitmapFactory.decodeResource(context.getResources(), R.drawable.table_n_6p);
//        te3.setBitImg(bitmap3);
        lstTable.add(te3);

        TableBean te4=new TableBean();
        te4.setrId("001");
        te4.setTableId("4");
        te4.setX(620);
        te4.setY(30);
        te4.setW(150);
        te4.setH(150);
        te4.setTableType("2");
        te4.setStat(2);
//        Bitmap bitmap2=BitmapFactory.decodeResource(context.getResources(), R.drawable.table_n_2p);
//        te2.setBitImg(bitmap2);
        lstTable.add(te4);

        TableBean te5=new TableBean();
        te5.setrId("001");
        te5.setTableId("5");
        te5.setX(20);
        te5.setY(300);
        te5.setW(150);
        te5.setH(150);
        te5.setTableType("6");
        te5.setStat(0);
//        Bitmap bitmap2=BitmapFactory.decodeResource(context.getResources(), R.drawable.table_n_2p);
//        te2.setBitImg(bitmap2);
        lstTable.add(te5);

        TableBean te6=new TableBean();
        te6.setrId("001");
        te6.setTableId("6");
        te6.setX(200);
        te6.setY(300);
        te6.setW(150);
        te6.setH(150);
        te6.setTableType("6");
        te6.setStat(0);
//        Bitmap bitmap2=BitmapFactory.decodeResource(context.getResources(), R.drawable.table_n_2p);
//        te2.setBitImg(bitmap2);
        lstTable.add(te6);

        restaurantInstance.setTableEntities(lstTable);*/
    }
    //向后台请求代码写在这里
    public RestaurantBean setTableEntities()
    {
/*        List<TableBean> lstTable=new ArrayList<>();
        TableBean te1=new TableBean();
        te1.setTableId("1");
        te1.setX(10);
        te1.setY(10);
        te1.setW(150);
        te1.setH(150);
        Bitmap bitmap= getRemoteBitmap("http://img3.imgtn.bdimg.com/it/u=1288260193,1973663318&fm=21&gp=0.jpg");
        te1.setBitImg(bitmap);
        lstTable.add(te1);

        TableBean te2=new TableBean();
        te2.setTableId("2");
        te2.setX(100);
        te2.setY(500);
        te2.setW(150);
        te2.setH(150);
        Bitmap bitmap2=getRemoteBitmap("http://img3.imgtn.bdimg.com/it/u=1288260193,1973663318&fm=21&gp=0.jpg");
        te2.setBitImg(bitmap2);
        lstTable.add(te2);

        TableBean te3=new TableBean();
        te3.setTableId("3");
        te3.setX(400);
        te3.setY(200);
        te3.setW(150);
        te3.setH(150);
        te3.setAngle(25.0f);
        te3.setStat(2);
        Bitmap bitmap3=getRemoteBitmap("http://img3.imgtn.bdimg.com/it/u=1288260193,1973663318&fm=21&gp=0.jpg");
        te3.setBitImg(bitmap3);
        lstTable.add(te3);
        restaurantInstance.setTableEntities(lstTable);*/

        return restaurantInstance;
    }

    private Bitmap getRemoteBitmap(String url)
    {
        OkHttpClient mOkHttpClient=new OkHttpClient();
        final Request request=new Request.Builder()
                .url(url)
                .build();
        try
        {
            Response response= mOkHttpClient.newCall(request).execute();
            Bitmap bitmap=BitmapFactory.decodeStream(response.body().byteStream());
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
