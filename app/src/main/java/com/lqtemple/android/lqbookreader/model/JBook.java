package com.lqtemple.android.lqbookreader.model;

/**
 * Created by sundxing on 16/12/4.
 */

public class JBook {

    /**
     * cover :
     * desc : 学诚大和尚的生平事迹
     * id : 1
     * language : zh-CN
     * name : 侧记
     * version : 1
     */

    private String cover;
    private String desc;
    private int id;
    private String language;
    private String name;
    private int version;


    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
