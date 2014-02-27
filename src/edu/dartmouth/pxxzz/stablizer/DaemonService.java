package edu.dartmouth.pxxzz.stablizer;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class DaemonService extends Service {

	//new timer code
	private Handler mHandler = new Handler();

	//
	//private final int rateNotification = 1000*60*5;
	private final int rateNotification = 1000*1;

	public void onCreate() {

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent == null)
		{
			CrashRestartAlarm.scheduleRestartOfService(this);
			stopSelf();
			return START_NOT_STICKY;
		}

		mHandler.removeCallbacks(mUpdateTimeTask);
		mHandler.postDelayed(mUpdateTimeTask, rateNotification);



		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		//return START_STICKY;
		return START_STICKY;
	}

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			mHandler.removeCallbacks(mUpdateTimeTask);
			mHandler.postDelayed(mUpdateTimeTask, rateNotification);


		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		try{
			mHandler.removeCallbacks(mUpdateTimeTask);
		}
		catch(Exception ex){}

	}


}



