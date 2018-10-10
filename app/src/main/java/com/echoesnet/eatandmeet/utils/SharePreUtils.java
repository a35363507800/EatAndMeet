package com.echoesnet.eatandmeet.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.IMUtils.PreferenceManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangben on 2015/5/23.
 */
public class SharePreUtils
{
    private SharePreUtils()
    {
    }

    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "eat_and_meet";

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    private static void put(@Nullable Context context, String key, Object object)
    {
        if (context == null)
            context = EamApplication.getInstance();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String)
        {
            editor.putString(key, (String) object);
        }
        else if (object instanceof Integer)
        {
            editor.putInt(key, (Integer) object);
        }
        else if (object instanceof Boolean)
        {
            editor.putBoolean(key, (Boolean) object);
        }
        else if (object instanceof Float)
        {
            editor.putFloat(key, (Float) object);
        }
        else if (object instanceof Long)
        {
            editor.putLong(key, (Long) object);
        }
        else
        {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    private static Object get(@Nullable Context context, String key, Object defaultObject)
    {
        if (context == null)
            context = EamApplication.getInstance();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        if (defaultObject instanceof String)
        {
            return sp.getString(key, (String) defaultObject);
        }
        else if (defaultObject instanceof Integer)
        {
            return sp.getInt(key, (Integer) defaultObject);
        }
        else if (defaultObject instanceof Boolean)
        {
            return sp.getBoolean(key, (Boolean) defaultObject);
        }
        else if (defaultObject instanceof Float)
        {
            return sp.getFloat(key, (Float) defaultObject);
        }
        else if (defaultObject instanceof Long)
        {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    private static void putSet(Context context, String key, Set<String> set)
    {
        if (context == null)
            context = EamApplication.getInstance();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(key, set);
        SharedPreferencesCompat.apply(editor);
    }

    private static Set<String> getSet(Context context, String key, Set<String> defaultSet)
    {
        if (context == null)
            context = EamApplication.getInstance();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getStringSet(key, defaultSet);
    }

    /**
     * 移除某个key值以及对应的值
     *
     * @param context
     * @param key
     */
    public static void remove(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 除了特定的数据清除其他数据
     *
     * @param context
     * @param keys
     */
    public static void removeValueExcludeSome(Context context, List<String> keys)
    {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Map<String, ?> allKeyValues = getAll(context);
        for (String key : allKeyValues.keySet())
        {
            if (!keys.contains(key))
            {
                editor.remove(key);
                SharedPreferencesCompat.apply(editor);
            }
        }
    }

    /**
     * 清除所有数据
     *
     * @param context
     */
    public static void clear(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat
    {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod()
        {
            try
            {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e)
            {
            }
            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor)
        {
            try
            {
                if (sApplyMethod != null)
                {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e)
            {
            } catch (IllegalAccessException e)
            {
            } catch (InvocationTargetException e)
            {
            }
            editor.commit();
        }
    }


    public static void setToken(Context context, String token)
    {
        put(context, "token", token);
    }

    public static String getToken(Context context)
    {
        return (String) get(context, "token", "");
    }


    public static void setFirst(Context context, String first)
    {
        put(context, "midAutumnUrl", first);
    }

    public static String getFirst(Context context)
    {
        return (String) get(context, "midAutumnUrl", "");
    }

    public static void setUserMobile(Context context, String userId)
    {
        put(context, "userMobile", userId);
    }

    public static String getUserMobile(Context context)
    {
        return (String) get(context, "userMobile", "");
    }

    public static void setUId(Context context, String uId)
    {
        put(context, "uId", uId);
    }

    public static String getUId(Context context)
    {
        return (String) get(context, "uId", "");
    }

    public static void setId(Context context, String id)
    {
        put(context, "userNumberId", id);
    }

    public static String getId(Context context)
    {
        return (String) get(context, "userNumberId", "");
    }

    public static void setIsVUser(Context context, String id)
    {
        put(context, "isVUser", id);
    }

    public static String getIsVUser(Context context)
    {
        return (String) get(context, "isVUser", "0");
    }


    public static void setHxId(Context context, String hxId)
    {
        put(context, "hxId", hxId);
        try
        {
            PreferenceManager.getInstance().setCurrentUserName(hxId);
        } catch (Exception e)
        {
            PreferenceManager.init(context);
            PreferenceManager.getInstance().setCurrentUserName(hxId);
        }

    }

    public static String getHxId(Context context)
    {
        return (String) get(context, "hxId", "");
    }

    //当前用户头像url
    public static void setHeadImg(Context context, String headImgUrl)
    {
        put(context, "headImgUrl", headImgUrl);
    }

    public static String getHeadImg(Context context)
    {
        return (String) get(context, "headImgUrl", "");
    }

    //当天用户进入主页次数
    public static void setDayCount(Context context, String dayCount)
    {
        put(context, "dayCount", dayCount);
    }

    public static String getDayCount(Context context)
    {
        return (String) get(context, "dayCount", "");
    }

    //当天用户任务弹窗次数
    public static void setTaskShowCount(Context context, String dayCount)
    {
        put(context, "taskShowCount", dayCount);
    }

    public static String getTaskShowCount(Context context)
    {
        return (String) get(context, "taskShowCount", "");
    }

    //当天用户成就弹窗次数
    public static void setAchieveShowCount(Context context, String dayCount)
    {
        put(context, "achieveShowCount", dayCount);
    }

    public static String getAchieveShowCount(Context context)
    {
        return (String) get(context, "achieveShowCount", "");
    }

    //当前用户昵称
    public static void setNicName(Context context, String nicName)
    {
        put(context, "nicName", nicName);
    }

    public static String getNicName(@Nullable Context context)
    {
        return (String) get(context, "nicName", "");
    }


    //当前用户直播用户名
    public static void setTlsName(Context context, String tlsName)
    {
        put(context, "tlsName", tlsName);
    }

    public static String getTlsName(@Nullable Context context)
    {
        return (String) get(context, "tlsName", "");
    }

    //当前用户直播签名
    public static void setTlsSign(Context context, String tlsSign)
    {
        put(context, "tlsSign", tlsSign);
    }

    public static String getTlsSign(Context context)
    {
        return (String) get(context, "tlsSign", "");
    }


    public static void setVersionCode(Context context, int version)
    {
        put(context, "versionCode", version);
    }

    public static int getVersionCode(Context context)
    {
        return (int) get(context, "versionCode", 0);
    }

    //是否是第一次安装
    public static void setIsFirstUse(Context context, boolean isFirstUse)
    {
        put(context, "isFirstUse", isFirstUse);
    }

    public static boolean getIsFirstUse(Context context)
    {
        return (boolean) get(context, "isFirstUse", true);
    }

    public static void setRestId(Context context, String restId)
    {
        put(context, "restId", restId);
    }

    public static String getRestId(Context context)
    {
        return (String) get(context, "restId", "");
    }

    public static void setClubId(Context context, String restId)
    {
        put(context, "clubId", restId);
    }

    public static String getClubId(Context context)
    {
        return (String) get(context, "clubId", "");
    }

    public static void setResName(Context context, String resName)
    {
        put(context, "resName", resName);
    }

    public static String getResName(Context context)
    {
        return (String) get(context, "resName", "");
    }

    // 保存聊天消息是否有声音
    public static void setIsSound(Context context, boolean isSound)
    {
        put(context, "isSound", isSound);
    }

    public static boolean getIsSound(Context context)
    {
        return (boolean) get(context, "isSound", true);
    }

    // 保存聊天消息是否震动
    public static void setIsVibrate(Context context, boolean isVibrate)
    {
        put(context, "isVibrate", isVibrate);
    }

    public static boolean getIsVibrate(Context context)
    {
        return (boolean) get(context, "isVibrate", true);
    }

    // 保存首页是否显示新手引导
    public static void setIsNewBieHome(Context context, boolean isNewBieHome)
    {
        put(context, "isNewBieHome", isNewBieHome);
    }

    public static boolean getIsNewBieHome(Context context)
    {
        return (boolean) get(context, "isNewBieHome", true);
    }


    // 保存订桌页面是否显示新手引导
    public static void setIsNewBieOrder(Context context, boolean isNewBieOrder)
    {
        put(context, "isNewBieOrder", isNewBieOrder);
    }

    public static boolean getIsNewBieOrder(Context context)
    {
        return (boolean) get(context, "isNewBieOrder", true);
    }

    // 保存发现页面是否显示新手引导
    public static void setIsNewBieFind(Context context, boolean isNewBieOrder)
    {
        put(context, "isNewBieFind", isNewBieOrder);
    }

    public static boolean getIsNewBieFind(Context context)
    {
        return (boolean) get(context, "isNewBieFind", true);
    }

    // 保存发现页面动态是否显示新手引导
    public static void setIsNewBieTrends(Context context, boolean isNewBieOrder)
    {
        put(context, "isNewBieTrends", isNewBieOrder);
    }

    public static boolean getIsNewBieTrends(Context context)
    {
        return (boolean) get(context, "isNewBieTrends", true);
    }

    // 保存动态发布页面是否显示新手引导
    public static void setIsNewBieTrendsPublish(Context context, boolean isNewBieOrder)
    {
        put(context, "isNewBieTrendsPublish", isNewBieOrder);
    }

    public static boolean getIsNewBieTrendsPublish(Context context)
    {
        return (boolean) get(context, "isNewBieTrendsPublish", true);
    }

    // 保存个人详情页是否显示新手引导
    public static void setIsNewBieInfo(Context context, boolean isNewBieInfo)
    {
        put(context, "isNewBieInfo", isNewBieInfo);
    }

    public static boolean getIsNewBieInfo(Context context)
    {
        return (boolean) get(context, "isNewBieInfo", true);
    }


    // 保存直播列表是否显示新手引导
    public static void setIsNewBieLiveList(Context context, boolean isNewBieInfo)
    {
        put(context, "isNewBieLiveList", isNewBieInfo);
    }

    public static boolean getIsNewBieLiveList(Context context)
    {
        return (boolean) get(context, "isNewBieLiveList", true);
    }

    // 保存直播列表是否显示新手引导
    public static void setIsNewDinnerDetail(Context context, boolean isNewBieInfo)
    {
        put(context, "isNewBieDinnerDetail", isNewBieInfo);
    }

    public static boolean getIsNewDinnerDetail(Context context)
    {
        return (boolean) get(context, "isNewBieDinnerDetail", true);
    }


    // 保存直播间是否显示新手引导
    public static void setIsNewLiveRoom(Context context, boolean isNewBieInfo)
    {
        put(context, "isNewBieLiveRoom", isNewBieInfo);
    }

    public static boolean getIsNewLiveRoom(Context context)
    {
        return (boolean) get(context, "isNewBieLiveRoom", true);
    }

    // 保存订位是否显示新手引导
    public static void setIsNewBieDing(Context context, boolean isNewBieInfo)
    {
        put(context, "isNewBieDing", isNewBieInfo);
    }

    public static boolean getIsNewBieDing(Context context)
    {
        return (boolean) get(context, "isNewBieDing", true);
    }

    // 保存第一次app安装进入是否显示新手引导
    public static void setIsNewAppInstaill(Context context, boolean isNewBieOrder)
    {
        put(context, "isNewAppInstaill", isNewBieOrder);
    }

    public static boolean getIsNewAppInstaill(Context context)
    {
        return (boolean) get(context, "isNewAppInstaill", true);
    }

    // 保存会话页面是否显示打招呼新手引导
    public static void setIsNewBieSayHi(Context context, boolean isNewBieSayHi)
    {
        put(context, "isNewBieSayHi", isNewBieSayHi);
    }

    public static boolean getIsNewBieSayHi(Context context)
    {
        return (boolean) get(context, "isNewBieSayHi", true);
    }

    // 保存聊天页面是否显示下载表情提示新手引导
    public static void setIsNewBieDownloadExpression(Context context, boolean isNewBieDownload)
    {
        put(context, "isNewBieDownloadExpress", isNewBieDownload);
    }

    public static boolean getIsNewBieDownloadExpression(Context context)
    {
        return (boolean) get(context, "isNewBieDownloadExpress", true);
    }

    public static void setIsNewBieFocusDynamic(Context context, boolean isNewBieDownload)
    {
        put(context, "isNewBieFocusDynamic", isNewBieDownload);
    }

    public static boolean getIsNewBieFocusDynamic(Context context)
    {
        return (boolean) get(context, "isNewBieFocusDynamic", true);
    }


    public static void setLaunchBgUrl(Context context, String launchBgUrl)
    {
        put(context, "launchBgUrl", launchBgUrl);
    }

    public static String getLaunchBgUrl(Context context)
    {
        return (String) get(context, "launchBgUrl", "");
    }

    public static void setLaunchBgSize(Context context, long launchBgUrl)
    {
        put(context, "launchBgSize", launchBgUrl);
    }

    public static long getLaunchBgSize(Context context)
    {
        return (long) get(context, "launchBgSize", 0);
    }

    public static void setGiftVersion(Context context, int giftVersion)
    {
        put(context, "giftVersion", giftVersion);
    }

    public static int getGiftVersion(Context context)
    {
        return (int) get(context, "giftVersion", -1);
    }

    public static void setIsSignAnchor(Context context, String giftVersion)
    {
        put(context, "isSignAnchor", giftVersion);
    }

    public static String getIsSignAnchor(Context context)
    {
        return (String) get(context, "isSignAnchor", "0");
    }

    public static void setPersonLabels(Context context, String personLabel)
    {
        put(context, "personLabel", personLabel);
    }

    public static String getPersonLabels(Context context)
    {
        return (String) get(context, "personLabel", "");
    }

    public static void setSearchHistory(Context context, String SearchHistory)
    {
        put(context, "SearchHistory", SearchHistory);
    }

    public static String getSearchHistory(Context context)
    {
        return (String) get(context, "SearchHistory", "");
    }

    public static void setSearchClubHistory(Context context, String SearchHistory)
    {
        put(context, "SearchClubHistory", SearchHistory);
    }

    public static String getSearchClubHistory(Context context)
    {
        return (String) get(context, "SearchClubHistory", "");
    }


    public static void setIsShowNotify(Context context, String isShowNotify)
    {
        put(context, "isShowNotify", isShowNotify);
    }

    public static String getIsShowNotify(Context context)
    {
        return (String) get(context, "isShowNotify", "1");
    }

    public static void setLevel(Context context, int level)
    {
        put(context, "level", level);
    }

    public static int getLevel(Context context)
    {

        return (int) get(context, "level", 0);
    }


    public static void setSex(Context context, String sex)
    {
        if (!TextUtils.isEmpty(sex))
            put(context, "sex", sex);
    }

    public static String getSex(Context context)
    {
        return (String) get(context, "sex", "男");
    }

    public static void setAge(Context context, String sex)
    {
        if (!TextUtils.isEmpty(sex))
            put(context, "age", sex);
    }

    public static String getAge(Context context)
    {
        return (String) get(context, "age", "");
    }


    //region 非正常退出直播间里面保存上一次的roomid

    /**
     * 如果是从任务栏强行退出，里面保存上一次的roomid
     *
     * @param context
     * @param preGroupId
     */
    public static void setPreGroupId(Context context, String preGroupId)
    {
        put(context, "preGroupId", preGroupId);
    }

    public static String getPreGroupId(Context context)
    {
        return (String) get(context, "preGroupId", "");
    }
    //endregion

    // 测试跳转状态值
    public static void setSource(Context context, String restId)
    {
        put(context, "source", restId);
    }

    public static String getSource(Context context)
    {
        return (String) get(context, "source", "");
    }

    public static void setUserSign(Context context, String sign)
    {
        put(context, "sign", sign);
    }

    public static String getUserSign(Context context)
    {
        return (String) get(context, "sign", "");
    }

    public static void setToOrderMeal(Context context, String toMeal)
    {
        put(context, "toMeal", toMeal);
    }

    public static String getToOrderMeal(Context context)
    {
        return (String) get(context, "toMeal", "");
    }

    public static void setToDate(Context context, String toDate)
    {
        put(context, "toDate", toDate);
    }

    public static String getToDate(Context context)
    {
        return (String) get(context, "toDate", "");
    }

    public static void setOrderType(Context context, String orderType)
    {
        put(context, "orderType", orderType);
    }

    public static String getOrderType(Context context)
    {
        return (String) get(context, "orderType", "");
    }

    public static void setPrivilege(Context context, List<String> saveData)
    {
        putSet(context, "privilege", new HashSet<String>(saveData));
    }

    public static List<String> getPrivilege(Context context)
    {
        return new ArrayList<>(getSet(context, "privilege", new HashSet<String>()));
    }

    public static void setPhoneContact(Context context, String token)
    {
        put(context, "phoneContact", token);
    }

    public static String getPhoneContact(Context context)
    {
        return (String) get(context, "phoneContact", "0");
    }


    //是否显示审核头像弹窗
    public static void setShowPop(Context context, boolean isShow)
    {
        put(context, "isShowPop", isShow);
    }

    public static boolean getShowPop(Context context)
    {
        return (boolean) get(context, "isShowPop", false);
    }

    //是否有双十一活动
    public static void setIsHasAct(boolean isHasAct)
    {
        put(null, "isHasAct", isHasAct);
    }

    public static boolean getIsHasAct()
    {
        return (boolean) get(null, "isHasAct", false);
    }


    /**
     * 邂逅未读关注动态数量
     *
     * @param context
     * @param focusTrendsCount
     */
    public static void setFocusTrendsCount(Context context, int focusTrendsCount)
    {
        put(context, "focusTrendsCount", focusTrendsCount);
    }

    public static int getFocusTrendsCount(Context context)
    {
        return (int) get(context, "focusTrendsCount", 0);
    }

    public static void setEncounterPageCatch(String catchStr)
    {
        put(null, "encounterPageCatch", catchStr);
    }

    public static String getEncounterPageCatch()
    {
        return (String) get(null, "encounterPageCatch", "{}");
    }

    public static void setSensitiveWords(String sensitiveWords)
    {
        put(null, "sensitiveWords", sensitiveWords);
    }

    public static String getSensitiveWords()
    {
        return (String) get(null, "sensitiveWords", "");
    }
}
