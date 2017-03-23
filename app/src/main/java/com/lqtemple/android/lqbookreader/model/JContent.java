package com.lqtemple.android.lqbookreader.model;

import java.util.List;

/**
 * Created by sundxing on 16/12/4.
 */

public class JContent {

    /**
     * annotation : [{"code":1,"explain":"san bao","target":"三宝"},{"code":2,"explain":"fa shen hui ming","target":"法身慧命"}]
     * audioTimeFrame : 0
     * audioUrl :
     * imageUrl : []
     * index : 4-0
     * position : 2
     * text : 法师曾说：“我天天忙，如果背后没有三宝[1]加持的力量，肯定做不下去，面对种种人事，都要负责任。”法师是在用自己的生命延续佛法，延续弟子们的法身慧命[2]。
     * type : 1
     */

    private int audioTimeFrame;
    private String audioUrl;
    private String index;
    private int position;
    private String text;
    private int type;
    private List<AnnotationBean> annotation;
    private List<String> imageUrl;

    public int getAudioTimeFrame() {
        return audioTimeFrame;
    }

    public void setAudioTimeFrame(int audioTimeFrame) {
        this.audioTimeFrame = audioTimeFrame;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getText() {

        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public Type getTypeEnum() {
        return Type.values()[type];
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<AnnotationBean> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(List<AnnotationBean> annotation) {
        this.annotation = annotation;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }




    public static class AnnotationBean {
        /**
         * code : 1
         * explain : san bao
         * target : 三宝
         */

        private int code;
        private String explain;
        private String target;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getExplain() {
            return explain;
        }

        public void setExplain(String explain) {
            this.explain = explain;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }
    }
}
