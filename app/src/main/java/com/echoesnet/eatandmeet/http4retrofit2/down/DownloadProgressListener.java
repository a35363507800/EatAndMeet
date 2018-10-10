package com.echoesnet.eatandmeet.http4retrofit2.down;

/**
 * Created by liuyang on 2016/12/20.
 */

public interface DownloadProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}