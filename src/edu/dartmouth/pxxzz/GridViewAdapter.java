package edu.dartmouth.pxxzz;

import java.util.ArrayList;
import java.util.LinkedList;

import edu.dartmouth.pxxzz.chatclient.MessageBody;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapter extends ArrayAdapter<ChatHead> {
	private Context mContext;
	private int layoutResourceId;
	private ArrayList<ChatHead> data;

	public GridViewAdapter(Context context, int layoutResourceId,
			ArrayList<ChatHead> chatHeadList) {
		super(context, layoutResourceId, chatHeadList);
		this.layoutResourceId = layoutResourceId;
		this.mContext = context;
		this.data = new ArrayList <ChatHead>(chatHeadList);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder = null;
		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new ViewHolder();
			holder.imageTitle = (TextView) row.findViewById(R.id.text);
			holder.image = (ImageView) row.findViewById(R.id.image);
			row.setTag(holder);

		} else {
			holder = (ViewHolder) row.getTag();
		}
		
		ChatHead chatHead = getItem(position);
		if(chatHead.status == 0){
			row.setBackgroundColor(mContext.getResources().getColor(android.R.color.background_light));
		}
		else {
			
			//STATUS: ON:
			row.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_green_light));
		}
		
		holder.imageTitle.setText(chatHead.nickname);		
		if(chatHead.getProfileImageBitmap() != null) {
			holder.image.setImageBitmap(chatHead.getProfileImageBitmap());
		}
		return row;
	}
	
	public void sendRequest(int position, PxxzzApplication pxxzzApplication,int ttl){
		
		Log.e("friendName", this.getItem(position).friendName+"request sent");
		new ServerInteractionAsyncTasks(mContext, pxxzzApplication).sendRequest(this.getItem(position).friendName,ttl);
	}
	static class ViewHolder {
		TextView imageTitle;
		ImageView image;
	}
}