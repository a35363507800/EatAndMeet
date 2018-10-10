package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.CAddEmojBean;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.FileUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.ZipUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/20.
 */

public class CAddEmojAdapter extends BaseAdapter
{
    private final static String TAG = CAddEmojAdapter.class.getSimpleName();
    private List<CAddEmojBean> dataSource;
    private Activity mContext;

    public CAddEmojAdapter(Activity context, List<CAddEmojBean> dataSource)
    {
        this.mContext = context;
        this.dataSource = dataSource;
    }

    @Override
    public int getCount()
    {
        return dataSource.size();
    }

    @Override
    public Object getItem(int position)
    {
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final CAddEmojBean emojBean = (CAddEmojBean) getItem(position);
        //Logger.t(TAG).d(emojBean.toString());
        ViewHolder holder = null;
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.litem_c_add_emoji, parent, false);
            holder.tvUserName = (TextView) convertView.findViewById(R.id.tv_u_nickName);
            holder.tvUserContactName = (TextView) convertView.findViewById(R.id.tv_u_phone_name);
            holder.ivUserImg = (RoundedImageView) convertView.findViewById(R.id.rdv_u_img);
            holder.tvUserOper = (Button) convertView.findViewById(R.id.btn_operate);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvUserName.setText(emojBean.getTitle());
        holder.tvUserContactName.setText(emojBean.getDescription());
        File file = new File(NetHelper.getRootDirPath(mContext) + NetHelper.EMOJI_FOLDER + emojBean.getEmojiFilePath());
        //如果解压缩文件存在，则认为已经下载了
        if (file.exists() && file.isDirectory())
        {
            holder.tvUserOper.setText("已下载");
            holder.tvUserOper.setEnabled(false);
            holder.tvUserOper.setBackgroundResource(R.color.white);
            holder.tvUserOper.setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
        }
        else
        {
            holder.tvUserOper.setText("下载");
            holder.tvUserOper.setEnabled(true);
            holder.tvUserOper.setBackgroundResource(R.drawable.round_cornor_18_mc7_bg);
            holder.tvUserOper.setTextColor(ContextCompat.getColor(mContext, R.color.MC7));
        }
        final TextView tempBtn = holder.tvUserOper;
        //下载Emojo
        holder.tvUserOper.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onDownLoadBtnClickedListener != null)
                    onDownLoadBtnClickedListener.onDownLoadBtnClicked(position, emojBean);
                tempBtn.setText(String.format(" %s %s", 0, "%"));
                downloadEmojiFile(emojBean.getEmojiZipUrl(), emojBean.getEmojiFilePath(), tempBtn);
            }
        });
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(emojBean.getImgUrl())
                .placeholder(R.drawable.userhead)
                .centerCrop()
                .into(holder.ivUserImg);
        return convertView;
    }

    /**
     * 下载并解压emoj表情
     *
     * @param zipFilePath
     * @param emojName
     * @param btn
     */
    private void downloadEmojiFile(final String zipFilePath, final String emojName, final TextView btn)
    {
        //返回值 -1：没有网络  1：WIFI网络2：wap网络3：net网络
        int i = NetHelper.getNetworkStatus(mContext);
        switch (i)
        {
            case -1:
                ToastUtils.showShort("当前无网络连接");
                break;
            case 1:
                //WiFi网络
                downloadEmojFile(zipFilePath, emojName, btn);
                break;
            case 2:
            case 3:
                new CustomAlertDialog(mContext)
                        .builder()
                        .setMsg("当前网络状态为移动网络，请确认")
                        .setTitle("提示")
                        .setPositiveButton("土豪请继续", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                downloadEmojFile(zipFilePath, emojName, btn);
                            }
                        }).setNegativeButton("取消", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                    }
                }).show();
                break;
        }

    }

    /**
     * 下载emoj文件
     *
     * @param url
     * @param name 文件名称
     * @param btn  操作按钮
     */
    private void downloadEmojFile(String url, String name, final TextView btn)
    {
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new FileCallBack(NetHelper.getRootDirPath(mContext) + NetHelper.EMOJI_FOLDER, name + ".zip")
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        Logger.t(TAG).d(e.getMessage());
                        btn.setEnabled(true);
                        btn.setText("下载失败");
                    }

                    @Override
                    public void onResponse(final File target)
                    {
                        Observable.create(new ObservableOnSubscribe<String>()
                        {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<String> e) throws Exception
                            {
                                //先下载，再解压
                                Logger.t(TAG).d("目标目录》" + NetHelper.getRootDirPath(mContext) + NetHelper.EMOJI_FOLDER);
                                ZipUtils.unZipWithoutSuffix(target, new File(NetHelper.getRootDirPath(mContext) + NetHelper.EMOJI_FOLDER), new ICommonOperateListener()
                                {
                                    @Override
                                    public void onSuccess(String response)
                                    {
                                        FileUtils.deleteFile(target.getPath());
                                        e.onNext(response);
                                    }

                                    @Override
                                    public void onError(String code, String msg)
                                    {
                                        e.onNext(code);
                                    }
                                });
                                /*ZipUtils.unZip(target, new File(NetHelper.getRootDirPath(mContext) + "Emojs"), new ICommonOperateListener()
                                {
                                    @Override
                                    public void onSuccess(String response)
                                    {
                                        FileUtils.deleteFile(target.getPath());
                                        e.onNext(response);
                                    }

                                    @Override
                                    public void onError(String code, String msg)
                                    {
                                       e.onNext(code);
                                    }
                                });*/
                            }
                        }).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe( new Observer<String>()
                                {
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d)
                                    {

                                    }

                                    @Override
                                    public void onNext(@NonNull String s)
                                    {
                                        if((EamConstant.EAM_FILE_UNZIP_FAILED+"").equals(s))
                                        {
                                            btn.setEnabled(true);
                                            btn.setText("下载失败");
                                        }
                                        else
                                        {
                                            btn.setEnabled(false);
                                            btn.setText("已下载");
                                            btn.setBackgroundResource(R.color.white);
                                            btn.setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
                                        }
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e)
                                    {
                                        btn.setEnabled(true);
                                        btn.setText("下载失败");
                                    }

                                    @Override
                                    public void onComplete()
                                    {

                                    }
                                });
                    }

                    @Override
                    public void inProgress(float mProgress, long total)
                    {
                        //Logger.t(TAG).d("p》"+mProgress+"    =="+total);
                        int progress = ((int) (mProgress * 100))-1;
                        btn.setText(String.format(" %s %s", progress, "%"));
                    }
                });
    }

    public class ViewHolder
    {
        RoundedImageView ivUserImg;
        TextView tvUserName;
        TextView tvUserContactName;
        Button tvUserOper;
    }

    private OnDownLoadBtnClickedListener onDownLoadBtnClickedListener;

    public interface OnDownLoadBtnClickedListener
    {
        void onDownLoadBtnClicked(int position, CAddEmojBean emojBean);
    }

    public void setOnDownLoadBtnClickedListener(OnDownLoadBtnClickedListener onDownLoadBtnClickedListener)
    {
        this.onDownLoadBtnClickedListener = onDownLoadBtnClickedListener;
    }


}
