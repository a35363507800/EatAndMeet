package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.RefreshLiveMsgBean;
import com.echoesnet.eatandmeet.models.bean.TXIMChatEntity;
import com.echoesnet.eatandmeet.models.datamodel.LiveMsgType;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.LruCacheBitmapLoader;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by an on 2017/5/17 0017.
 * Refactor by ben on 2017/11/8
 * description
 * 本类是直播间会话列表的适配类，直播间会话列表存在的最大问题就是会使直播间卡顿，关键问题有两个
 * 1：刷新频率 2：单次刷新对UI的影响
 * 单次：如果单次刷新都对UI有较大影响的话，那么就是无解的，所以首先需要解决单次刷新不卡顿问题。
 * 主要注意Listview 的item 一定要轻量，布局一定要简单，布局内控件一定要轻量，不要动态操作布局，不要使用Html,注意同类型布局重用...
 * <p>
 * 频率：方案1：固定频率（2秒）检查消息队列，如果存在消息则刷新
 * 方案2：当调用刷新频率高于每2秒一次时，采用批量刷新，例如当两次刷新操作间隔大于2秒时正常刷新，当小于2秒时候不刷新，加入消息队列，
 * 当消息队列达到一定长度时候 例如10，一次刷出来（此方案存在小瑕疵，需要配合设计解决，例如队列长度为10，某人连续刷新9次，那么
 * 如果没有其他刷新动作的前提下，则9条信息会暂时隐藏，等待下次刷新操作）。
 */
public class ChatMsgListAdapter extends BaseAdapter implements AbsListView.OnScrollListener, View.OnClickListener
{
    private static final String TAG = ChatMsgListAdapter.class.getSimpleName();
    private static final int MSG_TEXT = 0;
    private static final int MSG_TEXT_NAME_TEXT = 1;
    private static final int LEVEL_NAME_TEXT = 2;
    private static final int TEXT = 3;
    private static final int LEVEL_NAME_TEXT_IMAGE_1 = 4;
    private static final int LEVEL_NAME_TEXT_IMAGE_2 = 5;
    private static final int TEXT_NAME_TEXT = 6;
    private static final int MSG_NAME_TEXT = 7;


    private Activity mAct;
    private List<TXIMChatEntity> listMessage = null;
    private List<TXIMChatEntity> tempListMessage = null;
    private final int GIFT_SIZE;
    private ListView listView;

    private volatile boolean isLastItemVisible = true;
    private boolean mScrolling = false;
    private int pointer;

    private volatile TXIMChatEntity lastChatEntity;

