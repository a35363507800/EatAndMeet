package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.PhotoBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.GlideRequests;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * 预览adapter
 */
public class PreviewAdapter extends PagerAdapter
{
    Context mContext;
    LayoutInflater mLayoutInflater;
    List<PhotoBean> mList;

    public PreviewAdapter(Context mContext, List<PhotoBean> paths) {
        this.mContext = mContext;
        this.mList = paths;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList==null?0:mList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        PhotoBean bean=mList.get(position);
        View itemView = mLayoutInflater.inflate(R.layout.item_preview, container, false);
        PhotoView imageView =  itemView.findViewById(R.id.iv_pager);
        SubsamplingScaleImageView scaleImageView =  itemView.findViewById(R.id.ss_img);
        ImageView imgPlay =  itemView.findViewById(R.id.play_vedio);
        if (bean.getType() == 0)
        {
            imgPlay.setVisibility(View.GONE);
        }else
        {
            imgPlay.setVisibility(View.VISIBLE);
        }
        final String path = bean.getPath();
        GlideRequests glideRequests =  GlideApp.with(EamApplication.getInstance());
        if (path.endsWith(".gif"))
        {
            imageView.setVisibility(View.VISIBLE);
            scaleImageView.setVisibility(View.GONE);
            glideRequests.asGif().diskCacheStrategy(DiskCacheStrategy.RESOURCE).load(path).into(imageView);
        }
        else
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            if (bean.getType() == 0 && imageHeight >= imageWidth * 3)
            {
                Logger.t("previewAdapter").d("放大" + path);
                imageView.setVisibility(View.GONE);
                scaleImageView.setVisibility(View.VISIBLE);
                float ra = (CommonUtils.getScreenWidth(mContext) * 1f)/imageWidth;
                scaleImageView.setMaxScale(ra);
                scaleImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                scaleImageView.setImage(ImageSource.uri(path), new ImageViewState(ra, new PointF(0, 0), 0));
            }else {
                imageView.setVisibility(View.VISIBLE);
                scaleImageView.setVisibility(View.GONE);
                glideRequests.asBitmap()
                        .centerCrop()
                        .load(path).into(imageView);
                imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        if (onClickListener!=null){
                            onClickListener.onClickListener(position);
                        }
                    }
                });
            }
        }
        container.addView(itemView);
        imgPlay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onClickListener != null)
                    onClickListener.playVideo(bean,position);
            }
        });
        return itemView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      container.removeView((View)object);
    }
    OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener{

        void onClickListener(int position);

        void playVideo(PhotoBean photoBean,int position);
    }
}
