package edu.dartmouth.pxxzz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class ChatHead {

	public String friendName;
	public WindowManager.LayoutParams childWindowManagerParams;
	public ImageView profileImage;
	public Uri profileImageUri;
	public WindowManager rootWindowManager;
	public int friendIndex;
	public int status;
	public String nickname;
	public Handler handler;
	PxxzzApplication pxxzzApplication;
	Context mContext;
	CountDownTimer countDown;
	
	public void initializeParams() {
		childWindowManagerParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		childWindowManagerParams.gravity = Gravity.TOP | Gravity.LEFT;
//		childWindowManagerParams.x = -10;
//		childWindowManagerParams.y = 100 * this.friendIndex;
		childWindowManagerParams.x = 0;
		childWindowManagerParams.y = 0;
		childWindowManagerParams.horizontalMargin=0;
	}
	
	public Bitmap getProfileImageBitmap(){
		BitmapDrawable drawable = (BitmapDrawable)profileImage.getDrawable();
		
		if( drawable != null) {
			return drawable.getBitmap();
		}
		return null;
	}
	
	public ChatHead(String name, ImageView image, int index,
			WindowManager windowManager, Context context,
			PxxzzApplication application) {

		this.friendName = name;
		this.rootWindowManager = windowManager;
		this.profileImage = image;
		this.friendIndex = index;
		this.mContext = context;
		this.pxxzzApplication = application;
		initializeParams();
//		countDown = new CountDownTimer(3000, 3000) {
//
//			public void onFinish() {
//				Intent dismissChatHeadIntent = new Intent(Config.INTENT_FILTER_DISMISS_CHAT_HEAD);
//				dismissChatHeadIntent.putExtra(Config.EXTRA_FRIEND_NAME, friendName);
//				mContext.sendBroadcast(dismissChatHeadIntent);
//			}
//
//			@Override
//			public void onTick(long millisUntilFinished) {
//				// TODO Auto-generated method stub
//				
//			}
//		}.start();
//		profileImage.setOnTouchListener(new View.OnTouchListener() {
//			private int initialX;
//			private int initialY;
//			private float initialTouchX;
//			private float initialTouchY;
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if (pxxzzApplication.getCheeseTimeStatus())
//					return false;
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_MOVE:
//					Log.e("ACTION_MOVE","ACTION_MOVE");
//					childWindowManagerParams.x = initialX
//							+ (int) (event.getRawX() - initialTouchX);
//					childWindowManagerParams.y = initialY
//							+ (int) (event.getRawY() - initialTouchY);
//					rootWindowManager.updateViewLayout(profileImage,
//							childWindowManagerParams);
//					return true;
//				case MotionEvent.ACTION_DOWN:
//					Log.e("ACTION_DOWN","ACTION_DOWN");
//					initialX = childWindowManagerParams.x;
//					initialY = childWindowManagerParams.y;
//					initialTouchX = event.getRawX();
//					initialTouchY = event.getRawY();
//					new ServerInteractionAsyncTasks(mContext, pxxzzApplication)
//							.sendRequest(friendName);
//					Toast.makeText(v.getContext(), "Waiting...",
//							Toast.LENGTH_LONG).show();
//					return true;
//				case MotionEvent.ACTION_UP:
//					return true;
//
//				}
//				return false;
//			}
//		});
		//this.rootWindowManager.addView(profileImage, childWindowManagerParams);
	}
}