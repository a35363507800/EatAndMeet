package com.echoesnet.eatandmeet.utils.serverdatacache.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kylewbanks on 2013-10-09.
 */
public class ServerData {

    @SerializedName("sign")
    private String _sign;

    @SerializedName("timestamp")
    private String _timestamp;

    @SerializedName("body")
    private String _body;

    public ServerData(String sign, String timestamp, String body) {
        this._sign = sign;
        this._timestamp = timestamp;
        this._body = body;
    }

    public ServerData() {
    }

    public String getSign() {
        return _sign;
    }

    public void setSign(String _sign) {
        this._sign = _sign;
    }

    public String getTimestamp() {
        return _timestamp;
    }

    public void setTimestamp(String _timestamp) {
        this._timestamp = _timestamp;
    }

    public String getBody() {
        return _body;
    }

    public void setBody(String _body) {
        this._body = _body;
    }
}
