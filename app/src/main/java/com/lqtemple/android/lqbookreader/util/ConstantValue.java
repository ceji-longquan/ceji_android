package com.lqtemple.android.lqbookreader.util;

public interface ConstantValue {
	/**
	 * 音乐扫描开始
	 */
	int STARTED = 0;
	/**
	 * 音乐扫描结束
	 */
	int FINISHED = 1;
	/**
	 * 播放完成
	 */
	int PLAY_END = 2;
	/**
	 * 播放
	 */
	int OPTION_PLAY = 50;
	/**
	 * 暂停
	 */
	int OPTION_PAUSE = 51;
	/**
	 * 继续
	 */
	int OPTION_CONTINUE = 52;
	/**
	 * 修改进度
	 */
	int OPTION_UPDATE_PROGESS = 53;

	/**
	 * 再次启动activity，不做操作
	 */
	int OPTION_DEFAULT = 54;

	/**
	 * 设置seekbar的最大值
	 */
//	int SEEKBAR_MAX = 100;
	/**
	 * 修改seekbar位置
	 */
	int SEEKBAR_CHANGE=101;
}
