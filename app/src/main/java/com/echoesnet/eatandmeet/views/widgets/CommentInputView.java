package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.datamodel.EmojiIcon;
import com.echoesnet.eatandmeet.utils.IMUtils.EamSmileUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/9/16 0016
 * @description
 */
public class CommentInputView extends LinearLayout implements TrendsEmojiView.EmojiItemClick
{
    EditText editComment;
    TextView toCommentTv;
    TrendsEmojiView trendsEmojiView;
    IconTextView iconTvEmoji;
    private CommentInputListener commentInputListener;

    public CommentInputView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        initView();
    }
    public void setCommentInputListener(CommentInputListener commentInputListener)
    {
        this.commentInputListener = commentInputListener;
    }

    private void initView()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.comment_input_view,this);
        editComment = (EditText) findViewById(R.id.edit_comment);
        toCommentTv = (TextView) findViewById(R.id.tv_to_comment);
        trendsEmojiView = (TrendsEmojiView) findViewById(R.id.emoji_view);
        iconTvEmoji = (IconTextView) findViewById(R.id.icon_tv_emoji);
        iconTvEmoji.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (trendsEmojiView.getVisibility() == View.VISIBLE)
                {
                    trendsEmojiView.setVisibility(View.GONE);
                    iconTvEmoji.setText("{eam-s-smile-face}");
                    showOrHideSoftInput(true);
                } else
                {
                    iconTvEmoji.setText("{eam-e662}");
                    trendsEmojiView.setVisibility(View.VISIBLE);
                    showOrHideSoftInput(false);
                }
            }
        });
        editComment.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    editComment.setHint("输入评论");
                } else
                {
                    iconTvEmoji.setText("{eam-s-smile-face}");
                    trendsEmojiView.setVisibility(View.GONE);
                }
            }
        });
        editComment.addTextChangedListener(new TextWatcher()
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
                if (!TextUtils.isEmpty(s.toString()))
                {
                    toCommentTv.setEnabled(true);
                    toCommentTv.setAlpha(1);
                } else
                {
                    toCommentTv.setEnabled(false);
                    toCommentTv.setAlpha(0.3f);
                }

            }
        });
        trendsEmojiView.setEmojiItemClick(this);
        RxView.clicks(toCommentTv)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>()
                {
                    @Override
                    public void accept(Object o) throws Exception
                    {
                        String comment = editComment.getText().toString().trim();
                        if (TextUtils.isEmpty(comment))
                        {
                            ToastUtils.showShort("评论不可以为空");
                        }else {
                              if (commentInputListener != null)
                                  commentInputListener.commentClick(comment);
                        }
                    }
                });
    }

    /**
     * 点击外界 收起 输入法 或 emoji 表情  需在dispatchTouchEvent 中调用
     * @param mAct
     * @param ev
     */
    public boolean hideInputOrEmoji(Activity mAct,MotionEvent ev)
    {
        boolean isHide = false;
        if (ev.getAction() != MotionEvent.ACTION_DOWN)
            return false;
        Activity act = (Activity) mAct;
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
        {
            View v = act.getCurrentFocus();
            isHide = isHideInputOrEmoji(this, ev);
            if (isHide)
            {
                editComment.clearFocus();
                InputMethodManager imm = (InputMethodManager) mAct.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                iconTvEmoji.setText("{eam-s-smile-face}");
                trendsEmojiView.setVisibility(View.GONE);
            }
        }
        return isHide;
    }

    private boolean isHideInputOrEmoji(View v, MotionEvent event)
    {
        if (v != null)
        {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top)
            {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 初始化控件 收起输入法 emoji
     */
    public void regain(){
        toCommentTv.setEnabled(true);
        showOrHideSoftInput(false);
        iconTvEmoji.setText("{eam-s-smile-face}");
        trendsEmojiView.setVisibility(View.GONE);
        editComment.setText("");
    }

    public EditText getEditComment()
    {
        return editComment;
    }

    public void showOrHideSoftInput(boolean isShow)
    {
        if (isShow)
        {
            editComment.setFocusable(true);
            editComment.setFocusableInTouchMode(true);
            editComment.requestFocus();
            //打开软键盘
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } else
        {
            editComment.clearFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editComment.getWindowToken(), 0);
        }
    }

    @Override
    public void itemClick(EmojiIcon emojiBean)
    {
        editComment.append(EamSmileUtils.getSmiledText(getContext(), emojiBean.getEmojiText()));
    }

    @Override
    public void deleteClick()
    {
        if (!TextUtils.isEmpty(editComment.getText()))
        {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            editComment.dispatchKeyEvent(event);
        }
    }



    public interface CommentInputListener{

        void commentClick(String content);
    }

}
