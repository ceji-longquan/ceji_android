package com.lqtemple.android.lqbookreader.activity;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.annotation.InjectUtils;
import com.lqtemple.android.lqbookreader.annotation.InjectView;
import com.lqtemple.android.lqbookreader.model.VoiceModel;
import com.lqtemple.android.lqbookreader.view.NumberProgressBar;
import com.lzy.okserver.download.DownloadInfo;
import com.lzy.okserver.download.DownloadManager;
import com.lzy.okserver.download.DownloadService;
import com.lzy.okserver.listener.DownloadListener;
import com.lzy.okserver.task.ExecutorWithListener;

import java.util.List;


public class DownloadManagerActivity extends BaseActivity implements View.OnClickListener, ExecutorWithListener.OnAllTaskEndListener {
    private String  TAG = DownloadManagerActivity.class.getSimpleName();

    private List<DownloadInfo> allTask;
    private MyAdapter adapter;
    private DownloadManager downloadManager;

    @InjectView(R.id.titleRightbtn)
    private TextView titleRightbtn;

    @InjectView(R.id.listView)
    ListView listView;
/*
    private  ArrayList<VoiceModel> mAlreadDownList;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case DownLoadValue.SCANF:

                    adapter.notifyDataSetChanged();
                    Log.i(TAG, " updata suseecess= " );

                    break;
            }

        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        InjectUtils.bind(this);
//        mAlreadDownList = (ArrayList<VoiceModel>)getIntent().getSerializableExtra("mAlreadDownList");
        downloadManager = DownloadService.getDownloadManager();
        allTask = downloadManager.getAllTask();
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        titleRightbtn.setOnClickListener(this);

        downloadManager.getThreadPool().getExecutor().addOnAllTaskEndListener(this);
//        new AddTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);// 不用等待

    }


    @Override
    public void onAllTaskEnd() {
        for (DownloadInfo downloadInfo : allTask) {
            if (downloadInfo.getState() != DownloadManager.FINISH) {
                Toast.makeText(DownloadManagerActivity.this, R.string.other_down_task, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(DownloadManagerActivity.this, R.string.all_down_task, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //记得移除，否者会回调多次
        downloadManager.getThreadPool().getExecutor().removeOnAllTaskEndListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public void onClick(View v) {
      /*  switch (v.getId()) {
            case R.id.removeAll:
                downloadManager.removeAllTask();
                adapter.notifyDataSetChanged();  //移除的时候需要调用
                break;
            case R.id.pauseAll:
                downloadManager.pauseAllTask();
                break;
            case R.id.stopAll:
                downloadManager.stopAllTask();
                break;
            case R.id.startAll:
                downloadManager.startAllTask();
                break;
        }*/

