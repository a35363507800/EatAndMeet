package com.echoesnet.eatandmeet.views.adapters;

/**
 * Created by Administrator on 2016/4/26.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class HLV_Myinfo_Adapter extends BaseAdapter{
    private int[] mIconIDs;
    private String[] mTitles;
    private Context mContext;
    private LayoutInflater mInflater;
    Bitmap iconBitmap;
    private int selectIndex = -1;

    private String[] peopleIcons = {
            "http://img3.imgtn.bdimg.com/it/u=2468982627,2477902966&fm=23&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=1951543671,2588069013&fm=23&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=1850159850,51447102&fm=23&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=4272805490,2732495472&fm=23&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=1245732808,1122026240&fm=23&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=2114288856,3253688986&fm=23&gp=0.jpg"
    };


//    public HorizontalListViewAdapter(Context context, String[] titles, int[] ids){
//    public HLV_Myinfo_Adapter(Context context, int[] ids){
    public HLV_Myinfo_Adapter(Context context, String[] imhs) {
        this.mContext = context;
//        this.mIconIDs = ids;
        this.mTitles = imhs;
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return mTitles.length;
    }
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.hlv_myinfo_item, null);
            holder.mImage=(ImageView)convertView.findViewById(R.id.iv_myinfo_header);
//            holder.mTitle=(TextView)convertView.findViewById(R.id.text_list_item);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        if(position == selectIndex){
            convertView.setSelected(true);
        }else{
            convertView.setSelected(false);
        }

//        holder.mTitle.setText(mTitles[position]);
//        iconBitmap = getPropThumnail(mIconIDs[position]);
//        holder.mImage.setImageBitmap(iconBitmap);
//        holder.mImage.setBackgroundResource(mIconIDs[position]);

        holder.mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        holder.mImage.setCornerRadius(100);
//        holder.mImage.setBorderWidth(R.dimen.d1);
//        holder.mImage.setBorderColor(Color.WHITE);
//        holder.mImage.setOval(true);
        GlideApp.with(mContext).load(peopleIcons[position]).into(holder.mImage);

        return convertView;
    }

    private static class ViewHolder {
        private TextView mTitle ;
        private ImageView mImage;
    }
    public void setSelectIndex(int i){
        selectIndex = i;
    }
}
