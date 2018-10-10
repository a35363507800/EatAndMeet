package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.SearchUserBean;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * @author Administrator
 * @Date 2017/10/12
 * @Version 1.0
 */

public class SearchUserAdapter extends BaseAdapter
{
    private static final String TAG = SearchUserAdapter.class.getSimpleName();

    private Activity mAct;
    private List<SearchUserBean> data;
    private Holder holder;
    private OnFocusClickListener clickListener;

    private String searchKey = "";

    public SearchUserAdapter(Activity activity, List<SearchUserBean> data)
    {
        this.mAct = activity;
        this.data = data;
    }

    @Override
    public int getCount()
    {
        return data.size();
    }

    @Override
    public Object getItem(int position)
    {
        return data.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    public void setSearchKey(String key)
    {
        searchKey = key;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mAct).inflate(R.layout.search_user_item, parent, false);
            holder = new Holder();
            holder.headerView = (LevelHeaderView) convertView.findViewById(R.id.riv_head);
            holder.tvUserName = (TextView) convertView.findViewById(R.id.username);
            holder.tvExplain = (TextView) convertView.findViewById(R.id.tv_explain);
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }
        final SearchUserBean bean = data.get(position);
        holder.headerView.setHeadImageByUrl(bean.getUphUrl());
        holder.headerView.showRightIcon(bean.getIsVuser());
        String username = TextUtils.isEmpty(bean.getRemark()) ? bean.getNicName() : bean.getRemark();
        if (TextUtils.isEmpty(username))
        {
            username = "";
        }
        if (TextUtils.isEmpty(bean.getId()))
            bean.setId("");
        SpannableStringBuilder builder = matchKeys(searchKey, username);
        builder.append(" (ID:");
        builder.append(matchKeys(searchKey, bean.getId()));
        builder.append(")");

        holder.tvUserName.setText(builder);
        switch (bean.getFocus())
        {
            case "2":
                holder.tvExplain.setText("互相关注");
                holder.tvExplain.setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
                holder.tvExplain.setBackgroundResource(R.drawable.round_cornor_11_white_bg);
                holder.tvExplain.setEnabled(false);
                break;
            case "1":
                holder.tvExplain.setText("已关注");
                holder.tvExplain.setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
                holder.tvExplain.setBackgroundResource(R.drawable.round_cornor_11_white_bg);
                holder.tvExplain.setEnabled(false);
                break;
            default:
                holder.tvExplain.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
                holder.tvExplain.setBackgroundResource(R.drawable.round_cornor_11_c0412_bg);
                holder.tvExplain.setText("+关注");
                holder.tvExplain.setEnabled(true);
                break;
        }

        RxView.clicks(holder.tvExplain)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>()
                {
                    @Override
                    public void accept(@NonNull Object o) throws Exception
                    {
                        if ("0".equals(bean.getFocus()))
                        {
                            if (clickListener != null)
                                clickListener.onFocusClick(position, holder.tvExplain);
                        }

                    }
                });
        return convertView;
    }

    /**
     * 匹配 字符 高亮
     *
     * @param searchKey 搜索的关键词
     * @param key       全文
     * @return 带高亮的Spannable
     */
    private SpannableStringBuilder matchKeys(String searchKey, String key)
    {
        SpannableStringBuilder spBuilder = new SpannableStringBuilder(key);
        //匹配规则
        Pattern p = Pattern.compile("(?i)" + searchKey);
        //匹配字段
        Matcher m = p.matcher(spBuilder);
        int color = ContextCompat.getColor(mAct, R.color.C0412);
        while (m.find())
        {
            int start = m.start();
            int end = m.end();
            spBuilder.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spBuilder;
    }

    class Holder
    {
        private LevelHeaderView headerView;
        private TextView tvUserName;
        private TextView tvExplain;
    }

    public interface OnFocusClickListener
    {
        void onFocusClick(int position, View view);
    }

    public void setOnFocusClickListener(OnFocusClickListener itemClickListener)
    {
        clickListener = itemClickListener;
    }

}
