package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.GameInviteBean;
import com.echoesnet.eatandmeet.models.bean.StartGameBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.EmptyRecyclerView;
import com.echoesnet.eatandmeet.views.adapters.LiveGameInviteAdapter;
import com.echoesnet.eatandmeet.views.adapters.LiveGameInvitedAdapter;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/11/13 0013
 * @description
 */
public class LiveGameInviteDialog extends DialogFragment
{

    private final String TAG = LiveGameInviteDialog.class.getSimpleName();
    @BindView(R.id.rv_invite)
    EmptyRecyclerView rvInvite;
    @BindView(R.id.rv_invited)
    EmptyRecyclerView rvInvited;
    @BindView(R.id.img_send_invite)
    ImageView imgSendInvite;
    @BindView(R.id.fl_send_invite)
    LinearLayout flSendInvite;
    @BindView(R.id.img_cancel)
    ImageView imgCancel;
    @BindView(R.id.img_switch_1)
    ImageView imgSwitch1;
    @BindView(R.id.img_switch_2)
    ImageView imgSwitch2;
    @BindView(R.id.img_empty)
    ImageView imgEmpty;
    @BindView(R.id.rl_send_invite)
    RelativeLayout rlSendInvite;
    @BindView(R.id.tv_current_num)
    TextView tvCurrentNum;
    @BindView(R.id.tv_total_face_egg)
    TextView tvTotalFaceEgg;
    @BindView(R.id.rl_start)
    RelativeLayout rlStart;//开始界面
    @BindView(R.id.fl_start)
    LinearLayout flStart;
    @BindView(R.id.img_start)
    ImageView imgStart;
    @BindView(R.id.tv_surplus_face_egg)
    TextView tvSurplusFaceEgg;
    @BindView(R.id.ll_switch)
    LinearLayout llSwitch;
    @BindView(R.id.ll_invite)
    RelativeLayout llInvite;
    @BindView(R.id.rl_rule)
    RelativeLayout rlRule;//规则界面
    @BindView(R.id.tv_time)
    TextView tvTime;

    private Activity mAct;
    private List<GameInviteBean> gameInviteList;//可邀请列表
    private List<GameInviteBean> gameInvitedList;//邀请列表
    private LiveGameInviteAdapter liveGameInviteAdapter;
    private LiveGameInvitedAdapter liveGameInvitedAdapter;
    private boolean isShowing = false;
    private boolean isAnchor = false;
    private Disposable disposable;
    private Disposable timeDis;
    private boolean isStart = false;
    private boolean isShowRule = false;
    private boolean isShowStart = false;
    private boolean heartStart = false;
    private boolean inviteLoadMore = false;
    private boolean invitedLoadMore = false;
    private boolean hasStartCountDown = false;
    private String anchorUId;
    private InviteDialogListener inviteDialogListener;
    private int currentPosition;
    private String admissionFee = "20",  total = "0",  num = "0",  faceEgg = "0";

    public boolean isShowStart()
    {
        return isShowStart;
    }

    public int getCurrentPosition()
    {
        return currentPosition;
    }

    public void setAnchor(boolean anchor)
    {
        isAnchor = anchor;
    }

    public void setInviteDialogListener(InviteDialogListener inviteDialogListener)
    {
        this.inviteDialogListener = inviteDialogListener;
    }