    public ChatMsgListAdapter(Activity mAct, ListView listView)
    {
        this.mAct = mAct;
        this.listMessage = Collections.synchronizedList(new ArrayList<>());
        this.tempListMessage =Collections.synchronizedList(new ArrayList<>());
        this.listView = listView;
        GIFT_SIZE = CommonUtils.dp2px(mAct, 50);
        listView.setOnScrollListener(this);
        refreshTimer();
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public int getCount()
    {
        return listMessage.size();
    }

    @Override
    public TXIMChatEntity getItem(int position)
    {
        return listMessage.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemViewType(int position)
    {
        TXIMChatEntity itemBean = getItem(position);
        int msgType = itemBean.getType().getNum();
        if (msgType == LiveMsgType.NormalText.getNum() || msgType == LiveMsgType.FocusHost.getNum()
                || msgType == LiveMsgType.EnterRoom.getNum()
                || msgType == LiveMsgType.ParseHost.getNum())
        {
            return LEVEL_NAME_TEXT;
        }
        else if (msgType == LiveMsgType.SendRedPacket.getNum())
        {
            return LEVEL_NAME_TEXT_IMAGE_1;
        }
        else if (msgType == LiveMsgType.ReceiveRedPacket.getNum())
        {
            return MSG_TEXT_NAME_TEXT;
        }
        else if (msgType == LiveMsgType.Admin.getNum() || msgType == LiveMsgType.NotAdmin.getNum()
                || msgType == LiveMsgType.ShutUp.getNum() || msgType == LiveMsgType.NotShutUp.getNum()
                || msgType == LiveMsgType.ReceiveRedToSendPacket.getNum())
        {
            return MSG_NAME_TEXT;
        }
        else if (msgType == LiveMsgType.SmallGift.getNum() || msgType == LiveMsgType.BigGift.getNum())
        {
            return LEVEL_NAME_TEXT_IMAGE_2;
        }
        else if (msgType == LiveMsgType.DeclarationGoldenLight.getNum())
        {
            return TEXT_NAME_TEXT;
        }
        else if (msgType == LiveMsgType.Declaration.getNum())
        {
            return MSG_TEXT;
        }
        else
        {
            return TEXT;
        }
    }

    @Override
    public int getViewTypeCount()
    {
        return 8;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        MTViewHolder mtViewHolder = null;
        MNTViewHolder mntViewHolder = null;
        LNTViewHolder lntViewHolder = null;
        LNTI1ViewHolder lnti1ViewHolder = null;
        LNTI2ViewHolder lnti2ViewHolder = null;
        TNTViewHolder tntViewHolder = null;
        MTNTViewHolder mtntViewHolder = null;

        try
        {
            TXIMChatEntity itemBean = getItem(position);
            int viewType = getItemViewType(position);
            switch (viewType)
            {
                case MSG_TEXT:
                    if (convertView == null)
                    {
                        convertView = LayoutInflater.from(mAct).inflate(R.layout.item_chat_msg_text, null);
                        mtViewHolder = new MTViewHolder();
                        mtViewHolder.tvMsgContent = convertView.findViewById(R.id.tv_content);
                        convertView.setTag(mtViewHolder);
                    }
                    else
                    {
                        mtViewHolder = (MTViewHolder) convertView.getTag();
                    }
                    mtViewHolder.tvMsgContent.setText(itemBean.getContent());
                    break;
                case MSG_TEXT_NAME_TEXT:
                    if (convertView == null)
                    {
                        convertView = LayoutInflater.from(mAct).inflate(R.layout.item_chat_msg_text_name_text, null);
                        mtntViewHolder = new MTNTViewHolder();
                        mtntViewHolder.tvContentL = convertView.findViewById(R.id.tv_content_l);
                        mtntViewHolder.tvUser = convertView.findViewById(R.id.tv_user);
                        mtntViewHolder.tvContentR = convertView.findViewById(R.id.tv_content_r);
                        convertView.setTag(mtntViewHolder);
                    }
                    else
                    {
                        mtntViewHolder = (MTNTViewHolder) convertView.getTag();
                    }
                    mtntViewHolder.tvUser.setTag(itemBean);
                    mtntViewHolder.tvUser.setOnClickListener(this);
                    mtntViewHolder.tvUser.setText(getNameStr(itemBean.getGrpSendName(), false));
                    mtntViewHolder.tvContentL.setText(itemBean.getMsgL());
                    mtntViewHolder.tvContentR.setText(itemBean.getMsgR());
                    break;
                case LEVEL_NAME_TEXT:
                    if (convertView == null)
                    {
                        convertView = LayoutInflater.from(mAct).inflate(R.layout.item_chat_level_name_text, null);
                        lntViewHolder = new LNTViewHolder();
                        lntViewHolder.levelView = convertView.findViewById(R.id.level_view);
                        lntViewHolder.tvUser = convertView.findViewById(R.id.tv_user);
                        lntViewHolder.tvContent = convertView.findViewById(R.id.tv_content);
                        convertView.setTag(lntViewHolder);
                    }
                    else
                    {
                        lntViewHolder = (LNTViewHolder) convertView.getTag();
                    }
                    lntViewHolder.tvUser.setTag(itemBean);
                    lntViewHolder.tvUser.setOnClickListener(this);
                    lntViewHolder.tvUser.setText(getNameStr(itemBean.getGrpSendName(), true));
                    try
                    {
                        int level = Integer.parseInt(itemBean.getLevel());
                        if (level != 0)
                        {
                            lntViewHolder.levelView.setVisibility(View.VISIBLE);
                            lntViewHolder.levelView.setImageDrawable(ContextCompat.getDrawable(mAct, LevelView.getLevelImage(level)));
                        }
                        else
                        {
                            lntViewHolder.levelView.setImageDrawable(null);
                            lntViewHolder.levelView.setVisibility(View.GONE);
                        }

                    } catch (Exception e)
                    {
                        Logger.t(TAG).d(e.getMessage());
                    }
                    if (itemBean.getType().getNum() == LiveMsgType.NormalText.getNum())
                        lntViewHolder.tvContent.setTextColor(ContextCompat.getColor(mAct, R.color.white));
                    else
                        lntViewHolder.tvContent.setTextColor(Color.parseColor("#44cf89"));
                    lntViewHolder.tvContent.setText(itemBean.getContent());
                    break;
                case TEXT:
                    break;
                case LEVEL_NAME_TEXT_IMAGE_1:
                    if (convertView == null)
                    {
                        convertView = LayoutInflater.from(mAct).inflate(R.layout.item_chat_level_name_text_image_red, null);
                        lnti1ViewHolder = new LNTI1ViewHolder();
                        lnti1ViewHolder.tvUser = convertView.findViewById(R.id.tv_user);
                        lnti1ViewHolder.ivImage1 = convertView.findViewById(R.id.iv_red);
                        lnti1ViewHolder.tvContent = convertView.findViewById(R.id.tv_content);
                        lnti1ViewHolder.levelView = convertView.findViewById(R.id.level_view);
                        convertView.setTag(lnti1ViewHolder);
                    }
                    else
                    {
                        lnti1ViewHolder = (LNTI1ViewHolder) convertView.getTag();
                    }
                    lnti1ViewHolder.tvUser.setTag(itemBean);
                    lnti1ViewHolder.ivImage1.setTag(itemBean);
                    lnti1ViewHolder.tvUser.setOnClickListener(this);
                    lnti1ViewHolder.ivImage1.setOnClickListener(this);
                    bindSendPacketItem(itemBean, lnti1ViewHolder);
                    break;
                case LEVEL_NAME_TEXT_IMAGE_2:
                    if (convertView == null)
                    {
                        convertView = LayoutInflater.from(mAct).inflate(R.layout.item_chat_level_name_text_image_gift, null);
                        lnti2ViewHolder = new LNTI2ViewHolder();
                        lnti2ViewHolder.tvUser = convertView.findViewById(R.id.tv_user);
                        lnti2ViewHolder.ivImage2 = convertView.findViewById(R.id.iv_gift);
                        lnti2ViewHolder.tvContent = convertView.findViewById(R.id.tv_content);
                        lnti2ViewHolder.levelView = convertView.findViewById(R.id.level_view);
                        convertView.setTag(lnti2ViewHolder);
                    }
                    else
                    {
                        lnti2ViewHolder = (LNTI2ViewHolder) convertView.getTag();
                    }
                    lnti2ViewHolder.tvUser.setTag(itemBean);
                    lnti2ViewHolder.ivImage2.setTag(itemBean);
                    lnti2ViewHolder.tvUser.setOnClickListener(this);
                    lnti2ViewHolder.ivImage2.setOnClickListener(this);
                    bindGiftItem(itemBean, lnti2ViewHolder);
                    break;
                case TEXT_NAME_TEXT:
                    if (convertView == null)
                    {
                        convertView = LayoutInflater.from(mAct).inflate(R.layout.item_chat_text_name_text, null);
                        tntViewHolder = new TNTViewHolder();
                        tntViewHolder.tvUser = convertView.findViewById(R.id.tv_user);
                        tntViewHolder.tvContentL = convertView.findViewById(R.id.tv_content_l);
                        tntViewHolder.tvContentR = convertView.findViewById(R.id.tv_content_r);
                        convertView.setTag(tntViewHolder);
                    }
                    else
                    {
                        tntViewHolder = (TNTViewHolder) convertView.getTag();
                    }
                    tntViewHolder.tvUser.setTag(itemBean);
                    tntViewHolder.tvUser.setOnClickListener(this);
                    tntViewHolder.tvUser.setText(getNameStr(itemBean.getGrpSendName(), false));
                    tntViewHolder.tvContentL.setText(itemBean.getMsgL());
                    tntViewHolder.tvContentR.setText(itemBean.getMsgR());
                    break;
                case MSG_NAME_TEXT:
                    if (convertView == null)
                    {
                        convertView = LayoutInflater.from(mAct).inflate(R.layout.item_chat_msg_name_text, null);
                        mntViewHolder = new MNTViewHolder();
                        mntViewHolder.tvMsgContent = convertView.findViewById(R.id.tv_content);
                        mntViewHolder.tvUser = convertView.findViewById(R.id.tv_user);
                        convertView.setTag(mntViewHolder);
                    }
                    else
                    {
                        mntViewHolder = (MNTViewHolder) convertView.getTag();
                    }
                    mntViewHolder.tvUser.setTag(itemBean);
                    mntViewHolder.tvUser.setOnClickListener(this);
                    mntViewHolder.tvUser.setText(itemBean.getSenderName());
                    mntViewHolder.tvMsgContent.setText(itemBean.getContent());
                    break;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("获取view错误"+ e==null?"null":e.getMessage());
            EamLogger.t(TAG).writeToDefaultFile("获取view错误:"+ e==null?"null":e.getMessage());
        }
        return convertView;
    }

    private String getNameStr(String name, boolean isFlag)
    {
        return name + (isFlag ? ": " : "");
    }

    private void bindGiftItem(TXIMChatEntity itemBean, LNTI2ViewHolder holder)
    {
        try
        {
            int level = Integer.parseInt(itemBean.getLevel());
            if (level != 0)
            {
                holder.levelView.setImageDrawable(ContextCompat.getDrawable(mAct, LevelView.getLevelImage(level)));
                holder.levelView.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.levelView.setImageDrawable(null);
                holder.levelView.setVisibility(View.GONE);
            }
        } catch (Exception e)
        {
            Logger.t(TAG).d(e.getMessage());
        }

        LruCacheBitmapLoader.getInstance().putBitmapInto(mAct, itemBean.getGiftUrl(), holder.ivImage2, GIFT_SIZE, GIFT_SIZE);

        holder.tvUser.setText(getNameStr(itemBean.getGrpSendName(), true));
        holder.tvContent.setText(itemBean.getContent());
    }

    private void bindSendPacketItem(TXIMChatEntity itemBean, LNTI1ViewHolder holder)
    {
        holder.ivImage1.setImageDrawable(ContextCompat.getDrawable(mAct, R.drawable.ico_hongbao));
        holder.tvContent.setText(itemBean.getContent());
        try
        {
            int level = Integer.parseInt(itemBean.getLevel());
            if (level != 0)
            {
                holder.levelView.setVisibility(View.VISIBLE);
                holder.levelView.setImageDrawable(ContextCompat.getDrawable(mAct, LevelView.getLevelImage(level)));
            }
            else
            {
                holder.levelView.setImageDrawable(null);
                holder.levelView.setVisibility(View.GONE);
            }
        } catch (NumberFormatException e)
        {
            Logger.t(TAG).d(e.getMessage());
        }
        holder.tvUser.setText(getNameStr(itemBean.getGrpSendName(), true));
    }

    @Override
    public void notifyDataSetChanged()
    {
        if (mScrolling)
        {
            super.notifyDataSetChanged();
            return;
        }
        super.notifyDataSetChanged();
        // 自动滚动到底部
        listView.post(new Runnable()
        {
            @Override
            public void run()
            {
                listView.setSelection(listView.getCount() - 1);
            }
        });
    }

    private void refreshTimer()
    {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                if (mScrolling)
                {
                    pointer = 0;
                    return;
                }
                if (isLastItemVisible)
                {
                    if (tempListMessage.size() > 0)
                    {
                        //updateMsgList();
                        handler.sendEmptyMessage(1);
                    }
                }
                else
                {
                    pointer++;
                    if (pointer >= 3 && tempListMessage.size() > 0)
                    {
                        //updateMsgList();
                        handler.sendEmptyMessage(1);
                    }
                }
            }
        }, 1000, 2 * 1000);
    }

