package com.echoesnet.eatandmeet.views.widgets.LiveMsgDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.fragments.ChatFragment;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.joanzapata.iconify.widget.IconTextView;

/**
 * Created by yqh on 2017/3/28.
 */

public class LiveChatDialog extends DialogFragment implements View.OnClickListener
{
    private TextView root;
    private TextView userName;
    private EaseUser toEaseUser;
    private LinearLayout mainLLayout;
    private ChatFragment chatFragment;

    public static LiveChatDialog newInstance(EaseUser toEaseUser)
    {
        LiveChatDialog liveChatDialogFragment = new LiveChatDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.EXTRA_TO_EASEUSER, toEaseUser);
        liveChatDialogFragment.setArguments(bundle);
        return liveChatDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(R.style.inputDialog, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        if (getArguments() != null)
        {
            toEaseUser = getArguments().getParcelable(Constant.EXTRA_TO_EASEUSER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Window window = getDialog().getWindow();
        if (window != null)
        {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = CommonUtils.getScreenSize(getActivity()).width;
            window.setAttributes(lp);
            window.getAttributes().windowAnimations = R.style.AnimationBottomInOut;
        }
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.live_chat_dialog, container, false);

        root = view.findViewById(R.id.root);

        userName = (TextView) view.findViewById(R.id.to_chat_username);
        mainLLayout = (LinearLayout) view.findViewById(R.id.lLayout_main);

        IconTextView itvBack = view.findViewById(R.id.icon_back);
        IconTextView itvClose = view.findViewById(R.id.icon_close);

        itvBack.setOnClickListener(this);
        itvClose.setOnClickListener(this);

        root.setOnClickListener(this);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
        initView();
        return view;
    }

    private void initView()
    {
        chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.EXTRA_TO_EASEUSER, toEaseUser);
        chatFragment.setArguments(bundle);
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.lLayout_content, chatFragment)
                .commit();

        chatFragment.setOnEmojiMenuOpenListener(new ChatFragment.IEmojiMenuOpenListener()
        {
            @Override
            public void onEmojiMenuOpen()
            {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mainLLayout.getLayoutParams();
                params.height = CommonUtils.dp2px(getActivity(), 440);
                mainLLayout.setLayoutParams(params);
            }

            @Override
            public void onEmojiMenuClose()
            {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mainLLayout.getLayoutParams();
                params.height = CommonUtils.dp2px(getActivity(), 370);
                mainLLayout.setLayoutParams(params);
            }
        });

        /*chatFragment.setMoreAndEmojiListener(new ChatInputMenu.ChatInputMoreAndEmojiListener()
        {

            @Override
            public void onMoreOrEmojiBtnShow(View view)
            {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mainLLayout.getLayoutParams();
                layoutParams.height = CommonUtils.dp2px(getActivity(), 531);
                mainLLayout.setLayoutParams(layoutParams);
            }

            @Override
            public void onMoreOrEmojiBtnHide()
            {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mainLLayout.getLayoutParams();
                layoutParams.height = CommonUtils.dp2px(getActivity(), 331);
                mainLLayout.setLayoutParams(layoutParams);
            }
        });*/
        String name = TextUtils.isEmpty(toEaseUser.getRemark()) ? toEaseUser.getNickName() : toEaseUser.getRemark();
        userName.setText(name);
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        if (onDismissListener != null)
            onDismissListener.onDisMiss();
        super.onDismiss(dialog);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.root:
                dismiss();
                break;
            case R.id.icon_back:
            case R.id.icon_close:
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void show(FragmentManager manager, String tag)
    {

        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    private OnDismissListener onDismissListener;

    public void setOnDismissListener(OnDismissListener DismissListener)
    {
        onDismissListener = DismissListener;
    }

    public interface OnDismissListener
    {
        void onDisMiss();
    }

    public void upHeight()
    {
        if (getActivity() != null && !getActivity().isFinishing())
        {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInputFromWindow(root.getWindowToken(), 0, 0);
            imm.toggleSoftInputFromWindow(root.getWindowToken(), 0, 0);
        }
    }
}
