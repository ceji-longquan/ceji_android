package com.lqtemple.android.lqbookreader.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lqtemple.android.lqbookreader.FileUtils;
import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.model.Book;
import com.lqtemple.android.lqbookreader.model.Spine;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Book book;
    private File target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.enableDefaults();
        // 全屏
        initData();
    }

    private void initData() {
        AssetManager manager = getAssets();
        InputStream  inputStream = null;
        target = new File(getExternalCacheDir(), "test.json");
        try {

            inputStream = manager.open("cj.json");
            FileUtils.writeFile(target, inputStream);
        } catch (Exception e) {

        }

    }

    private void  initBookSpine() {
        Spine bookSpine = new Spine(book);
    }

    private void countPage() {
        List contents = book.getJContent();

    }

    public void onStartRead(View v) {
        Intent i = new Intent(this, ReadingActivity.class);
        i.setData(Uri.fromFile(target));
        startActivity(i);
    }
}
