package edu.dartmouth.pxxzz.gcm;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import edu.dartmouth.pxxzz.PxxzzApplication;
import edu.dartmouth.pxxzz.chatclient.ServerUtilities;
import edu.dartmouth.pxxzz.helper.Constants;

public class GCMManager {

	AsyncTask<Void, Void, Void> mRegisterGCMTask;
	private PxxzzApplication mApplication;

	public GCMManager(PxxzzApplication Application) {
		mApplication = Application;
	}

	public boolean isRegistered() {
		return GCMRegistrar.isRegisteredOnServer(mApplication.getApplicationContext());
	}

	public void checkGCM() {

	}

	public void registerGCM() {
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(mApplication.getApplicationContext());
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(mApplication.getApplicationContext());
		final String regId = GCMRegistrar.getRegistrationId(mApplication.getApplicationContext());
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(mApplication.getApplicationContext(), Constants.SENDER_ID);
		} else {

			// Try to register again, but not in the UI thread.
			// It's also necessary to cancel the thread onDestroy(),
			// hence the use of AsyncTask instead of a raw thread.
			final Context context = mApplication.getApplicationContext();
			mRegisterGCMTask = new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					boolean registered = ServerUtilities.register(context,
							regId, mApplication.getUsername());
					// At this point all attempts to register with the app
					// server failed, so we need to unregister the device
					// from GCM - the app will try to register again when
					// it is restarted. Note that GCM will send an
					// unregistered callback upon completion, but
					// GCMIntentService.onUnregistered() will ignore it.
					if (!registered) {
						GCMRegistrar.unregister(context);
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					mRegisterGCMTask = null;
				}

			};
			mRegisterGCMTask.execute(null, null, null);
		}
	}

}
