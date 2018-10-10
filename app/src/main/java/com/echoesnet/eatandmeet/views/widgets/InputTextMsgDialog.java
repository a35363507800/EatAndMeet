package com.echoesnet.eatandmeet.views.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.LiveSendPacketAct;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.Presenter.LiveRoomPre1;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.orhanobut.logger.Logger;


/**
 * Created by yqh on 2017/3/20.
 */

public class InputTextMsgDialog extends Dialog implements View.OnClickListener, View
        .OnKeyListener, SwitchView.OnStateChangedListener
{

    private LiveRoomPre1 mPresent;
    private Context mContext;
    private RelativeLayout root;
    private EditText etInputMsg;
    private Button btSendMsg;
    private SwitchView barrageSv;
    private InputMethodManager imm;
    private IOnSendMsgListener mListener;
    private ViewChangeListener mViewListener;
    //弹幕等级要求
    private int BARRAGE_LEVEL = 4;
    private int level;

    public InputTextMsgDialog(Context context, int theme, boolean barrageState, int liveState)
    {
        super(context, theme);
        this.mContext = context;
        initView(barrageState, liveState);
    }

    private void initView(boolean barrageState, int liveState)
    {
        setContentView(R.layout.input_text_dialog);
        root = find(R.id.root);
        etInputMsg = find(R.id.dialog_input);
        btSendMsg = find(R.id.dialog_send);
        barrageSv = find(R.id.sv_dialog_barrage);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        btSendMsg.setOnClickListener(this);
        root.setOnClickListener(this);
        etInputMsg.setOnKeyListener(this);

        try
        {
            this.level = SharePreUtils.getLevel(mContext);
        } catch (NumberFormatException ex)
        {
            this.level = 0;
        }

        barrageSv.setBarrageFlag(true);
        barrageSv.setOnStateChangedListener(this);

        if (SharePreUtils.getIsSignAnchor(mContext).equals("1"))
            BARRAGE_LEVEL = 0;

        if (liveState == LiveRecord.ROOM_MODE_HOST)
        {
            barrageSv.setVisibility(View.GONE);
        }
        else
        {
            if (level < BARRAGE_LEVEL)
            {
                barrageSv.toggleSwitch(false);
            }
            else
            {
                barrageSv.toggleSwitch(barrageState);
                if (barrageState)
                    etInputMsg.setHint(mContext.getResources().getString(R.string.edHintBarrageOn));

            }
        }

       queryBarrageLevel();

    }

    @Override
    public void toggleToOn()
    {
        if (level < BARRAGE_LEVEL)
        {
            barrageSv.toggleSwitch(false);
            ToastUtils.showShort("Lv" + BARRAGE_LEVEL + "级开启弹幕功能");
            return;
        }
        barrageSv.toggleSwitch(true);
        etInputMsg.setHint(mContext.getResources().getString(R.string.edHintBarrageOn));
        if (mViewListener != null)
        {
            mViewListener.toggleToOn();
        }
    }

    @Override
    public void toggleToOff()
    {
        if (level < BARRAGE_LEVEL)
        {
            barrageSv.toggleSwitch(false);
            ToastUtils.showShort("Lv" + BARRAGE_LEVEL + "级开启弹幕功能");
            return;
        }
        barrageSv.toggleSwitch(false);
        etInputMsg.setHint(mContext.getResources().getString(R.string.edHintBarrageOff));
        if (mViewListener != null)
        {
            mViewListener.toggleToOff();
        }

        //  ((LiveRoomAct1)mContext).switchBarrage(false);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.root:
                //根布局，点击关闭键盘及隐藏dialog
                if (imm != null)
                    imm.hideSoftInputFromWindow(etInputMsg.getWindowToken(), 0);
                dismiss();
                mListener.onInputHasShowOrHide();
                mListener.onClickRoot();
                break;
            case R.id.dialog_send:
                if (mListener != null)
                {
                    String text = etInputMsg.getText().toString().trim();
                    if (barrageSv.getState() == barrageSv.STATE_SWITCH_OFF)
                        mListener.onClick(v, "sendText", text);

                    else if (barrageSv.getState() == barrageSv.STATE_SWITCH_ON)
                    {
                        if (text.length() > 20)
                        {
                            ToastUtils.showShort("弹幕内容不能超过20字");
                            return;
                        }
                        mListener.onClick(v, "sendBarrageText", text);
                    }

                    etInputMsg.setText("");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        if (event.getAction() != KeyEvent.ACTION_UP)
        {   // 忽略其它事件
            return false;
        }
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_ENTER:
                if (mListener != null)
                {
                    String text = etInputMsg.getText().toString().trim();
                    if (barrageSv.getState() == barrageSv.STATE_SWITCH_OFF)
                        mListener.onClick(v, "sendText", text);
                    else if (barrageSv.getState() == barrageSv.STATE_SWITCH_ON)
                    {
                        if (text.length() > 20)
                        {
                            ToastUtils.showShort("弹幕内容不能超过20字");
                            break;
                        }
                        mListener.onClick(v, "sendBarrageText", text);
                    }

                    etInputMsg.setText("");
                }

                break;
            default:
                break;
        }
        return false;
    }

    public void setBarrageLevel(String level)
    {
        try
        {
            this.BARRAGE_LEVEL = Integer.parseInt(level);
        } catch (NumberFormatException ex)
        {

        }
    }

    public void updataLevel()
    {
        try
        {
            this.level = SharePreUtils.getLevel(mContext);
        } catch (NumberFormatException ex)
        {
            this.level = 0;
        }
    }

    public void hideTextInput()
    {
        if (imm != null)
            imm.hideSoftInputFromWindow(etInputMsg.getWindowToken(), 0);
        if (isShowing())
            dismiss();
    }

/*    private void sendText()
    {
        String text = etInputMsg.getText().toString().trim();
        if (!TextUtils.isEmpty(text))
        {
            mPresent.sendNormalGroupMessage(text);
            mPresent.sendBarrageMessage(text);
            etInputMsg.setText("");

        }
        else
        {
            ToastUtils.showShort(mContext, "请输入聊天内容");
        }
    }*/

    /*    public void hideSoftKeyBoard()
        {
            Timer timer = new Timer();
            timer.schedule(new TimerTask()
            {

                public void run()
                {
                    InputMethodManager inputManager = (InputMethodManager) etInputMsg.getContext
                    ().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(etInputMsg.getWindowToken(),
                    InputMethodManager.RESULT_HIDDEN);
                    etInputMsg.requestFocus();
                    dismiss();
                }
            }, 500);

        }*/


    private void queryBarrageLevel()
    {
        NetHelper.checkPrivilegeToLevel(mContext, "003", new ICommonOperateListener()
        {
            @Override
            public void onSuccess(String response)
            {
                if (BARRAGE_LEVEL!=0)
                    setBarrageLevel(response);
            }

            @Override
            public void onError(String code, String msg)
            {

            }
        });
    }

    @Override
    public void show()
    {
        super.show();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                InputMethodManager inputManager = (InputMethodManager) etInputMsg.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(etInputMsg, InputMethodManager.SHOW_FORCED);
                etInputMsg.requestFocus();
                if (mListener != null)
                {
                    mListener.onInputHasShowOrHide();
                }
            }
        }, 500);
    }

    private <T extends View> T find(int id)
    {
        return (T) findViewById(id);
    }

    public void setIOnSendMsgListener(IOnSendMsgListener listener)
    {
        this.mListener = listener;
    }

    public interface IOnSendMsgListener
    {
        void onClick(View view, String viewName, String msg);

        void onClickRoot();

        void onInputHasShowOrHide();
    }

    public void setViewChangeListener(ViewChangeListener listener)
    {
        this.mViewListener = listener;
    }

    public interface ViewChangeListener
    {
        void toggleToOn();

        void toggleToOff();

    }
}
