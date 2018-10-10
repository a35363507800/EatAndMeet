package com.ss007.swiprecycleview.hoders;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ss007.swiprecycleview.AdapterLoader;
import com.ss007.swiprecycleview.R;

public class BottomViewHolder extends RecyclerView.ViewHolder
{
    private LinearLayout contaier;
    private ProgressBar pb;
    private TextView content;
    @Nullable
    private final AdapterLoader.OnRefreshLoadMoreListener mListener;

    public BottomViewHolder(View itemView, AdapterLoader.OnRefreshLoadMoreListener listener)
    {

        super(itemView);
        contaier = (LinearLayout) itemView.findViewById(R.id.footer_container);
        pb = (ProgressBar) itemView.findViewById(R.id.progressbar);
        content = (TextView) itemView.findViewById(R.id.content);
        mListener = listener;
    }

    public void bindDateView(int state)
    {
        switch (state)
        {
            case AdapterLoader.STATE_LASTED:
                contaier.setVisibility(View.VISIBLE);
                contaier.setOnClickListener(null);
                pb.setVisibility(View.GONE);
                content.setText("---  没有更多了  ---");
                break;
            case AdapterLoader.STATE_LOADING:
                contaier.setVisibility(View.VISIBLE);
                content.setText("加载更多！！");
                contaier.setOnClickListener(null);
                pb.setVisibility(View.VISIBLE);
                if (mListener != null)
                {
                    mListener.onLoadMore();
                }
                break;
            case AdapterLoader.STATE_ERROR:
                contaier.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
                content.setText("--- 加载更多失败点击重试 ---");
                contaier.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (mListener != null)
                        {
                            mListener.onLoadMore();
                        }
                        content.setText("加载更多！！");
                        pb.setVisibility(View.VISIBLE);
                    }
                });
                break;
        }
    }

}
