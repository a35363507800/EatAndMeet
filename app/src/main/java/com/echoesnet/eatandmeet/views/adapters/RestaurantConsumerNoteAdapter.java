package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.RestaurantNoteBean;
import com.zhy.autolayout.AutoRelativeLayout;

/**
 * Created by Administrator on 2017/7/15.
 */

public class RestaurantConsumerNoteAdapter extends ExpandAdapter<RestaurantNoteBean>
{
    private ListItemView listItemView = null;

    public RestaurantConsumerNoteAdapter(Context context, ListView listView, TextView textView, AutoRelativeLayout layout)
    {
        super(context, listView, textView, layout);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final RestaurantNoteBean noteBean = getItem(position);
        if (convertView == null)
        {
            listItemView = new ListItemView();
            convertView = inflater.inflate(R.layout.item_resturant_consumer_notes, parent, false);
            listItemView.tvNoteTitle = (TextView) convertView.findViewById(R.id.tv_note_title);
            listItemView.tvNoteContent = (TextView) convertView.findViewById(R.id.tv_note_content);
            listItemView.tvNoteEnd = (TextView) convertView.findViewById(R.id.tv_note_end);
            convertView.setTag(listItemView);
        } else
        {
            listItemView = (ListItemView) convertView.getTag();
        }

        listItemView.tvNoteTitle.setText(noteBean.getNoteTitle());
        listItemView.tvNoteContent.setText(noteBean.getNoteContext());
        listItemView.tvNoteEnd.setText(noteBean.getNoteEnd());
        return convertView;
    }

    public final class ListItemView
    {
        public TextView tvNoteTitle;
        public TextView tvNoteContent;
        public TextView tvNoteEnd;
    }
}
