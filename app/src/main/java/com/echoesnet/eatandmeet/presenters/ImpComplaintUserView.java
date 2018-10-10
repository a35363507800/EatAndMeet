package com.echoesnet.eatandmeet.presenters;


import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IComplaintUserView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/29.
 */

public class ImpComplaintUserView extends BasePresenter<IComplaintUserView>
{
    private final String TAG = ImpComplaintUserView.class.getSimpleName();

    /**
     * 邀请人投诉
     *
     * @param luId     被投诉人ID
     * @param type     投诉原因(投诉内容-文档标注)
     * @param fdk      吐槽的内容
     * @param streamId 约会ID
     */
    public void commitMessage(String luId, String type, String fdk, String streamId)
    {
//        接口描述	邀请人投诉
//        接口名称	ReceiveC/complaintReceive
//        请求参数	参数名	    类型	        参数描述
//                  luId	    String	    被投诉人ID
//                  type	    String	    投诉内容（拼接成字符串）
//                  fdk	        String	    我有想吐槽的内容
//                  streamId    String      约会ID
//                  token	    String	    用户登录凭证
//                  uId	        String	    用户Id
//                  deviceId	String	    设备号
//        响应参数	参数名称	    类型	    参数描述
//                  code	    String	请查看附录1
//                  status	    Short	调用接口是否成功->0:成功,1:失败
//        请求方式	POST
//        备注	传输方式：通过Http协议传输数据， (V0.1 仅支持json)，作为移动APP和WEB端调用。

        if (getView() == null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        reqParamMap.put(ConstCodeTable.type, type);
        reqParamMap.put(ConstCodeTable.fdk, fdk);
        reqParamMap.put(ConstCodeTable.streamId, streamId);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerErrorCallback(NetInterfaceConstant.ReceiveC_complaintReceive,apiE.getErrorCode(),apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                getView().commitMessageCallback(response);
            }
        },NetInterfaceConstant.ReceiveC_complaintReceive,reqParamMap);

    }
}
