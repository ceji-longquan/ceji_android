package com.lqtemple.android.lqbookreader.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.annotation.InjectUtils;
import com.lqtemple.android.lqbookreader.annotation.InjectView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by ls on 2017/6/2.
 *
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */

public class MyLanguageActvity extends BaseActivity {
    private static final String TAG = "MyLanguageActvity";

    private ArrayList<String> langList;
    @InjectView(R.id.listView)
    private ListView mListView;
    private LanguageAdapter mLanguageAdapter;
    @InjectView(R.id.titleCenterbtn)
    private TextView titleCenterbtn;
    @InjectView(R.id.titleRightbtn)
    private TextView titleRightbtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_language);
        InjectUtils.bind(this);

        init();
        langList = new ArrayList();
        langList.add("China");
        langList.add("English");
        mLanguageAdapter = new LanguageAdapter(getApplicationContext(),langList);
        mListView.setAdapter(mLanguageAdapter);
    }

    private void init() {
        titleCenterbtn.setText(getText(R.string.my_lanuage));
        titleRightbtn.setBackgroundResource(0);
    }



    class LanguageAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<String> list;

        public LanguageAdapter(Context context, ArrayList<String> list) {
            super();
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            if (list != null) {
                return list.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (list != null) {
                return list.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold hold;
            if (convertView == null) {
                hold = new ViewHold();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.item_mylanuage_details, null);
                convertView.setTag(hold);
            } else {
                hold = (ViewHold) convertView.getTag();
            }

            hold.textView = (TextView) convertView.findViewById(R.id.booke_content);
            hold.imageView = (ImageView) convertView.findViewById(R.id.book_icon);
            hold.textView.setText(list.get(position));
            SharedPreferences spf =getSharedPreferences("language",Context.MODE_PRIVATE);
            int  language  = spf.getInt("key",1);
            if (language==1&&position ==0) {
                hold.imageView.setImageResource(R.mipmap.language_yes);

            }else  if (language==2 && position ==1){
                hold.imageView.setImageResource(R.mipmap.language_yes);

            }else if (language==3 && position ==2){
                hold.imageView.setImageResource(R.mipmap.language_yes);

            }else{
                hold.imageView.setImageResource(R.mipmap.language_no);
            }
            SharedPreferences.Editor edit = spf.edit();

            hold.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(position==0){
                        edit.putInt("key",1);
                    }else if(position==1){
                        edit.putInt("key",2);

                    }else{
                        edit.putInt("key",3);
                    }
                    edit.commit();

                    mLanguageAdapter.notifyDataSetChanged();

                }
            });

            return convertView;
        }

        class ViewHold {
            public ImageView imageView;
            public TextView textView;
        }

    }

    /**
     * 判断系统语言是否为中文
     * @return
     */
    public boolean isLunarSetting() {
        String language = getLanguageEnv();

        if (language != null
                && (language.trim().equals("zh-CN") || language.trim().equals("zh-TW")))
            return true;
        else
            return false;
    }

    private String getLanguageEnv() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = "zh-CN";
            } else if ("tw".equals(country)) {
                language = "zh-TW";
            }
        } else if ("pt".equals(language)) {
            if ("br".equals(country)) {
                language = "pt-BR";
            } else if ("pt".equals(country)) {
                language = "pt-PT";
            }
        }
        return language;
    }
}
