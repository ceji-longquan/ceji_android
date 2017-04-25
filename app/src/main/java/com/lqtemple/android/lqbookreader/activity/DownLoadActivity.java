package com.lqtemple.android.lqbookreader.activity;

/**
 * @author Administrator
 *         Created by Administrator on 2017/3/17 0017.
 * @version $Rev$
 * @updateDes ${TODO}
 */

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private final String TAG = "DownLoadActivity";
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.openManager)
    Button openManager;

    private ArrayList<VoiceModel> mVoiceModels;
    private DownloadManager downloadManager;
    private MainAdapter adapter;
//    private ScanfFileTask mScanfFileTask;
//    private List<File> mFileList;
    private ArrayList<VoiceModel> mAlreadDownList;
/*
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case DownLoadValue.SCANF:
                    Log.i(TAG, "mVoiceModels= " + mVoiceModels.size());

                    adapter.notifyDataSetChanged();
//                    adapter.updateItems(mVoiceModels);
                    Log.i(TAG, "mVoiceModels updata suseecess= " );

                    break;
            }

        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        InjectUtils.bind(this);
//        mFileList = new ArrayList<>();
        mAlreadDownList = new ArrayList<>();

//        new ScanfFileTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);// 不用等待


        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            }
        }

        initData();
        downloadManager = DownloadService.getDownloadManager();
        //设置下载目录
        downloadManager.setTargetFolder(LqBookConst.DOWN_PATH);
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
                Intent intent = new Intent(getApplicationContext(), DownloadManagerActivity.class);
                intent.putExtra("mAlreadDownList",mAlreadDownList);
                startActivity(intent);
            }
        });
    }


    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {

        new AlertDialog.Builder(DownLoadActivity.this)
                .setTitle(R.string.storage_permission)
                .setMessage(R.string.storage_permission_messahe)
                .setPositiveButton(R.string.now_open, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 开始提交请求权限
                        ActivityCompat.requestPermissions(DownLoadActivity.this, permissions, 321);

                    }
                })
                .setNegativeButton(R.string.now_cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
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
            View convertView = null;
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
                Log.i(TAG, "bind.isExit()= " +mVoiceModel.isExit());

            } else {
                download.setText(R.string.start_down);
                download.setEnabled(true);
            }
            if(mVoiceModel.isExit()){
                download.setText(R.string.alreadydown);
                download.setEnabled(false);
            }
            Log.i(TAG, "mVoiceModel.isExit()= " +mVoiceModel.isExit());

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
        apkInfo0.setName("001A.MP3");
        apkInfo0.setIconUrl("http://img4.imgtn.bdimg.com/it/u=1282952687,2433742667&fm=23&gp=0.jpg");
        apkInfo0.setUrl("http://www.theqi.com/buddhism/GL1/audio/001A.MP3");
        mVoiceModels.add(apkInfo0);
        VoiceModel apkInfo1 = new VoiceModel();
        apkInfo1.setName("001B.MP3");
        apkInfo1.setIconUrl("http://img4.imgtn.bdimg.com/it/u=1282952687,2433742667&fm=23&gp=0.jpg");
        apkInfo1.setUrl("http://www.theqi.com/buddhism/GL1/audio/001B.MP3");
        mVoiceModels.add(apkInfo1);
        VoiceModel apkInfo2 = new VoiceModel();
        apkInfo2.setName("002A.MP3");
        apkInfo2.setIconUrl("http://img4.imgtn.bdimg.com/it/u=1282952687,2433742667&fm=23&gp=0.jpg");
        apkInfo2.setUrl("http://www.theqi.com/buddhism/GL1/audio/002A.MP3");
        mVoiceModels.add(apkInfo2);
        VoiceModel apkInfo3 = new VoiceModel();
        apkInfo3.setName(getString(R.string.thrid_chapter));
        apkInfo3.setIconUrl("http://img4.imgtn.bdimg.com/it/u=1282952687,2433742667&fm=23&gp=0.jpg");
        apkInfo3.setUrl("http://www.theqi.com/buddhism/GL1/audio/002B.MP3");
        mVoiceModels.add(apkInfo3);
    }


/*    private class ScanfFileTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            ScanfLocalFile.getFiles(LqBookConst.DOWN_PATH, mFileList);

            for (File file:mFileList ) {
                Log.i(TAG, "file= " + file.getName());
                for (int i=0 ;i<mVoiceModels.size();i++) {
                    if(file.getName().equals(mVoiceModels.get(i).getName())){
                        mVoiceModels.get(i).setExit(true);
                        mAlreadDownList.add(mVoiceModels.get(i));
                        Log.i(TAG, "geti= " + mVoiceModels.get(i).getName());

                    }

                }

            }
            Message msg = Message.obtain();
            msg.what = DownLoadValue.SCANF;
            handler.sendMessage(msg);
            return null;
        }
    }*/

}
