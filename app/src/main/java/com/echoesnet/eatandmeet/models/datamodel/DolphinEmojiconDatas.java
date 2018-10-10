package com.echoesnet.eatandmeet.models.datamodel;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.IMUtils.EamSmileUtils;

public class DolphinEmojiconDatas
{
    private static String[] emojisId = new String[]{
            EamSmileUtils.ico_jianpan1,
            EamSmileUtils.ico_jianpan2,
            EamSmileUtils.ico_jianpan3,
            EamSmileUtils.ico_jianpan4,
            EamSmileUtils.ico_jianpan5,
            EamSmileUtils.ico_jianpan6,
            EamSmileUtils.ico_jianpan7,
            EamSmileUtils.ico_jianpan8,
            EamSmileUtils.ico_jianpan9,
            EamSmileUtils.ico_jianpan10,
            EamSmileUtils.ico_jianpan11,
            EamSmileUtils.ico_jianpan12,
            EamSmileUtils.ico_jianpan13,
            EamSmileUtils.ico_jianpan14,
            EamSmileUtils.ico_jianpan15,
            EamSmileUtils.ico_jianpan16,
            EamSmileUtils.ico_jianpan17,
            EamSmileUtils.ico_jianpan18,
            EamSmileUtils.ico_jianpan19,
            EamSmileUtils.ico_jianpan20,
            EamSmileUtils.ico_jianpan21,
            EamSmileUtils.ico_jianpan22,
            EamSmileUtils.ico_jianpan23,
            EamSmileUtils.ico_jianpan24
    };
    private static String[] emojisText = new String[]{
            EamSmileUtils.ico_jianpan1_text,
            EamSmileUtils.ico_jianpan2_text,
            EamSmileUtils.ico_jianpan3_text,
            EamSmileUtils.ico_jianpan4_text,
            EamSmileUtils.ico_jianpan5_text,
            EamSmileUtils.ico_jianpan6_text,
            EamSmileUtils.ico_jianpan7_text,
            EamSmileUtils.ico_jianpan8_text,
            EamSmileUtils.ico_jianpan9_text,
            EamSmileUtils.ico_jianpan10_text,
            EamSmileUtils.ico_jianpan11_text,
            EamSmileUtils.ico_jianpan12_text,
            EamSmileUtils.ico_jianpan13_text,
            EamSmileUtils.ico_jianpan14_text,
            EamSmileUtils.ico_jianpan15_text,
            EamSmileUtils.ico_jianpan16_text,
            EamSmileUtils.ico_jianpan17_text,
            EamSmileUtils.ico_jianpan18_text,
            EamSmileUtils.ico_jianpan19_text,
            EamSmileUtils.ico_jianpan20_text,
            EamSmileUtils.ico_jianpan21_text,
            EamSmileUtils.ico_jianpan22_text,
            EamSmileUtils.ico_jianpan23_text,
            EamSmileUtils.ico_jianpan24_text
    };


    private static int[] icons = new int[]{
            R.drawable.ico_jianpan1,
            R.drawable.ico_jianpan2,
            R.drawable.ico_jianpan3,
            R.drawable.ico_jianpan4,
            R.drawable.ico_jianpan5,
            R.drawable.ico_jianpan6,
            R.drawable.ico_jianpan7,
            R.drawable.ico_jianpan8,
            R.drawable.ico_jianpan9,
            R.drawable.ico_jianpan10,
            R.drawable.ico_jianpan11,
            R.drawable.ico_jianpan12,
            R.drawable.ico_jianpan13,
            R.drawable.ico_jianpan14,
            R.drawable.ico_jianpan15,
            R.drawable.ico_jianpan16,
            R.drawable.ico_jianpan17,
            R.drawable.ico_jianpan18,
            R.drawable.ico_jianpan19,
            R.drawable.ico_jianpan20,
            R.drawable.ico_jianpan21,
            R.drawable.ico_jianpan22,
            R.drawable.ico_jianpan23,
            R.drawable.ico_jianpan24
    };


    private static final EmojiIcon[] DATA = createData();

    private static EmojiIcon[] createData()
    {
        EmojiIcon[] datas = new EmojiIcon[icons.length];
        for (int i = 0; i < icons.length; i++)
        {
            datas[i] = new EmojiIcon(icons[i], emojisText[i], emojisId[i], EmojiIcon.Type.NORMAL_AS_EXPRESSION);
        }
        return datas;
    }

    public static EmojiIcon[] getData()
    {
        return DATA;
    }
}