    public void notifyDataChanged(@NonNull Map<String, Object> chatEntityMap)
    {
        boolean isAdd = (boolean) chatEntityMap.get("isAdd");
        if (isAdd)
        {
            tempListMessage.add((TXIMChatEntity) chatEntityMap.get("chatEntity"));
        }
        else
        {
            //updateMsgList();
            handler.sendEmptyMessage(1);
            return;
        }
    }

/*    public void updateMsgList(final RefreshLiveMsgBean liveMsgBean)
    {
        EamApplication.getInstance().getExecutor().execute(new Runnable()
        {
            @Override
            public void run()
            {
                pushMsgToList(liveMsgBean);
            }
        });
    }*/

    public void updateMsgList(final RefreshLiveMsgBean liveMsgBean)
    {
        Observable.create(new ObservableOnSubscribe<Map<String,Object>>()
        {
            @Override
            public void subscribe(ObservableEmitter<Map<String, Object>> e) throws Exception
            {
                pushMsgToList(liveMsgBean,e);
            }
        }).subscribeOn(Schedulers.computation())
                .unsubscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Map<String,Object>>()
                {
                    @Override
                    public void accept(Map<String,Object> entityMap) throws Exception
                    {
                        notifyDataChanged(entityMap);
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable e) throws Exception
                    {
                        EamLogger.t(TAG).writeToDefaultFile("消息类别解析错误》" + e.getMessage());
                        Logger.t(TAG).d("消息类别解析错误》" + e.getMessage());
                    }
                });
    }

    private void pushMsgToList(RefreshLiveMsgBean liveMsgBean,ObservableEmitter<Map<String,Object>> subscriber)
    {
        Logger.t(TAG).d("refreshText当前线程》》" + Thread.currentThread().getId());
        Map<String, Object> map = new HashMap<>();
        map.put("isAdd", true);
        if (liveMsgBean != null && !TextUtils.isEmpty(liveMsgBean.getText()))
        {
            TXIMChatEntity entity = new TXIMChatEntity();
            entity.setId(liveMsgBean.getId());
            entity.setuId(liveMsgBean.getuId());
            entity.setSenderName(liveMsgBean.getName());
            entity.setContent(liveMsgBean.getText());
            entity.setType(liveMsgBean.getType());
            entity.setLevel(liveMsgBean.getLevel());
            entity.setLiveLevelState(liveMsgBean.getLiveLevelState());
            entity.setStreamId(liveMsgBean.getStreamId());
            entity.setHxId(liveMsgBean.getHxId());
            entity.setMsgL(liveMsgBean.getMsgL());
            entity.setMsgR(liveMsgBean.getMsgR());
            //如果是礼物类型
            if (liveMsgBean.getType() == LiveMsgType.SmallGift || liveMsgBean.getType() == LiveMsgType.BigGift)
            {
                entity.setGiftName(liveMsgBean.getgName());
                entity.setGiftNum(liveMsgBean.getGiftNum());
                entity.setGiftUrl(liveMsgBean.getgUrl());
                entity.setIsBigGift(liveMsgBean.getgType());
            }
            int msgCount = getCount();
            Logger.t(TAG).d("live------>msgCount:" + msgCount);
            if (msgCount > 0)
            {
                TXIMChatEntity chatEntity =lastChatEntity;// listMessage.get(msgCount - 1);这个取的不准
                Logger.t(TAG).d("live------>listMessage上条数据：" + chatEntity.toString()+"\r\nlive------>本条数据："+entity.toString());
                if (entity.getType() == LiveMsgType.EnterRoom && chatEntity!=null && chatEntity.getType() == LiveMsgType.EnterRoom)
                {
                    Logger.t(TAG).d("live------>上一条与本条数据一致，同为 进入");
                    chatEntity.setContent(entity.getContent());
                    chatEntity.setSenderName(entity.getSenderName());
                    chatEntity.setLevel(entity.getLevel());
                    chatEntity.setId(entity.getId());
                    map.put("isAdd", false);
                    map.put("chatEntity", chatEntity);
                }
                else
                {
                    Logger.t(TAG).d("live------>上一条与本条数据不一致");
                    map.put("chatEntity", entity);
                }
                subscriber.onNext(map);
            }
            else
            {
                map.put("chatEntity", entity);
                subscriber.onNext(map);
            }
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == 1)
            {
                updateMsgList();
                notifyDataSetChanged();
                pointer = 0;//刷新后，计数归零
            }
        }
    };

    private void updateMsgList()
    {
        listMessage.addAll(tempListMessage);
        try
        {
            final int listMsgSize = listMessage.size();
            final int removeCount = listMsgSize - 60;
            if (removeCount > 0)
            {
                for (int i = 0; i < removeCount; i++)
                {
                    listMessage.remove(i);
                }
            }
        } catch (Exception e)
        {
            EamLogger.writeToDefaultFile("直播间公屏消息截短异常: " + e.getMessage());
        }
        lastChatEntity=listMessage.get(listMessage.size() - 1);
        tempListMessage.clear();
    }

    private void setLevel(LevelView levelView, String level)
    {
        levelView.setLevel(level, 1);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        switch (scrollState)
        {
            case SCROLL_STATE_FLING:
                break;
            case SCROLL_STATE_TOUCH_SCROLL:
                mScrolling = true;
                break;
            case SCROLL_STATE_IDLE:
                mScrolling = false;
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        final int lastItem = firstVisibleItem + visibleItemCount;
        Logger.t(TAG).d("lastItem:" + lastItem + "totalItemCount:" + totalItemCount);
        if (lastItem == totalItemCount)
            isLastItemVisible = true;
        else
            isLastItemVisible = false;
    }


    @Override
    public void onClick(View view)
    {
        if (mOnItemClickListener != null)
        {
            switch (view.getId())
            {
                case R.id.tv_user:
                    mOnItemClickListener.onUserNickClick(view, "", (TXIMChatEntity) view.getTag());
                    break;
                case R.id.iv_red:
                    mOnItemClickListener.onImageClick(view, "redPacket", (TXIMChatEntity) view.getTag());
                    break;
                case R.id.iv_gift:
                    mOnItemClickListener.onImageClick(view, "gift", (TXIMChatEntity) view.getTag());
                    break;
                default:
                    break;
            }
        }
    }


    private class MTViewHolder//"msg":"text"
    {
        private TextView tvMessage;
        private TextView tvMsgContent;
    }

    private class MNTViewHolder//"msg":"name":"text"
    {
        private TextView tvMessage;
        private TextView tvUser;
        private TextView tvMsgContent;
    }

    private class MTNTViewHolder//"message":"text":"name":"text"
    {
        private TextView tvMessage;//"消息："
        private TextView tvContentL;
        private TextView tvUser;
        private TextView tvContentR;
    }

    private class LNTViewHolder//"level":"name":"text"
    {
        private ImageView levelView;
        private TextView tvUser;
        private TextView tvContent;
    }

    private class TViewHolder//"text"
    {
        private TextView tvMsgContent;
    }

    private class LNTI1ViewHolder//"level":"name":"text":"image 1类型"
    {
        private ImageView levelView;
        private TextView tvUser;
        private TextView tvContent;
        private ImageView ivImage1;
    }

    private class LNTI2ViewHolder//"level":"name":"text":"image 2类型"
    {
        private ImageView levelView;
        private TextView tvUser;
        private TextView tvContent;
        private ImageView ivImage2;
    }

    private class TNTViewHolder//"text":"name"："text"
    {
        private TextView tvContentL;
        private TextView tvUser;
        private TextView tvContentR;
    }

    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickListener
    {
        void onUserNickClick(View view, String source, TXIMChatEntity entity);

        void onImageClick(View view, String source, TXIMChatEntity entity);
    }
}
