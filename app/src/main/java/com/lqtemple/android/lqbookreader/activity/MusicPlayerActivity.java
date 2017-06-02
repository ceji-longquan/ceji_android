package com.lqtemple.android.lqbookreader.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.activity.adapter.MySongListAdapter;
import com.lqtemple.android.lqbookreader.annotation.InjectUtils;
import com.lqtemple.android.lqbookreader.annotation.InjectView;
import com.lqtemple.android.lqbookreader.event.AgainPlayEvent;
import com.lqtemple.android.lqbookreader.event.MusicEvent;
import com.lqtemple.android.lqbookreader.event.UpdateListColorEvent;
import com.lqtemple.android.lqbookreader.util.LqBookConst;
import com.lqtemple.android.lqbookreader.util.MediaUtil;
import com.lqtemple.android.lqbookreader.util.PromptManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * Created by Administrator on 2017/3/30 0030.
 */

public class MusicPlayerActivity extends BasePlayActivity {
    private String TAG = MusicPlayerActivity.class.getSimpleName();
    @InjectView(R.id.musicListView)
    private ListView musicListView;
    private MySongListAdapter adapter;
    @InjectView(R.id.titleCenterbtn)
    private TextView titleCenterbtn;
    @InjectView(R.id.titleRightbtn)
    private TextView titleRightbtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        InjectUtils.bind(this);
        titleCenterbtn.setText(getText(R.string.music_play));

        //绑定事件接受
        EventBus.getDefault().register(this);
        setAdapter();

        titleRightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow) {
                    hidePlaybackControls();
                } else {
                    showQuickControl();
                }
            }
        });
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaUtil.CURRENTPOS = position;
                EventBus.getDefault().post(new MusicEvent(MediaUtil.getInstacen().getSongList().get(position)));
                adapter.notifyDataSetChanged();
                //点击播放音乐，不过需要判断一下当前是否有音乐在播放，需要关闭正在播放的

/*                Intent playIntent = new Intent(getApplicationContext(), FullScreenPlayerActivity.class);
                MusicMedia musicMedia = MediaUtil.getInstacen().getSongList().get(position);
                playIntent.putExtra("music", musicMedia);
                playIntent.putExtra("position", position);
                startActivity(playIntent);*/


            }
        });
        new InitDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);// 不用等待


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

    }

    /**
     * 音乐资源过多
     */
    class InitDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            PromptManager.showProgressDialog(MusicPlayerActivity.this);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // 加载多媒体信息
            MediaUtil.getInstacen().initMusics(MusicPlayerActivity.this);
            // SystemClock.sleep(100);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            PromptManager.closeProgressDialog();
            adapter.notifyDataSetChanged();

            if(LqBookConst.isServiceWork(getApplicationContext(),LqBookConst.MEDIA_SERVICE)){
                Log.i(TAG,"InitDataTask ====" + MediaUtil.CURRENTPOS);
                if (MediaUtil.getInstacen().getSongList().size() > 0) {
                    if (MediaUtil.CURRENTPOS >= 0 && MediaUtil.CURRENTPOS <= MediaUtil.getInstacen().getSongList().size()) {
                        EventBus.getDefault().post(new AgainPlayEvent(MediaUtil.getInstacen().getCurrent()));
                    }
                }
            }

        }
    }


    private void setAdapter() {
        adapter = new MySongListAdapter(getApplicationContext());
        musicListView.setAdapter(adapter);

    }


    @Subscribe
    public void onMessageEvent(UpdateListColorEvent event) {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销事件接受
        EventBus.getDefault().unregister(this);
    }


}
