package edu.dartmouth.pxxzz;

import java.util.Date;

import edu.dartmouth.pxxzz.chatclient.ChatClient;
import edu.dartmouth.pxxzz.chatclient.ChatMessage;
import edu.dartmouth.pxxzz.chatclient.MessageBody;
import edu.dartmouth.pxxzz.helper.Constants;
import edu.dartmouth.pxxzz.stablizer.BootAutoStartUpService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
/*
 * This service monitors the lock screen behavior of users. 
 * Send the package to server once the phone is unlocked. 
 * Send the package to server once the phone is locked. 
 * */
public class LockScreenService extends Service {
	private ChatClient mChatClient;
	private BroadcastReceiver screenReceiver;
	private BroadcastReceiver shutDownReceiver;
	private PxxzzApplication pxxzzApplication;
	private Context mContext;
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(screenReceiver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// Not used
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		IntentFilter screenReceiverIntentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		screenReceiverIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		screenReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();

				if (Intent.ACTION_SCREEN_OFF.equals(action)) {
					new PostMessageAsyncTask().execute(true);
				} 
				else if (Intent.ACTION_SCREEN_ON.equals(action)) {
					new PostMessageAsyncTask().execute(false);
				}
			}
		};
		registerReceiver(screenReceiver, screenReceiverIntentFilter);
		
		IntentFilter shutDownReceiverIntentFilter = new IntentFilter(Intent.ACTION_SHUTDOWN);
		shutDownReceiverIntentFilter.addAction(Intent.ACTION_SHUTDOWN);
		shutDownReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {

				new PostMessageAsyncTask().execute(true);
			}
		};
		registerReceiver(shutDownReceiver, shutDownReceiverIntentFilter);
		
		return START_STICKY;
	}
	public class PostMessageAsyncTask extends AsyncTask<Boolean, Void, Void> {

		@Override
		// Get history and upload it to the server.
		protected Void doInBackground(Boolean... lockStatus) {
		    //encode images to base64. and send it to the server. 
			// get input stream
			ChatMessage lockBroadcast;
			// LOCK. 
			// UNLOCK.
			if(lockStatus[0])
			{
				lockBroadcast = new ChatMessage(pxxzzApplication.getUsername(),null,null,null,null,
						pxxzzApplication.getNickname(), new Date(), MessageBody.FLAG_LOCK+"","0");
			}
			else
			{
				lockBroadcast = new ChatMessage(pxxzzApplication.getUsername(),null,null,null,null,
						pxxzzApplication.getNickname(), new Date(), MessageBody.FLAG_UNLOCK+"","0");
			}
			mChatClient.sendMessage(lockBroadcast);
			return null;
		}
	};
	public class ShutDownReceiver extends BroadcastReceiver {
		static final String TAG = "ShutDownReceiver";
		
		@Override
		public void onReceive(Context context, Intent intent) {

				context.startService(new Intent(context, BootAutoStartUpService.class));
		}
	}
	@Override
	public void onCreate() {
		super.onCreate();
		pxxzzApplication = (PxxzzApplication)getApplication();
		mChatClient = new ChatClient(pxxzzApplication.getUsername(), pxxzzApplication.getPassword());
		mContext = this;
		
	}

}