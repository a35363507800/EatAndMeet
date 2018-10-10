package com.echoesnet.eatandmeet.controllers.subscribers;

/**
 * Created by liukun on 16/3/10.
 */
public interface InterceptorOnErrorListener<T> {
    boolean onError(T t);
}
