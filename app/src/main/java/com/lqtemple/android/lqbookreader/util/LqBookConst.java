package com.lqtemple.android.lqbookreader.util;

import android.os.Environment;

/**
 * @author Administrator
 *         Created by Administrator on 2017/3/17 0017.
 * @version $Rev$
 * @updateDes ${TODO}
 */

public class LqBookConst {

    public static String DOWN_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LongQuan/ceji/";
    public static  int DOWN_TASK_NUM = 3;


    public static  String  START_PLAY_KEY = "startPlay";

    public static  int  DEFAULT_PROGRESS = -1;
    //开始播放
    public static  String  START_PLAY = "0";
    //开始播放暂停
    public static  String  START_PAUSE = "1";
    //恢复播放
    public static  String  RE_START_PLAY = "2";


    /**
     * 把进度条的数字转化为分钟
     * @param time
     * @return
     */
    public static String toTime(int time){
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }


}
