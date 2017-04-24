package com.lqtemple.android.lqbookreader.activity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.util.MediaUtil;

public class MySongListAdapter extends BaseAdapter {

	private Context context;

	public MySongListAdapter(Context context) {
		super();
		this.context = context;
	}

	@Override
	public int getCount() {

		return MediaUtil.getInstacen().getSongList().size();
	}

	@Override
	public Object getItem(int position) {

		return MediaUtil.getInstacen().getSongList().get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_music_player, null);
			holder.name = (TextView) convertView.findViewById(R.id.my_item_name);
			holder.img = (ImageView) convertView.findViewById(R.id.my_item_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position == MediaUtil.CURRENTPOS) {
			holder.name.setTextColor(Color.GREEN);
		}else{
			holder.name.setTextColor(Color.BLACK);
		}
//		holder.name.setTag(position);

		holder.name.setText((position + 1) + "." + MediaUtil.getInstacen().getSongList().get(position).getTitle());
		holder.img.setImageResource(R.mipmap.ic_launcher);

		return convertView;
	}

	class ViewHolder {

		public TextView name;
		public ImageView img;
	}
}
