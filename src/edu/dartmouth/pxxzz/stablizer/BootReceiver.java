package edu.dartmouth.pxxzz.stablizer;

//import com.example.UpdaterService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class BootReceiver extends BroadcastReceiver {
	static final String TAG = "BootReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {

			context.startService(new Intent(context, BootAutoStartUpService.class));
	}
}