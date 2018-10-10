package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;

/**
 * Created by Administrator on 2016/5/6.
 */
public class MyInfoPhotoAdapter extends BaseAdapter {

    private Context context;
    private String[] photos;

    public MyInfoPhotoAdapter(Context context, String[] photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override
    public int getCount() {
        return photos.length;
    }

    @Override
    public Object getItem(int position) {
        return photos[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
//            convertView = LayoutInflater.from(context).inflate(R.layout.myinfo_gridview_photo_item, parent, false);
            convertView = LayoutInflater.from(context).inflate(R.layout.myinfo_gridview_photo_item, null);
            viewHolder.iv_photo = (ImageView) convertView.findViewById(R.id.iv_photo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        GlideApp.with(EamApplication.getInstance()).load(photos[position]).into(viewHolder.iv_photo);
        return convertView;
    }

    class ViewHolder {
        private ImageView iv_photo;
    }

}
