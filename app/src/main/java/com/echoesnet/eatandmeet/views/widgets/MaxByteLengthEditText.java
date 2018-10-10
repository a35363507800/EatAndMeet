package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2016/10/8.
 */

public class MaxByteLengthEditText extends android.support.v7.widget.AppCompatEditText
{
    private int maxByteLength = 100;
    private String encoding = "GBK";

    public MaxByteLengthEditText(Context context)
    {
        super(context);
        init();
    }

    public MaxByteLengthEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        setFilters(new InputFilter[]{inputFilter});
    }

 /*   @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect)
    {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        focused = true;
    }*/

    public void setMaxByteLength(int maxByteLength)
    {
    this.maxByteLength = maxByteLength;
    //setFilters(new InputFilter[]{new InputFilter.LengthFilter()}
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    /**
     * input输入过滤
     */
    private InputFilter inputFilter = new InputFilter()
    {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                                     Spanned dest, int dstart, int dend)
        {
            try
            {
                int len = 0;
                boolean more = false;
                do
                {
                    SpannableStringBuilder builder =
                            new SpannableStringBuilder(dest).replace(dstart, dend, source.subSequence(start, end));
                    len = builder.toString().getBytes(encoding).length;
                    more = len > maxByteLength;
                    if (more)
                    {
                        end--;
                        source = source.subSequence(start, end);
                    }
                } while (more);
                return source;
            } catch (UnsupportedEncodingException e)
            {
                return "Exception";
            }
        }
    };
}
