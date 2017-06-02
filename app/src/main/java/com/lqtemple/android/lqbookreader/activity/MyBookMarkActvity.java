package com.lqtemple.android.lqbookreader.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.activity.adapter.BaseRecyclerAdapter;
import com.lqtemple.android.lqbookreader.activity.adapter.DividerItemDecoration;
import com.lqtemple.android.lqbookreader.activity.adapter.SettingAdapter;
import com.lqtemple.android.lqbookreader.annotation.InjectUtils;
import com.lqtemple.android.lqbookreader.annotation.InjectView;
import com.lqtemple.android.lqbookreader.model.BookMarkModel;

import java.util.ArrayList;

/**
 * Created by ls on 2017/6/2.
 *
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */

public class MyBookMarkActvity extends BaseActivity {
    private static final String TAG = "MyBookMarkActvity";

    private ArrayList my_setting_list;
    @InjectView(R.id.listView)
    private ListView mListView;
    private SettingAdapter mSettingAdapter;
    @InjectView(R.id.titleCenterbtn)
    private TextView titleCenterbtn;
    @InjectView(R.id.titleRightbtn)
    private TextView titleRightbtn;

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    private MarkAdapter adapter;
    private ArrayList<BookMarkModel> mModels;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookmark);
        InjectUtils.bind(this);

        init();
        initData();
    }

    private void init() {
        titleCenterbtn.setText(getText(R.string.my_bookmark));
        titleRightbtn.setBackgroundResource(0);
    }

    private void initData() {
        mModels = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            BookMarkModel mModel = new BookMarkModel();
            mModel.setMark(i+"");
            mModel.setMarkTime("2017-5-30");
            mModel.setMinTitle("AAAAAA");
            mModel.setMinTitleContent("AAAAAAAAAAAAAAAAAAAAAAAAA");
            mModel.setBookMarkeContent("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            mModels.add(mModel);

        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new MarkAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);

    }


    private class MarkAdapter extends BaseRecyclerAdapter<BookMarkModel, ViewHolder> {


        public MarkAdapter(Context context) {
            super(context, mModels);
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View convertView = null;
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_mybookmark_details, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.min_title = (TextView) convertView.findViewById(R.id.min_title);
            holder.min_title_content = (TextView) convertView.findViewById(R.id.min_title_content);
            holder.delete_note_icon = (ImageView) convertView.findViewById(R.id.delete_note_icon);
            holder.detail_note_icon = (ImageView) convertView.findViewById(R.id.detail_note_icon);

            return holder;

        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(position, mDatas.get(position));

        }
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView min_title, min_title_content;
        TextView mark, mark_time, mark_content;
        ImageView delete_note_icon, detail_note_icon;

        private int position;

        public ViewHolder(View itemView) {
            super(itemView);
            min_title = (TextView) itemView.findViewById(R.id.min_title);
            min_title_content = (TextView) itemView.findViewById(R.id.min_title_content);
            mark = (TextView) itemView.findViewById(R.id.mark);
            mark_time = (TextView) itemView.findViewById(R.id.mark_time);
            mark_content = (TextView) itemView.findViewById(R.id.mark_content);
        }


        public void bind(int position, BookMarkModel model) {
            this.position = position;
            mark.setText(model.getMark());
            mark_time.setText(model.getMarkTime());
            min_title.setText(model.getMinTitle());
            min_title_content.setText(model.getMinTitleContent());
            mark_content.setText(model.getBookMarkeContent());

        }


        @Override
        public void onClick(View v) {
/*            switch (v.getId()){
                case R.id.delete_note_icon :
                    Toast.makeText(getApplicationContext(),"delete",Toast.LENGTH_SHORT).show();
                    //执行删除操作
                    break;
                case R.id.detail_note_icon :
                    //执行进入详细内容
                    Toast.makeText(getApplicationContext(),"detail info",Toast.LENGTH_SHORT).show();

                    break;

            }*/


        }
    }
}
