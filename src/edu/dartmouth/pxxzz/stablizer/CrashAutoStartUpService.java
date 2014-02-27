package edu.dartmouth.pxxzz.stablizer;

import edu.dartmouth.pxxzz.ChatHeadService;
import edu.dartmouth.pxxzz.CheeseTimeService;
import edu.dartmouth.pxxzz.LockScreenService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;


public class CrashAutoStartUpService extends Service {
	private Boolean application_started = false;
	private Thread t;
	public void onCreate() {
		super.onCreate();

			application_started = true;
			
			Intent daemonServiceIntent = new Intent(this, DaemonService.class);
			startService(daemonServiceIntent);
			
			Intent chatHeadServiceIntent = new Intent(this, ChatHeadService.class);
			startService(chatHeadServiceIntent);
			
			Intent lockScreenServiceIntent = new Intent(this, LockScreenService.class);
			startService(lockScreenServiceIntent);
			
			Intent cheeseTimeServiceIntent = new Intent(this,
					CheeseTimeService.class);
			startService(cheeseTimeServiceIntent);

			t = new Thread() {
				public void run(){
					//wait for 5 seconds and stop
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					stopSelf();
				}
			};
			t.start();
			Toast.makeText(CrashAutoStartUpService.this, "StartUp service exiting",  Toast.LENGTH_SHORT).show();

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}



