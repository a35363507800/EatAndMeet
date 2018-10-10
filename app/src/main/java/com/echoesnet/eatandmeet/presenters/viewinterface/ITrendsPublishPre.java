package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.activities.TrendsPublishAct;
import com.echoesnet.eatandmeet.presenters.ImpITrendsPublishPre;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/18 0018
 * @description
 */
public interface ITrendsPublishPre
{

    /**
     * 发布动态
     * @param trendsPublish 发布动态类型 {@link ImpITrendsPublishPre.TrendsPublish}
     * @param imgs 图片路径
     * @param videoPath 视频路径
     * @param content 内容
     * @param posx 坐标
     * @param posy
     * @param location 位置描述
     */
   void startPublishTrends(TrendsPublishAct.TrendsPublish trendsPublish, List<String> imgs, String videoPath
        , String thumbnailPath, String content, double posx, double posy, String location, String showType);
}
