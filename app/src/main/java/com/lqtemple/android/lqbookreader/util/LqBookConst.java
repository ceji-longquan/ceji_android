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

    //开始播放
    public static  String  START_PLAY = "0";
    //开始播放暂停
    public static  String  START_PAUSE = "1";
    //恢复播放
    public static  String  RE_START_PLAY = "2";

}
