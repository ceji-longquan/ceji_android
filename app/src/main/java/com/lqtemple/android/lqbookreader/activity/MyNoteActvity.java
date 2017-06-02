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
import android.widget.Toast;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.activity.adapter.BaseRecyclerAdapter;
import com.lqtemple.android.lqbookreader.activity.adapter.DividerItemDecoration;
import com.lqtemple.android.lqbookreader.activity.adapter.SettingAdapter;
import com.lqtemple.android.lqbookreader.annotation.InjectUtils;
import com.lqtemple.android.lqbookreader.annotation.InjectView;
import com.lqtemple.android.lqbookreader.model.NoteModel;

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

public class MyNoteActvity extends BaseActivity {
    private static final String TAG = "MyNoteActvity";

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
    private NoteAdapter adapter;
    private ArrayList<NoteModel> mNoteModels;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_note);
        InjectUtils.bind(this);

        init();
        initData();


    }

    private void initData() {
        mNoteModels = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            NoteModel mNoteModel = new NoteModel();
            mNoteModel.setBookeContent("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            mNoteModel.setMinTitle("AAAAAA");
            mNoteModel.setMinTitleContent("AAAAAAAAAAAAAAAAAAAAAAAAA");
            mNoteModel.setNoteContent("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            mNoteModels.add(mNoteModel);

        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new NoteAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);

    }

    private void init() {
        titleCenterbtn.setText(getText(R.string.my_note));
        titleRightbtn.setBackgroundResource(0);
    }


    private class NoteAdapter extends BaseRecyclerAdapter<NoteModel, ViewHolder> {


        public NoteAdapter(Context context) {
            super(context, mNoteModels);
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View convertView = null;
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_mynote_details, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.min_title = (TextView) convertView.findViewById(R.id.min_title);
            holder.min_title_content = (TextView) convertView.findViewById(R.id.min_title_content);
            holder.booke_content = (TextView) convertView.findViewById(R.id.booke_content);
            holder.note_content = (TextView) convertView.findViewById(R.id.note_content);
            holder.note_time = (TextView) convertView.findViewById(R.id.note_time);
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
        TextView booke_content, note_content, note_time;
        ImageView delete_note_icon, detail_note_icon;

        private int position;

        public ViewHolder(View itemView) {
            super(itemView);
            min_title = (TextView) itemView.findViewById(R.id.min_title);
            min_title_content = (TextView) itemView.findViewById(R.id.min_title_content);
            booke_content = (TextView) itemView.findViewById(R.id.booke_content);
            note_content = (TextView) itemView.findViewById(R.id.note_content);
            note_time = (TextView) itemView.findViewById(R.id.note_time);
            delete_note_icon = (ImageView) itemView.findViewById(R.id.delete_note_icon);
            detail_note_icon = (ImageView) itemView.findViewById(R.id.detail_note_icon);
        }


        public void bind(int position, NoteModel model) {
            this.position = position;
            min_title.setText(model.getMinTitle());
            min_title_content.setText(model.getMinTitleContent());
            note_content.setText(model.getNoteContent());
//            note_time.setText(model.getMinTitleContent());
            booke_content.setText(model.getBookeContent());

            delete_note_icon.setOnClickListener(this);
            detail_note_icon.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.delete_note_icon :
                    Toast.makeText(getApplicationContext(),"delete",Toast.LENGTH_SHORT).show();
                    //执行删除操作
                    break;
                case R.id.detail_note_icon :
                    //执行进入详细内容
                    Toast.makeText(getApplicationContext(),"detail info",Toast.LENGTH_SHORT).show();

                    break;

            }


        }
    }

}
