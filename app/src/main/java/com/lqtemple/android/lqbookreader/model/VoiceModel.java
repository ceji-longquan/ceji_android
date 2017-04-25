package com.lqtemple.android.lqbookreader.model;

import java.io.Serializable;

public class VoiceModel implements Serializable {
    private static final long serialVersionUID = 2072893447591548402L;

    private String name;
    private String url;
    private String iconUrl;
    private boolean isExit= false;

    public VoiceModel() {
    }

    public VoiceModel(String name, String url, String iconUrl) {
        this.name = name;
        this.url = url;
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public boolean isExit() {
        return isExit;
    }

    public void setExit(boolean exit) {
        isExit = exit;
    }
}
