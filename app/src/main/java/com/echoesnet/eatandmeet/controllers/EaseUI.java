package com.echoesnet.eatandmeet.controllers;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.echoesnet.eam.icontextmodule.IconUtil;
import com.echoesnet.eatandmeet.models.datamodel.EmojiIcon;
import com.echoesnet.eatandmeet.utils.IMUtils.EaseAtMessageHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.EaseNotifier;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class EaseUI
{
    private static final String TAG = EaseUI.class.getSimpleName();

    private static EaseUI instance = null;          //the global EaseUI instance
    //private EaseUserProfileProvider userProvider;     //user profile provider
    private EaseSettingsProvider settingsProvider;
    private Context appContext = null;//application context
    private boolean sdkInited = false;//init flag: test if the sdk has been inited before, we don't need to init again
    private EaseNotifier notifier = null;//the notifier
    private List<Activity> activityList = new ArrayList<Activity>();//save foreground Activity which registered eventlistener

    private EaseUI()
    {  }

    public void pushActivity(Activity activity)
    {
        if (activityList!=null&&!activityList.contains(activity))
        {
            activityList.add(0, activity);
        }
    }
    public void popActivity(Activity activity)
    {
        activityList.remove(activity);
    }


    /**
     * get instance of EaseUI
     *
     * @return
     */
    public synchronized static EaseUI getInstance()
    {
        if (instance == null)
        {
            instance = new EaseUI();
        }
        return instance;
    }

    /**
     * this function will initialize the SDK and easeUI kit
     *
     * @param context
     * @param options use default if options is null
     * @return
     */
    public synchronized boolean init(Context context, EMOptions options)
    {
//        if (sdkInited)
//        {
//            return true;
//        }
        appContext = context;
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        IconUtil.getInstance().init();
        // if there is application has remote service, application:onCreate() maybe called twice
        // this check is to make sure SDK will initialized only once
        // return if process name is not application's name since the package name is the default process name
        if (processAppName == null || !processAppName.equalsIgnoreCase(appContext.getPackageName()))
        {
            return false;
        }
        if (options == null)
        {
            EMClient.getInstance().init(context, initChatOptions());
        }
        else
        {
            EMClient.getInstance().init(context, options);
        }

        initNotifier();
        registerMessageListener();

        if (settingsProvider == null)
        {
            settingsProvider = new DefaultSettingsProvider();
        }
        //sdkInited = true;
        return true;
    }


    protected EMOptions initChatOptions()
    {
        EMOptions options = new EMOptions();
        // change to need confirm contact invitation
        options.setAcceptInvitationAlways(false);
        // set if need read ack
        options.setRequireAck(true);
        // set if need delivery ack
        options.setRequireDeliveryAck(false);
        return options;
    }

    private  void initNotifier()
    {
        notifier = createNotifier();
        notifier.init(appContext);
    }

    private void registerMessageListener()
    {
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener()
        {

            @Override
            public void onMessageReceived(List<EMMessage> messages)
            {
                EaseAtMessageHelper.get().parseMessages(messages);
            }


            @Override
            public void onMessageChanged(EMMessage message, Object change)
            {

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages)
            {

            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> messages) {

            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages)
            {

            }
        });
    }

    protected EaseNotifier createNotifier()
    {
        return new EaseNotifier();
    }

    public EaseNotifier getNotifier()
    {
        return notifier;
    }

    public boolean hasForegroundActivies()
    {
        return activityList.size() != 0;
    }

    /**
     * set user profile provider
     *
     * @param userProvider
     */
/*    public void setUserProfileProvider(EaseUserProfileProvider userProvider)
    {
        this.userProvider = userProvider;
    }*/

    /**
     * get user profile provider
     *
     * @return
     */
/*    public EaseUserProfileProvider getUserProfileProvider()
    {
        return userProvider;
    }*/

    public void setSettingsProvider(EaseSettingsProvider settingsProvider)
    {
        this.settingsProvider = settingsProvider;
    }

    public EaseSettingsProvider getSettingsProvider()
    {
        return settingsProvider;
    }


    /**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     *
     * @param pID
     * @return
     */
    private String getAppName(int pID)
    {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext())
        {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try
            {
                if (info.pid == pID)
                {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e)
            {
                 Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    /**
     * User profile provider
     *
     * @author wei
     */
/*    public interface EaseUserProfileProvider
    {
        EaseUser getUser(String username);
    }*/

    /**
     * Emojicon provider
     */
    public interface EaseEmojiconInfoProvider
    {
        EmojiIcon getEmojiconInfo(String emojiconIdentityCode);

        /**
         * get Emojicon map, key is the text of emoji, value is the resource id or local path of emoji icon(can't be URL on internet)
         *
         * @return
         */
        Map<String, Object> getTextEmojiconMapping();
    }

    private EaseEmojiconInfoProvider emojiconInfoProvider;

    /**
     * Emojicon provider
     *
     * @return
     */
    public EaseEmojiconInfoProvider getEmojiconInfoProvider()
    {
        return emojiconInfoProvider;
    }

    /**
     * set Emojicon provider
     *
     * @param emojiconInfoProvider
     */
    public void setEmojiconInfoProvider(EaseEmojiconInfoProvider emojiconInfoProvider)
    {
        this.emojiconInfoProvider = emojiconInfoProvider;
    }

    /**
     * new message options provider
     */
    public interface EaseSettingsProvider
    {
        boolean isMsgNotifyAllowed(EMMessage message);

        boolean isMsgSoundAllowed(EMMessage message);

        boolean isMsgVibrateAllowed(EMMessage message);

        boolean isSpeakerOpened();
    }

    /**
     * default settings provider
     */
    protected class DefaultSettingsProvider implements EaseSettingsProvider
    {

        @Override
        public boolean isMsgNotifyAllowed(EMMessage message)
        {
            return true;
        }

        @Override
        public boolean isMsgSoundAllowed(EMMessage message)
        {
            return true;
        }

        @Override
        public boolean isMsgVibrateAllowed(EMMessage message)
        {
            return true;
        }

        @Override
        public boolean isSpeakerOpened()
        {
            return true;
        }
    }

    public Context getContext()
    {
        return appContext;
    }

    /**
     * 与外界的接口
     */
    public interface EaseUiEventHappenedListener
    {
        void onEventHappened(String eventType, Object info);
    }
}
