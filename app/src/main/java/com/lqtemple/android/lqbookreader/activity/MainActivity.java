package com.lqtemple.android.lqbookreader.activity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.model.Book;
import com.lqtemple.android.lqbookreader.model.RawBook;
import com.lqtemple.android.lqbookreader.model.Spine;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Book book;

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
            RawBook rawBook = JSON.parseObject(result, RawBook.class);
            Log.d("BOOK", rawBook.toString());
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
        Spine bookSpine = new Spine(book);
    }

    private void countPage() {
        List contents = book.getContent();

    }
}
