package com.echoesnet.eatandmeet.daos;


import android.content.Context;
import android.util.Log;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.datamodel.EmojiGroupEntity;
import com.echoesnet.eatandmeet.models.datamodel.EmojiIcon;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EmojiconBigExpressionData
{
    private static final String TAG = EmojiconBigExpressionData.class.getSimpleName();

    private static List<EmojiIcon> EmojDataLst = new ArrayList<>();
    private static List<EmojiGroupEntity> emojEntities = new ArrayList<>();

    //表情文件夹例如 /xiaomei 文件夹
    private static EmojiGroupEntity createData(File emojFile)
    {
        EmojiGroupEntity emojiconGroupEntity = new EmojiGroupEntity();
        try
        {
            //封面的图片文件夹
            File staticEmojFileFolder = new File(emojFile.getPath() + "/static_emoj");
/*        File [] sEmojImgs=staticEmojFileFolder.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String filename)
            {
                return false;
            }
        });*/
            File[] sEmojImgs = staticEmojFileFolder.listFiles();
            //动态图片文件夹
            File dynEmojFileFolder = new File(emojFile.getPath() + "/dyn_emoj");
            File[] dEmojImgs = dynEmojFileFolder.listFiles();
            //名字说明文件
            File emojName = new File(emojFile.getPath() + "/name");//.json

            HashMap<String, String> nameMap = new Gson().fromJson(CommonUtils.getJsonFromFile(emojName), new TypeToken<HashMap<String, String>>()
            {
            }.getType());


            EmojiIcon[] datas = new EmojiIcon[sEmojImgs.length];
            for (int i = 0; i < sEmojImgs.length; i++)
            {
                datas[i] = new EmojiIcon(sEmojImgs[i].getAbsolutePath(), null, EmojiIcon.Type.BIG_EXPRESSION);
                //http://huisheng.ufile.ucloud.cn/xiaomei_1_dyn_emoj/xmd_1.gif
                datas[i].setBitIconNetPath(CdnHelper.CDN_ORIGINAL_SITE + emojFile.getName() + "_dyn_emoj/" + dEmojImgs[i].getName() + ".gif");
                datas[i].setBigIconPath(dEmojImgs[i].getAbsolutePath());
                datas[i].setName(nameMap.get(sEmojImgs[i].getName()));//.split("\\.")[0]
                //datas[i].setColumnName("表情" +emojFile.getColumnName()+ (i + 1));
                // setIdentityCode : xiaomei_1_xms_1
                datas[i].setIdentityCode(emojFile.getName() + "_" + sEmojImgs[i].getName());//.split("\\.")[0]
/*                Logger.t(TAG).d("icon 路径 > "+CdnHelper.CDN_ORIGINAL_SITE+emojFile.getColumnName()+"_dyn_emoj/"+ dEmojImgs[i].getColumnName()
                        +"key"+sEmojImgs[i].getColumnName().split("\\.")[0]);*/
            }
            Logger.t(TAG).d("--------------->" + Arrays.asList(datas));
            emojiconGroupEntity.setEmojiconList(Arrays.asList(datas));
            emojiconGroupEntity.setName(emojFile.getName());
            File cover = new File(emojFile.getPath() + "/cover");//.png
            if (cover.exists())
            {
                emojiconGroupEntity.setIcon(0);
                emojiconGroupEntity.setIconPath(emojFile.getPath() + "/cover");//.png
            }
            else
            {
                emojiconGroupEntity.setIcon(0);
                try
                {
                    emojiconGroupEntity.setIconPath(sEmojImgs[0].getAbsolutePath());
                } catch (Exception e)
                {
                    emojiconGroupEntity.setIcon(R.drawable.biaoqing_1_cover);
                }
            }
            emojiconGroupEntity.setType(EmojiIcon.Type.BIG_EXPRESSION);
            //把所有表情都添加到数据源中
            EmojDataLst.addAll(Arrays.asList(datas));

        } catch (Exception e)
        {

        }

        return emojiconGroupEntity;
    }

    public static List<EmojiIcon> allEmojData(Context context)
    {
        //emojEntities(new File(NetHelper.getRootDirPath(context)+"Emojs"));
        return EmojDataLst;
    }

    public static List<EmojiGroupEntity> emojEntities(Context context)
    {
        emojEntities(new File(NetHelper.getRootDirPath(context) + NetHelper.EMOJI_FOLDER));
        return emojEntities;
    }

    public static List<EmojiGroupEntity> emojEntities(Context context, String emojiName)
    {
        emojEntities(new File(NetHelper.getRootDirPath(context) + NetHelper.EMOJI_FOLDER), emojiName);
        return emojEntities;
    }


/*    public static EaseEmojiconGroupEntity getData()
    {
        return createData();
    }*/

    /**
     * 根据表情名字添加的动态表情
     *
     * @param fileold ../EatAndMeet/Emojs
     * @return
     */
    private static void emojEntities(File fileold, String emojiName)
    {
        emojEntities.clear();
        try
        {
            Log.d("==================", "emojEntities: 添加动态表情的路径是 :" + fileold.getPath());
            if (fileold.exists() && fileold.isDirectory())
            {
                File[] emojsFiles = fileold.listFiles();
                for (int i = 0; i < emojsFiles.length; i++)
                {
                    //表情文件夹例如 /xiaomei 文件夹
                    if (emojsFiles[i].isDirectory() && emojsFiles[i].getName().equals(emojiName))
                    {
                        //动态和静态图文件夹
                        emojEntities.add(createData(emojsFiles[i]));
                    }
                }
            }
        } catch (Exception e)
        {
            Log.d("==================", "emojEntities: 集合添加动态表情 崩溃了");
            emojEntities.clear();
        }
    }

    /**
     * 要添加的动态表情
     *
     * @param fileold ../EatAndMeet/Emojs
     * @return
     */
    private static void emojEntities(File fileold)
    {
        emojEntities.clear();
        try
        {
            Log.d("==================", "emojEntities: 添加动态表情的路径是 :" + fileold.getPath());
            if (fileold.exists() && fileold.isDirectory())
            {
                File[] emojsFiles = fileold.listFiles();
                for (int i = 0; i < emojsFiles.length; i++)
                {
                    //表情文件夹例如 /xiaomei 文件夹
                    if (emojsFiles[i].isDirectory())
                    {
                        //动态和静态图文件夹
                        emojEntities.add(createData(emojsFiles[i]));
                    }
                }
            }
        } catch (Exception e)
        {
            Log.d("==================", "emojEntities: 集合添加动态表情 崩溃了");
            emojEntities.clear();
        }
    }

}
