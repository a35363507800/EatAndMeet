package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.ActionItemBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by lc on 2017/7/20 17.
 */

public class UserRightPop extends PopupWindow
{
    private final String TAG = UserRightPop.class.getSimpleName();
    private Activity mContext;
    protected final int LIST_PADDING = 10;
    protected Rect mRect = new Rect();
    private final int[] mLocation = new int[2];
    private int mScreenWidth, mScreenHeight;
    private boolean mIsDirty;
    private int popupGravity = Gravity.NO_GRAVITY;
    private OnItemOnClickListener mItemOnClickListener;
    private ListView mListView;
    private  ArrayList<ActionItemBean> mActionItems = new ArrayList<>();

    public UserRightPop(Activity context)
    {
        //设置布局的参数
        this(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mActionItems = new ArrayList<>();
    }

    public UserRightPop(Activity context, int width, int height)
    {
        this.mContext = context;
        //设置可以获得焦点
        setFocusable(true);
        //设置弹窗内可点击
        setTouchable(true);
        //设置弹窗外可点击
        setOutsideTouchable(true);
        //获得屏幕的宽度和高度
        mScreenWidth = CommonUtils.getScreenWidth(mContext);
        mScreenHeight = CommonUtils.getScreenHeight1(mContext);

        //设置弹窗的宽度和高度
        setWidth(width);
        setHeight(height);
        setBackgroundDrawable(new ColorDrawable(0));
        //设置弹窗的布局界面
        setContentView(LayoutInflater.from(mContext).inflate(R.layout.user_title_popup, null));
        initUI();
    }

    /**
     * 初始化弹窗列表
     */
    private void initUI()
    {
        mListView = (ListView) getContentView().findViewById(R.id.title_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3)
            {
                //点击子类项后，弹窗消失
                dismiss();
                if (mItemOnClickListener != null)
                    mItemOnClickListener.onItemClick(mActionItems.get(index), index);
            }
        });
    }

    /**
     * 显示弹窗列表界面
     */
    public void show(View view)
    {
        this.backgroundAlpha(0.5f);
        //获得点击屏幕的位置坐标
        view.getLocationOnScreen(mLocation);
        //设置矩形的大小
        mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(), mLocation[1] + view.getHeight());
        //判断是否需要添加或更新列表子类项
        if (mIsDirty)
        {
            populateActions();
        }
        //显示弹窗的位置
        showAtLocation(view, Gravity.TOP | Gravity.RIGHT, 46, mRect.bottom + 15);
//        showAtLocation(view, popupGravity, mScreenWidth - LIST_PADDING - (getWidth()/2), mRect.bottom);
    }

    public boolean checkIsAdded(String titleName)
    {
        for (int i = 0; i < mActionItems.size(); i++)
        {
            if (TextUtils.equals(titleName, mActionItems.get(i).getmTitle()))
            {
                return true;
            }
        }
      return false;
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mContext.getWindow().setAttributes(lp);
    }


    /**
     * 消失弹窗，设置添加屏幕的背景透明度
     */
    public void dismissPop()
    {
        this.backgroundAlpha(1f);
    }


    /**
     * 设置弹窗列表子项
     */
    UserRightPop.ViewHolder viewHolder;

    private void populateActions()
    {
        mIsDirty = false;
        //设置列表的适配器
        mListView.setAdapter(new BaseAdapter()
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                if (convertView == null)
                {
                    viewHolder = new UserRightPop.ViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.litem_title_popup, null);
                    viewHolder.itvIcon = (IconTextView) convertView.findViewById(R.id.itv_icon);
                    viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                    viewHolder.rivView = (RoundedImageView) convertView.findViewById(R.id.red_point);
                    convertView.setTag(viewHolder);
                } else
                {
                    viewHolder = (UserRightPop.ViewHolder) convertView.getTag();
                }
                ActionItemBean item = mActionItems.get(position);
                viewHolder.tvTitle.setText(item.mTitle);
                viewHolder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.C0322));

                viewHolder.itvIcon.setText(item.iconTextView);
                viewHolder.itvIcon.setTextColor(ContextCompat.getColor(mContext, R.color.C0322));
                if (item.isShow.equals("1") && position == 0)
                {
                    viewHolder.rivView.setVisibility(View.VISIBLE);
                } else
                {
                    viewHolder.rivView.setVisibility(View.GONE);
                }
                return convertView;
            }

            @Override
            public long getItemId(int position)
            {
                return position;
            }

            @Override
            public Object getItem(int position)
            {
                return mActionItems.get(position);
            }

            @Override
            public int getCount()
            {
                return mActionItems.size();
            }
        });
    }

    public class ViewHolder
    {
        public TextView tvTitle;
        public IconTextView itvIcon;
        public RoundedImageView rivView;
    }

    /**
     * 添加子类项
     */
    public void addAction(int positon, ActionItemBean action)
    {
        if (action != null)
        {
            mActionItems.add(positon, action);
            mIsDirty = true;
        }
    }

    /**
     * 移除子类项
     */
    public void removeAction(int position)
    {
        if (position < mActionItems.size())
            removeAction(mActionItems.get(position));
    }



    /**
     * 移除子类项，根据title
     */
    public void removeAction(String titleName)
    {
        if (titleName != null)
            for (int i = 0; i < mActionItems.size(); i++)
            {
                if (TextUtils.equals(titleName, mActionItems.get(i).getmTitle()))
                {
                    removeAction(mActionItems.get(i));
                }
            }

    }

    /**
     * 移除子类项
     */
    public void removeAction(ActionItemBean action)
    {
        if (action != null)
        {
            mActionItems.remove(action);
            mIsDirty = true;
        }
    }

    /**
     * 清除子类项
     */
    public void cleanAction()
    {
        if (mActionItems.isEmpty())
        {
            mActionItems.clear();
            mIsDirty = true;
        }
    }

    /**
     * 根据位置得到子类项
     */
    public ActionItemBean getAction(int position)
    {
        if (position < 0 || position > mActionItems.size())
            return null;
        return mActionItems.get(position);
    }

    /**
     * 设置监听事件
     */
    public void setItemOnClickListener(OnItemOnClickListener onItemOnClickListener)
    {
        this.mItemOnClickListener = onItemOnClickListener;
    }

    /**
     * 弹窗子类项按钮监听事件
     */
    public static interface OnItemOnClickListener
    {
        public void onItemClick(ActionItemBean item, int position);
    }


}
