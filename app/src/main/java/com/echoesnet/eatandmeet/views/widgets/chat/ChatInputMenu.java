package com.echoesnet.eatandmeet.views.widgets.chat;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.datamodel.DefaultEmojiconDatas;
import com.echoesnet.eatandmeet.models.datamodel.DolphinEmojiconDatas;
import com.echoesnet.eatandmeet.models.datamodel.EmojiGroupEntity;
import com.echoesnet.eatandmeet.models.datamodel.EmojiIcon;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.EamSmileUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.MyChatInputIconGroupAdapter;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yqh
 * @version 1.0
 * @createDate 2017/7/12
 * @Description 聊天界面底部操作UI
 */

public class ChatInputMenu extends LinearLayout implements View.OnClickListener
{
    private LayoutInflater layoutInflater;
    private Context context;
    private InputMethodManager inputManager;
    private EditText editText;
    private GridView gridView;
    private Button btnSend;
    private LinearLayout imgContainer;
    private MyChatInputIconGroupAdapter iconGroupAdapter;
    private SendVoiceView sendVoiceView;
    private ChatEmojiMenu chatEmojiMenu;
    private ChatInputMenuListener listener;
    private boolean ctrlPress = false;
    private boolean isInited = false;
    List<Integer> imgs = new ArrayList<>();

    public ChatInputMenu(Context context)
    {
        super(context);
        init(context, null);
    }

