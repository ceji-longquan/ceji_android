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
import android.widget.ImageView;
import android.widget.TextView;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.model.LeftMenu;

import java.util.List;

public class SettingAdapter extends BaseAdapter {

    private Context context;
    private List<LeftMenu> list;

    public SettingAdapter(Context context, List<LeftMenu> list) {
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
                    R.layout.item_setting, null);
            convertView.setTag(hold);
        } else {
            hold = (ViewHold) convertView.getTag();
        }

        hold.textView = (TextView) convertView.findViewById(R.id.my_item_name);
        hold.imageView = (ImageView) convertView.findViewById(R.id.my_item_img);

        hold.imageView.setImageResource(list.get(position).getImageView());
        hold.textView.setText(list.get(position).getText());
        return convertView;
    }

     class ViewHold {
        public ImageView imageView;
        public TextView textView;
    }

}
