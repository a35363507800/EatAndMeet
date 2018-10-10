package com.echoesnet.eatandmeet.http4retrofit2.down;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liuyang on 2016/12/20.
 */

public class DownloadParcelable implements Parcelable {

    private int progress;
    private long currentFileSize;
    private long totalFileSize;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getCurrentFileSize() {
        return currentFileSize;
    }

    public void setCurrentFileSize(long currentFileSize) {
        this.currentFileSize = currentFileSize;
    }

    public long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.progress);
        dest.writeLong(this.currentFileSize);
        dest.writeLong(this.totalFileSize);
    }

    public DownloadParcelable() {
    }

    protected DownloadParcelable(Parcel in) {
        this.progress = in.readInt();
        this.currentFileSize = in.readLong();
        this.totalFileSize = in.readLong();
    }

    public static final Parcelable.Creator<DownloadParcelable> CREATOR = new Parcelable.Creator<DownloadParcelable>() {
        @Override
        public DownloadParcelable createFromParcel(Parcel source) {
            return new DownloadParcelable(source);
        }

        @Override
        public DownloadParcelable[] newArray(int size) {
            return new DownloadParcelable[size];
        }
    };
}

