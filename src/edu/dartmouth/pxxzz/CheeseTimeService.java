package edu.dartmouth.pxxzz;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.dartmouth.pxxzz.helper.Constants;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class CheeseTimeService extends Service {
	private Context mContext;
	PxxzzApplication pxxzzApplication;
	private String friendName;

	/* Service Lifecycle */
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(screenReceiver);
		unregisterReceiver(requestReceiver);
		unregisterReceiver(busyReceiver);
		unregisterReceiver(photoReceiver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		initializeScreenHandler();
		initializeRequestHandler();
		initializePhotoReceiverHandler();
		initializeBusyReceiverHandler();
		return START_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		pxxzzApplication = (PxxzzApplication) getApplication();
	}

	/* Receivers */
	private BroadcastReceiver requestReceiver;
	private BroadcastReceiver screenReceiver;
	private BroadcastReceiver photoReceiver;
	private BroadcastReceiver busyReceiver;

	private void initializeScreenHandler() {
		IntentFilter screenReceiverIntentFilter = new IntentFilter(
				Intent.ACTION_SCREEN_ON);
		screenReceiverIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		screenReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();

				if (Intent.ACTION_SCREEN_OFF.equals(action) && pxxzzApplication.getCheeseTimeStatus()) {
					pxxzzApplication.stopCheeseTime(friendName);
				} 
				else if (Intent.ACTION_SCREEN_ON.equals(action)) {
				
				}
			}
		};
		registerReceiver(screenReceiver, screenReceiverIntentFilter);
	}

	private void initializeRequestHandler() {
		IntentFilter requestIntentFilter = new IntentFilter(
				Constants.INTENT_FILTER_CHEESE_TIME);
		requestReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.e("request", "cheese time request");
				if (pxxzzApplication.getCheeseTimeStatus()){
					Log.e("cheese time", pxxzzApplication.getCheeseTimeStatus()+"");
					return;
				}
					
				CharSequence text = "Pxxzz coming!";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();

				// TODO: display incoming friend request.
				friendName = intent.getStringExtra(Constants.EXTRA_FRIEND_NAME);
	
				int ttl = Integer.valueOf(intent.getStringExtra(Constants.EXTRA_GCM_TTL));
				pxxzzApplication.beginCheeseTime(friendName,ttl);
				
			}
		};
		registerReceiver(requestReceiver, requestIntentFilter);
	}

	public void initializeBusyReceiverHandler() {
		IntentFilter busyReceiverIntentFilter = new IntentFilter(
				Constants.INTENT_FILTER_BUSY_NOTIFICATION);
		busyReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String friendName = intent
						.getStringExtra(Constants.EXTRA_FRIEND_NAME);

				// Vibrate
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(300);

				CharSequence text = friendName + " is busy right now:(";
				int duration = Toast.LENGTH_LONG;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
		};
		registerReceiver(busyReceiver, busyReceiverIntentFilter);
	}

	public void initializePhotoReceiverHandler() {
		IntentFilter photoReceiverIntentFilter = new IntentFilter(
				Constants.INTENT_FILTER_PHOTO_TAKEN);
		photoReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.e("onReceive", "photo sent,"+pxxzzApplication.ttl);
				CharSequence text = "Pxxzz captured!";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();

				String encodedImagePath = intent
						.getStringExtra(Constants.EXTRA_ENCODED_IMAGE_PATH);
				Log.e("encodedImagePath",encodedImagePath);

				SharedPreferences prefs = getSharedPreferences("check_duplicates", MODE_PRIVATE);

				// Load user name
				String savedTimeStamp = prefs.getString(friendName, "");
				if (savedTimeStamp.equals("")) {
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString(friendName, encodedImagePath);
					editor.apply();
				} else {
					if(savedTimeStamp.equals(encodedImagePath)){
						Log.e("there","there is duplicate.");
						return;
					}
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString(friendName, encodedImagePath);
					editor.apply();
				}
				
				String encodedImage = Uri.encode(readFromFile(encodedImagePath));
				if(friendName==null)
				{
					friendName = pxxzzApplication.friendName;
					Log.e("friendName",friendName);
				}
				new ServerInteractionAsyncTasks(mContext, pxxzzApplication)
						.sendResponse(new Boolean("True"), friendName,
								encodedImage,pxxzzApplication.ttl+"");
			}
		};
		Log.e("regreciever", "reg once");
		registerReceiver(photoReceiver, photoReceiverIntentFilter);
	}

	private String readFromFile(String path) {

		String ret = "";

		try {
			InputStream inputStream = openFileInput(path);

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				ret = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}

		return ret;
	}
}