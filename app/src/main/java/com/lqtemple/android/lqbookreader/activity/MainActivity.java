package com.lqtemple.android.lqbookreader.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqtemple.android.lqbookreader.util.FileUtils;
import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.view.RoundView;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private TextView titleLefttbtn, titleRightbtn;
    private RecyclerView mRecyclerView;
    private List<String> mDatas;
    private HomeAdapter mAdapter;
    private File target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initToolBar();
//        StrictMode.enableDefaults();

        // 全屏
        initData();
        mDatas=new ArrayList<>();
        mDatas.add("AAA");
        mDatas.add("BBB");
        mDatas.add("CCC");
        mDatas.add("DDD");
        mDatas.add("DDD");
        mDatas.add("DDD");
        mDatas.add("DDD");
        mDatas.add("DDD");
        mDatas.add("DDD");
        mDatas.add("DDD");
        mDatas.add("DDD");
        mDatas.add("DDD");
        mDatas.add("BBB");

//        List<View> mViews =new ArrayList<>();
//        for (int i=0;i<5;i++ ) {
//            View view =new RoundView(getApplicationContext());
//            mViews.add(view);
//        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new HomeAdapter());



    }


    private void initToolBar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }


    private void initView() {
        titleLefttbtn = (TextView) findViewById(R.id.titleLefttbtn);
        titleLefttbtn.setVisibility(View.GONE);
        titleRightbtn = (TextView) findViewById(R.id.titleRightbtn);

        toolbar = (Toolbar) findViewById(R.id.titleBar);

        titleRightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), SettingActivity.class));

            }
        });

        mRecyclerView =(RecyclerView)findViewById(R.id.id_recyclerview);


    }

    private void initData() {
        AssetManager manager = getAssets();
        InputStream inputStream = null;
        target = new File(getExternalCacheDir(), "test.json");
        try {

            inputStream = manager.open("cj.json");
            FileUtils.writeFile(target, inputStream);
        } catch (Exception e) {

        }

    }


    public void onStartRead(View v) {
        Intent i = new Intent(this, ReadingActivity.class);
        i.setData(Uri.fromFile(target));
        startActivity(i);
    }


    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>
    {




        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    getApplicationContext()).inflate(R.layout.item_main, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position)
        {
            holder.tv.setText(mDatas.get(position));
            holder.roundview.addView(new RoundView(getApplicationContext()));
            holder.roundview.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = holder.getLayoutPosition();
                    Log.i(TAG,"pos=="+pos);
                    Log.i(TAG,"position=="+position);
                    Intent i = new Intent(getApplicationContext(), ReadingActivity.class);
                    i.setData(Uri.fromFile(target));
                    startActivity(i);

                }
            });;
        }

        @Override
        public int getItemCount()
        {
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {

            TextView tv;
            LinearLayout roundview;

            public MyViewHolder(View view)
            {
                super(view);
                tv = (TextView) view.findViewById(R.id.chapter);
                roundview =(LinearLayout) view.findViewById(R.id.roundview);
            }
        }
    }

}