        switch (v.getId()) {
            case R.id.titleRightbtn:
            startActivity(new Intent(getApplicationContext(),MusicPlayerActivity.class));
                break;
        }

    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return allTask.size();
        }

        @Override
        public DownloadInfo getItem(int position) {
            return allTask.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            DownloadInfo downloadInfo = getItem(position);
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(DownloadManagerActivity.this, R.layout.item_download_manager, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.refresh(downloadInfo);

            //对于非进度更新的ui放在这里，对于实时更新的进度ui，放在holder中
            VoiceModel apk = (VoiceModel) downloadInfo.getData();
            if (apk != null) {
                Glide.with(DownloadManagerActivity.this).load(apk.getIconUrl()).error(R.mipmap.ic_launcher).into(holder.icon);
                holder.name.setText(apk.getName());
            } else {
                holder.name.setText(downloadInfo.getFileName());
            }
            holder.download.setOnClickListener(holder);
            holder.remove.setOnClickListener(holder);
            holder.restart.setOnClickListener(holder);

            DownloadListener downloadListener = new MyDownloadListener();
            downloadListener.setUserTag(holder);
            downloadInfo.setListener(downloadListener);
            return convertView;
        }
    }

    private class ViewHolder implements View.OnClickListener {
        private DownloadInfo downloadInfo;
        private ImageView icon;
        private TextView name;
        private TextView downloadSize;
        private TextView tvProgress;
        private TextView netSpeed;
        private NumberProgressBar pbProgress;
        private Button download;
        private Button remove;
        private Button restart;

        public ViewHolder(View convertView) {
            icon = (ImageView) convertView.findViewById(R.id.icon);
            name = (TextView) convertView.findViewById(R.id.name);
            downloadSize = (TextView) convertView.findViewById(R.id.downloadSize);
            tvProgress = (TextView) convertView.findViewById(R.id.tvProgress);
            netSpeed = (TextView) convertView.findViewById(R.id.netSpeed);
            pbProgress = (NumberProgressBar) convertView.findViewById(R.id.pbProgress);
            download = (Button) convertView.findViewById(R.id.start);
            remove = (Button) convertView.findViewById(R.id.remove);
            restart = (Button) convertView.findViewById(R.id.restart);
        }

        public void refresh(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
            refresh();
        }


        //对于实时更新的进度ui，放在这里，例如进度的显示，而图片加载等，不要放在这，会不停的重复回调
        //也会导致内存泄漏
        private void refresh() {
            String downloadLength = Formatter.formatFileSize(DownloadManagerActivity.this, downloadInfo.getDownloadLength());
            String totalLength = Formatter.formatFileSize(DownloadManagerActivity.this, downloadInfo.getTotalLength());
            downloadSize.setText(downloadLength + "/" + totalLength);
            if (downloadInfo.getState() == DownloadManager.NONE) {
                netSpeed.setText(getString(R.string.stop));
                download.setText(R.string.start_down);
            } else if (downloadInfo.getState() == DownloadManager.PAUSE) {
                netSpeed.setText(R.string.paush);
                download.setText(R.string.paushing);
            } else if (downloadInfo.getState() == DownloadManager.ERROR) {
                netSpeed.setText(R.string.down_error);
                download.setText(R.string.down_error);
            } else if (downloadInfo.getState() == DownloadManager.WAITING) {
                netSpeed.setText(R.string.down_waiting);
                download.setText(R.string.down_wait);
            } else if (downloadInfo.getState() == DownloadManager.FINISH) {
//                if (ApkUtils.isAvailable(DownloadManagerActivity.this, new File(downloadInfo.getTargetPath()))) {
//                    download.setText("卸载");
//                } else {
//                    download.setText("安装");
//                }
                netSpeed.setText(R.string.downLoaded);
            } else if (downloadInfo.getState() == DownloadManager.DOWNLOADING) {
                String networkSpeed = Formatter.formatFileSize(DownloadManagerActivity.this, downloadInfo.getNetworkSpeed());
                netSpeed.setText(networkSpeed + "/s");
                download.setText(R.string.paush);
            }
            tvProgress.setText((Math.round(downloadInfo.getProgress() * 10000) * 1.0f / 100) + "%");
            pbProgress.setMax((int) downloadInfo.getTotalLength());
            pbProgress.setProgress((int) downloadInfo.getDownloadLength());
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == download.getId()) {
                switch (downloadInfo.getState()) {
                    case DownloadManager.PAUSE:
                    case DownloadManager.NONE:
                    case DownloadManager.ERROR:
                        downloadManager.addTask(downloadInfo.getUrl(), downloadInfo.getRequest(), downloadInfo.getListener());
                        break;
                    case DownloadManager.DOWNLOADING:
                        downloadManager.pauseTask(downloadInfo.getUrl());
                        break;
                    case DownloadManager.FINISH:
//                        if (ApkUtils.isAvailable(DownloadManagerActivity.this, new File(downloadInfo.getTargetPath()))) {
//                            ApkUtils.uninstall(DownloadManagerActivity.this, ApkUtils.getPackageName(DownloadManagerActivity.this, downloadInfo.getTargetPath()));
//                        } else {
//                            ApkUtils.install(DownloadManagerActivity.this, new File(downloadInfo.getTargetPath()));
//                        }
                        break;
                }
                refresh();
            } else if (v.getId() == remove.getId()) {
                Log.i(TAG,"=="+downloadInfo.getUrl()+"="+downloadInfo);
                String url = downloadInfo.getUrl();
                if(downloadManager!=null && url!=null){
                    downloadManager.removeTask(url,true);

                    MediaScannerConnection.scanFile(getApplicationContext() ,
                            new String[]{downloadInfo.getTargetPath()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> uri=" + uri);
                                }
                            });
                }
                adapter.notifyDataSetChanged();
            } else if (v.getId() == restart.getId()) {
                downloadManager.restartTask(downloadInfo.getUrl());
            }
        }
    }

    private class MyDownloadListener extends DownloadListener {

        @Override
        public void onProgress(DownloadInfo downloadInfo) {
            if (getUserTag() == null) return;
            ViewHolder holder = (ViewHolder) getUserTag();
            holder.refresh();  //这里不能使用传递进来的 DownloadInfo，否者会出现条目错乱的问题
        }

        @Override
        public void onFinish(DownloadInfo downloadInfo) {
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(LqBookConst.DOWN_PATH)));

            Toast.makeText(DownloadManagerActivity.this, getString(R.string.downLoaded) + downloadInfo.getTargetPath(), Toast.LENGTH_SHORT).show();

            MediaScannerConnection.scanFile(getApplicationContext() ,
                    new String[]{downloadInfo.getTargetPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        }

        @Override
        public void onError(DownloadInfo downloadInfo, String errorMsg, Exception e) {
            if (errorMsg != null) Toast.makeText(DownloadManagerActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    }



/*    private class AddTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {

            for (VoiceModel file:mAlreadDownList ) {
                Log.i(TAG, "file= " + file.getName());
                for (int i=0 ;i<allTask.size();i++) {
                    if(!file.getName().equals(allTask.get(i).getFileName())){

                        GetRequest request = OkGo.get(file.getUrl())//
                                .headers("headerKey1", "headerValue1")//
                                .headers("headerKey2", "headerValue2")//
                                .params("paramKey1", "paramValue1")//
                                .params("paramKey2", "paramValue2");
                        downloadManager.addTask(null, file, request, null);
                        Log.i(TAG, "geti= " + allTask.get(i).getFileName());

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
