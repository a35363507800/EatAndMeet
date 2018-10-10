package com.echoesnet.eatandmeet.presenters;

import android.text.TextUtils;

import com.echoesnet.eatandmeet.activities.TaskAct;
import com.echoesnet.eatandmeet.controllers.okhttpCallback.BaseCallback;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by an on 2017/2/28.
 */

public class ImpITaskActView extends BasePresenter<TaskAct>
{
    private final String TAG = ImpITaskActView.class.getSimpleName();

    /**
     * 更新任务红点
     */
    public void updateTaskOk()
    {
        if (getView() == null)
            return;
        Map<String, String> map = NetHelper.getCommonPartOfParam(getView());


        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    String task = jsonObject.getString("task");
                    String successes = jsonObject.getString("successes");
                    if (getView()!=null)
                        getView().updateTaskOkCallBack(task,successes);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, NetInterfaceConstant.TaskC_taskOk, map);

    }

}
