package com.echoesnet.eatandmeet.models.bean;

import java.util.List;

/**
 * Created by an on 2017/3/29 0029.
 */

public class Liveplay4FindBean {
    List<HotAnchorBean> artAnchor;
    List<HotAnchorBean> dateAnchor;
    List<HotAnchorBean> nearbyAnchor;

    public List<HotAnchorBean> getArtAnchor() {
        return artAnchor;
    }

    public void setArtAnchor(List<HotAnchorBean> artAnchor) {
        this.artAnchor = artAnchor;
    }

    public List<HotAnchorBean> getDateAnchor() {
        return dateAnchor;
    }

    public void setDateAnchor(List<HotAnchorBean> dateAnchor) {
        this.dateAnchor = dateAnchor;
    }

    public List<HotAnchorBean> getNearbyAnchor() {
        return nearbyAnchor;
    }

    public void setNearbyAnchor(List<HotAnchorBean> nearbyAnchor) {
        this.nearbyAnchor = nearbyAnchor;
    }
}
