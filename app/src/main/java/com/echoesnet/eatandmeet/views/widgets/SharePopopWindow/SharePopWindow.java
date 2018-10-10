package com.echoesnet.eatandmeet.views.widgets.SharePopopWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.ShareToFaceBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.adapters.SharePopupAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yqh
 * @version 1.0
 * @createDate 2017/4/17
 * @Description 平台分享窗口
 */
public class SharePopWindow extends PopupWindow implements GridView.OnItemClickListener
{
    /**
     * 我的动态
     */
    public static final int SHARE_WAY_DYNAMIC = 0;
    /**
     * 看脸好友
     */
    public static final int SHARE_WAY_APPFRIEND = 1;
    /**
     * 微信好友
     */
    public static final int SHARE_WAY_WECHAT_FRIEND = 2;
    /**
     * QQ好友
     */
    public static final int SHARE_WAY_QQ_FRIEND = 3;
    /**
     * QQ空间
     */
    public static final int SHARE_WAY_QZONE = 4;
    /**
     * 微信朋友圈
     */
    public static final int SHARE_WAY_WECHAT_MOMENT = 5;
    /**
     * 新浪微博
     */
    public static final int SHARE_WAY_SINA = 6;

    private Activity mAct;
    private int[] showIndex;
    //    private int[] itemImg = new int[]{
//            R.drawable.share_weixin_xhdpi, R.drawable.share_qq_xhdpi,
//            R.drawable.share_klhy_xhdpi, R.drawable.share_wxpyq_xhdpi,
//            R.drawable.share_qqkongjian_xhdpi, R.drawable.share_weibo_xhdpi};
    private int[] itemImg = new int[]
            {R.drawable.wode_dynamic_xhdpi, R.drawable.share_kanlian_xhdpi,
                    R.drawable.share_weixinhaoyou_xhdpi, R.drawable.share_qq_xhdpi,
                    R.drawable.share_qqkongjian_xhdpi, R.drawable.share_wxpyq_xhdpi, R.drawable.share_weibo_xhdpi};
    //private String[] item = new String[]{"微信好友", "QQ好友", "看脸好友", "微信朋友圈", "QQ空间", "新浪微博"};
    private String[] item = new String[]{"我的动态", "看脸好友", "微信好友", "QQ好友", "QQ空间", "微信朋友圈", "新浪微博"};
    private GridView.OnItemClickListener listener;
    private TextView popupTitle;
    private GridView gridView;
    private List<Map<String, Object>> aList = new ArrayList<>();
    private ShareToFaceBean shareInfo;
    private View hideView;
    private SharePopupAdapter adapter;
    private ShareItemClickListener shareItemClickListener;

    //切后台 是否是分享
    public static boolean isShared = false;

    /**
     * @param act
     * @param showIndex []中包含以下字段
     *                  "我的动态"       ： SHARE_WAY_DYNAMIC ;
     *                  "看脸好友"       ： SHARE_WAY_APPFRIEND ;
     *                  "微信好友"       ： SHARE_WAY_WECHAT_FRIEND ;
     *                  "QQ好友"         ： SHARE_WAY_QQ_FRIEND ;
     *                  "QQ空间"         ： SHARE_WAY_QZONE ;
     *                  "微信朋友圈"     ： SHARE_WAY_WECHAT_MOMENT ;
     *                  "新浪微博"       ： SHARE_WAY_SINA
     */
    public SharePopWindow(Activity act, int[] showIndex, ShareToFaceBean shareInfo)
    {
        this.mAct = act;
        this.showIndex = showIndex;
        this.shareInfo = shareInfo;
        initWindow();
    }

    public SharePopWindow(Activity act, int[] showIndex, GridView.OnItemClickListener listener)
    {
        this.mAct = act;
        this.showIndex = showIndex;
        this.listener = listener;
        initWindow();
//        may expose internal representation by storing an externally mutable object into SharePopWindow.showIndex
    }

    public void setShowIndex(int[] showIndex)
    {
        this.showIndex = showIndex;
        initData();
    }

    public ShareToFaceBean getShareInfo()
    {
        return shareInfo;
    }

    public void setShareItemClickListener(ShareItemClickListener shareItemClickListener)
    {
        this.shareItemClickListener = shareItemClickListener;
    }

    private void initWindow()
    {
        LayoutInflater inflater = (LayoutInflater) mAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.share_popup_window, null);
        popupTitle = (TextView) view.findViewById(R.id.popup_title);
        gridView = (GridView) view.findViewById(R.id.gridView);
        if (shareInfo == null)
            gridView.setOnItemClickListener(listener);
        else
            gridView.setOnItemClickListener(this);
        // 设置SelectPicPopupWindow的View
        this.setContentView(view);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(CommonUtils.getScreenSize(mAct).width);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
//        this.backgroundAlpha(0.5f);
        this.setAnimationStyle(R.style.PopupAnimation);
        this.getContentView().setFocusableInTouchMode(true);
        this.getContentView().setFocusable(true);
        initData();
    }

    private void initData()
    {
        if (showIndex == null)
            throw new IllegalArgumentException("showIndex不能为空");
        aList.clear();
//        Map<String, Integer> map;
        if (showIndex.length == 0)
            showIndex = new int[]{0, 1, 2, 3, 4, 5, 6};
        for (int i = 0; i < showIndex.length; i++)
        {
            if (showIndex[i] > 6)
                throw new IllegalArgumentException("请输入正确的显示图标的标志位");
            Map<String, Object> map1 = new HashMap<>();
            map1.put("icon", itemImg[showIndex[i]]);
            map1.put("des", item[showIndex[i]]);
            aList.add(map1);
        }

/*        for (int i : showIndex)
        {
            map = new HashMap<>();
            map.put(item[i], itemImg[i]);
            aList.add(map);
        }*/
        if (aList.size() < 3)
        {
            gridView.setNumColumns(aList.size());
        }
        if (adapter == null)
        {
            adapter = new SharePopupAdapter(mAct, aList);
            gridView.setAdapter(adapter);
        }
        else
        {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent, View hideView)
    {
        if (!this.isShowing())
        {
            if (hideView == null)
            {
                backgroundAlpha(0.5f);
            }
            else
            {
                this.hideView = hideView;
                hideView.setVisibility(View.VISIBLE);
            }

            this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        }
        else
        {
            this.dismiss();
        }
    }

    @Override
    public void dismiss()
    {
        if (hideView == null)
        {
            backgroundAlpha(1.0f);
        }
        else
        {
            hideView.setVisibility(View.GONE);
        }
        super.dismiss();
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    private void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = mAct.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mAct.getWindow().setAttributes(lp);
    }


    public void setPopupTitle(String title)
    {
        if (TextUtils.isEmpty(title))
        {
            title = "分享";
        }
        popupTitle.setText(title);
    }

    public List<Map<String, Object>> getShareData()
    {
        return aList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        isShared = true;
        if (CommonUtils.isFastDoubleClick())
            return;

        if (shareItemClickListener != null)
            shareItemClickListener.onItemCLick(position, item[showIndex[position]]);


        CommonUtils.shareWithApp(mAct, aList.get(position).get("des").toString(), shareInfo);
        dismiss();
/*        for (String shareKey : aList.get(position).keySet())
        {
            CommonUtils.shareWithApp(mAct, shareKey, shareInfo);
        }*/
    }

    public interface ShareItemClickListener
    {
        void onItemCLick(int position, String shareKey);
    }
}
