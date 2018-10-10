package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Created by ben on 2016/12/28.
 */

public interface IRegisterPre
{
    /**
     * 验证验证码
     *
     * @param mobile 电话
     * @param code   验证码
     * @param type   类型，主要用于不同地方发送验证码，例如登录，注册等
     */
    void validSecurityCode(String mobile, String code, String type, String tokenId);

    void register(String mobile, String password, String tokenId);
}
