package com.lqtemple.android.lqbookreader.activity;

/**
 * @author Administrator
 *         Created by Administrator on 2017/3/17 0017.
 * @version $Rev$
 * @updateDes ${TODO}
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.activity.adapter.BaseRecyclerAdapter;
import com.lqtemple.android.lqbookreader.activity.adapter.DividerItemDecoration;
import com.lqtemple.android.lqbookreader.annotation.InjectUtils;
import com.lqtemple.android.lqbookreader.annotation.InjectView;
import com.lqtemple.android.lqbookreader.model.VoiceModel;
import com.lqtemple.android.lqbookreader.util.LqBookConst;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.download.DownloadManager;
import com.lzy.okserver.download.DownloadService;

import java.util.ArrayList;


public class DownLoadActivity extends BaseActivity {

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.openManager)
    Button openManager;

    private ArrayList<VoiceModel> mVoiceModels;
    private DownloadManager downloadManager;
    private MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        InjectUtils.bind(this);

        initData();
        downloadManager = DownloadService.getDownloadManager();
        //设置下载目录
        downloadManager.setTargetFolder(Environment.getExternalStorageDirectory().getAbsolutePath() + "/LongQuan/ceji/");
        //设置下载任务个数
        downloadManager.getThreadPool().setCorePoolSize(LqBookConst.DOWN_TASK_NUM);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new MainAdapter(this);
        recyclerView.setAdapter(adapter);
        openManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DownloadManagerActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private class MainAdapter extends BaseRecyclerAdapter<VoiceModel, ViewHolder> {

        public MainAdapter(Context context) {
            super(context, mVoiceModels);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View convertView=null ;
            ViewHolder hold;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_download_details, parent, false);
                hold = new ViewHolder(convertView);
                convertView.setTag(hold);
            } else {
                hold = (ViewHolder) convertView.getTag();
            }

            hold.name = (TextView) convertView.findViewById(R.id.name);
            hold.download = (Button) convertView.findViewById(R.id.download);
            hold.icon = (ImageView) convertView.findViewById(R.id.icon);
            //            View view = inflater.inflate(R.layout.activity_download_details, parent, false);

            return hold;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            VoiceModel apkModel = mDatas.get(position);
            holder.bind(apkModel);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        ImageView icon;
        Button download;

        private VoiceModel mVoiceModel;

        public ViewHolder(View itemView) {
            super(itemView);
            InjectUtils.bind(itemView);

        }

        public void bind(VoiceModel voiceModel) {
            this.mVoiceModel = voiceModel;
            if (downloadManager.getDownloadInfo(voiceModel.getUrl()) != null) {
                download.setText(R.string.enqueue);
                download.setEnabled(false);
            } else {
                download.setText(R.string.start_down);
                download.setEnabled(true);
            }
            name.setText(voiceModel.getName());
            Glide.with(getApplicationContext()).load(voiceModel.getIconUrl()).error(R.mipmap.ic_launcher).into(icon);
            download.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.download) {
                if (downloadManager.getDownloadInfo(mVoiceModel.getUrl()) != null) {
                    Toast.makeText(getApplicationContext(), R.string.task_enqueue, Toast.LENGTH_SHORT).show();
                } else {
                    GetRequest request = OkGo.get(mVoiceModel.getUrl())//
                            .headers("headerKey1", "headerValue1")//
                            .headers("headerKey2", "headerValue2")//
                            .params("paramKey1", "paramValue1")//
                            .params("paramKey2", "paramValue2");
                    downloadManager.addTask(mVoiceModel.getUrl(), mVoiceModel, request, null);
                    download.setText(R.string.enqueue);
                    download.setEnabled(false);
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), DesActivity.class);
                intent.putExtra("apk", mVoiceModel);
                startActivity(intent);
            }
        }
    }

    private void initData() {


        mVoiceModels = new ArrayList<>();
        VoiceModel apkInfo0 = new VoiceModel();
        apkInfo0.setName(getString(R.string.first_chapter));
        apkInfo0.setIconUrl("http://img4.imgtn.bdimg.com/it/u=1282952687,2433742667&fm=23&gp=0.jpg");
        apkInfo0.setUrl("http://www.theqi.com/buddhism/GL1/audio/001A.MP3");
        mVoiceModels.add(apkInfo0);
        VoiceModel apkInfo1 = new VoiceModel();
        apkInfo1.setName(getString(R.string.two_chapter));
        apkInfo1.setIconUrl("http://img4.imgtn.bdimg.com/it/u=1282952687,2433742667&fm=23&gp=0.jpg");
        apkInfo1.setUrl("http://www.theqi.com/buddhism/GL1/audio/001B.MP3");
        mVoiceModels.add(apkInfo1);
        VoiceModel apkInfo2 = new VoiceModel();
        apkInfo2.setName(getString(R.string.thrid_chapter));
        apkInfo2.setIconUrl("http://img4.imgtn.bdimg.com/it/u=1282952687,2433742667&fm=23&gp=0.jpg");
        apkInfo2.setUrl("http://www.theqi.com/buddhism/GL1/audio/002A.MP3");
        mVoiceModels.add(apkInfo2);
        VoiceModel apkInfo3 = new VoiceModel();
        apkInfo3.setName(getString(R.string.thrid_chapter));
        apkInfo3.setIconUrl("http://img4.imgtn.bdimg.com/it/u=1282952687,2433742667&fm=23&gp=0.jpg");
        apkInfo3.setUrl("http://www.theqi.com/buddhism/GL1/audio/002B.MP3");
        mVoiceModels.add(apkInfo3);
    }


}