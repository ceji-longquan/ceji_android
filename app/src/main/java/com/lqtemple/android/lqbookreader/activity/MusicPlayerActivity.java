package com.lqtemple.android.lqbookreader.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.annotation.InjectUtils;
import com.lqtemple.android.lqbookreader.annotation.InjectView;
import com.lqtemple.android.lqbookreader.event.StartPlayEvent;
import com.lqtemple.android.lqbookreader.model.MusicMedia;
import com.lqtemple.android.lqbookreader.service.MusicPlayerService;
import com.lqtemple.android.lqbookreader.util.LqBookConst;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/3/30 0030.
 */

public class MusicPlayerActivity extends BasePlayActivity{
    private String  TAG = MusicPlayerActivity.class.getSimpleName();
    @InjectView(R.id.musicListView)
    private ListView musicListView ;
    private ArrayList<Map<String, Object>> listems = null;//需要显示在listview里的信息
    private ArrayList<MusicMedia> musicList = null; //音乐信息列表


    private BaseAdapter adapter;
    private AsyncQueryHandler asyncQuery;

    private MediaScannerConnection mScanConnection;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        InjectUtils.bind(this);

        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击播放音乐，不过需要判断一下当前是否有音乐在播放，需要关闭正在播放的
                //position 可以获取到点击的是哪一个，去 musicList 里寻找播放
                currentposition = position;
                startlayer(currentposition);
                EventBus.getDefault().post(new StartPlayEvent(position));


            }
        });

//        scanAllFile();


        asyncQuery = new MyAsyncQueryHandler(getContentResolver());
        //异步music信息
        asyncQueryContact();
        Log.i(TAG, "onCreate: ========");
        if(MusicPlayerService.isplay){
            EventBus.getDefault().post(new StartPlayEvent(100));
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void asyncQueryContact() {

        // TODO Auto-generated method stub
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(LqBookConst.DOWN_PATH);
//        String[] projection = { "_id", "display_name", "data1", "sort_key" };
                /*查询媒体数据库
        参数分别为（0,null,路径，要查询的列名，条件语句，条件参数，排序）
        视频：MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        图片;MediaStore.Images.Media.EXTERNAL_CONTENT_URI
         */
        asyncQuery.startQuery(0, null, uri, null, null, null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
//        asyncQuery.
//        query(MediaStore.Audio.Media.getContentUriForPath(LqBookConst.DOWN_PATH), null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);


    }



    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);

        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                musicList = new ArrayList<MusicMedia>();
                //遍历媒体数据库
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        //歌曲编号
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                        //歌曲标题
                        String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                        //歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                        String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                        int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                        //歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                        String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                        //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                        String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                        //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
                        int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                        //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                        Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));


                        if (size > 1024 * 800) {//大于800K
                            MusicMedia musicMedia = new MusicMedia();
                            musicMedia.setId(id);
                            musicMedia.setArtist(artist);
                            musicMedia.setSize(size);
                            musicMedia.setTitle(tilte);
                            musicMedia.setTime(duration);
                            musicMedia.setUrl(url);
                            musicMedia.setAlbum(album);
                            musicMedia.setAlbumId(albumId);
                            Log.i(TAG, "url: " + url);

                            if (url.startsWith(LqBookConst.DOWN_PATH)) {
                                musicList.add(musicMedia);
                            }

                        }
                        cursor.moveToNext();
                    }
                    cursor.close();
                    if (musicList.size() > 0) {
                        setAdapter(musicList);
                        setMusicList(musicList);
                    }
                }
            }}
    }

    private void setAdapter(List<MusicMedia> listData) {
        adapter = new ListAdapter(this, listData);
        musicListView.setAdapter(adapter);

    }




    private class ListAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<MusicMedia> list;

        public ListAdapter(Context context, List<MusicMedia> list) {
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_music_player, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.my_item_name);
                holder.img = (ImageView) convertView.findViewById(R.id.my_item_img);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            MusicMedia music = list.get(position);
            holder.name.setText(music.getTitle());
//            holder.img.setText(cv.getAsString(NUMBER));

            return convertView;
        }

        private class ViewHolder {
            TextView name;
            ImageView img;
        }

    }


}