    public void setAnchorUId(String anchorUId)
    {
        this.anchorUId = anchorUId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        mAct = getActivity();
        isStart = false;
        isShowRule = false;
        heartStart = false;
        gameInvitedList = new ArrayList<>();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
        View view = LayoutInflater.from(mAct).inflate(R.layout.dialog_live_game_invite, null);
        ButterKnife.bind(this, view);
        llInvite.setVisibility(isShowStart ? View.GONE : View.VISIBLE);
        llSwitch.setVisibility(isShowStart ? View.GONE : View.VISIBLE);
        rlStart.setVisibility(isShowStart ? View.VISIBLE : View.GONE);
        imgCancel.setTag("cancel");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mAct);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvInvite.setLayoutManager(linearLayoutManager);
        rvInvite.addOnScrollListener(new RecyclerView.OnScrollListener()
        {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                int endCompletelyPosition = ((LinearLayoutManager) rvInvite.getLayoutManager()).
                        findLastCompletelyVisibleItemPosition();
                if (endCompletelyPosition == rvInvite.getAdapter().getItemCount() - 2 && !inviteLoadMore)
                {
                    inviteLoadMore = true;
                    //执行加载更多的方法，无论是用接口还是别的方式都行
                    Logger.t("loadMore").d(">>>>>>>>>>>>>>>>加载更多");
                    if (inviteDialogListener != null)
                        inviteDialogListener.loadMoreInviteOrInvited(true,liveGameInviteAdapter.getItemCount() + "");
                }
            }
        });

        LinearLayoutManager LayoutManager = new LinearLayoutManager(mAct);
        LayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvInvited.setLayoutManager(LayoutManager);
        rvInvited.addOnScrollListener(new RecyclerView.OnScrollListener()
        {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                int endCompletelyPosition = ((LinearLayoutManager) rvInvited.getLayoutManager()).
                        findLastCompletelyVisibleItemPosition();
                if (endCompletelyPosition == rvInvited.getAdapter().getItemCount() - 2 && !invitedLoadMore)
                {
                    invitedLoadMore = true;
                    //执行加载更多的方法，无论是用接口还是别的方式都行
                    Logger.t("loadMore").d(">>>>>>>>>>>>>>>>加载更多");
                    if (inviteDialogListener != null)
                        inviteDialogListener.loadMoreInviteOrInvited(false,liveGameInviteAdapter.getItemCount() + "");
                }
            }
        });
        imgSwitch1.setPadding(CommonUtils.dp2px(mAct, isAnchor ? 15 : 0), CommonUtils.dp2px(mAct, isAnchor ? 15 : 0),
                CommonUtils.dp2px(mAct, isAnchor ? 15 : 0), CommonUtils.dp2px(mAct, isAnchor ? 15 : 0));
        imgSwitch1.setImageResource(isAnchor ? R.drawable.text_fans_on : R.drawable.text_live_on);

        if (isShowStart)
        {
            String currentNum = admissionFee + "脸蛋/次  当前" + num + "/4人";
            SpannableString spannableString = new SpannableString(currentNum);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mAct, R.color.C0311)), 0, admissionFee.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mAct, R.color.C0311)), currentNum.length() - 4, currentNum.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvCurrentNum.setText(spannableString);
            SpannableString string = new SpannableString("奖池累计:" + total + "脸蛋");
            string.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mAct, R.color.C0311)), 5, total.length() + 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvTotalFaceEgg.setText(string);
            tvSurplusFaceEgg.setText(String.format("剩余脸蛋:%s", faceEgg));
        }
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        gameInvitedList = new ArrayList<>();
        liveGameInvitedAdapter = new LiveGameInvitedAdapter(mAct, gameInvitedList, anchorUId);
        rvInvited.setAdapter(liveGameInvitedAdapter);
        liveGameInvitedAdapter.setInvitedItemClickListener(new LiveGameInvitedAdapter.InvitedItemClickListener()
        {
            @Override
            public void rejectInvite(int position, GameInviteBean gameInviteBean)
            {
                if (inviteDialogListener != null)
                    inviteDialogListener.rejectInvite(position, gameInviteBean);
            }

            @Override
            public void acceptInvite(GameInviteBean gameInviteBean)
            {
                if (inviteDialogListener != null)
                    inviteDialogListener.acceptInvite(gameInviteBean);
            }
        });
        gameInviteList = new ArrayList<>();
        liveGameInviteAdapter = new LiveGameInviteAdapter(mAct, gameInviteList, anchorUId);
        liveGameInviteAdapter.setGameInviteClickListener(hasSelect ->
        {
            if (hasSelect)
            {
                flSendInvite.setBackgroundResource(R.drawable.btn_invite_main);
                imgSendInvite.setImageResource(R.drawable.text_send_invite);
            } else
            {
                flSendInvite.setBackgroundResource(R.drawable.btn_main_off);
                imgSendInvite.setImageResource(R.drawable.text_send_invitation_off);
            }
        });
        rvInvite.setAdapter(liveGameInviteAdapter);
        rvInvite.setEmptyView(imgEmpty);
        rvInvited.setEmptyView(imgEmpty);
        if (currentPosition == 1)
            switchTo(currentPosition);
    }

    /**
     * 刷新邀请列表
     *
     * @param list
     * @param type 刷新或者上拉加载
     * @param selectPosition 列表需要刷新为已邀请的item position
     * @param noShowId
     */
    public void notifyInviteList(List<GameInviteBean> list,String type,List<Integer> selectPosition,String noShowId)
    {
        if (TextUtils.isEmpty(noShowId)){
            if (selectPosition != null && selectPosition.size() > 0)
            {
                for (int position : selectPosition)
                {
                    if (gameInviteList != null && position < gameInviteList.size())
                        gameInviteList.get(position).setStatus("1");
                }
                liveGameInviteAdapter.notifyDataSetChanged();
                imgEmpty.setVisibility(View.GONE);
            }else {
                if (list == null)
                    return;
                if (TextUtils.isEmpty(type) || "refresh".equals(type))
                    gameInviteList.clear();
                gameInviteList.addAll(list);
                if (gameInviteList.size() == 0)
                    rlSendInvite.setVisibility(View.GONE);
                else if (currentPosition == 0)
                    rlSendInvite.setVisibility(View.VISIBLE);
                liveGameInviteAdapter.notifyDataSetChanged();
            }
        }else {
            GameInviteBean gameInviteBean = new GameInviteBean();
            gameInviteBean.setId(noShowId);
            int position = gameInviteList.indexOf(gameInviteBean);
            if (position >= 0 && position < gameInviteList.size()){
                gameInviteList.get(position).setStatus("0");
                liveGameInviteAdapter.notifyDataSetChanged();
            }
        }

        inviteLoadMore = false;
    }

    /**
     * 刷新被邀请列表
     *
     * @param list
     */
    public void notifyInvitedList(List<GameInviteBean> list,String type)
    {
        if (list == null)
            return;
        if (TextUtils.isEmpty(type) || "refresh".equals(type))
            gameInvitedList.clear();
        gameInvitedList.addAll(list);
        liveGameInvitedAdapter.notifyDataSetChanged();
    }

    /**
     * 更改状态为已拒绝
     */
    public void deleteInvitedItem(int position)
    {
        if (position < 0)
            return;
        if (gameInvitedList != null && position < gameInvitedList.size())
            gameInvitedList.get(position).setStatus("1");
        liveGameInvitedAdapter.notifyDataSetChanged();
    }


    /**
     * 显示开始游戏界面
     *
     */
    public void showStart(StartGameBean startGameBean, FragmentActivity mAct, String tag,boolean isStartGame)
    {
        String admissionFee = startGameBean == null?"20":startGameBean.getAdmissionFee();//入场费
        String total = startGameBean == null?"0":startGameBean.getTotal();//   奖池累计
        String faceEgg = startGameBean == null?"0":startGameBean.getFaceEgg();//   脸蛋余额
        String sponsor = startGameBean == null?"0":startGameBean.getSponsor();//
        int left = 0;
        try
        {
            if (startGameBean != null)
                left = Integer.valueOf(startGameBean.getLeft());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        String num = startGameBean == null?"0":left > 0?startGameBean.getNum():startGameBean.getAgree();//  参与人数
        if (getDialog() != null)
        {
            getDialog().setCancelable(false);
            getDialog().setCanceledOnTouchOutside(false);
        }
        if (TextUtils.isEmpty(admissionFee) || TextUtils.isEmpty(total) || TextUtils.isEmpty(num) || TextUtils.isEmpty(faceEgg))
            return;
        Logger.t("showDialog").d("isShowing" + isShowing+ "isAdded" + isAdded() + "isRemoving" +isRemoving() );
        this.admissionFee = admissionFee;
        this.total = total;
        this.faceEgg = faceEgg;
        this.num = num;
        if (!isShowing && !isAdded() && !isRemoving())
        {
            try
            {
                isShowStart = true;
                show(mAct.getSupportFragmentManager(), tag);
            } catch (Exception e)
            {
                e.printStackTrace();
                Logger.t(TAG).d(e.getMessage());
            }
        }
        if (!isShowRule)
        {
            llInvite.setVisibility(View.GONE);
            llSwitch.setVisibility(View.GONE);
            rlStart.setVisibility(View.VISIBLE);
        }
        String s1 = left > 0?"当前有":"已有";
        String s2 = left > 0?"人准备中\n": "1".equals(sponsor)?"人接受游戏邀请\n":"人参与游戏\n";
        String s3 = "脸蛋/次";
        String currentNum =  s1 + num + s2 + admissionFee + s3;
        SpannableString spannableString = new SpannableString(currentNum);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mAct, R.color.C0314)), s1.length(), (s1 + num).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mAct, R.color.C0314)),
                currentNum.length() - admissionFee.length() -4, currentNum.length() -4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCurrentNum.setText(spannableString);
        SpannableString string = new SpannableString("奖池累计:" + total + "脸蛋");
        string.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mAct, R.color.C0314)), 5, total.length() + 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTotalFaceEgg.setText(string);
        tvSurplusFaceEgg.setText(String.format("剩余脸蛋:%s", faceEgg));
        if (!hasStartCountDown && left > 0 && isStartGame){
            hasStartCountDown = true;
            int countTime = left;
            Observable.interval(0, 1, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Function<Long, Integer>()
                    {
                        @Override
                        public Integer apply(Long aLong) throws Exception
                        {
                            return countTime - aLong.intValue();
                        }

                    })
                    .take(countTime + 1)
                    .doOnSubscribe(new Consumer<Disposable>()
                    {
                        @Override
                        public void accept(Disposable disposable) throws Exception
                        {
                            Logger.t("countdown").d("计时开始");
                        }
                    }).subscribe(new Observer<Integer>()
            {
                @Override
                public void onSubscribe(Disposable d)
                {
                    timeDis = d;
                }

                @Override
                public void onNext(Integer integer)
                {
                    tvTime.setVisibility(View.VISIBLE);
                    tvTime.setText("(" + integer + ")");
                    Logger.t("countdown").d("" + integer);
                }

                @Override
                public void onError(Throwable e)
                {

                }

                @Override
                public void onComplete()
                {
                    Logger.t("countdown").d("结束");
                }
            });
        }
    }

    @OnClick({R.id.img_switch_1, R.id.img_switch_2, R.id.fl_send_invite, R.id.img_rule, R.id.img_close, R.id.img_start, R.id.img_cancel, R.id.img_rule_close})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.img_switch_1:
                switchTo(0);
                break;
            case R.id.img_switch_2:
                switchTo(1);
                break;
            case R.id.fl_send_invite:
                if (liveGameInviteAdapter.getSelectedList().size() == 0)
                    return;
                List<String> selectUIdList = new ArrayList<>();
                List<String> selectIdList = new ArrayList<>();
                List<Integer> selectPosition = new ArrayList<>();
                for (GameInviteBean gameInviteBean : liveGameInviteAdapter.getSelectedList())
                {
                    selectUIdList.add(gameInviteBean.getUId());
                    selectIdList.add("u" + gameInviteBean.getId());
                    selectPosition.add(gameInviteBean.getPosition());
                }
                Logger.t("sendInvite").d(new Gson().toJson(selectUIdList));
                if (inviteDialogListener != null)
                    inviteDialogListener.sendInvite(new Gson().toJson(selectUIdList), selectIdList,selectPosition);
                break;
            case R.id.img_rule:
                isShowRule = true;
                llInvite.setVisibility(View.GONE);
                llSwitch.setVisibility(View.GONE);
                rlStart.setVisibility(View.GONE);
                rlRule.setVisibility(View.VISIBLE);
                break;
            case R.id.img_rule_close:
                isShowRule = false;
                llInvite.setVisibility(View.GONE);
                llSwitch.setVisibility(View.GONE);
                rlStart.setVisibility(View.VISIBLE);
                rlRule.setVisibility(View.GONE);
                break;
            case R.id.img_start:
                if (!isStart)
                {
                    isStart = true;
                    if (inviteDialogListener != null)
                        inviteDialogListener.startGameClick();
                }
                break;
            case R.id.img_close:
                new CustomAlertDialog(mAct)
                        .builder()
                        .setMsg("您已参与了游戏,确定要离开吗?")
                        .setTitle("提示")
                        .setPositiveButton("确定", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                dismissAllowingStateLoss();
                                if (inviteDialogListener != null)
                                    inviteDialogListener.exitGame();
                            }
                        }).setNegativeButton(null, null).show();

                break;
            case R.id.img_cancel:
                if ("edit".equals(imgCancel.getTag()))
                {
                    imgCancel.setImageResource(R.drawable.text_cancel);
                    liveGameInviteAdapter.setShowSelect(true);
                    liveGameInviteAdapter.notifyDataSetChanged();
                    imgCancel.setTag("cancel");
                } else
                {
                    imgCancel.setImageResource(R.drawable.text_edit);
                    liveGameInviteAdapter.initGameInvite();
                    flSendInvite.setBackgroundResource(R.drawable.btn_main_off);
                    imgSendInvite.setImageResource(R.drawable.text_send_invitation_off);
                    imgCancel.setTag("edit");
                }
                break;
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog)
    {
        if (disposable != null)
            disposable.dispose();
        if (timeDis != null)
            timeDis.dispose();
        heartStart = false;
        isShowing = false;
        isStart = false;
        isShowStart = false;
        hasStartCountDown = false;
        super.onDismiss(dialog);
    }

    /**
     * 开始游戏心跳
     */
    public void startGameHeart()
    {

        final int countTime = 30;
        int interval = 3;
        Logger.t("liveGame").d("heartStart>>>>" + heartStart);
        if (!heartStart)
        {
            heartStart = true;
            try
            {
                if (disposable != null)
                    disposable.dispose();
                disposable = Observable.interval(0, interval, TimeUnit.SECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Function<Long, Integer>()
                        {
                            @Override
                            public Integer apply(Long aLong) throws Exception
                            {
                                return countTime - aLong.intValue() * interval;
                            }

                        })
                        .take(countTime + interval)
                        .doOnSubscribe(new Consumer<Disposable>()
                        {
                            @Override
                            public void accept(Disposable disposable) throws Exception
                            {
                                Logger.t("liveGame").d("心跳开始");
                            }
                        }).subscribe(new Consumer<Integer>()
                        {
                            @Override
                            public void accept(Integer integer) throws Exception
                            {
                                Logger.t("liveGame").d("" + integer);
                                if (inviteDialogListener != null)
                                    inviteDialogListener.refreshStartGame();
                            }
                        });
            } catch (Exception e)
            {
                e.printStackTrace();
                Logger.t("liveGame").d(">>>>" + e.getMessage());
            }
        }

    }

    public void stopGameHeart(){
        if (disposable != null)
            disposable.dispose();
    }

    private void switchTo(int position)
    {
        imgEmpty.setVisibility(View.GONE);
        switch (position)
        {
            case 0:
//                if (inviteDialogListener != null)
//                    inviteDialogListener.inviteClicked();
                currentPosition = 0;
                rvInvite.setVisibility(View.VISIBLE);
                rlSendInvite.setVisibility(View.VISIBLE);
                rvInvited.setVisibility(View.GONE);
                imgSwitch1.setPadding(CommonUtils.dp2px(mAct, isAnchor ? 15 : 0), CommonUtils.dp2px(mAct, isAnchor ? 15 : 0),
                        CommonUtils.dp2px(mAct, isAnchor ? 15 : 0), CommonUtils.dp2px(mAct, isAnchor ? 15 : 0));
                imgSwitch1.setImageResource(isAnchor ? R.drawable.text_fans_on : R.drawable.text_live_on);
                imgSwitch1.setBackgroundResource(R.drawable.btn_label_on);
                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) imgSwitch1.getLayoutParams();
                params1.height = CommonUtils.dp2px(mAct,40);
                imgSwitch1.setLayoutParams(params1);
                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) imgSwitch2.getLayoutParams();
                params2.height = CommonUtils.dp2px(mAct,38);
                imgSwitch2.setLayoutParams(params2);
                imgSwitch2.setPadding(CommonUtils.dp2px(mAct, 22), CommonUtils.dp2px(mAct, 12),
                        CommonUtils.dp2px(mAct, 22), CommonUtils.dp2px(mAct, 13));
                imgSwitch2.setImageResource(R.drawable.text_invite_off);
                imgSwitch2.setBackgroundResource(R.drawable.btn_label_off);
                break;
            case 1:
                if (inviteDialogListener != null)
                    inviteDialogListener.invitedClicked();
                currentPosition = 1;
                rvInvite.setVisibility(View.GONE);
                rlSendInvite.setVisibility(View.GONE);
                rvInvited.setVisibility(View.VISIBLE);
                imgSwitch2.setPadding(CommonUtils.dp2px(mAct,4), CommonUtils.dp2px(mAct,4), CommonUtils.dp2px(mAct,3), CommonUtils.dp2px(mAct,2));
                imgSwitch1.setPadding(CommonUtils.dp2px(mAct, isAnchor ? 34 : 22), CommonUtils.dp2px(mAct, isAnchor ? 34 : 10),
                        CommonUtils.dp2px(mAct, isAnchor ? 34 : 22), CommonUtils.dp2px(mAct, isAnchor ? 34 : 12));
                LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) imgSwitch1.getLayoutParams();
                layoutParams1.height = CommonUtils.dp2px(mAct,38);
                imgSwitch1.setLayoutParams(layoutParams1);
                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) imgSwitch2.getLayoutParams();
                layoutParams2.height = CommonUtils.dp2px(mAct,40);
                imgSwitch2.setLayoutParams(layoutParams2);
                imgSwitch1.setImageResource(isAnchor ? R.drawable.text_fans_off : R.drawable.text_live_off);
                imgSwitch1.setBackgroundResource(R.drawable.btn_label_off);
                imgSwitch2.setImageResource(R.drawable.text_invite_on);
                imgSwitch2.setBackgroundResource(R.drawable.btn_label_on);
                break;
        }
    }

    /**
     * 显示dialog
     *
     * @param manager
     * @param tag
     * @param position 默认显示位置
     */
    public void show(FragmentManager manager, String tag, int position)
    {
        isShowing = true;
        super.show(manager, tag);
        currentPosition = position;
    }

    public void refreshStartBtn(boolean joinSuccess)
    {
        isStart = joinSuccess;
        flStart.setBackgroundResource(joinSuccess ? R.drawable.btn_main_off : R.drawable.btn_invite_main);
        imgStart.setImageResource(joinSuccess ? R.drawable.text_start_off : R.drawable.text_start);
    }

    public interface InviteDialogListener
    {

        void inviteClicked();

        void invitedClicked();

        void sendInvite(String list, List<String> selectTxIdList,List<Integer> selectPosition);

        void rejectInvite(int position, GameInviteBean gameInviteBean);

        void acceptInvite(GameInviteBean gameInviteBean);

        void refreshStartGame();

        void startGameClick();

        void exitGame();

        void loadMoreInviteOrInvited(boolean isInvite,String start);
    }


