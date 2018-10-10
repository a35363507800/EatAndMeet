package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.orhanobut.logger.Logger;

/**
 * Created by wangben on 2016/7/20.
 */
public class EditViewWithCharIndicate extends RelativeLayout
{
    private int maxCharNum;
    private String inputHint;
    private int inputTextColor, inputHintColor, bodyBackGround;
    private int inputGravity;

    public EditText getInputContent()
    {
        return inputContent;
    }

    private EditText inputContent;
    private TextView tvIndicator, tvMaxChar;
    private RelativeLayout rlEtBody;

    private Context context;
    private String inputAfterText;

    public EditViewWithCharIndicate(Context context)
    {
        this(context, null);
    }

    public EditViewWithCharIndicate(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs)
    {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EditViewWithCharIndicate);
        maxCharNum = ta.getInt(R.styleable.EditViewWithCharIndicate_maxCharNum, 500);
        inputTextColor = ta.getColor(R.styleable.EditViewWithCharIndicate_inputTextColor, ContextCompat.getColor(context, R.color.FC2));
        inputHintColor = ta.getColor(R.styleable.EditViewWithCharIndicate_inputHintColor, ContextCompat.getColor(context, R.color.FC3));
        inputHint = ta.getString(R.styleable.EditViewWithCharIndicate_inputHint);

        inputGravity = ta.getColor(R.styleable.EditViewWithCharIndicate_editTextGravity, Gravity.TOP | Gravity.LEFT);
        bodyBackGround = ta.getColor(R.styleable.EditViewWithCharIndicate_bodyBackGroundColor, ContextCompat.getColor(context, R.color.main_background_color));
        ta.recycle();
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        View v = LayoutInflater.from(getContext()).inflate(R.layout.uctr_editview_with_char_num_show, this);
        inputContent = (EditText) v.findViewById(R.id.et_input);
        tvIndicator = (TextView) v.findViewById(R.id.tv_current_char);
        tvMaxChar = (TextView) v.findViewById(R.id.tv_max_char);
        rlEtBody = (RelativeLayout) v.findViewById(R.id.rlEtBody);


        tvMaxChar.setText(String.format("/%d", maxCharNum));
        inputContent.setTextColor(inputTextColor);
        inputContent.setHintTextColor(inputHintColor);
        inputContent.setHint(inputHint);
        inputContent.setGravity(inputGravity);

        rlEtBody.setBackgroundColor(bodyBackGround);

        inputContent.setFilters(new InputFilter[]{filter});

        inputContent.addTextChangedListener(new TextWatcher()
        {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                inputAfterText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                temp = s;
               /* Logger.t("表情").d("s:" + s);
                if (containsEmoji(temp.toString()))
                {
                    ToastUtils.showShort(context, "不支持输入Emoji表情符号");
                    inputContent.setText(temp.subSequence(0, temp.length() - count));
                    inputContent.setSelection(inputContent.length());
                }*/
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                int number = maxCharNum - s.length();
               /* if (containsEmoji(s.toString()))
                    tvIndicator.setText((s.length() - 2) + "");
                else*/
                tvIndicator.setText((s.length()) + "");
                selectionStart = inputContent.getSelectionStart();
                selectionEnd = inputContent.getSelectionEnd();
                if (temp.length() > maxCharNum)
                {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    inputContent.setText(s);
                    inputContent.setSelection(tempSelection);//设置光标在最后
                }
            }
        });
    }

    public String getInputText()
    {
        return inputContent.getText().toString().trim();
    }

    InputFilter filter = new InputFilter()
    {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
        {
            for (int i = start; i < end; i++)
            {
                int type = Character.getType(source.charAt(i));
                //System.out.println("Type : " + type);
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL)
                {
                    ToastUtils.showShort("不支持输入Emoji表情符号");
                    return "";
                }
            }
            return null;
        }
    };


    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source)
    {
        int len = source.length();
        for (int i = 0; i < len; i++)
        {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint))
            { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint)
    {
        Logger.t("表情").d(String.valueOf(Integer.parseInt(String.valueOf(codePoint))));
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

}
