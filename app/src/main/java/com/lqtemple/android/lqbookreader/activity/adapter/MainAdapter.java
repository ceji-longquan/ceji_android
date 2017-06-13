package com.lqtemple.android.lqbookreader.activity.adapter;

/**
 * @author Administrator
 * Created by Administrator on 2017/3/13 0013.
 * @version $Rev$
 * @updateDes ${TODO}
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqtemple.android.lqbookreader.R;

import java.util.List;

public class MainAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private List<View> mViews;

    public MainAdapter(Context context, List<String> list,List<View> views) {
        super();
        this.context = context;
        this.list = list;
        this.mViews = views;

    }

    @Override
    public int getCount() {
        if (mViews != null) {
            return mViews.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mViews != null) {
            return mViews.get(position);
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
                    R.layout.item_main, null);
            convertView.setTag(hold);
        } else {
            hold = (ViewHold) convertView.getTag();
        }

        hold.chapter = (TextView) convertView.findViewById(R.id.chapter);
        hold.roundview = (LinearLayout) convertView.findViewById(R.id.roundview);
        parent.removeAllViews();
        hold.roundview.addView(mViews.get(position));
//        hold.chapter.setText(list.get(position));
        return convertView;
    }

     class ViewHold {
        public LinearLayout roundview;
        public TextView chapter;
    }

}
