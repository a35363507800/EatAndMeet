package com.echoesnet.eatandmeet.activities.liveplay.Presenter.Interfaces;

import com.echoesnet.eatandmeet.models.datamodel.ExitRoomType;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author       ben
 * @modifier
 * @createDate   2017/4/19
 * @version      1.0
 * @description  直播流程接口，presenter实现
 */
public interface ILiveFlowPre
{
     /**
      * Create room.
      */
     void createRoom();

     /**
      * Join room.
      *
      * @param roomId the room id
      */
     void joinRoom(String roomId);

     /**
      * Close room.
      */
     void closeRoom(ExitRoomType closeType);

     /**
      * Quit room.
      */
     void quitRoom(ExitRoomType quitType);
}
