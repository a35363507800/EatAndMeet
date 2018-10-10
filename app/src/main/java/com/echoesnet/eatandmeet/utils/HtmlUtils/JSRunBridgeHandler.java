package com.echoesnet.eatandmeet.utils.HtmlUtils;

import com.github.lzyzsd.jsbridge.CallBackFunction;

/**
 * Created by liuyang on 2017/8/29.
 */

public interface JSRunBridgeHandler {
    void jsRunning(String key, String data, CallBackFunction function);
}
