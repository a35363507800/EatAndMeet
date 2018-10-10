/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
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
package com.echoesnet.eatandmeet.utils.IMUtils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.echoesnet.eatandmeet.controllers.EaseUI;
import com.echoesnet.eatandmeet.models.datamodel.DefaultEmojiconDatas;
import com.echoesnet.eatandmeet.models.datamodel.DolphinEmojiconDatas;
import com.echoesnet.eatandmeet.models.datamodel.EmojiIcon;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.SmileImageSpan;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EamSmileUtils
{
    public static final String DELETE_KEY = "em_delete_delete_expression";

    public static final String ee_1 = "[):]";
    public static final String ee_2 = "[:D]";
    public static final String ee_3 = "[;)]";
    public static final String ee_4 = "[:-o]";
    public static final String ee_5 = "[:p]";
    public static final String ee_6 = "[(H)]";
    public static final String ee_7 = "[:@]";
    public static final String ee_8 = "[:s]";
    public static final String ee_9 = "[:$]";
    public static final String ee_10 = "[:(]";
    public static final String ee_11 = "[:'(]";
    public static final String ee_12 = "[:|]";
    public static final String ee_13 = "[(a)]";
    public static final String ee_14 = "[8o|]";
    public static final String ee_15 = "[8-|]";
    public static final String ee_16 = "[+o(]";
    public static final String ee_17 = "[<o)]";
    public static final String ee_18 = "[|-)]";
    public static final String ee_19 = "[*-)]";
    public static final String ee_20 = "[:-#]";
    public static final String ee_21 = "[:-*]";
    public static final String ee_22 = "[^o)]";
    public static final String ee_23 = "[8-)]";
    public static final String ee_24 = "[(|)]";
    public static final String ee_25 = "[(u)]";
    public static final String ee_26 = "[(S)]";
    public static final String ee_27 = "[(*)]";
    public static final String ee_28 = "[(#)]";
    public static final String ee_29 = "[(R)]";
    public static final String ee_30 = "[({)]";
    public static final String ee_31 = "[(})]";
    public static final String ee_32 = "[(k)]";
    public static final String ee_33 = "[(F)]";
    public static final String ee_34 = "[(W)]";
    public static final String ee_35 = "[(D)]";


    public static final String ico_jianpan1 = "ico_jianpan1";
    public static final String ico_jianpan2 = "ico_jianpan2";
    public static final String ico_jianpan3 = "ico_jianpan3";
    public static final String ico_jianpan4 = "ico_jianpan4";
    public static final String ico_jianpan5 = "ico_jianpan5";
    public static final String ico_jianpan6 = "ico_jianpan6";
    public static final String ico_jianpan7 = "ico_jianpan7";
    public static final String ico_jianpan8 = "ico_jianpan8";
    public static final String ico_jianpan9 = "ico_jianpan9";
    public static final String ico_jianpan10 = "ico_jianpan10";
    public static final String ico_jianpan11 = "ico_jianpan11";
    public static final String ico_jianpan12 = "ico_jianpan12";
    public static final String ico_jianpan13 = "ico_jianpan13";
    public static final String ico_jianpan14 = "ico_jianpan14";
    public static final String ico_jianpan15 = "ico_jianpan15";
    public static final String ico_jianpan16 = "ico_jianpan16";
    public static final String ico_jianpan17 = "ico_jianpan17";
    public static final String ico_jianpan18 = "ico_jianpan18";
    public static final String ico_jianpan19 = "ico_jianpan19";
    public static final String ico_jianpan20 = "ico_jianpan20";
    public static final String ico_jianpan21 = "ico_jianpan21";
    public static final String ico_jianpan22 = "ico_jianpan22";
    public static final String ico_jianpan23 = "ico_jianpan23";
    public static final String ico_jianpan24 = "ico_jianpan24";

    public static final String ico_jianpan1_text = "摊手";
    public static final String ico_jianpan2_text = "无聊 ";
    public static final String ico_jianpan3_text = "惊讶";
    public static final String ico_jianpan4_text = "疑问";
    public static final String ico_jianpan5_text = "委屈";
    public static final String ico_jianpan6_text = "不开心";
    public static final String ico_jianpan7_text = "可怜";
    public static final String ico_jianpan8_text = "咆哮";
    public static final String ico_jianpan9_text = "尴尬";
    public static final String ico_jianpan10_text = "挖鼻孔";
    public static final String ico_jianpan11_text = "鼓掌";
    public static final String ico_jianpan12_text = "泪";
    public static final String ico_jianpan13_text = "斜眼";
    public static final String ico_jianpan14_text = "冷";
    public static final String ico_jianpan15_text = "笑哭";
    public static final String ico_jianpan16_text = "微笑";
    public static final String ico_jianpan17_text = "哈哈";
    public static final String ico_jianpan18_text = "嘻嘻";
    public static final String ico_jianpan19_text = "飞吻";
    public static final String ico_jianpan20_text = "偷笑";
    public static final String ico_jianpan21_text = "得意";
    public static final String ico_jianpan22_text = "滑稽 ";
    public static final String ico_jianpan23_text = "害羞";
    public static final String ico_jianpan24_text = "汗";


    private static final Factory spannableFactory = Factory
            .getInstance();

    private static final Map<Pattern, Object> emoticons = new HashMap<Pattern, Object>();


    static
    {
        EmojiIcon[] emojicons = DefaultEmojiconDatas.getData();
        EmojiIcon[] emojiIcons = DolphinEmojiconDatas.getData();
        for (int i = 0; i < emojicons.length; i++)
        {
            addPattern(emojicons[i].getEmojiText(), emojicons[i].getIcon());
        }
        for (int i = 0; i < emojiIcons.length; i++)
        {
            addPattern(emojiIcons[i].getIdentityCode(), emojiIcons[i].getIcon());
        }

        EaseUI.EaseEmojiconInfoProvider emojiconInfoProvider = EaseUI.getInstance().getEmojiconInfoProvider();
        if (emojiconInfoProvider != null && emojiconInfoProvider.getTextEmojiconMapping() != null)
        {
            for (Entry<String, Object> entry : emojiconInfoProvider.getTextEmojiconMapping().entrySet())
            {
                addPattern(entry.getKey(), entry.getValue());
            }
        }

    }

    /**
     * add text and icon to the map
     *
     * @param emojiText-- text of emoji
     * @param icon        -- resource id or local path
     */
    public static void addPattern(String emojiText, Object icon)
    {
        emoticons.put(Pattern.compile(Pattern.quote(emojiText)), icon);
    }


    /**
     * replace existing spannable with smiles
     *
     * @param context
     * @param spannable
     * @return
     */
    public static boolean addSmiles(Context context, Spannable spannable)
    {
       return addSmiles(context,spannable,0,0);
    }

    /**
     * replace existing spannable with smiles
     *
     * @param context
     * @param spannable
     * @return
     */
    public static boolean addSmiles(Context context, Spannable spannable,int width,int height)
    {
        boolean hasChanges = false;
        for (Entry<Pattern, Object> entry : emoticons.entrySet())
        {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find())
            {
                boolean set = true;
                for (SmileImageSpan span : spannable.getSpans(matcher.start(),
                        matcher.end(), SmileImageSpan.class))
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end())
                        spannable.removeSpan(span);
                    else
                    {
                        set = false;
                        break;
                    }
                if (set)
                {
                    hasChanges = true;
                    Object value = entry.getValue();
                    if (value instanceof String && !((String) value).startsWith("http"))
                    {
                        File file = new File((String) value);
                        if (!file.exists() || file.isDirectory())
                        {
                            return false;
                        }
                        spannable.setSpan(new SmileImageSpan(context, Uri.fromFile(file)),
                                matcher.start(), matcher.end(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else
                    {
                        Drawable drawable = ContextCompat.getDrawable(context, (Integer) value);
                        drawable.setBounds(0,0,width == 0?drawable.getIntrinsicWidth():width,height == 0?drawable.getIntrinsicHeight():height);
                        spannable.setSpan(new SmileImageSpan(drawable),
                                matcher.start(), matcher.end(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }

        return hasChanges;
    }

    public static Spannable getSmiledText(Context context, CharSequence text)
    {
        Spannable spannable = spannableFactory.newSpannable(text);
        addSmiles(context, spannable);
        return spannable;
    }

    public static Spannable getSmiledText(Context context, CharSequence text,int width,int height)
    {
        Spannable spannable = spannableFactory.newSpannable(text);
        addSmiles(context, spannable,width,height);
        return spannable;
    }

    public static boolean containsKey(String key)
    {
        boolean b = false;
        for (Entry<Pattern, Object> entry : emoticons.entrySet())
        {
            Matcher matcher = entry.getKey().matcher(key);
            if (matcher.find())
            {
                b = true;
                break;
            }
        }

        return b;
    }

    public static int getSmilesSize()
    {
        return emoticons.size();
    }


}
