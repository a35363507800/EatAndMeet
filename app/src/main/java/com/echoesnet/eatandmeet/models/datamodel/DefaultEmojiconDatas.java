package com.echoesnet.eatandmeet.models.datamodel;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.IMUtils.EamSmileUtils;

public class DefaultEmojiconDatas
{
    private static String[] emojis = new String[]{
            EamSmileUtils.ee_1,
            EamSmileUtils.ee_2,
            EamSmileUtils.ee_3,
            EamSmileUtils.ee_4,
            EamSmileUtils.ee_5,
            EamSmileUtils.ee_6,
            EamSmileUtils.ee_7,
            EamSmileUtils.ee_8,
            EamSmileUtils.ee_9,
            EamSmileUtils.ee_10,
            EamSmileUtils.ee_11,
            EamSmileUtils.ee_12,
            EamSmileUtils.ee_13,
            EamSmileUtils.ee_14,
            EamSmileUtils.ee_15,
            EamSmileUtils.ee_16,
            EamSmileUtils.ee_17,
            EamSmileUtils.ee_18,
            EamSmileUtils.ee_19,
            EamSmileUtils.ee_20,
            EamSmileUtils.ee_21,
            EamSmileUtils.ee_22,
            EamSmileUtils.ee_23,
            EamSmileUtils.ee_24,
            EamSmileUtils.ee_25,
            EamSmileUtils.ee_26,
            EamSmileUtils.ee_27,
            EamSmileUtils.ee_28,
            EamSmileUtils.ee_29,
            EamSmileUtils.ee_30,
            EamSmileUtils.ee_31,
            EamSmileUtils.ee_32,
            EamSmileUtils.ee_33,
            EamSmileUtils.ee_34,
            EamSmileUtils.ee_35
    };

    private static int[] icons = new int[]{
            R.drawable.ee_1_blush,
            R.drawable.ee_7_smiley,
            R.drawable.ee_13_stuck_out_tongue_winking_eye,
            R.drawable.ee_2_open_mouth,
            R.drawable.ee_8_yum,
            R.drawable.ee_14_sunglasses,
            R.drawable.ee_3_rage,
            R.drawable.ee_9_confounded,
            R.drawable.ee_15_flushed,
            R.drawable.ee_4_disappointed,
            R.drawable.ee_10_sob,
            R.drawable.ee_16_neutral_face,
            R.drawable.ee_5_innocent,
            R.drawable.ee_11_grimacing,
            R.drawable.ee_17_laughing,
            R.drawable.ee_6_scream,
            R.drawable.ee_12_smiling_imp,
            R.drawable.ee_18_sleeping,
            R.drawable.ee_19_confused,
            R.drawable.ee_25_mask,
            R.drawable.ee_31_hushed,
            R.drawable.ee_20_smirk,
            R.drawable.ee_26_expressionless,
            R.drawable.ee_32_sparkling_heart,
            R.drawable.ee_21_broken_heart,
            R.drawable.ee_27_crescent_moon,
            R.drawable.ee_33_star2,
            R.drawable.ee_22_sun_with_face,
            R.drawable.ee_28_rainbow,
            R.drawable.ee_34_heart_eyes,
            R.drawable.ee_23_kissing_smiling_eyes,
            R.drawable.ee_29_kissing_heart,
            R.drawable.ee_35_rose,
            R.drawable.ee_24_fallen_leaf,
            R.drawable.ee_30_pray
    };


    private static final EmojiIcon[] DATA = createData();

    private static EmojiIcon[] createData()
    {
        EmojiIcon[] datas = new EmojiIcon[icons.length];
        for (int i = 0; i < icons.length; i++)
        {
            datas[i] = new EmojiIcon(icons[i], emojis[i], EmojiIcon.Type.NORMAL);
        }
        return datas;
    }

    public static EmojiIcon[] getData()
    {
        return DATA;
    }
}
