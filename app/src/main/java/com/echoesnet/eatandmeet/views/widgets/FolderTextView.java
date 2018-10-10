package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.IMUtils.EamSmileUtils;
import com.orhanobut.logger.Logger;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/11 0011
 * @description
 */
public class FolderTextView extends android.support.v7.widget.AppCompatTextView
{

    private static final String ELLIPSIS = "...";
    private static final String FOLD_TEXT = "收缩";
    private static final String UNFOLD_TEXT = "全文";

    /**
     * 收缩状态
     */
    private boolean isFold = false;

    /**
     * 绘制，防止重复进行绘制
     */
    private boolean isDrawed = false;
    /**
     * 内部绘制
     */
    private boolean isInner = false;

    /**
     * 折叠行数
     */
    private int foldLine;

    private boolean isShow = false;

    private boolean isShowFull = false;


    private String specialStr;

    /**
     * 全文本
     */
    private String fullText;
    private float mSpacingMult = 1.0f;
    private float mSpacingAdd = 0.0f;
    private FolderClickListener folderClickListener;

    public void init()
    {
        isShow = false;
    }

    /**
     * 设置高亮字
     * @param specialStr
     */
    public void setSpecialStr(String specialStr)
    {
        this.specialStr = specialStr;
    }


    public void setShowFull(boolean showFull)
    {
        isShowFull = showFull;
    }

    public FolderTextView(Context context)
    {
        this(context, null);
    }

    public FolderTextView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public FolderTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(R.styleable.FolderTextView);
        foldLine = a.getInt(R.styleable.FolderTextView_foldline, 2);

        a.recycle();
    }

    /**
     * 不更新全文本下，进行展开和收缩操作
     *
     * @param text
     */
    private void setUpdateText(CharSequence text)
    {
        isInner = true;
        setText(EamSmileUtils.getSmiledText(getContext(), text));

    }

    @Override
    public void setText(CharSequence text, BufferType type)
    {
        if (TextUtils.isEmpty(fullText) || !isInner)
        {
            isDrawed = false;
            fullText = String.valueOf(text);
        }
        try
        {
            Logger.t("folderText").d("text..." + text);
            super.setText(text, type);
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t("folderText").d(e.getMessage()+"...");
        }
    }

    @Override
    public void setLineSpacing(float add, float mult)
    {
        mSpacingAdd = add;
        mSpacingMult = mult;
        super.setLineSpacing(add, mult);
    }

    public int getFoldLine()
    {
        return foldLine;
    }

    public void setFoldLine(int foldLine)
    {
        this.foldLine = foldLine;
    }

    private Layout makeTextLayout(Spannable text)
    {
        return new StaticLayout(text, getPaint(), getWidth() - getPaddingLeft() - getPaddingRight(),
                Layout.Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (!isDrawed)
        {
            resetText();
        }
        super.onDraw(canvas);
        isDrawed = true;
        isInner = false;
    }

    private void resetText()
    {
        String spanText = fullText;

        SpannableString spanStr;

        //收缩状态
        if (isFold)
        {
            spanStr = createUnFoldSpan(spanText);
        } else
        { //展开状态
            spanStr = createFoldSpan(spanText);
        }

        setUpdateText(spanStr);
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 创建展开状态下的Span
     *
     * @param text 源文本
     * @return
     */
    private SpannableString createUnFoldSpan(String text)
    {
        String destStr = text + FOLD_TEXT;
        int start = destStr.length() - FOLD_TEXT.length();
        int end = destStr.length();
        if (start < 0)
            start = 0;

        SpannableString spanStr = new SpannableString(destStr);
        spanStr.setSpan(clickSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

    /**
     * 创建收缩状态下的Span
     *
     * @param text
     * @return
     */
    private SpannableString createFoldSpan(String text)
    {
        SpannableString spanStr = null;
        try
        {
            String destStr;
            if (!isShowFull)
            {
                destStr = tailorText(EamSmileUtils.getSmiledText(getContext(),text));
            }else
            {
                destStr = text;
            }
            spanStr = new SpannableString(destStr);
            if (!isShowFull)
            {
                int start = isShow ? destStr.length() - UNFOLD_TEXT.length() : destStr.length();
                int end = destStr.length();
                if (start < 0)
                    start = 0;
                Logger.t("folderText").d(destStr + "|start>>"+ start + "|end>>>" + end);
                if (start < end)
                    spanStr.setSpan(clickSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (!TextUtils.isEmpty(specialStr))
            {
                int index = text.indexOf(specialStr);
                Logger.t("folderText").d(destStr + "|start>>"+ index + "|end>>>" + index + specialStr.length());
                if (index >= 0)
                    spanStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.C0412)), index, index + specialStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e)
        {
            e.printStackTrace();

        }

        return spanStr;
    }

    /**
     * 裁剪文本至固定行数
     *
     * @param text 源文本
     * @return
     */
    private String tailorText(Spannable text)
    {
        String destStr = text + ELLIPSIS + UNFOLD_TEXT;
        Layout layout = makeTextLayout(EamSmileUtils.getSmiledText(getContext(),destStr));

        Logger.t("fold").d("限制行数" + getFoldLine() + "| " + layout.getLineCount() + "|length" + text.length() + "|" + text);
        //如果行数大于固定行数
        if (layout.getLineCount() > getFoldLine())
        {
            isShow = true;
            int index = layout.getLineEnd(getFoldLine());
            if (text.length() < index)
            {
                index = text.length();
            }
            ImageSpan[] imageSpen =  text.getSpans(text.length() - 6,text.length()-1,ImageSpan.class);
            if (imageSpen.length >0)
                text = (Spannable) text.subSequence(0,text.getSpanStart(imageSpen[imageSpen.length - 1]));
            else
                text = (Spannable) text.subSequence(0, index - 2); //从最后一位逐渐试错至固定行数
            return tailorText(text);
        } else
        {
            if (isShow)
                return destStr;
            else
                return text.toString();
        }
    }

    /**
     * 设置折叠点击
     *
     * @param folderClickListener
     */
    public void setFolderClickListener(FolderClickListener folderClickListener)
    {
        this.folderClickListener = folderClickListener;
    }

    ClickableSpan clickSpan = new ClickableSpan()
    {
        @Override
        public void onClick(View widget)
        {
            if (folderClickListener != null)
                folderClickListener.folderClick();
//            isFold = !isFold;
            isDrawed = false;
//            invalidate();
        }

        @Override
        public void updateDrawState(TextPaint ds)
        {
            ds.setColor(ContextCompat.getColor(getContext(), R.color.C0412));
        }
    };

    private int getAvailableWidth()
    {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private boolean isOverFlowed()
    {
        Paint paint = getPaint();
        float width = paint.measureText(getText().toString());
        if (width > getAvailableWidth()) return true;
        return false;
    }

    public interface FolderClickListener
    {
        void folderClick();
    }

}
