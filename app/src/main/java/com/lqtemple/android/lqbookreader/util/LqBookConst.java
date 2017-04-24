package com.lqtemple.android.lqbookreader.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;

import java.util.List;

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

    public static  String  MEDIA_SERVICE = "com.lqtemple.android.lqbookreader.service.MediaService";



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

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }


}
