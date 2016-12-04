package com.lqtemple.android.lqbookreader;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lqtemple.android.lqbookreader.model.RawBook;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RawBook book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 全屏
        initData();
    }

    private void initData() {
        AssetManager manager = getAssets();
        InputStream  inputStream = null;
        try {
             inputStream = manager.open("cj.json");
            byte[] bytes = new byte[inputStream.available()];
            int read = 0;
            while ((read = inputStream.read(bytes)) != -1 ){

            }
            String result = new String(bytes, "utf-8");
            book = JSON.parseObject(result, RawBook.class);
            Log.d("BOOK", book.toString());
         } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void  initBookSpine() {
        // 生成目录

    }

    private void countPage() {
        List contents = book.getContent();

    }
}
