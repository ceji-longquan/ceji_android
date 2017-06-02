package com.lqtemple.android.lqbookreader.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.annotation.InjectUtils;
import com.lqtemple.android.lqbookreader.annotation.InjectView;
import com.lqtemple.android.lqbookreader.model.VoiceModel;
import com.lqtemple.android.lqbookreader.util.LqBookConst;
import com.lqtemple.android.lqbookreader.view.NumberProgressBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.download.DownloadInfo;
import com.lzy.okserver.download.DownloadManager;
import com.lzy.okserver.download.DownloadService;
import com.lzy.okserver.listener.DownloadListener;

import java.util.ArrayList;
import java.util.List;

import static com.lqtemple.android.lqbookreader.R.id.down_icon;
import static com.lqtemple.android.lqbookreader.R.id.down_text;
import static com.lqtemple.android.lqbookreader.R.id.pbProgress;


/**
 * Created by ls on 2017/6/2.
 *
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */

public class MusicDownLoadActivity extends BaseActivity {

    private ArrayList<VoiceModel> mVoiceModels;
    private final String TAG = "DownLoadActivity";
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private MyAdapter adapter;
    private DownloadManager downloadManager;

    @InjectView(R.id.listView)
    ListView listView;

    @InjectView(R.id.titleRightbtn)
    private TextView titleRightbtn;
    @InjectView(R.id.titleCenterbtn)
    private TextView titleCenterbtn;
    private List<DownloadInfo> allTask;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_download_manager);
        InjectUtils.bind(this);
        checkVersion();

        initData();
        titleCenterbtn.setText(getText(R.string.my_music));

        titleRightbtn.setBackgroundResource(R.drawable.musicfile);
        titleRightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MusicPlayerActivity.class));
            }
        });

        downloadManager = DownloadService.getDownloadManager();
        //设置下载目录
        downloadManager.setTargetFolder(LqBookConst.DOWN_PATH);
        //设置下载任务个数
        downloadManager.getThreadPool().setCorePoolSize(LqBookConst.DOWN_TASK_NUM);


        adapter = new MyAdapter();
        listView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (downloadManager != null) {
            allTask = downloadManager.getAllTask();
        }
        adapter.notifyDataSetChanged();

    }

    private void checkVersion() {


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


    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mVoiceModels.size();
        }

        @Override
        public VoiceModel getItem(int position) {
            return mVoiceModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_music_download, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //            holder.refresh(voiceinfo);
            VoiceModel voiceinfo = getItem(position);

            holder.down_item_layout = (LinearLayout)convertView.findViewById(R.id.down_item_layout);
            holder.down_icon = (ImageView) convertView.findViewById(down_icon);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.pbProgress = (NumberProgressBar) convertView.findViewById(pbProgress);
            holder.down_text = (TextView) convertView.findViewById(down_text);
            DownloadInfo downloadInfo = downloadManager.getDownloadInfo(voiceinfo.getUrl());

            if ( downloadInfo!= null) {
                if(downloadInfo.getState()==DownloadManager.FINISH){
                    holder.down_item_layout.setBackgroundColor(getResources().getColor(R.color.titleBar));
                    holder.down_text.setText(R.string.alreadydown);
                }else {
                    holder.down_text.setText(R.string.alreadydowning);

                }
                holder.down_icon.setVisibility(View.GONE);
                holder.down_text.setVisibility(View.VISIBLE);
            } else {
                holder.down_item_layout.setBackgroundColor(getResources().getColor(R.color.white_text));
                holder.down_icon.setVisibility(View.VISIBLE);
                holder.down_text.setVisibility(View.GONE);

            }
            //对于非进度更新的ui放在这里，对于实时更新的进度ui，放在holder中
            if (voiceinfo != null) {
                holder.name.setText(voiceinfo.getName());
                //                holder.down_text.setText(voiceinfo.getName());

            }
            holder.down_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (v.getId() == holder.down_icon.getId()) {
                        if (downloadManager.getDownloadInfo(voiceinfo.getUrl()) != null) {
                            Toast.makeText(getApplicationContext(), R.string.task_enqueue, Toast.LENGTH_SHORT).show();
                        } else {
                            holder.down_icon.setVisibility(View.GONE);
                            holder.down_text.setVisibility(View.VISIBLE);
                            holder.down_text.setText(R.string.alreadydowning);

                            GetRequest request = OkGo.get(voiceinfo.getUrl())//
                                    .headers("headerKey1", "headerValue1")//
                                    .headers("headerKey2", "headerValue2")//
                                    .params("paramKey1", "paramValue1")//
                                    .params("paramKey2", "paramValue2");
                            downloadManager.addTask(voiceinfo.getUrl(), voiceinfo, request, null);

                            adapter.notifyDataSetChanged();

                        }
                    }
                }
            });

            holder.down_item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), DesActivity.class);
                    intent.putExtra("apk", voiceinfo);
                    startActivity(intent);
                }
            });

          DownloadListener downloadListener = new MyDownloadListener();
          downloadListener.setUserTag(holder);
            if(downloadInfo!=null){
                downloadInfo.setListener(downloadListener);
            }
            return convertView;
        }
    }

    private class ViewHolder implements View.OnClickListener {
        private DownloadInfo downloadInfo;
        private ImageView down_icon;
        private TextView name;
        private NumberProgressBar pbProgress;
        private TextView down_text;
        private LinearLayout down_item_layout;

        @Override
        public void onClick(View v) {

        }
    }






    private class MyDownloadListener extends DownloadListener {

        @Override
        public void onProgress(DownloadInfo downloadInfo) {
            if (getUserTag() == null)
                return;

            Log.i(TAG,"onProgress="+downloadInfo.getProgress());

            ViewHolder holder = (ViewHolder) getUserTag();
        }

        @Override
        public void onFinish(DownloadInfo downloadInfo) {
            //            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(LqBookConst.DOWN_PATH)));
            Log.i(TAG,"onFinish="+downloadInfo.getFileName());
            Toast.makeText(getApplicationContext(), getString(R.string.downLoaded) + downloadInfo.getTargetPath(), Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
            /**
             * 重新扫描数据库
             */
            MediaScannerConnection.scanFile(getApplicationContext(),
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
            if (errorMsg != null)
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }
    }


    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {

        new AlertDialog.Builder(MusicDownLoadActivity.this)
                .setTitle(R.string.storage_permission)
                .setMessage(R.string.storage_permission_messahe)
                .setPositiveButton(R.string.now_open, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 开始提交请求权限
                        ActivityCompat.requestPermissions(MusicDownLoadActivity.this, permissions, 321);

                    }
                })
                .setNegativeButton(R.string.now_cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }
}