//    /**
//     * 直播间可邀请用户
//     * @param gameId 游戏id
//     * @param roomId 直播间id
//     * @param start 起始
//     * @param num 条数
//     */
//    private void getCanInviteList(String gameId, String roomId, String start, String num)
//    {
//        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mAct);
//        reqMap.put(ConstCodeTable.roomId, roomId);
//        reqMap.put(ConstCodeTable.gameId, gameId);
//        reqMap.put(ConstCodeTable.startIdx, start);
//        reqMap.put(ConstCodeTable.num, num);
//        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
//        {
//            @Override
//            public void onHandledError(ApiException apiE)
//            {
//                super.onHandledError(apiE);
//            }
//
//            @Override
//            public void onHandledNetError(Throwable throwable)
//            {
//                super.onHandledNetError(throwable);
//            }
//
//            @Override
//            public void onNext(String response)
//            {
//                super.onNext(response);
//            }
//        }, NetInterfaceConstant.SunMoonStarC_canInviteList, reqMap);
//    }
//
//    /**
//     * 邀请列表
//     * @param gameId 游戏id
//     * @param roomId 直播间id
//     * @param start 起始
//     * @param num 条数
//     */
//    private void getGameInviters(String gameId, String roomId, String start, String num)
//    {
//        Map<String, String> reqMap = NetHelper.getCommonPartOfParam(mAct);
//        reqMap.put(ConstCodeTable.roomId, roomId);
//        reqMap.put(ConstCodeTable.gameId, gameId);
//        reqMap.put(ConstCodeTable.startIdx, start);
//        reqMap.put(ConstCodeTable.num, num);
//        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
//        {
//            @Override
//            public void onHandledError(ApiException apiE)
//            {
//                super.onHandledError(apiE);
//            }
//
//            @Override
//            public void onHandledNetError(Throwable throwable)
//            {
//                super.onHandledNetError(throwable);
//            }
//
//            @Override
//            public void onNext(String response)
//            {
//                super.onNext(response);
//            }
//        }, NetInterfaceConstant.SunMoonStarC_inviters, reqMap);
//    }

}
