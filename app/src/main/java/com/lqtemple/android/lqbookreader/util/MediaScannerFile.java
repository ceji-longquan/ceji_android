package com.lqtemple.android.lqbookreader.util;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

/**
 * Created by Administrator on 2017/3/30 0030.
 *
 * 扫描多媒体 可以指定文件夹
 */

public class MediaScannerFile {

    /**
     * 扫描指定的文件
     *
     * @param context
     * @param filePath
     * @param sListener
     */
    public static MediaScannerConnection scanFile(Context context, String[] filePath, String[] mineType,
                                                  MediaScannerConnection.OnScanCompletedListener sListener) {

        ClientProxy client = new ClientProxy(filePath, mineType, sListener);

        try {
            MediaScannerConnection connection = new MediaScannerConnection(
                    context.getApplicationContext(), client);
            client.mConnection = connection;
            connection.connect();
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    static class ClientProxy implements MediaScannerConnection.MediaScannerConnectionClient {
        final String[] mPaths;
        final String[] mMimeTypes;
        final MediaScannerConnection.OnScanCompletedListener mClient;
        MediaScannerConnection mConnection;
        int mNextPath;

        ClientProxy(String[] paths, String[] mimeTypes,
                    MediaScannerConnection.OnScanCompletedListener client) {
            mPaths = paths;
            mMimeTypes = mimeTypes;
            mClient = client;
        }

        public void onMediaScannerConnected() {
            scanNextPath();
        }

        public void onScanCompleted(String path, Uri uri) {
            if (mClient != null) {
                mClient.onScanCompleted(path, uri);
            }
            scanNextPath();
        }

        /**
         * 自动扫描下一个
         */
        void scanNextPath() {
            if (mNextPath >= mPaths.length) {
                mConnection.disconnect();
                return;
            }
            String mimeType = mMimeTypes != null ? mMimeTypes[mNextPath] : null;
            mConnection.scanFile(mPaths[mNextPath], mimeType);
            mNextPath++;
        }
    }

/*
    调用方法：

            if (null != mMediaScannerFile) {
        mMediaScannerFile.scanFile(mContext, musicFilePaths, null,
                this);
    }

    这里也可以传递一个文件夹进去：

    public void scanAllFile() {
        String[] rootDir = new String[] { Environment.getExternalStorageDirectory()+"/test"};
        mScanConnection = MediaScannerFile.scanFile(this, rootDir, null, this);
    }

    记得在退出时调用：

    public void destroy() {
        if (null != mScanConnection && mScanConnection.isConnected()) {
            mScanConnection.disconnect();
        }
    }*/


}
