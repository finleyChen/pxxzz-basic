package edu.dartmouth.pxxzz;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.google.android.gcm.GCMRegistrar;

import edu.dartmouth.pxxzz.chatclient.ServerUtilities;
import edu.dartmouth.pxxzz.friend.FriendList;
import edu.dartmouth.pxxzz.gcm.GCMManager;
import edu.dartmouth.pxxzz.helper.Constants;
import edu.dartmouth.pxxzz.stablizer.DaemonService;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class PxxzzApplication extends Application {
	private boolean inCheeseTime;
	private Notification mNotification;
	private NotificationManager mNotificationManager;
	CountDownTimer countDown;
	private PxxzzApplication pxxzzApplication;
	public String friendName;
	private ArrayList<ChatHead> chatHeadList;
	private WindowManager windowManager;
	private int friendCount;
	private GCMManager mGCMMgr;
	public FriendList mFriendList = new FriendList();
	public TakePicture actionInstance;
	public int ttl;
	
	private int[] notificationIconArray = { R.drawable.one, R.drawable.two,
			R.drawable.three, R.drawable.four, R.drawable.five };

	@Override
	public void onCreate() {
		super.onCreate();
		pxxzzApplication = this;
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		chatHeadList = new ArrayList<ChatHead>();
		mGCMMgr = new GCMManager(this);
		actionInstance = new TakePicture(pxxzzApplication);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	public String getNickname(){
		String key, nickname;

		// Load and update all profile views
		key = getString(R.string.preference_name);
		SharedPreferences prefs = getSharedPreferences(key, MODE_PRIVATE);

		// Load user name
		key = getString(R.string.preference_key_profile_name);
		nickname = prefs.getString(key, "");
		return nickname;
	}
	public String getImageProfileString(){
		FileInputStream fis;
		String encodedImage = null;
		try {
			fis = openFileInput(getString(R.string.profile_photo_file_name));
			Bitmap bmap = BitmapFactory.decodeStream(fis);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  					
			bmap.compress(Bitmap.CompressFormat.PNG, 100, baos); 
			byte[] byteArrayImage = baos.toByteArray(); 
			encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Uri.encode(encodedImage);
	}
	public WindowManager getWindowManager() {
		return windowManager;
	}

	public String getUsername() {
		return getSharedPreferences("BIO_SIGNIN", MODE_PRIVATE).getString(
				"USERNAME", "");
	}

	public String getPassword() {
		return getSharedPreferences("BIO_SIGNIN", MODE_PRIVATE).getString(
				"PASSWORD", "");
	}
	public String getCountDown() {
		String key, str_val;
		
		key = getString(R.string.preference_cheesetime);
		SharedPreferences prefs = getSharedPreferences(key, MODE_PRIVATE);

		// Load user name
		key = getString(R.string.preference_key_profile_cheese);
		str_val = prefs.getString(key, "");
		
		return str_val;
	}
	
	public boolean isLogin() {
		return getSharedPreferences("BIO_SIGNIN", MODE_PRIVATE).getBoolean(
				"ISREGISTERED", false);
	}

	public boolean getCheeseTimeStatus() {
		return inCheeseTime;
	}

	/**/
	public void createServices() {
		Intent daemonServiceIntent = new Intent(this, DaemonService.class);
		startService(daemonServiceIntent);

		Intent chatHeadServiceIntent = new Intent(this, ChatHeadService.class);
		startService(chatHeadServiceIntent);

		Intent lockScreenServiceIntent = new Intent(this,
				LockScreenService.class);
		startService(lockScreenServiceIntent);

		Intent cheeseTimeServiceIntent = new Intent(this,
				CheeseTimeService.class);
		startService(cheeseTimeServiceIntent);

	}

	public synchronized void setGcmRegSeq() {
		
		try {
			long gcmRegID = getPackageManager().getPackageInfo(getPackageName(), 0).lastUpdateTime;
			
			SharedPreferences.Editor spe = getSharedPreferences(
					"BIO_SIGNIN", MODE_PRIVATE).edit();
			spe.putLong("GCMREGTIME", gcmRegID);
			spe.commit();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void checkGCMConnection() {
		if(!isLogin()) {
			return;
		}
		long gcmRegID = getSharedPreferences("BIO_SIGNIN", Context.MODE_PRIVATE)
				.getLong("GCMREGTIME", -1);
		long installed = -1;
		try {
			installed = getPackageManager().getPackageInfo(getPackageName(), 0).lastUpdateTime;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			
			return;
		}
		
		if(gcmRegID == -1 || gcmRegID != installed) {
			mGCMMgr.registerGCM();
		}
	}

	/* Shake Sensing */
	private SensorManager mSensorManager;
	private float mAccel; // acceleration apart from gravity
	private float mAccelCurrent; // current acceleration including gravity
	private float mAccelLast; // last acceleration including gravity
	private final float SHAKE_THRESHOLD = 10;

	private final SensorEventListener mSensorListener = new SensorEventListener() {

		public void onSensorChanged(SensorEvent se) {
			float x = se.values[0];
			float y = se.values[1];
			float z = se.values[2];
			mAccelLast = mAccelCurrent;
			mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
			float delta = mAccelCurrent - mAccelLast;
			mAccel = mAccel * 0.9f + delta; // perform low-cut filter
			if (mAccel > SHAKE_THRESHOLD) {
				stopCheeseTime(friendName);
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	/* Helper functions */
	private void initializeAccelerometer() {
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		mAccel = 0.00f;
		mAccelCurrent = SensorManager.GRAVITY_EARTH;
		mAccelLast = SensorManager.GRAVITY_EARTH;
	}

	public void beginCheeseTime(String friendName, int count) {
		
		actionInstance.takeAPicture();
		ttl=count-1;
		inCheeseTime = true;
		this.friendName = friendName;
		initializeAccelerometer();
		mNotification = new Notification.Builder(
				getApplicationContext())
				.setContentTitle("Pxxzz coming!")
				.setSmallIcon(
						R.drawable.eye)
				.build();
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mNotification);
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(300);
		countDown = new CountDownTimer(count*1000, 1000) {

			public void onTick(long millisUntilFinished) {
			}

			public void onFinish() {
				mNotificationManager.cancel(0);
				inCheeseTime = false;
				// TODO: return photo result.
				mSensorManager.unregisterListener(mSensorListener);
			}
		}.start();

	}

	public void stopCheeseTime(String friendName) {
		inCheeseTime = false;
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(300);
		if (countDown != null) {
			countDown.cancel();
			Log.e("stopCheeseTime",friendName);
			mNotificationManager.cancel(0);
			actionInstance.stopPreviewAndFreeCamera();
			// Tells the server the responser rejected.
			new ServerInteractionAsyncTasks(getApplicationContext(),
					pxxzzApplication).sendResponse(new Boolean("False"),
					friendName, null, "0");
			mSensorManager.unregisterListener(mSensorListener);

		}

	}

	public void removeChatHead(String friendName) {
		for (ChatHead chatHead : chatHeadList) {
			if (chatHead.friendName.equals(friendName)) {
				chatHeadList.remove(chatHead);
				// windowManager.removeView(chatHead.profileImage);
				return;
			}
		}
		Log.e("after removeChatHead", chatHeadList.size() + "");
	}

	public void removeChatHeads() {
		friendCount = friendCount - 1;
		for (ChatHead chatHead : chatHeadList) {
			if (chatHead != null)
				windowManager.removeView(chatHead.profileImage);
		}
	}

	public boolean checkChatHeadExist(String friendName) {
		for (ChatHead chatHead : chatHeadList) {
			if (chatHead.friendName.equals(friendName)) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<ChatHead> getChatHeadList() {
		Log.e("getChatHeadList", "" + chatHeadList.size());
		return chatHeadList;
	}

	public void addChatHead(String friendName, ImageView profileImage) {
		for (ChatHead chatHead : chatHeadList) {
			Log.e("chatHead", chatHead.friendName);
		}
		ChatHead chatHead = new ChatHead(friendName, profileImage, friendCount,
				windowManager, getApplicationContext(), pxxzzApplication);
		Log.e("addChatHead", "addChatHead");
		chatHeadList.add(chatHead);
		friendCount = friendCount + 1;
		Log.e("after addChatHead", chatHeadList.size() + "");
	}

}