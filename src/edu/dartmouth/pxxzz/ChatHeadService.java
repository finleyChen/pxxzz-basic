package edu.dartmouth.pxxzz;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

import edu.dartmouth.pxxzz.ServerInteractionAsyncTasks.LoadImageAsyncTask;
import edu.dartmouth.pxxzz.helper.Constants;
import edu.dartmouth.pxxzz.helper.Utils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

// This service is always running, 
// waiting for GCM call, if there is a call, 
// the ChatHead is fired. 
public class ChatHeadService extends Service {
	private Utils utils;
	private PxxzzApplication pxxzzApplication;

	private WindowManager windowManager;
	private Context mContext;
	private BroadcastReceiver chatHeadCreateDismissReceiver;
	private BroadcastReceiver imageDisplayReceiver;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		initImageDisplayHandler();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// Not used
		return null;
	}


	public void initImageDisplayHandler() {
		imageDisplayReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(
						Constants.INTENT_FILTER_RECEIVE_IMAGE)) {
					String encodedImageUrl = intent
							.getStringExtra(Constants.EXTRA_IMAGE_PATH);
					String ttl = intent.getStringExtra(Constants.EXTRA_GCM_TTL);
					if(Integer.valueOf(ttl)==0)
						return;
					pxxzzApplication.ttl=Integer.valueOf(ttl)-1;
					pxxzzApplication.friendName = intent.getStringExtra(Constants.EXTRA_GCM_SOURCE);
					new ServerInteractionAsyncTasks(mContext, pxxzzApplication)
							.loadImage(encodedImageUrl);
					
					
		
					
				}
			}
		};
		registerReceiver(imageDisplayReceiver, new IntentFilter(
				Constants.INTENT_FILTER_RECEIVE_IMAGE));
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		pxxzzApplication = (PxxzzApplication) getApplication();
		utils = new Utils(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		pxxzzApplication.removeChatHeads();
		unregisterReceiver(imageDisplayReceiver);
		unregisterReceiver(chatHeadCreateDismissReceiver);
	}
}