    public ChatInputMenu(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public ChatInputMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        this.context = context;
        inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.my_chat_inputmenu, this);
        editText = (EditText) findViewById(R.id.et_inputMessage);
        btnSend = (Button) findViewById(R.id.btn_text_send);
        gridView = (GridView) findViewById(R.id.icon_group);
        sendVoiceView = (SendVoiceView) findViewById(R.id.my_send_voice_ui);
        chatEmojiMenu = (ChatEmojiMenu) findViewById(R.id.chat_emoji_menu);
        imgContainer = (LinearLayout) findViewById(R.id.ll_container);
        inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
//        if (CommonUtils.isInLiveRoom)
//            gridView.setOnItemClickListener(onItemClickListener);
//        else
//            gridView.setOnItemClickListener(this);
        initGridView();
        initListener();
    }

    public void init(List<EmojiGroupEntity> emojiGroupList)
    {
        if (isInited)
        {
            return;
        }
        if (emojiGroupList == null)
        {
            emojiGroupList = new ArrayList<>();
            emojiGroupList.add(new EmojiGroupEntity(R.drawable.ee_1_blush, Arrays.asList(DefaultEmojiconDatas.getData()), EmojiIcon.Type.NORMAL));
            emojiGroupList.add(new EmojiGroupEntity(R.drawable.ico_jianpan16, Arrays.asList(DolphinEmojiconDatas.getData()), EmojiIcon.Type.NORMAL_AS_EXPRESSION));
        }
        chatEmojiMenu.init(emojiGroupList);
        isInited = true;
    }

    public void clearInputFocus()
    {
        editText.clearFocus();
    }

    private void initGridView()
    {
        if (CommonUtils.isInLiveRoom)
        {
            imgs.add(R.drawable.ico_photo_xhdpi);
            imgs.add(R.drawable.ico_hongbao_chat_input);
            imgs.add(R.drawable.ico_location_xhdpi);
            imgs.add(R.drawable.ico_expression_xhdpi);
        }
        else
        {
            imgs.add(R.drawable.ico_voice_xhdpi);
            imgs.add(R.drawable.ico_photo_xhdpi);
            imgs.add(R.drawable.ico_camera_xhdpi);
            imgs.add(R.drawable.ico_hongbao_chat_input);
            imgs.add(R.drawable.ico_location_xhdpi);
            imgs.add(R.drawable.ico_expression_xhdpi);
        }


        for (int i = 0; i < imgs.size(); i++)
        {
            ImageView view = new ImageView(context);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            params.gravity = Gravity.CENTER_VERTICAL;
            view.setLayoutParams(params);
            view.setImageResource(imgs.get(i));
            view.setTag(imgs.get(i));
            view.setOnClickListener(this);
            imgContainer.addView(view);
        }


//        iconGroupAdapter = new MyChatInputIconGroupAdapter(context, imgs);
//        gridView.setAdapter(iconGroupAdapter);
    }

    public void hideKeyBroad()
    {
        inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * Hide the sendVoice view or emojiView.
     */
    public void hideSendVoiceViewOrEmojiView()
    {
        if (sendVoiceView == null || chatEmojiMenu == null)
            return;
        if (sendVoiceView.getVisibility() == VISIBLE)
        {
            sendVoiceView.setVisibility(GONE);
        }
        else if (chatEmojiMenu.getVisibility() == VISIBLE)
        {
            chatEmojiMenu.setVisibility(GONE);
            listener.onEmojiMenuClick(false);
        }
    }

    /**
     * 初始化监听
     */
    private void initListener()
    {
        sendVoiceView.setOnSendVoiceStateListener(new SendVoiceView.OnSendVoiceStateListener()
        {
            @Override
            public void onTouchListener(View view, MotionEvent event)
            {
                if (listener != null)
                    listener.onPressToSpeakBtnTouch(view, event);
            }
        });
        editText.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.onEditTextHasFocus("editText");
                hideSendVoiceViewOrEmojiView();
            }
        });
        editText.setOnFocusChangeListener(new OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    listener.onEditTextHasFocus("editText");
                    hideSendVoiceViewOrEmojiView();
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > 0)
                    btnSend.setBackground(ContextCompat.getDrawable(context, R.drawable.round_cornor_15_mc1_bg));
                else
                    btnSend.setBackground(ContextCompat.getDrawable(context, R.drawable.round_cornor_15_c0331_bg));
            }
        });

        editText.setOnKeyListener(new OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                EMLog.d("key", "keyCode:" + keyCode + " action:" + event.getAction());

                // test on Mac virtual machine: ctrl map to KEYCODE_UNKNOWN
                if (keyCode == KeyEvent.KEYCODE_UNKNOWN)
                {
                    if (event.getAction() == KeyEvent.ACTION_DOWN)
                    {
                        ctrlPress = true;
                    }
                    else if (event.getAction() == KeyEvent.ACTION_UP)
                    {
                        ctrlPress = false;
                    }
                }
                if (keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    if (listener != null)
                        listener.onEditTextHasFocus("editText_key_enter");
                }
                return false;
            }
        });
        btnSend.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String s = editText.getText().toString();
                if (!TextUtils.isEmpty(s))
                {
                    editText.setText("");
                    listener.onSendMessage(s);
                }
            }
        });
        /*editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
//                EMLog.d("key", "keyCode:" + event.getKeyCode() + " action" + event.getAction() + " ctrl:" + ctrlPress);
                if (actionId == EditorInfo.IME_ACTION_SEND
//                        ||(event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
//                                event.getAction() == KeyEvent.ACTION_DOWN &&
//                                ctrlPress)
                        )
                {
                    String s = editText.getText().toString();
                    if (TextUtils.isEmpty(s))
                    {
                        ToastUtils.showShort("请输入聊天内容.");
                        return true;
                    }
                    editText.setText("");
                    listener.onSendMessage(s);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });*/
        chatEmojiMenu.setEmojiconMenuListener(new ChatEmojiMenu.EmojiconMenuListener()
        {
            @Override
            public void onExpressionClicked(EmojiIcon emojicon)
            {
                if (emojicon.getType() == EmojiIcon.Type.NORMAL)
                {
                    if (emojicon.getEmojiText() != null)
                    {
                        editText.append(EamSmileUtils.getSmiledText(context, emojicon.getEmojiText()));
                    }
                }
                else
                {
                    if (listener != null)
                    {
                        listener.onBigExpressionClicked(emojicon);
                    }
                }
            }

            @Override
            public void onDeleteImageClicked()
            {
                if (!TextUtils.isEmpty(editText.getText()))
                {
                    KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                    editText.dispatchKeyEvent(event);
                }
            }

            @Override
            public void onScrollTabBarSendClicked()
            {
                String s = editText.getText().toString().trim();
                if (TextUtils.isEmpty(s))
                {
                    ToastUtils.showShort("请输入聊天内容.");
                    return;
                }
                editText.setText("");
                listener.onScrollBarSendMessage(s);
            }

            @Override
            public void onAddEmojiBtnClicked()
            {
                listener.onAddEmojiBtnClicked();
            }
        });
    }

    /**
     * 切换语音与emoji 显示
     *
     * @param isVoiceShow 是否是语音显示
     */
    private void changeVoiceAndEmojiShown(boolean isVoiceShow)
    {
        if (isVoiceShow)
        {
            if (chatEmojiMenu.getVisibility() == VISIBLE)
                chatEmojiMenu.setVisibility(GONE);
            if (sendVoiceView.getVisibility() == GONE)
                sendVoiceView.setVisibility(VISIBLE);
            else
                sendVoiceView.setVisibility(GONE);
        }
        else
        {
            if (sendVoiceView.getVisibility() == VISIBLE)
                sendVoiceView.setVisibility(GONE);
            if (chatEmojiMenu.getVisibility() == GONE)
            {
                chatEmojiMenu.setVisibility(VISIBLE);
                listener.onEmojiMenuClick(true);
            }
            else
            {
                chatEmojiMenu.setVisibility(GONE);
                listener.onEmojiMenuClick(false);
            }

        }
    }

    public boolean onBackPressed()
    {
        if (sendVoiceView.getVisibility() == VISIBLE)
        {
            sendVoiceView.setVisibility(GONE);
            return false;
        }
        else if (chatEmojiMenu.getVisibility() == VISIBLE)
        {
            chatEmojiMenu.setVisibility(GONE);
            return false;
        }
        else
        {
            return true;
        }

    }

    public ChatEmojiMenu getChatEmojiMenu()
    {
        return chatEmojiMenu;
    }

    public SendVoiceView getSendVoiceView()
    {
        return sendVoiceView;
    }

    public void setChatInputMenuListener(ChatInputMenuListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View v)
    {
        switch ((Integer) v.getTag())
        {
            case R.drawable.ico_voice_xhdpi:
                inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        listener.onEditTextHasFocus("voice");
                    }
                }, 300);

                if (CommonUtils.isVoicePermission())
                {
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            changeVoiceAndEmojiShown(true);
                        }
                    }, 300);
                }
                else
                {
                    ToastUtils.showShort("缺少录音权限，请打开后重试！");
                }
                break;
            case R.drawable.ico_photo_xhdpi:
                listener.onSelectImageClicked();
                break;
            case R.drawable.ico_camera_xhdpi:
                listener.onTakePhotoClicked();
                break;
            case R.drawable.ico_hongbao_chat_input:
                listener.onSendRedPackageClicked();
                break;
            case R.drawable.ico_location_xhdpi:
                listener.onSendLocationClicked();
                break;
            case R.drawable.ico_expression_xhdpi:
                inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                listener.onEditTextHasFocus("emoji");
                changeVoiceAndEmojiShown(false);
                break;

        }
    }

    public interface ChatInputMenuListener
    {
        /**
         * 发送普通消息
         */
        void onSendMessage(String content);

        /**
         * 表情处发送点击
         *
         * @param content
         */
        void onScrollBarSendMessage(String content);

        /**
         * 选择图片点击
         */
        void onSelectImageClicked();

        /**
         * 拍照点击
         */
        void onTakePhotoClicked();

        /**
         * 发送红包点击
         */
        void onSendRedPackageClicked();

        /**
         * 发送位置点击
         */
        void onSendLocationClicked();

        /**
         * 大表情点击
         */
        void onBigExpressionClicked(EmojiIcon emojicon);

        /**
         * 按下录音
         */
        boolean onPressToSpeakBtnTouch(View v, MotionEvent event);

        /**
         * 添加表情
         */
        void onAddEmojiBtnClicked();

        /**
         * 点击输入框 顶起 聊天内容
         */
        void onEditTextHasFocus(String type);

        /**
         * 点击emoji 监听，for 直播间 聊天窗口高度
         *
         * @param isShown
         */
        void onEmojiMenuClick(boolean isShown);

    }

}
