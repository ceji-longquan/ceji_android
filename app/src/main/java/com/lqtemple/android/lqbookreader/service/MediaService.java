package com.lqtemple.android.lqbookreader.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.activity.FullScreenPlayerActivity;
import com.lqtemple.android.lqbookreader.model.MusicMedia;
import com.lqtemple.android.lqbookreader.util.ConstantValue;
import com.lqtemple.android.lqbookreader.util.HandlerManager;
import com.lqtemple.android.lqbookreader.util.LqBookConst;
import com.lqtemple.android.lqbookreader.util.MediaUtil;
import com.lqtemple.android.lqbookreader.util.PromptManager;

public class MediaService extends Service implements OnCompletionListener, OnSeekCompleteListener, OnErrorListener {
	private  final String TAG = "MediaService";
	private static final int NOTIFICATION_ID = 1; // 如果id设置为0,会导致不能设置为前台service
	// mediaplayer
	private static MediaPlayer player;
	// private static ProgressThread thread;
	private static ProgressTask task;
	private String file;
	private int postion = 0;
	public static int default_postion = 0;
	public static boolean isPlaying = false;

	private MusicMedia mMusicMedia;


	// private Handler handler;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (player == null) {
			player = new MediaPlayer();
			player.setOnSeekCompleteListener(this);
			player.setOnCompletionListener(this);
			player.setOnErrorListener(this);
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		int option = intent.getIntExtra("option", LqBookConst.DEFAULT_PROGRESS);
		int progress = intent.getIntExtra("progress", LqBookConst.DEFAULT_PROGRESS);
		if (progress != LqBookConst.DEFAULT_PROGRESS) {
			this.postion = progress;
			// this.postion = progress * player.getDuration() / 100;
		}
		switch (option) {
			case ConstantValue.OPTION_PLAY:
				file = intent.getStringExtra("file");
				play(file);
				MediaUtil.PLAYSTATE = option;
				Log.i(TAG, "OPTION_PLAY=" + ConstantValue.OPTION_PLAY);

				break;
			case ConstantValue.OPTION_PAUSE:
				postion = player.getCurrentPosition();
				pause();
				MediaUtil.PLAYSTATE = option;
				Log.i(TAG, "OPTION_PAUSE=" + ConstantValue.OPTION_PAUSE);
				break;
			case ConstantValue.OPTION_CONTINUE:
				Log.i(TAG, "OPTION_CONTINUE=" + ConstantValue.OPTION_CONTINUE);

				playerToPosiztion(postion);
				// if (MediaUtil.PLAYSTATE == ConstantValue.OPTION_PLAY)
				if (file == "" || file == null) {
					file = intent.getStringExtra("file");
					play(file);
				} else {
					player.start();
					isPlaying = true;
				}
				MediaUtil.PLAYSTATE = option;

				break;
			case ConstantValue.OPTION_UPDATE_PROGESS:
				playerToPosiztion(postion);
				break;
			case ConstantValue.OPTION_DEFAULT:
				playerToPosiztion(default_postion);
				break;
		}
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		mMusicMedia = (MusicMedia)intent.getSerializableExtra("music");
		Log.i(TAG, "mMusicMedia=" + mMusicMedia);

		if(mMusicMedia!=null){
			//开启前台service
			Notification notification = null;
			if (Build.VERSION.SDK_INT < 16) {
				notification = new Notification.Builder(this)
						.setContentTitle(mMusicMedia.getTitle()).setContentText(mMusicMedia.getArtist())
						.setSmallIcon(R.drawable.musicfile).getNotification();
			} else {
				Notification.Builder builder = new Notification.Builder(this);
				Intent startIntent = new Intent(this, FullScreenPlayerActivity.class);
				startIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent contentIntent = PendingIntent.getActivity(this, 0,startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.setContentIntent(contentIntent);
				builder.setSmallIcon(R.drawable.musicfile);
				//        builder.setTicker("Foreground Service Start");
				builder.setContentTitle(mMusicMedia.getTitle());
				builder.setContentText(mMusicMedia.getArtist());
				notification = builder.build();
			}

			startForeground(NOTIFICATION_ID, notification);
		}


		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		stop();
		super.onDestroy();
	}

	private void play(String path) {

		if (player == null) {
			player = new MediaPlayer();
		}

		try {
			player.reset();
			player.setDataSource(path);
			player.prepare();
			player.start();
			isPlaying = true;
			/*
			 * Message msg = Message.obtain(); msg.what = ConstantValue.SEEKBAR_MAX; msg.arg1 = player.getDuration();
			 * 
			 * HandlerManager.getHandler().sendMessage(msg);
			 */
			/*
			 * if (thread == null) { thread = new ProgressThread(); thread.start(); }
			 */
			if (task == null) {
				task = new ProgressTask();
				task.execute();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void pause() {
		if (player != null && player.isPlaying()) {
			player.pause();
			isPlaying =false;
		}
	}

	private void stop() {
		if (player != null) {
			player.stop();
			player.release();
			isPlaying =false;
		}
	}

	private void playerToPosiztion(int posiztion) {

		if (posiztion > 0 && posiztion < player.getDuration()) {
			player.seekTo(posiztion);
		}
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		Log.i(TAG, "onProgressUpdate:arg0=" + arg0);
		HandlerManager.getHandler().sendEmptyMessage(ConstantValue.PLAY_END);
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {

		if (player.isPlaying()) {
			player.start();
			isPlaying = true;
		}
	}



	private class ProgressTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			while (true) {
//				Log.i(TAG, "isPlaying:" + player.isPlaying());
				SystemClock.sleep(1000);
				publishProgress();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			if (player.isPlaying()) {
				Message msg = Message.obtain();
				msg.what = ConstantValue.SEEKBAR_CHANGE;
				msg.arg1 = player.getCurrentPosition() + 1000;
				msg.arg2 = player.getDuration();
				default_postion = msg.arg1;
				HandlerManager.getHandler().sendMessage(msg);
				Log.i(TAG, "isPlaying：=" + isPlaying);
				Log.i(TAG, "onProgressUpdate:arg1=" + msg.arg1);
//				Log.i(TAG, "onProgressUpdate:arg2=" + msg.arg2);

			}
			super.onProgressUpdate(values);
		}

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		PromptManager.showToast(getApplicationContext(), "亲，音乐文件加载出错了");
		return false;
	}

}
