package com.echoesnet.eatandmeet.controllers.subscribers;

/**
 * Created by liukun on 16/3/10.
 */
public interface SubscriberOnErrorListener<T> {
    void onError(T t);
}
